package cofh.thermalexpansion.gui.container.machine;

import cofh.lib.gui.slot.ISlotValidator;
import cofh.lib.gui.slot.SlotEnergy;
import cofh.lib.gui.slot.SlotRemoveOnly;
import cofh.lib.gui.slot.SlotValidated;
import cofh.thermalexpansion.block.machine.TileSawmill;
import cofh.thermalexpansion.gui.container.ContainerTEBase;
import cofh.thermalexpansion.util.crafting.SawmillManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

public class ContainerSawmill extends ContainerTEBase implements ISlotValidator {

	protected TileSawmill myTile;

	public ContainerSawmill(InventoryPlayer inventory, TileEntity tile) {

		super(inventory, tile);

		myTile = (TileSawmill) tile;
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
