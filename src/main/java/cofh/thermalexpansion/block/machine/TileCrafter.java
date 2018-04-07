package cofh.thermalexpansion.block.machine;

import cofh.core.fluid.FluidTankCore;
import cofh.core.inventory.InventoryCraftingFalse;
import cofh.core.network.PacketBase;
import cofh.core.util.helpers.AugmentHelper;
import cofh.core.util.helpers.ItemHelper;
import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalexpansion.block.machine.BlockMachine.Type;
import cofh.thermalexpansion.gui.client.machine.GuiCrafter;
import cofh.thermalexpansion.gui.container.machine.ContainerCrafter;
import cofh.thermalexpansion.init.TEProps;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.InventoryCraftResult;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.RecipeTippedArrow;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.crafting.IShapedRecipe;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.FluidTankProperties;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import net.minecraftforge.fml.common.registry.GameRegistry;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.HashSet;

public class TileCrafter extends TileMachineBase {

	private static final int TYPE = Type.CRAFTER.getMetadata();
	public static int basePower = 20;

	public static final int DEFAULT_ENERGY = 400;

	public static final int SLOT_OUTPUT = 18;
	public static final int SLOT_CRAFTING_START = 20;

	public static void initialize() {

		SIDE_CONFIGS[TYPE] = new SideConfig();
		SIDE_CONFIGS[TYPE].numConfig = 5;
		SIDE_CONFIGS[TYPE].slotGroups = new int[][] { {}, { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17 }, { 18 }, { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18 }, { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18 } };
		SIDE_CONFIGS[TYPE].sideTypes = new int[] { NONE, INPUT_ALL, OUTPUT_ALL, OPEN, OMNI };
		SIDE_CONFIGS[TYPE].defaultSides = new byte[] { 1, 1, 2, 2, 2, 2 };

		ALT_SIDE_CONFIGS[TYPE] = new SideConfig();
		ALT_SIDE_CONFIGS[TYPE].numConfig = 2;
		ALT_SIDE_CONFIGS[TYPE].slotGroups = new int[][] { {}, { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17 }, { 18 }, { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18 }, { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18 } };
		ALT_SIDE_CONFIGS[TYPE].sideTypes = new int[] { NONE, OPEN };
		ALT_SIDE_CONFIGS[TYPE].defaultSides = new byte[] { 1, 1, 1, 1, 1, 1 };

		SLOT_CONFIGS[TYPE] = new SlotConfig();
		SLOT_CONFIGS[TYPE].allowInsertionSlot = new boolean[] { true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, false, false };
		SLOT_CONFIGS[TYPE].allowExtractionSlot = new boolean[] { true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, false };

		VALID_AUGMENTS[TYPE] = new HashSet<>();
		VALID_AUGMENTS[TYPE].add(TEProps.MACHINE_CRAFTER_INPUT);
		VALID_AUGMENTS[TYPE].add(TEProps.MACHINE_CRAFTER_TANK);

		LIGHT_VALUES[TYPE] = 7;

		GameRegistry.registerTileEntity(TileCrafter.class, "thermalexpansion:machine_crafter");

		config();
	}

	public static void config() {

		String category = "Machine.Crafter";
		BlockMachine.enable[TYPE] = ThermalExpansion.CONFIG.get(category, "Enable", true);

		String comment = "Adjust this value to change the Energy consumption (in RF/t) for a Sequential Fabricator. This base value will scale with block level and Augments.";
		basePower = ThermalExpansion.CONFIG.getConfiguration().getInt("BasePower", category, basePower, MIN_BASE_POWER, MAX_BASE_POWER, comment);

		ENERGY_CONFIGS[TYPE] = new EnergyConfig();
		ENERGY_CONFIGS[TYPE].setDefaultParams(basePower, smallStorage);
	}

	private int inputTracker;
	private int outputTracker;

