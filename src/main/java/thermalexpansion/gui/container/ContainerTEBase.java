package thermalexpansion.gui.container;

import cofh.api.core.IAugmentable;
import cofh.block.TileCoFHBase;
import cofh.gui.container.IAugmentableContainer;
import cofh.gui.slot.SlotAugment;
import cofh.gui.slot.SlotFalseCopy;
import cofh.util.ItemHelper;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

public class ContainerTEBase extends Container implements IAugmentableContainer {

	TileCoFHBase baseTile;

	protected Slot[] augmentSlots = new Slot[0];
	protected boolean[] augmentStatus = new boolean[0];

	public ContainerTEBase() {

	}

	public ContainerTEBase(TileEntity entity) {

		baseTile = (TileCoFHBase) entity;
	}

	public ContainerTEBase(InventoryPlayer inventory, TileEntity entity) {

		if (entity instanceof TileCoFHBase) {
			baseTile = (TileCoFHBase) entity;
		}
		/* Augment Slots */
		if (baseTile instanceof IAugmentable) {
			augmentSlots = new Slot[((IAugmentable) baseTile).getAugmentSlots().length];
			for (int i = 0; i < augmentSlots.length; i++) {
				augmentSlots[i] = addSlotToContainer(new SlotAugment((IAugmentable) baseTile, null, i, 0, 0));
			}
		}
		/* Player Inventory */
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 9; j++) {
				addSlotToContainer(new Slot(inventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
			}
		}
		for (int i = 0; i < 9; i++) {
			addSlotToContainer(new Slot(inventory, i, 8 + i * 18, 142));
		}
	}

	protected void addPlayerInventory(InventoryPlayer inventory) {

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
	public boolean canInteractWith(EntityPlayer player) {

		return baseTile == null ? true : baseTile.isUseable(player);
	}

	@Override
	public void detectAndSendChanges() {

		super.detectAndSendChanges();

		if (baseTile == null) {
			return;
		}
		for (int i = 0; i < crafters.size(); i++) {
			baseTile.sendGuiNetworkData(this, (ICrafting) crafters.get(i));
		}
	}

	@Override
	public void updateProgressBar(int i, int j) {

		if (baseTile == null) {
			return;
		}
		baseTile.receiveGuiNetworkData(i, j);
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int slotIndex) {

		ItemStack stack = null;
		Slot slot = (Slot) inventorySlots.get(slotIndex);

		int invPlayer = augmentSlots.length + 27;
		int invFull = invPlayer + 9;
		int invTile = invFull + (baseTile == null ? 0 : baseTile.getInvSlotCount());

		if (slot != null && slot.getHasStack()) {
			ItemStack stackInSlot = slot.getStack();
			stack = stackInSlot.copy();

			if (slotIndex >= invFull) {
				if (!this.mergeItemStack(stackInSlot, 0, invFull, true)) {
					return null;
				}
			} else {
				if (!this.mergeItemStack(stackInSlot, invFull, invTile, true)) {
					if (slotIndex >= invPlayer) {
						if (!this.mergeItemStack(stackInSlot, 0, invPlayer, true)) {
							return null;
						}
					} else {
						if (!this.mergeItemStack(stackInSlot, invPlayer, invFull, false)) {
							return null;
						}
					}
				}
			}
			if (stackInSlot.stackSize <= 0) {
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

	@Override
	public ItemStack slotClick(int slotId, int mouseButton, int modifier, EntityPlayer player) {

		Slot slot = slotId < 0 ? null : (Slot) this.inventorySlots.get(slotId);
		if (slot instanceof SlotFalseCopy) {
			if (mouseButton == 2) {
				slot.putStack(null);
				slot.onSlotChanged();
			} else {
				slot.putStack(player.inventory.getItemStack() == null ? null : player.inventory.getItemStack().copy());
			}
			return player.inventory.getItemStack();
		}
		return super.slotClick(slotId, mouseButton, modifier, player);
	}

	@Override
	protected boolean mergeItemStack(ItemStack stack, int slotMin, int slotMax, boolean reverse) {

		boolean slotFound = false;
		int k = reverse ? slotMax - 1 : slotMin;

		Slot slot;
		ItemStack stackInSlot;

		if (stack.isStackable()) {
			while (stack.stackSize > 0 && (!reverse && k < slotMax || reverse && k >= slotMin)) {
				slot = (Slot) this.inventorySlots.get(k);
				stackInSlot = slot.getStack();

				if (slot.isItemValid(stack) && ItemHelper.itemsEqualWithMetadata(stack, stackInSlot, true)) {
					int l = stackInSlot.stackSize + stack.stackSize;
					int slotLimit = Math.min(stack.getMaxStackSize(), slot.getSlotStackLimit());

					if (l <= slotLimit) {
						stack.stackSize = 0;
						stackInSlot.stackSize = l;
						slot.onSlotChanged();
						slotFound = true;
					} else if (stackInSlot.stackSize < slotLimit) {
						stack.stackSize -= slotLimit - stackInSlot.stackSize;
						stackInSlot.stackSize = slotLimit;
						slot.onSlotChanged();
						slotFound = true;
					}
				}
				k += reverse ? -1 : 1;
			}
		}
		if (stack.stackSize > 0) {
			k = reverse ? slotMax - 1 : slotMin;

			while (!reverse && k < slotMax || reverse && k >= slotMin) {
				slot = (Slot) this.inventorySlots.get(k);
				stackInSlot = slot.getStack();

				if (slot.isItemValid(stack) && stackInSlot == null) {
					slot.putStack(ItemHelper.cloneStack(stack, Math.min(stack.stackSize, slot.getSlotStackLimit())));
					slot.onSlotChanged();

					if (slot.getStack() != null) {
						stack.stackSize -= slot.getStack().stackSize;
						slotFound = true;
					}
					break;
				}
				k += reverse ? -1 : 1;
			}
		}
		return slotFound;
	}

	/* IAugmentableContainer */
	@Override
	public Slot[] getAugmentSlots() {

		return augmentSlots;
	}

}
