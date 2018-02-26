package cofh.thermalexpansion.plugins;

import cofh.core.util.ModPlugin;
import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalexpansion.util.managers.machine.ChargerManager;
import cofh.thermalexpansion.util.managers.machine.InsolatorManager;
import cofh.thermalexpansion.util.managers.machine.PulverizerManager;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Loader;

public class PluginAppliedEnergistics2 extends ModPlugin {

	public static final String MOD_ID = "appliedenergistics2";
	public static final String MOD_NAME = "Applied Energistics 2";

	public PluginAppliedEnergistics2() {

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
			/* PULVERIZER */
			{
				int energy = PulverizerManager.DEFAULT_ENERGY;

				PulverizerManager.addRecipe(energy, new ItemStack(Items.WHEAT), getItemStack("material", 1, 4));
				PulverizerManager.addRecipe(energy, getItemStack("sky_stone_block"), getItemStack("material", 1, 45));
			}
			/* INSOLATOR */
			{
				int energy = 120000;
				int water = 5000;

				InsolatorManager.addRecipe(energy, water, getItemStack("crystal_seed"), new ItemStack(Items.GLOWSTONE_DUST), getItemStack("material", 1, 10));
				InsolatorManager.addRecipe(energy, water, getItemStack("crystal_seed", 1, 600), new ItemStack(Items.GLOWSTONE_DUST), getItemStack("material", 1, 11));
				InsolatorManager.addRecipe(energy, water, getItemStack("crystal_seed", 1, 1200), new ItemStack(Items.GLOWSTONE_DUST), getItemStack("material", 1, 12));
			}
			/* CHARGER */
			{
				int energy = ChargerManager.DEFAULT_ENERGY;

				ChargerManager.addRecipe(energy, getItemStack("quartz_ore", 1), getItemStack("charged_quartz_ore", 1));
				ChargerManager.addRecipe(energy, getItemStack("material", 1, 0), getItemStack("material", 1, 1));
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
