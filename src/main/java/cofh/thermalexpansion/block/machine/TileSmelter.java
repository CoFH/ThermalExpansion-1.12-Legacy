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
import cofh.thermalexpansion.gui.client.machine.GuiSmelter;
import cofh.thermalexpansion.gui.container.machine.ContainerSmelter;
import cofh.thermalexpansion.init.TEProps;
import cofh.thermalexpansion.init.TESounds;
import cofh.thermalexpansion.util.managers.machine.SmelterManager;
import cofh.thermalexpansion.util.managers.machine.SmelterManager.SmelterRecipe;
import cofh.thermalfoundation.init.TFFluids;
import net.minecraft.entity.player.EntityPlayer;
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

public class TileSmelter extends TileMachineBase {

	private static final int TYPE = Type.SMELTER.getMetadata();
	public static int basePower = 20;

	public static final int FLUID_AMOUNT = 100;
	public static final int PYROTHEUM_ENERGY_MOD = 50;      // This is a penalty.
	public static final int PYROTHEUM_SECONDARY_MOD = 30;   // This is a bonus, it's subtracted.

	public static void initialize() {

		SIDE_CONFIGS[TYPE] = new SideConfig();
		SIDE_CONFIGS[TYPE].numConfig = 9;
		SIDE_CONFIGS[TYPE].slotGroups = new int[][] { {}, { 0, 1 }, { 2 }, { 3 }, { 2, 3 }, { 0 }, { 1 }, { 0, 1, 2, 3 }, { 0, 1, 2, 3 } };
		SIDE_CONFIGS[TYPE].sideTypes = new int[] { NONE, INPUT_ALL, OUTPUT_PRIMARY, OUTPUT_SECONDARY, OUTPUT_ALL, INPUT_PRIMARY, INPUT_SECONDARY, OPEN, OMNI };
		SIDE_CONFIGS[TYPE].defaultSides = new byte[] { 3, 1, 2, 2, 2, 2 };

		ALT_SIDE_CONFIGS[TYPE] = new SideConfig();
		ALT_SIDE_CONFIGS[TYPE].numConfig = 2;
		ALT_SIDE_CONFIGS[TYPE].slotGroups = new int[][] { {}, { 0, 1 }, { 2 }, { 3 }, { 2, 3 }, { 0 }, { 1 }, { 0, 1, 2, 3 }, { 0, 1, 2, 3 } };
		ALT_SIDE_CONFIGS[TYPE].sideTypes = new int[] { NONE, OPEN };
		ALT_SIDE_CONFIGS[TYPE].defaultSides = new byte[] { 1, 1, 1, 1, 1, 1 };

		SLOT_CONFIGS[TYPE] = new SlotConfig();
		SLOT_CONFIGS[TYPE].allowInsertionSlot = new boolean[] { true, true, false, false, false };
		SLOT_CONFIGS[TYPE].allowExtractionSlot = new boolean[] { false, false, true, true, false };

		VALID_AUGMENTS[TYPE] = new HashSet<>();
		VALID_AUGMENTS[TYPE].add(TEProps.MACHINE_SMELTER_FLUX);
		VALID_AUGMENTS[TYPE].add(TEProps.MACHINE_SMELTER_PYROTHEUM);

		VALID_AUGMENTS[TYPE].add(TEProps.MACHINE_SECONDARY);
		VALID_AUGMENTS[TYPE].add(TEProps.MACHINE_SECONDARY_NULL);

		LIGHT_VALUES[TYPE] = 14;

		GameRegistry.registerTileEntity(TileSmelter.class, "thermalexpansion:machine_smelter");

		config();
	}

	public static void config() {

		String category = "Machine.Smelter";
		BlockMachine.enable[TYPE] = ThermalExpansion.CONFIG.get(category, "Enable", true);

		String comment = "Adjust this value to change the Energy consumption (in RF/t) for an Induction Smelter. This base value will scale with block level and Augments.";
		basePower = ThermalExpansion.CONFIG.getConfiguration().getInt("BasePower", category, basePower, MIN_BASE_POWER, MAX_BASE_POWER, comment);

		ENERGY_CONFIGS[TYPE] = new EnergyConfig();
		ENERGY_CONFIGS[TYPE].setDefaultParams(basePower, smallStorage);
	}

	public static int getPyrotheumOutputAmount(ItemStack stack) {

		int amount = stack.getCount();
		return amount + Math.max(1, amount * 50 / 100);
	}

	private SmelterRecipe curRecipe;
	private int inputTrackerPrimary;
	private int inputTrackerSecondary;
	private int outputTrackerPrimary;
	private int outputTrackerSecondary;

	public boolean lockPrimary = true;

