package cofh.thermalexpansion.util.crafting;

import cofh.lib.inventory.ComparableItemStack;
import cofh.lib.inventory.ComparableItemStackSafe;
import cofh.lib.util.helpers.ItemHelper;
import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalfoundation.fluid.TFFluids;
import cofh.thermalfoundation.item.TFItems;

import gnu.trove.map.hash.THashMap;
import gnu.trove.set.hash.THashSet;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidContainerRegistry.FluidContainerData;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

public class TransposerManager {

	private static Map<List<Integer>, RecipeTransposer> recipeMapFill = new THashMap<List<Integer>, RecipeTransposer>();
	private static Map<ComparableItemStackSafe, RecipeTransposer> recipeMapExtraction = new THashMap<ComparableItemStackSafe, RecipeTransposer>();
	private static Set<ComparableItemStackSafe> validationSet = new THashSet<ComparableItemStackSafe>();
	private static ComparableItemStack query = new ComparableItemStackSafe(new ItemStack(Blocks.stone));
	private static boolean allowOverwrite = false;
	public static final int DEFAULT_ENERGY = 1600;

	static {
		allowOverwrite = ThermalExpansion.config.get("RecipeManagers.Transposer", "AllowRecipeOverwrite", false);
	}

	public static RecipeTransposer getFillRecipe(ItemStack input, FluidStack fluid) {

		return input == null || fluid == null || fluid.getFluid() == null ? null : recipeMapFill.get(Arrays.asList(query.set(input).hashCode(), fluid
				.getFluid().hashCode()));
	}

	public static RecipeTransposer getExtractionRecipe(ItemStack input) {

		return input == null ? null : recipeMapExtraction.get(query.set(input));
	}

	public static boolean fillRecipeExists(ItemStack input, FluidStack fluid) {

		return getFillRecipe(input, fluid) != null;
	}

	public static boolean extractionRecipeExists(ItemStack input, FluidStack fluid) {

		return getExtractionRecipe(input) != null;
	}

	public static RecipeTransposer[] getFillRecipeList() {

		return recipeMapFill.values().toArray(new RecipeTransposer[0]);
	}

	public static RecipeTransposer[] getExtractionRecipeList() {

		return recipeMapExtraction.values().toArray(new RecipeTransposer[0]);
	}

	public static boolean isItemValid(ItemStack input) {

		return input == null ? false : validationSet.contains(query.set(input));
	}

	public static void addDefaultRecipes() {

		String comment;
		String category = "RecipeManagers.Transposer.Recipes";

		boolean recipeMossyCobble = ThermalExpansion.config.get(category, "MossyCobblestone", true);
		boolean recipeMossyStoneBrick = ThermalExpansion.config.get(category, "MossyStoneBrick", true);
		boolean recipeEndStone = ThermalExpansion.config.get(category, "EndStone", true);
		boolean recipeNetherBrick = ThermalExpansion.config.get(category, "NetherBrick", false);

		if (recipeMossyCobble) {
			addFillRecipe(8000, new ItemStack(Blocks.cobblestone), new ItemStack(Blocks.mossy_cobblestone), new FluidStack(FluidRegistry.WATER, 250), false);
		}
		if (recipeMossyStoneBrick) {
			addFillRecipe(8000, new ItemStack(Blocks.stonebrick), new ItemStack(Blocks.stonebrick, 1, 1), new FluidStack(FluidRegistry.WATER, 250), false);
		}
		if (recipeEndStone) {
			addFillRecipe(8000, new ItemStack(Blocks.sandstone), new ItemStack(Blocks.end_stone), new FluidStack(TFFluids.fluidEnder, 250), false);
		}
		if (recipeNetherBrick) {
			addFillRecipe(4000, new ItemStack(Items.brick), new ItemStack(Items.netherbrick), new FluidStack(FluidRegistry.LAVA, 250), false);
		}
		addTEFillRecipe(4000, new ItemStack(Items.glowstone_dust), new ItemStack(Items.blaze_powder), new FluidStack(TFFluids.fluidRedstone, 200), false);
		addTEFillRecipe(4000, new ItemStack(Items.snowball), ItemHelper.cloneStack(TFItems.dustBlizz, 1), new FluidStack(TFFluids.fluidRedstone, 200), false);
		addTEFillRecipe(4000, new ItemStack(Blocks.sand), ItemHelper.cloneStack(TFItems.dustBlitz), new FluidStack(TFFluids.fluidRedstone, 200), false);
		addTEFillRecipe(4000, ItemHelper.cloneStack(TFItems.dustObsidian, 1), ItemHelper.cloneStack(TFItems.dustBasalz, 1), new FluidStack(
				TFFluids.fluidRedstone, 200), false);

		addTEFillRecipe(800, new ItemStack(Items.bucket), ItemHelper.cloneStack(TFItems.bucketRedstone, 1), new FluidStack(TFFluids.fluidRedstone, 1000), true);
		addTEFillRecipe(800, new ItemStack(Items.bucket), ItemHelper.cloneStack(TFItems.bucketGlowstone, 1), new FluidStack(TFFluids.fluidGlowstone, 1000),
				true);
		addTEFillRecipe(800, new ItemStack(Items.bucket), ItemHelper.cloneStack(TFItems.bucketEnder, 1), new FluidStack(TFFluids.fluidEnder, 1000), true);
		addTEFillRecipe(800, new ItemStack(Items.bucket), ItemHelper.cloneStack(TFItems.bucketPyrotheum, 1), new FluidStack(TFFluids.fluidPyrotheum, 1000),
				true);
		addTEFillRecipe(800, new ItemStack(Items.bucket), ItemHelper.cloneStack(TFItems.bucketCryotheum, 1), new FluidStack(TFFluids.fluidCryotheum, 1000),
				true);
		addTEFillRecipe(800, new ItemStack(Items.bucket), ItemHelper.cloneStack(TFItems.bucketAerotheum, 1), new FluidStack(TFFluids.fluidAerotheum, 1000),
				true);
		addTEFillRecipe(800, new ItemStack(Items.bucket), ItemHelper.cloneStack(TFItems.bucketPetrotheum, 1), new FluidStack(TFFluids.fluidPetrotheum, 1000),
				true);
		addTEFillRecipe(800, new ItemStack(Items.bucket), ItemHelper.cloneStack(TFItems.bucketCoal, 1), new FluidStack(TFFluids.fluidCoal, 1000), true);
	}

