package cofh.thermalexpansion.util.crafting;

import cofh.thermalexpansion.ThermalExpansion;

import gnu.trove.map.hash.THashMap;

import java.util.Map;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

public class PrecipitatorManager { // TODO

	private static Map<Fluid, RecipePrecipitator> recipeMap = new THashMap<Fluid, RecipePrecipitator>();
	@SuppressWarnings("unused")
	private static boolean allowOverwrite = false;

	static {
		allowOverwrite = ThermalExpansion.config.get("tweak.crafting", "Precipitator.AllowRecipeOverwrite", false);
	}

	public static RecipePrecipitator getRecipe(Fluid fluid) {

		return fluid == null ? null : recipeMap.get(fluid);
	}

	public static RecipePrecipitator getRecipe(FluidStack fluid) {

		return getRecipe(fluid.getFluid());
	}

	public static boolean recipeExists(Fluid fluid) {

		return getRecipe(fluid) != null;
	}

	public static boolean recipeExists(FluidStack fluid) {

		return recipeExists(fluid.getFluid());
	}

	public static RecipePrecipitator[] getRecipeList() {

		return recipeMap.values().toArray(new RecipePrecipitator[0]);
	}

	public static void addDefaultRecipes() {

	}

	public static void loadRecipes() {

	}

	public static void refreshRecipes() {

	}

	/* RECIPE CLASS */
	public static class RecipePrecipitator {

		final FluidStack[] fluid;
		final ItemStack[] outputs;
		final int[] energy;

		RecipePrecipitator(FluidStack[] fluid, ItemStack[] outputs, int[] energy) {

			this.fluid = fluid;
			this.outputs = outputs;
			this.energy = energy;
		}

		public FluidStack[] getInputs() {

			return fluid;
		}

		public ItemStack[] getOutputs() {

			return outputs;
		}

		public int[] getEnergy() {

			return energy;
		}
	}

}
