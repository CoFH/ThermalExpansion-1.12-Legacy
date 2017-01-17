package cofh.thermalexpansion.gui.container;

import cofh.api.item.IInventoryContainerItem;
import cofh.lib.gui.slot.ISlotValidator;
import cofh.lib.gui.slot.SlotValidated;
import cofh.lib.util.helpers.MathHelper;
import cofh.thermalexpansion.block.EnumType;
import cofh.thermalexpansion.block.strongbox.TileStrongbox;
import gnu.trove.map.hash.THashMap;
import invtweaks.api.container.ChestContainer;
import invtweaks.api.container.ChestContainer.RowSizeCallback;
import invtweaks.api.container.ContainerSection;
import invtweaks.api.container.ContainerSectionCallback;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.common.Optional;

import java.util.List;
import java.util.Map;

@ChestContainer ()
public class ContainerStrongbox extends ContainerTEBase implements ISlotValidator {

	int storageIndex;
	int rowSize;
	TileStrongbox myTile;

	public ContainerStrongbox(InventoryPlayer inventory, TileEntity tile) {

		super(tile);

		myTile = (TileStrongbox) tile;
		myTile.openInventory(inventory.player);

		storageIndex = myTile.getStorageIndex();
		rowSize = MathHelper.clamp(storageIndex + 1, 9, 13);

		int rows = MathHelper.clamp(storageIndex, 2, 8);
		int slots = rowSize * rows;

		addPlayerSlotsToContainer(inventory, 8 + 9 * (rowSize - 9), rows);

		if (storageIndex == 0) {
			addSlotToContainer(new SlotValidated(this, myTile, 0, 80, 26));
			rowSize = 1;
		} else {
			int yOffset = 17;
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
		myTile.closeInventory(player);
	}

	@Override
	protected boolean supportsShiftClick(int slotIndex) {

		return myTile.getType() != EnumType.CREATIVE.ordinal();
	}

	public TileStrongbox getTile() {

		return myTile;
	}

	/* Inventory Tweaks */
	@Optional.Method (modid = "inventorytweaks")
	@RowSizeCallback
	public int getRowSize() {

		return rowSize;
	}

	@ContainerSectionCallback
	@Optional.Method (modid = "inventorytweaks")
	public Map<ContainerSection, List<Slot>> getContainerSections() {

		Map<ContainerSection, List<Slot>> slotRefs = new THashMap<ContainerSection, List<Slot>>();

		slotRefs.put(ContainerSection.INVENTORY, inventorySlots.subList(0, 36));
		slotRefs.put(ContainerSection.INVENTORY_NOT_HOTBAR, inventorySlots.subList(0, 27));
		slotRefs.put(ContainerSection.INVENTORY_HOTBAR, inventorySlots.subList(27, 36));
		slotRefs.put(ContainerSection.CHEST, inventorySlots.subList(36, inventorySlots.size()));

		return slotRefs;
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
