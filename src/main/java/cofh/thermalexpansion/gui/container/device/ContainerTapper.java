package cofh.thermalexpansion.gui.container.device;

import cofh.core.gui.slot.ISlotValidator;
import cofh.core.gui.slot.SlotValidated;
import cofh.thermalexpansion.block.device.TileTapper;
import cofh.thermalexpansion.gui.container.ContainerTEBase;
import cofh.thermalexpansion.util.managers.device.TapperManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

public class ContainerTapper extends ContainerTEBase implements ISlotValidator {

	TileTapper myTile;

	public ContainerTapper(InventoryPlayer inventory, TileEntity tile) {

		super(inventory, tile);

		myTile = (TileTapper) tile;
		addSlotToContainer(new SlotValidated(this, myTile, 0, 35, 35));
	}

	@Override
	public boolean isItemValid(ItemStack stack) {

		return TapperManager.getFertilizerMultiplier(stack) > 0;
	}

}
