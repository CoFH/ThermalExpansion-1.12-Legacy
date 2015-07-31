package cofh.thermalexpansion.gui.container.plate;

import cofh.thermalexpansion.block.plate.TilePlateCharger;
import cofh.thermalexpansion.gui.container.ContainerTEBase;

import net.minecraft.entity.player.InventoryPlayer;


public class ContainerChargePlate extends ContainerTEBase {

	TilePlateCharger myTile;

	public ContainerChargePlate(InventoryPlayer inventory, TilePlateCharger tile) {

		super(inventory, tile, false, false);

		myTile = tile;

		// addSlotToContainer(new SlotEnergy(myTile, 0, 8, 53));
	}

}
