package thermalexpansion.gui.container.device;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCraftResult;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import thermalexpansion.block.device.TileWorkbench;
import thermalexpansion.gui.container.ContainerTEBase;
import thermalexpansion.gui.container.ISetSchematic;
import thermalexpansion.gui.slot.SlotCustomCraftingOutput;
import thermalexpansion.gui.slot.SlotSpecificItemWorkbench;
import thermalexpansion.item.SchematicHelper;
import thermalexpansion.item.TEItems;
import cofh.gui.slot.SlotFalseCopy;
import cofh.util.ItemHelper;
import cofh.util.ServerHelper;
import cofh.util.inventory.InventoryCraftingCustom;

public class ContainerWorkbench extends ContainerTEBase implements ISetSchematic {

	TileWorkbench myTile;
	public InventoryPlayer playerInv;
	public InventoryCraftingCustom craftMatrix;
	public IInventory craftResult = new InventoryCraftResult();
	public SlotCustomCraftingOutput myOutput;

	int INV_TILE_START = 13;

	public ContainerWorkbench(InventoryPlayer inventory, TileEntity entity) {

		super(entity);

		myTile = (TileWorkbench) entity;
		playerInv = inventory;
		craftMatrix = new InventoryCraftingCustom(this, 3, 3, myTile, myTile.getMatrixOffset());
		myOutput = new SlotCustomCraftingOutput(inventory.player, craftResult, 0, 143, 37, myTile, this);
		addSlotToContainer(myOutput);
		addSlotToContainer(new SlotSpecificItemWorkbench(myTile, 18, 17, 17, TEItems.diagramSchematic).setSlotStackLimit(1));
		addSlotToContainer(new SlotSpecificItemWorkbench(myTile, 19, 17, 37, TEItems.diagramSchematic).setSlotStackLimit(1));
		addSlotToContainer(new SlotSpecificItemWorkbench(myTile, 20, 17, 57, TEItems.diagramSchematic).setSlotStackLimit(1));

		// Fake Crafting Grid
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				this.addSlotToContainer(new SlotFalseCopy(craftMatrix, j + i * 3, 44 + j * 18, 19 + i * 18));
			}
		}
		for (int i = 0; i < 2; i++) {
			for (int j = 0; j < 9; j++) {
				addSlotToContainer(new Slot(myTile, j + i * 9, 8 + j * 18, 79 + i * 18));
			}
		}
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 9; j++) {
				addSlotToContainer(new Slot(inventory, j + i * 9 + 9, 8 + j * 18, 128 + i * 18));
			}
		}
		for (int i = 0; i < 9; i++) {
			addSlotToContainer(new Slot(inventory, i, 8 + i * 18, 186));
		}
		onCraftMatrixChanged(null);
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int i) {

		ItemStack stack = null;
		Slot slot = (Slot) inventorySlots.get(i);

		int invTile = myTile.getSizeInventory() - 3 + INV_TILE_START;
		int invPlayer = invTile + 27;
		int invFull = invTile + 36;

		if (slot != null && slot.getHasStack()) {
			ItemStack stackInSlot = slot.getStack();
			stack = stackInSlot.copy();

			if (i == 0) {
				if (!mergeItemStack(stackInSlot, invTile, invFull, false)) {
					return null;
				}
				slot.onSlotChange(stackInSlot, stack);
			}
			if (i >= 1 && i <= 3) {
				if (!mergeItemStack(stackInSlot, invTile, invPlayer, false)) {
					return null;
				}
			} else if (i != 0) {
				if (i >= INV_TILE_START && i < invTile) {
					if (!mergeItemStack(stackInSlot, invTile, invFull, true)) {
						return null;
					}
				} else if (i >= invTile && i < invFull) {
					if (SchematicHelper.isSchematic(stackInSlot)) {
						if (!mergeItemStack(stackInSlot, 1, 4, false)) {
							return null;
						}
					} else if (!mergeItemStack(stackInSlot, INV_TILE_START, invTile, false)) {
						return null;
					}
				}
			}
			if (stackInSlot.stackSize == 0) {
				slot.putStack((ItemStack) null);
			} else {
				slot.onSlotChanged();
			}
			if (stackInSlot.stackSize == stack.stackSize) {
				return null;
			}
			slot.onPickupFromSlot(player, stackInSlot);
		}
		return stack;
	}

	@Override
	public void onCraftMatrixChanged(IInventory inventory) {

		this.craftResult.setInventorySlotContents(0, ItemHelper.findMatchingRecipe(this.craftMatrix, playerInv.player.worldObj));
	}

	/* ISetSchematic */
	@Override
	public void writeSchematic() {

		ItemStack theSchematic = myTile.getStackInSlot(myTile.selectedSchematic + 18);

		if (theSchematic != null && craftResult.getStackInSlot(0) != null) {
			int theStackSize = theSchematic.stackSize;
			ItemStack newSchematic = SchematicHelper.getSchematic(SchematicHelper.getNBTForSchematic(craftMatrix, craftResult.getStackInSlot(0)));
			newSchematic.stackSize = theStackSize;
			myTile.setInventorySlotContents(myTile.selectedSchematic + 18, newSchematic);
		}
	}

	public boolean hasSchematic() {

		return SchematicHelper.isSchematic(myTile.getStackInSlot(myTile.getCurrentSchematicSlot()));
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

	@Override
	public ItemStack slotClick(int slotId, int mouseButton, int modifier, EntityPlayer player) {

		if (ServerHelper.isClientWorld(player.worldObj)) {
			ItemStack result = super.slotClick(slotId, mouseButton, modifier, player);
			int invTile = myTile.getSizeInventory() - 3 + INV_TILE_START;

			if (slotId >= INV_TILE_START && slotId < invTile) {
				myTile.createItemClient(false, myOutput.getStackNoUpdate());
			}
			return result;
		}
		return super.slotClick(slotId, mouseButton, modifier, player);
	}

}
