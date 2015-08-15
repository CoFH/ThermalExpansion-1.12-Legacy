package cofh.thermalexpansion.block.simple;

import net.minecraft.client.particle.EntityFX;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class BlockAirForce extends BlockAirBase {

	public BlockAirForce() {

		super(materialBarrier);
		disableStats();
	}

	@Override
	public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity ent) {

		if ((ent instanceof EntityFX) || (ent instanceof EntityPlayer && !world.isRemote))
			return;

		int meta = world.getBlockMetadata(x, y, z);
		ForgeDirection dir = ForgeDirection.getOrientation(meta^1);
		repositionEntity(world, x, y, z, ent, dir, .1);
		/*
		 * can we dampen sound effects for an entity collided with this?
		 */
	}

	public static void repositionEntity(World world, int x, int y, int z, Entity ent, ForgeDirection dir, double amount) {

		double l = amount, xO = dir.offsetX * l, yO = dir.offsetY * l, zO = dir.offsetZ * l;
		if (AxisAlignedBB.getBoundingBox(x, y, z, x + 1, y + 1, z + 1).isVecInside(Vec3.createVectorHelper(ent.posX, ent.posY - ent.yOffset, ent.posZ))) {
			ent.fallDistance *= 0.4;
			ent.motionY = 0;
		}
		if (AxisAlignedBB.getBoundingBox(x, y, z, x + 1, y + 1, z + 1).isVecInside(Vec3.createVectorHelper(ent.prevPosX, ent.prevPosY - ent.yOffset, ent.prevPosZ))) {
			if (!world.func_147461_a(ent.boundingBox).isEmpty() || !world.func_147461_a(ent.boundingBox.getOffsetBoundingBox(xO * 2, yO * 2, zO * 2)).isEmpty()) {
				xO = yO = zO = 0;
			}
			if (ent.motionX == 0 && ent.motionZ == 0) {
				switch (dir.ordinal() >> 1) {
				case 0:
					xO += (x - (ent.prevPosX - .5)) / 20;
					zO += (z - (ent.prevPosZ - .5)) / 20;
					break;
				case 1:
					xO += (x - (ent.prevPosX - .5)) / 20;
					yO += (y - (ent.prevPosY - ent.yOffset - .1)) / 20;
					break;
				case 2:
					yO += (y - (ent.prevPosY - ent.yOffset - .1)) / 20;
					zO += (z - (ent.prevPosZ - .5)) / 20;
					break;
				}
			}
			xO += ent.motionX;
			zO += ent.motionZ;
			if (ent instanceof EntityLivingBase) {
				((EntityLivingBase)ent).setPositionAndUpdate(ent.prevPosX + xO, ent.prevPosY - ent.yOffset + yO, ent.prevPosZ + zO);
			} else {
				ent.setLocationAndAngles(ent.prevPosX + xO, ent.prevPosY - ent.yOffset + yO, ent.prevPosZ + zO, ent.rotationYaw, ent.rotationPitch);
				ent.motionX = ent.motionZ = 0;
			}
			ent.motionX *= .5;
			ent.motionZ *= .5;
			ent.motionY = 0;
		}
	}

}
