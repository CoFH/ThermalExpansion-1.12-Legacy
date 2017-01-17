package cofh.thermalexpansion.block.dynamo;

import codechicken.lib.block.IParticleProvider;
import codechicken.lib.block.IType;
import codechicken.lib.item.ItemStackRegistry;
import codechicken.lib.model.blockbakery.BlockBakery;
import codechicken.lib.model.blockbakery.IBakeryBlock;
import codechicken.lib.model.blockbakery.ICustomBlockBakery;
import codechicken.lib.raytracer.RayTracer;
import codechicken.lib.vec.Cuboid6;
import codechicken.lib.vec.Rotation;
import codechicken.lib.vec.Vector3;
import cofh.core.util.crafting.RecipeAugmentable;
import cofh.lib.util.helpers.BlockHelper;
import cofh.lib.util.helpers.FluidHelper;
import cofh.lib.util.helpers.ItemHelper;
import cofh.lib.util.helpers.StringHelper;
import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalexpansion.block.BlockTEBase;
import cofh.thermalexpansion.block.CommonProperties;
import cofh.thermalexpansion.item.TEAugments;
import cofh.thermalexpansion.init.TEItemsOld;
import cofh.thermalexpansion.render.RenderDynamo;
import cofh.thermalexpansion.util.crafting.TECraftingHandler;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
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
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Locale;

public class BlockDynamo extends BlockTEBase implements IBakeryBlock {

