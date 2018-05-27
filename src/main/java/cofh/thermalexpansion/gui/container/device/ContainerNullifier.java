package cofh.thermalexpansion.gui.container.device;

import cofh.core.gui.container.ContainerTileAugmentable;
import cofh.thermalexpansion.block.device.TileNullifier;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.tileentity.TileEntity;

public class ContainerNullifier extends ContainerTileAugmentable {

	TileNullifier myTile;

	public ContainerNullifier(InventoryPlayer inventory, TileEntity tile) {

		super(inventory, tile);

		myTile = (TileNullifier) tile;
		addSlotToContainer(new Slot(myTile, 0, 80, 26));
	}

}
