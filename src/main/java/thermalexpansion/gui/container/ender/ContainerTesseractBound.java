package thermalexpansion.gui.container.ender;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.tileentity.TileEntity;

import thermalexpansion.block.ender.TileTesseractBound;
import thermalexpansion.gui.container.ContainerTEBase;

public class ContainerTesseractBound extends ContainerTEBase {

	TileTesseractBound myTile;

	public ContainerTesseractBound(InventoryPlayer inventory, TileEntity entity) {

		super(entity);
	}

}
