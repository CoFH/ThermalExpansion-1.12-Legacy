package thermalexpansion.item.tool;

import cofh.api.block.IBlockConfigGui;
import cofh.api.block.IBlockDebug;
import cofh.api.block.IBlockInfo;
import cofh.api.tileentity.ITileInfo;
import cofh.core.item.ItemBase;
import cofh.lib.util.helpers.ItemHelper;
import cofh.lib.util.helpers.ServerHelper;

import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import thermalexpansion.ThermalExpansion;

public class ItemMultimeter extends ItemBase {

	public ItemMultimeter() {

		super("thermalexpansion");
		setMaxStackSize(1);
		setCreativeTab(ThermalExpansion.tabTools);
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

		player.swingItem();

		Block block = world.getBlock(x, y, z);
		ArrayList<IChatComponent> info = new ArrayList<IChatComponent>();

		if (ItemHelper.getItemDamage(stack) == 0) {
			if (ServerHelper.isClientWorld(world)) {
				return false;
			}
			if (player.isSneaking() && block instanceof IBlockConfigGui) {
				if (((IBlockConfigGui)block).openConfigGui(world, x, y, z, ForgeDirection.VALID_DIRECTIONS[hitSide], player))
					return true;
			}
			if (block instanceof IBlockInfo) {
				((IBlockInfo) (block)).getBlockInfo(world, x, y, z, ForgeDirection.VALID_DIRECTIONS[hitSide], player, info, false);
				for (int i = 0; i < info.size(); i++) {
					player.addChatMessage(info.get(i));
				}
			} else {
				TileEntity theTile = world.getTileEntity(x, y, z);
				if (theTile instanceof ITileInfo) {
					if (ServerHelper.isServerWorld(world)) {
						((ITileInfo) theTile).getTileInfo(info, ForgeDirection.VALID_DIRECTIONS[hitSide], player, false);
						for (int i = 0; i < info.size(); i++) {
							player.addChatMessage(info.get(i));
						}
					}
				}
			}
		} else {
			if (player.isSneaking() && block instanceof IBlockDebug) {
				((IBlockDebug) (block)).debugBlock(world, x, y, z, ForgeDirection.VALID_DIRECTIONS[hitSide], player);

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
			} else {
				TileEntity theTile = world.getTileEntity(x, y, z);
				if (theTile instanceof ITileInfo) {
					if (ServerHelper.isServerWorld(world)) {
						((ITileInfo) theTile).getTileInfo(info, ForgeDirection.VALID_DIRECTIONS[hitSide], player, player.isSneaking());
						for (int i = 0; i < info.size(); i++) {
							player.addChatMessage(info.get(i));
						}
					}
				}
			}
		}
		info.clear();
		return false;
	}

}
