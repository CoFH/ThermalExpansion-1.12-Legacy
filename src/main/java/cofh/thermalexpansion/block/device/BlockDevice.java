package cofh.thermalexpansion.block.device;

import codechicken.lib.model.ModelRegistryHelper;
import codechicken.lib.model.bakery.CCBakeryModel;
import codechicken.lib.model.bakery.IBakeryProvider;
import codechicken.lib.model.bakery.ModelBakery;
import codechicken.lib.model.bakery.ModelErrorStateProperty;
import codechicken.lib.model.bakery.generation.IBakery;
import codechicken.lib.texture.IWorldBlockTextureProvider;
import codechicken.lib.texture.TextureUtils;
import cofh.core.render.IModelRegister;
import cofh.core.util.crafting.FluidIngredientFactory.FluidIngredient;
import cofh.core.util.helpers.BlockHelper;
import cofh.core.util.helpers.FluidHelper;
import cofh.core.util.helpers.ReconfigurableHelper;
import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalexpansion.block.BlockTEBase;
import cofh.thermalexpansion.init.TEProps;
import cofh.thermalexpansion.init.TETextures;
import cofh.thermalexpansion.item.ItemFrame;
import cofh.thermalexpansion.render.BakeryDevice;
import cofh.thermalfoundation.item.ItemMaterial;
import cofh.thermalfoundation.item.tome.ItemTomeExperience;
import cofh.thermalfoundation.item.tome.ItemTomeLexicon;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMap;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import static cofh.core.util.helpers.RecipeHelper.addShapedFluidRecipe;
import static cofh.core.util.helpers.RecipeHelper.addShapedRecipe;

public class BlockDevice extends BlockTEBase implements IModelRegister, IBakeryProvider, IWorldBlockTextureProvider {

	public static final PropertyEnum<Type> VARIANT = PropertyEnum.create("type", Type.class);

	public BlockDevice() {

		super(Material.IRON);

		setUnlocalizedName("device");

		setHardness(15.0F);
		setResistance(25.0F);
		setDefaultState(getBlockState().getBaseState().withProperty(VARIANT, Type.WATER_GEN));
	}

	@Override
	protected BlockStateContainer createBlockState() {

		BlockStateContainer.Builder builder = new BlockStateContainer.Builder(this);
		// Listed
		builder.add(VARIANT);
		// UnListed
		builder.add(ModelErrorStateProperty.ERROR_STATE);
		builder.add(TEProps.TILE_DEVICE);
		builder.add(TEProps.BAKERY_WORLD);

		return builder.build();
	}

