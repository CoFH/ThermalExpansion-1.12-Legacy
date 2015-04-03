package cofh.thermalexpansion.block.cache;

import cofh.api.tileentity.ISidedTexture;
import cofh.core.render.IconRegistry;
import cofh.core.util.CoreUtils;
import cofh.core.util.crafting.RecipeUpgrade;
import cofh.lib.util.helpers.ItemHelper;
import cofh.lib.util.helpers.ServerHelper;
import cofh.lib.util.helpers.StringHelper;
import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalexpansion.block.BlockTEBase;
import cofh.thermalexpansion.util.Utils;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.oredict.ShapedOreRecipe;

public class BlockCache extends BlockTEBase {

	public BlockCache() {

		super(Material.iron);
		setHardness(15.0F);
		setResistance(25.0F);
		setBlockName("thermalexpansion.cache");
		setHarvestLevel("pickaxe", 1);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int metadata) {

		if (metadata >= Types.values().length) {
			return null;
		}
		if (metadata == Types.CREATIVE.ordinal() && !enable[Types.CREATIVE.ordinal()]) {
			return null;
		}
		return new TileCache(metadata);
	}

	@Override
	public void getSubBlocks(Item item, CreativeTabs tab, List list) {

		if (enable[0]) {
			list.add(new ItemStack(item, 1, 0));
		}
		for (int i = 1; i < Types.values().length; i++) {
			list.add(new ItemStack(item, 1, i));
		}
	}

