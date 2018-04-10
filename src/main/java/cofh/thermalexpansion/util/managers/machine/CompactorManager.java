package cofh.thermalexpansion.util.managers.machine;

import cofh.core.inventory.ComparableItemStackValidated;
import cofh.core.util.helpers.ItemHelper;
import cofh.core.util.helpers.StringHelper;
import cofh.thermalfoundation.item.ItemCoin;
import cofh.thermalfoundation.item.ItemMaterial;
import gnu.trove.map.hash.THashMap;
import gnu.trove.set.hash.THashSet;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class CompactorManager {

	private static Map<ComparableItemStackValidated, CompactorRecipe> recipeMapAll = new THashMap<>();
	private static Map<ComparableItemStackValidated, CompactorRecipe> recipeMapPlate = new THashMap<>();
	private static Map<ComparableItemStackValidated, CompactorRecipe> recipeMapMint = new THashMap<>();
	private static Map<ComparableItemStackValidated, CompactorRecipe> recipeMapGear = new THashMap<>();
	private static Set<ComparableItemStackValidated> validationSet = new THashSet<>();

	public static final int DEFAULT_ENERGY = 4000;

	public static CompactorRecipe getRecipe(ItemStack input, Mode mode) {

		if (input.isEmpty()) {
			return null;
		}
		ComparableItemStackValidated query = new ComparableItemStackValidated(input);
		CompactorRecipe recipe = null;

		switch (mode) {
			case PLATE:
				recipe = recipeMapPlate.get(query);
				if (recipe == null) {
					query.metadata = OreDictionary.WILDCARD_VALUE;
					recipe = recipeMapPlate.get(query);
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
		if (recipe == null) {
			return recipeMapAll.get(query);
		}
		return recipe;
	}

	public static boolean recipeExists(ItemStack input, Mode mode) {

		return getRecipe(input, mode) != null;
	}

	public static CompactorRecipe[] getRecipeList(Mode mode) {

		switch (mode) {
			case PLATE:
				return recipeMapPlate.values().toArray(new CompactorRecipe[recipeMapPlate.size()]);
			case MINT:
				return recipeMapMint.values().toArray(new CompactorRecipe[recipeMapMint.size()]);
			case GEAR:
				return recipeMapGear.values().toArray(new CompactorRecipe[recipeMapGear.size()]);
			default:
				return recipeMapAll.values().toArray(new CompactorRecipe[recipeMapAll.size()]);
		}
	}

	public static boolean isItemValid(ItemStack input) {

		return !input.isEmpty() && validationSet.contains(new ComparableItemStackValidated(input));
	}

	public static void initialize() {

		addDefaultRecipe(new ItemStack(Items.BLAZE_POWDER, 5), new ItemStack(Items.BLAZE_ROD));
		addDefaultRecipe(ItemHelper.cloneStack(ItemMaterial.dustBlizz, 5), ItemMaterial.rodBlizz);
		addDefaultRecipe(ItemHelper.cloneStack(ItemMaterial.dustBlitz, 5), ItemMaterial.rodBlitz);
		addDefaultRecipe(ItemHelper.cloneStack(ItemMaterial.dustBasalz, 5), ItemMaterial.rodBasalz);

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
	}

	public static void refresh() {

		Map<ComparableItemStackValidated, CompactorRecipe> tempAll = new THashMap<>(recipeMapAll.size());
		Map<ComparableItemStackValidated, CompactorRecipe> tempPlate = new THashMap<>(recipeMapPlate.size());
		Map<ComparableItemStackValidated, CompactorRecipe> tempMint = new THashMap<>(recipeMapMint.size());
		Map<ComparableItemStackValidated, CompactorRecipe> tempGear = new THashMap<>(recipeMapGear.size());
		Set<ComparableItemStackValidated> tempSet = new THashSet<>();
		CompactorRecipe tempRecipe;

		for (Map.Entry<ComparableItemStackValidated, CompactorRecipe> entry : recipeMapAll.entrySet()) {
			tempRecipe = entry.getValue();
			ComparableItemStackValidated input = new ComparableItemStackValidated(tempRecipe.input);
			tempPlate.put(input, tempRecipe);
			tempSet.add(input);
		}
		for (Map.Entry<ComparableItemStackValidated, CompactorRecipe> entry : recipeMapPlate.entrySet()) {
			tempRecipe = entry.getValue();
			ComparableItemStackValidated input = new ComparableItemStackValidated(tempRecipe.input);
			tempPlate.put(input, tempRecipe);
			tempSet.add(input);
		}
		for (Map.Entry<ComparableItemStackValidated, CompactorRecipe> entry : recipeMapMint.entrySet()) {
			tempRecipe = entry.getValue();
			ComparableItemStackValidated input = new ComparableItemStackValidated(tempRecipe.input);
			tempMint.put(input, tempRecipe);
			tempSet.add(input);
		}
		for (Map.Entry<ComparableItemStackValidated, CompactorRecipe> entry : recipeMapGear.entrySet()) {
			tempRecipe = entry.getValue();
			ComparableItemStackValidated input = new ComparableItemStackValidated(tempRecipe.input);
			tempGear.put(input, tempRecipe);
			tempSet.add(input);
		}
		recipeMapAll.clear();
		recipeMapPlate.clear();
		recipeMapMint.clear();
		recipeMapGear.clear();

		recipeMapAll = tempAll;
		recipeMapPlate = tempPlate;
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
			case ALL:
				recipeMapAll.put(new ComparableItemStackValidated(input), recipe);
				break;
			case PLATE:
				recipeMapPlate.put(new ComparableItemStackValidated(input), recipe);
				break;
			case MINT:
				recipeMapMint.put(new ComparableItemStackValidated(input), recipe);
				break;
			case GEAR:
				recipeMapGear.put(new ComparableItemStackValidated(input), recipe);
				break;
		}
		validationSet.add(new ComparableItemStackValidated(input));
		return recipe;
	}

	/* REMOVE RECIPES */
	public static CompactorRecipe removeRecipe(ItemStack input, Mode mode) {

		switch (mode) {
			case ALL:
				return recipeMapAll.remove(new ComparableItemStackValidated(input));
			case PLATE:
				return recipeMapPlate.remove(new ComparableItemStackValidated(input));
			case MINT:
				return recipeMapMint.remove(new ComparableItemStackValidated(input));
			default:
				return recipeMapGear.remove(new ComparableItemStackValidated(input));
		}
	}

	/* HELPERS */
	public static ComparableItemStackValidated convertInput(ItemStack stack) {

		return new ComparableItemStackValidated(stack);
	}

	private static void addDefaultRecipe(ItemStack input, ItemStack output) {

		addRecipe(DEFAULT_ENERGY, input, output, Mode.ALL);
	}

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
			addRecipe(DEFAULT_ENERGY, ItemHelper.cloneStack(registeredIngot.get(0), 1), ItemHelper.cloneStack(plate, 1), Mode.PLATE);
		}
		if (!registeredGem.isEmpty()) {
			addRecipe(DEFAULT_ENERGY, ItemHelper.cloneStack(registeredGem.get(0), 1), ItemHelper.cloneStack(plate, 1), Mode.PLATE);
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
		ALL, PLATE, MINT, GEAR
	}

}
