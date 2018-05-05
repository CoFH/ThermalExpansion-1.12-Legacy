package cofh.thermalexpansion.block.machine;

import cofh.core.fluid.FluidTankCore;
import cofh.core.init.CoreProps;
import cofh.core.network.PacketBase;
import cofh.core.util.core.EnergyConfig;
import cofh.core.util.core.SideConfig;
import cofh.core.util.core.SlotConfig;
import cofh.core.util.helpers.AugmentHelper;
import cofh.core.util.helpers.FluidHelper;
import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalexpansion.block.machine.BlockMachine.Type;
import cofh.thermalexpansion.gui.client.machine.GuiBrewer;
import cofh.thermalexpansion.gui.container.machine.ContainerBrewer;
import cofh.thermalexpansion.init.TEProps;
import cofh.thermalexpansion.util.managers.machine.BrewerManager;
import cofh.thermalexpansion.util.managers.machine.BrewerManager.BrewerRecipe;
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

import static cofh.core.util.core.SideConfig.*;

public class TileBrewer extends TileMachineBase {

	private static final int TYPE = Type.BREWER.getMetadata();
	public static int basePower = 20;

	public static void initialize() {

		SIDE_CONFIGS[TYPE] = new SideConfig();
		SIDE_CONFIGS[TYPE].numConfig = 7;
		SIDE_CONFIGS[TYPE].slotGroups = new int[][] { {}, { 0 }, {}, { 0 }, {}, { 0 }, { 0 } };
		SIDE_CONFIGS[TYPE].sideTypes = new int[] { NONE, INPUT_ALL, OUTPUT_ALL, INPUT_PRIMARY, INPUT_SECONDARY, OPEN, OMNI };
		SIDE_CONFIGS[TYPE].defaultSides = new byte[] { 1, 1, 2, 2, 2, 2 };

		ALT_SIDE_CONFIGS[TYPE] = new SideConfig();
		ALT_SIDE_CONFIGS[TYPE].numConfig = 2;
		ALT_SIDE_CONFIGS[TYPE].slotGroups = new int[][] { {}, { 0 }, {}, { 0 }, { 0 } };
		ALT_SIDE_CONFIGS[TYPE].sideTypes = new int[] { NONE, OPEN };
		ALT_SIDE_CONFIGS[TYPE].defaultSides = new byte[] { 1, 1, 1, 1, 1, 1 };

		SLOT_CONFIGS[TYPE] = new SlotConfig();
		SLOT_CONFIGS[TYPE].allowInsertionSlot = new boolean[] { true, false };
		SLOT_CONFIGS[TYPE].allowExtractionSlot = new boolean[] { false, false };

		VALID_AUGMENTS[TYPE] = new HashSet<>();
		VALID_AUGMENTS[TYPE].add(TEProps.MACHINE_BREWER_REAGENT);

		LIGHT_VALUES[TYPE] = 12;

		GameRegistry.registerTileEntity(TileBrewer.class, "thermalexpansion:machine_brewer");

		config();
	}

	public static void config() {

		String category = "Machine.Brewer";
		BlockMachine.enable[TYPE] = ThermalExpansion.CONFIG.get(category, "Enable", true);

		String comment = "Adjust this value to change the Energy consumption (in RF/t) for a BREWER. This base value will scale with block level and Augments.";
		basePower = ThermalExpansion.CONFIG.getConfiguration().getInt("BasePower", category, basePower, MIN_BASE_POWER, MAX_BASE_POWER, comment);

		ENERGY_CONFIGS[TYPE] = new EnergyConfig();
		ENERGY_CONFIGS[TYPE].setDefaultParams(basePower, smallStorage);
	}

	private BrewerRecipe curRecipe;
	private int inputTracker;
	private int inputTrackerFluid;
	private int outputTrackerFluid;

	private FluidTankCore inputTank = new FluidTankCore(TEProps.MAX_FLUID_SMALL);
	private FluidTankCore outputTank = new FluidTankCore(TEProps.MAX_FLUID_LARGE);
	private FluidStack renderFluid = new FluidStack(FluidRegistry.WATER, 0);

	public TileBrewer() {

		super();
		inventory = new ItemStack[1 + 1];
		Arrays.fill(inventory, ItemStack.EMPTY);
		createAllSlots(inventory.length);
	}

	@Override
	public int getType() {

		return TYPE;
	}

