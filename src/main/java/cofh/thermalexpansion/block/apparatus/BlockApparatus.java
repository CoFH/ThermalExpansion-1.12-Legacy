package cofh.thermalexpansion.block.apparatus;

import codechicken.lib.model.ModelRegistryHelper;
import codechicken.lib.model.bakery.BlockBakeryProperties;
import codechicken.lib.model.bakery.CCBakeryModel;
import codechicken.lib.model.bakery.ModelBakery;
import codechicken.lib.texture.IWorldBlockTextureProvider;
import codechicken.lib.texture.TextureUtils;
import cofh.core.render.IModelRegister;
import cofh.lib.util.helpers.BlockHelper;
import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalexpansion.block.BlockTEBase;
import cofh.thermalexpansion.init.TEProps;
import cofh.thermalexpansion.init.TETextures;
import cofh.thermalexpansion.util.helpers.ReconfigurableHelper;
import cofh.thermalfoundation.item.ItemMaterial;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMap;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
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

public class BlockApparatus extends BlockTEBase implements IModelRegister, IWorldBlockTextureProvider {

	public static final PropertyEnum<BlockApparatus.Type> VARIANT = PropertyEnum.create("type", Type.class);

	public BlockApparatus() {

		super(Material.IRON);

		setUnlocalizedName("apparatus");

		setHardness(15.0F);
		setResistance(25.0F);
		setDefaultState(getBlockState().getBaseState().withProperty(VARIANT, Type.BREAKER));
	}

	@Override
	protected BlockStateContainer createBlockState() {

		BlockStateContainer.Builder builder = new BlockStateContainer.Builder(this);
		// Listed
		builder.add(VARIANT);
		// UnListed
		builder.add(BlockBakeryProperties.LAYER_FACE_SPRITE_MAP);
		builder.add(TEProps.LEVEL);
		builder.add(TEProps.ACTIVE);
		builder.add(TEProps.FACING);
		builder.add(TEProps.SIDE_CONFIG);

		return builder.build();
	}

	@Override
	@SideOnly (Side.CLIENT)
	public void getSubBlocks(@Nonnull Item item, CreativeTabs tab, NonNullList<ItemStack> list) {

		for (int i = 0; i < Type.METADATA_LOOKUP.length; i++) {
			if (enable[i]) {
				list.add(itemBlock.setDefaultTag(new ItemStack(item, 1, i)));
			}
		}
	}

	/* TYPE METHODS */
	@Override
	public IBlockState getStateFromMeta(int meta) {

		return this.getDefaultState().withProperty(VARIANT, Type.byMetadata(meta));
	}

	@Override
	public int getMetaFromState(IBlockState state) {

		return state.getValue(VARIANT).getMetadata();
	}

	@Override
	public int damageDropped(IBlockState state) {

		return state.getValue(VARIANT).getMetadata();
	}

