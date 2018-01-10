package cofh.thermalexpansion.util.managers.machine;

import cofh.core.inventory.ComparableItemStackSafe;
import cofh.core.util.helpers.ItemHelper;
import cofh.core.util.helpers.StringHelper;
import cofh.thermalfoundation.block.BlockStorage;
import cofh.thermalfoundation.block.BlockStorageAlloy;
import cofh.thermalfoundation.item.ItemCoin;
import cofh.thermalfoundation.item.ItemMaterial;
import gnu.trove.map.hash.THashMap;
import gnu.trove.set.hash.THashSet;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraftforge.oredict.OreDictionary;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class CompactorManager {

	private static Map<ComparableItemStackSafe, CompactorRecipe> recipeMapPress = new THashMap<>();
	private static Map<ComparableItemStackSafe, CompactorRecipe> recipeMapStorage = new THashMap<>();
	private static Map<ComparableItemStackSafe, CompactorRecipe> recipeMapMint = new THashMap<>();
	private static Map<ComparableItemStackSafe, CompactorRecipe> recipeMapGear = new THashMap<>();
	private static Set<ComparableItemStackSafe> validationSet = new THashSet<>();

	public static final int DEFAULT_ENERGY = 4000;
	public static final int DEFAULT_ENERGY_STORAGE = 400;

	public static CompactorRecipe getRecipe(ItemStack input, Mode mode) {

		if (input.isEmpty()) {
			return null;
		}
		ComparableItemStackSafe query = new ComparableItemStackSafe(input);
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
			case GEAR:
				recipe = recipeMapGear.get(query);
				if (recipe == null) {
					query.metadata = OreDictionary.WILDCARD_VALUE;
					recipe = recipeMapGear.get(query);
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
			case MINT:
				return recipeMapMint.values().toArray(new CompactorRecipe[recipeMapMint.size()]);
			default:
				return recipeMapGear.values().toArray(new CompactorRecipe[recipeMapGear.size()]);
		}
	}

	public static boolean isItemValid(ItemStack input) {

		return !input.isEmpty() && validationSet.contains(new ComparableItemStackSafe(input));
	}

	public static void initialize() {

		addRecipe(DEFAULT_ENERGY, new ItemStack(Items.BLAZE_POWDER, 5), new ItemStack(Items.BLAZE_ROD), Mode.PRESS);
		addRecipe(DEFAULT_ENERGY, ItemHelper.cloneStack(ItemMaterial.dustBlizz, 5), ItemMaterial.rodBlizz, Mode.PRESS);
		addRecipe(DEFAULT_ENERGY, ItemHelper.cloneStack(ItemMaterial.dustBlitz, 5), ItemMaterial.rodBlitz, Mode.PRESS);
		addRecipe(DEFAULT_ENERGY, ItemHelper.cloneStack(ItemMaterial.dustBasalz, 5), ItemMaterial.rodBasalz, Mode.PRESS);

		/* PLATES */
		{
			addDefaultPlateRecipe("Iron", ItemMaterial.plateIron);
			addDefaultPlateRecipe("Gold", ItemMaterial.plateGold);

			addDefaultPlateRecipe("Copper", ItemMaterial.plateCopper);
			addDefaultPlateRecipe("Tin", ItemMaterial.plateTin);
			addDefaultPlateRecipe("Silver", ItemMaterial.plateSilver);
			addDefaultPlateRecipe("Lead", ItemMaterial.plateLead);
			addDefaultPlateRecipe("Aluminum", ItemMaterial.plateAluminum);
			addDefaultPlateRecipe("Nickel", ItemMaterial.plateNickel);
			addDefaultPlateRecipe("Platinum", ItemMaterial.platePlatinum);
			addDefaultPlateRecipe("Iridium", ItemMaterial.plateIridium);
			addDefaultPlateRecipe("Mithril", ItemMaterial.plateMithril);

			addDefaultPlateRecipe("Steel", ItemMaterial.plateSteel);
			addDefaultPlateRecipe("Electrum", ItemMaterial.plateElectrum);
			addDefaultPlateRecipe("Invar", ItemMaterial.plateInvar);
			addDefaultPlateRecipe("Bronze", ItemMaterial.plateBronze);
			addDefaultPlateRecipe("Constantan", ItemMaterial.plateConstantan);
			addDefaultPlateRecipe("Signalum", ItemMaterial.plateSignalum);
			addDefaultPlateRecipe("Lumium", ItemMaterial.plateLumium);
			addDefaultPlateRecipe("Enderium", ItemMaterial.plateEnderium);
		}

		/* GEARS */
		{
			addDefaultGearRecipe("Iron", ItemMaterial.gearIron);
			addDefaultGearRecipe("Gold", ItemMaterial.gearGold);

			addDefaultGearRecipe("Copper", ItemMaterial.gearCopper);
			addDefaultGearRecipe("Tin", ItemMaterial.gearTin);
			addDefaultGearRecipe("Silver", ItemMaterial.gearSilver);
			addDefaultGearRecipe("Lead", ItemMaterial.gearLead);
			addDefaultGearRecipe("Aluminum", ItemMaterial.gearAluminum);
			addDefaultGearRecipe("Nickel", ItemMaterial.gearNickel);
			addDefaultGearRecipe("Platinum", ItemMaterial.gearPlatinum);
			addDefaultGearRecipe("Iridium", ItemMaterial.gearIridium);
			addDefaultGearRecipe("Mithril", ItemMaterial.gearMithril);

			addDefaultGearRecipe("Steel", ItemMaterial.gearSteel);
			addDefaultGearRecipe("Electrum", ItemMaterial.gearElectrum);
			addDefaultGearRecipe("Invar", ItemMaterial.gearInvar);
			addDefaultGearRecipe("Bronze", ItemMaterial.gearBronze);
			addDefaultGearRecipe("Constantan", ItemMaterial.gearConstantan);
			addDefaultGearRecipe("Signalum", ItemMaterial.gearSignalum);
			addDefaultGearRecipe("Lumium", ItemMaterial.gearLumium);
			addDefaultGearRecipe("Enderium", ItemMaterial.gearEnderium);
		}

		/* COINS */
		{
			addDefaultCoinRecipe("Iron", ItemCoin.coinIron);
			addDefaultCoinRecipe("Gold", ItemCoin.coinGold);

			addDefaultCoinRecipe("Copper", ItemCoin.coinCopper);
			addDefaultCoinRecipe("Tin", ItemCoin.coinTin);
			addDefaultCoinRecipe("Silver", ItemCoin.coinSilver);
			addDefaultCoinRecipe("Lead", ItemCoin.coinLead);
			addDefaultCoinRecipe("Aluminum", ItemCoin.coinAluminum);
			addDefaultCoinRecipe("Nickel", ItemCoin.coinNickel);
			addDefaultCoinRecipe("Platinum", ItemCoin.coinPlatinum);
			addDefaultCoinRecipe("Iridium", ItemCoin.coinIridium);
			addDefaultCoinRecipe("Mithril", ItemCoin.coinMithril);

			addDefaultCoinRecipe("Steel", ItemCoin.coinSteel);
			addDefaultCoinRecipe("Electrum", ItemCoin.coinElectrum);
			addDefaultCoinRecipe("Invar", ItemCoin.coinInvar);
			addDefaultCoinRecipe("Bronze", ItemCoin.coinBronze);
			addDefaultCoinRecipe("Constantan", ItemCoin.coinConstantan);
			addDefaultCoinRecipe("Signalum", ItemCoin.coinSignalum);
			addDefaultCoinRecipe("Lumium", ItemCoin.coinLumium);
			addDefaultCoinRecipe("Enderium", ItemCoin.coinEnderium);
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

		/* LOAD RECIPES */
		loadRecipes();
	}

	public static void loadRecipes() {

		/* PLATES / GEARS / COINS */
		String[] oreNames = OreDictionary.getOreNames();
		String oreType;

		for (String oreName : oreNames) {
			if (oreName.startsWith("plate")) {
				oreType = oreName.substring(5, oreName.length());
				addDefaultPlateRecipe(oreType);
			} else if (oreName.startsWith("gear")) {
				oreType = oreName.substring(4, oreName.length());
				addDefaultGearRecipe(oreType);
			} else if (oreName.startsWith("coin")) {
				oreType = oreName.substring(4, oreName.length());
				addDefaultCoinRecipe(oreType);
			}
		}

		/* STORAGE */
		for (IRecipe recipe : CraftingManager.REGISTRY) {
			if (recipe instanceof ShapedRecipes) {
				ShapedRecipes target = (ShapedRecipes) recipe;
				if (target.recipeItems.size() == 4 || target.recipeItems.size() == 9) {
					if (target.recipeItems.get(0).getMatchingStacks().length > 0) {
						boolean match = true;
						for (int i = 1; i < target.recipeItems.size(); i++) {
							match &= target.recipeItems.get(i).getMatchingStacks().length > 0 && ItemHelper.itemsIdentical(target.recipeItems.get(0).getMatchingStacks()[0], target.recipeItems.get(i).getMatchingStacks()[0]);
						}
						if (match) {
							addDefaultStorageRecipe(target.recipeItems.get(0).getMatchingStacks()[0], target.getRecipeOutput(), target.recipeItems.size());
						}
					}
				}
			}
		}
	}

	public static void refresh() {

		Map<ComparableItemStackSafe, CompactorRecipe> tempPress = new THashMap<>(recipeMapPress.size());
		Map<ComparableItemStackSafe, CompactorRecipe> tempStorage = new THashMap<>(recipeMapStorage.size());
		Map<ComparableItemStackSafe, CompactorRecipe> tempMint = new THashMap<>(recipeMapMint.size());
		Map<ComparableItemStackSafe, CompactorRecipe> tempGear = new THashMap<>(recipeMapGear.size());
		Set<ComparableItemStackSafe> tempSet = new THashSet<>();
		CompactorRecipe tempRecipe;

		for (Map.Entry<ComparableItemStackSafe, CompactorRecipe> entry : recipeMapPress.entrySet()) {
			tempRecipe = entry.getValue();
			ComparableItemStackSafe input = new ComparableItemStackSafe(tempRecipe.input);
			tempPress.put(input, tempRecipe);
			tempSet.add(input);
		}
		for (Map.Entry<ComparableItemStackSafe, CompactorRecipe> entry : recipeMapStorage.entrySet()) {
			tempRecipe = entry.getValue();
			ComparableItemStackSafe input = new ComparableItemStackSafe(tempRecipe.input);
			tempStorage.put(input, tempRecipe);
			tempSet.add(input);
		}
		for (Map.Entry<ComparableItemStackSafe, CompactorRecipe> entry : recipeMapMint.entrySet()) {
			tempRecipe = entry.getValue();
			ComparableItemStackSafe input = new ComparableItemStackSafe(tempRecipe.input);
			tempMint.put(input, tempRecipe);
			tempSet.add(input);
		}
		for (Map.Entry<ComparableItemStackSafe, CompactorRecipe> entry : recipeMapGear.entrySet()) {
			tempRecipe = entry.getValue();
			ComparableItemStackSafe input = new ComparableItemStackSafe(tempRecipe.input);
			tempGear.put(input, tempRecipe);
			tempSet.add(input);
		}
		recipeMapPress.clear();
		recipeMapStorage.clear();
		recipeMapMint.clear();
		recipeMapGear.clear();

		recipeMapPress = tempPress;
		recipeMapStorage = tempStorage;
		recipeMapMint = tempMint;
		recipeMapGear = tempGear;

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
				recipeMapPress.put(new ComparableItemStackSafe(input), recipe);
				break;
			case STORAGE:
				recipeMapStorage.put(new ComparableItemStackSafe(input), recipe);
				break;
			case MINT:
				recipeMapMint.put(new ComparableItemStackSafe(input), recipe);
				break;
			case GEAR:
				recipeMapGear.put(new ComparableItemStackSafe(input), recipe);
				break;
		}
		validationSet.add(new ComparableItemStackSafe(input));
		return recipe;
	}

	/* REMOVE RECIPES */
	public static CompactorRecipe removeRecipe(ItemStack input, Mode mode) {

		switch (mode) {
			case PRESS:
				return recipeMapPress.remove(new ComparableItemStackSafe(input));
			case STORAGE:
				return recipeMapStorage.remove(new ComparableItemStackSafe(input));
			case MINT:
				return recipeMapMint.remove(new ComparableItemStackSafe(input));
			default:
				return recipeMapGear.remove(new ComparableItemStackSafe(input));
		}
	}

	/* HELPERS */
	private static void addDefaultPlateRecipe(String oreType) {

		if (oreType == null || oreType.isEmpty()) {
			return;
		}
		String plateName = "plate" + StringHelper.titleCase(oreType);
		List<ItemStack> registeredPlate = OreDictionary.getOres(plateName, false);
		if (registeredPlate.isEmpty()) {
			return;
		}
		addDefaultPlateRecipe(oreType, registeredPlate.get(0));
	}

	private static void addDefaultPlateRecipe(String oreType, ItemStack plate) {

		if (oreType == null || oreType.isEmpty()) {
			return;
		}
		String ingotName = "ingot" + StringHelper.titleCase(oreType);
		String gemName = "gem" + StringHelper.titleCase(oreType);
		String blockName = "block" + StringHelper.titleCase(oreType);

		List<ItemStack> registeredIngot = OreDictionary.getOres(ingotName, false);
		List<ItemStack> registeredGem = OreDictionary.getOres(gemName, false);
		List<ItemStack> registeredBlock = OreDictionary.getOres(blockName, false);

		if (!registeredIngot.isEmpty()) {
			addRecipe(DEFAULT_ENERGY, ItemHelper.cloneStack(registeredIngot.get(0), 1), ItemHelper.cloneStack(plate, 1), Mode.PRESS);
		}
		if (!registeredGem.isEmpty()) {
			addRecipe(DEFAULT_ENERGY, ItemHelper.cloneStack(registeredGem.get(0), 1), ItemHelper.cloneStack(plate, 1), Mode.PRESS);
		}
		//		if (!registeredBlock.isEmpty()) {
		//			addRecipe(DEFAULT_ENERGY * 8, ItemHelper.cloneStack(registeredBlock.get(0), 1), ItemHelper.cloneStack(plate, 9), Mode.PRESS);
		//		}
	}

	private static void addDefaultGearRecipe(String oreType) {

		if (oreType == null || oreType.isEmpty()) {
			return;
		}
		String plateName = "gear" + StringHelper.titleCase(oreType);
		List<ItemStack> registeredGear = OreDictionary.getOres(plateName, false);
		if (registeredGear.isEmpty()) {
			return;
		}
		addDefaultGearRecipe(oreType, registeredGear.get(0));
	}

	private static void addDefaultGearRecipe(String oreType, ItemStack gear) {

		if (oreType == null || oreType.isEmpty()) {
			return;
		}
		String ingotName = "ingot" + StringHelper.titleCase(oreType);
		String gemName = "gem" + StringHelper.titleCase(oreType);
		String blockName = "block" + StringHelper.titleCase(oreType);

		List<ItemStack> registeredIngot = OreDictionary.getOres(ingotName, false);
		List<ItemStack> registeredGem = OreDictionary.getOres(gemName, false);
		List<ItemStack> registeredBlock = OreDictionary.getOres(blockName, false);

		if (!registeredIngot.isEmpty()) {
			addRecipe(DEFAULT_ENERGY, ItemHelper.cloneStack(registeredIngot.get(0), 4), ItemHelper.cloneStack(gear, 1), Mode.GEAR);
		}
		if (!registeredGem.isEmpty()) {
			addRecipe(DEFAULT_ENERGY, ItemHelper.cloneStack(registeredGem.get(0), 4), ItemHelper.cloneStack(gear, 1), Mode.GEAR);
		}
		//		if (!registeredBlock.isEmpty()) {
		//			addRecipe(DEFAULT_ENERGY * 8, ItemHelper.cloneStack(registeredBlock.get(0), 4), ItemHelper.cloneStack(gear, 9), Mode.GEAR);
		//		}
	}

	private static void addDefaultCoinRecipe(String oreType) {

		if (oreType == null || oreType.isEmpty()) {
			return;
		}
		String plateName = "coin" + StringHelper.titleCase(oreType);
		List<ItemStack> registeredCoin = OreDictionary.getOres(plateName, false);
		if (registeredCoin.isEmpty()) {
			return;
		}
		addDefaultCoinRecipe(oreType, registeredCoin.get(0));
	}

	private static void addDefaultCoinRecipe(String oreType, ItemStack coin) {

		if (oreType == null || oreType.isEmpty()) {
			return;
		}
		String nuggetName = "nugget" + StringHelper.titleCase(oreType);
		String ingotName = "ingot" + StringHelper.titleCase(oreType);
		String blockName = "block" + StringHelper.titleCase(oreType);

		List<ItemStack> registeredNugget = OreDictionary.getOres(nuggetName, false);
		List<ItemStack> registeredIngot = OreDictionary.getOres(ingotName, false);
		List<ItemStack> registeredBlock = OreDictionary.getOres(blockName, false);

		//		if (!registeredNugget.isEmpty()) {
		//			addRecipe(DEFAULT_ENERGY / 2, ItemHelper.cloneStack(registeredNugget.get(0), 3), ItemHelper.cloneStack(coin, 1), Mode.MINT);
		//		}
		if (!registeredIngot.isEmpty()) {
			addRecipe(DEFAULT_ENERGY, ItemHelper.cloneStack(registeredIngot.get(0), 1), ItemHelper.cloneStack(coin, 3), Mode.MINT);
		}
		//		if (!registeredBlock.isEmpty()) {
		//			addRecipe(DEFAULT_ENERGY * 6, ItemHelper.cloneStack(registeredBlock.get(0), 1), ItemHelper.cloneStack(coin, 27), Mode.MINT);
		//		}
	}

	public static void addDefaultPressRecipe(ItemStack input, ItemStack output) {

		addRecipe(DEFAULT_ENERGY, input, output, Mode.PRESS);
	}

	public static void addDefaultStorageRecipe(ItemStack input, ItemStack output, int count) {

		ItemStack inputStack = ItemHelper.cloneStack(input, count);

		if (!recipeExists(inputStack, Mode.STORAGE)) {
			addRecipe(DEFAULT_ENERGY_STORAGE, inputStack, output, Mode.STORAGE);
		}
	}

	public static void addDefaultStorageRecipe(ItemStack input, ItemStack output) {

		addDefaultStorageRecipe(input, output, 9);
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
		PRESS, STORAGE, MINT, GEAR
	}

}
