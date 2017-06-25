package cofh.thermalexpansion.init;

import cofh.thermalexpansion.plugins.ForestryPlugin;
import cofh.thermalexpansion.plugins.NaturaPlugin;
import cofh.thermalexpansion.plugins.RusticPlugin;
import cofh.thermalexpansion.plugins.TConstructPlugin;

public class TEPlugins {

	private TEPlugins() {

	}

	public static void preInit() {

	}

	public static void initialize() {

	}

	public static void postInit() {

		ForestryPlugin.initialize();
		NaturaPlugin.initialize();
		RusticPlugin.initialize();
		TConstructPlugin.initialize();
	}

}
