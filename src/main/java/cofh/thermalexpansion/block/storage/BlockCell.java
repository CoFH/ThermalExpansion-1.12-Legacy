package cofh.thermalexpansion.block.storage;

import codechicken.lib.model.ModelRegistryHelper;
import codechicken.lib.model.bakery.CCBakeryModel;
import codechicken.lib.model.bakery.IBakeryProvider;
import codechicken.lib.model.bakery.ModelBakery;
import codechicken.lib.model.bakery.ModelErrorStateProperty;
import codechicken.lib.model.bakery.generation.IBakery;
import cofh.core.init.CoreEnchantments;
import cofh.core.render.IModelRegister;
import cofh.core.util.StateMapper;
import cofh.core.util.helpers.BlockHelper;
import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalexpansion.block.BlockTEBase;
import cofh.thermalexpansion.init.TEProps;
import cofh.thermalexpansion.item.ItemFrame;
import cofh.thermalexpansion.render.BakeryCell;
import cofh.thermalexpansion.util.helpers.ReconfigurableHelper;
import cofh.thermalfoundation.init.TFProps;
import cofh.thermalfoundation.item.ItemMaterial;
import cofh.thermalfoundation.item.ItemUpgrade;
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

import static cofh.core.util.helpers.RecipeHelper.*;

public class BlockCell extends BlockTEBase implements IModelRegister, IBakeryProvider {

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
		builder.add(ModelErrorStateProperty.ERROR_STATE);
		builder.add(TEProps.TILE_CELL);

