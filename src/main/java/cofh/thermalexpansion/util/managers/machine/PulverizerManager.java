package cofh.thermalexpansion.util.managers.machine;

import cofh.core.inventory.ComparableItemStack;
import cofh.core.inventory.ComparableItemStackValidated;
import cofh.core.inventory.OreValidator;
import cofh.core.util.helpers.ColorHelper;
import cofh.core.util.helpers.ItemHelper;
import cofh.core.util.helpers.StringHelper;
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

	private static Map<ComparableItemStackValidated, PulverizerRecipe> recipeMap = new THashMap<>();
	private static OreValidator oreValidator = new OreValidator();

	static {
		oreValidator.addPrefix(ComparableItemStack.ORE);
		oreValidator.addPrefix(ComparableItemStack.INGOT);
		oreValidator.addPrefix(ComparableItemStack.NUGGET);
		oreValidator.addPrefix("log");
		oreValidator.addPrefix("plank");
		oreValidator.addExact("sand");
	}

	static final int ORE_MULTIPLIER = 2;
	public static final int DEFAULT_ENERGY = 4000;

	public static PulverizerRecipe getRecipe(ItemStack input) {

		if (input.isEmpty()) {
			return null;
		}
		ComparableItemStackValidated query = convertInput(input);

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

			//			addRecipe(energy, new ItemStack(Blocks.STONE), new ItemStack(Blocks.GRAVEL), new ItemStack(Blocks.SAND), 15);
			//			addRecipe(energy, new ItemStack(Blocks.COBBLESTONE), new ItemStack(Blocks.SAND), new ItemStack(Blocks.GRAVEL), 15);
			//			addRecipe(energy, new ItemStack(Blocks.GRAVEL), new ItemStack(Items.FLINT), new ItemStack(Blocks.SAND), 15);
			//			addRecipe(energy, new ItemStack(Blocks.NETHERRACK), new ItemStack(Blocks.GRAVEL), ItemMaterial.dustSulfur, 15);
			//			addRecipe(energy, new ItemStack(Blocks.GLOWSTONE), new ItemStack(Items.GLOWSTONE_DUST, 4));
			//			addRecipe(energy, new ItemStack(Blocks.MAGMA), new ItemStack(Items.MAGMA_CREAM, 4));
			//
			//			addRecipe(energy / 4, new ItemStack(Blocks.STONEBRICK), new ItemStack(Blocks.STONEBRICK, 1, 2));
			//			addRecipe(energy * 3 / 2, new ItemStack(Blocks.OBSIDIAN), ItemHelper.cloneStack(ItemMaterial.dustObsidian, 4));

			for (int i = 0; i < ColorHelper.WOOL_COLOR_CONFIG.length; i++) {
				addRecipe(energy, new ItemStack(Blocks.CONCRETE, 1, i), new ItemStack(Blocks.CONCRETE_POWDER, 1, i));
			}

			energy = DEFAULT_ENERGY / 2;

			//			addRecipe(energy, new ItemStack(Items.COAL, 1, 0), ItemMaterial.dustCoal, ItemMaterial.dustSulfur, 15);
			//			addRecipe(energy, new ItemStack(Items.COAL, 1, 1), ItemMaterial.dustCharcoal);

			//			addRecipe(energy, new ItemStack(Items.BLAZE_ROD), new ItemStack(Items.BLAZE_POWDER, 4), ItemMaterial.dustSulfur, 50);
			//			addRecipe(energy, ItemMaterial.rodBlizz, ItemHelper.cloneStack(ItemMaterial.dustBlizz, 4), new ItemStack(Items.SNOWBALL), 50);
			//			addRecipe(energy, ItemMaterial.rodBlitz, ItemHelper.cloneStack(ItemMaterial.dustBlitz, 4), ItemMaterial.dustNiter, 50);
			//			addRecipe(energy, ItemMaterial.rodBasalz, ItemHelper.cloneStack(ItemMaterial.dustBasalz, 4), ItemMaterial.dustObsidian, 50);
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

			addRecipe(energy, new ItemStack(Blocks.DOUBLE_PLANT, 1, 0), new ItemStack(Items.DYE, 4, 11));
			addRecipe(energy, new ItemStack(Blocks.DOUBLE_PLANT, 1, 1), new ItemStack(Items.DYE, 4, 13));
			addRecipe(energy, new ItemStack(Blocks.DOUBLE_PLANT, 1, 4), new ItemStack(Items.DYE, 4, 1));
			addRecipe(energy, new ItemStack(Blocks.DOUBLE_PLANT, 1, 5), new ItemStack(Items.DYE, 4, 9));

			addRecipe(energy, new ItemStack(Items.REEDS), new ItemStack(Items.SUGAR, 2));
		}

		/* DYES */
		{
			int energy = DEFAULT_ENERGY * 3 / 4;

			int[] dyeChance = new int[ColorHelper.WOOL_COLOR_CONFIG.length];
			for (int i = 0; i < ColorHelper.WOOL_COLOR_CONFIG.length; i++) {
				dyeChance[i] = 15;
			}
			dyeChance[EnumDyeColor.WHITE.getMetadata()] = 0;
			dyeChance[EnumDyeColor.BROWN.getMetadata()] = 0;
			dyeChance[EnumDyeColor.BLUE.getMetadata()] = 0;
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
			addRecipe(energy, new ItemStack(Blocks.LAPIS_ORE), new ItemStack(Items.DYE, 8, 4), ItemMaterial.dustSulfur, 20);
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
			addRecipe(energy, new ItemStack(Blocks.REDSTONE_LAMP), new ItemStack(Items.REDSTONE, 4), new ItemStack(Items.GLOWSTONE_DUST, 2));
			addRecipe(energy, new ItemStack(Blocks.BRICK_BLOCK), new ItemStack(Items.BRICK, 4));
			addRecipe(energy, new ItemStack(Blocks.NETHER_BRICK), new ItemStack(Items.NETHERBRICK, 4));

			for (int i = 0; i < 3; i++) {
				addRecipe(energy, new ItemStack(Blocks.QUARTZ_BLOCK, 1, i), new ItemStack(Items.QUARTZ, 4));
				addRecipe(energy, new ItemStack(Blocks.SANDSTONE, 1, i), new ItemStack(Blocks.SAND, 2), ItemMaterial.dustNiter, 40);
				addRecipe(energy, new ItemStack(Blocks.RED_SANDSTONE, 1, i), new ItemStack(Blocks.SAND, 2, 1), ItemMaterial.dustNiter, 40);
			}

			/* STAIRS */
			addRecipe(energy, new ItemStack(Blocks.BRICK_STAIRS), new ItemStack(Items.BRICK, 3));
			addRecipe(energy, new ItemStack(Blocks.NETHER_BRICK_STAIRS), new ItemStack(Items.NETHERBRICK, 3));
			addRecipe(energy, new ItemStack(Blocks.QUARTZ_STAIRS), new ItemStack(Items.QUARTZ, 3));
			addRecipe(energy, new ItemStack(Blocks.SANDSTONE_STAIRS), new ItemStack(Blocks.SAND, 2), ItemMaterial.dustNiter, 20);
			addRecipe(energy, new ItemStack(Blocks.RED_SANDSTONE_STAIRS), new ItemStack(Blocks.SAND, 2, 1), ItemMaterial.dustNiter, 20);

			/* SLABS */
			addRecipe(energy / 2, new ItemStack(Blocks.STONE_SLAB, 1, 4), new ItemStack(Items.BRICK, 2));
			addRecipe(energy / 2, new ItemStack(Blocks.STONE_SLAB, 1, 6), new ItemStack(Items.NETHERBRICK, 2));
			addRecipe(energy / 2, new ItemStack(Blocks.STONE_SLAB, 1, 7), new ItemStack(Items.QUARTZ, 2));
			addRecipe(energy / 2, new ItemStack(Blocks.STONE_SLAB, 1, 1), new ItemStack(Blocks.SAND), ItemMaterial.dustNiter, 20);
			addRecipe(energy / 2, new ItemStack(Blocks.STONE_SLAB2, 1, 0), new ItemStack(Blocks.SAND, 1, 1), ItemMaterial.dustNiter, 20);

			/* MISC */
			addRecipe(energy / 2, new ItemStack(Items.FLOWER_POT), new ItemStack(Items.BRICK, 3));
			addRecipe(energy / 2, new ItemStack(Items.GLASS_BOTTLE), new ItemStack(Blocks.SAND));

			energy = DEFAULT_ENERGY * 3 / 2;

			ItemStack diamond = new ItemStack(Items.DIAMOND);

			addRecycleRecipe(energy, new ItemStack(Items.DIAMOND_SWORD), diamond, 2);
			addRecycleRecipe(energy, new ItemStack(Items.DIAMOND_PICKAXE), diamond, 3);
			addRecycleRecipe(energy, new ItemStack(Items.DIAMOND_AXE), diamond, 3);
			addRecycleRecipe(energy, new ItemStack(Items.DIAMOND_SHOVEL), diamond, 1);
			addRecycleRecipe(energy, new ItemStack(Items.DIAMOND_HOE), diamond, 2);

			addRecycleRecipe(energy, new ItemStack(Items.DIAMOND_HELMET), diamond, 4);
			addRecycleRecipe(energy, new ItemStack(Items.DIAMOND_CHESTPLATE), diamond, 7);
			addRecycleRecipe(energy, new ItemStack(Items.DIAMOND_LEGGINGS), diamond, 6);
			addRecycleRecipe(energy, new ItemStack(Items.DIAMOND_BOOTS), diamond, 3);

			addRecycleRecipe(energy, new ItemStack(Items.DIAMOND_HORSE_ARMOR), diamond, 4);

			addRecycleRecipe(energy, TFEquipment.ToolSetVanilla.DIAMOND.toolBow, diamond, 2);
			addRecycleRecipe(energy, TFEquipment.ToolSetVanilla.DIAMOND.toolFishingRod, diamond, 2);
			addRecycleRecipe(energy, TFEquipment.ToolSetVanilla.DIAMOND.toolShears, diamond, 2);
			addRecycleRecipe(energy, TFEquipment.ToolSetVanilla.DIAMOND.toolSickle, diamond, 3);
			addRecycleRecipe(energy, TFEquipment.ToolSetVanilla.DIAMOND.toolHammer, diamond, 5);
			addRecycleRecipe(energy, TFEquipment.ToolSetVanilla.DIAMOND.toolShield, diamond, 6);
		}

		/* LOAD RECIPES */
		loadRecipes();
	}

	public static void loadRecipes() {

		/* APPLIED ENERGISTICS 2 */
		{
			int energy = DEFAULT_ENERGY * 3 / 4;

			if (ItemHelper.oreNameExists("oreCertusQuartz") && ItemHelper.oreNameExists("dustCertusQuartz") && ItemHelper.oreNameExists("crystalCertusQuartz")) {
				addRecipe(DEFAULT_ENERGY, OreDictionary.getOres("oreCertusQuartz", false).get(0), ItemHelper.cloneStack(OreDictionary.getOres("crystalCertusQuartz", false).get(0), 2), OreDictionary.getOres("dustCertusQuartz", false).get(0), 10);
				addRecipe(energy, OreDictionary.getOres("crystalCertusQuartz", false).get(0), OreDictionary.getOres("dustCertusQuartz", false).get(0));
			}
			if (ItemHelper.oreNameExists("dustEnderPearl")) {
				addRecipe(energy, new ItemStack(Items.ENDER_PEARL), ItemHelper.cloneStack(OreDictionary.getOres("dustEnderPearl", false).get(0), 1));
			}
			if (ItemHelper.oreNameExists("dustFluix") && ItemHelper.oreNameExists("crystalFluix")) {
				addRecipe(energy, OreDictionary.getOres("crystalFluix", false).get(0), OreDictionary.getOres("dustFluix", false).get(0));
			}
			if (ItemHelper.oreNameExists("dustNetherQuartz")) {
				addRecipe(energy, new ItemStack(Items.QUARTZ), ItemHelper.cloneStack(OreDictionary.getOres("dustNetherQuartz", false).get(0), 1));
			}
		}

		/* FORESTRY */
		{
			if (ItemHelper.oreNameExists("oreApatite") && ItemHelper.oreNameExists("gemApatite")) {
				addRecipe(DEFAULT_ENERGY, OreDictionary.getOres("oreApatite", false).get(0), ItemHelper.cloneStack(OreDictionary.getOres("gemApatite", false).get(0), 12), ItemMaterial.dustSulfur, 10);
			}
		}

		/* TERRAQUEOUS */
		{
			if (ItemHelper.oreNameExists("oreEndimium") && ItemHelper.oreNameExists("dustEndimium") && ItemHelper.oreNameExists("dustTinyEndimium")) {
				if (ItemHelper.oreNameExists("gemEndimium")) {
					addIngotToDustRecipe(DEFAULT_ENERGY, "gemEndimium", OreDictionary.getOres("dustEndimium", false).get(0));
				}
				addRecipe(DEFAULT_ENERGY, OreDictionary.getOres("oreEndimium", false).get(0), ItemHelper.cloneStack(OreDictionary.getOres("dustEndimium", false).get(0), 4), ItemHelper.cloneStack(OreDictionary.getOres("dustTinyEndimium", false).get(0), 3), 40);
			}
			if (ItemHelper.oreNameExists("oreBurnium") && ItemHelper.oreNameExists("dustBurnium") && ItemHelper.oreNameExists("dustTinyBurnium")) {
				if (ItemHelper.oreNameExists("gemBurnium")) {
					addIngotToDustRecipe(DEFAULT_ENERGY, "gemBurnium", OreDictionary.getOres("dustBurnium", false).get(0));
				}
				addRecipe(DEFAULT_ENERGY, OreDictionary.getOres("oreBurnium", false).get(0), ItemHelper.cloneStack(OreDictionary.getOres("dustBurnium", false).get(0), 4), ItemHelper.cloneStack(OreDictionary.getOres("dustTinyBurnium", false).get(0), 3), 40);
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

		Map<ComparableItemStackValidated, PulverizerRecipe> tempMap = new THashMap<>(recipeMap.size());
		PulverizerRecipe tempRecipe;

		for (Entry<ComparableItemStackValidated, PulverizerRecipe> entry : recipeMap.entrySet()) {
			tempRecipe = entry.getValue();
			tempMap.put(convertInput(tempRecipe.input), tempRecipe);
		}
		recipeMap.clear();
		recipeMap = tempMap;
	}

	/* ADD RECIPES */
	public static PulverizerRecipe addRecipe(int energy, ItemStack input, ItemStack primaryOutput, ItemStack secondaryOutput, int secondaryChance) {

		if (input.isEmpty() || primaryOutput.isEmpty() || energy <= 0 || recipeExists(input)) {
			return null;
		}
		PulverizerRecipe recipe = new PulverizerRecipe(input, primaryOutput, secondaryOutput, secondaryOutput.isEmpty() ? 0 : secondaryChance, energy);
		recipeMap.put(convertInput(input), recipe);
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

		return recipeMap.remove(convertInput(input));
	}

	/* HELPERS */
	public static ComparableItemStackValidated convertInput(ItemStack stack) {

		return new ComparableItemStackValidated(stack, oreValidator);
	}

	private static void addDefaultOreDictionaryRecipe(String oreType) {

		addDefaultOreDictionaryRecipe(oreType, "");
	}

	private static void addDefaultOreDictionaryRecipe(String oreType, String relatedType) {

		if (oreType == null || oreType.isEmpty()) {
			return;
		}
		String oreName = "ore" + StringHelper.titleCase(oreType);
		String dustName = "dust" + StringHelper.titleCase(oreType);
		String ingotName = "ingot" + StringHelper.titleCase(oreType);
		String clusterName = "cluster" + StringHelper.titleCase(oreType);
		String relatedName;

		List<ItemStack> registeredOre = OreDictionary.getOres(oreName, false);
		List<ItemStack> registeredDust = OreDictionary.getOres(dustName, false);
		List<ItemStack> registeredIngot = OreDictionary.getOres(ingotName, false);
		List<ItemStack> registeredCluster = OreDictionary.getOres(clusterName, false);
		List<ItemStack> registeredRelated = new ArrayList<>();

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

		if (oreType == null || oreType.isEmpty()) {
			return;
		}
		String oreName = "ore" + StringHelper.titleCase(oreType);
		String dustName = "dust" + StringHelper.titleCase(oreType);
		String gemName = "gem" + StringHelper.titleCase(oreType);
		String relatedName;

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

	public static void addRecycleRecipe(int energy, ItemStack input, ItemStack output, int outputSize) {

		addRecycleRecipe(energy, input, output, outputSize, true);
	}

	public static void addRecycleRecipe(int energy, ItemStack input, ItemStack output, int outputSize, boolean wildcard) {

		ItemStack recycleInput = wildcard ? input.copy() : new ItemStack(input.getItem(), 1, OreDictionary.WILDCARD_VALUE);
		addRecipe(energy, recycleInput, ItemHelper.cloneStack(output, outputSize));
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

}
