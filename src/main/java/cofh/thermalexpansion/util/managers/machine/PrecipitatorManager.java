package cofh.thermalexpansion.util.managers.machine;

import cofh.core.util.ItemWrapper;
import gnu.trove.map.hash.THashMap;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import java.util.Map;
import java.util.Map.Entry;

public class PrecipitatorManager {

	private static Map<ItemWrapper, PrecipitatorRecipe> recipeMap = new THashMap<>();

	public static final int DEFAULT_ENERGY = 800;

	public static PrecipitatorRecipe getRecipe(ItemStack input) {

		return input.isEmpty() ? null : recipeMap.get(new ItemWrapper(input));
	}

	public static boolean recipeExists(ItemStack input) {

		return getRecipe(input) != null;
	}

	public static PrecipitatorRecipe[] getRecipeList() {

		return recipeMap.values().toArray(new PrecipitatorRecipe[recipeMap.size()]);
	}

	public static void initialize() {

		addRecipe(DEFAULT_ENERGY, new ItemStack(Items.SNOWBALL, 4, 0), new FluidStack(FluidRegistry.WATER, Fluid.BUCKET_VOLUME / 2));
		addRecipe(DEFAULT_ENERGY, new ItemStack(Blocks.SNOW_LAYER, 2, 0), new FluidStack(FluidRegistry.WATER, Fluid.BUCKET_VOLUME / 2));
		addRecipe(DEFAULT_ENERGY * 2, new ItemStack(Blocks.ICE), new FluidStack(FluidRegistry.WATER, Fluid.BUCKET_VOLUME));

		/* LOAD RECIPES */
		loadRecipes();
	}

	public static void loadRecipes() {

	}

	public static void refresh() {

		Map<ItemWrapper, PrecipitatorRecipe> tempMap = new THashMap<>(recipeMap.size());
		PrecipitatorRecipe tempRecipe;

		for (Entry<ItemWrapper, PrecipitatorRecipe> entry : recipeMap.entrySet()) {
			tempRecipe = entry.getValue();
			ItemWrapper output = new ItemWrapper(tempRecipe.output);
			tempMap.put(output, tempRecipe);
		}
		recipeMap.clear();
		recipeMap = tempMap;
	}

	/* ADD RECIPES */
	public static PrecipitatorRecipe addRecipe(int energy, ItemStack output, FluidStack input) {

		if (output.isEmpty() || input == null || energy <= 0 || recipeExists(output)) {
			return null;
		}
		PrecipitatorRecipe recipe = new PrecipitatorRecipe(output, input, energy);
		recipeMap.put(new ItemWrapper(output), recipe);
		return recipe;
	}

	/* REMOVE RECIPES */
	public static PrecipitatorRecipe removeRecipe(ItemStack output) {

		return recipeMap.remove(new ItemWrapper(output));
	}

	/* RECIPE CLASS */
	public static class PrecipitatorRecipe {

		final ItemStack output;
		final FluidStack input;
		final int energy;

		PrecipitatorRecipe(ItemStack output, FluidStack input, int energy) {

			this.output = output;
			this.input = input;
			this.energy = energy;
		}

		public ItemStack getOutput() {

			return output;
		}

		public FluidStack getInput() {

			return input;
		}

		public int getEnergy() {

			return energy;
		}
	}

}
