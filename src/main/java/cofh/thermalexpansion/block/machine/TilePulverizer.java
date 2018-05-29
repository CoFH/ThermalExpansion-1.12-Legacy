package cofh.thermalexpansion.block.machine;

import cofh.core.fluid.FluidTankCore;
import cofh.core.init.CoreProps;
import cofh.core.network.PacketBase;
import cofh.core.util.core.EnergyConfig;
import cofh.core.util.core.SideConfig;
import cofh.core.util.core.SlotConfig;
import cofh.core.util.helpers.AugmentHelper;
import cofh.core.util.helpers.ItemHelper;
import cofh.core.util.helpers.MathHelper;
import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalexpansion.block.machine.BlockMachine.Type;
import cofh.thermalexpansion.gui.client.machine.GuiPulverizer;
import cofh.thermalexpansion.gui.container.machine.ContainerPulverizer;
import cofh.thermalexpansion.init.TEProps;
import cofh.thermalexpansion.init.TESounds;
import cofh.thermalexpansion.util.managers.machine.PulverizerManager;
import cofh.thermalexpansion.util.managers.machine.PulverizerManager.PulverizerRecipe;
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

public class TilePulverizer extends TileMachineBase {

	private static final int TYPE = Type.PULVERIZER.getMetadata();
	public static int basePower = 20;

	public static final int FLUID_AMOUNT = 100;
	public static final int GEODE_ENERGY_MOD = 25;
	public static final int PETROTHEUM_ENERGY_MOD = 50;     // This is a penalty.
	public static final int PETROTHEUM_SECONDARY_MOD = 25;  // This is a bonus, it's subtracted.

	public static void initialize() {

		SIDE_CONFIGS[TYPE] = new SideConfig();
		SIDE_CONFIGS[TYPE].numConfig = 7;
		SIDE_CONFIGS[TYPE].slotGroups = new int[][] { {}, { 0 }, { 1 }, { 2 }, { 1, 2 }, { 0, 1, 2 }, { 0, 1, 2 } };
		SIDE_CONFIGS[TYPE].sideTypes = new int[] { NONE, INPUT_ALL, OUTPUT_PRIMARY, OUTPUT_SECONDARY, OUTPUT_ALL, OPEN, OMNI };
		SIDE_CONFIGS[TYPE].defaultSides = new byte[] { 3, 1, 2, 2, 2, 2 };

		ALT_SIDE_CONFIGS[TYPE] = new SideConfig();
		ALT_SIDE_CONFIGS[TYPE].numConfig = 2;
		ALT_SIDE_CONFIGS[TYPE].slotGroups = new int[][] { {}, { 0 }, { 1 }, { 2 }, { 1, 2 }, { 0, 1, 2 }, { 0, 1, 2 } };
		ALT_SIDE_CONFIGS[TYPE].sideTypes = new int[] { NONE, OPEN };
		ALT_SIDE_CONFIGS[TYPE].defaultSides = new byte[] { 1, 1, 1, 1, 1, 1 };

		SLOT_CONFIGS[TYPE] = new SlotConfig();
		SLOT_CONFIGS[TYPE].allowInsertionSlot = new boolean[] { true, false, false, false };
		SLOT_CONFIGS[TYPE].allowExtractionSlot = new boolean[] { false, true, true, false };

		VALID_AUGMENTS[TYPE] = new HashSet<>();
		VALID_AUGMENTS[TYPE].add(TEProps.MACHINE_PULVERIZER_GEODE);
		VALID_AUGMENTS[TYPE].add(TEProps.MACHINE_PULVERIZER_PETROTHEUM);

		VALID_AUGMENTS[TYPE].add(TEProps.MACHINE_SECONDARY);
		VALID_AUGMENTS[TYPE].add(TEProps.MACHINE_SECONDARY_NULL);

		LIGHT_VALUES[TYPE] = 4;

		GameRegistry.registerTileEntity(TilePulverizer.class, "thermalexpansion:machine_pulverizer");

		config();
	}

	public static void config() {

		String category = "Machine.Pulverizer";
		BlockMachine.enable[TYPE] = ThermalExpansion.CONFIG.get(category, "Enable", true);

		String comment = "Adjust this value to change the Energy consumption (in RF/t) for a Pulverizer. This base value will scale with block level and Augments.";
		basePower = ThermalExpansion.CONFIG.getConfiguration().getInt("BasePower", category, basePower, MIN_BASE_POWER, MAX_BASE_POWER, comment);

		ENERGY_CONFIGS[TYPE] = new EnergyConfig();
		ENERGY_CONFIGS[TYPE].setDefaultParams(basePower, smallStorage);
	}

	public static int getPetrotheumOutputAmount(ItemStack stack) {

		int amount = stack.getCount();
		return amount + Math.max(1, amount * 50 / 100);
	}

	private PulverizerRecipe curRecipe;
	private int inputTracker;
	private int outputTrackerPrimary;
	private int outputTrackerSecondary;

