package cofh.thermalexpansion.util.managers.machine;

import cofh.core.init.CorePotions;
import cofh.core.inventory.ComparableItemStackNBT;
import cofh.core.util.helpers.ItemHelper;
import cofh.thermalfoundation.item.ItemMaterial;
import gnu.trove.map.hash.THashMap;
import gnu.trove.set.hash.THashSet;
import net.minecraft.init.Items;
import net.minecraft.init.PotionTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionType;
import net.minecraft.potion.PotionUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class BrewerManager {

	private static Map<List<ComparableItemStackBrewer>, BrewerRecipe> recipeMap = new THashMap<>();
	private static Set<ComparableItemStackBrewer> validationSet = new THashSet<>();
	// private static Set<ComparableItemStackBrewer> lockSet = new THashSet<>();

	static final ItemStack POTION_BASE = PotionUtils.addPotionToItemStack(ItemHelper.cloneStack(Items.POTIONITEM, 1), PotionTypes.WATER);
	static final ItemStack SPLASH_BASE = PotionUtils.addPotionToItemStack(ItemHelper.cloneStack(Items.SPLASH_POTION, 1), PotionTypes.WATER);
	static final ItemStack LINGERING_BASE = PotionUtils.addPotionToItemStack(ItemHelper.cloneStack(Items.LINGERING_POTION, 1), PotionTypes.WATER);

	static final ItemStack GUNPOWDER = ItemHelper.cloneStack(Items.GUNPOWDER, 1);
	static final ItemStack DRAGON_BREATH = ItemHelper.cloneStack(Items.DRAGON_BREATH, 1);

	public static final int DEFAULT_ENERGY = 1600;

	public static boolean isRecipeReversed(ItemStack primaryInput, ItemStack secondaryInput) {

		if (primaryInput.isEmpty() || secondaryInput.isEmpty()) {
			return false;
		}
		ComparableItemStackBrewer query = new ComparableItemStackBrewer(primaryInput);
		ComparableItemStackBrewer querySecondary = new ComparableItemStackBrewer(secondaryInput);

		BrewerRecipe recipe = recipeMap.get(Arrays.asList(query, querySecondary));
		return recipe == null && recipeMap.get(Arrays.asList(querySecondary, query)) != null;
	}

	public static BrewerRecipe getRecipe(ItemStack primaryInput, ItemStack secondaryInput) {

		if (primaryInput.isEmpty() || secondaryInput.isEmpty()) {
			return null;
		}
		ComparableItemStackBrewer query = new ComparableItemStackBrewer(primaryInput);
		ComparableItemStackBrewer querySecondary = new ComparableItemStackBrewer(secondaryInput);

		BrewerRecipe recipe = recipeMap.get(Arrays.asList(query, querySecondary));

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

	public static BrewerRecipe[] getRecipeList() {

		return recipeMap.values().toArray(new BrewerRecipe[recipeMap.size()]);
	}

	public static boolean isItemValid(ItemStack input) {

		return !input.isEmpty() && validationSet.contains(new ComparableItemStackBrewer(input));
	}

	public static boolean isVanillaPotion(ItemStack input) {

		return ItemHelper.areItemsEqual(input.getItem(), Items.POTIONITEM) || ItemHelper.areItemsEqual(input.getItem(), Items.SPLASH_POTION) || ItemHelper.areItemsEqual(input.getItem(), Items.LINGERING_POTION);
	}

	public static boolean isContainer(ItemStack input) {

		return !input.isEmpty() && (isVanillaPotion(input)); // || lockSet.contains(new ComparableItemStackBrewer(input)));
	}

	public static void initialize() {

		int energy = DEFAULT_ENERGY;

		ItemStack goldenCarrot = new ItemStack(Items.GOLDEN_CARROT);
		ItemStack rabbitFoot = new ItemStack(Items.RABBIT_FOOT);
		ItemStack magmaCream = new ItemStack(Items.MAGMA_CREAM);
		ItemStack sugar = new ItemStack(Items.SUGAR);
		ItemStack melon = new ItemStack(Items.SPECKLED_MELON);
		ItemStack spiderEye = new ItemStack(Items.SPIDER_EYE);
		ItemStack ghastTear = new ItemStack(Items.GHAST_TEAR);
		ItemStack blazePowder = new ItemStack(Items.BLAZE_POWDER);

		ItemStack redstone = ItemHelper.cloneStack(Items.REDSTONE, 1);
		ItemStack glowstone = ItemHelper.cloneStack(Items.GLOWSTONE_DUST, 1);
		ItemStack fermentedSpiderEye = ItemHelper.cloneStack(Items.FERMENTED_SPIDER_EYE, 1);

		addRecipe(energy, POTION_BASE, GUNPOWDER, SPLASH_BASE);
		addRecipe(energy, SPLASH_BASE, DRAGON_BREATH, LINGERING_BASE);

		addDefaultPotionRecipes(PotionTypes.WATER, goldenCarrot, PotionTypes.MUNDANE);
		addDefaultPotionRecipes(PotionTypes.WATER, rabbitFoot, PotionTypes.MUNDANE);
		addDefaultPotionRecipes(PotionTypes.WATER, magmaCream, PotionTypes.MUNDANE);
		addDefaultPotionRecipes(PotionTypes.WATER, sugar, PotionTypes.MUNDANE);
		addDefaultPotionRecipes(PotionTypes.WATER, melon, PotionTypes.MUNDANE);
		addDefaultPotionRecipes(PotionTypes.WATER, spiderEye, PotionTypes.MUNDANE);
		addDefaultPotionRecipes(PotionTypes.WATER, ghastTear, PotionTypes.MUNDANE);
		addDefaultPotionRecipes(PotionTypes.WATER, blazePowder, PotionTypes.MUNDANE);

		addDefaultPotionRecipes(PotionTypes.WATER, redstone, PotionTypes.MUNDANE);
		addDefaultPotionRecipes(PotionTypes.WATER, glowstone, PotionTypes.THICK);
		addDefaultPotionRecipes(PotionTypes.WATER, fermentedSpiderEye, PotionTypes.WEAKNESS);

		addDefaultPotionRecipes(PotionTypes.AWKWARD, goldenCarrot, PotionTypes.NIGHT_VISION);
		addDefaultPotionRecipes(PotionTypes.AWKWARD, rabbitFoot, PotionTypes.LEAPING);
		addDefaultPotionRecipes(PotionTypes.AWKWARD, magmaCream, PotionTypes.FIRE_RESISTANCE);
		addDefaultPotionRecipes(PotionTypes.AWKWARD, sugar, PotionTypes.SWIFTNESS);
		addDefaultPotionRecipes(PotionTypes.AWKWARD, new ItemStack(Items.FISH, 1, 3), PotionTypes.WATER_BREATHING);
		addDefaultPotionRecipes(PotionTypes.AWKWARD, melon, PotionTypes.HEALING);
		addDefaultPotionRecipes(PotionTypes.AWKWARD, spiderEye, PotionTypes.POISON);
		addDefaultPotionRecipes(PotionTypes.AWKWARD, ghastTear, PotionTypes.REGENERATION);
		addDefaultPotionRecipes(PotionTypes.AWKWARD, blazePowder, PotionTypes.STRENGTH);

		addDefaultPotionRecipes(PotionTypes.NIGHT_VISION, fermentedSpiderEye, PotionTypes.INVISIBILITY);
		addDefaultPotionRecipes(PotionTypes.LEAPING, fermentedSpiderEye, PotionTypes.SLOWNESS);
		addDefaultPotionRecipes(PotionTypes.SWIFTNESS, fermentedSpiderEye, PotionTypes.SLOWNESS);
		addDefaultPotionRecipes(PotionTypes.HEALING, fermentedSpiderEye, PotionTypes.HARMING);
		addDefaultPotionRecipes(PotionTypes.POISON, fermentedSpiderEye, PotionTypes.HARMING);

		addDefaultPotionRecipes(PotionTypes.NIGHT_VISION, redstone, PotionTypes.LONG_NIGHT_VISION);
		addDefaultPotionRecipes(PotionTypes.INVISIBILITY, redstone, PotionTypes.LONG_INVISIBILITY);
		addDefaultPotionRecipes(PotionTypes.LEAPING, redstone, PotionTypes.LONG_LEAPING);
		addDefaultPotionRecipes(PotionTypes.FIRE_RESISTANCE, redstone, PotionTypes.LONG_FIRE_RESISTANCE);
		addDefaultPotionRecipes(PotionTypes.SWIFTNESS, redstone, PotionTypes.LONG_SWIFTNESS);
		addDefaultPotionRecipes(PotionTypes.SLOWNESS, redstone, PotionTypes.LONG_SLOWNESS);
		addDefaultPotionRecipes(PotionTypes.WATER_BREATHING, redstone, PotionTypes.LONG_WATER_BREATHING);
		addDefaultPotionRecipes(PotionTypes.POISON, redstone, PotionTypes.LONG_POISON);
		addDefaultPotionRecipes(PotionTypes.REGENERATION, redstone, PotionTypes.LONG_REGENERATION);
		addDefaultPotionRecipes(PotionTypes.STRENGTH, redstone, PotionTypes.LONG_STRENGTH);
		addDefaultPotionRecipes(PotionTypes.WEAKNESS, redstone, PotionTypes.LONG_WEAKNESS);

		addDefaultPotionRecipes(PotionTypes.LEAPING, glowstone, PotionTypes.STRONG_LEAPING);
		addDefaultPotionRecipes(PotionTypes.SWIFTNESS, glowstone, PotionTypes.STRONG_SWIFTNESS);
		addDefaultPotionRecipes(PotionTypes.HEALING, glowstone, PotionTypes.STRONG_HEALING);
		addDefaultPotionRecipes(PotionTypes.HARMING, glowstone, PotionTypes.STRONG_HARMING);
		addDefaultPotionRecipes(PotionTypes.POISON, glowstone, PotionTypes.STRONG_POISON);
		addDefaultPotionRecipes(PotionTypes.REGENERATION, glowstone, PotionTypes.STRONG_REGENERATION);
		addDefaultPotionRecipes(PotionTypes.STRENGTH, glowstone, PotionTypes.STRONG_STRENGTH);

		addDefaultPotionRecipes(PotionTypes.LONG_NIGHT_VISION, fermentedSpiderEye, PotionTypes.LONG_INVISIBILITY);
		addDefaultPotionRecipes(PotionTypes.LONG_LEAPING, fermentedSpiderEye, PotionTypes.LONG_SLOWNESS);
		addDefaultPotionRecipes(PotionTypes.LONG_SWIFTNESS, fermentedSpiderEye, PotionTypes.LONG_SLOWNESS);
		addDefaultPotionRecipes(PotionTypes.LONG_POISON, fermentedSpiderEye, PotionTypes.HARMING);
		addDefaultPotionRecipes(PotionTypes.STRONG_HEALING, fermentedSpiderEye, PotionTypes.STRONG_HARMING);
		addDefaultPotionRecipes(PotionTypes.STRONG_POISON, fermentedSpiderEye, PotionTypes.STRONG_HARMING);

		addDefaultPotionRecipes(PotionTypes.WATER, ItemMaterial.dustBasalz, CorePotions.haste);
		addDefaultPotionRecipes(PotionTypes.WATER, ItemMaterial.dustObsidian, CorePotions.resistance);
		addDefaultPotionRecipes(PotionTypes.WATER, ItemMaterial.dustBlitz, CorePotions.levitation);
		addDefaultPotionRecipes(PotionTypes.WATER, ItemMaterial.dustBlizz, CorePotions.absorption);
		addDefaultPotionRecipes(PotionTypes.WATER, new ItemStack(Items.POISONOUS_POTATO), CorePotions.saturation);

		addDefaultPotionRecipes(CorePotions.haste, redstone, CorePotions.hasteLong);
		addDefaultPotionRecipes(CorePotions.resistance, redstone, CorePotions.resistanceLong);
		addDefaultPotionRecipes(CorePotions.levitation, redstone, CorePotions.levitationLong);
		addDefaultPotionRecipes(CorePotions.absorption, redstone, CorePotions.absorptionLong);
		addDefaultPotionRecipes(CorePotions.saturation, redstone, CorePotions.saturationLong);

		addDefaultPotionRecipes(CorePotions.haste, glowstone, CorePotions.hasteStrong);
		addDefaultPotionRecipes(CorePotions.resistance, glowstone, CorePotions.resistanceStrong);
		addDefaultPotionRecipes(CorePotions.absorption, glowstone, CorePotions.absorptionStrong);
		addDefaultPotionRecipes(CorePotions.saturation, glowstone, CorePotions.saturationStrong);

		/* LOAD RECIPES */
		loadRecipes();
	}

	public static void loadRecipes() {

	}

	public static void refresh() {

		Map<List<ComparableItemStackBrewer>, BrewerRecipe> tempMap = new THashMap<>(recipeMap.size());
		Set<ComparableItemStackBrewer> tempSet = new THashSet<>();
		BrewerRecipe tempRecipe;

		for (Entry<List<ComparableItemStackBrewer>, BrewerRecipe> entry : recipeMap.entrySet()) {
			tempRecipe = entry.getValue();
			ComparableItemStackBrewer primary = new ComparableItemStackBrewer(tempRecipe.primaryInput);
			ComparableItemStackBrewer secondary = new ComparableItemStackBrewer(tempRecipe.secondaryInput);

			tempMap.put(Arrays.asList(primary, secondary), tempRecipe);
			tempSet.add(primary);
			tempSet.add(secondary);
		}
		recipeMap.clear();
		recipeMap = tempMap;
		validationSet.clear();
		validationSet = tempSet;

		//		Set<ComparableItemStackBrewer> tempSet2 = new THashSet<>();
		//		for (ComparableItemStackBrewer entry : lockSet) {
		//			ComparableItemStackBrewer lock = new ComparableItemStackBrewer(new ItemStack(entry.item, entry.stackSize, entry.metadata, entry.tag));
		//			tempSet2.add(lock);
		//		}
		//		lockSet.clear();
		//		lockSet = tempSet2;
	}

	/* ADD RECIPES */
	public static BrewerRecipe addRecipe(int energy, ItemStack primaryInput, ItemStack secondaryInput, ItemStack output) {

		if (primaryInput.isEmpty() || secondaryInput.isEmpty() || energy <= 0 || recipeExists(primaryInput, secondaryInput)) {
			return null;
		}
		BrewerRecipe recipe = new BrewerRecipe(primaryInput, secondaryInput, output, energy);
		recipeMap.put(Arrays.asList(new ComparableItemStackBrewer(primaryInput), new ComparableItemStackBrewer(secondaryInput)), recipe);
		validationSet.add(new ComparableItemStackBrewer(primaryInput));
		validationSet.add(new ComparableItemStackBrewer(secondaryInput));
		return recipe;
	}

	/* HELPERS */
	public static void addDefaultPotionRecipes(PotionType inputType, ItemStack reagent, PotionType outputType) {

		ItemStack inputPotion = PotionUtils.addPotionToItemStack(ItemHelper.cloneStack(Items.POTIONITEM, 1), inputType);
		ItemStack inputSplash = PotionUtils.addPotionToItemStack(ItemHelper.cloneStack(Items.SPLASH_POTION, 1), inputType);
		ItemStack inputLingering = PotionUtils.addPotionToItemStack(ItemHelper.cloneStack(Items.LINGERING_POTION, 1), inputType);

		ItemStack outputPotion = PotionUtils.addPotionToItemStack(ItemHelper.cloneStack(Items.POTIONITEM, 1), outputType);
		ItemStack outputSplash = PotionUtils.addPotionToItemStack(ItemHelper.cloneStack(Items.SPLASH_POTION, 1), outputType);
		ItemStack outputLingering = PotionUtils.addPotionToItemStack(ItemHelper.cloneStack(Items.LINGERING_POTION, 1), outputType);

		addRecipe(DEFAULT_ENERGY, inputPotion, reagent, outputPotion);
		addRecipe(DEFAULT_ENERGY, inputSplash, reagent, outputSplash);
		addRecipe(DEFAULT_ENERGY, inputLingering, reagent, outputLingering);

		addRecipe(DEFAULT_ENERGY, inputPotion, GUNPOWDER, inputSplash);
		addRecipe(DEFAULT_ENERGY, inputSplash, DRAGON_BREATH, inputLingering);

		addRecipe(DEFAULT_ENERGY, outputPotion, GUNPOWDER, outputSplash);
		addRecipe(DEFAULT_ENERGY, outputSplash, DRAGON_BREATH, outputLingering);
	}

	/* REMOVE RECIPES */
	public static BrewerRecipe removeRecipe(ItemStack primaryInput, ItemStack secondaryInput) {

		return recipeMap.remove(Arrays.asList(new ComparableItemStackBrewer(primaryInput), new ComparableItemStackBrewer(secondaryInput)));
	}

	/* HELPERS */
	private static void addContainer(ItemStack potion) {

		// lockSet.add(new ComparableItemStackBrewer(potion));
	}

	/* RECIPE CLASS */
	public static class BrewerRecipe {

		final ItemStack primaryInput;
		final ItemStack secondaryInput;
		final ItemStack output;
		final int energy;

		BrewerRecipe(ItemStack primaryInput, ItemStack secondaryInput, ItemStack output, int energy) {

			this.primaryInput = primaryInput;
			this.secondaryInput = secondaryInput;
			this.output = output;
			this.energy = energy;
		}

		public ItemStack getPrimaryInput() {

			return primaryInput;
		}

		public ItemStack getSecondaryInput() {

			return secondaryInput;
		}

		public ItemStack getOutput() {

			return output;
		}

		public int getEnergy() {

			return energy;
		}
	}

	/* ITEMSTACK CLASS */
	public static class ComparableItemStackBrewer extends ComparableItemStackNBT {

		public ComparableItemStackBrewer(ItemStack stack) {

			super(stack);
			oreID = -1;
		}
	}

}
