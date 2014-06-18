package thermalexpansion.gui.container;

import cofh.api.core.ISecurable;
import cofh.core.CoFHProps;
import cofh.gui.container.ContainerInventoryItem;
import cofh.gui.slot.ISlotValidator;
import cofh.gui.slot.SlotValidated;
import cofh.gui.slot.SlotViewOnly;
import cofh.social.RegistryFriends;
import cofh.util.CoreUtils;
import cofh.util.MathHelper;
import cofh.util.SecurityHelper;

import invtweaks.api.container.ChestContainer;
import invtweaks.api.container.ChestContainer.RowSizeCallback;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import thermalexpansion.item.tool.ItemSatchel;

@ChestContainer()
public class ContainerSatchel extends ContainerInventoryItem implements ISecurable, ISlotValidator {

	int storageIndex;
	int rowSize;

	public ContainerSatchel(ItemStack stack, InventoryPlayer inventory) {

		super(stack, inventory);

		storageIndex = ItemSatchel.getStorageIndex(stack);
		rowSize = MathHelper.clampI(storageIndex + 1, 9, 13);

		int rows = MathHelper.clampI(storageIndex, 2, 8);
		int slots = rowSize * rows;

		addPlayerSlotsToContainer(inventory, 8 + 9 * (rowSize - 9), rows);

		if (storageIndex == 0) {
			addSlotToContainer(new SlotValidated(this, containerWrapper, 0, 80, 26));
			rowSize = 1;
		} else {
			int yOffset = storageIndex == 2 ? 26 : 17;
			for (int i = 0; i < slots; i++) {
				addSlotToContainer(new SlotValidated(this, containerWrapper, i, 8 + i % rowSize * 18, yOffset + i / rowSize * 18));
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
			if (i == inventory.currentItem) {
				addSlotToContainer(new SlotViewOnly(inventory, i, invOffset + i * 18, 88 + 18 * rows));
			} else {
				addSlotToContainer(new Slot(inventory, i, invOffset + i * 18, 88 + 18 * rows));
			}
		}
	}

	@RowSizeCallback
	public int getRowSize() {

		return rowSize;
	}

	/* ISecurable */
	@Override
	public boolean setAccess(AccessMode access) {

		if (SecurityHelper.setAccess(getContainerStack(), access)) {
			onSlotChanged();
			return true;
		}
		return false;
	}

	@Override
	public boolean setOwnerName(String name) {

		if (SecurityHelper.setOwnerName(getContainerStack(), name)) {
			onSlotChanged();
			return true;
		}
		return false;
	}

	@Override
	public AccessMode getAccess() {

		return SecurityHelper.getAccess(getContainerStack());
	}

	@Override
	public String getOwnerName() {

		return SecurityHelper.getOwnerName(getContainerStack());
	}

	@Override
	public boolean canPlayerAccess(String name) {

		AccessMode access = getAccess();
		String owner = getOwnerName();

		return access.isPublic() || (CoFHProps.enableOpSecureAccess && CoreUtils.isOp(name)) || owner.equals(CoFHProps.DEFAULT_OWNER) || owner.equals(name)
				|| access.isRestricted() && RegistryFriends.playerHasAccess(name, owner);
	}

	/* ISlotValidator */
	@Override
	public boolean isItemValid(ItemStack stack) {

		return containerWrapper.isItemValidForSlot(0, stack);
	}

}
