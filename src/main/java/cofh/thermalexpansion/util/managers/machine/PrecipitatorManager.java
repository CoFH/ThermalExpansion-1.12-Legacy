package cofh.thermalexpansion.util.managers.machine;

import gnu.trove.map.hash.TIntObjectHashMap;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import java.util.List;

public class PrecipitatorManager {

	private static TIntObjectHashMap<List<PrecipitatorRecipe>> recipeMap = new TIntObjectHashMap<>();

	public static final int DEFAULT_ENERGY = 800;

	public static void initialize() {

		/* LOAD RECIPES */
		loadRecipes();
	}

	public static void loadRecipes() {

	}

	public static void refresh() {

	}

	/* RECIPE CLASS */
	public static class PrecipitatorRecipe {

		final FluidStack input;
		final ItemStack output;
		final int energy;

		PrecipitatorRecipe(FluidStack input, ItemStack output, int energy) {

			this.input = input;
			this.output = output;
			this.energy = energy;
		}

		public FluidStack getInput() {

			return input;
		}

		public ItemStack getOutput() {

			return output;
		}

		public int getEnergy() {

			return energy;
		}
	}

}
