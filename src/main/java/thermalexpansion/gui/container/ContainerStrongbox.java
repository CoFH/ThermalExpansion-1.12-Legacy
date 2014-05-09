package thermalexpansion.gui.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

import thermalexpansion.block.strongbox.BlockStrongbox;
import thermalexpansion.block.strongbox.TileStrongbox;

@ChestContainer()
public class ContainerStrongbox extends ContainerTEBase {

	TileStrongbox myTile;

	public ContainerStrongbox(InventoryPlayer inventory, TileEntity entity) {

		super(entity);

		myTile = (TileStrongbox) entity;
		myTile.openInventory();

		int slotsPerRow = 9;
		int rows = myTile.getSizeInventory() / slotsPerRow;
		int invOffset = 8;

		if (myTile.type == BlockStrongbox.Types.CREATIVE.ordinal()) {
			rows = 2;
			addSlotToContainer(new Slot(myTile, 0, 80, 26));
		} else {
			if (myTile.type == BlockStrongbox.Types.RESONANT.ordinal()) {
				slotsPerRow = 12;
				invOffset = 35;
			}
			rows = myTile.getSizeInventory() / slotsPerRow;
			for (int i = 0; i < myTile.getSizeInventory(); i++) {
				addSlotToContainer(new Slot(myTile, i, 8 + i % slotsPerRow * 18, 17 + i / slotsPerRow * 18));
			}
		}
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 9; j++) {
				addSlotToContainer(new Slot(inventory, j + i * 9 + 9, invOffset + j * 18, 30 + 18 * rows + i * 18));
			}
		}
		for (int i = 0; i < 9; i++) {
			addSlotToContainer(new Slot(inventory, i, invOffset + i * 18, 88 + 18 * rows));
		}
	}

	@Override
	public void onContainerClosed(EntityPlayer player) {

		super.onContainerClosed(player);
		myTile.closeInventory();
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int i) {

		if (myTile.getType() == BlockStrongbox.Types.CREATIVE.ordinal()) {
			return null;
		}
		return super.transferStackInSlot(player, i);
	}

	public TileStrongbox getTile() {

		return myTile;
	}

	@RowSizeCallback
	public int getRowSize() {

		switch (BlockStrongbox.Types.values()[myTile.type]) {
		case CREATIVE:
			return 1;
		case RESONANT:
			return 12;
		default:
			return 9;
		}
	}

}
