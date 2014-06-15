package thermalexpansion.gui.container;

import cofh.block.TileCoFHBase;
import cofh.gui.container.IAugmentableContainer;
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
		for (int i = 0; i < crafters.size(); ++i) {
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
	public ItemStack transferStackInSlot(EntityPlayer player, int i) {

		ItemStack stack = null;
		Slot slot = (Slot) inventorySlots.get(i);

		int invTile = baseTile == null ? 0 : baseTile.getInvSlotCount();
		int invPlayer = invTile + 27;
		int invFull = invTile + 36;

		if (slot != null && slot.getHasStack()) {
			ItemStack stackInSlot = slot.getStack();
			stack = stackInSlot.copy();

			if (i < invTile) {
				if (!this.mergeItemStack(stackInSlot, invTile, invFull, true)) {
					return null;
				}
			} else if (!this.mergeItemStack(stackInSlot, 0, invTile, false)) {
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
		int k = slotMin;

		if (reverse) {
			k = slotMax - 1;
		}
		Slot slot;
		ItemStack stackInSlot;

		if (stack.isStackable()) {
			while (stack.stackSize > 0 && (!reverse && k < slotMax || reverse && k >= slotMin)) {
				slot = (Slot) this.inventorySlots.get(k);
				stackInSlot = slot.getStack();

				if (stackInSlot != null && stackInSlot.getItem() == stack.getItem()
						&& (!stack.getHasSubtypes() || ItemHelper.getItemDamage(stack) == ItemHelper.getItemDamage(stackInSlot))
						&& ItemStack.areItemStackTagsEqual(stack, stackInSlot)) {
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
				if (reverse) {
					--k;
				} else {
					++k;
				}
			}
		}
		if (stack.stackSize > 0) {
			if (reverse) {
				k = slotMax - 1;
			} else {
				k = slotMin;
			}
			while (!reverse && k < slotMax || reverse && k >= slotMin) {
				slot = (Slot) this.inventorySlots.get(k);
				stackInSlot = slot.getStack();

				if (stackInSlot == null) {
					slot.putStack(ItemHelper.cloneStack(stack, Math.min(stack.stackSize, slot.getSlotStackLimit())));
					slot.onSlotChanged();

					if (slot.getStack() != null) {
						stack.stackSize -= slot.getStack().stackSize;
						slotFound = true;
					}
					break;
				}
				if (reverse) {
					--k;
				} else {
					++k;
				}
			}
		}
		return slotFound;
	}

	/* IUpgradableContainer */
	@Override
	public void augmentTile() {

	}

	@Override
	public Slot[] getAugmentSlots() {

		return augmentSlots;
	}

	@Override
	public boolean[] getAugmentStatus() {

		return augmentStatus;
	}

}
