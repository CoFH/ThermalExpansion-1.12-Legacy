package thermalexpansion.gui.container.machine;

import cofh.gui.slot.ISlotValidator;
import cofh.gui.slot.SlotEnergy;
import cofh.gui.slot.SlotValidated;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

import thermalexpansion.block.machine.TileCrucible;
import thermalexpansion.gui.container.ContainerTEBase;
import thermalexpansion.util.crafting.CrucibleManager;

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
