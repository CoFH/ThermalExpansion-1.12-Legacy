package cofh.thermalexpansion.plugins.pam;

import cofh.core.util.helpers.ItemHelper;
import cofh.thermalexpansion.plugins.PluginTEBase;
import cofh.thermalexpansion.util.managers.device.FisherManager;
import cofh.thermalexpansion.util.managers.machine.CentrifugeManager;
import cofh.thermalexpansion.util.managers.machine.InsolatorManager;
import cofh.thermalexpansion.util.managers.machine.PulverizerManager;
import cofh.thermalexpansion.util.managers.machine.SawmillManager;
import cofh.thermalfoundation.item.ItemMaterial;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;

public class PluginHarvestCraft extends PluginTEBase {

	public static final String MOD_ID = "harvestcraft";
	public static final String MOD_NAME = "Pam's HarvestCraft";

	public PluginHarvestCraft() {

		super(MOD_ID, MOD_NAME);
	}

	@Override
	public void initializeDelegate() {

		ItemStack beeswax = getItemStack("beeswaxitem");
		ItemStack honey = getItemStack("honeyitem");
		ItemStack honeycomb = getItemStack("honeycombitem");
		ItemStack waxcomb = getItemStack("waxcombitem");

		ItemStack oilCooking = getItemStack("oliveoilitem");
		ItemStack oilSesame = getItemStack("sesameoilitem");

		ItemStack milkCoconut = getItemStack("coconutmilkitem");
		ItemStack milkFresh = getItemStack("freshmilkitem");

		ItemStack baitFruit = getItemStack("fruitbaititem");
		ItemStack baitGrain = getItemStack("grainbaititem");
		ItemStack baitVeggie = getItemStack("veggiebaititem");
		ItemStack baitFish = getItemStack("fishtrapbaititem");

		// ItemStack itemApple = getItemStack("appleitem");
		ItemStack itemAlmond = getItemStack("almonditem");
		ItemStack itemApricot = getItemStack("apricotitem");
		ItemStack itemAvocado = getItemStack("avocadoitem");
		ItemStack itemBanana = getItemStack("bananaitem");
		ItemStack itemBeans = getItemStack("beanitem");
		ItemStack itemBeet = getItemStack("beetitem");
		ItemStack itemBlackberry = getItemStack("blackberryitem");
		ItemStack itemBlueberry = getItemStack("blueberryitem");
		ItemStack itemCactusfruit = getItemStack("cactusfruititem");
		ItemStack itemCashew = getItemStack("cashewitem");
		ItemStack itemCherry = getItemStack("cherryitem");
		ItemStack itemChestnut = getItemStack("chestnutitem");
		ItemStack itemCinnamon = getItemStack("cinnamonitem");
		ItemStack itemCoconut = getItemStack("coconutitem");
		ItemStack itemCranberry = getItemStack("cranberryitem");
		ItemStack itemDate = getItemStack("dateitem");
		ItemStack itemDragonfruit = getItemStack("dragonfruititem");
		ItemStack itemDurian = getItemStack("durianitem");
		ItemStack itemFig = getItemStack("figitem");
		ItemStack itemGooseberry = getItemStack("gooseberryitem");
		ItemStack itemGrape = getItemStack("grapeitem");
		ItemStack itemGrapefruit = getItemStack("grapefruititem");
		ItemStack itemKiwi = getItemStack("kiwiitem");
		ItemStack itemLemon = getItemStack("lemonitem");
		ItemStack itemLime = getItemStack("limeitem");
		ItemStack itemMango = getItemStack("mangoitem");
		ItemStack itemNutmeg = getItemStack("nutmegitem");
		ItemStack itemOlive = getItemStack("oliveitem");
		ItemStack itemOrange = getItemStack("orangeitem");
		ItemStack itemPapaya = getItemStack("papayaitem");
		ItemStack itemPeach = getItemStack("peachitem");
		ItemStack itemPear = getItemStack("pearitem");
		ItemStack itemPeas = getItemStack("peasitem");
		ItemStack itemPecan = getItemStack("pecanitem");
		ItemStack itemPeppercorn = getItemStack("peppercornitem");
		ItemStack itemPersimmon = getItemStack("persimmonitem");
		ItemStack itemPistachio = getItemStack("pistachioitem");
		ItemStack itemPlum = getItemStack("plumitem");
		ItemStack itemPomegranate = getItemStack("pomegranateitem");
		ItemStack itemPotato = new ItemStack(Items.POTATO);
		ItemStack itemRaspberry = getItemStack("raspberryitem");
		ItemStack itemSeaweed = getItemStack("seaweeditem");
		ItemStack itemSilkenTofu = getItemStack("silkentofuitem");
		ItemStack itemFirmTofu = getItemStack("firmtofuitem");
		ItemStack itemSoybean = getItemStack("soybeanitem");
		ItemStack itemSoyMilk = getItemStack("soymilkitem");
		ItemStack itemStarfruit = getItemStack("starfruititem");
		ItemStack itemStrawberry = getItemStack("strawberryitem");
		ItemStack itemTeaLeaf = getItemStack("tealeafitem");
		ItemStack itemVanilla = getItemStack("vanillaitem");
		ItemStack itemWalnut = getItemStack("walnutitem");

		ItemStack itemBarley = getItemStack("barleyitem");
		ItemStack itemOats = getItemStack("oatsitem");
		ItemStack itemRice = getItemStack("riceitem");
		ItemStack itemRye = getItemStack("ryeitem");
		ItemStack itemWheat = new ItemStack(Items.WHEAT);

		ItemStack seedBarley = getItemStack("barleyseeditem");
		ItemStack seedOats = getItemStack("oatsseeditem");
		ItemStack seedRice = getItemStack("riceseeditem");
		ItemStack seedRye = getItemStack("ryeseeditem");

		ItemStack rawBeef = new ItemStack(Items.BEEF);
		ItemStack rawCalamari = getItemStack("calamarirawitem");
		ItemStack rawChicken = new ItemStack(Items.CHICKEN);
		ItemStack rawDuck = getItemStack("rawduckitem");
		ItemStack rawMutton = getItemStack("rawmuttonitem");
		ItemStack rawPork = new ItemStack(Items.PORKCHOP);
		ItemStack rawRabbit = new ItemStack(Items.RABBIT);
		ItemStack rawTurkey = getItemStack("rawturkeyitem");
		ItemStack rawVenison = getItemStack("rawvenisonitem");

		ItemStack rawTofuBeef = getItemStack("rawtofeakitem");
		ItemStack rawTofuChicken = getItemStack("rawtofickenitem");
		ItemStack rawTofuDuck = getItemStack("rawtofuduckitem");
		ItemStack rawTofuFish = getItemStack("rawtofishitem");
		ItemStack rawTofuMutton = getItemStack("rawtofuttonitem");
		ItemStack rawTofuPork = getItemStack("rawtofaconitem");
		ItemStack rawTofuRabbit = getItemStack("rawtofabbititem");
		ItemStack rawTofuTurkey = getItemStack("rawtofurkeyitem");
		ItemStack rawTofuVenison = getItemStack("rawtofenisonitem");

		ItemStack groundBeef = getItemStack("groundbeefitem");
		ItemStack groundChicken = getItemStack("groundchickenitem");
		ItemStack groundDuck = getItemStack("groundduckitem");
		ItemStack groundFish = getItemStack("groundfishitem");
		ItemStack groundMutton = getItemStack("groundmuttonitem");
		ItemStack groundPork = getItemStack("groundporkitem");
		ItemStack groundRabbit = getItemStack("groundrabbititem");
		ItemStack groundTurkey = getItemStack("groundturkeyitem");
		ItemStack groundVenison = getItemStack("groundvenisonitem");

		ItemStack groundCinnamon = getItemStack("groundcinnamonitem");
		ItemStack groundNutmeg = getItemStack("groundnutmegitem");

		ItemStack itemFlour = getItemStack("flouritem");
		ItemStack itemPepper = getItemStack("blackpepperitem");
		ItemStack itemSalt = getItemStack("saltitem");

		ItemStack juiceApricot = getItemStack("apricotjuiceitem");
		ItemStack juiceBlackberry = getItemStack("blackberryjuiceitem");
		ItemStack juiceBlueberry = getItemStack("blueberryjuiceitem");
		ItemStack juiceCactusfruit = getItemStack("cactusfruitjuiceitem");
		ItemStack juiceCarrot = getItemStack("carrotjuiceitem");
		ItemStack juiceCherry = getItemStack("cherryjuiceitem");
		ItemStack juiceCranberry = getItemStack("cranberryjuiceitem");
		ItemStack juiceFig = getItemStack("figjuiceitem");
		ItemStack juiceGrape = getItemStack("grapejuiceitem");
		ItemStack juiceGrapefruit = getItemStack("grapefruitjuiceitem");
		ItemStack juiceKiwi = getItemStack("kiwijuiceitem");
		ItemStack juiceLime = getItemStack("limejuiceitem");
		ItemStack juiceMango = getItemStack("mangojuiceitem");
		ItemStack juiceMelon = getItemStack("melonjuiceitem");
		ItemStack juiceOrange = getItemStack("orangejuiceitem");
		ItemStack juicePapaya = getItemStack("papayajuiceitem");
		ItemStack juicePeach = getItemStack("peachjuiceitem");
		ItemStack juicePear = getItemStack("pearjuiceitem");
		ItemStack juicePersimmon = getItemStack("persimmonjuiceitem");
		ItemStack juicePlum = getItemStack("plumjuiceitem");
		ItemStack juicePomegranate = getItemStack("pomegranatejuiceitem");
		ItemStack juiceRaspberry = getItemStack("raspberryjuiceitem");
		ItemStack juiceStarfruit = getItemStack("starfruitjuiceitem");
		ItemStack juiceStrawberry = getItemStack("strawberryjuiceitem");

		ItemStack saplingAlmond = getItemStack("almond_sapling");
		ItemStack saplingApple = getItemStack("apple_sapling");
		ItemStack saplingApricot = getItemStack("apricot_sapling");
		ItemStack saplingAvocado = getItemStack("avocado_sapling");
		ItemStack saplingBanana = getItemStack("banana_sapling");
		ItemStack saplingCashew = getItemStack("cashew_sapling");
		ItemStack saplingCherry = getItemStack("cherry_sapling");
		ItemStack saplingChestnut = getItemStack("chestnut_sapling");
		ItemStack saplingCoconut = getItemStack("coconut_sapling");
		ItemStack saplingDate = getItemStack("date_sapling");
		ItemStack saplingDragonfruit = getItemStack("dragonfruit_sapling");
		ItemStack saplingDurian = getItemStack("durian_sapling");
		ItemStack saplingFig = getItemStack("fig_sapling");
		ItemStack saplingGooseberry = getItemStack("gooseberry_sapling");
		ItemStack saplingGrapefruit = getItemStack("grapefruit_sapling");
		ItemStack saplingLemon = getItemStack("lemon_sapling");
		ItemStack saplingLime = getItemStack("lime_sapling");
		ItemStack saplingMango = getItemStack("mango_sapling");
		ItemStack saplingNutmeg = getItemStack("nutmeg_sapling");
		ItemStack saplingOlive = getItemStack("olive_sapling");
		ItemStack saplingOrange = getItemStack("orange_sapling");
		ItemStack saplingPapaya = getItemStack("papaya_sapling");
		ItemStack saplingPeach = getItemStack("peach_sapling");
		ItemStack saplingPear = getItemStack("pear_sapling");
		ItemStack saplingPecan = getItemStack("pecan_sapling");
		ItemStack saplingPeppercorn = getItemStack("peppercorn_sapling");
		ItemStack saplingPersimmon = getItemStack("persimmon_sapling");
		ItemStack saplingPistachio = getItemStack("pistachio_sapling");
		ItemStack saplingPlum = getItemStack("plum_sapling");
		ItemStack saplingPomegranate = getItemStack("pomegranate_sapling");
		ItemStack saplingStarfruit = getItemStack("starfruit_sapling");
		ItemStack saplingVanilla = getItemStack("vanilla_sapling");
		ItemStack saplingWalnut = getItemStack("walnut_sapling");

		ItemStack seedCotton = getItemStack("cottonseeditem");
		ItemStack seedMustard = getItemStack("mustardseeditem");
		ItemStack seedSesame = getItemStack("sesameseedsseeditem");
		ItemStack seedSunflower = getItemStack("sunflowerseedsitem");

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
		ItemStack fishGreenHeart = getItemStack("greenheartfishitem");
		ItemStack fishSardine = getItemStack("sardinerawitem");

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

		/* PULVERIZER */
		{
			ItemStack groundFishStack = ItemHelper.cloneStack(groundFish, 2);
			ItemStack itemFlourStack = ItemHelper.cloneStack(itemFlour, 2);

			int energy = PulverizerManager.DEFAULT_ENERGY / 4;

			PulverizerManager.addRecipe(energy, itemCinnamon, groundCinnamon, baitGrain);
			PulverizerManager.addRecipe(energy, itemNutmeg, groundNutmeg, baitGrain);

			PulverizerManager.addRecipe(energy, itemPeppercorn, itemPepper, baitVeggie);
			PulverizerManager.addRecipe(energy, itemSeaweed, itemSalt, baitVeggie);

			PulverizerManager.addRecipe(energy, rawBeef, ItemHelper.cloneStack(groundBeef, 2));
			PulverizerManager.addRecipe(energy, rawCalamari, groundFishStack);
			PulverizerManager.addRecipe(energy, rawChicken, ItemHelper.cloneStack(groundChicken, 2));
			PulverizerManager.addRecipe(energy, rawDuck, ItemHelper.cloneStack(groundDuck, 2));
			PulverizerManager.addRecipe(energy, rawMutton, ItemHelper.cloneStack(groundMutton, 2));
			PulverizerManager.addRecipe(energy, rawPork, ItemHelper.cloneStack(groundPork, 2));
			PulverizerManager.addRecipe(energy, rawRabbit, ItemHelper.cloneStack(groundRabbit, 2));
			PulverizerManager.addRecipe(energy, rawTurkey, ItemHelper.cloneStack(groundTurkey, 2));
			PulverizerManager.addRecipe(energy, rawVenison, ItemHelper.cloneStack(groundVenison, 2));

			PulverizerManager.addRecipe(energy, rawTofuBeef, ItemHelper.cloneStack(groundBeef, 2));
			PulverizerManager.addRecipe(energy, rawTofuChicken, ItemHelper.cloneStack(groundChicken, 2));
			PulverizerManager.addRecipe(energy, rawTofuDuck, ItemHelper.cloneStack(groundDuck, 2));
			PulverizerManager.addRecipe(energy, rawTofuFish, groundFishStack);
			PulverizerManager.addRecipe(energy, rawTofuMutton, ItemHelper.cloneStack(groundMutton, 2));
			PulverizerManager.addRecipe(energy, rawTofuPork, ItemHelper.cloneStack(groundPork, 2));
			PulverizerManager.addRecipe(energy, rawTofuRabbit, ItemHelper.cloneStack(groundRabbit, 2));
			PulverizerManager.addRecipe(energy, rawTofuTurkey, ItemHelper.cloneStack(groundTurkey, 2));
			PulverizerManager.addRecipe(energy, rawTofuVenison, ItemHelper.cloneStack(groundVenison, 2));

			PulverizerManager.addRecipe(energy, new ItemStack(Items.FISH, 1, 0), groundFishStack);
			PulverizerManager.addRecipe(energy, new ItemStack(Items.FISH, 1, 1), groundFishStack);
			PulverizerManager.addRecipe(energy, new ItemStack(Items.FISH, 1, 2), groundFishStack);
			PulverizerManager.addRecipe(energy, new ItemStack(Items.FISH, 1, 3), groundFishStack);

			PulverizerManager.addRecipe(energy, itemAlmond, itemFlourStack);
			PulverizerManager.addRecipe(energy, itemBanana, itemFlourStack);
			PulverizerManager.addRecipe(energy, itemBarley, itemFlourStack);
			PulverizerManager.addRecipe(energy, itemBeans, itemFlourStack);
			PulverizerManager.addRecipe(energy, itemChestnut, itemFlourStack);
			PulverizerManager.addRecipe(energy, itemCoconut, itemFlourStack);
			PulverizerManager.addRecipe(energy, itemOats, itemFlourStack);
			PulverizerManager.addRecipe(energy, itemPeas, itemFlourStack);
			PulverizerManager.addRecipe(energy, itemPotato, itemFlourStack);
			PulverizerManager.addRecipe(energy, itemRice, itemFlourStack);
			PulverizerManager.addRecipe(energy, itemRye, itemFlourStack);
			PulverizerManager.addRecipe(energy, itemSoybean, itemFlourStack);
			PulverizerManager.addRecipe(energy, itemWheat, itemFlourStack);

			PulverizerManager.addRecipe(energy, fishAnchovy, groundFishStack);
			PulverizerManager.addRecipe(energy, fishBass, groundFishStack);
			PulverizerManager.addRecipe(energy, fishCarp, groundFishStack);
			PulverizerManager.addRecipe(energy, fishCatfish, groundFishStack);
			PulverizerManager.addRecipe(energy, fishCharr, groundFishStack);
			PulverizerManager.addRecipe(energy, fishGrouper, groundFishStack);
			PulverizerManager.addRecipe(energy, fishHerring, groundFishStack);
			PulverizerManager.addRecipe(energy, fishMudfish, groundFishStack);
			PulverizerManager.addRecipe(energy, fishPerch, groundFishStack);
			PulverizerManager.addRecipe(energy, fishSnapper, groundFishStack);
			PulverizerManager.addRecipe(energy, fishTilapia, groundFishStack);
			PulverizerManager.addRecipe(energy, fishTrout, groundFishStack);
			PulverizerManager.addRecipe(energy, fishTuna, groundFishStack);
			PulverizerManager.addRecipe(energy, fishWalleye, groundFishStack);

			PulverizerManager.addRecipe(energy, fishGreenHeart, groundFishStack);
			PulverizerManager.addRecipe(energy, fishSardine, groundFishStack);
		}

		/* SAWMILL */
		{
			int energy = SawmillManager.DEFAULT_ENERGY;

			SawmillManager.addRecipe(energy, ItemHelper.cloneStack(itemBarley, 8), ItemMaterial.dustBiomass, seedBarley, 50);
			SawmillManager.addRecipe(energy, ItemHelper.cloneStack(itemOats, 8), ItemMaterial.dustBiomass, seedOats, 50);
			SawmillManager.addRecipe(energy, ItemHelper.cloneStack(itemRice, 8), ItemMaterial.dustBiomass, seedRice, 50);
			SawmillManager.addRecipe(energy, ItemHelper.cloneStack(itemRye, 8), ItemMaterial.dustBiomass, seedRye, 50);
		}

		/* INSOLATOR */
		{
			InsolatorManager.addDefaultTreeRecipe(saplingAlmond, ItemHelper.cloneStack(itemAlmond, 4), saplingAlmond);
			InsolatorManager.addDefaultTreeRecipe(saplingApple, ItemHelper.cloneStack(Items.APPLE, 4), saplingApple);
			InsolatorManager.addDefaultTreeRecipe(saplingApricot, ItemHelper.cloneStack(itemApricot, 4), saplingApricot);
			InsolatorManager.addDefaultTreeRecipe(saplingAvocado, ItemHelper.cloneStack(itemAvocado, 4), saplingAvocado);
			InsolatorManager.addDefaultTreeRecipe(saplingBanana, ItemHelper.cloneStack(itemBanana, 4), saplingBanana);
			InsolatorManager.addDefaultTreeRecipe(saplingCashew, ItemHelper.cloneStack(itemCashew, 4), saplingCashew);
			InsolatorManager.addDefaultTreeRecipe(saplingCherry, ItemHelper.cloneStack(itemCherry, 4), saplingCherry);
			InsolatorManager.addDefaultTreeRecipe(saplingChestnut, ItemHelper.cloneStack(itemChestnut, 4), saplingChestnut);
			InsolatorManager.addDefaultTreeRecipe(saplingCoconut, ItemHelper.cloneStack(itemCoconut, 4), saplingCoconut);
			InsolatorManager.addDefaultTreeRecipe(saplingDate, ItemHelper.cloneStack(itemDate, 4), saplingDate);
			InsolatorManager.addDefaultTreeRecipe(saplingDragonfruit, ItemHelper.cloneStack(itemDragonfruit, 4), saplingDragonfruit);
			InsolatorManager.addDefaultTreeRecipe(saplingDurian, ItemHelper.cloneStack(itemDurian, 4), saplingDurian);
			InsolatorManager.addDefaultTreeRecipe(saplingFig, ItemHelper.cloneStack(itemFig, 4), saplingFig);
			InsolatorManager.addDefaultTreeRecipe(saplingGooseberry, ItemHelper.cloneStack(itemGooseberry, 4), saplingGooseberry);
			InsolatorManager.addDefaultTreeRecipe(saplingGrapefruit, ItemHelper.cloneStack(itemGrapefruit, 4), saplingGrapefruit);
			InsolatorManager.addDefaultTreeRecipe(saplingLemon, ItemHelper.cloneStack(itemLemon, 4), saplingLemon);
			InsolatorManager.addDefaultTreeRecipe(saplingLime, ItemHelper.cloneStack(itemLime, 4), saplingLime);
			InsolatorManager.addDefaultTreeRecipe(saplingMango, ItemHelper.cloneStack(itemMango, 4), saplingMango);
			InsolatorManager.addDefaultTreeRecipe(saplingNutmeg, ItemHelper.cloneStack(itemNutmeg, 4), saplingNutmeg);
			InsolatorManager.addDefaultTreeRecipe(saplingOlive, ItemHelper.cloneStack(itemOlive, 4), saplingOlive);
			InsolatorManager.addDefaultTreeRecipe(saplingOrange, ItemHelper.cloneStack(itemOrange, 4), saplingOrange);
			InsolatorManager.addDefaultTreeRecipe(saplingPapaya, ItemHelper.cloneStack(itemPapaya, 4), saplingPapaya);
			InsolatorManager.addDefaultTreeRecipe(saplingPeach, ItemHelper.cloneStack(itemPeach, 4), saplingPeach);
			InsolatorManager.addDefaultTreeRecipe(saplingPear, ItemHelper.cloneStack(itemPear, 4), saplingPear);
			InsolatorManager.addDefaultTreeRecipe(saplingPecan, ItemHelper.cloneStack(itemPecan, 4), saplingPecan);
			InsolatorManager.addDefaultTreeRecipe(saplingPeppercorn, ItemHelper.cloneStack(itemPeppercorn, 4), saplingPeppercorn);
			InsolatorManager.addDefaultTreeRecipe(saplingPersimmon, ItemHelper.cloneStack(itemPersimmon, 4), saplingPersimmon);
			InsolatorManager.addDefaultTreeRecipe(saplingPistachio, ItemHelper.cloneStack(itemPistachio, 4), saplingPistachio);
			InsolatorManager.addDefaultTreeRecipe(saplingPlum, ItemHelper.cloneStack(itemPlum, 4), saplingPlum);
			InsolatorManager.addDefaultTreeRecipe(saplingPomegranate, ItemHelper.cloneStack(itemPomegranate, 4), saplingPomegranate);
			InsolatorManager.addDefaultTreeRecipe(saplingStarfruit, ItemHelper.cloneStack(itemStarfruit, 4), saplingStarfruit);
			InsolatorManager.addDefaultTreeRecipe(saplingVanilla, ItemHelper.cloneStack(itemVanilla, 4), saplingVanilla);
			InsolatorManager.addDefaultTreeRecipe(saplingWalnut, ItemHelper.cloneStack(itemWalnut, 4), saplingWalnut);
		}

		/* CENTRIFUGE */
		{
			int energy = 4000;

			CentrifugeManager.addRecipe(energy, honeycomb, asList(honey, beeswax), null);
			CentrifugeManager.addRecipe(energy, waxcomb, singletonList(ItemHelper.cloneStack(beeswax, 2)), null);

			CentrifugeManager.addRecipe(energy, itemApricot, asList(juiceApricot, baitFruit), null);
			CentrifugeManager.addRecipe(energy, itemBlackberry, asList(juiceBlackberry, baitFruit), null);
			CentrifugeManager.addRecipe(energy, itemBlueberry, asList(juiceBlueberry, baitFruit), null);
			CentrifugeManager.addRecipe(energy, itemCactusfruit, asList(juiceCactusfruit, baitFruit), null);
			CentrifugeManager.addRecipe(energy, itemCherry, asList(juiceCherry, baitFruit), null);
			CentrifugeManager.addRecipe(energy, itemCranberry, asList(juiceCranberry, baitFruit), null);
			CentrifugeManager.addRecipe(energy, itemFig, asList(juiceFig, baitFruit), null);
			CentrifugeManager.addRecipe(energy, itemGrape, asList(juiceGrape, baitFruit), null);
			CentrifugeManager.addRecipe(energy, itemGrapefruit, asList(juiceGrapefruit, baitFruit), null);
			CentrifugeManager.addRecipe(energy, itemKiwi, asList(juiceKiwi, baitFruit), null);
			CentrifugeManager.addRecipe(energy, itemLime, asList(juiceLime, baitFruit), null);
			CentrifugeManager.addRecipe(energy, itemMango, asList(juiceMango, baitFruit), null);
			CentrifugeManager.addRecipe(energy, itemOlive, asList(oilCooking, baitFruit), null);
			CentrifugeManager.addRecipe(energy, itemOrange, asList(juiceOrange, baitFruit), null);
			CentrifugeManager.addRecipe(energy, itemPapaya, asList(juicePapaya, baitFruit), null);
			CentrifugeManager.addRecipe(energy, itemPeach, asList(juicePeach, baitFruit), null);
			CentrifugeManager.addRecipe(energy, itemPear, asList(juicePear, baitFruit), null);
			CentrifugeManager.addRecipe(energy, itemPersimmon, asList(juicePersimmon, baitFruit), null);
			CentrifugeManager.addRecipe(energy, itemPlum, asList(juicePlum, baitFruit), null);
			CentrifugeManager.addRecipe(energy, itemPomegranate, asList(juicePomegranate, baitFruit), null);
			CentrifugeManager.addRecipe(energy, itemRaspberry, asList(juiceRaspberry, baitFruit), null);
			CentrifugeManager.addRecipe(energy, itemStarfruit, asList(juiceStarfruit, baitFruit), null);
			CentrifugeManager.addRecipe(energy, itemStrawberry, asList(juiceStrawberry, baitFruit), null);
			CentrifugeManager.addRecipe(energy, new ItemStack(Items.MELON), asList(juiceMelon, baitFruit), null);

			CentrifugeManager.addRecipe(energy, itemAvocado, asList(oilCooking, baitVeggie), null);
			CentrifugeManager.addRecipe(energy, itemBeet, asList(new ItemStack(Items.SUGAR), baitVeggie), null);
			CentrifugeManager.addRecipe(energy, itemCoconut, asList(milkCoconut, baitVeggie), null);
			CentrifugeManager.addRecipe(energy, new ItemStack(Blocks.PUMPKIN), asList(oilCooking, baitVeggie), null);
			CentrifugeManager.addRecipe(energy, new ItemStack(Items.CARROT), asList(juiceCarrot, baitVeggie), null);

			CentrifugeManager.addRecipe(energy, itemAlmond, asList(milkFresh, baitGrain), null);
			CentrifugeManager.addRecipe(energy, itemSoybean, asList(itemSilkenTofu, baitGrain), null);
			CentrifugeManager.addRecipe(energy, itemSilkenTofu, asList(itemFirmTofu, itemSoyMilk), null);
			CentrifugeManager.addRecipe(energy, itemTeaLeaf, asList(oilCooking, baitGrain), null);
			CentrifugeManager.addRecipe(energy, itemWalnut, asList(oilCooking, baitGrain), null);
			CentrifugeManager.addRecipe(energy, new ItemStack(Items.PUMPKIN_SEEDS), asList(oilCooking, baitGrain), null);

			CentrifugeManager.addRecipe(energy, seedCotton, asList(oilCooking, baitGrain), null);
			CentrifugeManager.addRecipe(energy, seedMustard, asList(oilCooking, baitGrain), null);
			CentrifugeManager.addRecipe(energy, seedSesame, asList(oilSesame, baitGrain), null);
			CentrifugeManager.addRecipe(energy, seedSunflower, asList(oilCooking, baitGrain), null);
		}

		/* FISHER */
		{
			FisherManager.addBait(baitFish, 2);

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
			FisherManager.addFish(fishGreenHeart, 20);
			FisherManager.addFish(fishSardine, 20);
		}
	}

}
