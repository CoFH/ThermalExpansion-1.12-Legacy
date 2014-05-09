package thermalexpansion.gui.client.device;

import cofh.gui.GuiBaseAdv;
import cofh.gui.element.ElementEnergyStored;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

import thermalexpansion.block.device.TileTinkerBench;
import thermalexpansion.core.TEProps;
import thermalexpansion.gui.container.device.ContainerTinkersBench;

public class GuiTinkersBench extends GuiBaseAdv {

	static final ResourceLocation TEXTURE = new ResourceLocation(TEProps.PATH_GUI + "Workbench.png");

	TileTinkerBench myTile;

	public GuiTinkersBench(InventoryPlayer inventory, TileEntity theTile) {

		super(new ContainerTinkersBench(inventory, theTile), TEXTURE);
		myTile = (TileTinkerBench) theTile;
		name = myTile.getName();
		ySize = 205;
	}

	@Override
	public void initGui() {

		super.initGui();

		addElement(new ElementEnergyStored(this, 8, 8, myTile.getEnergyStorage()));
	}

}
