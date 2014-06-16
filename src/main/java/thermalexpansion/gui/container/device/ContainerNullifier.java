package thermalexpansion.gui.container.device;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.tileentity.TileEntity;

import thermalexpansion.block.device.TileNullifier;
import thermalexpansion.gui.container.ContainerTEBase;

public class ContainerNullifier extends ContainerTEBase {

	TileNullifier myTile;
	public InventoryPlayer playerInv;

	public ContainerNullifier(InventoryPlayer inventory, TileEntity entity) {

		super(entity);

		myTile = (TileNullifier) entity;
		playerInv = inventory;

		addPlayerSlotsToContainer(inventory);
		addSlotToContainer(new Slot(myTile, 0, 80, 26));

	}

	private void addPlayerSlotsToContainer(InventoryPlayer inventory) {

		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 9; j++) {
				addSlotToContainer(new Slot(inventory, j + i * 9 + 9, 8 + j * 18, 66 + i * 18));
			}
		}
		for (int i = 0; i < 9; i++) {
			addSlotToContainer(new Slot(inventory, i, 8 + i * 18, 124));
		}
	}

}