	/* ITileEntityProvider */
	@Override
	public TileEntity createNewTileEntity(World world, int metadata) {

		if (metadata >= Type.values().length) {
			return null;
		}
		switch (Type.byMetadata(metadata)) {
			case BREAKER:
				return new TileBreaker();
			case COLLECTOR:
				return new TileCollector();
			default:
				return null;
		}
	}

	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase living, ItemStack stack) {

		if (stack.getTagCompound() != null) {
			TileApparatusBase tile = (TileApparatusBase) world.getTileEntity(pos);

			tile.readAugmentsFromNBT(stack.getTagCompound());
			tile.updateAugmentStatus();
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

		return ModelBakery.handleExtendedState((IExtendedBlockState) super.getExtendedState(state, world, pos), world, pos);
	}

	@Override
	public TextureAtlasSprite getTexture(EnumFacing side, ItemStack stack) {

		return side != EnumFacing.NORTH ? TETextures.APPARATUS_SIDE : TETextures.APPARATUS_FACE[stack.getMetadata() % Type.values().length];
	}

	@Override
	public TextureAtlasSprite getTexture(EnumFacing side, IBlockState state, BlockRenderLayer layer, IBlockAccess world, BlockPos pos) {

		TileEntity tileEntity = world.getTileEntity(pos);
		if (tileEntity instanceof TileApparatusBase) {
			TileApparatusBase tile = ((TileApparatusBase) tileEntity);
			return tile.getTexture(side.ordinal(), layer == BlockRenderLayer.SOLID ? 0 : 1);
		}
		return TextureUtils.getMissingSprite();
	}

	/* IModelRegister */
	@Override
	@SideOnly (Side.CLIENT)
	public void registerModels() {

		StateMap.Builder stateMap = new StateMap.Builder();
		stateMap.ignore(VARIANT);
		ModelLoader.setCustomStateMapper(this, stateMap.build());

		ModelResourceLocation location = new ModelResourceLocation(getRegistryName(), "normal");
		for (Type type : Type.values()) {
			ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), type.getMetadata(), location);
		}
		ModelRegistryHelper.register(location, new CCBakeryModel("thermalexpansion:blocks/apparatus/apparatus_side"));
	}

	/* IInitializer */
	@Override
	public boolean preInit() {

		this.setRegistryName("apparatus");
		GameRegistry.register(this);

		itemBlock = new ItemBlockApparatus(this);
		itemBlock.setRegistryName(this.getRegistryName());
		GameRegistry.register(itemBlock);

		ThermalExpansion.proxy.addIModelRegister(this);

		return true;
	}

	public boolean initialize() {

		TileApparatusBase.config();

		TileBreaker.initialize();
		TileCollector.initialize();

		apparatusBreaker = itemBlock.setDefaultTag(new ItemStack(this, 1, Type.BREAKER.getMetadata()));
		apparatusCollector = itemBlock.setDefaultTag(new ItemStack(this, 1, Type.COLLECTOR.getMetadata()));

		return true;
	}

	@Override
	public boolean postInit() {

		String machineFrame = "thermalexpansion:machineFrame";
		String tinPart = "thermalexpansion:machineTin";

		if (enable[Type.BREAKER.getMetadata()]) {
			addRecipe(ShapedRecipe(apparatusBreaker, " X ", "YCY", "IPI", 'C', machineFrame, 'I', tinPart, 'P', ItemMaterial.powerCoilGold, 'X', Items.IRON_PICKAXE, 'Y', "ingotIron"));
		}
		if (enable[Type.COLLECTOR.getMetadata()]) {
			addRecipe(ShapedRecipe(apparatusCollector, " X ", "YCY", "IPI", 'C', machineFrame, 'I', tinPart, 'P', ItemMaterial.powerCoilGold, 'X', Blocks.HOPPER, 'Y', "ingotIron"));
		}
		return true;
	}

	/* TYPE */
	public enum Type implements IStringSerializable {

		// @formatter:off
		BREAKER(0, "breaker"),
		COLLECTOR(1, "collector");
		// @formatter:on

		private static final BlockApparatus.Type[] METADATA_LOOKUP = new BlockApparatus.Type[values().length];
		private final int metadata;
		private final String name;
		private final int light;

		Type(int metadata, String name, int light) {

			this.metadata = metadata;
			this.name = name;
			this.light = light;
		}

		Type(int metadata, String name) {

			this(metadata, name, 0);
		}

		public int getMetadata() {

			return this.metadata;
		}

		@Override
		public String getName() {

			return this.name;
		}

		public int getLight() {

			return light;
		}

		public static Type byMetadata(int metadata) {

			if (metadata < 0 || metadata >= METADATA_LOOKUP.length) {
				metadata = 0;
			}
			return METADATA_LOOKUP[metadata];
		}

		static {
			for (Type type : values()) {
				METADATA_LOOKUP[type.getMetadata()] = type;
			}
		}
	}

	public static boolean[] enable = new boolean[Type.values().length];

	/* REFERENCES */
	public static ItemStack apparatusBreaker;
	public static ItemStack apparatusCollector;
	// Forcefield
	// Charger
	// Trap
	// Fertilizer
	// Harvester
	// Planter

	public static ItemBlockApparatus itemBlock;

}
