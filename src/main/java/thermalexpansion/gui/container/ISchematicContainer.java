package thermalexpansion.gui.container;

import net.minecraft.inventory.Slot;

public interface ISchematicContainer {

	void writeSchematic();

	boolean canWriteSchematic();

	Slot[] getCraftingSlots();

	Slot getResultSlot();

}
