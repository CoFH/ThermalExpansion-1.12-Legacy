package thermalexpansion.gui.container.machine;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import thermalexpansion.block.machine.TileRockGen;
import thermalexpansion.gui.container.ContainerTEBase;
import cofh.gui.slot.SlotLocked;
import cofh.gui.slot.SlotOutput;

public class ContainerRockGen extends ContainerTEBase {

	TileRockGen myTile;

	public ContainerRockGen(InventoryPlayer inventory, TileEntity entity) {

		super(entity);

		myTile = (TileRockGen) entity;
		addSlotToContainer(new SlotOutput(myTile, 0, 80, 49));
		addSlotToContainer(new SlotLocked(myTile, 1, 50, 19));
		addSlotToContainer(new SlotLocked(myTile, 2, 80, 19));
		addSlotToContainer(new SlotLocked(myTile, 3, 110, 19));

		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 9; j++) {
				addSlotToContainer(new Slot(inventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
			}
		}
		for (int i = 0; i < 9; i++) {
			addSlotToContainer(new Slot(inventory, i, 8 + i * 18, 142));
		}
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int i) {

		ItemStack stack = null;
		Slot slot = (Slot) inventorySlots.get(i);

		int invTile = myTile.inventory.length;
		int invPlayer = invTile + 27;
		int invFull = invTile + 36;

		if (slot != null && slot.getHasStack()) {
			ItemStack stackInSlot = slot.getStack();
			stack = stackInSlot.copy();

			if (i == 0) {
				if (!mergeItemStack(stackInSlot, invTile, invFull, true)) {
					return null;
				}
			} else {
				if (i >= invTile && i < invPlayer) {
					if (!mergeItemStack(stackInSlot, invPlayer, invFull, false)) {
						return null;
					}
				} else if (i >= invPlayer && i < invFull && !mergeItemStack(stackInSlot, invTile, invPlayer, false)) {
					return null;
				}
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
