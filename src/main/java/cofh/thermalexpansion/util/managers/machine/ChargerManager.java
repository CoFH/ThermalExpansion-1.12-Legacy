package cofh.thermalexpansion.util.managers.machine;

import cofh.core.inventory.ComparableItemStackSafe;
import cofh.core.util.helpers.ItemHelper;
import cofh.thermalfoundation.item.ItemFertilizer;
import gnu.trove.map.hash.THashMap;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import java.util.Map;
import java.util.Map.Entry;

public class ChargerManager {

	private static Map<ComparableItemStackSafe, ChargerRecipe> recipeMap = new THashMap<>();

	static final int DEFAULT_ENERGY = 3200;

	public static ChargerRecipe getRecipe(ItemStack input) {

		if (input.isEmpty()) {
			return null;
		}
		ComparableItemStackSafe query = new ComparableItemStackSafe(input);

		ChargerRecipe recipe = recipeMap.get(query);

		if (recipe == null) {
			query.metadata = OreDictionary.WILDCARD_VALUE;
			recipe = recipeMap.get(query);
		}
		return recipe;
	}

	public static boolean recipeExists(ItemStack input) {

		return getRecipe(input) != null;
	}

	public static ChargerRecipe[] getRecipeList() {

		return recipeMap.values().toArray(new ChargerRecipe[recipeMap.size()]);
	}

	public static void initialize() {

		addRecipe(DEFAULT_ENERGY, ItemFertilizer.fertilizerRich, ItemFertilizer.fertilizerFlux);

		/* LOAD RECIPES */
		loadRecipes();
	}

	public static void loadRecipes() {

		/* APPLIED ENERGISTICS 2 */
		if (ItemHelper.oreNameExists("crystalCertusQuartz") && ItemHelper.oreNameExists("crystalCertusQuartzCharged")) {
			addRecipe(DEFAULT_ENERGY, OreDictionary.getOres("crystalCertusQuartz", false).get(0), OreDictionary.getOres("crystalCertusQuartzCharged", false).get(0));
		}
		if (ItemHelper.oreNameExists("oreCertusQuartz") && ItemHelper.oreNameExists("oreCertusQuartzCharged")) {
			addRecipe(DEFAULT_ENERGY, OreDictionary.getOres("oreCertusQuartz", false).get(0), OreDictionary.getOres("oreCertusQuartzCharged", false).get(0));
		}
	}

	public static void refresh() {

		Map<ComparableItemStackSafe, ChargerRecipe> tempMap = new THashMap<>(recipeMap.size());
		ChargerRecipe tempRecipe;

		for (Entry<ComparableItemStackSafe, ChargerRecipe> entry : recipeMap.entrySet()) {
			tempRecipe = entry.getValue();
			tempMap.put(new ComparableItemStackSafe(tempRecipe.input), tempRecipe);
		}
		recipeMap.clear();
		recipeMap = tempMap;
	}

	/* ADD RECIPES */
	public static ChargerRecipe addRecipe(int energy, ItemStack input, ItemStack output) {

		if (input.isEmpty() || output.isEmpty() || energy <= 0 || recipeExists(input)) {
			return null;
		}
		ChargerRecipe recipe = new ChargerRecipe(input, output, energy);
		recipeMap.put(new ComparableItemStackSafe(input), recipe);
		return recipe;
	}

	/* REMOVE RECIPES */
	public static ChargerRecipe removeRecipe(ItemStack input) {

		return recipeMap.remove(new ComparableItemStackSafe(input));
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
	public static class ChargerRecipe {

		final ItemStack input;
		final ItemStack output;
		final int energy;

		public ChargerRecipe(ItemStack input, ItemStack output, int energy) {

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
