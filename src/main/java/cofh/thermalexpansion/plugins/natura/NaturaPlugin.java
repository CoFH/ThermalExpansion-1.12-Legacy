package cofh.thermalexpansion.plugins.natura;

import net.minecraftforge.fml.common.Loader;

public class NaturaPlugin {

	public static final String MOD_NAME = "forestry";

	public static void initialize() {

		if (!Loader.isModLoaded(MOD_NAME)) {
			return;
		}
	}
	
}
