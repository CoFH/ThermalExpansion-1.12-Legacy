package cofh.thermalexpansion.block.machine;

import cofh.core.fluid.FluidTankCore;
import cofh.core.network.PacketCoFHBase;
import cofh.core.util.helpers.AugmentHelper;
import cofh.core.util.helpers.ItemHelper;
import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalexpansion.block.machine.BlockMachine.Type;
import cofh.thermalexpansion.gui.client.machine.GuiSmelter;
import cofh.thermalexpansion.gui.container.machine.ContainerSmelter;
import cofh.thermalexpansion.init.TEProps;
import cofh.thermalexpansion.init.TESounds;
import cofh.thermalexpansion.util.managers.machine.InsolatorManager;
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
		SIDE_CONFIGS[TYPE].sideTypes = new int[] { 0, 1, 2, 3, 4, 5, 6, 7, 8 };
		SIDE_CONFIGS[TYPE].defaultSides = new byte[] { 3, 1, 2, 2, 2, 2 };

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

	private int inputTrackerPrimary;
	private int inputTrackerSecondary;
	private int outputTrackerPrimary;
	private int outputTrackerSecondary;

	public boolean lockPrimary = false;

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
		SmelterRecipe recipe = SmelterManager.getRecipe(inventory[1], inventory[0]);

		if (recipe == null) {
			return false;
		}
		if (SmelterManager.isRecipeReversed(inventory[0], inventory[1])) {
			if (recipe.getPrimaryInput().getCount() > inventory[1].getCount() || recipe.getSecondaryInput().getCount() > inventory[0].getCount()) {
				return false;
			}
		} else {
			if (recipe.getPrimaryInput().getCount() > inventory[0].getCount() || recipe.getSecondaryInput().getCount() > inventory[1].getCount()) {
				return false;
			}
		}
		boolean augmentPyrotheumCheck = augmentPyrotheum && (ItemHelper.isOre(inventory[0]) || ItemHelper.isOre(inventory[1]));

		if (augmentPyrotheumCheck && tank.getFluidAmount() < FLUID_AMOUNT) {
			return false;
		}
		ItemStack primaryItem = recipe.getPrimaryOutput();
		ItemStack secondaryItem = recipe.getSecondaryOutput();

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

		SmelterRecipe recipe = SmelterManager.getRecipe(inventory[1], inventory[0]);

		if (recipe == null) {
			return false;
		}
		if (augmentPyrotheum && (ItemHelper.isOre(inventory[0]) || ItemHelper.isOre(inventory[1])) && tank.getFluidAmount() < FLUID_AMOUNT) {
			return false;
		}
		if (SmelterManager.isRecipeReversed(inventory[0], inventory[1])) {
			if (recipe.getPrimaryInput().getCount() > inventory[1].getCount() || recipe.getSecondaryInput().getCount() > inventory[0].getCount()) {
				return false;
			}
		} else {
			if (recipe.getPrimaryInput().getCount() > inventory[0].getCount() || recipe.getSecondaryInput().getCount() > inventory[1].getCount()) {
				return false;
			}
		}
		return true;
	}

	@Override
	protected void processStart() {

		processMax = SmelterManager.getRecipe(inventory[1], inventory[0]).getEnergy() * energyMod / ENERGY_BASE;
		processRem = processMax;
	}

	@Override
	protected void processFinish() {

		SmelterRecipe recipe = SmelterManager.getRecipe(inventory[1], inventory[0]);

		if (recipe == null) {
			processOff();
			return;
		}
		ItemStack primaryItem = recipe.getPrimaryOutput();
		ItemStack secondaryItem = recipe.getSecondaryOutput();

		boolean augmentPyrotheumCheck = augmentPyrotheum && (ItemHelper.isOre(inventory[0]) || ItemHelper.isOre(inventory[1])) && tank.getFluidAmount() >= FLUID_AMOUNT;

		if (augmentPyrotheumCheck) {
			if (inventory[2].isEmpty()) {
				inventory[2] = ItemHelper.cloneStack(primaryItem, getPyrotheumOutputAmount(primaryItem));
			} else {
				inventory[2].grow(getPyrotheumOutputAmount(primaryItem));
			}
		} else {
			if (inventory[2].isEmpty()) {
				inventory[2] = ItemHelper.cloneStack(primaryItem);
			} else {
				inventory[2].grow(primaryItem.getCount());
			}
		}
		if (!secondaryItem.isEmpty()) {
			int modifiedChance = augmentPyrotheumCheck ? secondaryChance - PYROTHEUM_SECONDARY_MOD : secondaryChance;

			int recipeChance = recipe.getSecondaryOutputChance();
			if (recipeChance >= 100 || world.rand.nextInt(modifiedChance) < recipeChance) {
				if (inventory[3].isEmpty()) {
					inventory[3] = ItemHelper.cloneStack(secondaryItem);

					if (recipeChance > modifiedChance && world.rand.nextInt(SECONDARY_BASE) < recipeChance - modifiedChance) {
						inventory[3].grow(secondaryItem.getCount());
					}
				} else if (inventory[3].isItemEqual(secondaryItem)) {
					inventory[3].grow(secondaryItem.getCount());

					if (recipeChance > modifiedChance && world.rand.nextInt(SECONDARY_BASE) < recipeChance - modifiedChance) {
						inventory[3].grow(secondaryItem.getCount());
					}
				}
				if (inventory[3].getCount() > inventory[3].getMaxStackSize()) {
					inventory[3].setCount(inventory[3].getMaxStackSize());
				}
			}
		}
		if (recipe.hasFlux()) { // Flux is *always* secondary input, if present.
			int countInput = recipe.getPrimaryInput().getCount();
			int countFlux = recipe.getSecondaryInput().getCount();

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
			int count1 = recipe.getPrimaryInput().getCount();
			int count2 = recipe.getSecondaryInput().getCount();

			if (InsolatorManager.isRecipeReversed(inventory[0], inventory[1])) {
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

		if (!enableAutoInput) {
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

		if (!enableAutoOutput) {
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

		return augmentPyrotheum && tank.getFluidAmount() >= FLUID_AMOUNT && (ItemHelper.isOre(inventory[0]) || ItemHelper.isOre(inventory[1]));
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

		inputTrackerPrimary = nbt.getInteger("TrackIn1");
		inputTrackerSecondary = nbt.getInteger("TrackIn2");
		outputTrackerPrimary = nbt.getInteger("TrackOut1");
		outputTrackerSecondary = nbt.getInteger("TrackOut2");
		lockPrimary = nbt.getBoolean("SlotLock");
		tank.readFromNBT(nbt);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {

		super.writeToNBT(nbt);

		nbt.setInteger("TrackIn1", inputTrackerPrimary);
		nbt.setInteger("TrackIn2", inputTrackerSecondary);
		nbt.setInteger("TrackOut1", outputTrackerPrimary);
		nbt.setInteger("TrackOut2", outputTrackerSecondary);
		nbt.setBoolean("SlotLock", lockPrimary);
		tank.writeToNBT(nbt);
		return nbt;
	}

	/* NETWORK METHODS */

	/* CLIENT -> SERVER */
	@Override
	public PacketCoFHBase getModePacket() {

		PacketCoFHBase payload = super.getModePacket();

		payload.addBool(lockPrimary);

		return payload;
	}

	@Override
	protected void handleModePacket(PacketCoFHBase payload) {

		super.handleModePacket(payload);

		lockPrimary = payload.getBool();

		callNeighborTileChange();
	}

	/* SERVER -> CLIENT */
	@Override
	public PacketCoFHBase getGuiPacket() {

		PacketCoFHBase payload = super.getGuiPacket();

		payload.addBool(lockPrimary);
		payload.addBool(augmentPyrotheum);
		payload.addFluidStack(tank.getFluid());

		return payload;
	}

	@Override
	protected void handleGuiPacket(PacketCoFHBase payload) {

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
			tank.modifyFluidStored(-tank.getCapacity());
		}
	}

	@Override
	protected boolean installAugmentToSlot(int slot) {

		String id = AugmentHelper.getAugmentIdentifier(augments[slot]);

		if (TEProps.MACHINE_SMELTER_FLUX.equals(id)) {
			reuseChance += 10;
			energyMod += 10;
		}
		if (!augmentPyrotheum && TEProps.MACHINE_SMELTER_PYROTHEUM.equals(id)) {
			augmentPyrotheum = true;
			hasModeAugment = true;
			energyMod += PYROTHEUM_ENERGY_MOD;
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

		return TESounds.machineSmelter;
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

					if (from != null && !allowInsertion(sideConfig.sideTypes[sideCache[from.ordinal()]])) {
						return 0;
					}
					return tank.fill(resource, doFill);
				}

				@Nullable
				@Override
				public FluidStack drain(FluidStack resource, boolean doDrain) {

					if (isActive) {
						return null;
					}
					return tank.drain(resource, doDrain);
				}

				@Nullable
				@Override
				public FluidStack drain(int maxDrain, boolean doDrain) {

					if (isActive) {
						return null;
					}
					return tank.drain(maxDrain, doDrain);
				}
			});
		}
		return super.getCapability(capability, from);
	}

}
