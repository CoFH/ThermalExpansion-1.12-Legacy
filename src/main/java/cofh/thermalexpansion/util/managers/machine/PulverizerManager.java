package cofh.thermalexpansion.util.managers.machine;

import cofh.core.inventory.ComparableItemStack;
import cofh.core.util.helpers.ColorHelper;
import cofh.core.util.helpers.ItemHelper;
import cofh.core.util.helpers.StringHelper;
import cofh.core.util.oredict.OreDictionaryArbiter;
import cofh.thermalfoundation.block.BlockOreFluid;
import cofh.thermalfoundation.init.TFEquipment;
import cofh.thermalfoundation.item.ItemMaterial;
import gnu.trove.map.hash.THashMap;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class PulverizerManager {

	private static Map<ComparableItemStackPulverizer, PulverizerRecipe> recipeMap = new THashMap<>();

	static final int ORE_MULTIPLIER = 2;
	public static final int DEFAULT_ENERGY = 4000;

	public static PulverizerRecipe getRecipe(ItemStack input) {

		if (input.isEmpty()) {
			return null;
		}
		ComparableItemStackPulverizer query = new ComparableItemStackPulverizer(input);

		PulverizerRecipe recipe = recipeMap.get(query);

		if (recipe == null) {
			query.metadata = OreDictionary.WILDCARD_VALUE;
			recipe = recipeMap.get(query);
		}
		return recipe;
	}

	public static boolean recipeExists(ItemStack input) {

		return getRecipe(input) != null;
	}

	public static PulverizerRecipe[] getRecipeList() {

		return recipeMap.values().toArray(new PulverizerRecipe[recipeMap.size()]);
	}

	public static void initialize() {

		/* SPECIAL */
		{
			int energy = DEFAULT_ENERGY;

			addRecipe(energy, new ItemStack(Blocks.STONE), new ItemStack(Blocks.GRAVEL), new ItemStack(Blocks.SAND), 15);
			addRecipe(energy, new ItemStack(Blocks.COBBLESTONE), new ItemStack(Blocks.SAND), new ItemStack(Blocks.GRAVEL), 15);
			addRecipe(energy, new ItemStack(Blocks.GRAVEL), new ItemStack(Items.FLINT), new ItemStack(Blocks.SAND), 15);
			addRecipe(energy, new ItemStack(Blocks.NETHERRACK), new ItemStack(Blocks.GRAVEL), ItemMaterial.dustSulfur, 15);
			addRecipe(energy, new ItemStack(Blocks.SANDSTONE), ItemHelper.cloneStack(Blocks.SAND, 2), ItemMaterial.dustNiter, 25);
			addRecipe(energy, new ItemStack(Blocks.RED_SANDSTONE), new ItemStack(Blocks.SAND, 2, 1), ItemMaterial.dustNiter, 25);
			addRecipe(energy, new ItemStack(Blocks.GLOWSTONE), new ItemStack(Items.GLOWSTONE_DUST, 4));
			addRecipe(energy, new ItemStack(Blocks.MAGMA), new ItemStack(Items.MAGMA_CREAM, 4));

			addRecipe(energy / 4, new ItemStack(Blocks.STONEBRICK), new ItemStack(Blocks.STONEBRICK, 1, 2));
			addRecipe(energy * 3 / 2, new ItemStack(Blocks.OBSIDIAN), ItemHelper.cloneStack(ItemMaterial.dustObsidian, 4));

			energy = DEFAULT_ENERGY / 2;

			addRecipe(energy, new ItemStack(Items.COAL, 1, 0), ItemMaterial.dustCoal, ItemMaterial.dustSulfur, 15);
			addRecipe(energy, new ItemStack(Items.COAL, 1, 1), ItemMaterial.dustCharcoal);

			addRecipe(energy, new ItemStack(Items.BLAZE_ROD), new ItemStack(Items.BLAZE_POWDER, 4), ItemMaterial.dustSulfur, 50);
			addRecipe(energy, ItemMaterial.rodBlizz, ItemHelper.cloneStack(ItemMaterial.dustBlizz, 4), new ItemStack(Items.SNOWBALL), 50);
			addRecipe(energy, ItemMaterial.rodBlitz, ItemHelper.cloneStack(ItemMaterial.dustBlitz, 4), ItemMaterial.dustNiter, 50);
			addRecipe(energy, ItemMaterial.rodBasalz, ItemHelper.cloneStack(ItemMaterial.dustBasalz, 4), ItemMaterial.dustObsidian, 50);
		}

		/* PLANTS */
		{
			int energy = DEFAULT_ENERGY / 2;

			addRecipe(energy, new ItemStack(Blocks.LOG), ItemHelper.cloneStack(ItemMaterial.dustWood, 8));
			addRecipe(energy / 4, new ItemStack(Blocks.PLANKS), ItemHelper.cloneStack(ItemMaterial.dustWood, 2));

			addRecipe(energy, new ItemStack(Blocks.YELLOW_FLOWER), new ItemStack(Items.DYE, 4, 11));
			addRecipe(energy, new ItemStack(Blocks.RED_FLOWER, 1, 0), new ItemStack(Items.DYE, 4, 1));
			addRecipe(energy, new ItemStack(Blocks.RED_FLOWER, 1, 1), new ItemStack(Items.DYE, 4, 12));
			addRecipe(energy, new ItemStack(Blocks.RED_FLOWER, 1, 2), new ItemStack(Items.DYE, 4, 13));
			addRecipe(energy, new ItemStack(Blocks.RED_FLOWER, 1, 3), new ItemStack(Items.DYE, 4, 7));
			addRecipe(energy, new ItemStack(Blocks.RED_FLOWER, 1, 4), new ItemStack(Items.DYE, 4, 1));
			addRecipe(energy, new ItemStack(Blocks.RED_FLOWER, 1, 5), new ItemStack(Items.DYE, 4, 14));
			addRecipe(energy, new ItemStack(Blocks.RED_FLOWER, 1, 6), new ItemStack(Items.DYE, 4, 7));
			addRecipe(energy, new ItemStack(Blocks.RED_FLOWER, 1, 7), new ItemStack(Items.DYE, 4, 9));
			addRecipe(energy, new ItemStack(Blocks.RED_FLOWER, 1, 8), new ItemStack(Items.DYE, 4, 7));

			addRecipe(energy, new ItemStack(Blocks.DOUBLE_PLANT, 1, 0), new ItemStack(Items.DYE, 8, 11));
			addRecipe(energy, new ItemStack(Blocks.DOUBLE_PLANT, 1, 1), new ItemStack(Items.DYE, 8, 13));
			addRecipe(energy, new ItemStack(Blocks.DOUBLE_PLANT, 1, 4), new ItemStack(Items.DYE, 8, 1));
			addRecipe(energy, new ItemStack(Blocks.DOUBLE_PLANT, 1, 5), new ItemStack(Items.DYE, 8, 9));

			addRecipe(energy, new ItemStack(Items.REEDS), new ItemStack(Items.SUGAR, 2));
		}

		/* DYES */
		{
			int energy = DEFAULT_ENERGY * 3 / 4;

			int[] dyeChance = new int[ColorHelper.WOOL_COLOR_CONFIG.length];
			for (int i = 0; i < ColorHelper.WOOL_COLOR_CONFIG.length; i++) {
				dyeChance[i] = 5;
			}
			dyeChance[EnumDyeColor.WHITE.getMetadata()] = 0;
			dyeChance[EnumDyeColor.BROWN.getMetadata()] = 0;
			dyeChance[EnumDyeColor.BLUE.getMetadata()] = 0;
			dyeChance[EnumDyeColor.GREEN.getMetadata()] = 0;
			dyeChance[EnumDyeColor.BLACK.getMetadata()] = 0;

			ItemStack stringStack = ItemHelper.cloneStack(Items.STRING, 4);

			for (int i = 0; i < ColorHelper.WOOL_COLOR_CONFIG.length; i++) {
				if (dyeChance[i] > 0) {
					addRecipe(energy, new ItemStack(Blocks.WOOL, 1, i), stringStack, new ItemStack(Items.DYE, 1, 15 - i), dyeChance[i]);
				} else {
					addRecipe(energy, new ItemStack(Blocks.WOOL, 1, i), stringStack);
				}
			}
			addRecipe(energy, new ItemStack(Items.BONE), new ItemStack(Items.DYE, 6, 15));
		}

		/* ORES */
		{
			int energy = DEFAULT_ENERGY;

			addRecipe(energy, new ItemStack(Blocks.COAL_ORE), new ItemStack(Items.COAL, 3, 0), ItemMaterial.dustCoal, 25);
			addRecipe(energy, new ItemStack(Blocks.DIAMOND_ORE), new ItemStack(Items.DIAMOND, 2, 0));
			addRecipe(energy, new ItemStack(Blocks.EMERALD_ORE), new ItemStack(Items.EMERALD, 2, 0));
			addRecipe(energy, new ItemStack(Blocks.LAPIS_ORE), new ItemStack(Items.DYE, 12, 4), ItemMaterial.dustSulfur, 20);
			addRecipe(energy, new ItemStack(Blocks.REDSTONE_ORE), new ItemStack(Items.REDSTONE, 6), ItemMaterial.crystalCinnabar, 25);
			addRecipe(energy, new ItemStack(Blocks.QUARTZ_ORE), new ItemStack(Items.QUARTZ, 3), ItemMaterial.dustSulfur, 15);

			addOreToDustRecipe(energy, "oreIron", ItemMaterial.dustIron, ItemMaterial.dustNickel, 10);
			addOreToDustRecipe(energy, "oreGold", ItemMaterial.dustGold, ItemMaterial.crystalCinnabar, 5);

			addOreToDustRecipe(energy, "oreCopper", ItemMaterial.dustCopper, ItemMaterial.dustGold, 10);
			addOreToDustRecipe(energy, "oreTin", ItemMaterial.dustTin, ItemMaterial.dustIron, 10);
			addOreToDustRecipe(energy, "oreSilver", ItemMaterial.dustSilver, ItemMaterial.dustLead, 10);
			addOreToDustRecipe(energy, "oreAluminum", ItemMaterial.dustAluminum, ItemMaterial.dustIron, 10);
			addOreToDustRecipe(energy, "oreLead", ItemMaterial.dustLead, ItemMaterial.dustSilver, 10);
			addOreToDustRecipe(energy, "oreNickel", ItemMaterial.dustNickel, ItemMaterial.dustPlatinum, 10);
			addOreToDustRecipe(energy, "orePlatinum", ItemMaterial.dustPlatinum, ItemMaterial.dustIridium, 5);
			addOreToDustRecipe(energy, "oreIridium", ItemMaterial.dustIridium, ItemMaterial.dustPlatinum, 10);
			addOreToDustRecipe(energy, "oreMithril", ItemMaterial.dustMithril, ItemMaterial.dustGold, 10);

			addRecipe(energy, BlockOreFluid.oreFluidCrudeOilSand, ItemHelper.cloneStack(ItemMaterial.crystalCrudeOil, 3), ItemMaterial.globTar, 50);
			addRecipe(energy, BlockOreFluid.oreFluidCrudeOilGravel, ItemHelper.cloneStack(ItemMaterial.crystalCrudeOil, 3), new ItemStack(Items.FLINT), 50);
			addRecipe(energy, BlockOreFluid.oreFluidRedstone, ItemHelper.cloneStack(ItemMaterial.crystalRedstone, 3), ItemMaterial.crystalCinnabar, 50);
			addRecipe(energy, BlockOreFluid.oreFluidGlowstone, ItemHelper.cloneStack(ItemMaterial.crystalGlowstone, 3), ItemMaterial.dustSulfur, 30);
			addRecipe(energy, BlockOreFluid.oreFluidEnder, ItemHelper.cloneStack(ItemMaterial.crystalEnder, 3));
		}

		/* DUSTS */
		{
			int energy = DEFAULT_ENERGY / 2;

			addIngotToDustRecipe(energy, "ingotIron", ItemMaterial.dustIron);
			addIngotToDustRecipe(energy, "ingotGold", ItemMaterial.dustGold);
			addIngotToDustRecipe(energy, "ingotCopper", ItemMaterial.dustCopper);
			addIngotToDustRecipe(energy, "ingotTin", ItemMaterial.dustTin);
			addIngotToDustRecipe(energy, "ingotSilver", ItemMaterial.dustSilver);
			addIngotToDustRecipe(energy, "ingotLead", ItemMaterial.dustLead);
			addIngotToDustRecipe(energy, "ingotAluminum", ItemMaterial.dustAluminum);
			addIngotToDustRecipe(energy, "ingotNickel", ItemMaterial.dustNickel);
			addIngotToDustRecipe(energy, "ingotPlatinum", ItemMaterial.dustPlatinum);
			addIngotToDustRecipe(energy, "ingotIridium", ItemMaterial.dustIridium);
			addIngotToDustRecipe(energy, "ingotMithril", ItemMaterial.dustMithril);

			addIngotToDustRecipe(energy, "ingotSteel", ItemMaterial.dustSteel);
			addIngotToDustRecipe(energy, "ingotElectrum", ItemMaterial.dustElectrum);
			addIngotToDustRecipe(energy, "ingotInvar", ItemMaterial.dustInvar);
			addIngotToDustRecipe(energy, "ingotBronze", ItemMaterial.dustBronze);
			addIngotToDustRecipe(energy, "ingotSignalum", ItemMaterial.dustSignalum);
			addIngotToDustRecipe(energy, "ingotLumium", ItemMaterial.dustLumium);
			addIngotToDustRecipe(energy, "ingotEnderium", ItemMaterial.dustEnderium);
		}

		/* RECYCLING */
		{
			int energy = DEFAULT_ENERGY * 3 / 4;

			addRecipe(energy, new ItemStack(Blocks.GLASS), new ItemStack(Blocks.SAND));

			for (int i = 0; i < 15; i++) {
				addRecipe(energy, new ItemStack(Blocks.STAINED_GLASS, 1, i), new ItemStack(Blocks.SAND));
			}
			addRecipe(energy, new ItemStack(Blocks.REDSTONE_LAMP), new ItemStack(Items.GLOWSTONE_DUST, 4), new ItemStack(Items.REDSTONE, 4));
			addRecipe(energy, new ItemStack(Blocks.BRICK_BLOCK), new ItemStack(Items.BRICK, 4));
			addRecipe(energy, new ItemStack(Blocks.NETHER_BRICK), new ItemStack(Items.NETHERBRICK, 4));

			for (int i = 0; i < 3; i++) {
				addRecipe(energy, new ItemStack(Blocks.QUARTZ_BLOCK, 1, i), new ItemStack(Items.QUARTZ, 4));
			}
			addRecipe(energy, new ItemStack(Blocks.BRICK_STAIRS), new ItemStack(Items.BRICK, 6));
			addRecipe(energy, new ItemStack(Blocks.NETHER_BRICK_STAIRS), new ItemStack(Items.NETHERBRICK, 6));
			addRecipe(energy, new ItemStack(Blocks.QUARTZ_STAIRS), new ItemStack(Items.QUARTZ, 6));
			addRecipe(energy / 2, new ItemStack(Blocks.STONE_SLAB, 1, 4), new ItemStack(Items.BRICK, 2));
			addRecipe(energy / 2, new ItemStack(Blocks.STONE_SLAB, 1, 6), new ItemStack(Items.NETHERBRICK, 2));
			addRecipe(energy / 2, new ItemStack(Blocks.STONE_SLAB, 1, 7), new ItemStack(Items.QUARTZ, 2));

			for (int i = 0; i < 3; i++) {
				addRecipe(energy, new ItemStack(Blocks.SANDSTONE, 1, i), new ItemStack(Blocks.SAND, 2), ItemMaterial.dustNiter, 50);
			}
			addRecipe(energy, new ItemStack(Blocks.SANDSTONE_STAIRS), new ItemStack(Blocks.SAND, 2), ItemMaterial.dustNiter, 75);
			addRecipe(energy, new ItemStack(Blocks.STONE_SLAB, 1, 1), new ItemStack(Blocks.SAND, 1), ItemMaterial.dustNiter, 25);

			addRecipe(energy / 2, new ItemStack(Items.FLOWER_POT), new ItemStack(Items.BRICK, 3));
			addRecipe(energy / 2, new ItemStack(Items.GLASS_BOTTLE), new ItemStack(Blocks.SAND, 1));

			energy = DEFAULT_ENERGY * 3 / 2;

			addRecipe(energy, new ItemStack(Items.DIAMOND_SWORD), new ItemStack(Items.DIAMOND, 2));
			addRecipe(energy, new ItemStack(Items.DIAMOND_PICKAXE), new ItemStack(Items.DIAMOND, 3));
			addRecipe(energy, new ItemStack(Items.DIAMOND_AXE), new ItemStack(Items.DIAMOND, 3));
			addRecipe(energy, new ItemStack(Items.DIAMOND_SHOVEL), new ItemStack(Items.DIAMOND, 1));
			addRecipe(energy, new ItemStack(Items.DIAMOND_HOE), new ItemStack(Items.DIAMOND, 2));

			addRecipe(energy, new ItemStack(Items.DIAMOND_HELMET), new ItemStack(Items.DIAMOND, 5));
			addRecipe(energy, new ItemStack(Items.DIAMOND_CHESTPLATE), new ItemStack(Items.DIAMOND, 8));
			addRecipe(energy, new ItemStack(Items.DIAMOND_LEGGINGS), new ItemStack(Items.DIAMOND, 7));
			addRecipe(energy, new ItemStack(Items.DIAMOND_BOOTS), new ItemStack(Items.DIAMOND, 4));

			addRecipe(energy, new ItemStack(Items.DIAMOND_HORSE_ARMOR), new ItemStack(Items.DIAMOND, 8));

			addRecipe(energy, TFEquipment.ToolSetVanilla.DIAMOND.toolBow, new ItemStack(Items.DIAMOND, 2));
			addRecipe(energy, TFEquipment.ToolSetVanilla.DIAMOND.toolFishingRod, new ItemStack(Items.DIAMOND, 2));
			addRecipe(energy, TFEquipment.ToolSetVanilla.DIAMOND.toolShears, new ItemStack(Items.DIAMOND, 2));
			addRecipe(energy, TFEquipment.ToolSetVanilla.DIAMOND.toolSickle, new ItemStack(Items.DIAMOND, 3));
			addRecipe(energy, TFEquipment.ToolSetVanilla.DIAMOND.toolHammer, new ItemStack(Items.DIAMOND, 5));
			addRecipe(energy, TFEquipment.ToolSetVanilla.DIAMOND.toolShield, new ItemStack(Items.DIAMOND, 6));
		}

		/* LOAD RECIPES */
		loadRecipes();
	}

	public static void loadRecipes() {

		/* APPLIED ENERGISTICS 2 */
		{
			if (ItemHelper.oreNameExists("oreCertusQuartz") && ItemHelper.oreNameExists("dustCertusQuartz") && ItemHelper.oreNameExists("crystalCertusQuartz")) {
				addRecipe(DEFAULT_ENERGY, OreDictionary.getOres("oreCertusQuartz", false).get(0), ItemHelper.cloneStack(OreDictionary.getOres("crystalCertusQuartz", false).get(0), 2), OreDictionary.getOres("dustCertusQuartz", false).get(0), 10);
				addRecipe(DEFAULT_ENERGY, OreDictionary.getOres("crystalCertusQuartz", false).get(0), OreDictionary.getOres("dustCertusQuartz", false).get(0));
			}
			if (ItemHelper.oreNameExists("dustFluix") && ItemHelper.oreNameExists("crystalFluix")) {
				addRecipe(DEFAULT_ENERGY, OreDictionary.getOres("crystalFluix", false).get(0), OreDictionary.getOres("dustFluix", false).get(0));
			}
			if (ItemHelper.oreNameExists("dustNetherQuartz")) {
				addRecipe(DEFAULT_ENERGY, new ItemStack(Items.QUARTZ, 1), ItemHelper.cloneStack(OreDictionary.getOres("dustNetherQuartz", false).get(0), 1));
			}
		}

		/* FORESTRY */
		{
			if (ItemHelper.oreNameExists("oreApatite") && ItemHelper.oreNameExists("gemApatite")) {
				addRecipe(DEFAULT_ENERGY, OreDictionary.getOres("oreApatite", false).get(0), ItemHelper.cloneStack(OreDictionary.getOres("gemApatite", false).get(0), 12), ItemMaterial.dustSulfur, 10);
			}
		}

		/* SPECIFIC INTERACTIONS */
		{
			if (ItemHelper.oreNameExists("oreNiter")) {
				addRecipe(DEFAULT_ENERGY, OreDictionary.getOres("oreNiter", false).get(0), ItemHelper.cloneStack(ItemMaterial.dustNiter, 4));
			}
			if (ItemHelper.oreNameExists("oreSaltpeter")) {
				addRecipe(DEFAULT_ENERGY, OreDictionary.getOres("oreSaltpeter", false).get(0), ItemHelper.cloneStack(ItemMaterial.dustNiter, 4));
			}
			if (ItemHelper.oreNameExists("oreSulfur")) {
				addRecipe(DEFAULT_ENERGY, OreDictionary.getOres("oreSulfur", false).get(0), ItemHelper.cloneStack(ItemMaterial.dustSulfur, 6));
			}
		}

		/* GENERAL SCAN */
		{
			String[] oreNames = OreDictionary.getOreNames();
			String oreType;

			for (String oreName : oreNames) {
				if (oreName.startsWith("ore")) {
					oreType = oreName.substring(3, oreName.length());
					addDefaultOreDictionaryRecipe(oreType);
				} else if (oreName.startsWith("dust")) {
					oreType = oreName.substring(4, oreName.length());
					addDefaultOreDictionaryRecipe(oreType);
				} else if (oreName.startsWith("gem")) {
					oreType = oreName.substring(3, oreName.length());
					addDefaultOreDictionaryRecipeGem(oreType);
				}
			}
		}
	}

	public static void refresh() {

		Map<ComparableItemStackPulverizer, PulverizerRecipe> tempMap = new THashMap<>(recipeMap.size());
		PulverizerRecipe tempRecipe;

		for (Entry<ComparableItemStackPulverizer, PulverizerRecipe> entry : recipeMap.entrySet()) {
			tempRecipe = entry.getValue();
			tempMap.put(new ComparableItemStackPulverizer(tempRecipe.input), tempRecipe);
		}
		recipeMap.clear();
		recipeMap = tempMap;
	}

	/* ADD RECIPES */
	public static PulverizerRecipe addRecipe(int energy, ItemStack input, ItemStack primaryOutput, ItemStack secondaryOutput, int secondaryChance) {

		if (input.isEmpty() || primaryOutput.isEmpty() || energy <= 0 || recipeExists(input)) {
			return null;
		}
		PulverizerRecipe recipe = new PulverizerRecipe(input, primaryOutput, secondaryOutput, secondaryChance, energy);
		recipeMap.put(new ComparableItemStackPulverizer(input), recipe);
		return recipe;
	}

	public static PulverizerRecipe addRecipe(int energy, ItemStack input, ItemStack primaryOutput, ItemStack secondaryOutput) {

		return addRecipe(energy, input, primaryOutput, secondaryOutput, 100);
	}

	public static PulverizerRecipe addRecipe(int energy, ItemStack input, ItemStack primaryOutput) {

		return addRecipe(energy, input, primaryOutput, ItemStack.EMPTY, 0);
	}

	/* REMOVE RECIPES */
	public static PulverizerRecipe removeRecipe(ItemStack input) {

		return recipeMap.remove(new ComparableItemStackPulverizer(input));
	}

	/* HELPERS */
	private static void addDefaultOreDictionaryRecipe(String oreType) {

		addDefaultOreDictionaryRecipe(oreType, "");
	}

	private static void addDefaultOreDictionaryRecipe(String oreType, String relatedType) {

		if (oreType.isEmpty()) {
			return;
		}
		String oreName = "ore" + StringHelper.titleCase(oreType);
		String dustName = "dust" + StringHelper.titleCase(oreType);
		String ingotName = "ingot" + StringHelper.titleCase(oreType);
		String relatedName = null;

		List<ItemStack> registeredOre = OreDictionary.getOres(oreName, false);
		List<ItemStack> registeredDust = OreDictionary.getOres(dustName, false);
		List<ItemStack> registeredIngot = OreDictionary.getOres(ingotName, false);
		List<ItemStack> registeredRelated = new ArrayList<>();

		String clusterName = "cluster" + StringHelper.titleCase(oreType);
		List<ItemStack> registeredCluster = OreDictionary.getOres(clusterName, false);

		if (!relatedType.isEmpty()) {
			relatedName = "dust" + StringHelper.titleCase(relatedType);
			registeredRelated = OreDictionary.getOres(relatedName, false);
		}
		if (registeredDust.isEmpty()) {
			return;
		}
		ItemStack dust = registeredDust.get(0);

		if (registeredIngot.isEmpty()) {
			ingotName = null;
		}
		if (registeredOre.isEmpty()) {
			oreName = null;
		}
		if (registeredCluster.isEmpty()) {
			clusterName = null;
		}
		ItemStack related = ItemStack.EMPTY;
		if (related.isEmpty() && !registeredRelated.isEmpty()) {
			related = registeredRelated.get(0);
		}
		addOreToDustRecipe(4000, oreName, ItemHelper.cloneStack(dust, ORE_MULTIPLIER), related, related.isEmpty() ? 0 : 5);
		addOreToDustRecipe(4800, clusterName, ItemHelper.cloneStack(dust, ORE_MULTIPLIER), related, related.isEmpty() ? 0 : 5);
		addIngotToDustRecipe(2400, ingotName, ItemHelper.cloneStack(dust, 1));
	}

	private static void addDefaultOreDictionaryRecipeGem(String oreType) {

		addDefaultOreDictionaryRecipeGem(oreType, "");
	}

	private static void addDefaultOreDictionaryRecipeGem(String oreType, String relatedType) {

		if (oreType.isEmpty()) {
			return;
		}
		String oreName = "ore" + StringHelper.titleCase(oreType);
		String dustName = "dust" + StringHelper.titleCase(oreType);
		String gemName = "gem" + StringHelper.titleCase(oreType);
		String relatedName = null;

		List<ItemStack> registeredOre = OreDictionary.getOres(oreName, false);
		List<ItemStack> registeredDust = OreDictionary.getOres(dustName, false);
		List<ItemStack> registeredGem = OreDictionary.getOres(gemName, false);
		List<ItemStack> registeredRelated = new ArrayList<>();

		String clusterName = "cluster" + StringHelper.titleCase(oreType);
		List<ItemStack> registeredCluster = OreDictionary.getOres(clusterName, false);

		if (!relatedType.isEmpty()) {
			relatedName = "dust" + StringHelper.titleCase(relatedType);
			registeredRelated = OreDictionary.getOres(relatedName, false);
		}
		if (registeredGem.isEmpty()) {
			return;
		}
		ItemStack gem = registeredGem.get(0);
		ItemStack dust = ItemStack.EMPTY;

		if (!registeredDust.isEmpty()) {
			dust = registeredDust.get(0);
		}
		if (registeredOre.isEmpty()) {
			oreName = null;
		}
		if (registeredCluster.isEmpty()) {
			clusterName = null;
		}
		ItemStack related = ItemStack.EMPTY;
		if (related.isEmpty() && !registeredRelated.isEmpty()) {
			related = registeredRelated.get(0);
		}
		addOreToDustRecipe(DEFAULT_ENERGY, oreName, ItemHelper.cloneStack(gem, ORE_MULTIPLIER), related, related.isEmpty() ? 0 : 5);
		addOreToDustRecipe(DEFAULT_ENERGY * 5 / 4, clusterName, ItemHelper.cloneStack(gem, ORE_MULTIPLIER), related, related.isEmpty() ? 0 : 5);
		addIngotToDustRecipe(DEFAULT_ENERGY * 3 / 4, gemName, ItemHelper.cloneStack(dust, 1));
	}

	private static void addOreToDustRecipe(int energy, String oreName, ItemStack primaryOutput, ItemStack secondaryOutput, int secondaryChance) {

		if (primaryOutput.isEmpty() || oreName == null) {
			return;
		}
		List<ItemStack> registeredOres = OreDictionary.getOres(oreName, false);

		if (!registeredOres.isEmpty() && !recipeExists(OreDictionary.getOres(oreName, false).get(0))) {
			addRecipe(energy, ItemHelper.cloneStack(registeredOres.get(0), 1), ItemHelper.cloneStack(primaryOutput, ORE_MULTIPLIER), secondaryOutput, secondaryChance);
		}
	}

	private static void addIngotToDustRecipe(int energy, String ingotName, ItemStack dust) {

		if (dust.isEmpty() || ingotName == null) {
			return;
		}
		List<ItemStack> registeredOres = OreDictionary.getOres(ingotName, false);

		if (!registeredOres.isEmpty() && !recipeExists(OreDictionary.getOres(ingotName, false).get(0))) {
			addRecipe(energy, ItemHelper.cloneStack(registeredOres.get(0), 1), dust, ItemStack.EMPTY, 0);
		}
	}

	/* RECIPE CLASS */
	public static class PulverizerRecipe {

		final ItemStack input;
		final ItemStack primaryOutput;
		final ItemStack secondaryOutput;
		final int secondaryChance;
		final int energy;

		PulverizerRecipe(ItemStack input, ItemStack primaryOutput, ItemStack secondaryOutput, int secondaryChance, int energy) {

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
	public static class ComparableItemStackPulverizer extends ComparableItemStack {

		public static final String ORE = "ore";
		public static final String INGOT = "ingot";
		public static final String NUGGET = "nugget";
		public static final String PLANK = "plank";
		public static final String LOG = "log";
		public static final String SAND = "sand";

		public static boolean safeOreType(String oreName) {

			return oreName.startsWith(ORE) || oreName.startsWith(INGOT) || oreName.startsWith(NUGGET) || oreName.startsWith(PLANK) || oreName.startsWith(LOG) || oreName.equals(SAND);
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

		public ComparableItemStackPulverizer(ItemStack stack) {

			super(stack);
			oreID = getOreID(stack);
		}

		@Override
		public ComparableItemStackPulverizer set(ItemStack stack) {

			super.set(stack);
			oreID = getOreID(stack);

			return this;
		}
	}

}
