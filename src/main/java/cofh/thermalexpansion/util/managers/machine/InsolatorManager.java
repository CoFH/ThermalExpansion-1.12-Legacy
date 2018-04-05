package cofh.thermalexpansion.util.managers.machine;

import cofh.core.inventory.ComparableItemStackSafe;
import cofh.core.util.helpers.ItemHelper;
import cofh.core.util.helpers.StringHelper;
import cofh.thermalfoundation.item.ItemFertilizer;
import gnu.trove.map.hash.THashMap;
import gnu.trove.set.hash.THashSet;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import static java.util.Arrays.asList;

public class InsolatorManager {

	private static Map<List<ComparableItemStackInsolator>, InsolatorRecipe> recipeMap = new THashMap<>();
	private static Set<ComparableItemStackInsolator> validationSet = new THashSet<>();
	private static Set<ComparableItemStackInsolator> lockSet = new THashSet<>();

	public static final int CROP_MULTIPLIER_RICH = 3;
	public static final int CROP_MULTIPLIER_FLUX = 4;

	public static final int FLUID_FACTOR = 4;

	public static final int DEFAULT_ENERGY = 2400;
	public static final int DEFAULT_FLUID = DEFAULT_ENERGY / FLUID_FACTOR;

	public static boolean isRecipeReversed(ItemStack primaryInput, ItemStack secondaryInput) {

		if (primaryInput.isEmpty() || secondaryInput.isEmpty()) {
			return false;
		}
		ComparableItemStackInsolator query = new ComparableItemStackInsolator(primaryInput);
		ComparableItemStackInsolator querySecondary = new ComparableItemStackInsolator(secondaryInput);

		InsolatorRecipe recipe = recipeMap.get(asList(query, querySecondary));
		return recipe == null && recipeMap.get(asList(querySecondary, query)) != null;
	}

	public static InsolatorRecipe getRecipe(ItemStack primaryInput, ItemStack secondaryInput) {

		if (primaryInput.isEmpty() || secondaryInput.isEmpty()) {
			return null;
		}
		ComparableItemStackInsolator query = new ComparableItemStackInsolator(primaryInput);
		ComparableItemStackInsolator querySecondary = new ComparableItemStackInsolator(secondaryInput);

		InsolatorRecipe recipe = recipeMap.get(asList(query, querySecondary));

		if (recipe == null) {
			recipe = recipeMap.get(asList(querySecondary, query));
		}
		if (recipe == null) {
			return null;
		}
		return recipe;
	}

	public static boolean recipeExists(ItemStack primaryInput, ItemStack secondaryInput) {

		return getRecipe(primaryInput, secondaryInput) != null;
	}

	public static InsolatorRecipe[] getRecipeList() {

		return recipeMap.values().toArray(new InsolatorRecipe[recipeMap.size()]);
	}

	public static boolean isItemValid(ItemStack input) {

		return !input.isEmpty() && validationSet.contains(new ComparableItemStackInsolator(input));
	}

	public static boolean isItemFertilizer(ItemStack input) {

		return !input.isEmpty() && lockSet.contains(new ComparableItemStackInsolator(input));
	}

