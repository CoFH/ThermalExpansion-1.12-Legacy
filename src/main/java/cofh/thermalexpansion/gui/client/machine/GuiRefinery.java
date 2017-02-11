package cofh.thermalexpansion.gui.client.machine;

import cofh.lib.gui.element.*;
import cofh.thermalexpansion.block.machine.TileRefinery;
import cofh.thermalexpansion.gui.client.GuiPoweredBase;
import cofh.thermalexpansion.gui.container.machine.ContainerRefinery;
import cofh.thermalexpansion.gui.element.ElementSlotOverlay;
import cofh.thermalexpansion.init.TEProps;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

public class GuiRefinery extends GuiPoweredBase {

	public static final ResourceLocation TEXTURE = new ResourceLocation(TEProps.PATH_GUI_MACHINE + "refinery.png");

	private TileRefinery myTile;

	private ElementBase slotInput;
	private ElementSlotOverlay[] slotOutput = new ElementSlotOverlay[2];
	private ElementSlotOverlay[] slotTankOutput = new ElementSlotOverlay[2];

	private ElementFluid progressFluid;
	private ElementDualScaled progressOverlay;
	private ElementDualScaled speed;

	public GuiRefinery(InventoryPlayer inventory, TileEntity tile) {

		super(new ContainerRefinery(inventory, tile), tile, inventory.player, TEXTURE);

		generateInfo("tab.thermalexpansion.machine.refinery", 3);

		myTile = (TileRefinery) tile;
	}

	@Override
	public void initGui() {

		super.initGui();

		slotInput = addElement(new ElementSlotOverlay(this, 44, 20).setSlotInfo(0, 4, 2));
		slotOutput[0] = (ElementSlotOverlay) addElement(new ElementSlotOverlay(this, 112, 31).setSlotInfo(3, 1, 2));
		slotOutput[1] = (ElementSlotOverlay) addElement(new ElementSlotOverlay(this, 112, 31).setSlotInfo(2, 1, 1));
		slotTankOutput[0] = (ElementSlotOverlay) addElement(new ElementSlotOverlay(this, 152, 9).setSlotInfo(3, 3, 2).setVisible(false));
		slotTankOutput[1] = (ElementSlotOverlay) addElement(new ElementSlotOverlay(this, 152, 9).setSlotInfo(1, 3, 1).setVisible(false));

		addElement(new ElementEnergyStored(this, 8, 8, baseTile.getEnergyStorage()));
		addElement(new ElementFluidTank(this, 44, 20, myTile.getTank(0)).setGauge(0).setAlwaysShow(true).setShort());
		addElement(new ElementFluidTank(this, 152, 9, myTile.getTank(1)).setGauge(0).setAlwaysShow(true));

		progressFluid = (ElementFluid) addElement(new ElementFluid(this, 76, 34).setFluid(myTile.getTankFluid(0)).setSize(24, 16));
		progressOverlay = (ElementDualScaled) addElement(new ElementDualScaled(this, 76, 34).setMode(1).setBackground(false).setSize(24, 16).setTexture(TEX_DROP_RIGHT, 64, 16));
		speed = (ElementDualScaled) addElement(new ElementDualScaled(this, 44, 52).setSize(16, 16).setTexture(TEX_FLAME, 32, 16));
	}

	@Override
	protected void updateElementInformation() {

		super.updateElementInformation();

		slotInput.setVisible(baseTile.hasSide(1));
		slotOutput[0].setVisible(baseTile.hasSide(4));
		slotOutput[1].setVisible(baseTile.hasSide(3));

		slotTankOutput[0].setVisible(baseTile.hasSide(4));
		slotTankOutput[1].setVisible(baseTile.hasSide(2));

		if (!baseTile.hasSide(4)) {
			slotOutput[1].slotRender = 2;
			slotTankOutput[1].slotRender = 2;
		} else {
			slotOutput[1].slotRender = 1;
			slotTankOutput[1].slotRender = 1;
		}
		progressFluid.setFluid(myTile.getTankFluid(0));
		progressFluid.setSize(baseTile.getScaledProgress(PROGRESS), 16);
		progressOverlay.setQuantity(baseTile.getScaledProgress(PROGRESS));
		speed.setQuantity(baseTile.getScaledSpeed(SPEED));

	}

}
