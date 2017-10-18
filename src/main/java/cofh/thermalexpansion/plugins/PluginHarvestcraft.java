package cofh.thermalexpansion.plugins;

import cofh.core.util.ModPlugin;
import cofh.core.util.helpers.ItemHelper;
import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalexpansion.util.managers.FisherManager;
import cofh.thermalexpansion.util.managers.machine.CentrifugeManager;
import cofh.thermalexpansion.util.managers.machine.InsolatorManager;
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

			ItemStack saplingDate = getItemStack("date_sapling");
			ItemStack saplingPapaya = getItemStack("papaya_sapling");
			ItemStack saplingCherry = getItemStack("cherry_sapling");
			ItemStack saplingFig = getItemStack("fig_sapling");
			ItemStack saplingDragonfruit = getItemStack("dragonfruit_sapling");
			ItemStack saplingApple = getItemStack("apple_sapling");
			ItemStack saplingLemon = getItemStack("lemon_sapling");
			ItemStack saplingPear = getItemStack("pear_sapling");
			ItemStack saplingOlive = getItemStack("olive_sapling");
			ItemStack saplingGrapefruit = getItemStack("grapefruit_sapling");
			ItemStack saplingPomegranate = getItemStack("pomegranate_sapling");
			ItemStack saplingCashew = getItemStack("cashew_sapling");
			ItemStack saplingVanilla = getItemStack("vanilla_sapling");
			ItemStack saplingStarfruit = getItemStack("starfruit_sapling");
			ItemStack saplingBanana = getItemStack("banana_sapling");
			ItemStack saplingPlum = getItemStack("plum_sapling");
			ItemStack saplingAvocado = getItemStack("avocado_sapling");
			ItemStack saplingPecan = getItemStack("pecan_sapling");
			ItemStack saplingPistachio = getItemStack("pistachio_sapling");
			ItemStack saplingLime = getItemStack("lime_sapling");
			ItemStack saplingPeppercorn = getItemStack("peppercorn_sapling");
			ItemStack saplingAlmond = getItemStack("almond_sapling");
			ItemStack saplingGooseberry = getItemStack("gooseberry_sapling");
			ItemStack saplingPeach = getItemStack("peach_sapling");
			ItemStack saplingChestnut = getItemStack("chestnut_sapling");
			ItemStack saplingCoconut = getItemStack("coconut_sapling");
			ItemStack saplingMango = getItemStack("mango_sapling");
			ItemStack saplingApricot = getItemStack("apricot_sapling");
			ItemStack saplingOrange = getItemStack("orange_sapling");
			ItemStack saplingWalnut = getItemStack("walnut_sapling");
			ItemStack saplingPersimmon = getItemStack("persimmon_sapling");
			ItemStack saplingNutmeg = getItemStack("nutmeg_sapling");
			ItemStack saplingDurian = getItemStack("durian_sapling");

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
				InsolatorManager.addDefaultTreeRecipe(saplingDate, ItemHelper.cloneStack(itemDate, 2), saplingDate, 100);
				InsolatorManager.addDefaultTreeRecipe(saplingPapaya, ItemHelper.cloneStack(itemPapaya, 2), saplingPapaya, 100);
				InsolatorManager.addDefaultTreeRecipe(saplingCherry, ItemHelper.cloneStack(itemCherry, 2), saplingCherry, 100);
				InsolatorManager.addDefaultTreeRecipe(saplingFig, ItemHelper.cloneStack(itemFig, 2), saplingFig, 100);
				InsolatorManager.addDefaultTreeRecipe(saplingDragonfruit, ItemHelper.cloneStack(itemDragonfruit, 2), saplingDragonfruit, 100);
				InsolatorManager.addDefaultTreeRecipe(saplingApple, ItemHelper.cloneStack(Items.APPLE, 2), saplingApple, 100);
				InsolatorManager.addDefaultTreeRecipe(saplingLemon, ItemHelper.cloneStack(itemLemon, 2), saplingLemon, 100);
				InsolatorManager.addDefaultTreeRecipe(saplingPear, ItemHelper.cloneStack(itemPear, 2), saplingPear, 100);
				InsolatorManager.addDefaultTreeRecipe(saplingOlive, ItemHelper.cloneStack(itemOlive, 2), saplingOlive, 100);
				InsolatorManager.addDefaultTreeRecipe(saplingGrapefruit, ItemHelper.cloneStack(itemGrapefruit, 2), saplingGrapefruit, 100);
				InsolatorManager.addDefaultTreeRecipe(saplingPomegranate, ItemHelper.cloneStack(itemPomegranate, 2), saplingPomegranate, 100);
				InsolatorManager.addDefaultTreeRecipe(saplingCashew, ItemHelper.cloneStack(itemCashew, 2), saplingCashew, 100);
				InsolatorManager.addDefaultTreeRecipe(saplingVanilla, ItemHelper.cloneStack(itemVanilla, 2), saplingVanilla, 100);
				InsolatorManager.addDefaultTreeRecipe(saplingStarfruit, ItemHelper.cloneStack(itemStarfruit, 2), saplingStarfruit, 100);
				InsolatorManager.addDefaultTreeRecipe(saplingBanana, ItemHelper.cloneStack(itemBanana, 2), saplingBanana, 100);
				InsolatorManager.addDefaultTreeRecipe(saplingPlum, ItemHelper.cloneStack(itemPlum, 2), saplingPlum, 100);
				InsolatorManager.addDefaultTreeRecipe(saplingAvocado, ItemHelper.cloneStack(itemAvocado, 2), saplingAvocado, 100);
				InsolatorManager.addDefaultTreeRecipe(saplingPecan, ItemHelper.cloneStack(itemPecan, 2), saplingPecan, 100);
				InsolatorManager.addDefaultTreeRecipe(saplingPistachio, ItemHelper.cloneStack(itemPistachio, 2), saplingPistachio, 100);
				InsolatorManager.addDefaultTreeRecipe(saplingLime, ItemHelper.cloneStack(itemLime, 2), saplingLime, 100);
				InsolatorManager.addDefaultTreeRecipe(saplingPeppercorn, ItemHelper.cloneStack(itemPeppercorn, 2), saplingPeppercorn, 100);
				InsolatorManager.addDefaultTreeRecipe(saplingAlmond, ItemHelper.cloneStack(itemAlmond, 2), saplingAlmond, 100);
				InsolatorManager.addDefaultTreeRecipe(saplingGooseberry, ItemHelper.cloneStack(itemGooseberry, 2), saplingGooseberry, 100);
				InsolatorManager.addDefaultTreeRecipe(saplingPeach, ItemHelper.cloneStack(itemPeach, 2), saplingPeach, 100);
				InsolatorManager.addDefaultTreeRecipe(saplingChestnut, ItemHelper.cloneStack(itemChestnut, 2), saplingChestnut, 100);
				InsolatorManager.addDefaultTreeRecipe(saplingCoconut, ItemHelper.cloneStack(itemCoconut, 2), saplingCoconut, 100);
				InsolatorManager.addDefaultTreeRecipe(saplingMango, ItemHelper.cloneStack(itemMango, 2), saplingMango, 100);
				InsolatorManager.addDefaultTreeRecipe(saplingApricot, ItemHelper.cloneStack(itemApricot, 2), saplingApricot, 100);
				InsolatorManager.addDefaultTreeRecipe(saplingOrange, ItemHelper.cloneStack(itemOrange, 2), saplingOrange, 100);
				InsolatorManager.addDefaultTreeRecipe(saplingWalnut, ItemHelper.cloneStack(itemWalnut, 2), saplingWalnut, 100);
				InsolatorManager.addDefaultTreeRecipe(saplingPersimmon, ItemHelper.cloneStack(itemPersimmon, 2), saplingPersimmon, 100);
				InsolatorManager.addDefaultTreeRecipe(saplingNutmeg, ItemHelper.cloneStack(itemNutmeg, 2), saplingNutmeg, 100);
				InsolatorManager.addDefaultTreeRecipe(saplingDurian, ItemHelper.cloneStack(itemDurian, 2), saplingDurian, 100);
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
