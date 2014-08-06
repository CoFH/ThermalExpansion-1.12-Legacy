package thermalexpansion.gui.container.machine;

import cofh.lib.gui.slot.ISlotValidator;
import cofh.lib.gui.slot.SlotEnergy;
import cofh.lib.gui.slot.SlotValidated;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.SlotFurnace;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

import thermalexpansion.block.machine.TileFurnace;
import thermalexpansion.gui.container.ContainerTEBase;
import thermalexpansion.util.crafting.FurnaceManager;

public class ContainerFurnace extends ContainerTEBase implements ISlotValidator {

	TileFurnace myTile;

	public ContainerFurnace(InventoryPlayer inventory, TileEntity tile) {

		super(inventory, tile);

		myTile = (TileFurnace) tile;
		addSlotToContainer(new SlotValidated(this, myTile, 0, 56, 26));
		addSlotToContainer(new SlotFurnace(inventory.player, myTile, 1, 116, 35));
		addSlotToContainer(new SlotEnergy(myTile, myTile.getChargeSlot(), 8, 53));
	}

	@Override
	public boolean isItemValid(ItemStack stack) {

		return FurnaceManager.recipeExists(stack);
	}

}
