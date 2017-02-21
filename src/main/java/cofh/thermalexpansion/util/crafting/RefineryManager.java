package cofh.thermalexpansion.util.crafting;

import cofh.thermalfoundation.init.TFFluids;
import cofh.thermalfoundation.item.ItemMaterial;
import gnu.trove.map.hash.TIntObjectHashMap;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

public class RefineryManager {

	private static TIntObjectHashMap<RecipeRefinery> recipeMap = new TIntObjectHashMap<RecipeRefinery>();

	static final int DEFAULT_ENERGY = 5000;

	public static RecipeRefinery getRecipe(FluidStack input) {

		return input == null || input.getFluid() == null ? null : recipeMap.get(input.getFluid().hashCode());
	}

	public static boolean recipeExists(FluidStack input) {

		return getRecipe(input) != null;
	}

	public static RecipeRefinery[] getRecipeList() {

		return recipeMap.values(new RecipeRefinery[recipeMap.size()]);
	}

	public static void addDefaultRecipes() {

		int energy = DEFAULT_ENERGY;
		addRecipe(energy, new FluidStack(TFFluids.fluidCoal, 200), new FluidStack(TFFluids.fluidRefinedOil, 100), ItemMaterial.dustSulfur);
		addRecipe(energy, new FluidStack(TFFluids.fluidCrudeOil, 100), new FluidStack(TFFluids.fluidRefinedOil, 100), ItemMaterial.dustSulfur);
		addRecipe(energy, new FluidStack(TFFluids.fluidRefinedOil, 100), new FluidStack(TFFluids.fluidFuel, 100), ItemMaterial.dustSulfur);

		energy = DEFAULT_ENERGY / 2;
		addRecipe(energy, new FluidStack(TFFluids.fluidResin, 100), new FluidStack(TFFluids.fluidTreeOil, 50), ItemMaterial.globRosin);
	}

	public static void loadRecipes() {

	}

	/* ADD RECIPES */
	public static boolean addRecipe(int energy, FluidStack input, FluidStack outputFluid, ItemStack outputItem) {

		if (input == null || input.getFluid() == null || outputFluid == null || outputFluid.getFluid() == null || energy <= 0 || recipeExists(input)) {
			return false;
		}
		RecipeRefinery recipe = new RecipeRefinery(input, outputFluid, outputItem, energy);
		recipeMap.put(input.getFluid().hashCode(), recipe);
		return true;
	}

	/* REMOVE RECIPES */
	public static boolean removeRecipe(FluidStack input) {

		if (input == null || input.getFluid() == null) {
			return false;
		}
		return recipeMap.remove(input.getFluid().hashCode()) != null;
	}

	/* HELPERS */

	/* RECIPE CLASS */
	public static class RecipeRefinery {

		final FluidStack input;
		final FluidStack outputFluid;
		final ItemStack outputItem;
		final int energy;

		RecipeRefinery(FluidStack input, FluidStack outputFluid, ItemStack outputItem, int energy) {

			this.input = input;
			this.outputFluid = outputFluid;
			this.outputItem = outputItem;
			this.energy = energy;
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
	}

}
