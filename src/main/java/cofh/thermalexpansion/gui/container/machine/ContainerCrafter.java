package cofh.thermalexpansion.gui.container.machine;

import cofh.core.gui.slot.SlotEnergy;
import cofh.core.gui.slot.SlotFalseCopy;
import cofh.core.gui.slot.SlotRemoveOnly;
import cofh.thermalexpansion.block.machine.TileCrafter;
import cofh.thermalexpansion.gui.container.ContainerTEBase;
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

	public ContainerCrafter(InventoryPlayer inventory, TileEntity tile) {

		super(inventory, tile);

		myTile = (TileCrafter) tile;
		for (int i = 0; i < 2; i++) {
			for (int j = 0; j < 9; j++) {
				addSlotToContainer(new Slot(myTile, j + i * 9, 8 + j * 18, 77 + i * 18));
			}
		}
		addSlotToContainer(new SlotRemoveOnly(myTile, TileCrafter.SLOT_OUTPUT, 125, 35));
		addSlotToContainer(new SlotEnergy(myTile, myTile.getChargeSlot(), 8, 53));

		/* Crafting Grid */
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				addSlotToContainer(new SlotFalseCopy(myTile, TileCrafter.SLOT_CRAFTING_START + j + i * 3, 35 + j * 18, 17 + i * 18));
			}
		}
	}

	@Override
	protected int getPlayerInventoryVerticalOffset() {

		return 126;
	}

}
