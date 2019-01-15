package cofh.thermalexpansion.gui.container.storage;

import cofh.api.item.IInventoryContainerItem;
import cofh.core.gui.container.ContainerTileAugmentable;
import cofh.core.gui.slot.ISlotValidator;
import cofh.core.gui.slot.SlotValidated;
import cofh.core.util.helpers.MathHelper;
import cofh.thermalexpansion.block.storage.TileStrongbox;
import invtweaks.api.container.ChestContainer;
import invtweaks.api.container.ChestContainer.RowSizeCallback;
import invtweaks.api.container.ContainerSection;
import invtweaks.api.container.ContainerSectionCallback;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.common.Optional;

import java.util.List;
import java.util.Map;

@ChestContainer ()
public class ContainerStrongbox extends ContainerTileAugmentable implements ISlotValidator {

	int storageIndex;
	int rowSize;
	TileStrongbox myTile;

	public ContainerStrongbox(InventoryPlayer inventory, TileEntity tile) {

		super(tile);

		myTile = (TileStrongbox) tile;
		myTile.openInventory(inventory.player);

		storageIndex = myTile.getStorageIndex();
		rowSize = MathHelper.clamp(storageIndex, 9, 14);

		int rows = MathHelper.clamp(storageIndex, 2, 9);
		int slots = rowSize * rows;
		int yOffset = 17;

		bindPlayerInventory(inventory);

		if (storageIndex == 0) {
			addSlotToContainer(new SlotValidated(this, myTile, 0, 80, 26));
			rowSize = 1;
		} else {
			for (int i = 0; i < slots; i++) {
				addSlotToContainer(new SlotValidated(this, myTile, i, 8 + i % rowSize * 18, yOffset + i / rowSize * 18));
			}
		}
	}

	@Override
	protected int getPlayerInventoryVerticalOffset() {

		return 30 + 18 * MathHelper.clamp(storageIndex, 2, 9);
	}

	@Override
	protected int getPlayerInventoryHorizontalOffset() {

		return 8 + 9 * (rowSize - 9);
	}

	@Override
	public void onContainerClosed(EntityPlayer player) {

		super.onContainerClosed(player);
		myTile.closeInventory(player);
	}

	public TileStrongbox getTile() {

		return myTile;
	}

	/* ISlotValidator */
	@Override
	public boolean isItemValid(ItemStack stack) {

		return !stack.isEmpty() && (!(stack.getItem() instanceof IInventoryContainerItem) || ((IInventoryContainerItem) stack.getItem()).getSizeInventory(stack) <= 0);
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