	private FluidTankCore tank = new FluidTankCore(TEProps.MAX_FLUID_SMALL);

	/* AUGMENTS */
	protected boolean augmentGeode;
	protected boolean augmentPetrotheum;
	protected boolean flagPetrotheum;

	public TilePulverizer() {

		super();
		inventory = new ItemStack[1 + 1 + 1 + 1];
		Arrays.fill(inventory, ItemStack.EMPTY);
		createAllSlots(inventory.length);
		tank.setLock(TFFluids.fluidPetrotheum);
	}

	@Override
	public int getType() {

		return TYPE;
	}

	@Override
	protected boolean canStart() {

		if (inventory[0].isEmpty() || energyStorage.getEnergyStored() <= 0) {
			return false;
		}
		getRecipe();

		if (curRecipe == null) {
			return false;
		}
		if (inventory[0].getCount() < curRecipe.getInput().getCount()) {
			return false;
		}
		boolean augmentPetrotheumCheck = augmentPetrotheum && PulverizerManager.isOre(inventory[0]);

		if (augmentPetrotheumCheck && tank.getFluidAmount() < FLUID_AMOUNT) {
			return false;
		}
		ItemStack primaryItem = curRecipe.getPrimaryOutput();
		ItemStack secondaryItem = curRecipe.getSecondaryOutput();

		if (!secondaryItem.isEmpty() && !inventory[2].isEmpty()) {
			if (!augmentSecondaryNull) {
				if (!inventory[2].isItemEqual(secondaryItem)) {
					return false;
				}
				if (inventory[2].getCount() + secondaryItem.getCount() > secondaryItem.getMaxStackSize()) {
					return false;
				}
			}
		}
		return inventory[1].isEmpty() || inventory[1].isItemEqual(primaryItem) && inventory[1].getCount() + (augmentPetrotheumCheck ? getPetrotheumOutputAmount(primaryItem) : primaryItem.getCount()) <= primaryItem.getMaxStackSize();
	}

