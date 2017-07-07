package cofh.thermalexpansion.block.machine;

import codechicken.lib.model.ModelRegistryHelper;
import codechicken.lib.model.bakery.CCBakeryModel;
import codechicken.lib.model.bakery.IBakeryProvider;
import codechicken.lib.model.bakery.ModelBakery;
import codechicken.lib.model.bakery.generation.IBakery;
import codechicken.lib.texture.IWorldBlockTextureProvider;
import codechicken.lib.texture.TextureUtils;
import cofh.core.render.IModelRegister;
import cofh.core.util.helpers.BlockHelper;
import cofh.core.util.helpers.FluidHelper;
import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalexpansion.block.BlockTEBase;
import cofh.thermalexpansion.init.TEProps;
import cofh.thermalexpansion.init.TETextures;
import cofh.thermalexpansion.item.ItemFrame;
import cofh.thermalexpansion.plugins.jei.crafting.centrifuge.CentrifugeRecipeCategory;
import cofh.thermalexpansion.plugins.jei.crafting.charger.ChargerRecipeCategory;
import cofh.thermalexpansion.plugins.jei.crafting.compactor.CompactorRecipeCategory;
import cofh.thermalexpansion.plugins.jei.crafting.crucible.CrucibleRecipeCategory;
import cofh.thermalexpansion.plugins.jei.crafting.furnace.FurnaceRecipeCategory;
import cofh.thermalexpansion.plugins.jei.crafting.insolator.InsolatorRecipeCategory;
import cofh.thermalexpansion.plugins.jei.crafting.pulverizer.PulverizerRecipeCategory;
import cofh.thermalexpansion.plugins.jei.crafting.refinery.RefineryRecipeCategory;
import cofh.thermalexpansion.plugins.jei.crafting.sawmill.SawmillRecipeCategory;
import cofh.thermalexpansion.plugins.jei.crafting.smelter.SmelterRecipeCategory;
import cofh.thermalexpansion.plugins.jei.crafting.transposer.TransposerRecipeCategory;
import cofh.thermalexpansion.render.RenderMachine;
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
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import static cofh.core.util.helpers.RecipeHelper.addShapedRecipe;

public class BlockMachine extends BlockTEBase implements IModelRegister, IBakeryProvider, IWorldBlockTextureProvider {

	public static final PropertyEnum<Type> VARIANT = PropertyEnum.create("type", Type.class);

	public BlockMachine() {

		super(Material.IRON);

		setUnlocalizedName("machine");

		setHardness(15.0F);
		setResistance(25.0F);
		setDefaultState(getBlockState().getBaseState().withProperty(VARIANT, Type.FURNACE));
	}

	@Override
	protected BlockStateContainer createBlockState() {

		BlockStateContainer.Builder builder = new BlockStateContainer.Builder(this);
		// Listed
		builder.add(VARIANT);
		// UnListed
		builder.add(TEProps.CREATIVE);
		builder.add(TEProps.LEVEL);
		builder.add(TEProps.ACTIVE);
		builder.add(TEProps.FACING);
		builder.add(TEProps.SIDE_CONFIG);
		builder.add(TEProps.TILE);

		return builder.build();
	}