	public static void initialize() {

		/* FERTILIZER */
		{
			addFertilizer(ItemFertilizer.fertilizerBasic);
			addFertilizer(ItemFertilizer.fertilizerRich);
			addFertilizer(ItemFertilizer.fertilizerFlux);
		}

		/* CROPS */
		{
			addDefaultRecipe(new ItemStack(Items.WHEAT_SEEDS), new ItemStack(Items.WHEAT, 2), new ItemStack(Items.WHEAT_SEEDS), 110);
			addDefaultRecipe(new ItemStack(Items.BEETROOT_SEEDS), new ItemStack(Items.BEETROOT, 2), new ItemStack(Items.BEETROOT_SEEDS), 110);
			addDefaultRecipe(new ItemStack(Items.POTATO), new ItemStack(Items.POTATO, 3), new ItemStack(Items.POISONOUS_POTATO), 5);
			addDefaultRecipe(new ItemStack(Items.CARROT), new ItemStack(Items.CARROT, 3), ItemStack.EMPTY, 0);
			addDefaultRecipe(new ItemStack(Items.DYE, 1, 3), new ItemStack(Items.DYE, 3, 3), ItemStack.EMPTY, 0);
			addDefaultRecipe(new ItemStack(Items.REEDS), new ItemStack(Items.REEDS, 2), ItemStack.EMPTY, 0);
			addDefaultRecipe(new ItemStack(Blocks.CACTUS), new ItemStack(Blocks.CACTUS, 2), ItemStack.EMPTY, 0);
			addDefaultRecipe(new ItemStack(Blocks.VINE), new ItemStack(Blocks.VINE, 2), ItemStack.EMPTY, 0);
			addDefaultRecipe(new ItemStack(Blocks.WATERLILY), new ItemStack(Blocks.WATERLILY, 2), ItemStack.EMPTY, 0);
			addDefaultRecipe(new ItemStack(Items.PUMPKIN_SEEDS), new ItemStack(Blocks.PUMPKIN), new ItemStack(Items.PUMPKIN_SEEDS), 100);
			addDefaultRecipe(new ItemStack(Items.MELON_SEEDS), new ItemStack(Blocks.MELON_BLOCK), new ItemStack(Items.MELON_SEEDS), 100);
		}

		/* FLOWERS */
		{
			addDefaultRecipe(new ItemStack(Blocks.YELLOW_FLOWER, 1, 0), new ItemStack(Blocks.YELLOW_FLOWER, 3, 0), ItemStack.EMPTY, 0);
			addDefaultRecipe(new ItemStack(Blocks.RED_FLOWER, 1, 0), new ItemStack(Blocks.RED_FLOWER, 3, 0), ItemStack.EMPTY, 0);
			addDefaultRecipe(new ItemStack(Blocks.RED_FLOWER, 1, 1), new ItemStack(Blocks.RED_FLOWER, 3, 1), ItemStack.EMPTY, 0);
			addDefaultRecipe(new ItemStack(Blocks.RED_FLOWER, 1, 2), new ItemStack(Blocks.RED_FLOWER, 3, 2), ItemStack.EMPTY, 0);
			addDefaultRecipe(new ItemStack(Blocks.RED_FLOWER, 1, 3), new ItemStack(Blocks.RED_FLOWER, 3, 3), ItemStack.EMPTY, 0);
			addDefaultRecipe(new ItemStack(Blocks.RED_FLOWER, 1, 4), new ItemStack(Blocks.RED_FLOWER, 3, 4), ItemStack.EMPTY, 0);
			addDefaultRecipe(new ItemStack(Blocks.RED_FLOWER, 1, 5), new ItemStack(Blocks.RED_FLOWER, 3, 5), ItemStack.EMPTY, 0);
			addDefaultRecipe(new ItemStack(Blocks.RED_FLOWER, 1, 6), new ItemStack(Blocks.RED_FLOWER, 3, 6), ItemStack.EMPTY, 0);
			addDefaultRecipe(new ItemStack(Blocks.RED_FLOWER, 1, 7), new ItemStack(Blocks.RED_FLOWER, 3, 7), ItemStack.EMPTY, 0);
			addDefaultRecipe(new ItemStack(Blocks.RED_FLOWER, 1, 8), new ItemStack(Blocks.RED_FLOWER, 3, 8), ItemStack.EMPTY, 0);

			addDefaultRecipe(new ItemStack(Blocks.DOUBLE_PLANT, 1, 0), new ItemStack(Blocks.DOUBLE_PLANT, 2, 0), ItemStack.EMPTY, 0);
			addDefaultRecipe(new ItemStack(Blocks.DOUBLE_PLANT, 1, 1), new ItemStack(Blocks.DOUBLE_PLANT, 2, 1), ItemStack.EMPTY, 0);
			addDefaultRecipe(new ItemStack(Blocks.DOUBLE_PLANT, 1, 4), new ItemStack(Blocks.DOUBLE_PLANT, 2, 4), ItemStack.EMPTY, 0);
			addDefaultRecipe(new ItemStack(Blocks.DOUBLE_PLANT, 1, 5), new ItemStack(Blocks.DOUBLE_PLANT, 2, 5), ItemStack.EMPTY, 0);
		}

		/* MYCELIUM */
		{
			ArrayList<ItemStack> crops = new ArrayList<>();
			crops.add(new ItemStack(Blocks.BROWN_MUSHROOM));
			crops.add(new ItemStack(Blocks.RED_MUSHROOM));

			for (ItemStack input : crops) {
				addDefaultRecipe(input, ItemHelper.cloneStack(input, 2), ItemStack.EMPTY, 0);
			}
		}

		/* NETHER */
		{
			ArrayList<ItemStack> crops = new ArrayList<>();
			crops.add(new ItemStack(Items.NETHER_WART));

			for (ItemStack input : crops) {
				addDefaultRecipe(input, ItemHelper.cloneStack(input, 2), ItemStack.EMPTY, 0);
			}
		}

		/* END */
		{
			ItemStack input = new ItemStack(Blocks.CHORUS_FLOWER);
			ItemStack output = new ItemStack(Items.CHORUS_FRUIT);

			addDefaultRecipe(input, ItemHelper.cloneStack(output, 2), input, 100);
		}

		/* TREE */
		{
			addDefaultTreeRecipe(new ItemStack(Blocks.SAPLING, 1, 0), new ItemStack(Blocks.LOG, 6, 0), new ItemStack(Blocks.SAPLING, 1, 0));
			addDefaultTreeRecipe(new ItemStack(Blocks.SAPLING, 1, 1), new ItemStack(Blocks.LOG, 6, 1), new ItemStack(Blocks.SAPLING, 1, 1));
			addDefaultTreeRecipe(new ItemStack(Blocks.SAPLING, 1, 2), new ItemStack(Blocks.LOG, 6, 2), new ItemStack(Blocks.SAPLING, 1, 2));
			addDefaultTreeRecipe(new ItemStack(Blocks.SAPLING, 1, 3), new ItemStack(Blocks.LOG, 6, 3), new ItemStack(Blocks.SAPLING, 1, 3));
			addDefaultTreeRecipe(new ItemStack(Blocks.SAPLING, 1, 4), new ItemStack(Blocks.LOG2, 6, 0), new ItemStack(Blocks.SAPLING, 1, 4));
			addDefaultTreeRecipe(new ItemStack(Blocks.SAPLING, 1, 5), new ItemStack(Blocks.LOG2, 6, 1), new ItemStack(Blocks.SAPLING, 1, 5));
		}

		/* LOAD RECIPES */
		loadRecipes();
	}

