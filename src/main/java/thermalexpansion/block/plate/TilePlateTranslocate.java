package thermalexpansion.block.plate;

import cofh.core.util.CoreUtils;
import cpw.mods.fml.common.registry.GameRegistry;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;

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


		int[] v = getVector(distance);
		double x = xCoord + v[0] + .5;
		double y = yCoord + v[1] + .125;
		double z = zCoord + v[2] + .5;

		int x2 = xCoord + v[0];
		int y2 = yCoord + v[1];
		int z2 = zCoord + v[2];

		if (!worldObj.getBlock(x2, y2, z2).getMaterial().isSolid()) {
			if (entity instanceof EntityLivingBase) {
				CoreUtils.teleportEntityTo((EntityLivingBase) entity, x, y, z);
			} else {
				entity.setLocationAndAngles(x, y, z, entity.rotationYaw, entity.rotationPitch);
				entity.worldObj.playSoundAtEntity(entity, "mob.endermen.portal", 1.0F, 1.0F);
			}
		}
	}

}
