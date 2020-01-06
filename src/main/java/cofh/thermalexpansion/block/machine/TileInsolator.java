package cofh.thermalexpansion.block.machine;

import cofh.api.item.IAugmentItem.AugmentType;
import cofh.core.fluid.FluidTankCore;
import cofh.core.init.CoreProps;
import cofh.core.network.PacketBase;
import cofh.core.util.core.EnergyConfig;
import cofh.core.util.core.SideConfig;
import cofh.core.util.core.SlotConfig;
import cofh.core.util.helpers.AugmentHelper;
import cofh.core.util.helpers.ItemHelper;
import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalexpansion.block.machine.BlockMachine.Type;
import cofh.thermalexpansion.gui.client.machine.GuiInsolator;
import cofh.thermalexpansion.gui.container.machine.ContainerInsolator;
import cofh.thermalexpansion.init.TEProps;
import cofh.thermalexpansion.util.managers.machine.InsolatorManager;
import cofh.thermalexpansion.util.managers.machine.InsolatorManager.InsolatorRecipe;
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

import static cofh.core.util.core.SideConfig.*;

public class TileInsolator extends TileMachineBase {

	private static final int TYPE = Type.INSOLATOR.getMetadata();
	public static int basePower = 20;

	public static final int MONOCULTURE_ENERGY_MOD = 50;

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
		VALID_AUGMENTS[TYPE].add(TEProps.MACHINE_INSOLATOR_FERTILIZER);
		VALID_AUGMENTS[TYPE].add(TEProps.MACHINE_INSOLATOR_MONOCULTURE);
		VALID_AUGMENTS[TYPE].add(TEProps.MACHINE_INSOLATOR_TREE);

		VALID_AUGMENTS[TYPE].add(TEProps.MACHINE_SECONDARY);
		VALID_AUGMENTS[TYPE].add(TEProps.MACHINE_SECONDARY_NULL);

		LIGHT_VALUES[TYPE] = 14;

		GameRegistry.registerTileEntity(TileInsolator.class, "thermalexpansion:machine_insolator");

