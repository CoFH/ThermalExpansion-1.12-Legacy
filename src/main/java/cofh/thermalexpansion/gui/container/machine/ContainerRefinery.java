package cofh.thermalexpansion.gui.container.machine;

import cofh.core.gui.container.ContainerTileAugmentable;
import cofh.core.gui.slot.SlotEnergy;
import cofh.core.gui.slot.SlotRemoveOnly;
import cofh.thermalexpansion.block.machine.TileRefinery;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.tileentity.TileEntity;

public class ContainerRefinery extends ContainerTileAugmentable {

	TileRefinery myTile;

	public ContainerRefinery(InventoryPlayer inventory, TileEntity tile) {

		super(inventory, tile);

		myTile = (TileRefinery) tile;
		addSlotToContainer(new SlotRemoveOnly(myTile, 0, 116, 35));
		addSlotToContainer(new SlotEnergy(myTile, myTile.getChargeSlot(), 8, 53));
	}

}
