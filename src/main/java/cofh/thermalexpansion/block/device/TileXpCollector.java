package cofh.thermalexpansion.block.device;

import cofh.core.fluid.FluidTankCore;
import cofh.core.network.PacketCoFHBase;
import cofh.core.util.helpers.FluidHelper;
import cofh.core.util.helpers.MathHelper;
import cofh.core.util.helpers.ServerHelper;
import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalexpansion.block.device.BlockDevice.Type;
import cofh.thermalexpansion.gui.client.device.GuiXpCollector;
import cofh.thermalexpansion.gui.container.device.ContainerXpCollector;
import cofh.thermalexpansion.init.TEProps;
import cofh.thermalexpansion.util.managers.machine.XpManager;
import cofh.thermalfoundation.init.TFFluids;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.FluidTankProperties;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import net.minecraftforge.fml.common.registry.GameRegistry;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;

public class TileXpCollector extends TileDeviceBase implements ITickable {

	private static final int TYPE = Type.XP_COLLECTOR.getMetadata();

	public static void initialize() {

		SIDE_CONFIGS[TYPE] = new SideConfig();
		SIDE_CONFIGS[TYPE].numConfig = 5;
		SIDE_CONFIGS[TYPE].slotGroups = new int[][] { {}, { 0 }, {}, { 0 }, { 0 } };
		SIDE_CONFIGS[TYPE].sideTypes = new int[] { 0, 1, 4, 7, 8 };
		SIDE_CONFIGS[TYPE].defaultSides = new byte[] { 1, 1, 2, 2, 2, 2 };

		SLOT_CONFIGS[TYPE] = new SlotConfig();
		SLOT_CONFIGS[TYPE].allowInsertionSlot = new boolean[] { true };
		SLOT_CONFIGS[TYPE].allowExtractionSlot = new boolean[] { false };

		LIGHT_VALUES[TYPE] = 5;

		GameRegistry.registerTileEntity(TileXpCollector.class, "thermalexpansion:device_xp_collector");

		config();
	}

	public static void config() {

		String category = "Device.XpCollector";
		BlockDevice.enable[TYPE] = ThermalExpansion.CONFIG.get(category, "Enable", true);
	}

	private static final int RADIUS_ORB = 4;
	private static final int TIME_CONSTANT = 32;

	private int inputTracker;
	private int outputTracker;

	private int xpBuffer;
	private int boostXp;
	private int boostFactor;

	private FluidTankCore tank = new FluidTankCore(TEProps.MAX_FLUID_MEDIUM);

	private int offset;

	public TileXpCollector() {

		super();
		inventory = new ItemStack[1];
		Arrays.fill(inventory, ItemStack.EMPTY);
		createAllSlots(inventory.length);

		offset = MathHelper.RANDOM.nextInt(TIME_CONSTANT);
		tank.setLock(TFFluids.fluidExperience);

		hasAutoInput = true;
		hasAutoOutput = true;

		enableAutoInput = true;
		enableAutoOutput = true;
	}

	@Override
	public int getType() {

		return TYPE;
	}

	@Override
	public void update() {

		if (ServerHelper.isClientWorld(world)) {
			return;
		}
		if (!timeCheckOffset()) {
			return;
		}
		convertXp();
		transferOutputFluid();
		transferInput();

		boolean curActive = isActive;

		if (isActive) {
			if (xpBuffer <= 0) {
				collectXpOrbs();
			}
			if (!redstoneControlOrDisable()) {
				isActive = false;
			}
		} else if (redstoneControlOrDisable()) {
			isActive = true;
		}
		updateIfChanged(curActive);

	}

	protected void transferInput() {

		if (!enableAutoInput) {
			return;
		}
		int side;
		for (int i = inputTracker + 1; i <= inputTracker + 6; i++) {
			side = i % 6;
			if (isPrimaryInput(sideConfig.sideTypes[sideCache[side]])) {
				if (extractItem(0, ITEM_TRANSFER[level], EnumFacing.VALUES[side])) {
					inputTracker = side;
					break;
				}
			}
		}
	}

	protected void transferOutputFluid() {

		if (!enableAutoOutput || tank.getFluidAmount() <= 0) {
			return;
		}
		int side;
		FluidStack output = new FluidStack(tank.getFluid(), Math.min(tank.getFluidAmount(), tank.getCapacity()));
		for (int i = outputTracker + 1; i <= outputTracker + 6; i++) {
			side = i % 6;
			if (sideCache[side] == 1) {
				int toDrain = FluidHelper.insertFluidIntoAdjacentFluidHandler(this, EnumFacing.VALUES[side], output, true);
				if (toDrain > 0) {
					tank.drain(toDrain, true);
					outputTracker = side;
					break;
				}
			}
		}
	}

	protected void collectXpOrbs() {

		AxisAlignedBB area = new AxisAlignedBB(pos.add(-RADIUS_ORB, -RADIUS_ORB, -RADIUS_ORB), pos.add(1 + RADIUS_ORB, 1 + RADIUS_ORB, 1 + RADIUS_ORB));
		List<EntityXPOrb> xpOrbs = world.getEntitiesWithinAABB(EntityXPOrb.class, area, EntitySelectors.IS_ALIVE);

		for (EntityXPOrb orb : xpOrbs) {
			xpBuffer += orb.getXpValue();
			orb.setDead();
		}
	}

