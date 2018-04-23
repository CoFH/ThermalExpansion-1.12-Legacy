package cofh.thermalexpansion.util.managers.machine;

import cofh.core.init.CorePotions;
import cofh.core.init.CoreProps;
import cofh.core.inventory.ComparableItemStack;
import cofh.core.inventory.ComparableItemStackValidatedNBT;
import cofh.core.inventory.OreValidator;
import cofh.core.util.helpers.FluidHelper;
import cofh.core.util.helpers.ItemHelper;
import cofh.thermalfoundation.init.TFFluids;
import cofh.thermalfoundation.item.ItemMaterial;
import gnu.trove.map.hash.THashMap;
import gnu.trove.set.hash.THashSet;
import net.minecraft.init.Items;
import net.minecraft.init.PotionTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionType;
import net.minecraftforge.fluids.FluidStack;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import static java.util.Arrays.asList;

public class BrewerManager {

	private static Map<List<Integer>, BrewerRecipe> recipeMap = new THashMap<>();
	private static Set<ComparableItemStackValidatedNBT> validationSet = new THashSet<>();
	private static Set<String> validationFluids = new THashSet<>();
	private static OreValidator oreValidator = new OreValidator();

	static {
		oreValidator.addPrefix(ComparableItemStack.DUST);
		oreValidator.addPrefix(ComparableItemStack.GEM);
	}

	public static final int DEFAULT_ENERGY = 2400;
	public static final int DEFAULT_AMOUNT = CoreProps.BOTTLE_VOLUME * 3;

	public static BrewerRecipe getRecipe(ItemStack input, FluidStack fluid) {

		return input.isEmpty() || fluid == null ? null : recipeMap.get(asList(convertInput(input).hashCode(), FluidHelper.getFluidHash(fluid)));
	}

	public static boolean recipeExists(ItemStack input, FluidStack fluid) {

		return getRecipe(input, fluid) != null;
	}

	public static BrewerRecipe[] getRecipeList() {

		return recipeMap.values().toArray(new BrewerRecipe[recipeMap.size()]);
	}

	public static boolean isItemValid(ItemStack input) {

		return !input.isEmpty() && validationSet.contains(convertInput(input));
	}

	public static boolean isFluidValid(FluidStack fluid) {

		return fluid != null && validationFluids.contains(fluid.getFluid().getName());
	}

	public static void initialize() {

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

		/* VANILLA */
		{
			addDefaultPotionRecipes(PotionTypes.WATER, new ItemStack(Items.NETHER_WART), PotionTypes.AWKWARD);

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
		}

		/* COFH */
		{
			addDefaultPotionRecipes(PotionTypes.AWKWARD, ItemMaterial.dustBasalz, CorePotions.haste);
			addDefaultPotionRecipes(PotionTypes.AWKWARD, ItemMaterial.dustObsidian, CorePotions.resistance);
			addDefaultPotionRecipes(PotionTypes.AWKWARD, ItemMaterial.dustBlitz, CorePotions.levitation);
			addDefaultPotionRecipes(PotionTypes.AWKWARD, ItemMaterial.dustBlizz, CorePotions.absorption);
			addDefaultPotionRecipes(PotionTypes.REGENERATION, fermentedSpiderEye, CorePotions.wither);

			addDefaultPotionRecipes(CorePotions.haste, redstone, CorePotions.hasteLong);
			addDefaultPotionRecipes(CorePotions.resistance, redstone, CorePotions.resistanceLong);
			addDefaultPotionRecipes(CorePotions.levitation, redstone, CorePotions.levitationLong);
			addDefaultPotionRecipes(CorePotions.absorption, redstone, CorePotions.absorptionLong);
			addDefaultPotionRecipes(CorePotions.luck, redstone, CorePotions.luckLong);
			addDefaultPotionRecipes(CorePotions.wither, redstone, CorePotions.witherLong);

			addDefaultPotionRecipes(CorePotions.haste, glowstone, CorePotions.hasteStrong);
			addDefaultPotionRecipes(CorePotions.resistance, glowstone, CorePotions.resistanceStrong);
			addDefaultPotionRecipes(CorePotions.absorption, glowstone, CorePotions.absorptionStrong);
			addDefaultPotionRecipes(CorePotions.luck, glowstone, CorePotions.luckStrong);
			addDefaultPotionRecipes(CorePotions.wither, glowstone, CorePotions.witherStrong);
		}

		/* SWAPS */
		{
			int max = CoreProps.POTION_MAX;

			addSwapPotionRecipes("leaping", max);
			addSwapPotionRecipes("swiftness", max);
			addSwapPotionRecipes("healing", max);
			addSwapPotionRecipes("harming", max);
			addSwapPotionRecipes("poison", max);
			addSwapPotionRecipes("regeneration", max);
			addSwapPotionRecipes("strength", max);

			addSwapPotionRecipes("haste", 4);
			addSwapPotionRecipes("resistance", 4);
			addSwapPotionRecipes("absorption", max);
			addSwapPotionRecipes("luck", max);
			addSwapPotionRecipes("unluck", max);
			addSwapPotionRecipes("wither", max);

			max = 3;

			addSwapPotionRecipes("leaping", max, "+");
			addSwapPotionRecipes("swiftness", max, "+");
			addSwapPotionRecipes("poison", max, "+");
			addSwapPotionRecipes("regeneration", max, "+");
			addSwapPotionRecipes("strength", max, "+");

			addSwapPotionRecipes("haste", max, "+");
			addSwapPotionRecipes("resistance", max, "+");
			addSwapPotionRecipes("absorption", max, "+");
			addSwapPotionRecipes("luck", max, "+");
			addSwapPotionRecipes("unluck", max, "+");
			addSwapPotionRecipes("wither", max, "+");
		}
	}

