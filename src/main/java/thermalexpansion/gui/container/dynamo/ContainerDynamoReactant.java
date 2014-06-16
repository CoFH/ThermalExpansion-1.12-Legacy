package thermalexpansion.gui.container.dynamo;

import cofh.gui.slot.ISlotValidator;
import cofh.gui.slot.SlotValidated;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

import thermalexpansion.block.dynamo.TileDynamoReactant;
import thermalexpansion.gui.container.ContainerTEBase;

public class ContainerDynamoReactant extends ContainerTEBase implements ISlotValidator {

	TileDynamoReactant myTile;

	public ContainerDynamoReactant(InventoryPlayer inventory, TileEntity entity) {

		super(inventory, entity);

		myTile = (TileDynamoReactant) entity;
		addSlotToContainer(new SlotValidated(this, myTile, 0, 44, 35));
	}

	@Override
	public boolean isItemValid(ItemStack stack) {

		return TileDynamoReactant.getItemEnergyValue(stack) > 0;
	}

}
