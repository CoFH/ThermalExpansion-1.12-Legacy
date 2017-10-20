package cofh.thermalexpansion.util.managers.machine;

import cofh.core.init.CoreProps;
import cofh.core.util.helpers.FluidHelper;
import cofh.thermalfoundation.init.TFFluids;
import cofh.thermalfoundation.item.ItemMaterial;
import gnu.trove.map.hash.TIntObjectHashMap;
import gnu.trove.set.hash.THashSet;
import net.minecraft.init.PotionTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import java.util.Set;

public class RefineryManager {

	private static TIntObjectHashMap<RefineryRecipe> recipeMap = new TIntObjectHashMap<>();
	private static TIntObjectHashMap<RefineryRecipe> recipeMapPotion = new TIntObjectHashMap<>();
	private static Set<String> oilFluids = new THashSet<>();

	public static final int DEFAULT_ENERGY = 5000;

	public static RefineryRecipe getRecipe(FluidStack input) {

		return input == null ? null : recipeMap.get(FluidHelper.getFluidHash(input));
	}

	public static RefineryRecipe getRecipePotion(FluidStack input) {

		return input == null ? null : recipeMapPotion.get(FluidHelper.getFluidHash(input));
	}

	public static boolean recipeExists(FluidStack input) {

		return getRecipe(input) != null;
	}

	public static boolean recipeExistsPotion(FluidStack input) {

		return getRecipePotion(input) != null;
	}

	public static RefineryRecipe[] getRecipeList() {

		return recipeMap.values(new RefineryRecipe[recipeMap.size()]);
	}

	public static RefineryRecipe[] getRecipeListPotion() {

		return recipeMapPotion.values(new RefineryRecipe[recipeMapPotion.size()]);
	}

	public static boolean isFossilFuel(FluidStack fluid) {

		return fluid != null && oilFluids.contains(fluid.getFluid().getName());
	}

	public static void initialize() {

		int energy = DEFAULT_ENERGY;

		/* FOSSIL FUELS */
		{
			addFossilFuel(TFFluids.fluidCoal);
			addFossilFuel(TFFluids.fluidCrudeOil);
			addFossilFuel(TFFluids.fluidRefinedOil);

			addRecipe(energy, new FluidStack(TFFluids.fluidCoal, 200), new FluidStack(TFFluids.fluidRefinedOil, 100), ItemMaterial.globTar, 75);
			addRecipe(energy, new FluidStack(TFFluids.fluidCrudeOil, 200), new FluidStack(TFFluids.fluidRefinedOil, 150), ItemMaterial.globTar, 50);
			addRecipe(energy, new FluidStack(TFFluids.fluidRefinedOil, 150), new FluidStack(TFFluids.fluidFuel, 100), ItemMaterial.dustSulfur, 50);
		}

		addRecipe(energy, new FluidStack(TFFluids.fluidResin, 100), new FluidStack(TFFluids.fluidTreeOil, 50), ItemMaterial.globRosin, 75);

		addStrongPotionRecipes("leaping", CoreProps.POTION_MAX);
		addStrongPotionRecipes("swiftness", CoreProps.POTION_MAX);
		addStrongPotionRecipes("healing", CoreProps.POTION_MAX);
		addStrongPotionRecipes("harming", CoreProps.POTION_MAX);
		addStrongPotionRecipes("poison", CoreProps.POTION_MAX);
		addStrongPotionRecipes("regeneration", CoreProps.POTION_MAX);
		addStrongPotionRecipes("strength", CoreProps.POTION_MAX);

		addStrongPotionRecipes("haste", 4);
		addStrongPotionRecipes("resistance", CoreProps.POTION_MAX);
		addStrongPotionRecipes("absorption", CoreProps.POTION_MAX);
		addStrongPotionRecipes("wither", CoreProps.POTION_MAX);

		/* LOAD RECIPES */
		loadRecipes();
	}

	public static void loadRecipes() {

		/* IMMERSIVE PETROLEUM */
		{
			Fluid oil = FluidRegistry.getFluid("oil");
			if (oil != null) {
				addFossilFuel(oil);
				addRecipe(DEFAULT_ENERGY, new FluidStack(oil, 200), new FluidStack(TFFluids.fluidRefinedOil, 150), ItemMaterial.globTar, 50);
			}
		}
	}

	public static void refresh() {

	}

	/* ADD RECIPES */
	public static RefineryRecipe addRecipe(int energy, FluidStack input, FluidStack outputFluid, ItemStack outputItem, int chance) {

		if (input == null || outputFluid == null || energy <= 0 || recipeExists(input)) {
			return null;
		}
		RefineryRecipe recipe = new RefineryRecipe(input, outputFluid, outputItem, energy, chance);
		recipeMap.put(FluidHelper.getFluidHash(input), recipe);
		return recipe;
	}