	@Override
	protected boolean canStart() {

		if (inventory[0].isEmpty() || energyStorage.getEnergyStored() <= 0 || outputTank.getSpace() <= 0) {
			return false;
		}
		getRecipe();

		if (curRecipe == null) {
			return false;
		}
		if (inventory[0].getCount() < curRecipe.getInput().getCount()) {
			return false;
		}
		if (inputTank.getFluidAmount() < curRecipe.getInputFluid().amount) {
			return false;
		}
		FluidStack outputFluid = curRecipe.getOutputFluid();
		return outputTank.fill(outputFluid, false) == outputFluid.amount;
	}

	@Override
	protected boolean hasValidInput() {

		if (curRecipe == null) {
			getRecipe();
		}
		if (curRecipe == null) {
			return false;
		}
		return curRecipe.getInput().getCount() <= inventory[0].getCount();
	}

	@Override
	protected void clearRecipe() {

		curRecipe = null;
	}

	@Override
	protected void getRecipe() {

		curRecipe = BrewerManager.getRecipe(inventory[0], inputTank.getFluid());
	}

	@Override
	protected void processStart() {

		processMax = curRecipe.getEnergy() * energyMod / ENERGY_BASE;
		processRem = processMax;

		FluidStack prevStack = renderFluid.copy();
		renderFluid = curRecipe.getOutputFluid().copy();
		renderFluid.amount = 0;

		if (!FluidHelper.isFluidEqual(prevStack, renderFluid)) {
			sendFluidPacket();
		}
	}

	@Override
	protected void processFinish() {

		if (curRecipe == null) {
			getRecipe();
		}
		if (curRecipe == null) {
			processOff();
			return;
		}
		outputTank.fill(curRecipe.getOutputFluid(), true);
		inputTank.drain(curRecipe.getInputFluid().amount, true);

		int count = curRecipe.getInput().getCount();

		if (reuseChance > 0) {
			if (world.rand.nextInt(SECONDARY_BASE) >= reuseChance) {
				inventory[0].shrink(count);
			}
		} else {
			inventory[0].shrink(count);
		}
		if (inventory[0].getCount() <= 0) {
			inventory[0] = ItemStack.EMPTY;
		}
	}

	@Override
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

	@Override
	protected void transferOutput() {

		if (!getTransferOut()) {
			return;
		}
		transferOutputFluid();
	}

	private void transferInputFluid() {

		if (inputTank.getSpace() <= 0) {
			return;
		}
		int side;
		for (int i = inputTrackerFluid + 1; i <= inputTrackerFluid + 6; i++) {
			side = i % 6;
			if (isSecondaryInput(sideConfig.sideTypes[sideCache[side]])) {
				FluidStack input = FluidHelper.extractFluidFromAdjacentFluidHandler(this, EnumFacing.VALUES[side], FLUID_TRANSFER[level], false);
				if (input != null) { // TODO: Add validation logic.
					int toFill = inputTank.fill(input, true);
					if (toFill > 0) {
						FluidHelper.extractFluidFromAdjacentFluidHandler(this, EnumFacing.VALUES[side], toFill, true);
						inputTracker = side;
						break;
					}
				}
			}
		}
	}

	private void transferOutputFluid() {

		if (outputTank.getFluidAmount() <= 0) {
			return;
		}
		int side;
		FluidStack output = new FluidStack(outputTank.getFluid(), Math.min(outputTank.getFluidAmount(), FLUID_TRANSFER[level]));
		for (int i = outputTrackerFluid + 1; i <= outputTrackerFluid + 6; i++) {
			side = i % 6;
			if (isPrimaryOutput(sideConfig.sideTypes[sideCache[side]])) {
				int toDrain = FluidHelper.insertFluidIntoAdjacentFluidHandler(this, EnumFacing.VALUES[side], output, true);
				if (toDrain > 0) {
					outputTank.drain(toDrain, true);
					outputTrackerFluid = side;
					break;
				}
			}
		}
	}

	@Override
	public void update() {

		if (timeCheckEighth()) {
			transferOutput();
		}
		super.update();
	}

	/* GUI METHODS */
	@Override
	public Object getGuiClient(InventoryPlayer inventory) {

		return new GuiBrewer(inventory, this);
	}

	@Override
	public Object getGuiServer(InventoryPlayer inventory) {

		return new ContainerBrewer(inventory, this);
	}

	public FluidTankCore getTank(int tankIndex) {

		if (tankIndex == 0) {
			return inputTank;
		}
		return outputTank;
	}

	public FluidStack getTankFluid(int tankIndex) {

		if (tankIndex == 0) {
			return inputTank.getFluid();
		}
		return outputTank.getFluid();
	}

