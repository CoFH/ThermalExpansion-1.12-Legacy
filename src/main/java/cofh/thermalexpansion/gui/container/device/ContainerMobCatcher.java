package cofh.thermalexpansion.gui.container.device;

import cofh.core.gui.container.ContainerTileAugmentable;
import cofh.core.gui.slot.ISlotValidator;
import cofh.core.gui.slot.SlotRemoveOnly;
import cofh.core.gui.slot.SlotValidated;
import cofh.thermalexpansion.block.device.TileMobCatcher;
import cofh.thermalexpansion.init.TEItems;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

public class ContainerMobCatcher extends ContainerTileAugmentable implements ISlotValidator {

	TileMobCatcher myTile;

	public ContainerMobCatcher(InventoryPlayer inventory, TileEntity tile) {

		super(inventory, tile);

		myTile = (TileMobCatcher) tile;
		addSlotToContainer(new SlotValidated(this, myTile, 0, 35, 35));
		addSlotToContainer(new SlotRemoveOnly(myTile, 1, 107, 26));
		addSlotToContainer(new SlotRemoveOnly(myTile, 2, 125, 26));
		addSlotToContainer(new SlotRemoveOnly(myTile, 3, 107, 44));
		addSlotToContainer(new SlotRemoveOnly(myTile, 4, 125, 44));
	}

	@Override
	public boolean isItemValid(ItemStack stack) {

		return stack.getItem().equals(TEItems.itemMorb);
	}

}
