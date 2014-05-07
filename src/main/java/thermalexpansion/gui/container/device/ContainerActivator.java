package thermalexpansion.gui.container.device;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.tileentity.TileEntity;
import thermalexpansion.block.device.TileActivator;
import thermalexpansion.gui.container.ContainerTEBase;

public class ContainerActivator extends ContainerTEBase {

	TileActivator myTile;
	public InventoryPlayer playerInv;

	public ContainerActivator(InventoryPlayer inventory, TileEntity entity) {

		super(entity);

		myTile = (TileActivator) entity;
		playerInv = inventory;

		for (int i = 0; i < 3; ++i) {
			for (int j = 0; j < 3; ++j) {
				addSlotToContainer(new Slot(myTile, j + i * 3, 62 + j * 18, 17 + i * 18));
			}
		}
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 9; j++) {
				addSlotToContainer(new Slot(inventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
			}
		}
		for (int i = 0; i < 9; i++) {
			addSlotToContainer(new Slot(inventory, i, 8 + i * 18, 142));
		}
	}

}
