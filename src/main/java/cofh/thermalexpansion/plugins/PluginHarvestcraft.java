package cofh.thermalexpansion.plugins;

import cofh.core.util.ModPlugin;
import cofh.core.util.helpers.ItemHelper;
import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalexpansion.util.managers.FisherManager;
import cofh.thermalexpansion.util.managers.machine.CentrifugeManager;
import cofh.thermalexpansion.util.managers.machine.InsolatorManager;
import cofh.thermalexpansion.util.managers.machine.InsolatorManager.Type;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Loader;

import java.util.Arrays;

public class PluginHarvestcraft extends ModPlugin {

	public static final String MOD_ID = "harvestcraft";
	public static final String MOD_NAME = "HarvestCraft";

	public PluginHarvestcraft() {

		super(MOD_ID, MOD_NAME);
	}

	/* IInitializer */
	@Override
	public boolean initialize() {

		String category = "Plugins";
		String comment = "If TRUE, support for " + MOD_NAME + " is enabled.";
		enable = Loader.isModLoaded(MOD_ID) && ThermalExpansion.CONFIG.getConfiguration().getBoolean(MOD_NAME, category, true, comment);

		if (!enable) {
			return false;
		}
		return !error;
	}

	@Override
	public boolean register() {

		if (!enable) {
			return false;
		}
		try {
			ItemStack beeswax = getItemStack("beeswaxitem");
			ItemStack honey = getItemStack("honeyitem");
			ItemStack honeycomb = getItemStack("honeycombitem");
			ItemStack waxcomb = getItemStack("waxcombitem");

			ItemStack cookingOil = getItemStack("oliveoilitem");
			ItemStack sesameOil = getItemStack("sesameoilitem");

			ItemStack fruitBait = getItemStack("fruitbaititem");
			ItemStack grainBait = getItemStack("grainbaititem");
			ItemStack veggieBait = getItemStack("veggiebaititem");
			ItemStack fishBait = getItemStack("fishtrapbaititem");

			ItemStack saplingDate = getBlockStack("date_sapling");
			ItemStack saplingPapaya = getBlockStack("papaya_sapling");
			ItemStack saplingCherry = getBlockStack("cherry_sapling");
			ItemStack saplingFig = getBlockStack("fig_sapling");
			ItemStack saplingDragonfruit = getBlockStack("dragonfruit_sapling");
			ItemStack saplingApple = getBlockStack("apple_sapling");
			ItemStack saplingLemon = getBlockStack("lemon_sapling");
			ItemStack saplingPear = getBlockStack("pear_sapling");
			ItemStack saplingOlive = getBlockStack("olive_sapling");
			ItemStack saplingGrapefruit = getBlockStack("grapefruit_sapling");
			ItemStack saplingPomegranate = getBlockStack("pomegranate_sapling");
			ItemStack saplingCashew = getBlockStack("cashew_sapling");
			ItemStack saplingVanilla = getBlockStack("vanilla_sapling");
			ItemStack saplingStarfruit = getBlockStack("starfruit_sapling");
			ItemStack saplingBanana = getBlockStack("banana_sapling");
			ItemStack saplingPlum = getBlockStack("plum_sapling");
			ItemStack saplingAvocado = getBlockStack("avocado_sapling");
			ItemStack saplingPecan = getBlockStack("pecan_sapling");
			ItemStack saplingPistachio = getBlockStack("pistachio_sapling");
			ItemStack saplingLime = getBlockStack("lime_sapling");
			ItemStack saplingPeppercorn = getBlockStack("peppercorn_sapling");
			ItemStack saplingAlmond = getBlockStack("almond_sapling");
			ItemStack saplingGooseberry = getBlockStack("gooseberry_sapling");
			ItemStack saplingPeach = getBlockStack("peach_sapling");
			ItemStack saplingChestnut = getBlockStack("chestnut_sapling");
			ItemStack saplingCoconut = getBlockStack("coconut_sapling");
			ItemStack saplingMango = getBlockStack("mango_sapling");
			ItemStack saplingApricot = getBlockStack("apricot_sapling");
			ItemStack saplingOrange = getBlockStack("orange_sapling");
			ItemStack saplingWalnut = getBlockStack("walnut_sapling");
			ItemStack saplingPersimmon = getBlockStack("persimmon_sapling");
			ItemStack saplingNutmeg = getBlockStack("nutmeg_sapling");
			ItemStack saplingDurian = getBlockStack("durian_sapling");

			ItemStack itemDate = getItemStack("dateitem");
			ItemStack itemPapaya = getItemStack("papayaitem");
			ItemStack itemCherry = getItemStack("cherryitem");
			ItemStack itemFig = getItemStack("figitem");
			ItemStack itemDragonfruit = getItemStack("dragonfruititem");
			// ItemStack itemApple = getItemStack("appleitem");
			ItemStack itemLemon = getItemStack("lemonitem");
			ItemStack itemPear = getItemStack("pearitem");
			ItemStack itemOlive = getItemStack("oliveitem");
			ItemStack itemGrapefruit = getItemStack("grapefruititem");
			ItemStack itemPomegranate = getItemStack("pomegranateitem");
			ItemStack itemCashew = getItemStack("cashewitem");
			ItemStack itemVanilla = getItemStack("vanillaitem");
			ItemStack itemStarfruit = getItemStack("starfruititem");
			ItemStack itemBanana = getItemStack("bananaitem");
			ItemStack itemPlum = getItemStack("plumitem");
			ItemStack itemAvocado = getItemStack("avocadoitem");
			ItemStack itemPecan = getItemStack("pecanitem");
			ItemStack itemPistachio = getItemStack("pistachioitem");
			ItemStack itemLime = getItemStack("limeitem");
			ItemStack itemPeppercorn = getItemStack("peppercornitem");
			ItemStack itemAlmond = getItemStack("almonditem");
			ItemStack itemGooseberry = getItemStack("gooseberryitem");
			ItemStack itemPeach = getItemStack("peachitem");
			ItemStack itemChestnut = getItemStack("chestnutitem");
			ItemStack itemCoconut = getItemStack("coconutitem");
			ItemStack itemMango = getItemStack("mangoitem");
			ItemStack itemApricot = getItemStack("apricotitem");
			ItemStack itemOrange = getItemStack("orangeitem");
			ItemStack itemWalnut = getItemStack("walnutitem");
			ItemStack itemPersimmon = getItemStack("persimmonitem");
			ItemStack itemNutmeg = getItemStack("nutmegitem");
			ItemStack itemDurian = getItemStack("durianitem");

			ItemStack fishAnchovy = getItemStack("anchovyrawitem");
			ItemStack fishBass = getItemStack("bassrawitem");
			ItemStack fishCarp = getItemStack("carprawitem");
			ItemStack fishCatfish = getItemStack("catfishrawitem");
			ItemStack fishCharr = getItemStack("charrrawitem");
			ItemStack fishEel = getItemStack("eelrawitem");
			ItemStack fishGrouper = getItemStack("grouperrawitem");
			ItemStack fishHerring = getItemStack("herringrawitem");
			ItemStack fishMudfish = getItemStack("mudfishrawitem");
			ItemStack fishPerch = getItemStack("perchrawitem");
			ItemStack fishSnapper = getItemStack("snapperrawitem");
			ItemStack fishTilapia = getItemStack("tilapiarawitem");
			ItemStack fishTrout = getItemStack("troutrawitem");
			ItemStack fishTuna = getItemStack("tunarawitem");
			ItemStack fishWalleye = getItemStack("walleyerawitem");

			/* Currently omitting the following:
				clam
				crab
				crayfish
				frog
				jellyfish
				octopus
				scallop
				shrimp
				snail
				turtle
			 */

			// These seem to not have much of a use yet, so also omitting.
			ItemStack fishGreenHeart = getItemStack("greenheartfishitem");
			ItemStack fishSardine = getItemStack("sardinerawitem");

			/* INSOLATOR */
			{
				InsolatorManager.addDefaultRecipe(saplingDate, ItemHelper.cloneStack(itemDate, 2), saplingDate, 100, false, Type.TREE);
				InsolatorManager.addDefaultRecipe(saplingPapaya, ItemHelper.cloneStack(itemPapaya, 2), saplingPapaya, 100, false, Type.TREE);
				InsolatorManager.addDefaultRecipe(saplingCherry, ItemHelper.cloneStack(itemCherry, 2), saplingCherry, 100, false, Type.TREE);
				InsolatorManager.addDefaultRecipe(saplingFig, ItemHelper.cloneStack(itemFig, 2), saplingFig, 100, false, Type.TREE);
				InsolatorManager.addDefaultRecipe(saplingDragonfruit, ItemHelper.cloneStack(itemDragonfruit, 2), saplingDragonfruit, 100, false, Type.TREE);
				InsolatorManager.addDefaultRecipe(saplingApple, ItemHelper.cloneStack(Items.APPLE, 2), saplingApple, 100, false, Type.TREE);
				InsolatorManager.addDefaultRecipe(saplingLemon, ItemHelper.cloneStack(itemLemon, 2), saplingLemon, 100, false, Type.TREE);
				InsolatorManager.addDefaultRecipe(saplingPear, ItemHelper.cloneStack(itemPear, 2), saplingPear, 100, false, Type.TREE);
				InsolatorManager.addDefaultRecipe(saplingOlive, ItemHelper.cloneStack(itemOlive, 2), saplingOlive, 100, false, Type.TREE);
				InsolatorManager.addDefaultRecipe(saplingGrapefruit, ItemHelper.cloneStack(itemGrapefruit, 2), saplingGrapefruit, 100, false, Type.TREE);
				InsolatorManager.addDefaultRecipe(saplingPomegranate, ItemHelper.cloneStack(itemPomegranate, 2), saplingPomegranate, 100, false, Type.TREE);
				InsolatorManager.addDefaultRecipe(saplingCashew, ItemHelper.cloneStack(itemCashew, 2), saplingCashew, 100, false, Type.TREE);
				InsolatorManager.addDefaultRecipe(saplingVanilla, ItemHelper.cloneStack(itemVanilla, 2), saplingVanilla, 100, false, Type.TREE);
				InsolatorManager.addDefaultRecipe(saplingStarfruit, ItemHelper.cloneStack(itemStarfruit, 2), saplingStarfruit, 100, false, Type.TREE);
				InsolatorManager.addDefaultRecipe(saplingBanana, ItemHelper.cloneStack(itemBanana, 2), saplingBanana, 100, false, Type.TREE);
				InsolatorManager.addDefaultRecipe(saplingPlum, ItemHelper.cloneStack(itemPlum, 2), saplingPlum, 100, false, Type.TREE);
				InsolatorManager.addDefaultRecipe(saplingAvocado, ItemHelper.cloneStack(itemAvocado, 2), saplingAvocado, 100, false, Type.TREE);
				InsolatorManager.addDefaultRecipe(saplingPecan, ItemHelper.cloneStack(itemPecan, 2), saplingPecan, 100, false, Type.TREE);
				InsolatorManager.addDefaultRecipe(saplingPistachio, ItemHelper.cloneStack(itemPistachio, 2), saplingPistachio, 100, false, Type.TREE);
				InsolatorManager.addDefaultRecipe(saplingLime, ItemHelper.cloneStack(itemLime, 2), saplingLime, 100, false, Type.TREE);
				InsolatorManager.addDefaultRecipe(saplingPeppercorn, ItemHelper.cloneStack(itemPeppercorn, 2), saplingPeppercorn, 100, false, Type.TREE);
				InsolatorManager.addDefaultRecipe(saplingAlmond, ItemHelper.cloneStack(itemAlmond, 2), saplingAlmond, 100, false, Type.TREE);
				InsolatorManager.addDefaultRecipe(saplingGooseberry, ItemHelper.cloneStack(itemGooseberry, 2), saplingGooseberry, 100, false, Type.TREE);
				InsolatorManager.addDefaultRecipe(saplingPeach, ItemHelper.cloneStack(itemPeach, 2), saplingPeach, 100, false, Type.TREE);
				InsolatorManager.addDefaultRecipe(saplingChestnut, ItemHelper.cloneStack(itemChestnut, 2), saplingChestnut, 100, false, Type.TREE);
				InsolatorManager.addDefaultRecipe(saplingCoconut, ItemHelper.cloneStack(itemCoconut, 2), saplingCoconut, 100, false, Type.TREE);
				InsolatorManager.addDefaultRecipe(saplingMango, ItemHelper.cloneStack(itemMango, 2), saplingMango, 100, false, Type.TREE);
				InsolatorManager.addDefaultRecipe(saplingApricot, ItemHelper.cloneStack(itemApricot, 2), saplingApricot, 100, false, Type.TREE);
				InsolatorManager.addDefaultRecipe(saplingOrange, ItemHelper.cloneStack(itemOrange, 2), saplingOrange, 100, false, Type.TREE);
				InsolatorManager.addDefaultRecipe(saplingWalnut, ItemHelper.cloneStack(itemWalnut, 2), saplingWalnut, 100, false, Type.TREE);
				InsolatorManager.addDefaultRecipe(saplingPersimmon, ItemHelper.cloneStack(itemPersimmon, 2), saplingPersimmon, 100, false, Type.TREE);
				InsolatorManager.addDefaultRecipe(saplingNutmeg, ItemHelper.cloneStack(itemNutmeg, 2), saplingNutmeg, 100, false, Type.TREE);
				InsolatorManager.addDefaultRecipe(saplingDurian, ItemHelper.cloneStack(itemDurian, 2), saplingDurian, 100, false, Type.TREE);
			}

			/* CENTRIFUGE */
			{
				int energy = 4000;

				CentrifugeManager.addRecipe(energy, honeycomb, Arrays.asList(honey, beeswax), null);
				CentrifugeManager.addRecipe(energy, waxcomb, Arrays.asList(ItemHelper.cloneStack(beeswax, 2)), null);
			}

			/* FISHER */
			{
				FisherManager.addFish(fishAnchovy, 20);
				FisherManager.addFish(fishBass, 20);
				FisherManager.addFish(fishCarp, 20);
				FisherManager.addFish(fishCatfish, 20);
				FisherManager.addFish(fishCharr, 20);
				FisherManager.addFish(fishEel, 20);
				FisherManager.addFish(fishGrouper, 20);
				FisherManager.addFish(fishHerring, 20);
				FisherManager.addFish(fishMudfish, 20);
				FisherManager.addFish(fishPerch, 20);
				FisherManager.addFish(fishSnapper, 20);
				FisherManager.addFish(fishTilapia, 20);
				FisherManager.addFish(fishTrout, 20);
				FisherManager.addFish(fishTuna, 20);
				FisherManager.addFish(fishWalleye, 20);
			}
		} catch (Throwable t) {
			ThermalExpansion.LOG.error("Thermal Expansion: " + MOD_NAME + " Plugin encountered an error:", t);
			error = true;
		}
		if (!error) {
			ThermalExpansion.LOG.info("Thermal Expansion: " + MOD_NAME + " Plugin Enabled.");
		}
		return !error;
	}

}
