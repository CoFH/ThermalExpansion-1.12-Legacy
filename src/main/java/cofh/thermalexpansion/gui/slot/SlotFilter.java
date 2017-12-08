package cofh.thermalexpansion.gui.slot;

import cofh.core.gui.slot.SlotFalseCopy;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;

public class SlotFilter extends SlotFalseCopy {

    public SlotFilter(IInventory inventory, int index, int x, int y) {
        super(inventory, index, x, y);
    }
}
