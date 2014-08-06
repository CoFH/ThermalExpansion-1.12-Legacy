package thermalexpansion.gui.container.machine;

import cofh.core.util.CoreUtils;
import cofh.lib.gui.container.ContainerFalse;
import cofh.lib.gui.slot.SlotCraftingLocked;
import cofh.lib.gui.slot.SlotEnergy;
import cofh.lib.gui.slot.SlotFalseCopy;
import cofh.lib.gui.slot.SlotRemoveOnly;
import cofh.lib.gui.slot.SlotSpecificItem;
import cofh.lib.util.helpers.ItemHelper;

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
import thermalexpansion.item.TEItems;
import thermalexpansion.network.PacketTEBase;
import thermalexpansion.util.SchematicHelper;

public class ContainerAssembler extends ContainerTEBase implements ISchematicContainer {

	TileAssembler myTile;
	InventoryCrafting craftMatrix = new InventoryCrafting(this, 3, 3);
	IInventory craftResult = new InventoryCraftResult();

	public Slot craftSlots[] = new Slot[9];
	public Slot resultSlot;

	public ContainerAssembler(InventoryPlayer inventory, TileEntity tile) {

		super(inventory, tile);

		myTile = (TileAssembler) tile;
		for (int i = 0; i < 2; i++) {
			for (int j = 0; j < 9; j++) {
				addSlotToContainer(new Slot(myTile, 3 + j + i * 9, 8 + j * 18, 74 + i * 18));
			}
		}
		addSlotToContainer(new SlotSpecificItem(myTile, 0, 56, 34, TEItems.diagramSchematic).setSlotStackLimit(1));
		addSlotToContainer(new SlotRemoveOnly(myTile, 1, 116, 35));
		addSlotToContainer(new SlotEnergy(myTile, myTile.getChargeSlot(), 8, 53));

		/* Crafting Grid */
		for (int i = 0; i < 9; i++) {
			craftSlots[i] = addSlotToContainer(new SlotFalseCopy(craftMatrix, i, 0, 0));
		}
		resultSlot = addSlotToContainer(new SlotCraftingLocked(inventory.player, craftMatrix, craftResult, 0, 0, 0));
	}

	@Override
	protected void addPlayerInventory(InventoryPlayer inventory) {

		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 9; j++) {
				addSlotToContainer(new Slot(inventory, j + i * 9 + 9, 8 + j * 18, 123 + i * 18));
			}
		}
		for (int i = 0; i < 9; i++) {
			addSlotToContainer(new Slot(inventory, i, 8 + i * 18, 181));
		}
	}

	@Override
	public ItemStack slotClick(int slot, int x, int y, EntityPlayer player) {

		if (slot == resultSlot.slotNumber && resultSlot.getHasStack() && CoreUtils.isClient()) {
			if (SchematicHelper.isSchematic(myTile.getStackInSlot(0))) {
				PacketTEBase.sendTabSchematicPacketToServer();
				writeSchematic();
			}
		}
		return super.slotClick(slot, x, y, player);
	}

	@Override
	public void onCraftMatrixChanged(IInventory inventory) {

		InventoryCrafting craftMatrixTemp = new InventoryCrafting(new ContainerFalse(), 3, 3);
		this.craftResult.setInventorySlotContents(0, ItemHelper.findMatchingRecipe(this.craftMatrix, myTile.getWorldObj()));
	}

	/* ISetSchematic */
	@Override
	public void writeSchematic() {

		ItemStack schematic = myTile.getStackInSlot(0);

		if (schematic != null && resultSlot.getHasStack()) {
			ItemStack newSchematic = SchematicHelper.writeNBTToSchematic(schematic,
					SchematicHelper.getNBTForSchematic(craftMatrix, craftResult.getStackInSlot(0)));
			newSchematic.stackSize = schematic.stackSize;
			myTile.setInventorySlotContents(0, newSchematic);
			for (int i = 0; i < 9; i++) {
				craftSlots[i].putStack(null);
			}
			resultSlot.putStack(null);
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
