package cofh.thermalexpansion.block.machine;

import codechicken.lib.block.IParticleProvider;
import codechicken.lib.block.IType;
import codechicken.lib.item.ItemStackRegistry;
import cofh.core.render.IconRegistry;
import cofh.lib.util.helpers.BlockHelper;
import cofh.lib.util.helpers.FluidHelper;
import cofh.lib.util.helpers.ItemHelper;
import cofh.lib.util.helpers.StringHelper;
import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalexpansion.block.BlockTEBase;
import cofh.thermalexpansion.block.CommonProperties;
import cofh.thermalexpansion.block.simple.BlockFrame;
import cofh.thermalexpansion.client.bakery.BlockBakery;
import cofh.thermalexpansion.client.IBlockLayeredTextureProvider;
import cofh.thermalexpansion.core.TEProps;
import cofh.thermalexpansion.item.TEAugments;
import cofh.thermalexpansion.item.TEItems;
import cofh.thermalexpansion.plugins.nei.handlers.NEIRecipeWrapper;
import cofh.thermalexpansion.util.crafting.RecipeMachine;
import cofh.thermalexpansion.util.crafting.TECraftingHandler;
import cofh.thermalexpansion.util.helpers.ReconfigurableHelper;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import java.util.List;
import java.util.Locale;

import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.IFluidHandler;

import javax.annotation.Nullable;

public class BlockMachine extends BlockTEBase implements IBlockLayeredTextureProvider {

    public static final PropertyEnum<Types> TYPES = PropertyEnum.create("type", Types.class);

	public BlockMachine() {

		super(Material.IRON);
		setHardness(15.0F);
		setResistance(25.0F);
		setUnlocalizedName("thermalexpansion.machine");
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
        return new ExtendedBlockState.Builder(this).add(TYPES).add(CommonProperties.SPRITE_FACE_LAYER_PROPERTY ).build();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos) {
        return BlockBakery.handleExtendedState((IExtendedBlockState) state, world.getTileEntity(pos));
    }

    @Override
    public TileEntity createNewTileEntity(World world, int metadata) {

		if (metadata >= Types.values().length) {
            return null;
		}
		switch (Types.values()[metadata]) {
		case FURNACE:
			return new TileFurnace();
		case PULVERIZER:
			return new TilePulverizer();
		case SAWMILL:
			return new TileSawmill();
		case SMELTER:
			return new TileSmelter();
		case CRUCIBLE:
			return new TileCrucible();
		case TRANSPOSER:
			return new TileTransposer();
		case PRECIPITATOR:
			return new TilePrecipitator();
		case EXTRUDER:
			return new TileExtruder();
		case ACCUMULATOR:
			return new TileAccumulator();
		case ASSEMBLER:
			return new TileAssembler();
		case CHARGER:
			return new TileCharger();
		case INSOLATOR:
			return new TileInsolator();
		default:
			return null;
		}
	}

	@Override
	public void getSubBlocks(Item item, CreativeTabs tab, List<ItemStack> list) {

		for (int i = 0; i < Types.values().length; i++) {
			for (int j = 0; j < 4; j++) {
				if (creativeTiers[j]) {
					list.add(ItemBlockMachine.setDefaultTag(new ItemStack(item, 1, i), (byte) j));
				}
			}
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
			if (FluidHelper.fillHandlerWithContainer(world, (IFluidHandler) tile, player)) {
				return true;
			}
		}
		return super.onBlockActivated(world, pos, state, player, hand, heldItem, side, hitX, hitY, hitZ);
	}

    @Override
    public boolean isNormalCube(IBlockState state, IBlockAccess world, BlockPos pos) {
		return false;
	}

    @Override
    public boolean isSideSolid(IBlockState base_state, IBlockAccess world, BlockPos pos, EnumFacing side) {
		return true;
	}


	@Override
    @SideOnly(Side.CLIENT)
	public TextureAtlasSprite getTexture(EnumFacing side, int metadata) {

		if (side.ordinal() == 0) {
			return IconRegistry.getIcon("MachineBottom");
		}
		if (side.ordinal() == 1) {
			return IconRegistry.getIcon("MachineTop");
		}
		return side.ordinal() != 3 ? IconRegistry.getIcon("MachineSide") : IconRegistry.getIcon("MachineFace", metadata % Types.values().length);
	}

