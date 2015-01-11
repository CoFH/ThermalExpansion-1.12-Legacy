package thermalexpansion.block.plate;

import cofh.core.util.CoreUtils;
import cpw.mods.fml.common.registry.GameRegistry;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;

public class TilePlateTranslocate extends TilePlateBase {

	public static void initialize() {

		GameRegistry.registerTileEntity(TilePlateTranslocate.class, "cofh.thermalexpansion.PlateTranslocate");
	}

	public TilePlateTranslocate() {

		super(BlockPlate.Types.TRANSLOCATE);
	}

	byte distance = 16;

	@Override
	public void onEntityCollidedWithBlock(Entity entity) {

		int[] v = getVector(distance);
		double x = xCoord + v[0] + .5;
		double y = yCoord + v[1] + .125;
		double z = zCoord + v[2] + .5;
		if (!(entity instanceof EntityLivingBase) && entity.getBoundingBox() == null) {
			x = entity.posX + v[0];
			y = entity.posY + v[1];
			z = entity.posZ + v[2];
		}

		int x2 = xCoord + v[0];
		int y2 = yCoord + v[1];
		int z2 = zCoord + v[2];

		Block block = worldObj.getBlock(x2, y2, z2);
		if (!(block.isOpaqueCube() || block.getMaterial().isSolid())) {
			if (entity instanceof EntityLivingBase) {
				if (worldObj.isRemote) {
					return;
				}
				CoreUtils.teleportEntityTo((EntityLivingBase) entity, x, y, z, true);
			} else {
				entity.setLocationAndAngles(x, y, z, entity.rotationYaw, entity.rotationPitch);
				entity.worldObj.playSoundAtEntity(entity, "mob.endermen.portal", 0.5F, 1.0F);
			}
		}
	}

}
