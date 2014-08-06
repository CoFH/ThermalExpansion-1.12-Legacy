package thermalexpansion.gui.container.machine;

import cofh.lib.gui.slot.SlotEnergy;
import cofh.lib.gui.slot.SlotRemoveOnly;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.tileentity.TileEntity;

import thermalexpansion.block.machine.TileCharger;
import thermalexpansion.gui.container.ContainerTEBase;

public class ContainerCharger extends ContainerTEBase {

	TileCharger myTile;

	public ContainerCharger(InventoryPlayer inventory, TileEntity tile) {

		super(inventory, tile);

		myTile = (TileCharger) tile;
		addSlotToContainer(new SlotEnergy(myTile, 0, 35, 31));
		addSlotToContainer(new SlotRemoveOnly(myTile, 1, 125, 31));
	}

}
