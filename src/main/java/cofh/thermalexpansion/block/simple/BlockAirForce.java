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

		if (world.isRemote ? ent instanceof EntityFX : ent instanceof EntityPlayer) {
			return;
		}

		int meta = world.getBlockMetadata(x, y, z);
		ForgeDirection dir = ForgeDirection.getOrientation(meta ^ 1);
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
		if (AxisAlignedBB.getBoundingBox(x, y, z, x + 1, y + 1, z + 1).isVecInside(
				Vec3.createVectorHelper(ent.prevPosX, ent.prevPosY - ent.yOffset, ent.prevPosZ))) {
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
			} else if (false) {
				double a = Math.atan2(ent.motionX, ent.motionZ), s = Math.sqrt(ent.motionX * ent.motionX + ent.motionZ * ent.motionZ);
				System.out.format(a + " " + ((ent.rotationYaw * Math.PI / 180) % Math.PI) + "\n");
				double a2 = ent.rotationPitch * Math.PI / 180 * (1 - Math.abs(Math.sin(((ent.rotationYaw * Math.PI / 180) % Math.PI) - a)));
				// FIXME: get correct pitch, when movement is sideways (a/d) don't apply vertical movement
				double v = Math.cos(a2);
				xO += s * Math.sin(a) * v;
				zO += s * Math.cos(a) * v;
				yO += s * Math.sin(a2);
			} else {
				xO += ent.motionX;
				zO += ent.motionZ;
			}
			if (ent instanceof EntityLivingBase) {
				((EntityLivingBase) ent).setPositionAndUpdate(ent.prevPosX + xO, ent.prevPosY - ent.yOffset + yO, ent.prevPosZ + zO);
			} else {
				ent.setLocationAndAngles(ent.prevPosX + xO, ent.prevPosY - ent.yOffset + yO, ent.prevPosZ + zO, ent.rotationYaw, ent.rotationPitch);
			}
			ent.lastTickPosX = ent.posX - xO;
			ent.lastTickPosY = ent.posY - yO;
			ent.lastTickPosZ = ent.posZ - zO;
			ent.motionX *= .5;
			ent.motionZ *= .5;
			ent.motionY = 0;
		}
	}

}
