package cofh.thermalexpansion.util.managers.machine;

import cofh.core.inventory.ComparableItemStackValidated;
import cofh.core.util.helpers.ItemHelper;
import cofh.core.util.helpers.StringHelper;
import gnu.trove.map.hash.THashMap;
import gnu.trove.set.hash.THashSet;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import java.util.Map;
import java.util.Set;

public class CompactorManager {

	private static Map<ComparableItemStackValidated, CompactorRecipe> recipeMapAll = new THashMap<>();
	private static Map<ComparableItemStackValidated, CompactorRecipe> recipeMapPlate = new THashMap<>();
	private static Map<ComparableItemStackValidated, CompactorRecipe> recipeMapCoin = new THashMap<>();
	private static Map<ComparableItemStackValidated, CompactorRecipe> recipeMapGear = new THashMap<>();
	private static Set<ComparableItemStackValidated> validationSet = new THashSet<>();

	public static final int DEFAULT_ENERGY = 4000;

	public static CompactorRecipe getRecipe(ItemStack input, Mode mode) {

		if (input.isEmpty()) {
			return null;
		}
		ComparableItemStackValidated query = new ComparableItemStackValidated(input);
		CompactorRecipe recipe = null;

		switch (mode) {
			case PLATE:
				recipe = recipeMapPlate.get(query);
				if (recipe == null) {
					query.metadata = OreDictionary.WILDCARD_VALUE;
					recipe = recipeMapPlate.get(query);
				}
				break;
			case COIN:
				recipe = recipeMapCoin.get(query);
				if (recipe == null) {
					query.metadata = OreDictionary.WILDCARD_VALUE;
					recipe = recipeMapCoin.get(query);
				}
				break;
			case GEAR:
				recipe = recipeMapGear.get(query);
				if (recipe == null) {
					query.metadata = OreDictionary.WILDCARD_VALUE;
					recipe = recipeMapGear.get(query);
				}
				break;
		}
		if (recipe == null) {
			return recipeMapAll.get(query);
		}
		return recipe;
	}

	public static boolean recipeExists(ItemStack input, Mode mode) {

		return getRecipe(input, mode) != null;
	}

	public static CompactorRecipe[] getRecipeList(Mode mode) {

		switch (mode) {
			case PLATE:
				return recipeMapPlate.values().toArray(new CompactorRecipe[0]);
			case COIN:
				return recipeMapCoin.values().toArray(new CompactorRecipe[0]);
			case GEAR:
				return recipeMapGear.values().toArray(new CompactorRecipe[0]);
			default:
				return recipeMapAll.values().toArray(new CompactorRecipe[0]);
		}
	}

	public static boolean isItemValid(ItemStack input) {

		return !input.isEmpty() && validationSet.contains(new ComparableItemStackValidated(input));
	}

	public static void initialize() {

		/* GENERAL SCAN */
		String[] oreNames = OreDictionary.getOreNames();
		String oreType;

		for (String oreName : oreNames) {
			if (oreName.startsWith("plate")) {
				oreType = oreName.substring(5, oreName.length());
				addDefaultPlateRecipe(oreType);
			} else if (oreName.startsWith("gear")) {
				oreType = oreName.substring(4, oreName.length());
				addDefaultGearRecipe(oreType);
			} else if (oreName.startsWith("coin")) {
				oreType = oreName.substring(4, oreName.length());
				addDefaultCoinRecipe(oreType);
			}
		}
	}

	public static void refresh() {

		Map<ComparableItemStackValidated, CompactorRecipe> tempAll = new THashMap<>(recipeMapAll.size());
		Map<ComparableItemStackValidated, CompactorRecipe> tempPlate = new THashMap<>(recipeMapPlate.size());
		Map<ComparableItemStackValidated, CompactorRecipe> tempMint = new THashMap<>(recipeMapCoin.size());
		Map<ComparableItemStackValidated, CompactorRecipe> tempGear = new THashMap<>(recipeMapGear.size());
		Set<ComparableItemStackValidated> tempSet = new THashSet<>();
		CompactorRecipe tempRecipe;

		for (Map.Entry<ComparableItemStackValidated, CompactorRecipe> entry : recipeMapAll.entrySet()) {
			tempRecipe = entry.getValue();
			ComparableItemStackValidated input = new ComparableItemStackValidated(tempRecipe.input);
			tempPlate.put(input, tempRecipe);
			tempSet.add(input);
		}
		for (Map.Entry<ComparableItemStackValidated, CompactorRecipe> entry : recipeMapPlate.entrySet()) {
			tempRecipe = entry.getValue();
			ComparableItemStackValidated input = new ComparableItemStackValidated(tempRecipe.input);
			tempPlate.put(input, tempRecipe);
			tempSet.add(input);
		}
		for (Map.Entry<ComparableItemStackValidated, CompactorRecipe> entry : recipeMapCoin.entrySet()) {
			tempRecipe = entry.getValue();
			ComparableItemStackValidated input = new ComparableItemStackValidated(tempRecipe.input);
			tempMint.put(input, tempRecipe);
			tempSet.add(input);
		}
		for (Map.Entry<ComparableItemStackValidated, CompactorRecipe> entry : recipeMapGear.entrySet()) {
			tempRecipe = entry.getValue();
			ComparableItemStackValidated input = new ComparableItemStackValidated(tempRecipe.input);
			tempGear.put(input, tempRecipe);
			tempSet.add(input);
		}
		recipeMapAll.clear();
		recipeMapPlate.clear();
		recipeMapCoin.clear();
		recipeMapGear.clear();

		recipeMapAll = tempAll;
		recipeMapPlate = tempPlate;
		recipeMapCoin = tempMint;
		recipeMapGear = tempGear;

		validationSet.clear();
		validationSet = tempSet;
	}

