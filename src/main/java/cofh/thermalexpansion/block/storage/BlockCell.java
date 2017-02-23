package cofh.thermalexpansion.block.storage;

import codechicken.lib.model.ModelRegistryHelper;
import codechicken.lib.model.blockbakery.*;
import cofh.api.core.IModelRegister;
import cofh.core.util.StateMapper;
import cofh.lib.util.helpers.BlockHelper;
import cofh.thermalexpansion.block.BlockTEBase;
import cofh.thermalexpansion.init.TEProps;
import cofh.thermalexpansion.item.ItemFrame;
import cofh.thermalexpansion.render.RenderCell;
import cofh.thermalexpansion.util.ReconfigurableHelper;
import cofh.thermalfoundation.item.ItemMaterial;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
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

import static cofh.lib.util.helpers.ItemHelper.ShapedRecipe;
import static cofh.lib.util.helpers.ItemHelper.addRecipe;

public class BlockCell extends BlockTEBase implements IBakeryBlock, IModelRegister {

	public BlockCell() {

		super(Material.IRON);

		setUnlocalizedName("cell");

		setHardness(15.0F);
		setResistance(25.0F);
	}

	@Override
	protected BlockStateContainer createBlockState() {

		BlockStateContainer.Builder builder = new BlockStateContainer.Builder(this);
		// UnListed
		// builder.add(TEProps.CREATIVE);
		builder.add(TEProps.LEVEL);
		// builder.add(TEProps.LIGHT);
		builder.add(TEProps.SCALE);
		builder.add(TEProps.FACING);
		builder.add(TEProps.SIDE_CONFIG_RAW);

		return builder.build();
	}

	@Override
	@SideOnly (Side.CLIENT)
	public void getSubBlocks(@Nonnull Item item, CreativeTabs tab, List<ItemStack> list) {

		if (enable) {
			for (int i = 0; i < 5; i++) {
				list.add(ItemBlockCell.setDefaultTag(new ItemStack(item, 1, 0), i));
			}
		}
	}

	/* ITileEntityProvider */
	@Override
	public TileEntity createNewTileEntity(World world, int metadata) {

		return new TileCell();
	}

	/* BLOCK METHODS */
	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase living, ItemStack stack) {

		if (stack.getTagCompound() != null) {
			TileCell tile = (TileCell) world.getTileEntity(pos);

			tile.setLevel(stack.getTagCompound().getByte("Level"));
			tile.amountRecv = stack.getTagCompound().getInteger("Recv");
			tile.amountSend = stack.getTagCompound().getInteger("Send");
			tile.setEnergyStored(stack.getTagCompound().getInteger("Energy"));

			int facing = BlockHelper.determineXZPlaceFacing(living);
			int storedFacing = ReconfigurableHelper.getFacing(stack);
			byte[] sideCache = ReconfigurableHelper.getSideCache(stack, tile.getDefaultSides());

			tile.sideCache[0] = sideCache[0];
			tile.sideCache[1] = sideCache[1];
			tile.sideCache[facing] = sideCache[storedFacing];
			tile.sideCache[BlockHelper.getLeftSide(facing)] = sideCache[BlockHelper.getLeftSide(storedFacing)];
			tile.sideCache[BlockHelper.getRightSide(facing)] = sideCache[BlockHelper.getRightSide(storedFacing)];
			tile.sideCache[BlockHelper.getOppositeSide(facing)] = sideCache[BlockHelper.getOppositeSide(storedFacing)];
		}
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

	/* HELPERS */
	@Override
	public NBTTagCompound getItemStackTag(IBlockAccess world, BlockPos pos) {

		NBTTagCompound retTag = super.getItemStackTag(world, pos);
		TileCell tile = (TileCell) world.getTileEntity(pos);

		if (tile != null) {
			retTag.setInteger("Recv", tile.amountRecv);
			retTag.setInteger("Send", tile.amountSend);
		}
		return retTag;
	}

	/* RENDERING METHODS */
	@Override
	@SideOnly (Side.CLIENT)
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

		return RenderCell.INSTANCE;
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

		this.setRegistryName("cell");
		GameRegistry.register(this);

		itemBlock = new ItemBlockCell(this);
		itemBlock.setRegistryName(this.getRegistryName());
		GameRegistry.register(itemBlock);

		return true;
	}

	@Override
	public boolean initialize() {

		TileCell.initialize();

		cell = new ItemStack[5];

		for (int i = 0; i < 5; i++) {
			cell[i] = ItemBlockCell.setDefaultTag(new ItemStack(this), i);
		}
		return true;
	}

	@Override
	public boolean postInit() {

		// @formatter:off
		if (enable) {
			addRecipe(ShapedRecipe(cell[0],
					" I ",
					"ICI",
					" P ",
					'C', ItemFrame.frameCell,
					'I', "ingotLead",
					'P', ItemMaterial.powerCoilElectrum
			));
		}
		// @formatter:on

		return true;
	}

	public static boolean enable;

	/* REFERENCES */
	public static ItemStack cell[];
	public static ItemBlockCell itemBlock;

}