	static AxisAlignedBB[] boundingBox = new AxisAlignedBB[12];
	public static final PropertyEnum<Types> TYPES = PropertyEnum.create("type", Types.class);

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
		setHardness(15.0F);
		setResistance(25.0F);
		setUnlocalizedName("thermalexpansion.dynamo");
	}

	@Override
	public int getMetaFromState(IBlockState state) {

		return state.getValue(TYPES).meta();
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {

		return getDefaultState().withProperty(TYPES, Types.fromMeta(meta));
	}

	@Override
	protected BlockStateContainer createBlockState() {

		return new ExtendedBlockState.Builder(this).add(TYPES).add(CommonProperties.FACING_PROPERTY).add(CommonProperties.ACTIVE_PROPERTY).add(CommonProperties.TYPE_PROPERTY).add(CommonProperties.ACTIVE_SPRITE_PROPERTY).build();
	}

	@Override
	@SideOnly (Side.CLIENT)
	public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos) {

		return BlockBakery.handleExtendedState((IExtendedBlockState) state, world.getTileEntity(pos));
	}

	@Override
	@SideOnly (Side.CLIENT)
	public ICustomBlockBakery getCustomBakery() {

		return RenderDynamo.instance;
	}

	@Override
	public boolean canRenderInLayer(IBlockState state, BlockRenderLayer layer) {

		return layer == BlockRenderLayer.SOLID || layer == BlockRenderLayer.CUTOUT;
	}

	@Override
	public boolean isFullCube(IBlockState state) {

		return false;
	}

	@Override
	public TileEntity createNewTileEntity(World world, int metadata) {

		if (metadata >= Types.values().length) {
			return null;
		}
		switch (Types.values()[metadata]) {
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
			default:
				return null;
		}
	}

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

	@Override
	public void getSubBlocks(Item item, CreativeTabs tab, List<ItemStack> list) {

		for (int i = 0; i < Types.values().length; i++) {
			list.add(ItemBlockDynamo.setDefaultTag(new ItemStack(item, 1, i)));
		}
	}

	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase living, ItemStack stack) {

		TileDynamoBase tile = (TileDynamoBase) world.getTileEntity(pos);

		if (stack.getTagCompound() != null) {
			tile.readAugmentsFromNBT(stack.getTagCompound());
			tile.installAugments();
			tile.setEnergyStored(stack.getTagCompound().getInteger("Energy"));
		}
		super.onBlockPlacedBy(world, pos, state, living, stack);
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

	@Override
	public boolean hasComparatorInputOverride(IBlockState state) {

		return true;
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
	public NBTTagCompound getItemStackTag(IBlockAccess world, BlockPos pos) {

		NBTTagCompound tag = super.getItemStackTag(world, pos);
		TileDynamoBase tile = (TileDynamoBase) world.getTileEntity(pos);

		if (tile != null) {
			if (tag == null) {
				tag = new NBTTagCompound();
			}
			tag.setInteger("Energy", tile.getEnergyStored(null));
			tile.writeAugmentsToNBT(tag);
		}
		return tag;
	}

	/* IInitializer */
	@Override
	public boolean initialize() {

		TileDynamoBase.configure();
		TileDynamoSteam.initialize();
		TileDynamoMagmatic.initialize();
		TileDynamoCompression.initialize();
		TileDynamoReactant.initialize();
		TileDynamoEnervation.initialize();

		if (defaultRedstoneControl) {
			defaultAugments[0] = ItemHelper.cloneStack(TEAugments.generalRedstoneControl);
		}
		dynamoSteam = ItemBlockDynamo.setDefaultTag(new ItemStack(this, 1, Types.STEAM.ordinal()));
		dynamoMagmatic = ItemBlockDynamo.setDefaultTag(new ItemStack(this, 1, Types.MAGMATIC.ordinal()));
		dynamoCompression = ItemBlockDynamo.setDefaultTag(new ItemStack(this, 1, Types.COMPRESSION.ordinal()));
		dynamoReactant = ItemBlockDynamo.setDefaultTag(new ItemStack(this, 1, Types.REACTANT.ordinal()));
		dynamoEnervation = ItemBlockDynamo.setDefaultTag(new ItemStack(this, 1, Types.ENERVATION.ordinal()));

		ItemStackRegistry.registerCustomItemStack("dynamoSteam", dynamoSteam);
		ItemStackRegistry.registerCustomItemStack("dynamoMagmatic", dynamoMagmatic);
		ItemStackRegistry.registerCustomItemStack("dynamoCompression", dynamoCompression);
		ItemStackRegistry.registerCustomItemStack("dynamoReactant", dynamoReactant);
		ItemStackRegistry.registerCustomItemStack("dynamoEnervation", dynamoEnervation);

		return true;
	}

	@Override
	public boolean postInit() {

		if (enable[Types.STEAM.ordinal()]) {
			GameRegistry.addRecipe(new RecipeAugmentable(dynamoSteam, defaultAugments, new Object[] { " C ", "GIG", "IRI", 'C', TEItemsOld.powerCoilSilver, 'G', "gearCopper", 'I', "ingotCopper", 'R', "dustRedstone" }));
		}
		if (enable[Types.MAGMATIC.ordinal()]) {
			GameRegistry.addRecipe(new RecipeAugmentable(dynamoMagmatic, defaultAugments, new Object[] { " C ", "GIG", "IRI", 'C', TEItemsOld.powerCoilSilver, 'G', "gearInvar", 'I', "ingotInvar", 'R', "dustRedstone" }));
		}
		if (enable[Types.COMPRESSION.ordinal()]) {
			GameRegistry.addRecipe(new RecipeAugmentable(dynamoCompression, defaultAugments, new Object[] { " C ", "GIG", "IRI", 'C', TEItemsOld.powerCoilSilver, 'G', "gearTin", 'I', "ingotTin", 'R', "dustRedstone" }));
		}
		if (enable[Types.REACTANT.ordinal()]) {
			GameRegistry.addRecipe(new RecipeAugmentable(dynamoReactant, defaultAugments, new Object[] { " C ", "GIG", "IRI", 'C', TEItemsOld.powerCoilSilver, 'G', "gearBronze", 'I', "ingotBronze", 'R', "dustRedstone" }));
		}
		if (enable[Types.ENERVATION.ordinal()]) {
			GameRegistry.addRecipe(new RecipeAugmentable(dynamoEnervation, defaultAugments, new Object[] { " C ", "GIG", "IRI", 'C', TEItemsOld.powerCoilSilver, 'G', "gearElectrum", 'I', "ingotElectrum", 'R', "dustRedstone" }));
		}
		TECraftingHandler.addSecureRecipe(dynamoSteam);
		TECraftingHandler.addSecureRecipe(dynamoMagmatic);
		TECraftingHandler.addSecureRecipe(dynamoCompression);
		TECraftingHandler.addSecureRecipe(dynamoEnervation);
		TECraftingHandler.addSecureRecipe(dynamoReactant);

		return true;
	}

	public static void refreshItemStacks() {

		dynamoSteam = ItemBlockDynamo.setDefaultTag(dynamoSteam);
		dynamoMagmatic = ItemBlockDynamo.setDefaultTag(dynamoMagmatic);
		dynamoCompression = ItemBlockDynamo.setDefaultTag(dynamoCompression);
		dynamoReactant = ItemBlockDynamo.setDefaultTag(dynamoReactant);
		dynamoEnervation = ItemBlockDynamo.setDefaultTag(dynamoEnervation);
	}

	public enum Types implements IStringSerializable, IType, IParticleProvider {
		STEAM, MAGMATIC, COMPRESSION, REACTANT, ENERVATION;

		@Override
		public String getName() {

			return name().toLowerCase(Locale.US);
		}

		public static Types fromMeta(int meta) {

			try {
				return values()[meta];
			} catch (IndexOutOfBoundsException e) {
				throw new RuntimeException("Someone has requested an invalid metadata for a block inside ThermalExpansion.", e);
			}
		}

		@Override
		public IProperty<?> getTypeProperty() {

			return TYPES;
		}

		@Override
		public int meta() {

			return ordinal();
		}

		@Override
		public String getParticleTexture() {

			return "thermalexpansion:blocks/dynamo/dynamo_" + getName();
		}

		public static int meta(Types type) {

			return type.ordinal();
		}
	}

	public static final String[] NAMES = { "steam", "magmatic", "compression", "reactant", "enervation" };
	public static boolean[] enable = new boolean[Types.values().length];

	public static ItemStack[] defaultAugments = new ItemStack[4];

	public static boolean defaultRedstoneControl = true;

	static {
		String category = "Dynamo.";

		for (int i = 0; i < Types.values().length; i++) {
			enable[i] = ThermalExpansion.CONFIG.get(category + StringHelper.titleCase(NAMES[i]), "Recipe.Enable", true);
		}
	}

	public static ItemStack dynamoSteam;
	public static ItemStack dynamoMagmatic;
	public static ItemStack dynamoCompression;
	public static ItemStack dynamoReactant;
	public static ItemStack dynamoEnervation;

}
