package cofh.thermalexpansion.init;

import cofh.thermalexpansion.plugins.forestry.ForestryPlugin;
import cofh.thermalexpansion.plugins.natura.NaturaPlugin;
import cofh.thermalexpansion.plugins.tconstruct.TinkersConstructPlugin;

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
		TinkersConstructPlugin.initialize();
	}

}
