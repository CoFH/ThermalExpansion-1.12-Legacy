package cofh.thermalexpansion.util.managers.machine;

import gnu.trove.map.hash.THashMap;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

import java.util.List;
import java.util.Map;

public class ExtruderManager {

	private static Map<List<Fluid>, List<ExtruderRecipe>> recipeMap = new THashMap<>();

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
	public static class ExtruderRecipe {

		final FluidStack inputHot;
		final FluidStack inputCold;
		final ItemStack output;
		final int energy;

		ExtruderRecipe(FluidStack inputHot, FluidStack inputCold, ItemStack output, int energy) {

			this.inputHot = inputHot;
			this.inputCold = inputCold;
			this.output = output;
			this.energy = energy;
		}

		public FluidStack getInputHot() {

			return inputHot;
		}

		public FluidStack getInputCold() {

			return inputCold;
		}

		public ItemStack getOutput() {

			return output;
		}

		public int getEnergy() {

			return energy;
		}
	}

}
