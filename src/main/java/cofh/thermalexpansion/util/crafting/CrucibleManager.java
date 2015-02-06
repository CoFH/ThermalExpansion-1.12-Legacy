package cofh.thermalexpansion.util.crafting;

import cofh.core.util.oredict.OreDictionaryArbiter;
import cofh.lib.inventory.ComparableItemStackSafe;
import cofh.lib.util.helpers.ItemHelper;
import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalexpansion.core.TEProps;
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
	private static ComparableItemStackSafe query = new ComparableItemStackSafe(new ItemStack(Blocks.stone));
	private static boolean allowOverwrite = false;

	static {
		allowOverwrite = ThermalExpansion.config.get("tweak.crafting", "Crucible.AllowRecipeOverwrite", false);
	}

	public static RecipeCrucible getRecipe(ItemStack input) {

		return input == null ? null : recipeMap.get(query.set(input));
	}

	public static boolean recipeExists(ItemStack input) {

		return getRecipe(input) != null;
	}

	public static RecipeCrucible[] getRecipeList() {

		return recipeMap.values().toArray(new RecipeCrucible[0]);
	}

	public static void addDefaultRecipes() {

		boolean recipeNetherrack = ThermalExpansion.config.get("tweak.crafting", "Crucible.Netherrack", true);
		boolean recipeBlazeRod = ThermalExpansion.config.get("tweak.crafting", "Crucible.BlazeRod", true);

		int tweakNetherrackRF = ThermalExpansion.config.get("tweak.crafting", "Crucible.Netherrack.Energy", TEProps.lavaRF * 6 / 10);
		int tweakBlazeRodRF = ThermalExpansion.config.get("tweak.crafting", "Crucible.BlazeRod.Energy", TEProps.lavaRF / 10);

		if (recipeNetherrack) {
			if (tweakNetherrackRF >= TEProps.lavaRF / 10 && tweakNetherrackRF <= TEProps.lavaRF) {
				addTERecipe(tweakNetherrackRF, new ItemStack(Blocks.netherrack), new FluidStack(FluidRegistry.LAVA, FluidContainerRegistry.BUCKET_VOLUME));
			} else {
				addTERecipe(TEProps.lavaRF * 6 / 10, new ItemStack(Blocks.netherrack), new FluidStack(FluidRegistry.LAVA, FluidContainerRegistry.BUCKET_VOLUME));
				ThermalExpansion.log.info("'Crucible.Netherrack.Energy' config value is out of acceptable range. Using default.");
				ThermalExpansion.config.set("tweak.crafting", "Crucible.Netherrack.Energy", TEProps.lavaRF * 6 / 10);
			}
		}
		if (recipeBlazeRod) {
			if (tweakBlazeRodRF >= TEProps.lavaRF / 20 && tweakBlazeRodRF <= TEProps.lavaRF) {
				addTERecipe(tweakBlazeRodRF, new ItemStack(Items.blaze_rod), new FluidStack(FluidRegistry.LAVA, FluidContainerRegistry.BUCKET_VOLUME / 4));
			} else {
				addTERecipe(TEProps.lavaRF / 10, new ItemStack(Items.blaze_rod), new FluidStack(FluidRegistry.LAVA, FluidContainerRegistry.BUCKET_VOLUME / 4));
				ThermalExpansion.log.info("'Crucible.BlazeRod.Energy' config value is out of acceptable range. Using default.");
				ThermalExpansion.config.set("tweak.crafting", "Crucible.BlazeRod.Energy", TEProps.lavaRF / 10);
			}
		}
		int defaultCost = TEProps.lavaRF * 8 / 5;

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
		addTERecipe(8000, TFItems.dustPyrotheum, new FluidStack(TFFluids.fluidPyrotheum, 100));
		addTERecipe(8000, TFItems.dustCryotheum, new FluidStack(TFFluids.fluidCryotheum, 100));
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
