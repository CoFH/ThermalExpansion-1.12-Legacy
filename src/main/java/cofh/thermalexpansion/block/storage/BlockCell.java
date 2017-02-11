package cofh.thermalexpansion.block.storage;

import codechicken.lib.model.blockbakery.BlockBakery;
import codechicken.lib.model.blockbakery.IBakeryBlock;
import codechicken.lib.model.blockbakery.ICustomBlockBakery;
import cofh.api.core.IModelRegister;
import cofh.thermalexpansion.block.BlockTEBase;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import java.util.List;

public class BlockCell extends BlockTEBase implements IBakeryBlock, IModelRegister {

	public BlockCell() {

		super(Material.IRON);

		setUnlocalizedName("cell");

		setHardness(15.0F);
		setResistance(25.0F);
	}

	@Override
	@SideOnly (Side.CLIENT)
	public void getSubBlocks(@Nonnull Item item, CreativeTabs tab, List<ItemStack> list) {

		if (enable) {
			list.add(ItemBlockCell.setDefaultTag(new ItemStack(item, 1, 0)));
		}
	}

	/* ITileEntityProvider */
	@Override
	public TileEntity createNewTileEntity(World world, int metadata) {

		if (metadata >= 1) {
			return null;
		}
		return null;
	}

	/* BLOCK METHODS */
	@Override
	public boolean canRenderInLayer(IBlockState state, BlockRenderLayer layer) {

		return layer == BlockRenderLayer.CUTOUT || layer == BlockRenderLayer.TRANSLUCENT;
	}

	@Override
	public boolean hasComparatorInputOverride(IBlockState state) {

		return true;
	}

	@Override
	public boolean isFullCube(IBlockState state) {

		return false;
	}

	@Override
	public boolean isSideSolid(IBlockState base_state, IBlockAccess world, BlockPos pos, EnumFacing side) {

		return true;
	}

	/* RENDERING METHODS */
	@Override
	@SideOnly (Side.CLIENT)
	public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos) {

		return BlockBakery.handleExtendedState((IExtendedBlockState) super.getExtendedState(state, world, pos), world.getTileEntity(pos));
	}

	/* IBakeryBlock */
	@Override
	@SideOnly (Side.CLIENT)
	public ICustomBlockBakery getCustomBakery() {

		return null;
	}

	/* IModelRegister */
	@Override
	@SideOnly (Side.CLIENT)
	public void registerModels() {

	}

	/* IInitializer */
	@Override
	public boolean preInit() {

		this.setRegistryName("cell");
		GameRegistry.register(this);

		ItemBlockCell itemBlock = new ItemBlockCell(this);
		itemBlock.setRegistryName(this.getRegistryName());
		GameRegistry.register(itemBlock);

		return true;
	}

	@Override
	public boolean initialize() {

		return true;
	}

	@Override
	public boolean postInit() {

		// @formatter:off

		// @formatter:on

		return true;
	}

	public static boolean enable;

	/* REFERENCES */
	public static ItemStack cell;

}
