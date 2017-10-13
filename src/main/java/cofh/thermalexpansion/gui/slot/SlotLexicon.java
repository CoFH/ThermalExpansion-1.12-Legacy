package cofh.thermalexpansion.gui.slot;

import cofh.core.gui.slot.SlotFalseCopy;
import cofh.thermalexpansion.block.device.TileLexicon;
import cofh.thermalfoundation.util.LexiconManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public class SlotLexicon extends SlotFalseCopy {

	protected TileLexicon myTile;

	public SlotLexicon(TileLexicon tile, IInventory inventory, int slotIndex, int x, int y) {

		super(inventory, slotIndex, x, y);
		myTile = tile;
	}

	@Override
	public ItemStack decrStackSize(int amount) {

		return ItemStack.EMPTY;
	}

	@Override
	public ItemStack getStack() {

		return myTile.getInventorySlots(0)[getSlotIndex()];
	}

	@Override
	public int getSlotStackLimit() {

		return 1;
	}

	@Override
	public boolean canTakeStack(EntityPlayer player) {

		return false;
	}

	@Override
	public boolean isHere(IInventory inv, int slotIn) {

		return false;
	}

	@Override
	public boolean isItemValid(ItemStack stack) {

		return stack == ItemStack.EMPTY || LexiconManager.validOre(stack) && !myTile.hasPreferredStack(stack);
	}

	@Override
	public void onSlotChanged() {

		myTile.onSlotUpdate(getSlotIndex());
	}

	@Override
	public void putStack(ItemStack stack) {

		if (!isItemValid(stack)) {
			return;
		}
		if (!stack.isEmpty()) {
			stack.setCount(1);
		}
		myTile.getInventorySlots(0)[getSlotIndex()] = stack;
		onSlotChanged();
	}

}
