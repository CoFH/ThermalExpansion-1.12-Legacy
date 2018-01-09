package cofh.thermalexpansion.util.managers.device;

import cofh.core.inventory.ComparableItemStackSafe;
import cofh.core.util.helpers.ItemHelper;
import cofh.thermalfoundation.block.BlockStorage;
import cofh.thermalfoundation.block.BlockStorageAlloy;
import cofh.thermalfoundation.item.ItemMaterial;
import gnu.trove.map.hash.THashMap;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraftforge.oredict.OreDictionary;

import java.util.Map;

public class FactorizerManager {

	private static Map<ComparableItemStackSafe, FactorizerRecipe> recipeMap = new THashMap<>();
	private static Map<ComparableItemStackSafe, FactorizerRecipe> recipeMapReverse = new THashMap<>();

	public static FactorizerRecipe getRecipe(ItemStack input, boolean reverse) {

		if (input.isEmpty()) {
			return null;
		}
		ComparableItemStackSafe query = new ComparableItemStackSafe(input);
		FactorizerRecipe recipe;

		if (reverse) {
			recipe = recipeMapReverse.get(query);
			if (recipe == null) {
				query.metadata = OreDictionary.WILDCARD_VALUE;
				recipe = recipeMapReverse.get(query);
			}
		} else {
			recipe = recipeMap.get(query);
			if (recipe == null) {
				query.metadata = OreDictionary.WILDCARD_VALUE;
				recipe = recipeMap.get(query);
			}
		}
		return recipe;
	}

	public static boolean recipeExists(ItemStack input, boolean reverse) {

		return getRecipe(input, reverse) != null;
	}

	public static FactorizerRecipe[] getRecipeList(boolean reverse) {

		if (reverse) {
			return recipeMapReverse.values().toArray(new FactorizerRecipe[recipeMapReverse.size()]);
		}
		return recipeMap.values().toArray(new FactorizerRecipe[recipeMap.size()]);
	}

	public static void initialize() {

		addDefaultRecipe(ItemMaterial.ingotIron, new ItemStack(Blocks.IRON_BLOCK));
		addDefaultRecipe(ItemMaterial.ingotGold, new ItemStack(Blocks.GOLD_BLOCK));
		addDefaultRecipe(ItemMaterial.gemDiamond, new ItemStack(Blocks.DIAMOND_BLOCK));
		addDefaultRecipe(ItemMaterial.gemEmerald, new ItemStack(Blocks.EMERALD_BLOCK));

		addDefaultRecipe(ItemMaterial.ingotCopper, BlockStorage.blockCopper);
		addDefaultRecipe(ItemMaterial.ingotTin, BlockStorage.blockTin);
		addDefaultRecipe(ItemMaterial.ingotSilver, BlockStorage.blockSilver);
		addDefaultRecipe(ItemMaterial.ingotLead, BlockStorage.blockLead);
		addDefaultRecipe(ItemMaterial.ingotAluminum, BlockStorage.blockAluminum);
		addDefaultRecipe(ItemMaterial.ingotNickel, BlockStorage.blockNickel);
		addDefaultRecipe(ItemMaterial.ingotPlatinum, BlockStorage.blockPlatinum);
		addDefaultRecipe(ItemMaterial.ingotIridium, BlockStorage.blockIridium);
		addDefaultRecipe(ItemMaterial.ingotMithril, BlockStorage.blockMithril);

		addDefaultRecipe(ItemMaterial.ingotSteel, BlockStorageAlloy.blockSteel);
		addDefaultRecipe(ItemMaterial.ingotElectrum, BlockStorageAlloy.blockElectrum);
		addDefaultRecipe(ItemMaterial.ingotInvar, BlockStorageAlloy.blockInvar);
		addDefaultRecipe(ItemMaterial.ingotBronze, BlockStorageAlloy.blockBronze);
		addDefaultRecipe(ItemMaterial.ingotConstantan, BlockStorageAlloy.blockConstantan);
		addDefaultRecipe(ItemMaterial.ingotSignalum, BlockStorageAlloy.blockSignalum);
		addDefaultRecipe(ItemMaterial.ingotLumium, BlockStorageAlloy.blockLumium);
		addDefaultRecipe(ItemMaterial.ingotEnderium, BlockStorageAlloy.blockEnderium);

		addDefaultRecipe(ItemMaterial.nuggetIron, ItemMaterial.ingotIron);
		addDefaultRecipe(ItemMaterial.nuggetGold, ItemMaterial.ingotGold);
		addDefaultRecipe(ItemMaterial.nuggetDiamond, ItemMaterial.gemDiamond);
		addDefaultRecipe(ItemMaterial.nuggetEmerald, ItemMaterial.gemEmerald);

		addDefaultRecipe(ItemMaterial.nuggetCopper, ItemMaterial.ingotCopper);
		addDefaultRecipe(ItemMaterial.nuggetTin, ItemMaterial.ingotTin);
		addDefaultRecipe(ItemMaterial.nuggetSilver, ItemMaterial.ingotSilver);
		addDefaultRecipe(ItemMaterial.nuggetLead, ItemMaterial.ingotLead);
		addDefaultRecipe(ItemMaterial.nuggetAluminum, ItemMaterial.ingotAluminum);
		addDefaultRecipe(ItemMaterial.nuggetNickel, ItemMaterial.ingotNickel);
		addDefaultRecipe(ItemMaterial.nuggetPlatinum, ItemMaterial.ingotPlatinum);
		addDefaultRecipe(ItemMaterial.nuggetIridium, ItemMaterial.ingotIridium);
		addDefaultRecipe(ItemMaterial.nuggetMithril, ItemMaterial.ingotMithril);

		addDefaultRecipe(ItemMaterial.nuggetSteel, ItemMaterial.ingotSteel);
		addDefaultRecipe(ItemMaterial.nuggetElectrum, ItemMaterial.ingotElectrum);
		addDefaultRecipe(ItemMaterial.nuggetInvar, ItemMaterial.ingotInvar);
		addDefaultRecipe(ItemMaterial.nuggetBronze, ItemMaterial.ingotBronze);
		addDefaultRecipe(ItemMaterial.nuggetConstantan, ItemMaterial.ingotConstantan);
		addDefaultRecipe(ItemMaterial.nuggetSignalum, ItemMaterial.ingotSignalum);
		addDefaultRecipe(ItemMaterial.nuggetLumium, ItemMaterial.ingotLumium);
		addDefaultRecipe(ItemMaterial.nuggetEnderium, ItemMaterial.ingotEnderium);

		/* LOAD RECIPES */
		loadRecipes();
	}

