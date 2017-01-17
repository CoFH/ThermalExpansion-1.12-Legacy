package cofh.thermalexpansion.gui.container.device;

import cofh.thermalexpansion.block.device.TileNullifier;
import cofh.thermalexpansion.gui.container.ContainerTEBase;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.tileentity.TileEntity;

public class ContainerNullifier extends ContainerTEBase {

	TileNullifier myTile;

	public ContainerNullifier(InventoryPlayer inventory, TileEntity tile) {

		super(inventory, tile);

		myTile = (TileNullifier) tile;
		addSlotToContainer(new Slot(myTile, 0, 80, 26));
	}

}
