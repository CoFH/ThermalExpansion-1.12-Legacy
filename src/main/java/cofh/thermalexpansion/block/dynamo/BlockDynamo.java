package cofh.thermalexpansion.block.dynamo;

import codechicken.lib.model.ModelRegistryHelper;
import codechicken.lib.model.blockbakery.*;
import codechicken.lib.raytracer.RayTracer;
import codechicken.lib.vec.Cuboid6;
import codechicken.lib.vec.Rotation;
import codechicken.lib.vec.Vector3;
import cofh.api.core.IModelRegister;
import cofh.lib.util.helpers.BlockHelper;
import cofh.lib.util.helpers.FluidHelper;
import cofh.thermalexpansion.block.BlockTEBase;
import cofh.thermalexpansion.init.TEProps;
import cofh.thermalexpansion.render.RenderDynamo;
import cofh.thermalfoundation.item.ItemMaterial;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMap;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

import static cofh.lib.util.helpers.ItemHelper.ShapedRecipe;
import static cofh.lib.util.helpers.ItemHelper.addRecipe;

public class BlockDynamo extends BlockTEBase implements IBakeryBlock, IModelRegister {

	public static final PropertyEnum<BlockDynamo.Type> VARIANT = PropertyEnum.<BlockDynamo.Type>create("type", Type.class);

	static AxisAlignedBB[] boundingBox = new AxisAlignedBB[12];

	static {
		Cuboid6 bb = new Cuboid6(0, 0, 0, 1, 10 / 16., 1);
		Vector3 p = new Vector3(0.5, 0.5, 0.5);
		boundingBox[1] = bb.aabb();
		boundingBox[0] = bb.apply(Rotation.sideRotations[1].at(p)).aabb();
		for (int i = 2; i < 6; ++i) {
			boundingBox[i] = bb.copy().apply(Rotation.sideRotations[i].at(p)).aabb();
		}

		bb = new Cuboid6(.25, .5, .25, .75, 1, .75);
		boundingBox[1 + 6] = bb.aabb();
		boundingBox[0 + 6] = bb.apply(Rotation.sideRotations[1].at(p)).aabb();
		for (int i = 2; i < 6; ++i) {
			boundingBox[i + 6] = bb.copy().apply(Rotation.sideRotations[i].at(p)).aabb();
		}
	}

	public BlockDynamo() {

		super(Material.IRON);

		setUnlocalizedName("thermalexpansion.dynamo");

		setHardness(15.0F);
		setResistance(25.0F);
		setDefaultState(getBlockState().getBaseState().withProperty(VARIANT, Type.STEAM));
	}

	@Override
	protected BlockStateContainer createBlockState() {

		BlockStateContainer.Builder builder = new BlockStateContainer.Builder(this);
		// Listed
		builder.add(VARIANT);
		// UnListed
		builder.add(TEProps.ACTIVE);
		builder.add(TEProps.FACING);
		builder.add(TEProps.ACTIVE_SPRITE_PROPERTY);
		return builder.build();
	}

