package cofh.thermalexpansion.block.light;

import cofh.api.tileentity.ITileInfo;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class TileIlluminator extends TileLightBase implements ITileInfo {

	public static void initialize() {

		GameRegistry.registerTileEntity(TileIlluminator.class, "thermalexpansion:light_illuminator");
	}

	public static void config() {

	}

}
