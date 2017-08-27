package cofh.thermalexpansion.util.managers.machine;

import cofh.core.util.oredict.OreDictionaryArbiter;
import cofh.lib.inventory.ComparableItemStack;
import cofh.lib.inventory.InventoryCraftingFalse;
import cofh.lib.util.helpers.ItemHelper;
import cofh.thermalfoundation.item.ItemMaterial;
import gnu.trove.map.hash.THashMap;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class SawmillManager {

	private static Map<ComparableItemStackSawmill, SawmillRecipe> recipeMap = new THashMap<>();

	static final float LOG_MULTIPLIER = 1.5F;
	static final int DEFAULT_ENERGY = 1600;

	public static SawmillRecipe getRecipe(ItemStack input) {

		if (input.isEmpty()) {
			return null;
		}
		ComparableItemStackSawmill query = new ComparableItemStackSawmill(input);

		SawmillRecipe recipe = recipeMap.get(query);

		if (recipe == null) {
			query.metadata = OreDictionary.WILDCARD_VALUE;
			recipe = recipeMap.get(query);
		}
		return recipe;
	}

	public static boolean recipeExists(ItemStack input) {

		return getRecipe(input) != null;
	}

	public static SawmillRecipe[] getRecipeList() {

		return recipeMap.values().toArray(new SawmillRecipe[recipeMap.size()]);
	}

	public static void initialize() {

		/*
		 * Conversion rate as follows:
		 * Floor(Planks / 2)
		 * 1 Sawdust / 4 Planks (25% / Plank)
		 * 1 Sawdust / 4 Sticks (25% / Stick)
		 */

		/* VANILLA LOGS */
		{
			int energy = DEFAULT_ENERGY / 2;

			addRecipe(energy, new ItemStack(Blocks.LOG, 1, 0), new ItemStack(Blocks.PLANKS, (int) (4 * LOG_MULTIPLIER), 0), ItemMaterial.dustWood);
			addRecipe(energy, new ItemStack(Blocks.LOG, 1, 1), new ItemStack(Blocks.PLANKS, (int) (4 * LOG_MULTIPLIER), 1), ItemMaterial.dustWood);
			addRecipe(energy, new ItemStack(Blocks.LOG, 1, 2), new ItemStack(Blocks.PLANKS, (int) (4 * LOG_MULTIPLIER), 2), ItemMaterial.dustWood);
			addRecipe(energy, new ItemStack(Blocks.LOG, 1, 3), new ItemStack(Blocks.PLANKS, (int) (4 * LOG_MULTIPLIER), 3), ItemMaterial.dustWood);
			addRecipe(energy, new ItemStack(Blocks.LOG2, 1, 0), new ItemStack(Blocks.PLANKS, (int) (4 * LOG_MULTIPLIER), 4), ItemMaterial.dustWood);
			addRecipe(energy, new ItemStack(Blocks.LOG2, 1, 1), new ItemStack(Blocks.PLANKS, (int) (4 * LOG_MULTIPLIER), 5), ItemMaterial.dustWood);
		}

		/* MISC WOOD BLOCKS */
		{
			int energy = DEFAULT_ENERGY * 3 / 2;

			addRecipe(energy, new ItemStack(Blocks.CHEST), new ItemStack(Blocks.PLANKS, 4), ItemHelper.cloneStack(ItemMaterial.dustWood, 2));
			addRecipe(energy, new ItemStack(Blocks.CRAFTING_TABLE), new ItemStack(Blocks.PLANKS, 2), ItemMaterial.dustWood);
			addRecipe(energy / 2, new ItemStack(Blocks.WOODEN_BUTTON, 2), new ItemStack(Blocks.PLANKS, 1), ItemMaterial.dustWood, 25);
			addRecipe(energy, new ItemStack(Blocks.WOODEN_PRESSURE_PLATE), new ItemStack(Blocks.PLANKS, 1), ItemMaterial.dustWood, 50);
			addRecipe(energy, new ItemStack(Blocks.TRAPDOOR), new ItemStack(Blocks.PLANKS, 1), ItemMaterial.dustWood, 75);
			addRecipe(energy / 2, new ItemStack(Items.BOWL, 2), new ItemStack(Blocks.PLANKS, 1), ItemMaterial.dustWood, 25);
			addRecipe(energy, new ItemStack(Items.SIGN), new ItemStack(Blocks.PLANKS, 1), ItemMaterial.dustWood, 50);
		}

		/* DOORS */
		{
			int energy = DEFAULT_ENERGY * 3 / 2;

			addRecipe(energy, new ItemStack(Items.OAK_DOOR), new ItemStack(Blocks.PLANKS, 1, 0), ItemMaterial.dustWood, 50);
			addRecipe(energy, new ItemStack(Items.SPRUCE_DOOR), new ItemStack(Blocks.PLANKS, 1, 1), ItemMaterial.dustWood, 50);
			addRecipe(energy, new ItemStack(Items.BIRCH_DOOR), new ItemStack(Blocks.PLANKS, 1, 2), ItemMaterial.dustWood, 50);
			addRecipe(energy, new ItemStack(Items.JUNGLE_DOOR), new ItemStack(Blocks.PLANKS, 1, 3), ItemMaterial.dustWood, 50);
			addRecipe(energy, new ItemStack(Items.ACACIA_DOOR), new ItemStack(Blocks.PLANKS, 1, 4), ItemMaterial.dustWood, 50);
			addRecipe(energy, new ItemStack(Items.DARK_OAK_DOOR), new ItemStack(Blocks.PLANKS, 1, 5), ItemMaterial.dustWood, 50);
		}

		/* FENCES */
		{
			int energy = DEFAULT_ENERGY * 3 / 2;

			addRecipe(energy, new ItemStack(Blocks.OAK_FENCE), new ItemStack(Blocks.PLANKS, 1, 0), ItemMaterial.dustWood, 25);
			addRecipe(energy, new ItemStack(Blocks.SPRUCE_FENCE), new ItemStack(Blocks.PLANKS, 1, 1), ItemMaterial.dustWood, 25);
			addRecipe(energy, new ItemStack(Blocks.BIRCH_FENCE), new ItemStack(Blocks.PLANKS, 1, 2), ItemMaterial.dustWood, 25);
			addRecipe(energy, new ItemStack(Blocks.JUNGLE_FENCE), new ItemStack(Blocks.PLANKS, 1, 3), ItemMaterial.dustWood, 25);
			addRecipe(energy, new ItemStack(Blocks.ACACIA_FENCE), new ItemStack(Blocks.PLANKS, 1, 4), ItemMaterial.dustWood, 25);
			addRecipe(energy, new ItemStack(Blocks.DARK_OAK_FENCE), new ItemStack(Blocks.PLANKS, 1, 5), ItemMaterial.dustWood, 25);
		}

		/* FENCE GATES */
		{
			int energy = DEFAULT_ENERGY * 3 / 2;

			addRecipe(energy, new ItemStack(Blocks.OAK_FENCE_GATE), new ItemStack(Blocks.PLANKS, 1, 0), ItemMaterial.dustWood, 150);
			addRecipe(energy, new ItemStack(Blocks.SPRUCE_FENCE_GATE), new ItemStack(Blocks.PLANKS, 1, 1), ItemMaterial.dustWood, 150);
			addRecipe(energy, new ItemStack(Blocks.BIRCH_FENCE_GATE), new ItemStack(Blocks.PLANKS, 1, 2), ItemMaterial.dustWood, 150);
			addRecipe(energy, new ItemStack(Blocks.JUNGLE_FENCE_GATE), new ItemStack(Blocks.PLANKS, 1, 3), ItemMaterial.dustWood, 150);
			addRecipe(energy, new ItemStack(Blocks.ACACIA_FENCE_GATE), new ItemStack(Blocks.PLANKS, 1, 4), ItemMaterial.dustWood, 150);
			addRecipe(energy, new ItemStack(Blocks.DARK_OAK_FENCE_GATE), new ItemStack(Blocks.PLANKS, 1, 5), ItemMaterial.dustWood, 150);
		}

		/* STAIRS */
		{
			int energy = DEFAULT_ENERGY * 3 / 2;

			addRecipe(energy, new ItemStack(Blocks.OAK_STAIRS), new ItemStack(Blocks.PLANKS, 1, 0), ItemMaterial.dustWood, 25);
			addRecipe(energy, new ItemStack(Blocks.SPRUCE_STAIRS), new ItemStack(Blocks.PLANKS, 1, 1), ItemMaterial.dustWood, 25);
			addRecipe(energy, new ItemStack(Blocks.BIRCH_STAIRS), new ItemStack(Blocks.PLANKS, 1, 2), ItemMaterial.dustWood, 25);
			addRecipe(energy, new ItemStack(Blocks.JUNGLE_STAIRS), new ItemStack(Blocks.PLANKS, 1, 3), ItemMaterial.dustWood, 25);
			addRecipe(energy, new ItemStack(Blocks.ACACIA_STAIRS), new ItemStack(Blocks.PLANKS, 1, 4), ItemMaterial.dustWood, 25);
			addRecipe(energy, new ItemStack(Blocks.DARK_OAK_STAIRS), new ItemStack(Blocks.PLANKS, 1, 5), ItemMaterial.dustWood, 25);
		}

		/* BOATS */
		{
			int energy = DEFAULT_ENERGY * 3 / 2;

			addRecipe(energy, new ItemStack(Items.BOAT), new ItemStack(Blocks.PLANKS, 2, 0), ItemMaterial.dustWood, 125);
			addRecipe(energy, new ItemStack(Items.SPRUCE_BOAT), new ItemStack(Blocks.PLANKS, 2, 1), ItemMaterial.dustWood, 125);
			addRecipe(energy, new ItemStack(Items.BIRCH_BOAT), new ItemStack(Blocks.PLANKS, 2, 2), ItemMaterial.dustWood, 125);
			addRecipe(energy, new ItemStack(Items.JUNGLE_BOAT), new ItemStack(Blocks.PLANKS, 2, 3), ItemMaterial.dustWood, 125);
			addRecipe(energy, new ItemStack(Items.ACACIA_BOAT), new ItemStack(Blocks.PLANKS, 2, 4), ItemMaterial.dustWood, 125);
			addRecipe(energy, new ItemStack(Items.DARK_OAK_BOAT), new ItemStack(Blocks.PLANKS, 2, 5), ItemMaterial.dustWood, 125);
		}

		/* WOOD EQUIPMENT */
		{
			int energy = DEFAULT_ENERGY;

			addRecipe(energy, new ItemStack(Items.WOODEN_SWORD), new ItemStack(Blocks.PLANKS, 1), ItemMaterial.dustWood, 75);
			addRecipe(energy, new ItemStack(Items.WOODEN_SHOVEL), new ItemStack(Blocks.PLANKS, 1), ItemMaterial.dustWood, 75);
			addRecipe(energy, new ItemStack(Items.WOODEN_PICKAXE), new ItemStack(Blocks.PLANKS, 1), ItemMaterial.dustWood, 125);
			addRecipe(energy, new ItemStack(Items.WOODEN_AXE), new ItemStack(Blocks.PLANKS, 1), ItemMaterial.dustWood, 125);
			addRecipe(energy, new ItemStack(Items.WOODEN_HOE), new ItemStack(Blocks.PLANKS, 1), ItemMaterial.dustWood);
		}

		/* LEATHER EQUIPMENT */
		{
			int energy = DEFAULT_ENERGY;

			addRecipe(energy, new ItemStack(Items.LEATHER_HELMET), new ItemStack(Items.LEATHER, 2), new ItemStack(Items.LEATHER, 1), 50);
			addRecipe(energy, new ItemStack(Items.LEATHER_CHESTPLATE), new ItemStack(Items.LEATHER, 4), new ItemStack(Items.LEATHER, 1), 80);
			addRecipe(energy, new ItemStack(Items.LEATHER_LEGGINGS), new ItemStack(Items.LEATHER, 3), new ItemStack(Items.LEATHER, 1), 70);
			addRecipe(energy, new ItemStack(Items.LEATHER_BOOTS), new ItemStack(Items.LEATHER, 2), new ItemStack(Items.LEATHER, 1), 40);
		}

		/* MIXED OUTPUT - WOOD PRIMARY */
		{
			int energy = DEFAULT_ENERGY * 3 / 2;

			addRecipe(energy, new ItemStack(Items.BED), new ItemStack(Blocks.PLANKS, 1), new ItemStack(Blocks.WOOL, 2));
			addRecipe(energy, new ItemStack(Blocks.BOOKSHELF), new ItemStack(Blocks.PLANKS, 3), new ItemStack(Items.BOOK, 3), 25);
			addRecipe(energy, new ItemStack(Blocks.JUKEBOX), new ItemStack(Blocks.PLANKS, 4), new ItemStack(Items.DIAMOND, 1), 25);
			addRecipe(energy, new ItemStack(Blocks.NOTEBLOCK), new ItemStack(Blocks.PLANKS, 4), new ItemStack(Items.REDSTONE, 1), 25);
		}

		/* NON-WOOD PRIMARY */
		{
			int energy = DEFAULT_ENERGY * 3 / 4;

			addRecipe(energy, new ItemStack(Blocks.MELON_BLOCK), new ItemStack(Items.MELON, 9));
			addRecipe(energy, new ItemStack(Blocks.LEVER), new ItemStack(Blocks.COBBLESTONE, 1), ItemMaterial.dustWood, 25);
			addRecipe(energy, new ItemStack(Blocks.REDSTONE_TORCH), new ItemStack(Items.REDSTONE, 1), ItemMaterial.dustWood, 25);
			addRecipe(energy, new ItemStack(Items.PAINTING), new ItemStack(Blocks.WOOL, 1), ItemHelper.cloneStack(ItemMaterial.dustWood, 2));
			addRecipe(energy, new ItemStack(Items.ITEM_FRAME), new ItemStack(Items.LEATHER, 1), ItemHelper.cloneStack(ItemMaterial.dustWood, 2));
		}

		/* LOAD RECIPES */
		loadRecipes();
	}

	public static void loadRecipes() {

		addAllLogs();

		/* RUBBER WOOD */
		//		if (ItemHelper.oreNameExists("woodRubber")) {
		//			if (ItemHelper.oreNameExists("itemRawRubber")) {
		//				addRecipe(1200, OreDictionary.getOres("woodRubber").get(0), new ItemStack(Blocks.PLANKS, 5, 3), OreDictionary.getOres("itemRawRubber").get(0), 50);
		//			} else if (ItemHelper.oreNameExists("itemRubber")) {
		//				addRecipe(1200, OreDictionary.getOres("woodRubber").get(0), new ItemStack(Blocks.PLANKS, 5, 3), OreDictionary.getOres("itemRubber").get(0), 50);
		//			} else {
		//				addRecipe(1200, OreDictionary.getOres("woodRubber").get(0), new ItemStack(Blocks.PLANKS, 5, 3));
		//			}
		//		}
	}

	public static void refresh() {

		Map<ComparableItemStackSawmill, SawmillRecipe> tempMap = new THashMap<>(recipeMap.size());
		SawmillRecipe tempRecipe;

		for (Entry<ComparableItemStackSawmill, SawmillRecipe> entry : recipeMap.entrySet()) {
			tempRecipe = entry.getValue();
			tempMap.put(new ComparableItemStackSawmill(tempRecipe.input), tempRecipe);
		}
		recipeMap.clear();
		recipeMap = tempMap;
	}

	/* ADD RECIPES */
	public static SawmillRecipe addRecipe(int energy, ItemStack input, ItemStack primaryOutput, ItemStack secondaryOutput, int secondaryChance) {

		if (input.isEmpty() || primaryOutput.isEmpty() || energy <= 0 || recipeExists(input)) {
			return null;
		}
		SawmillRecipe recipe = new SawmillRecipe(input, primaryOutput, secondaryOutput, secondaryOutput.isEmpty() ? 0 : secondaryChance, energy);
		recipeMap.put(new ComparableItemStackSawmill(input), recipe);
		return recipe;
	}

	public static SawmillRecipe addRecipe(int energy, ItemStack input, ItemStack primaryOutput, ItemStack secondaryOutput) {

		return addRecipe(energy, input, primaryOutput, secondaryOutput, 100);
	}

	public static SawmillRecipe addRecipe(int energy, ItemStack input, ItemStack primaryOutput) {

		return addRecipe(energy, input, primaryOutput, ItemStack.EMPTY, 0);
	}

	/* REMOVE RECIPES */
	public static SawmillRecipe removeRecipe(ItemStack input) {

		return recipeMap.remove(new ComparableItemStackSawmill(input));
	}

	/* HELPERS */
	private static void addAllLogs() {

		InventoryCraftingFalse tempCrafting = new InventoryCraftingFalse(3, 3);

		for (int i = 0; i < 9; i++) {
			tempCrafting.setInventorySlotContents(i, ItemStack.EMPTY);
		}
		List<ItemStack> registeredOres = OreDictionary.getOres("logWood", false);

		for (ItemStack logEntry : registeredOres) {
			Block logBlock = Block.getBlockFromItem(logEntry.getItem());

			if (Blocks.LOG.equals(logBlock) || Blocks.LOG2.equals(logBlock)) {
				continue;
			}
			if (ItemHelper.getItemDamage(logEntry) == OreDictionary.WILDCARD_VALUE) {
				for (int j = 0; j < 16; j++) {
					ItemStack log = ItemHelper.cloneStack(logEntry, 1);
					log.setItemDamage(j);
					tempCrafting.setInventorySlotContents(0, log);
					ItemStack resultEntry = ItemHelper.getCraftingResult(tempCrafting, null);

					if (!resultEntry.isEmpty()) {
						ItemStack result = resultEntry.copy();
						result.setCount((int) (result.getCount() * LOG_MULTIPLIER));
						addRecipe(DEFAULT_ENERGY / 2, log, result, ItemMaterial.dustWood);
					}
				}
			} else {
				ItemStack log = ItemHelper.cloneStack(logEntry, 1);
				tempCrafting.setInventorySlotContents(0, log);
				ItemStack resultEntry = ItemHelper.getCraftingResult(tempCrafting, null);

				if (!resultEntry.isEmpty()) {
					ItemStack result = resultEntry.copy();
					result.setCount((int) (result.getCount() * LOG_MULTIPLIER));
					addRecipe(DEFAULT_ENERGY / 2, log, result, ItemMaterial.dustWood);
				}
			}
		}
	}

	/* RECIPE CLASS */
	public static class SawmillRecipe {

		final ItemStack input;
		final ItemStack primaryOutput;
		final ItemStack secondaryOutput;
		final int secondaryChance;
		final int energy;

		SawmillRecipe(ItemStack input, ItemStack primaryOutput, ItemStack secondaryOutput, int secondaryChance, int energy) {

			this.input = input;
			this.primaryOutput = primaryOutput;
			this.secondaryOutput = secondaryOutput;
			this.secondaryChance = secondaryChance;
			this.energy = energy;

			if (input.getCount() <= 0) {
				input.setCount(1);
			}
			if (primaryOutput.getCount() <= 0) {
				primaryOutput.setCount(1);
			}
			if (!secondaryOutput.isEmpty() && secondaryOutput.getCount() <= 0) {
				secondaryOutput.setCount(1);
			}
		}

		public ItemStack getInput() {

			return input;
		}

		public ItemStack getPrimaryOutput() {

			return primaryOutput;
		}

		public ItemStack getSecondaryOutput() {

			return secondaryOutput;
		}

		public int getSecondaryOutputChance() {

			return secondaryChance;
		}

		public int getEnergy() {

			return energy;
		}
	}

	/* ITEMSTACK CLASS */
	public static class ComparableItemStackSawmill extends ComparableItemStack {

		public static final String ORE = "ore";
		public static final String INGOT = "ingot";
		public static final String NUGGET = "nugget";

		public static boolean safeOreType(String oreName) {

			return oreName.startsWith(ORE) || oreName.startsWith(INGOT) || oreName.startsWith(NUGGET);
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

		public ComparableItemStackSawmill(ItemStack stack) {

			super(stack);
			oreID = getOreID(stack);
		}

		@Override
		public ComparableItemStackSawmill set(ItemStack stack) {

			super.set(stack);
			oreID = getOreID(stack);

			return this;
		}
	}

}
