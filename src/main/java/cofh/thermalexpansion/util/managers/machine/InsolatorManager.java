package cofh.thermalexpansion.util.managers.machine;

import cofh.core.util.oredict.OreDictionaryArbiter;
import cofh.lib.inventory.ComparableItemStack;
import cofh.lib.util.helpers.ItemHelper;
import cofh.lib.util.helpers.StringHelper;
import cofh.thermalfoundation.item.ItemFertilizer;
import gnu.trove.map.hash.THashMap;
import gnu.trove.set.hash.THashSet;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import java.util.*;
import java.util.Map.Entry;

public class InsolatorManager {

	private static Map<List<ComparableItemStackInsolator>, InsolatorRecipe> recipeMap = new THashMap<>();
	private static Set<ComparableItemStackInsolator> validationSet = new THashSet<>();
	private static Set<ComparableItemStackInsolator> lockSet = new THashSet<>();

	static final int CROP_MULTIPLIER_RICH = 3;
	static final int CROP_MULTIPLIER_FLUX = 4;
	static final int DEFAULT_ENERGY = 5000;
	static final int DEFAULT_ENERGY_RICH = 7500;
	static final int DEFAULT_ENERGY_FLUX = 10000;

	public static boolean isRecipeReversed(ItemStack primaryInput, ItemStack secondaryInput) {

		if (primaryInput.isEmpty() || secondaryInput.isEmpty()) {
			return false;
		}
		ComparableItemStackInsolator query = new ComparableItemStackInsolator(primaryInput);
		ComparableItemStackInsolator querySecondary = new ComparableItemStackInsolator(secondaryInput);

		InsolatorRecipe recipe = recipeMap.get(Arrays.asList(query, querySecondary));
		return recipe == null && recipeMap.get(Arrays.asList(querySecondary, query)) != null;
	}

