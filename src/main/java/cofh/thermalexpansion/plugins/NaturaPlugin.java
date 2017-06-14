package cofh.thermalexpansion.plugins;

public class NaturaPlugin {

	private NaturaPlugin() {

	}

	public static final String MOD_ID = "natura";
	public static final String MOD_NAME = "Natura";

	public static void initialize() {

		String category = "Plugins";
		String comment = "If TRUE, support for " + MOD_NAME + " is enabled.";

		//		boolean enable = ThermalExpansion.CONFIG.getConfiguration().getBoolean(MOD_NAME, category, true, comment);
		//
		//		if (!enable || !Loader.isModLoaded(MOD_ID)) {
		//			return;
		//		}
		//
		//		ThermalExpansion.LOG.info("Thermal Expansion: " + MOD_NAME + " Plugin Enabled.");
	}

}
