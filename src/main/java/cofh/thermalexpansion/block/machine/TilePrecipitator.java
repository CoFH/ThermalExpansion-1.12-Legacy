package cofh.thermalexpansion.block.machine;

import cofh.core.fluid.FluidTankCore;
import cofh.core.gui.container.ICustomInventory;
import cofh.core.network.PacketBase;
import cofh.core.util.helpers.FluidHelper;
import cofh.core.util.helpers.ItemHelper;
import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalexpansion.block.machine.BlockMachine.Type;
import cofh.thermalexpansion.gui.client.machine.GuiPrecipitator;
import cofh.thermalexpansion.gui.container.machine.ContainerPrecipitator;
import cofh.thermalexpansion.init.TEProps;
import cofh.thermalexpansion.util.managers.machine.PrecipitatorManager;
import cofh.thermalexpansion.util.managers.machine.PrecipitatorManager.PrecipitatorRecipe;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
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
import java.util.Arrays;
import java.util.HashSet;

public class TilePrecipitator extends TileMachineBase implements ICustomInventory {

	private static final int TYPE = Type.PRECIPITATOR.getMetadata();
	public static int basePower = 20;

	public static void initialize() {

		SIDE_CONFIGS[TYPE] = new SideConfig();
		SIDE_CONFIGS[TYPE].numConfig = 5;
		SIDE_CONFIGS[TYPE].slotGroups = new int[][] { {}, {}, { 0 }, { 0 }, { 0 } };
		SIDE_CONFIGS[TYPE].sideTypes = new int[] { NONE, INPUT_ALL, OUTPUT_ALL, OPEN, OMNI };
		SIDE_CONFIGS[TYPE].defaultSides = new byte[] { 1, 1, 2, 2, 2, 2 };

		ALT_SIDE_CONFIGS[TYPE] = new SideConfig();
		ALT_SIDE_CONFIGS[TYPE].numConfig = 2;
		ALT_SIDE_CONFIGS[TYPE].slotGroups = new int[][] { {}, {}, { 0 }, { 0 }, { 0 } };
		ALT_SIDE_CONFIGS[TYPE].sideTypes = new int[] { NONE, OPEN };
		ALT_SIDE_CONFIGS[TYPE].defaultSides = new byte[] { 1, 1, 1, 1, 1, 1 };

		SLOT_CONFIGS[TYPE] = new SlotConfig();
		SLOT_CONFIGS[TYPE].allowInsertionSlot = new boolean[] { false, false };
		SLOT_CONFIGS[TYPE].allowExtractionSlot = new boolean[] { true, false };

		VALID_AUGMENTS[TYPE] = new HashSet<>();

		GameRegistry.registerTileEntity(TilePrecipitator.class, "thermalexpansion:machine_precipitator");

		config();
	}

	public static void config() {

		String category = "Machine.Precipitator";
		BlockMachine.enable[TYPE] = ThermalExpansion.CONFIG.get(category, "Enable", true);

		String comment = "Adjust this value to change the Energy consumption (in RF/t) for a Glacial Precipitator. This base value will scale with block level and Augments.";
		basePower = ThermalExpansion.CONFIG.getConfiguration().getInt("BasePower", category, basePower, MIN_BASE_POWER, MAX_BASE_POWER, comment);

		ENERGY_CONFIGS[TYPE] = new EnergyConfig();
		ENERGY_CONFIGS[TYPE].setDefaultParams(basePower, smallStorage);
	}

	private ItemStack[] outputItem = new ItemStack[2];
	private int outputTracker;
	private int index = 0;
	private byte direction = 0;

	private FluidTankCore tank = new FluidTankCore(TEProps.MAX_FLUID_MEDIUM);

	public TilePrecipitator() {

		super();
		inventory = new ItemStack[1 + 1];
		Arrays.fill(inventory, ItemStack.EMPTY);
		createAllSlots(inventory.length);

		outputItem[0] = PrecipitatorManager.getOutput(index);
		outputItem[1] = outputItem[0].copy();
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

		PrecipitatorRecipe recipe = PrecipitatorManager.getRecipe(outputItem[0]);

		if (recipe == null) {
			return false;
		}
		if (!FluidHelper.isFluidEqual(tank.getFluid(), recipe.getInput())) {
			return false;
		}

		if (tank.getFluidAmount() < recipe.getInput().amount || energyStorage.getEnergyStored() <= 0) {
			return false;
		}
		if (inventory[0].isEmpty()) {
			return true;
		}
		if (!inventory[0].isItemEqual(outputItem[0])) {
			return false;
		}
		return inventory[0].getCount() + outputItem[0].getCount() <= outputItem[0].getMaxStackSize();
	}

	@Override
	protected boolean canFinish() {

		return processRem <= 0;
	}

	@Override
	protected void processStart() {

		processMax = PrecipitatorManager.getRecipe(outputItem[0]).getEnergy() * energyMod / ENERGY_BASE;
		processRem = processMax;
		outputItem[1] = outputItem[0].copy();
	}

