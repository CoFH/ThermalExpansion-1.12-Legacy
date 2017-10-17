package cofh.thermalexpansion.util.managers.machine;

import cofh.core.init.CorePotions;
import cofh.core.init.CoreProps;
import cofh.core.inventory.ComparableItemStackNBT;
import cofh.core.util.helpers.FluidHelper;
import cofh.core.util.helpers.ItemHelper;
import cofh.core.util.oredict.OreDictionaryArbiter;
import cofh.thermalfoundation.init.TFFluids;
import cofh.thermalfoundation.item.ItemMaterial;
import gnu.trove.map.hash.THashMap;
import gnu.trove.set.hash.THashSet;
import net.minecraft.init.Items;
import net.minecraft.init.PotionTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import java.util.*;
import java.util.Map.Entry;

public class BrewerManager {

	private static Map<List<Integer>, BrewerRecipe> recipeMap = new THashMap<>();
	private static Set<ComparableItemStackBrewer> validationSet = new THashSet<>();
	private static Set<String> validationFluids = new THashSet<>();

	public static final int DEFAULT_ENERGY = 1600;
	public static final int DEFAULT_AMOUNT = CoreProps.BOTTLE_VOLUME * 3;

	public static BrewerRecipe getRecipe(ItemStack input, FluidStack fluid) {

		return input.isEmpty() || fluid == null ? null : recipeMap.get(Arrays.asList(new ComparableItemStackBrewer(input).hashCode(), FluidHelper.getFluidHash(fluid)));
	}

	public static boolean recipeExists(ItemStack input, FluidStack fluid) {

		return getRecipe(input, fluid) != null;
	}

	public static BrewerRecipe[] getRecipeList() {

		return recipeMap.values().toArray(new BrewerRecipe[recipeMap.size()]);
	}

	public static boolean isItemValid(ItemStack input) {

		return !input.isEmpty() && validationSet.contains(new ComparableItemStackBrewer(input));
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
		}

		/* COFH */
		{
			addDefaultPotionRecipes(PotionTypes.WATER, ItemMaterial.dustBasalz, CorePotions.haste);
			addDefaultPotionRecipes(PotionTypes.WATER, ItemMaterial.dustObsidian, CorePotions.resistance);
			addDefaultPotionRecipes(PotionTypes.WATER, ItemMaterial.dustBlitz, CorePotions.levitation);
			addDefaultPotionRecipes(PotionTypes.WATER, ItemMaterial.dustBlizz, CorePotions.absorption);
			addDefaultPotionRecipes(PotionTypes.WATER, new ItemStack(Items.POISONOUS_POTATO), CorePotions.saturation);
		}

		/* LOAD RECIPES */
		loadRecipes();
	}

	public static void loadRecipes() {

	}

	public static void refresh() {

		Map<List<Integer>, BrewerRecipe> tempMap = new THashMap<>(recipeMap.size());
		Set<ComparableItemStackBrewer> tempSet = new THashSet<>();
		BrewerRecipe tempRecipe;

		for (Entry<List<Integer>, BrewerRecipe> entry : recipeMap.entrySet()) {
			tempRecipe = entry.getValue();
			ComparableItemStackBrewer input = new ComparableItemStackBrewer(tempRecipe.input);
			tempMap.put(Arrays.asList(input.hashCode(), FluidHelper.getFluidHash(tempRecipe.inputFluid)), tempRecipe);
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
		recipeMap.put(Arrays.asList(new ComparableItemStackBrewer(input).hashCode(), FluidHelper.getFluidHash(inputFluid)), recipe);
		validationSet.add(new ComparableItemStackBrewer(input));
		validationFluids.add(inputFluid.getFluid().getName());
		return recipe;
	}

	/* REMOVE RECIPES */
	public static BrewerRecipe removeRecipe(ItemStack input, FluidStack fluid) {

		return recipeMap.remove(Arrays.asList(new ComparableItemStackBrewer(input).hashCode(), FluidHelper.getFluidHash(fluid)));
	}

	/* HELPERS */
	public static void addDefaultPotionRecipes(PotionType input, ItemStack reagent, PotionType output) {

		addRecipe(DEFAULT_ENERGY, reagent, getPotion(DEFAULT_AMOUNT, input), getPotion(DEFAULT_AMOUNT, output));
		addRecipe(DEFAULT_ENERGY, reagent, getSplashPotion(DEFAULT_AMOUNT, input), getSplashPotion(DEFAULT_AMOUNT, output));
		addRecipe(DEFAULT_ENERGY, reagent, getLingeringPotion(DEFAULT_AMOUNT, input), getLingeringPotion(DEFAULT_AMOUNT, output));

		addRecipe(DEFAULT_ENERGY, new ItemStack(Items.GUNPOWDER), getPotion(DEFAULT_AMOUNT, input), getSplashPotion(DEFAULT_AMOUNT, input));
		addRecipe(DEFAULT_ENERGY, new ItemStack(Items.DRAGON_BREATH), getSplashPotion(DEFAULT_AMOUNT, input), getLingeringPotion(DEFAULT_AMOUNT, input));

		addRecipe(DEFAULT_ENERGY, new ItemStack(Items.GUNPOWDER), getPotion(DEFAULT_AMOUNT, output), getSplashPotion(DEFAULT_AMOUNT, output));
		addRecipe(DEFAULT_ENERGY, new ItemStack(Items.DRAGON_BREATH), getSplashPotion(DEFAULT_AMOUNT, output), getLingeringPotion(DEFAULT_AMOUNT, output));
	}

	public static FluidStack getPotion(int amount, PotionType type) {

		if (type == PotionTypes.WATER) {
			return new FluidStack(FluidRegistry.WATER, amount);
		}
		return addPotionToFluidStack(new FluidStack(TFFluids.fluidPotion, amount), type);
	}

	public static FluidStack getSplashPotion(int amount, PotionType type) {

		return addPotionToFluidStack(new FluidStack(TFFluids.fluidPotionSplash, amount), type);
	}

	public static FluidStack getLingeringPotion(int amount, PotionType type) {

		return addPotionToFluidStack(new FluidStack(TFFluids.fluidPotionLingering, amount), type);
	}

	public static FluidStack addPotionToFluidStack(FluidStack stack, PotionType type) {

		ResourceLocation resourcelocation = PotionType.REGISTRY.getNameForObject(type);

		if (type == PotionTypes.EMPTY) {
			if (stack.tag != null) {
				stack.tag.removeTag("Potion");
				if (stack.tag.hasNoTags()) {
					stack.tag = null;
				}
			}
		} else {
			if (stack.tag == null) {
				stack.tag = new NBTTagCompound();
			}
			stack.tag.setString("Potion", resourcelocation.toString());
		}
		return stack;
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

	/* ITEMSTACK CLASS */
	public static class ComparableItemStackBrewer extends ComparableItemStackNBT {

		public static final String DUST = "dust";
		public static final String NUGGET = "nugget";

		public static boolean safeOreType(String oreName) {

			return oreName.startsWith(DUST) || oreName.startsWith(NUGGET);
		}

		public static int getOreID(ItemStack stack) {

			ArrayList<Integer> ids = OreDictionaryArbiter.getAllOreIDs(stack);

			if (ids != null) {
				for (Integer id : ids) {
					if (id != -1 && safeOreType(ItemHelper.oreProxy.getOreName(id))) {
						return id;
					}
				}
			}
			return -1;
		}

		public ComparableItemStackBrewer(ItemStack stack) {

			super(stack);
			oreID = getOreID(stack);
		}
	}

}