	public static void loadRecipes() {

		addFillRecipe(1600, ItemHelper.getOre("oreCinnabar"), ItemHelper.cloneStack(TFItems.crystalCinnabar, 1), new FluidStack(TFFluids.fluidCryotheum, 200),
				false);

		for (FluidContainerData data : FluidContainerRegistry.getRegisteredFluidContainerData()) {
			if (FluidContainerRegistry.isBucket(data.emptyContainer)) {
				addFillRecipe(800, data, true);
			} else {
				addFillRecipe(1600, data, true);
			}
		}
	}

	public static void refreshRecipes() {

		Map<List<Integer>, RecipeTransposer> tempFillMap = new THashMap<List<Integer>, RecipeTransposer>(recipeMapFill.size());
		Map<ComparableItemStackSafe, RecipeTransposer> tempExtractMap = new THashMap<ComparableItemStackSafe, RecipeTransposer>(recipeMapExtraction.size());
		Set<ComparableItemStackSafe> tempSet = new THashSet<ComparableItemStackSafe>();
		RecipeTransposer tempRecipe;

		for (Entry<List<Integer>, RecipeTransposer> entry : recipeMapFill.entrySet()) {
			tempRecipe = entry.getValue();
			ComparableItemStackSafe inputStack = new ComparableItemStackSafe(tempRecipe.input);
			FluidStack fluid = tempRecipe.fluid.copy();
			tempFillMap.put(Arrays.asList(inputStack.hashCode(), fluid.getFluid().hashCode()), tempRecipe);
			tempSet.add(inputStack);
		}
		for (Entry<ComparableItemStackSafe, RecipeTransposer> entry : recipeMapExtraction.entrySet()) {
			tempRecipe = entry.getValue();
			ComparableItemStackSafe inputStack = new ComparableItemStackSafe(tempRecipe.input);
			tempExtractMap.put(inputStack, tempRecipe);
			tempSet.add(inputStack);
		}
		recipeMapFill.clear();
		recipeMapFill = tempFillMap;
		recipeMapExtraction.clear();
		recipeMapExtraction = tempExtractMap;
		validationSet.clear();
		validationSet = tempSet;
	}

	/* ADD RECIPES */
	public static boolean addTEFillRecipe(int energy, ItemStack input, ItemStack output, FluidStack fluid, boolean reversible) {

		if (input == null || output == null || fluid == null || fluid.getFluid() == null || fluid.amount <= 0 || energy <= 0) {
			return false;
		}
		RecipeTransposer recipeFill = new RecipeTransposer(input, output, fluid, energy, 100);

		ComparableItemStackSafe inputStack = new ComparableItemStackSafe(input);
		recipeMapFill.put(Arrays.asList(inputStack.hashCode(), fluid.getFluid().hashCode()), recipeFill);
		validationSet.add(inputStack);

		if (reversible) {
			addTEExtractionRecipe(energy, output, input, fluid, 100, false);
		}
		return true;
	}