	/* ADD RECIPES */
	public static CompactorRecipe addRecipe(int energy, ItemStack input, ItemStack output, Mode mode) {

		if (input.isEmpty() || output.isEmpty() || energy <= 0 || recipeExists(input, mode)) {
			return null;
		}
		CompactorRecipe recipe = new CompactorRecipe(input, output, energy);

		switch (mode) {
			case ALL:
				recipeMapAll.put(new ComparableItemStackValidated(input), recipe);
				break;
			case PLATE:
				recipeMapPlate.put(new ComparableItemStackValidated(input), recipe);
				break;
			case COIN:
				recipeMapCoin.put(new ComparableItemStackValidated(input), recipe);
				break;
			case GEAR:
				recipeMapGear.put(new ComparableItemStackValidated(input), recipe);
				break;
		}
		validationSet.add(new ComparableItemStackValidated(input));
		return recipe;
	}

	/* REMOVE RECIPES */
	public static CompactorRecipe removeRecipe(ItemStack input, Mode mode) {

		switch (mode) {
			case ALL:
				return recipeMapAll.remove(new ComparableItemStackValidated(input));
			case PLATE:
				return recipeMapPlate.remove(new ComparableItemStackValidated(input));
			case COIN:
				return recipeMapCoin.remove(new ComparableItemStackValidated(input));
			default:
				return recipeMapGear.remove(new ComparableItemStackValidated(input));
		}
	}

	/* HELPERS */
	public static ComparableItemStackValidated convertInput(ItemStack stack) {

		return new ComparableItemStackValidated(stack);
	}

	private static void addDefaultPlateRecipe(String oreType) {

		if (oreType == null || oreType.isEmpty()) {
			return;
		}
		String plateName = "plate" + StringHelper.titleCase(oreType);
		if (!ItemHelper.oreNameExists(plateName)) {
			return;
		}
		addDefaultPlateRecipe(oreType, ItemHelper.getOre(plateName));
	}

	private static void addDefaultPlateRecipe(String oreType, ItemStack plate) {

		if (oreType == null || oreType.isEmpty()) {
			return;
		}
		addRecipe(DEFAULT_ENERGY, ItemHelper.getOre("ingot" + StringHelper.titleCase(oreType)), ItemHelper.cloneStack(plate, 1), Mode.PLATE);
		addRecipe(DEFAULT_ENERGY, ItemHelper.getOre("gem" + StringHelper.titleCase(oreType)), ItemHelper.cloneStack(plate, 1), Mode.PLATE);
	}

	private static void addDefaultGearRecipe(String oreType) {

		if (oreType == null || oreType.isEmpty()) {
			return;
		}
		String gearName = "gear" + StringHelper.titleCase(oreType);
		if (!ItemHelper.oreNameExists(gearName)) {
			return;
		}
		addDefaultGearRecipe(oreType, ItemHelper.getOre(gearName));
	}

	private static void addDefaultGearRecipe(String oreType, ItemStack gear) {

		if (oreType == null || oreType.isEmpty()) {
			return;
		}
		addRecipe(DEFAULT_ENERGY, ItemHelper.getOre("ingot" + StringHelper.titleCase(oreType), 4), ItemHelper.cloneStack(gear, 1), Mode.GEAR);
		addRecipe(DEFAULT_ENERGY, ItemHelper.getOre("gem" + StringHelper.titleCase(oreType), 4), ItemHelper.cloneStack(gear, 1), Mode.GEAR);
	}

	private static void addDefaultCoinRecipe(String oreType) {

		if (oreType == null || oreType.isEmpty()) {
			return;
		}
		String coinName = "coin" + StringHelper.titleCase(oreType);
		if (!ItemHelper.oreNameExists(coinName)) {
			return;
		}
		addDefaultCoinRecipe(oreType, ItemHelper.getOre(coinName));
	}

	private static void addDefaultCoinRecipe(String oreType, ItemStack coin) {

		if (oreType == null || oreType.isEmpty()) {
			return;
		}
		addRecipe(DEFAULT_ENERGY, ItemHelper.getOre("ingot" + StringHelper.titleCase(oreType)), ItemHelper.cloneStack(coin, 3), Mode.COIN);
	}

	/* RECIPE CLASS */
	public static class CompactorRecipe {

		final ItemStack input;
		final ItemStack output;
		final int energy;

		CompactorRecipe(ItemStack input, ItemStack output, int energy) {

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

	/* MODE ENUM */
	public enum Mode {
		ALL, PLATE, COIN, GEAR
	}

}
