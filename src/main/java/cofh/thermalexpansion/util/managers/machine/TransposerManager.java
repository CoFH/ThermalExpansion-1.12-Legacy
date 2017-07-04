package cofh.thermalexpansion.util.managers.machine;

import cofh.core.inventory.ComparableItemStack;
import cofh.core.util.helpers.ItemHelper;
import cofh.core.util.oredict.OreDictionaryArbiter;
import cofh.thermalfoundation.init.TFFluids;
import cofh.thermalfoundation.item.ItemFertilizer;
import cofh.thermalfoundation.item.ItemMaterial;
import gnu.trove.map.hash.THashMap;
import gnu.trove.set.hash.THashSet;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import java.util.*;
import java.util.Map.Entry;

public class TransposerManager {

	private static Map<List<Integer>, TransposerRecipe> recipeMapFill = new THashMap<>();
	private static Map<ComparableItemStackTransposer, TransposerRecipe> recipeMapExtract = new THashMap<>();
	private static Set<ComparableItemStackTransposer> validationSet = new THashSet<>();

	public static final int DEFAULT_ENERGY = 400;

	public static TransposerRecipe getFillRecipe(ItemStack input, FluidStack fluid) {

		return input.isEmpty() || fluid == null ? null : recipeMapFill.get(Arrays.asList(new ComparableItemStackTransposer(input).hashCode(), fluid.getFluid().hashCode()));
	}

	public static TransposerRecipe getExtractRecipe(ItemStack input) {

		return input.isEmpty() ? null : recipeMapExtract.get(new ComparableItemStackTransposer(input));
	}

	public static boolean fillRecipeExists(ItemStack input, FluidStack fluid) {

		return getFillRecipe(input, fluid) != null;
	}

	public static boolean extractRecipeExists(ItemStack input, FluidStack fluid) {

		return getExtractRecipe(input) != null;
	}

	public static TransposerRecipe[] getFillRecipeList() {

		return recipeMapFill.values().toArray(new TransposerRecipe[recipeMapFill.size()]);
	}

	public static TransposerRecipe[] getExtractRecipeList() {

		return recipeMapExtract.values().toArray(new TransposerRecipe[recipeMapExtract.size()]);
	}

	public static boolean isItemValid(ItemStack input) {

		return !input.isEmpty() && validationSet.contains(new ComparableItemStackTransposer(input));
	}

	public static void initialize() {

		/* BLOCKS */
		{
			addFillRecipe(4000, new ItemStack(Blocks.COBBLESTONE), new ItemStack(Blocks.MOSSY_COBBLESTONE), new FluidStack(FluidRegistry.WATER, 250), false);
			addFillRecipe(4000, new ItemStack(Blocks.STONEBRICK), new ItemStack(Blocks.STONEBRICK, 1, 1), new FluidStack(FluidRegistry.WATER, 250), false);
			addFillRecipe(4000, new ItemStack(Blocks.SANDSTONE), new ItemStack(Blocks.END_STONE), new FluidStack(TFFluids.fluidEnder, 250), false);
			addFillRecipe(4000, new ItemStack(Items.BRICK), new ItemStack(Items.NETHERBRICK), new FluidStack(FluidRegistry.LAVA, 250), false);

			addExtractRecipe(2400, new ItemStack(Blocks.CACTUS), ItemStack.EMPTY, new FluidStack(FluidRegistry.WATER, 500), 0, false);
			addExtractRecipe(2400, new ItemStack(Blocks.REEDS), new ItemStack(Items.SUGAR, 2), new FluidStack(FluidRegistry.WATER, 250), 0, false);
		}

		/* ELEMENTAL */
		{
			addFillRecipe(4000, new ItemStack(Items.GLOWSTONE_DUST), new ItemStack(Items.BLAZE_POWDER), new FluidStack(TFFluids.fluidRedstone, 200), false);
			addFillRecipe(4000, new ItemStack(Items.SNOWBALL), ItemHelper.cloneStack(ItemMaterial.dustBlizz, 1), new FluidStack(TFFluids.fluidRedstone, 200), false);
			addFillRecipe(4000, new ItemStack(Blocks.SAND), ItemHelper.cloneStack(ItemMaterial.dustBlitz), new FluidStack(TFFluids.fluidRedstone, 200), false);
			addFillRecipe(4000, ItemHelper.cloneStack(ItemMaterial.dustObsidian), ItemHelper.cloneStack(ItemMaterial.dustBasalz), new FluidStack(TFFluids.fluidRedstone, 200), false);
		}

		addFillRecipe(4000, new ItemStack(Blocks.SPONGE, 1, 0), new ItemStack(Blocks.SPONGE, 1, 1), new FluidStack(FluidRegistry.WATER, 1000), true);

		addFillRecipe(2000, ItemHelper.cloneStack(ItemFertilizer.fertilizerBasic), ItemHelper.cloneStack(ItemFertilizer.fertilizerRich), new FluidStack(TFFluids.fluidSap, 500), false);

		/* LOAD RECIPES */
		loadRecipes();
	}

	public static void loadRecipes() {

		addFillRecipe(2000, ItemHelper.getOre("oreCinnabar"), ItemHelper.cloneStack(ItemMaterial.crystalCinnabar, 1), new FluidStack(TFFluids.fluidCryotheum, 200), false);
	}

