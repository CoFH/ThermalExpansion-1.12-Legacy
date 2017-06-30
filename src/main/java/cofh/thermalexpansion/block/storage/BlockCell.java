package cofh.thermalexpansion.block.storage;

import codechicken.lib.model.ModelRegistryHelper;
import codechicken.lib.model.bakery.CCBakeryModel;
import codechicken.lib.model.bakery.IBakeryProvider;
import codechicken.lib.model.bakery.ModelBakery;
import codechicken.lib.model.bakery.generation.IBakery;
import cofh.core.init.CoreEnchantments;
import cofh.core.render.IModelRegister;
import cofh.core.util.StateMapper;
import cofh.lib.util.helpers.BlockHelper;
import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalexpansion.block.BlockTEBase;
import cofh.thermalexpansion.init.TEProps;
import cofh.thermalexpansion.item.ItemFrame;
import cofh.thermalexpansion.render.RenderCell;
import cofh.thermalexpansion.util.helpers.ReconfigurableHelper;
import cofh.thermalfoundation.item.ItemMaterial;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import static cofh.lib.util.helpers.RecipeHelper.addShapedRecipe;

public class BlockCell extends BlockTEBase implements IBakeryProvider, IModelRegister {

	public BlockCell() {

		super(Material.IRON);

		setUnlocalizedName("cell");

		setHardness(15.0F);
		setResistance(25.0F);
		setDefaultState(getBlockState().getBaseState());
	}

	@Override
	protected BlockStateContainer createBlockState() {

		BlockStateContainer.Builder builder = new BlockStateContainer.Builder(this);
		// UnListed
		builder.add(TEProps.CREATIVE);
		builder.add(TEProps.LEVEL);
		builder.add(TEProps.HOLDING);
		builder.add(TEProps.FACING);
		builder.add(TEProps.SIDE_CONFIG);
		builder.add(TEProps.SCALE);

		return builder.build();
	}

	@Override
	public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> items) {

		if (enable) {
			if (TEProps.creativeTabShowAllLevels) {
				for (int j = 0; j < 5; j++) {
					items.add(itemBlock.setDefaultTag(new ItemStack(this, 1, 0), j));
				}
			} else {
				items.add(itemBlock.setDefaultTag(new ItemStack(this, 1, 0), TEProps.creativeTabLevel));
			}
			if (TEProps.creativeTabShowCreative) {
				items.add(itemBlock.setCreativeTag(new ItemStack(this, 1, 0), 4));
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

			tile.isCreative = (stack.getTagCompound().getBoolean("Creative"));
			tile.enchantHolding = (byte) EnchantmentHelper.getEnchantmentLevel(CoreEnchantments.holding, stack);
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

		return false;
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
			if (tile.enchantHolding > 0) {
				CoreEnchantments.addEnchantment(retTag, CoreEnchantments.holding, tile.enchantHolding);
			}
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

		return ModelBakery.handleExtendedState((IExtendedBlockState) super.getExtendedState(state, world, pos), world, pos);
	}

	/* IBakeryBlock */
	@Override
	@SideOnly (Side.CLIENT)
	public IBakery getBakery() {

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
		ModelRegistryHelper.register(mapper.location, new CCBakeryModel("thermalexpansion:blocks/storage/cell_side_0"));

		ModelBakery.registerBlockKeyGenerator(this, state -> {

			StringBuilder builder = new StringBuilder(ModelBakery.defaultBlockKeyGenerator.generateKey(state));
			builder.append(",creative=").append(state.getValue(TEProps.CREATIVE));
			builder.append(",level=").append(state.getValue(TEProps.LEVEL));
			builder.append(",holding=").append(state.getValue(TEProps.HOLDING));
			builder.append(",facing=").append(state.getValue(TEProps.FACING));
			builder.append(",scale=").append(state.getValue(TEProps.SCALE));
			builder.append(",side_config{");
			for (int i : state.getValue(TEProps.SIDE_CONFIG)) {
				builder.append(",").append(i);
			}
			builder.append("}");
			return builder.toString();
		});

		ModelBakery.registerItemKeyGenerator(itemBlock, stack -> ModelBakery.defaultItemKeyGenerator.generateKey(stack) + ",creative=" + itemBlock.isCreative(stack) + ",level=" + itemBlock.getLevel(stack));
	}

	/* IInitializer */
	@Override
	public boolean preInit() {

		this.setRegistryName("cell");
		ForgeRegistries.BLOCKS.register(this);

		itemBlock = new ItemBlockCell(this);
		itemBlock.setRegistryName(this.getRegistryName());
		ForgeRegistries.ITEMS.register(itemBlock);

		ThermalExpansion.proxy.addIModelRegister(this);

		return true;
	}

	@Override
	public boolean initialize() {

		TileCell.initialize();

		cell = new ItemStack[5];

		for (int i = 0; i < 5; i++) {
			cell[i] = itemBlock.setDefaultTag(new ItemStack(this), i);
		}
		addRecipes();

		return true;
	}

	@Override
	public boolean postInit() {

		return true;
	}

	/* HELPERS */
	private void addRecipes() {

		// @formatter:off
		if (enable) {
			addShapedRecipe(cell[0],
					" X ",
					"ICI",
					" P ",
					'C', ItemFrame.frameCell,
					'I', "ingotLead",
					'P', ItemMaterial.powerCoilElectrum,
					'X', Blocks.REDSTONE_BLOCK
			);
		}
		// @formatter:on
	}

	public static boolean enable;

	/* REFERENCES */
	public static ItemStack cell[];
	public static ItemBlockCell itemBlock;

}