	@Override
	@SideOnly (Side.CLIENT)
	public void getSubBlocks(@Nonnull Item item, CreativeTabs tab, List<ItemStack> list) {

		for (int i = 0; i < Type.METADATA_LOOKUP.length; i++) {
			if (enable[i]) {
				list.add(ItemBlockDynamo.setDefaultTag(new ItemStack(item, 1, i)));
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
		switch (Type.values()[metadata]) {
			case STEAM:
				return new TileDynamoSteam();
			case MAGMATIC:
				return new TileDynamoMagmatic();
			case COMPRESSION:
				return new TileDynamoCompression();
			case REACTANT:
				return new TileDynamoReactant();
			case ENERVATION:
				return new TileDynamoEnervation();
			case NUMISMATIC:
				return new TileDynamoNumismatic();
			default:
				return null;
		}
	}

	/* BLOCK METHODS */
	@Override
	public void addCollisionBoxToList(IBlockState state, World world, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, @Nullable Entity entityIn) {

		int facing = ((TileDynamoBase) world.getTileEntity(pos)).facing;

		AxisAlignedBB base, coil;
		base = boundingBox[facing].offset(pos);
		coil = boundingBox[facing + 6].offset(pos);

		if (coil.intersectsWith(entityBox)) {
			collidingBoxes.add(coil);
		}
		if (base.intersectsWith(entityBox)) {
			collidingBoxes.add(base);
		}
	}

	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase living, ItemStack stack) {

		if (stack.getTagCompound() != null) {
			TileDynamoBase tile = (TileDynamoBase) world.getTileEntity(pos);

			tile.setLevel(stack.getTagCompound().getByte("Level"));
			tile.readAugmentsFromNBT(stack.getTagCompound());
			tile.updateAugmentStatus();
			tile.setEnergyStored(stack.getTagCompound().getInteger("Energy"));
		}
		super.onBlockPlacedBy(world, pos, state, living, stack);
	}

	@Override
	@SideOnly (Side.CLIENT)
	public boolean canRenderInLayer(IBlockState state, BlockRenderLayer layer) {

		return layer == BlockRenderLayer.SOLID || layer == BlockRenderLayer.CUTOUT;
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

		TileEntity tile = world.getTileEntity(pos);

		if (!(tile instanceof TileDynamoBase)) {
			return false;
		}
		TileDynamoBase theTile = (TileDynamoBase) tile;
		return theTile.facing == BlockHelper.SIDE_OPPOSITE[side.ordinal()];
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, @Nullable ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {

		TileDynamoBase tile = (TileDynamoBase) world.getTileEntity(pos);

		if (tile != null && tile.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null)) {
			IFluidHandler handler = tile.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null);
			if (FluidHelper.drainItemToHandler(heldItem, handler, player, hand)) {
				return true;
			}
		}
		return super.onBlockActivated(world, pos, state, player, hand, heldItem, side, hitX, hitY, hitZ);
	}

	@Nullable
	@Override
	public RayTraceResult collisionRayTrace(IBlockState blockState, World worldIn, BlockPos pos, Vec3d start, Vec3d end) {

		TileEntity tileEntity = worldIn.getTileEntity(pos);
		if (tileEntity != null) {
			int facing = ((TileDynamoBase) tileEntity).facing;
			//Due to CCL Black magic, passing a CuboidRayTraceResult down this method will cause CCL to render its contained BB.
			return RayTracer.rayTraceCuboidsClosest(start, end, pos, boundingBox[facing], boundingBox[facing + 6]);
		}
		return super.collisionRayTrace(blockState, worldIn, pos, start, end);
	}

