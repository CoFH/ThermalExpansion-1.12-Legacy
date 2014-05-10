package thermalexpansion.util.crafting;

import cofh.util.ItemHelper;
import cofh.util.inventory.ComparableItemStackSafe;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraftforge.oredict.OreDictionary;

import thermalexpansion.ThermalExpansion;
import thermalfoundation.item.TFItems;

public class FurnaceManager {

	private static Map<ComparableItemStackSafe, RecipeFurnace> recipeMap = new HashMap();
	private static ComparableItemStackSafe query = new ComparableItemStackSafe(new ItemStack(Blocks.stone));
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

		addOreDictRecipe(energy, "oreCopper", TFItems.ingotCopper);
		addOreDictRecipe(energy, "oreTin", TFItems.ingotTin);
		addOreDictRecipe(energy, "oreSilver", TFItems.ingotSilver);
		addOreDictRecipe(energy, "oreLead", TFItems.ingotLead);
		addOreDictRecipe(energy, "oreNickel", TFItems.ingotNickel);
		addOreDictRecipe(energy, "orePlatinum", TFItems.ingotPlatinum);

		energy = DEFAULT_ENERGY * 10 / 16;

		addOreDictRecipe(energy, "dustCopper", TFItems.ingotCopper);
		addOreDictRecipe(energy, "dustTin", TFItems.ingotTin);
		addOreDictRecipe(energy, "dustSilver", TFItems.ingotSilver);
		addOreDictRecipe(energy, "dustLead", TFItems.ingotLead);
		addOreDictRecipe(energy, "dustNickel", TFItems.ingotNickel);
		addOreDictRecipe(energy, "dustPlatinum", TFItems.ingotPlatinum);
		addOreDictRecipe(energy, "dustElectrum", TFItems.ingotElectrum);
		addOreDictRecipe(energy, "dustInvar", TFItems.ingotInvar);
		addOreDictRecipe(energy, "dustBronze", TFItems.ingotBronze);

		Map<ItemStack, ItemStack> smeltingList = FurnaceRecipes.smelting().getSmeltingList();
		ItemStack output;

		for (ItemStack key : smeltingList.keySet()) {

			energy = DEFAULT_ENERGY;

			if (recipeExists(key)) {
				continue;
			}
			if (!ItemHelper.hasOreName(key) || ComparableItemStackSafe.getOreID(key) == -1) {
				key.setItemDamage(OreDictionary.WILDCARD_VALUE);
			}
			output = smeltingList.get(key);

			if (ItemHelper.isDust(key) && ItemHelper.isIngot(output)) {
				addRecipe(energy * 10 / 16, key, output, false);
			} else {
				addRecipe(energy, key, output, false);
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
