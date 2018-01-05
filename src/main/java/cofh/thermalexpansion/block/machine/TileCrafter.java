package cofh.thermalexpansion.block.machine;

import cofh.core.fluid.FluidTankCore;
import cofh.core.inventory.InventoryCraftingFalse;
import cofh.core.network.PacketBase;
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
import net.minecraftforge.common.crafting.IShapedRecipe;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.registry.GameRegistry;

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

		SLOT_CONFIGS[TYPE] = new SlotConfig();
		SLOT_CONFIGS[TYPE].allowInsertionSlot = new boolean[] { true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, false, false };
		SLOT_CONFIGS[TYPE].allowExtractionSlot = new boolean[] { true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, false };

		VALID_AUGMENTS[TYPE] = new HashSet<>();

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

	private FluidTankCore tank = new FluidTankCore(TEProps.MAX_FLUID_LARGE);

	protected boolean validRecipe;

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
		if (craftResult.getRecipeUsed() == null) {
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

		for (ItemStack remaining : remainingItems) {
			if (!remaining.isEmpty()) {
				for (int i = 0; i < SLOT_OUTPUT; i++) {
					if (inventory[i].isEmpty()) {
						inventory[i] = remaining;
						break;
					} else if (remaining.getMaxStackSize() > 1 && ItemHelper.itemsIdentical(inventory[i], remaining) && inventory[i].getCount() < inventory[i].getMaxStackSize()) {
						inventory[i].grow(1);
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
	protected void transferOutput() {

		if (!enableAutoOutput) {
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

	@Override
	protected void setLevelFlags() {

		super.setLevelFlags();

		hasAutoInput = false;
		enableAutoInput = false;
	}

	private boolean checkIngredients() {

		return craftRecipe != null && craftRecipe.checkIngredients();
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

	public boolean hasRecipe() {

		return validRecipe;
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

	public void setRecipe() {

		for (int i = 0; i < 9; i++) {
			craftMatrix.setInventorySlotContents(i, inventory[i + SLOT_CRAFTING_START]);
		}
		IRecipe curRecipe = craftResult.getRecipeUsed();
		IRecipe newRecipe = CraftingManager.findMatchingRecipe(craftMatrix, world);

		if (curRecipe != null && curRecipe.equals(newRecipe)) {
			return;
		}
		ItemStack stack = ItemStack.EMPTY;

		if (newRecipe != null) {
			stack = newRecipe.getCraftingResult(craftMatrix);
			craftRecipe = CrafterRecipe.getRecipe(newRecipe, this);

			if (craftRecipe == null) {
				newRecipe = null;
				stack = ItemStack.EMPTY;
			}
		} else {
			craftRecipe = null;
		}
		if (craftRecipe == null) {
			if (isActive) {
				processOff();
			}
		}
		inventory[SLOT_CRAFTING_START + 9] = stack;
		craftResult.setRecipeUsed(newRecipe);
		craftResult.setInventorySlotContents(0, inventory[SLOT_CRAFTING_START + 9]);
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

		payload.addBool(craftRecipe != null);
		payload.addFluidStack(getTankFluid());
		return payload;
	}

	@Override
	protected void handleGuiPacket(PacketBase payload) {

		super.handleGuiPacket(payload);

		validRecipe = payload.getBool();
		tank.setFluid(payload.getFluidStack());
	}

	/* IInventory */
	@Override
	public int getSizeInventory() {

		return inventory.length - 9 - 1;
	}

	/* CAPABILITIES */
	//	@Override
	//	public boolean hasCapability(Capability<?> capability, EnumFacing from) {
	//
	//		return super.hasCapability(capability, from) || capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY;
	//	}
	//
	//	@Override
	//	public <T> T getCapability(Capability<T> capability, final EnumFacing from) {
	//
	//		if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
	//			return CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.cast(new IFluidHandler() {
	//
	//				@Override
	//				public IFluidTankProperties[] getTankProperties() {
	//
	//					FluidTankInfo info = tank.getInfo();
	//					return new IFluidTankProperties[] { new FluidTankProperties(info.fluid, info.capacity, true, true) };
	//				}
	//
	//				@Override
	//				public int fill(FluidStack resource, boolean doFill) {
	//
	//					return tank.fill(resource, doFill);
	//				}
	//
	//				@Nullable
	//				@Override
	//				public FluidStack drain(FluidStack resource, boolean doDrain) {
	//
	//					if (isActive) {
	//						return null;
	//					}
	//					return tank.drain(resource, doDrain);
	//				}
	//
	//				@Nullable
	//				@Override
	//				public FluidStack drain(int maxDrain, boolean doDrain) {
	//
	//					if (isActive) {
	//						return null;
	//					}
	//					return tank.drain(maxDrain, doDrain);
	//				}
	//			});
	//		}
	//		return super.getCapability(capability, from);
	//	}

	/* RECIPE CLASS */
	public static class CrafterRecipe {

		private TileCrafter myTile;
		private boolean valid = true;

		private int[] craftSlots = new int[9];
		private Ingredient[] craftIngredients = new Ingredient[9];

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

		private boolean checkIngredients() {

			craftSlots = new int[9];
			int[] craftCount = new int[18];

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

		private void evaluate() {

			myTile.craftMatrix = new InventoryCraftingFalse(3, 3);

			for (int i = 0; i < 9; i++) {
				if (craftSlots[i] > 0) {
					myTile.craftMatrix.setInventorySlotContents(i, ItemHelper.cloneStack(myTile.inventory[craftSlots[i] - 1], 1));
					myTile.inventory[craftSlots[i] - 1].shrink(1);
					if (myTile.inventory[craftSlots[i] - 1].getCount() <= 0) {
						myTile.inventory[craftSlots[i] - 1] = ItemStack.EMPTY;
					}
				} else {
					myTile.craftMatrix.setInventorySlotContents(i, ItemStack.EMPTY);
				}
			}
		}

	}

}
