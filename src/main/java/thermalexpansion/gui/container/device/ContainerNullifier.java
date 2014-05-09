package thermalexpansion.gui.container.device;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
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

		addSlotToContainer(new Slot(myTile, 0, 80, 26));
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 9; j++) {
				addSlotToContainer(new Slot(inventory, j + i * 9 + 9, 8 + j * 18, 66 + i * 18));
			}
		}
		for (int i = 0; i < 9; i++) {
			addSlotToContainer(new Slot(inventory, i, 8 + i * 18, 124));
		}
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int i) {

		ItemStack stack = null;
		Slot slot = (Slot) inventorySlots.get(i);

		int invTile = 1;
		int invPlayer = invTile + 27;
		int invFull = invTile + 36;

		if (slot != null && slot.getHasStack()) {
			ItemStack stackInSlot = slot.getStack();
			stack = stackInSlot.copy();

			if (i != 0) {
				if (i >= invTile && i < invPlayer) {

					if (!mergeItemStack(stackInSlot, 0, invTile, false)) {
						return null;
					}

				} else if (i >= invPlayer && i < invFull) {
					if (!mergeItemStack(stackInSlot, invTile, invPlayer, false)) {
						return null;
					}
				}
			} else if (!mergeItemStack(stackInSlot, invTile, invFull, false)) {
				return null;
			}
			if (stackInSlot.stackSize == 0) {
				slot.putStack((ItemStack) null);
			} else {
				slot.onSlotChanged();
			}
			if (stackInSlot.stackSize == stack.stackSize) {
				return null;
			}
			slot.onPickupFromSlot(player, stackInSlot);
		}
		return stack;
	}

}
