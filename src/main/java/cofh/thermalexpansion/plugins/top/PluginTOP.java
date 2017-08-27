package cofh.thermalexpansion.plugins.top;

import cofh.core.util.ModPlugin;
import cofh.thermalexpansion.ThermalExpansion;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.event.FMLInterModComms;

public class PluginTOP extends ModPlugin {

	public static final String MOD_ID = "theoneprobe";
	public static final String MOD_NAME = "The One Probe";

	public PluginTOP() {

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
		FMLInterModComms.sendFunctionMessage("theoneprobe", "getTheOneProbe", CompatibilityTOP.class.getName());

		return !error;
	}

	@Override
	public boolean register() {

		if (!enable) {
			return false;
		}
		if (!error) {
			ThermalExpansion.LOG.info("Thermal Expansion: " + MOD_NAME + " Plugin Enabled.");
		}
		return !error;
	}

	/* HELPERS */
	public static int chestContentsBorderColor = 0xff006699;

}
