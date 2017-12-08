package cofh.thermalexpansion.gui.slot;

import cofh.api.item.IInventoryContainerItem;
import cofh.core.gui.slot.SlotFalseCopy;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public class SlotSatchelFilter extends SlotFalseCopy {

	public SlotSatchelFilter(IInventory inventory, int index, int x, int y) {

		super(inventory, index, x, y);
	}

	@Override
	public boolean isItemValid(ItemStack stack) {

		return !(stack.getItem() instanceof IInventoryContainerItem) || ((IInventoryContainerItem) stack.getItem()).getSizeInventory(stack) <= 0;
	}

}
