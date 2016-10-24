package cofh.thermalexpansion.block.strongbox;

import static cofh.lib.util.helpers.ItemHelper.ShapedRecipe;

import codechicken.lib.item.ItemStackRegistry;
import cofh.core.enchantment.CoFHEnchantment;
import cofh.core.util.CoreUtils;
import cofh.core.util.crafting.RecipeUpgrade;
import cofh.lib.util.helpers.StringHelper;
import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalexpansion.block.BlockTEBase;
import cofh.thermalexpansion.block.EnumType;
import cofh.thermalexpansion.block.TileInventory;
import cofh.thermalexpansion.util.crafting.TECraftingHandler;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.common.registry.GameRegistry;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockStrongbox extends BlockTEBase {

    public static final PropertyEnum<EnumType> TYPES = PropertyEnum.create("type", EnumType.class);

	public BlockStrongbox() {

		super(Material.IRON);
		setHardness(20.0F);
		setResistance(120.0F);
		setUnlocalizedName("thermalexpansion.strongbox");
		//setBlockBounds(0.0625F, 0.0F, 0.0625F, 0.9375F, 0.875F, 0.9375F);
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
			return new TileStrongboxCreative(metadata);
		}
		return new TileStrongbox(metadata);
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
		if (getMetaFromState(state1) == 0 && !enable[0]) {
			world.setBlockToAir(pos);
			return;
		}
		if (stack.getTagCompound() != null) {
			TileStrongbox tile = (TileStrongbox) world.getTileEntity(pos);

			tile.enchant = (byte) EnchantmentHelper.getEnchantmentLevel(CoFHEnchantment.holding, stack);
			tile.createInventory();

			if (stack.getTagCompound().hasKey("Inventory")) {
				tile.readInventoryFromNBT(stack.getTagCompound());
			}
		}
		super.onBlockPlacedBy(world, pos, state, living, stack);
	}

    @Override
    public float getBlockHardness(IBlockState blockState, World world, BlockPos pos) {
		return HARDNESS[getMetaFromState(blockState)];
	}

    @Override
    public float getExplosionResistance(World world, BlockPos pos, Entity exploder, Explosion explosion) {
		return RESISTANCE[getMetaFromState(world.getBlockState(pos))];
	}

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.INVISIBLE;
    }

	@Override
	public NBTTagCompound getItemStackTag(IBlockAccess world, BlockPos pos) {

		NBTTagCompound tag = super.getItemStackTag(world, pos);
		TileStrongbox tile = (TileStrongbox) world.getTileEntity(pos);

		if (tile != null) {
			if (tag == null) {
				tag = new NBTTagCompound();
			}
			if (tile.enchant > 0) {
				CoFHEnchantment.addEnchantment(tag, CoFHEnchantment.holding, tile.enchant);
			}
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

		if (state.getValue(TYPES) == EnumType.CREATIVE && !CoreUtils.isOp(player)) {
			return false;
		}
		return super.canDismantle(player, world, pos);
	}

	/* IInitializer */
	@Override
	public boolean initialize() {

		TileStrongbox.initialize();
		TileStrongboxCreative.initialize();

		strongboxCreative = new ItemStack(this, 1, EnumType.CREATIVE.ordinal());
		strongboxBasic = new ItemStack(this, 1, EnumType.BASIC.ordinal());
		strongboxHardened = new ItemStack(this, 1, EnumType.HARDENED.ordinal());
		strongboxReinforced = new ItemStack(this, 1, EnumType.REINFORCED.ordinal());
		strongboxResonant = new ItemStack(this, 1, EnumType.RESONANT.ordinal());

		ItemStackRegistry.registerCustomItemStack("strongboxCreative", strongboxCreative);
        ItemStackRegistry.registerCustomItemStack("strongboxBasic", strongboxBasic);
        ItemStackRegistry.registerCustomItemStack("strongboxHardened", strongboxHardened);
        ItemStackRegistry.registerCustomItemStack("strongboxReinforced", strongboxReinforced);
        ItemStackRegistry.registerCustomItemStack("strongboxResonant", strongboxResonant);

		return true;
	}

	@Override
	public boolean postInit() {

		if (enable[EnumType.BASIC.ordinal()]) {
			GameRegistry.addRecipe(ShapedRecipe(strongboxBasic, " I ", "IXI", " I ", 'I', "ingotTin", 'X', Blocks.CHEST));
		}
		if (enable[EnumType.HARDENED.ordinal()]) {
			GameRegistry.addRecipe(new RecipeUpgrade(strongboxHardened, new Object[] { " I ", "IXI", " I ", 'I', "ingotInvar", 'X', strongboxBasic }));
			GameRegistry.addRecipe(ShapedRecipe(strongboxHardened, "IYI", "YXY", "IYI", 'I', "ingotInvar", 'X', Blocks.CHEST, 'Y', "ingotTin"));
		}
		if (enable[EnumType.REINFORCED.ordinal()]) {
			GameRegistry.addRecipe(new RecipeUpgrade(strongboxReinforced, new Object[] { " G ", "GXG", " G ", 'X', strongboxHardened, 'G', "blockGlassHardened" }));
		}
		if (enable[EnumType.RESONANT.ordinal()]) {
			GameRegistry.addRecipe(new RecipeUpgrade(strongboxResonant, new Object[] { " I ", "IXI", " I ", 'I', "ingotEnderium", 'X', strongboxReinforced }));
		}
		TECraftingHandler.addSecureRecipe(strongboxCreative);
		TECraftingHandler.addSecureRecipe(strongboxBasic);
		TECraftingHandler.addSecureRecipe(strongboxHardened);
		TECraftingHandler.addSecureRecipe(strongboxReinforced);
		TECraftingHandler.addSecureRecipe(strongboxResonant);
		return true;
	}

	public static final String[] NAMES = { "creative", "basic", "hardened", "reinforced", "resonant" };
	public static final float[] HARDNESS = { -1.0F, 5.0F, 15.0F, 20.0F, 20.0F };
	public static final int[] RESISTANCE = { 1200, 15, 90, 120, 120 };
	public static boolean[] enable = new boolean[EnumType.values().length];

	static {
		String category = "Strongbox.";

		enable[0] = ThermalExpansion.config.get(category + StringHelper.titleCase(NAMES[0]), "Enable", true);
		for (int i = 1; i < EnumType.values().length; i++) {
			enable[i] = ThermalExpansion.config.get(category + StringHelper.titleCase(NAMES[i]), "Recipe.Enable", true);
		}
	}

	public static ItemStack strongboxCreative;
	public static ItemStack strongboxBasic;
	public static ItemStack strongboxHardened;
	public static ItemStack strongboxReinforced;
	public static ItemStack strongboxResonant;

}
