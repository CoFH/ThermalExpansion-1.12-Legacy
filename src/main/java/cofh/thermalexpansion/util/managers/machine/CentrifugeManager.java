package cofh.thermalexpansion.util.managers.machine;

import cofh.core.inventory.ComparableItemStackSafe;
import cofh.core.util.helpers.ColorHelper;
import cofh.core.util.helpers.ItemHelper;
import cofh.thermalfoundation.init.TFFluids;
import cofh.thermalfoundation.item.ItemMaterial;
import gnu.trove.map.hash.THashMap;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
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

		addRecipe(energy * 2, ItemHelper.cloneStack(ItemMaterial.dustElectrum, 2), Arrays.asList(ItemHelper.cloneStack(ItemMaterial.dustGold), ItemHelper.cloneStack(ItemMaterial.dustSilver)), null);
		addRecipe(energy * 3, ItemHelper.cloneStack(ItemMaterial.dustInvar, 3), Arrays.asList(ItemHelper.cloneStack(ItemMaterial.dustIron, 2), ItemHelper.cloneStack(ItemMaterial.dustNickel)), null);
		addRecipe(energy * 4, ItemHelper.cloneStack(ItemMaterial.dustBronze, 4), Arrays.asList(ItemHelper.cloneStack(ItemMaterial.dustCopper, 3), ItemHelper.cloneStack(ItemMaterial.dustTin)), null);
		addRecipe(energy * 2, ItemHelper.cloneStack(ItemMaterial.dustConstantan, 2), Arrays.asList(ItemHelper.cloneStack(ItemMaterial.dustCopper), ItemHelper.cloneStack(ItemMaterial.dustNickel)), null);
		addRecipe(energy * 4, ItemHelper.cloneStack(ItemMaterial.dustSignalum, 4), Arrays.asList(ItemHelper.cloneStack(ItemMaterial.dustCopper, 3), ItemHelper.cloneStack(ItemMaterial.dustSilver)), new FluidStack(TFFluids.fluidRedstone, Fluid.BUCKET_VOLUME));
		addRecipe(energy * 4, ItemHelper.cloneStack(ItemMaterial.dustLumium, 4), Arrays.asList(ItemHelper.cloneStack(ItemMaterial.dustTin, 3), ItemHelper.cloneStack(ItemMaterial.dustSilver)), new FluidStack(TFFluids.fluidGlowstone, Fluid.BUCKET_VOLUME));
		addRecipe(energy * 4, ItemHelper.cloneStack(ItemMaterial.dustEnderium, 4), Arrays.asList(ItemHelper.cloneStack(ItemMaterial.dustLead, 3), ItemHelper.cloneStack(ItemMaterial.dustPlatinum)), new FluidStack(TFFluids.fluidEnder, Fluid.BUCKET_VOLUME));

		addRecipe(energy * 2, ItemHelper.cloneStack(ItemMaterial.dustPyrotheum, 2), Arrays.asList(ItemHelper.cloneStack(ItemMaterial.dustCoal), ItemHelper.cloneStack(ItemMaterial.dustSulfur), new ItemStack(Items.BLAZE_POWDER), new ItemStack(Items.REDSTONE)), null);
		addRecipe(energy * 2, ItemHelper.cloneStack(ItemMaterial.dustCryotheum, 2), Arrays.asList(ItemHelper.cloneStack(ItemMaterial.dustNiter), ItemHelper.cloneStack(ItemMaterial.dustBlizz), new ItemStack(Items.SNOWBALL), new ItemStack(Items.REDSTONE)), null);
		addRecipe(energy * 2, ItemHelper.cloneStack(ItemMaterial.dustAerotheum, 2), Arrays.asList(ItemHelper.cloneStack(ItemMaterial.dustNiter), ItemHelper.cloneStack(ItemMaterial.dustBlitz), new ItemStack(Blocks.SAND), new ItemStack(Items.REDSTONE)), null);
		addRecipe(energy * 2, ItemHelper.cloneStack(ItemMaterial.dustPetrotheum, 2), Arrays.asList(ItemHelper.cloneStack(ItemMaterial.dustObsidian), ItemHelper.cloneStack(ItemMaterial.dustBasalz), new ItemStack(Items.CLAY_BALL), new ItemStack(Items.REDSTONE)), null);

		/* CONCRETE POWDER */
		{
			int[] dyeChance = new int[ColorHelper.WOOL_COLOR_CONFIG.length];
			for (int i = 0; i < ColorHelper.WOOL_COLOR_CONFIG.length; i++) {
				dyeChance[i] = 10;
			}
			dyeChance[EnumDyeColor.WHITE.getMetadata()] = 0;
			dyeChance[EnumDyeColor.BROWN.getMetadata()] = 0;
			dyeChance[EnumDyeColor.BLUE.getMetadata()] = 0;
			dyeChance[EnumDyeColor.BLACK.getMetadata()] = 0;

			ItemStack gravel = new ItemStack(Blocks.GRAVEL);
			ItemStack sand = new ItemStack(Blocks.SAND);

			for (int i = 0; i < ColorHelper.WOOL_COLOR_CONFIG.length; i++) {
				if (dyeChance[i] > 0) {
					addRecipe(energy, new ItemStack(Blocks.CONCRETE_POWDER, 2, i), Arrays.asList(gravel, sand, new ItemStack(Items.DYE, 1, 15 - i)), Arrays.asList(100, 100, dyeChance[i]), null);
				} else {
					addRecipe(energy, new ItemStack(Blocks.CONCRETE_POWDER, 2, i), Arrays.asList(gravel, sand), null);
				}
			}
		}

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

		protected ItemStack input;
		protected List<ItemStack> output;
		protected List<Integer> chance;
		protected FluidStack fluid;
		protected int energy;

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
	public static class ComparableItemStackCentrifuge extends ComparableItemStackSafe {

		@Override
		public boolean safeOreType(String oreName) {

			return oreName.startsWith(DUST);
		}

		public ComparableItemStackCentrifuge(ItemStack stack) {

			super(stack);
		}
	}

}
