package cofh.thermalexpansion.gui.container.storage;

import cofh.api.core.IFilterable;
import cofh.api.core.ISecurable;
import cofh.core.gui.container.ContainerCore;
import cofh.core.gui.slot.SlotLocked;
import cofh.core.util.CoreUtils;
import cofh.core.util.filter.ItemFilterWrapper;
import cofh.core.util.helpers.SecurityHelper;
import cofh.core.util.helpers.StringHelper;
import cofh.thermalexpansion.gui.slot.SlotSatchelFilter;
import cofh.thermalexpansion.item.ItemSatchel;
import cofh.thermalexpansion.network.PacketTEBase;
import com.mojang.authlib.GameProfile;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerSatchelFilter extends ContainerCore implements IFilterable, ISecurable {

	protected final ItemFilterWrapper filterWrapper;
	protected final EntityPlayer player;
	protected final int filterIndex;
	protected boolean valid = true;

	public ContainerSatchelFilter(ItemStack stack, InventoryPlayer inventory) {

		player = inventory.player;
		filterIndex = inventory.currentItem;
		filterWrapper = new ItemFilterWrapper(stack, ItemSatchel.getFilterSize(stack));

		addFilterSlots();
		bindPlayerInventory(inventory);
	}

	private void addFilterSlots() {

		int x0 = 7;
		int y0 = 21;

		for (int i = 0; i <= ItemSatchel.getLevel(filterWrapper.getFilterStack()); i++) {
			for (int j = 0; j < 7; j++) {
				addSlotToContainer(new SlotSatchelFilter(filterWrapper, 7 * i + j, x0 + (18 * j), y0 + (18 * i)));
			}
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

		return 133;
	}

	@Override
	protected int getSizeInventory() {

		return 0;
	}

	@Override
	public void detectAndSendChanges() {

		ItemStack item = player.inventory.mainInventory.get(filterIndex);
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

		ItemStack item = player.inventory.mainInventory.get(filterIndex);
		if (valid && !item.isEmpty() && item.getItem() == filterWrapper.getFilterItem()) {
			player.inventory.mainInventory.set(filterIndex, filterWrapper.getFilterStack());
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
			PacketTEBase.sendFilterPacketToServer(flag, value);
		}
		filterWrapper.markDirty();
	}

	/* ISecurable */
	@Override
	public boolean setAccess(AccessMode access) {

		if (SecurityHelper.setAccess(getFilterStack(), access)) {
			onSlotChanged();
			if (CoreUtils.isClient()) {
				PacketTEBase.sendSecurityPacketToServer(this);
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
