package thermalexpansion.gui.container.machine;

import cofh.gui.slot.ISlotValidator;
import cofh.gui.slot.SlotEnergy;
import cofh.gui.slot.SlotRemoveOnly;
import cofh.gui.slot.SlotValidated;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

import thermalexpansion.block.machine.TileSawmill;
import thermalexpansion.gui.container.ContainerTEBase;
import thermalexpansion.util.crafting.SawmillManager;

public class ContainerSawmill extends ContainerTEBase implements ISlotValidator {

	TileSawmill myTile;

	public ContainerSawmill(InventoryPlayer inventory, TileEntity entity) {

		super(inventory, entity);

		myTile = (TileSawmill) entity;
		addSlotToContainer(new SlotValidated(this, myTile, 0, 56, 26));
		addSlotToContainer(new SlotRemoveOnly(myTile, 1, 116, 26));
		addSlotToContainer(new SlotRemoveOnly(myTile, 2, 134, 26));
		addSlotToContainer(new SlotRemoveOnly(myTile, 3, 116, 53));
		addSlotToContainer(new SlotEnergy(myTile, myTile.getChargeSlot(), 8, 53));
	}

	@Override
	public boolean isItemValid(ItemStack stack) {

		return SawmillManager.recipeExists(stack);
	}

}
