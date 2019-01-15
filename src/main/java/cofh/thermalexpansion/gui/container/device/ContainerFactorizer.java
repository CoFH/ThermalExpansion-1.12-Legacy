package cofh.thermalexpansion.gui.container.device;

import cofh.core.gui.container.ContainerTileAugmentable;
import cofh.core.gui.slot.ISlotValidator;
import cofh.core.gui.slot.SlotRemoveOnly;
import cofh.core.gui.slot.SlotValidated;
import cofh.thermalexpansion.block.device.TileFactorizer;
import cofh.thermalexpansion.util.managers.device.FactorizerManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

public class ContainerFactorizer extends ContainerTileAugmentable implements ISlotValidator {

	TileFactorizer myTile;

	public ContainerFactorizer(InventoryPlayer inventory, TileEntity tile) {

		super(inventory, tile);

		myTile = (TileFactorizer) tile;
		addSlotToContainer(new SlotValidated(this, myTile, 0, 44, 26));
		addSlotToContainer(new SlotRemoveOnly(myTile, 1, 116, 26));
	}

	@Override
	public boolean isItemValid(ItemStack stack) {

		return FactorizerManager.recipeExists(stack, myTile.recipeMode);
	}

}
