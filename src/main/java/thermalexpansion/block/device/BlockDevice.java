package thermalexpansion.block.device;

import cofh.api.core.ISecurable;
import cofh.api.tileentity.ISidedTexture;
import cofh.render.IconRegistry;
import cofh.util.RecipeUpgrade;
import cofh.util.StringHelper;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import java.util.List;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
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
import net.minecraftforge.oredict.ShapedOreRecipe;

import thermalexpansion.ThermalExpansion;
import thermalexpansion.block.BlockTEBase;
import thermalexpansion.block.strongbox.BlockStrongbox;
import thermalexpansion.item.TEEquipment;
import thermalexpansion.item.TEItems;

public class BlockDevice extends BlockTEBase {

	public BlockDevice() {

		super(Material.iron);
		setHardness(15.0F);
		setResistance(25.0F);
		setBlockName("thermalexpansion.device");
	}

	@Override
	public TileEntity createNewTileEntity(World world, int metadata) {

		if (metadata >= Types.values().length) {
			return null;
		}
		switch (Types.values()[metadata]) {
		case WORKBENCH:
			return new TileWorkbench();
		case ACTIVATOR:
			return new TileActivator();
		case BREAKER:
			return new TileBreaker();
		case PUMP:
			return new TilePump();
		case NULLIFIER:
			return new TileNullifier();
		default:
			return null;
		}
	}

