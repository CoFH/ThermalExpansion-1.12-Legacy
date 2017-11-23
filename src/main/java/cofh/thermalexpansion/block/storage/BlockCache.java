package cofh.thermalexpansion.block.storage;

import codechicken.lib.model.ModelRegistryHelper;
import codechicken.lib.model.bakery.CCBakeryModel;
import codechicken.lib.model.bakery.IBakeryProvider;
import codechicken.lib.model.bakery.ModelBakery;
import codechicken.lib.model.bakery.ModelErrorStateProperty;
import codechicken.lib.model.bakery.generation.IBakery;
import codechicken.lib.texture.IWorldBlockTextureProvider;
import codechicken.lib.texture.TextureUtils;
import cofh.api.block.IConfigGui;
import cofh.core.init.CoreEnchantments;
import cofh.core.render.IModelRegister;
import cofh.core.util.StateMapper;
import cofh.core.util.helpers.ItemHelper;
import cofh.core.util.helpers.MathHelper;
import cofh.core.util.helpers.ServerHelper;
import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalexpansion.block.BlockTEBase;
import cofh.thermalexpansion.init.TEProps;
import cofh.thermalexpansion.init.TETextures;
import cofh.thermalexpansion.render.BakeryCache;
import cofh.thermalfoundation.item.ItemMaterial;
import cofh.thermalfoundation.item.ItemUpgrade;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import static cofh.core.util.helpers.RecipeHelper.*;

public class BlockCache extends BlockTEBase implements IModelRegister, IBakeryProvider, IWorldBlockTextureProvider, IConfigGui {

	public BlockCache() {

		super(Material.IRON);

		setUnlocalizedName("cache");

		setHardness(15.0F);
		setResistance(25.0F);
		setDefaultState(getBlockState().getBaseState());

		standardGui = false;
		configGui = true;
	}

	@Override
	protected BlockStateContainer createBlockState() {

		BlockStateContainer.Builder builder = new BlockStateContainer.Builder(this);
		// UnListed
		builder.add(ModelErrorStateProperty.ERROR_STATE);
		builder.add(TEProps.TILE_CACHE);

		return builder.build();
	}

