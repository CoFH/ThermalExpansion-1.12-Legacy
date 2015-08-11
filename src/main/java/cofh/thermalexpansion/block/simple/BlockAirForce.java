package cofh.thermalexpansion.block.simple;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import java.util.Random;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFireworkSparkFX;
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

		if (!(ent instanceof EntityLivingBase) || (ent instanceof EntityPlayer && !world.isRemote))
			return;

		int meta = world.getBlockMetadata(x, y, z);
		EntityLivingBase entity = (EntityLivingBase) ent;
		ForgeDirection dir = ForgeDirection.getOrientation(meta^1);
		double l = .1, xO = dir.offsetX * l, yO = dir.offsetY * l, zO = dir.offsetZ * l;
		xO += entity.motionX * l;
		zO += entity.motionZ * l;
		if (AxisAlignedBB.getBoundingBox(x, y, z, x + 1, y + 1, z + 1).isVecInside(Vec3.createVectorHelper(entity.prevPosX, entity.prevPosY - entity.yOffset, entity.prevPosZ))) {
			entity.setPositionAndUpdate(ent.prevPosX + xO, ent.prevPosY - entity.yOffset + yO, ent.prevPosZ + zO);
			entity.motionY = 0;
		}
		/*
		 * Meta maps to ForgeDirection:
		 * ^ move entity to last position, then move pos and last pos by a specified amount (anti-gravity)
		 * ^ can we dampen sound effects for an entity collided with this?
		 */
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void randomDisplayTick(World world, int x, int y, int z, Random rand) {

		EntityFireworkSparkFX spark = new EntityFireworkSparkFX(world, x + 0.5 + rand.nextGaussian() * 0.1, y + 0.5 + rand.nextGaussian() * 0.1, z + 0.5
				+ rand.nextGaussian() * 0.1, 0, 0, 0, Minecraft.getMinecraft().effectRenderer) {

			@Override
			public void moveEntity(double x, double y, double z) {

			}
		};
		spark.setColour(0xFF66FF);
		spark.setFadeColour(0xFFAAFF);
		Minecraft.getMinecraft().effectRenderer.addEffect(spark);
	}

}
