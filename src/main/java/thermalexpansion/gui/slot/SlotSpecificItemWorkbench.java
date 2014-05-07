package thermalexpansion.gui.slot;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import thermalexpansion.block.device.TileWorkbench;
import cofh.gui.slot.SlotSpecificItem;

public class SlotSpecificItemWorkbench extends SlotSpecificItem {

	TileWorkbench myTile;

	public SlotSpecificItemWorkbench(IInventory inventory, int slotIndex, int x, int y, ItemStack stack) {

		super(inventory, slotIndex, x, y, stack);
		myTile = (TileWorkbench) inventory;
	}

	@Override
	public boolean canTakeStack(EntityPlayer par1EntityPlayer) {

		if (myTile.getCurrentSchematicSlot() == getSlotIndex()) {
			return true;
		} else {
			myTile.setCurrentSchematicSlot(getSlotIndex());
			onSlotChanged();
			return false;
		}
	}

}
