package cofh.thermalexpansion.util.crafting;

import cofh.core.util.oredict.OreDictionaryArbiter;
import cofh.lib.inventory.ComparableItemStack;
import cofh.lib.util.helpers.ItemHelper;
import cofh.thermalfoundation.block.BlockStorage;
import cofh.thermalfoundation.block.BlockStorageAlloy;
import cofh.thermalfoundation.item.ItemMaterial;
import gnu.trove.map.hash.THashMap;
import gnu.trove.set.hash.THashSet;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

public class CompactorManager {

	private static Map<ComparableItemStackCompactor, RecipeCompactor> recipePressMap = new THashMap<ComparableItemStackCompactor, RecipeCompactor>();
	private static Map<ComparableItemStackCompactor, RecipeCompactor> recipeStorageMap = new THashMap<ComparableItemStackCompactor, RecipeCompactor>();
	private static Map<ComparableItemStackCompactor, RecipeCompactor> recipeMintMap = new THashMap<ComparableItemStackCompactor, RecipeCompactor>();
	private static Set<ComparableItemStackCompactor> validationSet = new THashSet<ComparableItemStackCompactor>();

	static final int DEFAULT_ENERGY = 4000;
	static final int DEFAULT_ENERGY_STORAGE = 400;

	public static RecipeCompactor getRecipe(ItemStack input, Mode mode) {

		if (input == null) {
			return null;
		}
		ComparableItemStackCompactor query = new ComparableItemStackCompactor(input);
		RecipeCompactor recipe = null;

		switch(mode) {
			case PRESS:
				recipe = recipePressMap.get(query);

				if (recipe == null) {
					query.metadata = OreDictionary.WILDCARD_VALUE;
					recipe = recipePressMap.get(query);
				}
				break;
			case STORAGE:
				recipe = recipeStorageMap.get(query);

				if (recipe == null) {
					query.metadata = OreDictionary.WILDCARD_VALUE;
					recipe = recipeStorageMap.get(query);
				}
				break;
			case MINT:
				recipe = recipeMintMap.get(query);

				if (recipe == null) {
					query.metadata = OreDictionary.WILDCARD_VALUE;
					recipe = recipeMintMap.get(query);
				}
				break;
		}
		return recipe;
	}

	public static boolean recipeExists(ItemStack input, Mode mode) {

		return getRecipe(input, mode) != null;
	}

	public static RecipeCompactor[] getRecipeList(Mode mode) {

		switch (mode) {
			case PRESS:
				return recipePressMap.values().toArray(new RecipeCompactor[recipePressMap.values().size()]);
			case STORAGE:
				return recipeStorageMap.values().toArray(new RecipeCompactor[recipeStorageMap.values().size()]);
		}
		return recipeMintMap.values().toArray(new RecipeCompactor[recipeMintMap.values().size()]);
	}

	public static boolean isItemValid(ItemStack input) {

		return input != null && validationSet.contains(new ComparableItemStackCompactor(input));
	}

