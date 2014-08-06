package thermalexpansion.gui.container.dynamo;

import cofh.lib.gui.slot.ISlotValidator;
import cofh.lib.gui.slot.SlotValidated;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

import thermalexpansion.block.dynamo.TileDynamoReactant;
import thermalexpansion.gui.container.ContainerTEBase;

public class ContainerDynamoReactant extends ContainerTEBase implements ISlotValidator {

	TileDynamoReactant myTile;

	public ContainerDynamoReactant(InventoryPlayer inventory, TileEntity tile) {

		super(inventory, tile);

		myTile = (TileDynamoReactant) tile;
		addSlotToContainer(new SlotValidated(this, myTile, 0, 44, 35));
	}

	@Override
	public boolean isItemValid(ItemStack stack) {

		return TileDynamoReactant.getReactantEnergy(stack) > 0;
	}

}
