package cofh.thermalexpansion.gui.container.machine;

import cofh.core.gui.slot.*;
import cofh.thermalexpansion.block.machine.TileCrafter;
import cofh.thermalexpansion.gui.container.ContainerTEBase;
import cofh.thermalfoundation.item.ItemDiagram;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCraftResult;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.tileentity.TileEntity;

public class ContainerCrafter extends ContainerTEBase {

	protected TileCrafter myTile;
	protected InventoryCrafting craftMatrix = new InventoryCrafting(this, 3, 3);
	protected IInventory craftResult = new InventoryCraftResult();

	private Slot craftSlots[] = new Slot[9];
	private Slot resultSlot;

	public ContainerCrafter(InventoryPlayer inventory, TileEntity tile) {

		super(inventory, tile);

		myTile = (TileCrafter) tile;
		for (int i = 0; i < 2; i++) {
			for (int j = 0; j < 9; j++) {
				addSlotToContainer(new Slot(myTile, 3 + j + i * 9, 8 + j * 18, 74 + i * 18));
			}
		}
		addSlotToContainer(new SlotSpecificItem(myTile, 0, 56, 34, ItemDiagram.schematic).setSlotStackLimit(1));
		addSlotToContainer(new SlotRemoveOnly(myTile, 1, 116, 35));
		addSlotToContainer(new SlotEnergy(myTile, myTile.getChargeSlot(), 8, 53));

		/* Crafting Grid */
		for (int i = 0; i < 9; i++) {
			craftSlots[i] = addSlotToContainer(new SlotFalseCopy(craftMatrix, i, 0, 0));
		}
		resultSlot = addSlotToContainer(new SlotCraftingLocked(inventory.player, craftMatrix, craftResult, 0, 0, 0));
	}

}
