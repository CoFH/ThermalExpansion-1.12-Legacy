package cofh.thermalexpansion.util.managers.machine;

import cofh.core.init.CoreProps;
import cofh.lib.inventory.ComparableItemStackSafe;
import cofh.lib.util.helpers.ItemHelper;
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

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class CrucibleManager {

	private static Map<ComparableItemStackSafe, RecipeCrucible> recipeMap = new THashMap<>();

	static final int DEFAULT_ENERGY = 8000;

	public static RecipeCrucible getRecipe(ItemStack input) {

		return input == null ? null : recipeMap.get(new ComparableItemStackSafe(input));
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
			addRecipe(8000, ItemMaterial.dustPyrotheum, new FluidStack(TFFluids.fluidPyrotheum, 250));
			addRecipe(8000, ItemMaterial.dustCryotheum, new FluidStack(TFFluids.fluidCryotheum, 250));
			addRecipe(8000, ItemMaterial.dustAerotheum, new FluidStack(TFFluids.fluidAerotheum, 250));
			addRecipe(8000, ItemMaterial.dustPetrotheum, new FluidStack(TFFluids.fluidPetrotheum, 250));
		}
		/* LOAD RECIPES */
		loadRecipes();
	}

	public static void loadRecipes() {

	}

	public static void refresh() {

		Map<ComparableItemStackSafe, RecipeCrucible> tempMap = new THashMap<>(recipeMap.size());
		RecipeCrucible tempRecipe;

		for (Entry<ComparableItemStackSafe, RecipeCrucible> entry : recipeMap.entrySet()) {
			tempRecipe = entry.getValue();
			tempMap.put(new ComparableItemStackSafe(tempRecipe.input), tempRecipe);
		}
		recipeMap.clear();
		recipeMap = tempMap;
	}

	/* ADD RECIPES */
	public static boolean addRecipe(int energy, ItemStack input, FluidStack output) {

		if (input == null || output == null || output.amount <= 0 || energy <= 0 || recipeExists(input)) {
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

}
