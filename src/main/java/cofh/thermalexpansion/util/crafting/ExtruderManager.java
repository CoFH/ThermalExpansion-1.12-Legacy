package cofh.thermalexpansion.util.crafting;

import cofh.thermalexpansion.ThermalExpansion;
import gnu.trove.map.hash.THashMap;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import java.util.List;
import java.util.Map;

public class ExtruderManager { // TODO

	private static Map<List<?>, RecipeExtruder> recipeMap = new THashMap<List<?>, RecipeExtruder>();
	@SuppressWarnings ("unused")
	private static boolean allowOverwrite = false;

	static {
		allowOverwrite = ThermalExpansion.CONFIG.get("RecipeManagers.Extruder", "AllowRecipeOverwrite", false);
	}

	public static RecipeExtruder[] getRecipeList() {

		return recipeMap.values().toArray(new RecipeExtruder[0]);
	}

	public static void addDefaultRecipes() {

	}

	public static void loadRecipes() {

	}

	public static void refreshRecipes() {

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
