package cofh.thermalexpansion.util.managers.machine;

import cofh.core.util.ItemWrapper;
import cofh.core.util.helpers.ItemHelper;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class ExtruderManager {

	private static Map<ItemWrapper, ExtruderRecipe> recipeMapIgneous = new Object2ObjectOpenHashMap<>();
	private static Map<ItemWrapper, ExtruderRecipe> recipeMapSedimentary = new Object2ObjectOpenHashMap<>();
	private static List<ItemStack> outputListIgneous = new ArrayList<>();
	private static List<ItemStack> outputListSedimentary = new ArrayList<>();

	public static final int DEFAULT_ENERGY = 800;

	public static ExtruderRecipe getRecipe(ItemStack input, boolean sedimentary) {

		if (input.isEmpty()) {
			return null;
		}
		return sedimentary ? recipeMapSedimentary.get(new ItemWrapper(input)) : recipeMapIgneous.get(new ItemWrapper(input));
	}

	public static boolean recipeExists(ItemStack input, boolean sedimentary) {

		return getRecipe(input, sedimentary) != null;
	}

	public static ItemStack getOutput(int index, boolean sedimentary) {

		return sedimentary ? outputListSedimentary.get(index) : outputListIgneous.get(index);
	}

	public static int getOutputListSize(boolean sedimentary) {

		return sedimentary ? outputListSedimentary.size() : outputListIgneous.size();
	}

	public static int getIndex(ItemStack output, boolean sedimentary) {

		if (sedimentary) {
			for (int i = 0; i < outputListSedimentary.size(); i++) {
				if (ItemHelper.itemsIdentical(output, outputListSedimentary.get(i))) {
					return i;
				}
			}
		} else {
			for (int i = 0; i < outputListIgneous.size(); i++) {
				if (ItemHelper.itemsIdentical(output, outputListIgneous.get(i))) {
					return i;
				}
			}
		}
		// Default to first if no match found.
		return 0;
	}

	public static ExtruderRecipe[] getRecipeList(boolean sedimentary) {

		if (sedimentary) {
			return recipeMapSedimentary.values().toArray(new ExtruderRecipe[recipeMapSedimentary.size()]);
		}
		return recipeMapIgneous.values().toArray(new ExtruderRecipe[recipeMapIgneous.size()]);
	}

	public static void initialize() {

		addRecipeIgneous(DEFAULT_ENERGY / 2, new ItemStack(Blocks.COBBLESTONE), new FluidStack(FluidRegistry.LAVA, 0), new FluidStack(FluidRegistry.WATER, 0));
		addRecipeIgneous(DEFAULT_ENERGY, new ItemStack(Blocks.STONE), new FluidStack(FluidRegistry.LAVA, 0), new FluidStack(FluidRegistry.WATER, Fluid.BUCKET_VOLUME));
		addRecipeIgneous(DEFAULT_ENERGY * 2, new ItemStack(Blocks.OBSIDIAN), new FluidStack(FluidRegistry.LAVA, Fluid.BUCKET_VOLUME), new FluidStack(FluidRegistry.WATER, Fluid.BUCKET_VOLUME));

		addRecipeIgneous(DEFAULT_ENERGY, new ItemStack(Blocks.STONE, 1, 1), new FluidStack(FluidRegistry.LAVA, 0), new FluidStack(FluidRegistry.WATER, Fluid.BUCKET_VOLUME));
		addRecipeIgneous(DEFAULT_ENERGY, new ItemStack(Blocks.STONE, 1, 3), new FluidStack(FluidRegistry.LAVA, 0), new FluidStack(FluidRegistry.WATER, Fluid.BUCKET_VOLUME));
		addRecipeIgneous(DEFAULT_ENERGY, new ItemStack(Blocks.STONE, 1, 5), new FluidStack(FluidRegistry.LAVA, 0), new FluidStack(FluidRegistry.WATER, Fluid.BUCKET_VOLUME));

		addRecipeSedimentary(DEFAULT_ENERGY * 3, new ItemStack(Blocks.GRAVEL, 1), new FluidStack(FluidRegistry.LAVA, 0), new FluidStack(FluidRegistry.WATER, 1500));
		addRecipeSedimentary(DEFAULT_ENERGY * 4, new ItemStack(Blocks.SAND, 1), new FluidStack(FluidRegistry.LAVA, 0), new FluidStack(FluidRegistry.WATER, 1500));
		addRecipeSedimentary(DEFAULT_ENERGY * 4, new ItemStack(Blocks.SAND, 1, 1), new FluidStack(FluidRegistry.LAVA, 0), new FluidStack(FluidRegistry.WATER, 1500));
		addRecipeSedimentary(DEFAULT_ENERGY * 8, new ItemStack(Blocks.SANDSTONE, 1), new FluidStack(FluidRegistry.LAVA, 0), new FluidStack(FluidRegistry.WATER, Fluid.BUCKET_VOLUME * 2));
		addRecipeSedimentary(DEFAULT_ENERGY * 8, new ItemStack(Blocks.RED_SANDSTONE, 1), new FluidStack(FluidRegistry.LAVA, 0), new FluidStack(FluidRegistry.WATER, Fluid.BUCKET_VOLUME * 2));

		/* LOAD RECIPES */
		loadRecipes();
	}

	public static void loadRecipes() {

	}

	public static void refresh() {

		Map<ItemWrapper, ExtruderRecipe> tempMapIgneous = new Object2ObjectOpenHashMap<>(recipeMapIgneous.size());
		Map<ItemWrapper, ExtruderRecipe> tempMapSedimentary = new Object2ObjectOpenHashMap<>(recipeMapSedimentary.size());
		ExtruderRecipe tempRecipe;

		for (Entry<ItemWrapper, ExtruderRecipe> entry : recipeMapIgneous.entrySet()) {
			tempRecipe = entry.getValue();
			ItemWrapper output = new ItemWrapper(tempRecipe.output);
			tempMapIgneous.put(output, tempRecipe);
		}
		for (Entry<ItemWrapper, ExtruderRecipe> entry : recipeMapSedimentary.entrySet()) {
			tempRecipe = entry.getValue();
			ItemWrapper output = new ItemWrapper(tempRecipe.output);
			tempMapSedimentary.put(output, tempRecipe);
		}
		recipeMapIgneous.clear();
		recipeMapSedimentary.clear();

		recipeMapIgneous = tempMapIgneous;
		recipeMapSedimentary = tempMapSedimentary;
	}

	/* ADD RECIPES */
	public static ExtruderRecipe addRecipeIgneous(int energy, ItemStack output, FluidStack inputHot, FluidStack inputCold) {

		if (output.isEmpty() || inputHot == null || inputCold == null || energy <= 0 || recipeExists(output, false)) {
			return null;
		}
		ExtruderRecipe recipe = new ExtruderRecipe(output, inputHot, inputCold, energy);
		recipeMapIgneous.put(new ItemWrapper(output), recipe);
		outputListIgneous.add(output);
		return recipe;
	}

	public static ExtruderRecipe addRecipeSedimentary(int energy, ItemStack output, FluidStack inputHot, FluidStack inputCold) {

		if (output.isEmpty() || inputHot == null || inputCold == null || energy <= 0 || recipeExists(output, true)) {
			return null;
		}
		ExtruderRecipe recipe = new ExtruderRecipe(output, inputHot, inputCold, energy);
		recipeMapSedimentary.put(new ItemWrapper(output), recipe);
		outputListSedimentary.add(output);
		return recipe;
	}

	/* REMOVE RECIPES */
	//	public static ExtruderRecipe removeRecipe(ItemStack output) {
	//
	//		return recipeMapIgneous.remove(new ItemWrapper(output));
	//	}

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
