package cofh.thermalexpansion.gui.container.device;

import cofh.core.gui.slot.ISlotValidator;
import cofh.core.gui.slot.SlotRemoveOnly;
import cofh.core.gui.slot.SlotValidated;
import cofh.thermalexpansion.block.device.TileCatcher;
import cofh.thermalexpansion.gui.container.ContainerTEBase;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

public class ContainerCatcher extends ContainerTEBase implements ISlotValidator{

	TileCatcher myTile;

	public ContainerCatcher(InventoryPlayer inventory, TileEntity tile) {

		super(inventory, tile);

		myTile = (TileCatcher) tile;
		addSlotToContainer(new SlotValidated(this, myTile, 0, 35, 35));
		addSlotToContainer(new SlotRemoveOnly(myTile, 1, 107, 26));
		addSlotToContainer(new SlotRemoveOnly(myTile, 2, 125, 26));
		addSlotToContainer(new SlotRemoveOnly(myTile, 3, 107, 44));
		addSlotToContainer(new SlotRemoveOnly(myTile, 4, 125, 44));
	}

	@Override
	public boolean isItemValid(ItemStack stack) {

		return myTile.isItemValidForSlot(0,stack);
	}
}
