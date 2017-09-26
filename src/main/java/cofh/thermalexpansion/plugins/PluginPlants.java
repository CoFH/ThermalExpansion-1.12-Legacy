package cofh.thermalexpansion.plugins;

import cofh.core.util.ModPlugin;
import cofh.core.util.helpers.ItemHelper;
import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalexpansion.util.managers.machine.InsolatorManager;
import cofh.thermalexpansion.util.managers.machine.InsolatorManager.Type;
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
			ItemStack seedAmaranthus = getItemStack("amaranthus_h_seeds",1);
			ItemStack seedOkra = getItemStack("okra_seeds",1);
			ItemStack seedPineapple = getItemStack("pineapple_seeds",1);

			ItemStack cropAmaranthus = getItemStack("amaranthus_h",1);
			ItemStack cropOkra = getItemStack("okra",1);
			ItemStack cropPineapple = getItemStack("pineapple",1);

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

				InsolatorManager.addDefaultTreeRecipe(saplingBlackKauri, ItemHelper.cloneStack(logBlackKauri, 4), saplingBlackKauri, 50, false, Type.TREE);
				InsolatorManager.addDefaultTreeRecipe(saplingBrazilianPine, ItemHelper.cloneStack(logBrazilianPine, 4), saplingBrazilianPine, 50, false, Type.TREE);
				InsolatorManager.addDefaultTreeRecipe(saplingIntenseCedar, ItemHelper.cloneStack(logIntenseCedar, 4), saplingIntenseCedar, 50, false, Type.TREE);
				InsolatorManager.addDefaultTreeRecipe(saplingMurrayPine, ItemHelper.cloneStack(logMurrayPine, 4), saplingMurrayPine, 50, false, Type.TREE);

				InsolatorManager.addDefaultTreeRecipe(saplingAshen, ItemHelper.cloneStack(logAshen, 4), saplingAshen, 50, false, Type.NETHER_TREE);
				InsolatorManager.addDefaultTreeRecipe(saplingBlazing, ItemHelper.cloneStack(logBlazing, 4), saplingBlazing, 50, false, Type.NETHER_TREE);

				InsolatorManager.addDefaultTreeRecipe(saplingCrystal, ItemHelper.cloneStack(logCrystal, 4), saplingCrystal, 50, false, Type.TREE);
				InsolatorManager.addDefaultTreeRecipe(saplingDarkCrystal, ItemHelper.cloneStack(logDarkCrystal, 4), saplingDarkCrystal, 50, false, Type.TREE);
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
