package thermalexpansion.gui.container;

import cofh.api.item.IInventoryContainerItem;
import cofh.gui.slot.ISlotValidator;
import cofh.gui.slot.SlotValidated;
import cofh.util.MathHelper;

import invtweaks.api.container.ChestContainer;
import invtweaks.api.container.ChestContainer.RowSizeCallback;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

import thermalexpansion.block.strongbox.BlockStrongbox;
import thermalexpansion.block.strongbox.TileStrongbox;

@ChestContainer()
public class ContainerStrongbox extends ContainerTEBase implements ISlotValidator {

	int storageIndex;
	int rowSize;
	TileStrongbox myTile;

	public ContainerStrongbox(InventoryPlayer inventory, TileEntity entity) {

		super(entity);

		myTile = (TileStrongbox) entity;
		myTile.openInventory();

		storageIndex = myTile.getStorageIndex();
		rowSize = MathHelper.clampI(storageIndex + 1, 9, 13);

		int rows = MathHelper.clampI(storageIndex, 2, 8);
		int slots = rowSize * rows;

		addPlayerSlotsToContainer(inventory, 8 + 9 * (rowSize - 9), rows);

		if (storageIndex == 0) {
			addSlotToContainer(new SlotValidated(this, myTile, 0, 80, 26));
			rowSize = 1;
		} else {
			int yOffset = storageIndex == 2 ? 26 : 17;
			for (int i = 0; i < slots; i++) {
				addSlotToContainer(new SlotValidated(this, myTile, i, 8 + i % rowSize * 18, yOffset + i / rowSize * 18));
			}
		}
	}

	private void addPlayerSlotsToContainer(InventoryPlayer inventory, int invOffset, int rows) {

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

		return rowSize;
	}

	/* ISlotValidator */
	@Override
	public boolean isItemValid(ItemStack stack) {

		if (stack == null) {
			return false;
		}
		if (stack.getItem() instanceof IInventoryContainerItem) {
			return ((IInventoryContainerItem) stack.getItem()).getSizeInventory(stack) <= 0;
		}
		return true;
	}

}
