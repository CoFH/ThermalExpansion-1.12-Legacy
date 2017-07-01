package cofh.thermalexpansion.gui.container.dynamo;

import cofh.core.gui.slot.ISlotValidator;
import cofh.core.gui.slot.SlotValidated;
import cofh.thermalexpansion.block.dynamo.TileDynamoNumismatic;
import cofh.thermalexpansion.gui.container.ContainerTEBase;
import cofh.thermalexpansion.util.managers.dynamo.NumismaticManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

public class ContainerDynamoNumismatic extends ContainerTEBase implements ISlotValidator {

	TileDynamoNumismatic myTile;

	public ContainerDynamoNumismatic(InventoryPlayer inventory, TileEntity tile) {

		super(inventory, tile);

		myTile = (TileDynamoNumismatic) tile;
		addSlotToContainer(new SlotValidated(this, myTile, 0, 44, 35));
	}

	@Override
	public boolean isItemValid(ItemStack stack) {

		return NumismaticManager.getFuelEnergy(stack) > 0;
	}

}
