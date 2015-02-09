package cofh.thermalexpansion.gui.container.ender;

import cofh.thermalexpansion.block.ender.TileTesseract;
import cofh.thermalexpansion.gui.container.ContainerTEBase;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

public class ContainerTesseract extends ContainerTEBase {

	TileTesseract myTile;

	public ContainerTesseract(InventoryPlayer inventory, TileEntity tile) {

		super(tile);

		myTile = (TileTesseract) tile;
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int i) {

		return null;
	}

}
