package cofh.thermalexpansion.gui.container.device;

import cofh.core.gui.container.ContainerTileAugmentable;
import cofh.thermalexpansion.block.device.TileItemBuffer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.tileentity.TileEntity;

public class ContainerItemBuffer extends ContainerTileAugmentable {

	TileItemBuffer myTile;

	public ContainerItemBuffer(InventoryPlayer inventory, TileEntity tile) {

		super(inventory, tile);

		myTile = (TileItemBuffer) tile;

		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				addSlotToContainer(new Slot(myTile, j + i * 3, 62 + j * 18, 17 + i * 18));
			}
		}
	}

}
