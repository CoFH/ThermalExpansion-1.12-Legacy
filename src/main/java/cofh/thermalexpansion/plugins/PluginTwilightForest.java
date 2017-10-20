package cofh.thermalexpansion.plugins;

import cofh.core.util.ModPlugin;
import cofh.core.util.helpers.ItemHelper;
import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalexpansion.util.managers.machine.InsolatorManager;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Loader;

public class PluginTwilightForest extends ModPlugin {

	public static final String MOD_ID = "twilightforest";
	public static final String MOD_NAME = "Twilight Forest";

	public PluginTwilightForest() {

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
				InsolatorManager.addDefaultRecipe(plantMossPatch, ItemHelper.cloneStack(plantMossPatch, 3), ItemStack.EMPTY, 0);
				InsolatorManager.addDefaultRecipe(plantMayapple, ItemHelper.cloneStack(plantMayapple, 3), ItemStack.EMPTY, 0);
				InsolatorManager.addDefaultRecipe(plantCloverPatch, ItemHelper.cloneStack(plantCloverPatch, 3), ItemStack.EMPTY, 0);
				InsolatorManager.addDefaultRecipe(plantFiddlehead, ItemHelper.cloneStack(plantFiddlehead, 3), ItemStack.EMPTY, 0);

				InsolatorManager.addDefaultRecipe(plantMushgloom, ItemHelper.cloneStack(plantMushgloom, 2), ItemStack.EMPTY, 0);

				InsolatorManager.addDefaultTreeRecipe(saplingTwilightOak, ItemHelper.cloneStack(logTwilightOak, 4), saplingTwilightOak, 50);
				InsolatorManager.addDefaultTreeRecipe(saplingCanopy, ItemHelper.cloneStack(logCanopy, 4), saplingCanopy, 50);
				InsolatorManager.addDefaultTreeRecipe(saplingMangrove, ItemHelper.cloneStack(logMangrove, 4), saplingMangrove, 50);
				InsolatorManager.addDefaultTreeRecipe(saplingDarkwood, ItemHelper.cloneStack(logDarkwood, 4), saplingDarkwood, 50);

				InsolatorManager.addDefaultTreeRecipe(saplingTimewood, ItemHelper.cloneStack(logTimewood, 6), saplingTimewood, 50);
				InsolatorManager.addDefaultTreeRecipe(saplingTranswood, ItemHelper.cloneStack(logTranswood, 6), saplingTranswood, 50);
				InsolatorManager.addDefaultTreeRecipe(saplingMinewood, ItemHelper.cloneStack(logMinewood, 6), saplingMinewood, 50);
				InsolatorManager.addDefaultTreeRecipe(saplingSortingwood, ItemHelper.cloneStack(logSortingwood, 6), saplingSortingwood, 50);

				InsolatorManager.addDefaultTreeRecipe(saplingRainbow, ItemHelper.cloneStack(logTwilightOak, 4), saplingRainbow, 50);
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