	public static void addDefaultRecipes() {

		/* PRESS */
		{
			addDefaultPressRecipe(ItemMaterial.ingotCopper, ItemMaterial.plateCopper);
			addDefaultPressRecipe(ItemMaterial.ingotTin, ItemMaterial.plateTin);
			addDefaultPressRecipe(ItemMaterial.ingotSilver, ItemMaterial.plateSilver);
			addDefaultPressRecipe(ItemMaterial.ingotLead, ItemMaterial.plateLead);
			addDefaultPressRecipe(ItemMaterial.ingotAluminum, ItemMaterial.plateAluminum);
			addDefaultPressRecipe(ItemMaterial.ingotNickel, ItemMaterial.plateNickel);
			addDefaultPressRecipe(ItemMaterial.ingotPlatinum, ItemMaterial.platePlatinum);
			addDefaultPressRecipe(ItemMaterial.ingotIridium, ItemMaterial.plateIridium);
			addDefaultPressRecipe(ItemMaterial.ingotMithril, ItemMaterial.plateMithril);

			addDefaultPressRecipe(ItemMaterial.ingotSteel, ItemMaterial.plateSteel);
			addDefaultPressRecipe(ItemMaterial.ingotElectrum, ItemMaterial.plateElectrum);
			addDefaultPressRecipe(ItemMaterial.ingotBronze, ItemMaterial.plateBronze);
			addDefaultPressRecipe(ItemMaterial.ingotSignalum, ItemMaterial.plateSignalum);
			addDefaultPressRecipe(ItemMaterial.ingotLumium, ItemMaterial.plateLumium);
			addDefaultPressRecipe(ItemMaterial.ingotEnderium, ItemMaterial.plateEnderium);
		}

		/* STORAGE */
		{
			addDefaultStorageRecipe(ItemHelper.cloneStack(Items.WHEAT, 1), ItemHelper.cloneStack(Blocks.HAY_BLOCK, 1));
			addDefaultStorageRecipe(ItemHelper.cloneStack(Items.REDSTONE, 1), ItemHelper.cloneStack(Blocks.REDSTONE_BLOCK, 1));

			addDefaultStorageRecipe(ItemMaterial.ingotIron, ItemHelper.cloneStack(Blocks.IRON_BLOCK, 1));
			addDefaultStorageRecipe(ItemMaterial.ingotGold, ItemHelper.cloneStack(Blocks.GOLD_BLOCK, 1));
			addDefaultStorageRecipe(ItemMaterial.gemDiamond, ItemHelper.cloneStack(Blocks.DIAMOND_BLOCK, 1));

			addDefaultStorageRecipe(ItemMaterial.ingotCopper, BlockStorage.blockCopper);
			addDefaultStorageRecipe(ItemMaterial.ingotTin, BlockStorage.blockTin);
			addDefaultStorageRecipe(ItemMaterial.ingotSilver, BlockStorage.blockSilver);
			addDefaultStorageRecipe(ItemMaterial.ingotLead, BlockStorage.blockLead);
			addDefaultStorageRecipe(ItemMaterial.ingotAluminum, BlockStorage.blockAluminum);
			addDefaultStorageRecipe(ItemMaterial.ingotNickel, BlockStorage.blockNickel);
			addDefaultStorageRecipe(ItemMaterial.ingotPlatinum, BlockStorage.blockPlatinum);
			addDefaultStorageRecipe(ItemMaterial.ingotIridium, BlockStorage.blockIridium);
			addDefaultStorageRecipe(ItemMaterial.ingotMithril, BlockStorage.blockMithril);

			addDefaultStorageRecipe(ItemMaterial.ingotSteel, BlockStorageAlloy.blockSteel);
			addDefaultStorageRecipe(ItemMaterial.ingotElectrum, BlockStorageAlloy.blockElectrum);
			addDefaultStorageRecipe(ItemMaterial.ingotBronze, BlockStorageAlloy.blockBronze);
			addDefaultStorageRecipe(ItemMaterial.ingotSignalum, BlockStorageAlloy.blockSignalum);
			addDefaultStorageRecipe(ItemMaterial.ingotLumium, BlockStorageAlloy.blockLumium);
			addDefaultStorageRecipe(ItemMaterial.ingotEnderium, BlockStorageAlloy.blockEnderium);

			addDefaultStorageRecipe(ItemMaterial.nuggetIron, ItemMaterial.ingotIron);
			addDefaultStorageRecipe(ItemMaterial.nuggetGold, ItemMaterial.ingotGold);
			addDefaultStorageRecipe(ItemMaterial.nuggetDiamond, ItemMaterial.gemDiamond);

			addDefaultStorageRecipe(ItemMaterial.nuggetCopper, ItemMaterial.ingotCopper);
			addDefaultStorageRecipe(ItemMaterial.nuggetTin, ItemMaterial.ingotTin);
			addDefaultStorageRecipe(ItemMaterial.nuggetSilver, ItemMaterial.ingotSilver);
			addDefaultStorageRecipe(ItemMaterial.nuggetLead, ItemMaterial.ingotLead);
			addDefaultStorageRecipe(ItemMaterial.nuggetAluminum, ItemMaterial.ingotAluminum);
			addDefaultStorageRecipe(ItemMaterial.nuggetNickel, ItemMaterial.ingotNickel);
			addDefaultStorageRecipe(ItemMaterial.nuggetPlatinum, ItemMaterial.ingotPlatinum);
			addDefaultStorageRecipe(ItemMaterial.nuggetIridium, ItemMaterial.ingotIridium);
			addDefaultStorageRecipe(ItemMaterial.nuggetMithril, ItemMaterial.ingotMithril);

			addDefaultStorageRecipe(ItemMaterial.nuggetSteel, ItemMaterial.ingotSteel);
			addDefaultStorageRecipe(ItemMaterial.nuggetElectrum, ItemMaterial.ingotElectrum);
			addDefaultStorageRecipe(ItemMaterial.nuggetBronze, ItemMaterial.ingotBronze);
			addDefaultStorageRecipe(ItemMaterial.nuggetSignalum, ItemMaterial.ingotSignalum);
			addDefaultStorageRecipe(ItemMaterial.nuggetLumium, ItemMaterial.ingotLumium);
			addDefaultStorageRecipe(ItemMaterial.nuggetEnderium, ItemMaterial.ingotEnderium);
		}
	}

