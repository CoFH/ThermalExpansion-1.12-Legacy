package cofh.thermalexpansion.gui.container.storage;

import cofh.api.core.ISecurable;
import cofh.core.gui.container.ContainerInventoryItem;
import cofh.core.gui.slot.ISlotValidator;
import cofh.core.gui.slot.SlotLocked;
import cofh.core.gui.slot.SlotValidated;
import cofh.core.network.PacketCore;
import cofh.core.util.CoreUtils;
import cofh.core.util.helpers.MathHelper;
import cofh.core.util.helpers.SecurityHelper;
import cofh.core.util.helpers.StringHelper;
import cofh.thermalexpansion.gui.slot.SlotSatchelCreative;
import cofh.thermalexpansion.gui.slot.SlotSatchelVoid;
import cofh.thermalexpansion.item.ItemSatchel;
import com.mojang.authlib.GameProfile;
import invtweaks.api.container.ChestContainer;
import invtweaks.api.container.ChestContainer.RowSizeCallback;
import invtweaks.api.container.ContainerSection;
import invtweaks.api.container.ContainerSectionCallback;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Optional;

import java.util.List;
import java.util.Map;

@ChestContainer
public class ContainerSatchel extends ContainerInventoryItem implements ISecurable, ISlotValidator {

	static final String NAME = "item.thermalexpansion.satchel.name";

	boolean isCreative;
	boolean isVoid;

	int storageIndex;
	int rowSize;

	public ContainerSatchel(ItemStack stack, InventoryPlayer inventory) {

		super(stack, inventory);

		isCreative = ItemSatchel.isCreative(stack);
		isVoid = ItemSatchel.isVoid(stack);

		storageIndex = ItemSatchel.getStorageIndex(stack);
		rowSize = MathHelper.clamp(storageIndex, 9, 14);

		int rows = MathHelper.clamp(storageIndex, 2, 9);
		int slots = rowSize * rows;
		int yOffset = 17;

		bindPlayerInventory(inventory);

		switch (storageIndex) {
			case 0:
				if (isVoid) {
					addSlotToContainer(new SlotSatchelVoid(containerWrapper, 0, 80, 26));
				} else {
					addSlotToContainer(new SlotSatchelCreative(this, containerWrapper, 0, 80, 26));
				}
				rowSize = 1;
				break;
			case 1:
				yOffset += 9;
				for (int i = 0; i < 9; i++) {
					addSlotToContainer(new SlotValidated(this, containerWrapper, i, 8 + i % rowSize * 18, yOffset + i / rowSize * 18));
				}
				break;
			default:
				for (int i = 0; i < slots; i++) {
					addSlotToContainer(new SlotValidated(this, containerWrapper, i, 8 + i % rowSize * 18, yOffset + i / rowSize * 18));
				}
				break;
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
	public String getInventoryName() {

		return containerWrapper.hasCustomName() ? containerWrapper.getName() : StringHelper.localize(NAME);
	}

	@Override
	protected int getPlayerInventoryVerticalOffset() {

		return 30 + 18 * MathHelper.clamp(storageIndex, 2, 9);
	}

	@Override
	protected int getPlayerInventoryHorizontalOffset() {

		return 8 + 9 * (rowSize - 9);
	}

	/* ISecurable */
	@Override
	public boolean setAccess(AccessMode access) {

		if (SecurityHelper.setAccess(getContainerStack(), access)) {
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

	/* INVENTORY TWEAKS */
	@ContainerSectionCallback
	@Optional.Method (modid = "inventorytweaks")
	public Map<ContainerSection, List<Slot>> getContainerSections() {

		Map<ContainerSection, List<Slot>> slotRefs = new Object2ObjectOpenHashMap<>();

		slotRefs.put(ContainerSection.INVENTORY, inventorySlots.subList(0, 36));
		slotRefs.put(ContainerSection.INVENTORY_NOT_HOTBAR, inventorySlots.subList(0, 27));
		slotRefs.put(ContainerSection.INVENTORY_HOTBAR, inventorySlots.subList(27, 36));
		slotRefs.put(ContainerSection.CHEST, inventorySlots.subList(36, inventorySlots.size()));

		return slotRefs;
	}

	@RowSizeCallback
	@Optional.Method (modid = "inventorytweaks")
	public int getRowSize() {

		return rowSize;
	}

}
