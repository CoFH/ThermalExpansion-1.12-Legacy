package cofh.thermalexpansion.gui.container;

import cofh.lib.gui.container.CustomInventoryWrapper;
import cofh.lib.gui.slot.SlotFalseCopy;
import cofh.lib.inventory.InventoryCraftingCustom;
import cofh.lib.util.helpers.ItemHelper;
import cofh.lib.util.helpers.ServerHelper;
import cofh.thermalexpansion.block.workbench.TileWorkbench;
import cofh.thermalexpansion.gui.slot.SlotCraftingOutputWorkbench;
import cofh.thermalexpansion.gui.slot.SlotSpecificItemWorkbench;
import cofh.thermalexpansion.item.TEItems;
import cofh.thermalexpansion.util.helpers.SchematicHelper;
import cpw.mods.fml.common.Optional;

import gnu.trove.map.hash.THashMap;

import invtweaks.api.container.ChestContainer.RowSizeCallback;
import invtweaks.api.container.ContainerSection;
import invtweaks.api.container.ContainerSectionCallback;

import java.util.List;
import java.util.Map;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCraftResult;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

public class ContainerWorkbench extends ContainerTEBase implements ISchematicContainer {

	TileWorkbench myTile;
	IInventory craftResult = new InventoryCraftResult();

	int type = 1;
	int numSchematic = 3;
	int numInventory = 18;
	int rowSize = 9;

	public InventoryCraftingCustom craftMatrix;
	public SlotCraftingOutputWorkbench myOutput;

	public ContainerWorkbench(InventoryPlayer inventory, TileEntity tile) {

		super(tile);

		myTile = (TileWorkbench) tile;

		type = myTile.getType();
		numSchematic = TileWorkbench.SCHEMATICS[type];
		numInventory = TileWorkbench.INVENTORY[type];
		int gridXOffset = 44;

		switch (type) {
		case 1:
			addPlayerSlotsToContainer(inventory, 8, 128);
			addSchematicSlotsToContainer(17, 18, 3, 1);
			addInventorySlotsToContainer(myTile, 8, 79, 2, 9);
			myOutput = new SlotCraftingOutputWorkbench(myTile, this, inventory.player, craftResult, 0, 143, 37);
			rowSize = 9;
			break;
		case 2:
			addPlayerSlotsToContainer(inventory, 8, 146);
			addSchematicSlotsToContainer(10, 18, 3, 2);
			addInventorySlotsToContainer(myTile, 8, 79, 3, 9);
			myOutput = new SlotCraftingOutputWorkbench(myTile, this, inventory.player, craftResult, 0, 143, 37);
			rowSize = 9;
			gridXOffset = 54;
			break;
		case 3:
			addPlayerSlotsToContainer(inventory, 26, 146);
			addSchematicSlotsToContainer(16, 18, 3, 3);
			addInventorySlotsToContainer(myTile, 8, 79, 3, 11);
			myOutput = new SlotCraftingOutputWorkbench(myTile, this, inventory.player, craftResult, 0, 179, 37);
			rowSize = 11;
			gridXOffset = 80;
			break;
		default:
			addPlayerSlotsToContainer(inventory, 35, 146);
			addSchematicSlotsToContainer(16, 18, 3, 4);
			addInventorySlotsToContainer(myTile, 8, 79, 3, 12);
			myOutput = new SlotCraftingOutputWorkbench(myTile, this, inventory.player, craftResult, 0, 197, 37);
			rowSize = 13;
			gridXOffset = 98;
		}
		craftMatrix = new InventoryCraftingCustom(this, 3, 3, new CustomInventoryWrapper(myTile, 0), 0);
		addSlotToContainer(myOutput);

		/* Crafting Grid */
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				this.addSlotToContainer(new SlotFalseCopy(craftMatrix, j + i * 3, gridXOffset + j * 18, 19 + i * 18));
			}
		}
		onCraftMatrixChanged(null);
	}

	private void addPlayerSlotsToContainer(InventoryPlayer inventory, int xOffset, int yOffset) {

		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 9; j++) {
				addSlotToContainer(new Slot(inventory, j + i * 9 + 9, xOffset + j * 18, yOffset + i * 18));
			}
		}
		for (int i = 0; i < 9; i++) {
			addSlotToContainer(new Slot(inventory, i, xOffset + i * 18, yOffset + 58));
		}
	}

	private void addInventorySlotsToContainer(IInventory inventory, int xOffset, int yOffset, int rows, int cols) {

		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				addSlotToContainer(new Slot(inventory, numSchematic + j + i * cols, xOffset + j * 18, yOffset + i * 18));
			}
		}
	}

	private void addSchematicSlotsToContainer(int xOffset, int yOffset, int rows, int cols) {

		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				addSlotToContainer(new SlotSpecificItemWorkbench(myTile, j + i * cols, xOffset + j * 19, yOffset + i * 19, TEItems.diagramSchematic)
						.setSlotStackLimit(1));
			}
		}
	}

	@Override
	public void onCraftMatrixChanged(IInventory inventory) {

		this.craftResult.setInventorySlotContents(0, ItemHelper.findMatchingRecipe(this.craftMatrix, myTile.getWorldObj()));
	}

	@Override
	public ItemStack slotClick(int slotId, int mouseButton, int modifier, EntityPlayer player) {

		myTile.updateClient = true;

		int invPlayer = 36;
		int invSchematic = invPlayer + numSchematic;
		int invTile = invSchematic + numInventory;

		if (slotId == invTile) {
			modifier = 0;
		}
		if (slotId >= invPlayer && slotId < invSchematic) {
			Slot slot = (Slot) inventorySlots.get(slotId);
			if (slot.getHasStack()) {
				int schematic = myTile.getCurrentSchematicSlot();
				myTile.setCurrentSchematicSlot(slot.getSlotIndex());

				if (mouseButton == 1 && modifier == 1) {
					myTile.setCraftingGrid();
					modifier = 0;
					slotId = invTile;
				} else if (schematic != myTile.getCurrentSchematicSlot()) {
					return player.inventory.getItemStack();
				}
			}
		}
		if (ServerHelper.isClientWorld(player.worldObj)) {
			ItemStack result = super.slotClick(slotId, mouseButton, modifier, player);
			if (slotId >= invSchematic && slotId < invTile) {
				myTile.createItemClient(false, myOutput.getStackNoUpdate());
			}
			return result;
		}
		return super.slotClick(slotId, mouseButton, modifier, player);
	}

	public boolean hasSchematic() {

		return SchematicHelper.isSchematic(myTile.getStackInSlot(myTile.getCurrentSchematicSlot()));
	}

	/* ISchematicContainer */
	@Override
	public void writeSchematic() {

		ItemStack schematic = myTile.getStackInSlot(myTile.selectedSchematic);

		if (schematic != null && craftResult.getStackInSlot(0) != null) {
			ItemStack newSchematic = SchematicHelper.writeNBTToSchematic(schematic,
					SchematicHelper.getNBTForSchematic(craftMatrix, myTile.getWorldObj(), craftResult.getStackInSlot(0)));
			newSchematic.stackSize = schematic.stackSize;
			myTile.setInventorySlotContents(myTile.selectedSchematic, newSchematic);
		}
	}

	@Override
	public boolean canWriteSchematic() {

		return hasSchematic() && craftResult.getStackInSlot(0) != null;
	}

	@Override
	public Slot[] getCraftingSlots() {

		return null;
	}

	@Override
	public Slot getResultSlot() {

		return null;
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
		slotRefs.put(ContainerSection.CHEST, inventorySlots.subList(36, 36 + numInventory));

		return slotRefs;
	}

}
