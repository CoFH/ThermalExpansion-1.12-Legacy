package cofh.thermalexpansion.block.machine;

import codechicken.lib.model.ModelRegistryHelper;
import codechicken.lib.model.blockbakery.BlockBakery;
import codechicken.lib.model.blockbakery.BlockBakeryProperties;
import codechicken.lib.model.blockbakery.CCBakeryModel;
import codechicken.lib.texture.IWorldBlockTextureProvider;
import codechicken.lib.texture.TextureUtils;
import cofh.api.core.IModelRegister;
import cofh.lib.util.helpers.BlockHelper;
import cofh.lib.util.helpers.FluidHelper;
import cofh.thermalexpansion.block.BlockTEBase;
import cofh.thermalexpansion.init.TEProps;
import cofh.thermalexpansion.init.TETextures;
import cofh.thermalexpansion.util.ReconfigurableHelper;
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
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
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

public class BlockMachine extends BlockTEBase implements IModelRegister, IWorldBlockTextureProvider {

	public static final PropertyEnum<BlockMachine.Type> VARIANT = PropertyEnum.<BlockMachine.Type>create("type", BlockMachine.Type.class);

	public BlockMachine() {

		super(Material.IRON);

		setUnlocalizedName("machine");

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

		list.add(new ItemStack(item, 1, 0));
		list.add(new ItemStack(item, 1, 1));
		list.add(new ItemStack(item, 1, 2));
		list.add(new ItemStack(item, 1, 3));
		list.add(new ItemStack(item, 1, 4));
		list.add(new ItemStack(item, 1, 5));
		list.add(new ItemStack(item, 1, 6));
		list.add(new ItemStack(item, 1, 7));
		list.add(new ItemStack(item, 1, 13));
		list.add(new ItemStack(item, 1, 14));
	}

	/* TYPE METHODS */
	@Override
	public IBlockState getStateFromMeta(int meta) {

		return this.getDefaultState().withProperty(VARIANT, BlockMachine.Type.byMetadata(meta));
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

		if (metadata >= BlockMachine.Type.values().length) {
			return null;
		}
		switch (BlockMachine.Type.byMetadata(metadata)) {
			case FURNACE:
				return new TileFurnace();
			case PULVERIZER:
				return new TilePulverizer();
			case SAWMILL:
				return new TileSawmill();
			case SMELTER:
				return new TileSmelter();
			case INSOLATOR:
				return new TileInsolator();
			case CHARGER:
				return new TileCharger();
			case CRUCIBLE:
				return new TileCrucible();
			case TRANSPOSER:
				return new TileTransposer();
			case CRAFTER:
				return new TileCrafter();
			case PRECIPITATOR:
				return new TilePrecipitator();
			case EXTRUDER:
				return new TileExtruder();
			default:
				return null;
		}
	}

