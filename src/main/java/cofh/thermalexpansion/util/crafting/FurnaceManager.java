package cofh.thermalexpansion.util.crafting;

import cofh.core.util.oredict.OreDictionaryArbiter;
import cofh.lib.inventory.ComparableItemStack;
import cofh.lib.util.helpers.ItemHelper;
import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalexpansion.api.crafting.recipes.IFurnaceRecipe;
import cofh.thermalfoundation.item.TFItems;

import gnu.trove.map.hash.THashMap;
import gnu.trove.set.hash.THashSet;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraftforge.oredict.OreDictionary;

public class FurnaceManager {

	private static Map<ComparableItemStackFurnace, RecipeFurnace> recipeMap = new THashMap<ComparableItemStackFurnace, RecipeFurnace>();
	private static Set<ComparableItemStackFurnace> foodSet = new THashSet<ComparableItemStackFurnace>();
	private static boolean allowOverwrite = false;
	public static final int DEFAULT_ENERGY = 1600;

	private static Set<Block> handledBlocks = new THashSet<Block>();

	static {
		allowOverwrite = ThermalExpansion.config.get("RecipeManagers.Furnace", "AllowRecipeOverwrite", false);

		handledBlocks.add(Blocks.cactus);
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
		ComparableItemStackFurnace query = new ComparableItemStackFurnace(input);

		RecipeFurnace recipe = recipeMap.get(query);

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

	public static boolean isFoodItem(ItemStack input) {

		if (input == null) {
			return false;
		}
		ComparableItemStackFurnace query = new ComparableItemStackFurnace(input);

		if (foodSet.contains(query)) {
			return true;
		}
		query.metadata = OreDictionary.WILDCARD_VALUE;
		return foodSet.contains(query);
	}

	public static void addDefaultRecipes() {

		addTERecipe(DEFAULT_ENERGY / 2, new ItemStack(Blocks.cactus), new ItemStack(Items.dye, 1, 2));
		addTERecipe(DEFAULT_ENERGY * 2, new ItemStack(Blocks.hay_block), new ItemStack(Items.coal, 1, 1));

		addTERecipe(DEFAULT_ENERGY / 2, new ItemStack(Items.porkchop), new ItemStack(Items.cooked_porkchop));
		addTERecipe(DEFAULT_ENERGY / 2, new ItemStack(Items.beef), new ItemStack(Items.cooked_beef));
		addTERecipe(DEFAULT_ENERGY / 2, new ItemStack(Items.chicken), new ItemStack(Items.cooked_chicken));
		addTERecipe(DEFAULT_ENERGY / 2, new ItemStack(Items.potato), new ItemStack(Items.baked_potato));

		foodSet.add(new ComparableItemStackFurnace(new ItemStack(Items.porkchop)));
		foodSet.add(new ComparableItemStackFurnace(new ItemStack(Items.beef)));
		foodSet.add(new ComparableItemStackFurnace(new ItemStack(Items.chicken)));
		foodSet.add(new ComparableItemStackFurnace(new ItemStack(Items.potato)));

		for (int i = 0; i < 2; i++) {
			addTERecipe(DEFAULT_ENERGY / 2, new ItemStack(Items.fish, 1, i), new ItemStack(Items.cooked_fished, 1, i));
			foodSet.add(new ComparableItemStackFurnace(new ItemStack(Items.fish, 1, i)));
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

		energy = DEFAULT_ENERGY * 6 / 16;

		addOreDictRecipe(energy, "oreberryIron", TFItems.nuggetIron);
		addOreDictRecipe(energy, "oreberryGold", TFItems.nuggetGold);
		addOreDictRecipe(energy, "oreberryCopper", TFItems.nuggetCopper);
		addOreDictRecipe(energy, "oreberryTin", TFItems.nuggetTin);
		addOreDictRecipe(energy, "oreberrySilver", TFItems.nuggetSilver);
		addOreDictRecipe(energy, "oreberryLead", TFItems.nuggetLead);
		addOreDictRecipe(energy, "oreberryNickel", TFItems.nuggetNickel);
		addOreDictRecipe(energy, "oreberryPlatinum", TFItems.nuggetPlatinum);
	}

	public static void loadRecipes() {

		Map<ItemStack, ItemStack> smeltingList = FurnaceRecipes.smelting().getSmeltingList();
		ItemStack output;

		for (ItemStack key : smeltingList.keySet()) {
			if (key == null || recipeExists(key)) {
				continue;
			}
			output = smeltingList.get(key);
			if (output == null || handledBlocks.contains(Block.getBlockFromItem(key.getItem()))) {
				continue;
			}
			int energy = DEFAULT_ENERGY;
			if (output.getItem() instanceof ItemFood) {
				foodSet.add(new ComparableItemStackFurnace(key));
				energy /= 2;
			}
			if (ItemHelper.isDust(key) && ItemHelper.isIngot(output)) {
				addRecipe(energy * 10 / 16, key, output, false);
			} else {
				if (ItemHelper.getItemDamage(key) == OreDictionary.WILDCARD_VALUE) {
					ItemStack testKey = ItemHelper.cloneStack(key);
					testKey.setItemDamage(0);

					if (ItemHelper.hasOreName(testKey) && ComparableItemStackFurnace.safeOreType(ItemHelper.getOreName(testKey))) {
						addRecipe(energy, testKey, output, false);
						continue;
					}
				}
				addRecipe(energy, key, output, false);
			}
		}
	}

	public static void refreshRecipes() {

		Map<ComparableItemStackFurnace, RecipeFurnace> tempMap = new THashMap<ComparableItemStackFurnace, RecipeFurnace>(recipeMap.size());
		Set<ComparableItemStackFurnace> tempSet = new THashSet<ComparableItemStackFurnace>();
		RecipeFurnace tempRecipe;

		for (Entry<ComparableItemStackFurnace, RecipeFurnace> entry : recipeMap.entrySet()) {
			tempRecipe = entry.getValue();
			tempMap.put(new ComparableItemStackFurnace(tempRecipe.input), tempRecipe);

			if (tempRecipe.isOutputFood()) {
				tempSet.add(new ComparableItemStackFurnace(tempRecipe.input));
			}
		}
		recipeMap.clear();
		recipeMap = tempMap;
		foodSet.clear();
		foodSet = tempSet;
	}

	/* ADD RECIPES */
	public static boolean addTERecipe(int energy, ItemStack input, ItemStack output) {

		if (input == null || output == null || energy <= 0) {
			return false;
		}
		RecipeFurnace recipe = new RecipeFurnace(input, output, energy);
		recipeMap.put(new ComparableItemStackFurnace(input), recipe);
		return true;
	}

	public static boolean addRecipe(int energy, ItemStack input, ItemStack output, boolean overwrite) {

		if (input == null || output == null || energy <= 0 || !(allowOverwrite & overwrite) && recipeMap.get(new ComparableItemStackFurnace(input)) != null) {
			return false;
		}
		RecipeFurnace recipe = new RecipeFurnace(input, output, energy);
		recipeMap.put(new ComparableItemStackFurnace(input), recipe);
		return true;
	}

	/* REMOVE RECIPES */
	public static boolean removeRecipe(ItemStack input) {

		return recipeMap.remove(new ComparableItemStackFurnace(input)) != null;
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
	public static class RecipeFurnace implements IFurnaceRecipe {

		final ItemStack input;
		final ItemStack output;
		final int energy;

		boolean isOutputFood;

		RecipeFurnace(ItemStack input, ItemStack output, int energy) {

			this.input = input;
			this.output = output;
			this.energy = energy;

			if (input.stackSize <= 0) {
				input.stackSize = 1;
			}
			if (output.stackSize <= 0) {
				output.stackSize = 1;
			}
			if (output.getItem() instanceof ItemFood) {
				isOutputFood = true;
			}
		}

		@Override
		public boolean isOutputFood() {

			return isOutputFood;
		}

		@Override
		public ItemStack getInput() {

			return input.copy();
		}

		@Override
		public ItemStack getOutput() {

			return output.copy();
		}

		@Override
		public int getEnergy() {

			return energy;
		}
	}

	/* ITEMSTACK CLASS */
	public static class ComparableItemStackFurnace extends ComparableItemStack {

		static final String ORE = "ore";
		static final String DUST = "dust";

		public static boolean safeOreType(String oreName) {

			return oreName.startsWith(ORE) || oreName.startsWith(DUST);
		}

		public static int getOreID(ItemStack stack) {

			int id = ItemHelper.oreProxy.getOreID(stack);

			if (id == -1 || !safeOreType(ItemHelper.oreProxy.getOreName(id))) {
				return -1;
			}
			return id;
		}

		public ComparableItemStackFurnace(ItemStack stack) {

			super(stack);
			oreID = getOreID(stack);
		}

		public ComparableItemStackFurnace(Item item, int damage, int stackSize) {

			super(item, damage, stackSize);
			this.oreID = getOreID(this.toItemStack());
		}

		@Override
		public ComparableItemStackFurnace set(ItemStack stack) {

			super.set(stack);
			oreID = getOreID(stack);

			return this;
		}
	}

}
