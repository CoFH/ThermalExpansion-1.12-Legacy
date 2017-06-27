package cofh.thermalexpansion.init;

import cofh.thermalexpansion.plugins.*;

public class TEPlugins {

	private TEPlugins() {

	}

	public static void preInit() {

	}

	public static void initialize() {

	}

	public static void postInit() {

		ActuallyAdditionsPlugin.initialize();
		ForestryPlugin.initialize();
		HarvestcraftPlugin.initialize();
		NaturaPlugin.initialize();
		RusticPlugin.initialize();
		TConstructPlugin.initialize();

		ForestryPlugin.postInit();
	}

}
