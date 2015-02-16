package cofh.thermalexpansion.util.crafting;

import cofh.lib.inventory.ComparableItemStack;
import cofh.lib.util.helpers.ItemHelper;
import cofh.lib.util.helpers.MathHelper;
import cofh.lib.util.helpers.StringHelper;
import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalexpansion.item.TEItems;

import gnu.trove.map.hash.THashMap;
import gnu.trove.set.hash.THashSet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public class InsolatorManager {

	private static Map<List<ComparableItemStack>, RecipeInsolator> recipeMap = new THashMap<List<ComparableItemStack>, RecipeInsolator>();
	private static Set<ComparableItemStack> validationSet = new THashSet<ComparableItemStack>();
	private static ComparableItemStack query = new ComparableItemStack(new ItemStack(Blocks.stone));
	private static ComparableItemStack querySecondary = new ComparableItemStack(new ItemStack(Blocks.stone));
	private static boolean allowOverwrite = false;

	private static int cropMultiplierSpecial = 2;

	public static final int DEFAULT_ENERGY = 7200;
	public static final int DEFAULT_ENERGY_SPECIAL = 9600;

	static {
		allowOverwrite = ThermalExpansion.config.get("RecipeManagers.Insolator", "AllowRecipeOverwrite", false);

		String category = "RecipeManagers.Insolator.Crop";
		String comment = "This sets the boosted rate for Crop growth - when Rich Phyto-Gro is used. This number is used in all automatically generated recipes.";
		cropMultiplierSpecial = MathHelper.clampI(ThermalExpansion.config.get(category, "DefaultMultiplier", cropMultiplierSpecial, comment), 1, 64);
	}

	public static boolean isRecipeReversed(ItemStack primaryInput, ItemStack secondaryInput) {

		if (primaryInput == null || secondaryInput == null) {
			return false;
		}
		RecipeInsolator recipe = recipeMap.get(Arrays.asList(query.set(primaryInput), querySecondary.set(secondaryInput)));
		return recipe != null ? false : recipeMap.get(Arrays.asList(querySecondary, query)) != null;
	}

	public static RecipeInsolator getRecipe(ItemStack primaryInput, ItemStack secondaryInput) {

		if (primaryInput == null || secondaryInput == null) {
			return null;
		}
		RecipeInsolator recipe = recipeMap.get(Arrays.asList(query.set(primaryInput), querySecondary.set(secondaryInput)));

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

	public static RecipeInsolator[] getRecipeList() {

		return recipeMap.values().toArray(new RecipeInsolator[0]);
	}

	public static boolean isItemValid(ItemStack input) {

		return input == null ? false : validationSet.contains(query.set(input));
	}

	public static void addDefaultRecipes() {

		// OreDictionary.registerOre("seedCarrot", new ItemStack(Items.carrot));

		addDefaultRecipe(new ItemStack(Items.wheat_seeds), new ItemStack(Items.wheat), new ItemStack(Items.wheat_seeds), 150);
		addDefaultRecipe(new ItemStack(Items.potato), new ItemStack(Items.potato, 3), new ItemStack(Items.poisonous_potato), 2);
		addDefaultRecipe(new ItemStack(Items.carrot), new ItemStack(Items.carrot, 3), null, 0);
		addDefaultRecipe(new ItemStack(Items.reeds), new ItemStack(Items.reeds, 2), null, 0);
		addDefaultRecipe(new ItemStack(Blocks.cactus), new ItemStack(Blocks.cactus, 2), null, 0);
		addDefaultRecipe(new ItemStack(Items.pumpkin_seeds), new ItemStack(Blocks.pumpkin), null, 0);
		addDefaultRecipe(new ItemStack(Items.melon_seeds), new ItemStack(Blocks.melon_block), null, 0);
	}

	public static void loadRecipes() {

		String[] oreNameList = OreDictionary.getOreNames();
		String oreName = "";

		for (int i = 0; i < oreNameList.length; i++) {
			if (oreNameList[i].startsWith("seed")) {
				oreName = oreNameList[i].substring(4, oreNameList[i].length());

				// if (isStandardOre(oreNameList[i])) {
				addDefaultOreDictionaryRecipe(oreName);
				// }
			}
		}
	}

	public static void refreshRecipes() {

		Map<List<ComparableItemStack>, RecipeInsolator> tempMap = new THashMap<List<ComparableItemStack>, RecipeInsolator>(recipeMap.size());
		Set<ComparableItemStack> tempSet = new THashSet<ComparableItemStack>();
		RecipeInsolator tempRecipe;

		for (Entry<List<ComparableItemStack>, RecipeInsolator> entry : recipeMap.entrySet()) {
			tempRecipe = entry.getValue();
			ComparableItemStack primary = new ComparableItemStack(tempRecipe.primaryInput);
			ComparableItemStack secondary = new ComparableItemStack(tempRecipe.secondaryInput);

			tempMap.put(Arrays.asList(primary, secondary), tempRecipe);
			tempSet.add(primary);
			tempSet.add(secondary);
		}
		recipeMap.clear();
		recipeMap = tempMap;
		validationSet.clear();
		validationSet = tempSet;
	}

	/* ADD RECIPES */
	protected static boolean addTERecipe(int energy, ItemStack primaryInput, ItemStack secondaryInput, ItemStack primaryOutput, ItemStack secondaryOutput,
			int secondaryChance) {

		if (primaryInput == null || secondaryInput == null || energy <= 0) {
			return false;
		}
		RecipeInsolator recipe = new RecipeInsolator(primaryInput, secondaryInput, primaryOutput, secondaryOutput, secondaryChance, energy);
		recipeMap.put(Arrays.asList(new ComparableItemStack(primaryInput), new ComparableItemStack(secondaryInput)), recipe);
		validationSet.add(new ComparableItemStack(primaryInput));
		validationSet.add(new ComparableItemStack(secondaryInput));
		return true;
	}

	public static boolean addRecipe(int energy, ItemStack primaryInput, ItemStack secondaryInput, ItemStack primaryOutput, ItemStack secondaryOutput,
			int secondaryChance, boolean overwrite) {

		if (primaryInput == null || secondaryInput == null || energy <= 0 || !(allowOverwrite & overwrite) && recipeExists(primaryInput, secondaryInput)) {
			return false;
		}
		RecipeInsolator recipe = new RecipeInsolator(primaryInput, secondaryInput, primaryOutput, secondaryOutput, secondaryChance, energy);
		recipeMap.put(Arrays.asList(new ComparableItemStack(primaryInput), new ComparableItemStack(secondaryInput)), recipe);
		validationSet.add(new ComparableItemStack(primaryInput));
		validationSet.add(new ComparableItemStack(secondaryInput));
		return true;
	}

	/* HELPER FUNCTIONS */
	public static void addDefaultOreDictionaryRecipe(String oreType) {

		String seedName = "seed" + StringHelper.titleCase(oreType);
		String cropName = "crop" + StringHelper.titleCase(oreType);

		ArrayList<ItemStack> registeredSeed = OreDictionary.getOres(seedName);
		ArrayList<ItemStack> registeredCrop = OreDictionary.getOres(cropName);

		if (registeredSeed.isEmpty() || registeredCrop.isEmpty()) {
			return;
		}
		boolean isTuber = false;
		boolean isBlock = false;
		for (int i = 0; i < registeredSeed.size(); i++) {
			for (int j = 0; j < registeredCrop.size(); j++) {
				if (ItemHelper.itemsEqualWithMetadata(registeredSeed.get(i), registeredCrop.get(j))) {
					isTuber = true;
				}
			}
		}
		if (ItemHelper.isBlock(registeredCrop.get(0))) {
			isBlock = true;
		}
		ItemStack seed = ItemHelper.cloneStack(registeredSeed.get(0), 1);
		ItemStack crop = ItemHelper.cloneStack(registeredCrop.get(0), isTuber ? 3 : 1);

		if (isBlock || isTuber) {
			addDefaultRecipe(seed, crop, null, 0);
		} else {
			addDefaultRecipe(seed, crop, seed, 150);
		}

	}

	public static void addDefaultRecipe(ItemStack primaryInput, ItemStack primaryOutput, ItemStack secondaryOutput, int secondaryChance) {

		if (secondaryOutput != null) {
			addRecipe(DEFAULT_ENERGY, primaryInput, TEItems.fertilizer, primaryOutput, secondaryOutput, secondaryChance);

			if (secondaryChance < 100) {
				secondaryChance = Math.min(100, secondaryChance * cropMultiplierSpecial);
			}
			addRecipe(DEFAULT_ENERGY_SPECIAL, primaryInput, TEItems.fertilizerRich,
					ItemHelper.cloneStack(primaryOutput, primaryOutput.stackSize * cropMultiplierSpecial), secondaryOutput, secondaryChance);
		} else {
			addRecipe(DEFAULT_ENERGY, primaryInput, TEItems.fertilizer, primaryOutput);
			addRecipe(DEFAULT_ENERGY_SPECIAL, primaryInput, TEItems.fertilizerRich,
					ItemHelper.cloneStack(primaryOutput, primaryOutput.stackSize * cropMultiplierSpecial));
		}
	}

	public static boolean addRecipe(int energy, ItemStack primaryInput, ItemStack secondaryInput, ItemStack primaryOutput) {

		return addRecipe(energy, primaryInput, secondaryInput, primaryOutput, false);
	}

	public static boolean addRecipe(int energy, ItemStack primaryInput, ItemStack secondaryInput, ItemStack primaryOutput, boolean overwrite) {

		return addRecipe(energy, primaryInput, secondaryInput, primaryOutput, null, 0, overwrite);
	}

	public static boolean addRecipe(int energy, ItemStack primaryInput, ItemStack secondaryInput, ItemStack primaryOutput, ItemStack secondaryOutput) {

		return addRecipe(energy, primaryInput, secondaryInput, primaryOutput, secondaryOutput, false);
	}

	public static boolean addRecipe(int energy, ItemStack primaryInput, ItemStack secondaryInput, ItemStack primaryOutput, ItemStack secondaryOutput,
			boolean overwrite) {

		return addRecipe(energy, primaryInput, secondaryInput, primaryOutput, secondaryOutput, 100, overwrite);
	}

	public static boolean addRecipe(int energy, ItemStack primaryInput, ItemStack secondaryInput, ItemStack primaryOutput, ItemStack secondaryOutput,
			int secondaryChance) {

		return addRecipe(energy, primaryInput, secondaryInput, primaryOutput, secondaryOutput, secondaryChance, false);
	}

	/* RECIPE CLASS */
	public static class RecipeInsolator {

		final ItemStack primaryInput;
		final ItemStack secondaryInput;
		final ItemStack primaryOutput;
		final ItemStack secondaryOutput;
		final int secondaryChance;
		final int energy;

		RecipeInsolator(ItemStack primaryInput, ItemStack secondaryInput, ItemStack primaryOutput, ItemStack secondaryOutput, int secondaryChance, int energy) {

			this.primaryInput = primaryInput;
			this.secondaryInput = secondaryInput;
			this.primaryOutput = primaryOutput;
			this.secondaryOutput = secondaryOutput;
			this.secondaryChance = secondaryChance;
			this.energy = energy;
		}

		public ItemStack getPrimaryInput() {

			return primaryInput.copy();
		}

		public ItemStack getSecondaryInput() {

			return secondaryInput.copy();
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

}
