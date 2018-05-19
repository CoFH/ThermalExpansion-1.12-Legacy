package cofh.thermalexpansion.util.managers.machine;

import cofh.core.init.CoreProps;
import cofh.core.util.helpers.FluidHelper;
import cofh.thermalfoundation.init.TFFluids;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.init.PotionTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionType;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import java.util.Set;

public class RefineryManager {

	private static Int2ObjectOpenHashMap<RefineryRecipe> recipeMap = new Int2ObjectOpenHashMap<>();
	private static Int2ObjectOpenHashMap<RefineryRecipe> recipeMapPotion = new Int2ObjectOpenHashMap<>();
	private static Set<String> oilFluids = new ObjectOpenHashSet<>();

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

		return recipeMap.values().toArray(new RefineryRecipe[0]);
	}

	public static RefineryRecipe[] getRecipeListPotion() {

		return recipeMapPotion.values().toArray(new RefineryRecipe[0]);
	}

	public static boolean isFossilFuel(FluidStack fluid) {

		return fluid != null && oilFluids.contains(fluid.getFluid().getName());
	}

	public static void initialize() {

		int min = 2;
		int max = 4;

		addStrongPotionRecipes("leaping", min, max);
		addStrongPotionRecipes("swiftness", min, max);
		addStrongPotionRecipes("healing", min, max);
		addStrongPotionRecipes("harming", min, max);
		addStrongPotionRecipes("poison", min, max);
		addStrongPotionRecipes("regeneration", min, max);
		addStrongPotionRecipes("strength", min, max);

		addStrongPotionRecipes("haste", min, max);
		addStrongPotionRecipes("resistance", min, max);
		addStrongPotionRecipes("absorption", min, max);
		addStrongPotionRecipes("luck", min, max);
		addStrongPotionRecipes("unluck", min, max);
		addStrongPotionRecipes("wither", min, max);

		min = 1;
		max = 3;

		addStrongPotionRecipes("leaping", min, max, "+");
		addStrongPotionRecipes("swiftness", min, max, "+");
		addStrongPotionRecipes("poison", min, max, "+");
		addStrongPotionRecipes("regeneration", min, max, "+");
		addStrongPotionRecipes("strength", min, max, "+");

		addStrongPotionRecipes("haste", min, max, "+");
		addStrongPotionRecipes("resistance", min, max, "+");
		addStrongPotionRecipes("absorption", min, max, "+");
		addStrongPotionRecipes("luck", min, max, "+");
		addStrongPotionRecipes("unluck", min, max, "+");
		addStrongPotionRecipes("wither", min, max, "+");
	}

	public static void refresh() {

	}

	/* ADD RECIPES */
	public static RefineryRecipe addRecipe(int energy, FluidStack input, FluidStack outputFluid, ItemStack outputItem, int chance) {

		if (input == null || outputFluid == null || energy <= 0 || recipeExists(input)) {
			return null;
		}
		if (outputItem.isEmpty()) {
			chance = 0;
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
		RefineryRecipe recipe = new RefineryRecipe(input, outputFluid, ItemStack.EMPTY, energy, 0);
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
	public static void addFossilFuel(Fluid fluid) {

		if (!FluidRegistry.isFluidRegistered(fluid)) {
			return;
		}
		oilFluids.add(fluid.getName());
	}

	public static void addStrongPotionRecipes(String baseName, int minRank, int maxRank) {

		addStrongPotionRecipes(baseName, minRank, maxRank, "");
	}

	public static void addStrongPotionRecipes(String baseName, int minRank, int maxRank, String postfix) {

		int baseAmount = CoreProps.BOTTLE_VOLUME / 5;
		int inputAmount;
		int outputAmount;

		for (int i = maxRank; i > minRank; i--) {
			outputAmount = baseAmount * (5 + minRank - i);
			inputAmount = outputAmount + baseAmount;

			PotionType inputType = getPotionType(baseName, i - 1, postfix);
			PotionType outputType = getPotionType(baseName, i, postfix);

			if (inputType == PotionTypes.EMPTY || outputType == PotionTypes.EMPTY) {
				continue;
			}
			FluidStack inputPotion = TFFluids.addPotionToFluidStack(new FluidStack(TFFluids.fluidPotion, inputAmount), inputType);
			FluidStack outputPotion = TFFluids.addPotionToFluidStack(new FluidStack(TFFluids.fluidPotion, outputAmount), outputType);
			addRecipePotion(DEFAULT_ENERGY / 2, inputPotion, outputPotion);

			inputPotion = TFFluids.addPotionToFluidStack(new FluidStack(TFFluids.fluidPotionSplash, inputAmount), inputType);
			outputPotion = TFFluids.addPotionToFluidStack(new FluidStack(TFFluids.fluidPotionSplash, outputAmount), outputType);
			addRecipePotion(DEFAULT_ENERGY / 2, inputPotion, outputPotion);

			inputPotion = TFFluids.addPotionToFluidStack(new FluidStack(TFFluids.fluidPotionLingering, inputAmount), inputType);
			outputPotion = TFFluids.addPotionToFluidStack(new FluidStack(TFFluids.fluidPotionLingering, outputAmount), outputType);
			addRecipePotion(DEFAULT_ENERGY / 2, inputPotion, outputPotion);
		}
	}

	public static PotionType getPotionType(String baseName, int rank, String postfix) {

		PotionType ret;
		switch (rank) {
			case 1:
				ret = PotionType.getPotionTypeForName("cofhcore:" + baseName + postfix);
				if (ret == PotionTypes.EMPTY) { // Vanilla Potion
					ret = PotionType.getPotionTypeForName("long_" + baseName);
				}
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
