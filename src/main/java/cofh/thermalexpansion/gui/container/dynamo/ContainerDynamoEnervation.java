package cofh.thermalexpansion.gui.container.dynamo;

import cofh.lib.gui.slot.ISlotValidator;
import cofh.lib.gui.slot.SlotValidated;
import cofh.thermalexpansion.block.dynamo.TileDynamoEnervation;
import cofh.thermalexpansion.gui.container.ContainerTEBase;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

public class ContainerDynamoEnervation extends ContainerTEBase implements ISlotValidator {

	TileDynamoEnervation myTile;

	public ContainerDynamoEnervation(InventoryPlayer inventory, TileEntity tile) {

		super(inventory, tile);

		myTile = (TileDynamoEnervation) tile;
		addSlotToContainer(new SlotValidated(this, myTile, 0, 44, 35));
	}

	@Override
	public boolean isItemValid(ItemStack stack) {

		return TileDynamoEnervation.getEnergyValue(stack) > 0;
	}

}
