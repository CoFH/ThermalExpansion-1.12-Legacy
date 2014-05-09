package thermalexpansion.util.crafting;

import cofh.util.ItemHelper;
import cofh.util.inventory.ComparableItemStackSafe;

import geologic.item.GLItems;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraftforge.oredict.OreDictionary;

import thermalexpansion.ThermalExpansion;

public class FurnaceManager {

	private static Map<ComparableItemStackSafe, RecipeFurnace> recipeMap = new HashMap();
	private static ComparableItemStackSafe query = new ComparableItemStackSafe(new ItemStack(Block.stone));
	private static boolean allowOverwrite = false;
	public static final int DEFAULT_ENERGY = 1600;

	static {
		allowOverwrite = ThermalExpansion.config.get("tweak.crafting", "Furnace.AllowRecipeOverwrite", false);
	}

	public static RecipeFurnace getRecipe(ItemStack input) {

		if (input == null) {
			return null;
		}
		RecipeFurnace recipe = recipeMap.get(query.set(input));

		if (recipe == null) {
			query.metadata = OreDictionary.WILDCARD_VALUE;
			recipe = recipeMap.get(query);
		}
		return recipe;
	}

	public static boolean recipeExists(ItemStack input) {

		return getRecipe(input) != null;
	}

	public static RecipeFurnace[] getRecipeList() {

		return recipeMap.values().toArray(new RecipeFurnace[0]);
	}

	public static void addDefaultRecipes() {

		addTERecipe(800, new ItemStack(Blocks.cactus), new ItemStack(Items.dye, 1, 2));
		addTERecipe(400, new ItemStack(Items.porkchop), new ItemStack(Items.cooked_porkchop));
		addTERecipe(400, new ItemStack(Items.fish), new ItemStack(Items.cooked_fished));
		addTERecipe(400, new ItemStack(Items.beef), new ItemStack(Items.cooked_beef));
		addTERecipe(400, new ItemStack(Items.chicken), new ItemStack(Items.cooked_chicken));
		addTERecipe(400, new ItemStack(Items.potato), new ItemStack(Items.baked_potato));
	}

	public static void loadRecipes() {

		addDefaultRecipes();

		int energy = DEFAULT_ENERGY;

		addOreDictRecipe(energy, "oreCopper", GLItems.ingotCopper);
		addOreDictRecipe(energy, "oreTin", GLItems.ingotTin);
		addOreDictRecipe(energy, "oreSilver", GLItems.ingotSilver);
		addOreDictRecipe(energy, "oreLead", GLItems.ingotLead);
		addOreDictRecipe(energy, "oreNickel", GLItems.ingotNickel);
		addOreDictRecipe(energy, "orePlatinum", GLItems.ingotPlatinum);

		energy = DEFAULT_ENERGY * 10 / 16;

		addOreDictRecipe(energy, "dustCopper", GLItems.ingotCopper);
		addOreDictRecipe(energy, "dustTin", GLItems.ingotTin);
		addOreDictRecipe(energy, "dustSilver", GLItems.ingotSilver);
		addOreDictRecipe(energy, "dustLead", GLItems.ingotLead);
		addOreDictRecipe(energy, "dustNickel", GLItems.ingotNickel);
		addOreDictRecipe(energy, "dustPlatinum", GLItems.ingotPlatinum);
		addOreDictRecipe(energy, "dustElectrum", GLItems.ingotElectrum);
		addOreDictRecipe(energy, "dustInvar", GLItems.ingotInvar);
		addOreDictRecipe(energy, "dustBronze", GLItems.ingotBronze);

		Map<List<Integer>, ItemStack> metaSmeltingList = FurnaceRecipes.smelting().getMetaSmeltingList();
		Map<Integer, ItemStack> smeltingList = FurnaceRecipes.smelting().getSmeltingList();

		ItemStack input;
		ItemStack output;

		for (List<Integer> key : metaSmeltingList.keySet()) {

			energy = DEFAULT_ENERGY;

			input = new ItemStack(key.get(0), 1, key.get(1));
			output = metaSmeltingList.get(key);

			if (ItemHelper.isDust(input) && ItemHelper.isIngot(output)) {
				addRecipe(energy * 10 / 16, input, output, false);
			} else {
				addRecipe(energy, input, output, false);
			}
		}
		for (Integer key : smeltingList.keySet()) {

			energy = DEFAULT_ENERGY;

			input = new ItemStack(key, 1, 0);

			if (recipeExists(input)) {
				continue;
			}
			if (!ItemHelper.hasOreName(input) || ComparableItemStackSafe.getOreID(input) == -1) {
				input = new ItemStack(key, 1, OreDictionary.WILDCARD_VALUE);
			}
			output = smeltingList.get(key);

			if (ItemHelper.isDust(input) && ItemHelper.isIngot(output)) {
				addRecipe(energy * 10 / 16, input, output, false);
			} else {
				addRecipe(energy, input, output, false);
			}
		}
	}

	/* ADD RECIPES */
	public static boolean addTERecipe(int energy, ItemStack input, ItemStack output) {

		if (input == null || output == null || energy <= 0) {
			return false;
		}
		RecipeFurnace recipe = new RecipeFurnace(input, output, energy);
		recipeMap.put(new ComparableItemStackSafe(input), recipe);
		return true;
	}

	public static boolean addRecipe(int energy, ItemStack input, ItemStack output, boolean overwrite) {

		if (input == null || output == null || energy <= 0 || !(allowOverwrite & overwrite) && recipeMap.get(query.set(input)) != null) {
			return false;
		}
		RecipeFurnace recipe = new RecipeFurnace(input, output, energy);
		recipeMap.put(new ComparableItemStackSafe(input), recipe);
		return true;
	}

	/* HELPER FUNCTIONS */
	public static void addOreDictRecipe(String oreName, ItemStack output) {

		addOreDictRecipe(DEFAULT_ENERGY, oreName, output);
	}

	public static void addOreDictRecipe(int energy, String oreName, ItemStack output) {

		if (ItemHelper.oreNameExists(oreName)) {
			addRecipe(energy, ItemHelper.cloneStack(OreDictionary.getOres(oreName).get(0), 1), output, false);
		}
	}

	/* RECIPE CLASS */
	public static class RecipeFurnace {

		final ItemStack input;
		final ItemStack output;
		final int energy;

		RecipeFurnace(ItemStack input, ItemStack output, int energy) {

			this.input = input;
			this.output = output;
			this.energy = energy;
		}

		public ItemStack getInput() {

			return input.copy();
		}

		public ItemStack getOutput() {

			return output.copy();
		}

		public int getEnergy() {

			return energy;
		}
	}

}
