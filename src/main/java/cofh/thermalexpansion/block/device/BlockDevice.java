package cofh.thermalexpansion.block.device;

import codechicken.lib.item.ItemStackRegistry;

import cofh.core.render.IconRegistry;
import cofh.core.util.crafting.RecipeAugmentable;
import cofh.lib.util.helpers.BlockHelper;
import cofh.lib.util.helpers.ItemHelper;
import cofh.lib.util.helpers.StringHelper;
import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalexpansion.block.BlockTEBase;
import cofh.thermalexpansion.block.CommonProperties;
import cofh.thermalexpansion.block.TileAugmentable;
import cofh.thermalexpansion.client.bakery.BlockBakery;
import cofh.thermalexpansion.client.IBlockLayeredTextureProvider;
import cofh.thermalexpansion.item.TEAugments;
import cofh.thermalexpansion.item.TEEquipment;
import cofh.thermalexpansion.item.TEItems;
import cofh.thermalexpansion.util.crafting.TECraftingHandler;
import cofh.thermalexpansion.util.helpers.ReconfigurableHelper;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.fml.common.registry.GameRegistry;



import java.util.ArrayList;
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
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockDevice extends BlockTEBase implements IBlockLayeredTextureProvider {

    public static final PropertyEnum<Types> TYPES = PropertyEnum.create("type", Types.class);

	public BlockDevice() {

		super(Material.IRON);
		setHardness(15.0F);
		setResistance(25.0F);
		setUnlocalizedName("thermalexpansion.device");
        setDefaultState(getDefaultState().withProperty(TYPES, Types.ACTIVATOR));
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
    @SideOnly(Side.CLIENT)
    public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos) {
        TileEntity tileEntity = world.getTileEntity(pos);
        return BlockBakery.handleExtendedState((IExtendedBlockState) state, tileEntity);
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new ExtendedBlockState.Builder(this).add(TYPES).add(CommonProperties.SPRITE_FACE_LAYER_PROPERTY).build();
    }

    @Override
    public TileEntity createNewTileEntity(World world, int metadata) {

		if (metadata >= Types.values().length) {
            return null;
		}
		switch (Types.values()[metadata]) {
		case WORKBENCH_FALSE:
			return new TileWorkbenchFalse();
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
		case EXTENDER:
			return new TileExtender();
		default:
			return null;
		}
	}

	@Override
	public void getSubBlocks(Item item, CreativeTabs tab, List<ItemStack> list) {

		for (int i = 0; i < Types.values().length; i++) {
			if (enable[i]) {
				list.add(ItemBlockDevice.setDefaultTag(new ItemStack(item, 1, i)));
			}
		}
	}

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase living, ItemStack stack) {
		if (stack.getTagCompound() != null) {
			TileEntity aTile = world.getTileEntity(pos);

			if (aTile instanceof TileAugmentable) {
				TileAugmentable tile = ((TileAugmentable) aTile);

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
    public TextureAtlasSprite getTexture(EnumFacing side, int metadata) {

        if (metadata == Types.WORKBENCH_FALSE.ordinal()) {
            if (side.ordinal() == 0) {
                return IconRegistry.getIcon("WorkbenchBottom", 1);
            } else if (side.ordinal() == 1) {
                return IconRegistry.getIcon("WorkbenchTop", 1);
            }
            return IconRegistry.getIcon("WorkbenchSide", 1);
        }
        return side.ordinal() != 3 ? IconRegistry.getIcon("DeviceSide"): IconRegistry.getIcon("DeviceFace", metadata % Types.values().length);
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
        IconRegistry.addIcon("DeviceSide", "thermalexpansion:blocks/device/device_side", textureMap);

        // Face Textures
        for (int i = 0; i < Types.values().length; i++) {
            if (i == Types.WORKBENCH_FALSE.ordinal() || i == Types.PUMP.ordinal() || i == Types.EXTENDER.ordinal()) {
                continue;
            }
            IconRegistry.addIcon("DeviceFace" + i, "thermalexpansion:blocks/device/device_face_" + NAMES[i], textureMap);
            IconRegistry.addIcon("DeviceActive" + i, "thermalexpansion:blocks/device/device_active_" + NAMES[i], textureMap);
        }
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

	/* IDismantleable */
	@Override
	public ArrayList<ItemStack> dismantleBlock(EntityPlayer player, World world, BlockPos pos, boolean returnDrops) {

		NBTTagCompound tag = getItemStackTag(world, pos);

		TileEntity tile = world.getTileEntity(pos);
		if (tile instanceof TileAugmentable) {
			if (tag == null) {
				tag = new NBTTagCompound();
			}
			TileAugmentable theTile = (TileAugmentable) tile;

			ReconfigurableHelper.setItemStackTagReconfig(tag, theTile);
			tag.setInteger("Energy", theTile.getEnergyStored(null));
			theTile.writeAugmentsToNBT(tag);
		}
		return super.dismantleBlock(player, tag, world, pos, returnDrops, false);
	}

	/* IInitializer */
	@Override
	public boolean initialize() {

		TileDeviceBase.configure();

		TileWorkbenchFalse.initialize();
		TileActivator.initialize();
		TileBreaker.initialize();
		TileCollector.initialize();
		TileNullifier.initialize();
		TileBuffer.initialize();
		TileExtender.initialize();

		if (defaultRedstoneControl) {
			defaultAugments[0] = ItemHelper.cloneStack(TEAugments.generalRedstoneControl);
		}
		if (defaultReconfigSides) {
			defaultAugments[1] = ItemHelper.cloneStack(TEAugments.generalReconfigSides);
		}
		activator = ItemBlockDevice.setDefaultTag(new ItemStack(this, 1, Types.ACTIVATOR.ordinal()));
		breaker = ItemBlockDevice.setDefaultTag(new ItemStack(this, 1, Types.BREAKER.ordinal()));
		collector = ItemBlockDevice.setDefaultTag(new ItemStack(this, 1, Types.COLLECTOR.ordinal()));
		nullifier = ItemBlockDevice.setDefaultTag(new ItemStack(this, 1, Types.NULLIFIER.ordinal()));
		buffer = ItemBlockDevice.setDefaultTag(new ItemStack(this, 1, Types.BUFFER.ordinal()));
		// extender = ItemBlockDevice.setDefaultTag(new ItemStack(this, 1, Types.EXTENDER.ordinal()));

        ItemStackRegistry.registerCustomItemStack("activator", activator);
		ItemStackRegistry.registerCustomItemStack("breaker", breaker);
        ItemStackRegistry.registerCustomItemStack("collector", collector);
        ItemStackRegistry.registerCustomItemStack("nullifier", nullifier);
        ItemStackRegistry.registerCustomItemStack("buffer", buffer);
		// GameRegistry.registerCustomItemStack("extender", extender);

		return true;
	}

	@Override
	public boolean postInit() {

		String category = "Device.Breaker";
		String comment = "If enabled, The Block Breaker will require a Diamond Pickaxe instead of an Invar Pickaxe.";
		boolean breakerDiamondPickaxe = ThermalExpansion.config.get(category, "Recipe.RequireDiamondPickaxe", false, comment);
		ItemStack pickaxe = breakerDiamondPickaxe ? new ItemStack(Items.DIAMOND_PICKAXE) : TEEquipment.toolInvarPickaxe;

		String tinPart = "thermalexpansion:machineTin";

		// @formatter:off
		if (enable[Types.ACTIVATOR.ordinal()]) {
			GameRegistry.addRecipe(new RecipeAugmentable(activator, defaultAugments, new Object[] {
					" X ",
					"ICI",
					" P ",
					'C', Blocks.PISTON,
					'I', tinPart,
					'P', TEItems.powerCoilGold,
					'X', Blocks.CHEST
			}));
		}
		if (enable[Types.BREAKER.ordinal()]) {
			GameRegistry.addRecipe(new RecipeAugmentable(breaker, defaultAugments, new Object[] {
					" X ",
					"ICI",
					" P ",
					'C', Blocks.PISTON,
					'I', tinPart,
					'P', TEItems.pneumaticServo,
					'X', pickaxe
			}));
		}
		if (enable[Types.COLLECTOR.ordinal()]) {
			GameRegistry.addRecipe(new RecipeAugmentable(collector, defaultAugments, new Object[] {
					" X ",
					"ICI",
					" P ",
					'C', Blocks.PISTON,
					'I', tinPart,
					'P', TEItems.pneumaticServo,
					'X', Blocks.HOPPER
			}));
		}
		if (enable[Types.NULLIFIER.ordinal()]) {
			GameRegistry.addRecipe(new RecipeAugmentable(nullifier, defaultAugments, new Object[] {
					" X ",
					"ICI",
					" P ",
					'C', Items.LAVA_BUCKET,
					'I', tinPart,
					'P', TEItems.pneumaticServo,
					'X', "ingotInvar"
			}));
		}
		if (enable[Types.BUFFER.ordinal()]) {
			GameRegistry.addRecipe(new RecipeAugmentable(buffer, defaultAugments, new Object[] {
					" X ",
					"ICI",
					" P ",
					'C', Blocks.HOPPER,
					'I', tinPart,
					'P', TEItems.pneumaticServo,
					'X', "gearCopper"
			}));
		}
		if (enable[Types.EXTENDER.ordinal()]) {
//			GameRegistry.addRecipe(new RecipeAugmentable(extender, defaultAugments, new Object[] {
//					" X ",
//					"ICI",
//					" P ",
//					'C', Blocks.hopper,
//					'I', tinPart,
//					'P', TEItems.pneumaticServo,
//					'X', "gearCopper"
//			}));
		}
		// @formatter:on

		TECraftingHandler.addSecureRecipe(activator);
		TECraftingHandler.addSecureRecipe(breaker);
		TECraftingHandler.addSecureRecipe(collector);
		TECraftingHandler.addSecureRecipe(nullifier);
		TECraftingHandler.addSecureRecipe(buffer);
		// TECraftingHandler.addSecureRecipe(extender);

		return true;
	}

	public static void refreshItemStacks() {

		activator = ItemBlockDevice.setDefaultTag(activator);
		breaker = ItemBlockDevice.setDefaultTag(breaker);
		collector = ItemBlockDevice.setDefaultTag(collector);
		nullifier = ItemBlockDevice.setDefaultTag(nullifier);
		buffer = ItemBlockDevice.setDefaultTag(buffer);
		// extender = ItemBlockDevice.setDefaultTag(extender);
	}

    public enum Types implements IStringSerializable {
		WORKBENCH_FALSE,
        PUMP,
        ACTIVATOR,
        BREAKER,
        COLLECTOR,
        NULLIFIER,
        BUFFER,
        EXTENDER;

        @Override
        public String getName() {
            return name().toLowerCase(Locale.US);
        }

        public int meta() {
            return ordinal();
        }

        public static Types fromMeta(int meta) {
            try {
                return values()[meta];
            } catch (IndexOutOfBoundsException e){
                throw new RuntimeException("Someone has requested an invalid metadata for a block inside ThermalExpansion.", e);
            }
        }

        public static int meta(Types type) {
            return type.ordinal();
        }
	}

	public static final String[] NAMES = { "workbench", "pump", "activator", "breaker", "collector", "nullifier", "buffer", "extender" };
	public static boolean[] enable = new boolean[Types.values().length];
	public static ItemStack[] defaultAugments = new ItemStack[4];

	public static boolean defaultRedstoneControl = true;
	public static boolean defaultReconfigSides = true;

	static {
		String category = "Device.";

		for (int i = 0; i < Types.values().length; i++) {
			enable[i] = ThermalExpansion.config.get(category + StringHelper.titleCase(NAMES[i]), "Recipe.Enable", true);
		}
		enable[Types.WORKBENCH_FALSE.ordinal()] = false;
		enable[Types.PUMP.ordinal()] = false;
		enable[Types.EXTENDER.ordinal()] = false;
		ThermalExpansion.config.removeCategory(category + StringHelper.titleCase(NAMES[Types.WORKBENCH_FALSE.ordinal()]));
		ThermalExpansion.config.removeCategory(category + StringHelper.titleCase(NAMES[Types.PUMP.ordinal()]));
		ThermalExpansion.config.removeCategory(category + StringHelper.titleCase(NAMES[Types.EXTENDER.ordinal()]));
	}

	public static ItemStack pump;
	public static ItemStack activator;
	public static ItemStack breaker;
	public static ItemStack collector;
	public static ItemStack nullifier;
	public static ItemStack buffer;
	public static ItemStack extender;

}