	public static RefineryRecipe addRecipe(int energy, FluidStack input, FluidStack outputFluid, ItemStack outputItem) {

		return addRecipe(energy, input, outputFluid, outputItem, 100);
	}

	public static RefineryRecipe addRecipe(int energy, FluidStack input, FluidStack outputFluid) {

		return addRecipe(energy, input, outputFluid, ItemStack.EMPTY, 0);
	}

	public static RefineryRecipe addRecipePotion(int energy, FluidStack input, FluidStack outputFluid) {

		if (input == null || outputFluid == null || energy <= 0 || recipeExistsPotion(input)) {
			return null;
		}
		RefineryRecipe recipe = new RefineryRecipe(input, outputFluid, null, energy, 0);
		recipeMapPotion.put(FluidHelper.getFluidHash(input), recipe);
		return recipe;
	}

	/* REMOVE RECIPES */
	public static RefineryRecipe removeRecipe(FluidStack input) {

		if (input == null) {
			return null;
		}
		return recipeMap.remove(FluidHelper.getFluidHash(input));
	}

	public static RefineryRecipe removeRecipePotion(FluidStack input) {

		if (input == null) {
			return null;
		}
		return recipeMapPotion.remove(FluidHelper.getFluidHash(input));
	}

	/* HELPERS */
	private static void addFossilFuel(Fluid fluid) {

		if (fluid == null) {
			return;
		}
		oilFluids.add(fluid.getName());
	}

	public static void addStrongPotionRecipes(String baseName, int maxRank) {

		int baseAmount = CoreProps.BOTTLE_VOLUME / 5;
		int inputAmount;
		int outputAmount;

		for (int i = maxRank; i > 2; i--) {
			outputAmount = baseAmount + baseAmount * (CoreProps.POTION_MAX - i);
			inputAmount = outputAmount + baseAmount;

			PotionType inputType = getPotionType(baseName, i - 1);
			PotionType outputType = getPotionType(baseName, i);

			if (inputType == PotionTypes.EMPTY || outputType == PotionTypes.EMPTY) {
				continue;
			}
			FluidStack inputPotion = addPotionToFluidStack(new FluidStack(TFFluids.fluidPotion, inputAmount), inputType);
			FluidStack outputPotion = addPotionToFluidStack(new FluidStack(TFFluids.fluidPotion, outputAmount), outputType);
			addRecipePotion(DEFAULT_ENERGY / 2, inputPotion, outputPotion);

			inputPotion = addPotionToFluidStack(new FluidStack(TFFluids.fluidPotionSplash, inputAmount), inputType);
			outputPotion = addPotionToFluidStack(new FluidStack(TFFluids.fluidPotionSplash, outputAmount), outputType);
			addRecipePotion(DEFAULT_ENERGY / 2, inputPotion, outputPotion);

			inputPotion = addPotionToFluidStack(new FluidStack(TFFluids.fluidPotionLingering, inputAmount), inputType);
			outputPotion = addPotionToFluidStack(new FluidStack(TFFluids.fluidPotionLingering, outputAmount), outputType);
			addRecipePotion(DEFAULT_ENERGY / 2, inputPotion, outputPotion);
		}
	}

	public static PotionType getPotionType(String baseName, int rank) {

		PotionType ret;

		switch (rank) {
			case 1:
				ret = PotionType.getPotionTypeForName(baseName);
				break;
			case 2:
				ret = PotionType.getPotionTypeForName("cofhcore:" + baseName + 2);
				if (ret == PotionTypes.EMPTY) { // Vanilla Potion
					ret = PotionType.getPotionTypeForName("strong_" + baseName);
				}
				break;
			default:
				ret = PotionType.getPotionTypeForName("cofhcore:" + baseName + rank);
		}
		return ret;
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
	public static class RefineryRecipe {

		final FluidStack input;
		final FluidStack outputFluid;
		final ItemStack outputItem;
		final int energy;
		final int chance;

		RefineryRecipe(FluidStack input, FluidStack outputFluid, ItemStack outputItem, int energy, int chance) {

			this.input = input;
			this.outputFluid = outputFluid;
			this.outputItem = outputItem;
			this.energy = energy;
			this.chance = chance;
		}

		public FluidStack getInput() {

			return input;
		}

		public FluidStack getOutputFluid() {

			return outputFluid;
		}

		public ItemStack getOutputItem() {

			return outputItem;
		}

		public int getEnergy() {

			return energy;
		}

		public int getChance() {

			return chance;
		}

	}

}