	@Override
	public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> items) {

		for (int i = 0; i < Type.METADATA_LOOKUP.length; i++) {
			if (enable[i]) {
				if (TEProps.creativeTabShowAllLevels) {
					for (int j = 0; j < 5; j++) {
						items.add(itemBlock.setDefaultTag(new ItemStack(this, 1, i), j));
					}
				} else {
					items.add(itemBlock.setDefaultTag(new ItemStack(this, 1, i), TEProps.creativeTabLevel));
				}
				if (TEProps.creativeTabShowCreative) {
					items.add(itemBlock.setCreativeTag(new ItemStack(this, 1, i), 4));
				}
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
			case COMPACTOR:
				return new TileCompactor();
			case CRUCIBLE:
				return new TileCrucible();
			case REFINERY:
				return new TileRefinery();
			case TRANSPOSER:
				return new TileTransposer();
			case CHARGER:
				return new TileCharger();
			case CENTRIFUGE:
				return new TileCentrifuge();
			case CRAFTER:                       // TODO
				return null;
			case BREWER:                        // TODO
				return null;
			case ENCHANTER:                     // TODO
				return null;
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

			tile.setLevel(stack.getTagCompound().getByte("Level"));
			tile.readAugmentsFromNBT(stack.getTagCompound());
			tile.updateAugmentStatus();
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
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {

		TileEntity tile = world.getTileEntity(pos);

		if (!(tile instanceof TileTransposer) && tile.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null)) {
			IFluidHandler handler = tile.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null);
			if (FluidHelper.drainItemToHandler(player.getHeldItem(hand), handler, player, hand)) {
				return true;
			}
		}
		return super.onBlockActivated(world, pos, state, player, hand, side, hitX, hitY, hitZ);
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
	public IBakery getBakery() {

		return RenderMachine.INSTANCE;
	}

	@Override // Inventory
	@SideOnly (Side.CLIENT)
	public TextureAtlasSprite getTexture(EnumFacing side, ItemStack stack) {

		if (side == EnumFacing.DOWN) {
			return TETextures.MACHINE_BOTTOM;
		}
		if (side == EnumFacing.UP) {
			return TETextures.MACHINE_TOP;
		}
		return side != EnumFacing.NORTH ? TETextures.MACHINE_SIDE : TETextures.MACHINE_FACE[stack.getMetadata() % Type.values().length];
	}

	@Override // World
	@SideOnly (Side.CLIENT)
	public TextureAtlasSprite getTexture(EnumFacing side, IBlockState state, BlockRenderLayer layer, IBlockAccess world, BlockPos pos) {

		TileEntity tileEntity = world.getTileEntity(pos);
		if (tileEntity instanceof TileMachineBase) {
			TileMachineBase tile = ((TileMachineBase) tileEntity);
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
		ModelRegistryHelper.register(location, new CCBakeryModel("thermalexpansion:blocks/machine/machine_top_0"));

		ModelBakery.registerBlockKeyGenerator(this, state -> {

			TileMachineBase tile = ((TileMachineBase) state.getValue(TEProps.TILE));
			StringBuilder builder = new StringBuilder(state.getBlock().getRegistryName() + "|" + state.getBlock().getMetaFromState(state));
			builder.append(",creative=").append(state.getValue(TEProps.CREATIVE));
			builder.append(",level=").append(state.getValue(TEProps.LEVEL));
			builder.append(",facing=").append(state.getValue(TEProps.FACING));
			builder.append(",active=").append(state.getValue(TEProps.ACTIVE));
			builder.append(",side_config={");
			for (int i : state.getValue(TEProps.SIDE_CONFIG)) {
				builder.append(",").append(i);
			}
			builder.append("}");
			if (tile.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null) && tile.isActive) {
				builder.append(",front=").append(tile.getTexture(tile.getFacing(), 0).getIconName());
			}
			return builder.toString();
		});

		ModelBakery.registerItemKeyGenerator(itemBlock, stack -> ModelBakery.defaultItemKeyGenerator.generateKey(stack) + ",creative=" + itemBlock.isCreative(stack) + ",level=" + itemBlock.getLevel(stack));
	}

	/* IInitializer */
	@Override
	public boolean initialize() {

		this.setRegistryName("machine");
		ForgeRegistries.BLOCKS.register(this);

		itemBlock = new ItemBlockMachine(this);
		itemBlock.setRegistryName(this.getRegistryName());
		ForgeRegistries.ITEMS.register(itemBlock);

		TileMachineBase.config();

		TileFurnace.initialize();
		TilePulverizer.initialize();
		TileSawmill.initialize();
		TileSmelter.initialize();
		TileInsolator.initialize();
		TileCompactor.initialize();
		TileCrucible.initialize();
		TileRefinery.initialize();
		TileTransposer.initialize();
		TileCharger.initialize();
		TileCentrifuge.initialize();
		// TileCrafter.initialize();
		// TileBrewer.initialize();
		// TileEnchanter.initialize();
		TilePrecipitator.initialize();
		TileExtruder.initialize();

		ThermalExpansion.proxy.addIModelRegister(this);

		return true;
	}

	@Override
	public boolean register() {

		machineFurnace = itemBlock.setDefaultTag(new ItemStack(this, 1, Type.FURNACE.getMetadata()));
		machinePulverizer = itemBlock.setDefaultTag(new ItemStack(this, 1, Type.PULVERIZER.getMetadata()));
		machineSawmill = itemBlock.setDefaultTag(new ItemStack(this, 1, Type.SAWMILL.getMetadata()));
		machineSmelter = itemBlock.setDefaultTag(new ItemStack(this, 1, Type.SMELTER.getMetadata()));
		machineInsolator = itemBlock.setDefaultTag(new ItemStack(this, 1, Type.INSOLATOR.getMetadata()));
		machineCompactor = itemBlock.setDefaultTag(new ItemStack(this, 1, Type.COMPACTOR.getMetadata()));
		machineCrucible = itemBlock.setDefaultTag(new ItemStack(this, 1, Type.CRUCIBLE.getMetadata()));
		machineRefinery = itemBlock.setDefaultTag(new ItemStack(this, 1, Type.REFINERY.getMetadata()));
		machineTransposer = itemBlock.setDefaultTag(new ItemStack(this, 1, Type.TRANSPOSER.getMetadata()));
		machineCharger = itemBlock.setDefaultTag(new ItemStack(this, 1, Type.CHARGER.getMetadata()));
		machineCentrifuge = itemBlock.setDefaultTag(new ItemStack(this, 1, Type.CENTRIFUGE.getMetadata()));
		// machineCrafter = itemBlock.setDefaultTag(new ItemStack(this, 1, Type.CRAFTER.getMetadata()));
		// machineBrewer = itemBlock.setDefaultTag(new ItemStack(this, 1, Type.BREWER.getMetadata()));
		// machineEnchanter = itemBlock.setDefaultTag(new ItemStack(this, 1, Type.ENCHANTER.getMetadata()));
		machinePrecipitator = itemBlock.setDefaultTag(new ItemStack(this, 1, Type.PRECIPITATOR.getMetadata()));
		machineExtruder = itemBlock.setDefaultTag(new ItemStack(this, 1, Type.EXTRUDER.getMetadata()));

		addRecipes();

		return true;
	}

	/* HELPERS */
	private void addRecipes() {

		String copperPart = "gearCopper";
		String invarPart = "gearInvar";

		// @formatter:off
		if (enable[Type.FURNACE.getMetadata()]) {
			addShapedRecipe(machineFurnace,
					" X ",
					"YCY",
					"IPI",
					'C', ItemFrame.frameMachine,
					'I', copperPart,
					'P', ItemMaterial.powerCoilGold,
					'X', "dustRedstone",
					'Y', Blocks.BRICK_BLOCK
			);
		} else {
			FurnaceRecipeCategory.enable = false;
		}
		if (enable[Type.PULVERIZER.getMetadata()]) {
			addShapedRecipe(machinePulverizer,
					" X ",
					"YCY",
					"IPI",
					'C', ItemFrame.frameMachine,
					'I', copperPart,
					'P', ItemMaterial.powerCoilGold,
					'X', Blocks.PISTON,
					'Y', Items.FLINT
			);
		} else {
			PulverizerRecipeCategory.enable = false;
		}
		if (enable[Type.SAWMILL.getMetadata()]) {
			addShapedRecipe(machineSawmill,
					" X ",
					"YCY",
					"IPI",
					'C', ItemFrame.frameMachine,
					'I', copperPart,
					'P', ItemMaterial.powerCoilGold,
					'X', "gearIron",
					'Y', "plankWood"
			);
		} else {
			SawmillRecipeCategory.enable = false;
		}
		if (enable[Type.SMELTER.getMetadata()]) {
			addShapedRecipe(machineSmelter,
					" X ",
					"YCY",
					"IPI",
					'C', ItemFrame.frameMachine,
					'I', invarPart,
					'P', ItemMaterial.powerCoilGold,
					'X', Items.BUCKET,
					'Y', "ingotInvar"
			);
		}else {
			SmelterRecipeCategory.enable = false;
		}
		if (enable[Type.INSOLATOR.getMetadata()]) {
			addShapedRecipe(machineInsolator,
					" X ",
					"YCY",
					"IPI",
					'C', ItemFrame.frameMachine,
					'I', copperPart,
					'P', ItemMaterial.powerCoilGold,
					'X', "gearLumium",
					'Y', "dirt"
			);
		}else {
			InsolatorRecipeCategory.enable = false;
		}
		if (enable[Type.COMPACTOR.getMetadata()]) {
			addShapedRecipe(machineCompactor,
					" X ",
					"YCY",
					"IPI",
					'C', ItemFrame.frameMachine,
					'I', copperPart,
					'P', ItemMaterial.powerCoilGold,
					'X', Blocks.PISTON,
					'Y', "ingotBronze"
			);
		}else {
			CompactorRecipeCategory.enable = false;
		}
		if (enable[Type.CRUCIBLE.getMetadata()]) {
			addShapedRecipe(machineCrucible,
					" X ",
					"YCY",
					"IPI",
					'C', ItemFrame.frameMachine,
					'I', invarPart,
					'P', ItemMaterial.powerCoilGold,
					'X', ItemMaterial.powerCoilElectrum,
					'Y', Blocks.NETHER_BRICK
			);
		}else {
			CrucibleRecipeCategory.enable = false;
		}
		if (enable[Type.REFINERY.getMetadata()]) {
			addShapedRecipe(machineRefinery,
					" X ",
					"YCY",
					"IPI",
					'C', ItemFrame.frameMachine,
					'I', copperPart,
					'P', ItemMaterial.powerCoilGold,
					'X', "gearNickel",
					'Y', "blockGlass"
			);
		} else {
			RefineryRecipeCategory.enable = false;
		}
		if (enable[Type.TRANSPOSER.getMetadata()]) {
			addShapedRecipe(machineTransposer,
					" X ",
					"YCY",
					"IPI",
					'C', ItemFrame.frameMachine,
					'I', invarPart,
					'P', ItemMaterial.powerCoilGold,
					'X', Items.BUCKET,
					'Y', "blockGlass"
			);
		} else {
			TransposerRecipeCategory.enable = false;
		}
		if (enable[Type.CHARGER.getMetadata()]) {
			addShapedRecipe(machineCharger,
					" X ",
					"YCY",
					"IPI",
					'C', ItemFrame.frameMachine,
					'I', copperPart,
					'P', ItemMaterial.powerCoilGold,
					'X', ItemMaterial.powerCoilElectrum,
					'Y', ItemMaterial.powerCoilSilver
			);
		} else {
			ChargerRecipeCategory.enable = false;
		}
		if (enable[Type.CENTRIFUGE.getMetadata()]) {
			addShapedRecipe(machineCentrifuge,
					" X ",
					"YCY",
					"IPI",
					'C', ItemFrame.frameMachine,
					'I', copperPart,
					'P', ItemMaterial.powerCoilGold,
					'X', Items.COMPASS,
					'Y', "ingotConstantan"
			);
		} else {
			CentrifugeRecipeCategory.enable = false;
		}
//		if (enable[Type.CRAFTER.getMetadata()]) {
//			addShapedRecipe(machineCrafter,
//					" X ",
//					"YCY",
//					"IPI",
//					'C', ItemFrame.frameMachine,
//					'I', copperPart,
//					'P', ItemMaterial.powerCoilGold,
//					'X', "chestWood",
//					'Y', "gearTin"
//			);
//		}
		if (enable[Type.PRECIPITATOR.getMetadata()]) {
			addShapedRecipe(machinePrecipitator,
					" X ",
					"YCY",
					"IPI",
					'C', ItemFrame.frameMachine,
					'I', copperPart,
					'P', ItemMaterial.powerCoilGold,
					'X', Blocks.PISTON,
					'Y', "ingotInvar"
			);
		}
		if (enable[Type.EXTRUDER.getMetadata()]) {
			addShapedRecipe(machineExtruder,
					" X ",
					"YCY",
					"IPI",
					'C', ItemFrame.frameMachine,
					'I', invarPart,
					'P', ItemMaterial.powerCoilGold,
					'X', Blocks.PISTON,
					'Y', "blockGlass"
			);
		}
		// @formatter:on
	}

	/* TYPE */
	public enum Type implements IStringSerializable {

		// @formatter:off
		FURNACE(0, "furnace"),
		PULVERIZER(1, "pulverizer"),
		SAWMILL(2, "sawmill"),
		SMELTER(3, "smelter"),
		INSOLATOR(4, "insolator"),
		COMPACTOR(5, "compactor"),
		CRUCIBLE(6, "crucible"),
		REFINERY(7, "refinery"),
		TRANSPOSER(8, "transposer"),
		CHARGER(9, "charger"),
		CENTRIFUGE(10, "centrifuge"),
		CRAFTER(11, "crafter"),
		BREWER(12, "brewer"),
		ENCHANTER(13, "enchanter"),
		PRECIPITATOR(14, "precipitator"),
		EXTRUDER(15, "extruder");
		// @formatter:on

		private static final Type[] METADATA_LOOKUP = new Type[values().length];
		private final int metadata;
		private final String name;

		Type(int metadata, String name) {

			this.metadata = metadata;
			this.name = name;
		}

		public int getMetadata() {

			return this.metadata;
		}

		@Override
		public String getName() {

			return this.name;
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
	public static ItemStack machineFurnace;
	public static ItemStack machinePulverizer;
	public static ItemStack machineSawmill;
	public static ItemStack machineSmelter;
	public static ItemStack machineInsolator;
	public static ItemStack machineCompactor;
	public static ItemStack machineCrucible;
	public static ItemStack machineRefinery;
	public static ItemStack machineTransposer;
	public static ItemStack machineCharger;
	public static ItemStack machineCentrifuge;
	public static ItemStack machineCrafter;
	public static ItemStack machineBrewer;
	public static ItemStack machineEnchanter;
	public static ItemStack machinePrecipitator;
	public static ItemStack machineExtruder;

	public static ItemBlockMachine itemBlock;

}
