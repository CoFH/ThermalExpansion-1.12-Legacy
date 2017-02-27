package cofh.thermalexpansion.block.storage;

import codechicken.lib.model.ModelRegistryHelper;
import codechicken.lib.model.blockbakery.BlockBakery;
import codechicken.lib.model.blockbakery.BlockBakeryProperties;
import codechicken.lib.model.blockbakery.CCBakeryModel;
import codechicken.lib.texture.IWorldBlockTextureProvider;
import codechicken.lib.texture.TextureUtils;
import cofh.api.core.IModelRegister;
import cofh.lib.util.RayTracer;
import cofh.lib.util.helpers.ItemHelper;
import cofh.lib.util.helpers.ServerHelper;
import cofh.thermalexpansion.block.BlockTEBase;
import cofh.thermalexpansion.init.TEProps;
import cofh.thermalexpansion.init.TETextures;
import cofh.thermalexpansion.util.Utils;
import cofh.thermalfoundation.item.ItemMaterial;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMap;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

import static cofh.lib.util.helpers.ItemHelper.ShapedRecipe;
import static cofh.lib.util.helpers.ItemHelper.addRecipe;

public class BlockCache extends BlockTEBase implements IModelRegister, IWorldBlockTextureProvider {

	public BlockCache() {

		super(Material.IRON);

		setUnlocalizedName("cache");

		setHardness(15.0F);
		setResistance(25.0F);

		setHarvestLevel("pickaxe", 1);
	}

	@Override
	protected BlockStateContainer createBlockState() {

		BlockStateContainer.Builder builder = new BlockStateContainer.Builder(this);
		// UnListed
		builder.add(TEProps.CREATIVE);
		builder.add(BlockBakeryProperties.LAYER_FACE_SPRITE_MAP);
		builder.add(TEProps.LEVEL);
		builder.add(TEProps.SCALE);
		builder.add(TEProps.FACING);

		return builder.build();
	}

	@Override
	@SideOnly (Side.CLIENT)
	public void getSubBlocks(@Nonnull Item item, CreativeTabs tab, List<ItemStack> list) {

		if (enable) {
			for (int i = 0; i < 5; i++) {
				list.add(itemBlock.setDefaultTag(new ItemStack(item, 1, 0), i));
			}
		}
	}

	/* ITileEntityProvider */
	@Override
	public TileEntity createNewTileEntity(World world, int metadata) {

		return new TileCache();
	}

