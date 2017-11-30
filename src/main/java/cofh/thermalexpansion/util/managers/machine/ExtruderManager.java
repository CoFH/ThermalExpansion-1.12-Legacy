package cofh.thermalexpansion.util.managers.machine;

import cofh.core.util.ItemWrapper;
import gnu.trove.map.hash.THashMap;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import java.util.Map;
import java.util.Map.Entry;

public class ExtruderManager {

	private static Map<ItemWrapper, ExtruderRecipe> recipeMap = new THashMap<>();

	public static final int DEFAULT_ENERGY = 800;

	public static ExtruderRecipe getRecipe(ItemStack input) {

		return input.isEmpty() ? null : recipeMap.get(new ItemWrapper(input));
	}

	public static boolean recipeExists(ItemStack input) {

		return getRecipe(input) != null;
	}

	public static ExtruderRecipe[] getRecipeList() {

		return recipeMap.values().toArray(new ExtruderRecipe[recipeMap.size()]);
	}

	public static void initialize() {

		addRecipe(DEFAULT_ENERGY / 2, new ItemStack(Blocks.COBBLESTONE), new FluidStack(FluidRegistry.LAVA, 0), new FluidStack(FluidRegistry.WATER, 0));
		addRecipe(DEFAULT_ENERGY, new ItemStack(Blocks.STONE), new FluidStack(FluidRegistry.LAVA, 0), new FluidStack(FluidRegistry.WATER, Fluid.BUCKET_VOLUME));
		addRecipe(DEFAULT_ENERGY * 2, new ItemStack(Blocks.OBSIDIAN), new FluidStack(FluidRegistry.LAVA, Fluid.BUCKET_VOLUME), new FluidStack(FluidRegistry.WATER, Fluid.BUCKET_VOLUME));

		/* LOAD RECIPES */
		loadRecipes();
	}

	public static void loadRecipes() {

	}

	public static void refresh() {

		Map<ItemWrapper, ExtruderRecipe> tempMap = new THashMap<>(recipeMap.size());
		ExtruderRecipe tempRecipe;

		for (Entry<ItemWrapper, ExtruderRecipe> entry : recipeMap.entrySet()) {
			tempRecipe = entry.getValue();
			ItemWrapper output = new ItemWrapper(tempRecipe.output);
			tempMap.put(output, tempRecipe);
		}
		recipeMap.clear();
		recipeMap = tempMap;
	}

	/* ADD RECIPES */
	public static ExtruderRecipe addRecipe(int energy, ItemStack output, FluidStack inputHot, FluidStack inputCold) {

		if (output.isEmpty() || inputHot == null || inputCold == null || energy <= 0 || recipeExists(output)) {
			return null;
		}
		ExtruderRecipe recipe = new ExtruderRecipe(output, inputHot, inputCold, energy);
		recipeMap.put(new ItemWrapper(output), recipe);
		return recipe;
	}

	/* REMOVE RECIPES */
	public static ExtruderRecipe removeRecipe(ItemStack output) {

		return recipeMap.remove(new ItemWrapper(output));
	}

	/* RECIPE CLASS */
	public static class ExtruderRecipe {

		final ItemStack output;
		final FluidStack inputHot;
		final FluidStack inputCold;
		final int energy;

		ExtruderRecipe(ItemStack output, FluidStack inputHot, FluidStack inputCold, int energy) {

			this.inputHot = inputHot;
			this.inputCold = inputCold;
			this.output = output;
			this.energy = energy;
		}

		public ItemStack getOutput() {

			return output;
		}

		public FluidStack getInputHot() {

			return inputHot;
		}

		public FluidStack getInputCold() {

			return inputCold;
		}

		public int getEnergy() {

			return energy;
		}
	}

}
