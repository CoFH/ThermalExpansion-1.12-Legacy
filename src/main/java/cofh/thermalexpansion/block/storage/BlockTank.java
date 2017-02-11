package cofh.thermalexpansion.block.storage;

import codechicken.lib.model.blockbakery.BlockBakery;
import codechicken.lib.model.blockbakery.IBakeryBlock;
import codechicken.lib.model.blockbakery.ICustomBlockBakery;
import cofh.api.core.IModelRegister;
import cofh.lib.util.helpers.FluidHelper;
import cofh.thermalexpansion.block.BlockTEBase;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class BlockTank extends BlockTEBase implements IBakeryBlock, IModelRegister {

	public BlockTank() {

		super(Material.GLASS);

		setUnlocalizedName("tank");

		setHardness(15.0F);
		setResistance(25.0F);
	}

	@Override
	@SideOnly (Side.CLIENT)
	public void getSubBlocks(@Nonnull Item item, CreativeTabs tab, List<ItemStack> list) {

		if (enable) {
			list.add(ItemBlockTank.setDefaultTag(new ItemStack(item, 1, 0)));
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
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase living, ItemStack stack) {

		if (stack.getTagCompound() != null) {
			//			TileTank tile = (TileTank) world.getTileEntity(pos);
			//
			//			tile.setLevel(stack.getTagCompound().getByte("Level"));
		}
		super.onBlockPlacedBy(world, pos, state, living, stack);
	}

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

		return side == EnumFacing.UP || side == EnumFacing.DOWN;
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, @Nullable ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {

		TileEntity tile = world.getTileEntity(pos);

		if (tile != null) {
			IFluidHandler handler = tile.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null);
			return FluidHelper.interactWithHandler(heldItem, handler, player, hand) || FluidHelper.isFluidHandler(heldItem);
		}
		return super.onBlockActivated(world, pos, state, player, hand, heldItem, side, hitX, hitY, hitZ);
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {

		return new AxisAlignedBB(0.125F, 0F, 0.125F, 0.875F, 1F, 0.875F);
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

		this.setRegistryName("tank");
		GameRegistry.register(this);

		ItemBlockTank itemBlock = new ItemBlockTank(this);
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
	public static ItemStack tank;

}