		return builder.build();
	}

	@Override
	public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> items) {

		if (enable) {
			if (TEProps.creativeTabShowAllLevels) {
				for (int j = 0; j <= TFProps.LEVEL_MAX; j++) {
					items.add(itemBlock.setDefaultTag(new ItemStack(this), j));
				}
			} else {
				items.add(itemBlock.setDefaultTag(new ItemStack(this), TEProps.creativeTabLevel));
			}
			if (TEProps.creativeTabShowCreative) {
				items.add(itemBlock.setCreativeTag(new ItemStack(this)));
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

	/* IBakeryProvider */
	@Override
	@SideOnly (Side.CLIENT)
	public IBakery getBakery() {

		return BakeryCell.INSTANCE;
	}

	/* IModelRegister */
	@Override
	@SideOnly (Side.CLIENT)
	public void registerModels() {

		StateMapper mapper = new StateMapper("thermalexpansion", "cell", "cell");
		ModelLoader.setCustomModelResourceLocation(itemBlock, 0, mapper.location);
		ModelLoader.setCustomStateMapper(this, mapper);
		ModelLoader.setCustomMeshDefinition(itemBlock, mapper);
		ModelRegistryHelper.register(mapper.location, new CCBakeryModel());

		ModelBakery.registerBlockKeyGenerator(this, state -> {

			StringBuilder builder = new StringBuilder(ModelBakery.defaultBlockKeyGenerator.generateKey(state));
			TileCell cell = state.getValue(TEProps.TILE_CELL);
			builder.append(",creative=").append(cell.isCreative);
			builder.append(",level=").append(cell.getLevel());
			builder.append(",holding=").append(cell.enchantHolding);
			builder.append(",facing=").append(cell.getFacing());
			builder.append(",scale=").append(cell.getLightValue());
			builder.append(",side_config{");
			for (int i : cell.sideCache) {
				builder.append(",").append(i);
			}
			builder.append("}");
			return builder.toString();
		});

		ModelBakery.registerItemKeyGenerator(itemBlock, stack -> ModelBakery.defaultItemKeyGenerator.generateKey(stack) + ",creative=" + itemBlock.isCreative(stack) + ",level=" + itemBlock.getLevel(stack));
	}

	/* IInitializer */
	@Override
	public boolean initialize() {

		this.setRegistryName("cell");
		ForgeRegistries.BLOCKS.register(this);

		itemBlock = new ItemBlockCell(this);
		itemBlock.setRegistryName(this.getRegistryName());
		ForgeRegistries.ITEMS.register(itemBlock);

		TileCell.initialize();

		ThermalExpansion.proxy.addIModelRegister(this);

		return true;
	}

	@Override
	public boolean register() {

		cell = new ItemStack[5];

		for (int i = 0; i < 5; i++) {
			cell[i] = itemBlock.setDefaultTag(new ItemStack(this), i);
		}
		cellCreative = itemBlock.setCreativeTag(new ItemStack(this));

		addRecipes();
		addUpgradeRecipes();
		addClassicRecipes();

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
					'C', ItemFrame.frameCell0,
					'I', "ingotLead",
					'P', ItemMaterial.powerCoilElectrum,
					'X', Blocks.REDSTONE_BLOCK
			);
		}
		// @formatter:on
	}

	private void addUpgradeRecipes() {

		if (!enableUpgradeKitCrafting || !enable) {
			return;
		}
		if (!enableClassicRecipes) {
			for (int j = 0; j < 4; j++) {
				addShapelessUpgradeKitRecipe(cell[j + 1], cell[j], ItemUpgrade.upgradeIncremental[j]);
			}
			for (int j = 1; j < 4; j++) {
				for (int k = 0; k <= j; k++) {
					addShapelessUpgradeKitRecipe(cell[j + 1], cell[k], ItemUpgrade.upgradeFull[j]);
				}
			}
		}
		for (int j = 0; j < 5; j++) {
			addShapelessUpgradeKitRecipe(cellCreative, cell[j], ItemUpgrade.upgradeCreative);
		}
	}

	private void addClassicRecipes() {

		if (!enableClassicRecipes || !enable) {
			return;
		}
		// @formatter:off
		addShapedRecipe(cell[1],
				"YXY",
				"ICI",
				"YPY",
				'C', ItemFrame.frameCell0,
				'I', "ingotLead",
				'P', ItemMaterial.powerCoilElectrum,
				'X', Blocks.REDSTONE_BLOCK,
				'Y', "ingotInvar"
		);
		addShapedRecipe(cell[1],
				" X ",
				"ICI",
				" P ",
				'C', ItemFrame.frameCell1,
				'I', "ingotLead",
				'P', ItemMaterial.powerCoilElectrum,
				'X', Blocks.REDSTONE_BLOCK
		);
		addShapedRecipe(cell[2],
				" X ",
				"ICI",
				" P ",
				'C', ItemFrame.frameCell2Filled,
				'I', "ingotLead",
				'P', ItemMaterial.powerCoilElectrum,
				'X', "ingotSilver"
		);
		addShapedRecipe(cell[3],
				" X ",
				"ICI",
				" P ",
				'C', ItemFrame.frameCell3Filled,
				'I', "ingotLead",
				'P', ItemMaterial.powerCoilElectrum,
				'X', "ingotSilver"
		);
		addShapedRecipe(cell[4],
				" X ",
				"ICI",
				" P ",
				'C', ItemFrame.frameCell4Filled,
				'I', "ingotLead",
				'P', ItemMaterial.powerCoilElectrum,
				'X', "ingotSilver"
		);

		addShapedUpgradeRecipe(cell[1],
				" I ",
				"ICI",
				" I ",
				'C', cell[0],
				'I', "ingotInvar"
		);
		addShapedUpgradeRecipe(cell[3],
				" I ",
				"ICI",
				" I ",
				'C', cell[2],
				'I', "ingotSignalum"
		);
		addShapedUpgradeRecipe(cell[4],
				" I ",
				"ICI",
				" I ",
				'C', cell[3],
				'I', "ingotEnderium"
		);
		// @formatter:on
	}

	public static boolean enable = true;
	public static boolean enableCreative = true;
	public static boolean enableSecurity = true;

	public static boolean enableClassicRecipes = false;
	public static boolean enableUpgradeKitCrafting = true;

	/* REFERENCES */
	public static ItemStack cell[];
	public static ItemStack cellCreative;
	public static ItemBlockCell itemBlock;

}
