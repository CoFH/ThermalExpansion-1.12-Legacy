package cofh.thermalexpansion.block.simple;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleFirework;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Random;

public class BlockAirSignal extends BlockAirBase /*implements IRedNetOutputNode*/ {

    public BlockAirSignal() {

        super(materialBarrier);
        disableStats();
        //setBlockTextureName("glowstone");
        //setBlockBounds(0, 0, 0, 0, 0, 0);
    }

    @Override
    public int getWeakPower(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
        return getMetaFromState(blockState);
    }

    @Override
    public boolean canProvidePower(IBlockState state) {
        return true;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void randomDisplayTick(IBlockState state, World world, BlockPos pos, Random rand) {

        ParticleFirework.Spark spark = new ParticleFirework.Spark(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, rand.nextGaussian() / 10, rand.nextDouble() / 6, rand.nextGaussian() / 10, Minecraft.getMinecraft().effectRenderer);
        spark.setColor(0xFF0000);
        spark.setColorFade(0x660000);
        spark.setTwinkle(true);
        Minecraft.getMinecraft().effectRenderer.addEffect(spark);
    }

	/* IRedNetOutputNode */
    /*@Override
	public RedNetConnectionType getConnectionType(World world, int x, int y, int z, ForgeDirection side) {

		return RedNetConnectionType.PlateSingle;
	}

	@Override
	public int[] getOutputValues(World world, int x, int y, int z, ForgeDirection side) {

		return null;
	}

	@Override
	public int getOutputValue(World world, int x, int y, int z, ForgeDirection side, int subnet) {

		return isProvidingWeakPower(world, x, y, z, side.ordinal());
	}*/

}