	@Override
	public boolean hasComparatorInputOverride() {

		return true;
	}

	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase living, ItemStack stack) {

		if (stack.stackTagCompound != null) {
			TileCache tile = (TileCache) world.getTileEntity(x, y, z);
			tile.locked = stack.stackTagCompound.getBoolean("Lock");

			if (stack.stackTagCompound.hasKey("Item")) {
				ItemStack stored = ItemHelper.readItemStackFromNBT(stack.stackTagCompound.getCompoundTag("Item"));
				tile.setStoredItemType(stored, stored.stackSize);
			}
		}
		super.onBlockPlacedBy(world, x, y, z, living, stack);
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int hitSide, float hitX, float hitY, float hitZ) {

		if (super.onBlockActivated(world, x, y, z, player, hitSide, hitX, hitY, hitZ)) {
			if (Utils.isHoldingUsableWrench(player, x, y, z)) {
				return true;
			}
		}
		TileCache tile = (TileCache) world.getTileEntity(x, y, z);
		boolean playSound = false;
		int insertCount = 0;

		if (ItemHelper.isPlayerHoldingNothing(player)) {
			if (player.isSneaking()) {
				tile.toggleLock();

				if (tile.locked) {
					world.playSoundEffect(x + 0.5D, y + 0.5D, z + 0.5D, "random.click", 0.1F, 0.8F);
				} else {
					world.playSoundEffect(x + 0.5D, y + 0.5D, z + 0.5D, "random.click", 0.1F, 0.6F);
				}
				return true;
			}
			if (tile.getStoredItemType() != null) {
				for (int i = 0; i < player.inventory.getSizeInventory(); i++) {
					if (tile.insertItem(ForgeDirection.UNKNOWN, player.inventory.getStackInSlot(i), true) != player.inventory.getStackInSlot(i)) {
						player.inventory.setInventorySlotContents(i, tile.insertItem(ForgeDirection.UNKNOWN, player.inventory.getStackInSlot(i), false));
						playSound = true;
					}
				}
				if (playSound) {
					world.playSoundEffect(x + 0.5, y + 0.5, z + 0.5, "random.orb", 0.1F, 0.7F);
				}
			}
			return true;
		}
		ItemStack heldStack = player.getCurrentEquippedItem();
		ItemStack ret = tile.insertItem(ForgeDirection.UNKNOWN, heldStack, false);

		if (!player.capabilities.isCreativeMode && ret != heldStack) {
			player.inventory.setInventorySlotContents(player.inventory.currentItem, ret);
			playSound = true;
		}
		if (playSound) {
			world.playSoundEffect(x + 0.5, y + 0.5, z + 0.5, "random.orb", 0.1F, 0.7F);
		}
		return true;
	}

	@Override
	public void onBlockClicked(World world, int x, int y, int z, EntityPlayer player) {

		if (ServerHelper.isClientWorld(world)) {
			return;
		}
		boolean playSound = false;
		TileCache tile = (TileCache) world.getTileEntity(x, y, z);

		int extractAmount = player.isSneaking() ? 1 : 64;
		ItemStack extract = tile.extractItem(ForgeDirection.UNKNOWN, extractAmount, true);

		if (!player.capabilities.isCreativeMode) {
			if (!player.inventory.addItemStackToInventory(extract)) {
				return;
			}
			playSound = true;
			tile.extractItem(ForgeDirection.UNKNOWN, extractAmount, false);
		} else {
			tile.extractItem(ForgeDirection.UNKNOWN, extractAmount, false);
		}
		if (playSound) {
			world.playSoundEffect(x + 0.5, y + 0.5, z + 0.5, "random.pop", 0.1F, 0.8F);
		}
	}

	@Override
	public float getPlayerRelativeBlockHardness(EntityPlayer player, World world, int x, int y, int z) {

		ItemStack stack = player.getCurrentEquippedItem();
		if (stack == null || !ForgeHooks.isToolEffective(stack, world.getBlock(x, y, z), world.getBlockMetadata(x, y, z))) {
			return -1;
		}
		return super.getPlayerRelativeBlockHardness(player, world, x, y, z);
	}

	@Override
	public int getRenderBlockPass() {

		return 1;
	}

	@Override
	public boolean canRenderInPass(int pass) {

		renderPass = pass;
		return pass < 2;
	}

	@Override
	public boolean isOpaqueCube() {

		return true;
	}

	@Override
	public boolean renderAsNormalBlock() {

		return true;
	}

	@Override
	public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side) {

		ISidedTexture tile = (ISidedTexture) world.getTileEntity(x, y, z);
		return tile == null ? null : tile.getTexture(side, renderPass);
	}

	@Override
	public IIcon getIcon(int side, int metadata) {

		if (side == 0) {
			return IconRegistry.getIcon("CacheBottom", metadata);
		}
		if (side == 1) {
			return IconRegistry.getIcon("CacheTop", metadata);
		}
		return side != 3 ? IconRegistry.getIcon("CacheSide", metadata) : IconRegistry.getIcon("CacheFace", metadata);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister ir) {

		for (int i = 0; i < 9; i++) {
			IconRegistry.addIcon("CacheMeter" + i, "thermalexpansion:cache/Cache_Meter_" + i, ir);
		}
		for (int i = 0; i < Types.values().length; i++) {
			IconRegistry.addIcon("CacheBottom" + i, "thermalexpansion:cache/Cache_" + StringHelper.titleCase(NAMES[i]) + "_Bottom", ir);
			IconRegistry.addIcon("CacheTop" + i, "thermalexpansion:cache/Cache_" + StringHelper.titleCase(NAMES[i]) + "_Top", ir);
			IconRegistry.addIcon("CacheSide" + i, "thermalexpansion:cache/Cache_" + StringHelper.titleCase(NAMES[i]) + "_Side", ir);
			IconRegistry.addIcon("CacheFace" + i, "thermalexpansion:cache/Cache_" + StringHelper.titleCase(NAMES[i]) + "_Face", ir);
		}
		IconRegistry.addIcon("CacheBlank", "thermalexpansion:config/Config_None", ir);
	}

	@Override
	public NBTTagCompound getItemStackTag(World world, int x, int y, int z) {

		NBTTagCompound tag = super.getItemStackTag(world, x, y, z);
		TileCache tile = (TileCache) world.getTileEntity(x, y, z);

		if (tile != null && tile.storedStack != null) {
			tag = new NBTTagCompound();
			tag.setBoolean("Lock", tile.locked);
			tag.setTag("Item", ItemHelper.writeItemStackToNBT(tile.storedStack, tile.getStoredCount(), new NBTTagCompound()));
		}
		return tag;
	}

	/* IDismantleable */
	@Override
	public ArrayList<ItemStack> dismantleBlock(EntityPlayer player, World world, int x, int y, int z, boolean returnDrops) {

		NBTTagCompound tag = getItemStackTag(world, x, y, z);

		TileEntity tile = world.getTileEntity(x, y, z);
		if (tile instanceof TileCache) {
			((TileCache) tile).inventory = new ItemStack[((TileCache) tile).inventory.length];
		}
		return super.dismantleBlock(player, tag, world, x, y, z, returnDrops, false);
	}

	@Override
	public boolean canDismantle(EntityPlayer player, World world, int x, int y, int z) {

		if (world.getBlockMetadata(x, y, z) == Types.CREATIVE.ordinal() && !CoreUtils.isOp(player)) {
			return false;
		}
		return super.canDismantle(player, world, x, y, z);
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
			GameRegistry.addRecipe(new ShapedOreRecipe(cacheBasic, new Object[] { " I ", "IXI", " I ", 'I', "ingotTin", 'X', "logWood" }));
		}
		if (enable[Types.HARDENED.ordinal()]) {
			GameRegistry.addRecipe(new RecipeUpgrade(cacheHardened, new Object[] { " I ", "IXI", " I ", 'I', "ingotInvar", 'X', cacheBasic }));
			GameRegistry
					.addRecipe(new ShapedOreRecipe(cacheHardened, new Object[] { "IYI", "YXY", "IYI", 'I', "ingotInvar", 'X', "logWood", 'Y', "ingotTin" }));
		}
		if (enable[Types.REINFORCED.ordinal()]) {
			GameRegistry.addRecipe(new RecipeUpgrade(cacheReinforced, new Object[] { " G ", "GXG", " G ", 'X', cacheHardened, 'G', "blockGlassHardened" }));
		}
		if (enable[Types.RESONANT.ordinal()]) {
			GameRegistry.addRecipe(new RecipeUpgrade(cacheResonant, new Object[] { " I ", "IXI", " I ", 'I', "ingotEnderium", 'X', cacheReinforced }));
		}
		return true;
	}

	public static enum Types {
		CREATIVE, BASIC, HARDENED, REINFORCED, RESONANT
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
