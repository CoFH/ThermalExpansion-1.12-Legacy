package cofh.thermalexpansion.plugins;

import cofh.core.util.helpers.ItemHelper;
import cofh.thermalexpansion.util.managers.machine.CentrifugeManager;
import cofh.thermalexpansion.util.managers.machine.InsolatorManager;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;

public class PluginTwilightForest extends PluginTEBase {

	public static final String MOD_ID = "twilightforest";
	public static final String MOD_NAME = "Twilight Forest";

	public PluginTwilightForest() {

		super(MOD_ID, MOD_NAME);
	}

	@Override
	public void registerDelegate() {

		ItemStack plantMossPatch = getItemStack("twilight_plant", 1, 0);
		ItemStack plantMayapple = getItemStack("twilight_plant", 1, 1);
		ItemStack plantCloverPatch = getItemStack("twilight_plant", 1, 2);
		ItemStack plantFiddlehead = getItemStack("twilight_plant", 1, 3);
		ItemStack plantMushgloom = getItemStack("twilight_plant", 1, 4);

		ItemStack saplingTwilightOak = getItemStack("twilight_sapling", 1, 0);
		ItemStack saplingCanopy = getItemStack("twilight_sapling", 1, 1);
		ItemStack saplingMangrove = getItemStack("twilight_sapling", 1, 2);
		ItemStack saplingDarkwood = getItemStack("twilight_sapling", 1, 3);

		// ItemStack saplingTwilightOakRobust = getItemStack("twilight_sapling", 1, 4);

		ItemStack saplingTimewood = getItemStack("twilight_sapling", 1, 5);
		ItemStack saplingTranswood = getItemStack("twilight_sapling", 1, 6);
		ItemStack saplingMinewood = getItemStack("twilight_sapling", 1, 7);
		ItemStack saplingSortingwood = getItemStack("twilight_sapling", 1, 8);

		ItemStack saplingRainbow = getItemStack("twilight_sapling", 1, 9);

		ItemStack logTwilightOak = getItemStack("twilight_log", 1, 0);
		ItemStack logCanopy = getItemStack("twilight_log", 1, 1);
		ItemStack logMangrove = getItemStack("twilight_log", 1, 2);
		ItemStack logDarkwood = getItemStack("twilight_log", 1, 3);

		ItemStack logTimewood = getItemStack("magic_log", 1, 0);
		ItemStack logTranswood = getItemStack("magic_log", 1, 1);
		ItemStack logMinewood = getItemStack("magic_log", 1, 2);
		ItemStack logSortingwood = getItemStack("magic_log", 1, 3);

		/* INSOLATOR */
		{
			int energy = InsolatorManager.DEFAULT_ENERGY;

			InsolatorManager.addDefaultRecipe(plantMossPatch, ItemHelper.cloneStack(plantMossPatch, 3), ItemStack.EMPTY, 0);
			InsolatorManager.addDefaultRecipe(plantMayapple, ItemHelper.cloneStack(plantMayapple, 3), ItemStack.EMPTY, 0);
			InsolatorManager.addDefaultRecipe(plantCloverPatch, ItemHelper.cloneStack(plantCloverPatch, 3), ItemStack.EMPTY, 0);
			InsolatorManager.addDefaultRecipe(plantFiddlehead, ItemHelper.cloneStack(plantFiddlehead, 3), ItemStack.EMPTY, 0);

			InsolatorManager.addDefaultRecipe(plantMushgloom, ItemHelper.cloneStack(plantMushgloom, 2), ItemStack.EMPTY, 0);

			InsolatorManager.addDefaultTreeRecipe(saplingTwilightOak, ItemHelper.cloneStack(logTwilightOak, 6), saplingTwilightOak);
			InsolatorManager.addDefaultTreeRecipe(saplingCanopy, ItemHelper.cloneStack(logCanopy, 6), saplingCanopy);
			InsolatorManager.addDefaultTreeRecipe(saplingMangrove, ItemHelper.cloneStack(logMangrove, 6), saplingMangrove);
			InsolatorManager.addDefaultTreeRecipe(saplingDarkwood, ItemHelper.cloneStack(logDarkwood, 6), saplingDarkwood);

			InsolatorManager.addDefaultTreeRecipe(energy * 2, saplingTimewood, ItemHelper.cloneStack(logTimewood, 6), saplingTimewood, 30);
			InsolatorManager.addDefaultTreeRecipe(energy * 2, saplingTranswood, ItemHelper.cloneStack(logTranswood, 6), saplingTranswood, 30);
			InsolatorManager.addDefaultTreeRecipe(energy * 2, saplingMinewood, ItemHelper.cloneStack(logMinewood, 6), saplingMinewood, 30);
			InsolatorManager.addDefaultTreeRecipe(energy * 2, saplingSortingwood, ItemHelper.cloneStack(logSortingwood, 6), saplingSortingwood, 30);

			InsolatorManager.addDefaultTreeRecipe(saplingRainbow, ItemHelper.cloneStack(logTwilightOak, 6), saplingRainbow);
		}

		/* CENTRIFUGE */
		{
			/* ANIMALS */
			CentrifugeManager.addDefaultMobRecipe(MOD_ID + ":wild_boar", singletonList(new ItemStack(Items.PORKCHOP, 3)), singletonList(70), 2);
			CentrifugeManager.addDefaultMobRecipe(MOD_ID + ":bighorn_sheep", singletonList(new ItemStack(Items.MUTTON, 2)), singletonList(80), 2);
			CentrifugeManager.addDefaultMobRecipe(MOD_ID + ":bunny", asList(new ItemStack(Items.RABBIT_HIDE), new ItemStack(Items.RABBIT), new ItemStack(Items.RABBIT_FOOT)), asList(50, 50, 10), 2);
			CentrifugeManager.addDefaultMobRecipe(MOD_ID + ":deer", asList(new ItemStack(Items.LEATHER, 2), getItemStack("raw_venison", 3, 0)), asList(50, 70), 2);
			CentrifugeManager.addDefaultMobRecipe(MOD_ID + ":firefly", singletonList(new ItemStack(Items.GLOWSTONE_DUST)), singletonList(5), 0);
			CentrifugeManager.addDefaultMobRecipe(MOD_ID + ":penguin", singletonList(new ItemStack(Items.FEATHER, 2)), singletonList(50), 2);
			CentrifugeManager.addDefaultMobRecipe(MOD_ID + ":raven", singletonList(getItemStack("raven_feather", 2, 0)), singletonList(50), 2);
			CentrifugeManager.addDefaultMobRecipe(MOD_ID + ":squirrel", emptyList(), emptyList(), 2);
			CentrifugeManager.addDefaultMobRecipe(MOD_ID + ":tiny_bird", singletonList(new ItemStack(Items.FEATHER, 2)), singletonList(50), 2);

			/* MOBS*/
			CentrifugeManager.addDefaultMobRecipe(MOD_ID + ":armored_giant", singletonList(getItemStack("giant_sword")), singletonList(50), 5);
			CentrifugeManager.addDefaultMobRecipe(MOD_ID + ":blockchain_goblin", singletonList(getItemStack("armor_shard", 2, 0)), singletonList(50), 5);
			CentrifugeManager.addDefaultMobRecipe(MOD_ID + ":death_tome", asList(new ItemStack(Items.PAPER, 3), new ItemStack(Items.BOOK)), asList(90, 80), 5);
			CentrifugeManager.addDefaultMobRecipe(MOD_ID + ":fire_beetle", singletonList(new ItemStack(Items.GUNPOWDER, 2)), singletonList(50), 5);
			CentrifugeManager.addDefaultMobRecipe(MOD_ID + ":giant_miner", singletonList(getItemStack("giant_pickaxe")), singletonList(50), 5);
			CentrifugeManager.addDefaultMobRecipe(MOD_ID + ":goblin_knight_lower", singletonList(getItemStack("armor_shard", 2, 0)), singletonList(50), 5);
			CentrifugeManager.addDefaultMobRecipe(MOD_ID + ":hedge_spider", asList(new ItemStack(Items.STRING, 2), new ItemStack(Items.SPIDER_EYE)), asList(50, 25), 5);
			CentrifugeManager.addDefaultMobRecipe(MOD_ID + ":helmet_crab", asList(getItemStack("armor_shard", 2, 0), new ItemStack(Items.FISH)), asList(50, 25), 5);
			CentrifugeManager.addDefaultMobRecipe(MOD_ID + ":hostile_wolf", emptyList(), emptyList(), 2);
			CentrifugeManager.addDefaultMobRecipe(MOD_ID + ":ice_crystal", singletonList(new ItemStack(Items.SNOWBALL, 2)), singletonList(50), 5);
			CentrifugeManager.addDefaultMobRecipe(MOD_ID + ":king_spider", asList(new ItemStack(Items.STRING, 2), new ItemStack(Items.SPIDER_EYE)), asList(50, 25), 5);
			CentrifugeManager.addDefaultMobRecipe(MOD_ID + ":kobold", asList(new ItemStack(Items.WHEAT, 2), new ItemStack(Items.GOLD_NUGGET)), asList(50, 25), 5);
			CentrifugeManager.addDefaultMobRecipe(MOD_ID + ":mini_ghast", asList(new ItemStack(Items.GHAST_TEAR), new ItemStack(Items.GUNPOWDER)), asList(50, 50), 5);
			CentrifugeManager.addDefaultMobRecipe(MOD_ID + ":minotaur", singletonList(getItemStack("raw_meef", 1, 0)), singletonList(50), 5);
			CentrifugeManager.addDefaultMobRecipe(MOD_ID + ":mist_wolf", emptyList(), emptyList(), 5);
			CentrifugeManager.addDefaultMobRecipe(MOD_ID + ":mosquito_swarm", emptyList(), emptyList(), 5);
			CentrifugeManager.addDefaultMobRecipe(MOD_ID + ":pinch_beetle", emptyList(), emptyList(), 5);
			CentrifugeManager.addDefaultMobRecipe(MOD_ID + ":redcap", singletonList(new ItemStack(Items.COAL, 2)), singletonList(50), 5);
			CentrifugeManager.addDefaultMobRecipe(MOD_ID + ":redcap_sapper", singletonList(new ItemStack(Items.COAL, 2)), singletonList(50), 5);
			CentrifugeManager.addDefaultMobRecipe(MOD_ID + ":skeleton_druid", asList(new ItemStack(Items.BONE, 2), getItemStack("torchberries", 2, 0)), asList(50, 50), 5);
			CentrifugeManager.addDefaultMobRecipe(MOD_ID + ":slime_beetle", singletonList(new ItemStack(Items.SLIME_BALL, 2)), singletonList(50), 5);
			CentrifugeManager.addDefaultMobRecipe(MOD_ID + ":snow_guardian", singletonList(new ItemStack(Items.SNOWBALL, 2)), singletonList(50), 5);
			CentrifugeManager.addDefaultMobRecipe(MOD_ID + ":stable_ice_core", singletonList(new ItemStack(Items.SNOWBALL, 2)), singletonList(50), 5);
			CentrifugeManager.addDefaultMobRecipe(MOD_ID + ":swarm_spider", asList(new ItemStack(Items.STRING, 2), new ItemStack(Items.SPIDER_EYE)), asList(50, 25), 2);
			CentrifugeManager.addDefaultMobRecipe(MOD_ID + ":tower_broodling", asList(new ItemStack(Items.STRING, 2), new ItemStack(Items.SPIDER_EYE)), asList(50, 25), 3);
			CentrifugeManager.addDefaultMobRecipe(MOD_ID + ":tower_ghast", asList(new ItemStack(Items.GHAST_TEAR), new ItemStack(Items.GUNPOWDER)), asList(50, 50), 5);
			CentrifugeManager.addDefaultMobRecipe(MOD_ID + ":tower_golem", asList(new ItemStack(Items.IRON_INGOT, 2), getItemStack("tower_wood", 2, 0)), asList(50, 50), 5);
			CentrifugeManager.addDefaultMobRecipe(MOD_ID + ":tower_termite", singletonList(getItemStack("borer_essence", 2, 0)), singletonList(50), 5);
			CentrifugeManager.addDefaultMobRecipe(MOD_ID + ":troll", singletonList(getItemStack("magic_beans", 2, 0)), singletonList(2), 5);
			CentrifugeManager.addDefaultMobRecipe(MOD_ID + ":unstable_ice_core", singletonList(new ItemStack(Items.SNOWBALL, 2)), singletonList(50), 5);
			CentrifugeManager.addDefaultMobRecipe(MOD_ID + ":winter_wolf", singletonList(getItemStack("arctic_fur", 2, 0)), singletonList(50), 5);
			CentrifugeManager.addDefaultMobRecipe(MOD_ID + ":wraith", singletonList(new ItemStack(Items.GLOWSTONE_DUST, 2)), singletonList(50), 5);
			CentrifugeManager.addDefaultMobRecipe(MOD_ID + ":yeti", singletonList(getItemStack("arctic_fur", 2, 0)), singletonList(50), 5);

			/* BOSSES */
			// CentrifugeManager.addDefaultMobRecipe("twilightforest:naga", emptyList(), emptyList(), 5);
			// CentrifugeManager.addDefaultMobRecipe("twilightforest:lich", emptyList(), emptyList(), 5);
			// CentrifugeManager.addDefaultMobRecipe("twilightforest:quest_ram", emptyList(), emptyList(), 5);

			// CentrifugeManager.addDefaultMobRecipe("twilightforest:minoshroom", singletonList(getItemStack("meef_stroganoff", 5, 0)), singletonList(60), 100);
			// CentrifugeManager.addDefaultMobRecipe("twilightforest:hydra", emptyList(), emptyList(), 5);

			// CentrifugeManager.addDefaultMobRecipe("twilightforest:knight_phantom", emptyList(), emptyList(), 5);
			// CentrifugeManager.addDefaultMobRecipe("twilightforest:ur_ghast", emptyList(), emptyList(), 5);

			// CentrifugeManager.addDefaultMobRecipe("twilightforest:yeti_alpha", emptyList(), emptyList(), 5);
			// CentrifugeManager.addDefaultMobRecipe("twilightforest:snow_queen", emptyList(), emptyList(), 5);

			/* NYI */
			// CentrifugeManager.addDefaultMobRecipe("twilightforest:adherent", emptyList(), emptyList(), 5);
			// CentrifugeManager.addDefaultMobRecipe("twilightforest:harbinger_cube", emptyList(), emptyList(), 5);
			// CentrifugeManager.addDefaultMobRecipe("twilightforest:roving_cube", emptyList(), emptyList(), 5);
		}
	}

}