	@Override
	public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> items) {

		if (enable) {
			if (TEProps.creativeTabShowAllLevels) {
				for (int j = 0; j < 5; j++) {
					items.add(itemBlock.setDefaultTag(new ItemStack(this, 1, 0), j));
				}
			} else {
				items.add(itemBlock.setDefaultTag(new ItemStack(this, 1, 0), TEProps.creativeTabLevel));
			}
			if (TEProps.creativeTabShowCreative) {
				items.add(itemBlock.setCreativeTag(new ItemStack(this, 1, 0)));
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
		TileCache tile = (TileCache) world.getTileEntity(pos);

		int extractAmount = !player.isSneaking() && !player.capabilities.isCreativeMode ? 1 : 64;
		ItemStack extract = tile.extractItem(extractAmount, true);
		if (extract.isEmpty()) {
			return;
		}
		if (!player.capabilities.isCreativeMode) {
			if (!player.inventory.addItemStackToInventory(extract)) {
				// apparently this returns false if it succeeds but doesn't have room for all
				// seemingly designed for inserts of single items but supports > 1 inserts because Notch
				if (extract.getCount() == extractAmount) {
					return;
				}
				extractAmount -= extract.getCount();
			}
			tile.extractItem(extractAmount, false);
		} else {
			player.inventory.addItemStackToInventory(extract);
			tile.extractItem(extractAmount, false);
		}
		world.playSound(null, pos, SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.BLOCKS, 0.4F, 0.8F);
	}

	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase living, ItemStack stack) {

		if (stack.getTagCompound() != null) {
			TileCache tile = (TileCache) world.getTileEntity(pos);

			tile.isCreative = (stack.getTagCompound().getBoolean("Creative"));
			tile.enchantHolding = (byte) EnchantmentHelper.getEnchantmentLevel(CoreEnchantments.holding, stack);
			tile.setLevel(stack.getTagCompound().getByte("Level"));

			if (stack.getTagCompound().hasKey("Item")) {
				ItemStack stored = ItemHelper.readItemStackFromNBT(stack.getTagCompound().getCompoundTag("Item"));

				tile.setStoredItemType(stored, stored.getCount());
				tile.lock = stack.getTagCompound().getBoolean("Lock");
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
	public boolean onBlockActivatedDelegate(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {

		TileCache tile = (TileCache) world.getTileEntity(pos);

		if (tile != null) {
			if (ItemHelper.isPlayerHoldingNothing(player)) {
				if (player.isSneaking()) {
					tile.setLocked(!tile.isLocked());
					if (tile.isLocked()) {
						world.playSound(null, pos, SoundEvents.UI_BUTTON_CLICK, SoundCategory.BLOCKS, 0.2F, 0.8F);
					} else {
						world.playSound(null, pos, SoundEvents.UI_BUTTON_CLICK, SoundCategory.BLOCKS, 0.3F, 0.5F);
					}
					return true;
				}
			}
			boolean playSound = false;

			ItemStack heldItem = player.getHeldItem(hand);
			ItemStack ret = tile.insertItem(heldItem, false);
			long time = player.getEntityData().getLong("thermalexpansion:CacheClick"), currentTime = world.getTotalWorldTime();
			player.getEntityData().setLong("thermalexpansion:CacheClick", currentTime);

			if (!player.capabilities.isCreativeMode) {
				if (ret != heldItem) {
					player.inventory.setInventorySlotContents(player.inventory.currentItem, ret);
					playSound = true;
				}
				if (!tile.getStoredItemType().isEmpty() && currentTime - time < 15) {
					playSound &= !insertAllItemsFromPlayer(tile, player);
				}
			}
			if (playSound) {
				world.playSound(null, pos, SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.BLOCKS, 0.1F, 0.7F);
			}
			return true;
		}
		return false;
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
			if (tile.insertItem(player.inventory.getStackInSlot(i), true) != player.inventory.getStackInSlot(i)) {
				player.inventory.setInventorySlotContents(i, tile.insertItem(player.inventory.getStackInSlot(i), false));
				playSound = true;
			}
		}
		if (playSound) {
			tile.getWorld().playSound(null, tile.getPos(), SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.BLOCKS, 0.1F, 0.7F);
		}
		return playSound;
	}

	@Override
	public float getPlayerRelativeBlockHardness(IBlockState state, EntityPlayer player, World world, BlockPos pos) {

		ItemStack stack = player.getHeldItemMainhand();
		if (stack.isEmpty() || !ForgeHooks.isToolEffective(world, pos, stack)) {
			return -1;
		}
		return super.getPlayerRelativeBlockHardness(state, player, world, pos);
	}

	/* HELPERS */
	@Override
	public NBTTagCompound getItemStackTag(IBlockAccess world, BlockPos pos) {

		NBTTagCompound retTag = super.getItemStackTag(world, pos);
		TileCache tile = (TileCache) world.getTileEntity(pos);

		if (tile != null) {
			if (tile.enchantHolding > 0) {
				CoreEnchantments.addEnchantment(retTag, CoreEnchantments.holding, tile.enchantHolding);
			}
			if (!tile.storedStack.isEmpty()) {
				retTag.setTag("Item", ItemHelper.writeItemStackToNBT(tile.storedStack, tile.getStoredCount(), new NBTTagCompound()));
				retTag.setBoolean("Lock", tile.lock);
			}
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

		return ModelBakery.handleExtendedState((IExtendedBlockState) super.getExtendedState(state, world, pos), world, pos);
	}

	/* IBakeryProvider */
	@Override
	public IBakery getBakery() {

		return BakeryCache.INSTANCE;
	}

	/* IWorldBlockTextureProvider */
	@Override
	@SideOnly (Side.CLIENT)
	public TextureAtlasSprite getTexture(EnumFacing side, ItemStack stack) {

		boolean isCreative = itemBlock.isCreative(stack);
		int level = itemBlock.getLevel(stack);

		if (side == EnumFacing.DOWN) {
			return isCreative ? TETextures.CACHE_BOTTOM_C : TETextures.CACHE_BOTTOM[level];
		}
		if (side == EnumFacing.UP) {
			return isCreative ? TETextures.CACHE_TOP_C : TETextures.CACHE_TOP[level];
		}
		return side != EnumFacing.NORTH ? isCreative ? TETextures.CACHE_SIDE_C : TETextures.CACHE_SIDE[level] : isCreative ? TETextures.CACHE_FACE_C : TETextures.CACHE_FACE[level];
	}

	@Override
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

		StateMapper mapper = new StateMapper("thermalexpansion", "cache", "cache");
		ModelLoader.setCustomModelResourceLocation(itemBlock, 0, mapper.location);
		ModelLoader.setCustomStateMapper(this, mapper);
		ModelLoader.setCustomMeshDefinition(itemBlock, mapper);
		ModelRegistryHelper.register(mapper.location, new CCBakeryModel());

		ModelBakery.registerBlockKeyGenerator(this, state -> {

			StringBuilder builder = new StringBuilder(state.getBlock().getRegistryName().toString() + "|" + state.getBlock().getMetaFromState(state));
			TileCache tile = state.getValue(TEProps.TILE_CACHE);
			builder.append(",creative=").append(tile.isCreative);
			builder.append(",level=").append(tile.getLevel());
			builder.append(",holding=").append(tile.enchantHolding);
			builder.append(",facing=").append(tile.getFacing());
			builder.append(",scale=").append(MathHelper.clamp(tile.getScaledItemsStored(9), 0, 8));
			return builder.toString();
		});

		ModelBakery.registerItemKeyGenerator(itemBlock, stack -> ModelBakery.defaultItemKeyGenerator.generateKey(stack) + ",creative=" + itemBlock.isCreative(stack) + ",level=" + itemBlock.getLevel(stack));
	}

	/* IInitializer */
	@Override
	public boolean initialize() {

		this.setRegistryName("cache");
		ForgeRegistries.BLOCKS.register(this);

		itemBlock = new ItemBlockCache(this);
		itemBlock.setRegistryName(this.getRegistryName());
		ForgeRegistries.ITEMS.register(itemBlock);

		TileCache.initialize();

		ThermalExpansion.proxy.addIModelRegister(this);

		return true;
	}

	@Override
	public boolean register() {

		cache = new ItemStack[5];

		for (int i = 0; i < 5; i++) {
			cache[i] = itemBlock.setDefaultTag(new ItemStack(this), i);
		}
		cacheCreative = itemBlock.setCreativeTag(new ItemStack(this));

		addRecipes();
		addUpgradeRecipes();
		addClassicRecipes();

		return true;
	}

	/* HELPERS */
	private void addRecipes() {

		// @formatter:off
		if (enable) {
			addShapedRecipe(cache[0],
					" I ",
					"ICI",
					" P ",
					'C', "chestWood",
					'I', "ingotTin",
					'P', ItemMaterial.redstoneServo
			);
		}
		// @formatter:on
	}

	private void addUpgradeRecipes() {

		if (!enableUpgradeKitCrafting || !enable) {
			return;
		}
		if (!enableClassicRecipes) {
			for (int j = 0; j < 4; j++) {
				addShapelessUpgradeKitRecipe(cache[j + 1], cache[j], ItemUpgrade.upgradeIncremental[j]);
			}
			for (int j = 1; j < 4; j++) {
				for (int k = 0; k <= j; k++) {
					addShapelessUpgradeKitRecipe(cache[j + 1], cache[k], ItemUpgrade.upgradeFull[j]);
				}
			}
		}
		for (int j = 0; j < 5; j++) {
			addShapelessUpgradeKitRecipe(cacheCreative, cache[j], ItemUpgrade.upgradeCreative);
		}
	}

	private void addClassicRecipes() {

		if (!enableClassicRecipes || !enable) {
			return;
		}
		// @formatter:off
		addShapedRecipe(cache[1],
				"YIY",
				"ICI",
				"YPY",
				'C', "chestWood",
				'I', "ingotTin",
				'P', ItemMaterial.redstoneServo,
				'Y', "ingotInvar"
		);
		addShapedUpgradeRecipe(cache[1],
				" I ",
				"ICI",
				" I ",
				'C', cache[0],
				'I', "ingotInvar"
		);
		addShapedUpgradeRecipe(cache[2],
				"YIY",
				"ICI",
				"YIY",
				'C', cache[1],
				'I', "ingotElectrum",
				'Y', "blockGlassHardened"
		);
		addShapedUpgradeRecipe(cache[3],
				" I ",
				"ICI",
				" I ",
				'C', cache[2],
				'I', "ingotSignalum"
		);
		addShapedUpgradeRecipe(cache[4],
				" I ",
				"ICI",
				" I ",
				'C', cache[3],
				'I', "ingotEnderium"
		);
		// @formatter:on
	}

	public static boolean enable;
	public static boolean enableClassicRecipes;
	public static boolean enableUpgradeKitCrafting;

	/* REFERENCES */
	public static ItemStack cache[];
	public static ItemStack cacheCreative;
	public static ItemBlockCache itemBlock;

}
