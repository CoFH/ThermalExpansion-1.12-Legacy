package cofh.thermalexpansion.block.machine;

import cofh.core.fluid.FluidTankCore;
import cofh.core.network.PacketCoFHBase;
import cofh.core.util.helpers.FluidHelper;
import cofh.core.util.helpers.ServerHelper;
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

public class TileBrewer extends TileMachineBase {

	private static final int TYPE = Type.BREWER.getMetadata();
	public static int basePower = 20;

	public static void initialize() {

		SIDE_CONFIGS[TYPE] = new SideConfig();
		SIDE_CONFIGS[TYPE].numConfig = 7;
		SIDE_CONFIGS[TYPE].slotGroups = new int[][] { {}, { 0 }, {}, { 0 }, {}, { 0 }, { 0 } };
		SIDE_CONFIGS[TYPE].sideTypes = new int[] { 0, 1, 4, 5, 6, 7, 8 };
		SIDE_CONFIGS[TYPE].defaultSides = new byte[] { 1, 1, 2, 2, 2, 2 };

		SLOT_CONFIGS[TYPE] = new SlotConfig();
		SLOT_CONFIGS[TYPE].allowInsertionSlot = new boolean[] { true, false };
		SLOT_CONFIGS[TYPE].allowExtractionSlot = new boolean[] { false, false };

		VALID_AUGMENTS[TYPE] = new HashSet<>();

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

	private int inputTracker;
	private int inputTrackerFluid;
	private int outputTrackerFluid;

	private FluidTankCore inputTank = new FluidTankCore(TEProps.MAX_FLUID_SMALL);
	private FluidTankCore outputTank = new FluidTankCore(TEProps.MAX_FLUID_LARGE);

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
	public void update() {

		if (ServerHelper.isClientWorld(world)) {
			return;
		}
		transferOutputFluid();

		super.update();
	}

	@Override
	protected boolean canStart() {

		if (inventory[0].isEmpty() || energyStorage.getEnergyStored() <= 0) {
			return false;
		}
		BrewerRecipe recipe = BrewerManager.getRecipe(inventory[0], inputTank.getFluid());

		if (recipe == null) {
			return false;
		}
		if (inventory[0].getCount() < recipe.getInput().getCount()) {
			return false;
		}
		if (inputTank.getFluidAmount() < recipe.getInputFluid().amount) {
			return false;
		}
		FluidStack outputFluid = recipe.getOutputFluid();
		return outputTank.fill(outputFluid, false) == outputFluid.amount;
	}

	@Override
	protected boolean hasValidInput() {

		BrewerRecipe recipe = BrewerManager.getRecipe(inventory[0], inputTank.getFluid());
		return recipe != null && recipe.getInput().getCount() <= inventory[0].getCount();
	}

	@Override
	protected void processStart() {

		processMax = BrewerManager.getRecipe(inventory[0], inputTank.getFluid()).getEnergy() * energyMod / ENERGY_BASE;
		processRem = processMax;
	}

	@Override
	protected void processFinish() {

		BrewerRecipe recipe = BrewerManager.getRecipe(inventory[0], inputTank.getFluid());

		if (recipe == null) {
			processOff();
			return;
		}
		outputTank.fill(recipe.getOutputFluid(), true);
		inputTank.drain(recipe.getInputFluid().amount, true);

		inventory[0].shrink(recipe.getInput().getCount());

		if (inventory[0].getCount() <= 0) {
			inventory[0] = ItemStack.EMPTY;
		}
	}

	@Override
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

	private void transferInputFluid() {

		if (!enableAutoInput) {
			return;
		}
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

		if (!enableAutoOutput) {
			return;
		}
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

		inputTracker = nbt.getInteger("TrackIn");
		inputTrackerFluid = nbt.getInteger("TrackInFluid");
		outputTrackerFluid = nbt.getInteger("TrackOutFluid");

		inputTank.readFromNBT(nbt.getCompoundTag("TankIn"));
		outputTank.readFromNBT(nbt.getCompoundTag("TankOut"));
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {

		super.writeToNBT(nbt);

		nbt.setInteger("TrackIn", inputTracker);
		nbt.setInteger("TrackInFluid", inputTrackerFluid);
		nbt.setInteger("TrackOutFluid", outputTrackerFluid);

		nbt.setTag("TankIn", inputTank.writeToNBT(new NBTTagCompound()));
		nbt.setTag("TankOut", outputTank.writeToNBT(new NBTTagCompound()));
		return nbt;
	}

	/* NETWORK METHODS */

	/* SERVER -> CLIENT */
	@Override
	public PacketCoFHBase getGuiPacket() {

		PacketCoFHBase payload = super.getGuiPacket();

		payload.addFluidStack(inputTank.getFluid());
		payload.addFluidStack(outputTank.getFluid());
		return payload;
	}

	@Override
	protected void handleGuiPacket(PacketCoFHBase payload) {

		super.handleGuiPacket(payload);
		inputTank.setFluid(payload.getFluidStack());
		outputTank.setFluid(payload.getFluidStack());
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

					if (from != null && !allowInsertion(sideConfig.sideTypes[sideCache[from.ordinal()]])) {
						return 0;
					}
					if (!BrewerManager.isFluidValid(resource)) {
						return 0;
					}
					return inputTank.fill(resource, doFill);
				}

				@Nullable
				@Override
				public FluidStack drain(FluidStack resource, boolean doDrain) {

					if (from != null && isPrimaryOutput(sideConfig.sideTypes[sideCache[from.ordinal()]])) {
						return null;
					}
					if (resource == null || !resource.isFluidEqual(outputTank.getFluid())) {
						return null;
					}
					return outputTank.drain(resource.amount, doDrain);
				}

				@Nullable
				@Override
				public FluidStack drain(int maxDrain, boolean doDrain) {

					if (from != null && isPrimaryOutput(sideConfig.sideTypes[sideCache[from.ordinal()]])) {
						return null;
					}
					return outputTank.drain(maxDrain, doDrain);
				}
			});
		}
		return super.getCapability(capability, from);
	}

}