	private FluidTankCore tank = new FluidTankCore(TEProps.MAX_FLUID_SMALL);

	/* AUGMENTS */
	protected boolean augmentPyrotheum;
	protected boolean flagPyrotheum;

	public TileSmelter() {

		super();
		inventory = new ItemStack[2 + 1 + 1 + 1];
		Arrays.fill(inventory, ItemStack.EMPTY);
		createAllSlots(inventory.length);
		tank.setLock(TFFluids.fluidPyrotheum);
	}

	@Override
	public int getType() {

		return TYPE;
	}

	@Override
	public int getMaxInputSlot() {

		return 1;
	}

	@Override
	protected boolean canStart() {

		if (inventory[0].isEmpty() || inventory[1].isEmpty() || energyStorage.getEnergyStored() <= 0) {
			return false;
		}
		getRecipe();

		if (curRecipe == null) {
			return false;
		}
		if (SmelterManager.isRecipeReversed(inventory[0], inventory[1])) {
			if (curRecipe.getPrimaryInput().getCount() > inventory[1].getCount() || curRecipe.getSecondaryInput().getCount() > inventory[0].getCount()) {
				return false;
			}
		} else {
			if (curRecipe.getPrimaryInput().getCount() > inventory[0].getCount() || curRecipe.getSecondaryInput().getCount() > inventory[1].getCount()) {
				return false;
			}
		}
		boolean augmentPyrotheumCheck = augmentPyrotheum && (SmelterManager.isOre(inventory[0]) || SmelterManager.isOre(inventory[1]));

		if (augmentPyrotheumCheck && tank.getFluidAmount() < FLUID_AMOUNT) {
			return false;
		}
		ItemStack primaryItem = curRecipe.getPrimaryOutput();
		ItemStack secondaryItem = curRecipe.getSecondaryOutput();

		if (!secondaryItem.isEmpty() && !inventory[3].isEmpty()) {
			if (!augmentSecondaryNull) {
				if (!inventory[3].isItemEqual(secondaryItem)) {
					return false;
				}
				if (inventory[3].getCount() + secondaryItem.getCount() > secondaryItem.getMaxStackSize()) {
					return false;
				}
			}
		}
		return inventory[2].isEmpty() || inventory[2].isItemEqual(primaryItem) && inventory[2].getCount() + (augmentPyrotheumCheck ? getPyrotheumOutputAmount(primaryItem) : primaryItem.getCount()) <= primaryItem.getMaxStackSize();
	}

	@Override
	protected boolean hasValidInput() {

		if (curRecipe == null) {
			getRecipe();
		}
		if (curRecipe == null) {
			return false;
		}
		if (augmentPyrotheum && (SmelterManager.isOre(inventory[0]) || SmelterManager.isOre(inventory[1])) && tank.getFluidAmount() < FLUID_AMOUNT) {
			return false;
		}
		if (SmelterManager.isRecipeReversed(inventory[0], inventory[1])) {
			return curRecipe.getPrimaryInput().getCount() <= inventory[1].getCount() && curRecipe.getSecondaryInput().getCount() <= inventory[0].getCount();
		} else {
			return curRecipe.getPrimaryInput().getCount() <= inventory[0].getCount() && curRecipe.getSecondaryInput().getCount() <= inventory[1].getCount();
		}
	}

	@Override
	protected void clearRecipe() {

		curRecipe = null;
	}

