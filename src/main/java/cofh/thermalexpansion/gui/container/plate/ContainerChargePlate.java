package cofh.thermalexpansion.gui.container.plate;

import cofh.lib.gui.slot.SlotEnergy;
import cofh.thermalexpansion.block.plate.TilePlateCharger;
import cofh.thermalexpansion.gui.container.ContainerTEBase;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;


public class ContainerChargePlate extends ContainerTEBase {

	TilePlateCharger myTile;

	public ContainerChargePlate(InventoryPlayer inventory, TilePlateCharger tile) {

		super(inventory, tile, false, false);

		myTile = tile;

		addSlotToContainer(new SlotEnergy(myTile, 0, 8, 53) {

			@Override
			public ItemStack getStack() {

				return myTile.getEnergySlot();
			}

			@Override
			public void putStack(ItemStack stack) {

				myTile.setEnergySlot(stack);
				onSlotChanged();
			}

			@Override
			public void onSlotChanged() {

				((TileEntity) myTile).markDirty();
			}

			@Override
			public int getSlotStackLimit() {

				return 1;
			}

			@Override
			public ItemStack decrStackSize(int amount) {

				if (myTile.getEnergySlot() == null) {
					return null;
				}
				ItemStack stack = myTile.getEnergySlot().splitStack(1);
				putStack(null);

				return stack;
			}

			@Override
			public boolean isSlotInInventory(IInventory inventory, int slot) {

				return false;
			}
		});
	}

}
