package thermalexpansion.block.simple;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import java.util.Random;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFireworkSparkFX;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockAirLight extends BlockAirBase {

	public BlockAirLight() {

		super(materialBarrier);
	}

	@Override
	public int getLightValue(IBlockAccess world, int x, int y, int z) {

		return world.getBlockMetadata(x, y, z);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void randomDisplayTick(World world, int x, int y, int z, Random rand) {

		EntityFireworkSparkFX spark = new EntityFireworkSparkFX(world, x + 0.5 + rand.nextGaussian() * 0.6,
				y + 0.5 + rand.nextGaussian() * 0.55,
				z + 0.5 + rand.nextGaussian() * 0.55,
				0, 0, 0, Minecraft.getMinecraft().effectRenderer) {
			@Override
			public void moveEntity(double x, double y, double z) {}
		};
		spark.setColour(0xFFFF66);
		spark.setFadeColour(0xFFFFAA);
		Minecraft.getMinecraft().effectRenderer.addEffect(spark);
	}

}
