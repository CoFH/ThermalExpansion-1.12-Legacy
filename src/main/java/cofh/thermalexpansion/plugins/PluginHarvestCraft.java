package cofh.thermalexpansion.plugins;

import cofh.core.util.ModPlugin;
import cofh.core.util.helpers.ItemHelper;
import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalexpansion.util.managers.device.FisherManager;
import cofh.thermalexpansion.util.managers.machine.CentrifugeManager;
import cofh.thermalexpansion.util.managers.machine.InsolatorManager;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Loader;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;

public class PluginHarvestCraft extends ModPlugin {

	public static final String MOD_ID = "harvestcraft";
	public static final String MOD_NAME = "HarvestCraft";

	public PluginHarvestCraft() {

		super(MOD_ID, MOD_NAME);
	}

	/* IInitializer */
	@Override
	public boolean initialize() {

		String category = "Plugins";
		String comment = "If TRUE, support for " + MOD_NAME + " is enabled.";
		enable = ThermalExpansion.CONFIG.getConfiguration().getBoolean(MOD_NAME, category, true, comment) && Loader.isModLoaded(MOD_ID);

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
				InsolatorManager.addDefaultTreeRecipe(saplingDate, ItemHelper.cloneStack(itemDate, 4), saplingDate);
				InsolatorManager.addDefaultTreeRecipe(saplingPapaya, ItemHelper.cloneStack(itemPapaya, 4), saplingPapaya);
				InsolatorManager.addDefaultTreeRecipe(saplingCherry, ItemHelper.cloneStack(itemCherry, 4), saplingCherry);
				InsolatorManager.addDefaultTreeRecipe(saplingFig, ItemHelper.cloneStack(itemFig, 4), saplingFig);
				InsolatorManager.addDefaultTreeRecipe(saplingDragonfruit, ItemHelper.cloneStack(itemDragonfruit, 4), saplingDragonfruit);
				InsolatorManager.addDefaultTreeRecipe(saplingApple, ItemHelper.cloneStack(Items.APPLE, 4), saplingApple);
				InsolatorManager.addDefaultTreeRecipe(saplingLemon, ItemHelper.cloneStack(itemLemon, 4), saplingLemon);
				InsolatorManager.addDefaultTreeRecipe(saplingPear, ItemHelper.cloneStack(itemPear, 4), saplingPear);
				InsolatorManager.addDefaultTreeRecipe(saplingOlive, ItemHelper.cloneStack(itemOlive, 4), saplingOlive);
				InsolatorManager.addDefaultTreeRecipe(saplingGrapefruit, ItemHelper.cloneStack(itemGrapefruit, 4), saplingGrapefruit);
				InsolatorManager.addDefaultTreeRecipe(saplingPomegranate, ItemHelper.cloneStack(itemPomegranate, 4), saplingPomegranate);
				InsolatorManager.addDefaultTreeRecipe(saplingCashew, ItemHelper.cloneStack(itemCashew, 4), saplingCashew);
				InsolatorManager.addDefaultTreeRecipe(saplingVanilla, ItemHelper.cloneStack(itemVanilla, 4), saplingVanilla);
				InsolatorManager.addDefaultTreeRecipe(saplingStarfruit, ItemHelper.cloneStack(itemStarfruit, 4), saplingStarfruit);
				InsolatorManager.addDefaultTreeRecipe(saplingBanana, ItemHelper.cloneStack(itemBanana, 4), saplingBanana);
				InsolatorManager.addDefaultTreeRecipe(saplingPlum, ItemHelper.cloneStack(itemPlum, 4), saplingPlum);
				InsolatorManager.addDefaultTreeRecipe(saplingAvocado, ItemHelper.cloneStack(itemAvocado, 4), saplingAvocado);
				InsolatorManager.addDefaultTreeRecipe(saplingPecan, ItemHelper.cloneStack(itemPecan, 4), saplingPecan);
				InsolatorManager.addDefaultTreeRecipe(saplingPistachio, ItemHelper.cloneStack(itemPistachio, 4), saplingPistachio);
				InsolatorManager.addDefaultTreeRecipe(saplingLime, ItemHelper.cloneStack(itemLime, 4), saplingLime);
				InsolatorManager.addDefaultTreeRecipe(saplingPeppercorn, ItemHelper.cloneStack(itemPeppercorn, 4), saplingPeppercorn);
				InsolatorManager.addDefaultTreeRecipe(saplingAlmond, ItemHelper.cloneStack(itemAlmond, 4), saplingAlmond);
				InsolatorManager.addDefaultTreeRecipe(saplingGooseberry, ItemHelper.cloneStack(itemGooseberry, 4), saplingGooseberry);
				InsolatorManager.addDefaultTreeRecipe(saplingPeach, ItemHelper.cloneStack(itemPeach, 4), saplingPeach);
				InsolatorManager.addDefaultTreeRecipe(saplingChestnut, ItemHelper.cloneStack(itemChestnut, 4), saplingChestnut);
				InsolatorManager.addDefaultTreeRecipe(saplingCoconut, ItemHelper.cloneStack(itemCoconut, 4), saplingCoconut);
				InsolatorManager.addDefaultTreeRecipe(saplingMango, ItemHelper.cloneStack(itemMango, 4), saplingMango);
				InsolatorManager.addDefaultTreeRecipe(saplingApricot, ItemHelper.cloneStack(itemApricot, 4), saplingApricot);
				InsolatorManager.addDefaultTreeRecipe(saplingOrange, ItemHelper.cloneStack(itemOrange, 4), saplingOrange);
				InsolatorManager.addDefaultTreeRecipe(saplingWalnut, ItemHelper.cloneStack(itemWalnut, 4), saplingWalnut);
				InsolatorManager.addDefaultTreeRecipe(saplingPersimmon, ItemHelper.cloneStack(itemPersimmon, 4), saplingPersimmon);
				InsolatorManager.addDefaultTreeRecipe(saplingNutmeg, ItemHelper.cloneStack(itemNutmeg, 4), saplingNutmeg);
				InsolatorManager.addDefaultTreeRecipe(saplingDurian, ItemHelper.cloneStack(itemDurian, 4), saplingDurian);
			}

			/* CENTRIFUGE */
			{
				int energy = 4000;

				CentrifugeManager.addRecipe(energy, honeycomb, asList(honey, beeswax), null);
				CentrifugeManager.addRecipe(energy, waxcomb, singletonList(ItemHelper.cloneStack(beeswax, 2)), null);
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
