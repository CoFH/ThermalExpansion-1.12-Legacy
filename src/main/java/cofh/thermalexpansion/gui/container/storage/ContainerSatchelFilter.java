package cofh.thermalexpansion.gui.container.storage;

import cofh.api.core.IFilterable;
import cofh.api.core.ISecurable;
import cofh.core.gui.container.ContainerCore;
import cofh.core.gui.slot.SlotLocked;
import cofh.core.network.PacketCore;
import cofh.core.util.CoreUtils;
import cofh.core.util.filter.ItemFilterWrapper;
import cofh.core.util.helpers.MathHelper;
import cofh.core.util.helpers.SecurityHelper;
import cofh.core.util.helpers.StringHelper;
import cofh.thermalexpansion.gui.slot.SlotSatchelFilter;
import cofh.thermalexpansion.item.ItemSatchel;
import com.mojang.authlib.GameProfile;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerSatchelFilter extends ContainerCore implements IFilterable, ISecurable {

	protected final ItemFilterWrapper filterWrapper;
	protected final EntityPlayer player;
	protected final int containerIndex;
	protected final int filterIndex;
	protected boolean valid = true;

	public ContainerSatchelFilter(ItemStack stack, InventoryPlayer inventory) {

		player = inventory.player;
		containerIndex = inventory.currentItem;
		filterWrapper = new ItemFilterWrapper(stack, ItemSatchel.getFilterSize(stack));

		filterIndex = ItemSatchel.getLevel(stack);
		int rows = MathHelper.clamp(filterIndex + 1, 1, 3);
		int slots = ItemSatchel.getFilterSize(stack);
		int rowSize = slots / rows;

		int xOffset = 62 - 9 * rowSize;
		int yOffset = 44 - 9 * rows;

		bindPlayerInventory(inventory);

		for (int i = 0; i < slots; i++) {
			addSlotToContainer(new SlotSatchelFilter(filterWrapper, i, xOffset + i % rowSize * 18, yOffset + i / rowSize * 18));
		}
	}

	@Override
	protected void bindPlayerInventory(InventoryPlayer inventoryPlayer) {

		int xOffset = getPlayerInventoryHorizontalOffset();
		int yOffset = getPlayerInventoryVerticalOffset();

		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 9; j++) {
				addSlotToContainer(new Slot(inventoryPlayer, j + i * 9 + 9, xOffset + j * 18, yOffset + i * 18));
			}
		}
		for (int i = 0; i < 9; i++) {
			if (i == inventoryPlayer.currentItem) {
				addSlotToContainer(new SlotLocked(inventoryPlayer, i, xOffset + i * 18, yOffset + 58));
			} else {
				addSlotToContainer(new Slot(inventoryPlayer, i, xOffset + i * 18, yOffset + 58));
			}
		}
	}

	@Override
	protected int getPlayerInventoryVerticalOffset() {

		return 84;
	}

	@Override
	protected int getSizeInventory() {

		return 0;
	}

	@Override
	public void detectAndSendChanges() {

		ItemStack item = player.inventory.mainInventory.get(containerIndex);
		if (item.isEmpty() || item.getItem() != filterWrapper.getFilterItem()) {
			valid = false;
			return;
		}
		super.detectAndSendChanges();
	}

	@Override
	public boolean canInteractWith(EntityPlayer player) {

		onSlotChanged();
		if (filterWrapper.getDirty() && !valid) {
			player.inventory.setItemStack(ItemStack.EMPTY);
		}
		return valid;
	}

	/* HELPERS */
	public void onSlotChanged() {

		ItemStack item = player.inventory.mainInventory.get(containerIndex);
		if (valid && !item.isEmpty() && item.getItem() == filterWrapper.getFilterItem()) {
			player.inventory.mainInventory.set(containerIndex, filterWrapper.getFilterStack());
		}
	}

	public boolean getFlag(int flag) {

		return filterWrapper.getFilter().getFlag(flag);
	}

	public String getInventoryName() {

		return filterWrapper.hasCustomName() ? filterWrapper.getName() : StringHelper.localize(ContainerSatchel.NAME);
	}

	public ItemStack getFilterStack() {

		return filterWrapper.getFilterStack();
	}

	/* IFilterable */
	public void setFlag(int flag, boolean value) {

		filterWrapper.getFilter().setFlag(flag, value);
		if (CoreUtils.isClient()) {
			PacketCore.sendFilterPacketToServer(flag, value);
		}
		filterWrapper.markDirty();
	}

	/* ISecurable */
	@Override
	public boolean setAccess(AccessMode access) {

		if (SecurityHelper.setAccess(getFilterStack(), access)) {
			onSlotChanged();
			if (CoreUtils.isClient()) {
				PacketCore.sendSecurityPacketToServer(this);
			}
			return true;
		}
		return false;
	}

	@Override
	public AccessMode getAccess() {

		return SecurityHelper.getAccess(getFilterStack());
	}

	@Override
	public String getOwnerName() {

		return SecurityHelper.getOwnerName(getFilterStack());
	}

	@Override
	public GameProfile getOwner() {

		return SecurityHelper.getOwner(getFilterStack());
	}

	@Override
	public boolean canPlayerAccess(EntityPlayer player) {

		throw new UnsupportedOperationException();
	}

	@Override
	public boolean setOwnerName(String name) {

		throw new UnsupportedOperationException();
	}

	@Override
	public boolean setOwner(GameProfile name) {

		throw new UnsupportedOperationException();
	}

}
