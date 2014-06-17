package thermalexpansion.gui;

import cofh.block.TileCoFHBase;
import cofh.util.ItemHelper;
import cpw.mods.fml.common.network.IGuiHandler;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import thermalexpansion.gui.client.GuiSatchel;
import thermalexpansion.gui.container.ContainerSatchel;
import thermalexpansion.item.tool.ItemSatchel;

public class GuiHandler implements IGuiHandler {

	@Override
	public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {

		switch (id) {
		case 0:
			TileEntity tile = world.getTileEntity(x, y, z);
			if (tile instanceof TileCoFHBase) {
				return ((TileCoFHBase) tile).getGuiClient(player.inventory);
			}
		case 1:
			if (ItemHelper.isPlayerHoldingItem(ItemSatchel.class, player)) {
				return new GuiSatchel(player.inventory, new ContainerSatchel(player.getCurrentEquippedItem(), player.inventory));
			}
		default:
			return null;
		}
	}

	@Override
	public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {

		switch (id) {
		case 0:
			TileEntity tile = world.getTileEntity(x, y, z);
			if (tile instanceof TileCoFHBase) {
				return ((TileCoFHBase) tile).getGuiServer(player.inventory);
			}
		case 1:
			if (ItemHelper.isPlayerHoldingItem(ItemSatchel.class, player)) {
				return new ContainerSatchel(player.getCurrentEquippedItem(), player.inventory);
			}
		default:
			return null;
		}
	}

}
