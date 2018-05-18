package cofh.thermalexpansion.gui.container.device;

import cofh.api.core.IFilterable;
import cofh.core.gui.container.ContainerTileAugmentable;
import cofh.core.gui.slot.SlotFalseCopy;
import cofh.core.network.PacketCore;
import cofh.core.util.CoreUtils;
import cofh.core.util.filter.ItemFilterWrapperTile;
import cofh.thermalexpansion.block.device.TileItemBuffer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.tileentity.TileEntity;

public class ContainerItemBufferFilter extends ContainerTileAugmentable implements IFilterable {

	final TileItemBuffer myTile;
	final ItemFilterWrapperTile filterWrapper;

	public ContainerItemBufferFilter(InventoryPlayer inventory, TileEntity tile) {

		super(inventory, tile);

		myTile = (TileItemBuffer) tile;
		filterWrapper = new ItemFilterWrapperTile(myTile, myTile.getFilter());

		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				addSlotToContainer(new SlotFalseCopy(filterWrapper, j + i * 3, 35 + j * 18, 17 + i * 18));
			}
		}
	}

	/* IFilterable */
	public void setFlag(int flag, boolean value) {

		filterWrapper.getFilter().setFlag(flag, value);
		if (CoreUtils.isClient()) {
			PacketCore.sendFilterPacketToServer(flag, value);
		}
		filterWrapper.markDirty();
	}

}
