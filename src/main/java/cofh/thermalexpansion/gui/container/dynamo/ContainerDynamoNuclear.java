package cofh.thermalexpansion.gui.container.dynamo;

import cofh.core.gui.container.ContainerTileAugmentable;
import cofh.core.gui.slot.ISlotValidator;
import cofh.core.gui.slot.SlotRemoveOnly;
import cofh.core.gui.slot.SlotValidated;
import cofh.thermalexpansion.block.dynamo.TileDynamoNuclear;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

public class ContainerDynamoNuclear extends ContainerTileAugmentable implements ISlotValidator {

	TileDynamoNuclear myTile;

	public ContainerDynamoNuclear(InventoryPlayer inventory, TileEntity tile) {

		super(inventory, tile);

		myTile = (TileDynamoNuclear) tile;
		addSlotToContainer(new SlotValidated(this, myTile, 0, 44, 35));
		addSlotToContainer(new SlotRemoveOnly(myTile, 1, 116, 26));
	}

	@Override
	public boolean isItemValid(ItemStack stack) {

		return myTile.isItemValidForSlot(0, stack);
	}

}
