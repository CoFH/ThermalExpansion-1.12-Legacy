package cofh.thermalexpansion.gui.container.machine;

import cofh.lib.gui.slot.ISlotValidator;
import cofh.lib.gui.slot.SlotEnergy;
import cofh.lib.gui.slot.SlotRemoveOnly;
import cofh.lib.gui.slot.SlotValidated;
import cofh.thermalexpansion.block.machine.TileCentrifuge;
import cofh.thermalexpansion.gui.container.ContainerTEBase;
import cofh.thermalexpansion.util.managers.machine.CentrifugeManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

public class ContainerCentrifuge extends ContainerTEBase implements ISlotValidator {

	protected TileCentrifuge myTile;

	public ContainerCentrifuge(InventoryPlayer inventory, TileEntity tile) {

		super(inventory, tile);

		myTile = (TileCentrifuge) tile;
		addSlotToContainer(new SlotValidated(this, myTile, 0, 44, 26));
		addSlotToContainer(new SlotRemoveOnly(myTile, 1, 107, 26));
		addSlotToContainer(new SlotRemoveOnly(myTile, 2, 125, 26));
		addSlotToContainer(new SlotRemoveOnly(myTile, 3, 107, 44));
		addSlotToContainer(new SlotRemoveOnly(myTile, 4, 127, 44));
		addSlotToContainer(new SlotEnergy(myTile, myTile.getChargeSlot(), 8, 53));
	}

	@Override
	public boolean isItemValid(ItemStack stack) {

		return CentrifugeManager.recipeExists(stack);
	}

}
