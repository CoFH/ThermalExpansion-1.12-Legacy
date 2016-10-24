package cofh.thermalexpansion.gui.container;

import cofh.api.tileentity.ISecurable;
import cofh.core.util.CoreUtils;
import cofh.lib.gui.container.ContainerInventoryItem;
import cofh.lib.gui.slot.ISlotValidator;
import cofh.lib.gui.slot.SlotValidated;
import cofh.lib.gui.slot.SlotViewOnly;
import cofh.lib.util.helpers.MathHelper;
import cofh.lib.util.helpers.SecurityHelper;
import cofh.thermalexpansion.gui.slot.SlotSatchelCreative;
import cofh.thermalexpansion.item.ItemSatchel;
import cofh.thermalexpansion.network.PacketTEBase;
import com.mojang.authlib.GameProfile;
import net.minecraftforge.fml.common.Optional;

import gnu.trove.map.hash.THashMap;

import invtweaks.api.container.ChestContainer;
import invtweaks.api.container.ChestContainer.RowSizeCallback;
import invtweaks.api.container.ContainerSection;
import invtweaks.api.container.ContainerSectionCallback;

import java.util.List;
import java.util.Map;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

@ChestContainer()
public class ContainerSatchel extends ContainerInventoryItem implements ISecurable, ISlotValidator {

	int storageIndex;
	int rowSize;

	public ContainerSatchel(ItemStack stack, InventoryPlayer inventory) {

		super(stack, inventory);

		storageIndex = ItemSatchel.getStorageIndex(stack);
		rowSize = MathHelper.clamp(storageIndex + 1, 9, 13);

		int rows = MathHelper.clamp(storageIndex, 2, 8);
		int slots = rowSize * rows;
		int yOffset = 17;

		addPlayerSlotsToContainer(inventory, 8 + 9 * (rowSize - 9), rows);

		if (storageIndex == 0) {
			addSlotToContainer(new SlotSatchelCreative(this, containerWrapper, 0, 80, 26));
			rowSize = 1;
		} else if (storageIndex == 1) {
			yOffset += 9;
			for (int i = 0; i < 9; i++) {
				addSlotToContainer(new SlotValidated(this, containerWrapper, i, 8 + i % rowSize * 18, yOffset + i / rowSize * 18));
			}
		} else {
			for (int i = 0; i < slots; i++) {
				addSlotToContainer(new SlotValidated(this, containerWrapper, i, 8 + i % rowSize * 18, yOffset + i / rowSize * 18));
			}
		}
	}

	@Override
	protected int getPlayerInventoryVerticalOffset() {

		return 84;
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

	/* Inventory Tweaks */
	@Optional.Method(modid = "inventorytweaks")
	@RowSizeCallback
	public int getRowSize() {

		return rowSize;
	}

	@ContainerSectionCallback
	@Optional.Method(modid = "inventorytweaks")
	public Map<ContainerSection, List<Slot>> getContainerSections() {

		Map<ContainerSection, List<Slot>> slotRefs = new THashMap<ContainerSection, List<Slot>>();

		slotRefs.put(ContainerSection.INVENTORY, inventorySlots.subList(0, 36));
		slotRefs.put(ContainerSection.INVENTORY_NOT_HOTBAR, inventorySlots.subList(0, 27));
		slotRefs.put(ContainerSection.INVENTORY_HOTBAR, inventorySlots.subList(27, 36));
		slotRefs.put(ContainerSection.CHEST, inventorySlots.subList(36, inventorySlots.size()));

		return slotRefs;
	}

	/* ISecurable */
	@Override
	public boolean setAccess(AccessMode access) {

		if (SecurityHelper.setAccess(getContainerStack(), access)) {
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

		return SecurityHelper.getAccess(getContainerStack());
	}

	@Override
	public String getOwnerName() {

		return SecurityHelper.getOwnerName(getContainerStack());
	}

	@Override
	public GameProfile getOwner() {

		return SecurityHelper.getOwner(getContainerStack());
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

	/* ISlotValidator */
	@Override
	public boolean isItemValid(ItemStack stack) {

		return containerWrapper.isItemValidForSlot(0, stack);
	}

}
