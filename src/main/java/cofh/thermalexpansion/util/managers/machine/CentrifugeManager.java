package cofh.thermalexpansion.util.managers.machine;

import cofh.core.util.oredict.OreDictionaryArbiter;
import cofh.lib.inventory.ComparableItemStack;
import cofh.lib.util.helpers.ItemHelper;
import cofh.thermalfoundation.init.TFFluids;
import cofh.thermalfoundation.item.ItemMaterial;
import gnu.trove.map.hash.THashMap;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.OreDictionary;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class CentrifugeManager {

	private static Map<ComparableItemStackCentrifuge, RecipeCentrifuge> recipeMap = new THashMap<>();

	static final int DEFAULT_ENERGY = 4000;

	public static RecipeCentrifuge getRecipe(ItemStack input) {

		if (input.isEmpty()) {
			return null;
		}
		ComparableItemStackCentrifuge query = new ComparableItemStackCentrifuge(input);

		RecipeCentrifuge recipe = recipeMap.get(query);

		if (recipe == null) {
			query.metadata = OreDictionary.WILDCARD_VALUE;
			recipe = recipeMap.get(query);
		}
		return recipe;
	}

	public static boolean recipeExists(ItemStack input) {

		return getRecipe(input) != null;
	}

	public static RecipeCentrifuge[] getRecipeList() {

		return recipeMap.values().toArray(new RecipeCentrifuge[recipeMap.size()]);
	}

	public static void initialize() {

		int energy = DEFAULT_ENERGY;

		addRecipe(energy, ItemHelper.cloneStack(ItemMaterial.dustElectrum, 2), Arrays.asList(ItemHelper.cloneStack(ItemMaterial.dustGold), ItemHelper.cloneStack(ItemMaterial.dustSilver)), null);
		addRecipe(energy, ItemHelper.cloneStack(ItemMaterial.dustInvar, 2), Arrays.asList(ItemHelper.cloneStack(ItemMaterial.dustIron, 2), ItemHelper.cloneStack(ItemMaterial.dustNickel)), null);
		addRecipe(energy, ItemHelper.cloneStack(ItemMaterial.dustBronze, 4), Arrays.asList(ItemHelper.cloneStack(ItemMaterial.dustCopper, 3), ItemHelper.cloneStack(ItemMaterial.dustTin)), null);
		addRecipe(energy, ItemHelper.cloneStack(ItemMaterial.dustConstantan, 2), Arrays.asList(ItemHelper.cloneStack(ItemMaterial.dustCopper), ItemHelper.cloneStack(ItemMaterial.dustNickel)), null);
		addRecipe(energy, ItemHelper.cloneStack(ItemMaterial.dustSignalum, 4), Arrays.asList(ItemHelper.cloneStack(ItemMaterial.dustCopper, 3), ItemHelper.cloneStack(ItemMaterial.dustSilver)), new FluidStack(TFFluids.fluidRedstone, 1000));
		addRecipe(energy, ItemHelper.cloneStack(ItemMaterial.dustLumium, 4), Arrays.asList(ItemHelper.cloneStack(ItemMaterial.dustTin, 3), ItemHelper.cloneStack(ItemMaterial.dustSilver)), new FluidStack(TFFluids.fluidGlowstone, 1000));
		addRecipe(energy, ItemHelper.cloneStack(ItemMaterial.dustEnderium, 4), Arrays.asList(ItemHelper.cloneStack(ItemMaterial.dustTin, 2), ItemHelper.cloneStack(ItemMaterial.dustSilver), ItemHelper.cloneStack(ItemMaterial.dustPlatinum)), new FluidStack(TFFluids.fluidEnder, 1000));

		addRecipe(energy, ItemHelper.cloneStack(ItemMaterial.dustPyrotheum, 2), Arrays.asList(ItemHelper.cloneStack(ItemMaterial.dustCoal), ItemHelper.cloneStack(ItemMaterial.dustSulfur), new ItemStack(Items.BLAZE_POWDER), new ItemStack(Items.REDSTONE)), null);
		addRecipe(energy, ItemHelper.cloneStack(ItemMaterial.dustCryotheum, 2), Arrays.asList(ItemHelper.cloneStack(ItemMaterial.dustNiter), ItemHelper.cloneStack(ItemMaterial.dustBlizz), new ItemStack(Items.SNOWBALL), new ItemStack(Items.REDSTONE)), null);
		addRecipe(energy, ItemHelper.cloneStack(ItemMaterial.dustAerotheum, 2), Arrays.asList(ItemHelper.cloneStack(ItemMaterial.dustNiter), ItemHelper.cloneStack(ItemMaterial.dustBlitz), new ItemStack(Blocks.SAND), new ItemStack(Items.REDSTONE)), null);
		addRecipe(energy, ItemHelper.cloneStack(ItemMaterial.dustPetrotheum, 2), Arrays.asList(ItemHelper.cloneStack(ItemMaterial.dustObsidian), ItemHelper.cloneStack(ItemMaterial.dustBasalz), new ItemStack(Items.CLAY_BALL), new ItemStack(Items.REDSTONE)), null);

		/* LOAD RECIPES */
		loadRecipes();
	}

	public static void loadRecipes() {

	}

	/* ADD RECIPES */
	public static RecipeCentrifuge addRecipe(int energy, ItemStack input, List<ItemStack> output, FluidStack fluid) {

		if (input.isEmpty() || output.isEmpty() || output.size() > 4 || energy <= 0 || recipeExists(input)) {
			return null;
		}
		for (ItemStack stack : output) {
			if (stack.isEmpty()) {
				return null;
			}
		}
		RecipeCentrifuge recipe = new RecipeCentrifuge(input, output, fluid, energy);
		recipeMap.put(new ComparableItemStackCentrifuge(input), recipe);
		return recipe;
	}

	/* REMOVE RECIPES */
	public static RecipeCentrifuge removeRecipe(ItemStack input) {

		return recipeMap.remove(new ComparableItemStackCentrifuge(input));
	}

	public static void refresh() {

		Map<ComparableItemStackCentrifuge, RecipeCentrifuge> tempMap = new THashMap<>(recipeMap.size());
		RecipeCentrifuge tempRecipe;

		for (Entry<ComparableItemStackCentrifuge, RecipeCentrifuge> entry : recipeMap.entrySet()) {
			tempRecipe = entry.getValue();
			tempMap.put(new ComparableItemStackCentrifuge(tempRecipe.input), tempRecipe);
		}
		recipeMap.clear();
		recipeMap = tempMap;
	}

	/* RECIPE CLASS */
	public static class RecipeCentrifuge {

		final ItemStack input;
		final List<ItemStack> output;
		final FluidStack fluid;
		final int energy;

		RecipeCentrifuge(ItemStack input, List<ItemStack> output, @Nullable FluidStack fluid, int energy) {

			this.input = input;
			this.output = output;
			this.fluid = fluid;
			this.energy = energy;

			if (input.getCount() <= 0) {
				input.setCount(1);
			}
		}

		public ItemStack getInput() {

			return input.copy();
		}

		public List<ItemStack> getOutput() {

			return output;
		}

		public FluidStack getFluid() {

			return fluid;
		}

		public int getEnergy() {

			return energy;
		}
	}

	/* ITEMSTACK CLASS */
	public static class ComparableItemStackCentrifuge extends ComparableItemStack {

		static final String DUST = "dust";

		static boolean safeOreType(String oreName) {

			return oreName.startsWith(DUST);
		}

		static int getOreID(ItemStack stack) {

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

		ComparableItemStackCentrifuge(ItemStack stack) {

			super(stack);
			oreID = getOreID(stack);
		}

		@Override
		public ComparableItemStackCentrifuge set(ItemStack stack) {

			super.set(stack);
			oreID = getOreID(stack);

			return this;
		}
	}

}
