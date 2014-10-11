package thermalexpansion.block.plate;

import cpw.mods.fml.common.registry.GameRegistry;

import net.minecraft.entity.Entity;

public class TilePlateImpulse extends TilePlateBase {

	public static void initialize() {

		GameRegistry.registerTileEntity(TilePlateImpulse.class, "cofh.thermalexpansion.PlateImpulse");
	}

	@Override
	public int getType() {

		return BlockPlate.Types.IMPULSE.ordinal();
	}

	@Override
	public void onEntityCollidedWithBlock(Entity theEntity) {

		double x = 0;
		double y = 0;
		double z = 0;

		switch (direction) {
		case 0:
			y = -3;
			break;
		case 1:
			y = 3;
			break;
		case 2:
			z = -5;
			y = 1;
			break;
		case 3:
			z = 5;
			y = 1;
			break;
		case 4:
			x = -5;
			y = 1;
			break;
		case 5:
			x = 5;
			y = 1;
			break;
		}
		accelerateEntity(theEntity, x, y, z);
	}

	protected void accelerateEntity(Entity theEntity, double x, double y, double z) {

		theEntity.motionX += x;
		theEntity.motionY = y;
		theEntity.motionZ += z;
	}

}
