package cofh.thermalexpansion.util.crafting;

import cofh.core.CoFHProps;
import cofh.core.util.oredict.OreDictionaryArbiter;
import cofh.lib.inventory.ComparableItemStackSafe;
import cofh.lib.util.helpers.ItemHelper;
import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalexpansion.api.crafting.recipes.ICrucibleRecipe;
import cofh.thermalfoundation.init.TFFluids;
import cofh.thermalfoundation.item.ItemMaterial;
import gnu.trove.map.hash.THashMap;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;

public class CrucibleManager {

	private static Map<ComparableItemStackSafe, RecipeCrucible> recipeMap = new THashMap<ComparableItemStackSafe, RecipeCrucible>();
	private static boolean allowOverwrite = false;
	public static final int DEFAULT_ENERGY = 8000;

	static {
		allowOverwrite = ThermalExpansion.CONFIG.get("RecipeManagers.Crucible", "AllowRecipeOverwrite", false);
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

		boolean recipeNetherrack = ThermalExpansion.CONFIG.get(category, "Netherrack", true);
		boolean recipeBlazeRod = ThermalExpansion.CONFIG.get(category, "BlazeRod", true);

		int tweakNetherrackRF = ThermalExpansion.CONFIG.get(category, "Netherrack.Energy", CoFHProps.LAVA_RF * 6 / 10);
		int tweakBlazeRodRF = ThermalExpansion.CONFIG.get(category, "BlazeRod.Energy", CoFHProps.LAVA_RF / 10);

		if (recipeNetherrack) {
			if (tweakNetherrackRF >= CoFHProps.LAVA_RF / 100 && tweakNetherrackRF <= CoFHProps.LAVA_RF) {
				addTERecipe(tweakNetherrackRF, new ItemStack(Blocks.NETHERRACK), new FluidStack(FluidRegistry.LAVA, Fluid.BUCKET_VOLUME));
			} else {
				addTERecipe(CoFHProps.LAVA_RF * 6 / 10, new ItemStack(Blocks.NETHERRACK), new FluidStack(FluidRegistry.LAVA, Fluid.BUCKET_VOLUME));
				ThermalExpansion.LOG.info("'Netherrack.Energy' config value is out of acceptable range. Using default.");
				ThermalExpansion.CONFIG.set(category, "Netherrack.Energy", CoFHProps.LAVA_RF * 6 / 10);
			}
		}
		if (recipeBlazeRod) {
			if (tweakBlazeRodRF >= CoFHProps.LAVA_RF / 20 && tweakBlazeRodRF <= CoFHProps.LAVA_RF) {
				addTERecipe(tweakBlazeRodRF, new ItemStack(Items.BLAZE_ROD), new FluidStack(FluidRegistry.LAVA, Fluid.BUCKET_VOLUME / 4));
			} else {
				addTERecipe(CoFHProps.LAVA_RF / 100, new ItemStack(Items.BLAZE_ROD), new FluidStack(FluidRegistry.LAVA, Fluid.BUCKET_VOLUME / 4));
				ThermalExpansion.LOG.info("'BlazeRod.Energy' config value is out of acceptable range. Using default.");
				ThermalExpansion.CONFIG.set(category, "BlazeRod.Energy", CoFHProps.LAVA_RF / 10);
			}
		}
		int defaultCost = CoFHProps.LAVA_RF * 8 / 5;

		addTERecipe(defaultCost, new ItemStack(Blocks.COBBLESTONE), new FluidStack(FluidRegistry.LAVA, Fluid.BUCKET_VOLUME));
		addTERecipe(defaultCost, new ItemStack(Blocks.STONE), new FluidStack(FluidRegistry.LAVA, Fluid.BUCKET_VOLUME));
		addTERecipe(defaultCost, new ItemStack(Blocks.OBSIDIAN), new FluidStack(FluidRegistry.LAVA, Fluid.BUCKET_VOLUME));
		addTERecipe(200, new ItemStack(Items.SNOWBALL), new FluidStack(FluidRegistry.WATER, Fluid.BUCKET_VOLUME / 8));
		addTERecipe(800, new ItemStack(Blocks.SNOW), new FluidStack(FluidRegistry.WATER, Fluid.BUCKET_VOLUME / 2));
		addTERecipe(1600, new ItemStack(Blocks.ICE), new FluidStack(FluidRegistry.WATER, Fluid.BUCKET_VOLUME));
		addTERecipe(8000, new ItemStack(Items.REDSTONE), new FluidStack(TFFluids.fluidRedstone, 100));
		addTERecipe(8000 * 9, new ItemStack(Blocks.REDSTONE_BLOCK), new FluidStack(TFFluids.fluidRedstone, 100 * 9));
		addTERecipe(20000, new ItemStack(Items.GLOWSTONE_DUST), new FluidStack(TFFluids.fluidGlowstone, 250));
		addTERecipe(20000 * 4, new ItemStack(Blocks.GLOWSTONE), new FluidStack(TFFluids.fluidGlowstone, 1000));
		addTERecipe(20000, new ItemStack(Items.ENDER_PEARL), new FluidStack(TFFluids.fluidEnder, 250));
		addTERecipe(8000, ItemMaterial.dustPyrotheum, new FluidStack(TFFluids.fluidPyrotheum, 250));
		addTERecipe(8000, ItemMaterial.dustCryotheum, new FluidStack(TFFluids.fluidCryotheum, 250));
		addTERecipe(8000, ItemMaterial.dustAerotheum, new FluidStack(TFFluids.fluidAerotheum, 250));
		addTERecipe(8000, ItemMaterial.dustPetrotheum, new FluidStack(TFFluids.fluidPetrotheum, 250));
		addTERecipe(8000, ItemMaterial.dustCoal, new FluidStack(TFFluids.fluidCoal, 100));
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

		@Override
		public ItemStack getInput() {

			return input.copy();
		}

		@Override
		public FluidStack getOutput() {

			return output.copy();
		}

		@Override
		public int getEnergy() {

			return energy;
		}

	}

}