	public static void refresh() {

		Map<List<Integer>, TransposerRecipe> tempFill = new THashMap<>(recipeMapFill.size());
		Map<ComparableItemStackTransposer, TransposerRecipe> tempExtract = new THashMap<>(recipeMapExtract.size());
		Set<ComparableItemStackTransposer> tempSet = new THashSet<>();
		TransposerRecipe tempRecipe;

		for (Entry<List<Integer>, TransposerRecipe> entry : recipeMapFill.entrySet()) {
			tempRecipe = entry.getValue();
			ComparableItemStackTransposer input = new ComparableItemStackTransposer(tempRecipe.input);
			FluidStack fluid = tempRecipe.fluid.copy();
			tempFill.put(Arrays.asList(input.hashCode(), fluid.getFluid().hashCode()), tempRecipe);
			tempSet.add(input);
		}
		for (Entry<ComparableItemStackTransposer, TransposerRecipe> entry : recipeMapExtract.entrySet()) {
			tempRecipe = entry.getValue();
			ComparableItemStackTransposer input = new ComparableItemStackTransposer(tempRecipe.input);
			tempExtract.put(input, tempRecipe);
			tempSet.add(input);
		}
		recipeMapFill.clear();
		recipeMapExtract.clear();

		recipeMapFill = tempFill;
		recipeMapExtract = tempExtract;

		validationSet.clear();
		validationSet = tempSet;
	}

	/* ADD RECIPES */
	public static TransposerRecipe addFillRecipe(int energy, ItemStack input, ItemStack output, FluidStack fluid, boolean reversible) {

		if (input.isEmpty() || output.isEmpty() || fluid == null || fluid.amount <= 0 || energy <= 0) {
			return null;
		}
		if (fillRecipeExists(input, fluid)) {
			return null;
		}
		TransposerRecipe recipeFill = new TransposerRecipe(input, output, fluid, energy, 100);
		recipeMapFill.put(Arrays.asList(new ComparableItemStackTransposer(input).hashCode(), fluid.getFluid().hashCode()), recipeFill);
		validationSet.add(new ComparableItemStackTransposer(input));

		if (reversible) {
			addExtractRecipe(energy, output, input, fluid, 100, false);
		}
		return recipeFill;
	}

	public static TransposerRecipe addExtractRecipe(int energy, ItemStack input, ItemStack output, FluidStack fluid, int chance, boolean reversible) {

		if (input.isEmpty() || fluid == null || fluid.amount <= 0 || energy <= 0) {
			return null;
		}
		if (extractRecipeExists(input, fluid)) {
			return null;
		}
		if (output == null && reversible || output.isEmpty() && chance != 0) {
			return null;
		}
		TransposerRecipe recipeExtraction = new TransposerRecipe(input, output, fluid, energy, chance);
		recipeMapExtract.put(new ComparableItemStackTransposer(input), recipeExtraction);
		validationSet.add(new ComparableItemStackTransposer(input));

		if (reversible) {
			addFillRecipe(energy, output, input, fluid, false);
		}
		return recipeExtraction;
	}

	/* REMOVE RECIPES */
	public static TransposerRecipe removeFillRecipe(ItemStack input, FluidStack fluid) {

		return recipeMapFill.remove(Arrays.asList(new ComparableItemStackTransposer(input).hashCode(), fluid.getFluid().hashCode()));
	}

	public static TransposerRecipe removeExtractRecipe(ItemStack input) {

		return recipeMapExtract.remove(new ComparableItemStackTransposer(input));
	}

	/* RECIPE CLASS */
	public static class TransposerRecipe {

		final ItemStack input;
		final ItemStack output;
		final FluidStack fluid;
		final int energy;
		final int chance;

		public TransposerRecipe(ItemStack input, ItemStack output, FluidStack fluid, int energy, int chance) {

			this.input = input;
			this.output = output;
			this.fluid = fluid;
			this.energy = energy;
			this.chance = chance;
		}

		public ItemStack getInput() {

			return input;
		}

		public ItemStack getOutput() {

			return output;
		}

		public FluidStack getFluid() {

			return fluid;
		}

		public int getEnergy() {

			return energy;
		}

		public int getChance() {

			return chance;
		}

	}

	/* ITEMSTACK CLASS */
	public static class ComparableItemStackTransposer extends ComparableItemStack {

		public static final String CROP = "crop";
		public static final String SEED = "seed";

		public static final String GEM = "gem";
		public static final String ORE = "ore";
		public static final String DUST = "dust";
		public static final String INGOT = "ingot";
		public static final String NUGGET = "nugget";

		public static boolean safeOreType(String oreName) {

			return oreName.startsWith(CROP) || oreName.startsWith(SEED) || oreName.startsWith(GEM) || oreName.startsWith(ORE) || oreName.startsWith(DUST) || oreName.startsWith(INGOT) || oreName.startsWith(NUGGET);
		}

		public static int getOreID(ItemStack stack) {

			ArrayList<Integer> ids = OreDictionaryArbiter.getAllOreIDs(stack);

			if (ids != null) {
				for (int i = 0, e = ids.size(); i < e; ) {
					int id = ids.get(i++);
					if (id != -1 && safeOreType(ItemHelper.oreProxy.getOreName(id))) {
						return id;
					}
				}
			}
			return -1;
		}

		public ComparableItemStackTransposer(ItemStack stack) {

			super(stack);
			oreID = getOreID(stack);
		}

		@Override
		public ComparableItemStackTransposer set(ItemStack stack) {

			super.set(stack);
			oreID = getOreID(stack);

			return this;
		}
	}

}
