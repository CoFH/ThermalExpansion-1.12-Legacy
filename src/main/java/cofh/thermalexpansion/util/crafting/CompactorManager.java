package cofh.thermalexpansion.util.crafting;

import cofh.core.util.oredict.OreDictionaryArbiter;
import cofh.lib.inventory.ComparableItemStack;
import cofh.lib.util.helpers.ItemHelper;
import cofh.thermalfoundation.block.BlockStorage;
import cofh.thermalfoundation.block.BlockStorageAlloy;
import cofh.thermalfoundation.item.ItemMaterial;
import gnu.trove.map.hash.THashMap;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import java.util.ArrayList;
import java.util.Map;

public class CompactorManager {

	private static Map<ComparableItemStackCompactor, RecipeCompactor> recipeMap = new THashMap<ComparableItemStackCompactor, RecipeCompactor>();

	static final int DEFAULT_ENERGY = 800;

	public static RecipeCompactor getRecipe(ItemStack input) {

		if (input == null) {
			return null;
		}
		ComparableItemStackCompactor query = new ComparableItemStackCompactor(input);

		RecipeCompactor recipe = recipeMap.get(query);

		if (recipe == null) {
			query.metadata = OreDictionary.WILDCARD_VALUE;
			recipe = recipeMap.get(query);
		}
		return recipe;
	}

	public static boolean recipeExists(ItemStack input) {

		return getRecipe(input) != null;
	}

	public static RecipeCompactor[] getRecipeList() {

		return recipeMap.values().toArray(new RecipeCompactor[0]);
	}

	public static void addDefaultRecipes() {

		addStorageRecipe(ItemMaterial.ingotIron, ItemHelper.cloneStack(Blocks.IRON_BLOCK, 1));
		addStorageRecipe(ItemMaterial.ingotGold, ItemHelper.cloneStack(Blocks.GOLD_BLOCK, 1));
		addStorageRecipe(ItemMaterial.gemDiamond, ItemHelper.cloneStack(Blocks.DIAMOND_BLOCK, 1));

		addStorageRecipe(ItemMaterial.ingotCopper, BlockStorage.blockCopper);
		addStorageRecipe(ItemMaterial.ingotTin, BlockStorage.blockTin);
		addStorageRecipe(ItemMaterial.ingotSilver, BlockStorage.blockSilver);
		addStorageRecipe(ItemMaterial.ingotLead, BlockStorage.blockLead);
		addStorageRecipe(ItemMaterial.ingotAluminum, BlockStorage.blockAluminum);
		addStorageRecipe(ItemMaterial.ingotNickel, BlockStorage.blockNickel);
		addStorageRecipe(ItemMaterial.ingotPlatinum, BlockStorage.blockPlatinum);
		addStorageRecipe(ItemMaterial.ingotIridium, BlockStorage.blockIridium);
		addStorageRecipe(ItemMaterial.ingotMithril, BlockStorage.blockMithril);

		addStorageRecipe(ItemMaterial.ingotSteel, BlockStorageAlloy.blockSteel);
		addStorageRecipe(ItemMaterial.ingotElectrum, BlockStorageAlloy.blockElectrum);
		addStorageRecipe(ItemMaterial.ingotBronze, BlockStorageAlloy.blockBronze);
		addStorageRecipe(ItemMaterial.ingotSignalum, BlockStorageAlloy.blockSignalum);
		addStorageRecipe(ItemMaterial.ingotLumium, BlockStorageAlloy.blockLumium);
		addStorageRecipe(ItemMaterial.ingotEnderium, BlockStorageAlloy.blockEnderium);

		addStorageRecipe(ItemMaterial.nuggetIron, ItemMaterial.ingotIron);
		addStorageRecipe(ItemMaterial.nuggetGold, ItemMaterial.ingotGold);
		addStorageRecipe(ItemMaterial.nuggetDiamond, ItemMaterial.gemDiamond);

		addStorageRecipe(ItemMaterial.nuggetCopper, ItemMaterial.ingotCopper);
		addStorageRecipe(ItemMaterial.nuggetTin, ItemMaterial.ingotTin);
		addStorageRecipe(ItemMaterial.nuggetSilver, ItemMaterial.ingotSilver);
		addStorageRecipe(ItemMaterial.nuggetLead, ItemMaterial.ingotLead);
		addStorageRecipe(ItemMaterial.nuggetAluminum, ItemMaterial.ingotAluminum);
		addStorageRecipe(ItemMaterial.nuggetNickel, ItemMaterial.ingotNickel);
		addStorageRecipe(ItemMaterial.nuggetPlatinum, ItemMaterial.ingotPlatinum);
		addStorageRecipe(ItemMaterial.nuggetIridium, ItemMaterial.ingotIridium);
		addStorageRecipe(ItemMaterial.nuggetMithril, ItemMaterial.ingotMithril);

		addStorageRecipe(ItemMaterial.nuggetSteel, ItemMaterial.ingotSteel);
		addStorageRecipe(ItemMaterial.nuggetElectrum, ItemMaterial.ingotElectrum);
		addStorageRecipe(ItemMaterial.nuggetBronze, ItemMaterial.ingotBronze);
		addStorageRecipe(ItemMaterial.nuggetSignalum, ItemMaterial.ingotSignalum);
		addStorageRecipe(ItemMaterial.nuggetLumium, ItemMaterial.ingotLumium);
		addStorageRecipe(ItemMaterial.nuggetEnderium, ItemMaterial.ingotEnderium);

	}

