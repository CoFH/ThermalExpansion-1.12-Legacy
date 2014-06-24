package thermalexpansion.block.lamp;

import cofh.render.IconRegistry;
import cofh.util.ColorHelper;
import cofh.util.ItemHelper;
import cofh.util.ServerHelper;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import java.util.List;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import thermalexpansion.ThermalExpansion;
import thermalexpansion.block.BlockTEBase;
import thermalexpansion.core.TEProps;

public class BlockLamp extends BlockTEBase {

	public BlockLamp() {

		super(Material.glass);
		setHardness(3.0F);
		setResistance(150.0F);
		setStepSound(soundTypeGlass);
		setBlockName("thermalexpansion.lamp");
	}

	@Override
	public TileEntity createNewTileEntity(World world, int metadata) {

		return new TileLamp();
	}

	@Override
	public void getSubBlocks(Item item, CreativeTabs tab, List list) {

		if (enable) {
			list.add(new ItemStack(item, 1, 0));
		}
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int bSide, float hitX, float hitY, float hitZ) {

		TileLamp theTile = (TileLamp) world.getTileEntity(x, y, z);

		if (ItemHelper.isPlayerHoldingItem(Items.dye, player)) {
			if (ServerHelper.isServerWorld(world)) {
				theTile.setColor(ColorHelper.getDyeColor(player.getCurrentEquippedItem().getItemDamage()));

				if (!player.capabilities.isCreativeMode) {
					player.inventory.setInventorySlotContents(player.inventory.currentItem, ItemHelper.consumeItem(player.getCurrentEquippedItem()));
				}
				world.playSoundEffect(x + 0.5, y + 0.5, z + 0.5, "note.harp", 0.5F, 1.0F);
			}
			return true;
		}
		if (ItemHelper.isPlayerHoldingItem(Items.glowstone_dust, player)) {
			if (ServerHelper.isServerWorld(world)) {
				theTile.resetColor();

				if (!player.capabilities.isCreativeMode) {
					player.inventory.setInventorySlotContents(player.inventory.currentItem, ItemHelper.consumeItem(player.getCurrentEquippedItem()));
				}
				world.playSoundEffect(x + 0.5, y + 0.5, z + 0.5, "random.orb", 0.25F, 1.0F);
			}
			return true;
		}
		return super.onBlockActivated(world, x, y, z, player, bSide, hitX, hitY, hitZ);
	}

	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase living, ItemStack stack) {

		if (stack.stackTagCompound != null) {
			TileLamp theTile = (TileLamp) world.getTileEntity(x, y, z);
			theTile.modified = true;
			theTile.color = stack.stackTagCompound.getInteger("Color");
		}
		super.onBlockPlacedBy(world, x, y, z, living, stack);
	}

	@Override
	public void onNeighborChange(IBlockAccess world, int x, int y, int z, int tileX, int tileY, int tileZ) {

	}

	@Override
	public boolean canRenderInPass(int pass) {

		renderPass = pass;
		return pass < 2;
	}

	@Override
	public int getRenderBlockPass() {

		return 1;
	}

	@Override
	public int getRenderType() {

		return TEProps.renderIdLamp;
	}

	@Override
	public boolean isSideSolid(IBlockAccess world, int x, int y, int z, ForgeDirection side) {

		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister ir) {

		IconRegistry.addIcon("Lamp0", "thermalexpansion:lamp/Lamp_Basic", ir);
		IconRegistry.addIcon("LampHalo", "thermalexpansion:lamp/Lamp_Halo", ir);
	}

	@Override
	public NBTTagCompound getItemStackTag(World world, int x, int y, int z) {

		TileLamp theTile = (TileLamp) world.getTileEntity(x, y, z);
		NBTTagCompound tag = null;

		if (theTile != null && theTile.modified) {
			tag = new NBTTagCompound();
			tag.setInteger("Color", theTile.color);
		}
		return tag;
	}

	/* IInitializer */
	@Override
	public boolean initialize() {

		TileLamp.initialize();

		lamp = new ItemStack(this, 1, 0);

		GameRegistry.registerCustomItemStack("lamp", lamp);

		return true;
	}

	@Override
	public boolean postInit() {

		if (enable) {

		}
		return true;
	}

	public static boolean enable;

	static {
		String category = "block.feature";
		enable = ThermalExpansion.config.get(category, "Lamp.Enable", true);
	}

	public static ItemStack lamp;

}