	protected void collectMachineXp() {

	}

	protected void convertXp() {

		if (boostXp <= 0 && XpManager.getCatalystFactor(inventory[0]) > 0) {
			boostXp = XpManager.getCatalystXp(inventory[0]);
			boostFactor = XpManager.getCatalystFactor(inventory[0]);

			inventory[0].shrink(1);
			if (inventory[0].getCount() <= 0) {
				inventory[0] = ItemStack.EMPTY;
			}
		}
		int conversion = (XpManager.XP_CONVERSION * (100 + boostFactor)) / 100;
		int toConvert;

		if (xpBuffer * conversion <= tank.getSpace()) {
			tank.modifyFluidStored(xpBuffer * conversion);
			toConvert = xpBuffer;
			xpBuffer = 0;
		} else {
			tank.modifyFluidStored(tank.getSpace());
			toConvert = tank.getSpace() / conversion;
			xpBuffer -= toConvert;
		}
		boostXp -= toConvert * boostFactor / 100;

		if (boostXp <= 0) {
			boostXp = 0;
			boostFactor = 0;
		}
		/* If anyone reads this and thinks "Hey, that means that catalysts are inconsistent and randomly worth more XP,"
		you are correct. This is an intentional compromise on my part to keep CPU usage down. It wouldn't be difficult
		to be super picky here and subdivide "regular" XP and "bonus" XP, and then run extra calculations and item
		consumptions, but that level of bookkeeping gets in the way of just having the thing work efficiently. So yeah,
		sometimes the catalysts work a little better than their "raw" value would suggest. I'm okay with that, and you
		should be too. :) */
	}

	protected boolean timeCheckOffset() {

		return (world.getTotalWorldTime() + offset) % TIME_CONSTANT == 0;
	}

	public int getBoostFactor() {

		return boostFactor;
	}

	/* GUI METHODS */
	@Override
	public Object getGuiClient(InventoryPlayer inventory) {

		return new GuiXpCollector(inventory, this);
	}

	@Override
	public Object getGuiServer(InventoryPlayer inventory) {

		return new ContainerXpCollector(inventory, this);
	}

	@Override
	public FluidTankCore getTank() {

		return tank;
	}

	@Override
	public FluidStack getTankFluid() {

		return tank.getFluid();
	}

	/* NBT METHODS */
	@Override
	public void readFromNBT(NBTTagCompound nbt) {

		super.readFromNBT(nbt);

		inputTracker = nbt.getInteger("TrackIn");
		outputTracker = nbt.getInteger("TrackOut");
		tank.readFromNBT(nbt);

		boostXp = nbt.getInteger("BoostXp");
		boostFactor = nbt.getInteger("BoostFactor");
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {

		super.writeToNBT(nbt);

		nbt.setInteger("TrackIn", inputTracker);
		nbt.setInteger("TrackOut", outputTracker);
		tank.writeToNBT(nbt);

		nbt.setInteger("BoostXp", boostXp);
		nbt.setInteger("BoostFactor", boostFactor);

		return nbt;
	}

	/* NETWORK METHODS */
	@Override
	public PacketCoFHBase getGuiPacket() {

		PacketCoFHBase payload = super.getGuiPacket();

		payload.addInt(boostXp);
		payload.addInt(boostFactor);
		payload.addFluidStack(tank.getFluid());

		return payload;
	}

	@Override
	protected void handleGuiPacket(PacketCoFHBase payload) {

		super.handleGuiPacket(payload);

		boostXp = payload.getInt();
		boostFactor = payload.getInt();
		tank.setFluid(payload.getFluidStack());
	}

	/* IInventory */
	@Override
	public boolean isItemValidForSlot(int slot, ItemStack stack) {

		return XpManager.getCatalystFactor(stack) > 0;
	}

	/* CAPABILITIES */
	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing from) {

		return super.hasCapability(capability, from) || capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY;
	}

	@Override
	public <T> T getCapability(Capability<T> capability, final EnumFacing from) {

		if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
			return CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.cast(new IFluidHandler() {

				@Override
				public IFluidTankProperties[] getTankProperties() {

					FluidTankInfo info = tank.getInfo();
					return new IFluidTankProperties[] { new FluidTankProperties(info.fluid, info.capacity, false, true) };
				}

				@Override
				public int fill(FluidStack resource, boolean doFill) {

					return 0;
				}

				@Nullable
				@Override
				public FluidStack drain(FluidStack resource, boolean doDrain) {

					if (from != null && !allowExtraction(sideConfig.sideTypes[sideCache[from.ordinal()]])) {
						return null;
					}
					return tank.drain(resource, doDrain);
				}

				@Nullable
				@Override
				public FluidStack drain(int maxDrain, boolean doDrain) {

					if (from != null && !allowExtraction(sideConfig.sideTypes[sideCache[from.ordinal()]])) {
						return null;
					}
					return tank.drain(maxDrain, doDrain);
				}
			});
		}
		return super.getCapability(capability, from);
	}

}
