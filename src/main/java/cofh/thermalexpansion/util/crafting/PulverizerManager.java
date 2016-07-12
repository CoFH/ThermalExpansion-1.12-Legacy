package cofh.thermalexpansion.util.crafting;

import cofh.core.util.oredict.OreDictionaryArbiter;
import cofh.lib.inventory.ComparableItemStack;
import cofh.lib.util.helpers.ColorHelper;
import cofh.lib.util.helpers.ItemHelper;
import cofh.lib.util.helpers.MathHelper;
import cofh.lib.util.helpers.StringHelper;
import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalexpansion.api.crafting.recipes.IPulverizerRecipe;
import cofh.thermalexpansion.item.TEItems;
import cofh.thermalfoundation.item.TFItems;
import cofh.thermalfoundation.item.VanillaEquipment;
import cpw.mods.fml.common.registry.GameRegistry;

import gnu.trove.map.hash.THashMap;

import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public class PulverizerManager {

	private static Map<ComparableItemStackPulverizer, RecipePulverizer> recipeMap = new THashMap<ComparableItemStackPulverizer, RecipePulverizer>();
	private static boolean allowOverwrite = false;
	public static final int DEFAULT_ENERGY = 3200;

	private static int oreMultiplier = 2;

	static {
		allowOverwrite = ThermalExpansion.config.get("RecipeManagers.Pulverizer", "AllowRecipeOverwrite", false);

		String category = "RecipeManagers.Pulverizer.Ore";
		String comment = "This sets the default rate for Ore->Dust conversion. This number is used in all automatically generated recipes.";
		oreMultiplier = MathHelper.clamp(ThermalExpansion.config.get(category, "DefaultMultiplier", oreMultiplier, comment), 1, 64);
	}

	public static RecipePulverizer getRecipe(ItemStack input) {

		if (input == null) {
			return null;
		}
		ComparableItemStackPulverizer query = new ComparableItemStackPulverizer(input);

		RecipePulverizer recipe = recipeMap.get(query);

		if (recipe == null) {
			query.metadata = OreDictionary.WILDCARD_VALUE;
			recipe = recipeMap.get(query);
		}
		return recipe;
	}

	public static boolean recipeExists(ItemStack input) {

		return getRecipe(input) != null;
	}

	public static RecipePulverizer[] getRecipeList() {

		return recipeMap.values().toArray(new RecipePulverizer[0]);
	}

	public static void addDefaultRecipes() {

		String comment;
		String category = "RecipeManagers.Pulverizer.Recipes";

		boolean recipeSandstone = ThermalExpansion.config.get(category, "Sandstone", true);
		boolean recipeNetherrack = ThermalExpansion.config.get(category, "Netherrack", true);
		boolean recipeWool = ThermalExpansion.config.get(category, "Wool", true);
		boolean recipeReed = ThermalExpansion.config.get(category, "Reed", true);
		boolean recipeBone = ThermalExpansion.config.get(category, "Bone", true);
		boolean recipeBlazeRod = ThermalExpansion.config.get(category, "BlazeRod", true);
		boolean recipeBlizzRod = ThermalExpansion.config.get(category, "BlizzRod", true);
		boolean recipeBlitzRod = ThermalExpansion.config.get(category, "BlitzRod", true);
		boolean recipeBasalzRod = ThermalExpansion.config.get(category, "BasalzRod", true);
		boolean recipeHorseArmor = ThermalExpansion.config.get(category, "HorseArmor", true);
		boolean recipeRedstoneLamp = ThermalExpansion.config.get(category, "RedstoneLamp", true);
		boolean recipeGlassBottle = ThermalExpansion.config.get(category, "GlassBottle", true);

		{ /* RECYCLING */
			addRecipe(3200, new ItemStack(Blocks.glass), new ItemStack(Blocks.sand));
			for (int i = 0; i < 16; ++i) {
				addRecipe(3200, new ItemStack(Blocks.stained_glass, 1, i), new ItemStack(Blocks.sand));
			}

			if (recipeRedstoneLamp) {
				addRecipe(3200, new ItemStack(Blocks.redstone_lamp), new ItemStack(Items.glowstone_dust, 4), new ItemStack(Items.redstone, 4));
			}

			addRecipe(1800, new ItemStack(Blocks.brick_block), new ItemStack(Items.brick, 4));
			addRecipe(1800, new ItemStack(Blocks.nether_brick), new ItemStack(Items.netherbrick, 4));
			for (int i = 0; i < 3; i++) {
				addRecipe(1800, new ItemStack(Blocks.quartz_block, 1, i), new ItemStack(Items.quartz, 4));
			}

			addRecipe(2400, new ItemStack(Blocks.brick_stairs), new ItemStack(Items.brick, 6));
			addRecipe(2400, new ItemStack(Blocks.nether_brick_stairs), new ItemStack(Items.netherbrick, 6));
			addRecipe(2400, new ItemStack(Blocks.quartz_stairs), new ItemStack(Items.quartz, 6));

			addRecipe(1200, new ItemStack(Blocks.stone_slab, 1, 4), new ItemStack(Items.brick, 2));
			addRecipe(1200, new ItemStack(Blocks.stone_slab, 1, 6), new ItemStack(Items.netherbrick, 2));
			addRecipe(1200, new ItemStack(Blocks.stone_slab, 1, 7), new ItemStack(Items.quartz, 2));

			if (recipeSandstone) {
				for (int i = 0; i < 3; i++) {
					addTERecipe(3200, new ItemStack(Blocks.sandstone, 1, i), new ItemStack(Blocks.sand, 2), TFItems.dustNiter, 50);
				}
				addRecipe(4800, new ItemStack(Blocks.sandstone_stairs), new ItemStack(Blocks.sand, 2), TFItems.dustNiter, 75);
				addRecipe(2400, new ItemStack(Blocks.stone_slab, 1, 1), new ItemStack(Blocks.sand, 1), TFItems.dustNiter, 25);
			}

			addRecipe(800, new ItemStack(Items.flower_pot), new ItemStack(Items.brick, 3));

			if (recipeGlassBottle) {
				addRecipe(800, new ItemStack(Items.glass_bottle), new ItemStack(Blocks.sand, 1));
			}

			if (recipeHorseArmor) {
				addRecipe(4800, new ItemStack(Items.iron_horse_armor), ItemHelper.cloneStack(TFItems.dustIron, 5));
				addRecipe(4800, new ItemStack(Items.golden_horse_armor), ItemHelper.cloneStack(TFItems.dustGold, 5));
				addRecipe(4800, new ItemStack(Items.diamond_horse_armor), new ItemStack(Items.diamond, 5, 0));
			}

			// TODO:
			// significant_ingredient * 600 + (stick/string) * 300 ?
			// i like the feel of these values. re-do other recycle recipes for tools/armor?
			addRecipe(1500, new ItemStack(Items.diamond_sword), new ItemStack(Items.diamond, 2));
			addRecipe(2400, new ItemStack(Items.diamond_pickaxe), new ItemStack(Items.diamond, 3));
			addRecipe(2400, new ItemStack(Items.diamond_axe), new ItemStack(Items.diamond, 3));
			addRecipe(1200, new ItemStack(Items.diamond_shovel), new ItemStack(Items.diamond, 1));
			addRecipe(1800, new ItemStack(Items.diamond_hoe), new ItemStack(Items.diamond, 2));
			addRecipe(3000, new ItemStack(Items.diamond_helmet), new ItemStack(Items.diamond, 5));
			addRecipe(4800, new ItemStack(Items.diamond_chestplate), new ItemStack(Items.diamond, 8));
			addRecipe(4200, new ItemStack(Items.diamond_leggings), new ItemStack(Items.diamond, 7));
			addRecipe(2400, new ItemStack(Items.diamond_boots), new ItemStack(Items.diamond, 4));
			addRecipe(2400, VanillaEquipment.Diamond.toolBow, new ItemStack(Items.diamond, 2));
			addRecipe(2100, VanillaEquipment.Diamond.toolFishingRod, new ItemStack(Items.diamond, 2));
			addRecipe(1200, VanillaEquipment.Diamond.toolShears, new ItemStack(Items.diamond, 2));
			addRecipe(2100, VanillaEquipment.Diamond.toolSickle, new ItemStack(Items.diamond, 3));
		}

		addRecipe(3200, new ItemStack(Blocks.stone), new ItemStack(Blocks.gravel), new ItemStack(Blocks.sand), 15);
		addRecipe(3200, new ItemStack(Blocks.cobblestone), new ItemStack(Blocks.sand), new ItemStack(Blocks.gravel), 15);
		addRecipe(3200, new ItemStack(Blocks.gravel), new ItemStack(Items.flint), new ItemStack(Blocks.sand), 15);
		addRecipe(800, new ItemStack(Blocks.stonebrick), new ItemStack(Blocks.stonebrick, 1, 2));

		addRecipe(2400, new ItemStack(Items.coal, 1, 0), TFItems.dustCoal, TFItems.dustSulfur, 15);
		addRecipe(2400, new ItemStack(Items.coal, 1, 1), TFItems.dustCharcoal);
		addRecipe(4000, new ItemStack(Blocks.obsidian), ItemHelper.cloneStack(TFItems.dustObsidian, 4));

		if (recipeNetherrack) {
			addTERecipe(3200, new ItemStack(Blocks.netherrack), new ItemStack(Blocks.gravel), TFItems.dustSulfur, 15);
		}
		addRecipe(2400, new ItemStack(Blocks.coal_ore), new ItemStack(Items.coal, 3, 0), TFItems.dustCoal, 25);
		addRecipe(2400, new ItemStack(Blocks.diamond_ore), new ItemStack(Items.diamond, 2, 0));
		addRecipe(2400, new ItemStack(Blocks.emerald_ore), new ItemStack(Items.emerald, 2, 0));
		addRecipe(2400, new ItemStack(Blocks.glowstone), new ItemStack(Items.glowstone_dust, 4));
		addRecipe(2400, new ItemStack(Blocks.lapis_ore), new ItemStack(Items.dye, 12, 4), TFItems.dustSulfur, 20);
		addTERecipe(3200, new ItemStack(Blocks.redstone_ore), new ItemStack(Items.redstone, 6), TFItems.crystalCinnabar, 25);
		addRecipe(2400, new ItemStack(Blocks.quartz_ore), new ItemStack(Items.quartz, 3), TFItems.dustSulfur, 15);

		addRecipe(1600, new ItemStack(Blocks.log), ItemHelper.cloneStack(TEItems.sawdust, 8));

		addRecipe(1600, new ItemStack(Blocks.yellow_flower), new ItemStack(Items.dye, 4, 11));
		addRecipe(1600, new ItemStack(Blocks.red_flower, 1, 0), new ItemStack(Items.dye, 4, 1));
		addRecipe(1600, new ItemStack(Blocks.red_flower, 1, 1), new ItemStack(Items.dye, 4, 12));
		addRecipe(1600, new ItemStack(Blocks.red_flower, 1, 2), new ItemStack(Items.dye, 4, 13));
		addRecipe(1600, new ItemStack(Blocks.red_flower, 1, 3), new ItemStack(Items.dye, 4, 7));
		addRecipe(1600, new ItemStack(Blocks.red_flower, 1, 4), new ItemStack(Items.dye, 4, 1));
		addRecipe(1600, new ItemStack(Blocks.red_flower, 1, 5), new ItemStack(Items.dye, 4, 14));
		addRecipe(1600, new ItemStack(Blocks.red_flower, 1, 6), new ItemStack(Items.dye, 4, 7));
		addRecipe(1600, new ItemStack(Blocks.red_flower, 1, 7), new ItemStack(Items.dye, 4, 9));
		addRecipe(1600, new ItemStack(Blocks.red_flower, 1, 8), new ItemStack(Items.dye, 4, 7));

		addRecipe(1600, new ItemStack(Blocks.double_plant, 1, 0), new ItemStack(Items.dye, 8, 11));
		addRecipe(1600, new ItemStack(Blocks.double_plant, 1, 1), new ItemStack(Items.dye, 8, 13));
		addRecipe(1600, new ItemStack(Blocks.double_plant, 1, 4), new ItemStack(Items.dye, 8, 1));
		addRecipe(1600, new ItemStack(Blocks.double_plant, 1, 5), new ItemStack(Items.dye, 8, 9));

		if (recipeWool) {
			category = "RecipeManagers.Pulverizer.Wool";
			int[] dyeChance = new int[ColorHelper.woolColorConfig.length];

			comment = "This sets the default rate for Wool->String conversion. This number is used in all automatically generated recipes.";
			int numString = ThermalExpansion.config.get(category, "String", 4, comment);
			ItemStack stringStack = new ItemStack(Items.string, numString);

			for (int i = 0; i < ColorHelper.woolColorConfig.length; i++) {
				dyeChance[i] = 5;
			}
			dyeChance[0] = 0;
			dyeChance[12] = 0;
			dyeChance[13] = 0;
			dyeChance[15] = 0;

			category = "RecipeManagers.Pulverizer.Wool.Dye";
			for (int i = 0; i < ColorHelper.woolColorConfig.length; i++) {
				dyeChance[i] = MathHelper.clamp(ThermalExpansion.config.get(category, ColorHelper.woolColorConfig[i], dyeChance[i]), 0, 100);

				if (dyeChance[i] > 0) {
					addTERecipe(1600, new ItemStack(Blocks.wool, 1, i), stringStack, new ItemStack(Items.dye, 1, 15 - i), dyeChance[i]);
				} else {
					addTERecipe(1600, new ItemStack(Blocks.wool, 1, i), stringStack);
				}
			}
		}
		if (recipeReed) {
			addTERecipe(800, new ItemStack(Items.reeds), new ItemStack(Items.sugar, 2));
		}
		if (recipeBone) {
			addTERecipe(1600, new ItemStack(Items.bone), new ItemStack(Items.dye, 6, 15));
		}
		if (recipeBlazeRod) {
			addTERecipe(1600, new ItemStack(Items.blaze_rod), new ItemStack(Items.blaze_powder, 4), TFItems.dustSulfur, 50);
		}
		if (recipeBlizzRod) {
			addTERecipe(1600, TFItems.rodBlizz, ItemHelper.cloneStack(TFItems.dustBlizz, 4), new ItemStack(Items.snowball), 50);
		}
		if (recipeBlitzRod) {
			addTERecipe(1600, TFItems.rodBlitz, ItemHelper.cloneStack(TFItems.dustBlitz, 4), TFItems.dustNiter, 50);
		}
		if (recipeBasalzRod) {
			addTERecipe(1600, TFItems.rodBasalz, ItemHelper.cloneStack(TFItems.dustBasalz, 4), TFItems.dustObsidian, 50);
		}
		int energy = 4000;

		addOreNameToDustRecipe(energy, "oreIron", TFItems.dustIron, TFItems.dustNickel, 10);
		addOreNameToDustRecipe(energy, "oreGold", TFItems.dustGold, TFItems.crystalCinnabar, 5);
		addOreNameToDustRecipe(energy, "oreCopper", TFItems.dustCopper, TFItems.dustGold, 10);
		addOreNameToDustRecipe(energy, "oreTin", TFItems.dustTin, TFItems.dustIron, 10);
		addOreNameToDustRecipe(energy, "oreSilver", TFItems.dustSilver, TFItems.dustLead, 10);
		addOreNameToDustRecipe(energy, "oreLead", TFItems.dustLead, TFItems.dustSilver, 10);
		addOreNameToDustRecipe(energy, "oreNickel", TFItems.dustNickel, TFItems.dustPlatinum, 10);
		addOreNameToDustRecipe(energy, "orePlatinum", TFItems.dustPlatinum, null, 0);

		energy = 2400;

		addIngotNameToDustRecipe(energy, "ingotIron", TFItems.dustIron);
		addIngotNameToDustRecipe(energy, "ingotGold", TFItems.dustGold);
		addIngotNameToDustRecipe(energy, "ingotCopper", TFItems.dustCopper);
		addIngotNameToDustRecipe(energy, "ingotTin", TFItems.dustTin);
		addIngotNameToDustRecipe(energy, "ingotSilver", TFItems.dustSilver);
		addIngotNameToDustRecipe(energy, "ingotLead", TFItems.dustLead);
		addIngotNameToDustRecipe(energy, "ingotNickel", TFItems.dustNickel);
		addIngotNameToDustRecipe(energy, "ingotPlatinum", TFItems.dustPlatinum);
		addIngotNameToDustRecipe(energy, "ingotElectrum", TFItems.dustElectrum);
		addIngotNameToDustRecipe(energy, "ingotInvar", TFItems.dustInvar);
		addIngotNameToDustRecipe(energy, "ingotBronze", TFItems.dustBronze);
	}

	public static void loadRecipes() {

		String category = "RecipeManagers.Pulverizer.Recipes";

		boolean siliconRecipe = ThermalExpansion.config.get(category, "Silicon", true);
		boolean diamondRecipe = ThermalExpansion.config.get(category, "Diamond", true);
		boolean enderPearlRecipe = ThermalExpansion.config.get(category, "EnderPearl", true);

		if (ItemHelper.oreNameExists("itemSilicon") && siliconRecipe) {
			addRecipe(1600, new ItemStack(Blocks.sand, 1), ItemHelper.cloneStack(OreDictionary.getOres("itemSilicon").get(0), 1));
		}
		if (ItemHelper.oreNameExists("dustDiamond") && diamondRecipe) {
			addRecipe(3200, new ItemStack(Items.diamond, 1), ItemHelper.cloneStack(OreDictionary.getOres("dustDiamond").get(0), 1));
		}
		if (ItemHelper.oreNameExists("dustEnderPearl") && enderPearlRecipe) {
			addRecipe(1600, new ItemStack(Items.ender_pearl), ItemHelper.cloneStack(OreDictionary.getOres("dustEnderPearl").get(0), 1));
		}
		if (ItemHelper.oreNameExists("oreSaltpeter")) {
			addRecipe(2400, OreDictionary.getOres("oreSaltpeter").get(0), ItemHelper.cloneStack(TFItems.dustNiter, 4));
		}
		if (ItemHelper.oreNameExists("oreSulfur")) {
			addRecipe(2400, OreDictionary.getOres("oreSulfur").get(0), ItemHelper.cloneStack(TFItems.dustSulfur, 6));
		}
		/* APATITE */
		if (ItemHelper.oreNameExists("oreApatite") && ItemHelper.oreNameExists("gemApatite")) {
			addRecipe(2400, OreDictionary.getOres("oreApatite").get(0), ItemHelper.cloneStack(OreDictionary.getOres("gemApatite").get(0), 12),
				TFItems.dustSulfur, 10);
		}
		/* AMETHYST */
		if (ItemHelper.oreNameExists("oreAmethyst") && ItemHelper.oreNameExists("gemAmethyst")) {
			addRecipe(2400, OreDictionary.getOres("oreAmethyst").get(0), ItemHelper.cloneStack(OreDictionary.getOres("gemAmethyst").get(0), 2));
		}
		if (ItemHelper.oreNameExists("gemAmethyst") && ItemHelper.oreNameExists("dustAmethyst")) {
			addRecipe(3200, OreDictionary.getOres("gemAmethyst").get(0), ItemHelper.cloneStack(OreDictionary.getOres("dustAmethyst").get(0), 1));
		}
		/* PERIDOT */
		if (ItemHelper.oreNameExists("orePeridot") && ItemHelper.oreNameExists("gemPeridot")) {
			addRecipe(2400, OreDictionary.getOres("orePeridot").get(0), ItemHelper.cloneStack(OreDictionary.getOres("gemPeridot").get(0), 2));
		}
		if (ItemHelper.oreNameExists("gemPeridot") && ItemHelper.oreNameExists("dustPeridot")) {
			addRecipe(3200, OreDictionary.getOres("gemPeridot").get(0), ItemHelper.cloneStack(OreDictionary.getOres("dustPeridot").get(0), 1));
		}
		/* RUBY */
		if (ItemHelper.oreNameExists("oreRuby") && ItemHelper.oreNameExists("gemRuby")) {
			addRecipe(2400, OreDictionary.getOres("oreRuby").get(0), ItemHelper.cloneStack(OreDictionary.getOres("gemRuby").get(0), 2));
		}
		if (ItemHelper.oreNameExists("gemRuby") && ItemHelper.oreNameExists("dustRuby")) {
			addRecipe(3200, OreDictionary.getOres("gemRuby").get(0), ItemHelper.cloneStack(OreDictionary.getOres("dustRuby").get(0), 1));
		}
		/* SAPPHIRE */
		if (ItemHelper.oreNameExists("oreSapphire") && ItemHelper.oreNameExists("gemSapphire")) {
			addRecipe(2400, OreDictionary.getOres("oreSapphire").get(0), ItemHelper.cloneStack(OreDictionary.getOres("gemSapphire").get(0), 2));
		}
		if (ItemHelper.oreNameExists("gemSapphire") && ItemHelper.oreNameExists("dustSapphire")) {
			addRecipe(3200, OreDictionary.getOres("gemSapphire").get(0), ItemHelper.cloneStack(OreDictionary.getOres("dustSapphire").get(0), 1));
		}
		/* APPLIED ENERGISTICS 2 */
		if (ItemHelper.oreNameExists("oreCertusQuartz") && ItemHelper.oreNameExists("dustCertusQuartz") && ItemHelper.oreNameExists("crystalCertusQuartz")) {
			addRecipe(2400, OreDictionary.getOres("oreCertusQuartz").get(0), ItemHelper.cloneStack(OreDictionary.getOres("crystalCertusQuartz").get(0), 2),
				OreDictionary.getOres("dustCertusQuartz").get(0), 10);
			addRecipe(1600, OreDictionary.getOres("crystalCertusQuartz").get(0), OreDictionary.getOres("dustCertusQuartz").get(0));
		}
		if (ItemHelper.oreNameExists("dustFluix") && ItemHelper.oreNameExists("crystalFluix")) {
			addRecipe(1600, OreDictionary.getOres("crystalFluix").get(0), OreDictionary.getOres("dustFluix").get(0));
		}
		if (ItemHelper.oreNameExists("dustNetherQuartz")) {
			addRecipe(1600, new ItemStack(Items.quartz, 1), ItemHelper.cloneStack(OreDictionary.getOres("dustNetherQuartz").get(0), 1));
		}

		String[] oreNameList = OreDictionary.getOreNames();
		String oreName = "";

		for (int i = 0; i < oreNameList.length; i++) {
			if (oreNameList[i].startsWith("ore")) {
				oreName = oreNameList[i].substring(3, oreNameList[i].length());
				addDefaultOreDictionaryRecipe(oreName);
			} else if (oreNameList[i].startsWith("dust")) {
				oreName = oreNameList[i].substring(4, oreNameList[i].length());
				addDefaultOreDictionaryRecipe(oreName);
			}
		}
	}

	public static void refreshRecipes() {

		Map<ComparableItemStackPulverizer, RecipePulverizer> tempMap = new THashMap<ComparableItemStackPulverizer, RecipePulverizer>(recipeMap.size());
		RecipePulverizer tempRecipe;

		for (Entry<ComparableItemStackPulverizer, RecipePulverizer> entry : recipeMap.entrySet()) {
			tempRecipe = entry.getValue();
			tempMap.put(new ComparableItemStackPulverizer(tempRecipe.input), tempRecipe);
		}
		recipeMap.clear();
		recipeMap = tempMap;
	}

	/* ADD RECIPES */
	protected static boolean addTERecipe(int energy, ItemStack input, ItemStack primaryOutput, ItemStack secondaryOutput, int secondaryChance) {

		if (input == null || primaryOutput == null || energy <= 0) {
			return false;
		}
		RecipePulverizer recipe = new RecipePulverizer(input, primaryOutput, secondaryOutput, secondaryChance, energy);
		recipeMap.put(new ComparableItemStackPulverizer(input), recipe);
		return true;
	}

	public static boolean addRecipe(int energy, ItemStack input, ItemStack primaryOutput, ItemStack secondaryOutput, int secondaryChance, boolean overwrite) {

		if (input == null || primaryOutput == null || energy <= 0 || !(allowOverwrite & overwrite) && recipeExists(input)) {
			return false;
		}
		RecipePulverizer recipe = new RecipePulverizer(input, primaryOutput, secondaryOutput, secondaryChance, energy);
		recipeMap.put(new ComparableItemStackPulverizer(input), recipe);
		return true;
	}

	/* REMOVE RECIPES */
	public static boolean removeRecipe(ItemStack input) {

		return recipeMap.remove(new ComparableItemStackPulverizer(input)) != null;
	}

	/* HELPER FUNCTIONS */
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

		ArrayList<ItemStack> registeredOre = OreDictionary.getOres(oreName);
		ArrayList<ItemStack> registeredDust = OreDictionary.getOres(dustName);
		ArrayList<ItemStack> registeredIngot = OreDictionary.getOres(ingotName);
		ArrayList<ItemStack> registeredRelated = new ArrayList<ItemStack>();

		String clusterName = "cluster" + StringHelper.titleCase(oreType);
		ArrayList<ItemStack> registeredCluster = OreDictionary.getOres(clusterName);

		if (relatedType != "") {
			relatedName = "dust" + StringHelper.titleCase(relatedType);
			registeredRelated = OreDictionary.getOres(relatedName);
		}
		if (registeredDust.isEmpty()) {
			return;
		}
		ItemStack dust = GameRegistry.findItemStack("ThermalFoundation", dustName, 1);
		if (dust != null && !OreDictionaryArbiter.getAllOreNames(dust).contains(dustName)) {
			dust = null;
		}
		if (dust == null) {
			dust = registeredDust.get(0);
		}
		if (registeredIngot.isEmpty()) {
			ingotName = null;
		}
		if (registeredOre.isEmpty()) {
			oreName = null;
		}
		if (registeredCluster.isEmpty()) {
			clusterName = null;
		}
		ItemStack related = null;
		if (relatedName != null) {
			related = GameRegistry.findItemStack("ThermalFoundation", relatedName, 1);
			if (related != null && !OreDictionaryArbiter.getAllOreNames(related).contains(relatedName)) {
				related = null;
			}
		}
		if (related == null && !registeredRelated.isEmpty()) {
			related = registeredRelated.get(0);
		}
		addOreNameToDustRecipe(4000, oreName, ItemHelper.cloneStack(dust, oreMultiplier), related, 5);
		addOreNameToDustRecipe(4800, clusterName, ItemHelper.cloneStack(dust, oreMultiplier), related, 5);
		addIngotNameToDustRecipe(2400, ingotName, ItemHelper.cloneStack(dust, 1));
	}

	public static void addOreNameToDustRecipe(int energy, String oreName, ItemStack primaryOutput, ItemStack secondaryOutput, int secondaryChance) {

		if (primaryOutput == null || oreName == null) {
			return;
		}
		ArrayList<ItemStack> registeredOres = OreDictionary.getOres(oreName);

		if (!registeredOres.isEmpty()) {
			addRecipe(energy, ItemHelper.cloneStack(registeredOres.get(0), 1), ItemHelper.cloneStack(primaryOutput, oreMultiplier), secondaryOutput,
				secondaryChance);
		}
	}

	public static void addOreToDustRecipe(int energy, ItemStack ore, ItemStack primaryOutput, ItemStack secondaryOutput, int secondaryChance) {

		if (primaryOutput == null) {
			return;
		}
		ItemStack dust = ItemHelper.cloneStack(primaryOutput, oreMultiplier);
		addRecipe(energy, ore, dust, secondaryOutput, secondaryChance);
	}

	public static void addIngotNameToDustRecipe(int energy, String ingotName, ItemStack dust) {

		if (dust == null || ingotName == null) {
			return;
		}
		ArrayList<ItemStack> registeredOres = OreDictionary.getOres(ingotName);

		if (!registeredOres.isEmpty()) {
			addRecipe(energy, ItemHelper.cloneStack(registeredOres.get(0), 1), dust, null, 0);
		}
	}

	public static void addIngotNameToDustRecipe(int energy, String oreType) {

	}

	public static boolean addTERecipe(int energy, ItemStack input, ItemStack primaryOutput) {

		return addTERecipe(energy, input, primaryOutput, null, 0);
	}

	public static boolean addRecipe(int energy, ItemStack input, ItemStack primaryOutput) {

		return addRecipe(energy, input, primaryOutput, false);
	}

	public static boolean addRecipe(int energy, ItemStack input, ItemStack primaryOutput, boolean overwrite) {

		return addRecipe(energy, input, primaryOutput, null, 0, overwrite);
	}

	public static boolean addRecipe(int energy, ItemStack input, ItemStack primaryOutput, ItemStack secondaryOutput) {

		return addRecipe(energy, input, primaryOutput, secondaryOutput, false);
	}

	public static boolean addRecipe(int energy, ItemStack input, ItemStack primaryOutput, ItemStack secondaryOutput, boolean overwrite) {

		return addRecipe(energy, input, primaryOutput, secondaryOutput, 100, overwrite);
	}

	public static boolean addRecipe(int energy, ItemStack input, ItemStack primaryOutput, ItemStack secondaryOutput, int secondaryChance) {

		return addRecipe(energy, input, primaryOutput, secondaryOutput, secondaryChance, false);
	}

	/* RECIPE CLASS */
	public static class RecipePulverizer implements IPulverizerRecipe {

		final ItemStack input;
		final ItemStack primaryOutput;
		final ItemStack secondaryOutput;
		final int secondaryChance;
		final int energy;

		RecipePulverizer(ItemStack input, ItemStack primaryOutput, ItemStack secondaryOutput, int secondaryChance, int energy) {

			this.input = input;
			this.primaryOutput = primaryOutput;
			this.secondaryOutput = secondaryOutput;
			this.secondaryChance = secondaryChance;
			this.energy = energy;

			if (input.stackSize <= 0) {
				input.stackSize = 1;
			}
			if (primaryOutput.stackSize <= 0) {
				primaryOutput.stackSize = 1;
			}
			if (secondaryOutput != null && secondaryOutput.stackSize <= 0) {
				secondaryOutput.stackSize = 1;
			}
		}

		@Override
		public ItemStack getInput() {

			return input.copy();
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
	public static class ComparableItemStackPulverizer extends ComparableItemStack {

		static final String ORE = "ore";
		static final String INGOT = "ingot";
		static final String NUGGET = "nugget";
		static final String LOG = "log";
		static final String SAND = "sand";

		public static boolean safeOreType(String oreName) {

			return oreName.startsWith(ORE) || oreName.startsWith(INGOT) || oreName.startsWith(NUGGET) || oreName.startsWith(LOG) || oreName.equals(SAND);
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

		public ComparableItemStackPulverizer(ItemStack stack) {

			super(stack);
			oreID = getOreID(stack);
		}

		public ComparableItemStackPulverizer(Item item, int damage, int stackSize) {

			super(item, damage, stackSize);
			this.oreID = getOreID(this.toItemStack());
		}

		@Override
		public ComparableItemStackPulverizer set(ItemStack stack) {

			super.set(stack);
			oreID = getOreID(stack);

			return this;
		}
	}

}