	/* RENDERING METHODS */
	@Override
	@SideOnly (Side.CLIENT)
	public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos) {

		return BlockBakery.handleExtendedState((IExtendedBlockState) super.getExtendedState(state, world, pos), world.getTileEntity(pos));
	}

	/* IBakeryBlock */
	@Override
	public ICustomBlockBakery getCustomBakery() {

		return RenderDynamo.INSTANCE;
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
		ModelRegistryHelper.register(location, new CCBakeryModel("thermalexpansion:blocks/dynamo/dynamo_coil_redstone"));

		BlockBakery.registerBlockKeyGenerator(this, new IBlockStateKeyGenerator() {
			@Override
			public String generateKey(IExtendedBlockState state) {
				//TODO CCL internal classes for helping with this / phase in hashing instead of string gen.
				StringBuilder builder = new StringBuilder(state.getBlock().getRegistryName().toString());
				builder.append(",");
				builder.append(state.getValue(TEProps.FACING));
				builder.append(",");
				builder.append(state.getValue(TEProps.ACTIVE));
				builder.append(",");
				builder.append(state.getValue(VARIANT));
				return builder.toString();
			}
		});
	}

	/* IInitializer */
	@Override
	public boolean preInit() {

		this.setRegistryName("dynamo");
		GameRegistry.register(this);

		ItemBlockDynamo itemBlock = new ItemBlockDynamo(this);
		itemBlock.setRegistryName(this.getRegistryName());
		GameRegistry.register(itemBlock);

		return true;
	}

	@Override
	public boolean initialize() {

		TileDynamoBase.config();

		TileDynamoSteam.initialize();
		TileDynamoMagmatic.initialize();
		TileDynamoCompression.initialize();
		TileDynamoReactant.initialize();
		TileDynamoEnervation.initialize();
		TileDynamoNumismatic.initialize();

		dynamoSteam = ItemBlockDynamo.setDefaultTag(new ItemStack(this, 1, Type.STEAM.getMetadata()));
		dynamoMagmatic = ItemBlockDynamo.setDefaultTag(new ItemStack(this, 1, Type.MAGMATIC.getMetadata()));
		dynamoCompression = ItemBlockDynamo.setDefaultTag(new ItemStack(this, 1, Type.COMPRESSION.getMetadata()));
		dynamoReactant = ItemBlockDynamo.setDefaultTag(new ItemStack(this, 1, Type.REACTANT.getMetadata()));
		dynamoEnervation = ItemBlockDynamo.setDefaultTag(new ItemStack(this, 1, Type.ENERVATION.getMetadata()));
		dynamoNumismatic = ItemBlockDynamo.setDefaultTag(new ItemStack(this, 1, Type.NUMISMATIC.getMetadata()));

		return true;
	}

	@Override
	public boolean postInit() {

		// @formatter:off
		if (enable[Type.STEAM.getMetadata()]) {
			addRecipe(ShapedRecipe(dynamoSteam,
					" C ",
					"GIG",
					"IRI",
					'C', ItemMaterial.powerCoilSilver,
					'G', "gearCopper",
					'I', "ingotIron",
					'R', "dustRedstone"
			));
		}
		if (enable[Type.MAGMATIC.getMetadata()]) {
			addRecipe(ShapedRecipe(dynamoMagmatic,
					" C ",
					"GIG",
					"IRI",
					'C', ItemMaterial.powerCoilSilver,
					'G', "gearInvar",
					'I', "ingotIron",
					'R', "dustRedstone"
			));
		}
		if (enable[Type.COMPRESSION.getMetadata()]) {
			addRecipe(ShapedRecipe(dynamoCompression,
					" C ",
					"GIG",
					"IRI",
					'C', ItemMaterial.powerCoilSilver,
					'G', "gearTin",
					'I', "ingotIron",
					'R', "dustRedstone"
			));
		}
		if (enable[Type.REACTANT.getMetadata()]) {
			addRecipe(ShapedRecipe(dynamoReactant,
					" C ",
					"GIG",
					"IRI",
					'C', ItemMaterial.powerCoilSilver,
					'G', "gearLead",
					'I', "ingotIron",
					'R', "dustRedstone"
			));
		}
		if (enable[Type.ENERVATION.getMetadata()]) {
			addRecipe(ShapedRecipe(dynamoEnervation,
					" C ",
					"GIG",
					"IRI",
					'C', ItemMaterial.powerCoilSilver,
					'G', "gearElectrum",
					'I', "ingotIron",
					'R', "dustRedstone"
			));
		}
		if (enable[Type.NUMISMATIC.getMetadata()]) {
			addRecipe(ShapedRecipe(dynamoNumismatic,
					" C ",
					"GIG",
					"IRI",
					'C', ItemMaterial.powerCoilSilver,
					'G', "gearConstantan",
					'I', "ingotIron",
					'R', "dustRedstone"
			));
		}
		// @formatter:on

		return true;
	}

	/* TYPE */
	public enum Type implements IStringSerializable {

		// @formatter:off
		STEAM(0, "steam"),
		MAGMATIC(1, "magmatic"),
		COMPRESSION(2, "compression"),
		REACTANT(3, "reactant"),
		ENERVATION(4, "enervation"),
		NUMISMATIC(5, "numismatic");
		// @formatter:on

		private static final BlockDynamo.Type[] METADATA_LOOKUP = new BlockDynamo.Type[values().length];
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
	public static ItemStack dynamoSteam;
	public static ItemStack dynamoMagmatic;
	public static ItemStack dynamoCompression;
	public static ItemStack dynamoReactant;
	public static ItemStack dynamoEnervation;
	public static ItemStack dynamoNumismatic;

}
