package thermalexpansion.block;

import cofh.api.tileentity.IRedstoneControl;
import cofh.api.tileentity.ISecurable;
import cofh.core.block.BlockCoFHBase;
import cofh.core.block.TileCoFHBase;
import cofh.core.util.CoreUtils;
import cofh.lib.util.helpers.ItemHelper;
import cofh.lib.util.helpers.RedstoneControlHelper;
import cofh.lib.util.helpers.SecurityHelper;
import cofh.lib.util.helpers.ServerHelper;
import cpw.mods.fml.common.eventhandler.Event.Result;

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
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.Action;

import thermalexpansion.ThermalExpansion;
import thermalexpansion.block.simple.BlockGlass;
import thermalexpansion.util.Utils;

public abstract class BlockTEBase extends BlockCoFHBase {

	protected boolean basicGui = true;

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

		PlayerInteractEvent event = new PlayerInteractEvent(player, Action.RIGHT_CLICK_BLOCK, x, y, z, hitSide, world);
		if (MinecraftForge.EVENT_BUS.post(event) || event.getResult() == Result.DENY || event.useBlock == Result.DENY) {
			return false;
		}
		if (Utils.isHoldingDebugger(player)) {
			return true;
		}
		if (Utils.isHoldingMultimeter(player)) {
			return true;
		}
		if (player.isSneaking()) {
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
		if (basicGui && ServerHelper.isServerWorld(world)) {
			return tile.openGui(player);
		}
		return true;
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
					CoreUtils.dismantleLog(player.getCommandSenderName(), this, bMeta, x, y, z);
				}
			}
		}
		ArrayList<ItemStack> ret = new ArrayList<ItemStack>();
		ret.add(dropBlock);
		return ret;
	}

}
