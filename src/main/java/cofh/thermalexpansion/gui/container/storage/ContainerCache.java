package cofh.thermalexpansion.gui.container.storage;

import cofh.core.gui.container.ContainerTileAugmentable;
import cofh.thermalexpansion.block.storage.TileCache;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.tileentity.TileEntity;

public class ContainerCache extends ContainerTileAugmentable {

	TileCache myTile;

	public ContainerCache(InventoryPlayer inventory, TileEntity tile) {

		super(inventory, tile);

		myTile = (TileCache) tile;

		/* Custom Inventory */
		// addSlotToContainer(new SlotCustomInventory(myTile, 0, null, 0, 44, 24, false));
	}

}
