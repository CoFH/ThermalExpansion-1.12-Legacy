package cofh.thermalexpansion.block.storage;

import codechicken.lib.model.ModelRegistryHelper;
import codechicken.lib.model.blockbakery.BlockBakery;
import codechicken.lib.model.blockbakery.CCBakeryModel;
import codechicken.lib.model.blockbakery.IBlockStateKeyGenerator;
import codechicken.lib.model.blockbakery.IItemStackKeyGenerator;
import codechicken.lib.texture.IWorldBlockTextureProvider;
import codechicken.lib.texture.TextureUtils;
import cofh.api.core.IModelRegister;
import cofh.core.util.StateMapper;
import cofh.thermalexpansion.block.BlockTEBase;
import cofh.thermalexpansion.block.machine.TileMachineBase;
import cofh.thermalexpansion.init.TEProps;
import cofh.thermalexpansion.init.TETextures;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import java.util.List;

public class BlockCache extends BlockTEBase implements IModelRegister, IWorldBlockTextureProvider {

	public BlockCache() {

		super(Material.IRON);

		setUnlocalizedName("cache");

		setHardness(15.0F);
		setResistance(25.0F);
	}

	@Override
	protected BlockStateContainer createBlockState() {

		BlockStateContainer.Builder builder = new BlockStateContainer.Builder(this);
		// UnListed
		// builder.add(TEProps.CREATIVE);
		builder.add(TEProps.LEVEL);
		builder.add(TEProps.SCALE);
		builder.add(TEProps.FACING);

		return builder.build();
	}

	@Override
	@SideOnly (Side.CLIENT)
	public void getSubBlocks(@Nonnull Item item, CreativeTabs tab, List<ItemStack> list) {

		if (enable) {
			list.add(ItemBlockCache.setDefaultTag(new ItemStack(item, 1, 0)));
		}
		for (int i = 0; i < 5; i++) {
			list.add(ItemBlockCache.setDefaultTag(new ItemStack(item, 1, 0), i));
		}
	}

	/* ITileEntityProvider */
	@Override
	public TileEntity createNewTileEntity(World world, int metadata) {

		return new TileCache();
	}

	/* BLOCK METHODS */
	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase living, ItemStack stack) {

		super.onBlockPlacedBy(world, pos, state, living, stack);
	}

		@Override
	public boolean hasComparatorInputOverride(IBlockState state) {

		return true;
	}

	@Override
	public boolean isNormalCube(IBlockState state, IBlockAccess world, BlockPos pos) {

		return false;
	}

	@Override
	public boolean isOpaqueCube(IBlockState state) {

		return true;
	}

	@Override
	public boolean isSideSolid(IBlockState base_state, IBlockAccess world, BlockPos pos, EnumFacing side) {

		return true;
	}

	/* RENDERING METHODS */
	@Override
	@SideOnly (Side.CLIENT)
	public boolean canRenderInLayer(IBlockState state, BlockRenderLayer layer) {

		return layer == BlockRenderLayer.SOLID || layer == BlockRenderLayer.CUTOUT;
	}

	@Override
	@SideOnly (Side.CLIENT)
	public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos) {

		return BlockBakery.handleExtendedState((IExtendedBlockState) super.getExtendedState(state, world, pos), world.getTileEntity(pos));
	}

	@Override // Inventory
	@SideOnly (Side.CLIENT)
	public TextureAtlasSprite getTexture(EnumFacing side, int metadata) {

		if (side == EnumFacing.DOWN) {
			return TETextures.CACHE_BOTTOM[0];
		}
		if (side == EnumFacing.UP) {
			return TETextures.CACHE_TOP[0];
		}
		return side != EnumFacing.NORTH ? TETextures.CACHE_SIDE[0] : TETextures.CACHE_FACE[0];
	}

	@Override // World
	@SideOnly (Side.CLIENT)
	public TextureAtlasSprite getTexture(EnumFacing side, IBlockState state, BlockRenderLayer layer, IBlockAccess world, BlockPos pos) {

		TileEntity tileEntity = world.getTileEntity(pos);
		if (tileEntity instanceof TileMachineBase) {
			TileCache tile = ((TileCache) tileEntity);
			return tile.getTexture(side.ordinal(), layer == BlockRenderLayer.SOLID ? 0 : 1, 0);
		}
		return TextureUtils.getMissingSprite();
	}

	/* IModelRegister */
	@Override
	@SideOnly (Side.CLIENT)
	public void registerModels() {

		StateMapper mapper = new StateMapper("thermalexpansion", "cell", "cell");
		ModelLoader.setCustomModelResourceLocation(itemBlock, 0, mapper.location);
		ModelLoader.setCustomStateMapper(this, mapper);
		ModelLoader.setCustomMeshDefinition(itemBlock, mapper);
		ModelRegistryHelper.register(mapper.location, new CCBakeryModel(""));//TODO override particles.

		BlockBakery.registerBlockKeyGenerator(this, new IBlockStateKeyGenerator() {
			@Override
			public String generateKey(IExtendedBlockState state) {

				StringBuilder builder = new StringBuilder(BlockBakery.defaultBlockKeyGenerator.generateKey(state));
				builder.append(",level=").append(state.getValue(TEProps.LEVEL));
				builder.append(",side_config{");
				for (int i : state.getValue(TEProps.SIDE_CONFIG_RAW)) {
					builder.append(",").append(i);
				}
				builder.append("}");
				builder.append(",facing=").append(state.getValue(TEProps.FACING));
				builder.append(",meter_level").append(state.getValue(TEProps.SCALE));
				return builder.toString();
			}
		});

		BlockBakery.registerItemKeyGenerator(itemBlock, new IItemStackKeyGenerator() {
			@Override
			public String generateKey(ItemStack stack) {

				return BlockBakery.defaultItemKeyGenerator.generateKey(stack) + ",level=" + ItemBlockCell.getLevel(stack);
			}
		});
	}

	/* IInitializer */
	@Override
	public boolean preInit() {

		this.setRegistryName("cache");
		GameRegistry.register(this);

		itemBlock = new ItemBlockCache(this);
		itemBlock.setRegistryName(this.getRegistryName());
		GameRegistry.register(itemBlock);

		return true;
	}

	@Override
	public boolean initialize() {

		TileCache.initialize();

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
	public static ItemStack cache;
	public static ItemBlockCache itemBlock;

}