	/* BLOCK METHODS */
	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase living, ItemStack stack) {

		if (stack.getTagCompound() != null) {
			TileMachineBase tile = (TileMachineBase) world.getTileEntity(pos);

			tile.readAugmentsFromNBT(stack.getTagCompound());
			tile.installAugments();
			tile.setEnergyStored(stack.getTagCompound().getInteger("Energy"));

			int facing = BlockHelper.determineXZPlaceFacing(living);
			int storedFacing = ReconfigurableHelper.getFacing(stack);
			byte[] sideCache = ReconfigurableHelper.getSideCache(stack, tile.getDefaultSides());

			tile.sideCache[0] = sideCache[0];
			tile.sideCache[1] = sideCache[1];
			tile.sideCache[facing] = 0;
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
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, @Nullable ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {

		TileEntity tile = world.getTileEntity(pos);

		if (tile instanceof TileExtruder || tile instanceof TilePrecipitator) {
			if (tile.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null)) {
				IFluidHandler handler = tile.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null);
				if (FluidHelper.drainItemToHandler(heldItem, handler, player, hand)) {
					return true;
				}
			}
		}
		return super.onBlockActivated(world, pos, state, player, hand, heldItem, side, hitX, hitY, hitZ);
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

	@Override //Inv.
	@SideOnly (Side.CLIENT)
	public TextureAtlasSprite getTexture(EnumFacing side, int metadata) {

		if (side == EnumFacing.DOWN) {
			return TETextures.MACHINE_BOTTOM;
		}
		if (side == EnumFacing.UP) {
			return TETextures.MACHINE_TOP;
		}
		return side != EnumFacing.NORTH ? TETextures.MACHINE_SIDE : TETextures.MACHINE_FACE[metadata % Type.values().length];
	}

	@Override //World
	@SideOnly (Side.CLIENT)
	public TextureAtlasSprite getTexture(EnumFacing side, IBlockState state, BlockRenderLayer layer, IBlockAccess world, BlockPos pos) {

		TileEntity tileEntity = world.getTileEntity(pos);
		if (tileEntity instanceof TileMachineBase) {
			TileMachineBase machine = ((TileMachineBase) tileEntity);
			//TODO ISidedTexture needs to change to support layers + passes.
			return machine.getTexture(side.ordinal(), layer == BlockRenderLayer.SOLID ? 0 : 1);
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
		ModelRegistryHelper.register(location, new CCBakeryModel("thermalexpansion:blocks/machine/machine_bottom"));
	}

	/* IInitializer */
	@Override
	public boolean preInit() {

		this.setRegistryName("machine");
		GameRegistry.register(this);

		ItemBlockMachine itemBlock = new ItemBlockMachine(this);
		itemBlock.setRegistryName(this.getRegistryName());
		GameRegistry.register(itemBlock);

		return true;
	}

	@Override
	public boolean initialize() {

		TileMachineBase.config();

		TileFurnace.initialize();
		TilePulverizer.initialize();
		TileSawmill.initialize();
		TileSmelter.initialize();
		TileInsolator.initialize();
		TileCharger.initialize();
		TileCrucible.initialize();
		TileTransposer.initialize();
		// transcapsulator
		// centrifuge
		// crafter
		// brewer
		// enchanter
		TilePrecipitator.initialize();
		TileExtruder.initialize();

		machineFurnace = ItemBlockMachine.setDefaultTag(new ItemStack(this, 1, BlockMachine.Type.FURNACE.getMetadata()));
		machinePulverizer = ItemBlockMachine.setDefaultTag(new ItemStack(this, 1, BlockMachine.Type.PULVERIZER.getMetadata()));
		machineSawmill = ItemBlockMachine.setDefaultTag(new ItemStack(this, 1, BlockMachine.Type.SAWMILL.getMetadata()));
		machineSmelter = ItemBlockMachine.setDefaultTag(new ItemStack(this, 1, BlockMachine.Type.SMELTER.getMetadata()));
		machineInsolator = ItemBlockMachine.setDefaultTag(new ItemStack(this, 1, BlockMachine.Type.INSOLATOR.getMetadata()));
		machineCharger = ItemBlockMachine.setDefaultTag(new ItemStack(this, 1, BlockMachine.Type.CHARGER.getMetadata()));
		machineCrucible = ItemBlockMachine.setDefaultTag(new ItemStack(this, 1, BlockMachine.Type.CRUCIBLE.getMetadata()));
		machineTransposer = ItemBlockMachine.setDefaultTag(new ItemStack(this, 1, BlockMachine.Type.TRANSPOSER.getMetadata()));
		// transcapsulator
		// centrifuge
		//machineCrafter = ItemBlockMachine.setDefaultTag(new ItemStack(this, 1, BlockMachine.Type.CRAFTER.getMetadata()));
		// brewer
		// enchanter
		machinePrecipitator = ItemBlockMachine.setDefaultTag(new ItemStack(this, 1, BlockMachine.Type.PRECIPITATOR.getMetadata()));
		machineExtruder = ItemBlockMachine.setDefaultTag(new ItemStack(this, 1, BlockMachine.Type.EXTRUDER.getMetadata()));

		return true;
	}

	@Override
	public boolean postInit() {

		String machineFrame = "thermalexpansion:machineFrame";
		String copperPart = "thermalexpansion:machineCopper";
		String invarPart = "thermalexpansion:machineInvar";

		// @formatter:off
		if (enable[BlockMachine.Type.FURNACE.getMetadata()]) {
			addRecipe(ShapedRecipe(machineFurnace,
					" X ",
					"YCY",
					"IPI",
					'C', machineFrame,
					'I', copperPart,
					'P', ItemMaterial.powerCoilGold,
					'X', "dustRedstone",
					'Y', Blocks.BRICK_BLOCK
			));
		}
		if (enable[BlockMachine.Type.PULVERIZER.getMetadata()]) {
			addRecipe(ShapedRecipe(machinePulverizer,
					" X ",
					"YCY",
					"IPI",
					'C', machineFrame,
					'I', copperPart,
					'P', ItemMaterial.powerCoilGold,
					'X', Blocks.PISTON,
					'Y', Blocks.BRICK_BLOCK
			));
		}
		if (enable[BlockMachine.Type.SAWMILL.getMetadata()]) {
			addRecipe(ShapedRecipe(machineSawmill,
					" X ",
					"YCY",
					"IPI",
					'C', machineFrame,
					'I', copperPart,
					'P', ItemMaterial.powerCoilGold,
					'X', "gearIron",
					'Y', "plankWood"
			));
		}
		if (enable[BlockMachine.Type.SMELTER.getMetadata()]) {
			addRecipe(ShapedRecipe(machineSmelter,
					" X ",
					"YCY",
					"IPI",
					'C', machineFrame,
					'I', invarPart,
					'P', ItemMaterial.powerCoilGold,
					'X', Items.BUCKET,
					'Y', "ingotInvar"
			));
		}
		if (enable[BlockMachine.Type.INSOLATOR.getMetadata()]) {
			addRecipe(ShapedRecipe(machineInsolator,
					" X ",
					"YCY",
					"IPI",
					'C', machineFrame,
					'I', copperPart,
					'P', ItemMaterial.powerCoilGold,
					'X', "gearLumium",
					'Y', "dirt"
			));
		}
		if (enable[BlockMachine.Type.CHARGER.getMetadata()]) {
			addRecipe(ShapedRecipe(machineCharger,
					" X ",
					"YCY",
					"IPI",
					'C', machineFrame,
					'I', copperPart,
					'P', ItemMaterial.powerCoilGold,
					'X', "gearLumium",		// TODO: CELL
					'Y', ItemMaterial.powerCoilSilver
			));
		}
		if (enable[BlockMachine.Type.CRUCIBLE.getMetadata()]) {
			addRecipe(ShapedRecipe(machineCrucible,
					" X ",
					"YCY",
					"IPI",
					'C', machineFrame,
					'I', invarPart,
					'P', ItemMaterial.powerCoilGold,
					'X', "gearLumium",		// TODO: CELL
					'Y', Blocks.NETHER_BRICK
			));
		}
		if (enable[BlockMachine.Type.TRANSPOSER.getMetadata()]) {
			addRecipe(ShapedRecipe(machineTransposer,
					" X ",
					"YCY",
					"IPI",
					'C', machineFrame,
					'I', copperPart,
					'P', ItemMaterial.powerCoilGold,
					'X', Blocks.CHEST,
					'Y', "gearTin"
			));
		}
		if (enable[BlockMachine.Type.CRAFTER.getMetadata()]) {
			addRecipe(ShapedRecipe(machineCrafter,
					" X ",
					"YCY",
					"IPI",
					'C', machineFrame,
					'I', copperPart,
					'P', ItemMaterial.powerCoilGold,
					'X', Blocks.PISTON,
					'Y', "ingotInvar"
			));
		}
		if (enable[BlockMachine.Type.PRECIPITATOR.getMetadata()]) {
			addRecipe(ShapedRecipe(machinePrecipitator,
					" X ",
					"YCY",
					"IPI",
					'C', machineFrame,
					'I', copperPart,
					'P', ItemMaterial.powerCoilGold,
					'X', Blocks.PISTON,
					'Y', "ingotInvar"
			));
		}
		if (enable[BlockMachine.Type.EXTRUDER.getMetadata()]) {
			addRecipe(ShapedRecipe(machineExtruder,
					" X ",
					"YCY",
					"IPI",
					'C', machineFrame,
					'I', invarPart,
					'P', ItemMaterial.powerCoilGold,
					'X', Blocks.PISTON,
					'Y', "blockGlass"
			));
		}
		// @formatter:on

		return true;
	}

	/* TYPE */
	public enum Type implements IStringSerializable {

		// @formatter:off
		FURNACE(0, "furnace", machineFurnace),
		PULVERIZER(1, "pulverizer", machinePulverizer),
		SAWMILL(2, "sawmill", machineSawmill),
		SMELTER(3, "smelter", machineSmelter),
		INSOLATOR(4, "insolator", machineInsolator),
		CHARGER(5, "charger", machineCharger),
		CRUCIBLE(6, "crucible", machineCrucible),
		TRANSPOSER(7, "transposer", machineTransposer),
		TRANSCAPSULATOR(8, "transcapsulator", machineTranscapsulator),
		CENTRIFUGE(9, "centrifuge", machineCentrifuge),
		CRAFTER(10, "crafter", machineCrafter),
		BREWER(11, "brewer", machineBrewer),
		ENCHANTER(12, "enchanter", machineEnchanter),
		PRECIPITATOR(13, "precipitator", machinePrecipitator),
		EXTRUDER(14, "extruder", machineExtruder);
		// @formatter:on

		private static final BlockMachine.Type[] METADATA_LOOKUP = new BlockMachine.Type[values().length];
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

			return this.light;
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

	public static boolean[] enable = new boolean[BlockMachine.Type.values().length];

	/* REFERENCES */
	public static ItemStack machineFurnace;
	public static ItemStack machinePulverizer;
	public static ItemStack machineSawmill;
	public static ItemStack machineSmelter;
	public static ItemStack machineInsolator;
	public static ItemStack machineCharger;
	public static ItemStack machineCrucible;
	public static ItemStack machineTransposer;
	public static ItemStack machineTranscapsulator;
	public static ItemStack machineCentrifuge;
	public static ItemStack machineCrafter;
	public static ItemStack machineBrewer;
	public static ItemStack machineEnchanter;
	public static ItemStack machinePrecipitator;
	public static ItemStack machineExtruder;

}
