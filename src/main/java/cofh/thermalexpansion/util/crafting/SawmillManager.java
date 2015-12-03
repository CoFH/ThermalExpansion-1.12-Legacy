package cofh.thermalexpansion.util.crafting;

import cofh.lib.inventory.ComparableItemStack;
import cofh.lib.util.helpers.ItemHelper;
import cofh.lib.util.helpers.MathHelper;
import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalexpansion.api.crafting.recipes.ISawmillRecipe;
import cofh.thermalexpansion.item.TEItems;

import gnu.trove.map.hash.THashMap;

import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public class SawmillManager {

	private static Map<ComparableItemStackSawmill, RecipeSawmill> recipeMap = new THashMap<ComparableItemStackSawmill, RecipeSawmill>();
	private static boolean allowOverwrite = false;
	public static final int DEFAULT_ENERGY = 1600;

	private static float logMultiplier = 1.5F;

	static {
		allowOverwrite = ThermalExpansion.config.get("RecipeManagers.Sawmill", "AllowRecipeOverwrite", false);

		String category = "RecipeManagers.Sawmill.Log";
		String comment = "This sets the default rate for Log->Plank conversion. This number is used in all automatically generated recipes.";
		logMultiplier = MathHelper.clampF((float) ThermalExpansion.config.get(category, "DefaultMultiplier", logMultiplier, comment), 1F, 64F);
	}

	public static RecipeSawmill getRecipe(ItemStack input) {

		if (input == null) {
			return null;
		}
		ComparableItemStackSawmill query = new ComparableItemStackSawmill(input);

		RecipeSawmill recipe = recipeMap.get(query);

		if (recipe == null) {
			query.metadata = OreDictionary.WILDCARD_VALUE;
			recipe = recipeMap.get(query);
		}
		return recipe;
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
		addRecipe(800, new ItemStack(Blocks.melon_block), new ItemStack(Items.melon, 8));

		addRecipe(2400, new ItemStack(Items.boat), new ItemStack(Blocks.planks, 5));
		addRecipe(1600, new ItemStack(Items.wooden_sword), new ItemStack(Blocks.planks, 2), TEItems.sawdust);
		addRecipe(1600, new ItemStack(Items.wooden_shovel), new ItemStack(Blocks.planks, 1), TEItems.sawdust);
		addRecipe(1600, new ItemStack(Items.wooden_pickaxe), new ItemStack(Blocks.planks, 3), TEItems.sawdust);
		addRecipe(1600, new ItemStack(Items.wooden_axe), new ItemStack(Blocks.planks, 3), TEItems.sawdust);
		addRecipe(1600, new ItemStack(Items.wooden_hoe), new ItemStack(Blocks.planks, 2), TEItems.sawdust);
	}

	public static void loadRecipes() {

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

	public static void refreshRecipes() {

		Map<ComparableItemStackSawmill, RecipeSawmill> tempMap = new THashMap<ComparableItemStackSawmill, RecipeSawmill>(recipeMap.size());
		RecipeSawmill tempRecipe;

		for (Entry<ComparableItemStackSawmill, RecipeSawmill> entry : recipeMap.entrySet()) {
			tempRecipe = entry.getValue();
			tempMap.put(new ComparableItemStackSawmill(tempRecipe.input), tempRecipe);
		}
		recipeMap.clear();
		recipeMap = tempMap;
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

	/* REMOVE RECIPES */
	public static boolean removeRecipe(ItemStack input) {

		return recipeMap.remove(new ComparableItemStackSawmill(input)) != null;
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

		for (int i = 0; i < 9; i++) {
			tempCrafting.setInventorySlotContents(i, null);
		}
		ArrayList<ItemStack> registeredOres;
		registeredOres = OreDictionary.getOres("logWood");
		for (int i = 0; i < registeredOres.size(); i++) {
			ItemStack logEntry = registeredOres.get(i);

			if (ItemHelper.getItemDamage(logEntry) == OreDictionary.WILDCARD_VALUE) {
				for (int j = 0; j < 16; j++) {
					ItemStack log = ItemHelper.cloneStack(logEntry, 1);
					log.setItemDamage(j);
					tempCrafting.setInventorySlotContents(0, log);
					ItemStack resultEntry = ItemHelper.findMatchingRecipe(tempCrafting, null);

					if (resultEntry != null) {
						ItemStack result = resultEntry.copy();
						result.stackSize *= logMultiplier;
						addRecipe(800, log, result, TEItems.sawdust);
					}
				}
			} else {
				ItemStack log = ItemHelper.cloneStack(logEntry, 1);
				tempCrafting.setInventorySlotContents(0, log);
				ItemStack resultEntry = ItemHelper.findMatchingRecipe(tempCrafting, null);

				if (resultEntry != null) {
					ItemStack result = resultEntry.copy();
					result.stackSize *= logMultiplier;
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
	public static class RecipeSawmill implements ISawmillRecipe {

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

			if (input.stackSize <= 0) {
				input.stackSize = 1;
			}
			if (primaryOutput.stackSize <= 0) {
				primaryOutput.stackSize = 1;
			}
			if (secondaryOutput != null && secondaryOutput.stackSize <= 0) {
				secondaryOutput.stackSize = 1;
			}
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

		static final String ORE = "ore";
		static final String INGOT = "ingot";
		static final String NUGGET = "nugget";

		static final String RUBBER = "woodRubber";

		public static boolean safeOreType(String oreName) {

			return oreName.startsWith(ORE) || oreName.startsWith(INGOT) || oreName.startsWith(NUGGET) || oreName.equals(RUBBER);
		}

		public static int getOreID(ItemStack stack) {

			int id = ItemHelper.oreProxy.getOreID(stack);

			if (id == -1 || !safeOreType(ItemHelper.oreProxy.getOreName(id))) {
				return -1;
			}
			return id;
		}

		public static int getOreID(String oreName) {

			if (!safeOreType(oreName)) {
				return -1;
			}
			return ItemHelper.oreProxy.getOreID(oreName);
		}

		public ComparableItemStackSawmill(ItemStack stack) {

			super(stack);
			oreID = getOreID(stack);
		}

		public ComparableItemStackSawmill(Item item, int damage, int stackSize) {

			super(item, damage, stackSize);
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
