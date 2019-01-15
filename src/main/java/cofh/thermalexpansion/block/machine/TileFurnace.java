package cofh.thermalexpansion.block.machine;

import cofh.core.fluid.FluidTankCore;
import cofh.core.init.CoreProps;
import cofh.core.network.PacketBase;
import cofh.core.util.core.EnergyConfig;
import cofh.core.util.core.SideConfig;
import cofh.core.util.core.SlotConfig;
import cofh.core.util.helpers.AugmentHelper;
import cofh.core.util.helpers.FluidHelper;
import cofh.core.util.helpers.ItemHelper;
import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalexpansion.block.machine.BlockMachine.Type;
import cofh.thermalexpansion.gui.client.machine.GuiFurnace;
import cofh.thermalexpansion.gui.container.machine.ContainerFurnace;
import cofh.thermalexpansion.init.TEProps;
import cofh.thermalexpansion.init.TESounds;
import cofh.thermalexpansion.util.managers.machine.FurnaceManager;
import cofh.thermalexpansion.util.managers.machine.FurnaceManager.FurnaceRecipe;
import cofh.thermalfoundation.init.TFFluids;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundEvent;
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

import static cofh.core.util.core.SideConfig.*;

public class TileFurnace extends TileMachineBase {

	private static final int TYPE = Type.FURNACE.getMetadata();
	public static int basePower = 20;

	public static final int FOOD_ENERGY_MOD = 50;
	public static final int ORE_ENERGY_MOD = 50;
	public static final int PYRO_ENERGY_MOD = 50;

	public static void initialize() {

		SIDE_CONFIGS[TYPE] = new SideConfig();
		SIDE_CONFIGS[TYPE].numConfig = 5;
		SIDE_CONFIGS[TYPE].slotGroups = new int[][] { {}, { 0 }, { 1 }, { 0, 1 }, { 0, 1 } };
		SIDE_CONFIGS[TYPE].sideTypes = new int[] { NONE, INPUT_ALL, OUTPUT_ALL, OPEN, OMNI };
		SIDE_CONFIGS[TYPE].defaultSides = new byte[] { 1, 1, 2, 2, 2, 2 };

		ALT_SIDE_CONFIGS[TYPE] = new SideConfig();
		ALT_SIDE_CONFIGS[TYPE].numConfig = 2;
		ALT_SIDE_CONFIGS[TYPE].slotGroups = new int[][] { {}, { 0 }, { 1 }, { 0, 1 }, { 0, 1 } };
		ALT_SIDE_CONFIGS[TYPE].sideTypes = new int[] { NONE, OPEN };
		ALT_SIDE_CONFIGS[TYPE].defaultSides = new byte[] { 1, 1, 1, 1, 1, 1 };

		SLOT_CONFIGS[TYPE] = new SlotConfig();
		SLOT_CONFIGS[TYPE].allowInsertionSlot = new boolean[] { true, false, false };
		SLOT_CONFIGS[TYPE].allowExtractionSlot = new boolean[] { false, true, false };

		VALID_AUGMENTS[TYPE] = new HashSet<>();
		VALID_AUGMENTS[TYPE].add(TEProps.MACHINE_FURNACE_FOOD);
		VALID_AUGMENTS[TYPE].add(TEProps.MACHINE_FURNACE_ORE);
		VALID_AUGMENTS[TYPE].add(TEProps.MACHINE_FURNACE_PYROLYSIS);

		LIGHT_VALUES[TYPE] = 14;

		GameRegistry.registerTileEntity(TileFurnace.class, "thermalexpansion:machine_furnace");

		config();
	}

	public static void config() {

		String category = "Machine.Furnace";
		BlockMachine.enable[TYPE] = ThermalExpansion.CONFIG.get(category, "Enable", true);

		String comment = "Adjust this value to change the Energy consumption (in RF/t) for a Redstone Furnace. This base value will scale with block level and Augments.";
		basePower = ThermalExpansion.CONFIG.getConfiguration().getInt("BasePower", category, basePower, MIN_BASE_POWER, MAX_BASE_POWER, comment);

		ENERGY_CONFIGS[TYPE] = new EnergyConfig();
		ENERGY_CONFIGS[TYPE].setDefaultParams(basePower, smallStorage);
	}

	private FurnaceRecipe curRecipe;
	private int inputTracker;
	private int outputTracker;
	private int outputTrackerFluid;

	private FluidTankCore tank = new FluidTankCore(TEProps.MAX_FLUID_SMALL);

