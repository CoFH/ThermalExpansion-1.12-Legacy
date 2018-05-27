package cofh.thermalexpansion.gui.container.machine;

import cofh.core.gui.container.ContainerTileAugmentable;
import cofh.core.gui.slot.ISlotValidator;
import cofh.core.gui.slot.SlotEnergy;
import cofh.core.gui.slot.SlotRemoveOnly;
import cofh.core.gui.slot.SlotValidated;
import cofh.thermalexpansion.block.machine.TileEnchanter;
import cofh.thermalexpansion.util.managers.machine.EnchanterManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

public class ContainerEnchanter extends ContainerTileAugmentable {

	TileEnchanter myTile;

	public ContainerEnchanter(InventoryPlayer inventory, TileEntity tile) {

		super(inventory, tile);

		myTile = (TileEnchanter) tile;
		addSlotToContainer(new SlotValidated(lockSlot, myTile, 0, 32, 26));
		addSlotToContainer(new SlotValidated(otherSlot, myTile, 1, 56, 26));
		addSlotToContainer(new SlotRemoveOnly(myTile, 2, 116, 35));
		addSlotToContainer(new SlotEnergy(myTile, myTile.getChargeSlot(), 8, 53));
	}

	private ISlotValidator lockSlot = new ISlotValidator() {

		@Override
		public boolean isItemValid(ItemStack stack) {

			if (myTile.lockPrimary) {
				return EnchanterManager.isItemArcana(stack);
			}
			return EnchanterManager.isItemValid(stack);
		}

	};

	private ISlotValidator otherSlot = new ISlotValidator() {

		@Override
		public boolean isItemValid(ItemStack stack) {

			if (myTile.lockPrimary) {
				return !EnchanterManager.isItemArcana(stack) && EnchanterManager.isItemValid(stack);
			}
			return EnchanterManager.isItemValid(stack);
		}

	};

}
