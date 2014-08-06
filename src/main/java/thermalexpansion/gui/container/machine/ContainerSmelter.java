package thermalexpansion.gui.container.machine;

import cofh.lib.gui.slot.ISlotValidator;
import cofh.lib.gui.slot.SlotEnergy;
import cofh.lib.gui.slot.SlotRemoveOnly;
import cofh.lib.gui.slot.SlotValidated;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

import thermalexpansion.block.machine.TileSmelter;
import thermalexpansion.gui.container.ContainerTEBase;
import thermalexpansion.util.crafting.SmelterManager;

public class ContainerSmelter extends ContainerTEBase implements ISlotValidator {

	TileSmelter myTile;

	public ContainerSmelter(InventoryPlayer inventory, TileEntity tile) {

		super(inventory, tile);

		myTile = (TileSmelter) tile;
		addSlotToContainer(new SlotValidated(this, myTile, 0, 32, 26));
		addSlotToContainer(new SlotValidated(this, myTile, 1, 56, 26));
		addSlotToContainer(new SlotRemoveOnly(myTile, 2, 116, 26));
		addSlotToContainer(new SlotRemoveOnly(myTile, 3, 134, 26));
		addSlotToContainer(new SlotRemoveOnly(myTile, 4, 116, 53));
		addSlotToContainer(new SlotEnergy(myTile, myTile.getChargeSlot(), 8, 53));
	}

	@Override
	public boolean isItemValid(ItemStack stack) {

		return SmelterManager.isItemValid(stack);
	}

}
