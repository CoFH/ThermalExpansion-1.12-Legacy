package thermalexpansion.block.plate;

import cpw.mods.fml.common.registry.GameRegistry;

import net.minecraft.entity.Entity;

public class TilePlateImpulse extends TilePlateBase {

	public static void initialize() {

		GameRegistry.registerTileEntity(TilePlateImpulse.class, "cofh.thermalexpansion.PlateImpulse");
	}

	public TilePlateImpulse() {

		super(BlockPlate.Types.IMPULSE);
	}

	@Override
	public void onEntityCollidedWithBlock(Entity theEntity) {

		int[] v = getVector(3, 1, 0);
		accelerateEntity(theEntity, v[0], v[1], v[2]);
	}

	protected void accelerateEntity(Entity theEntity, double x, double y, double z) {

		theEntity.motionX += x;
		theEntity.motionY = y;
		theEntity.motionZ += z;
	}

}
