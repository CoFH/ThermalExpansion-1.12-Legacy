package cofh.thermalexpansion.gui.container.machine;

import cofh.core.gui.container.ContainerTileAugmentable;
import cofh.core.gui.slot.ISlotValidator;
import cofh.core.gui.slot.SlotEnergy;
import cofh.core.gui.slot.SlotRemoveOnly;
import cofh.core.gui.slot.SlotValidated;
import cofh.thermalexpansion.block.machine.TileInsolator;
import cofh.thermalexpansion.util.managers.machine.InsolatorManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

public class ContainerInsolator extends ContainerTileAugmentable {

	TileInsolator myTile;

	public ContainerInsolator(InventoryPlayer inventory, TileEntity tile) {

		super(inventory, tile);

		myTile = (TileInsolator) tile;
		addSlotToContainer(new SlotValidated(lockSlot, myTile, 0, 32, 26));
		addSlotToContainer(new SlotValidated(otherSlot, myTile, 1, 56, 26));
		addSlotToContainer(new SlotRemoveOnly(myTile, 2, 116, 26));
		addSlotToContainer(new SlotRemoveOnly(myTile, 3, 116, 53));
		addSlotToContainer(new SlotEnergy(myTile, myTile.getChargeSlot(), 8, 53));
	}

	private ISlotValidator lockSlot = new ISlotValidator() {

		@Override
		public boolean isItemValid(ItemStack stack) {

			if (myTile.lockPrimary) {
				return InsolatorManager.isItemFertilizer(stack);
			}
			return InsolatorManager.isItemValid(stack);
		}

	};

	private ISlotValidator otherSlot = new ISlotValidator() {

		@Override
		public boolean isItemValid(ItemStack stack) {

			if (myTile.lockPrimary) {
				return !InsolatorManager.isItemFertilizer(stack) && InsolatorManager.isItemValid(stack);
			}
			return InsolatorManager.isItemValid(stack);
		}

	};

}
