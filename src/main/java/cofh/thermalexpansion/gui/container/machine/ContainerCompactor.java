package cofh.thermalexpansion.gui.container.machine;

import cofh.lib.gui.slot.ISlotValidator;
import cofh.lib.gui.slot.SlotEnergy;
import cofh.lib.gui.slot.SlotRemoveOnly;
import cofh.lib.gui.slot.SlotValidated;
import cofh.thermalexpansion.block.machine.TileCompactor;
import cofh.thermalexpansion.gui.container.ContainerTEBase;
import cofh.thermalexpansion.util.crafting.CompactorManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

public class ContainerCompactor extends ContainerTEBase implements ISlotValidator {

	TileCompactor myTile;

	public ContainerCompactor(InventoryPlayer inventory, TileEntity tile) {

		super(inventory, tile);

		myTile = (TileCompactor) tile;
		addSlotToContainer(new SlotValidated(this, myTile, 0, 53, 26));
		addSlotToContainer(new SlotRemoveOnly(myTile, 1, 116, 35));
		addSlotToContainer(new SlotEnergy(myTile, myTile.getChargeSlot(), 8, 53));
	}

	@Override
	public boolean isItemValid(ItemStack stack) {

		return CompactorManager.isItemValid(stack);
	}

}