	/* AUGMENTS */
	protected boolean augmentFood;
	protected boolean augmentOre;
	protected boolean augmentPyrolysis;
	protected boolean flagPyrolysis;

	public TileFurnace() {

		super();
		inventory = new ItemStack[1 + 1 + 1];
		Arrays.fill(inventory, ItemStack.EMPTY);
		createAllSlots(inventory.length);
		tank.setLock(TFFluids.fluidCreosote);
	}

	@Override
	public int getType() {

		return TYPE;
	}

	@Override
	protected int calcEnergy() {

		if (augmentPyrolysis) {
			return Math.min(energyConfig.minPower, energyStorage.getEnergyStored());
		}
		return super.calcEnergy();
	}

	@Override
	protected boolean canStart() {

		if (inventory[0].isEmpty() || energyStorage.getEnergyStored() <= 0) {
			return false;
		}
		if (augmentFood && !FurnaceManager.isFood(inventory[0]) || augmentOre && !FurnaceManager.isOre(inventory[0])) {
			return false;
		}
		getRecipe();

		if (curRecipe == null) {
			return false;
		}
		if (inventory[0].getCount() < curRecipe.getInput().getCount()) {
			return false;
		}
		ItemStack output = curRecipe.getOutput();

		return inventory[1].isEmpty() || inventory[1].isItemEqual(output) && inventory[1].getCount() + output.getCount() <= output.getMaxStackSize();
	}

	@Override
	protected boolean hasValidInput() {

		if (augmentFood && !FurnaceManager.isFood(inventory[0]) || augmentOre && !FurnaceManager.isOre(inventory[0])) {
			return false;
		}
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

		curRecipe = FurnaceManager.getRecipe(inventory[0], augmentPyrolysis);
	}

	@Override
	protected void processStart() {

		processMax = curRecipe.getEnergy() * energyMod / ENERGY_BASE;
		processRem = processMax;
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
		ItemStack output = curRecipe.getOutput();
		if (inventory[1].isEmpty()) {
			inventory[1] = ItemHelper.cloneStack(output);
		} else {
			inventory[1].grow(output.getCount());
		}
		if (augmentPyrolysis) {
			tank.fill(new FluidStack(TFFluids.fluidCreosote, curRecipe.getCreosote()), true);
		} else {
			if ((augmentFood && FurnaceManager.isFood(inventory[0]) || augmentOre && FurnaceManager.isOre(inventory[0])) && inventory[1].getCount() < inventory[1].getMaxStackSize()) {
				inventory[1].grow(Math.max(1, output.getCount() / 2));
			}
		}
		inventory[0].shrink(curRecipe.getInput().getCount());

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
		if (augmentPyrolysis) {
			transferOutputFluid();
		}
		if (inventory[1].isEmpty()) {
			return;
		}
		int side;
		for (int i = outputTracker + 1; i <= outputTracker + 6; i++) {
			side = i % 6;
			if (isPrimaryOutput(sideConfig.sideTypes[sideCache[side]])) {
				if (transferItem(1, ITEM_TRANSFER[level], EnumFacing.VALUES[side])) {
					outputTracker = side;
					break;
				}
			}
		}
	}

	private void transferOutputFluid() {

		if (!getTransferOut()) {
			return;
		}
		if (tank.getFluidAmount() <= 0) {
			return;
		}
		int side;
		FluidStack output = new FluidStack(tank.getFluid(), Math.min(tank.getFluidAmount(), FLUID_TRANSFER[level]));
		for (int i = outputTrackerFluid + 1; i <= outputTrackerFluid + 6; i++) {
			side = i % 6;
			if (isPrimaryOutput(sideConfig.sideTypes[sideCache[side]])) {
				int toDrain = FluidHelper.insertFluidIntoAdjacentFluidHandler(this, EnumFacing.VALUES[side], output, true);
				if (toDrain > 0) {
					tank.drain(toDrain, true);
					outputTrackerFluid = side;
					break;
				}
			}
		}
	}

	@Override
	public void update() {

		if (augmentPyrolysis && timeCheckEighth()) {
			transferOutputFluid();
		}
		super.update();
	}

	/* GUI METHODS */
	@Override
	public Object getGuiClient(InventoryPlayer inventory) {

		return new GuiFurnace(inventory, this);
	}

	@Override
	public Object getGuiServer(InventoryPlayer inventory) {

		return new ContainerFurnace(inventory, this);
	}

	@Override
	public FluidTankCore getTank() {

		return tank;
	}

