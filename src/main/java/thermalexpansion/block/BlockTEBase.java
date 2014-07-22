package thermalexpansion.block;

import cofh.api.block.IDismantleable;
import cofh.api.tileentity.IRedstoneControl;
import cofh.api.tileentity.ISecurable;
import cofh.block.BlockCoFHBase;
import cofh.block.TileCoFHBase;
import cofh.util.CoreUtils;
import cofh.util.ItemHelper;
import cofh.util.RedstoneControlHelper;
import cofh.util.SecurityHelper;
import cofh.util.ServerHelper;

import java.util.ArrayList;

import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

import thermalexpansion.ThermalExpansion;
import thermalexpansion.block.simple.BlockGlass;
import thermalexpansion.util.Utils;

public abstract class BlockTEBase extends BlockCoFHBase implements IDismantleable {

	public BlockTEBase(Material material) {

		super(material);
		setStepSound(soundTypeStone);
		setCreativeTab(ThermalExpansion.tabBlocks);
	}

	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase living, ItemStack stack) {

		TileEntity tile = world.getTileEntity(x, y, z);

		if (tile instanceof TileTEBase) {
			((TileTEBase) tile).setInvName(ItemHelper.getNameFromItemStack(stack));
		}
		super.onBlockPlacedBy(world, x, y, z, living, stack);
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int hitSide, float hitX, float hitY, float hitZ) {

		if (Utils.isHoldingMultimeter(player, x, y, z)) {
			return true;
		}

		if (player.isSneaking()) { // TODO: remove? wrenches actively check this now
			if (Utils.isHoldingUsableWrench(player, x, y, z)) {
				if (ServerHelper.isServerWorld(world) && canDismantle(player, world, x, y, z)) {
					dismantleBlock(player, world, x, y, z, false);
				}
				Utils.usedWrench(player, x, y, z);
				return true;
			}
			return false;
		}
		TileTEBase tile = (TileTEBase) world.getTileEntity(x, y, z);

		if (tile == null) {
			return false;
		}
		if (Utils.isHoldingUsableWrench(player, x, y, z)) {
			if (ServerHelper.isServerWorld(world)) {
				tile.onWrench(player, hitSide);
			}
			Utils.usedWrench(player, x, y, z);
			return true;
		}
		return tile.openGui(player);
	}

	@Override
	public IIcon getIcon(int side, int metadata) {

		return BlockGlass.TEXTURE;
	}

	@Override
	public NBTTagCompound getItemStackTag(World world, int x, int y, int z) {

		TileEntity tile = world.getTileEntity(x, y, z);

		NBTTagCompound retTag = null;

		if (tile instanceof TileTEBase && (!((TileTEBase) tile).tileName.isEmpty())) {
			retTag = ItemHelper.setItemStackTagName(retTag, ((TileTEBase) tile).tileName);
		}
		if (tile instanceof TileInventory && ((TileInventory) tile).isSecured()) {
			retTag = SecurityHelper.setItemStackTagSecure(retTag, (ISecurable) tile);
		}
		if (tile instanceof IRedstoneControl) {
			retTag = RedstoneControlHelper.setItemStackTagRS(retTag, (IRedstoneControl) tile);
		}
		return retTag;
	}

	/* Dismantle Helper */
	@Override
	public ArrayList<ItemStack> dismantleBlock(EntityPlayer player, NBTTagCompound nbt, World world, int x, int y, int z, boolean returnDrops, boolean simulate) {

		TileEntity tile = world.getTileEntity(x, y, z);
		int bMeta = world.getBlockMetadata(x, y, z);

		// if (tile instanceof TileCoFHBase) {
		// bMeta = ((TileCoFHBase) tile).getType();
		// }
		ItemStack dropBlock = new ItemStack(this, 1, bMeta);

		if (nbt != null) {
			dropBlock.setTagCompound(nbt);
		}
		if (!simulate) {
			if (tile instanceof TileCoFHBase) {
				((TileCoFHBase) tile).blockDismantled();
			}
			world.setBlockToAir(x, y, z);

			if (!returnDrops) {
				float f = 0.3F;
				double x2 = world.rand.nextFloat() * f + (1.0F - f) * 0.5D;
				double y2 = world.rand.nextFloat() * f + (1.0F - f) * 0.5D;
				double z2 = world.rand.nextFloat() * f + (1.0F - f) * 0.5D;
				EntityItem item = new EntityItem(world, x + x2, y + y2, z + z2, dropBlock);
				item.delayBeforeCanPickup = 10;
				world.spawnEntityInWorld(item);

				if (player != null) {
					CoreUtils.dismantleLog(player.getDisplayName(), this, bMeta, x, y, z);
				}
			}
		}
		ArrayList<ItemStack> ret = new ArrayList<ItemStack>();
		ret.add(dropBlock);
		return ret;
	}

}
