package cofh.thermalexpansion.plugins;

import cofh.core.util.ModPlugin;
import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalexpansion.util.managers.machine.InsolatorManager;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Loader;

public class PluginPlantsLegacy extends ModPlugin {

	public static final String MOD_ID = "plants";
	public static final String MOD_NAME = "Plants (Legacy)";

	public PluginPlantsLegacy() {

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

			/* INSOLATOR */
			{
				String cosmeticPlant = "cosmetic_1";
				for (int i = 0; i < 16; i++) {
					InsolatorManager.addDefaultRecipe(getItemStack(cosmeticPlant, 1, i), getItemStack(cosmeticPlant, 3, i), ItemStack.EMPTY, 0);
				}
				cosmeticPlant = "cosmetic_2";
				for (int i = 0; i < 16; i++) {
					InsolatorManager.addDefaultRecipe(getItemStack(cosmeticPlant, 1, i), getItemStack(cosmeticPlant, 3, i), ItemStack.EMPTY, 0);
				}
				cosmeticPlant = "cosmetic_3";
				for (int i = 0; i < 16; i++) {
					InsolatorManager.addDefaultRecipe(getItemStack(cosmeticPlant, 1, i), getItemStack(cosmeticPlant, 3, i), ItemStack.EMPTY, 0);
				}
				cosmeticPlant = "cosmetic_4";
				for (int i = 0; i < 16; i++) {
					InsolatorManager.addDefaultRecipe(getItemStack(cosmeticPlant, 1, i), getItemStack(cosmeticPlant, 3, i), ItemStack.EMPTY, 0);
				}
				cosmeticPlant = "cosmetic_5";
				for (int i = 0; i < 6; i++) {
					InsolatorManager.addDefaultRecipe(getItemStack(cosmeticPlant, 1, i), getItemStack(cosmeticPlant, 3, i), ItemStack.EMPTY, 0);
				}
				cosmeticPlant = "cosmetic_6";
				for (int i = 0; i < 16; i++) {
					InsolatorManager.addDefaultRecipe(getItemStack(cosmeticPlant, 1, i), getItemStack(cosmeticPlant, 3, i), ItemStack.EMPTY, 0);
				}
				cosmeticPlant = "cosmetic_7";
				for (int i = 0; i < 16; i++) {
					InsolatorManager.addDefaultRecipe(getItemStack(cosmeticPlant, 1, i), getItemStack(cosmeticPlant, 3, i), ItemStack.EMPTY, 0);
				}
				cosmeticPlant = "cosmetic_8";
				for (int i = 0; i < 3; i++) {
					InsolatorManager.addDefaultRecipe(getItemStack(cosmeticPlant, 1, i), getItemStack(cosmeticPlant, 3, i), ItemStack.EMPTY, 0);
				}
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
