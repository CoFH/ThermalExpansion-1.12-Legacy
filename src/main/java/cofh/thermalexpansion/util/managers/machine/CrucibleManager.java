package cofh.thermalexpansion.util.managers.machine;

import cofh.core.init.CoreProps;
import cofh.core.util.oredict.OreDictionaryArbiter;
import cofh.lib.inventory.ComparableItemStack;
import cofh.lib.util.helpers.ItemHelper;
import cofh.thermalfoundation.block.BlockOreFluid;
import cofh.thermalfoundation.init.TFFluids;
import cofh.thermalfoundation.item.ItemMaterial;
import gnu.trove.map.hash.THashMap;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.OreDictionary;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class CrucibleManager {

	private static Map<ComparableItemStackCrucible, RecipeCrucible> recipeMap = new THashMap<>();

	static final int DEFAULT_ENERGY = 8000;

	public static RecipeCrucible getRecipe(ItemStack input) {

		return input == null ? null : recipeMap.get(new ComparableItemStackCrucible(input));
	}

	public static boolean recipeExists(ItemStack input) {

		return getRecipe(input) != null;
	}

	public static RecipeCrucible[] getRecipeList() {

		return recipeMap.values().toArray(new RecipeCrucible[recipeMap.size()]);
	}

	public static void initialize() {

		/* LAVA */
		{
			int netherrack_RF = CoreProps.LAVA_RF * 6 / 10;
			int blaze_rod_RF = CoreProps.LAVA_RF / 10;
			int rock_RF = CoreProps.LAVA_RF * 8 / 5;

			addRecipe(netherrack_RF, new ItemStack(Blocks.NETHERRACK), new FluidStack(FluidRegistry.LAVA, Fluid.BUCKET_VOLUME));
			addRecipe(blaze_rod_RF, new ItemStack(Items.BLAZE_ROD), new FluidStack(FluidRegistry.LAVA, Fluid.BUCKET_VOLUME / 4));
			addRecipe(rock_RF, new ItemStack(Blocks.COBBLESTONE), new FluidStack(FluidRegistry.LAVA, Fluid.BUCKET_VOLUME));
			addRecipe(rock_RF, new ItemStack(Blocks.STONE), new FluidStack(FluidRegistry.LAVA, Fluid.BUCKET_VOLUME));
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

		Map<ComparableItemStackCrucible, RecipeCrucible> tempMap = new THashMap<>(recipeMap.size());
		RecipeCrucible tempRecipe;

		for (Entry<ComparableItemStackCrucible, RecipeCrucible> entry : recipeMap.entrySet()) {
			tempRecipe = entry.getValue();
			tempMap.put(new ComparableItemStackCrucible(tempRecipe.input), tempRecipe);
		}
		recipeMap.clear();
		recipeMap = tempMap;
	}

	/* ADD RECIPES */
	public static RecipeCrucible addRecipe(int energy, ItemStack input, FluidStack output) {

		if (input == null || output == null || output.amount <= 0 || energy <= 0 || recipeExists(input)) {
			return null;
		}
		RecipeCrucible recipe = new RecipeCrucible(input, output, energy);
		recipeMap.put(new ComparableItemStackCrucible(input), recipe);
		return recipe;
	}

	/* REMOVE RECIPES */
	public static RecipeCrucible removeRecipe(ItemStack input) {

		return recipeMap.remove(new ComparableItemStackCrucible(input));
	}

	/* HELPERS */
	private static void addOreDictionaryRecipe(int energy, String oreName, int stackSize, FluidStack output) {

		List<ItemStack> registeredOres = OreDictionary.getOres(oreName, false);

		for (ItemStack ore : registeredOres) {
			addRecipe(energy, ItemHelper.cloneStack(ore, stackSize), output);
		}
	}

	/* RECIPE CLASS */
	public static class RecipeCrucible {

		final ItemStack input;
		final FluidStack output;
		final int energy;

		RecipeCrucible(ItemStack input, FluidStack output, int energy) {

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
