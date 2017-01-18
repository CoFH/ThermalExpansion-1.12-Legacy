package cofh.thermalexpansion.block.machine;

import codechicken.lib.texture.TextureUtils;
import cofh.lib.util.helpers.BlockHelper;
import cofh.lib.util.helpers.FluidHelper;
import cofh.lib.util.helpers.ItemHelper;
import cofh.thermalexpansion.block.BlockTEBase;
import cofh.thermalexpansion.init.TEProps;
import cofh.thermalexpansion.item.TEAugments;
import cofh.thermalexpansion.util.ReconfigurableHelper;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.creativetab.CreativeTabs;
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
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class BlockMachine extends BlockTEBase {

	public static final PropertyEnum<BlockMachine.Type> VARIANT = PropertyEnum.<BlockMachine.Type>create("type", BlockMachine.Type.class);

	public BlockMachine() {

		super(Material.IRON);

		setUnlocalizedName("machine");

		setHardness(15.0F);
		setResistance(25.0F);
	}

	@Override
	protected BlockStateContainer createBlockState() {

		IProperty[] listed = new IProperty[] { VARIANT };
		IUnlistedProperty[] unlisted = new IUnlistedProperty[] { TEProps.ACTIVE, TEProps.FACING, TEProps.SIDE_CONFIG[0], TEProps.SIDE_CONFIG[1], TEProps.SIDE_CONFIG[2], TEProps.SIDE_CONFIG[3], TEProps.SIDE_CONFIG[4], TEProps.SIDE_CONFIG[5] };

		return new ExtendedBlockState(this, listed, unlisted);
	}

	@Override
	@SideOnly (Side.CLIENT)
	public void getSubBlocks(@Nonnull Item item, CreativeTabs tab, List<ItemStack> list) {

		for (int i = 0; i < BlockMachine.Type.METADATA_LOOKUP.length; i++) {
			for (int j = 0; j < 4; j++) {
				if (creativeTiers[j]) {
					list.add(ItemBlockMachine.setDefaultTag(new ItemStack(item, 1, i), (byte) j));
				}
			}
		}
	}

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
		switch (BlockMachine.Type.values()[metadata]) {
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
			default:
				return null;
		}
	}

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
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, @Nullable ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {

		TileEntity tile = world.getTileEntity(pos);

		if (tile instanceof TileExtruder || tile instanceof TilePrecipitator) {
			if (tile != null && tile.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null)) {
				IFluidHandler handler = tile.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null);
				if (FluidHelper.drainItemToHandler(heldItem, handler, player, hand)) {
					return true;
				}
			}
		}
		return super.onBlockActivated(world, pos, state, player, hand, heldItem, side, hitX, hitY, hitZ);
	}