	public static boolean addTEExtractionRecipe(int energy, ItemStack input, ItemStack output, FluidStack fluid, int chance, boolean reversible) {

		if (input == null || fluid == null || fluid.getFluid() == null || fluid.amount <= 0 || energy <= 0) {
			return false;
		}
		if (output == null && (reversible || chance != 0)) {
			return false;
		}
		RecipeTransposer recipeExtraction = new RecipeTransposer(input, output, fluid, energy, chance);

		ComparableItemStackSafe inputStack = new ComparableItemStackSafe(input);
		recipeMapExtraction.put(inputStack, recipeExtraction);
		validationSet.add(inputStack);

		if (reversible) {
			addTEFillRecipe(energy, output, input, fluid, false);
		}
		return true;
	}

	public static boolean addFillRecipe(int energy, ItemStack input, ItemStack output, FluidStack fluid, boolean reversible, boolean overwrite) {

		if (input == null || output == null || fluid == null || fluid.getFluid() == null || fluid.amount <= 0 || energy <= 0) {
			return false;
		}
		if (!(allowOverwrite & overwrite) && fillRecipeExists(input, fluid)) {
			return false;
		}
		RecipeTransposer recipeFill = new RecipeTransposer(input, output, fluid, energy, 100);
		recipeMapFill.put(Arrays.asList(new ComparableItemStackSafe(input).hashCode(), fluid.getFluid().hashCode()), recipeFill);
		validationSet.add(new ComparableItemStackSafe(input));

		if (reversible) {
			addExtractionRecipe(energy, output, input, fluid, 100, false, overwrite);
		}
		return true;
	}

	public static boolean addExtractionRecipe(int energy, ItemStack input, ItemStack output, FluidStack fluid, int chance, boolean reversible, boolean overwrite) {

		if (input == null || fluid == null || fluid.getFluid() == null || fluid.amount <= 0 || energy <= 0) {
			return false;
		}
		if (!overwrite && extractionRecipeExists(input, fluid)) {
			return false;
		}
		if (output == null && reversible || output == null && chance != 0) {
			return false;
		}
		RecipeTransposer recipeExtraction = new RecipeTransposer(input, output, fluid, energy, chance);
		recipeMapExtraction.put(new ComparableItemStackSafe(input), recipeExtraction);
		validationSet.add(new ComparableItemStackSafe(input));

		if (reversible) {
			addFillRecipe(energy, output, input, fluid, false, overwrite);
		}
		return true;
	}

	/* REMOVE RECIPES */
	public static boolean removeFillRecipe(ItemStack input, FluidStack fluid) {

		return recipeMapFill.remove(Arrays.asList(new ComparableItemStackSafe(input).hashCode(), fluid.getFluid().hashCode())) != null;
	}

	public static boolean removeExtractionRecipe(ItemStack input) {

		return recipeMapExtraction.remove(new ComparableItemStackSafe(input)) != null;
	}

	/* HELPER FUNCTIONS */
	public static boolean addFillRecipe(int energy, FluidContainerData data, boolean reversible) {

		return addFillRecipe(energy, data.emptyContainer, data.filledContainer, data.fluid, reversible, false);
	}

	public static boolean addFillRecipe(int energy, ItemStack input, ItemStack output, FluidStack fluid, boolean reversible) {

		return addFillRecipe(energy, input, output, fluid, reversible, false);
	}

	public static boolean addExtractionRecipe(int energy, ItemStack input, ItemStack output, FluidStack fluid, int chance, boolean reversible) {

		return addExtractionRecipe(energy, input, output, fluid, chance, reversible, false);
	}

	/* RECIPE CLASS */
	public static class RecipeTransposer {

		final ItemStack input;
		final ItemStack output;
		final FluidStack fluid;
		final int energy;
		final int chance;

		RecipeTransposer(ItemStack input, ItemStack output, FluidStack fluid, int energy, int chance) {

			this.input = input;
			this.output = output;
			this.fluid = fluid;
			this.energy = energy;
			this.chance = chance;
		}

		public ItemStack getInput() {

			return input.copy();
		}

		public ItemStack getOutput() {

			if (output != null) {
				return output.copy();
			}
			return null;
		}

		public FluidStack getFluid() {

			return fluid.copy();
		}

		public int getEnergy() {

			return energy;
		}

		public int getChance() {

			return chance;
		}

	}

}
