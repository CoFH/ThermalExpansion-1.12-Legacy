package thermalexpansion.util.crafting;

import cofh.util.ItemHelper;
import cofh.util.inventory.ComparableItemStack;

import gnu.trove.map.TMap;
import gnu.trove.map.hash.THashMap;

import java.util.ArrayList;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraftforge.oredict.OreDictionary;

import thermalexpansion.ThermalExpansion;
import thermalexpansion.item.TEItems;

public class SawmillManager {

	private static TMap<ComparableItemStackSawmill, RecipeSawmill> recipeMap = new THashMap();
	private static ComparableItemStackSawmill query = new ComparableItemStackSawmill(new ItemStack(Blocks.stone));
	private static boolean allowOverwrite = false;

	static {
		allowOverwrite = ThermalExpansion.config.get("tweak.crafting", "Sawmill.AllowRecipeOverwrite", false);
	}

	public static RecipeSawmill getRecipe(ItemStack input) {

		if (input == null) {
			return null;
		}
		return recipeMap.get(query.set(input));
	}

	public static boolean recipeExists(ItemStack input) {

		return getRecipe(input) != null;
	}

	public static RecipeSawmill[] getRecipeList() {

		return recipeMap.values().toArray(new RecipeSawmill[0]);
	}

	public static void addDefaultRecipes() {

		addRecipe(2400, new ItemStack(Blocks.noteblock), new ItemStack(Blocks.planks, 8), new ItemStack(Items.redstone, 1));
		addRecipe(2400, new ItemStack(Items.bed), new ItemStack(Blocks.planks, 3), new ItemStack(Blocks.wool, 3));
		addRecipe(2400, new ItemStack(Blocks.bookshelf), new ItemStack(Blocks.planks, 6), new ItemStack(Items.book, 3));
		addRecipe(2400, new ItemStack(Blocks.chest), new ItemStack(Blocks.planks, 8));
		addRecipe(2400, new ItemStack(Blocks.crafting_table), new ItemStack(Blocks.planks, 4));
		addRecipe(2400, new ItemStack(Items.sign), new ItemStack(Blocks.planks, 2));
		addRecipe(2400, new ItemStack(Items.wooden_door), new ItemStack(Blocks.planks, 6));
		addRecipe(2400, new ItemStack(Blocks.wooden_pressure_plate), new ItemStack(Blocks.planks, 2));
		addRecipe(2400, new ItemStack(Blocks.jukebox), new ItemStack(Blocks.planks, 8), new ItemStack(Items.diamond, 1));
		addRecipe(2400, new ItemStack(Blocks.trapdoor), new ItemStack(Blocks.planks, 3));
		addRecipe(2400, new ItemStack(Blocks.fence_gate), new ItemStack(Blocks.planks, 2), TEItems.sawdust);

		addRecipe(2400, new ItemStack(Items.boat), new ItemStack(Blocks.planks, 5));
		addRecipe(1600, new ItemStack(Items.wooden_sword), new ItemStack(Blocks.planks, 2), TEItems.sawdust);
		addRecipe(1600, new ItemStack(Items.wooden_shovel), new ItemStack(Blocks.planks, 1), TEItems.sawdust);
		addRecipe(1600, new ItemStack(Items.wooden_pickaxe), new ItemStack(Blocks.planks, 3), TEItems.sawdust);
		addRecipe(1600, new ItemStack(Items.wooden_axe), new ItemStack(Blocks.planks, 3), TEItems.sawdust);
		addRecipe(1600, new ItemStack(Items.wooden_hoe), new ItemStack(Blocks.planks, 2), TEItems.sawdust);
	}

	public static void loadRecipes() {

		addDefaultRecipes();

		addAllLogs();

		if (ItemHelper.oreNameExists("woodRubber")) {
			if (ItemHelper.oreNameExists("itemRawRubber")) {
				addRecipe(1200, OreDictionary.getOres("woodRubber").get(0), new ItemStack(Blocks.planks, 5, 3), OreDictionary.getOres("itemRawRubber").get(0),
						50);
			} else if (ItemHelper.oreNameExists("itemRubber")) {
				addRecipe(1200, OreDictionary.getOres("woodRubber").get(0), new ItemStack(Blocks.planks, 5, 3), OreDictionary.getOres("itemRubber").get(0), 50);
			} else {
				addRecipe(1200, OreDictionary.getOres("woodRubber").get(0), new ItemStack(Blocks.planks, 5, 3));
			}
		}
	}

	/* ADD RECIPES */
	public static boolean addTERecipe(int energy, ItemStack input, ItemStack primaryOutput, ItemStack secondaryOutput, int secondaryChance) {

		if (input == null || primaryOutput == null || energy <= 0) {
			return false;
		}
		RecipeSawmill recipe = new RecipeSawmill(input, primaryOutput, secondaryOutput, secondaryChance, energy);
		recipeMap.put(new ComparableItemStackSawmill(input), recipe);
		return true;
	}

	public static boolean addRecipe(int energy, ItemStack input, ItemStack primaryOutput, ItemStack secondaryOutput, int secondaryChance, boolean overwrite) {

		if (input == null || primaryOutput == null || energy <= 0 || !(allowOverwrite & overwrite) && recipeExists(input)) {
			return false;
		}
		RecipeSawmill recipe = new RecipeSawmill(input, primaryOutput, secondaryOutput, secondaryChance, energy);
		recipeMap.put(new ComparableItemStackSawmill(input), recipe);
		return true;
	}