	@Override
	public void getSubBlocks(Item item, CreativeTabs tab, List list) {

		for (int i = 0; i < Types.values().length; i++) {
			if (enable[i]) {
				list.add(new ItemStack(item, 1, i));
			}
		}
	}

	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase living, ItemStack stack) {

		if (stack.stackTagCompound != null) {
			TileEntity theTile = world.getTileEntity(x, y, z);
			if (theTile instanceof TileWorkbench) {
				TileWorkbench tile = (TileWorkbench) theTile;

				if (stack.stackTagCompound.hasKey("Owner")) {
					tile.setOwnerName(stack.stackTagCompound.getString("Owner"));
					tile.setAccess(ISecurable.AccessMode.values()[stack.stackTagCompound.getByte("Access")]);
					tile.selectedSchematic = stack.stackTagCompound.getByte("Mode");
					tile.readInventoryFromNBT(stack.stackTagCompound);
				}
			}
		}
		super.onBlockPlacedBy(world, x, y, z, living, stack);
	}

	@Override
	public float getBlockHardness(World world, int x, int y, int z) {

		TileEntity tile = world.getTileEntity(x, y, z);
		return tile instanceof ISecurable ? -1 : super.getBlockHardness(world, x, y, z);
	}

	@Override
	public float getExplosionResistance(Entity entity, World world, int x, int y, int z, double explosionX, double explosionY, double explosionZ) {

		TileEntity tile = world.getTileEntity(x, y, z);
		return tile instanceof ISecurable ? 1200 : super.getExplosionResistance(entity, world, x, y, z, explosionX, explosionY, explosionZ);
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
	public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side) {

		ISidedTexture theTile = (ISidedTexture) world.getTileEntity(x, y, z);
		return theTile == null ? null : theTile.getTexture(side, renderPass);
	}

	@Override
	public IIcon getIcon(int side, int metadata) {

		if (metadata == Types.WORKBENCH.ordinal()) {
			if (side == 0) {
				return IconRegistry.getIcon("WorkbenchBottom");
			} else if (side == 1) {
				return IconRegistry.getIcon("WorkbenchTop");
			}
			return IconRegistry.getIcon("WorkbenchSide");
		}
		return side != 3 ? IconRegistry.getIcon("DeviceSide") : IconRegistry.getIcon("DeviceFace" + metadata);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister ir) {

		IconRegistry.addIcon("WorkbenchBottom", "thermalexpansion:device/Device_Bottom_Workbench", ir);
		IconRegistry.addIcon("WorkbenchTop", "thermalexpansion:device/Device_Top_Workbench", ir);
		IconRegistry.addIcon("WorkbenchSide", "thermalexpansion:device/Device_Side_Workbench", ir);

		IconRegistry.addIcon("DeviceSide", "thermalexpansion:device/Device_Side", ir);

		// Face Textures
		for (int i = 2; i < Types.values().length; i++) {
			IconRegistry.addIcon("DeviceFace" + i, "thermalexpansion:device/Device_Face_" + StringHelper.titleCase(NAMES[i]), ir);
			IconRegistry.addIcon("DeviceActive" + i, "thermalexpansion:device/Device_Active_" + StringHelper.titleCase(NAMES[i]), ir);
		}
	}

	@Override
	public NBTTagCompound getItemStackTag(World world, int x, int y, int z) {

		NBTTagCompound tag = super.getItemStackTag(world, x, y, z);
		TileEntity tile = world.getTileEntity(x, y, z);

		if (tile instanceof TileWorkbench) {
			TileWorkbench theTile = (TileWorkbench) tile;

			if (tag == null) {
				tag = new NBTTagCompound();
			}
			tag.setString("Owner", theTile.owner);
			tag.setByte("Access", (byte) theTile.getAccess().ordinal());
			tag.setByte("Mode", (byte) theTile.selectedSchematic);

			theTile.writeInventoryToNBT(tag);
		}
		return tag;
	}

	/* IDismantleable */
	@Override
	public ItemStack dismantleBlock(EntityPlayer player, World world, int x, int y, int z, boolean returnBlock) {

		NBTTagCompound tag = getItemStackTag(world, x, y, z);

		TileEntity tile = world.getTileEntity(x, y, z);
		if (tile instanceof TileWorkbench) {
			((TileWorkbench) tile).inventory = new ItemStack[((TileWorkbench) tile).inventory.length];
		}
		return super.dismantleBlock(player, tag, world, x, y, z, returnBlock, false);
	}

	/* IInitializer */
	@Override
	public boolean initialize() {

		TileWorkbench.initialize();
		TileActivator.initialize();
		TileBreaker.initialize();
		// TilePump.initialize();
		TileNullifier.initialize();

		workbench = new ItemStack(this, 1, Types.WORKBENCH.ordinal());
		// lexicon = new ItemStack(this, 1, Types.LEXICON.ordinal());
		activator = new ItemStack(this, 1, Types.ACTIVATOR.ordinal());
		breaker = new ItemStack(this, 1, Types.BREAKER.ordinal());
		// pump = new ItemStack(this, 1, Types.PUMP.ordinal());
		nullifier = new ItemStack(this, 1, Types.NULLIFIER.ordinal());

		GameRegistry.registerCustomItemStack("workbench", workbench);
		GameRegistry.registerCustomItemStack("activator", activator);
		GameRegistry.registerCustomItemStack("breaker", breaker);
		GameRegistry.registerCustomItemStack("nullifier", nullifier);

		return true;
	}

	@Override
	public boolean postInit() {

		String category = "tweak.recipe";
		boolean breakerUseDiamondPickaxe = ThermalExpansion.config.get(category, "Breaker.UseDiamondPickaxe", false);

		ItemStack pickaxe = null;

		if (breakerUseDiamondPickaxe) {
			pickaxe = new ItemStack(Items.diamond_pickaxe);
		} else {
			pickaxe = TEEquipment.toolInvarPickaxe;
		}
		if (enable[Types.WORKBENCH.ordinal()]) {
			GameRegistry.addRecipe(new RecipeUpgrade(7, workbench, new Object[] { " X ", "ICI", " P ", 'C', Blocks.crafting_table, 'I', "ingotCopper", 'P',
					BlockStrongbox.strongboxBasic, 'X', Items.paper }));
			GameRegistry.addRecipe(new ShapedOreRecipe(workbench, new Object[] { "YXY", "ICI", "YPY", 'C', Blocks.crafting_table, 'I', "ingotCopper", 'P',
					Blocks.chest, 'X', Items.paper, 'Y', "ingotTin" }));
		}
		if (enable[Types.LEXICON.ordinal()]) {

		}
		if (enable[Types.ACTIVATOR.ordinal()]) {
			GameRegistry.addRecipe(new ShapedOreRecipe(activator, new Object[] { " X ", "ICI", " P ", 'C', Blocks.piston, 'I', "ingotTin", 'P',
					TEItems.pneumaticServo, 'X', Blocks.chest }));
		}
		if (enable[Types.BREAKER.ordinal()]) {
			GameRegistry.addRecipe(new ShapedOreRecipe(breaker, new Object[] { " X ", "ICI", " P ", 'C', Blocks.piston, 'I', "ingotTin", 'P',
					TEItems.pneumaticServo, 'X', pickaxe }));
		}
		if (enable[Types.PUMP.ordinal()]) {

		}
		if (enable[Types.NULLIFIER.ordinal()]) {
			GameRegistry.addRecipe(new ShapedOreRecipe(nullifier, new Object[] { " X ", "ICI", " P ", 'C', Items.lava_bucket, 'I', "ingotTin", 'P',
					TEItems.pneumaticServo, 'X', "ingotInvar" }));
		}
		return true;
	}

	public static enum Types {
		WORKBENCH, LEXICON, ACTIVATOR, BREAKER, PUMP, NULLIFIER
	}

	public static String[] NAMES = { "workbench", "lexicon", "activator", "breaker", "pump", "nullifier" };
	public static boolean[] enable = new boolean[Types.values().length];

	static {
		String category = "block.feature";
		enable[Types.WORKBENCH.ordinal()] = ThermalExpansion.config.get(category, "Device.Workbench", true);
		enable[Types.ACTIVATOR.ordinal()] = ThermalExpansion.config.get(category, "Device.Activator", true);
		enable[Types.BREAKER.ordinal()] = ThermalExpansion.config.get(category, "Device.Breaker", true);
		enable[Types.NULLIFIER.ordinal()] = ThermalExpansion.config.get(category, "Device.Nullifier", true);
	}

	public static ItemStack workbench;
	public static ItemStack lexicon;
	public static ItemStack activator;
	public static ItemStack breaker;
	public static ItemStack pump;
	public static ItemStack nullifier;

}
