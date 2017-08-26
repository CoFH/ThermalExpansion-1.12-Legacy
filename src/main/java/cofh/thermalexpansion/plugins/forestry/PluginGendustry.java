package cofh.thermalexpansion.plugins.forestry;

import cofh.core.util.ModPlugin;
import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalexpansion.util.managers.machine.CentrifugeManager;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Loader;

import java.util.Arrays;

public class PluginGendustry extends ModPlugin {

	public static final String PARENT_ID = PluginForestry.MOD_ID;
	public static final String MOD_ID = "gendustry";
	public static final String MOD_NAME = "Gendustry";

	public PluginGendustry() {

		super(MOD_ID, MOD_NAME);
	}

	/* IInitializer */
	@Override
	public boolean initialize() {

		String category = "Plugins";
		String comment = "If TRUE, support for " + MOD_NAME + " is enabled.";
		enable = Loader.isModLoaded(PARENT_ID) && Loader.isModLoaded(MOD_ID) && ThermalExpansion.CONFIG.getConfiguration().getBoolean(MOD_NAME, category, true, comment);

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
			ItemStack dropHoney = getItemStack(PARENT_ID, "honey_drop", 1, 0);
			ItemStack wax = getItemStack(PARENT_ID, "beeswax", 1, 0);

			ItemStack[] tintedCombs = new ItemStack[16];
			ItemStack[] tintedDrops = new ItemStack[16];

			int tintedStart = 10;

			for (int i = tintedStart; i < 16 + tintedStart; i++) {
				tintedCombs[i] = getItemStack("honey_comb", 1, i);
				tintedDrops[i] = getItemStack("honey_drop", 1, i);
			}

			/* CENTRIFUGE */
			{
				int energy = CentrifugeManager.DEFAULT_ENERGY;

				for (int i = 0; i < 16; i++) {
					CentrifugeManager.addRecipe(energy, tintedCombs[i], Arrays.asList(tintedDrops[i], dropHoney, wax), Arrays.asList(100, 30, 50), null);
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
