package cofh.thermalexpansion.gui.container.dynamo;

import cofh.core.gui.container.ContainerTileAugmentable;
import cofh.core.gui.slot.ISlotValidator;
import cofh.core.gui.slot.SlotValidated;
import cofh.thermalexpansion.block.dynamo.TileDynamoReactant;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

public class ContainerDynamoReactant extends ContainerTileAugmentable implements ISlotValidator {

	TileDynamoReactant myTile;

	public ContainerDynamoReactant(InventoryPlayer inventory, TileEntity tile) {

		super(inventory, tile);

		myTile = (TileDynamoReactant) tile;
		addSlotToContainer(new SlotValidated(this, myTile, 0, 44, 35));
	}

	@Override
	public boolean isItemValid(ItemStack stack) {

		return myTile.isItemValidForSlot(0, stack);
	}

}
