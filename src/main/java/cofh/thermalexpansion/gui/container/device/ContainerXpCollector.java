package cofh.thermalexpansion.gui.container.device;

import cofh.core.gui.slot.ISlotValidator;
import cofh.core.gui.slot.SlotValidated;
import cofh.thermalexpansion.block.device.TileXpCollector;
import cofh.thermalexpansion.gui.container.ContainerTEBase;
import cofh.thermalexpansion.util.managers.device.XpCollectorManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

public class ContainerXpCollector extends ContainerTEBase implements ISlotValidator {

	TileXpCollector myTile;

	public ContainerXpCollector(InventoryPlayer inventory, TileEntity tile) {

		super(inventory, tile);

		myTile = (TileXpCollector) tile;
		addSlotToContainer(new SlotValidated(this, myTile, 0, 35, 35));
	}

	@Override
	public boolean isItemValid(ItemStack stack) {

		return XpCollectorManager.getCatalystFactor(stack) > 0;
	}

}
