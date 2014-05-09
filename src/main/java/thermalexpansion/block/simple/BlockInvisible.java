package thermalexpansion.block.simple;

import cofh.api.core.IInitializer;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.Random;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockInvisible extends BlockContainer implements IInitializer {

	public BlockInvisible(int id) {

		super(Material.air);
		disableStats();
		setTextureName("glowstone");
		setBlockBounds(0, 0, 0, 0, 0, 0);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int metadata) {

		return new TileInvisible();
	}

	@Override
	public boolean canProvidePower() {

		return true;
	}

	@Override
	public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int metadata, int fortune) {

		ArrayList<ItemStack> ret = new ArrayList<ItemStack>();
		return ret;
	}

	@Override
	public int getLightValue(IBlockAccess world, int x, int y, int z) {

		TileInvisible tile = (TileInvisible) world.getTileEntity(x, y, z);

		if (tile != null) {
			return tile.light;
		}
		return 0;
	}

	@Override
	public int isProvidingWeakPower(IBlockAccess world, int x, int y, int z, int side) {

		TileInvisible tile = (TileInvisible) world.getTileEntity(x, y, z);

		if (tile != null) {
			return tile.signal;
		}
		return 0;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void randomDisplayTick(World world, int x, int y, int z, Random rand) {

		TileInvisible tile = (TileInvisible) world.getTileEntity(x, y, z);

		if (tile != null) {

		}
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {

		return null;
	}

	@Override
	public boolean isOpaqueCube() {

		return false;
	}

	@Override
	public boolean renderAsNormalBlock() {

		return false;
	}

	@Override
	public int getRenderType() {

		return -1;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister ir) {

	}

	/* IIinitializer */
	@Override
	public boolean preInit() {

		return true;
	}

	@Override
	public boolean initialize() {

		return true;
	}

	@Override
	public boolean postInit() {

		TileInvisible.initialize();
		return true;
	}

}
