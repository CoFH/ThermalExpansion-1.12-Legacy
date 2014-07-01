package thermalexpansion.gui.container.dynamo;

import cofh.gui.slot.ISlotValidator;
import cofh.gui.slot.SlotValidated;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

import thermalexpansion.block.dynamo.TileDynamoEnervation;
import thermalexpansion.gui.container.ContainerTEBase;

public class ContainerDynamoEnervation extends ContainerTEBase implements ISlotValidator {

	TileDynamoEnervation myTile;

	public ContainerDynamoEnervation(InventoryPlayer inventory, TileEntity entity) {

		super(inventory, entity);

		myTile = (TileDynamoEnervation) entity;
		addSlotToContainer(new SlotValidated(this, myTile, 0, 44, 35));
	}

	@Override
	public boolean isItemValid(ItemStack stack) {

		return TileDynamoEnervation.getEnergyValue(stack) > 0;
	}

}
