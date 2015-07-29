package cofh.thermalexpansion.block.plate;

import cofh.repack.codechicken.lib.vec.Vector3;
import cpw.mods.fml.common.registry.GameRegistry;

public class TilePlateExcursion extends TilePlatePoweredBase {

	public static void initialize() {

		GameRegistry.registerTileEntity(TilePlateExcursion.class, "cofh.thermalexpansion.PlateExcursion");
	}

	public TilePlateExcursion() {

		super(BlockPlate.Types.POWERED_IMPULSE, 200000);
	}

	int bindY = 24;
	int ticksElapsed = 0;

	public Vector3 getMovementVector() {

		return new Vector3(0, bindY, 0);
	}

}
