package cofh.thermalexpansion.block.cache;

import static cofh.lib.util.helpers.ItemHelper.ShapedRecipe;

import codechicken.lib.block.IParticleProvider;
import codechicken.lib.block.IType;
import codechicken.lib.model.blockbakery.BlockBakeryProperties;
import codechicken.lib.raytracer.RayTracer;
import codechicken.lib.texture.IWorldBlockTextureProvider;
import codechicken.lib.texture.TextureUtils;
import codechicken.lib.util.ItemUtils;
import codechicken.lib.util.SoundUtils;
import codechicken.lib.vec.Vector3;
import cofh.core.render.IconRegistry;
import cofh.core.util.CoreUtils;
import cofh.core.util.crafting.RecipeUpgrade;
import cofh.lib.util.helpers.ItemHelper;
import cofh.lib.util.helpers.ServerHelper;
import cofh.lib.util.helpers.StringHelper;
import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalexpansion.block.BlockTEBase;
import codechicken.lib.model.blockbakery.BlockBakery;
import cofh.thermalexpansion.block.TileInventory;
import cofh.thermalexpansion.util.Utils;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.fml.common.registry.GameRegistry;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

public class BlockCache extends BlockTEBase implements IWorldBlockTextureProvider {

    public static final PropertyEnum<Types> TYPES = PropertyEnum.create("type", Types.class);

