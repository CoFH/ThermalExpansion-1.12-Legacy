package thermalexpansion.block.strongbox;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.oredict.ShapedOreRecipe;
import thermalexpansion.ThermalExpansion;
import thermalexpansion.block.BlockTEBase;
import thermalexpansion.block.TileInventory;
import cofh.api.tileentity.ISecureTile;
import cofh.core.CoFHProps;
import cofh.util.CoreUtils;
import cofh.util.UpgradeRecipe;
import cpw.mods.fml.common.registry.GameRegistry;

public class BlockStrongbox extends BlockTEBase {

	public BlockStrongbox() {

		super(Material.iron);
		setHardness(20.0F);
		setResistance(120.0F);
		setBlockName("thermalexpansion.strongbox");
		setBlockBounds(0.0625F, 0.0F, 0.0625F, 0.9375F, 0.875F, 0.9375F);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int metadata) {

		if (metadata >= Types.values().length) {
			return null;
		}
		if (metadata == Types.CREATIVE.ordinal()) {
			return new TileStrongboxCreative(metadata);
		}
		return new TileStrongbox(metadata);
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
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase living, ItemStack stack) {

		if (stack.stackTagCompound != null) {
			TileStrongbox tile = (TileStrongbox) world.getTileEntity(x, y, z);

			if (stack.stackTagCompound.hasKey("Owner")) {
				tile.setOwnerName(stack.stackTagCompound.getString("Owner"));
				tile.setAccess(ISecureTile.AccessMode.values()[stack.stackTagCompound.getByte("Access")]);
				tile.readInventoryFromNBT(stack.stackTagCompound);
			}
		}
		super.onBlockPlacedBy(world, x, y, z, living, stack);
	}

	@Override
	public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int metadata, int fortune) {

		ArrayList<ItemStack> ret = new ArrayList<ItemStack>();
		NBTTagCompound tag = null;

		if (!secureOwner.equals(CoFHProps.DEFAULT_OWNER)) {
			tag = new NBTTagCompound();
			tag.setString("Owner", secureOwner);
			tag.setByte("Access", secureAccess);
			secureOwner = CoFHProps.DEFAULT_OWNER;
			secureAccess = 0;
		}
		ItemStack retStack = new ItemStack(this, 1, damageDropped(metadata));
		retStack.setTagCompound(tag);
		ret.add(retStack);
		return ret;
	}

	@Override
	public float getBlockHardness(World world, int x, int y, int z) {

		return -1; // HARDNESS[world.getBlockMetadata(x, y, z)];
	}

	@Override
	public float getExplosionResistance(Entity entity, World world, int x, int y, int z, double explosionX, double explosionY, double explosionZ) {

		return RESISTANCE[world.getBlockMetadata(x, y, z)];
	}

	@Override
	public int getRenderType() {

		return -1;
	}

	@Override
	public NBTTagCompound getItemStackTag(World world, int x, int y, int z) {

		TileStrongbox tile = (TileStrongbox) world.getTileEntity(x, y, z);
		NBTTagCompound tag = super.getItemStackTag(world, x, y, z);

		if (tile != null) {
			if (tag == null) {
				tag = new NBTTagCompound();
			}
			tag.setString("Owner", tile.getOwnerName());
			tag.setByte("Access", (byte) tile.getAccess().ordinal());
			tile.writeInventoryToNBT(tag);
		}
		return tag;
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

		TileStrongbox.initialize();
		TileStrongboxCreative.initialize();

		strongboxCreative = new ItemStack(this, 1, Types.CREATIVE.ordinal());
		strongboxBasic = new ItemStack(this, 1, Types.BASIC.ordinal());
		strongboxHardened = new ItemStack(this, 1, Types.HARDENED.ordinal());
		strongboxReinforced = new ItemStack(this, 1, Types.REINFORCED.ordinal());
		strongboxResonant = new ItemStack(this, 1, Types.RESONANT.ordinal());

		GameRegistry.registerCustomItemStack("strongboxCreative", strongboxCreative);
		GameRegistry.registerCustomItemStack("strongboxBasic", strongboxBasic);
		GameRegistry.registerCustomItemStack("strongboxHardened", strongboxHardened);
		GameRegistry.registerCustomItemStack("strongboxReinforced", strongboxReinforced);
		GameRegistry.registerCustomItemStack("strongboxResonant", strongboxResonant);

		return true;
	}

	@Override
	public boolean postInit() {

		if (enable[Types.BASIC.ordinal()]) {
			GameRegistry.addRecipe(new ShapedOreRecipe(strongboxBasic, new Object[] { " I ", "IXI", " I ", 'I', "ingotTin", 'X', Blocks.chest }));
		}
		if (enable[Types.HARDENED.ordinal()]) {
			GameRegistry.addRecipe(new UpgradeRecipe(strongboxHardened, new Object[] { " I ", "IXI", " I ", 'I', "ingotInvar", 'X', strongboxBasic }));
			GameRegistry.addRecipe(new ShapedOreRecipe(strongboxHardened, new Object[] { "IYI", "YXY", "IYI", 'I', "ingotInvar", 'X', Blocks.chest, 'Y',
					"ingotTin" }));
		}
		if (enable[Types.REINFORCED.ordinal()]) {
			GameRegistry.addRecipe(new UpgradeRecipe(strongboxReinforced, new Object[] { " G ", "GXG", " G ", 'X', strongboxHardened, 'G', "glassHardened" }));
		}
		if (enable[Types.RESONANT.ordinal()]) {
			GameRegistry.addRecipe(new UpgradeRecipe(strongboxResonant, new Object[] { " I ", "IXI", " I ", 'I', "ingotEnderium", 'X', strongboxReinforced }));
		}
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
		String category = "block.feature";
		enable[Types.CREATIVE.ordinal()] = ThermalExpansion.config.get(category, "Strongbox.Creative", true);
		enable[Types.BASIC.ordinal()] = ThermalExpansion.config.get(category, "Strongbox.Basic", true);
		enable[Types.HARDENED.ordinal()] = ThermalExpansion.config.get(category, "Strongbox.Hardened", true);
		enable[Types.REINFORCED.ordinal()] = ThermalExpansion.config.get(category, "Strongbox.Reinforced", true);
		enable[Types.RESONANT.ordinal()] = ThermalExpansion.config.get(category, "Strongbox.Resonant", true);
	}

	public static ItemStack strongboxCreative;
	public static ItemStack strongboxBasic;
	public static ItemStack strongboxHardened;
	public static ItemStack strongboxReinforced;
	public static ItemStack strongboxResonant;

}