	@Override
	public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> items) {

		for (int i = 0; i < Type.METADATA_LOOKUP.length; i++) {
			if (enable[i]) {
				items.add(itemBlock.setDefaultTag(new ItemStack(this, 1, i)));
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
			case WATER_GEN:
				return new TileWaterGen();
			case NULLIFIER:
				return new TileNullifier();
			case HEAT_SINK:
				return new TileHeatSink();
			case TAPPER:
				return new TileTapper();
			case FISHER:
				return new TileFisher();
			case ITEM_BUFFER:
				return new TileItemBuffer();
			case FLUID_BUFFER:
				return new TileFluidBuffer();
			case LEXICON:
				return new TileLexicon();
			case XP_COLLECTOR:
				return new TileXpCollector();
			case DIFFUSER:
				return new TileDiffuser();
			case FACTORIZER:
				return new TileFactorizer();
			case MOB_CATCHER:
				return new TileMobCatcher();
			case ITEM_COLLECTOR:
				return new TileItemCollector();
			//			case CHUNK_LOADER:                      // TODO
			//				return null;
			default:
				return null;
		}
	}

	/* BLOCK METHODS */
	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase living, ItemStack stack) {

		if (stack.getTagCompound() != null) {
			TileDeviceBase tile = (TileDeviceBase) world.getTileEntity(pos);

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

	@Override
	public boolean onBlockActivatedDelegate(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {

		TileEntity tile = world.getTileEntity(pos);

		if (tile != null && tile.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null)) {
			ItemStack heldItem = player.getHeldItem(hand);
			IFluidHandler handler = tile.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null);

			if (FluidHelper.isFluidHandler(heldItem)) {
				FluidHelper.interactWithHandler(heldItem, handler, player, hand);
				return true;
			}
		}
		return false;
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

	/* IBakeryProvider */
	@Override
	public IBakery getBakery() {

		return BakeryDevice.INSTANCE;
	}

	/* IWorldTextureProvider */
	@Override
	@SideOnly (Side.CLIENT)
	public TextureAtlasSprite getTexture(EnumFacing side, ItemStack stack) {

		if (side == EnumFacing.DOWN) {
			return TETextures.DEVICE_BOTTOM;
		}
		if (side == EnumFacing.UP) {
			return TETextures.DEVICE_TOP;
		}
		return side != EnumFacing.NORTH ? TETextures.DEVICE_SIDE : TETextures.DEVICE_FACE[stack.getMetadata() % Type.values().length];
	}

	@Override
	@SideOnly (Side.CLIENT)
	public TextureAtlasSprite getTexture(EnumFacing side, IBlockState state, BlockRenderLayer layer, IBlockAccess world, BlockPos pos) {

		TileEntity tileEntity = world.getTileEntity(pos);
		if (tileEntity instanceof TileDeviceBase) {
			TileDeviceBase tile = (TileDeviceBase) tileEntity;
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
		ModelRegistryHelper.register(location, new CCBakeryModel());

		ModelBakery.registerBlockKeyGenerator(this, state -> {

			StringBuilder builder = new StringBuilder(state.getBlock().getRegistryName().toString() + "|" + state.getBlock().getMetaFromState(state));
			TileDeviceBase tile = state.getValue(TEProps.TILE_DEVICE);
			builder.append("facing=").append(tile.getFacing());
			builder.append(",active=").append(tile.isActive);
			builder.append(",side_config={");
			for (int i : tile.sideCache) {
				builder.append(",").append(i);
			}
			builder.append("}");
			if (tile.hasFluidUnderlay() && tile.isActive) {
				FluidStack stack = tile.getRenderFluid();
				builder.append(",fluid=").append(stack != null ? FluidHelper.getFluidHash(stack) : tile.getTexture(tile.getFacing(), 0).getIconName());
			}
			return builder.toString();
		});
	}

	/* IInitializer */
	@Override
	public boolean preInit() {

		this.setRegistryName("device");
		ForgeRegistries.BLOCKS.register(this);

		itemBlock = new ItemBlockDevice(this);
		itemBlock.setRegistryName(this.getRegistryName());
		ForgeRegistries.ITEMS.register(itemBlock);

		TileDeviceBase.config();

		TileWaterGen.initialize();
		TileNullifier.initialize();
		TileHeatSink.initialize();
		TileTapper.initialize();
		TileFisher.initialize();
		TileItemBuffer.initialize();
		TileFluidBuffer.initialize();
		TileLexicon.initialize();
		TileXpCollector.initialize();
		TileDiffuser.initialize();
		TileFactorizer.initialize();
		TileMobCatcher.initialize();
		TileItemCollector.initialize();

		ThermalExpansion.proxy.addIModelRegister(this);

		return true;
	}

	@Override
	public boolean initialize() {

		deviceWaterGen = itemBlock.setDefaultTag(new ItemStack(this, 1, Type.WATER_GEN.getMetadata()));
		deviceNullifier = itemBlock.setDefaultTag(new ItemStack(this, 1, Type.NULLIFIER.getMetadata()));
		deviceHeatSink = itemBlock.setDefaultTag(new ItemStack(this, 1, Type.HEAT_SINK.getMetadata()));
		deviceTapper = itemBlock.setDefaultTag(new ItemStack(this, 1, Type.TAPPER.getMetadata()));
		deviceFisher = itemBlock.setDefaultTag(new ItemStack(this, 1, Type.FISHER.getMetadata()));
		deviceItemBuffer = itemBlock.setDefaultTag(new ItemStack(this, 1, Type.ITEM_BUFFER.getMetadata()));
		deviceFluidBuffer = itemBlock.setDefaultTag(new ItemStack(this, 1, Type.FLUID_BUFFER.getMetadata()));
		deviceLexicon = itemBlock.setDefaultTag(new ItemStack(this, 1, Type.LEXICON.getMetadata()));
		deviceExpCollector = itemBlock.setDefaultTag(new ItemStack(this, 1, Type.XP_COLLECTOR.getMetadata()));
		deviceDiffuser = itemBlock.setDefaultTag(new ItemStack(this, 1, Type.DIFFUSER.getMetadata()));
		deviceFactorizer = itemBlock.setDefaultTag(new ItemStack(this, 1, Type.FACTORIZER.getMetadata()));
		deviceMobCatcher = itemBlock.setDefaultTag(new ItemStack(this, 1, Type.MOB_CATCHER.getMetadata()));
		deviceItemCollector = itemBlock.setDefaultTag(new ItemStack(this, 1, Type.ITEM_COLLECTOR.getMetadata()));

		addRecipes();

		return true;
	}

	/* HELPERS */
	private void addRecipes() {

		String ironPart = "gearIron";

		// @formatter:off
		if (enable[Type.WATER_GEN.getMetadata()]) {
			addShapedRecipe(deviceWaterGen,
					" X ",
					"YCY",
					"IPI",
					'C', ItemFrame.frameDevice,
					'I', ironPart,
					'P', ItemMaterial.redstoneServo,
					'X', Items.BUCKET,
					'Y', "blockGlass"
			);
		}
		if (enable[Type.NULLIFIER.getMetadata()]) {
			addShapedFluidRecipe(deviceNullifier,
					" X ",
					"YCY",
					"IPI",
					'C', ItemFrame.frameDevice,
					'I', ironPart,
					'P', ItemMaterial.redstoneServo,
					'X', new FluidIngredient("lava"),
					'Y', Blocks.BRICK_BLOCK
			);
		}
		if (enable[Type.HEAT_SINK.getMetadata()]) {
			addShapedRecipe(deviceHeatSink,
					" X ",
					"YCY",
					"IPI",
					'C', ItemFrame.frameDevice,
					'I', ironPart,
					'P', ItemMaterial.redstoneServo,
					'X', "ingotCopper",
					'Y', "ingotInvar"
			);
		}
		if (enable[Type.TAPPER.getMetadata()]) {
			addShapedRecipe(deviceTapper,
					" X ",
					"YCY",
					"IPI",
					'C', ItemFrame.frameDevice,
					'I', ironPart,
					'P', ItemMaterial.redstoneServo,
					'X', "ingotCopper",
					'Y', "plankWood"
			);
		}
		if (enable[Type.FISHER.getMetadata()]) {
			addShapedRecipe(deviceFisher,
					" X ",
					"YCY",
					"IPI",
					'C', ItemFrame.frameDevice,
					'I', ironPart,
					'P', ItemMaterial.redstoneServo,
					'X', Items.FISHING_ROD,
					'Y', Blocks.IRON_BARS
			);
		}
		if (enable[Type.ITEM_BUFFER.getMetadata()]) {
			addShapedRecipe(deviceItemBuffer,
					" X ",
					"YCY",
					"IPI",
					'C', ItemFrame.frameDevice,
					'I', ironPart,
					'P', ItemMaterial.redstoneServo,
					'X', "chestWood",
					'Y', "ingotTin"
			);
		}
		if (enable[Type.FLUID_BUFFER.getMetadata()]) {
			addShapedRecipe(deviceFluidBuffer,
					" X ",
					"YCY",
					"IPI",
					'C', ItemFrame.frameDevice,
					'I', ironPart,
					'P', ItemMaterial.redstoneServo,
					'X', "blockGlass",
					'Y', "ingotCopper"
			);
		}
		if (enable[Type.LEXICON.getMetadata()]) {
			addShapedRecipe(deviceLexicon,
					" X ",
					"YCY",
					"IPI",
					'C', ItemFrame.frameDevice,
					'I', ironPart,
					'P', ItemMaterial.redstoneServo,
					'X', ItemTomeLexicon.tomeLexicon,
					'Y', "ingotLead"
			);
		}
		if (enable[Type.XP_COLLECTOR.getMetadata()]) {
			addShapedRecipe(deviceExpCollector,
					" X ",
					"YCY",
					"IPI",
					'C', ItemFrame.frameDevice,
					'I', ironPart,
					'P', ItemMaterial.redstoneServo,
					'X', ItemTomeExperience.tomeExperience,
					'Y', "ingotGold"
			);
		}
		if (enable[Type.DIFFUSER.getMetadata()]) {
			addShapedRecipe(deviceDiffuser,
					" X ",
					"YCY",
					"IPI",
					'C', ItemFrame.frameDevice,
					'I', ironPart,
					'P', ItemMaterial.redstoneServo,
					'X', "blockGlassHardened",
					'Y', "ingotSilver"
			);
		}
		if (enable[Type.FACTORIZER.getMetadata()]) {
			addShapedRecipe(deviceFactorizer,
					" X ",
					"YCY",
					"IPI",
					'C', ItemFrame.frameDevice,
					'I', ironPart,
					'P', ItemMaterial.redstoneServo,
					'X', "workbench",
					'Y', "ingotLead"
			);
		}
		if (enable[Type.MOB_CATCHER.getMetadata()]) {
			addShapedRecipe(deviceMobCatcher,
					" X ",
					"YCY",
					"IPI",
					'C', ItemFrame.frameDevice,
					'I', ironPart,
					'P', ItemMaterial.redstoneServo,
					'X', Blocks.DISPENSER,
					'Y', "ingotConstantan"
			);
		}
//		if (enable[Type.ITEM_COLLECTOR.getMetadata()]) {
//			addShapedRecipe(deviceItemBuffer,
//					" X ",
//					"YCY",
//					"IPI",
//					'C', ItemFrame.frameDevice,
//					'I', ironPart,
//					'P', ItemMaterial.redstoneServo,
//					'X', "chestWood",
//					'Y', "ingotTin"
//			);
//		}
		// @formatter:on
	}

	/* TYPE */
	public enum Type implements IStringSerializable {

		// @formatter:off
		WATER_GEN(0, "water_gen"),
		NULLIFIER(1, "nullifier"),
		HEAT_SINK(2, "heat_sink"),
		TAPPER(3, "tapper"),
		FISHER(4, "fisher"),
		ITEM_BUFFER(5, "item_buffer"),
		FLUID_BUFFER(6, "fluid_buffer"),
		LEXICON(7, "lexicon"),
		XP_COLLECTOR(8, "xp_collector"),
		DIFFUSER(9, "diffuser"),
		FACTORIZER(10, "factorizer"),
		MOB_CATCHER(11, "mob_catcher"),
		ITEM_COLLECTOR(12, "item_collector"),
		CHUNK_LOADER(13, "chunk_loader");
		// @formatter:on

		private static final Type[] METADATA_LOOKUP = new Type[values().length];
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
	public static ItemStack deviceWaterGen;
	public static ItemStack deviceNullifier;
	public static ItemStack deviceHeatSink;
	public static ItemStack deviceTapper;
	public static ItemStack deviceFisher;
	public static ItemStack deviceItemBuffer;
	public static ItemStack deviceFluidBuffer;
	public static ItemStack deviceLexicon;
	public static ItemStack deviceExpCollector;
	public static ItemStack deviceDiffuser;
	public static ItemStack deviceFactorizer;
	public static ItemStack deviceMobCatcher;
	public static ItemStack deviceItemCollector;
	public static ItemStack deviceChunkLoader;

	public static ItemBlockDevice itemBlock;

}
