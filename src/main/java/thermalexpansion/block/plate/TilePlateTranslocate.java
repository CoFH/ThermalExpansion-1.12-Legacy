package thermalexpansion.block.plate;

import net.minecraft.entity.Entity;
import cpw.mods.fml.common.registry.GameRegistry;

public class TilePlateTranslocate extends TilePlateBase {

	public static void initialize() {

		GameRegistry.registerTileEntity(TilePlateTranslocate.class, "cofh.thermalexpansion.PlateTranslocate");
	}

	byte distance = 16;

	@Override
	public int getType() {

		return BlockPlate.Types.TRANSLOCATE.ordinal();
	}

	@Override
	public void onEntityCollidedWithBlock(Entity theEntity) {

		double x = 0;
		double y = 0;
		double z = 0;

		switch (alignment) {
		case 0:
			y = -distance;
			break;
		case 1:
			y = distance;
			break;
		case 2:
			z = -distance;
			break;
		case 3:
			z = distance;
			break;
		case 4:
			x = -distance;
			break;
		case 5:
			x = distance;
			break;
		}
		theEntity.setPosition(xCoord + x, yCoord + y + theEntity.height, zCoord + z);
		// theEntity.setPositionAndRotation(par1, par3, par5, par7, par8)
	}

}
