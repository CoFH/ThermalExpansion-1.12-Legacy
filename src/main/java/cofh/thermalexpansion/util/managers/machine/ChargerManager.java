package cofh.thermalexpansion.util.managers.machine;

import cofh.lib.inventory.ComparableItemStackSafe;
import cofh.lib.util.helpers.ItemHelper;
import cofh.thermalfoundation.item.ItemFertilizer;
import gnu.trove.map.hash.THashMap;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import java.util.Map;
import java.util.Map.Entry;

public class ChargerManager {

	private static Map<ComparableItemStackSafe, RecipeCharger> recipeMap = new THashMap<>();

	static final int DEFAULT_ENERGY = 3200;

	public static RecipeCharger getRecipe(ItemStack input) {

		if (input == null) {
			return null;
		}
		ComparableItemStackSafe query = new ComparableItemStackSafe(input);

		RecipeCharger recipe = recipeMap.get(query);

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

		return recipeMap.values().toArray(new RecipeCharger[recipeMap.size()]);
	}

	public static void addDefaultRecipes() {

		addRecipe(DEFAULT_ENERGY, ItemFertilizer.fertilizerRich, ItemFertilizer.fertilizerFlux);
	}

	public static void loadRecipes() {

		/* APPLIED ENERGISTICS 2 */
		if (ItemHelper.oreNameExists("crystalCertusQuartz") && ItemHelper.oreNameExists("crystalCertusQuartzCharged")) {
			addRecipe(DEFAULT_ENERGY, OreDictionary.getOres("crystalCertusQuartz", false).get(0), OreDictionary.getOres("crystalCertusQuartzCharged", false).get(0));
		}
	}

	public static void refreshRecipes() {

		Map<ComparableItemStackSafe, RecipeCharger> tempMap = new THashMap<>(recipeMap.size());
		RecipeCharger tempRecipe;

		for (Entry<ComparableItemStackSafe, RecipeCharger> entry : recipeMap.entrySet()) {
			tempRecipe = entry.getValue();
			tempMap.put(new ComparableItemStackSafe(tempRecipe.input), tempRecipe);
		}
		recipeMap.clear();
		recipeMap = tempMap;
	}

	/* ADD RECIPES */
	public static boolean addRecipe(int energy, ItemStack input, ItemStack output) {

		if (input == null || output == null || energy <= 0 || recipeExists(input)) {
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

	/* HELPERS */
	private static void addOreDictRecipe(String oreName, ItemStack output) {

		addOreDictRecipe(DEFAULT_ENERGY, oreName, output);
	}

	private static void addOreDictRecipe(int energy, String oreName, ItemStack output) {

		if (ItemHelper.oreNameExists(oreName) && !recipeExists(OreDictionary.getOres(oreName, false).get(0))) {
			addRecipe(energy, ItemHelper.cloneStack(OreDictionary.getOres(oreName, false).get(0), 1), output);
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

			return input;
		}

		public ItemStack getOutput() {

			return output;
		}

		public int getEnergy() {

			return energy;
		}
	}

}