//	@Override
//	@SideOnly (Side.CLIENT)
//	public TextureAtlasSprite getTexture(EnumFacing side, int metadata) {
//
//		if (side.ordinal() == 0) {
//			return TETextures.MACHINE_BOTTOM;
//		}
//		if (side.ordinal() == 1) {
//			return TETextures.MACHINE_TOP;
//		}
//		return side.ordinal() != 2 ? TETextures.MACHINE_SIDE : TETextures.MACHINE_FACE[metadata % Type.values().length];
//	}

	@SideOnly (Side.CLIENT)
	public TextureAtlasSprite getTexture(EnumFacing side, IBlockState state, BlockRenderLayer layer, IBlockAccess access, BlockPos pos) {

		TileEntity tileEntity = access.getTileEntity(pos);
		if (tileEntity instanceof TileMachineBase) {
			TileMachineBase machine = ((TileMachineBase) tileEntity);
			//TODO ISidedTexture needs to change to support layers + passes.
			return machine.getTexture(side.ordinal(), layer == BlockRenderLayer.SOLID ? 0 : 1);
		}
		return TextureUtils.getMissingSprite();
	}

	@Override
	@SideOnly (Side.CLIENT)
	public boolean canRenderInLayer(IBlockState state, BlockRenderLayer layer) {

		return layer == BlockRenderLayer.SOLID || layer == BlockRenderLayer.CUTOUT;
	}

	@Override
	public NBTTagCompound getItemStackTag(IBlockAccess world, BlockPos pos) {

		NBTTagCompound tag = super.getItemStackTag(world, pos);
		TileMachineBase tile = (TileMachineBase) world.getTileEntity(pos);

		if (tile != null) {
			if (tag == null) {
				tag = new NBTTagCompound();
			}
			ReconfigurableHelper.setItemStackTagReconfig(tag, tile);
			tag.setInteger("Energy", tile.getEnergyStored(null));
			tile.writeAugmentsToNBT(tag);
		}
		return tag;
	}

	/* IInitializer */
	@Override
	public boolean preInit() {

		GameRegistry.registerBlock(this, ItemBlockMachine.class, "machine");

		return true;
	}

	@Override
	public boolean initialize() {

		TileMachineBase.config();
		TileFurnace.initialize();
		TilePulverizer.initialize();
		TileSawmill.initialize();
		TileSmelter.initialize();
		TileCrucible.initialize();
		TileTransposer.initialize();
		TilePrecipitator.initialize();
		TileExtruder.initialize();
		TileCharger.initialize();
		TileInsolator.initialize();

		if (defaultAutoTransfer) {
			defaultAugments[0] = ItemHelper.cloneStack(TEAugments.generalAutoOutput);
		}
		if (defaultRedstoneControl) {
			defaultAugments[1] = ItemHelper.cloneStack(TEAugments.generalRedstoneControl);
		}
		if (defaultReconfigSides) {
			defaultAugments[2] = ItemHelper.cloneStack(TEAugments.generalReconfigSides);
		}
		machineFurnace = ItemBlockMachine.setDefaultTag(new ItemStack(this, 1, Type.FURNACE.getMetadata()));
		machinePulverizer = ItemBlockMachine.setDefaultTag(new ItemStack(this, 1, Type.PULVERIZER.getMetadata()));
		machineSawmill = ItemBlockMachine.setDefaultTag(new ItemStack(this, 1, Type.SAWMILL.getMetadata()));
		machineSmelter = ItemBlockMachine.setDefaultTag(new ItemStack(this, 1, Type.SMELTER.getMetadata()));
		machineCrucible = ItemBlockMachine.setDefaultTag(new ItemStack(this, 1, Type.CRUCIBLE.getMetadata()));
		machineTransposer = ItemBlockMachine.setDefaultTag(new ItemStack(this, 1, Type.TRANSPOSER.getMetadata()));
		machineCharger = ItemBlockMachine.setDefaultTag(new ItemStack(this, 1, Type.CHARGER.getMetadata()));
		machineInsolator = ItemBlockMachine.setDefaultTag(new ItemStack(this, 1, Type.INSOLATOR.getMetadata()));

		//		machinePrecipitator = ItemBlockMachine.setDefaultTag(new ItemStack(this, 1, Type.PRECIPITATOR.ordinal()));
		//		machineExtruder = ItemBlockMachine.setDefaultTag(new ItemStack(this, 1, Type.EXTRUDER.ordinal()));
		//		machineAccumulator = ItemBlockMachine.setDefaultTag(new ItemStack(this, 1, Type.ACCUMULATOR.ordinal()));
		//		machineAssembler = ItemBlockMachine.setDefaultTag(new ItemStack(this, 1, Type.ASSEMBLER.ordinal()));

		return true;
	}

	@Override
	public boolean postInit() {

		//		String machineFrame = "thermalexpansion:machineFrame";
		//		String copperPart = "thermalexpansion:machineCopper";
		//		String invarPart = "thermalexpansion:machineInvar";
		//
		//		// @formatter:off
//		if (enable[Type.FURNACE.ordinal()]) {
//			GameRegistry.addRecipe(new RecipeMachine(furnace, defaultAugments, new Object[]{
//					" X ",
//					"YCY",
//					"IPI",
//					'C', machineFrame,
//					'I', copperPart,
//					'P', TEItemsOld.powerCoilGold,
//					'X', "dustRedstone",
//					'Y', Blocks.BRICK_BLOCK
//			}));
//		}
//		if (enable[Type.PULVERIZER.ordinal()]) {
//			String category = "Machine.Pulverizer";
//			String comment = "If enabled, the Pulverizer will require Diamonds instead of Flint.";
//			Item component = ThermalExpansion.CONFIG.get(category, "RequireDiamonds", false, comment) ? Items.DIAMOND : Items.FLINT;
//			GameRegistry.addRecipe(new RecipeMachine(pulverizer, defaultAugments, new Object[] {
//					" X ",
//					"YCY",
//					"IPI",
//					'C', machineFrame,
//					'I', copperPart,
//					'P', TEItemsOld.powerCoilGold,
//					'X', Blocks.PISTON,
//					'Y', component
//			}));
//		}
//		if (enable[Type.SAWMILL.ordinal()]) {
//			GameRegistry.addRecipe(new RecipeMachine(sawmill, defaultAugments, new Object[] {
//					" X ",
//					"YCY",
//					"IPI",
//					'C', machineFrame,
//					'I', copperPart,
//					'P', TEItemsOld.powerCoilGold,
//					'X', Items.IRON_AXE,
//					'Y', "plankWood"
//			}));
//		}
//		if (enable[Type.SMELTER.ordinal()]) {
//			GameRegistry.addRecipe(new RecipeMachine(smelter, defaultAugments, new Object[] {
//					" X ",
//					"YCY",
//					"IPI",
//					'C', machineFrame,
//					'I', invarPart,
//					'P', TEItemsOld.powerCoilGold,
//					'X', Items.BUCKET,
//					'Y', "ingotInvar"
//			}));
//		}
//		if (enable[Type.CRUCIBLE.ordinal()]) {
//			GameRegistry.addRecipe(new RecipeMachine(crucible, defaultAugments, new Object[] {
//					" X ",
//					"YCY",
//					"IPI",
//					'C', machineFrame,
//					'I', invarPart,
//					'P', TEItemsOld.powerCoilGold,
//					'X', BlockFrame.frameCellBasic,
//					'Y', Blocks.NETHER_BRICK
//			}));
//		}
//		if (enable[Type.TRANSPOSER.ordinal()]) {
//			GameRegistry.addRecipe(new RecipeMachine(transposer, defaultAugments, new Object[] {
//					" X ",
//					"YCY",
//					"IPI",
//					'C', machineFrame,
//					'I', copperPart,
//					'P', TEItemsOld.powerCoilGold,
//					'X', Items.BUCKET,
//					'Y', "blockGlass"
//			}));
//		}
//		if (enable[Type.PRECIPITATOR.ordinal()]) {
//			GameRegistry.addRecipe(new RecipeMachine(precipitator, defaultAugments, new Object[] {
//					" X ",
//					"YCY",
//					"IPI",
//					'C', machineFrame,
//					'I', copperPart,
//					'P', TEItemsOld.powerCoilGold,
//					'X', Blocks.PISTON,
//					'Y', "ingotInvar"
//			}));
//		}
//		if (enable[Type.EXTRUDER.ordinal()]) {
//			GameRegistry.addRecipe(new RecipeMachine(extruder, defaultAugments, new Object[] {
//					" X ",
//					"YCY",
//					"IPI",
//					'C', machineFrame,
//					'I', copperPart,
//					'P', TEItemsOld.pneumaticServo,
//					'X', Blocks.PISTON,
//					'Y', "blockGlass"
//			}));
//		}
//		if (enable[Type.ACCUMULATOR.ordinal()]) {
//			GameRegistry.addRecipe(new RecipeMachine(accumulator, defaultAugments, new Object[] {
//					" X ",
//					"YCY",
//					"IPI",
//					'C', machineFrame,
//					'I', copperPart,
//					'P', TEItemsOld.pneumaticServo,
//					'X', Items.BUCKET,
//					'Y', "blockGlass"
//			}));
//		}
//		if (enable[Type.ASSEMBLER.ordinal()]) {
//			GameRegistry.addRecipe(new RecipeMachine(assembler, defaultAugments, new Object[] {
//					" X ",
//					"YCY",
//					"IPI",
//					'C', machineFrame,
//					'I', copperPart,
//					'P', TEItemsOld.powerCoilGold,
//					'X', Blocks.CHEST,
//					'Y', "gearTin"
//			}));
//		}
//		if (enable[Type.CHARGER.ordinal()]) {
//			GameRegistry.addRecipe(new RecipeMachine(charger, defaultAugments, new Object[] {
//					" X ",
//					"YCY",
//					"IPI",
//					'C', machineFrame,
//					'I', copperPart,
//					'P', TEItemsOld.powerCoilGold,
//					'X', BlockFrame.frameCellBasic,
//					'Y', TEItemsOld.powerCoilSilver
//			}));
//		}
//		if (enable[Type.INSOLATOR.ordinal()]) {
//			GameRegistry.addRecipe(new RecipeMachine(insolator, defaultAugments, new Object[] {
//					" X ",
//					"YCY",
//					"IPI",
//					'C', machineFrame,
//					'I', copperPart,
//					'P', TEItemsOld.powerCoilGold,
//					'X', "gearLumium",
//					'Y', Blocks.DIRT
//			}));
//		}
		// @formatter:on

		//		TECraftingHandler.addMachineUpgradeRecipes(furnace);
		//		TECraftingHandler.addMachineUpgradeRecipes(pulverizer);
		//		TECraftingHandler.addMachineUpgradeRecipes(sawmill);
		//		TECraftingHandler.addMachineUpgradeRecipes(smelter);
		//		TECraftingHandler.addMachineUpgradeRecipes(crucible);
		//		TECraftingHandler.addMachineUpgradeRecipes(transposer);
		//		TECraftingHandler.addMachineUpgradeRecipes(precipitator);
		//		TECraftingHandler.addMachineUpgradeRecipes(extruder);
		//		TECraftingHandler.addMachineUpgradeRecipes(accumulator);
		//		TECraftingHandler.addMachineUpgradeRecipes(assembler);
		//		TECraftingHandler.addMachineUpgradeRecipes(charger);
		//		TECraftingHandler.addMachineUpgradeRecipes(insolator);
		//
		//		TECraftingHandler.addSecureRecipe(furnace);
		//		TECraftingHandler.addSecureRecipe(pulverizer);
		//		TECraftingHandler.addSecureRecipe(sawmill);
		//		TECraftingHandler.addSecureRecipe(smelter);
		//		TECraftingHandler.addSecureRecipe(crucible);
		//		TECraftingHandler.addSecureRecipe(transposer);
		//		TECraftingHandler.addSecureRecipe(precipitator);
		//		TECraftingHandler.addSecureRecipe(extruder);
		//		TECraftingHandler.addSecureRecipe(accumulator);
		//		TECraftingHandler.addSecureRecipe(assembler);
		//		TECraftingHandler.addSecureRecipe(charger);
		//		TECraftingHandler.addSecureRecipe(insolator);

		return true;
	}

	public static void refreshItemStacks() {

		//		furnace = ItemBlockMachine.setDefaultTag(furnace);
		//		pulverizer = ItemBlockMachine.setDefaultTag(pulverizer);
		//		sawmill = ItemBlockMachine.setDefaultTag(sawmill);
		//		smelter = ItemBlockMachine.setDefaultTag(smelter);
		//		crucible = ItemBlockMachine.setDefaultTag(crucible);
		//		transposer = ItemBlockMachine.setDefaultTag(transposer);
		//		precipitator = ItemBlockMachine.setDefaultTag(precipitator);
		//		extruder = ItemBlockMachine.setDefaultTag(extruder);
		//		accumulator = ItemBlockMachine.setDefaultTag(accumulator);
		//		assembler = ItemBlockMachine.setDefaultTag(assembler);
		//		charger = ItemBlockMachine.setDefaultTag(charger);
		//		insolator = ItemBlockMachine.setDefaultTag(insolator);
	}

	/* TYPE */
	public static enum Type implements IStringSerializable {

		// @formatter:off
		FURNACE(0, "furnace", machineFurnace),
		PULVERIZER(1, "pulverizer", machinePulverizer),
		SAWMILL(2, "sawmill", machineSawmill),
		SMELTER(3, "smelter", machineSmelter),
		INSOLATOR(4, "insolator", machineInsolator),
		CHARGER(5, "charger", machineCharger),
		CRUCIBLE(6, "crucible", machineCrucible),
		TRANSPOSER(7, "transposer", machineTransposer),
		// TRANSCAPSULATOR
		CENTRIFUGE(9, "centrifuge", machineCentrifuge),
		CRAFTER(10, "crafter", machineCrafter),
		// BREWER
		// ENCHANTER
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

	public static boolean[] enable = new boolean[Type.values().length];
	public static boolean[] creativeTiers = new boolean[4];
	public static ItemStack[] defaultAugments = new ItemStack[3];

	public static boolean defaultAutoTransfer = true;
	public static boolean defaultRedstoneControl = true;
	public static boolean defaultReconfigSides = true;

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