		config();
	}

	public static void config() {

		String category = "Machine.Insolator";
		BlockMachine.enable[TYPE] = ThermalExpansion.CONFIG.get(category, "Enable", true);

		String comment = "Adjust this value to change the Energy consumption (in RF/t) for a Phytogenic Insolator. This base value will scale with block level and Augments.";
		basePower = ThermalExpansion.CONFIG.getConfiguration().getInt("BasePower", category, basePower, MIN_BASE_POWER, MAX_BASE_POWER, comment);

		ENERGY_CONFIGS[TYPE] = new EnergyConfig();
		ENERGY_CONFIGS[TYPE].setDefaultParams(basePower, smallStorage);
	}

	private InsolatorRecipe curRecipe;
	private int inputTrackerPrimary;
	private int inputTrackerSecondary;
	private int outputTrackerPrimary;
	private int outputTrackerSecondary;

	public boolean lockPrimary = true;

	private FluidTankCore tank = new FluidTankCore(TEProps.MAX_FLUID_LARGE);

	/* AUGMENTS */
	protected boolean augmentTree;
	protected boolean augmentMonoculture;

	public TileInsolator() {

		super();
		inventory = new ItemStack[2 + 1 + 1 + 1];
		Arrays.fill(inventory, ItemStack.EMPTY);
		createAllSlots(inventory.length);
		tank.setLock(FluidRegistry.WATER);
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
		if (tank.getFluidAmount() < curRecipe.getWater()) {
			return false;
		}
		if (curRecipe.getType() == InsolatorManager.Type.TREE && !augmentTree) {
			return false;
		}
		if (InsolatorManager.isRecipeReversed(inventory[0], inventory[1])) {
			if (curRecipe.getPrimaryInput().getCount() > inventory[1].getCount() || curRecipe.getSecondaryInput().getCount() > inventory[0].getCount()) {
				return false;
			}
		} else {
			if (curRecipe.getPrimaryInput().getCount() > inventory[0].getCount() || curRecipe.getSecondaryInput().getCount() > inventory[1].getCount()) {
				return false;
			}
		}
		ItemStack primaryItem = curRecipe.getPrimaryOutput();
		ItemStack secondaryItem = curRecipe.getSecondaryOutput();

		if (!secondaryItem.isEmpty() && !inventory[3].isEmpty()) {
			if (!augmentSecondaryNull) {
				if (!ItemHelper.itemsIdentical(inventory[3], secondaryItem)) {
					return false;
				}
				if (inventory[3].getCount() + secondaryItem.getCount() > secondaryItem.getMaxStackSize()) {
					return false;
				}
			}
		}
		return inventory[2].isEmpty() || ItemHelper.itemsIdentical(inventory[2], primaryItem) && inventory[2].getCount() + primaryItem.getCount() <= primaryItem.getMaxStackSize();
	}

	@Override
	protected boolean hasValidInput() {

		if (curRecipe == null) {
			getRecipe();
		}
		if (curRecipe == null) {
			return false;
		}
		if (InsolatorManager.isRecipeReversed(inventory[0], inventory[1])) {
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

		curRecipe = InsolatorManager.getRecipe(inventory[1], inventory[0]);
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
		tank.modifyFluidStored(-curRecipe.getWater());
		ItemStack primaryItem = curRecipe.getPrimaryOutput();
		ItemStack secondaryItem = curRecipe.getSecondaryOutput();
		boolean hasFertilizer = curRecipe.hasFertilizer();

		if (hasFertilizer) { // Fertilizer is *always* secondary input, if present.
			ItemStack input = curRecipe.getPrimaryInput();

			if (inventory[2].isEmpty()) {
				inventory[2] = ItemHelper.cloneStack(primaryItem);
			} else {
				inventory[2].grow(primaryItem.getCount());
			}
			if (!secondaryItem.isEmpty()) {
				int modifiedChance = secondaryChance;

				int recipeChance = curRecipe.getSecondaryOutputChance();
				if (augmentMonoculture && secondaryItem.isItemEqual(input)) {
					recipeChance -= 100;
				}
				if (recipeChance >= 100 || world.rand.nextInt(modifiedChance) < recipeChance) {
					if (inventory[3].isEmpty()) {
						inventory[3] = ItemHelper.cloneStack(secondaryItem);
					} else if (inventory[3].isItemEqual(secondaryItem)) {
						inventory[3].grow(secondaryItem.getCount());
					}
					if (world.rand.nextInt(SECONDARY_BASE) < recipeChance - modifiedChance) {
						inventory[3].grow(secondaryItem.getCount());
					}
					if (inventory[3].getCount() > inventory[3].getMaxStackSize()) {
						inventory[3].setCount(inventory[3].getMaxStackSize());
					}
				}
			}
			int countInput = augmentMonoculture ? 0 : curRecipe.getPrimaryInput().getCount();
			int countFertilizer = curRecipe.getSecondaryInput().getCount();

			if (reuseChance > 0) {
				if (InsolatorManager.isItemFertilizer(inventory[0])) {
					if (world.rand.nextInt(SECONDARY_BASE) >= reuseChance) {
						inventory[0].shrink(countFertilizer);
					}
					inventory[1].shrink(countInput);
				} else {
					if (world.rand.nextInt(SECONDARY_BASE) >= reuseChance) {
						inventory[1].shrink(countFertilizer);
					}
					inventory[0].shrink(countInput);
				}
			} else {
				if (InsolatorManager.isItemFertilizer(inventory[0])) {
					inventory[0].shrink(countFertilizer);
					inventory[1].shrink(countInput);
				} else {
					inventory[1].shrink(countFertilizer);
					inventory[0].shrink(countInput);
				}
			}
		} else {
			if (inventory[2].isEmpty()) {
				inventory[2] = ItemHelper.cloneStack(primaryItem);
			} else {
				inventory[2].grow(primaryItem.getCount());
			}
			if (!secondaryItem.isEmpty()) {
				int modifiedChance = secondaryChance;

				int recipeChance = curRecipe.getSecondaryOutputChance();
				if (recipeChance >= 100 || world.rand.nextInt(modifiedChance) < recipeChance) {
					if (inventory[3].isEmpty()) {
						inventory[3] = ItemHelper.cloneStack(secondaryItem);

						if (world.rand.nextInt(SECONDARY_BASE) < recipeChance - modifiedChance) {
							inventory[3].grow(secondaryItem.getCount());
						}
					} else if (inventory[3].isItemEqual(secondaryItem)) {
						inventory[3].grow(secondaryItem.getCount());

						if (world.rand.nextInt(SECONDARY_BASE) < recipeChance - modifiedChance) {
							inventory[3].grow(secondaryItem.getCount());
						}
					}
					if (inventory[3].getCount() > inventory[3].getMaxStackSize()) {
						inventory[3].setCount(inventory[3].getMaxStackSize());
					}
				}
			}
			int count1 = curRecipe.getPrimaryInput().getCount();
			int count2 = curRecipe.getSecondaryInput().getCount();

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

		return new GuiInsolator(inventory, this);
	}

	@Override
	public Object getGuiServer(InventoryPlayer inventory) {

		return new ContainerInsolator(inventory, this);
	}

	@Override
	public FluidTankCore getTank() {

		return tank;
	}

	@Override
	public FluidStack getTankFluid() {

		return tank.getFluid();
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
		outputTrackerPrimary = nbt.getInteger("Tracker1");
		outputTrackerSecondary = nbt.getInteger("Tracker2");
		lockPrimary = nbt.getBoolean("SlotLock");
		tank.readFromNBT(nbt);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {

		super.writeToNBT(nbt);

		nbt.setInteger(CoreProps.TRACK_IN, inputTrackerPrimary);
		nbt.setInteger(CoreProps.TRACK_IN_2, inputTrackerSecondary);
		nbt.setInteger("Tracker1", outputTrackerPrimary);
		nbt.setInteger("Tracker2", outputTrackerSecondary);
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
		payload.addFluidStack(tank.getFluid());

		return payload;
	}

	@Override
	protected void handleGuiPacket(PacketBase payload) {

		super.handleGuiPacket(payload);

		lockPrimary = payload.getBool();
		tank.setFluid(payload.getFluidStack());
	}

	/* HELPERS */
	@Override
	protected void preAugmentInstall() {

		super.preAugmentInstall();

		augmentTree = false;
		augmentMonoculture = false;
	}

	@Override
	protected boolean isValidAugment(AugmentType type, String id) {

		if (augmentMonoculture && TEProps.MACHINE_INSOLATOR_MONOCULTURE.equals(id)) {
			return false;
		}
		if (augmentTree && TEProps.MACHINE_INSOLATOR_TREE.equals(id)) {
			return false;
		}
		return super.isValidAugment(type, id);
	}

	@Override
	protected boolean installAugmentToSlot(int slot) {

		String id = AugmentHelper.getAugmentIdentifier(augments[slot]);

		if (TEProps.MACHINE_INSOLATOR_FERTILIZER.equals(id)) {
			reuseChance += 20;
			energyMod += 15;
		}
		if (!augmentMonoculture && TEProps.MACHINE_INSOLATOR_MONOCULTURE.equals(id)) {
			augmentMonoculture = true;
			hasModeAugment = true;
			reuseChance += 10;
			energyMod += MONOCULTURE_ENERGY_MOD;
			return true;
		}
		if (!augmentTree && TEProps.MACHINE_INSOLATOR_TREE.equals(id)) {
			augmentTree = true;
			return true;
		}
		return super.installAugmentToSlot(slot);
	}

	/* IInventory */
	@Override
	public boolean isItemValidForSlot(int slot, ItemStack stack) {

		if (lockPrimary) {
			if (slot == 0) {
				return InsolatorManager.isItemFertilizer(stack);
			}
			if (slot == 1) {
				return !InsolatorManager.isItemFertilizer(stack) && InsolatorManager.isItemValid(stack);
			}
		}
		return slot > 1 || InsolatorManager.isItemValid(stack);
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
