package cofh.thermalexpansion.util.managers.machine;

import cofh.core.inventory.ComparableItemStackValidatedNBT;
import cofh.core.util.helpers.ItemHelper;
import cofh.core.util.helpers.StringHelper;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import java.util.Map;
import java.util.Set;

public class CompactorManager {

	private static Map<ComparableItemStackValidatedNBT, CompactorRecipe> recipeMapAll = new Object2ObjectOpenHashMap<>();
	private static Map<ComparableItemStackValidatedNBT, CompactorRecipe> recipeMapPlate = new Object2ObjectOpenHashMap<>();
	private static Map<ComparableItemStackValidatedNBT, CompactorRecipe> recipeMapCoin = new Object2ObjectOpenHashMap<>();
	private static Map<ComparableItemStackValidatedNBT, CompactorRecipe> recipeMapGear = new Object2ObjectOpenHashMap<>();
	private static Set<ComparableItemStackValidatedNBT> validationSet = new ObjectOpenHashSet<>();

	public static final int DEFAULT_ENERGY = 4000;

	public static CompactorRecipe getRecipe(ItemStack input, Mode mode) {

		if (input.isEmpty()) {
			return null;
		}
		ComparableItemStackValidatedNBT query = new ComparableItemStackValidatedNBT(input);
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

		return !input.isEmpty() && validationSet.contains(new ComparableItemStackValidatedNBT(input));
	}

	public static void initialize() {

		/* GENERAL SCAN */
		String[] oreNames = OreDictionary.getOreNames();
		String oreType;

		for (String oreName : oreNames) {
			if (oreName.startsWith("plate")) {
				oreType = oreName.substring(5);
				addDefaultPlateRecipe(oreType);
			} else if (oreName.startsWith("gear")) {
				oreType = oreName.substring(4);
				addDefaultGearRecipe(oreType);
			} else if (oreName.startsWith("coin")) {
				oreType = oreName.substring(4);
				addDefaultCoinRecipe(oreType);
			}
		}
	}

	public static void refresh() {

		Map<ComparableItemStackValidatedNBT, CompactorRecipe> tempAll = new Object2ObjectOpenHashMap<>(recipeMapAll.size());
		Map<ComparableItemStackValidatedNBT, CompactorRecipe> tempPlate = new Object2ObjectOpenHashMap<>(recipeMapPlate.size());
		Map<ComparableItemStackValidatedNBT, CompactorRecipe> tempMint = new Object2ObjectOpenHashMap<>(recipeMapCoin.size());
		Map<ComparableItemStackValidatedNBT, CompactorRecipe> tempGear = new Object2ObjectOpenHashMap<>(recipeMapGear.size());
		Set<ComparableItemStackValidatedNBT> tempSet = new ObjectOpenHashSet<>();
		CompactorRecipe tempRecipe;

		for (Map.Entry<ComparableItemStackValidatedNBT, CompactorRecipe> entry : recipeMapAll.entrySet()) {
			tempRecipe = entry.getValue();
			ComparableItemStackValidatedNBT input = new ComparableItemStackValidatedNBT(tempRecipe.input);
			tempPlate.put(input, tempRecipe);
			tempSet.add(input);
		}
		for (Map.Entry<ComparableItemStackValidatedNBT, CompactorRecipe> entry : recipeMapPlate.entrySet()) {
			tempRecipe = entry.getValue();
			ComparableItemStackValidatedNBT input = new ComparableItemStackValidatedNBT(tempRecipe.input);
			tempPlate.put(input, tempRecipe);
			tempSet.add(input);
		}
		for (Map.Entry<ComparableItemStackValidatedNBT, CompactorRecipe> entry : recipeMapCoin.entrySet()) {
			tempRecipe = entry.getValue();
			ComparableItemStackValidatedNBT input = new ComparableItemStackValidatedNBT(tempRecipe.input);
			tempMint.put(input, tempRecipe);
			tempSet.add(input);
		}
		for (Map.Entry<ComparableItemStackValidatedNBT, CompactorRecipe> entry : recipeMapGear.entrySet()) {
			tempRecipe = entry.getValue();
			ComparableItemStackValidatedNBT input = new ComparableItemStackValidatedNBT(tempRecipe.input);
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
				recipeMapAll.put(new ComparableItemStackValidatedNBT(input), recipe);
				break;
			case PLATE:
				recipeMapPlate.put(new ComparableItemStackValidatedNBT(input), recipe);
				break;
			case COIN:
				recipeMapCoin.put(new ComparableItemStackValidatedNBT(input), recipe);
				break;
			case GEAR:
				recipeMapGear.put(new ComparableItemStackValidatedNBT(input), recipe);
				break;
		}
		validationSet.add(new ComparableItemStackValidatedNBT(input));
		return recipe;
	}

	/* REMOVE RECIPES */
	public static CompactorRecipe removeRecipe(ItemStack input, Mode mode) {

		switch (mode) {
			case ALL:
				return recipeMapAll.remove(new ComparableItemStackValidatedNBT(input));
			case PLATE:
				return recipeMapPlate.remove(new ComparableItemStackValidatedNBT(input));
			case COIN:
				return recipeMapCoin.remove(new ComparableItemStackValidatedNBT(input));
			default:
				return recipeMapGear.remove(new ComparableItemStackValidatedNBT(input));
		}
	}

	/* HELPERS */
	public static ComparableItemStackValidatedNBT convertInput(ItemStack stack) {

		return new ComparableItemStackValidatedNBT(stack);
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