	@Override
	protected void getRecipe() {

		curRecipe = SmelterManager.getRecipe(inventory[1], inventory[0]);
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

		boolean augmentPyrotheumCheck = augmentPyrotheum && (SmelterManager.isOre(inventory[0]) || SmelterManager.isOre(inventory[1])) && tank.getFluidAmount() >= FLUID_AMOUNT;

		if (augmentPyrotheumCheck) {
			if (inventory[2].isEmpty()) {
				inventory[2] = ItemHelper.cloneStack(primaryItem, getPyrotheumOutputAmount(primaryItem));
			} else {
				inventory[2].grow(getPyrotheumOutputAmount(primaryItem));
			}
			tank.modifyFluidStored(-FLUID_AMOUNT);
		} else {
			if (inventory[2].isEmpty()) {
				inventory[2] = ItemHelper.cloneStack(primaryItem);
			} else {
				inventory[2].grow(primaryItem.getCount());
			}
		}
		if (!secondaryItem.isEmpty()) {
			int modifiedChance = augmentPyrotheumCheck ? secondaryChance - PYROTHEUM_SECONDARY_MOD : secondaryChance;
			modifiedChance = MathHelper.clamp(modifiedChance, SECONDARY_MIN, SECONDARY_BASE);

			int recipeChance = curRecipe.getSecondaryOutputChance();
			if (recipeChance >= 100 || world.rand.nextInt(modifiedChance) < recipeChance) {
				if (inventory[3].isEmpty()) {
					inventory[3] = ItemHelper.cloneStack(secondaryItem);
				} else if (inventory[3].isItemEqual(secondaryItem)) {
					inventory[3].grow(secondaryItem.getCount());
				}
				if (recipeChance > modifiedChance && world.rand.nextInt(SECONDARY_BASE) < recipeChance - modifiedChance) {
					inventory[3].grow(secondaryItem.getCount());
				}
				if (inventory[3].getCount() > inventory[3].getMaxStackSize()) {
					inventory[3].setCount(inventory[3].getMaxStackSize());
				}
			}
		}
		if (curRecipe.hasFlux()) { // Flux is *always* secondary input, if present.
			int countInput = curRecipe.getPrimaryInput().getCount();
			int countFlux = curRecipe.getSecondaryInput().getCount();

			if (reuseChance > 0) {
				if (SmelterManager.isItemFlux(inventory[0])) {
					if (world.rand.nextInt(SECONDARY_BASE) >= reuseChance) {
						inventory[0].shrink(countFlux);
					}
					inventory[1].shrink(countInput);
				} else {
					if (world.rand.nextInt(SECONDARY_BASE) >= reuseChance) {
						inventory[1].shrink(countFlux);
					}
					inventory[0].shrink(countInput);
				}
			} else {
				if (SmelterManager.isItemFlux(inventory[0])) {
					inventory[0].shrink(countFlux);
					inventory[1].shrink(countInput);
				} else {
					inventory[1].shrink(countFlux);
					inventory[0].shrink(countInput);
				}
			}
		} else {
			int count1 = curRecipe.getPrimaryInput().getCount();
			int count2 = curRecipe.getSecondaryInput().getCount();

			if (SmelterManager.isRecipeReversed(inventory[0], inventory[1])) {
				inventory[1].shrink(count1);
				inventory[0].shrink(count2);
			} else {
				inventory[0].shrink(count1);
				inventory[1].shrink(count2);
			}
		}
		if (inventory[0].getCount() <= 0) {
			inventory[0] = ItemStack.EMPTY;
		}
		if (inventory[1].getCount() <= 0) {
			inventory[1] = ItemStack.EMPTY;
		}
	}

