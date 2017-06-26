package cofh.thermalexpansion.init;

import cofh.thermalexpansion.plugins.ForestryPlugin;
import cofh.thermalexpansion.plugins.HarvestcraftPlugin;
import cofh.thermalexpansion.plugins.NaturaPlugin;
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
		HarvestcraftPlugin.initialize();
		NaturaPlugin.initialize();
		TConstructPlugin.initialize();

		ForestryPlugin.postInit();
	}

}
