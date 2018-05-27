package cofh.thermalexpansion.gui.container.machine;

import cofh.core.gui.container.ContainerTileAugmentable;
import cofh.core.gui.slot.ISlotValidator;
import cofh.core.gui.slot.SlotEnergy;
import cofh.core.gui.slot.SlotValidated;
import cofh.thermalexpansion.block.machine.TileCrucible;
import cofh.thermalexpansion.util.managers.machine.CrucibleManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

public class ContainerCrucible extends ContainerTileAugmentable implements ISlotValidator {

	TileCrucible myTile;

	public ContainerCrucible(InventoryPlayer inventory, TileEntity tile) {

		super(inventory, tile);

		myTile = (TileCrucible) tile;
		addSlotToContainer(new SlotValidated(this, myTile, 0, 53, 26));
		addSlotToContainer(new SlotEnergy(myTile, myTile.getChargeSlot(), 8, 53));
	}

	@Override
	public boolean isItemValid(ItemStack stack) {

		if (myTile.augmentLava() && !CrucibleManager.isLava(stack)) {
			return false;
		}
		return CrucibleManager.recipeExists(stack);
	}

}
