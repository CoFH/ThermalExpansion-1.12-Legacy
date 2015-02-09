package cofh.thermalexpansion.gui.container.machine;

import cofh.lib.gui.slot.ISlotValidator;
import cofh.lib.gui.slot.SlotEnergy;
import cofh.lib.gui.slot.SlotValidated;
import cofh.thermalexpansion.block.machine.TileCrucible;
import cofh.thermalexpansion.gui.container.ContainerTEBase;
import cofh.thermalexpansion.util.crafting.CrucibleManager;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

public class ContainerCrucible extends ContainerTEBase implements ISlotValidator {

	TileCrucible myTile;

	public ContainerCrucible(InventoryPlayer inventory, TileEntity tile) {

		super(inventory, tile);

		myTile = (TileCrucible) tile;
		addSlotToContainer(new SlotValidated(this, myTile, 0, 56, 26));
		addSlotToContainer(new SlotEnergy(myTile, myTile.getChargeSlot(), 8, 53));
	}

	@Override
	public boolean isItemValid(ItemStack stack) {

		return CrucibleManager.recipeExists(stack);
	}

}
