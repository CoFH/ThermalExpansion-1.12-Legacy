package thermalexpansion.gui.container.machine;

import cofh.gui.slot.ISlotValidator;
import cofh.gui.slot.SlotEnergy;
import cofh.gui.slot.SlotOutput;
import cofh.gui.slot.SlotValidated;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

import thermalexpansion.block.machine.TilePulverizer;
import thermalexpansion.gui.container.ContainerTEBase;
import thermalexpansion.util.crafting.PulverizerManager;

public class ContainerPulverizer extends ContainerTEBase implements ISlotValidator {

	TilePulverizer myTile;

	public ContainerPulverizer(InventoryPlayer inventory, TileEntity entity) {

		super(inventory, entity);

		myTile = (TilePulverizer) entity;
		addSlotToContainer(new SlotValidated(this, myTile, 0, 56, 26));
		addSlotToContainer(new SlotOutput(myTile, 1, 116, 26));
		addSlotToContainer(new SlotOutput(myTile, 2, 134, 26));
		addSlotToContainer(new SlotOutput(myTile, 3, 116, 53));
		addSlotToContainer(new SlotEnergy(myTile, myTile.getChargeSlot(), 8, 53));
	}

	@Override
	public boolean isItemValid(ItemStack stack) {

		return PulverizerManager.recipeExists(stack);
	}

}
