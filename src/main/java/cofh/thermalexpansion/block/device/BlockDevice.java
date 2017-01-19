package cofh.thermalexpansion.block.device;

import codechicken.lib.model.ModelRegistryHelper;
import codechicken.lib.model.blockbakery.BlockBakery;
import codechicken.lib.model.blockbakery.BlockBakeryProperties;
import codechicken.lib.model.blockbakery.CCBakeryModel;
import codechicken.lib.texture.IWorldBlockTextureProvider;
import codechicken.lib.texture.TextureUtils;
import cofh.api.core.IModelRegister;
import cofh.lib.util.helpers.BlockHelper;
import cofh.lib.util.helpers.ItemHelper;
import cofh.thermalexpansion.block.BlockTEBase;
import cofh.thermalexpansion.block.TileAugmentable;
import cofh.thermalexpansion.init.TEProps;
import cofh.thermalexpansion.init.TETextures;
import cofh.thermalexpansion.item.TEAugments;
import cofh.thermalexpansion.util.ReconfigurableHelper;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMap;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
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

public class BlockDevice extends BlockTEBase implements IModelRegister, IWorldBlockTextureProvider {

	public static final PropertyEnum<BlockDevice.Type> VARIANT = PropertyEnum.<BlockDevice.Type>create("type", BlockDevice.Type.class);

	public BlockDevice() {

		super(Material.IRON);

		setUnlocalizedName("thermalexpansion.device");

		setHardness(15.0F);
		setResistance(25.0F);
	}

	@Override
	protected BlockStateContainer createBlockState() {

		BlockStateContainer.Builder builder = new BlockStateContainer.Builder(this);
		// Listed
		builder.add(VARIANT);
		// UnListed
		builder.add(BlockBakeryProperties.LAYER_FACE_SPRITE_MAP);
		builder.add(TEProps.ACTIVE);
		builder.add(TEProps.FACING);
		builder.add(TEProps.SIDE_CONFIG[0]).add(TEProps.SIDE_CONFIG[1]).add(TEProps.SIDE_CONFIG[2]).add(TEProps.SIDE_CONFIG[3]).add(TEProps.SIDE_CONFIG[4]).add(TEProps.SIDE_CONFIG[5]);

		return builder.build();
	}

	@Override
	@SideOnly (Side.CLIENT)
	public void getSubBlocks(@Nonnull Item item, CreativeTabs tab, List<ItemStack> list) {

		//		for (int i = 0; i < BlockDevice.Type.METADATA_LOOKUP.length; i++) {
		//			list.add(new ItemStack(item, 1, i));
		//		}
		list.add(new ItemStack(item, 1, 0));
		list.add(new ItemStack(item, 1, 1));
		list.add(new ItemStack(item, 1, 2));
		list.add(new ItemStack(item, 1, 4));
		list.add(new ItemStack(item, 1, 5));
	}

	/* TYPE METHODS */
	@Override
	public IBlockState getStateFromMeta(int meta) {

		return this.getDefaultState().withProperty(VARIANT, BlockDevice.Type.byMetadata(meta));
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

		if (metadata >= BlockDevice.Type.values().length) {
			return null;
		}
		switch (Type.byMetadata(metadata)) {
			case ACTIVATOR:
				return new TileActivator();
			case BREAKER:
				return new TileBreaker();
			case COLLECTOR:
				return new TileCollector();
			case NULLIFIER:
				return new TileNullifier();
			case BUFFER:
				return new TileBuffer();
			//			case EXTENDER:
			//				return new TileExtender();
			default:
				return null;
		}
	}

