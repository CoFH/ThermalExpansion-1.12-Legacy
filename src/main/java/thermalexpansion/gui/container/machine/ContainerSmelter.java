package thermalexpansion.gui.container.machine;

import cofh.gui.slot.ISlotValidator;
import cofh.gui.slot.SlotEnergy;
import cofh.gui.slot.SlotOutput;
import cofh.gui.slot.SlotValidated;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

import thermalexpansion.block.machine.TileSmelter;
import thermalexpansion.gui.container.ContainerTEBase;
import thermalexpansion.util.crafting.SmelterManager;

public class ContainerSmelter extends ContainerTEBase implements ISlotValidator {

	TileSmelter myTile;

	public ContainerSmelter(InventoryPlayer inventory, TileEntity entity) {

		super(inventory, entity);

		myTile = (TileSmelter) entity;
		addSlotToContainer(new SlotValidated(this, myTile, 0, 32, 26));
		addSlotToContainer(new SlotValidated(this, myTile, 1, 56, 26));
		addSlotToContainer(new SlotOutput(myTile, 2, 116, 26));
		addSlotToContainer(new SlotOutput(myTile, 3, 134, 26));
		addSlotToContainer(new SlotOutput(myTile, 4, 116, 53));
		addSlotToContainer(new SlotEnergy(myTile, myTile.getChargeSlot(), 8, 53));
	}

	@Override
	public boolean isItemValid(ItemStack stack) {

		return SmelterManager.isItemValid(stack);
	}

}