	/* NBT METHODS */
	@Override
	public void readFromNBT(NBTTagCompound nbt) {

		super.readFromNBT(nbt);

		inputTracker = nbt.getInteger(CoreProps.TRACK_IN);
		inputTrackerFluid = nbt.getInteger("TrackInFluid");
		outputTrackerFluid = nbt.getInteger("TrackOutFluid");

		inputTank.readFromNBT(nbt.getCompoundTag("TankIn"));
		outputTank.readFromNBT(nbt.getCompoundTag("TankOut"));
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {

		super.writeToNBT(nbt);

		nbt.setInteger(CoreProps.TRACK_IN, inputTracker);
		nbt.setInteger("TrackInFluid", inputTrackerFluid);
		nbt.setInteger("TrackOutFluid", outputTrackerFluid);

		nbt.setTag("TankIn", inputTank.writeToNBT(new NBTTagCompound()));
		nbt.setTag("TankOut", outputTank.writeToNBT(new NBTTagCompound()));
		return nbt;
	}

	/* NETWORK METHODS */

	/* SERVER -> CLIENT */
	@Override
	public PacketBase getFluidPacket() {

		PacketBase payload = super.getFluidPacket();

		payload.addFluidStack(renderFluid);

		return payload;
	}

	@Override
	public PacketBase getGuiPacket() {

		PacketBase payload = super.getGuiPacket();

		payload.addFluidStack(inputTank.getFluid());

		if (outputTank.getFluid() == null) {
			payload.addFluidStack(renderFluid);
		} else {
			payload.addFluidStack(outputTank.getFluid());
		}
		return payload;
	}

	@Override
	protected void handleFluidPacket(PacketBase payload) {

		super.handleFluidPacket(payload);

		renderFluid = payload.getFluidStack();

		callBlockUpdate();
	}

	@Override
	protected void handleGuiPacket(PacketBase payload) {

		super.handleGuiPacket(payload);

		inputTank.setFluid(payload.getFluidStack());
		outputTank.setFluid(payload.getFluidStack());
	}

	/* HELPERS */
	@Override
	protected void preAugmentInstall() {

		super.preAugmentInstall();

	}

	@Override
	protected boolean installAugmentToSlot(int slot) {

		String id = AugmentHelper.getAugmentIdentifier(augments[slot]);

		if (TEProps.MACHINE_BREWER_REAGENT.equals(id)) {
			reuseChance += 15;
			energyMod += 10;
		}
		return super.installAugmentToSlot(slot);
	}

	/* IInventory */
	@Override
	public boolean isItemValidForSlot(int slot, ItemStack stack) {

		return slot != 0 || BrewerManager.isItemValid(stack);
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

					FluidTankInfo inputInfo = inputTank.getInfo();
					FluidTankInfo outputInfo = outputTank.getInfo();
					return new IFluidTankProperties[] { new FluidTankProperties(inputInfo.fluid, inputInfo.capacity, true, false), new FluidTankProperties(outputInfo.fluid, outputInfo.capacity, false, true) };
				}

				@Override
				public int fill(FluidStack resource, boolean doFill) {

					if (from == null || allowInsertion(sideConfig.sideTypes[sideCache[from.ordinal()]])) {
						if (!BrewerManager.isFluidValid(resource)) {
							return 0;
						}
						return inputTank.fill(resource, doFill);
					}
					return 0;
				}

				@Nullable
				@Override
				public FluidStack drain(FluidStack resource, boolean doDrain) {

					if (from == null || allowExtraction(sideConfig.sideTypes[sideCache[from.ordinal()]])) {
						FluidStack ret = outputTank.drain(resource, doDrain);

						if (ret != null) {
							return ret;
						}
						if (!isActive && (from == null || allowInsertion(sideConfig.sideTypes[sideCache[from.ordinal()]]))) {
							return inputTank.drain(resource, doDrain);
						}
					}
					return null;
				}

				@Nullable
				@Override
				public FluidStack drain(int maxDrain, boolean doDrain) {

					if (from == null || allowExtraction(sideConfig.sideTypes[sideCache[from.ordinal()]])) {
						FluidStack ret = outputTank.drain(maxDrain, doDrain);

						if (ret != null) {
							return ret;
						}
						if (!isActive && (from == null || allowInsertion(sideConfig.sideTypes[sideCache[from.ordinal()]]))) {
							return inputTank.drain(maxDrain, doDrain);
						}
					}
					return null;
				}
			});
		}
		return super.getCapability(capability, from);
	}

}
