package cofh.thermalexpansion.block.simple;

import cofh.core.block.BlockCoFHBase;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockAirBase extends Block {

	public static final Material materialBarrier = new Material(MapColor.airColor).setReplaceable();

	public BlockAirBase(Material material) {

		super(material);
		disableStats();
		setBlockTextureName("glowstone");
		setBlockBounds(0, 0, 0, 0, 0, 0);
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {

		return null;
	}

	@Override
	public int getRenderType() {

		return -1;
	}

	@Override
	public boolean isAir(IBlockAccess world, int x, int y, int z) {

		return true;
	}

	@Override
	public boolean isOpaqueCube() {

		return false;
	}

	@Override
	public boolean isNormalCube() {

		return false;
	}

	@Override
	public boolean isReplaceable(IBlockAccess world, int x, int y, int z) {

		return true;
	}

	@Override
	public boolean renderAsNormalBlock() {

		return false;
	}

	@Override
	public void randomDisplayTick(World world, int x, int y, int z, Random rand) {

	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister ir) {

	}

	@Override
	public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int metadata, int fortune) {

		return BlockCoFHBase.NO_DROP;
	}

	@Override
	public void dropBlockAsItemWithChance(World world, int x, int y, int z, int meta, float chance, int fortune) {

		return;
	}

}
