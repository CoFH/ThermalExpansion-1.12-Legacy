package cofh.thermalexpansion.gui.container.device;

import cofh.core.gui.slot.ISlotValidator;
import cofh.core.gui.slot.SlotValidated;
import cofh.thermalexpansion.block.device.TileDiffuser;
import cofh.thermalexpansion.gui.container.ContainerTEBase;
import cofh.thermalexpansion.util.managers.device.DiffuserManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

public class ContainerDiffuser extends ContainerTEBase implements ISlotValidator {

	TileDiffuser myTile;

	public ContainerDiffuser(InventoryPlayer inventory, TileEntity tile) {

		super(inventory, tile);

		myTile = (TileDiffuser) tile;
		addSlotToContainer(new SlotValidated(this, myTile, 0, 35, 35));
	}

	@Override
	public boolean isItemValid(ItemStack stack) {

		return DiffuserManager.isValidReagent(stack);
	}

}
