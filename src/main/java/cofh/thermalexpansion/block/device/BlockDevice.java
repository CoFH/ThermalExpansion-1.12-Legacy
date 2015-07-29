package cofh.thermalexpansion.block.device;

import cofh.api.tileentity.ISidedTexture;
import cofh.core.render.IconRegistry;
import cofh.core.util.crafting.RecipeAugmentable;
import cofh.lib.util.helpers.BlockHelper;
import cofh.lib.util.helpers.ItemHelper;
import cofh.lib.util.helpers.StringHelper;
import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalexpansion.block.BlockTEBase;
import cofh.thermalexpansion.block.TileAugmentable;
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
		case WORKBENCH_FALSE:
			return new TileWorkbenchFalse();
		case COLLECTOR:
			return new TileCollector();
		case ACTIVATOR:
			return new TileActivator();
		case BREAKER:
			return new TileBreaker();
		case EXTENDER:
			return new TileExtender();
		case NULLIFIER:
			return new TileNullifier();
		case BUFFER:
			return new TileBuffer();
		default:
			return null;
		}
	}

	@Override
	public void getSubBlocks(Item item, CreativeTabs tab, List list) {

		for (int i = 0; i < Types.values().length; i++) {
			if (enable[i]) {
				list.add(ItemBlockDevice.setDefaultTag(new ItemStack(item, 1, i)));
			}
		}
	}

	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase living, ItemStack stack) {

		if (stack.stackTagCompound != null) {
			TileEntity aTile = world.getTileEntity(x, y, z);

			if (aTile instanceof TileAugmentable) {
				TileAugmentable tile = (TileAugmentable) world.getTileEntity(x, y, z);

				tile.readAugmentsFromNBT(stack.stackTagCompound);
				tile.installAugments();
				tile.setEnergyStored(stack.stackTagCompound.getInteger("Energy"));

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
		super.onBlockPlacedBy(world, x, y, z, living, stack);
	}

	@Override
	public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity entity) {

		// TileDeviceBase tile = (TileDeviceBase) world.getTileEntity(x, y, z);
		// if (tile == null) {
		// return;
		// }
		// tile.onEntityCollidedWithBlock(entity);
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

		if (metadata == Types.WORKBENCH_FALSE.ordinal()) {
			if (side == 0) {
				return IconRegistry.getIcon("WorkbenchBottom", 1);
			} else if (side == 1) {
				return IconRegistry.getIcon("WorkbenchTop", 1);
			}
			return IconRegistry.getIcon("WorkbenchSide", 1);
		}
		return side != 3 ? deviceSide : deviceFace[metadata % Types.values().length];
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister ir) {

		deviceSide = ir.registerIcon("thermalexpansion:device/Device_Side");

		// Face Textures
		for (int i = 0; i < Types.values().length; i++) {
			if (i == 0 || i == 1 || i == 4) {
				continue;
			}
			deviceFace[i] = ir.registerIcon("thermalexpansion:device/Device_Face_" + StringHelper.titleCase(NAMES[i]));
			deviceActive[i] = ir.registerIcon("thermalexpansion:device/Device_Active_" + StringHelper.titleCase(NAMES[i]));
		}
	}

	@Override
	public NBTTagCompound getItemStackTag(World world, int x, int y, int z) {

		NBTTagCompound tag = super.getItemStackTag(world, x, y, z);
		TileEntity tile = world.getTileEntity(x, y, z);

		if (tile instanceof TileAugmentable) {
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
		if (tile instanceof TileAugmentable) {
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

		TileDeviceBase.configure();
		// TODO: Remove Workbench :)
		TileWorkbenchFalse.initialize();
		TileCollector.initialize();
		TileActivator.initialize();
		TileBreaker.initialize();
		TileExtender.initialize();
		TileNullifier.initialize();
		TileBuffer.initialize();

		if (defaultAutoTransfer) {
			// defaultAugments[0] = ItemHelper.cloneStack(TEAugments.generalAutoTransfer);
		}
		if (defaultRedstoneControl) {
			defaultAugments[1] = ItemHelper.cloneStack(TEAugments.generalRedstoneControl);
		}
		if (defaultReconfigSides) {
			defaultAugments[2] = ItemHelper.cloneStack(TEAugments.generalReconfigSides);
		}
		// collector = ItemBlockDevice.setDefaultTag(new ItemStack(this, 1, Types.COLLECTOR.ordinal()));
		activator = ItemBlockDevice.setDefaultTag(new ItemStack(this, 1, Types.ACTIVATOR.ordinal()));
		breaker = ItemBlockDevice.setDefaultTag(new ItemStack(this, 1, Types.BREAKER.ordinal()));
		// extender = ItemBlockDevice.setDefaultTag(new ItemStack(this, 1, Types.EXTENDER.ordinal()));
		nullifier = ItemBlockDevice.setDefaultTag(new ItemStack(this, 1, Types.NULLIFIER.ordinal()));
		buffer = ItemBlockDevice.setDefaultTag(new ItemStack(this, 1, Types.BUFFER.ordinal()));

		// GameRegistry.registerCustomItemStack("collector", collector);
		GameRegistry.registerCustomItemStack("activator", activator);
		GameRegistry.registerCustomItemStack("breaker", breaker);
		// GameRegistry.registerCustomItemStack("extender", extender);
		GameRegistry.registerCustomItemStack("nullifier", nullifier);
		GameRegistry.registerCustomItemStack("buffer", buffer);

		return true;
	}

	@Override
	public boolean postInit() {

		String category = "Device.Breaker";
		String comment = "If enabled, The Block Breaker will require a Diamond Pickaxe instead of an Invar Pickaxe.";
		boolean breakerDiamondPickaxe = ThermalExpansion.config.get(category, "Recipe.RequireDiamondPickaxe", false, comment);
		ItemStack pickaxe = breakerDiamondPickaxe ? new ItemStack(Items.diamond_pickaxe) : TEEquipment.toolInvarPickaxe;

		String tinPart = "thermalexpansion:machineTin";

		// @formatter:off
		if (enable[Types.COLLECTOR.ordinal()]) {
//			GameRegistry.addRecipe(new RecipeAugmentable(collector, defaultAugments, new Object[] {
//					" X ",
//					"ICI",
//					" P ",
//					'C', Blocks.piston,
//					'I', tinPart,
//					'P', TEItems.pneumaticServo,
//					'X', Blocks.hopper
//			}));
		}
		if (enable[Types.ACTIVATOR.ordinal()]) {
			GameRegistry.addRecipe(new RecipeAugmentable(activator, defaultAugments, new Object[] {
					" X ",
					"ICI",
					" P ",
					'C', Blocks.piston,
					'I', tinPart,
					'P', TEItems.powerCoilGold,
					'X', Blocks.chest
			}));
		}
		if (enable[Types.BREAKER.ordinal()]) {
			GameRegistry.addRecipe(new RecipeAugmentable(breaker, defaultAugments, new Object[] {
					" X ",
					"ICI",
					" P ",
					'C', Blocks.piston,
					'I', tinPart,
					'P', TEItems.pneumaticServo,
					'X', pickaxe
			}));
		}
		if (enable[Types.EXTENDER.ordinal()]) {

		}
		if (enable[Types.NULLIFIER.ordinal()]) {
			GameRegistry.addRecipe(new RecipeAugmentable(nullifier, defaultAugments, new Object[] {
					" X ",
					"ICI",
					" P ",
					'C', Items.lava_bucket,
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
					'C', Blocks.hopper,
					'I', tinPart,
					'P', TEItems.pneumaticServo,
					'X', "gearCopper"
			}));
		}
		// @formatter:on

		// TECraftingHandler.addSecureRecipe(collector);
		TECraftingHandler.addSecureRecipe(activator);
		TECraftingHandler.addSecureRecipe(breaker);
		// TECraftingHandler.addSecureRecipe(extender);
		TECraftingHandler.addSecureRecipe(nullifier);
		TECraftingHandler.addSecureRecipe(buffer);

		return true;
	}

	public static void refreshItemStacks() {

		// collector = ItemBlockDevice.setDefaultTag(collector);
		activator = ItemBlockDevice.setDefaultTag(activator);
		breaker = ItemBlockDevice.setDefaultTag(breaker);
		// extender = ItemBlockDevice.setDefaultTag(extender);
		nullifier = ItemBlockDevice.setDefaultTag(nullifier);
		buffer = ItemBlockDevice.setDefaultTag(buffer);
	}

	public static enum Types {
		WORKBENCH_FALSE, COLLECTOR, ACTIVATOR, BREAKER, EXTENDER, NULLIFIER, BUFFER
	}

	public static IIcon deviceSide;

	public static IIcon[] deviceFace = new IIcon[Types.values().length];
	public static IIcon[] deviceActive = new IIcon[Types.values().length];

	public static final String[] NAMES = { "workbench", "collector", "activator", "breaker", "extender", "nullifier", "buffer" };
	public static boolean[] enable = new boolean[Types.values().length];
	public static ItemStack[] defaultAugments = new ItemStack[3];

	public static boolean defaultAutoTransfer = true;
	public static boolean defaultRedstoneControl = true;
	public static boolean defaultReconfigSides = true;

	static {
		String category = "Device.";

		for (int i = 0; i < Types.values().length; i++) {
			enable[i] = ThermalExpansion.config.get(category + StringHelper.titleCase(NAMES[i]), "Recipe.Enable", true);
		}
		enable[Types.WORKBENCH_FALSE.ordinal()] = false;
		enable[Types.COLLECTOR.ordinal()] = false;
		enable[Types.EXTENDER.ordinal()] = false;
		ThermalExpansion.config.removeCategory(category + StringHelper.titleCase(NAMES[Types.WORKBENCH_FALSE.ordinal()]));
		ThermalExpansion.config.removeCategory(category + StringHelper.titleCase(NAMES[Types.COLLECTOR.ordinal()]));
		ThermalExpansion.config.removeCategory(category + StringHelper.titleCase(NAMES[Types.EXTENDER.ordinal()]));
		// ThermalExpansion.config.removeCategory(category + StringHelper.titleCase(NAMES[Types.BUFFER.ordinal()]));
	}

	public static ItemStack collector;
	public static ItemStack activator;
	public static ItemStack breaker;
	public static ItemStack extender;
	public static ItemStack nullifier;
	public static ItemStack buffer;

}