	public static void loadRecipes() {

		String[] oreNameList = OreDictionary.getOreNames();
		String oreName;

		for (String name : oreNameList) {
			if (name.startsWith("seed")) {
				oreName = name.substring(4, name.length());
				addDefaultOreDictionaryRecipe(oreName);
			}
		}
	}

	public static void refresh() {

		Map<List<ComparableItemStackInsolator>, InsolatorRecipe> tempMap = new THashMap<>(recipeMap.size());
		Set<ComparableItemStackInsolator> tempSet = new THashSet<>();
		InsolatorRecipe tempRecipe;

		for (Entry<List<ComparableItemStackInsolator>, InsolatorRecipe> entry : recipeMap.entrySet()) {
			tempRecipe = entry.getValue();
			ComparableItemStackInsolator primary = new ComparableItemStackInsolator(tempRecipe.primaryInput);
			ComparableItemStackInsolator secondary = new ComparableItemStackInsolator(tempRecipe.secondaryInput);

			tempMap.put(asList(primary, secondary), tempRecipe);
			tempSet.add(primary);
			tempSet.add(secondary);
		}
		recipeMap.clear();
		recipeMap = tempMap;

		validationSet.clear();
		validationSet = tempSet;

		Set<ComparableItemStackInsolator> tempSet2 = new THashSet<>();
		for (ComparableItemStackInsolator entry : lockSet) {
			ComparableItemStackInsolator lock = new ComparableItemStackInsolator(new ItemStack(entry.item, entry.stackSize, entry.metadata));
			tempSet2.add(lock);
		}
		lockSet.clear();
		lockSet = tempSet2;
	}