	@Override
	protected void processFinish() {

		PrecipitatorRecipe recipe = PrecipitatorManager.getRecipe(outputItem[1]);

		if (recipe == null) {
			processOff();
			return;
		}
		ItemStack output = recipe.getOutput();
		if (inventory[0].isEmpty()) {
			inventory[0] = ItemHelper.cloneStack(output);
		} else {
			inventory[0].grow(output.getCount());
		}
		tank.drain(recipe.getInput().amount, true);
		outputItem[1] = outputItem[0].copy();
	}

	@Override
	protected void transferOutput() {

		if (!getTransferOut()) {
			return;
		}
		if (inventory[0].isEmpty()) {
			return;
		}
		int side;
		for (int i = outputTracker + 1; i <= outputTracker + 6; i++) {
			side = i % 6;
			if (isPrimaryOutput(sideConfig.sideTypes[sideCache[side]])) {
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
		if (tag.hasKey("OutputItem", 10)) {
			index = PrecipitatorManager.getIndex(new ItemStack(tag.getCompoundTag("OutputItem")));
			outputItem[0] = PrecipitatorManager.getOutput(index);
			if (!isActive) {
				outputItem[1] = outputItem[0].copy();
			}
		}
		return true;
	}

	@Override
	protected boolean writePortableTagInternal(EntityPlayer player, NBTTagCompound tag) {

		if (!super.writePortableTagInternal(player, tag)) {
			return false;
		}
		tag.setTag("OutputItem", outputItem[0].writeToNBT(new NBTTagCompound()));
		return true;
	}

	@Override
	protected void setLevelFlags() {

		super.setLevelFlags();

		hasAutoInput = false;
		enableAutoInput = false;
	}

	private void setOutput() {

		if (index >= PrecipitatorManager.getOutputListSize()) {
			index = 0;
		} else if (index < 0) {
			index = PrecipitatorManager.getOutputListSize() - 1;
		}
		outputItem[0] = PrecipitatorManager.getOutput(index);
		if (!isActive) {
			outputItem[1] = outputItem[0].copy();
		}
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

	@Override
	public FluidTankCore getTank() {

		return tank;
	}

	@Override
	public FluidStack getTankFluid() {

		return tank.getFluid();
	}

	public void setMode(byte direction) {

		this.direction = direction;
		sendModePacket();
		this.direction = 0;
	}

	/* NBT METHODS */
	@Override
	public void readFromNBT(NBTTagCompound nbt) {

		super.readFromNBT(nbt);

		outputTracker = nbt.getInteger("TrackOut");

		if (nbt.hasKey("OutputItem", 10)) {
			index = PrecipitatorManager.getIndex(new ItemStack(nbt.getCompoundTag("OutputItem")));
		}
		outputItem[0] = PrecipitatorManager.getOutput(index);
		outputItem[1] = outputItem[0].copy();

		tank.readFromNBT(nbt);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {

		super.writeToNBT(nbt);

		nbt.setInteger("TrackOut", outputTracker);

		nbt.setTag("OutputItem", outputItem[0].writeToNBT(new NBTTagCompound()));
		tank.writeToNBT(nbt);
		return nbt;
	}

	/* NETWORK METHODS */

	/* CLIENT -> SERVER */
	@Override
	public PacketBase getModePacket() {

		PacketBase payload = super.getModePacket();

		payload.addByte(direction);

		return payload;
	}

	@Override
	protected void handleModePacket(PacketBase payload) {

		super.handleModePacket(payload);

		direction = payload.getByte();
		index += direction;
		setOutput();
		direction = 0;
	}

	/* SERVER -> CLIENT */
	@Override
	public PacketBase getGuiPacket() {

		PacketBase payload = super.getGuiPacket();

		payload.addFluidStack(tank.getFluid());

		return payload;
	}

	@Override
	protected void handleGuiPacket(PacketBase payload) {

		super.handleGuiPacket(payload);

		tank.setFluid(payload.getFluidStack());
	}

	/* ICustomInventory */
	@Override
	public ItemStack[] getInventorySlots(int inventoryIndex) {

		return outputItem;
	}

	@Override
	public int getSlotStackLimit(int slotIndex) {

		return 64;
	}

	@Override
	public void onSlotUpdate(int slotIndex) {

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

					if (from == null || allowInsertion(sideConfig.sideTypes[sideCache[from.ordinal()]])) {
						return tank.fill(resource, doFill);
					}
					return 0;
				}

				@Nullable
				@Override
				public FluidStack drain(FluidStack resource, boolean doDrain) {

					if (isActive) {
						return null;
					}
					if (from == null || allowExtraction(sideConfig.sideTypes[sideCache[from.ordinal()]])) {
						return tank.drain(resource, doDrain);
					}
					return null;
				}

				@Nullable
				@Override
				public FluidStack drain(int maxDrain, boolean doDrain) {

					if (isActive) {
						return null;
					}
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
