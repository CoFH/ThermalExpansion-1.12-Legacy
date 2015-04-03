package cofh.thermalexpansion.util.crafting;

import cofh.core.util.oredict.OreDictionaryArbiter;
import cofh.lib.inventory.ComparableItemStackSafe;
import cofh.lib.util.helpers.ItemHelper;
import cofh.thermalexpansion.ThermalExpansion;

import gnu.trove.map.hash.THashMap;

import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public class ChargerManager {

	private static Map<ComparableItemStackSafe, RecipeCharger> recipeMap = new THashMap<ComparableItemStackSafe, RecipeCharger>();
	private static ComparableItemStackSafe query = new ComparableItemStackSafe(new ItemStack(Blocks.stone));
	private static boolean allowOverwrite = false;
	public static final int DEFAULT_ENERGY = 4000;

	static {
		allowOverwrite = ThermalExpansion.config.get("RecipeManagers.Charger", "AllowRecipeOverwrite", false);
	}

	public static RecipeCharger getRecipe(ItemStack input) {

		if (input == null) {
			return null;
		}
		RecipeCharger recipe = recipeMap.get(query.set(input));

		if (recipe == null) {
			query.metadata = OreDictionary.WILDCARD_VALUE;
			recipe = recipeMap.get(query);
		}
		return recipe;
	}

	public static boolean recipeExists(ItemStack input) {

		return getRecipe(input) != null;
	}

	public static RecipeCharger[] getRecipeList() {

		return recipeMap.values().toArray(new RecipeCharger[0]);
	}

	public static void addDefaultRecipes() {

	}

	public static void loadRecipes() {

		if (ItemHelper.oreNameExists("crystalCertusQuartz") && ItemHelper.oreNameExists("crystalCertusQuartzCharged")) {
			addRecipe(3200, OreDictionary.getOres("crystalCertusQuartz").get(0), OreDictionary.getOres("crystalCertusQuartzCharged").get(0));
		}
	}

	public static void refreshRecipes() {

		Map<ComparableItemStackSafe, RecipeCharger> tempMap = new THashMap<ComparableItemStackSafe, RecipeCharger>(recipeMap.size());
		RecipeCharger tempRecipe;

		for (Entry<ComparableItemStackSafe, RecipeCharger> entry : recipeMap.entrySet()) {
			tempRecipe = entry.getValue();
			tempMap.put(new ComparableItemStackSafe(tempRecipe.input), tempRecipe);
		}
		recipeMap.clear();
		recipeMap = tempMap;
	}

	/* ADD RECIPES */
	public static boolean addTERecipe(int energy, ItemStack input, ItemStack output) {

		if (input == null || output == null || energy <= 0) {
			return false;
		}
		RecipeCharger recipe = new RecipeCharger(input, output, energy);
		recipeMap.put(new ComparableItemStackSafe(input), recipe);
		return true;
	}

	public static boolean addRecipe(int energy, ItemStack input, ItemStack output) {

		return addRecipe(energy, input, output, false);
	}

	public static boolean addRecipe(int energy, ItemStack input, ItemStack output, boolean overwrite) {

		if (input == null || output == null || energy <= 0 || !(allowOverwrite & overwrite) && recipeMap.get(query.set(input)) != null) {
			return false;
		}
		RecipeCharger recipe = new RecipeCharger(input, output, energy);
		recipeMap.put(new ComparableItemStackSafe(input), recipe);
		return true;
	}

	/* REMOVE RECIPES */
	public static boolean removeRecipe(ItemStack input) {

		return recipeMap.remove(new ComparableItemStackSafe(input)) != null;
	}

	/* HELPER FUNCTIONS */
	public static void addOreDictRecipe(String oreName, ItemStack output) {

		addOreDictRecipe(DEFAULT_ENERGY, oreName, output);
	}

	public static void addOreDictRecipe(int energy, String oreName, ItemStack output) {

		if (ItemHelper.oreNameExists(oreName)) {
			addRecipe(energy, ItemHelper.cloneStack(OreDictionaryArbiter.getOres(oreName).get(0), 1), output, false);
		}
	}

	/* RECIPE CLASS */
	public static class RecipeCharger {

		final ItemStack input;
		final ItemStack output;
		final int energy;

		RecipeCharger(ItemStack input, ItemStack output, int energy) {

			this.input = input;
			this.output = output;
			this.energy = energy;
		}

		public ItemStack getInput() {

			return input.copy();
		}

		public ItemStack getOutput() {

			return output.copy();
		}

		public int getEnergy() {

			return energy;
		}
	}

}
