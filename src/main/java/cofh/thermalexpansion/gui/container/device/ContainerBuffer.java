package cofh.thermalexpansion.gui.container.device;

import cofh.thermalexpansion.block.device.TileItemBuffer;
import cofh.thermalexpansion.gui.container.ContainerTEBase;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.tileentity.TileEntity;

public class ContainerBuffer extends ContainerTEBase {

	TileItemBuffer myTile;

	public ContainerBuffer(InventoryPlayer inventory, TileEntity tile) {

		super(inventory, tile);

		myTile = (TileItemBuffer) tile;

		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				addSlotToContainer(new Slot(myTile, j + i * 3, 62 + j * 18, 17 + i * 18));
			}
		}
	}

}
