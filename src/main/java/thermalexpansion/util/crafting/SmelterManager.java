package thermalexpansion.util.crafting;

import cofh.util.ItemHelper;
import cofh.util.StringHelper;
import cofh.util.inventory.ComparableItemStackSafe;

import geologic.item.GLItems;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import thermalexpansion.ThermalExpansion;
import thermalexpansion.block.TEBlocks;
import thermalexpansion.item.TEItems;

public class SmelterManager {

	public static ItemStack blockSand = new ItemStack(Blocks.sand);
	public static ItemStack ingotIron = new ItemStack(Items.iron_ingot);
	public static ItemStack ingotGold = new ItemStack(Items.gold_ingot);

	private static Map<List, RecipeSmelter> recipeMap = new HashMap();
	private static Set<ComparableItemStackSafe> validationSet = new HashSet();
	private static ComparableItemStackSafe query = new ComparableItemStackSafe(new ItemStack(Blocks.stone));
	private static ComparableItemStackSafe querySecondary = new ComparableItemStackSafe(new ItemStack(Blocks.stone));
	private static boolean allowOverwrite = false;

	private static ArrayList<String> blastList = new ArrayList<String>();

	static {
		allowOverwrite = ThermalExpansion.config.get("tweak.crafting", "Smelter.AllowRecipeOverwrite", false);

		blastList.add("enderium");

		blastList.add("aluminum");
		blastList.add("ardite");
		blastList.add("cobalt");
	}

	public static boolean isRecipeReversed(ItemStack primaryInput, ItemStack secondaryInput) {

		if (primaryInput == null || secondaryInput == null) {
			return false;
		}
		RecipeSmelter recipe = recipeMap.get(Arrays.asList(query.set(primaryInput), querySecondary.set(secondaryInput)));
		return recipe != null ? false : recipeMap.get(Arrays.asList(querySecondary, query)) != null;
	}

	public static RecipeSmelter getRecipe(ItemStack primaryInput, ItemStack secondaryInput) {

		if (primaryInput == null || secondaryInput == null) {
			return null;
		}
		RecipeSmelter recipe = recipeMap.get(Arrays.asList(query.set(primaryInput), querySecondary.set(secondaryInput)));

		if (recipe == null) {
			recipe = recipeMap.get(Arrays.asList(querySecondary, query));
		}
		if (recipe == null) {
			return null;
		}
		return recipe;
	}

	public static boolean recipeExists(ItemStack primaryInput, ItemStack secondaryInput) {

		return getRecipe(primaryInput, secondaryInput) != null;
	}

	public static RecipeSmelter[] getRecipeList() {

		return recipeMap.values().toArray(new RecipeSmelter[0]);
	}

	public static boolean isItemValid(ItemStack input) {

		if (input == null) {
			return false;
		}
		return validationSet.contains(query.set(input));
	}

	public static boolean isStandardOre(String oreName) {

		return ItemHelper.oreNameExists(oreName) && FurnaceManager.recipeExists(OreDictionary.getOres(oreName).get(0));
	}

	public static void addDefaultRecipes() {

		addTERecipe(4000, new ItemStack(Blocks.redstone_ore), blockSand, new ItemStack(Blocks.redstone_block), TEItems.slagRich, 40);
		addTERecipe(4000, new ItemStack(Blocks.netherrack, 4), new ItemStack(Blocks.soul_sand), new ItemStack(Blocks.nether_brick, 2), GLItems.dustSulfur, 25);
	}

