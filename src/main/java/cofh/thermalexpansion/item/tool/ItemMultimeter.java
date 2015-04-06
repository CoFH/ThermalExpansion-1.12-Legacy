package cofh.thermalexpansion.item.tool;

import cofh.api.block.IBlockConfigGui;
import cofh.api.block.IBlockDebug;
import cofh.api.block.IBlockInfo;
import cofh.api.tileentity.ITileInfo;
import cofh.core.item.ItemBase;
import cofh.lib.util.helpers.ItemHelper;
import cofh.lib.util.helpers.ServerHelper;
import cofh.lib.util.helpers.StringHelper;
import cofh.thermalexpansion.ThermalExpansion;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class ItemMultimeter extends ItemBase {

	public ItemMultimeter() {

		super("thermalexpansion");
		setMaxStackSize(1);
		setCreativeTab(ThermalExpansion.tabTools);
	}

	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean check) {

		if (StringHelper.displayShiftForDetail && !StringHelper.isShiftKeyDown()) {
			list.add(StringHelper.shiftForDetails());
		}
		if (!StringHelper.isShiftKeyDown()) {
			return;
		}
		if (ItemHelper.getItemDamage(stack) == 0) {
			list.add(StringHelper.getInfoText("info.thermalexpansion.tool.multimeter.0"));
			list.add(StringHelper.getNoticeText("info.thermalexpansion.tool.multimeter.1"));
		}
	}

	@Override
	public boolean hasEffect(ItemStack stack) {

		return ItemHelper.getItemDamage(stack) != 0;
	}

	@Override
	public boolean isFull3D() {

		return false;
	}

	@Override
	public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int hitSide, float hitX, float hitY, float hitZ) {

		return false;
	}

	@Override
	public boolean onItemUseFirst(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int hitSide, float hitX, float hitY, float hitZ) {

		boolean r = doItemUse(stack, player, world, x, y, z, hitSide, hitX, hitY, hitZ);
		if (r) { // HACK: forge is fucking stupid with this method
			ServerHelper.sendItemUsePacket(stack, player, world, x, y, z, hitSide, hitX, hitY, hitZ);
		}
		return r;
	}

	public boolean doItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int hitSide, float hitX, float hitY, float hitZ) {

		player.swingItem();

		Block block = world.getBlock(x, y, z);
		ArrayList<IChatComponent> info = new ArrayList<IChatComponent>();

		if (ItemHelper.getItemDamage(stack) == 0) { // Multimeter
			if (ServerHelper.isClientWorld(world)) {
				if (block instanceof IBlockConfigGui || block instanceof IBlockInfo) {
					return true;
				}
				TileEntity theTile = world.getTileEntity(x, y, z);
				return theTile instanceof ITileInfo;
			}
			if (player.isSneaking() && block instanceof IBlockConfigGui) {
				if (((IBlockConfigGui) block).openConfigGui(world, x, y, z, ForgeDirection.VALID_DIRECTIONS[hitSide], player)) {
					return true;
				}
			}
			if (block instanceof IBlockInfo) {
				((IBlockInfo) (block)).getBlockInfo(world, x, y, z, ForgeDirection.VALID_DIRECTIONS[hitSide], player, info, false);

				for (int i = 0; i < info.size(); i++) {
					player.addChatMessage(info.get(i));
				}
				info.clear();
				return true;
			} else {
				TileEntity theTile = world.getTileEntity(x, y, z);
				if (theTile instanceof ITileInfo) {
					if (ServerHelper.isServerWorld(world)) {
						((ITileInfo) theTile).getTileInfo(info, ForgeDirection.UNKNOWN, player, false);
						for (int i = 0; i < info.size(); i++) {
							player.addChatMessage(info.get(i));
						}
					}
					info.clear();
					return true;
				}
			}
			info.clear();
			return false;
		} else { // Debugger
			if (player.isSneaking() && block instanceof IBlockDebug) {
				((IBlockDebug) (block)).debugBlock(world, x, y, z, ForgeDirection.VALID_DIRECTIONS[hitSide], player);
				return true;
			} else if (block instanceof IBlockInfo) {
				if (ServerHelper.isClientWorld(world)) {
					info.add(new ChatComponentText("-Client-"));
				} else {
					info.add(new ChatComponentText("-Server-"));
				}
				((IBlockInfo) (block)).getBlockInfo(world, x, y, z, ForgeDirection.VALID_DIRECTIONS[hitSide], player, info, true);
				for (int i = 0; i < info.size(); i++) {
					player.addChatMessage(info.get(i));
				}
				info.clear();
				return true;
			} else {
				TileEntity theTile = world.getTileEntity(x, y, z);
				if (theTile instanceof ITileInfo) {
					if (ServerHelper.isServerWorld(world)) {
						((ITileInfo) theTile).getTileInfo(info, ForgeDirection.UNKNOWN, player, player.isSneaking());
						for (int i = 0; i < info.size(); i++) {
							player.addChatMessage(info.get(i));
						}
					}
					info.clear();
					return true;
				}
			}
		}
		info.clear();
		return false;
	}

}
