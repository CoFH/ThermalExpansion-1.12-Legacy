package cofh.thermalexpansion.gui.container.machine;

import cofh.core.gui.container.ContainerTileAugmentable;
import cofh.core.gui.slot.ISlotValidator;
import cofh.core.gui.slot.SlotEnergy;
import cofh.core.gui.slot.SlotValidated;
import cofh.thermalexpansion.block.machine.TileBrewer;
import cofh.thermalexpansion.util.managers.machine.BrewerManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

public class ContainerBrewer extends ContainerTileAugmentable implements ISlotValidator {

	TileBrewer myTile;

	public ContainerBrewer(InventoryPlayer inventory, TileEntity tile) {

		super(inventory, tile);

		myTile = (TileBrewer) tile;
		addSlotToContainer(new SlotValidated(this, myTile, 0, 80, 34));
		addSlotToContainer(new SlotEnergy(myTile, myTile.getChargeSlot(), 8, 53));
	}

	@Override
	public boolean isItemValid(ItemStack stack) {

		return BrewerManager.isItemValid(stack);
	}

}
