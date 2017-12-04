package cofh.thermalexpansion.gui.container.device;

import cofh.core.gui.slot.ISlotValidator;
import cofh.core.gui.slot.SlotRemoveOnly;
import cofh.core.gui.slot.SlotValidated;
import cofh.thermalexpansion.block.device.TileFisher;
import cofh.thermalexpansion.gui.container.ContainerTEBase;
import cofh.thermalexpansion.util.managers.device.FisherManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

public class ContainerFisher extends ContainerTEBase implements ISlotValidator {

	TileFisher myTile;

	public ContainerFisher(InventoryPlayer inventory, TileEntity tile) {

		super(inventory, tile);

		myTile = (TileFisher) tile;
		addSlotToContainer(new SlotValidated(this, myTile, 0, 35, 35));
		addSlotToContainer(new SlotRemoveOnly(myTile, 1, 107, 26));
		addSlotToContainer(new SlotRemoveOnly(myTile, 2, 125, 26));
		addSlotToContainer(new SlotRemoveOnly(myTile, 3, 107, 44));
		addSlotToContainer(new SlotRemoveOnly(myTile, 4, 125, 44));
	}

	@Override
	public boolean isItemValid(ItemStack stack) {

		return FisherManager.isValidBait(stack);
	}

}
