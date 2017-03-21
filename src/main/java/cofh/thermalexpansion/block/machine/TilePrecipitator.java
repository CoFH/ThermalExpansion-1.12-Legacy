package cofh.thermalexpansion.block.machine;

import cofh.core.fluid.FluidTankCore;
import cofh.core.network.PacketCoFHBase;
import cofh.lib.gui.container.ICustomInventory;
import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalexpansion.gui.client.machine.GuiPrecipitator;
import cofh.thermalexpansion.gui.container.machine.ContainerPrecipitator;
import cofh.thermalexpansion.init.TEProps;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.FluidTankProperties;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import net.minecraftforge.fml.common.registry.GameRegistry;

import javax.annotation.Nullable;
import java.util.ArrayList;

public class TilePrecipitator extends TileMachineBase implements ICustomInventory {

	private static final int TYPE = BlockMachine.Type.PRECIPITATOR.getMetadata();
	public static int basePower = 20;

	public static void initialize() {

		processItems[0] = new ItemStack(Items.SNOWBALL, 4, 0);
		processItems[1] = new ItemStack(Blocks.SNOW);
		processItems[2] = new ItemStack(Blocks.ICE);

		SIDE_CONFIGS[TYPE] = new SideConfig();
		SIDE_CONFIGS[TYPE].numConfig = 4;
		SIDE_CONFIGS[TYPE].slotGroups = new int[][] { {}, {}, { 0 }, { 0 } };
		SIDE_CONFIGS[TYPE].allowInsertionSide = new boolean[] { false, true, false, true };
		SIDE_CONFIGS[TYPE].allowExtractionSide = new boolean[] { false, false, true, true };
		SIDE_CONFIGS[TYPE].sideTex = new int[] { 0, 1, 4, 7 };
		SIDE_CONFIGS[TYPE].defaultSides = new byte[] { 1, 1, 2, 2, 2, 2 };

		SLOT_CONFIGS[TYPE] = new SlotConfig();
		SLOT_CONFIGS[TYPE].allowInsertionSlot = new boolean[] { false, false };
		SLOT_CONFIGS[TYPE].allowExtractionSlot = new boolean[] { true, false };

		VALID_AUGMENTS[TYPE] = new ArrayList<>();

		GameRegistry.registerTileEntity(TilePrecipitator.class, "thermalexpansion:machine_precipitator");

		config();
	}

	public static void config() {

		String category = "Machine.Precipitator";
		BlockMachine.enable[TYPE] = ThermalExpansion.CONFIG.get(category, "Enable", true);

		ENERGY_CONFIGS[TYPE] = new EnergyConfig();
		ENERGY_CONFIGS[TYPE].setDefaultParams(basePower);
	}

	private static int[] processWater = { 500, 500, 1000 };
	private static int[] processEnergy = { 800, 800, 1600 };
	private static ItemStack[] processItems = new ItemStack[3];

	private int outputTracker;
	private byte curSelection;
	private byte prevSelection;

	private FluidTankCore tank = new FluidTankCore(TEProps.MAX_FLUID_SMALL);

	public TilePrecipitator() {

		super();
		inventory = new ItemStack[1 + 1];
		createAllSlots(inventory.length);
		tank.setLock(FluidRegistry.WATER);
	}

	@Override
	public int getType() {

		return TYPE;
	}

	@Override
	protected int getMaxInputSlot() {

		// This is a hack to prevent super() logic from working.
		return -1;
	}

	@Override
	protected boolean canStart() {

		if (tank.getFluidAmount() < processWater[curSelection] || energyStorage.getEnergyStored() <= 0) {
			return false;
		}
		if (inventory[0] == null) {
			return true;
		}
		if (!inventory[0].isItemEqual(processItems[curSelection])) {
			return false;
		}
		return inventory[0].stackSize + processItems[curSelection].stackSize <= processItems[prevSelection].getMaxStackSize();
	}

	@Override
	protected boolean canFinish() {

		return processRem <= 0;
	}

	@Override
	protected void processStart() {

		processMax = processEnergy[curSelection] * energyMod / ENERGY_BASE;
		processRem = processMax;
		prevSelection = curSelection;
	}

	@Override
	protected void processFinish() {

		if (inventory[0] == null) {
			inventory[0] = processItems[prevSelection].copy();
		} else {
			inventory[0].stackSize += processItems[prevSelection].stackSize;
		}
		tank.drain(processWater[prevSelection], true);
		prevSelection = curSelection;
	}

