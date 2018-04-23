package cofh.thermalexpansion.gui.container.machine;

import cofh.core.gui.slot.ISlotValidator;
import cofh.core.gui.slot.SlotEnergy;
import cofh.core.gui.slot.SlotValidated;
import cofh.core.util.helpers.ItemHelper;
import cofh.thermalexpansion.block.machine.TileFurnace;
import cofh.thermalexpansion.gui.container.ContainerTEBase;
import cofh.thermalexpansion.util.managers.machine.FurnaceManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.SlotFurnaceOutput;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

public class ContainerFurnace extends ContainerTEBase implements ISlotValidator {

	TileFurnace myTile;

	public ContainerFurnace(InventoryPlayer inventory, TileEntity tile) {

		super(inventory, tile);

		myTile = (TileFurnace) tile;
		addSlotToContainer(new SlotValidated(this, myTile, 0, 53, 26));
		addSlotToContainer(new SlotFurnaceOutput(inventory.player, myTile, 1, 116, 35));
		addSlotToContainer(new SlotEnergy(myTile, myTile.getChargeSlot(), 8, 53));
	}

	@Override
	public boolean isItemValid(ItemStack stack) {

		if (myTile.augmentFood() && !FurnaceManager.isFood(stack)) {
			return false;
		}
		if (myTile.augmentOre() && !ItemHelper.isOre(stack)) {
			return false;
		}
		return FurnaceManager.recipeExists(stack, myTile.augmentPyrolysis());
	}

}
