package cofh.thermalexpansion.util.managers.machine;

import cofh.core.init.CoreProps;
import cofh.core.inventory.ComparableItemStack;
import cofh.core.util.helpers.ItemHelper;
import cofh.core.util.oredict.OreDictionaryArbiter;
import cofh.thermalfoundation.block.BlockOreFluid;
import cofh.thermalfoundation.init.TFFluids;
import cofh.thermalfoundation.item.ItemMaterial;
import gnu.trove.map.hash.THashMap;
import gnu.trove.set.hash.THashSet;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class CrucibleManager {

	private static Map<ComparableItemStackCrucible, CrucibleRecipe> recipeMap = new THashMap<>();
	private static Set<ComparableItemStackCrucible> lavaSet = new THashSet<>();

	public static final int DEFAULT_ENERGY = 8000;

	public static CrucibleRecipe getRecipe(ItemStack input) {

		return input.isEmpty() ? null : recipeMap.get(new ComparableItemStackCrucible(input));
	}

	public static boolean recipeExists(ItemStack input) {

		return getRecipe(input) != null;
	}

	public static CrucibleRecipe[] getRecipeList() {

		return recipeMap.values().toArray(new CrucibleRecipe[recipeMap.size()]);
	}

	public static boolean isLava(ItemStack input) {

		return !input.isEmpty() && lavaSet.contains(new ComparableItemStackCrucible(input));
	}

	public static void initialize() {

		/* LAVA */
		{
			int netherrack_RF = CoreProps.LAVA_RF * 3 / 5;
			int magma_RF = CoreProps.LAVA_RF * 2 / 5;
			int rock_RF = CoreProps.LAVA_RF * 8 / 5;

			addRecipe(netherrack_RF, new ItemStack(Blocks.NETHERRACK), new FluidStack(FluidRegistry.LAVA, Fluid.BUCKET_VOLUME));
			addRecipe(magma_RF, new ItemStack(Blocks.MAGMA), new FluidStack(FluidRegistry.LAVA, Fluid.BUCKET_VOLUME));
			addRecipe(rock_RF, new ItemStack(Blocks.COBBLESTONE), new FluidStack(FluidRegistry.LAVA, Fluid.BUCKET_VOLUME));
			addRecipe(rock_RF, new ItemStack(Blocks.STONE), new FluidStack(FluidRegistry.LAVA, Fluid.BUCKET_VOLUME));
			addRecipe(rock_RF, new ItemStack(Blocks.STONE), new FluidStack(FluidRegistry.LAVA, Fluid.BUCKET_VOLUME));
			addRecipe(rock_RF, new ItemStack(Blocks.STONE, 1, 5), new FluidStack(FluidRegistry.LAVA, Fluid.BUCKET_VOLUME));
			addRecipe(rock_RF, new ItemStack(Blocks.STONE, 1, 6), new FluidStack(FluidRegistry.LAVA, Fluid.BUCKET_VOLUME));
			addRecipe(rock_RF, new ItemStack(Blocks.OBSIDIAN), new FluidStack(FluidRegistry.LAVA, Fluid.BUCKET_VOLUME));
		}

		/* VANILLA */
		{
			addRecipe(200, new ItemStack(Items.SNOWBALL), new FluidStack(FluidRegistry.WATER, Fluid.BUCKET_VOLUME / 8));
			addRecipe(800, new ItemStack(Blocks.SNOW), new FluidStack(FluidRegistry.WATER, Fluid.BUCKET_VOLUME / 2));
			addRecipe(1600, new ItemStack(Blocks.ICE), new FluidStack(FluidRegistry.WATER, Fluid.BUCKET_VOLUME));

			addRecipe(8000, new ItemStack(Items.REDSTONE), new FluidStack(TFFluids.fluidRedstone, 100));
			addRecipe(8000 * 9, new ItemStack(Blocks.REDSTONE_BLOCK), new FluidStack(TFFluids.fluidRedstone, 100 * 9));
			addRecipe(20000, new ItemStack(Items.GLOWSTONE_DUST), new FluidStack(TFFluids.fluidGlowstone, 250));
			addRecipe(20000 * 4, new ItemStack(Blocks.GLOWSTONE), new FluidStack(TFFluids.fluidGlowstone, 1000));
			addRecipe(20000, new ItemStack(Items.ENDER_PEARL), new FluidStack(TFFluids.fluidEnder, 250));
		}

		/* TF MATERIALS */
		{
			addRecipe(2000, ItemMaterial.globTar, new FluidStack(TFFluids.fluidCreosote, 100));
			addRecipe(4000, ItemMaterial.dustCoal, new FluidStack(TFFluids.fluidCoal, 100));

			addRecipe(2000, ItemMaterial.crystalCrudeOil, new FluidStack(TFFluids.fluidCrudeOil, 250));
			addRecipe(2000, ItemMaterial.crystalRedstone, new FluidStack(TFFluids.fluidRedstone, 250));
			addRecipe(2000, ItemMaterial.crystalGlowstone, new FluidStack(TFFluids.fluidGlowstone, 250));
			addRecipe(2000, ItemMaterial.crystalEnder, new FluidStack(TFFluids.fluidEnder, 250));

			addRecipe(8000, ItemMaterial.dustPyrotheum, new FluidStack(TFFluids.fluidPyrotheum, 250));
			addRecipe(8000, ItemMaterial.dustCryotheum, new FluidStack(TFFluids.fluidCryotheum, 250));
			addRecipe(8000, ItemMaterial.dustAerotheum, new FluidStack(TFFluids.fluidAerotheum, 250));
			addRecipe(8000, ItemMaterial.dustPetrotheum, new FluidStack(TFFluids.fluidPetrotheum, 250));
		}

		/* TF FLUID ORES */
		{
			addRecipe(4000, BlockOreFluid.oreFluidCrudeOilSand, new FluidStack(TFFluids.fluidCrudeOil, 1000));
			addRecipe(4000, BlockOreFluid.oreFluidCrudeOilGravel, new FluidStack(TFFluids.fluidCrudeOil, 1000));
			addRecipe(4000, BlockOreFluid.oreFluidRedstone, new FluidStack(TFFluids.fluidRedstone, 1000));
			addRecipe(4000, BlockOreFluid.oreFluidGlowstone, new FluidStack(TFFluids.fluidGlowstone, 1000));
			addRecipe(4000, BlockOreFluid.oreFluidEnder, new FluidStack(TFFluids.fluidEnder, 1000));
		}

		/* LOAD RECIPES */
		loadRecipes();
	}

	public static void loadRecipes() {

	}

	public static void refresh() {

		Map<ComparableItemStackCrucible, CrucibleRecipe> tempMap = new THashMap<>(recipeMap.size());
		Set<ComparableItemStackCrucible> tempSet = new THashSet<>();
		CrucibleRecipe tempRecipe;

		for (Entry<ComparableItemStackCrucible, CrucibleRecipe> entry : recipeMap.entrySet()) {
			tempRecipe = entry.getValue();
			ComparableItemStackCrucible input = new ComparableItemStackCrucible(tempRecipe.input);
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
		ComparableItemStackCrucible inputCrucible = new ComparableItemStackCrucible(input);

		CrucibleRecipe recipe = new CrucibleRecipe(input, output, energy);
		recipeMap.put(inputCrucible, recipe);

		if (FluidRegistry.LAVA.equals(output.getFluid())) {
			lavaSet.add(inputCrucible);
		}
		return recipe;
	}

	/* REMOVE RECIPES */
	public static CrucibleRecipe removeRecipe(ItemStack input) {

		ComparableItemStackCrucible inputCrucible = new ComparableItemStackCrucible(input);
		lavaSet.remove(inputCrucible);
		return recipeMap.remove(inputCrucible);
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

	/* ITEMSTACK CLASS */
	public static class ComparableItemStackCrucible extends ComparableItemStack {

		public static final String NUGGET = "nugget";
		public static final String INGOT = "ingot";
		public static final String ORE = "ore";
		public static final String BLOCK = "block";
		public static final String DUST = "dust";
		public static final String PLATE = "plate";

		public static boolean safeOreType(String oreName) {

			return oreName.startsWith(INGOT) || oreName.startsWith(ORE) || oreName.startsWith(NUGGET) || oreName.startsWith(BLOCK) || oreName.startsWith(DUST) || oreName.equals(PLATE);
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

		public ComparableItemStackCrucible(ItemStack stack) {

			super(stack);
			oreID = getOreID(stack);
		}

		@Override
		public ComparableItemStackCrucible set(ItemStack stack) {

			super.set(stack);
			oreID = getOreID(stack);

			return this;
		}
	}

}
