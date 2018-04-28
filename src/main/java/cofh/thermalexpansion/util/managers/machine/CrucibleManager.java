package cofh.thermalexpansion.util.managers.machine;

import cofh.core.inventory.ComparableItemStackValidated;
import gnu.trove.map.hash.THashMap;
import gnu.trove.set.hash.THashSet;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class CrucibleManager {

	private static Map<ComparableItemStackValidated, CrucibleRecipe> recipeMap = new THashMap<>();
	private static Set<ComparableItemStackValidated> lavaSet = new THashSet<>();

	public static final int DEFAULT_ENERGY = 8000;

	public static CrucibleRecipe getRecipe(ItemStack input) {

		return input.isEmpty() ? null : recipeMap.get(new ComparableItemStackValidated(input));
	}

	public static boolean recipeExists(ItemStack input) {

		return getRecipe(input) != null;
	}

	public static CrucibleRecipe[] getRecipeList() {

		return recipeMap.values().toArray(new CrucibleRecipe[0]);
	}

	public static boolean isLava(ItemStack input) {

		return !input.isEmpty() && lavaSet.contains(new ComparableItemStackValidated(input));
	}

	public static void initialize() {

	}

	public static void refresh() {

		Map<ComparableItemStackValidated, CrucibleRecipe> tempMap = new THashMap<>(recipeMap.size());
		Set<ComparableItemStackValidated> tempSet = new THashSet<>();
		CrucibleRecipe tempRecipe;

		for (Entry<ComparableItemStackValidated, CrucibleRecipe> entry : recipeMap.entrySet()) {
			tempRecipe = entry.getValue();
			ComparableItemStackValidated input = new ComparableItemStackValidated(tempRecipe.input);
			tempMap.put(input, tempRecipe);

			if (FluidRegistry.LAVA.equals(tempRecipe.getOutput().getFluid())) {
				tempSet.add(input);
			}
		}
		recipeMap.clear();
		recipeMap = tempMap;

		lavaSet.clear();
		lavaSet = tempSet;
	}

	/* ADD RECIPES */
	public static CrucibleRecipe addRecipe(int energy, ItemStack input, FluidStack output) {

		if (input.isEmpty() || output == null || output.amount <= 0 || energy <= 0 || recipeExists(input)) {
			return null;
		}
		ComparableItemStackValidated inputCrucible = new ComparableItemStackValidated(input);

		CrucibleRecipe recipe = new CrucibleRecipe(input, output, energy);
		recipeMap.put(inputCrucible, recipe);

		if (FluidRegistry.LAVA.equals(output.getFluid())) {
			lavaSet.add(inputCrucible);
		}
		return recipe;
	}

	/* REMOVE RECIPES */
	public static CrucibleRecipe removeRecipe(ItemStack input) {

		ComparableItemStackValidated inputCrucible = new ComparableItemStackValidated(input);
		lavaSet.remove(inputCrucible);
		return recipeMap.remove(inputCrucible);
	}

	/* HELPERS */
	public static ComparableItemStackValidated convertInput(ItemStack stack) {

		return new ComparableItemStackValidated(stack);
	}

	/* RECIPE CLASS */
	public static class CrucibleRecipe {

		final ItemStack input;
		final FluidStack output;
		final int energy;

		CrucibleRecipe(ItemStack input, FluidStack output, int energy) {

			this.input = input;
			this.output = output;
			this.energy = energy;
		}

		public ItemStack getInput() {

			return input;
		}

		public FluidStack getOutput() {

			return output;
		}

		public int getEnergy() {

			return energy;
		}
	}

}
