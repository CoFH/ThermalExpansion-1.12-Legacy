package thermalexpansion.gui.slot;

import cofh.lib.gui.slot.SlotSpecificItem;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

import thermalexpansion.block.device.TileWorkbench;

public class SlotSpecificItemWorkbench extends SlotSpecificItem {

	TileWorkbench myTile;

	public SlotSpecificItemWorkbench(IInventory inventory, int slotIndex, int x, int y, ItemStack stack) {

		super(inventory, slotIndex, x, y, stack);
		myTile = (TileWorkbench) inventory;
	}

	@Override
	public boolean canTakeStack(EntityPlayer player) {

		if (myTile.getCurrentSchematicSlot() == getSlotIndex()) {
			return true;
		}
		myTile.setCurrentSchematicSlot(getSlotIndex());
		onSlotChanged();
		return false;

	}

}