	public static void loadRecipes() {

		addDefaultRecipes();

		boolean steelRecipe = ThermalExpansion.config.get("tweak.crafting", "Smelter.Steel.Enable", true);

		/* BLOCKS */
		ItemStack blockGlass = new ItemStack(TEBlocks.blockGlass, 2, 0);
		addAlloyRecipe(4000, "dustLead", 1, "dustObsidian", 8, blockGlass);
		addAlloyRecipe(4000, "ingotLead", 1, "dustObsidian", 8, blockGlass);

		addDefaultOreDictionaryRecipe("oreIron", "dustIron", ingotIron, GLItems.ingotNickel);
		addDefaultOreDictionaryRecipe("oreGold", "dustGold", ingotGold, null, 10, 75, 25);
		addDefaultOreDictionaryRecipe("oreCopper", "dustCopper", GLItems.ingotCopper, ingotGold);
		addDefaultOreDictionaryRecipe("oreTin", "dustTin", GLItems.ingotTin, ingotIron);
		addDefaultOreDictionaryRecipe("oreSilver", "dustSilver", GLItems.ingotSilver, GLItems.ingotLead);
		addDefaultOreDictionaryRecipe("oreLead", "dustLead", GLItems.ingotLead, GLItems.ingotSilver);
		addDefaultOreDictionaryRecipe("oreNickel", "dustNickel", GLItems.ingotNickel, GLItems.ingotPlatinum, 15, 75, 25);
		addDefaultOreDictionaryRecipe("orePlatinum", "dustPlatinum", GLItems.ingotPlatinum);
		addDefaultOreDictionaryRecipe(null, "dustElectrum", GLItems.ingotElectrum);
		addDefaultOreDictionaryRecipe(null, "dustInvar", GLItems.ingotInvar);
		addDefaultOreDictionaryRecipe(null, "dustBronze", GLItems.ingotBronze);

		/* ALLOYS */
		ItemStack stackElectrum = ItemHelper.cloneStack(GLItems.ingotElectrum, 2);
		ItemStack stackInvar = ItemHelper.cloneStack(GLItems.ingotInvar, 3);
		ItemStack stackBronze = ItemHelper.cloneStack(GLItems.ingotBronze, 4);

		addAlloyRecipe(1600, "dustSilver", 1, "dustGold", 1, stackElectrum);
		addAlloyRecipe(2400, "ingotSilver", 1, "ingotGold", 1, stackElectrum);
		addAlloyRecipe(1600, "dustNickel", 1, "dustIron", 2, stackInvar);
		addAlloyRecipe(2400, "ingotNickel", 1, "ingotIron", 2, stackInvar);
		addAlloyRecipe(1600, "dustTin", 1, "dustCopper", 3, stackBronze);
		addAlloyRecipe(2400, "ingotTin", 1, "ingotCopper", 3, stackBronze);

		/* CROSSMOD SUPPORT */
		if (ItemHelper.oreNameExists("ingotSteel") && steelRecipe) {
			addAlloyRecipe(8000, "dustCoal", 2, "dustSteel", 1, ItemHelper.cloneStack(OreDictionary.getOres("ingotSteel").get(0), 1));
			addAlloyRecipe(8000, "dustCoal", 2, "dustIron", 1, ItemHelper.cloneStack(OreDictionary.getOres("ingotSteel").get(0), 1));
			addAlloyRecipe(8000, "dustCoal", 2, "ingotIron", 1, ItemHelper.cloneStack(OreDictionary.getOres("ingotSteel").get(0), 1));
			addAlloyRecipe(8000, "charcoal", 4, "dustSteel", 1, ItemHelper.cloneStack(OreDictionary.getOres("ingotSteel").get(0), 1));
			addAlloyRecipe(8000, "charcoal", 4, "dustIron", 1, ItemHelper.cloneStack(OreDictionary.getOres("ingotSteel").get(0), 1));
			addAlloyRecipe(8000, "charcoal", 4, "ingotIron", 1, ItemHelper.cloneStack(OreDictionary.getOres("ingotSteel").get(0), 1));
		}
		String[] oreNameList = OreDictionary.getOreNames();
		String oreName = "";

		for (int i = 0; i < oreNameList.length; i++) {
			if (oreNameList[i].startsWith("ore")) {
				oreName = oreNameList[i].substring(3, oreNameList[i].length());

				if (isStandardOre(oreNameList[i])) {
					addDefaultOreDictionaryRecipe(oreName);
				}
			} else if (oreNameList[i].startsWith("dust")) {
				oreName = oreNameList[i].substring(4, oreNameList[i].length());

				if (isStandardOre(oreNameList[i])) {
					addDefaultOreDictionaryRecipe(oreName);
				}
			}
		}
		for (int i = 0; i < blastList.size(); i++) {
			addBlastOreRecipe(blastList.get(i));
		}
	}

	/* ADD RECIPES */
	public static boolean addTERecipe(int energy, ItemStack primaryInput, ItemStack secondaryInput, ItemStack primaryOutput, ItemStack secondaryOutput, int secondaryChance) {

		if (primaryInput == null || secondaryInput == null || energy <= 0) {
			return false;
		}
		RecipeSmelter recipe = new RecipeSmelter(primaryInput, secondaryInput, primaryOutput, secondaryOutput, secondaryChance, energy);
		recipeMap.put(Arrays.asList(new ComparableItemStackSafe(primaryInput), new ComparableItemStackSafe(secondaryInput)), recipe);
		validationSet.add(new ComparableItemStackSafe(primaryInput));
		validationSet.add(new ComparableItemStackSafe(secondaryInput));
		return true;
	}

