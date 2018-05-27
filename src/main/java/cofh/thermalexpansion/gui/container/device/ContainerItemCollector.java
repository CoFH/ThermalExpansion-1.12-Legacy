package cofh.thermalexpansion.gui.container.device;

import cofh.core.gui.container.ContainerTileAugmentable;
import cofh.core.gui.slot.ISlotValidator;
import cofh.thermalexpansion.block.device.TileItemCollector;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

public class ContainerItemCollector extends ContainerTileAugmentable implements ISlotValidator {

	TileItemCollector myTile;

	public ContainerItemCollector(InventoryPlayer inventory, TileEntity tile) {

		super(inventory, tile);

		myTile = (TileItemCollector) tile;

		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				addSlotToContainer(new Slot(myTile, j + i * 3, 62 + j * 18, 17 + i * 18));
			}
		}
	}

	@Override
	public boolean isItemValid(ItemStack stack) {

		return myTile.isItemValidForSlot(0, stack);
	}

}
