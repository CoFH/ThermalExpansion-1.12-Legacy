package cofh.thermalexpansion.gui.container.machine;

import cofh.lib.gui.slot.ISlotValidator;
import cofh.lib.gui.slot.SlotEnergy;
import cofh.lib.gui.slot.SlotRemoveOnly;
import cofh.lib.gui.slot.SlotValidated;
import cofh.thermalexpansion.block.machine.TileSmelter;
import cofh.thermalexpansion.gui.container.ContainerTEBase;
import cofh.thermalexpansion.util.crafting.SmelterManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

public class ContainerSmelter extends ContainerTEBase {

	TileSmelter myTile;

	ISlotValidator lockSlot = new ISlotValidator() {

		@Override
		public boolean isItemValid(ItemStack stack) {

			if (myTile.lockPrimary) {
				return SmelterManager.isItemFlux(stack);
			}
			return SmelterManager.isItemValid(stack);
		}

	};

	ISlotValidator otherSlot = new ISlotValidator() {

		@Override
		public boolean isItemValid(ItemStack stack) {

			if (myTile.lockPrimary) {
				return !SmelterManager.isItemFlux(stack) && SmelterManager.isItemValid(stack);
			}
			return SmelterManager.isItemValid(stack);
		}

	};

	public ContainerSmelter(InventoryPlayer inventory, TileEntity tile) {

		super(inventory, tile);

		myTile = (TileSmelter) tile;
		addSlotToContainer(new SlotValidated(lockSlot, myTile, 0, 32, 26));
		addSlotToContainer(new SlotValidated(otherSlot, myTile, 1, 56, 26));
		addSlotToContainer(new SlotRemoveOnly(myTile, 2, 116, 26));
		addSlotToContainer(new SlotRemoveOnly(myTile, 3, 134, 26));
		addSlotToContainer(new SlotRemoveOnly(myTile, 4, 116, 53));
		addSlotToContainer(new SlotEnergy(myTile, myTile.getChargeSlot(), 8, 53));
	}

}
