package thermalexpansion.gui.container;

import net.minecraft.inventory.Slot;

public interface ISetSchematic {

	public void writeSchematic();

	public boolean canWriteSchematic();

	public Slot[] getCraftingSlots();

	public Slot getResultSlot();

}
