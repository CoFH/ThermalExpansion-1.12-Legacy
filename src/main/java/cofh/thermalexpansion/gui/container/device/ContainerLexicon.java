package cofh.thermalexpansion.gui.container.device;

import cofh.core.gui.slot.ISlotValidator;
import cofh.core.gui.slot.SlotRemoveOnly;
import cofh.core.gui.slot.SlotValidated;
import cofh.core.util.helpers.ItemHelper;
import cofh.thermalexpansion.block.device.TileLexicon;
import cofh.thermalexpansion.gui.container.ContainerTEBase;
import cofh.thermalexpansion.gui.slot.SlotLexicon;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

public class ContainerLexicon extends ContainerTEBase implements ISlotValidator {

	TileLexicon myTile;

	public ContainerLexicon(InventoryPlayer inventory, TileEntity tile) {

		super(inventory, tile);

		myTile = (TileLexicon) tile;
		addSlotToContainer(new SlotValidated(this, myTile, 0, 26, 35));
		addSlotToContainer(new SlotRemoveOnly(myTile, 1, 134, 35));

		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				addSlotToContainer(new SlotLexicon(myTile, 2 + j + i * 3, 62 + j * 18, 17 + i * 18));
			}
		}
	}

	@Override
	public boolean isItemValid(ItemStack stack) {

		return myTile.hasPreferredStack(stack) && !ItemHelper.itemsIdentical(stack, myTile.getPreferredStack(stack));
	}

}
