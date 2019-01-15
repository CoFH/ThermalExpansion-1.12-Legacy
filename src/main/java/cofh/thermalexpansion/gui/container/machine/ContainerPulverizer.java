package cofh.thermalexpansion.gui.container.machine;

import cofh.core.gui.container.ContainerTileAugmentable;
import cofh.core.gui.slot.ISlotValidator;
import cofh.core.gui.slot.SlotEnergy;
import cofh.core.gui.slot.SlotRemoveOnly;
import cofh.core.gui.slot.SlotValidated;
import cofh.thermalexpansion.block.machine.TilePulverizer;
import cofh.thermalexpansion.util.managers.machine.PulverizerManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

public class ContainerPulverizer extends ContainerTileAugmentable implements ISlotValidator {

	TilePulverizer myTile;

	public ContainerPulverizer(InventoryPlayer inventory, TileEntity tile) {

		super(inventory, tile);

		myTile = (TilePulverizer) tile;
		addSlotToContainer(new SlotValidated(this, myTile, 0, 53, 26));
		addSlotToContainer(new SlotRemoveOnly(myTile, 1, 116, 26));
		addSlotToContainer(new SlotRemoveOnly(myTile, 2, 116, 53));
		addSlotToContainer(new SlotEnergy(myTile, myTile.getChargeSlot(), 8, 53));
	}

	@Override
	public boolean isItemValid(ItemStack stack) {

		return PulverizerManager.recipeExists(stack);
	}

}
