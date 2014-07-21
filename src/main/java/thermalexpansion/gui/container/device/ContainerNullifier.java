package thermalexpansion.gui.container.device;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.tileentity.TileEntity;

import thermalexpansion.block.device.TileNullifier;
import thermalexpansion.gui.container.ContainerTEBase;

public class ContainerNullifier extends ContainerTEBase {

	TileNullifier myTile;

	public ContainerNullifier(InventoryPlayer inventory, TileEntity tile) {

		super(inventory, tile);

		myTile = (TileNullifier) tile;
		addSlotToContainer(new Slot(myTile, 0, 80, 26));
	}

}
