package cofh.thermalexpansion.util.managers.machine;

import cofh.core.inventory.ComparableItemStack;
import cofh.core.util.helpers.ItemHelper;
import cofh.core.util.oredict.OreDictionaryArbiter;
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

	private static Map<ComparableItemStackCentrifuge, CentrifugeRecipe> recipeMap = new THashMap<>();

	public static final int DEFAULT_ENERGY = 4000;

	public static CentrifugeRecipe getRecipe(ItemStack input) {

		if (input.isEmpty()) {
			return null;
		}
		ComparableItemStackCentrifuge query = new ComparableItemStackCentrifuge(input);

		CentrifugeRecipe recipe = recipeMap.get(query);

		if (recipe == null) {
			query.metadata = OreDictionary.WILDCARD_VALUE;
			recipe = recipeMap.get(query);
		}
		return recipe;
	}

	public static boolean recipeExists(ItemStack input) {

		return getRecipe(input) != null;
	}

	public static CentrifugeRecipe[] getRecipeList() {

		return recipeMap.values().toArray(new CentrifugeRecipe[recipeMap.size()]);
	}

	public static void initialize() {

		int energy = DEFAULT_ENERGY;

		addRecipe(energy, new ItemStack(Items.MAGMA_CREAM), Arrays.asList(new ItemStack(Items.SLIME_BALL), new ItemStack(Items.BLAZE_POWDER)), null);

		addRecipe(energy, ItemHelper.cloneStack(ItemMaterial.dustElectrum, 2), Arrays.asList(ItemHelper.cloneStack(ItemMaterial.dustGold), ItemHelper.cloneStack(ItemMaterial.dustSilver)), null);
		addRecipe(energy, ItemHelper.cloneStack(ItemMaterial.dustInvar, 3), Arrays.asList(ItemHelper.cloneStack(ItemMaterial.dustIron, 2), ItemHelper.cloneStack(ItemMaterial.dustNickel)), null);
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
	public static CentrifugeRecipe addRecipe(int energy, ItemStack input, List<ItemStack> output, List<Integer> chance, FluidStack fluid) {

		if (input.isEmpty() || (output.isEmpty() && fluid == null) || output.size() > 4 || energy <= 0 || recipeExists(input)) {
			return null;
		}
		CentrifugeRecipe recipe = new CentrifugeRecipe(input, output, chance, fluid, energy);
		recipeMap.put(new ComparableItemStackCentrifuge(input), recipe);
		return recipe;
	}

	public static CentrifugeRecipe addRecipe(int energy, ItemStack input, List<ItemStack> output, FluidStack fluid) {

		if (input.isEmpty() || (output.isEmpty() && fluid == null) || output.size() > 4 || energy <= 0 || recipeExists(input)) {
			return null;
		}
		CentrifugeRecipe recipe = new CentrifugeRecipe(input, output, null, fluid, energy);
		recipeMap.put(new ComparableItemStackCentrifuge(input), recipe);
		return recipe;
	}

	/* REMOVE RECIPES */
	public static CentrifugeRecipe removeRecipe(ItemStack input) {

		return recipeMap.remove(new ComparableItemStackCentrifuge(input));
	}

	public static void refresh() {

		Map<ComparableItemStackCentrifuge, CentrifugeRecipe> tempMap = new THashMap<>(recipeMap.size());
		CentrifugeRecipe tempRecipe;

		for (Entry<ComparableItemStackCentrifuge, CentrifugeRecipe> entry : recipeMap.entrySet()) {
			tempRecipe = entry.getValue();
			tempMap.put(new ComparableItemStackCentrifuge(tempRecipe.input), tempRecipe);
		}
		recipeMap.clear();
		recipeMap = tempMap;
	}

	/* RECIPE CLASS */
	public static class CentrifugeRecipe {

		final ItemStack input;
		final List<ItemStack> output;
		final List<Integer> chance;
		final FluidStack fluid;
		final int energy;

		CentrifugeRecipe(ItemStack input, @Nullable List<ItemStack> output, @Nullable List<Integer> chance, @Nullable FluidStack fluid, int energy) {

			this.input = input;
			this.output = new ArrayList<>();
			if (output != null) {
				this.output.addAll(output);
			}
			this.chance = new ArrayList<>();
			if (chance != null) {
				this.chance.addAll(chance);
			} else {
				for (int i = 0; i < this.output.size(); i++) {
					this.chance.add(100);
				}
			}
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

		public List<Integer> getChance() {

			return chance;
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

		public static final String DUST = "dust";

		public static boolean safeOreType(String oreName) {

			return oreName.startsWith(DUST);
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
