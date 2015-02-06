package cofh.thermalexpansion.gui.container.ender;

import cofh.thermalexpansion.block.ender.TileTesseractBound;
import cofh.thermalexpansion.gui.container.ContainerTEBase;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.tileentity.TileEntity;


public class ContainerTesseractBound extends ContainerTEBase {

	TileTesseractBound myTile;

	public ContainerTesseractBound(InventoryPlayer inventory, TileEntity tile) {

		super(tile);
	}

}