	public static void loadRecipes() {

	}

	public static void refreshRecipes() {

		Map<ComparableItemStackCompactor, RecipeCompactor> tempPress = new THashMap<ComparableItemStackCompactor, RecipeCompactor>(recipePressMap.size());
		Map<ComparableItemStackCompactor, RecipeCompactor> tempStorage = new THashMap<ComparableItemStackCompactor, RecipeCompactor>(recipeStorageMap.size());
		Map<ComparableItemStackCompactor, RecipeCompactor> tempMint = new THashMap<ComparableItemStackCompactor, RecipeCompactor>(recipeMintMap.size());
		Set<ComparableItemStackCompactor> tempSet = new THashSet<ComparableItemStackCompactor>();
		RecipeCompactor tempRecipe;

		for (Map.Entry<ComparableItemStackCompactor, RecipeCompactor> entry : recipePressMap.entrySet()) {
			tempRecipe = entry.getValue();
			ComparableItemStackCompactor input = new ComparableItemStackCompactor(tempRecipe.input);
			tempPress.put(input, tempRecipe);
			tempSet.add(input);
		}
		for (Map.Entry<ComparableItemStackCompactor, RecipeCompactor> entry : recipeStorageMap.entrySet()) {
			tempRecipe = entry.getValue();
			ComparableItemStackCompactor input = new ComparableItemStackCompactor(tempRecipe.input);
			tempStorage.put(input, tempRecipe);
			tempSet.add(input);
		}
		for (Map.Entry<ComparableItemStackCompactor, RecipeCompactor> entry : recipeMintMap.entrySet()) {
			tempRecipe = entry.getValue();
			ComparableItemStackCompactor input = new ComparableItemStackCompactor(tempRecipe.input);
			tempMint.put(input, tempRecipe);
			tempSet.add(input);
		}
		recipePressMap.clear();
		recipeStorageMap.clear();
		recipeMintMap.clear();

		recipePressMap = tempPress;
		recipeStorageMap = tempStorage;
		recipeMintMap = tempMint;

		validationSet.clear();
		validationSet = tempSet;
	}

	/* ADD RECIPES */
	public static boolean addRecipe(int energy, ItemStack input, ItemStack output, Mode mode) {

		if (input == null || output == null || energy <= 0 || recipeExists(input, mode)) {
			return false;
		}
		RecipeCompactor recipe = new RecipeCompactor(input, output, energy);

		switch (mode) {
			case PRESS:
				recipePressMap.put(new ComparableItemStackCompactor(input), recipe);
				break;
			case STORAGE:
				recipeStorageMap.put(new ComparableItemStackCompactor(input), recipe);
				break;
			case MINT:
				recipeMintMap.put(new ComparableItemStackCompactor(input), recipe);
				break;
		}
		validationSet.add(new ComparableItemStackCompactor(input));
		return true;
	}

	/* REMOVE RECIPES */
	public static boolean removeRecipe(ItemStack input, Mode mode) {

		switch (mode) {
			case PRESS:
				return recipePressMap.remove(new ComparableItemStackCompactor(input)) != null;
			case STORAGE:
				return recipeStorageMap.remove(new ComparableItemStackCompactor(input)) != null;
		}
		return recipeMintMap.remove(new ComparableItemStackCompactor(input)) != null;
	}

	/* HELPERS */
	private static void addDefaultPressRecipe(ItemStack input, ItemStack output) {

		addRecipe(DEFAULT_ENERGY, input, output, Mode.PRESS);
	}

	private static void addDefaultStorageRecipe(ItemStack input, ItemStack output) {

		ItemStack nine = ItemHelper.cloneStack(input, 9);

		if (!recipeExists(nine, Mode.STORAGE)) {
			addRecipe(DEFAULT_ENERGY_STORAGE, nine, output, Mode.STORAGE);
		}
	}

	private static void addDefaultMintRecipe(ItemStack input, ItemStack output) {

		addRecipe(DEFAULT_ENERGY, input, output, Mode.MINT);
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

			return input;
		}

		public ItemStack getOutput() {

			return output;
		}

		public int getEnergy() {

			return energy;
		}
	}

	/* MODE ENUM */
	public enum Mode {
		PRESS, STORAGE, MINT
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
