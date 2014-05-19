package thermalexpansion.gui.client.machine;

import cofh.core.CoFHProps;
import cofh.gui.GuiBaseAdv;
import cofh.gui.element.ElementBase;
import cofh.gui.element.ElementDualScaled;
import cofh.gui.element.ElementFluid;
import cofh.gui.element.ElementFluidTank;
import cofh.gui.element.TabConfiguration;
import cofh.gui.element.TabInfo;
import cofh.gui.element.TabRedstone;
import cofh.gui.element.TabTutorial;
import cofh.util.FluidHelper;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

import thermalexpansion.block.machine.TileExtruder;
import thermalexpansion.core.TEProps;
import thermalexpansion.gui.container.machine.ContainerExtruder;
import thermalexpansion.gui.element.ElementSlotOverlay;

public class GuiExtruder extends GuiBaseAdv {

	static final ResourceLocation TEXTURE = new ResourceLocation(TEProps.PATH_GUI_MACHINE + "Extruder.png");
	static final String INFO = "Mixes water and lava to make different types of igneous rock.\n\nThe selected item is what you'll get, if there is enough water and lava in the tanks.\n\nMC physics is fun!";

	TileExtruder myTile;

	ElementBase slotInputLava;
	ElementBase slotInputWater;
	ElementBase slotOutput;
	ElementFluid progressLava;
	ElementFluid progressWater;
	ElementDualScaled progressLavaOverlay;
	ElementDualScaled progressWaterOverlay;

	public GuiExtruder(InventoryPlayer inventory, TileEntity theTile) {

		super(new ContainerExtruder(inventory, theTile), TEXTURE);
		myTile = (TileExtruder) theTile;
		name = myTile.getInventoryName();
	}

	@Override
	public void initGui() {

		super.initGui();

		slotInputLava = addElement(new ElementSlotOverlay(this, 8, 9).setSlotInfo(0, 3, 2));
		slotInputWater = addElement(new ElementSlotOverlay(this, 152, 9).setSlotInfo(0, 3, 2));
		slotOutput = addElement(new ElementSlotOverlay(this, 76, 45).setSlotInfo(3, 1, 2));

		addElement(new ElementFluidTank(this, 8, 9, myTile.getTank(0)));
		addElement(new ElementFluidTank(this, 152, 9, myTile.getTank(1)));
		progressLava = (ElementFluid) addElement(new ElementFluid(this, 40, 49).setFluid(FluidHelper.LAVA).setSize(24, 16));
		progressWater = (ElementFluid) addElement(new ElementFluid(this, 112, 49).setFluid(FluidHelper.WATER).setSize(24, 16));
		progressLavaOverlay = (ElementDualScaled) addElement(new ElementDualScaled(this, 40, 49).setMode(1).setBackground(false).setSize(24, 16)
				.setTexture(TEX_DROP_RIGHT, 48, 16));
		progressWaterOverlay = (ElementDualScaled) addElement(new ElementDualScaled(this, 112, 49).setMode(2).setBackground(false).setSize(24, 16)
				.setTexture(TEX_DROP_LEFT, 48, 16));

		addTab(new TabRedstone(this, myTile));
		addTab(new TabConfiguration(this, myTile));
		addTab(new TabInfo(this, INFO));
		addTab(new TabTutorial(this, CoFHProps.tutorialTabRedstone + "\n\n" + CoFHProps.tutorialTabConfiguration));
	}

	@Override
	protected void updateElements() {

		slotInputWater.setVisible(myTile.hasSide(1));
		slotInputLava.setVisible(myTile.hasSide(1));
		slotOutput.setVisible(myTile.hasSide(2));

		progressLava.setSize(myTile.getScaledProgress(PROGRESS), 16);
		progressWater.setPosition(112 + PROGRESS - myTile.getScaledProgress(PROGRESS), 49);
		progressWater.setSize(myTile.getScaledProgress(PROGRESS), 16);
		progressLavaOverlay.setQuantity(myTile.getScaledProgress(PROGRESS));
		progressWaterOverlay.setQuantity(myTile.getScaledProgress(PROGRESS));
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int x, int y) {

		super.drawGuiContainerBackgroundLayer(f, x, y);

		mc.renderEngine.bindTexture(texture);
		drawCurSelection();
		drawPrevSelection();
	}

	@Override
	protected void mouseClicked(int mX, int mY, int mButton) {

		if (48 <= mouseX && mouseX < 128 && 18 <= mouseY && mouseY < 36) {
			if (49 <= mouseX && mouseX < 67) {
				myTile.setMode(0);
			} else if (79 <= mouseX && mouseX < 97) {
				myTile.setMode(1);
			} else if (109 <= mouseX && mouseX < 127) {
				myTile.setMode(2);
			}
		} else {
			super.mouseClicked(mX, mY, mButton);
		}
	}

	protected void drawCurSelection() {

		int offset = 32;
		if (myTile.getPrevSelection() == myTile.getCurSelection() && myTile.isActive()) {
			offset = 64;
		}
		switch (myTile.getCurSelection()) {
		case 0:
			drawTexturedModalRect(guiLeft + 42, guiTop + 11, 192, offset, 32, 32);
			break;
		case 1:
			drawTexturedModalRect(guiLeft + 72, guiTop + 11, 192, offset, 32, 32);
			break;
		case 2:
			drawTexturedModalRect(guiLeft + 102, guiTop + 11, 192, offset, 32, 32);
			break;
		}
	}

	protected void drawPrevSelection() {

		switch (myTile.getPrevSelection()) {
		case 0:
			drawTexturedModalRect(guiLeft + 42, guiTop + 11, 224, 32, 32, 32);
			break;
		case 1:
			drawTexturedModalRect(guiLeft + 72, guiTop + 11, 224, 32, 32, 32);
			break;
		case 2:
			drawTexturedModalRect(guiLeft + 102, guiTop + 11, 224, 32, 32, 32);
			break;
		}
	}

}