	public static void loadRecipes() {

	}

	public static void refreshRecipes() {

		Map<ComparableItemStackCompactor, RecipeCompactor> tempMap = new THashMap<ComparableItemStackCompactor, RecipeCompactor>(recipeMap.size());
		RecipeCompactor tempRecipe;

		for (Map.Entry<ComparableItemStackCompactor, RecipeCompactor> entry : recipeMap.entrySet()) {
			tempRecipe = entry.getValue();
			tempMap.put(new ComparableItemStackCompactor(tempRecipe.input), tempRecipe);
		}
		recipeMap.clear();
		recipeMap = tempMap;
	}

	/* ADD RECIPES */
	public static boolean addRecipe(int energy, ItemStack input, ItemStack output) {

		if (input == null || output == null || energy <= 0 || recipeExists(input)) {
			return false;
		}
		RecipeCompactor recipe = new RecipeCompactor(input, output, energy);
		recipeMap.put(new ComparableItemStackCompactor(input), recipe);
		return true;
	}

	/* REMOVE RECIPES */
	public static boolean removeRecipe(ItemStack input) {

		return recipeMap.remove(new ComparableItemStackCompactor(input)) != null;
	}

	/* HELPER FUNCTIONS */
	private static void addOreDictRecipe(int energy, String oreName, ItemStack output) {

		ItemStack input = OreDictionary.getOres(oreName).get(0);

		if (ItemHelper.oreNameExists(oreName) && !recipeExists(input)) {
			addRecipe(energy, ItemHelper.cloneStack(input, 1), output);
		}
	}

	private static void addStorageRecipe(ItemStack input, ItemStack output) {

		ItemStack nine = ItemHelper.cloneStack(input, 9);

		if (!recipeExists(nine)) {
			addRecipe(DEFAULT_ENERGY, nine, output);
		}
	}

	/* RECIPE CLASS */
	public static class RecipeCompactor {

		final ItemStack input;
		final ItemStack output;
		final int energy;

		RecipeCompactor(ItemStack input, ItemStack output, int energy) {

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

			return input.copy();
		}

		public ItemStack getOutput() {

			return output.copy();
		}

		public int getEnergy() {

			return energy;
		}
	}

	/* ITEMSTACK CLASS */
	public static class ComparableItemStackCompactor extends ComparableItemStack {

		static final String NUGGET = "nugget";
		static final String INGOT = "ingot";

		static boolean safeOreType(String oreName) {

			return oreName.startsWith(NUGGET) || oreName.startsWith(INGOT);
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

		ComparableItemStackCompactor(ItemStack stack) {

			super(stack);
			oreID = getOreID(stack);
		}

		@Override
		public ComparableItemStackCompactor set(ItemStack stack) {

			super.set(stack);
			oreID = getOreID(stack);

			return this;
		}
	}

}
