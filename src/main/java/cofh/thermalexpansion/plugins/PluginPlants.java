package cofh.thermalexpansion.plugins;

import cofh.core.util.ModPlugin;
import cofh.core.util.helpers.ItemHelper;
import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalexpansion.util.managers.machine.InsolatorManager;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Loader;

public class PluginPlants extends ModPlugin {

	public static final String MOD_ID = "plants2";
	public static final String MOD_NAME = "Plants";

	public PluginPlants() {

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
			ItemStack seedAmaranthus = getItemStack("amaranthus_h_seeds", 1);
			ItemStack seedOkra = getItemStack("okra_seeds", 1);
			ItemStack seedPineapple = getItemStack("pineapple_seeds", 1);

			ItemStack cropAmaranthus = getItemStack("amaranthus_h", 1);
			ItemStack cropOkra = getItemStack("okra", 1);
			ItemStack cropPineapple = getItemStack("pineapple", 1);

			ItemStack cropBlackberry = getItemStack("blackberry", 1, 0);
			ItemStack cropBlueberry = getItemStack("blueberry", 1, 0);
			ItemStack cropHuckleberry = getItemStack("huckleberry", 1, 0);
			ItemStack cropRaspberry = getItemStack("raspberry", 1, 0);

			ItemStack bushBlackberry = getItemStack("bushling", 1, 0);
			ItemStack bushBlueberry = getItemStack("bushling", 1, 1);
			ItemStack bushHuckleberry = getItemStack("bushling", 1, 4);
			ItemStack bushRaspberry = getItemStack("bushling", 1, 5);

			ItemStack plantDollsEyes = getItemStack("harvest_0", 1, 0);
			ItemStack plantGalapagos = getItemStack("harvest_0", 1, 1);
			ItemStack plantRagweed = getItemStack("harvest_0", 1, 2);
			ItemStack plantCotton = getItemStack("harvest_0", 1, 3);
			ItemStack plantCarrot = getItemStack("harvest_0", 1, 4);
			ItemStack plantPokeweed = getItemStack("harvest_0", 1, 5);
			ItemStack plantPlantain = getItemStack("harvest_0", 1, 6);
			ItemStack plantBlackRaspberry = getItemStack("harvest_0", 1, 7);

			ItemStack plantWineRaspberry = getItemStack("harvest_1", 1, 0);
			ItemStack plantSaffron = getItemStack("harvest_1", 1, 1);
			ItemStack plantHorsenettle = getItemStack("harvest_1", 1, 2);
			ItemStack plantClimbingNightshade = getItemStack("harvest_1", 1, 3);
			ItemStack plantBlackNightshade = getItemStack("harvest_1", 1, 4);

			ItemStack plantBlazingOrchid = getItemStack("nether_harvest", 1, 0);
			ItemStack plantFloweringMagma = getItemStack("nether_harvest", 1, 1);
			ItemStack plantGoldenFlame = getItemStack("nether_harvest", 1, 2);
			ItemStack plantEmberian = getItemStack("nether_harvest", 1, 4);
			ItemStack plantSmolderingBerry = getItemStack("nether_harvest", 1, 5);

			ItemStack plantDysentery = getItemStack("double_harvest_0", 1, 0);

			ItemStack harvestDollsEyes = getItemStack("actaea_p", 1, 0);
			ItemStack harvestGalapagos = getItemStack("alternanthera_f", 1, 0);
			ItemStack harvestRagweed = getItemStack("ambrosia_a", 1, 0);
			ItemStack harvestCotton = getItemStack("generic", 1, 0);
			ItemStack harvestCarrot = getItemStack("daucus_c", 1, 0);
			ItemStack harvestPokeweed = getItemStack("phytolacca_a", 1, 0);
			ItemStack harvestPlantain = getItemStack("plantago_m", 1, 0);
			ItemStack harvestBlackRaspberry = getItemStack("rubus_o", 1, 0);

			ItemStack harvestWineRaspberry = getItemStack("rubus_p", 1, 0);
			ItemStack harvestSaffron = getItemStack("saffron", 1, 0);
			ItemStack harvestHorsenettle = getItemStack("solanum_c", 1, 0);
			ItemStack harvestClimbingNightshade = getItemStack("solanum_d", 1, 0);
			ItemStack harvestBlackNightshade = getItemStack("solanum_n", 1, 0);

			ItemStack harvestBlazingOrchid = getItemStack("generic", 1, 10);
			ItemStack harvestFloweringMagma = getItemStack("generic", 1, 11);
			ItemStack harvestSmolderingBerry = getItemStack("generic", 1, 12);
			ItemStack harvestEmberian = getItemStack("generic", 1, 13);

			ItemStack harvestDysentery = getItemStack("alyxia_b", 1, 0);

			ItemStack saplingBlackKauri = getItemStack("sapling_0", 1, 0);
			ItemStack saplingBrazilianPine = getItemStack("sapling_0", 1, 1);
			ItemStack saplingIntenseCedar = getItemStack("sapling_0", 1, 2);
			ItemStack saplingMurrayPine = getItemStack("sapling_0", 1, 3);

			ItemStack saplingAshen = getItemStack("nether_sapling", 1, 0);
			ItemStack saplingBlazing = getItemStack("nether_sapling", 1, 1);

			ItemStack saplingCrystal = getItemStack("crystal_sapling", 1, 0);
			ItemStack saplingDarkCrystal = getItemStack("crystal_sapling", 1, 1);

			ItemStack logBlackKauri = getItemStack("log_0", 1, 0);
			ItemStack logBrazilianPine = getItemStack("log_0", 1, 1);
			ItemStack logIntenseCedar = getItemStack("log_0", 1, 2);
			ItemStack logMurrayPine = getItemStack("log_0", 1, 3);

			ItemStack logAshen = getItemStack("nether_log", 1, 0);
			ItemStack logBlazing = getItemStack("nether_log", 1, 1);

			ItemStack logCrystal = getItemStack("crystal_log", 1, 0);
			ItemStack logDarkCrystal = getItemStack("crystal_log", 1, 1);

			/* INSOLATOR */
			{
				String plant = "cosmetic_0";
				for (int i = 0; i < 16; i++) {
					InsolatorManager.addDefaultRecipe(getItemStack(plant, 1, i), getItemStack(plant, 3, i), ItemStack.EMPTY, 0);
				}
				plant = "cosmetic_1";
				for (int i = 0; i < 16; i++) {
					InsolatorManager.addDefaultRecipe(getItemStack(plant, 1, i), getItemStack(plant, 3, i), ItemStack.EMPTY, 0);
				}
				plant = "cosmetic_2";
				for (int i = 0; i < 16; i++) {
					InsolatorManager.addDefaultRecipe(getItemStack(plant, 1, i), getItemStack(plant, 3, i), ItemStack.EMPTY, 0);
				}
				plant = "cosmetic_3";
				for (int i = 0; i < 16; i++) {
					InsolatorManager.addDefaultRecipe(getItemStack(plant, 1, i), getItemStack(plant, 3, i), ItemStack.EMPTY, 0);
				}
				plant = "cosmetic_4";
				for (int i = 0; i < 16; i++) {
					InsolatorManager.addDefaultRecipe(getItemStack(plant, 1, i), getItemStack(plant, 3, i), ItemStack.EMPTY, 0);
				}
				plant = "desert_0";
				for (int i = 0; i < 16; i++) {
					InsolatorManager.addDefaultRecipe(getItemStack(plant, 1, i), getItemStack(plant, 3, i), ItemStack.EMPTY, 0);
				}
				plant = "desert_1";
				for (int i = 0; i < 2; i++) {
					InsolatorManager.addDefaultRecipe(getItemStack(plant, 1, i), getItemStack(plant, 3, i), ItemStack.EMPTY, 0);
				}
				plant = "double_0";
				for (int i = 0; i < 6; i++) {
					InsolatorManager.addDefaultRecipe(getItemStack(plant, 1, i), getItemStack(plant, 3, i), ItemStack.EMPTY, 0);
				}

				InsolatorManager.addDefaultRecipe(seedAmaranthus, cropAmaranthus, seedAmaranthus, 110);
				InsolatorManager.addDefaultRecipe(seedOkra, cropOkra, seedOkra, 110);
				InsolatorManager.addDefaultRecipe(seedPineapple, cropPineapple, seedPineapple, 110);

				InsolatorManager.addDefaultRecipe(bushBlackberry, ItemHelper.cloneStack(cropBlackberry, 2), bushBlackberry, 100);
				InsolatorManager.addDefaultRecipe(bushBlueberry, ItemHelper.cloneStack(cropBlueberry, 2), bushBlueberry, 100);
				InsolatorManager.addDefaultRecipe(bushHuckleberry, ItemHelper.cloneStack(cropHuckleberry, 2), bushHuckleberry, 100);
				InsolatorManager.addDefaultRecipe(bushRaspberry, ItemHelper.cloneStack(cropRaspberry, 2), bushRaspberry, 100);

				InsolatorManager.addDefaultRecipe(plantDollsEyes, ItemHelper.cloneStack(harvestDollsEyes, 2), plantDollsEyes, 100);
				InsolatorManager.addDefaultRecipe(plantGalapagos, ItemHelper.cloneStack(harvestGalapagos, 2), plantGalapagos, 100);
				InsolatorManager.addDefaultRecipe(plantRagweed, ItemHelper.cloneStack(harvestRagweed, 2), plantRagweed, 100);
				InsolatorManager.addDefaultRecipe(plantCotton, ItemHelper.cloneStack(harvestCotton, 2), plantCotton, 100);
				InsolatorManager.addDefaultRecipe(plantCarrot, ItemHelper.cloneStack(harvestCarrot, 2), plantCarrot, 100);
				InsolatorManager.addDefaultRecipe(plantPokeweed, ItemHelper.cloneStack(harvestPokeweed, 2), plantPokeweed, 100);
				InsolatorManager.addDefaultRecipe(plantPlantain, ItemHelper.cloneStack(harvestPlantain, 2), plantPlantain, 100);
				InsolatorManager.addDefaultRecipe(plantBlackRaspberry, ItemHelper.cloneStack(harvestBlackRaspberry, 2), plantBlackRaspberry, 100);

				InsolatorManager.addDefaultRecipe(plantWineRaspberry, ItemHelper.cloneStack(harvestWineRaspberry, 2), plantWineRaspberry, 100);
				InsolatorManager.addDefaultRecipe(plantSaffron, ItemHelper.cloneStack(harvestSaffron, 2), plantSaffron, 100);
				InsolatorManager.addDefaultRecipe(plantHorsenettle, ItemHelper.cloneStack(harvestHorsenettle, 2), plantHorsenettle, 100);
				InsolatorManager.addDefaultRecipe(plantClimbingNightshade, ItemHelper.cloneStack(harvestClimbingNightshade, 2), plantClimbingNightshade, 100);
				InsolatorManager.addDefaultRecipe(plantBlackNightshade, ItemHelper.cloneStack(harvestBlackNightshade, 2), plantBlackNightshade, 100);

				InsolatorManager.addDefaultRecipe(plantBlazingOrchid, ItemHelper.cloneStack(harvestBlazingOrchid, 2), plantBlazingOrchid, 100);
				InsolatorManager.addDefaultRecipe(plantFloweringMagma, ItemHelper.cloneStack(harvestFloweringMagma, 2), plantFloweringMagma, 100);
				InsolatorManager.addDefaultRecipe(plantGoldenFlame, ItemHelper.cloneStack(Items.SPECKLED_MELON, 2), plantGoldenFlame, 100);
				InsolatorManager.addDefaultRecipe(plantEmberian, ItemHelper.cloneStack(harvestEmberian, 2), plantEmberian, 100);
				InsolatorManager.addDefaultRecipe(plantSmolderingBerry, ItemHelper.cloneStack(harvestSmolderingBerry, 2), plantSmolderingBerry, 100);

				InsolatorManager.addDefaultRecipe(plantDysentery, ItemHelper.cloneStack(harvestDysentery, 2), plantDysentery, 100);

				InsolatorManager.addDefaultTreeRecipe(saplingBlackKauri, ItemHelper.cloneStack(logBlackKauri, 4), saplingBlackKauri, 50);
				InsolatorManager.addDefaultTreeRecipe(saplingBrazilianPine, ItemHelper.cloneStack(logBrazilianPine, 4), saplingBrazilianPine, 50);
				InsolatorManager.addDefaultTreeRecipe(saplingIntenseCedar, ItemHelper.cloneStack(logIntenseCedar, 4), saplingIntenseCedar, 50);
				InsolatorManager.addDefaultTreeRecipe(saplingMurrayPine, ItemHelper.cloneStack(logMurrayPine, 4), saplingMurrayPine, 50);

				InsolatorManager.addDefaultTreeRecipe(saplingAshen, ItemHelper.cloneStack(logAshen, 4), saplingAshen, 50);
				InsolatorManager.addDefaultTreeRecipe(saplingBlazing, ItemHelper.cloneStack(logBlazing, 4), saplingBlazing, 50);

				InsolatorManager.addDefaultTreeRecipe(saplingCrystal, ItemHelper.cloneStack(logCrystal, 4), saplingCrystal, 50);
				InsolatorManager.addDefaultTreeRecipe(saplingDarkCrystal, ItemHelper.cloneStack(logDarkCrystal, 4), saplingDarkCrystal, 50);
			}

			/* TAPPER */
			{

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
