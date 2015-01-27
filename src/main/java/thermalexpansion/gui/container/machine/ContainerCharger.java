package thermalexpansion.gui.container.machine;

import cofh.lib.gui.slot.ISlotValidator;
import cofh.lib.gui.slot.SlotEnergy;
import cofh.lib.gui.slot.SlotRemoveOnly;
import cofh.lib.gui.slot.SlotValidated;
import cofh.lib.util.helpers.EnergyHelper;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

import thermalexpansion.block.machine.TileCharger;
import thermalexpansion.gui.container.ContainerTEBase;
import thermalexpansion.util.crafting.ChargerManager;

public class ContainerCharger extends ContainerTEBase implements ISlotValidator {

	TileCharger myTile;

	public ContainerCharger(InventoryPlayer inventory, TileEntity tile) {

		super(inventory, tile);

		myTile = (TileCharger) tile;
		addSlotToContainer(new SlotValidated(this, myTile, 0, 44, 35));
		addSlotToContainer(new SlotRemoveOnly(myTile, 1, 80, 35));
		addSlotToContainer(new SlotRemoveOnly(myTile, 2, 125, 35));
		addSlotToContainer(new SlotEnergy(myTile, myTile.getChargeSlot(), 8, 53));
	}

	@Override
	public boolean isItemValid(ItemStack stack) {

		return ChargerManager.recipeExists(stack) || EnergyHelper.isEnergyContainerItem(stack);
	}

}