	/* ADD RECIPES */
	public static InsolatorRecipe addRecipe(int energy, int water, ItemStack primaryInput, ItemStack secondaryInput, ItemStack primaryOutput, ItemStack secondaryOutput, int secondaryChance, Type type) {

		if (primaryInput.isEmpty() || secondaryInput.isEmpty() || energy <= 0 || water <= 0 || recipeExists(primaryInput, secondaryInput)) {
			return null;
		}
		InsolatorRecipe recipe = new InsolatorRecipe(primaryInput, secondaryInput, primaryOutput, secondaryOutput, secondaryOutput.isEmpty() ? 0 : secondaryChance, energy, water, type);
		recipeMap.put(asList(new ComparableItemStackInsolator(primaryInput), new ComparableItemStackInsolator(secondaryInput)), recipe);
		validationSet.add(new ComparableItemStackInsolator(primaryInput));
		validationSet.add(new ComparableItemStackInsolator(secondaryInput));
		return recipe;
	}

	public static InsolatorRecipe addRecipe(int energy, int water, ItemStack primaryInput, ItemStack secondaryInput, ItemStack primaryOutput, ItemStack secondaryOutput, int secondaryChance) {

		return addRecipe(energy, water, primaryInput, secondaryInput, primaryOutput, secondaryOutput, secondaryChance, Type.STANDARD);
	}

	public static InsolatorRecipe addRecipe(int energy, int water, ItemStack primaryInput, ItemStack secondaryInput, ItemStack primaryOutput, ItemStack secondaryOutput) {

		return addRecipe(energy, water, primaryInput, secondaryInput, primaryOutput, secondaryOutput, 100, Type.STANDARD);
	}

	public static InsolatorRecipe addRecipe(int energy, int water, ItemStack primaryInput, ItemStack secondaryInput, ItemStack primaryOutput) {

		return addRecipe(energy, water, primaryInput, secondaryInput, primaryOutput, ItemStack.EMPTY, 0, Type.STANDARD);
	}

	/* NO WATER VARIANTS */
	public static InsolatorRecipe addRecipe(int energy, ItemStack primaryInput, ItemStack secondaryInput, ItemStack primaryOutput, ItemStack secondaryOutput, int secondaryChance, Type type) {

		return addRecipe(energy, energy / FLUID_FACTOR, primaryInput, secondaryInput, primaryOutput, secondaryOutput, secondaryChance, type);
	}

	public static InsolatorRecipe addRecipe(int energy, ItemStack primaryInput, ItemStack secondaryInput, ItemStack primaryOutput, ItemStack secondaryOutput, int secondaryChance) {

		return addRecipe(energy, energy / FLUID_FACTOR, primaryInput, secondaryInput, primaryOutput, secondaryOutput, secondaryChance, Type.STANDARD);
	}

	public static InsolatorRecipe addRecipe(int energy, ItemStack primaryInput, ItemStack secondaryInput, ItemStack primaryOutput, ItemStack secondaryOutput) {

		return addRecipe(energy, energy / FLUID_FACTOR, primaryInput, secondaryInput, primaryOutput, secondaryOutput, 100, Type.STANDARD);
	}

	public static InsolatorRecipe addRecipe(int energy, ItemStack primaryInput, ItemStack secondaryInput, ItemStack primaryOutput) {

		return addRecipe(energy, energy / FLUID_FACTOR, primaryInput, secondaryInput, primaryOutput, ItemStack.EMPTY, 0, Type.STANDARD);
	}

	/* REMOVE RECIPES */
	public static InsolatorRecipe removeRecipe(ItemStack primaryInput, ItemStack secondaryInput) {

		return recipeMap.remove(asList(new ComparableItemStackInsolator(primaryInput), new ComparableItemStackInsolator(secondaryInput)));
	}

	/* HELPERS */
	private static void addFertilizer(ItemStack fertilizer) {

		lockSet.add(new ComparableItemStackInsolator(fertilizer));
	}

