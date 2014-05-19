package thermalexpansion.util.crafting;

import gnu.trove.map.TMap;
import gnu.trove.map.hash.THashMap;

import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import thermalexpansion.ThermalExpansion;

public class ExtruderManager {

	private static TMap<List, RecipeExtruder> recipeMap = new THashMap();
	private static boolean allowOverwrite = false;

	static {
		allowOverwrite = ThermalExpansion.config.get("tweak.crafting", "Extruder.AllowRecipeOverwrite", false);
	}

	public static RecipeExtruder[] getRecipeList() {

		return recipeMap.values().toArray(new RecipeExtruder[0]);
	}

	public static void addDefaultRecipes() {

	}

	public static void loadRecipes() {

		addDefaultRecipes();
	}

	/* RECIPE CLASS */
	public static class RecipeExtruder {

		final FluidStack hotFluid;
		final FluidStack coldFluid;
		final ItemStack[] outputs;

		RecipeExtruder(FluidStack hotFluid, FluidStack coldFluid, ItemStack[] outputs) {

			this.hotFluid = hotFluid;
			this.coldFluid = coldFluid;
			this.outputs = new ItemStack[outputs.length];

			for (int i = 0; i < outputs.length; i++) {
				this.outputs[i] = outputs[i].copy();
			}
		}

		public FluidStack getHotInput() {

			return hotFluid;
		}

		public FluidStack getColdInput() {

			return coldFluid;
		}

		public ItemStack[] getOutputs() {

			return outputs;
		}
	}

}
