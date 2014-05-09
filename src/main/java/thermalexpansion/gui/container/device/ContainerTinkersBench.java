package thermalexpansion.gui.container.device;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

import thermalexpansion.block.device.TileTinkerBench;
import thermalexpansion.gui.container.ContainerTEBase;

public class ContainerTinkersBench extends ContainerTEBase {

	TileTinkerBench myTile;

	public ContainerTinkersBench(InventoryPlayer inventory, TileEntity entity) {

		super(entity);

		myTile = (TileTinkerBench) entity;
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int i) {

		return null;
	}

}