	private InventoryCraftingFalse craftMatrix = new InventoryCraftingFalse(3, 3);
	private InventoryCraftResult craftResult = new InventoryCraftResult();
	private CrafterRecipe craftRecipe;
	private boolean hasRecipeChanges;

	private FluidTankCore tank = new FluidTankCore(TEProps.MAX_FLUID_LARGE);

	/* AUGMENTS */
	protected boolean augmentInput;
	protected boolean augmentTank;
	protected boolean flagTank;
	protected boolean usingTank;

	public TileCrafter() {

		super();
		inventory = new ItemStack[18 + 1 + 1 + 9 + 1];
		Arrays.fill(inventory, ItemStack.EMPTY);
		createAllSlots(inventory.length - 9 - 1);
	}

	@Override
	public int getType() {

		return TYPE;
	}

	@Override
	public int getChargeSlot() {

		return inventory.length - 1 - 9 - 1;
	}

	@Override
	protected int getMaxInputSlot() {

		// This is a hack to prevent super() logic from working.
		return -1;
	}

	@Override
	protected boolean canStart() {

		if (energyStorage.getEnergyStored() <= 0) {
			return false;
		}
		if (craftResult.getRecipeUsed() == null || hasRecipeChanges) {
			return false;
		}
		if (!checkIngredients()) {
			return false;
		}
		ItemStack output = craftResult.getStackInSlot(0);

		return inventory[SLOT_OUTPUT].isEmpty() || inventory[SLOT_OUTPUT].isItemEqual(output) && inventory[SLOT_OUTPUT].getCount() + output.getCount() <= output.getMaxStackSize();
	}

	@Override
	protected void processStart() {

		processMax = DEFAULT_ENERGY;
		processRem = processMax;
	}

