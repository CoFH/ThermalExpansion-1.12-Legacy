package cofh.thermalexpansion.gui.container.device;

import cofh.lib.gui.slot.SlotCustomInventory;
import cofh.lib.gui.slot.SlotEnergy;
import cofh.lib.gui.slot.SlotRemoveOnly;
import cofh.thermalexpansion.block.device.TileLexicon;
import cofh.thermalexpansion.gui.container.ContainerTEBase;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.tileentity.TileEntity;

public class ContainerLexicon extends ContainerTEBase {

	TileLexicon myTile;

	public ContainerLexicon(InventoryPlayer inventory, TileEntity tile) {

		super(inventory, tile);

		myTile = (TileLexicon) tile;

		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 2; j++) {
				addSlotToContainer(new Slot(myTile, j + i * 2, 35 + j * 18, 17 + i * 18));
			}
		}
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 2; j++) {
				addSlotToContainer(new SlotRemoveOnly(myTile, 6 + j + i * 2, 125 + j * 18, 17 + i * 18));
			}
		}
		for (int i = 0; i < 9; i++) {
			addSlotToContainer(new SlotCustomInventory(myTile, 0, null, i, 8 + i * 18, 84, false));
		}
		Slot slot = new SlotEnergy(myTile, myTile.getChargeSlot(), 8, 53);
		if (myTile.getEnergyStorage().getMaxEnergyStored() > 0 || slot.getStack() != null) {
			addSlotToContainer(slot);
		}
	}

	@Override
	protected int getPlayerInventoryVerticalOffset() {

		return 115;
	}

}