	public BlockCache() {
		super(Material.IRON);
		setHardness(15.0F);
		setResistance(25.0F);
		setUnlocalizedName("thermalexpansion.cache");
		setHarvestLevel("pickaxe", 1);
        setDefaultState(getDefaultState().withProperty(TYPES, Types.BASIC));
	}

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(TYPES).ordinal();
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
        return new ExtendedBlockState(this, new IProperty[]{TYPES}, new IUnlistedProperty[]{ BlockBakeryProperties.LAYER_FACE_SPRITE_MAP });
    }

    @Override
	public TileEntity createNewTileEntity(World world, int metadata) {
		if (metadata >= Types.values().length) {
			return null;
		}
        Types type = Types.fromMeta(metadata);
		if (type == Types.CREATIVE && !enable[Types.CREATIVE.meta()]) {
			return null;
		}
		return new TileCache(metadata);
	}

	@Override
	public void getSubBlocks(Item item, CreativeTabs tab, List<ItemStack> list) {

		if (enable[0]) {
			list.add(new ItemStack(item, 1, 0));
		}
		for (int i = 1; i < Types.values().length; i++) {
			list.add(new ItemStack(item, 1, i));
		}
	}

	@Override
	public boolean hasComparatorInputOverride(IBlockState state) {
		return true;
	}

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase living, ItemStack stack) {
        Types type = state.getValue(TYPES);
        if (type == Types.CREATIVE && !enable[Types.CREATIVE.meta()]) {
			world.setBlockToAir(pos);
			return;
		}
		if (stack.getTagCompound() != null) {
			TileCache tile = (TileCache) world.getTileEntity(pos);
			tile.locked = stack.getTagCompound().getBoolean("Lock");

			if (stack.getTagCompound().hasKey("Item")) {
				ItemStack stored = ItemHelper.readItemStackFromNBT(stack.getTagCompound().getCompoundTag("Item"));
				tile.setStoredItemType(stored, stored.stackSize);
			}
		}
		super.onBlockPlacedBy(world, pos, state, living, stack);
	}

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, @Nullable ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
		if (super.onBlockActivated(world, pos, state, player, hand, heldItem, side, hitX, hitY, hitZ)) {
			if (Utils.isHoldingUsableWrench(player, RayTracer.retrace(player))) {
				return true;
			}
		}
		TileCache tile = (TileCache) world.getTileEntity(pos);
		boolean playSound = false;

		if (ItemHelper.isPlayerHoldingNothing(player)) {
			if (player.isSneaking()) {
				tile.toggleLock();

				if (tile.locked) {
                    SoundUtils.playSoundAt(Vector3.fromTileCenter(tile), world, SoundCategory.BLOCKS, SoundEvents.UI_BUTTON_CLICK, 0.2F, 0.8F);
				} else {
                    SoundUtils.playSoundAt(Vector3.fromTileCenter(tile), world, SoundCategory.BLOCKS, SoundEvents.UI_BUTTON_CLICK, 0.3F, 0.5F);
				}
				return true;
			}
			if (tile.getStoredItemType() != null) {
				insertAllItemsFromPlayer(tile, player);
			}
			return true;
		}
		ItemStack heldStack = ItemUtils.getHeldStack(player);
        ItemStack ret = tile.insertItem(null, heldStack, false);
		long time = player.getEntityData().getLong("TE:lastCacheClick"), currentTime = world.getTotalWorldTime();
		player.getEntityData().setLong("TE:lastCacheClick", currentTime);

		if (!player.capabilities.isCreativeMode) {
			if (ret != heldStack) {
				player.inventory.setInventorySlotContents(player.inventory.currentItem, ret);
				playSound = true;
			}
			if (tile.getStoredItemType() != null && currentTime - time < 15) {
				playSound &= !insertAllItemsFromPlayer(tile, player);
			}
		}
		if (playSound) {
            SoundUtils.playSoundAt(Vector3.fromTileCenter(tile), tile.getWorld(), SoundCategory.BLOCKS, SoundEvents.ENTITY_EXPERIENCE_ORB_TOUCH, 0.1F, 0.7F);
		}
		return true;
	}

	private static boolean insertAllItemsFromPlayer(TileCache tile, EntityPlayer player) {

		boolean playSound = false;
		for (int i = 0; i < player.inventory.getSizeInventory(); i++) {
			if (tile.insertItem(null, player.inventory.getStackInSlot(i), true) != player.inventory.getStackInSlot(i)) {
				player.inventory.setInventorySlotContents(i, tile.insertItem(null, player.inventory.getStackInSlot(i), false));
				playSound = true;
			}
		}
		if (playSound) {
            SoundUtils.playSoundAt(Vector3.fromTileCenter(tile), tile.getWorld(), SoundCategory.BLOCKS, SoundEvents.ENTITY_EXPERIENCE_ORB_TOUCH, 0.1F, 0.7F);
		}
		return playSound;
	}

    @Override
    public void onBlockClicked(World world, BlockPos pos, EntityPlayer player) {
		if (ServerHelper.isClientWorld(world)) {
			return;
		}
		boolean playSound = false;
		TileCache tile = (TileCache) world.getTileEntity(pos);

		int extractAmount = !player.isSneaking() ? 1 : 64;
		ItemStack extract = tile.extractItem(null, extractAmount, true);
		if (extract == null) {
			return;
		}

		if (!player.capabilities.isCreativeMode) {
			if (!player.inventory.addItemStackToInventory(extract)) {
				// apparently this returns false if it succeeds but doesn't have room for all.
				// apparently designed for inserts of single items but supports >1 inserts because notch
				if (extract.stackSize == extractAmount) {
					return;
				}
				extractAmount -= extract.stackSize;
			}
			playSound = true;
			tile.extractItem(null, extractAmount, false);
		} else {
			tile.extractItem(null, extractAmount, false);
		}
		if (playSound) {
            SoundUtils.playSoundAt(new Vector3(pos).add(0.5), world, SoundCategory.BLOCKS, SoundEvents.ENTITY_ITEM_PICKUP, 0.4F, 0.8F);
		}
	}

    @Override
    public boolean removedByPlayer(IBlockState state, World world, BlockPos pos, EntityPlayer player, boolean willHarvest) {
		if (player.capabilities.isCreativeMode && !player.isSneaking()) {
			onBlockClicked(world, pos, player);
			return false;
		} else {
		    onBlockHarvested(world, pos, world.getBlockState(pos), player);
			return world.setBlockToAir(pos);
		}
	}

    @Override
    public float getPlayerRelativeBlockHardness(IBlockState state, EntityPlayer player, World world, BlockPos pos) {
		ItemStack stack = player.getHeldItemMainhand();
		if (stack == null || !ForgeHooks.isToolEffective(world, pos, stack)) {
			return -1;
		}
		return super.getPlayerRelativeBlockHardness(state, player, world, pos);
	}

	@Override
	public boolean isOpaqueCube(IBlockState state) {

		return true;
	}

	@Override
    @SideOnly(Side.CLIENT)
	public TextureAtlasSprite getTexture(EnumFacing side, int metadata) {
		if (side.ordinal() == 0) {
			return IconRegistry.getIcon("CacheBottom", metadata);
		}
		if (side.ordinal() == 1) {
			return IconRegistry.getIcon("CacheTop", metadata);
		}
		return side.ordinal() != 2 ? IconRegistry.getIcon("CacheSide", metadata) : IconRegistry.getIcon("CacheFace", metadata);
	}

    @Override
    @SideOnly(Side.CLIENT)
    public TextureAtlasSprite getTexture(EnumFacing side, IBlockState state, BlockRenderLayer layer, IBlockAccess world, BlockPos pos) {
        TileEntity tileEntity = world.getTileEntity(pos);
        if (tileEntity instanceof TileCache) {
            TileCache cache = ((TileCache) tileEntity);
            if (layer == BlockRenderLayer.CUTOUT && cache.storedStack != null) {
                cache.getTexture(side.ordinal(), 1);//Cull the meter if there is no stack.
            } else {
                return cache.getTexture(side.ordinal(), 0);
            }
        }
        return TextureUtils.getMissingSprite();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(TextureMap textureMap) {
        for (int i = 0; i < 9; i++) {
            IconRegistry.addIcon("CacheMeter" + i, "thermalexpansion:blocks/cache/cache_meter_" + i, textureMap);
        }
        for (int i = 0; i < Types.values().length; i++) {
            IconRegistry.addIcon("CacheBottom" + i, "thermalexpansion:blocks/cache/cache_" + NAMES[i] + "_bottom", textureMap);
            IconRegistry.addIcon("CacheTop" + i, "thermalexpansion:blocks/cache/cache_" + NAMES[i] + "_top", textureMap);
            IconRegistry.addIcon("CacheSide" + i, "thermalexpansion:blocks/cache/cache_" + NAMES[i] + "_side", textureMap);
            IconRegistry.addIcon("CacheFace" + i, "thermalexpansion:blocks/cache/cache_" + NAMES[i] + "_face", textureMap);
        }
        IconRegistry.addIcon("CacheBlank", "thermalexpansion:blocks/config/config_none", textureMap);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean canRenderInLayer(IBlockState state, BlockRenderLayer layer) {
        return layer == BlockRenderLayer.SOLID || layer == BlockRenderLayer.CUTOUT;
    }

    @Override
	public NBTTagCompound getItemStackTag(IBlockAccess world, BlockPos pos) {

		NBTTagCompound tag = super.getItemStackTag(world, pos);
		TileCache tile = (TileCache) world.getTileEntity(pos);

		if (tile != null && tile.storedStack != null) {
			tag = new NBTTagCompound();
			tag.setBoolean("Lock", tile.locked);
			tag.setTag("Item", ItemHelper.writeItemStackToNBT(tile.storedStack, tile.getStoredCount(), new NBTTagCompound()));
		}
		return tag;
	}

	/* IDismantleable */
    @Override
    public ArrayList<ItemStack> dismantleBlock(World world, BlockPos pos, IBlockState state, EntityPlayer player, boolean returnDrops) {

        NBTTagCompound tag = getItemStackTag(world, pos);

        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof TileInventory) {
            ((TileInventory) tile).inventory = new ItemStack[((TileInventory) tile).inventory.length];
        }
        return super.dismantleDelegate(tag, world, pos, player, returnDrops, false);
    }

    /* IDismantleable */
    @Override
    public boolean canDismantle(World world, BlockPos pos, IBlockState state, EntityPlayer player) {

        if (state.getBlock().getMetaFromState(state) == Types.CREATIVE.ordinal() && !CoreUtils.isOp(player)) {
            return false;
        }
        return super.canDismantle(world, pos, state, player);
    }

	/* IInitializer */
	@Override
	public boolean initialize() {

		TileCache.initialize();

		cacheCreative = new ItemStack(this, 1, Types.CREATIVE.ordinal());
		cacheBasic = new ItemStack(this, 1, Types.BASIC.ordinal());
		cacheHardened = new ItemStack(this, 1, Types.HARDENED.ordinal());
		cacheReinforced = new ItemStack(this, 1, Types.REINFORCED.ordinal());
		cacheResonant = new ItemStack(this, 1, Types.RESONANT.ordinal());

		return true;
	}

	@Override
	public boolean postInit() {

		if (enable[Types.BASIC.ordinal()]) {
			GameRegistry.addRecipe(ShapedRecipe(cacheBasic, " I ", "IXI", " I ", 'I', "ingotTin", 'X', "logWood"));
		}
		if (enable[Types.HARDENED.ordinal()]) {
			GameRegistry.addRecipe(new RecipeUpgrade(cacheHardened, new Object[] { " I ", "IXI", " I ", 'I', "ingotInvar", 'X', cacheBasic }));
			GameRegistry.addRecipe(ShapedRecipe(cacheHardened, "IYI", "YXY", "IYI", 'I', "ingotInvar", 'X', "logWood", 'Y', "ingotTin"));
		}
		if (enable[Types.REINFORCED.ordinal()]) {
			GameRegistry.addRecipe(new RecipeUpgrade(cacheReinforced, new Object[] { " G ", "GXG", " G ", 'X', cacheHardened, 'G', "blockGlassHardened" }));
		}
		if (enable[Types.RESONANT.ordinal()]) {
			GameRegistry.addRecipe(new RecipeUpgrade(cacheResonant, new Object[] { " I ", "IXI", " I ", 'I', "ingotEnderium", 'X', cacheReinforced }));
		}
		return true;
	}

    public enum Types implements IStringSerializable, IType, IParticleProvider {
        CREATIVE,
        BASIC,
        HARDENED,
        REINFORCED,
        RESONANT;

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
            return "thermalexpansion:blocks/cache/cache_" + getName() + "_side";
        }

        public static int meta(Types type) {
            return type.ordinal();
        }
    }

    public static final String[] NAMES = { "creative", "basic", "hardened", "reinforced", "resonant" };
	public static boolean[] enable = new boolean[Types.values().length];

	static {
		String category = "Cache.";

		enable[0] = ThermalExpansion.config.get(category + StringHelper.titleCase(NAMES[0]), "Enable", true);
		for (int i = 1; i < Types.values().length; i++) {
			enable[i] = ThermalExpansion.config.get(category + StringHelper.titleCase(NAMES[i]), "Recipe.Enable", true);
		}
	}

	public static ItemStack cacheCreative;
	public static ItemStack cacheBasic;
	public static ItemStack cacheHardened;
	public static ItemStack cacheReinforced;
	public static ItemStack cacheResonant;
}