    @Override
    @SideOnly(Side.CLIENT)
    public int getTexturePasses() {
        return 2;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public BlockRenderLayer getRenderlayerForPass(int pass) {
        return pass >= 1 ? BlockRenderLayer.CUTOUT : BlockRenderLayer.SOLID;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean canRenderInLayer(IBlockState state, BlockRenderLayer layer) {
        return layer == BlockRenderLayer.SOLID || layer == BlockRenderLayer.CUTOUT;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(TextureMap textureMap) {
        // Base Textures
        IconRegistry.addIcon("MachineBottom", "thermalexpansion:blocks/machine/machine_bottom", textureMap);
        IconRegistry.addIcon("MachineTop", "thermalexpansion:blocks/machine/machine_top", textureMap);
        IconRegistry.addIcon("MachineSide", "thermalexpansion:blocks/machine/machine_side", textureMap);

        // Face Textures
        for (int i = 0; i < Types.values().length; i++) {
            IconRegistry.addIcon("MachineFace" + i, "thermalexpansion:blocks/machine/machine_face_" + NAMES[i], textureMap);
            IconRegistry.addIcon("MachineActive" + i, "thermalexpansion:blocks/machine/machine_active_" + NAMES[i], textureMap);
        }

        // Config Textures
        IconRegistry.addIcon(TEProps.TEXTURE_DEFAULT + 0, "thermalexpansion:blocks/config/config_none", textureMap);
        IconRegistry.addIcon(TEProps.TEXTURE_DEFAULT + 1, "thermalexpansion:blocks/config/config_blue", textureMap);
        IconRegistry.addIcon(TEProps.TEXTURE_DEFAULT + 2, "thermalexpansion:blocks/config/config_red", textureMap);
        IconRegistry.addIcon(TEProps.TEXTURE_DEFAULT + 3, "thermalexpansion:blocks/config/config_yellow", textureMap);
        IconRegistry.addIcon(TEProps.TEXTURE_DEFAULT + 4, "thermalexpansion:blocks/config/config_orange", textureMap);
        IconRegistry.addIcon(TEProps.TEXTURE_DEFAULT + 5, "thermalexpansion:blocks/config/config_green", textureMap);
        IconRegistry.addIcon(TEProps.TEXTURE_DEFAULT + 6, "thermalexpansion:blocks/config/config_purple", textureMap);
        IconRegistry.addIcon(TEProps.TEXTURE_DEFAULT + 7, "thermalexpansion:blocks/config/config_open", textureMap);

        IconRegistry.addIcon(TEProps.TEXTURE_CB + 0, "thermalexpansion:blocks/config/config_none", textureMap);
        IconRegistry.addIcon(TEProps.TEXTURE_CB + 1, "thermalexpansion:blocks/config/config_blue_cb", textureMap);
        IconRegistry.addIcon(TEProps.TEXTURE_CB + 2, "thermalexpansion:blocks/config/config_red_cb", textureMap);
        IconRegistry.addIcon(TEProps.TEXTURE_CB + 3, "thermalexpansion:blocks/config/config_yellow_cb", textureMap);
        IconRegistry.addIcon(TEProps.TEXTURE_CB + 4, "thermalexpansion:blocks/config/config_orange_cb", textureMap);
        IconRegistry.addIcon(TEProps.TEXTURE_CB + 5, "thermalexpansion:blocks/config/config_green_cb", textureMap);
        IconRegistry.addIcon(TEProps.TEXTURE_CB + 6, "thermalexpansion:blocks/config/config_purple_cb", textureMap);
        IconRegistry.addIcon(TEProps.TEXTURE_CB + 7, "thermalexpansion:blocks/config/config_open", textureMap);
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
	public boolean initialize() {

		TileMachineBase.configure();
		TileFurnace.initialize();
		TilePulverizer.initialize();
		TileSawmill.initialize();
		TileSmelter.initialize();
		TileCrucible.initialize();
		TileTransposer.initialize();
		TilePrecipitator.initialize();
		TileExtruder.initialize();
		TileAccumulator.initialize();
		TileAssembler.initialize();
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
		furnace = ItemBlockMachine.setDefaultTag(new ItemStack(this, 1, Types.FURNACE.ordinal()));
		pulverizer = ItemBlockMachine.setDefaultTag(new ItemStack(this, 1, Types.PULVERIZER.ordinal()));
		sawmill = ItemBlockMachine.setDefaultTag(new ItemStack(this, 1, Types.SAWMILL.ordinal()));
		smelter = ItemBlockMachine.setDefaultTag(new ItemStack(this, 1, Types.SMELTER.ordinal()));
		crucible = ItemBlockMachine.setDefaultTag(new ItemStack(this, 1, Types.CRUCIBLE.ordinal()));
		transposer = ItemBlockMachine.setDefaultTag(new ItemStack(this, 1, Types.TRANSPOSER.ordinal()));
		precipitator = ItemBlockMachine.setDefaultTag(new ItemStack(this, 1, Types.PRECIPITATOR.ordinal()));
		extruder = ItemBlockMachine.setDefaultTag(new ItemStack(this, 1, Types.EXTRUDER.ordinal()));
		accumulator = ItemBlockMachine.setDefaultTag(new ItemStack(this, 1, Types.ACCUMULATOR.ordinal()));
		assembler = ItemBlockMachine.setDefaultTag(new ItemStack(this, 1, Types.ASSEMBLER.ordinal()));
		charger = ItemBlockMachine.setDefaultTag(new ItemStack(this, 1, Types.CHARGER.ordinal()));
		insolator = ItemBlockMachine.setDefaultTag(new ItemStack(this, 1, Types.INSOLATOR.ordinal()));

		ItemStackRegistry.registerCustomItemStack("furnace", furnace);
        ItemStackRegistry.registerCustomItemStack("pulverizer", pulverizer);
        ItemStackRegistry.registerCustomItemStack("sawmill", sawmill);
        ItemStackRegistry.registerCustomItemStack("smelter", smelter);
        ItemStackRegistry.registerCustomItemStack("crucible", crucible);
        ItemStackRegistry.registerCustomItemStack("transposer", transposer);
        ItemStackRegistry.registerCustomItemStack("precipitator", precipitator);
        ItemStackRegistry.registerCustomItemStack("extruder", extruder);
        ItemStackRegistry.registerCustomItemStack("accumulator", accumulator);
        ItemStackRegistry.registerCustomItemStack("assembler", assembler);
        ItemStackRegistry.registerCustomItemStack("charger", charger);
        ItemStackRegistry.registerCustomItemStack("insolator", insolator);

		return true;
	}

	@Override
	public boolean postInit() {

		String machineFrame = "thermalexpansion:machineFrame";
		String copperPart = "thermalexpansion:machineCopper";
		String invarPart = "thermalexpansion:machineInvar";

		// @formatter:off
		if (enable[Types.FURNACE.ordinal()]) {
			NEIRecipeWrapper.addMachineRecipe(new RecipeMachine(furnace, defaultAugments, new Object[] {
					" X ",
					"YCY",
					"IPI",
					'C', machineFrame,
					'I', copperPart,
					'P', TEItems.powerCoilGold,
					'X', "dustRedstone",
					'Y', Blocks.BRICK_BLOCK
			}));
		}
		if (enable[Types.PULVERIZER.ordinal()]) {
			String category = "Machine.Pulverizer";
			String comment = "If enabled, the Pulverizer will require Diamonds instead of Flint.";
			Item component = ThermalExpansion.config.get(category, "RequireDiamonds", false, comment) ? Items.DIAMOND : Items.FLINT;
			NEIRecipeWrapper.addMachineRecipe(new RecipeMachine(pulverizer, defaultAugments, new Object[] {
					" X ",
					"YCY",
					"IPI",
					'C', machineFrame,
					'I', copperPart,
					'P', TEItems.powerCoilGold,
					'X', Blocks.PISTON,
					'Y', component
			}));
		}
		if (enable[Types.SAWMILL.ordinal()]) {
			NEIRecipeWrapper.addMachineRecipe(new RecipeMachine(sawmill, defaultAugments, new Object[] {
					" X ",
					"YCY",
					"IPI",
					'C', machineFrame,
					'I', copperPart,
					'P', TEItems.powerCoilGold,
					'X', Items.IRON_AXE,
					'Y', "plankWood"
			}));
		}
		if (enable[Types.SMELTER.ordinal()]) {
			NEIRecipeWrapper.addMachineRecipe(new RecipeMachine(smelter, defaultAugments, new Object[] {
					" X ",
					"YCY",
					"IPI",
					'C', machineFrame,
					'I', invarPart,
					'P', TEItems.powerCoilGold,
					'X', Items.BUCKET,
					'Y', "ingotInvar"
			}));
		}
		if (enable[Types.CRUCIBLE.ordinal()]) {
			NEIRecipeWrapper.addMachineRecipe(new RecipeMachine(crucible, defaultAugments, new Object[] {
					" X ",
					"YCY",
					"IPI",
					'C', machineFrame,
					'I', invarPart,
					'P', TEItems.powerCoilGold,
					'X', BlockFrame.frameCellBasic,
					'Y', Blocks.NETHER_BRICK
			}));
		}
		if (enable[Types.TRANSPOSER.ordinal()]) {
			NEIRecipeWrapper.addMachineRecipe(new RecipeMachine(transposer, defaultAugments, new Object[] {
					" X ",
					"YCY",
					"IPI",
					'C', machineFrame,
					'I', copperPart,
					'P', TEItems.powerCoilGold,
					'X', Items.BUCKET,
					'Y', "blockGlass"
			}));
		}
		if (enable[Types.PRECIPITATOR.ordinal()]) {
			NEIRecipeWrapper.addMachineRecipe(new RecipeMachine(precipitator, defaultAugments, new Object[] {
					" X ",
					"YCY",
					"IPI",
					'C', machineFrame,
					'I', copperPart,
					'P', TEItems.powerCoilGold,
					'X', Blocks.PISTON,
					'Y', "ingotInvar"
			}));
		}
		if (enable[Types.EXTRUDER.ordinal()]) {
			NEIRecipeWrapper.addMachineRecipe(new RecipeMachine(extruder, defaultAugments, new Object[] {
					" X ",
					"YCY",
					"IPI",
					'C', machineFrame,
					'I', copperPart,
					'P', TEItems.pneumaticServo,
					'X', Blocks.PISTON,
					'Y', "blockGlass"
			}));
		}
		if (enable[Types.ACCUMULATOR.ordinal()]) {
			NEIRecipeWrapper.addMachineRecipe(new RecipeMachine(accumulator, defaultAugments, new Object[] {
					" X ",
					"YCY",
					"IPI",
					'C', machineFrame,
					'I', copperPart,
					'P', TEItems.pneumaticServo,
					'X', Items.BUCKET,
					'Y', "blockGlass"
			}));
		}
		if (enable[Types.ASSEMBLER.ordinal()]) {
			NEIRecipeWrapper.addMachineRecipe(new RecipeMachine(assembler, defaultAugments, new Object[] {
					" X ",
					"YCY",
					"IPI",
					'C', machineFrame,
					'I', copperPart,
					'P', TEItems.powerCoilGold,
					'X', Blocks.CHEST,
					'Y', "gearTin"
			}));
		}
		if (enable[Types.CHARGER.ordinal()]) {
			NEIRecipeWrapper.addMachineRecipe(new RecipeMachine(charger, defaultAugments, new Object[] {
					" X ",
					"YCY",
					"IPI",
					'C', machineFrame,
					'I', copperPart,
					'P', TEItems.powerCoilGold,
					'X', BlockFrame.frameCellBasic,
					'Y', TEItems.powerCoilSilver
			}));
		}
		if (enable[Types.INSOLATOR.ordinal()]) {
			NEIRecipeWrapper.addMachineRecipe(new RecipeMachine(insolator, defaultAugments, new Object[] {
					" X ",
					"YCY",
					"IPI",
					'C', machineFrame,
					'I', copperPart,
					'P', TEItems.powerCoilGold,
					'X', "gearLumium",
					'Y', Blocks.DIRT
			}));
		}
		// @formatter:on

		TECraftingHandler.addMachineUpgradeRecipes(furnace);
		TECraftingHandler.addMachineUpgradeRecipes(pulverizer);
		TECraftingHandler.addMachineUpgradeRecipes(sawmill);
		TECraftingHandler.addMachineUpgradeRecipes(smelter);
		TECraftingHandler.addMachineUpgradeRecipes(crucible);
		TECraftingHandler.addMachineUpgradeRecipes(transposer);
		TECraftingHandler.addMachineUpgradeRecipes(precipitator);
		TECraftingHandler.addMachineUpgradeRecipes(extruder);
		TECraftingHandler.addMachineUpgradeRecipes(accumulator);
		TECraftingHandler.addMachineUpgradeRecipes(assembler);
		TECraftingHandler.addMachineUpgradeRecipes(charger);
		TECraftingHandler.addMachineUpgradeRecipes(insolator);

		TECraftingHandler.addSecureRecipe(furnace);
		TECraftingHandler.addSecureRecipe(pulverizer);
		TECraftingHandler.addSecureRecipe(sawmill);
		TECraftingHandler.addSecureRecipe(smelter);
		TECraftingHandler.addSecureRecipe(crucible);
		TECraftingHandler.addSecureRecipe(transposer);
		TECraftingHandler.addSecureRecipe(precipitator);
		TECraftingHandler.addSecureRecipe(extruder);
		TECraftingHandler.addSecureRecipe(accumulator);
		TECraftingHandler.addSecureRecipe(assembler);
		TECraftingHandler.addSecureRecipe(charger);
		TECraftingHandler.addSecureRecipe(insolator);

		return true;
	}

	public static void refreshItemStacks() {

		furnace = ItemBlockMachine.setDefaultTag(furnace);
		pulverizer = ItemBlockMachine.setDefaultTag(pulverizer);
		sawmill = ItemBlockMachine.setDefaultTag(sawmill);
		smelter = ItemBlockMachine.setDefaultTag(smelter);
		crucible = ItemBlockMachine.setDefaultTag(crucible);
		transposer = ItemBlockMachine.setDefaultTag(transposer);
		precipitator = ItemBlockMachine.setDefaultTag(precipitator);
		extruder = ItemBlockMachine.setDefaultTag(extruder);
		accumulator = ItemBlockMachine.setDefaultTag(accumulator);
		assembler = ItemBlockMachine.setDefaultTag(assembler);
		charger = ItemBlockMachine.setDefaultTag(charger);
		insolator = ItemBlockMachine.setDefaultTag(insolator);
	}

    public enum Types implements IStringSerializable, IType, IParticleProvider {
		FURNACE,
        PULVERIZER,
        SAWMILL,
        SMELTER,
        CRUCIBLE,
        TRANSPOSER,
        PRECIPITATOR,
        EXTRUDER,
        ACCUMULATOR,
        ASSEMBLER,
        CHARGER,
        INSOLATOR;

        @Override
        public String getName() {
            return name().toLowerCase(Locale.US);
        }


        public static Types fromMeta(int meta) {
            try {
                return values()[meta];
            } catch (IndexOutOfBoundsException e){
                throw new RuntimeException("Someone has requested an invalid metadata for a block inside ThermalExpansion.", e);
            }
        }

        @Override
        public int meta() {
            return ordinal();
        }

        @Override
        public IProperty<?> getTypeProperty() {
            return TYPES;
        }

        @Override
        public String getParticleTexture() {
            return "thermalexpansion:blocks/machine/machine_side";
        }

        public static int meta(Types type) {
            return type.ordinal();
        }

	}

	public static final String[] NAMES = { "furnace", "pulverizer", "sawmill", "smelter", "crucible", "transposer", "precipitator", "extruder", "accumulator",
			"assembler", "charger", "insolator" };
	public static boolean[] enable = new boolean[Types.values().length];
	public static boolean[] creativeTiers = new boolean[4];
	public static ItemStack[] defaultAugments = new ItemStack[3];

	public static boolean defaultAutoTransfer = true;
	public static boolean defaultRedstoneControl = true;
	public static boolean defaultReconfigSides = true;

	static {
		String category = "Machine.";

		for (int i = 0; i < Types.values().length; i++) {
			enable[i] = ThermalExpansion.config.get(category + StringHelper.titleCase(NAMES[i]), "Recipe.Enable", true);
		}
		category = "Machine.All";

		creativeTiers[0] = ThermalExpansion.config.get(category, "CreativeTab.Basic", false);
		creativeTiers[1] = ThermalExpansion.config.get(category, "CreativeTab.Hardened", false);
		creativeTiers[2] = ThermalExpansion.config.get(category, "CreativeTab.Reinforced", false);
		creativeTiers[3] = ThermalExpansion.config.get(category, "CreativeTab.Resonant", true);

		category += ".Augments";

		defaultAutoTransfer = ThermalExpansion.config.get(category, "Default.AutoTransfer", true);
		defaultRedstoneControl = ThermalExpansion.config.get(category, "Default.RedstoneControl", true);
		defaultReconfigSides = ThermalExpansion.config.get(category, "Default.ReconfigurableSides", true);
	}

	public static ItemStack furnace;
	public static ItemStack pulverizer;
	public static ItemStack sawmill;
	public static ItemStack smelter;
	public static ItemStack crucible;
	public static ItemStack transposer;
	public static ItemStack precipitator;
	public static ItemStack extruder;
	public static ItemStack accumulator;
	public static ItemStack assembler;
	public static ItemStack charger;
	public static ItemStack insolator;

}
