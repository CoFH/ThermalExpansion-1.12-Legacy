package thermalexpansion.gui.container.machine;

import cofh.CoFHCore;
import cofh.gui.container.ContainerFalse;
import cofh.gui.slot.SlotCraftingLocked;
import cofh.gui.slot.SlotEnergy;
import cofh.gui.slot.SlotFalseCopy;
import cofh.gui.slot.SlotRemoveOnly;
import cofh.gui.slot.SlotSpecificItem;
import cofh.util.ItemHelper;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCraftResult;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

import thermalexpansion.block.machine.TileAssembler;
import thermalexpansion.gui.container.ContainerTEBase;
import thermalexpansion.gui.container.ISchematicContainer;
import thermalexpansion.item.SchematicHelper;
import thermalexpansion.item.TEItems;
import thermalexpansion.network.GenericTEPacket;

public class ContainerAssembler extends ContainerTEBase implements ISchematicContainer {

	TileAssembler myTile;

	// Schematic Tab Stuff
	InventoryCrafting craftMatrix = new InventoryCrafting(this, 3, 3);
	IInventory craftResult = new InventoryCraftResult();
	InventoryPlayer playerInv;
	Slot craftSlots[] = new Slot[9];
	Slot resultSlot;

	public ContainerAssembler(InventoryPlayer inventory, TileEntity entity) {

		super(entity);

		myTile = (TileAssembler) entity;
		playerInv = inventory;
		addSlotToContainer(new SlotSpecificItem(myTile, 0, 56, 34, TEItems.diagramSchematic).setSlotStackLimit(1));
		addSlotToContainer(new SlotRemoveOnly(myTile, 1, 116, 35));
		addSlotToContainer(new SlotEnergy(myTile, myTile.getChargeSlot(), 8, 53));

		for (int i = 0; i < 2; ++i) {
			for (int j = 0; j < 9; ++j) {
				addSlotToContainer(new Slot(myTile, j + i * 9 + 3, 8 + j * 18, 74 + i * 18));
			}
		}
		for (int i = 0; i < 3; ++i) {
			for (int j = 0; j < 9; ++j) {
				addSlotToContainer(new Slot(inventory, j + i * 9 + 9, 8 + j * 18, 123 + i * 18));
			}
		}
		for (int i = 0; i < 9; ++i) {
			addSlotToContainer(new Slot(inventory, i, 8 + i * 18, 181));
		}

		for (int i = 0; i < 9; i++) {
			craftSlots[i] = addSlotToContainer(new SlotFalseCopy(craftMatrix, i, 0, 0));
		}
		resultSlot = addSlotToContainer(new SlotCraftingLocked(inventory.player, craftMatrix, craftResult, 0, 0, 0));
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int i) {

		ItemStack stack = null;
		Slot slot = (Slot) inventorySlots.get(i);

		int invTile = myTile.inventory.length;
		int invPlayer = invTile + 27;
		int invFull = invTile + 36;

		if (slot != null && slot.getHasStack()) {
			ItemStack stackInSlot = slot.getStack();
			stack = stackInSlot.copy();

			if (i < invTile) {
				if (!mergeItemStack(stackInSlot, invTile, invFull, true)) {
					return null;
				}
			} else {
				if (SchematicHelper.isSchematic(stackInSlot)) {
					if (!mergeItemStack(stackInSlot, 0, 1, false)) {
						return null;
					}
				} else if (i >= invTile && i < invFull) {
					if (!mergeItemStack(stackInSlot, 3, invTile, false)) {
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
	public ItemStack slotClick(int slot, int x, int y, EntityPlayer player) {

		if (slot == 66 && CoFHCore.proxy.isClient()) {
			ItemStack stack = myTile.getStackInSlot(0);

			if (stack != null) {
				if (SchematicHelper.isSchematic(stack)) {
					GenericTEPacket.sendCreateSchematicPacketToServer();
				}
			}
		}
		return super.slotClick(slot, x, y, player);
	}

	@Override
	public void onCraftMatrixChanged(IInventory inventory) {

		InventoryCrafting craftMatrixTemp = new InventoryCrafting(new ContainerFalse(), 3, 3);
		this.craftResult.setInventorySlotContents(0, ItemHelper.findMatchingRecipe(this.craftMatrix, playerInv.player.worldObj));
	}

	/* ISetSchematic */
	@Override
	public void writeSchematic() {

		ItemStack theSchematic = myTile.getStackInSlot(0);

		if (theSchematic != null && craftResult.getStackInSlot(0) != null) {
			int theStackSize = theSchematic.stackSize;
			ItemStack newSchematic = SchematicHelper.getSchematic(SchematicHelper.getNBTForSchematic(craftMatrix, craftResult.getStackInSlot(0)));
			newSchematic.stackSize = theStackSize;
			myTile.setInventorySlotContents(0, newSchematic);
			for (int i = 0; i < 9; i++) {
				craftSlots[i].putStack(null);
			}
		}
	}

	@Override
	public boolean canWriteSchematic() {

		return SchematicHelper.isSchematic(myTile.getStackInSlot(0)) && craftResult.getStackInSlot(0) != null;
	}

	@Override
	public Slot[] getCraftingSlots() {

		return craftSlots;
	}

	@Override
	public Slot getResultSlot() {

		return resultSlot;
	}

}
