package cofh.thermalexpansion.plugins;

import cofh.core.util.ModPlugin;
import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalexpansion.util.managers.machine.CentrifugeManager;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Loader;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;

public class PluginIceAndFire extends ModPlugin {

	public static final String MOD_ID = "iceandfire";
	public static final String MOD_NAME = "Ice and Fire";

	public PluginIceAndFire() {

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
			/* CENTRIFUGE */
			{
				ItemStack pixieDust = getItemStack("iceandfire:pixie_dust", 1, 0);

				CentrifugeManager.addDefaultMobRecipe("iceandfire:snowvillager", singletonList(new ItemStack(Items.EMERALD)), singletonList(2), 0);
				CentrifugeManager.addDefaultMobRecipe("iceandfire:hippogryph", asList(new ItemStack(Items.FEATHER, 5), new ItemStack(Items.LEATHER, 5)), asList(50, 50), 2);
				CentrifugeManager.addDefaultMobRecipe("iceandfire:if_pixie", singletonList(pixieDust), singletonList(50), 3);
				CentrifugeManager.addDefaultMobRecipe("iceandfire:cyclops", asList(new ItemStack(Items.LEATHER, 10), new ItemStack(Items.MUTTON, 10), new ItemStack(Blocks.WOOL, 5)), asList(50, 50, 50), 5);
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