	public static void addDefaultOreDictionaryRecipe(String oreType) {

		if (oreType.length() <= 0) {
			return;
		}
		String seedName = "seed" + StringHelper.titleCase(oreType);
		String cropName = "crop" + StringHelper.titleCase(oreType);

		List<ItemStack> registeredSeeds = OreDictionary.getOres(seedName, false);
		List<ItemStack> registeredCrops = OreDictionary.getOres(cropName, false);

		if (registeredSeeds.isEmpty() || registeredCrops.isEmpty()) {
			return;
		}
		boolean isTuber = false;
		boolean isBlock = false;
		for (ItemStack seed : registeredSeeds) {
			for (ItemStack crop : registeredCrops) {
				if (ItemHelper.itemsEqualWithMetadata(seed, crop)) {
					isTuber = true;
				}
			}
		}
		if (ItemHelper.isBlock(registeredCrops.get(0))) {
			isBlock = true;
		}
		ItemStack seed = ItemHelper.cloneStack(registeredSeeds.get(0), 1);
		ItemStack crop = ItemHelper.cloneStack(registeredCrops.get(0), isTuber ? 3 : 1);

		if (isBlock || isTuber) {
			addDefaultRecipe(seed, crop, ItemStack.EMPTY, 0);
		} else {
			addDefaultRecipe(seed, crop, seed, 110);
		}
	}

	public static void addDefaultRecipe(ItemStack primaryInput, ItemStack primaryOutput, ItemStack secondaryOutput, int secondaryChance) {

		addDefaultRecipe(DEFAULT_ENERGY, primaryInput, primaryOutput, secondaryOutput, secondaryChance, Type.STANDARD);
	}

	public static void addDefaultRecipe(int energy, ItemStack primaryInput, ItemStack primaryOutput, ItemStack secondaryOutput, int secondaryChance, Type type) {

		addDefaultRecipe(energy, energy / FLUID_FACTOR, primaryInput, primaryOutput, secondaryOutput, secondaryChance, Math.min(secondaryChance * CROP_MULTIPLIER_RICH, 125), Math.min(secondaryChance * CROP_MULTIPLIER_FLUX, 150), type);
	}

	public static void addDefaultRecipe(int energy, int water, ItemStack primaryInput, ItemStack primaryOutput, ItemStack secondaryOutput, int secondaryChance, int secondaryChanceRich, int secondaryChanceFlux, Type type) {

		if (!secondaryOutput.isEmpty()) {
			addRecipe(energy, water, primaryInput, ItemFertilizer.fertilizerBasic, primaryOutput, secondaryOutput, secondaryChance, type);
			addRecipe(energy * 5 / 4, water * 5 / 4, primaryInput, ItemFertilizer.fertilizerRich, ItemHelper.cloneStack(primaryOutput, primaryOutput.getCount() * CROP_MULTIPLIER_RICH), secondaryOutput, secondaryChanceRich, type);
			addRecipe(energy * 3 / 2, water * 3 / 2, primaryInput, ItemFertilizer.fertilizerFlux, ItemHelper.cloneStack(primaryOutput, primaryOutput.getCount() * CROP_MULTIPLIER_FLUX), secondaryOutput, secondaryChanceFlux, type);
		} else {
			addRecipe(energy, water, primaryInput, ItemFertilizer.fertilizerBasic, primaryOutput, ItemStack.EMPTY, 0, type);
			addRecipe(energy * 5 / 4, water * 5 / 4, primaryInput, ItemFertilizer.fertilizerRich, ItemHelper.cloneStack(primaryOutput, primaryOutput.getCount() * CROP_MULTIPLIER_RICH), ItemStack.EMPTY, 0, type);
			addRecipe(energy * 3 / 2, water * 3 / 2, primaryInput, ItemFertilizer.fertilizerFlux, ItemHelper.cloneStack(primaryOutput, primaryOutput.getCount() * CROP_MULTIPLIER_FLUX), ItemStack.EMPTY, 0, type);
		}
	}

	public static void addDefaultTreeRecipe(ItemStack primaryInput, ItemStack primaryOutput, ItemStack secondaryOutput) {

		addDefaultTreeRecipe(DEFAULT_ENERGY, primaryInput, primaryOutput, secondaryOutput, 100);
	}

