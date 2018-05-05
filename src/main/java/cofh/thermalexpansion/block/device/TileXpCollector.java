package cofh.thermalexpansion.block.device;

import cofh.core.fluid.FluidTankCore;
import cofh.core.init.CoreProps;
import cofh.core.network.PacketBase;
import cofh.core.util.core.SideConfig;
import cofh.core.util.core.SlotConfig;
import cofh.core.util.helpers.FluidHelper;
import cofh.core.util.helpers.MathHelper;
import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalexpansion.block.device.BlockDevice.Type;
import cofh.thermalexpansion.gui.client.device.GuiXpCollector;
import cofh.thermalexpansion.gui.container.device.ContainerXpCollector;
import cofh.thermalexpansion.init.TEProps;
import cofh.thermalexpansion.util.managers.device.XpCollectorManager;
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

import static cofh.core.util.core.SideConfig.*;

public class TileXpCollector extends TileDeviceBase implements ITickable {

	private static final int TYPE = Type.XP_COLLECTOR.getMetadata();

	public static void initialize() {

		SIDE_CONFIGS[TYPE] = new SideConfig();
		SIDE_CONFIGS[TYPE].numConfig = 5;
		SIDE_CONFIGS[TYPE].slotGroups = new int[][] { {}, { 0 }, {}, { 0 }, { 0 } };
		SIDE_CONFIGS[TYPE].sideTypes = new int[] { NONE, INPUT_ALL, OUTPUT_ALL, OPEN, OMNI };
		SIDE_CONFIGS[TYPE].defaultSides = new byte[] { 1, 1, 2, 2, 2, 2 };

		SLOT_CONFIGS[TYPE] = new SlotConfig();
		SLOT_CONFIGS[TYPE].allowInsertionSlot = new boolean[] { true };
		SLOT_CONFIGS[TYPE].allowExtractionSlot = new boolean[] { false };

		LIGHT_VALUES[TYPE] = 2;

		GameRegistry.registerTileEntity(TileXpCollector.class, "thermalexpansion:device_xp_collector");

		config();
	}

	public static void config() {

		String category = "Device.XpCollector";
		BlockDevice.enable[TYPE] = ThermalExpansion.CONFIG.get(category, "Enable", true);
	}

	private static final int RADIUS = 5;
	private static final int TIME_CONSTANT = 16;

	private int inputTracker;
	private int outputTracker;

	private int xpBuffer;
	private int maxBoostXp;
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

		if (!getTransferIn()) {
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

		if (!getTransferOut() || tank.getFluidAmount() <= 0) {
			return;
		}
		int side;
		FluidStack output = new FluidStack(tank.getFluid(), Math.min(tank.getFluidAmount(), tank.getCapacity()));
		for (int i = outputTracker + 1; i <= outputTracker + 6; i++) {
			side = i % 6;
			if (isPrimaryOutput(sideConfig.sideTypes[sideCache[side]])) {
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

		AxisAlignedBB area = new AxisAlignedBB(pos.add(-RADIUS, -RADIUS, -RADIUS), pos.add(1 + RADIUS, 1 + RADIUS, 1 + RADIUS));
		List<EntityXPOrb> xpOrbs = world.getEntitiesWithinAABB(EntityXPOrb.class, area, EntitySelectors.IS_ALIVE);

		for (EntityXPOrb orb : xpOrbs) {
			xpBuffer += orb.getXpValue();
			orb.setDead();
		}
	}

	protected void collectMachineXp() {

	}

	protected void convertXp() {

		if (boostXp <= 0 && XpCollectorManager.getCatalystFactor(inventory[0]) > 0) {
			boostXp = XpCollectorManager.getCatalystXp(inventory[0]);
			boostFactor = XpCollectorManager.getCatalystFactor(inventory[0]);

			inventory[0].shrink(1);
			if (inventory[0].getCount() <= 0) {
				inventory[0] = ItemStack.EMPTY;
			}
		}
		int conversion = (CoreProps.MB_PER_XP * (100 + boostFactor)) / 100;
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

	@Override
	public int getScaledSpeed(int scale) {

		if (maxBoostXp <= 0) {
			maxBoostXp = Math.max(boostXp, 100);
		}
		return boostXp * scale / maxBoostXp;
	}

	public int getBoostFactor() {

		return boostFactor;
	}

	/* NBT METHODS */
	@Override
	public void readFromNBT(NBTTagCompound nbt) {

		super.readFromNBT(nbt);

		inputTracker = nbt.getInteger(CoreProps.TRACK_IN);
		outputTracker = nbt.getInteger(CoreProps.TRACK_OUT);
		tank.readFromNBT(nbt);

		boostFactor = nbt.getInteger("BoostFactor");
		boostXp = nbt.getInteger("BoostXp");
		maxBoostXp = nbt.getInteger("BoostXpMax");

		if (maxBoostXp <= 0) {
			maxBoostXp = Math.max(boostXp, 100);
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {

		super.writeToNBT(nbt);

		nbt.setInteger(CoreProps.TRACK_IN, inputTracker);
		nbt.setInteger(CoreProps.TRACK_OUT, outputTracker);
		tank.writeToNBT(nbt);

		nbt.setInteger("BoostFactor", boostFactor);
		nbt.setInteger("BoostXp", boostXp);
		nbt.setInteger("BoostXpMax", maxBoostXp);

		return nbt;
	}

	/* NETWORK METHODS */
	@Override
	public PacketBase getGuiPacket() {

		PacketBase payload = super.getGuiPacket();

		payload.addInt(boostXp);
		payload.addInt(boostFactor);
		payload.addFluidStack(tank.getFluid());

		return payload;
	}

	@Override
	protected void handleGuiPacket(PacketBase payload) {

		super.handleGuiPacket(payload);

		boostXp = payload.getInt();
		boostFactor = payload.getInt();
		tank.setFluid(payload.getFluidStack());
	}

	/* IInventory */
	@Override
	public boolean isItemValidForSlot(int slot, ItemStack stack) {

		return XpCollectorManager.getCatalystFactor(stack) > 0;
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

					if (from == null || allowExtraction(sideConfig.sideTypes[sideCache[from.ordinal()]])) {
						return tank.drain(resource, doDrain);
					}
					return null;
				}

				@Nullable
				@Override
				public FluidStack drain(int maxDrain, boolean doDrain) {

					if (from == null || allowExtraction(sideConfig.sideTypes[sideCache[from.ordinal()]])) {
						return tank.drain(maxDrain, doDrain);
					}
					return null;
				}
			});
		}
		return super.getCapability(capability, from);
	}

}
