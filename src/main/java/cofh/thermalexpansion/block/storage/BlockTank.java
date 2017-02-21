package cofh.thermalexpansion.block.storage;

import codechicken.lib.block.property.unlisted.UnlistedFluidStackProperty;
import codechicken.lib.model.ModelRegistryHelper;
import codechicken.lib.model.blockbakery.*;
import cofh.api.core.IModelRegister;
import cofh.core.util.StateMapper;
import cofh.lib.util.helpers.FluidHelper;
import cofh.thermalexpansion.block.BlockTEBase;
import cofh.thermalexpansion.init.TEProps;
import cofh.thermalexpansion.render.RenderTank;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.fluids.FluidStack;
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
	protected BlockStateContainer createBlockState() {

		BlockStateContainer.Builder builder = new BlockStateContainer.Builder(this);

		// UnListed
		builder.add(FLUID_STACK);
		builder.add(TEProps.ACTIVE);
		builder.add(TEProps.LEVEL);

		return builder.build();
	}

	@Override
	@SideOnly (Side.CLIENT)
	public void getSubBlocks(@Nonnull Item item, CreativeTabs tab, List<ItemStack> list) {

		if (enable) {
			list.add(ItemBlockTank.setDefaultTag(new ItemStack(item, 1, 0)));
		}
		for (int i = 0; i < 5; i++) {
			list.add(ItemBlockTank.setDefaultTag(new ItemStack(item, 1, 0), i));
		}
	}

	/* ITileEntityProvider */
	@Override
	public TileEntity createNewTileEntity(World world, int metadata) {

		if (metadata >= 1) {
			return null;
		}
		return new TileTank();
	}

	/* BLOCK METHODS */
	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase living, ItemStack stack) {

		if (stack.getTagCompound() != null) {
			TileTank tile = (TileTank) world.getTileEntity(pos);

			tile.setLevel(stack.getTagCompound().getByte("Level"));

			FluidStack fluid = FluidStack.loadFluidStackFromNBT(stack.getTagCompound().getCompoundTag("Fluid"));

			if (fluid != null) {
				tile.getTank().setFluid(fluid);
			}
		}
		super.onBlockPlacedBy(world, pos, state, living, stack);
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
	public boolean isNormalCube(IBlockState state, IBlockAccess world, BlockPos pos) {

		return false;
	}

	@Override
	public boolean isOpaqueCube(IBlockState state) {

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
			if (FluidHelper.isFluidHandler(heldItem) && FluidHelper.interactWithHandler(heldItem, handler, player, hand)) {
				return true;
			}
		}
		return super.onBlockActivated(world, pos, state, player, hand, heldItem, side, hitX, hitY, hitZ);
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {

		return new AxisAlignedBB(0.125F, 0F, 0.125F, 0.875F, 1F, 0.875F);
	}

	/* HELPERS */
	@Override
	public NBTTagCompound getItemStackTag(IBlockAccess world, BlockPos pos) {

		NBTTagCompound retTag = super.getItemStackTag(world, pos);
		TileTank tile = (TileTank) world.getTileEntity(pos);

		if (tile != null) {
			FluidStack fluid = tile.getTankFluid();
			if (fluid != null) {
				retTag.setTag("Fluid", fluid.writeToNBT(new NBTTagCompound()));
			}
		}
		return retTag;
	}

	/* RENDERING METHODS */
	@Override
	public boolean canRenderInLayer(IBlockState state, BlockRenderLayer layer) {

		return layer == BlockRenderLayer.CUTOUT || layer == BlockRenderLayer.TRANSLUCENT;
	}

	@Override
	@SideOnly (Side.CLIENT)
	public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos) {

		return BlockBakery.handleExtendedState((IExtendedBlockState) super.getExtendedState(state, world, pos), world.getTileEntity(pos));
	}

	/* IBakeryBlock */
	@Override
	@SideOnly (Side.CLIENT)
	public ICustomBlockBakery getCustomBakery() {

		return RenderTank.instance;
	}

	/* IModelRegister */
	@Override
	@SideOnly (Side.CLIENT)
	public void registerModels() {

		StateMapper mapper = new StateMapper("thermalexpansion", "tank", "tank");
		ModelLoader.setCustomModelResourceLocation(itemBlock, 0, mapper.location);
		ModelLoader.setCustomStateMapper(this, mapper);
		ModelLoader.setCustomMeshDefinition(itemBlock, mapper);
		ModelRegistryHelper.register(mapper.location, new CCBakeryModel(""));//TODO override particles.
		BlockBakery.registerBlockKeyGenerator(this, new IBlockStateKeyGenerator() {
			@Override
			public String generateKey(IExtendedBlockState state) {
				StringBuilder builder = new StringBuilder(BlockBakery.defaultBlockKeyGenerator.generateKey(state));
				builder.append(",level=").append(state.getValue(TEProps.LEVEL));
				builder.append(",output=").append(TEProps.ACTIVE);
				FluidStack stack = state.getValue(FLUID_STACK);
				if (stack != null) {
					builder.append(",fluid=").append(stack.getFluid().getName());
					builder.append(",amount=").append(stack.amount);
				}

				return builder.toString();
			}
		});
		BlockBakery.registerItemKeyGenerator(itemBlock, new IItemStackKeyGenerator() {
			@Override
			public String generateKey(ItemStack stack) {
				String fluidAppend = "";
				if (stack.getTagCompound() != null) {

					FluidStack fluid = FluidStack.loadFluidStackFromNBT(stack.getTagCompound().getCompoundTag("Fluid"));
					if (fluid != null && fluid.amount > 0) {
						fluidAppend = ",fluid=" + fluid.getFluid().getName() + ",amount=" + fluid.amount;
					}
				}
				return BlockBakery.defaultItemKeyGenerator.generateKey(stack) + ",level=" + ItemBlockCell.getLevel(stack) + fluidAppend;
			}
		});

	}

	/* IInitializer */
	@Override
	public boolean preInit() {

		this.setRegistryName("tank");
		GameRegistry.register(this);

		itemBlock = new ItemBlockTank(this);
		itemBlock.setRegistryName(this.getRegistryName());
		GameRegistry.register(itemBlock);

		return true;
	}

	@Override
	public boolean initialize() {

		TileTank.initialize();

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
	public static ItemBlockTank itemBlock;

	public static final UnlistedFluidStackProperty FLUID_STACK = new UnlistedFluidStackProperty("fluid_stack");

}
