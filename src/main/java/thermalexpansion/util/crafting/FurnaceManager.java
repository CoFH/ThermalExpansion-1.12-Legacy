package thermalexpansion.util.crafting;

import cofh.core.util.oredict.OreDictionaryArbiter;
import cofh.lib.inventory.ComparableItemStackSafe;
import cofh.lib.util.helpers.ItemHelper;

import gnu.trove.map.hash.THashMap;
import gnu.trove.set.hash.THashSet;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraftforge.oredict.OreDictionary;

import thermalexpansion.ThermalExpansion;
import thermalfoundation.item.TFItems;

public class FurnaceManager {

	private static Map<ComparableItemStackSafe, RecipeFurnace> recipeMap = new THashMap();
	private static ComparableItemStackSafe query = new ComparableItemStackSafe(new ItemStack(Blocks.stone));
	private static boolean allowOverwrite = false;
	public static final int DEFAULT_ENERGY = 1600;

	private static Set<Block> handledBlocks = new THashSet();

	static {
		allowOverwrite = ThermalExpansion.config.get("tweak.crafting", "Furnace.AllowRecipeOverwrite", false);

		handledBlocks.add(Blocks.gold_ore);
		handledBlocks.add(Blocks.iron_ore);
		handledBlocks.add(Blocks.coal_ore);
		handledBlocks.add(Blocks.diamond_ore);
		handledBlocks.add(Blocks.emerald_ore);
		handledBlocks.add(Blocks.lapis_ore);
		handledBlocks.add(Blocks.redstone_ore);
		handledBlocks.add(Blocks.quartz_ore);
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
		addTERecipe(400, new ItemStack(Items.beef), new ItemStack(Items.cooked_beef));
		addTERecipe(400, new ItemStack(Items.chicken), new ItemStack(Items.cooked_chicken));
		addTERecipe(400, new ItemStack(Items.potato), new ItemStack(Items.baked_potato));

		for (int i = 0; i < 2; i++) {
			addTERecipe(400, new ItemStack(Items.fish, 1, i), new ItemStack(Items.cooked_fished, 1, i));
		}

		int energy = DEFAULT_ENERGY;

		addOreDictRecipe("oreIron", TFItems.ingotIron);
		addOreDictRecipe("oreGold", TFItems.ingotGold);
		addOreDictRecipe("oreCopper", TFItems.ingotCopper);
		addOreDictRecipe("oreTin", TFItems.ingotTin);
		addOreDictRecipe("oreSilver", TFItems.ingotSilver);
		addOreDictRecipe("oreLead", TFItems.ingotLead);
		addOreDictRecipe("oreNickel", TFItems.ingotNickel);
		addOreDictRecipe("orePlatinum", TFItems.ingotPlatinum);

		addOreDictRecipe("oreCoal", new ItemStack(Items.coal, 1, 0));
		addOreDictRecipe("oreDiamond", new ItemStack(Items.diamond, 1, 0));
		addOreDictRecipe("oreEmerald", new ItemStack(Items.emerald, 1, 0));
		addOreDictRecipe("oreLapis", new ItemStack(Items.dye, 6, 4));
		addOreDictRecipe("oreRedstone", new ItemStack(Items.redstone, 4, 0));
		addOreDictRecipe("oreQuartz", new ItemStack(Items.quartz, 1, 0));

		energy = DEFAULT_ENERGY * 10 / 16;

		addOreDictRecipe(energy, "dustIron", TFItems.ingotIron);
		addOreDictRecipe(energy, "dustGold", TFItems.ingotGold);
		addOreDictRecipe(energy, "dustCopper", TFItems.ingotCopper);
		addOreDictRecipe(energy, "dustTin", TFItems.ingotTin);
		addOreDictRecipe(energy, "dustSilver", TFItems.ingotSilver);
		addOreDictRecipe(energy, "dustLead", TFItems.ingotLead);
		addOreDictRecipe(energy, "dustNickel", TFItems.ingotNickel);
		addOreDictRecipe(energy, "dustPlatinum", TFItems.ingotPlatinum);
		addOreDictRecipe(energy, "dustElectrum", TFItems.ingotElectrum);
		addOreDictRecipe(energy, "dustInvar", TFItems.ingotInvar);
		addOreDictRecipe(energy, "dustBronze", TFItems.ingotBronze);

	}

	public static void loadRecipes() {

		Map<ItemStack, ItemStack> smeltingList = FurnaceRecipes.smelting().getSmeltingList();
		ItemStack output;

		int energy = DEFAULT_ENERGY;

		for (ItemStack key : smeltingList.keySet()) {
			if (recipeExists(key)) {
				continue;
			}
			if (handledBlocks.contains(Block.getBlockFromItem(key.getItem()))) {
				continue;
			}
			output = smeltingList.get(key);

			if (ItemHelper.isDust(key) && ItemHelper.isIngot(output)) {
				addRecipe(energy * 10 / 16, key, output, false);
			} else {
				addRecipe(energy, key, output, false);
			}
		}
	}

	public static void refreshRecipes() {

		Map<ComparableItemStackSafe, RecipeFurnace> tempMap = new THashMap(recipeMap.size());
		RecipeFurnace tempRecipe;

		for (Entry<ComparableItemStackSafe, RecipeFurnace> entry : recipeMap.entrySet()) {
			tempRecipe = entry.getValue();
			tempMap.put(new ComparableItemStackSafe(tempRecipe.input), tempRecipe);
		}
		recipeMap.clear();
		recipeMap = tempMap;
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

	public static boolean removeRecipe(ItemStack input) {

		return recipeMap.remove(new ComparableItemStackSafe(input)) != null;
	}

	/* HELPER FUNCTIONS */
	public static void addOreDictRecipe(String oreName, ItemStack output) {

		addOreDictRecipe(DEFAULT_ENERGY, oreName, output);
	}

	public static void addOreDictRecipe(int energy, String oreName, ItemStack output) {

		if (ItemHelper.oreNameExists(oreName)) {
			addRecipe(energy, ItemHelper.cloneStack(OreDictionaryArbiter.getOres(oreName).get(0), 1), output, false);
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