	public static void refresh() {

		Map<List<Integer>, BrewerRecipe> tempMap = new THashMap<>(recipeMap.size());
		Set<ComparableItemStackValidatedNBT> tempSet = new THashSet<>();
		BrewerRecipe tempRecipe;

		for (Entry<List<Integer>, BrewerRecipe> entry : recipeMap.entrySet()) {
			tempRecipe = entry.getValue();
			ComparableItemStackValidatedNBT input = convertInput(tempRecipe.input);
			tempMap.put(asList(input.hashCode(), FluidHelper.getFluidHash(tempRecipe.inputFluid)), tempRecipe);
			tempSet.add(input);
		}
		recipeMap.clear();
		recipeMap = tempMap;

		validationSet.clear();
		validationSet = tempSet;
	}

	/* ADD RECIPES */
	public static BrewerRecipe addRecipe(int energy, ItemStack input, FluidStack inputFluid, FluidStack outputFluid) {

		if (input.isEmpty() || inputFluid == null || outputFluid == null || energy <= 0 || recipeExists(input, inputFluid)) {
			return null;
		}
		BrewerRecipe recipe = new BrewerRecipe(input, inputFluid, outputFluid, energy);
		recipeMap.put(asList(convertInput(input).hashCode(), FluidHelper.getFluidHash(inputFluid)), recipe);
		validationSet.add(convertInput(input));
		validationFluids.add(inputFluid.getFluid().getName());
		return recipe;
	}

	/* REMOVE RECIPES */
	public static BrewerRecipe removeRecipe(ItemStack input, FluidStack fluid) {

		return recipeMap.remove(asList(convertInput(input).hashCode(), FluidHelper.getFluidHash(fluid)));
	}

	/* HELPERS */
	public static ComparableItemStackValidatedNBT convertInput(ItemStack stack) {

		return new ComparableItemStackValidatedNBT(stack, oreValidator);
	}

	public static void addDefaultPotionRecipes(PotionType input, ItemStack reagent, PotionType output) {

		addRecipe(DEFAULT_ENERGY, reagent, TFFluids.getPotion(DEFAULT_AMOUNT, input), TFFluids.getPotion(DEFAULT_AMOUNT, output));
		addRecipe(DEFAULT_ENERGY, reagent, TFFluids.getSplashPotion(DEFAULT_AMOUNT, input), TFFluids.getSplashPotion(DEFAULT_AMOUNT, output));
		addRecipe(DEFAULT_ENERGY, reagent, TFFluids.getLingeringPotion(DEFAULT_AMOUNT, input), TFFluids.getLingeringPotion(DEFAULT_AMOUNT, output));

		addSwapPotionRecipes(input);
		addSwapPotionRecipes(output);
	}

	public static void addSwapPotionRecipes(PotionType potion) {

		addRecipe(DEFAULT_ENERGY, new ItemStack(Items.GUNPOWDER), TFFluids.getPotion(DEFAULT_AMOUNT, potion), TFFluids.getSplashPotion(DEFAULT_AMOUNT, potion));
		addRecipe(DEFAULT_ENERGY, new ItemStack(Items.DRAGON_BREATH), TFFluids.getSplashPotion(DEFAULT_AMOUNT, potion), TFFluids.getLingeringPotion(DEFAULT_AMOUNT, potion));
	}

	public static void addSwapPotionRecipes(String baseName, int maxRank) {

		addSwapPotionRecipes(baseName, maxRank, "");
	}

	public static void addSwapPotionRecipes(String baseName, int maxRank, String postfix) {

		for (int i = maxRank; i > 2; i--) {
			PotionType type = getPotionType(baseName, i, postfix);

			System.out.println(type);

			if (type == PotionTypes.EMPTY) {
				continue;
			}
			addSwapPotionRecipes(type);
		}
	}

	public static PotionType getPotionType(String baseName, int rank, String postfix) {

		PotionType ret;
		switch (rank) {
			case 1:
				ret = PotionType.getPotionTypeForName(baseName);
				break;
			case 2:
				ret = PotionType.getPotionTypeForName("cofhcore:" + baseName + 2 + postfix);
				if (ret == PotionTypes.EMPTY) { // Vanilla Potion
					ret = PotionType.getPotionTypeForName("strong_" + baseName);
				}
				break;
			default:
				ret = PotionType.getPotionTypeForName("cofhcore:" + baseName + rank + postfix);
		}
		return ret;
	}

	/* RECIPE CLASS */
	public static class BrewerRecipe {

		final ItemStack input;
		final FluidStack inputFluid;
		final FluidStack outputFluid;
		final int energy;

		BrewerRecipe(ItemStack input, FluidStack inputFluid, FluidStack outputFluid, int energy) {

			this.input = input;
			this.inputFluid = inputFluid;
			this.outputFluid = outputFluid;
			this.energy = energy;
		}

		public ItemStack getInput() {

			return input;
		}

		public FluidStack getInputFluid() {

			return inputFluid;
		}

		public FluidStack getOutputFluid() {

			return outputFluid;
		}

		public int getEnergy() {

			return energy;
		}
	}

}