	public static boolean addRecipe(int energy, ItemStack primaryInput, ItemStack secondaryInput, ItemStack primaryOutput, ItemStack secondaryOutput, int secondaryChance, boolean overwrite) {

		if (primaryInput == null || secondaryInput == null || energy <= 0 || !(allowOverwrite & overwrite) && recipeExists(primaryInput, secondaryInput)) {
			return false;
		}
		RecipeSmelter recipe = new RecipeSmelter(primaryInput, secondaryInput, primaryOutput, secondaryOutput, secondaryChance, energy);
		recipeMap.put(Arrays.asList(new ComparableItemStackSafe(primaryInput), new ComparableItemStackSafe(secondaryInput)), recipe);
		validationSet.add(new ComparableItemStackSafe(primaryInput));
		validationSet.add(new ComparableItemStackSafe(secondaryInput));
		return true;
	}

	/* HELPER FUNCTIONS */
	public static void addDefaultOreDictionaryRecipe(String oreName, String dustName, ItemStack ingot, ItemStack ingotRelated, int richSlagChance, int slagOreChance, int slagDustChance) {

		if (ingot == null) {
			return;
		}
		ItemStack ingot2 = ItemHelper.cloneStack(ingot, 2);

		if (oreName != null) {
			addOreToIngotRecipe(oreName, ingot2, ItemHelper.cloneStack(ingot, 3), ItemHelper.cloneStack(ingotRelated, 1), richSlagChance, slagOreChance);
		}
		if (dustName != null) {
			addDustToIngotRecipe(dustName, ingot2, slagDustChance);
		}
	}

	public static void addDefaultOreDictionaryRecipe(String oreType) {

		addDefaultOreDictionaryRecipe(oreType, "");
	}

	public static void addDefaultOreDictionaryRecipe(String oreType, String relatedType) {

		String oreName = "ore" + StringHelper.titleCase(oreType);
		String dustName = "dust" + StringHelper.titleCase(oreType);
		String ingotName = "ingot" + StringHelper.titleCase(oreType);

		ArrayList<ItemStack> registeredOre = OreDictionary.getOres(oreName);
		ArrayList<ItemStack> registeredDust = OreDictionary.getOres(dustName);
		ArrayList<ItemStack> registeredIngot = OreDictionary.getOres(ingotName);
		ArrayList<ItemStack> registeredRelated = new ArrayList<ItemStack>();

		if (relatedType != "") {
			String relatedName = "ingot" + StringHelper.titleCase(relatedType);
			registeredRelated = OreDictionary.getOres(relatedName);
		}
		if (registeredIngot.isEmpty()) {
			return;
		}
		if (registeredOre.isEmpty()) {
			oreName = null;
		}
		if (registeredDust.isEmpty()) {
			dustName = null;
		}
		if (!registeredRelated.isEmpty() && registeredRelated.get(0) != null) {
			addDefaultOreDictionaryRecipe(oreName, dustName, registeredIngot.get(0), registeredRelated.get(0), 5, 75, 25);
		} else {
			addDefaultOreDictionaryRecipe(oreName, dustName, registeredIngot.get(0), null, 5, 75, 25);
		}
	}

	public static void addDefaultOreDictionaryRecipe(String oreName, String dustName, ItemStack ingot) {

		addDefaultOreDictionaryRecipe(oreName, dustName, ingot, null, 5, 75, 25);
	}

	public static void addDefaultOreDictionaryRecipe(String oreName, String dustName, ItemStack ingot, ItemStack ingotRelated) {

		addDefaultOreDictionaryRecipe(oreName, dustName, ingot, ingotRelated, 5, 75, 25);
	}

	public static void addOreToIngotRecipe(String oreName, ItemStack ingot2, ItemStack ingot3, ItemStack ingotSecondary, int richSlagChance, int slagOreChance) {

		ArrayList<ItemStack> registeredOres = OreDictionary.getOres(oreName);

		if (registeredOres.size() > 0) {
			ItemStack ore = registeredOres.get(0);
			addRecipe(3200, ore, blockSand, ingot2, TEItems.slagRich, richSlagChance);
			addRecipe(4000, ore, TEItems.slagRich, ingot3, TEItems.slag, slagOreChance);
			addRecipe(4000, ore, GLItems.dustPyrotheum, ingot2, TEItems.slagRich, Math.min(60, richSlagChance * 3));

			if (ingotSecondary != null) {
				addRecipe(4000, ore, TEItems.crystalCinnabar, ingot3, ingotSecondary, 100);
			} else {
				addRecipe(4000, ore, TEItems.crystalCinnabar, ingot3, TEItems.slagRich, 75);
			}
		}
	}

	public static void addDustToIngotRecipe(String dustName, ItemStack ingot2, int slagDustChance) {

		ArrayList<ItemStack> registeredOres = OreDictionary.getOres(dustName);

		if (registeredOres.size() > 0) {
			addRecipe(800, ItemHelper.cloneStack(registeredOres.get(0), 2), blockSand, ingot2, TEItems.slag, slagDustChance);
		}
	}

