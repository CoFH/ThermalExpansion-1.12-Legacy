package thermalexpansion.block.cache;

import cofh.api.tileentity.ISidedBlockTexture;
import cofh.render.IconRegistry;
import cofh.util.CoreUtils;
import cofh.util.StringHelper;
import cofh.util.UpgradeRecipe;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import java.util.List;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.oredict.ShapedOreRecipe;

import thermalexpansion.ThermalExpansion;
import thermalexpansion.block.BlockTEBase;
import thermalexpansion.block.TileInventory;

public class BlockCache extends BlockTEBase {

	public BlockCache() {

		super(Material.iron);
		setHardness(15.0F);
		setResistance(25.0F);
		setBlockName("thermalexpansion.cache");
	}

	@Override
	public TileEntity createNewTileEntity(World world, int metadata) {

		if (metadata >= Types.values().length) {
			return null;
		}
		return new TileCache(metadata);
	}

	@Override
	public void getSubBlocks(Item item, CreativeTabs tab, List list) {

		for (int i = 0; i < Types.values().length; ++i) {
			if (enable[i]) {
				list.add(new ItemStack(item, 1, i));
			}
		}
	}

	@Override
	public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side) {

		ISidedBlockTexture tile = (ISidedBlockTexture) world.getTileEntity(x, y, z);
		return tile == null ? null : tile.getBlockTexture(side, renderPass);
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
	public boolean isOpaqueCube() {

		return true;
	}

	@Override
	public boolean renderAsNormalBlock() {

		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister ir) {

		for (int i = 0; i < Types.values().length; i++) {
			IconRegistry.addIcon("CacheBottom" + i, "thermalexpansion:cache/Cache_" + StringHelper.titleCase(NAMES[i]) + "_Bottom", ir);
			IconRegistry.addIcon("CacheTop" + i, "thermalexpansion:cache/Cache_" + StringHelper.titleCase(NAMES[i]) + "_Top", ir);
			IconRegistry.addIcon("CacheSide" + i, "thermalexpansion:cache/Cache_" + StringHelper.titleCase(NAMES[i]) + "_Side", ir);
			IconRegistry.addIcon("CacheFace" + i, "thermalexpansion:cache/Cache_" + StringHelper.titleCase(NAMES[i]) + "_Face", ir);
		}
	}

	/* IDismantleable */
	@Override
	public ItemStack dismantleBlock(EntityPlayer player, World world, int x, int y, int z, boolean returnBlock) {

		NBTTagCompound tag = getItemStackTag(world, x, y, z);

		TileEntity tile = world.getTileEntity(x, y, z);
		if (tile instanceof TileInventory) {
			((TileInventory) tile).inventory = new ItemStack[((TileInventory) tile).inventory.length];
		}
		return super.dismantleBlock(player, tag, world, x, y, z, returnBlock, false);
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
			GameRegistry.addRecipe(new UpgradeRecipe(cacheHardened, new Object[] { " I ", "IXI", " I ", 'I', "ingotInvar", 'X', cacheBasic }));
			GameRegistry
					.addRecipe(new ShapedOreRecipe(cacheHardened, new Object[] { "IYI", "YXY", "IYI", 'I', "ingotInvar", 'X', "logWood", 'Y', "ingotTin" }));
		}
		if (enable[Types.REINFORCED.ordinal()]) {
			GameRegistry.addRecipe(new UpgradeRecipe(cacheReinforced, new Object[] { " G ", "GXG", " G ", 'X', cacheHardened, 'G', "glassHardened" }));
		}
		if (enable[Types.RESONANT.ordinal()]) {
			GameRegistry.addRecipe(new UpgradeRecipe(cacheResonant, new Object[] { " I ", "IXI", " I ", 'I', "ingotEnderium", 'X', cacheReinforced }));
		}
		return true;
	}

	public static enum Types {
		CREATIVE, BASIC, HARDENED, REINFORCED, RESONANT
	}

	public static final String[] NAMES = { "creative", "basic", "hardened", "reinforced", "resonant" };
	public static boolean[] enable = new boolean[Types.values().length];

	static {
		String category = "block.feature";
		enable[Types.CREATIVE.ordinal()] = ThermalExpansion.config.get(category, "Cache.Creative", true);
		enable[Types.BASIC.ordinal()] = ThermalExpansion.config.get(category, "Cache.Basic", true);
		enable[Types.HARDENED.ordinal()] = ThermalExpansion.config.get(category, "Cache.Hardened", true);
		enable[Types.REINFORCED.ordinal()] = ThermalExpansion.config.get(category, "Cache.Reinforced", true);
		enable[Types.RESONANT.ordinal()] = ThermalExpansion.config.get(category, "Cache.Resonant", true);
	}

	public static ItemStack cacheCreative;
	public static ItemStack cacheBasic;
	public static ItemStack cacheHardened;
	public static ItemStack cacheReinforced;
	public static ItemStack cacheResonant;

}
