package cofh.thermalexpansion.util.crafting;

import cofh.core.util.oredict.OreDictionaryArbiter;
import cofh.lib.inventory.ComparableItemStack;
import cofh.lib.util.helpers.ItemHelper;
import cofh.thermalfoundation.item.ItemMaterial;
import gnu.trove.map.hash.THashMap;
import gnu.trove.set.hash.THashSet;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraftforge.oredict.OreDictionary;

import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class FurnaceManager {

	private static Map<ComparableItemStackFurnace, RecipeFurnace> recipeMap = new THashMap<>();
	private static Set<ComparableItemStackFurnace> foodSet = new THashSet<>();
	private static Set<ComparableItemStackFurnace> oreSet = new THashSet<>();

	static final int DEFAULT_ENERGY = 2000;

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

		return recipeMap.values().toArray(new RecipeFurnace[recipeMap.values().size()]);
	}

	public static boolean isFood(ItemStack input) {

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

	public static boolean isOre(ItemStack input) {

		if (input == null) {
			return false;
		}
		ComparableItemStackFurnace query = new ComparableItemStackFurnace(input);

		if (oreSet.contains(query)) {
			return true;
		}
		query.metadata = OreDictionary.WILDCARD_VALUE;
		return oreSet.contains(query);
	}

	public static void addDefaultRecipes() {

		/* SPECIAL */
		{
			addRecipe(DEFAULT_ENERGY / 2, new ItemStack(Blocks.CACTUS), new ItemStack(Items.DYE, 1, 2));
			addRecipe(DEFAULT_ENERGY, new ItemStack(Blocks.HAY_BLOCK), new ItemStack(Items.COAL, 1, 1));
			// addRecipe(DEFAULT_ENERGY * 2, new ItemStack(Items.ROTTEN_FLESH, 2, 0), new ItemStack(Items.LEATHER));
		}

		/* FOOD */
		{
			int energy = DEFAULT_ENERGY / 2;

			addRecipe(energy, new ItemStack(Items.PORKCHOP, 1, OreDictionary.WILDCARD_VALUE), new ItemStack(Items.COOKED_PORKCHOP));
			addRecipe(energy, new ItemStack(Items.BEEF, 1, OreDictionary.WILDCARD_VALUE), new ItemStack(Items.COOKED_BEEF));
			addRecipe(energy, new ItemStack(Items.CHICKEN, 1, OreDictionary.WILDCARD_VALUE), new ItemStack(Items.COOKED_CHICKEN));
			addRecipe(energy, new ItemStack(Items.MUTTON, 1, OreDictionary.WILDCARD_VALUE), new ItemStack(Items.COOKED_MUTTON));
			addRecipe(energy, new ItemStack(Items.RABBIT, 1, OreDictionary.WILDCARD_VALUE), new ItemStack(Items.COOKED_RABBIT));
			addRecipe(energy, new ItemStack(Items.POTATO, 1, OreDictionary.WILDCARD_VALUE), new ItemStack(Items.BAKED_POTATO));
			addRecipe(energy, new ItemStack(Items.FISH, 1, 0), new ItemStack(Items.COOKED_FISH, 1, 0));
			addRecipe(energy, new ItemStack(Items.FISH, 1, 1), new ItemStack(Items.COOKED_FISH, 1, 1));

			foodSet.add(new ComparableItemStackFurnace(new ItemStack(Items.PORKCHOP, 1, OreDictionary.WILDCARD_VALUE)));
			foodSet.add(new ComparableItemStackFurnace(new ItemStack(Items.BEEF, 1, OreDictionary.WILDCARD_VALUE)));
			foodSet.add(new ComparableItemStackFurnace(new ItemStack(Items.CHICKEN, 1, OreDictionary.WILDCARD_VALUE)));
			foodSet.add(new ComparableItemStackFurnace(new ItemStack(Items.MUTTON, 1, OreDictionary.WILDCARD_VALUE)));
			foodSet.add(new ComparableItemStackFurnace(new ItemStack(Items.RABBIT, 1, OreDictionary.WILDCARD_VALUE)));
			foodSet.add(new ComparableItemStackFurnace(new ItemStack(Items.POTATO, 1, OreDictionary.WILDCARD_VALUE)));
			foodSet.add(new ComparableItemStackFurnace(new ItemStack(Items.FISH, 1, 0)));
			foodSet.add(new ComparableItemStackFurnace(new ItemStack(Items.FISH, 1, 1)));
		}

		/* ORES */
		{
			int energy = DEFAULT_ENERGY;

			addOreDictRecipe(energy, "oreIron", ItemMaterial.ingotIron);
			addOreDictRecipe(energy, "oreGold", ItemMaterial.ingotGold);

			addOreDictRecipe(energy, "oreCopper", ItemMaterial.ingotCopper);
			addOreDictRecipe(energy, "oreTin", ItemMaterial.ingotTin);
			addOreDictRecipe(energy, "oreSilver", ItemMaterial.ingotSilver);
			addOreDictRecipe(energy, "oreLead", ItemMaterial.ingotLead);
			addOreDictRecipe(energy, "oreAluminum", ItemMaterial.ingotAluminum);
			addOreDictRecipe(energy, "oreNickel", ItemMaterial.ingotNickel);
			addOreDictRecipe(energy, "orePlatinum", ItemMaterial.ingotPlatinum);
			addOreDictRecipe(energy, "oreIridium", ItemMaterial.ingotIridium);
			addOreDictRecipe(energy, "oreMithril", ItemMaterial.ingotMithril);

			addOreDictRecipe(energy, "oreCoal", new ItemStack(Items.COAL, 1, 0));
			addOreDictRecipe(energy, "oreDiamond", new ItemStack(Items.DIAMOND, 1, 0));
			addOreDictRecipe(energy, "oreEmerald", new ItemStack(Items.EMERALD, 1, 0));
			addOreDictRecipe(energy, "oreLapis", new ItemStack(Items.DYE, 6, 4));
			addOreDictRecipe(energy, "oreRedstone", new ItemStack(Items.REDSTONE, 3, 0));
			addOreDictRecipe(energy, "oreQuartz", new ItemStack(Items.QUARTZ, 1, 0));
		}

		/* DUSTS */
		{
			int energy = DEFAULT_ENERGY * 14 / 20;

			addOreDictRecipe(energy, "dustIron", ItemMaterial.ingotIron);
			addOreDictRecipe(energy, "dustGold", ItemMaterial.ingotGold);

			addOreDictRecipe(energy, "dustCopper", ItemMaterial.ingotCopper);
			addOreDictRecipe(energy, "dustTin", ItemMaterial.ingotTin);
			addOreDictRecipe(energy, "dustSilver", ItemMaterial.ingotSilver);
			addOreDictRecipe(energy, "dustLead", ItemMaterial.ingotLead);
			addOreDictRecipe(energy, "dustAluminum", ItemMaterial.ingotAluminum);
			addOreDictRecipe(energy, "dustNickel", ItemMaterial.ingotNickel);
			addOreDictRecipe(energy, "dustPlatinum", ItemMaterial.ingotPlatinum);
			addOreDictRecipe(energy, "dustIridium", ItemMaterial.ingotIridium);
			addOreDictRecipe(energy, "dustMithril", ItemMaterial.ingotMithril);

			// No Steel
			addOreDictRecipe(energy, "dustElectrum", ItemMaterial.ingotElectrum);
			addOreDictRecipe(energy, "dustInvar", ItemMaterial.ingotInvar);
			addOreDictRecipe(energy, "dustBronze", ItemMaterial.ingotBronze);
			addOreDictRecipe(energy, "dustSignalum", ItemMaterial.ingotSignalum);
			addOreDictRecipe(energy, "dustLumium", ItemMaterial.ingotLumium);
			// No Enderium
		}

		/* OREBERRIES */
		{
			int energy = DEFAULT_ENERGY * 8 / 20;
			// addOreDictRecipe(energy, "oreberryIron", ItemMaterial.nuggetIron);
			// addOreDictRecipe(energy, "oreberryGold", ItemMaterial.nuggetGold);
			addOreDictRecipe(energy, "oreberryCopper", ItemMaterial.nuggetCopper);
			addOreDictRecipe(energy, "oreberryTin", ItemMaterial.nuggetTin);
			addOreDictRecipe(energy, "oreberrySilver", ItemMaterial.nuggetSilver);
			addOreDictRecipe(energy, "oreberryLead", ItemMaterial.nuggetLead);
			addOreDictRecipe(energy, "oreberryAluminum", ItemMaterial.nuggetAluminum);
			addOreDictRecipe(energy, "oreberryNickel", ItemMaterial.nuggetNickel);
			addOreDictRecipe(energy, "oreberryPlatinum", ItemMaterial.nuggetPlatinum);
			addOreDictRecipe(energy, "oreberryIridium", ItemMaterial.nuggetIridium);
			addOreDictRecipe(energy, "oreberryMithril", ItemMaterial.nuggetMithril);

			// addOreDictRecipe(energy, "oreberrySteel", ItemMaterial.nuggetSteel);
			addOreDictRecipe(energy, "oreberryElectrum", ItemMaterial.nuggetElectrum);
			addOreDictRecipe(energy, "oreberryInvar", ItemMaterial.nuggetInvar);
			addOreDictRecipe(energy, "oreberryBronze", ItemMaterial.nuggetBronze);
			addOreDictRecipe(energy, "oreberrySignalum", ItemMaterial.nuggetSignalum);
			addOreDictRecipe(energy, "oreberryLumium", ItemMaterial.nuggetLumium);
			// addOreDictRecipe(energy, "oreberryEnderium", ItemMaterial.nuggetEnderium);
		}
	}

	public static void loadRecipes() {

		Map<ItemStack, ItemStack> smeltingList = FurnaceRecipes.instance().getSmeltingList();
		Set<Block> handledBlocks = new THashSet<>();
		ItemStack output;

		handledBlocks.add(Blocks.CACTUS);
		handledBlocks.add(Blocks.GOLD_ORE);
		handledBlocks.add(Blocks.IRON_ORE);
		handledBlocks.add(Blocks.COAL_ORE);
		handledBlocks.add(Blocks.DIAMOND_ORE);
		handledBlocks.add(Blocks.EMERALD_ORE);
		handledBlocks.add(Blocks.LAPIS_ORE);
		handledBlocks.add(Blocks.REDSTONE_ORE);
		handledBlocks.add(Blocks.QUARTZ_ORE);

		for (ItemStack key : smeltingList.keySet()) {
			if (key == null || key.getItem() == null || recipeExists(key)) {
				continue;
			}
			output = smeltingList.get(key);
			if (output == null || handledBlocks.contains(Block.getBlockFromItem(key.getItem()))) {
				continue;
			}
			int energy = DEFAULT_ENERGY;

			/* FOOD */
			if (output.getItem() instanceof ItemFood) {
				foodSet.add(new ComparableItemStackFurnace(key));
				energy /= 2;
			}
			/* DUST */
			if (ItemHelper.isDust(key) && ItemHelper.isIngot(output)) {
				addRecipe(energy * 14 / 20, key, output);

			/* STANDARD */
			} else {
				if (ItemHelper.getItemDamage(key) == OreDictionary.WILDCARD_VALUE) {
					ItemStack testKey = ItemHelper.cloneStack(key);
					testKey.setItemDamage(0);

					if (ItemHelper.hasOreName(testKey) && ComparableItemStackFurnace.safeOreType(ItemHelper.getOreName(testKey))) {
						addRecipe(energy, testKey, output);
						continue;
					}
				}
				addRecipe(energy, key, output);
			}
		}
	}

	public static void refreshRecipes() {

		Map<ComparableItemStackFurnace, RecipeFurnace> tempMap = new THashMap<>(recipeMap.size());
		Set<ComparableItemStackFurnace> tempFood = new THashSet<>();
		Set<ComparableItemStackFurnace> tempOre = new THashSet<>();
		RecipeFurnace tempRecipe;

		for (Entry<ComparableItemStackFurnace, RecipeFurnace> entry : recipeMap.entrySet()) {
			tempRecipe = entry.getValue();
			tempMap.put(new ComparableItemStackFurnace(tempRecipe.input), tempRecipe);
		}
		for (ComparableItemStackFurnace entry : foodSet) {
			ComparableItemStackFurnace food = new ComparableItemStackFurnace(new ItemStack(entry.item, entry.stackSize, entry.metadata));
			tempFood.add(food);
		}
		for (ComparableItemStackFurnace entry : oreSet) {
			ComparableItemStackFurnace ore = new ComparableItemStackFurnace(new ItemStack(entry.item, entry.stackSize, entry.metadata));
			tempOre.add(ore);
		}
		recipeMap.clear();
		recipeMap = tempMap;
		foodSet.clear();
		foodSet = tempFood;
		oreSet.clear();
		oreSet = tempOre;
	}

	/* ADD RECIPES */
	public static boolean addRecipe(int energy, ItemStack input, ItemStack output) {

		if (input == null || output == null || energy <= 0 || recipeExists(input)) {
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

	/* HELPERS */
	private static void addOreDictRecipe(int energy, String oreName, ItemStack output) {

		if (ItemHelper.oreNameExists(oreName) && !recipeExists(OreDictionary.getOres(oreName).get(0))) {
			addRecipe(energy, ItemHelper.cloneStack(OreDictionary.getOres(oreName).get(0), 1), output);

			if (oreName.startsWith("ore") && ItemHelper.isIngot(output)) {
				oreSet.add(new ComparableItemStackFurnace(OreDictionary.getOres(oreName).get(0)));
			}
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

			if (input.stackSize <= 0) {
				input.stackSize = 1;
			}
			if (output.stackSize <= 0) {
				output.stackSize = 1;
			}
		}

		public ItemStack getInput() {

			return input;
		}

		public ItemStack getOutput() {

			return output;
		}

		public int getEnergy() {

			return energy;
		}
	}

	/* ITEMSTACK CLASS */
	public static class ComparableItemStackFurnace extends ComparableItemStack {

		public static final String ORE = "ore";
		public static final String DUST = "dust";

		public static boolean safeOreType(String oreName) {

			return oreName.startsWith(ORE) || oreName.startsWith(DUST);
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

		public ComparableItemStackFurnace(ItemStack stack) {

			super(stack);
			oreID = getOreID(stack);
		}

		@Override
		public ComparableItemStackFurnace set(ItemStack stack) {

			super.set(stack);
			oreID = getOreID(stack);

			return this;
		}
	}

}
