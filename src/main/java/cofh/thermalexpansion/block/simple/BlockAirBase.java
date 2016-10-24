package cofh.thermalexpansion.block.simple;

import cofh.core.block.BlockCoFHBase;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.item.ItemStack;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockAirBase extends Block {

	public static final Material materialBarrier = new Material(MapColor.AIR).setReplaceable();

	public BlockAirBase(Material material) {

		super(material);
		disableStats();
		//setBlockTextureName("glowstone");
		//setBlockBounds(0, 0, 0, 0, 0, 0);
	}

	@Override
	public EnumBlockRenderType getRenderType(IBlockState state) {
		return EnumBlockRenderType.INVISIBLE;
	}

    @Override
    public boolean isAir(IBlockState state, IBlockAccess world, BlockPos pos) {
        return true;
    }

	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean isNormalCube(IBlockState state) {

		return false;
	}

    @Override
    public boolean isReplaceable(IBlockAccess worldIn, BlockPos pos) {
        return super.isReplaceable(worldIn, pos);
    }

    @Override
    public void randomDisplayTick(IBlockState stateIn, World worldIn, BlockPos pos, Random rand) {
    }

	//@Override
	//@SideOnly(Side.CLIENT)
	//public void registerBlockIcons(IIconRegister ir) {
	//}

    @Override
    public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
        return BlockCoFHBase.NO_DROP;
    }

    @Override
    public void dropBlockAsItemWithChance(World worldIn, BlockPos pos, IBlockState state, float chance, int fortune) {
    }

}
