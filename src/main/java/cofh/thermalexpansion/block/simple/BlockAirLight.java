package cofh.thermalexpansion.block.simple;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.particle.ParticleFirework;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Random;

import net.minecraft.client.Minecraft;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockAirLight extends BlockAirBase {

	public BlockAirLight() {

		super(materialBarrier);
	}

    @Override
    public int getLightValue(IBlockState state, IBlockAccess world, BlockPos pos) {
		return getMetaFromState(state);
	}

	@SideOnly(Side.CLIENT)
    @Override
    public void randomDisplayTick(IBlockState stateIn, World world, BlockPos pos, Random rand) {
        ParticleFirework.Spark spark = new ParticleFirework.Spark(world, pos.getX() + 0.5 + rand.nextGaussian() * 0.6, pos.getY() + 0.5 + rand.nextGaussian() * 0.55, pos.getZ() + 0.5
				+ rand.nextGaussian() * 0.55, 0, 0, 0, Minecraft.getMinecraft().effectRenderer) {

			@Override
			public void moveEntity(double x, double y, double z) {

			}
		};
		spark.setColor(0xFFFF66);
		spark.setColorFade(0xFFFFAA);
		Minecraft.getMinecraft().effectRenderer.addEffect(spark);
	}

}