	@Override
	public FluidStack getTankFluid() {

		return tank.getFluid();
	}

	public boolean augmentFood() {

		return augmentFood;
	}

	public boolean augmentOre() {

		return augmentOre;
	}

	public boolean augmentPyrolysis() {

		return augmentPyrolysis;
	}

	public boolean augmentPyrolysisClient() {

		return augmentPyrolysis && flagPyrolysis;
	}

	/* NBT METHODS */
	@Override
	public void readFromNBT(NBTTagCompound nbt) {

		super.readFromNBT(nbt);

		inputTracker = nbt.getInteger(CoreProps.TRACK_IN);
		outputTracker = nbt.getInteger(CoreProps.TRACK_OUT);
		outputTrackerFluid = nbt.getInteger("TrackOutFluid");
		tank.readFromNBT(nbt);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {

		super.writeToNBT(nbt);

		nbt.setInteger(CoreProps.TRACK_IN, inputTracker);
		nbt.setInteger(CoreProps.TRACK_OUT, outputTracker);
		nbt.setInteger("TrackOutFluid", outputTrackerFluid);
		tank.writeToNBT(nbt);
		return nbt;
	}

	/* NETWORK METHODS */
	@Override
	public PacketBase getGuiPacket() {

		PacketBase payload = super.getGuiPacket();

		payload.addBool(augmentFood);
		payload.addBool(augmentOre);
		payload.addBool(augmentPyrolysis);
		payload.addFluidStack(tank.getFluid());
		return payload;
	}

	@Override
	protected void handleGuiPacket(PacketBase payload) {

		super.handleGuiPacket(payload);

		augmentFood = payload.getBool();
		augmentOre = payload.getBool();
		augmentPyrolysis = payload.getBool();
		flagPyrolysis = augmentPyrolysis;
		tank.setFluid(payload.getFluidStack());
	}

	/* HELPERS */
	@Override
	protected void preAugmentInstall() {

		super.preAugmentInstall();

		augmentFood = false;
		augmentOre = false;
		augmentPyrolysis = false;
	}

	@Override
	protected void postAugmentInstall() {

		super.postAugmentInstall();

		if (!augmentPyrolysis) {
			tank.clearLocked();
			tank.setFluid(null);
		}
	}

	@Override
	protected boolean installAugmentToSlot(int slot) {

		String id = AugmentHelper.getAugmentIdentifier(augments[slot]);

		if (!augmentFood && TEProps.MACHINE_FURNACE_FOOD.equals(id)) {
			augmentFood = true;
			hasModeAugment = true;
			energyMod += FOOD_ENERGY_MOD;
			return true;
		}
		if (!augmentOre && TEProps.MACHINE_FURNACE_ORE.equals(id)) {
			augmentOre = true;
			hasModeAugment = true;
			energyMod += ORE_ENERGY_MOD;
			return true;
		}
		if (!augmentPyrolysis && TEProps.MACHINE_FURNACE_PYROLYSIS.equals(id)) {
			augmentPyrolysis = true;
			hasModeAugment = true;
			energyMod += PYRO_ENERGY_MOD;
			tank.setLock(TFFluids.fluidCreosote);
			return true;
		}
		return super.installAugmentToSlot(slot);
	}

	/* IEnergyInfo */
	@Override
	public int getInfoMaxEnergyPerTick() {

		return augmentPyrolysis ? energyConfig.minPower : energyConfig.maxPower;
	}

	/* IInventory */
	@Override
	public boolean isItemValidForSlot(int slot, ItemStack stack) {

		return slot != 0 || (augmentFood ? FurnaceManager.isFood(stack) : augmentOre ? FurnaceManager.isOre(stack) && FurnaceManager.recipeExists(stack, augmentPyrolysis) : FurnaceManager.recipeExists(stack, augmentPyrolysis));
	}

	/* ISoundSource */
	@Override
	public SoundEvent getSoundEvent() {

		return TEProps.enableSounds ? TESounds.machineFurnace : null;
	}

	/* CAPABILITIES */
	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing from) {

		return super.hasCapability(capability, from) || augmentPyrolysis && capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY;
	}

	@Override
	public <T> T getCapability(Capability<T> capability, final EnumFacing from) {

		if (augmentPyrolysis && capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
			return CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.cast(new IFluidHandler() {

				@Override
				public IFluidTankProperties[] getTankProperties() {

					FluidTankInfo info = tank.getInfo();
					return new IFluidTankProperties[] { new FluidTankProperties(info.fluid, info.capacity, true, false) };
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