	@Override
	protected void transferOutput() {

		if (!enableAutoOutput) {
			return;
		}
		if (inventory[0] == null) {
			return;
		}
		int side;
		for (int i = outputTracker + 1; i <= outputTracker + 6; i++) {
			side = i % 6;
			if (sideCache[side] == 2) {
				if (transferItem(0, ITEM_TRANSFER[level], EnumFacing.VALUES[side])) {
					outputTracker = side;
					break;
				}
			}
		}
	}

	@Override
	protected boolean readPortableTagInternal(EntityPlayer player, NBTTagCompound tag) {

		if (!super.readPortableTagInternal(player, tag)) {
			return false;
		}
		if (tag.hasKey("Sel")) {
			curSelection = tag.getByte("Sel");
			if (!isActive) {
				prevSelection = curSelection;
			}
		}
		return true;
	}

	@Override
	protected boolean writePortableTagInternal(EntityPlayer player, NBTTagCompound tag) {

		if (!super.writePortableTagInternal(player, tag)) {
			return false;
		}
		tag.setByte("Sel", curSelection);
		return true;
	}

	@Override
	protected void setLevelFlags() {

		super.setLevelFlags();

		hasAutoInput = false;
		enableAutoInput = false;
	}

	/* GUI METHODS */
	@Override
	public Object getGuiClient(InventoryPlayer inventory) {

		return new GuiPrecipitator(inventory, this);
	}

	@Override
	public Object getGuiServer(InventoryPlayer inventory) {

		return new ContainerPrecipitator(inventory, this);
	}

	public int getCurSelection() {

		return curSelection;
	}

	public int getPrevSelection() {

		return prevSelection;
	}

	@Override
	public FluidTankCore getTank() {

		return tank;
	}

	@Override
	public FluidStack getTankFluid() {

		return tank.getFluid();
	}

	public void setMode(int selection) {

		byte lastSelection = curSelection;
		curSelection = (byte) selection;
		sendModePacket();
		curSelection = lastSelection;
	}

	/* NBT METHODS */
	@Override
	public void readFromNBT(NBTTagCompound nbt) {

		super.readFromNBT(nbt);

		outputTracker = nbt.getInteger("TrackOut");
		prevSelection = nbt.getByte("Prev");
		curSelection = nbt.getByte("Sel");

		tank.readFromNBT(nbt);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {

		super.writeToNBT(nbt);

		nbt.setInteger("TrackOut", outputTracker);
		nbt.setByte("Prev", prevSelection);
		nbt.setByte("Sel", curSelection);

		tank.writeToNBT(nbt);
		return nbt;
	}

	/* NETWORK METHODS */

	/* CLIENT -> SERVER */
	@Override
	public PacketCoFHBase getModePacket() {

		PacketCoFHBase payload = super.getModePacket();

		payload.addByte(curSelection);

		return payload;
	}

	@Override
	protected void handleModePacket(PacketCoFHBase payload) {

		super.handleModePacket(payload);

		curSelection = payload.getByte();
		if (!isActive) {
			prevSelection = curSelection;
		}
	}

	/* SERVER -> CLIENT */
	@Override
	public PacketCoFHBase getGuiPacket() {

		PacketCoFHBase payload = super.getGuiPacket();

		payload.addByte(curSelection);
		payload.addByte(prevSelection);
		payload.addInt(tank.getFluidAmount());

		return payload;
	}

	@Override
	protected void handleGuiPacket(PacketCoFHBase payload) {

		super.handleGuiPacket(payload);

		curSelection = payload.getByte();
		prevSelection = payload.getByte();
		tank.getFluid().amount = payload.getInt();
	}

	/* ICustomInventory */
	@Override
	public ItemStack[] getInventorySlots(int inventoryIndex) {

		return processItems;
	}

	@Override
	public int getSlotStackLimit(int slotIndex) {

		return 64;
	}

	@Override
	public void onSlotUpdate() {

		markChunkDirty();
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
					return new IFluidTankProperties[] { new FluidTankProperties(info.fluid, info.capacity, true, false) };
				}

				@Override
				public int fill(FluidStack resource, boolean doFill) {

					if (from != null && sideCache[from.ordinal()] != 1) {
						return 0;
					}
					if (resource.getFluid() != FluidRegistry.WATER) {
						return 0;
					}
					return tank.fill(resource, doFill);
				}

				@Nullable
				@Override
				public FluidStack drain(FluidStack resource, boolean doDrain) {

					return null;
				}

				@Nullable
				@Override
				public FluidStack drain(int maxDrain, boolean doDrain) {

					return null;
				}
			});
		}
		return super.getCapability(capability, from);
	}
}
