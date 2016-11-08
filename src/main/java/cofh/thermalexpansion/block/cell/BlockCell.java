package cofh.thermalexpansion.block.cell;

import static cofh.lib.util.helpers.ItemHelper.ShapedRecipe;

import codechicken.lib.item.ItemStackRegistry;
import cofh.core.util.CoreUtils;
import cofh.core.util.crafting.RecipeUpgradeOverride;
import cofh.lib.util.helpers.BlockHelper;
import cofh.lib.util.helpers.ItemHelper;
import cofh.lib.util.helpers.StringHelper;
import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalexpansion.block.BlockTEBase;
import cofh.thermalexpansion.block.CommonProperties;
import cofh.thermalexpansion.block.EnumType;
import cofh.thermalexpansion.block.simple.BlockFrame;
import cofh.thermalexpansion.client.IBlockLayerProvider;
import codechicken.lib.block.property.unlisted.UnlistedIntegerProperty;
import cofh.thermalexpansion.client.bakery.BlockBakery;
import cofh.thermalexpansion.client.bakery.IBakeryBlock;
import cofh.thermalexpansion.client.bakery.ICustomBlockBakery;
import cofh.thermalexpansion.core.TEProps;
import cofh.thermalexpansion.item.TEItems;
import cofh.thermalexpansion.render.RenderCell;
import cofh.thermalexpansion.util.crafting.PulverizerManager;
import cofh.thermalexpansion.util.crafting.TECraftingHandler;
import cofh.thermalexpansion.util.helpers.ReconfigurableHelper;
import cofh.thermalfoundation.item.TFItems;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.fml.common.registry.GameRegistry;
import java.util.List;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockCell extends BlockTEBase implements IBlockLayerProvider, IBakeryBlock {

    public static final PropertyEnum<EnumType> TYPES = PropertyEnum.create("type", EnumType.class);

    public static final UnlistedIntegerProperty CHARGE_PROPERTY = new UnlistedIntegerProperty("charge");

	public BlockCell() {

		super(Material.IRON);
		setHardness(20.0F);
		setResistance(120.0F);
		setUnlocalizedName("thermalexpansion.cell");
        setDefaultState(getDefaultState().withProperty(TYPES, EnumType.BASIC));
	}

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(TYPES).ordinal();
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(TYPES, EnumType.fromMeta(meta));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos) {
        return BlockBakery.handleExtendedState((IExtendedBlockState) state, world.getTileEntity(pos));
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new ExtendedBlockState.Builder(this).add(TYPES).add(CommonProperties.SPRITE_FACE_LAYER_PROPERTY).add(CommonProperties.TYPE_PROPERTY).add(CHARGE_PROPERTY).add(CommonProperties.FACING_PROPERTY).add(CommonProperties.ACTIVE_SPRITE_PROPERTY).build();
    }

	@Override
	public TileEntity createNewTileEntity(World world, int metadata) {
		if (metadata >= EnumType.values().length) {
			return null;
		}
		EnumType type = EnumType.fromMeta(metadata);
		if (type == EnumType.CREATIVE) {
			if (!enable[EnumType.CREATIVE.meta()]) {
				return null;
			}
			return new TileCellCreative(metadata);
		}
		return new TileCell(metadata);
	}

	@Override
	public void getSubBlocks(Item item, CreativeTabs tab, List<ItemStack> list) {

		if (enable[0]) {
			list.add(ItemBlockCell.setDefaultTag(new ItemStack(item, 1, 0), -1));
		}
		for (int i = 1; i < EnumType.values().length; i++) {
			list.add(ItemBlockCell.setDefaultTag(new ItemStack(item, 1, i), 0));
			list.add(ItemBlockCell.setDefaultTag(new ItemStack(item, 1, i), TileCell.CAPACITY[i]));
		}
	}

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase living, ItemStack stack) {
		if (getMetaFromState(state) == 0 && !enable[0]) {
			world.setBlockToAir(pos);
			return;
		}
		if (stack.getTagCompound() != null) {
			TileCell tile = (TileCell) world.getTileEntity(pos);

			tile.setEnergyStored(stack.getTagCompound().getInteger("Energy"));
			tile.energySend = stack.getTagCompound().getInteger("Send");
			tile.energyReceive = stack.getTagCompound().getInteger("Recv");

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
    public float getBlockHardness(IBlockState blockState, World world, BlockPos pos) {
		return HARDNESS[getMetaFromState(blockState)];
	}

    @Override
    public float getExplosionResistance(World world, BlockPos pos, Entity exploder, Explosion explosion) {
		return RESISTANCE[getMetaFromState(world.getBlockState(pos))];
	}

	@Override
	public boolean hasComparatorInputOverride(IBlockState state) {

		return true;
	}

    @Override
    public boolean isSideSolid(IBlockState base_state, IBlockAccess world, BlockPos pos, EnumFacing side) {
		return true;
	}

    @Override
    @SideOnly(Side.CLIENT)
    public int getTexturePasses() {
        return 2;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public BlockRenderLayer getRenderlayerForPass(int pass) {
        return pass > 0 ? BlockRenderLayer.TRANSLUCENT :BlockRenderLayer.CUTOUT;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean canRenderInLayer(IBlockState state, BlockRenderLayer layer) {
        return layer == BlockRenderLayer.CUTOUT || layer == BlockRenderLayer.TRANSLUCENT;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public ICustomBlockBakery getCustomBakery() {
        return RenderCell.instance;
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Override
	public NBTTagCompound getItemStackTag(IBlockAccess world, BlockPos pos) {

		NBTTagCompound tag = super.getItemStackTag(world, pos);
		TileCell tile = (TileCell) world.getTileEntity(pos);

		if (tile != null) {
			if (tag == null) {
				tag = new NBTTagCompound();
			}
			ReconfigurableHelper.setItemStackTagReconfig(tag, tile);

			tag.setInteger("Energy", tile.getEnergyStored(null));
			tag.setInteger("Send", tile.energySend);
			tag.setInteger("Recv", tile.energyReceive);
		}
		return tag;
	}

	/* IDismantleable */
	@Override
	public boolean canDismantle(EntityPlayer player, World world, BlockPos pos) {
        EnumType type = world.getBlockState(pos).getValue(TYPES);
		if (type == EnumType.CREATIVE && !CoreUtils.isOp(player)) {
			return false;
		}
		return super.canDismantle(player, world, pos);
	}

	/* IInitializer */
	@Override
	public boolean initialize() {

		TileCell.initialize();
		TileCellCreative.initialize();

		cellCreative = new ItemStack(this, 1, EnumType.CREATIVE.meta());
		cellBasic = new ItemStack(this, 1, EnumType.BASIC.meta());
		cellHardened = new ItemStack(this, 1, EnumType.HARDENED.meta());
		cellReinforced = new ItemStack(this, 1, EnumType.REINFORCED.meta());
		cellResonant = new ItemStack(this, 1, EnumType.RESONANT.meta());

		ItemBlockCell.setDefaultTag(cellCreative, 0);
		ItemBlockCell.setDefaultTag(cellBasic, 0);
		ItemBlockCell.setDefaultTag(cellHardened, 0);
		ItemBlockCell.setDefaultTag(cellReinforced, 0);
		ItemBlockCell.setDefaultTag(cellResonant, 0);

		ItemStackRegistry.registerCustomItemStack("cellCreative", cellCreative);
        ItemStackRegistry.registerCustomItemStack("cellBasic", cellBasic);
        ItemStackRegistry.registerCustomItemStack("cellHardened", cellHardened);
        ItemStackRegistry.registerCustomItemStack("cellReinforced", cellReinforced);
        ItemStackRegistry.registerCustomItemStack("cellResonant", cellResonant);

		return true;
	}

	@Override
	public boolean postInit() {

		if (enable[EnumType.BASIC.meta()]) {
			GameRegistry.addRecipe(ShapedRecipe(cellBasic, " I ", "IXI", " P ", 'I', "ingotCopper", 'X', BlockFrame.frameCellBasic, 'P', TEItems.powerCoilElectrum));
			PulverizerManager.addRecipe(4000, cellBasic, ItemHelper.cloneStack(Items.REDSTONE, 8), ItemHelper.cloneStack(TFItems.ingotLead, 3));
		}
		if (enable[EnumType.HARDENED.meta()]) {
			GameRegistry.addRecipe(ShapedRecipe(cellHardened, " I ", "IXI", " P ", 'I', "ingotCopper", 'X', BlockFrame.frameCellHardened, 'P', TEItems.powerCoilElectrum));
			GameRegistry.addRecipe(new RecipeUpgradeOverride(cellHardened, new Object[] { " I ", "IXI", " I ", 'I', "ingotInvar", 'X', cellBasic }).addInteger(
					"Send", TileCell.MAX_SEND[1], TileCell.MAX_SEND[2]).addInteger("Recv", TileCell.MAX_RECEIVE[1], TileCell.MAX_RECEIVE[2]));
			GameRegistry.addRecipe(ShapedRecipe(cellHardened, "IYI", "YXY", "IPI", 'I', "ingotInvar", 'X', BlockFrame.frameCellBasic, 'Y', "ingotCopper", 'P', TEItems.powerCoilElectrum));
			PulverizerManager.addRecipe(4000, cellHardened, ItemHelper.cloneStack(Items.REDSTONE, 8), ItemHelper.cloneStack(TFItems.ingotInvar, 3));
		}
		if (enable[EnumType.REINFORCED.meta()]) {
			GameRegistry.addRecipe(ShapedRecipe(cellReinforced, " X ", "YCY", "IPI", 'C', BlockFrame.frameCellReinforcedFull, 'I', "ingotLead", 'P', TEItems.powerCoilElectrum, 'X', "ingotElectrum", 'Y', "ingotElectrum"));
		}
		if (enable[EnumType.RESONANT.meta()]) {
			GameRegistry.addRecipe(ShapedRecipe(cellResonant, " X ", "YCY", "IPI", 'C', BlockFrame.frameCellResonantFull, 'I', "ingotLead", 'P', TEItems.powerCoilElectrum, 'X', "ingotElectrum", 'Y', "ingotElectrum"));
			GameRegistry.addRecipe(new RecipeUpgradeOverride(cellResonant, new Object[] { " I ", "IXI", " I ", 'I', "ingotEnderium", 'X', cellReinforced })
					.addInteger("Send", TileCell.MAX_SEND[3], TileCell.MAX_SEND[4]).addInteger("Recv", TileCell.MAX_RECEIVE[3], TileCell.MAX_RECEIVE[4]));
		}
		TECraftingHandler.addSecureRecipe(cellCreative);
		TECraftingHandler.addSecureRecipe(cellBasic);
		TECraftingHandler.addSecureRecipe(cellHardened);
		TECraftingHandler.addSecureRecipe(cellReinforced);
		TECraftingHandler.addSecureRecipe(cellResonant);

		return true;
	}

	public static final String[] NAMES = { "creative", "basic", "hardened", "reinforced", "resonant" };
	public static final float[] HARDNESS = { -1.0F, 5.0F, 15.0F, 20.0F, 20.0F };
	public static final int[] RESISTANCE = { 1200, 15, 90, 120, 120 };
	public static boolean[] enable = new boolean[EnumType.values().length];

	static {
		String category = "Cell.";

		enable[0] = ThermalExpansion.config.get(category + StringHelper.titleCase(NAMES[0]), "Enable", true);
		for (int i = 1; i < EnumType.values().length; i++) {
			enable[i] = ThermalExpansion.config.get(category + StringHelper.titleCase(NAMES[i]), "Recipe.Enable", true);
		}
	}

	public static final String TEXTURE_DEFAULT = "CellConfig_";
	public static final String TEXTURE_CB = "CellConfig_CB_";

	public static String textureSelection = TEXTURE_DEFAULT;

	public static ItemStack cellCreative;
	public static ItemStack cellBasic;
	public static ItemStack cellHardened;
	public static ItemStack cellReinforced;
	public static ItemStack cellResonant;

}
