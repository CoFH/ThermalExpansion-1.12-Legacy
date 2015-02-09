package cofh.thermalexpansion.block.device;

import cofh.api.tileentity.ISidedTexture;
import cofh.core.render.IconRegistry;
import cofh.core.util.crafting.RecipeAugmentable;
import cofh.core.util.crafting.RecipeUpgrade;
import cofh.lib.util.helpers.BlockHelper;
import cofh.lib.util.helpers.ItemHelper;
import cofh.lib.util.helpers.StringHelper;
import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalexpansion.block.BlockTEBase;
import cofh.thermalexpansion.block.TileAugmentable;
import cofh.thermalexpansion.block.strongbox.BlockStrongbox;
import cofh.thermalexpansion.item.TEAugments;
import cofh.thermalexpansion.item.TEEquipment;
import cofh.thermalexpansion.item.TEItems;
import cofh.thermalexpansion.util.ReconfigurableHelper;
import cofh.thermalexpansion.util.crafting.TECraftingHandler;
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

		if (enable[0]) {
			list.add(new ItemStack(item, 1, 0));
		}
		for (int i = 1; i < Types.values().length; i++) {
			if (enable[i]) {
				list.add(ItemBlockDevice.setDefaultTag(new ItemStack(item, 1, i)));
			}
		}
	}

	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase living, ItemStack stack) {

		if (stack.stackTagCompound != null) {
			TileEntity aTile = world.getTileEntity(x, y, z);

			if (aTile instanceof TileWorkbench) {
				TileWorkbench tile = (TileWorkbench) aTile;

				if (stack.stackTagCompound.hasKey("Inventory")) {
					tile.selectedSchematic = stack.stackTagCompound.getByte("Mode");
					tile.readInventoryFromNBT(stack.stackTagCompound);
				}
			} else if (aTile instanceof TileAugmentable) {
				TileAugmentable tile = (TileAugmentable) world.getTileEntity(x, y, z);

				tile.readAugmentsFromNBT(stack.stackTagCompound);
				tile.installAugments();
				tile.setEnergyStored(stack.stackTagCompound.getInteger("Energy"));

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
		}
		super.onBlockPlacedBy(world, x, y, z, living, stack);
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
			// TODO: this is very temporary; pump-hiding
			if (i == Types.PUMP.ordinal()) {
				continue;
			}
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
			tag.setString("Owner", theTile.getOwnerName());
			tag.setByte("Access", (byte) theTile.getAccess().ordinal());
			tag.setByte("Mode", (byte) theTile.selectedSchematic);

			theTile.writeInventoryToNBT(tag);
		} else if (tile instanceof TileAugmentable) {
			TileAugmentable theTile = (TileAugmentable) world.getTileEntity(x, y, z);

			if (tag == null) {
				tag = new NBTTagCompound();
			}
			ReconfigurableHelper.setItemStackTagReconfig(tag, theTile);
			tag.setInteger("Energy", theTile.getEnergyStored(ForgeDirection.UNKNOWN));

			theTile.writeAugmentsToNBT(tag);
		}
		return tag;
	}

	/* IDismantleable */
	@Override
	public ArrayList<ItemStack> dismantleBlock(EntityPlayer player, World world, int x, int y, int z, boolean returnDrops) {

		NBTTagCompound tag = getItemStackTag(world, x, y, z);

		TileEntity tile = world.getTileEntity(x, y, z);
		if (tile instanceof TileWorkbench) {
			((TileWorkbench) tile).inventory = new ItemStack[((TileWorkbench) tile).inventory.length];
		} else if (tile instanceof TileAugmentable) {
			if (tag == null) {
				tag = new NBTTagCompound();
			}
			TileAugmentable theTile = (TileAugmentable) tile;

			ReconfigurableHelper.setItemStackTagReconfig(tag, theTile);
			tag.setInteger("Energy", theTile.getEnergyStored(ForgeDirection.UNKNOWN));
			theTile.writeAugmentsToNBT(tag);
		}
		return super.dismantleBlock(player, tag, world, x, y, z, returnDrops, false);
	}

	/* IInitializer */
	@Override
	public boolean initialize() {

		TileWorkbench.initialize();
		// TileLexicon.initialize();
		TileActivator.initialize();
		TileBreaker.initialize();
		// TilePump.initialize();
		TileNullifier.initialize();

		if (defaultAutoTransfer) {
			// defaultAugments[0] = ItemHelper.cloneStack(TEAugments.generalAutoTransfer);
		}
		if (defaultRedstoneControl) {
			defaultAugments[1] = ItemHelper.cloneStack(TEAugments.generalRedstoneControl);
		}
		if (defaultReconfigSides) {
			defaultAugments[2] = ItemHelper.cloneStack(TEAugments.generalReconfigSides);
		}
		workbench = new ItemStack(this, 1, Types.WORKBENCH.ordinal());
		// lexicon = new ItemStack(this, 1, Types.LEXICON.ordinal());
		activator = ItemBlockDevice.setDefaultTag(new ItemStack(this, 1, Types.ACTIVATOR.ordinal()));
		breaker = ItemBlockDevice.setDefaultTag(new ItemStack(this, 1, Types.BREAKER.ordinal()));
		// pump = new ItemStack(this, 1, Types.PUMP.ordinal());
		nullifier = ItemBlockDevice.setDefaultTag(new ItemStack(this, 1, Types.NULLIFIER.ordinal()));

		GameRegistry.registerCustomItemStack("workbench", workbench);
		// GameRegistry.registerCustomItemStack("lexicon", lexicon);
		GameRegistry.registerCustomItemStack("activator", activator);
		GameRegistry.registerCustomItemStack("breaker", breaker);
		// GameRegistry.registerCustomItemStack("pump", pump);
		GameRegistry.registerCustomItemStack("nullifier", nullifier);

		return true;
	}

	@Override
	public boolean postInit() {

		String category = "tweak.recipe";
		breakerUseDiamondPickaxe = ThermalExpansion.config.get(category, "Breaker.UseDiamondPickaxe", false);

		ItemStack pickaxe = breakerUseDiamondPickaxe ? new ItemStack(Items.diamond_pickaxe) : TEEquipment.toolInvarPickaxe;

		String tinPart = "thermalexpansion:machineTin";

		if (enable[Types.WORKBENCH.ordinal()]) {
			GameRegistry.addRecipe(new RecipeUpgrade(7, workbench, new Object[] { " X ", "ICI", " P ", 'C', Blocks.crafting_table, 'I', "ingotCopper", 'P',
					BlockStrongbox.strongboxBasic, 'X', Items.paper }));
			GameRegistry.addRecipe(new ShapedOreRecipe(workbench, new Object[] { "YXY", "ICI", "YPY", 'C', Blocks.crafting_table, 'I', "ingotCopper", 'P',
					Blocks.chest, 'X', Items.paper, 'Y', "ingotTin" }));
		}
		if (enable[Types.LEXICON.ordinal()]) {

		}
		if (enable[Types.ACTIVATOR.ordinal()]) {
			GameRegistry.addRecipe(new RecipeAugmentable(activator, defaultAugments, new Object[] { " X ", "ICI", " P ", 'C', Blocks.piston, 'I', tinPart, 'P',
					TEItems.pneumaticServo, 'X', Blocks.chest }));
		}
		if (enable[Types.BREAKER.ordinal()]) {
			GameRegistry.addRecipe(new RecipeAugmentable(breaker, defaultAugments, new Object[] { " X ", "ICI", " P ", 'C', Blocks.piston, 'I', tinPart, 'P',
					TEItems.pneumaticServo, 'X', pickaxe }));
		}
		if (enable[Types.PUMP.ordinal()]) {

		}
		if (enable[Types.NULLIFIER.ordinal()]) {
			GameRegistry.addRecipe(new RecipeAugmentable(nullifier, defaultAugments, new Object[] { " X ", "ICI", " P ", 'C', Items.lava_bucket, 'I', tinPart,
					'P', TEItems.pneumaticServo, 'X', "ingotInvar" }));
		}
		TECraftingHandler.addSecureRecipe(workbench);
		// TECraftingHandler.addSecureRecipe(lexicon);
		TECraftingHandler.addSecureRecipe(activator);
		TECraftingHandler.addSecureRecipe(breaker);
		// TECraftingHandler.addSecureRecipe(pump);
		TECraftingHandler.addSecureRecipe(nullifier);

		return true;
	}

	public static void refreshItemStacks() {

		// lexicon = new ItemStack(this, 1, Types.LEXICON.ordinal());
		activator = ItemBlockDevice.setDefaultTag(activator);
		breaker = ItemBlockDevice.setDefaultTag(breaker);
		// pump = new ItemStack(this, 1, Types.PUMP.ordinal());
		nullifier = ItemBlockDevice.setDefaultTag(nullifier);
	}

	public static enum Types {
		WORKBENCH, LEXICON, ACTIVATOR, BREAKER, PUMP, NULLIFIER
	}

	public static String[] NAMES = { "workbench", "lexicon", "activator", "breaker", "pump", "nullifier" };
	public static boolean[] enable = new boolean[Types.values().length];
	public static ItemStack[] defaultAugments = new ItemStack[3];

	public static boolean defaultAutoTransfer = true;
	public static boolean defaultRedstoneControl = true;
	public static boolean defaultReconfigSides = true;

	public static boolean breakerUseDiamondPickaxe = false;

	static {
		String category = "block.device";
		enable[Types.WORKBENCH.ordinal()] = ThermalExpansion.config.get(category, "Workbench", true);
		enable[Types.ACTIVATOR.ordinal()] = ThermalExpansion.config.get(category, "Activator", true);
		enable[Types.BREAKER.ordinal()] = ThermalExpansion.config.get(category, "Breaker", true);
		enable[Types.NULLIFIER.ordinal()] = ThermalExpansion.config.get(category, "Nullifier", true);
	}

	public static ItemStack workbench;
	public static ItemStack lexicon;
	public static ItemStack activator;
	public static ItemStack breaker;
	public static ItemStack pump;
	public static ItemStack nullifier;

}
