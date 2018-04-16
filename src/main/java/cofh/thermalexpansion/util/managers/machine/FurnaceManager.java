package cofh.thermalexpansion.util.managers.machine;

import cofh.core.inventory.ComparableItemStack;
import cofh.core.inventory.ComparableItemStackValidated;
import cofh.core.inventory.OreValidator;
import cofh.core.util.helpers.ItemHelper;
import cofh.thermalexpansion.util.parsers.ConstantParser;
import gnu.trove.map.hash.THashMap;
import gnu.trove.set.hash.THashSet;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraftforge.oredict.OreDictionary;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class FurnaceManager {

	private static Map<ComparableItemStackValidated, FurnaceRecipe> recipeMap = new THashMap<>();
	private static Map<ComparableItemStackValidated, FurnaceRecipe> recipeMapPyrolysis = new THashMap<>();
	private static Set<ComparableItemStackValidated> foodSet = new THashSet<>();
	private static OreValidator oreValidator = new OreValidator();

	static {
		oreValidator.addPrefix(ComparableItemStack.ORE);
		oreValidator.addPrefix(ComparableItemStack.DUST);
		oreValidator.addPrefix("log");
	}

	public static final int DEFAULT_ENERGY = 2000;

	public static FurnaceRecipe getRecipe(ItemStack input) {

		if (input.isEmpty()) {
			return null;
		}
		ComparableItemStackValidated query = convertInput(input);

		FurnaceRecipe recipe = recipeMap.get(query);

		if (recipe == null) {
			query.metadata = OreDictionary.WILDCARD_VALUE;
			recipe = recipeMap.get(query);
		}
		return recipe;
	}

	public static FurnaceRecipe getRecipePyrolysis(ItemStack input) {

		if (input.isEmpty()) {
			return null;
		}
		ComparableItemStackValidated query = convertInput(input);

		FurnaceRecipe recipe = recipeMapPyrolysis.get(query);

		if (recipe == null) {
			query.metadata = OreDictionary.WILDCARD_VALUE;
			recipe = recipeMapPyrolysis.get(query);
		}
		return recipe;
	}

	public static boolean recipeExists(ItemStack input) {

		return getRecipe(input) != null;
	}

	public static boolean recipeExistsPyrolysis(ItemStack input) {

		return getRecipePyrolysis(input) != null;
	}

	public static FurnaceRecipe[] getRecipeList() {

		return recipeMap.values().toArray(new FurnaceRecipe[recipeMap.size()]);
	}

	public static FurnaceRecipe[] getRecipeListPyrolysis() {

		return recipeMapPyrolysis.values().toArray(new FurnaceRecipe[recipeMapPyrolysis.size()]);
	}

	public static boolean isFood(ItemStack input) {

		if (input.isEmpty()) {
			return false;
		}
		ComparableItemStackValidated query = convertInput(input);

		if (foodSet.contains(query)) {
			return true;
		}
		query.metadata = OreDictionary.WILDCARD_VALUE;
		return foodSet.contains(query);
	}

	public static void initialize() {

		Map<ItemStack, ItemStack> smeltingList = FurnaceRecipes.instance().getSmeltingList();
		ItemStack output;

		for (ItemStack key : smeltingList.keySet()) {
			if (key.isEmpty() || recipeExists(key)) {
				continue;
			}
			output = smeltingList.get(key);

			if (ConstantParser.hasOre(ItemHelper.getOreName(output))) {
				output = ItemHelper.cloneStack(ConstantParser.getOre(ItemHelper.getOreName(output)), output.getCount());
			}
			int energy = DEFAULT_ENERGY;

			/* FOOD */
			if (output.getItem() instanceof ItemFood) {
				foodSet.add(convertInput(key));
				energy /= 2;
			}
			/* DUST */
			if (ItemHelper.isDust(key) && ItemHelper.isIngot(output)) {
				addRecipe(energy * 3 / 4, key, output);
				/* STANDARD */
			} else {
				if (ItemHelper.getItemDamage(key) == OreDictionary.WILDCARD_VALUE) {
					ItemStack testKey = ItemHelper.cloneStack(key);
					testKey.setItemDamage(0);

					if (ItemHelper.hasOreName(testKey) && oreValidator.validate(ItemHelper.getOreName(testKey))) {
						addRecipe(energy, testKey, output);
						continue;
					}
				}
				addRecipe(energy, key, output);
			}
		}
	}

	public static void refresh() {

		Map<ComparableItemStackValidated, FurnaceRecipe> tempMap = new THashMap<>(recipeMap.size());
		Map<ComparableItemStackValidated, FurnaceRecipe> tempMapPyrolysis = new THashMap<>(recipeMapPyrolysis.size());
		Set<ComparableItemStackValidated> tempFood = new THashSet<>();
		FurnaceRecipe tempRecipe;

		for (Entry<ComparableItemStackValidated, FurnaceRecipe> entry : recipeMap.entrySet()) {
			tempRecipe = entry.getValue();
			tempMap.put(convertInput(tempRecipe.input), tempRecipe);
		}
		for (Entry<ComparableItemStackValidated, FurnaceRecipe> entry : recipeMapPyrolysis.entrySet()) {
			tempRecipe = entry.getValue();
			tempMapPyrolysis.put(convertInput(tempRecipe.input), tempRecipe);
		}
		for (ComparableItemStackValidated entry : foodSet) {
			ComparableItemStackValidated food = convertInput(new ItemStack(entry.item, entry.stackSize, entry.metadata));
			tempFood.add(food);
		}
		recipeMap.clear();
		recipeMap = tempMap;

		recipeMapPyrolysis.clear();
		recipeMapPyrolysis = tempMapPyrolysis;

		foodSet.clear();
		foodSet = tempFood;
	}

	/* ADD RECIPES */
	public static FurnaceRecipe addRecipe(int energy, ItemStack input, ItemStack output) {

		if (input.isEmpty() || output.isEmpty() || energy <= 0 || recipeExists(input)) {
			return null;
		}
		FurnaceRecipe recipe = new FurnaceRecipe(input, output, energy);
		recipeMap.put(convertInput(input), recipe);
		return recipe;
	}

	public static FurnaceRecipe addRecipePyrolysis(int energy, ItemStack input, ItemStack output, int creosote) {

		if (input.isEmpty() || output.isEmpty() || energy <= 0 || recipeExistsPyrolysis(input)) {
			return null;
		}
		FurnaceRecipe recipe = new FurnaceRecipe(input, output, energy, creosote);
		recipeMapPyrolysis.put(convertInput(input), recipe);
		return recipe;
	}

	/* REMOVE RECIPES */
	public static FurnaceRecipe removeRecipe(ItemStack input) {

		return recipeMap.remove(convertInput(input));
	}

	public static FurnaceRecipe removeRecipePyrolysis(ItemStack input) {

		return recipeMapPyrolysis.remove(convertInput(input));
	}

	/* HELPERS */
	public static ComparableItemStackValidated convertInput(ItemStack stack) {

		return new ComparableItemStackValidated(stack, oreValidator);
	}

	private static void addOreDictRecipe(int energy, String oreName, ItemStack output) {

		if (ItemHelper.oreNameExists(oreName) && !recipeExists(OreDictionary.getOres(oreName, false).get(0))) {
			addRecipe(energy, ItemHelper.cloneStack(OreDictionary.getOres(oreName, false).get(0), 1), output);
		}
	}

	/* RECIPE CLASS */
	public static class FurnaceRecipe {

		final ItemStack input;
		final ItemStack output;
		final int energy;
		final int creosote;

		FurnaceRecipe(ItemStack input, ItemStack output, int energy) {

			this(input, output, energy, 0);
		}

		FurnaceRecipe(ItemStack input, ItemStack output, int energy, int creosote) {

			this.input = input;
			this.output = output;
			this.energy = energy;
			this.creosote = creosote;
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

		public int getCreosote() {

			return creosote;
		}
	}

}