	public static void addDefaultTreeRecipe(int energy, ItemStack primaryInput, ItemStack primaryOutput, ItemStack secondaryOutput, int secondaryChance) {

		if (!secondaryOutput.isEmpty()) {
			addRecipe(energy * 2, primaryInput, ItemFertilizer.fertilizerBasic, primaryOutput, secondaryOutput, secondaryChance, Type.TREE);
			addRecipe(energy * 5 / 4 * 2, primaryInput, ItemFertilizer.fertilizerRich, ItemHelper.cloneStack(primaryOutput, primaryOutput.getCount() * CROP_MULTIPLIER_RICH), secondaryOutput, Math.min(secondaryChance * CROP_MULTIPLIER_RICH, 125), Type.TREE);
			addRecipe(energy * 3 / 2 * 2, primaryInput, ItemFertilizer.fertilizerFlux, ItemHelper.cloneStack(primaryOutput, primaryOutput.getCount() * CROP_MULTIPLIER_FLUX), secondaryOutput, Math.min(secondaryChance * CROP_MULTIPLIER_FLUX, 150), Type.TREE);
		} else {
			addRecipe(energy * 2, primaryInput, ItemFertilizer.fertilizerBasic, primaryOutput, ItemStack.EMPTY, 0, Type.TREE);
			addRecipe(energy * 5 / 4 * 2, primaryInput, ItemFertilizer.fertilizerRich, ItemHelper.cloneStack(primaryOutput, primaryOutput.getCount() * CROP_MULTIPLIER_RICH), ItemStack.EMPTY, 0, Type.TREE);
			addRecipe(energy * 3 / 2 * 2, primaryInput, ItemFertilizer.fertilizerFlux, ItemHelper.cloneStack(primaryOutput, primaryOutput.getCount() * CROP_MULTIPLIER_FLUX), ItemStack.EMPTY, 0, Type.TREE);
		}
	}

	/* RECIPE CLASS */
	public static class InsolatorRecipe {

		final ItemStack primaryInput;
		final ItemStack secondaryInput;
		final ItemStack primaryOutput;
		final ItemStack secondaryOutput;
		final int secondaryChance;
		final int energy;
		final int water;
		final boolean hasFertilizer;
		final Type type;

		InsolatorRecipe(ItemStack secondaryInput, ItemStack primaryInput, ItemStack primaryOutput, ItemStack secondaryOutput, int secondaryChance, int energy, int water, Type type) {

			if (isItemFertilizer(primaryInput) && !isItemFertilizer(secondaryInput)) {
				this.primaryInput = secondaryInput;
				this.secondaryInput = primaryInput;
			} else {
				this.primaryInput = primaryInput;
				this.secondaryInput = secondaryInput;
			}
			this.primaryOutput = primaryOutput;
			this.secondaryOutput = secondaryOutput;
			this.secondaryChance = secondaryChance;
			this.energy = energy;
			this.water = water;
			this.type = type;
			this.hasFertilizer = isItemFertilizer(secondaryInput) || isItemFertilizer(primaryInput);
		}

		public ItemStack getPrimaryInput() {

			return primaryInput;
		}

		public ItemStack getSecondaryInput() {

			return secondaryInput;
		}

		public ItemStack getPrimaryOutput() {

			return primaryOutput;
		}

		public ItemStack getSecondaryOutput() {

			return secondaryOutput;
		}

		public int getSecondaryOutputChance() {

			return secondaryChance;
		}

		public int getEnergy() {

			return energy;
		}

		public int getWater() {

			return water;
		}

		public Type getType() {

			return type;
		}

		public boolean hasFertilizer() {

			return hasFertilizer;
		}
	}

	/* TYPE ENUM */
	public enum Type {
		STANDARD, TREE
	}

	/* ITEMSTACK CLASS */
	public static class ComparableItemStackInsolator extends ComparableItemStackSafe {

		public static final String SEED = "seed";
		public static final String CROP = "crop";
		public static final String SEEDS = "seeds";

		@Override
		public boolean safeOreType(String oreName) {

			return !oreName.startsWith(SEEDS) && (oreName.startsWith(SEED) && oreName.length() > SEED.length() || oreName.startsWith(CROP) && oreName.length() > CROP.length());
		}

		public ComparableItemStackInsolator(ItemStack stack) {

			super(stack);
		}
	}

}
