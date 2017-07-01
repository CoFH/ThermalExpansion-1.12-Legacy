package cofh.thermalexpansion.gui.slot;

import cofh.core.gui.slot.ISlotValidator;
import cofh.core.gui.slot.SlotFalseCopy;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public class SlotFalseCopyValidated extends SlotFalseCopy {

	ISlotValidator validator;

	public SlotFalseCopyValidated(ISlotValidator validator, IInventory inventory, int index, int x, int y) {

		super(inventory, index, x, y);
		this.validator = validator;
	}

	@Override
	public boolean isItemValid(ItemStack stack) {

		return validator.isItemValid(stack);
	}

}
