package thermalexpansion.gui.container.machine;

import cofh.gui.slot.SlotEnergy;
import cofh.gui.slot.SlotOutput;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.tileentity.TileEntity;

import thermalexpansion.block.machine.TileCharger;
import thermalexpansion.gui.container.ContainerTEBase;

public class ContainerCharger extends ContainerTEBase {

	TileCharger myTile;

	public ContainerCharger(InventoryPlayer inventory, TileEntity entity) {

		super(inventory, entity);

		myTile = (TileCharger) entity;
		addSlotToContainer(new SlotEnergy(myTile, 0, 35, 31));
		addSlotToContainer(new SlotOutput(myTile, 1, 125, 31));
	}

}
