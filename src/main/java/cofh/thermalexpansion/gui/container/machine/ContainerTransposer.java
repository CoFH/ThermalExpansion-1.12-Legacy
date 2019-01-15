package cofh.thermalexpansion.gui.container.machine;

import cofh.core.gui.container.ContainerTileAugmentable;
import cofh.core.gui.slot.ISlotValidator;
import cofh.core.gui.slot.SlotEnergy;
import cofh.core.gui.slot.SlotRemoveOnly;
import cofh.core.gui.slot.SlotValidated;
import cofh.core.util.helpers.FluidHelper;
import cofh.thermalexpansion.block.machine.TileTransposer;
import cofh.thermalexpansion.util.managers.machine.TransposerManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

public class ContainerTransposer extends ContainerTileAugmentable implements ISlotValidator {

	TileTransposer myTile;

	public ContainerTransposer(InventoryPlayer inventory, TileEntity tile) {

		super(inventory, tile);

		myTile = (TileTransposer) tile;
		addSlotToContainer(new SlotValidated(this, myTile, 0, 44, 19));
		addSlotToContainer(new SlotRemoveOnly(myTile, 1, 80, 19));
		addSlotToContainer(new SlotRemoveOnly(myTile, 2, 80, 49));
		addSlotToContainer(new SlotEnergy(myTile, myTile.getChargeSlot(), 8, 53));
	}

	@Override
	public boolean isItemValid(ItemStack stack) {

		return TransposerManager.isItemValid(stack) || FluidHelper.isFluidHandler(stack);
	}

}
