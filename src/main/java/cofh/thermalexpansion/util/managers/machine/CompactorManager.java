package cofh.thermalexpansion.util.managers.machine;

import cofh.core.inventory.ComparableItemStack;
import cofh.core.util.helpers.ItemHelper;
import cofh.core.util.oredict.OreDictionaryArbiter;
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
import java.util.Map;
import java.util.Set;

public class CompactorManager {

	private static Map<ComparableItemStackCompactor, CompactorRecipe> recipeMapPress = new THashMap<>();
	private static Map<ComparableItemStackCompactor, CompactorRecipe> recipeMapStorage = new THashMap<>();
	private static Map<ComparableItemStackCompactor, CompactorRecipe> recipeMapMint = new THashMap<>();
	private static Set<ComparableItemStackCompactor> validationSet = new THashSet<>();

	public static final int DEFAULT_ENERGY = 4000;
	public static final int DEFAULT_ENERGY_STORAGE = 400;

	public static CompactorRecipe getRecipe(ItemStack input, Mode mode) {

		if (input.isEmpty()) {
			return null;
		}
		ComparableItemStackCompactor query = new ComparableItemStackCompactor(input);
		CompactorRecipe recipe = null;

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

	public static CompactorRecipe[] getRecipeList(Mode mode) {

		switch (mode) {
			case PRESS:
				return recipeMapPress.values().toArray(new CompactorRecipe[recipeMapPress.size()]);
			case STORAGE:
				return recipeMapStorage.values().toArray(new CompactorRecipe[recipeMapStorage.size()]);
		}
		return recipeMapMint.values().toArray(new CompactorRecipe[recipeMapMint.size()]);
	}

	public static boolean isItemValid(ItemStack input) {

		return !input.isEmpty() && validationSet.contains(new ComparableItemStackCompactor(input));
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
			addDefaultStorageRecipe(ItemMaterial.ingotIron, new ItemStack(Blocks.IRON_BLOCK));
			addDefaultStorageRecipe(ItemMaterial.ingotGold, new ItemStack(Blocks.GOLD_BLOCK));
			addDefaultStorageRecipe(ItemMaterial.gemDiamond, new ItemStack(Blocks.DIAMOND_BLOCK));
			addDefaultStorageRecipe(ItemMaterial.gemEmerald, new ItemStack(Blocks.EMERALD_BLOCK));

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
			addDefaultStorageRecipe(ItemMaterial.ingotInvar, BlockStorageAlloy.blockInvar);
			addDefaultStorageRecipe(ItemMaterial.ingotBronze, BlockStorageAlloy.blockBronze);
			addDefaultStorageRecipe(ItemMaterial.ingotConstantan, BlockStorageAlloy.blockConstantan);
			addDefaultStorageRecipe(ItemMaterial.ingotSignalum, BlockStorageAlloy.blockSignalum);
			addDefaultStorageRecipe(ItemMaterial.ingotLumium, BlockStorageAlloy.blockLumium);
			addDefaultStorageRecipe(ItemMaterial.ingotEnderium, BlockStorageAlloy.blockEnderium);

			addDefaultStorageRecipe(ItemMaterial.nuggetIron, ItemMaterial.ingotIron);
			addDefaultStorageRecipe(ItemMaterial.nuggetGold, ItemMaterial.ingotGold);
			addDefaultStorageRecipe(ItemMaterial.nuggetDiamond, ItemMaterial.gemDiamond);
			addDefaultStorageRecipe(ItemMaterial.nuggetEmerald, ItemMaterial.gemEmerald);

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
			addDefaultStorageRecipe(ItemMaterial.nuggetInvar, ItemMaterial.ingotInvar);
			addDefaultStorageRecipe(ItemMaterial.nuggetBronze, ItemMaterial.ingotBronze);
			addDefaultStorageRecipe(ItemMaterial.nuggetConstantan, ItemMaterial.ingotConstantan);
			addDefaultStorageRecipe(ItemMaterial.nuggetSignalum, ItemMaterial.ingotSignalum);
			addDefaultStorageRecipe(ItemMaterial.nuggetLumium, ItemMaterial.ingotLumium);
			addDefaultStorageRecipe(ItemMaterial.nuggetEnderium, ItemMaterial.ingotEnderium);
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
		//loadRecipes();
	}

	public static void loadRecipes() {

		for (IRecipe recipe : CraftingManager.REGISTRY) {

			if (recipe instanceof ShapedRecipes) {
				//				ShapedRecipes target = (ShapedRecipes) recipe;
				//				if (target.recipeItems.size() == 4 || target.recipeItems.size() == 9) {
				//					boolean match = true;
				//					for (int i = 1; i < target.recipeItems.size(); i++) {
				//						match &= ItemHelper.itemsIdentical(target.recipeItems.get(0), target.recipeItems.get(i));
				//					}
				//					if (match) {
				//						addDefaultStorageRecipe(target.recipeItems.get(0), target.getRecipeOutput(), target.recipeItems.size());
				//					}
				//				}
			} else if (recipe instanceof ShapelessRecipes) {
				//				ShapelessRecipes target = (ShapelessRecipes) recipe;
				//				if (target.getRecipeSize() == 4 || target.getRecipeSize() == 9) {
				//					boolean match = true;
				//					for (int i = 1; i < target.getRecipeSize(); i++) {
				//						match &= ItemHelper.itemsIdentical(target.recipeItems.get(0), target.recipeItems.get(i));
				//					}
				//					if (match) {
				//						addDefaultStorageRecipe(target.recipeItems.get(0), target.getRecipeOutput(), target.getRecipeSize());
				//					}
				//				}
			} else if (recipe instanceof ShapedOreRecipe) {
				//				ShapedOreRecipe target = (ShapedOreRecipe) recipe;
				//				if (target.getRecipeSize() == 4 || target.getRecipeSize() == 9) {
				//					boolean match = true;
				//					if (target.getInput()[0] instanceof List && !((List) target.getInput()[0]).isEmpty()) {
				//						ItemStack input = ((List<ItemStack>) target.getInput()[0]).get(0);
				//						for (int i = 1; i < target.getRecipeSize(); i++) {
				//							if (target.getInput()[i] instanceof List && !((List) target.getInput()[i]).isEmpty()) {
				//								ItemStack compare = ((List<ItemStack>) target.getInput()[i]).get(0);
				//								match &= ItemHelper.itemsIdentical(input, compare);
				//							} else {
				//								match = false;
				//							}
				//						}
				//						if (match) {
				//							List<ItemStack> ores = (List<ItemStack>) target.getInput()[0];
				//							for (ItemStack ore : ores) {
				//								addDefaultStorageRecipe(ore, target.getRecipeOutput(), target.getRecipeSize());
				//							}
				//						}
				//					} else if (target.getInput()[0] instanceof ItemStack) {
				//						ItemStack input = (ItemStack) target.getInput()[0];
				//						for (int i = 1; i < target.getRecipeSize(); i++) {
				//							if (target.getInput()[i] instanceof ItemStack) {
				//								match &= ItemHelper.itemsIdentical(input, (ItemStack) target.getInput()[i]);
				//							} else {
				//								match = false;
				//							}
				//						}
				//						if (match) {
				//							addDefaultStorageRecipe((ItemStack) target.getInput()[0], target.getRecipeOutput(), target.getRecipeSize());
				//						}
				//					}
				//				}
			} else if (recipe instanceof ShapelessOreRecipe) {
				//				ShapelessOreRecipe target = (ShapelessOreRecipe) recipe;
				//				if (target.getRecipeSize() == 4 || target.getRecipeSize() == 9) {
				//					boolean match = true;
				//					if (target.getInput().get(0) instanceof List && !((List) target.getInput().get(0)).isEmpty()) {
				//						ItemStack input = ((List<ItemStack>) target.getInput().get(0)).get(0);
				//						for (int i = 1; i < target.getRecipeSize(); i++) {
				//							if (target.getInput().get(i) instanceof List && !((List) target.getInput().get(i)).isEmpty()) {
				//								ItemStack compare = ((List<ItemStack>) target.getInput().get(i)).get(0);
				//								match &= ItemHelper.itemsIdentical(input, compare);
				//							} else {
				//								match = false;
				//							}
				//						}
				//						if (match) {
				//							List<ItemStack> ores = (List<ItemStack>) target.getInput().get(0);
				//							for (ItemStack ore : ores) {
				//								addDefaultStorageRecipe(ore, target.getRecipeOutput(), target.getRecipeSize());
				//							}
				//						}
				//					} else if (target.getInput().get(0) instanceof ItemStack) {
				//						ItemStack input = (ItemStack) target.getInput().get(0);
				//						for (int i = 1; i < target.getRecipeSize(); i++) {
				//							if (target.getInput().get(i) instanceof ItemStack) {
				//								match &= ItemHelper.itemsIdentical(input, (ItemStack) target.getInput().get(i));
				//							} else {
				//								match = false;
				//							}
				//						}
				//						if (match) {
				//							addDefaultStorageRecipe((ItemStack) target.getInput().get(0), target.getRecipeOutput(), target.getRecipeSize());
				//						}
				//					}
				//				}
			}
		}
	}

	public static void refresh() {

		Map<ComparableItemStackCompactor, CompactorRecipe> tempPress = new THashMap<>(recipeMapPress.size());
		Map<ComparableItemStackCompactor, CompactorRecipe> tempStorage = new THashMap<>(recipeMapStorage.size());
		Map<ComparableItemStackCompactor, CompactorRecipe> tempMint = new THashMap<>(recipeMapMint.size());
		Set<ComparableItemStackCompactor> tempSet = new THashSet<>();
		CompactorRecipe tempRecipe;

		for (Map.Entry<ComparableItemStackCompactor, CompactorRecipe> entry : recipeMapPress.entrySet()) {
			tempRecipe = entry.getValue();
			ComparableItemStackCompactor input = new ComparableItemStackCompactor(tempRecipe.input);
			tempPress.put(input, tempRecipe);
			tempSet.add(input);
		}
		for (Map.Entry<ComparableItemStackCompactor, CompactorRecipe> entry : recipeMapStorage.entrySet()) {
			tempRecipe = entry.getValue();
			ComparableItemStackCompactor input = new ComparableItemStackCompactor(tempRecipe.input);
			tempStorage.put(input, tempRecipe);
			tempSet.add(input);
		}
		for (Map.Entry<ComparableItemStackCompactor, CompactorRecipe> entry : recipeMapMint.entrySet()) {
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
	public static CompactorRecipe addRecipe(int energy, ItemStack input, ItemStack output, Mode mode) {

		if (input.isEmpty() || output.isEmpty() || energy <= 0 || recipeExists(input, mode)) {
			return null;
		}
		CompactorRecipe recipe = new CompactorRecipe(input, output, energy);

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
		return recipe;
	}

	/* REMOVE RECIPES */
	public static CompactorRecipe removeRecipe(ItemStack input, Mode mode) {

		switch (mode) {
			case PRESS:
				return recipeMapPress.remove(new ComparableItemStackCompactor(input));
			case STORAGE:
				return recipeMapStorage.remove(new ComparableItemStackCompactor(input));
		}
		return recipeMapMint.remove(new ComparableItemStackCompactor(input));
	}

	/* HELPERS */
	private static void addDefaultPressRecipe(ItemStack input, ItemStack output) {

		addRecipe(DEFAULT_ENERGY, input, output, Mode.PRESS);
	}

	private static void addDefaultStorageRecipe(ItemStack input, ItemStack output) {

		ItemStack inputStack = ItemHelper.cloneStack(input, 9);

		if (!recipeExists(inputStack, Mode.STORAGE)) {
			addRecipe(DEFAULT_ENERGY_STORAGE, inputStack, output, Mode.STORAGE);
		}
	}

	private static void addDefaultStorageRecipe(ItemStack input, ItemStack output, int count) {

		ItemStack inputStack = ItemHelper.cloneStack(input, count);

		if (!recipeExists(inputStack, Mode.STORAGE)) {
			addRecipe(DEFAULT_ENERGY_STORAGE, inputStack, output, Mode.STORAGE);
		}
	}

	private static void addDefaultMintRecipe(ItemStack nugget, ItemStack ingot, ItemStack block, ItemStack output) {

		addRecipe(DEFAULT_ENERGY / 2, ItemHelper.cloneStack(nugget, 3), ItemHelper.cloneStack(output, 1), Mode.MINT);
		addRecipe(DEFAULT_ENERGY, ItemHelper.cloneStack(ingot, 1), ItemHelper.cloneStack(output, 3), Mode.MINT);
		addRecipe(DEFAULT_ENERGY * 6, ItemHelper.cloneStack(block, 1), ItemHelper.cloneStack(output, 27), Mode.MINT);
	}

	/* RECIPE CLASS */
	public static class CompactorRecipe {

		final ItemStack input;
		final ItemStack output;
		final int energy;

		CompactorRecipe(ItemStack input, ItemStack output, int energy) {

			this.input = input;
			this.output = output;
			this.energy = energy;
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
		public static final String DUST = "dust";

		public static boolean safeOreType(String oreName) {

			return oreName.startsWith(NUGGET) || oreName.startsWith(INGOT) || oreName.startsWith(BLOCK) || oreName.startsWith(DUST);
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
