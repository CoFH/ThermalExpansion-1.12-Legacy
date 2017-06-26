package cofh.thermalexpansion.plugins;

import cofh.lib.util.helpers.ItemHelper;
import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalexpansion.util.managers.TapperManager;
import cofh.thermalexpansion.util.managers.machine.CentrifugeManager;
import cofh.thermalexpansion.util.managers.machine.InsolatorManager;
import cofh.thermalexpansion.util.managers.machine.InsolatorManager.Type;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import java.util.Arrays;

public class HarvestcraftPlugin {

	private HarvestcraftPlugin() {

	}

	public static final String MOD_ID = "harvestcraft";
	public static final String MOD_NAME = "HarvestCraft";

	public static void initialize() {

		String category = "Plugins";
		String comment = "If TRUE, support for " + MOD_NAME + " is enabled.";

		boolean enable = ThermalExpansion.CONFIG.getConfiguration().getBoolean(MOD_NAME, category, true, comment);

		if (!enable || !Loader.isModLoaded(MOD_ID)) {
			return;
		}
		try {
			ItemStack beeswax = getItem("beeswaxitem");
			ItemStack honey = getItem("honeyitem");
			ItemStack honeycomb = getItem("honeycombitem");
			ItemStack waxcomb = getItem("waxcombitem");

			ItemStack cookingOil = getItem("oliveoilitem");
			ItemStack sesameOil = getItem("sesameoilitem");

			ItemStack fruitBait = getItem("fruitbaititem");
			ItemStack grainBait = getItem("grainbaititem");
			ItemStack veggieBait = getItem("veggiebaititem");
			ItemStack fishBait = getItem("fishtrapbaititem");

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

			ItemStack itemDate = getItem("dateitem");
			ItemStack itemPapaya = getItem("papayaitem");
			ItemStack itemCherry = getItem("cherryitem");
			ItemStack itemFig = getItem("figitem");
			ItemStack itemDragonfruit = getItem("dragonfruititem");
			ItemStack itemApple = getItem("appleitem");
			ItemStack itemLemon = getItem("lemonitem");
			ItemStack itemPear = getItem("pearitem");
			ItemStack itemOlive = getItem("oliveitem");
			ItemStack itemGrapefruit = getItem("grapefruititem");
			ItemStack itemPomegranate = getItem("pomegranateitem");
			ItemStack itemCashew = getItem("cashewitem");
			ItemStack itemVanilla = getItem("vanillaitem");
			ItemStack itemStarfruit = getItem("starfruititem");
			ItemStack itemBanana = getItem("bananaitem");
			ItemStack itemPlum = getItem("plumitem");
			ItemStack itemAvocado = getItem("avocadoitem");
			ItemStack itemPecan = getItem("pecanitem");
			ItemStack itemPistachio = getItem("pistachioitem");
			ItemStack itemLime = getItem("limeitem");
			ItemStack itemPeppercorn = getItem("peppercornitem");
			ItemStack itemAlmond = getItem("almonditem");
			ItemStack itemGooseberry = getItem("gooseberryitem");
			ItemStack itemPeach = getItem("peachitem");
			ItemStack itemChestnut = getItem("chestnutitem");
			ItemStack itemCoconut = getItem("coconutitem");
			ItemStack itemMango = getItem("mangoitem");
			ItemStack itemApricot = getItem("apricotitem");
			ItemStack itemOrange = getItem("orangeitem");
			ItemStack itemWalnut = getItem("walnutitem");
			ItemStack itemPersimmon = getItem("persimmonitem");
			ItemStack itemNutmeg = getItem("nutmegitem");
			ItemStack itemDurian = getItem("durianitem");

			/* INSOLATOR */
			{
				InsolatorManager.addDefaultRecipe(saplingDate, ItemHelper.cloneStack(itemDate, 2), saplingDate, 100, false, Type.TREE);
				InsolatorManager.addDefaultRecipe(saplingPapaya, ItemHelper.cloneStack(itemPapaya, 2), saplingPapaya, 100, false, Type.TREE);
				InsolatorManager.addDefaultRecipe(saplingCherry, ItemHelper.cloneStack(itemCherry, 2), saplingCherry, 100, false, Type.TREE);
				InsolatorManager.addDefaultRecipe(saplingFig, ItemHelper.cloneStack(itemFig, 2), saplingFig, 100, false, Type.TREE);
				InsolatorManager.addDefaultRecipe(saplingDragonfruit, ItemHelper.cloneStack(itemDragonfruit, 2), saplingDragonfruit, 100, false, Type.TREE);
				InsolatorManager.addDefaultRecipe(saplingApple, ItemHelper.cloneStack(itemApple, 2), saplingApple, 100, false, Type.TREE);
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

			ThermalExpansion.LOG.info("Thermal Expansion: " + MOD_NAME + " Plugin Enabled.");
		} catch (Throwable t) {
			ThermalExpansion.LOG.error("Thermal Expansion: " + MOD_NAME + " Plugin encountered an error:", t);
		}
	}

	/* HELPERS */
	private static ItemStack getBlockStack(String name, int amount, int meta) {

		Block block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(MOD_ID + ":" + name));
		return block != null ? new ItemStack(block, amount, meta) : ItemStack.EMPTY;
	}

	private static ItemStack getBlockStack(String name, int amount) {

		return getBlockStack(name, amount, 0);
	}

	private static ItemStack getBlockStack(String name) {

		return getBlockStack(name, 1, 0);
	}

	private static Block getBlock(String name) {

		return ForgeRegistries.BLOCKS.getValue(new ResourceLocation(MOD_ID + ":" + name));
	}

	private static ItemStack getItem(String name, int amount, int meta) {

		Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(MOD_ID + ":" + name));
		return item != null ? new ItemStack(item, amount, meta) : ItemStack.EMPTY;
	}

	private static ItemStack getItem(String name) {

		return getItem(name, 1, 0);
	}

	private static void addLeafMapping(Block logBlock, Block leafBlock, int metadata) {

		IBlockState logState = logBlock.getStateFromMeta(metadata);

		for (Boolean check_decay : BlockLeaves.CHECK_DECAY.getAllowedValues()) {
			IBlockState leafState = leafBlock.getStateFromMeta(metadata).withProperty(BlockLeaves.DECAYABLE, Boolean.TRUE).withProperty(BlockLeaves.CHECK_DECAY, check_decay);
			TapperManager.addLeafMappingDirect(logState, leafState);
		}
	}

}
