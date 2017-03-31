package cofh.thermalexpansion.util.managers.machine;

import cofh.core.util.oredict.OreDictionaryArbiter;
import cofh.lib.inventory.ComparableItemStack;
import cofh.lib.util.helpers.ItemHelper;
import cofh.thermalfoundation.block.BlockStorage;
import cofh.thermalfoundation.block.BlockStorageAlloy;
import cofh.thermalfoundation.item.ItemCoin;
import cofh.thermalfoundation.item.ItemMaterial;
import gnu.trove.map.hash.THashMap;
import gnu.trove.set.hash.THashSet;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CompactorManager {

	private static Map<ComparableItemStackCompactor, RecipeCompactor> recipeMapPress = new THashMap<>();
	private static Map<ComparableItemStackCompactor, RecipeCompactor> recipeMapStorage = new THashMap<>();
	private static Map<ComparableItemStackCompactor, RecipeCompactor> recipeMapMint = new THashMap<>();
	private static Set<ComparableItemStackCompactor> validationSet = new THashSet<>();

	static final int DEFAULT_ENERGY = 4000;
	static final int DEFAULT_ENERGY_STORAGE = 400;

	public static RecipeCompactor getRecipe(ItemStack input, Mode mode) {

		if (input == null) {
			return null;
		}
		ComparableItemStackCompactor query = new ComparableItemStackCompactor(input);
		RecipeCompactor recipe = null;

		switch (mode) {
			case PRESS:
				recipe = recipeMapPress.get(query);

				if (recipe == null) {
					query.metadata = OreDictionary.WILDCARD_VALUE;
					recipe = recipeMapPress.get(query);
				}
				break;
			case STORAGE:
				recipe = recipeMapStorage.get(query);

				if (recipe == null) {
					query.metadata = OreDictionary.WILDCARD_VALUE;
					recipe = recipeMapStorage.get(query);
				}
				break;
			case MINT:
				recipe = recipeMapMint.get(query);

				if (recipe == null) {
					query.metadata = OreDictionary.WILDCARD_VALUE;
					recipe = recipeMapMint.get(query);
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
				return recipeMapPress.values().toArray(new RecipeCompactor[recipeMapPress.size()]);
			case STORAGE:
				return recipeMapStorage.values().toArray(new RecipeCompactor[recipeMapStorage.size()]);
		}
		return recipeMapMint.values().toArray(new RecipeCompactor[recipeMapMint.size()]);
	}

	public static boolean isItemValid(ItemStack input) {

		return input != null && validationSet.contains(new ComparableItemStackCompactor(input));
	}

	public static void initialize() {

		/* PRESS */
		{
			addDefaultPressRecipe(ItemMaterial.ingotIron, ItemMaterial.plateIron);
			addDefaultPressRecipe(ItemMaterial.ingotGold, ItemMaterial.plateGold);

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
			addDefaultPressRecipe(ItemMaterial.ingotInvar, ItemMaterial.plateInvar);
			addDefaultPressRecipe(ItemMaterial.ingotBronze, ItemMaterial.plateBronze);
			addDefaultPressRecipe(ItemMaterial.ingotConstantan, ItemMaterial.plateConstantan);
			addDefaultPressRecipe(ItemMaterial.ingotSignalum, ItemMaterial.plateSignalum);
			addDefaultPressRecipe(ItemMaterial.ingotLumium, ItemMaterial.plateLumium);
			addDefaultPressRecipe(ItemMaterial.ingotEnderium, ItemMaterial.plateEnderium);
		}

		/* STORAGE */
		{
			addDefaultStorageRecipe(ItemMaterial.ingotCopper, BlockStorage.blockCopper, 9);
			addDefaultStorageRecipe(ItemMaterial.ingotTin, BlockStorage.blockTin, 9);
			addDefaultStorageRecipe(ItemMaterial.ingotSilver, BlockStorage.blockSilver, 9);
			addDefaultStorageRecipe(ItemMaterial.ingotLead, BlockStorage.blockLead, 9);
			addDefaultStorageRecipe(ItemMaterial.ingotAluminum, BlockStorage.blockAluminum, 9);
			addDefaultStorageRecipe(ItemMaterial.ingotNickel, BlockStorage.blockNickel, 9);
			addDefaultStorageRecipe(ItemMaterial.ingotPlatinum, BlockStorage.blockPlatinum, 9);
			addDefaultStorageRecipe(ItemMaterial.ingotIridium, BlockStorage.blockIridium, 9);
			addDefaultStorageRecipe(ItemMaterial.ingotMithril, BlockStorage.blockMithril, 9);

			addDefaultStorageRecipe(ItemMaterial.ingotSteel, BlockStorageAlloy.blockSteel, 9);
			addDefaultStorageRecipe(ItemMaterial.ingotElectrum, BlockStorageAlloy.blockElectrum, 9);
			addDefaultStorageRecipe(ItemMaterial.ingotInvar, BlockStorageAlloy.blockInvar, 9);
			addDefaultStorageRecipe(ItemMaterial.ingotBronze, BlockStorageAlloy.blockBronze, 9);
			addDefaultStorageRecipe(ItemMaterial.ingotConstantan, BlockStorageAlloy.blockConstantan, 9);
			addDefaultStorageRecipe(ItemMaterial.ingotSignalum, BlockStorageAlloy.blockSignalum, 9);
			addDefaultStorageRecipe(ItemMaterial.ingotLumium, BlockStorageAlloy.blockLumium, 9);
			addDefaultStorageRecipe(ItemMaterial.ingotEnderium, BlockStorageAlloy.blockEnderium, 9);

			addDefaultStorageRecipe(ItemMaterial.nuggetIron, ItemMaterial.ingotIron, 9);
			addDefaultStorageRecipe(ItemMaterial.nuggetGold, ItemMaterial.ingotGold, 9);
			addDefaultStorageRecipe(ItemMaterial.nuggetDiamond, ItemMaterial.gemDiamond, 9);

			addDefaultStorageRecipe(ItemMaterial.nuggetCopper, ItemMaterial.ingotCopper, 9);
			addDefaultStorageRecipe(ItemMaterial.nuggetTin, ItemMaterial.ingotTin, 9);
			addDefaultStorageRecipe(ItemMaterial.nuggetSilver, ItemMaterial.ingotSilver, 9);
			addDefaultStorageRecipe(ItemMaterial.nuggetLead, ItemMaterial.ingotLead, 9);
			addDefaultStorageRecipe(ItemMaterial.nuggetAluminum, ItemMaterial.ingotAluminum, 9);
			addDefaultStorageRecipe(ItemMaterial.nuggetNickel, ItemMaterial.ingotNickel, 9);
			addDefaultStorageRecipe(ItemMaterial.nuggetPlatinum, ItemMaterial.ingotPlatinum, 9);
			addDefaultStorageRecipe(ItemMaterial.nuggetIridium, ItemMaterial.ingotIridium, 9);
			addDefaultStorageRecipe(ItemMaterial.nuggetMithril, ItemMaterial.ingotMithril, 9);

			addDefaultStorageRecipe(ItemMaterial.nuggetSteel, ItemMaterial.ingotSteel, 9);
			addDefaultStorageRecipe(ItemMaterial.nuggetElectrum, ItemMaterial.ingotElectrum, 9);
			addDefaultStorageRecipe(ItemMaterial.nuggetInvar, ItemMaterial.ingotInvar, 9);
			addDefaultStorageRecipe(ItemMaterial.nuggetBronze, ItemMaterial.ingotBronze, 9);
			addDefaultStorageRecipe(ItemMaterial.nuggetConstantan, ItemMaterial.ingotConstantan, 9);
			addDefaultStorageRecipe(ItemMaterial.nuggetSignalum, ItemMaterial.ingotSignalum, 9);
			addDefaultStorageRecipe(ItemMaterial.nuggetLumium, ItemMaterial.ingotLumium, 9);
			addDefaultStorageRecipe(ItemMaterial.nuggetEnderium, ItemMaterial.ingotEnderium, 9);
		}

		/* MINT */
		{
			addDefaultMintRecipe(ItemMaterial.nuggetIron, ItemMaterial.ingotIron, new ItemStack(Blocks.IRON_BLOCK), ItemCoin.coinIron);
			addDefaultMintRecipe(ItemMaterial.nuggetGold, ItemMaterial.ingotGold, new ItemStack(Blocks.GOLD_BLOCK), ItemCoin.coinGold);

			addDefaultMintRecipe(ItemMaterial.nuggetCopper, ItemMaterial.ingotCopper, BlockStorage.blockCopper, ItemCoin.coinCopper);
			addDefaultMintRecipe(ItemMaterial.nuggetTin, ItemMaterial.ingotTin, BlockStorage.blockTin, ItemCoin.coinTin);
			addDefaultMintRecipe(ItemMaterial.nuggetSilver, ItemMaterial.ingotSilver, BlockStorage.blockSilver, ItemCoin.coinSilver);
			addDefaultMintRecipe(ItemMaterial.nuggetLead, ItemMaterial.ingotLead, BlockStorage.blockLead, ItemCoin.coinLead);
			addDefaultMintRecipe(ItemMaterial.nuggetAluminum, ItemMaterial.ingotAluminum, BlockStorage.blockAluminum, ItemCoin.coinAluminum);
			addDefaultMintRecipe(ItemMaterial.nuggetNickel, ItemMaterial.ingotNickel, BlockStorage.blockNickel, ItemCoin.coinNickel);
			addDefaultMintRecipe(ItemMaterial.nuggetPlatinum, ItemMaterial.ingotPlatinum, BlockStorage.blockPlatinum, ItemCoin.coinPlatinum);
			addDefaultMintRecipe(ItemMaterial.nuggetIridium, ItemMaterial.ingotIridium, BlockStorage.blockIridium, ItemCoin.coinIridium);
			addDefaultMintRecipe(ItemMaterial.nuggetMithril, ItemMaterial.ingotMithril, BlockStorage.blockMithril, ItemCoin.coinMithril);

			addDefaultMintRecipe(ItemMaterial.nuggetSteel, ItemMaterial.ingotSteel, BlockStorageAlloy.blockSteel, ItemCoin.coinSteel);
			addDefaultMintRecipe(ItemMaterial.nuggetElectrum, ItemMaterial.ingotElectrum, BlockStorageAlloy.blockElectrum, ItemCoin.coinElectrum);
			addDefaultMintRecipe(ItemMaterial.nuggetInvar, ItemMaterial.ingotInvar, BlockStorageAlloy.blockInvar, ItemCoin.coinInvar);
			addDefaultMintRecipe(ItemMaterial.nuggetBronze, ItemMaterial.ingotBronze, BlockStorageAlloy.blockBronze, ItemCoin.coinBronze);
			addDefaultMintRecipe(ItemMaterial.nuggetConstantan, ItemMaterial.ingotConstantan, BlockStorageAlloy.blockConstantan, ItemCoin.coinConstantan);
			addDefaultMintRecipe(ItemMaterial.nuggetSignalum, ItemMaterial.ingotSignalum, BlockStorageAlloy.blockSignalum, ItemCoin.coinSignalum);
			addDefaultMintRecipe(ItemMaterial.nuggetLumium, ItemMaterial.ingotLumium, BlockStorageAlloy.blockLumium, ItemCoin.coinLumium);
			addDefaultMintRecipe(ItemMaterial.nuggetEnderium, ItemMaterial.ingotEnderium, BlockStorageAlloy.blockEnderium, ItemCoin.coinEnderium);
		}
		/* LOAD RECIPES */
		loadRecipes();
	}

	public static void loadRecipes() {

		for (IRecipe recipe : CraftingManager.getInstance().getRecipeList()) {

			if (recipe instanceof ShapedRecipes) {
				ShapedRecipes target = (ShapedRecipes) recipe;
				if (target.recipeItems.length == 4 || target.recipeItems.length == 9) {
					boolean match = true;
					for (int i = 1; i < target.recipeItems.length; i++) {
						match &= ItemHelper.itemsIdentical(target.recipeItems[0], target.recipeItems[i]);
					}
					if (match) {
						addDefaultStorageRecipe(target.recipeItems[0], target.getRecipeOutput(), target.recipeItems.length);
					}
				}
			} else if (recipe instanceof ShapelessRecipes) {
				ShapelessRecipes target = (ShapelessRecipes) recipe;
				if (target.getRecipeSize() == 4 || target.getRecipeSize() == 9) {
					boolean match = true;
					for (int i = 1; i < target.getRecipeSize(); i++) {
						match &= ItemHelper.itemsIdentical(target.recipeItems.get(0), target.recipeItems.get(i));
					}
					if (match) {
						addDefaultStorageRecipe(target.recipeItems.get(0), target.getRecipeOutput(), target.getRecipeSize());
					}
				}
			} else if (recipe instanceof ShapedOreRecipe) {
				ShapedOreRecipe target = (ShapedOreRecipe) recipe;
				if (target.getRecipeSize() == 4 || target.getRecipeSize() == 9) {
					boolean match = true;
					if (target.getInput()[0] instanceof List) {
						ItemStack input = ((List<ItemStack>) target.getInput()[0]).get(0);
						for (int i = 1; i < target.getRecipeSize(); i++) {
							if (target.getInput()[i] instanceof List) {
								ItemStack compare = ((List<ItemStack>) target.getInput()[i]).get(0);
								match &= ItemHelper.itemsIdentical(input, compare);
							} else {
								match = false;
							}
						}
						if (match) {
							List<ItemStack> ores = (List<ItemStack>) target.getInput()[0];
							for (ItemStack ore : ores) {
								addDefaultStorageRecipe(ore, target.getRecipeOutput(), target.getRecipeSize());
							}
						}
					} else if (target.getInput()[0] instanceof ItemStack) {
						ItemStack input = (ItemStack) target.getInput()[0];
						for (int i = 1; i < target.getRecipeSize(); i++) {
							if (target.getInput()[i] instanceof ItemStack) {
								match &= ItemHelper.itemsIdentical(input, (ItemStack) target.getInput()[i]);
							} else {
								match = false;
							}
						}
						if (match) {
							addDefaultStorageRecipe((ItemStack) target.getInput()[0], target.getRecipeOutput(), target.getRecipeSize());
						}
					}
				}
			} else if (recipe instanceof ShapelessOreRecipe) {
				ShapelessOreRecipe target = (ShapelessOreRecipe) recipe;
				if (target.getRecipeSize() == 4 || target.getRecipeSize() == 9) {
					boolean match = true;
					if (target.getInput().get(0) instanceof List) {
						ItemStack input = ((List<ItemStack>) target.getInput().get(0)).get(0);
						for (int i = 1; i < target.getRecipeSize(); i++) {
							if (target.getInput().get(i) instanceof List) {
								ItemStack compare = ((List<ItemStack>) target.getInput().get(i)).get(0);
								match &= ItemHelper.itemsIdentical(input, compare);
							} else {
								match = false;
							}
						}
						if (match) {
							List<ItemStack> ores = (List<ItemStack>) target.getInput().get(0);
							for (ItemStack ore : ores) {
								addDefaultStorageRecipe(ore, target.getRecipeOutput(), target.getRecipeSize());
							}
						}
					} else if (target.getInput().get(0) instanceof ItemStack) {
						ItemStack input = (ItemStack) target.getInput().get(0);
						for (int i = 1; i < target.getRecipeSize(); i++) {
							if (target.getInput().get(i) instanceof ItemStack) {
								match &= ItemHelper.itemsIdentical(input, (ItemStack) target.getInput().get(i));
							} else {
								match = false;
							}
						}
						if (match) {
							addDefaultStorageRecipe((ItemStack) target.getInput().get(0), target.getRecipeOutput(), target.getRecipeSize());
						}
					}
				}
			}
		}
	}

	public static void refresh() {

		Map<ComparableItemStackCompactor, RecipeCompactor> tempPress = new THashMap<>(recipeMapPress.size());
		Map<ComparableItemStackCompactor, RecipeCompactor> tempStorage = new THashMap<>(recipeMapStorage.size());
		Map<ComparableItemStackCompactor, RecipeCompactor> tempMint = new THashMap<>(recipeMapMint.size());
		Set<ComparableItemStackCompactor> tempSet = new THashSet<>();
		RecipeCompactor tempRecipe;

		for (Map.Entry<ComparableItemStackCompactor, RecipeCompactor> entry : recipeMapPress.entrySet()) {
			tempRecipe = entry.getValue();
			ComparableItemStackCompactor input = new ComparableItemStackCompactor(tempRecipe.input);
			tempPress.put(input, tempRecipe);
			tempSet.add(input);
		}
		for (Map.Entry<ComparableItemStackCompactor, RecipeCompactor> entry : recipeMapStorage.entrySet()) {
			tempRecipe = entry.getValue();
			ComparableItemStackCompactor input = new ComparableItemStackCompactor(tempRecipe.input);
			tempStorage.put(input, tempRecipe);
			tempSet.add(input);
		}
		for (Map.Entry<ComparableItemStackCompactor, RecipeCompactor> entry : recipeMapMint.entrySet()) {
			tempRecipe = entry.getValue();
			ComparableItemStackCompactor input = new ComparableItemStackCompactor(tempRecipe.input);
			tempMint.put(input, tempRecipe);
			tempSet.add(input);
		}
		recipeMapPress.clear();
		recipeMapStorage.clear();
		recipeMapMint.clear();

		recipeMapPress = tempPress;
		recipeMapStorage = tempStorage;
		recipeMapMint = tempMint;

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
				recipeMapPress.put(new ComparableItemStackCompactor(input), recipe);
				break;
			case STORAGE:
				recipeMapStorage.put(new ComparableItemStackCompactor(input), recipe);
				break;
			case MINT:
				recipeMapMint.put(new ComparableItemStackCompactor(input), recipe);
				break;
		}
		validationSet.add(new ComparableItemStackCompactor(input));
		return true;
	}

	/* REMOVE RECIPES */
	public static boolean removeRecipe(ItemStack input, Mode mode) {

		switch (mode) {
			case PRESS:
				return recipeMapPress.remove(new ComparableItemStackCompactor(input)) != null;
			case STORAGE:
				return recipeMapStorage.remove(new ComparableItemStackCompactor(input)) != null;
		}
		return recipeMapMint.remove(new ComparableItemStackCompactor(input)) != null;
	}

	/* HELPERS */
	private static void addDefaultPressRecipe(ItemStack input, ItemStack output) {

		addRecipe(DEFAULT_ENERGY, input, output, Mode.PRESS);
	}

	private static void addDefaultStorageRecipe(ItemStack input, ItemStack output, int count) {

		ItemStack inputStack = ItemHelper.cloneStack(input, count);

		if (!recipeExists(inputStack, Mode.STORAGE)) {
			addRecipe(DEFAULT_ENERGY_STORAGE, inputStack, output, Mode.STORAGE);
		}
	}

	private static void addDefaultMintRecipe(ItemStack nugget, ItemStack ingot, ItemStack block, ItemStack output) {

		addRecipe(DEFAULT_ENERGY / 4, ItemHelper.cloneStack(nugget, 3), ItemHelper.cloneStack(output, 1), Mode.MINT);
		addRecipe(DEFAULT_ENERGY, ItemHelper.cloneStack(ingot, 1), ItemHelper.cloneStack(output, 3), Mode.MINT);
		addRecipe(DEFAULT_ENERGY * 8, ItemHelper.cloneStack(block, 1), ItemHelper.cloneStack(output, 27), Mode.MINT);
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

		public static final String NUGGET = "nugget";
		public static final String INGOT = "ingot";
		public static final String BLOCK = "block";

		public static boolean safeOreType(String oreName) {

			return oreName.startsWith(NUGGET) || oreName.startsWith(INGOT) || oreName.startsWith(BLOCK);
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

		public ComparableItemStackCompactor(ItemStack stack) {

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