	/* BLOCK METHODS */
	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase living, ItemStack stack) {

		if (stack.getTagCompound() != null) {
			TileDeviceBase tile = (TileDeviceBase) world.getTileEntity(pos);

			tile.readAugmentsFromNBT(stack.getTagCompound());
			tile.installAugments();
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

	@Override
	public NBTTagCompound getItemStackTag(IBlockAccess world, BlockPos pos) {

		NBTTagCompound tag = super.getItemStackTag(world, pos);
		TileEntity tile = world.getTileEntity(pos);

		if (tile instanceof TileAugmentable) {
			TileAugmentable theTile = (TileAugmentable) tile;

			if (tag == null) {
				tag = new NBTTagCompound();
			}
			ReconfigurableHelper.setItemStackTagReconfig(tag, theTile);
			tag.setInteger("Energy", theTile.getEnergyStored(null));

			theTile.writeAugmentsToNBT(tag);
		}
		return tag;
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

	@Override
	public TextureAtlasSprite getTexture(EnumFacing side, int metadata) {

		return side != EnumFacing.NORTH ? TETextures.DEVICE_SIDE : TETextures.DEVICE_FACE[metadata % Type.values().length];
	}

	@Override
	public TextureAtlasSprite getTexture(EnumFacing side, IBlockState state, BlockRenderLayer layer, IBlockAccess world, BlockPos pos) {

		TileEntity tileEntity = world.getTileEntity(pos);
		if (tileEntity instanceof TileDeviceBase) {
			TileDeviceBase device = ((TileDeviceBase) tileEntity);
			return device.getTexture(side.ordinal(), layer == BlockRenderLayer.SOLID ? 0 : 1);
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
		ModelRegistryHelper.register(location, new CCBakeryModel("thermalexpansion:blocks/device/device_side"));
	}

	/* IInitializer */
	@Override
	public boolean preInit() {

		this.setRegistryName("device");
		GameRegistry.register(this);

		ItemBlockDevice itemBlock = new ItemBlockDevice(this);
		itemBlock.setRegistryName(this.getRegistryName());
		GameRegistry.register(itemBlock);

		return true;
	}

	@Override
	public boolean initialize() {

		TileDeviceBase.config();

		TileActivator.initialize();
		TileBreaker.initialize();
		TileCollector.initialize();
		//watergen
		TileNullifier.initialize();
		TileBuffer.initialize();
		TileExtender.initialize();

		if (defaultRedstoneControl) {
			defaultAugments[0] = ItemHelper.cloneStack(TEAugments.generalRedstoneControl);
		}
		if (defaultReconfigSides) {
			defaultAugments[1] = ItemHelper.cloneStack(TEAugments.generalReconfigSides);
		}
		deviceActivator = ItemBlockDevice.setDefaultTag(new ItemStack(this, 1, BlockDevice.Type.ACTIVATOR.getMetadata()));
		deviceBreaker = ItemBlockDevice.setDefaultTag(new ItemStack(this, 1, BlockDevice.Type.BREAKER.getMetadata()));
		deviceCollector = ItemBlockDevice.setDefaultTag(new ItemStack(this, 1, BlockDevice.Type.COLLECTOR.getMetadata()));
		//deviceWaterGen = ItemBlockDevice.setDefaultTag(new ItemStack(this, 1, BlockDevice.Type.WATERGEN.getMetadata()));
		deviceNullifier = ItemBlockDevice.setDefaultTag(new ItemStack(this, 1, BlockDevice.Type.NULLIFIER.getMetadata()));
		deviceBuffer = ItemBlockDevice.setDefaultTag(new ItemStack(this, 1, BlockDevice.Type.BUFFER.getMetadata()));

		return true;
	}

	@Override
	public boolean postInit() {

		return true;
	}

	public static void refreshItemStacks() {

	}

	/* TYPE */
	public enum Type implements IStringSerializable {

		// @formatter:off
		ACTIVATOR(0, "activator", deviceActivator),
		BREAKER(1, "breaker", deviceBreaker),
		COLLECTOR(2, "collector", deviceCollector),
		WATERGEN(3, "watergen", deviceWaterGen),
		NULLIFIER(4, "nullifier", deviceNullifier),
		BUFFER(5, "buffer", deviceBuffer),
		EXTENDER(6, "extender", deviceExtender);
		// @formatter:on

		private static final BlockDevice.Type[] METADATA_LOOKUP = new BlockDevice.Type[values().length];
		private final int metadata;
		private final String name;
		private final ItemStack stack;

		private final int light;

		Type(int metadata, String name, ItemStack stack, int light) {

			this.metadata = metadata;
			this.name = name;
			this.stack = stack;

			this.light = light;
		}

		Type(int metadata, String name, ItemStack stack) {

			this(metadata, name, stack, 0);
		}

		public int getMetadata() {

			return this.metadata;
		}

		@Override
		public String getName() {

			return this.name;
		}

		public ItemStack getStack() {

			return this.stack;
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

	public static boolean[] enable = new boolean[BlockDevice.Type.values().length];
	public static ItemStack[] defaultAugments = new ItemStack[4];

	public static boolean defaultRedstoneControl = true;
	public static boolean defaultReconfigSides = true;

	/* REFERENCES */
	public static ItemStack deviceActivator;
	public static ItemStack deviceBreaker;
	public static ItemStack deviceCollector;
	public static ItemStack deviceWaterGen;
	public static ItemStack deviceNullifier;
	public static ItemStack deviceBuffer;
	public static ItemStack deviceExtender;

}
