package cofh.thermalexpansion.gui.container.device;

import cofh.lib.gui.slot.ISlotValidator;
import cofh.lib.gui.slot.SlotValidated;
import cofh.thermalexpansion.block.device.TileFisher;
import cofh.thermalexpansion.gui.container.ContainerTEBase;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

public class ContainerFisher extends ContainerTEBase implements ISlotValidator {

	TileFisher myTile;

	public ContainerFisher(InventoryPlayer inventory, TileEntity tile) {

		super(inventory, tile);

		myTile = (TileFisher) tile;
		addSlotToContainer(new SlotValidated(this, myTile, 0, 35, 35));

		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				addSlotToContainer(new Slot(myTile, j + i * 3 + 1, 98 + j * 18, 17 + i * 18));
			}
		}
	}

	@Override
	public boolean isItemValid(ItemStack stack) {

		return true;
	}

}
