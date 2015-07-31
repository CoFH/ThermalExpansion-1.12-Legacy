package cofh.thermalexpansion.block.simple;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import java.util.Random;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFireworkSparkFX;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;

public class BlockAirForce extends BlockAirBase {

	public BlockAirForce() {

		super(materialBarrier);
	}

	@Override
	public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity ent) {

		int meta = world.getBlockMetadata(x, y, z);
		/*
		 * Meta maps to ForgeDirection: ^ move entity to last position, then move pos and last pos by a specified amount (anti-gravity) ^ can we dampen sound
		 * effects for an entity collided with this? ^ display tick may be removed, depending on if i can do something interesting ^ display tick is aux
		 * particles to the main effect rendering done by the TE ^ on random update ticks (?) check to see if we should remove ourself if the block behind is
		 * not this or a 'source' ?
		 */
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void randomDisplayTick(World world, int x, int y, int z, Random rand) {

		EntityFireworkSparkFX spark = new EntityFireworkSparkFX(world, x + 0.5 + rand.nextGaussian() * 0.6, y + 0.5 + rand.nextGaussian() * 0.55, z + 0.5
				+ rand.nextGaussian() * 0.55, 0, 0, 0, Minecraft.getMinecraft().effectRenderer) {

			@Override
			public void moveEntity(double x, double y, double z) {

			}
		};
		spark.setColour(0xFFFF66);
		spark.setFadeColour(0xFFFFAA);
	}

}
