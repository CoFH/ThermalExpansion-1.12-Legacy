package cofh.thermalexpansion.util.crafting;

import codechicken.lib.item.ItemStackRegistry;
import cofh.core.util.oredict.OreDictionaryArbiter;
import cofh.lib.inventory.ComparableItemStack;
import cofh.lib.util.helpers.ItemHelper;
import cofh.lib.util.helpers.MathHelper;
import cofh.lib.util.helpers.StringHelper;
import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalexpansion.api.crafting.recipes.ISmelterRecipe;
import cofh.thermalexpansion.block.simple.BlockGlass;
import cofh.thermalexpansion.item.TEItems;
import cofh.thermalfoundation.item.Equipment;
import cofh.thermalfoundation.item.TFItems;
import cofh.thermalfoundation.item.VanillaEquipment;

import gnu.trove.map.hash.THashMap;
import gnu.trove.set.hash.THashSet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public class SmelterManager {

	public static ItemStack blockSand = new ItemStack(Blocks.SAND);
	public static ItemStack blockSoulSand = new ItemStack(Blocks.SOUL_SAND);

	private static Map<List<ComparableItemStackSmelter>, RecipeSmelter> recipeMap = new THashMap<List<ComparableItemStackSmelter>, RecipeSmelter>();
	private static Set<ComparableItemStackSmelter> validationSet = new THashSet<ComparableItemStackSmelter>();
	private static Set<ComparableItemStackSmelter> lockSet = new THashSet<ComparableItemStackSmelter>();
	private static boolean allowOverwrite = false;
	public static final int DEFAULT_ENERGY = 3200;

	private static int oreMultiplier = 2;
	private static int oreMultiplierSpecial = 3;

	private static ArrayList<String> blastList = new ArrayList<String>();

	static {
		allowOverwrite = ThermalExpansion.config.get("RecipeManagers.Smelter", "AllowRecipeOverwrite", false);

		String category = "RecipeManagers.Smelter.Ore";
		String comment = "This sets the default rate for Ore->Ingot conversion. This number is used in all automatically generated recipes.";
		oreMultiplier = MathHelper.clamp(ThermalExpansion.config.get(category, "DefaultMultiplier", oreMultiplier, comment), 1, 64);

		comment = "This sets the boosted rate for Ore->Ingot conversion - when Rich Slag or Cinnabar Crystals are used. This number is used in all automatically generated recipes.";
		oreMultiplierSpecial = MathHelper.clamp(ThermalExpansion.config.get(category, "SpecialMultiplier", oreMultiplierSpecial, comment), 1, 64);

		blastList.add("mithril");
		blastList.add("enderium");

		blastList.add("aluminum");
		blastList.add("ardite");
		blastList.add("cobalt");
	}

	public static boolean isRecipeReversed(ItemStack primaryInput, ItemStack secondaryInput) {

		if (primaryInput == null || secondaryInput == null) {
			return false;
		}
		ComparableItemStackSmelter query = new ComparableItemStackSmelter(primaryInput);
		ComparableItemStackSmelter querySecondary = new ComparableItemStackSmelter(secondaryInput);

		RecipeSmelter recipe = recipeMap.get(Arrays.asList(query, querySecondary));
		return recipe == null && recipeMap.get(Arrays.asList(querySecondary, query)) != null;
	}

	public static RecipeSmelter getRecipe(ItemStack primaryInput, ItemStack secondaryInput) {

		if (primaryInput == null || secondaryInput == null) {
			return null;
		}
		ComparableItemStackSmelter query = new ComparableItemStackSmelter(primaryInput);
		ComparableItemStackSmelter querySecondary = new ComparableItemStackSmelter(secondaryInput);

		RecipeSmelter recipe = recipeMap.get(Arrays.asList(query, querySecondary));

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

		return input != null && validationSet.contains(new ComparableItemStackSmelter(input));
	}

	public static boolean isItemFlux(ItemStack input) {

		return input != null && lockSet.contains(new ComparableItemStackSmelter(input));
	}

	public static boolean isStandardOre(String oreName) {

		return ItemHelper.oreNameExists(oreName) && FurnaceManager.recipeExists(OreDictionary.getOres(oreName).get(0));
	}

	public static void addDefaultRecipes() {

		addFlux(blockSand);
		addFlux(blockSoulSand);
		addFlux(TEItems.slagRich);
		addFlux(TFItems.crystalCinnabar);
		addFlux(TFItems.dustPyrotheum);

		addTERecipe(4000, new ItemStack(Blocks.COBBLESTONE, 2), blockSand, new ItemStack(Blocks.STONEBRICK, 1), TEItems.slag, 100);
		addTERecipe(4000, new ItemStack(Blocks.REDSTONE_ORE), blockSand, new ItemStack(Blocks.REDSTONE_BLOCK), TEItems.slagRich, 50);
		addTERecipe(4000, new ItemStack(Blocks.NETHERRACK, 4), blockSoulSand, new ItemStack(Blocks.NETHER_BRICK_STAIRS, 2), TFItems.dustSulfur, 25);
		addTERecipe(4000, new ItemStack(Blocks.QUARTZ_ORE), blockSoulSand, new ItemStack(Blocks.QUARTZ_BLOCK), TEItems.slagRich, 25);
		// sulfur? rich sulfur? what do we even do here?

		{ // variable locality (let's not accidentally use it elsewhere)
			ItemStack blockGlass = ItemHelper.cloneStack(BlockGlass.glassHardened, 2);
			addAlloyRecipe(4000, "dustLead", 1, "dustObsidian", 4, blockGlass);
			addAlloyRecipe(4000, "ingotLead", 1, "dustObsidian", 4, blockGlass);
			blockGlass = ItemHelper.cloneStack(BlockGlass.glassHardenedIlluminated, 2);
			addAlloyRecipe(4000, "dustLumium", 1, "dustObsidian", 4, blockGlass);
			addAlloyRecipe(4000, "ingotLumium", 1, "dustObsidian", 4, blockGlass);
		}

		addDefaultOreDictionaryRecipe("oreIron", "dustIron", TFItems.ingotIron, TFItems.ingotNickel);
		addDefaultOreDictionaryRecipe("oreGold", "dustGold", TFItems.ingotGold, null, 20, 75, 25);
		addDefaultOreDictionaryRecipe("oreCopper", "dustCopper", TFItems.ingotCopper, TFItems.ingotGold);
		addDefaultOreDictionaryRecipe("oreTin", "dustTin", TFItems.ingotTin, TFItems.ingotIron);
		addDefaultOreDictionaryRecipe("oreSilver", "dustSilver", TFItems.ingotSilver, TFItems.ingotLead);
		addDefaultOreDictionaryRecipe("oreLead", "dustLead", TFItems.ingotLead, TFItems.ingotSilver);
		addDefaultOreDictionaryRecipe("oreNickel", "dustNickel", TFItems.ingotNickel, TFItems.ingotPlatinum, 15, 75, 25);
		addDefaultOreDictionaryRecipe("orePlatinum", "dustPlatinum", TFItems.ingotPlatinum);
		addDefaultOreDictionaryRecipe(null, "dustElectrum", TFItems.ingotElectrum);
		addDefaultOreDictionaryRecipe(null, "dustInvar", TFItems.ingotInvar);
		addDefaultOreDictionaryRecipe(null, "dustBronze", TFItems.ingotBronze);

		{ /* ALLOYS */
			ItemStack stackElectrum = ItemHelper.cloneStack(TFItems.ingotElectrum, 2);
			addAlloyRecipe(1600, "dustSilver", 1, "dustGold", 1, stackElectrum);
			addAlloyRecipe(2400, "ingotSilver", 1, "ingotGold", 1, stackElectrum);

			ItemStack stackInvar = ItemHelper.cloneStack(TFItems.ingotInvar, 3);
			addAlloyRecipe(1600, "dustNickel", 1, "dustIron", 2, stackInvar);
			addAlloyRecipe(2400, "ingotNickel", 1, "ingotIron", 2, stackInvar);

			ItemStack stackBronze = ItemHelper.cloneStack(TFItems.ingotBronze, 4);
			addAlloyRecipe(1600, "dustTin", 1, "dustCopper", 3, stackBronze);
			addAlloyRecipe(2400, "ingotTin", 1, "ingotCopper", 3, stackBronze);
		}

		{ /* RECYCLING */
			ItemStack ingot = new ItemStack(Items.IRON_INGOT, 1);
			// no minecart, rails. Railcraft causes resource duplication there
			addRecipe(5000, blockSand, new ItemStack(Items.COMPASS), new ItemStack(Items.IRON_INGOT, 4), TEItems.slagRich, 10); // consumes redstone
			addRecipe(5000, blockSand, new ItemStack(Items.FLINT_AND_STEEL), new ItemStack(Items.IRON_INGOT, 1), TEItems.slag, 90); // make a use for flint: slag!
			addRecycleRecipe(5000, new ItemStack(Items.IRON_DOOR), ingot, 6);
			addRecycleRecipe(5000, new ItemStack(Items.BUCKET), ingot, 3);
			addRecycleRecipe(5000, new ItemStack(Items.CAULDRON), ingot, 7);
			addRecycleRecipe(5000, new ItemStack(Blocks.HOPPER), ingot, 5);
			addRecycleRecipe(5000, new ItemStack(Blocks.IRON_BARS, 8), ingot, 3);
			addRecycleRecipe(5000, new ItemStack(Blocks.HEAVY_WEIGHTED_PRESSURE_PLATE), ingot, 2);
			addRecycleRecipe(5000, new ItemStack(Items.IRON_SWORD), ingot, 2);
			addRecycleRecipe(5000, new ItemStack(Items.IRON_PICKAXE), ingot, 3);
			addRecycleRecipe(5000, new ItemStack(Items.IRON_AXE), ingot, 3);
			addRecycleRecipe(5000, new ItemStack(Items.IRON_SHOVEL), ingot, 1);
			addRecycleRecipe(5000, new ItemStack(Items.IRON_HOE), ingot, 2);
			addRecycleRecipe(5000, new ItemStack(Items.IRON_HELMET), ingot, 5);
			addRecycleRecipe(5000, new ItemStack(Items.IRON_CHESTPLATE), ingot, 8);
			addRecycleRecipe(5000, new ItemStack(Items.IRON_LEGGINGS), ingot, 7);
			addRecycleRecipe(5000, new ItemStack(Items.IRON_BOOTS), ingot, 4);

			for (int i = 0; i < 3; ++i) {
				addRecycleRecipe(3800 + 1200 * (3 - i), new ItemStack(Blocks.ANVIL, 1, i), ingot, 4 + 9 * (3 - i));
			}

			ingot = new ItemStack(Items.GOLD_INGOT);
			addRecipe(5000, blockSand, new ItemStack(Items.CLOCK), new ItemStack(Items.GOLD_INGOT, 4), TEItems.slagRich, 10); // consumes redstone
			addRecycleRecipe(5000, new ItemStack(Blocks.LIGHT_WEIGHTED_PRESSURE_PLATE), ingot, 2);
			addRecycleRecipe(5000, new ItemStack(Items.GOLDEN_SWORD), ingot, 2);
			addRecycleRecipe(5000, new ItemStack(Items.GOLDEN_PICKAXE), ingot, 3);
			addRecycleRecipe(5000, new ItemStack(Items.GOLDEN_AXE), ingot, 3);
			addRecycleRecipe(5000, new ItemStack(Items.GOLDEN_SHOVEL), ingot, 1);
			addRecycleRecipe(5000, new ItemStack(Items.GOLDEN_HOE), ingot, 2);
			addRecycleRecipe(5000, new ItemStack(Items.GOLDEN_HELMET), ingot, 5);
			addRecycleRecipe(5000, new ItemStack(Items.GOLDEN_CHESTPLATE), ingot, 8);
			addRecycleRecipe(5000, new ItemStack(Items.GOLDEN_LEGGINGS), ingot, 7);
			addRecycleRecipe(5000, new ItemStack(Items.GOLDEN_BOOTS), ingot, 4);

			for (VanillaEquipment e : new VanillaEquipment[] { VanillaEquipment.Iron, VanillaEquipment.Gold }) {

				ingot = OreDictionaryArbiter.getOres("ingot" + e.name()).get(0);
				addRecycleRecipe(5000, e.toolBow, ingot, 2);
				addRecycleRecipe(5000, e.toolFishingRod, ingot, 2);
				addRecycleRecipe(5000, e.toolShears, ingot, 2);
				addRecycleRecipe(5000, e.toolSickle, ingot, 3);
			}

			for (Equipment e : Equipment.values()) {

				ingot = ItemStackRegistry.findItemStack("ThermalFoundation", "ingot" + e.name(), 1); // suck it, oredict
				addRecycleRecipe(5000, e.toolSword, ingot, 2);
				addRecycleRecipe(5000, e.toolPickaxe, ingot, 3);
				addRecycleRecipe(5000, e.toolAxe, ingot, 3);
				addRecycleRecipe(5000, e.toolShovel, ingot, 1);
				addRecycleRecipe(5000, e.toolHoe, ingot, 2);
				addRecycleRecipe(5000, e.armorHelmet, ingot, 5);
				addRecycleRecipe(5000, e.armorPlate, ingot, 8);
				addRecycleRecipe(5000, e.armorLegs, ingot, 7);
				addRecycleRecipe(5000, e.armorBoots, ingot, 4);
				addRecycleRecipe(5000, e.toolBow, ingot, 2);
				addRecycleRecipe(5000, e.toolFishingRod, ingot, 2);
				addRecycleRecipe(5000, e.toolShears, ingot, 2);
				addRecycleRecipe(5000, e.toolSickle, ingot, 3);
			}
		}
	}

	public static void loadRecipes() {

		String category = "RecipeManagers.Smelter.Recipes";

		boolean steelRecipe = ThermalExpansion.config.get(category, "Steel", true);

		if (ItemHelper.oreNameExists("ingotSteel") && steelRecipe) {
			ItemStack ingotSteel = ItemHelper.cloneStack(OreDictionary.getOres("ingotSteel").get(0), 1);

			addAlloyRecipe(8000, "dustCoal", 2, "dustSteel", 1, ingotSteel);
			addAlloyRecipe(8000, "dustCoal", 2, "dustIron", 1, ingotSteel);
			addAlloyRecipe(8000, "dustCoal", 2, "ingotIron", 1, ingotSteel);
			addAlloyRecipe(8000, "dustCharcoal", 4, "dustSteel", 1, ingotSteel);
			addAlloyRecipe(8000, "dustCharcoal", 4, "dustIron", 1, ingotSteel);
			addAlloyRecipe(8000, "dustCharcoal", 4, "ingotIron", 1, ingotSteel);
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

	public static void refreshRecipes() {

		Map<List<ComparableItemStackSmelter>, RecipeSmelter> tempMap = new THashMap<List<ComparableItemStackSmelter>, RecipeSmelter>(recipeMap.size());
		Set<ComparableItemStackSmelter> tempSet = new THashSet<ComparableItemStackSmelter>();
		RecipeSmelter tempRecipe;

		for (Entry<List<ComparableItemStackSmelter>, RecipeSmelter> entry : recipeMap.entrySet()) {
			tempRecipe = entry.getValue();
			ComparableItemStackSmelter primary = new ComparableItemStackSmelter(tempRecipe.primaryInput);
			ComparableItemStackSmelter secondary = new ComparableItemStackSmelter(tempRecipe.secondaryInput);

			tempMap.put(Arrays.asList(primary, secondary), tempRecipe);
			tempSet.add(primary);
			tempSet.add(secondary);
		}
		recipeMap.clear();
		recipeMap = tempMap;
		validationSet.clear();
		validationSet = tempSet;

		Set<ComparableItemStackSmelter> tempSet2 = new THashSet<ComparableItemStackSmelter>();
		for (ComparableItemStackSmelter entry : lockSet) {
			ComparableItemStackSmelter lock = new ComparableItemStackSmelter(new ItemStack(entry.item, entry.stackSize, entry.metadata));
			tempSet2.add(lock);
		}
		lockSet.clear();
		lockSet = tempSet2;
	}

	/* ADD RECIPES */
	protected static boolean addTERecipe(int energy, ItemStack primaryInput, ItemStack secondaryInput, ItemStack primaryOutput, ItemStack secondaryOutput,
			int secondaryChance) {

		if (primaryInput == null || secondaryInput == null || energy <= 0) {
			return false;
		}
		RecipeSmelter recipe = new RecipeSmelter(primaryInput, secondaryInput, primaryOutput, secondaryOutput, secondaryChance, energy);
		recipeMap.put(Arrays.asList(new ComparableItemStackSmelter(primaryInput), new ComparableItemStackSmelter(secondaryInput)), recipe);
		validationSet.add(new ComparableItemStackSmelter(primaryInput));
		validationSet.add(new ComparableItemStackSmelter(secondaryInput));
		return true;
	}

	public static boolean addRecipe(int energy, ItemStack primaryInput, ItemStack secondaryInput, ItemStack primaryOutput, ItemStack secondaryOutput,
			int secondaryChance, boolean overwrite) {

		if (primaryInput == null || secondaryInput == null || energy <= 0 || !(allowOverwrite & overwrite) && recipeExists(primaryInput, secondaryInput)) {
			return false;
		}
		RecipeSmelter recipe = new RecipeSmelter(primaryInput, secondaryInput, primaryOutput, secondaryOutput, secondaryChance, energy);
		recipeMap.put(Arrays.asList(new ComparableItemStackSmelter(primaryInput), new ComparableItemStackSmelter(secondaryInput)), recipe);
		validationSet.add(new ComparableItemStackSmelter(primaryInput));
		validationSet.add(new ComparableItemStackSmelter(secondaryInput));
		return true;
	}

	/* REMOVE RECIPES */
	public static boolean removeRecipe(ItemStack primaryInput, ItemStack secondaryInput) {

		return recipeMap.remove(Arrays.asList(new ComparableItemStackSmelter(primaryInput), new ComparableItemStackSmelter(secondaryInput))) != null;
	}

	/* HELPER FUNCTIONS */
	private static void addFlux(ItemStack flux) {

		lockSet.add(new ComparableItemStackSmelter(flux));
	}

	public static void addDefaultOreDictionaryRecipe(String oreName, String dustName, ItemStack ingot, ItemStack ingotRelated, int richSlagChance,
			int slagOreChance, int slagDustChance) {

		if (ingot == null) {
			return;
		}
		if (oreName != null) {
			addOreToIngotRecipe(oreName, ItemHelper.cloneStack(ingot, oreMultiplier), ItemHelper.cloneStack(ingot, oreMultiplierSpecial),
				ItemHelper.cloneStack(ingotRelated, 1), richSlagChance, slagOreChance);
		}
		if (dustName != null) {
			addDustToIngotRecipe(dustName, ItemHelper.cloneStack(ingot, 2), slagDustChance);
		}
	}

	public static void addDefaultOreDictionaryRecipe(String oreType) {

		addDefaultOreDictionaryRecipe(oreType, "");
	}

	public static void addDefaultOreDictionaryRecipe(String oreType, String relatedType) {

		if (oreType.length() <= 0) {
			return;
		}
		String oreName = "ore" + StringHelper.titleCase(oreType);
		String dustName = "dust" + StringHelper.titleCase(oreType);
		String ingotName = "ingot" + StringHelper.titleCase(oreType);
		String relatedName = null;

		List<ItemStack> registeredOre = OreDictionary.getOres(oreName);
        List<ItemStack> registeredDust = OreDictionary.getOres(dustName);
        List<ItemStack> registeredIngot = OreDictionary.getOres(ingotName);
        List<ItemStack> registeredRelated = new ArrayList<ItemStack>();

		if (relatedType != "") {
			relatedName = "ingot" + StringHelper.titleCase(relatedType);
			registeredRelated = OreDictionary.getOres(relatedName);
		}
		if (registeredIngot.isEmpty()) {
			return;
		}
		ItemStack ingot = ItemStackRegistry.findItemStack("ThermalFoundation", ingotName, 1);
		if (ingot != null && !OreDictionaryArbiter.getAllOreNames(ingot).contains(ingotName)) {
			ingot = null;
		}
		if (ingot == null) {
			ingot = registeredIngot.get(0);
		}
		if (registeredOre.isEmpty()) {
			oreName = null;
		}
		if (registeredDust.isEmpty()) {
			dustName = null;
		}
		ItemStack related = null;
		if (relatedName != null) {
			related = ItemStackRegistry.findItemStack("ThermalFoundation", relatedName, 1);
			if (related != null && !OreDictionaryArbiter.getAllOreNames(related).contains(relatedName)) {
				related = null;
			}
		}
		if (related == null && !registeredRelated.isEmpty()) {
			related = registeredRelated.get(0);
		}
		addDefaultOreDictionaryRecipe(oreName, dustName, ingot, related, 5, 75, 25);
	}

	public static void addDefaultOreDictionaryRecipe(String oreName, String dustName, ItemStack ingot) {

		addDefaultOreDictionaryRecipe(oreName, dustName, ingot, null, 5, 75, 25);
	}

	public static void addDefaultOreDictionaryRecipe(String oreName, String dustName, ItemStack ingot, ItemStack ingotRelated) {

		addDefaultOreDictionaryRecipe(oreName, dustName, ingot, ingotRelated, 5, 75, 25);
	}

	public static void addOreToIngotRecipe(String oreName, ItemStack ingot2, ItemStack ingot3, ItemStack ingotSecondary, int richSlagChance, int slagOreChance) {

		List<ItemStack> registeredOres = OreDictionary.getOres(oreName);

		if (registeredOres.size() > 0) {
			ItemStack ore = registeredOres.get(0);
			addRecipe(3200, ore, blockSand, ingot2, TEItems.slagRich, richSlagChance);
			addRecipe(4000, ore, TEItems.slagRich, ingot3, TEItems.slag, slagOreChance);
			addRecipe(4000, ore, TFItems.dustPyrotheum, ingot2, TEItems.slagRich, Math.min(60, richSlagChance * 3));

			if (ingotSecondary != null) {
				addRecipe(4000, ore, TFItems.crystalCinnabar, ingot3, ingotSecondary, 100);
			} else {
				addRecipe(4000, ore, TFItems.crystalCinnabar, ingot3, TEItems.slagRich, 75);
			}
		}
	}

	public static void addDustToIngotRecipe(String dustName, ItemStack ingot2, int slagDustChance) {

        List<ItemStack> registeredOres = OreDictionary.getOres(dustName);

		if (registeredOres.size() > 0) {
			addRecipe(800, ItemHelper.cloneStack(registeredOres.get(0), 2), blockSand, ingot2, TEItems.slag, slagDustChance);
		}
	}

	public static void addAlloyRecipe(int energy, String primaryOreName, int primaryAmount, String secondaryOreName, int secondaryAmount,
			ItemStack primaryOutput) {

        List<ItemStack> primaryOreList = OreDictionary.getOres(primaryOreName);
        List<ItemStack> secondaryOreList = OreDictionary.getOres(secondaryOreName);

		if (primaryOreList.size() > 0 && secondaryOreList.size() > 0) {
			addAlloyRecipe(energy, ItemHelper.cloneStack(primaryOreList.get(0), primaryAmount),
				ItemHelper.cloneStack(secondaryOreList.get(0), secondaryAmount), primaryOutput);
		}
	}

	public static void addAlloyRecipe(int energy, ItemStack primaryInput, ItemStack secondaryInput, ItemStack primaryOutput) {

		addTERecipe(energy, primaryInput, secondaryInput, primaryOutput, null, 0);
	}

	public static void addBlastOreRecipe(String oreType) {

		String oreName = "ore" + StringHelper.titleCase(oreType);
		String dustName = "dust" + StringHelper.titleCase(oreType);
		String ingotName = "ingot" + StringHelper.titleCase(oreType);

		// if (OreDictionary.)
        List<ItemStack> registeredOre = OreDictionary.getOres(oreName);
        List<ItemStack> registeredDust = OreDictionary.getOres(dustName);
        List<ItemStack> registeredIngot = OreDictionary.getOres(ingotName);

		if (registeredIngot.isEmpty()) {
			return;
		}
		ItemStack ingot = ItemStackRegistry.findItemStack("ThermalFoundation", ingotName, 1);
		if (ingot == null) {
			ingot = registeredIngot.get(0);
			if (ingot != null && !OreDictionaryArbiter.getAllOreNames(ingot).contains(ingotName)) {
				ingot = null;
			}
		}
		if (!registeredOre.isEmpty()) {
			addRecipe(12000, ItemHelper.cloneStack(registeredOre.get(0), 1), TFItems.dustPyrotheum, ItemHelper.cloneStack(ingot, 2));
		}
		if (!registeredDust.isEmpty()) {
			addRecipe(8000, ItemHelper.cloneStack(registeredDust.get(0), 2), TFItems.dustPyrotheum, ItemHelper.cloneStack(ingot, 2));
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

	public static boolean addRecipe(int energy, ItemStack primaryInput, ItemStack secondaryInput, ItemStack primaryOutput, ItemStack secondaryOutput,
			boolean overwrite) {

		return addRecipe(energy, primaryInput, secondaryInput, primaryOutput, secondaryOutput, 100, overwrite);
	}

	public static boolean addRecipe(int energy, ItemStack primaryInput, ItemStack secondaryInput, ItemStack primaryOutput, ItemStack secondaryOutput,
			int secondaryChance) {

		return addRecipe(energy, primaryInput, secondaryInput, primaryOutput, secondaryOutput, secondaryChance, false);
	}

	public static boolean addRecycleRecipe(int energy, ItemStack input, ItemStack output, int outputSize) {

		return addRecipe(energy, blockSand, input, ItemHelper.cloneStack(output, outputSize), TEItems.slag, outputSize * 5 + 5, false);
	}

	/* RECIPE CLASS */
	public static class RecipeSmelter implements ISmelterRecipe {

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

			if (primaryInput.stackSize <= 0) {
				primaryInput.stackSize = 1;
			}
			if (secondaryInput.stackSize <= 0) {
				secondaryInput.stackSize = 1;
			}
			if (primaryOutput.stackSize <= 0) {
				primaryOutput.stackSize = 1;
			}
			if (secondaryOutput != null && secondaryOutput.stackSize <= 0) {
				secondaryOutput.stackSize = 1;
			}
		}

		@Override
		public ItemStack getPrimaryInput() {

			return primaryInput.copy();
		}

		@Override
		public ItemStack getSecondaryInput() {

			return secondaryInput.copy();
		}

		@Override
		public ItemStack getPrimaryOutput() {

			return primaryOutput.copy();
		}

		@Override
		public ItemStack getSecondaryOutput() {

			if (secondaryOutput == null) {
				return null;
			}
			return secondaryOutput.copy();
		}

		@Override
		public int getSecondaryOutputChance() {

			return secondaryChance;
		}

		@Override
		public int getEnergy() {

			return energy;
		}
	}

	/* ITEMSTACK CLASS */
	public static class ComparableItemStackSmelter extends ComparableItemStack {

		static final String BLOCK = "block";
		static final String ORE = "ore";
		static final String DUST = "dust";
		static final String INGOT = "ingot";
		static final String NUGGET = "nugget";
		static final String SAND = "sand";

		public static boolean safeOreType(String oreName) {

			return oreName.startsWith(BLOCK) || oreName.startsWith(ORE) || oreName.startsWith(DUST) || oreName.startsWith(INGOT) || oreName.startsWith(NUGGET)
					|| oreName.equals("sand");
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

		public static int getOreID(String oreName) {

			if (!safeOreType(oreName)) {
				return -1;
			}
			return ItemHelper.oreProxy.getOreID(oreName);
		}

		public ComparableItemStackSmelter(ItemStack stack) {

			super(stack);
			oreID = getOreID(stack);
		}

		public ComparableItemStackSmelter(Item item, int damage, int stackSize) {

			super(item, damage, stackSize);
			this.oreID = getOreID(this.toItemStack());
		}

		@Override
		public ComparableItemStackSmelter set(ItemStack stack) {

			super.set(stack);
			oreID = getOreID(stack);

			return this;
		}
	}

}
