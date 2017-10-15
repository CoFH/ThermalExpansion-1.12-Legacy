package cofh.thermalexpansion.gui.slot;

import cofh.core.gui.slot.SlotFalseCopy;
import cofh.thermalexpansion.block.device.TileLexicon;
import cofh.thermalfoundation.util.LexiconManager;
import net.minecraft.item.ItemStack;

public class SlotLexicon extends SlotFalseCopy {

	protected TileLexicon myTile;

	public SlotLexicon(TileLexicon tile, int slotIndex, int x, int y) {

		super(tile, slotIndex, x, y);
		myTile = tile;
	}

	@Override
	public boolean isItemValid(ItemStack stack) {

		return stack == ItemStack.EMPTY || LexiconManager.validOre(stack) && !myTile.hasPreferredStack(stack);
	}

	@Override
	public void onSlotChanged() {

		myTile.updatePreferredStacks();
		myTile.markChunkDirty();
	}

}
