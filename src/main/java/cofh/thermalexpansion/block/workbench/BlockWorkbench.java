package cofh.thermalexpansion.block.workbench;

import static cofh.lib.util.helpers.ItemHelper.ShapedRecipe;

import codechicken.lib.item.ItemStackRegistry;
import cofh.core.render.IconRegistry;
import cofh.core.util.CoreUtils;
import cofh.core.util.crafting.RecipeUpgrade;
import cofh.lib.util.helpers.StringHelper;
import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalexpansion.block.BlockTEBase;
import cofh.thermalexpansion.block.EnumType;
import cofh.thermalexpansion.block.TileInventory;
import cofh.thermalexpansion.block.strongbox.BlockStrongbox;
import cofh.thermalexpansion.util.crafting.TECraftingHandler;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraftforge.fml.common.registry.GameRegistry;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.material.Material;
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
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockWorkbench extends BlockTEBase {

    public static final PropertyEnum<EnumType> TYPES = PropertyEnum.create("type", EnumType.class);

	public BlockWorkbench() {

		super(Material.IRON);
		setHardness(15.0F);
		setResistance(25.0F);
		setUnlocalizedName("thermalexpansion.workbench");
	}

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(TYPES).ordinal();
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(TYPES, EnumType.fromMeta(meta));
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, TYPES);
    }

	@Override
	public TileEntity createNewTileEntity(World world, int metadata) {

		if (metadata >= EnumType.values().length) {
			return null;
		}
		if (metadata == EnumType.CREATIVE.ordinal()) {
			if (!enable[EnumType.CREATIVE.ordinal()]) {
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
		for (int i = 1; i < EnumType.values().length; i++) {
			list.add(new ItemStack(item, 1, i));
		}
	}

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase living, ItemStack stack) {
        IBlockState state1 = world.getBlockState(pos);
        if (state1.getBlock().getMetaFromState(state1) == 0 && !enable[0]) {
			world.setBlockToAir(pos);
			return;
		}
		if (stack.getTagCompound() != null) {
			TileWorkbench tile = (TileWorkbench) world.getTileEntity(pos);

			if (stack.getTagCompound().hasKey("Inventory")) {
				tile.readInventoryFromNBT(stack.getTagCompound());
			}
		}
		super.onBlockPlacedBy(world, pos, state, living, stack);
	}


    @Override
	public float getBlockHardness(IBlockState blockState, World world, BlockPos pos) {

		return HARDNESS[blockState.getBlock().getMetaFromState(blockState)];
	}

    @Override
    public float getExplosionResistance(World world, BlockPos pos, Entity exploder, Explosion explosion) {
        IBlockState state = world.getBlockState(pos);
		return RESISTANCE[state.getBlock().getMetaFromState(state)];
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

	//@Override
	//public IIcon getIcon(int side, int metadata) {
//
	//	if (side == 0) {
	//		return IconRegistry.getIcon("WorkbenchBottom", metadata);
	//	}
	//	if (side == 1) {
	//		return IconRegistry.getIcon("WorkbenchTop", metadata);
	//	}
	//	return IconRegistry.getIcon("WorkbenchSide", metadata);
	//}

	//@Override
	//@SideOnly(Side.CLIENT)
	//public void registerBlockIcons(IIconRegister ir) {
//
	//	for (int i = 0; i < Types.values().length; i++) {
	//		IconRegistry.addIcon("WorkbenchBottom" + i, "thermalexpansion:workbench/Workbench_" + StringHelper.titleCase(NAMES[i]) + "_Bottom", ir);
	//		IconRegistry.addIcon("WorkbenchTop" + i, "thermalexpansion:workbench/Workbench_" + StringHelper.titleCase(NAMES[i]) + "_Top", ir);
	//		IconRegistry.addIcon("WorkbenchSide" + i, "thermalexpansion:workbench/Workbench_" + StringHelper.titleCase(NAMES[i]) + "_Side", ir);
	//	}
	//}

	@Override
	public NBTTagCompound getItemStackTag(IBlockAccess world, BlockPos pos) {

		NBTTagCompound tag = super.getItemStackTag(world, pos);
		TileWorkbench tile = (TileWorkbench) world.getTileEntity(pos);

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
	public ArrayList<ItemStack> dismantleBlock(EntityPlayer player, World world, BlockPos pos, boolean returnDrops) {

		NBTTagCompound tag = getItemStackTag(world, pos);

		TileEntity tile = world.getTileEntity(pos);
		if (tile instanceof TileInventory) {
			((TileInventory) tile).inventory = new ItemStack[((TileInventory) tile).inventory.length];
		}
		return super.dismantleBlock(player, tag, world, pos, returnDrops, false);
	}

	@Override
	public boolean canDismantle(EntityPlayer player, World world, BlockPos pos) {
        IBlockState state = world.getBlockState(pos);
		if (state.getBlock().getMetaFromState(state) == EnumType.CREATIVE.ordinal() && !CoreUtils.isOp(player)) {
			return false;
		}
		return super.canDismantle(player, world, pos);
	}

	/* IInitializer */
	@Override
	public boolean initialize() {

		TileWorkbench.initialize();
		TileWorkbenchCreative.initialize();

		workbenchCreative = new ItemStack(this, 1, EnumType.CREATIVE.ordinal());
		workbenchBasic = new ItemStack(this, 1, EnumType.BASIC.ordinal());
		workbenchHardened = new ItemStack(this, 1, EnumType.HARDENED.ordinal());
		workbenchReinforced = new ItemStack(this, 1, EnumType.REINFORCED.ordinal());
		workbenchResonant = new ItemStack(this, 1, EnumType.RESONANT.ordinal());

		ItemStackRegistry.registerCustomItemStack("workbenchCreative", workbenchCreative);
        ItemStackRegistry.registerCustomItemStack("workbenchBasic", workbenchBasic);
        ItemStackRegistry.registerCustomItemStack("workbenchHardened", workbenchHardened);
        ItemStackRegistry.registerCustomItemStack("workbenchReinforced", workbenchReinforced);
        ItemStackRegistry.registerCustomItemStack("workbenchResonant", workbenchResonant);

		return true;
	}

	@Override
	public boolean postInit() {

		if (enable[EnumType.BASIC.ordinal()]) {
			GameRegistry.addRecipe(new RecipeUpgrade(7, workbenchBasic, new Object[] { " X ", "ICI", " P ", 'C', Blocks.CRAFTING_TABLE, 'I', "ingotCopper",
					'P', BlockStrongbox.strongboxBasic, 'X', Items.PAPER }));
			GameRegistry.addRecipe(ShapedRecipe(workbenchBasic, "YXY", "ICI", "YPY", 'C', Blocks.CRAFTING_TABLE, 'I', "ingotCopper", 'P', Blocks.CHEST, 'X', Items.PAPER, 'Y', "ingotTin"));
		}
		if (enable[EnumType.HARDENED.ordinal()]) {
			GameRegistry.addRecipe(new RecipeUpgrade(workbenchHardened, new Object[] { " I ", "IXI", " I ", 'I', "ingotInvar", 'X', workbenchBasic }));
			GameRegistry.addRecipe(new RecipeUpgrade(7, workbenchHardened, new Object[] { "IPI", "CTC", "IBI", 'T', Blocks.CRAFTING_TABLE, 'C', "ingotCopper",
					'B', BlockStrongbox.strongboxBasic, 'P', Items.PAPER, 'I', "ingotInvar" }));
		}
		if (enable[EnumType.REINFORCED.ordinal()]) {
			GameRegistry.addRecipe(new RecipeUpgrade(workbenchReinforced, new Object[] { " I ", "IXI", " I ", 'I', "ingotSignalum", 'X', workbenchHardened }));
		}
		if (enable[EnumType.RESONANT.ordinal()]) {
			GameRegistry.addRecipe(new RecipeUpgrade(workbenchResonant, new Object[] { " I ", "IXI", " I ", 'I', "ingotEnderium", 'X', workbenchReinforced }));
		}
		TECraftingHandler.addSecureRecipe(workbenchCreative);
		TECraftingHandler.addSecureRecipe(workbenchBasic);
		TECraftingHandler.addSecureRecipe(workbenchHardened);
		TECraftingHandler.addSecureRecipe(workbenchReinforced);
		TECraftingHandler.addSecureRecipe(workbenchResonant);

		return true;
	}

	public static final String[] NAMES = { "creative", "basic", "hardened", "reinforced", "resonant" };
	public static final float[] HARDNESS = { -1.0F, 5.0F, 15.0F, 20.0F, 20.0F };
	public static final int[] RESISTANCE = { 1200, 15, 90, 120, 120 };
	public static boolean[] enable = new boolean[EnumType.values().length];

	static {
		String category = "Workbench.";

		enable[0] = ThermalExpansion.config.get(category + StringHelper.titleCase(NAMES[0]), "Enable", true);
		for (int i = 1; i < EnumType.values().length; i++) {
			enable[i] = ThermalExpansion.config.get(category + StringHelper.titleCase(NAMES[i]), "Recipe.Enable", true);
		}
	}

	public static ItemStack workbenchCreative;
	public static ItemStack workbenchBasic;
	public static ItemStack workbenchHardened;
	public static ItemStack workbenchReinforced;
	public static ItemStack workbenchResonant;

}