	public static void loadRecipes() {

		/* STORAGE */
		for (IRecipe recipe : CraftingManager.REGISTRY) {
			if (recipe instanceof ShapedRecipes) {
				ShapedRecipes target = (ShapedRecipes) recipe;
				if (/*target.recipeItems.size() == 4 ||*/ target.recipeItems.size() == 9) {
					if (target.recipeItems.get(0).getMatchingStacks().length > 0) {
						boolean match = true;
						for (int i = 1; i < target.recipeItems.size(); i++) {
							match &= target.recipeItems.get(i).getMatchingStacks().length > 0 && ItemHelper.itemsIdentical(target.recipeItems.get(0).getMatchingStacks()[0], target.recipeItems.get(i).getMatchingStacks()[0]);
						}
						if (match) {
							addDefaultRecipe(target.recipeItems.get(0).getMatchingStacks()[0], target.getRecipeOutput(), target.recipeItems.size());
						}
					}
				}
			}
		}
	}

	public static void refresh() {

		Map<ComparableItemStackSafe, FactorizerRecipe> tempMap = new THashMap<>(recipeMap.size());
		Map<ComparableItemStackSafe, FactorizerRecipe> tempMapReverse = new THashMap<>(recipeMapReverse.size());
		FactorizerRecipe tempRecipe;

		for (Map.Entry<ComparableItemStackSafe, FactorizerRecipe> entry : recipeMap.entrySet()) {
			tempRecipe = entry.getValue();
			ComparableItemStackSafe input = new ComparableItemStackSafe(tempRecipe.input);
			tempMap.put(input, tempRecipe);
		}
		for (Map.Entry<ComparableItemStackSafe, FactorizerRecipe> entry : recipeMapReverse.entrySet()) {
			tempRecipe = entry.getValue();
			ComparableItemStackSafe input = new ComparableItemStackSafe(tempRecipe.input);
			tempMapReverse.put(input, tempRecipe);
		}
		recipeMap.clear();
		recipeMapReverse.clear();

		recipeMap = tempMap;
		recipeMapReverse = tempMapReverse;
	}

	/* ADD RECIPES */
	public static FactorizerRecipe addRecipe(ItemStack input, ItemStack output, boolean reverse) {

		if (input.isEmpty() || output.isEmpty() || recipeExists(input, reverse)) {
			return null;
		}
		FactorizerRecipe recipe = new FactorizerRecipe(input, output);

		if (reverse) {
			recipeMapReverse.put(new ComparableItemStackSafe(input), recipe);
		} else {
			recipeMap.put(new ComparableItemStackSafe(input), recipe);
		}
		return recipe;
	}

	/* REMOVE RECIPES */
	public static FactorizerRecipe removeRecipe(ItemStack input, boolean reverse) {

		if (reverse) {
			return recipeMapReverse.remove(new ComparableItemStackSafe(input));
		} else {
			return recipeMap.remove(new ComparableItemStackSafe(input));
		}
	}

	public static void addDefaultRecipe(ItemStack input, ItemStack output) {

		addDefaultRecipe(input, output, 9);
	}

	public static void addDefaultRecipe(ItemStack input, ItemStack output, int count) {

		ItemStack inputStack = ItemHelper.cloneStack(input, count);

		if (!recipeExists(inputStack, false)) {
			addRecipe(inputStack, output, false);
		}
		if (!recipeExists(output, true)) {
			addRecipe(output, inputStack, true);
		}
	}

	/* RECIPE CLASS */
	public static class FactorizerRecipe {

		final ItemStack input;
		final ItemStack output;

		FactorizerRecipe(ItemStack input, ItemStack output) {

			this.input = input;
			this.output = output;
		}

		public ItemStack getInput() {

			return input;
		}

		public ItemStack getOutput() {

			return output;
		}
	}

}
