package cofh.thermalexpansion.util.managers.machine;

import cofh.thermalfoundation.init.TFFluids;
import cofh.thermalfoundation.item.ItemMaterial;
import gnu.trove.map.hash.TIntObjectHashMap;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

public class RefineryManager {

	private static TIntObjectHashMap<RefineryRecipe> recipeMap = new TIntObjectHashMap<>();

	public static final int DEFAULT_ENERGY = 5000;

	public static RefineryRecipe getRecipe(FluidStack input) {

		return input == null ? null : recipeMap.get(input.getFluid().getName().hashCode());
	}

	public static boolean recipeExists(FluidStack input) {

		return getRecipe(input) != null;
	}

	public static RefineryRecipe[] getRecipeList() {

		return recipeMap.values(new RefineryRecipe[recipeMap.size()]);
	}

	public static void initialize() {

		int energy = DEFAULT_ENERGY;
		addRecipe(energy, new FluidStack(TFFluids.fluidCoal, 100), new FluidStack(TFFluids.fluidRefinedOil, 50), ItemMaterial.globTar);
		addRecipe(energy, new FluidStack(TFFluids.fluidCrudeOil, 100), new FluidStack(TFFluids.fluidRefinedOil, 100), ItemMaterial.globTar);
		addRecipe(energy, new FluidStack(TFFluids.fluidRefinedOil, 100), new FluidStack(TFFluids.fluidFuel, 100), ItemMaterial.dustSulfur);

		energy = DEFAULT_ENERGY / 2;
		addRecipe(energy, new FluidStack(TFFluids.fluidResin, 100), new FluidStack(TFFluids.fluidTreeOil, 50), ItemMaterial.globRosin);

		/* LOAD RECIPES */
		loadRecipes();
	}

	public static void loadRecipes() {

		/* IMMERSIVE PETROLEUM */
		{
			Fluid oil = FluidRegistry.getFluid("oil");
			if (oil != null) {
				addRecipe(DEFAULT_ENERGY, new FluidStack(oil, 100), new FluidStack(TFFluids.fluidRefinedOil, 100), ItemMaterial.globTar);
			}
		}

		/* INDUSTRIALCRAFT 2 */
		{
			Fluid biomass = FluidRegistry.getFluid("ic2biomass");
			Fluid biogas = FluidRegistry.getFluid("ic2biogas");
			if (biomass != null && biogas != null) {
				addRecipe(DEFAULT_ENERGY / 2, new FluidStack(biomass, 10), new FluidStack(biogas, 200));
			}
		}
	}

	public static void refresh() {

	}

	/* ADD RECIPES */
	public static RefineryRecipe addRecipe(int energy, FluidStack input, FluidStack outputFluid, ItemStack outputItem) {

		if (input == null || outputFluid == null || energy <= 0 || recipeExists(input)) {
			return null;
		}
		RefineryRecipe recipe = new RefineryRecipe(input, outputFluid, outputItem, energy);
		recipeMap.put(input.getFluid().getName().hashCode(), recipe);
		return recipe;
	}

	public static RefineryRecipe addRecipe(int energy, FluidStack input, FluidStack outputFluid) {

		return addRecipe(energy, input, outputFluid, ItemStack.EMPTY);
	}

	/* REMOVE RECIPES */
	public static RefineryRecipe removeRecipe(FluidStack input) {

		if (input == null) {
			return null;
		}
		return recipeMap.remove(input.getFluid().getName().hashCode());
	}

	/* HELPERS */

	/* RECIPE CLASS */
	public static class RefineryRecipe {

		final FluidStack input;
		final FluidStack outputFluid;
		final ItemStack outputItem;
		final int energy;

		RefineryRecipe(FluidStack input, FluidStack outputFluid, ItemStack outputItem, int energy) {

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
