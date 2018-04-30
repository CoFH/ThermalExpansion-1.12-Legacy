package cofh.thermalexpansion.gui.container.device;

import cofh.core.gui.container.ContainerTileAugmentable;
import cofh.core.gui.slot.ISlotValidator;
import cofh.core.gui.slot.SlotValidated;
import cofh.thermalexpansion.block.device.TileChunkLoader;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

public class ContainerChunkLoader extends ContainerTileAugmentable implements ISlotValidator {

	TileChunkLoader myTile;

	public ContainerChunkLoader(InventoryPlayer inventory, TileEntity tile) {

		super(inventory, tile);

		myTile = (TileChunkLoader) tile;
		addSlotToContainer(new SlotValidated(this, myTile, 0, 35, 35));
	}

	@Override
	public boolean isItemValid(ItemStack stack) {

		return stack.getItem().equals(Items.ENDER_PEARL);
	}

}