	@Override
	protected void transferInput() {

		if (!getTransferIn()) {
			return;
		}
		int side;
		for (int i = inputTrackerPrimary + 1; i <= inputTrackerPrimary + 6; i++) {
			side = i % 6;
			if (isPrimaryInput(sideConfig.sideTypes[sideCache[side]])) {
				if (extractItem(0, ITEM_TRANSFER[level], EnumFacing.VALUES[side])) {
					inputTrackerPrimary = side;
					break;
				}
			}
		}
		for (int i = inputTrackerSecondary + 1; i <= inputTrackerSecondary + 6; i++) {
			side = i % 6;
			if (isSecondaryInput(sideConfig.sideTypes[sideCache[side]])) {
				if (extractItem(1, ITEM_TRANSFER[level], EnumFacing.VALUES[side])) {
					inputTrackerSecondary = side;
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
		if (!inventory[2].isEmpty()) {
			for (int i = outputTrackerPrimary + 1; i <= outputTrackerPrimary + 6; i++) {
				side = i % 6;
				if (isPrimaryOutput(sideConfig.sideTypes[sideCache[side]])) {
					if (transferItem(2, ITEM_TRANSFER[level], EnumFacing.VALUES[side])) {
						outputTrackerPrimary = side;
						break;
					}
				}
			}
		}
		if (inventory[3].isEmpty()) {
			return;
		}
		for (int i = outputTrackerSecondary + 1; i <= outputTrackerSecondary + 6; i++) {
			side = i % 6;
			if (isSecondaryOutput(sideConfig.sideTypes[sideCache[side]])) {
				if (transferItem(3, ITEM_TRANSFER[level], EnumFacing.VALUES[side])) {
					outputTrackerSecondary = side;
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
		lockPrimary = tag.getBoolean("SlotLock");
		return true;
	}

	@Override
	protected boolean writePortableTagInternal(EntityPlayer player, NBTTagCompound tag) {

		if (!super.writePortableTagInternal(player, tag)) {
			return false;
		}
		tag.setBoolean("SlotLock", lockPrimary);
		return true;
	}

	/* GUI METHODS */
	@Override
	public Object getGuiClient(InventoryPlayer inventory) {

		return new GuiSmelter(inventory, this);
	}

	@Override
	public Object getGuiServer(InventoryPlayer inventory) {

		return new ContainerSmelter(inventory, this);
	}

	@Override
	public FluidTankCore getTank() {

		return tank;
	}

	@Override
	public FluidStack getTankFluid() {

		return tank.getFluid();
	}

	public boolean augmentPyrotheum() {

		return augmentPyrotheum && flagPyrotheum;
	}

	public boolean fluidArrow() {

		return augmentPyrotheum && tank.getFluidAmount() >= FLUID_AMOUNT && (SmelterManager.isOre(inventory[0]) || SmelterManager.isOre(inventory[1]));
	}

	public void setMode(boolean mode) {

		boolean lastMode = lockPrimary;
		lockPrimary = mode;
		sendModePacket();
		lockPrimary = lastMode;
	}

	/* NBT METHODS */
	@Override
	public void readFromNBT(NBTTagCompound nbt) {

		super.readFromNBT(nbt);

		inputTrackerPrimary = nbt.getInteger(CoreProps.TRACK_IN);
		inputTrackerSecondary = nbt.getInteger(CoreProps.TRACK_IN_2);
		outputTrackerPrimary = nbt.getInteger(CoreProps.TRACK_OUT);
		outputTrackerSecondary = nbt.getInteger(CoreProps.TRACK_OUT_2);
		lockPrimary = nbt.getBoolean("SlotLock");
		tank.readFromNBT(nbt);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {

		super.writeToNBT(nbt);

		nbt.setInteger(CoreProps.TRACK_IN, inputTrackerPrimary);
		nbt.setInteger(CoreProps.TRACK_IN_2, inputTrackerSecondary);
		nbt.setInteger(CoreProps.TRACK_OUT, outputTrackerPrimary);
		nbt.setInteger(CoreProps.TRACK_OUT_2, outputTrackerSecondary);
		nbt.setBoolean("SlotLock", lockPrimary);
		tank.writeToNBT(nbt);
		return nbt;
	}

	/* NETWORK METHODS */

	/* CLIENT -> SERVER */
	@Override
	public PacketBase getModePacket() {

		PacketBase payload = super.getModePacket();

		payload.addBool(lockPrimary);

		return payload;
	}

	@Override
	protected void handleModePacket(PacketBase payload) {

		super.handleModePacket(payload);

		lockPrimary = payload.getBool();

		callNeighborTileChange();
	}

	/* SERVER -> CLIENT */
	@Override
	public PacketBase getGuiPacket() {

		PacketBase payload = super.getGuiPacket();

		payload.addBool(lockPrimary);
		payload.addBool(augmentPyrotheum);
		payload.addFluidStack(tank.getFluid());

		return payload;
	}

	@Override
	protected void handleGuiPacket(PacketBase payload) {

		super.handleGuiPacket(payload);

		lockPrimary = payload.getBool();
		augmentPyrotheum = payload.getBool();
		flagPyrotheum = augmentPyrotheum;
		tank.setFluid(payload.getFluidStack());
	}

	/* HELPERS */
	@Override
	protected void preAugmentInstall() {

		super.preAugmentInstall();

		augmentPyrotheum = false;
	}

	@Override
	protected void postAugmentInstall() {

		super.postAugmentInstall();

		if (!augmentPyrotheum) {
			tank.drain(tank.getCapacity(), true);
		}
	}

	@Override
	protected boolean installAugmentToSlot(int slot) {

		String id = AugmentHelper.getAugmentIdentifier(augments[slot]);

		if (TEProps.MACHINE_SMELTER_FLUX.equals(id)) {
			reuseChance += 15;
			energyMod += 15;
		}
		if (!augmentPyrotheum && TEProps.MACHINE_SMELTER_PYROTHEUM.equals(id)) {
			augmentPyrotheum = true;
			hasModeAugment = true;
			energyMod += PYROTHEUM_ENERGY_MOD;
			tank.setLock(TFFluids.fluidPyrotheum);
			return true;
		}
		return super.installAugmentToSlot(slot);
	}

	/* IInventory */
	@Override
	public boolean isItemValidForSlot(int slot, ItemStack stack) {

		if (lockPrimary) {
			if (slot == 0) {
				return SmelterManager.isItemFlux(stack);
			}
			if (slot == 1) {
				return !SmelterManager.isItemFlux(stack) && SmelterManager.isItemValid(stack);
			}
		}
		return slot > 1 || SmelterManager.isItemValid(stack);
	}

	/* ISoundSource */
	@Override
	public SoundEvent getSoundEvent() {

		return TEProps.enableSounds ? TESounds.machineSmelter : null;
	}

	/* CAPABILITIES */
	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing from) {

		return super.hasCapability(capability, from) || augmentPyrotheum && capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY;
	}

	@Override
	public <T> T getCapability(Capability<T> capability, final EnumFacing from) {

		if (augmentPyrotheum && capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
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