	public static InsolatorRecipe getRecipe(ItemStack primaryInput, ItemStack secondaryInput) {

		if (primaryInput.isEmpty() || secondaryInput.isEmpty()) {
			return null;
		}
		ComparableItemStackInsolator query = new ComparableItemStackInsolator(primaryInput);
		ComparableItemStackInsolator querySecondary = new ComparableItemStackInsolator(secondaryInput);

		InsolatorRecipe recipe = recipeMap.get(Arrays.asList(query, querySecondary));

		if (recipe == null) {
			recipe = recipeMap.get(Arrays.asList(querySecondary, query));
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
			addDefaultRecipe(new ItemStack(Items.WHEAT_SEEDS), new ItemStack(Items.WHEAT), new ItemStack(Items.WHEAT_SEEDS), 110);
			addDefaultRecipe(new ItemStack(Items.BEETROOT_SEEDS), new ItemStack(Items.BEETROOT), new ItemStack(Items.BEETROOT_SEEDS), 110);
			addDefaultRecipe(new ItemStack(Items.POTATO), new ItemStack(Items.POTATO, 3), new ItemStack(Items.POISONOUS_POTATO), 5);
			addDefaultRecipe(new ItemStack(Items.CARROT), new ItemStack(Items.CARROT, 3), ItemStack.EMPTY, 0);
			addDefaultRecipe(new ItemStack(Items.DYE, 1, 3), new ItemStack(Items.DYE, 3, 3), ItemStack.EMPTY, 0);
			addDefaultRecipe(new ItemStack(Items.REEDS), new ItemStack(Items.REEDS, 2), ItemStack.EMPTY, 0);
			addDefaultRecipe(new ItemStack(Blocks.CACTUS), new ItemStack(Blocks.CACTUS, 2), ItemStack.EMPTY, 0);
			addDefaultRecipe(new ItemStack(Blocks.VINE), new ItemStack(Blocks.VINE, 2), ItemStack.EMPTY, 0);
			addDefaultRecipe(new ItemStack(Blocks.WATERLILY), new ItemStack(Blocks.WATERLILY, 2), ItemStack.EMPTY, 0);
			addDefaultRecipe(new ItemStack(Items.PUMPKIN_SEEDS), new ItemStack(Blocks.PUMPKIN), ItemStack.EMPTY, 0);
			addDefaultRecipe(new ItemStack(Items.MELON_SEEDS), new ItemStack(Blocks.MELON_BLOCK), ItemStack.EMPTY, 0);
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

			addDefaultRecipe(new ItemStack(Blocks.DOUBLE_PLANT, 1, 0), new ItemStack(Blocks.DOUBLE_PLANT, 3, 0), ItemStack.EMPTY, 0);
			addDefaultRecipe(new ItemStack(Blocks.DOUBLE_PLANT, 1, 1), new ItemStack(Blocks.DOUBLE_PLANT, 3, 1), ItemStack.EMPTY, 0);
			addDefaultRecipe(new ItemStack(Blocks.DOUBLE_PLANT, 1, 4), new ItemStack(Blocks.DOUBLE_PLANT, 3, 4), ItemStack.EMPTY, 0);
			addDefaultRecipe(new ItemStack(Blocks.DOUBLE_PLANT, 1, 5), new ItemStack(Blocks.DOUBLE_PLANT, 3, 5), ItemStack.EMPTY, 0);
		}

		/* MYCELIUM */
		{
			ArrayList<ItemStack> crops = new ArrayList<>();
			crops.add(new ItemStack(Blocks.BROWN_MUSHROOM));
			crops.add(new ItemStack(Blocks.RED_MUSHROOM));

			for (ItemStack input : crops) {
				addDefaultRecipe(input, ItemHelper.cloneStack(input, 2), ItemStack.EMPTY, 0, false, Type.MYCELIUM);
			}
		}

		/* NETHER */
		{
			ArrayList<ItemStack> crops = new ArrayList<>();
			crops.add(new ItemStack(Items.NETHER_WART));

			for (ItemStack input : crops) {
				addDefaultRecipe(input, ItemHelper.cloneStack(input, 2), ItemStack.EMPTY, 0, false, Type.NETHER);
			}
		}

		/* END */
		{
			ItemStack input = new ItemStack(Blocks.CHORUS_FLOWER);
			ItemStack output = new ItemStack(Blocks.CHORUS_PLANT);

			addDefaultRecipe(input, ItemHelper.cloneStack(output, 2), input, 100, false, Type.END);
		}

		/* TREE */
		{
			addDefaultTreeRecipe(new ItemStack(Blocks.SAPLING, 1, 0), new ItemStack(Blocks.LOG, 4, 0), new ItemStack(Blocks.SAPLING, 1, 0), 50, false, Type.TREE);
			addDefaultTreeRecipe(new ItemStack(Blocks.SAPLING, 1, 1), new ItemStack(Blocks.LOG, 4, 1), new ItemStack(Blocks.SAPLING, 1, 1), 50, false, Type.TREE);
			addDefaultTreeRecipe(new ItemStack(Blocks.SAPLING, 1, 2), new ItemStack(Blocks.LOG, 4, 2), new ItemStack(Blocks.SAPLING, 1, 2), 50, false, Type.TREE);
			addDefaultTreeRecipe(new ItemStack(Blocks.SAPLING, 1, 3), new ItemStack(Blocks.LOG, 4, 3), new ItemStack(Blocks.SAPLING, 1, 3), 50, false, Type.TREE);
			addDefaultTreeRecipe(new ItemStack(Blocks.SAPLING, 1, 4), new ItemStack(Blocks.LOG2, 4, 0), new ItemStack(Blocks.SAPLING, 1, 4), 50, false, Type.TREE);
			addDefaultTreeRecipe(new ItemStack(Blocks.SAPLING, 1, 5), new ItemStack(Blocks.LOG2, 4, 1), new ItemStack(Blocks.SAPLING, 1, 5), 50, false, Type.TREE);
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

			tempMap.put(Arrays.asList(primary, secondary), tempRecipe);
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
	public static InsolatorRecipe addRecipe(int energy, ItemStack primaryInput, ItemStack secondaryInput, ItemStack primaryOutput, ItemStack secondaryOutput, int secondaryChance, boolean copyNBT, Type type) {

		if (primaryInput.isEmpty() || secondaryInput.isEmpty() || energy <= 0 || recipeExists(primaryInput, secondaryInput)) {
			return null;
		}
		InsolatorRecipe recipe = new InsolatorRecipe(primaryInput, secondaryInput, primaryOutput, secondaryOutput, secondaryOutput.isEmpty() ? 0 : secondaryChance, energy, copyNBT, type);
		recipeMap.put(Arrays.asList(new ComparableItemStackInsolator(primaryInput), new ComparableItemStackInsolator(secondaryInput)), recipe);
		validationSet.add(new ComparableItemStackInsolator(primaryInput));
		validationSet.add(new ComparableItemStackInsolator(secondaryInput));
		return recipe;
	}

	public static InsolatorRecipe addRecipe(int energy, ItemStack primaryInput, ItemStack secondaryInput, ItemStack primaryOutput, ItemStack secondaryOutput, int secondaryChance) {

		return addRecipe(energy, primaryInput, secondaryInput, primaryOutput, secondaryOutput, secondaryChance, false, Type.STANDARD);
	}

	public static InsolatorRecipe addRecipe(int energy, ItemStack primaryInput, ItemStack secondaryInput, ItemStack primaryOutput, ItemStack secondaryOutput) {

		return addRecipe(energy, primaryInput, secondaryInput, primaryOutput, secondaryOutput, 100);
	}

	public static InsolatorRecipe addRecipe(int energy, ItemStack primaryInput, ItemStack secondaryInput, ItemStack primaryOutput) {

		return addRecipe(energy, primaryInput, secondaryInput, primaryOutput, ItemStack.EMPTY, 0);
	}

	/* REMOVE RECIPES */
	public static InsolatorRecipe removeRecipe(ItemStack primaryInput, ItemStack secondaryInput) {

		return recipeMap.remove(Arrays.asList(new ComparableItemStackInsolator(primaryInput), new ComparableItemStackInsolator(secondaryInput)));
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

	public static void addDefaultRecipe(ItemStack primaryInput, ItemStack primaryOutput, ItemStack secondaryOutput, int secondaryChance, boolean copyNBT, Type type) {

		if (!secondaryOutput.isEmpty()) {
			addRecipe(DEFAULT_ENERGY, primaryInput, ItemFertilizer.fertilizerBasic, primaryOutput, secondaryOutput, secondaryChance, copyNBT, type);
			addRecipe(DEFAULT_ENERGY_RICH, primaryInput, ItemFertilizer.fertilizerRich, ItemHelper.cloneStack(primaryOutput, primaryOutput.getCount() * CROP_MULTIPLIER_RICH), secondaryOutput, secondaryChance < 100 ? 100 : secondaryChance < 125 ? 125 : secondaryChance, copyNBT, type);
			addRecipe(DEFAULT_ENERGY_FLUX, primaryInput, ItemFertilizer.fertilizerFlux, ItemHelper.cloneStack(primaryOutput, primaryOutput.getCount() * CROP_MULTIPLIER_FLUX), secondaryOutput, secondaryChance < 150 ? 150 : secondaryChance, copyNBT, type);
		} else {
			addRecipe(DEFAULT_ENERGY, primaryInput, ItemFertilizer.fertilizerBasic, primaryOutput, ItemStack.EMPTY, 0, copyNBT, type);
			addRecipe(DEFAULT_ENERGY_RICH, primaryInput, ItemFertilizer.fertilizerRich, ItemHelper.cloneStack(primaryOutput, primaryOutput.getCount() * CROP_MULTIPLIER_RICH), ItemStack.EMPTY, 0, copyNBT, type);
			addRecipe(DEFAULT_ENERGY_FLUX, primaryInput, ItemFertilizer.fertilizerFlux, ItemHelper.cloneStack(primaryOutput, primaryOutput.getCount() * CROP_MULTIPLIER_FLUX), ItemStack.EMPTY, 0, copyNBT, type);
		}
	}

	public static void addDefaultTreeRecipe(ItemStack primaryInput, ItemStack primaryOutput, ItemStack secondaryOutput, int secondaryChance, boolean copyNBT, Type type) {

		if (!secondaryOutput.isEmpty()) {
			addRecipe(DEFAULT_ENERGY * 2, primaryInput, ItemFertilizer.fertilizerBasic, primaryOutput, secondaryOutput, secondaryChance, copyNBT, type);
			addRecipe(DEFAULT_ENERGY_RICH * 2, primaryInput, ItemFertilizer.fertilizerRich, ItemHelper.cloneStack(primaryOutput, primaryOutput.getCount() * CROP_MULTIPLIER_RICH), secondaryOutput, secondaryChance < 100 ? 100 : secondaryChance < 125 ? 125 : secondaryChance, copyNBT, type);
			addRecipe(DEFAULT_ENERGY_FLUX * 2, primaryInput, ItemFertilizer.fertilizerFlux, ItemHelper.cloneStack(primaryOutput, primaryOutput.getCount() * CROP_MULTIPLIER_FLUX), secondaryOutput, secondaryChance < 150 ? 150 : secondaryChance, copyNBT, type);
		} else {
			addRecipe(DEFAULT_ENERGY * 2, primaryInput, ItemFertilizer.fertilizerBasic, primaryOutput, ItemStack.EMPTY, 0, copyNBT, type);
			addRecipe(DEFAULT_ENERGY_RICH * 2, primaryInput, ItemFertilizer.fertilizerRich, ItemHelper.cloneStack(primaryOutput, primaryOutput.getCount() * CROP_MULTIPLIER_RICH), ItemStack.EMPTY, 0, copyNBT, type);
			addRecipe(DEFAULT_ENERGY_FLUX * 2, primaryInput, ItemFertilizer.fertilizerFlux, ItemHelper.cloneStack(primaryOutput, primaryOutput.getCount() * CROP_MULTIPLIER_FLUX), ItemStack.EMPTY, 0, copyNBT, type);
		}
	}

	public static void addDefaultRecipe(ItemStack primaryInput, ItemStack primaryOutput, ItemStack secondaryOutput, int secondaryChance) {

		addDefaultRecipe(primaryInput, primaryOutput, secondaryOutput, secondaryChance, false, Type.STANDARD);
	}

	/* RECIPE CLASS */
	public static class InsolatorRecipe {

		final ItemStack primaryInput;
		final ItemStack secondaryInput;
		final ItemStack primaryOutput;
		final ItemStack secondaryOutput;
		final int secondaryChance;
		final int energy;
		final boolean copyNBT;
		final Type type;

		InsolatorRecipe(ItemStack primaryInput, ItemStack secondaryInput, ItemStack primaryOutput, ItemStack secondaryOutput, int secondaryChance, int energy, boolean copyNBT, Type type) {

			this.primaryInput = primaryInput;
			this.secondaryInput = secondaryInput;
			this.primaryOutput = primaryOutput;
			this.secondaryOutput = secondaryOutput;
			this.secondaryChance = secondaryChance;
			this.energy = energy;
			this.copyNBT = copyNBT;
			this.type = type;

			if (primaryInput.getCount() <= 0) {
				primaryInput.setCount(1);
			}
			if (secondaryInput.getCount() <= 0) {
				secondaryInput.setCount(1);
			}
			if (primaryOutput.getCount() <= 0) {
				primaryOutput.setCount(1);
			}
			if (!secondaryOutput.isEmpty() && secondaryOutput.getCount() <= 0) {
				secondaryOutput.setCount(1);
			}
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

		public Type getType() {

			return type;
		}
	}

	/* TYPE ENUM */
	public enum Type {
		STANDARD, MYCELIUM, NETHER, END, TREE, MYCELIUM_TREE, NETHER_TREE, END_TREE
	}

	/* ITEMSTACK CLASS */
	public static class ComparableItemStackInsolator extends ComparableItemStack {

		public static final String SEED = "seed";
		public static final String CROP = "crop";

		public static boolean safeOreType(String oreName) {

			return oreName.startsWith(SEED) || oreName.startsWith(CROP);
		}

		public static int getOreID(ItemStack stack) {

			ArrayList<Integer> ids = OreDictionaryArbiter.getAllOreIDs(stack);

			if (ids != null) {
				for (int i = 0, e = ids.size(); i < e; ) {
					int id = ids.get(i++);
					if (id != -1 && safeOreType(ItemHelper.oreProxy.getOreName(id))) {
						return id;
					}
				}
			}
			return -1;
		}

		public ComparableItemStackInsolator(ItemStack stack) {

			super(stack);
			oreID = getOreID(stack);
		}

		@Override
		public ComparableItemStackInsolator set(ItemStack stack) {

			super.set(stack);
			oreID = getOreID(stack);

			return this;
		}
	}

}
