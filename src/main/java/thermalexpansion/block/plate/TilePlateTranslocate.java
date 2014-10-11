package thermalexpansion.block.plate;

import cpw.mods.fml.common.registry.GameRegistry;

import net.minecraft.entity.Entity;

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
	public void onEntityCollidedWithBlock(Entity entity) {

		double x = 0;
		double y = 0;
		double z = 0;

		switch (direction) {
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
		entity.setPosition(entity.posX + x, entity.posY + y, entity.posZ + z);
		// theEntity.setPositionAndRotation(par1, par3, par5, par7, par8)
	}

}
