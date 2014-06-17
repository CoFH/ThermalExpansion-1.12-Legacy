package thermalexpansion.gui.container.machine;

import cofh.gui.slot.SlotCustomInventory;
import cofh.gui.slot.SlotRemoveOnly;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.tileentity.TileEntity;

import thermalexpansion.block.machine.TileExtruder;
import thermalexpansion.gui.container.ContainerTEBase;

public class ContainerExtruder extends ContainerTEBase {

	TileExtruder myTile;

	public ContainerExtruder(InventoryPlayer inventory, TileEntity entity) {

		super(inventory, entity);

		myTile = (TileExtruder) entity;
		addSlotToContainer(new SlotRemoveOnly(myTile, 0, 80, 49));

		/* Custom Inventory */
		addSlotToContainer(new SlotCustomInventory(myTile, 0, null, 0, 50, 19, false));
		addSlotToContainer(new SlotCustomInventory(myTile, 0, null, 1, 80, 19, false));
		addSlotToContainer(new SlotCustomInventory(myTile, 0, null, 2, 110, 19, false));
	}

}
