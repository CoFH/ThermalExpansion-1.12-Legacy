package cofh.thermalexpansion.util.crafting;

import cofh.core.CoFHProps;
import cofh.core.util.oredict.OreDictionaryArbiter;
import cofh.lib.inventory.ComparableItemStackSafe;
import cofh.lib.util.helpers.ItemHelper;
import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalexpansion.api.crafting.recipes.ICrucibleRecipe;
import cofh.thermalfoundation.fluid.TFFluids;
import cofh.thermalfoundation.item.TFItems;

import gnu.trove.map.hash.THashMap;

import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

public class CrucibleManager {

	private static Map<ComparableItemStackSafe, RecipeCrucible> recipeMap = new THashMap<ComparableItemStackSafe, RecipeCrucible>();
	private static boolean allowOverwrite = false;
	public static final int DEFAULT_ENERGY = 8000;

	static {
		allowOverwrite = ThermalExpansion.config.get("RecipeManagers.Crucible", "AllowRecipeOverwrite", false);
	}

	public static RecipeCrucible getRecipe(ItemStack input) {

		return input == null ? null : recipeMap.get(new ComparableItemStackSafe(input));
	}

	public static boolean recipeExists(ItemStack input) {

		return getRecipe(input) != null;
	}

	public static RecipeCrucible[] getRecipeList() {

		return recipeMap.values().toArray(new RecipeCrucible[0]);
	}

	public static void addDefaultRecipes() {

		String category = "RecipeManagers.Crucible.Recipes";

		boolean recipeNetherrack = ThermalExpansion.config.get(category, "Netherrack", true);
		boolean recipeBlazeRod = ThermalExpansion.config.get(category, "BlazeRod", true);

		int tweakNetherrackRF = ThermalExpansion.config.get(category, "Netherrack.Energy", CoFHProps.LAVA_RF * 6 / 10);
		int tweakBlazeRodRF = ThermalExpansion.config.get(category, "BlazeRod.Energy", CoFHProps.LAVA_RF / 10);

		if (recipeNetherrack) {
			if (tweakNetherrackRF >= CoFHProps.LAVA_RF / 100 && tweakNetherrackRF <= CoFHProps.LAVA_RF) {
				addTERecipe(tweakNetherrackRF, new ItemStack(Blocks.netherrack), new FluidStack(FluidRegistry.LAVA, FluidContainerRegistry.BUCKET_VOLUME));
			} else {
				addTERecipe(CoFHProps.LAVA_RF * 6 / 10, new ItemStack(Blocks.netherrack), new FluidStack(FluidRegistry.LAVA,
						FluidContainerRegistry.BUCKET_VOLUME));
				ThermalExpansion.log.info("'Netherrack.Energy' config value is out of acceptable range. Using default.");
				ThermalExpansion.config.set(category, "Netherrack.Energy", CoFHProps.LAVA_RF * 6 / 10);
			}
		}
		if (recipeBlazeRod) {
			if (tweakBlazeRodRF >= CoFHProps.LAVA_RF / 20 && tweakBlazeRodRF <= CoFHProps.LAVA_RF) {
				addTERecipe(tweakBlazeRodRF, new ItemStack(Items.blaze_rod), new FluidStack(FluidRegistry.LAVA, FluidContainerRegistry.BUCKET_VOLUME / 4));
			} else {
				addTERecipe(CoFHProps.LAVA_RF / 100, new ItemStack(Items.blaze_rod), new FluidStack(FluidRegistry.LAVA,
						FluidContainerRegistry.BUCKET_VOLUME / 4));
				ThermalExpansion.log.info("'BlazeRod.Energy' config value is out of acceptable range. Using default.");
				ThermalExpansion.config.set(category, "BlazeRod.Energy", CoFHProps.LAVA_RF / 10);
			}
		}
		int defaultCost = CoFHProps.LAVA_RF * 8 / 5;

		addTERecipe(defaultCost, new ItemStack(Blocks.cobblestone), new FluidStack(FluidRegistry.LAVA, FluidContainerRegistry.BUCKET_VOLUME));
		addTERecipe(defaultCost, new ItemStack(Blocks.stone), new FluidStack(FluidRegistry.LAVA, FluidContainerRegistry.BUCKET_VOLUME));
		addTERecipe(defaultCost, new ItemStack(Blocks.obsidian), new FluidStack(FluidRegistry.LAVA, FluidContainerRegistry.BUCKET_VOLUME));
		addTERecipe(200, new ItemStack(Items.snowball), new FluidStack(FluidRegistry.WATER, FluidContainerRegistry.BUCKET_VOLUME / 8));
		addTERecipe(800, new ItemStack(Blocks.snow), new FluidStack(FluidRegistry.WATER, FluidContainerRegistry.BUCKET_VOLUME / 2));
		addTERecipe(1600, new ItemStack(Blocks.ice), new FluidStack(FluidRegistry.WATER, FluidContainerRegistry.BUCKET_VOLUME));
		addTERecipe(8000, new ItemStack(Items.redstone), new FluidStack(TFFluids.fluidRedstone, 100));
		addTERecipe(8000 * 9, new ItemStack(Blocks.redstone_block), new FluidStack(TFFluids.fluidRedstone, 100 * 9));
		addTERecipe(20000, new ItemStack(Items.glowstone_dust), new FluidStack(TFFluids.fluidGlowstone, 250));
		addTERecipe(20000 * 4, new ItemStack(Blocks.glowstone), new FluidStack(TFFluids.fluidGlowstone, 1000));
		addTERecipe(20000, new ItemStack(Items.ender_pearl), new FluidStack(TFFluids.fluidEnder, 250));
		addTERecipe(8000, TFItems.dustPyrotheum, new FluidStack(TFFluids.fluidPyrotheum, 250));
		addTERecipe(8000, TFItems.dustCryotheum, new FluidStack(TFFluids.fluidCryotheum, 250));
		addTERecipe(8000, TFItems.dustAerotheum, new FluidStack(TFFluids.fluidAerotheum, 250));
		addTERecipe(8000, TFItems.dustPetrotheum, new FluidStack(TFFluids.fluidPetrotheum, 250));
		addTERecipe(8000, TFItems.dustCoal, new FluidStack(TFFluids.fluidCoal, 100));
	}

