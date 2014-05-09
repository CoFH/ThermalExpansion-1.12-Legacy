package thermalexpansion.gui.container.machine;

import cofh.gui.slot.SlotEnergy;
import cofh.gui.slot.SlotOutput;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

import thermalexpansion.block.machine.TileSmelter;
import thermalexpansion.gui.container.ContainerTEBase;
import thermalexpansion.util.crafting.SmelterManager;

public class ContainerSmelter extends ContainerTEBase {

	TileSmelter myTile;

	public ContainerSmelter(InventoryPlayer inventory, TileEntity entity) {

		super(entity);

		myTile = (TileSmelter) entity;
		addSlotToContainer(new Slot(myTile, 0, 32, 26));
		addSlotToContainer(new Slot(myTile, 1, 56, 26));
		addSlotToContainer(new SlotOutput(myTile, 2, 116, 26));
		addSlotToContainer(new SlotOutput(myTile, 3, 134, 26));
		addSlotToContainer(new SlotOutput(myTile, 4, 116, 53));
		addSlotToContainer(new SlotEnergy(myTile, 5, 8, 53));

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

			if (i == 2 || i == 3 || i == 4) {
				if (!mergeItemStack(stackInSlot, invTile, invFull, true)) {
					return null;
				}
			} else if (i != 0 && i != 1) {
				if (SmelterManager.isItemValid(stackInSlot)) {
					if (!mergeItemStack(stackInSlot, 0, 2, false)) {
						return null;
					}
				} else if (i >= invTile && i < invPlayer) {
					if (!mergeItemStack(stackInSlot, invPlayer, invFull, false)) {
						return null;
					}
				} else if (i >= invPlayer && i < invFull && !mergeItemStack(stackInSlot, invTile, invPlayer, false)) {
					return null;
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