	@Override
	protected void processFinish() {

		IRecipe recipe = craftResult.getRecipeUsed();

		if (recipe == null || !checkIngredients()) {
			processOff();
			return;
		}
		craftRecipe.evaluate();

		ItemStack output = recipe.getCraftingResult(craftMatrix);
		NonNullList<ItemStack> remainingItems = recipe.getRemainingItems(craftMatrix);

		for (int i = 0; i < remainingItems.size(); i++) {
			ItemStack remaining = remainingItems.get(i);
			if (!remaining.isEmpty() && !craftRecipe.isFalseBucket(i)) {
				for (int j = 0; j < SLOT_OUTPUT; j++) {
					if (inventory[j].isEmpty()) {
						inventory[j] = remaining;
						break;
					} else if (remaining.getMaxStackSize() > 1 && ItemHelper.itemsIdentical(inventory[j], remaining) && inventory[j].getCount() < inventory[j].getMaxStackSize()) {
						inventory[j].grow(1);
						break;
					}
				}
			}
		}
		if (inventory[SLOT_OUTPUT].isEmpty()) {
			inventory[SLOT_OUTPUT] = ItemHelper.cloneStack(output);
		} else {
			inventory[SLOT_OUTPUT].grow(output.getCount());
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
				for (int j = 0; j < 9; j++) {
					if (!inventory[j + SLOT_CRAFTING_START].isEmpty()) {
						if (!extractItem(j, ITEM_TRANSFER[level], EnumFacing.VALUES[side])) {
							extractItem(j + 9, ITEM_TRANSFER[level], EnumFacing.VALUES[side]);
						}
					}
					inputTracker = side;
				}
				return;
			}
		}
	}

	@Override
	protected void transferOutput() {

		if (!getTransferOut()) {
			return;
		}
		if (inventory[SLOT_OUTPUT].isEmpty()) {
			return;
		}
		int side;
		for (int i = outputTracker + 1; i <= outputTracker + 6; i++) {
			side = i % 6;
			if (isPrimaryOutput(sideConfig.sideTypes[sideCache[side]])) {
				if (transferItem(SLOT_OUTPUT, ITEM_TRANSFER[level], EnumFacing.VALUES[side])) {
					outputTracker = side;
					break;
				}
			}
		}
	}

	public void setRecipe() {

		for (int i = 0; i < 9; i++) {
			craftMatrix.setInventorySlotContents(i, inventory[i + SLOT_CRAFTING_START]);
		}
		IRecipe newRecipe = CraftingManager.findMatchingRecipe(craftMatrix, world);

		ItemStack stack = ItemStack.EMPTY;

		if (newRecipe != null) {
			stack = newRecipe.getCraftingResult(craftMatrix);
			craftRecipe = CrafterRecipe.getRecipe(newRecipe, this);

			if (craftRecipe == null) {
				newRecipe = null;
				stack = ItemStack.EMPTY;
				usingTank = false;
			}
		} else {
			craftRecipe = null;
			usingTank = false;
		}
		if (craftRecipe == null) {
			if (isActive) {
				processOff();
			}
		}
		inventory[SLOT_CRAFTING_START + 9] = stack;
		craftResult.setRecipeUsed(newRecipe);
		craftResult.setInventorySlotContents(0, inventory[SLOT_CRAFTING_START + 9]);
		clearRecipeChanges();
	}

	private boolean checkIngredients() {

		return craftRecipe != null && craftRecipe.checkIngredients();
	}

	private ItemStack getTankAsBucket() {

		if (augmentTank && tank.getFluidAmount() >= Fluid.BUCKET_VOLUME) {
			//noinspection ConstantConditions
			return FluidUtil.getFilledBucket(tank.getFluid());
		}
		return ItemStack.EMPTY;
	}

	/* GUI METHODS */
	@Override
	public int getInvSlotCount() {

		return inventory.length - 9 - 1;
	}

	@Override
	public Object getGuiClient(InventoryPlayer inventory) {

		return new GuiCrafter(inventory, this);
	}

	@Override
	public Object getGuiServer(InventoryPlayer inventory) {

		return new ContainerCrafter(inventory, this);
	}

	@Override
	public FluidTankCore getTank() {

		return tank;
	}

	@Override
	public FluidStack getTankFluid() {

		return tank.getFluid();
	}

	public boolean augmentTank() {

		return augmentTank && flagTank;
	}

	public boolean fluidArrow() {

		return augmentTank() && usingTank;
	}

	public void markRecipeChanges() {

		hasRecipeChanges = true;

		if (isActive) {
			processOff();
		}
	}

	public void clearRecipeChanges() {

		hasRecipeChanges = false;
	}

	/* NBT METHODS */
	@Override
	public void readFromNBT(NBTTagCompound nbt) {

		super.readFromNBT(nbt);

		inputTracker = nbt.getInteger("TrackIn");
		outputTracker = nbt.getInteger("TrackOut");
		tank.readFromNBT(nbt);

		setRecipe();
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {

		super.writeToNBT(nbt);

		nbt.setInteger("TrackIn", inputTracker);
		nbt.setInteger("TrackOut", outputTracker);
		tank.writeToNBT(nbt);
		return nbt;
	}

	/* NETWORK METHODS */

	/* CLIENT -> SERVER */
	@Override
	public PacketBase getModePacket() {

		PacketBase payload = super.getModePacket();

		for (int i = SLOT_CRAFTING_START; i < SLOT_CRAFTING_START + 9; i++) {
			payload.addItemStack(inventory[i]);
		}
		return payload;
	}

	@Override
	protected void handleModePacket(PacketBase payload) {

		super.handleModePacket(payload);

		for (int i = SLOT_CRAFTING_START; i < SLOT_CRAFTING_START + 9; i++) {
			inventory[i] = payload.getItemStack();
		}
		setRecipe();
	}

	/* SERVER -> CLIENT */
	@Override
	public PacketBase getGuiPacket() {

		PacketBase payload = super.getGuiPacket();

		payload.addBool(augmentInput);
		payload.addBool(augmentTank);
		payload.addBool(usingTank);
		payload.addFluidStack(getTankFluid());
		return payload;
	}

	@Override
	protected void handleGuiPacket(PacketBase payload) {

		super.handleGuiPacket(payload);

		augmentInput = payload.getBool();
		augmentTank = payload.getBool();
		flagTank = augmentTank;
		usingTank = payload.getBool();
		tank.setFluid(payload.getFluidStack());
	}

	/* HELPERS */
	@Override
	protected void preAugmentInstall() {

		super.preAugmentInstall();

		augmentInput = false;
		augmentTank = false;
	}

	@Override
	protected void postAugmentInstall() {

		super.postAugmentInstall();

		if (!augmentTank) {
			tank.drain(tank.getCapacity(), true);

			if (usingTank) {
				processOff();
			}
			usingTank = false;
		}
	}

	@Override
	protected boolean installAugmentToSlot(int slot) {

		String id = AugmentHelper.getAugmentIdentifier(augments[slot]);

		if (!augmentInput && TEProps.MACHINE_CRAFTER_INPUT.equals(id)) {
			augmentInput = true;
			return true;
		}
		if (!augmentTank && TEProps.MACHINE_CRAFTER_TANK.equals(id)) {
			augmentTank = true;
			return true;
		}
		return super.installAugmentToSlot(slot);
	}

	/* IInventory */
	@Override
	public int getSizeInventory() {

		return inventory.length - 9 - 1;
	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack stack) {

		if (!augmentInput) {
			return true;
		}
		return slot >= SLOT_OUTPUT || craftRecipe == null || craftRecipe.validStack(stack, slot % 9);
	}

	/* ITransferControl */
	@Override
	public boolean hasTransferIn() {

		return augmentInput && hasAutoInput;
	}

	@Override
	public boolean getTransferIn() {

		return hasTransferIn() && enableAutoInput && !hasRecipeChanges;
	}

	/* CAPABILITIES */
	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing from) {

		return super.hasCapability(capability, from) || augmentTank && capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY;
	}

	@Override
	public <T> T getCapability(Capability<T> capability, final EnumFacing from) {

		if (augmentTank && capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
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

	/* RECIPE CLASS */
	public static class CrafterRecipe {

		private TileCrafter myTile;
		private boolean valid = true;

		private int[] craftSlots = new int[9];
		private Ingredient[] craftIngredients;

		private boolean isItemStackRecipe = false;
		private ItemStack[] craftStacks = new ItemStack[9];

		public static CrafterRecipe getRecipe(IRecipe recipe, TileCrafter tile) {

			CrafterRecipe wrapper = new CrafterRecipe(recipe, tile);
			return wrapper.valid ? wrapper : null;
		}

		public static boolean validRecipe(IRecipe recipe) {

			return recipe.getIngredients().size() > 0 || recipe instanceof RecipeTippedArrow;
		}

		private CrafterRecipe(IRecipe recipe, TileCrafter tile) {

			myTile = tile;
			NonNullList<Ingredient> ingredients = recipe.getIngredients();
			craftIngredients = new Ingredient[9];

			if (recipe instanceof IShapedRecipe) {
				int j = 0;
				for (int i = 0; i < 9; i++) {
					if (myTile.inventory[SLOT_CRAFTING_START + i].isEmpty()) {
						craftIngredients[i] = Ingredient.EMPTY;
						if (ingredients.size() > j && ingredients.get(j).equals(Ingredient.EMPTY)) {
							j++;
						}
					} else {
						craftIngredients[i] = ingredients.get(j);
						j++;
					}
				}
			} else if (ingredients.size() > 0) {
				ItemStack[] sortedRecipe = new ItemStack[9];
				for (int i = 0; i < ingredients.size(); i++) {
					for (int j = SLOT_CRAFTING_START; j < SLOT_CRAFTING_START + 9; j++) {
						if (ingredients.get(i).apply(myTile.inventory[j])) {
							craftIngredients[i] = ingredients.get(i);
							sortedRecipe[i] = myTile.inventory[j].copy();
						}
					}
				}
				for (int i = ingredients.size(); i < 9; i++) {
					craftIngredients[i] = Ingredient.EMPTY;
					sortedRecipe[i] = ItemStack.EMPTY;
				}
				System.arraycopy(sortedRecipe, 0, myTile.inventory, 20, 9);
			} else if (recipe instanceof RecipeTippedArrow) {
				isItemStackRecipe = true;
				for (int i = 0; i < 9; i++) {
					if (myTile.inventory[SLOT_CRAFTING_START + i].isEmpty()) {
						craftStacks[i] = ItemStack.EMPTY;
					} else {
						craftStacks[i] = ItemHelper.cloneStack(myTile.inventory[SLOT_CRAFTING_START + i], 1);
					}
				}
			} else {
				craftIngredients = null;
				craftStacks = null;
				valid = false;
			}
		}

		private void evaluate() {

			myTile.craftMatrix = new InventoryCraftingFalse(3, 3);

			for (int i = 0; i < 9; i++) {
				if (craftSlots[i] > 0) {
					myTile.craftMatrix.setInventorySlotContents(i, ItemHelper.cloneStack(myTile.inventory[craftSlots[i] - 1], 1));
					myTile.inventory[craftSlots[i] - 1].shrink(1);
					if (myTile.inventory[craftSlots[i] - 1].getCount() <= 0) {
						myTile.inventory[craftSlots[i] - 1] = ItemStack.EMPTY;
					}
				} else if (craftSlots[i] < 0) {
					myTile.craftMatrix.setInventorySlotContents(i, ItemHelper.cloneStack(myTile.getTankAsBucket(), 1));
					myTile.tank.drain(Fluid.BUCKET_VOLUME, true);
				} else {
					myTile.craftMatrix.setInventorySlotContents(i, ItemStack.EMPTY);
				}
			}
		}

		private boolean checkIngredients() {

			craftSlots = new int[9];
			int[] craftCount = new int[18];
			myTile.usingTank = false;

			ItemStack tankStack = myTile.getTankAsBucket();
			int tankCount = 0;

			if (isItemStackRecipe) {
				scan:
				for (int i = 0; i < 9; i++) {
					if (craftStacks[i].equals(ItemStack.EMPTY)) {
						continue;
					}
					for (int j = 0; j < SLOT_OUTPUT; j++) {
						if (ItemHelper.itemsIdentical(craftStacks[i], myTile.inventory[j]) && myTile.inventory[j].getCount() - craftCount[j] > 0) {
							craftCount[j]++;
							craftSlots[i] = j + 1;
							continue scan;
						}
					}
					return false;
				}
			} else {
				scan:
				for (int i = 0; i < 9; i++) {
					if (craftIngredients[i].equals(Ingredient.EMPTY)) {
						continue;
					}
					if (!tankStack.isEmpty()) {
						if (craftIngredients[i].apply(tankStack) && myTile.tank.getFluidAmount() - tankCount > 0) {
							tankCount += Fluid.BUCKET_VOLUME;
							craftSlots[i] = -1;
							myTile.usingTank = true;
							continue;
						}
					}
					for (int j = 0; j < SLOT_OUTPUT; j++) {
						if (craftIngredients[i].apply(myTile.inventory[j]) && myTile.inventory[j].getCount() - craftCount[j] > 0) {
							craftCount[j]++;
							craftSlots[i] = j + 1;
							continue scan;
						}
					}
					return false;
				}
			}
			return true;
		}

		private boolean isFalseBucket(int slot) {

			return craftSlots[slot] == -1;
		}

		private boolean validStack(ItemStack stack, int slot) {

			if (isItemStackRecipe) {
				return ItemHelper.itemsIdentical(stack, craftStacks[slot]);
			}
			return craftIngredients[slot].apply(stack);
		}

	}

}
