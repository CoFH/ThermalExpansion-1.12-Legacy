package thermalexpansion.block.light;

import cpw.mods.fml.common.registry.GameRegistry;

public class TileLightFalse extends TileLight {

	public static void initialize() {

		GameRegistry.registerTileEntity(TileLightFalse.class, "thermalexpansion.LightFalse");
	}

	@Override
	public int getLightValue() {

		return 0;
	}

}
