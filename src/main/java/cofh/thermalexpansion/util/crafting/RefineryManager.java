package cofh.thermalexpansion.util.crafting;

import cofh.thermalfoundation.init.TFFluids;
import gnu.trove.map.hash.TIntObjectHashMap;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

public class RefineryManager {

	private static TIntObjectHashMap<RecipeRefinery> recipeMap = new TIntObjectHashMap<RecipeRefinery>();

	static final int DEFAULT_ENERGY = 2000;

	public static RecipeRefinery getRecipe(FluidStack input) {

		return input == null || input.getFluid() == null ? null : recipeMap.get(input.getFluid().hashCode());
	}

	public static boolean recipeExists(FluidStack input) {

		return getRecipe(input) != null;
	}

	public static RecipeRefinery[] getRecipeList() {

		return (RecipeRefinery[]) recipeMap.values();
	}

	public static void addDefaultRecipes() {

		addRecipe(4000, new FluidStack(TFFluids.fluidGlowstone, 50), new FluidStack(FluidRegistry.WATER, 50), new ItemStack(Items.GLOWSTONE_DUST));
	}

	public static void refreshRecipes() {

		TIntObjectHashMap<RecipeRefinery> tempMap = new TIntObjectHashMap<RecipeRefinery>(recipeMap.size());

		for (RecipeRefinery recipe : (RecipeRefinery[]) recipeMap.values()) {
			tempMap.put(recipe.input.hashCode(), recipe);
		}
		recipeMap.clear();
		recipeMap = tempMap;
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