	public static void loadRecipes() {

	}

	public static void refreshRecipes() {

		Map<ComparableItemStackSafe, RecipeCrucible> tempMap = new THashMap<ComparableItemStackSafe, RecipeCrucible>(recipeMap.size());
		RecipeCrucible tempRecipe;

		for (Entry<ComparableItemStackSafe, RecipeCrucible> entry : recipeMap.entrySet()) {
			tempRecipe = entry.getValue();
			tempMap.put(new ComparableItemStackSafe(tempRecipe.input), tempRecipe);
		}
		recipeMap.clear();
		recipeMap = tempMap;
	}

	/* ADD RECIPES */
	public static boolean addTERecipe(int energy, ItemStack input, FluidStack output) {

		if (input == null || output == null || output.amount <= 0 || energy <= 0) {
			return false;
		}
		RecipeCrucible recipe = new RecipeCrucible(input, output, energy);
		recipeMap.put(new ComparableItemStackSafe(input), recipe);
		return true;
	}

	public static boolean addRecipe(int energy, ItemStack input, FluidStack output, boolean overwrite) {

		if (input == null || output == null || output.amount <= 0 || energy <= 0 || !(allowOverwrite & overwrite) && recipeExists(input)) {
			return false;
		}
		RecipeCrucible recipe = new RecipeCrucible(input, output, energy);
		recipeMap.put(new ComparableItemStackSafe(input), recipe);
		return true;
	}

	/* REMOVE RECIPES */
	public static boolean removeRecipe(ItemStack input) {

		return recipeMap.remove(new ComparableItemStackSafe(input)) != null;
	}

	/* HELPER FUNCTIONS */
	public static void addOreDictionaryRecipe(int energy, String oreName, int stackSize, FluidStack output) {

		ArrayList<ItemStack> registeredOres = OreDictionaryArbiter.getOres(oreName);
		for (int i = 0; i < registeredOres.size(); i++) {
			addTERecipe(energy, ItemHelper.cloneStack(registeredOres.get(i), stackSize), output);
		}
	}

	public static boolean addRecipe(int energy, ItemStack input, FluidStack output) {

		return addRecipe(energy, input, output, false);
	}

	/* RECIPE CLASS */
	public static class RecipeCrucible implements ICrucibleRecipe {

		final ItemStack input;
		final FluidStack output;
		final int energy;

		RecipeCrucible(ItemStack input, FluidStack output, int energy) {

			this.input = input;
			this.output = output;
			this.energy = energy;
		}

		public ItemStack getInput() {

			return input.copy();
		}

		public FluidStack getOutput() {

			return output.copy();
		}

		public int getEnergy() {

			return energy;
		}

	}

}
