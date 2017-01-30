package cofh.thermalexpansion.gui.container.machine;

import cofh.lib.gui.slot.ISlotValidator;
import cofh.lib.gui.slot.SlotEnergy;
import cofh.lib.gui.slot.SlotRemoveOnly;
import cofh.lib.gui.slot.SlotValidated;
import cofh.thermalexpansion.block.machine.TileInsolator;
import cofh.thermalexpansion.gui.container.ContainerTEBase;
import cofh.thermalexpansion.util.crafting.InsolatorManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

public class ContainerInsolator extends ContainerTEBase {

	protected TileInsolator myTile;

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