	@Override
	protected boolean hasValidInput() {

		if (curRecipe == null) {
			getRecipe();
		}
		if (curRecipe == null) {
			return false;
		}
		if (augmentPetrotheum && PulverizerManager.isOre(inventory[0]) && tank.getFluidAmount() < FLUID_AMOUNT) {
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

		curRecipe = PulverizerManager.getRecipe(inventory[0]);
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
		ItemStack primaryItem = curRecipe.getPrimaryOutput();
		ItemStack secondaryItem = curRecipe.getSecondaryOutput();

		boolean augmentPetrotheumCheck = augmentPetrotheum && PulverizerManager.isOre(inventory[0]) && tank.getFluidAmount() >= FLUID_AMOUNT;

		if (augmentPetrotheumCheck) {
			if (inventory[1].isEmpty()) {
				inventory[1] = ItemHelper.cloneStack(primaryItem, getPetrotheumOutputAmount(primaryItem));
			} else {
				inventory[1].grow(getPetrotheumOutputAmount(primaryItem));
			}
			tank.modifyFluidStored(-FLUID_AMOUNT);
		} else {
			if (inventory[1].isEmpty()) {
				inventory[1] = ItemHelper.cloneStack(primaryItem);
			} else {
				inventory[1].grow(primaryItem.getCount());
			}
		}
		if (!secondaryItem.isEmpty()) {
			int modifiedChance = augmentPetrotheumCheck ? secondaryChance - PETROTHEUM_SECONDARY_MOD : secondaryChance;
			modifiedChance = MathHelper.clamp(modifiedChance, SECONDARY_MIN, SECONDARY_BASE);

			int recipeChance = curRecipe.getSecondaryOutputChance();
			if (recipeChance >= 100 || world.rand.nextInt(modifiedChance) < recipeChance) {
				if (inventory[2].isEmpty()) {
					inventory[2] = ItemHelper.cloneStack(secondaryItem);
				} else if (inventory[2].isItemEqual(secondaryItem)) {
					inventory[2].grow(secondaryItem.getCount());
				}
				if (recipeChance > modifiedChance && world.rand.nextInt(SECONDARY_BASE) < recipeChance - modifiedChance) {
					inventory[2].grow(secondaryItem.getCount());
				}
				if (inventory[2].getCount() > inventory[2].getMaxStackSize()) {
					inventory[2].setCount(inventory[2].getMaxStackSize());
				}
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
		int side;
		if (!inventory[1].isEmpty()) {
			for (int i = outputTrackerPrimary + 1; i <= outputTrackerPrimary + 6; i++) {
				side = i % 6;
				if (isPrimaryOutput(sideConfig.sideTypes[sideCache[side]])) {
					if (transferItem(1, ITEM_TRANSFER[level], EnumFacing.VALUES[side])) {
						outputTrackerPrimary = side;
						break;
					}
				}
			}
		}
		if (inventory[2].isEmpty()) {
			return;
		}
		for (int i = outputTrackerSecondary + 1; i <= outputTrackerSecondary + 6; i++) {
			side = i % 6;
			if (isSecondaryOutput(sideConfig.sideTypes[sideCache[side]])) {
				if (transferItem(2, ITEM_TRANSFER[level], EnumFacing.VALUES[side])) {
					outputTrackerSecondary = side;
					break;
				}
			}
		}
	}

	/* GUI METHODS */
	@Override
	public Object getGuiClient(InventoryPlayer inventory) {

		return new GuiPulverizer(inventory, this);
	}

	@Override
	public Object getGuiServer(InventoryPlayer inventory) {

		return new ContainerPulverizer(inventory, this);
	}

	@Override
	public FluidTankCore getTank() {

		return tank;
	}

	@Override
	public FluidStack getTankFluid() {

		return tank.getFluid();
	}

	public boolean augmentPetrotheum() {

		return augmentPetrotheum && flagPetrotheum;
	}

	public boolean fluidArrow() {

		return augmentPetrotheum && tank.getFluidAmount() >= FLUID_AMOUNT && PulverizerManager.isOre(inventory[0]);
	}

	/* NBT METHODS */
	@Override
	public void readFromNBT(NBTTagCompound nbt) {

		super.readFromNBT(nbt);

		inputTracker = nbt.getInteger(CoreProps.TRACK_IN);
		outputTrackerPrimary = nbt.getInteger(CoreProps.TRACK_OUT);
		outputTrackerSecondary = nbt.getInteger(CoreProps.TRACK_OUT_2);
		tank.readFromNBT(nbt);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {

		super.writeToNBT(nbt);

		nbt.setInteger(CoreProps.TRACK_IN, inputTracker);
		nbt.setInteger(CoreProps.TRACK_OUT, outputTrackerPrimary);
		nbt.setInteger(CoreProps.TRACK_OUT_2, outputTrackerSecondary);
		tank.writeToNBT(nbt);
		return nbt;
	}

	/* NETWORK METHODS */
	@Override
	public PacketBase getGuiPacket() {

		PacketBase payload = super.getGuiPacket();

		payload.addBool(augmentPetrotheum);
		payload.addFluidStack(tank.getFluid());
		return payload;
	}

	@Override
	protected void handleGuiPacket(PacketBase payload) {

		super.handleGuiPacket(payload);

		augmentPetrotheum = payload.getBool();
		flagPetrotheum = augmentPetrotheum;
		tank.setFluid(payload.getFluidStack());
	}

	/* HELPERS */
	@Override
	protected void preAugmentInstall() {

		super.preAugmentInstall();

		augmentGeode = false;
		augmentPetrotheum = false;
	}

	@Override
	protected void postAugmentInstall() {

		super.postAugmentInstall();

		if (!augmentPetrotheum) {
			tank.drain(tank.getCapacity(), true);
		}
	}

	@Override
	protected boolean installAugmentToSlot(int slot) {

		String id = AugmentHelper.getAugmentIdentifier(augments[slot]);

		if (!augmentGeode && TEProps.MACHINE_PULVERIZER_GEODE.equals(id)) {
			augmentGeode = true;
			hasModeAugment = true;
			energyMod += GEODE_ENERGY_MOD;
			return true;
		}
		if (!augmentPetrotheum && TEProps.MACHINE_PULVERIZER_PETROTHEUM.equals(id)) {
			augmentPetrotheum = true;
			hasModeAugment = true;
			energyMod += PETROTHEUM_ENERGY_MOD;
			tank.setLock(TFFluids.fluidPetrotheum);
			return true;
		}
		return super.installAugmentToSlot(slot);
	}

	/* IInventory */
	@Override
	public boolean isItemValidForSlot(int slot, ItemStack stack) {

		return slot != 0 || PulverizerManager.recipeExists(stack);
	}

	/* ISoundSource */
	@Override
	public SoundEvent getSoundEvent() {

		return TEProps.enableSounds ? TESounds.machinePulverizer : null;
	}

	/* CAPABILITIES */
	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing from) {

		return super.hasCapability(capability, from) || augmentPetrotheum && capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY;
	}

	@Override
	public <T> T getCapability(Capability<T> capability, final EnumFacing from) {

		if (augmentPetrotheum && capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
			return CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.cast(new IFluidHandler() {

				@Override
				public IFluidTankProperties[] getTankProperties() {

					FluidTankInfo info = tank.getInfo();
					return new IFluidTankProperties[] { new FluidTankProperties(info.fluid, info.capacity, true, false) };
				}

				@Override
				public int fill(FluidStack resource, boolean doFill) {

					return tank.fill(resource, doFill);
					//					if (from == null || allowInsertion(sideConfig.sideTypes[sideCache[from.ordinal()]])) {
					//						return tank.fill(resource, doFill);
					//					}
					//					return 0;
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