	/* HELPER FUNCTIONS */
	public static void addAllLogs() {

		Container tempContainer = new Container() {

			@Override
			public boolean canInteractWith(EntityPlayer player) {

				return false;
			}

		};
		InventoryCrafting tempCrafting = new InventoryCrafting(tempContainer, 3, 3);
		ArrayList recipeList = (ArrayList) CraftingManager.getInstance().getRecipeList();

		for (int i = 1; i < 9; i++) {
			tempCrafting.setInventorySlotContents(i, null);
		}
		ArrayList<ItemStack> registeredOres;
		registeredOres = OreDictionary.getOres("logWood");
		for (int i = 0; i < registeredOres.size(); i++) {
			ItemStack logEntry = registeredOres.get(i);

			if (logEntry.getItemDamage() == OreDictionary.WILDCARD_VALUE) {
				for (int j = 0; j < 16; j++) {
					ItemStack log = ItemHelper.cloneStack(logEntry, 1);
					tempCrafting.setInventorySlotContents(0, log);
					ItemStack resultEntry = ItemHelper.findMatchingRecipe(tempCrafting, null);

					if (resultEntry != null) {
						ItemStack result = resultEntry.copy();
						result.stackSize *= 1.5F;
						addRecipe(800, log, result, TEItems.sawdust);
					}
				}
			} else {
				ItemStack log = ItemHelper.cloneStack(logEntry, 1);
				tempCrafting.setInventorySlotContents(0, log);
				ItemStack resultEntry = ItemHelper.findMatchingRecipe(tempCrafting, null);

				if (resultEntry != null) {
					ItemStack result = resultEntry.copy();
					result.stackSize *= 1.5F;
					addRecipe(800, log, result, TEItems.sawdust);
				}
			}
		}
	}

	public static boolean addTERecipe(int energy, ItemStack input, ItemStack primaryOutput) {

		return addTERecipe(energy, input, primaryOutput, null, 0);
	}

	public static boolean addTERecipe(int energy, ItemStack input, ItemStack primaryOutput, ItemStack secondaryOutput) {

		return addTERecipe(energy, input, primaryOutput, secondaryOutput, 100);
	}

	public static boolean addRecipe(int energy, ItemStack input, ItemStack primaryOutput) {

		return addRecipe(energy, input, primaryOutput, false);
	}

	public static boolean addRecipe(int energy, ItemStack input, ItemStack primaryOutput, boolean overwrite) {

		return addRecipe(energy, input, primaryOutput, null, 0, overwrite);
	}

	public static boolean addRecipe(int energy, ItemStack input, ItemStack primaryOutput, ItemStack secondaryOutput) {

		return addRecipe(energy, input, primaryOutput, secondaryOutput, false);
	}

	public static boolean addRecipe(int energy, ItemStack input, ItemStack primaryOutput, ItemStack secondaryOutput, boolean overwrite) {

		return addRecipe(energy, input, primaryOutput, secondaryOutput, 100, overwrite);
	}

	public static boolean addRecipe(int energy, ItemStack input, ItemStack primaryOutput, ItemStack secondaryOutput, int secondaryChance) {

		return addRecipe(energy, input, primaryOutput, secondaryOutput, secondaryChance, false);
	}

	/* RECIPE CLASS */
	public static class RecipeSawmill {

		final ItemStack input;
		final ItemStack primaryOutput;
		final ItemStack secondaryOutput;
		final int secondaryChance;
		final int energy;

		RecipeSawmill(ItemStack input, ItemStack primaryOutput, ItemStack secondaryOutput, int secondaryChance, int energy) {

			this.input = input;
			this.primaryOutput = primaryOutput;
			this.secondaryOutput = secondaryOutput;
			this.secondaryChance = secondaryChance;
			this.energy = energy;
		}

		public ItemStack getInput() {

			return input.copy();
		}

		public ItemStack getPrimaryOutput() {

			return primaryOutput.copy();
		}

		public ItemStack getSecondaryOutput() {

			if (secondaryOutput == null) {
				return null;
			}
			return secondaryOutput.copy();
		}

		public int getSecondaryOutputChance() {

			return secondaryChance;
		}

		public int getEnergy() {

			return energy;
		}
	}

	/* ITEMSTACK CLASS */
	public static class ComparableItemStackSawmill extends ComparableItemStack {

		static final String BLOCK = "block";
		static final String ORE = "ore";
		static final String DUST = "dust";
		static final String INGOT = "ingot";
		static final String NUGGET = "nugget";

		static final String RUBBER = "woodRubber";

		public static boolean safeOreType(String oreName) {

			return oreName.startsWith(BLOCK) || oreName.startsWith(ORE) || oreName.startsWith(DUST) || oreName.startsWith(INGOT) || oreName.startsWith(NUGGET)
					|| oreName.equals(RUBBER);
		}

		public static int getOreID(ItemStack stack) {

			int id = OreDictionary.getOreID(stack);

			if (id == -1 || !safeOreType(OreDictionary.getOreName(id))) {
				return -1;
			}
			return id;
		}

		public static int getOreID(String oreName) {

			if (!safeOreType(oreName)) {
				return -1;
			}
			return OreDictionary.getOreID(oreName);
		}

		public ComparableItemStackSawmill(ItemStack stack) {

			super(stack);
			oreID = getOreID(stack);
		}

		public ComparableItemStackSawmill(int itemID, int damage, int stackSize) {

			super(itemID, damage, stackSize);
			this.oreID = getOreID(this.toItemStack());
		}

		@Override
		public ComparableItemStackSawmill set(ItemStack stack) {

			super.set(stack);
			oreID = getOreID(stack);

			return this;
		}
	}

}