	public static void addAlloyRecipe(int energy, String primaryOreName, int primaryAmount, String secondaryOreName, int secondaryAmount, ItemStack primaryOutput) {

		ArrayList<ItemStack> primaryOreList = OreDictionary.getOres(primaryOreName);
		ArrayList<ItemStack> secondaryOreList = OreDictionary.getOres(secondaryOreName);

		if (primaryOreList.size() > 0 && secondaryOreList.size() > 0) {
			addAlloyRecipe(energy, ItemHelper.cloneStack(primaryOreList.get(0), primaryAmount), ItemHelper.cloneStack(secondaryOreList.get(0), secondaryAmount), primaryOutput);
		}
	}

	public static void addAlloyRecipe(int energy, ItemStack primaryInput, ItemStack secondaryInput, ItemStack primaryOutput) {

		addTERecipe(energy, primaryInput, secondaryInput, primaryOutput, null, 0);
	}

	public static void addBlastOreRecipe(String oreType) {

		String oreName = "ore" + StringHelper.titleCase(oreType);
		String dustName = "dust" + StringHelper.titleCase(oreType);
		String ingotName = "ingot" + StringHelper.titleCase(oreType);

		ArrayList<ItemStack> registeredOre = OreDictionary.getOres(oreName);
		ArrayList<ItemStack> registeredDust = OreDictionary.getOres(dustName);
		ArrayList<ItemStack> registeredIngot = OreDictionary.getOres(ingotName);

		if (registeredIngot.isEmpty()) {
			return;
		}
		if (!registeredOre.isEmpty()) {
			addRecipe(12000, ItemHelper.cloneStack(registeredOre.get(0), 1), GLItems.dustPyrotheum, ItemHelper.cloneStack(registeredIngot.get(0), 2));
		}
		if (!registeredDust.isEmpty()) {
			addRecipe(8000, ItemHelper.cloneStack(registeredDust.get(0), 2), GLItems.dustPyrotheum, ItemHelper.cloneStack(registeredIngot.get(0), 2));
		}
	}

	public static void addBlastOreName(String oreName) {

		blastList.add(StringHelper.camelCase(oreName));
	}

	public static boolean addRecipe(int energy, ItemStack primaryInput, ItemStack secondaryInput, ItemStack primaryOutput) {

		return addRecipe(energy, primaryInput, secondaryInput, primaryOutput, false);
	}

	public static boolean addRecipe(int energy, ItemStack primaryInput, ItemStack secondaryInput, ItemStack primaryOutput, boolean overwrite) {

		return addRecipe(energy, primaryInput, secondaryInput, primaryOutput, null, 0, overwrite);
	}

	public static boolean addRecipe(int energy, ItemStack primaryInput, ItemStack secondaryInput, ItemStack primaryOutput, ItemStack secondaryOutput) {

		return addRecipe(energy, primaryInput, secondaryInput, primaryOutput, secondaryOutput, false);
	}

	public static boolean addRecipe(int energy, ItemStack primaryInput, ItemStack secondaryInput, ItemStack primaryOutput, ItemStack secondaryOutput, boolean overwrite) {

		return addRecipe(energy, primaryInput, secondaryInput, primaryOutput, secondaryOutput, 100, overwrite);
	}

	public static boolean addRecipe(int energy, ItemStack primaryInput, ItemStack secondaryInput, ItemStack primaryOutput, ItemStack secondaryOutput, int secondaryChance) {

		return addRecipe(energy, primaryInput, secondaryInput, primaryOutput, secondaryOutput, secondaryChance, false);
	}

	/* RECIPE CLASS */
	public static class RecipeSmelter {

		final ItemStack primaryInput;
		final ItemStack secondaryInput;
		final ItemStack primaryOutput;
		final ItemStack secondaryOutput;
		final int secondaryChance;
		final int energy;

		RecipeSmelter(ItemStack primaryInput, ItemStack secondaryInput, ItemStack primaryOutput, ItemStack secondaryOutput, int secondaryChance, int energy) {

			this.primaryInput = primaryInput;
			this.secondaryInput = secondaryInput;
			this.primaryOutput = primaryOutput;
			this.secondaryOutput = secondaryOutput;
			this.secondaryChance = secondaryChance;
			this.energy = energy;
		}

		public ItemStack getPrimaryInput() {

			return primaryInput.copy();
		}

		public ItemStack getSecondaryInput() {

			return secondaryInput.copy();
		}

		public ItemStack getPrimaryOutput() {

			return primaryOutput.copy();
		}

		public ItemStack getSecondaryOutput() {

			if (secondaryOutput == null) {
				return null;
			}
			return secondaryOutput.copy();
		}

		public int getSecondaryOutputChance() {

			return secondaryChance;
		}

		public int getEnergy() {

			return energy;
		}
	}

}
