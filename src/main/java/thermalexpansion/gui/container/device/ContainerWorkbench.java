package thermalexpansion.gui.container.device;

import cofh.lib.gui.container.CustomInventoryWrapper;
import cofh.lib.gui.slot.SlotFalseCopy;
import cofh.lib.inventory.InventoryCraftingCustom;
import cofh.lib.util.helpers.ItemHelper;
import cofh.lib.util.helpers.ServerHelper;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCraftResult;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

import thermalexpansion.block.device.TileWorkbench;
import thermalexpansion.gui.container.ContainerTEBase;
import thermalexpansion.gui.container.ISchematicContainer;
import thermalexpansion.gui.slot.SlotCraftingOutputWorkbench;
import thermalexpansion.gui.slot.SlotSpecificItemWorkbench;
import thermalexpansion.item.TEItems;
import thermalexpansion.util.SchematicHelper;

public class ContainerWorkbench extends ContainerTEBase implements ISchematicContainer {

	TileWorkbench myTile;
	InventoryCraftingCustom craftMatrix;
	IInventory craftResult = new InventoryCraftResult();

	public SlotCraftingOutputWorkbench myOutput;

	public ContainerWorkbench(InventoryPlayer inventory, TileEntity tile) {

		super(tile);

		myTile = (TileWorkbench) tile;
		addPlayerSlotsToContainer(inventory, 8, 128);
		for (int i = 0; i < 2; i++) {
			for (int j = 0; j < 9; j++) {
				addSlotToContainer(new Slot(myTile, 3 + j + i * 9, 8 + j * 18, 79 + i * 18));
			}
		}
		addSlotToContainer(new SlotSpecificItemWorkbench(myTile, 2, 17, 57, TEItems.diagramSchematic).setSlotStackLimit(1));
		addSlotToContainer(new SlotSpecificItemWorkbench(myTile, 1, 17, 37, TEItems.diagramSchematic).setSlotStackLimit(1));
		addSlotToContainer(new SlotSpecificItemWorkbench(myTile, 0, 17, 17, TEItems.diagramSchematic).setSlotStackLimit(1));

		craftMatrix = new InventoryCraftingCustom(this, 3, 3, new CustomInventoryWrapper(myTile, 0), 0);
		myOutput = new SlotCraftingOutputWorkbench(myTile, this, inventory.player, craftResult, 0, 143, 37);
		addSlotToContainer(myOutput);

		/* Crafting Grid */
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				this.addSlotToContainer(new SlotFalseCopy(craftMatrix, j + i * 3, 44 + j * 18, 19 + i * 18));
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

	@Override
	public void onCraftMatrixChanged(IInventory inventory) {

		this.craftResult.setInventorySlotContents(0, ItemHelper.findMatchingRecipe(this.craftMatrix, myTile.getWorldObj()));
	}

	@Override
	public ItemStack slotClick(int slotId, int mouseButton, int modifier, EntityPlayer player) {

		if (slotId == 57) {
			modifier = 0;
		}
		if (mouseButton == 1 && modifier == 1 && slotId >= 54 && slotId < 57) {
			Slot slot = (Slot) inventorySlots.get(slotId);
			if (slot.getHasStack()) {
				myTile.setCurrentSchematicSlot(slot.getSlotIndex());
				myTile.setCraftingGrid();
				modifier = 0;
				slotId = 57;
			}
		}
		if (ServerHelper.isClientWorld(player.worldObj)) {
			ItemStack result = super.slotClick(slotId, mouseButton, modifier, player);
			if (slotId >= 36 && slotId < 36 + myTile.getSizeInventory()) {
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
					SchematicHelper.getNBTForSchematic(craftMatrix, craftResult.getStackInSlot(0)));
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

}
