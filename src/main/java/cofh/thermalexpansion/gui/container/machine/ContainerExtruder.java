package cofh.thermalexpansion.gui.container.machine;

import cofh.core.gui.slot.SlotCustomInventory;
import cofh.core.gui.slot.SlotEnergy;
import cofh.core.gui.slot.SlotRemoveOnly;
import cofh.thermalexpansion.block.machine.TileExtruder;
import cofh.thermalexpansion.gui.container.ContainerTEBase;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.tileentity.TileEntity;

public class ContainerExtruder extends ContainerTEBase {

	TileExtruder myTile;

	public ContainerExtruder(InventoryPlayer inventory, TileEntity tile) {

		super(inventory, tile);

		myTile = (TileExtruder) tile;
		addSlotToContainer(new SlotRemoveOnly(myTile, 0, 134, 26));
		addSlotToContainer(new SlotEnergy(myTile, myTile.getChargeSlot(), 8, 53));

		/* Custom Inventory */
		addSlotToContainer(new SlotCustomInventory(myTile, 0, null, 0, 89, 53, false));
	}

}
