package cofh.thermalexpansion.block.device;

import codechicken.lib.item.ItemStackRegistry;


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
import cofh.thermalexpansion.util.crafting.TECraftingHandler;
import cofh.thermalexpansion.util.helpers.ReconfigurableHelper;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.registry.GameRegistry;



import java.util.ArrayList;
import java.util.List;

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


public class BlockDevice extends BlockTEBase {

	public BlockDevice() {

		super(Material.IRON);
		setHardness(15.0F);
		setResistance(25.0F);
		setUnlocalizedName("thermalexpansion.device");
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
	public void getSubBlocks(Item item, CreativeTabs tab, List list) {

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

	//@Override
	//public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity entity) {

		// TileDeviceBase tile = (TileDeviceBase) world.getTileEntity(x, y, z);
		// if (tile == null) {
		// return;
		// }
		// tile.onEntityCollidedWithBlock(entity);
	//}

	//@Override
	public int getRenderBlockPass() {

		return 1;
	}

	//@Override
	public boolean canRenderInPass(int pass) {

		renderPass = pass;
		return pass < 2;
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
	public boolean renderAsNormalBlock() {

		return true;
	}

	/*@Override
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
			if (i == Types.WORKBENCH_FALSE.ordinal() || i == Types.PUMP.ordinal() || i == Types.EXTENDER.ordinal()) {
				continue;
			}
			deviceFace[i] = ir.registerIcon("thermalexpansion:device/Device_Face_" + StringHelper.titleCase(NAMES[i]));
			deviceActive[i] = ir.registerIcon("thermalexpansion:device/Device_Active_" + StringHelper.titleCase(NAMES[i]));
		}
	}*/

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

	public enum Types {
		WORKBENCH_FALSE, PUMP, ACTIVATOR, BREAKER, COLLECTOR, NULLIFIER, BUFFER, EXTENDER
	}

	public static TextureAtlasSprite deviceSide;

	public static TextureAtlasSprite[] deviceFace = new TextureAtlasSprite[Types.values().length];
	public static TextureAtlasSprite[] deviceActive = new TextureAtlasSprite[Types.values().length];

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