	/* BLOCK METHODS */
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
				// apparently designed for inserts of single items but supports > 1 inserts because notch
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
			world.playSound(null, pos, SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.BLOCKS, 0.4F, 0.8F);
		}
	}

	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase living, ItemStack stack) {

		if (stack.getTagCompound() != null) {
			TileCache tile = (TileCache) world.getTileEntity(pos);

			tile.isCreative = (stack.getTagCompound().getBoolean("Creative"));
			tile.setLevel(stack.getTagCompound().getByte("Level"));
			tile.locked = stack.getTagCompound().getBoolean("Lock");

			if (stack.getTagCompound().hasKey("Item")) {
				ItemStack stored = ItemHelper.readItemStackFromNBT(stack.getTagCompound().getCompoundTag("Item"));
				tile.setStoredItemType(stored, stored.stackSize);
			}
		}
		super.onBlockPlacedBy(world, pos, state, living, stack);
	}

	@Override
	public boolean hasComparatorInputOverride(IBlockState state) {

		return true;
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
					world.playSound(null, pos, SoundEvents.UI_BUTTON_CLICK, SoundCategory.BLOCKS, 0.2F, 0.8F);
				} else {
					world.playSound(null, pos, SoundEvents.UI_BUTTON_CLICK, SoundCategory.BLOCKS, 0.3F, 0.5F);
				}
				return true;
			}
			if (tile.getStoredItemType() != null) {
				insertAllItemsFromPlayer(tile, player);
			}
			return true;
		}
		ItemStack heldStack = ItemHelper.getMainhandStack(player);
		ItemStack ret = tile.insertItem(null, heldStack, false);
		long time = player.getEntityData().getLong("thermalexpansion:CacheClick"), currentTime = world.getTotalWorldTime();
		player.getEntityData().setLong("thermalexpansion:CacheClick", currentTime);

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
			world.playSound(null, pos, SoundEvents.ENTITY_EXPERIENCE_ORB_TOUCH, SoundCategory.BLOCKS, 0.1F, 0.7F);
		}
		return true;
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

	private static boolean insertAllItemsFromPlayer(TileCache tile, EntityPlayer player) {

		boolean playSound = false;
		for (int i = 0; i < player.inventory.getSizeInventory(); i++) {
			if (tile.insertItem(null, player.inventory.getStackInSlot(i), true) != player.inventory.getStackInSlot(i)) {
				player.inventory.setInventorySlotContents(i, tile.insertItem(null, player.inventory.getStackInSlot(i), false));
				playSound = true;
			}
		}
		if (playSound) {
			tile.getWorld().playSound(null, tile.getPos(), SoundEvents.ENTITY_EXPERIENCE_ORB_TOUCH, SoundCategory.BLOCKS, 0.1F, 0.7F);
		}
		return playSound;
	}

	@Override
	public float getPlayerRelativeBlockHardness(IBlockState state, EntityPlayer player, World world, BlockPos pos) {

		ItemStack stack = player.getHeldItemMainhand();
		if (stack == null || !ForgeHooks.isToolEffective(world, pos, stack)) {
			return -1;
		}
		return super.getPlayerRelativeBlockHardness(state, player, world, pos);
	}

	/* HELPERS */
	@Override
	public NBTTagCompound getItemStackTag(IBlockAccess world, BlockPos pos) {

		NBTTagCompound retTag = super.getItemStackTag(world, pos);
		TileCache tile = (TileCache) world.getTileEntity(pos);

		if (tile != null && tile.storedStack != null) {
			retTag.setBoolean("Lock", tile.locked);
			retTag.setTag("Item", ItemHelper.writeItemStackToNBT(tile.storedStack, tile.getStoredCount(), new NBTTagCompound()));
		}
		return retTag;
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

	@Override // Inventory
	@SideOnly (Side.CLIENT)
	public TextureAtlasSprite getTexture(EnumFacing side, ItemStack stack) {

		boolean isCreative = itemBlock.isCreative(stack);
		int level = itemBlock.getLevel(stack);

		if (side == EnumFacing.DOWN) {
			return isCreative ? TETextures.CACHE_BOTTOM_C :TETextures.CACHE_BOTTOM[level];
		}
		if (side == EnumFacing.UP) {
			return isCreative ? TETextures.CACHE_TOP_C :TETextures.CACHE_TOP[level];
		}
		return side != EnumFacing.NORTH ? isCreative ? TETextures.CACHE_SIDE_C : TETextures.CACHE_SIDE[level] : isCreative ? TETextures.CACHE_FACE_C : TETextures.CACHE_FACE[level];
	}

	@Override // World
	@SideOnly (Side.CLIENT)
	public TextureAtlasSprite getTexture(EnumFacing side, IBlockState state, BlockRenderLayer layer, IBlockAccess world, BlockPos pos) {

		TileEntity tileEntity = world.getTileEntity(pos);
		if (tileEntity instanceof TileCache) {
			TileCache tile = ((TileCache) tileEntity);
			return tile.getTexture(side.ordinal(), layer == BlockRenderLayer.SOLID ? 0 : 1);
		}
		return TextureUtils.getMissingSprite();
	}

	/* IModelRegister */
	@Override
	@SideOnly (Side.CLIENT)
	public void registerModels() {

		StateMap.Builder stateMap = new StateMap.Builder();
		ModelLoader.setCustomStateMapper(this, stateMap.build());

		ModelResourceLocation location = new ModelResourceLocation(getRegistryName(), "normal");
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 0, location);
		ModelRegistryHelper.register(location, new CCBakeryModel("thermalexpansion:blocks/storage/cache_top_0"));

		BlockBakery.registerItemKeyGenerator(itemBlock, stack -> BlockBakery.defaultItemKeyGenerator.generateKey(stack) + ",creative=" + itemBlock.isCreative(stack) + ",level=" + itemBlock.getLevel(stack));
	}

	/* IInitializer */
	@Override
	public boolean preInit() {

		this.setRegistryName("cache");
		GameRegistry.register(this);

		itemBlock = new ItemBlockCache(this);
		itemBlock.setRegistryName(this.getRegistryName());
		GameRegistry.register(itemBlock);

		return true;
	}

	@Override
	public boolean initialize() {

		TileCache.initialize();

		cache = new ItemStack[5];

		for (int i = 0; i < 5; i++) {
			cache[i] = itemBlock.setDefaultTag(new ItemStack(this), i);
		}
		return true;
	}

	@Override
	public boolean postInit() {

		// @formatter:off
		if (enable) {
			addRecipe(ShapedRecipe(cache[0],
					" I ",
					"ICI",
					" P ",
					'C', "chestWood",
					'I', "ingotTin",
					'P', ItemMaterial.redstoneServo
			));
		}
		// @formatter:on

		return true;
	}

	public static boolean enable;

	/* REFERENCES */
	public static ItemStack cache[];
	public static ItemBlockCache itemBlock;

}
