package cofh.thermalexpansion.block.workbench;

import static cofh.lib.util.helpers.ItemHelper.ShapedRecipe;

import cofh.core.render.IconRegistry;
import cofh.core.util.CoreUtils;
import cofh.core.util.crafting.RecipeUpgrade;
import cofh.lib.util.helpers.StringHelper;
import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalexpansion.block.BlockTEBase;
import cofh.thermalexpansion.block.TileInventory;
import cofh.thermalexpansion.block.strongbox.BlockStrongbox;
import cofh.thermalexpansion.util.crafting.TECraftingHandler;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class BlockWorkbench extends BlockTEBase {

	public BlockWorkbench() {

		super(Material.iron);
		setHardness(15.0F);
		setResistance(25.0F);
		setBlockName("thermalexpansion.workbench");
	}

	@Override
	public TileEntity createNewTileEntity(World world, int metadata) {

		if (metadata >= Types.values().length) {
			return null;
		}
		if (metadata == Types.CREATIVE.ordinal()) {
			if (!enable[Types.CREATIVE.ordinal()]) {
				return null;
			}
			return new TileWorkbenchCreative(metadata);
		}
		return new TileWorkbench(metadata);
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
	public float getBlockHardness(World world, int x, int y, int z) {

		return HARDNESS[world.getBlockMetadata(x, y, z)];
	}

	@Override
	public float getExplosionResistance(Entity entity, World world, int x, int y, int z, double explosionX, double explosionY, double explosionZ) {

		return RESISTANCE[world.getBlockMetadata(x, y, z)];
	}

	@Override
	public boolean isNormalCube(IBlockAccess world, int x, int y, int z) {

		return false;
	}

	@Override
	public boolean isOpaqueCube() {

		return true;
	}

	@Override
	public boolean isSideSolid(IBlockAccess world, int x, int y, int z, ForgeDirection side) {

		return true;
	}

	@Override
	public boolean renderAsNormalBlock() {

		return true;
	}

	@Override
	public IIcon getIcon(int side, int metadata) {

		if (side == 0) {
			return IconRegistry.getIcon("WorkbenchBottom", metadata);
		}
		if (side == 1) {
			return IconRegistry.getIcon("WorkbenchTop", metadata);
		}
		return IconRegistry.getIcon("WorkbenchSide", metadata);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister ir) {

		for (int i = 0; i < Types.values().length; i++) {
			IconRegistry.addIcon("WorkbenchBottom" + i, "thermalexpansion:workbench/Workbench_" + StringHelper.titleCase(NAMES[i]) + "_Bottom", ir);
			IconRegistry.addIcon("WorkbenchTop" + i, "thermalexpansion:workbench/Workbench_" + StringHelper.titleCase(NAMES[i]) + "_Top", ir);
			IconRegistry.addIcon("WorkbenchSide" + i, "thermalexpansion:workbench/Workbench_" + StringHelper.titleCase(NAMES[i]) + "_Side", ir);
		}
	}

	@Override
	public NBTTagCompound getItemStackTag(World world, int x, int y, int z) {

		NBTTagCompound tag = super.getItemStackTag(world, x, y, z);
		TileWorkbench tile = (TileWorkbench) world.getTileEntity(x, y, z);

		if (tile != null) {
			if (tag == null) {
				tag = new NBTTagCompound();
			}
			tag.setByte("Mode", (byte) tile.selectedSchematic);
			tile.writeInventoryToNBT(tag);
		}
		return tag;
	}

	/* IDismantleable */
	@Override
	public ArrayList<ItemStack> dismantleBlock(EntityPlayer player, World world, int x, int y, int z, boolean returnDrops) {

		NBTTagCompound tag = getItemStackTag(world, x, y, z);

		TileEntity tile = world.getTileEntity(x, y, z);
		if (tile instanceof TileInventory) {
			((TileInventory) tile).inventory = new ItemStack[((TileInventory) tile).inventory.length];
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

		TileWorkbench.initialize();
		TileWorkbenchCreative.initialize();

		workbenchCreative = new ItemStack(this, 1, Types.CREATIVE.ordinal());
		workbenchBasic = new ItemStack(this, 1, Types.BASIC.ordinal());
		workbenchHardened = new ItemStack(this, 1, Types.HARDENED.ordinal());
		workbenchReinforced = new ItemStack(this, 1, Types.REINFORCED.ordinal());
		workbenchResonant = new ItemStack(this, 1, Types.RESONANT.ordinal());

		GameRegistry.registerCustomItemStack("workbenchCreative", workbenchCreative);
		GameRegistry.registerCustomItemStack("workbenchBasic", workbenchBasic);
		GameRegistry.registerCustomItemStack("workbenchHardened", workbenchHardened);
		GameRegistry.registerCustomItemStack("workbenchReinforced", workbenchReinforced);
		GameRegistry.registerCustomItemStack("workbenchResonant", workbenchResonant);

		return true;
	}

	@Override
	public boolean postInit() {

		if (enable[Types.BASIC.ordinal()]) {
			GameRegistry.addRecipe(new RecipeUpgrade(7, workbenchBasic, new Object[] { " X ", "ICI", " P ", 'C', Blocks.crafting_table, 'I', "ingotCopper",
					'P', BlockStrongbox.strongboxBasic, 'X', Items.paper }));
			GameRegistry.addRecipe(ShapedRecipe(workbenchBasic, new Object[] { "YXY", "ICI", "YPY", 'C', Blocks.crafting_table, 'I', "ingotCopper", 'P',
					Blocks.chest, 'X', Items.paper, 'Y', "ingotTin" }));
		}
		if (enable[Types.HARDENED.ordinal()]) {
			GameRegistry.addRecipe(new RecipeUpgrade(workbenchHardened, new Object[] { " I ", "IXI", " I ", 'I', "ingotInvar", 'X', workbenchBasic }));
			GameRegistry.addRecipe(ShapedRecipe(workbenchHardened, new Object[] { "IYI", "YXY", "IYI", 'I', "ingotInvar", 'X', "logWood", 'Y', "ingotTin" }));
		}
		if (enable[Types.REINFORCED.ordinal()]) {
			GameRegistry.addRecipe(new RecipeUpgrade(workbenchReinforced, new Object[] { " I ", "IXI", " I ", 'I', "ingotSignalum", 'X', workbenchHardened }));
		}
		if (enable[Types.RESONANT.ordinal()]) {
			GameRegistry.addRecipe(new RecipeUpgrade(workbenchResonant, new Object[] { " I ", "IXI", " I ", 'I', "ingotEnderium", 'X', workbenchReinforced }));
		}
		TECraftingHandler.addSecureRecipe(workbenchCreative);
		TECraftingHandler.addSecureRecipe(workbenchBasic);
		TECraftingHandler.addSecureRecipe(workbenchHardened);
		TECraftingHandler.addSecureRecipe(workbenchReinforced);
		TECraftingHandler.addSecureRecipe(workbenchResonant);

		return true;
	}

	public static enum Types {
		CREATIVE, BASIC, HARDENED, REINFORCED, RESONANT
	}

	public static final String[] NAMES = { "creative", "basic", "hardened", "reinforced", "resonant" };
	public static final float[] HARDNESS = { -1.0F, 5.0F, 15.0F, 20.0F, 20.0F };
	public static final int[] RESISTANCE = { 1200, 15, 90, 120, 120 };
	public static boolean[] enable = new boolean[Types.values().length];

	static {
		String category = "Workbench.";

		enable[0] = ThermalExpansion.config.get(category + StringHelper.titleCase(NAMES[0]), "Enable", true);
		for (int i = 1; i < Types.values().length; i++) {
			enable[i] = ThermalExpansion.config.get(category + StringHelper.titleCase(NAMES[i]), "Recipe.Enable", true);
		}
	}

	public static ItemStack workbenchCreative;
	public static ItemStack workbenchBasic;
	public static ItemStack workbenchHardened;
	public static ItemStack workbenchReinforced;
	public static ItemStack workbenchResonant;

}
