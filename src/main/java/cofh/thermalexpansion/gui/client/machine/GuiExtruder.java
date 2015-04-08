package cofh.thermalexpansion.gui.client.machine;

import cofh.lib.gui.element.ElementBase;
import cofh.lib.gui.element.ElementDualScaled;
import cofh.lib.gui.element.ElementFluid;
import cofh.lib.gui.element.ElementFluidTank;
import cofh.lib.util.helpers.FluidHelper;
import cofh.thermalexpansion.block.machine.TileExtruder;
import cofh.thermalexpansion.core.TEProps;
import cofh.thermalexpansion.gui.client.GuiAugmentableBase;
import cofh.thermalexpansion.gui.container.machine.ContainerExtruder;
import cofh.thermalexpansion.gui.element.ElementSlotOverlay;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

public class GuiExtruder extends GuiAugmentableBase {

	static final ResourceLocation TEXTURE = new ResourceLocation(TEProps.PATH_GUI_MACHINE + "Extruder.png");

	TileExtruder myTile;

	ElementBase slotInputLava;
	ElementBase slotInputWater;
	ElementBase slotOutput;
	ElementFluid progressLava;
	ElementFluid progressWater;
	ElementDualScaled progressLavaOverlay;
	ElementDualScaled progressWaterOverlay;

	public GuiExtruder(InventoryPlayer inventory, TileEntity tile) {

		super(new ContainerExtruder(inventory, tile), tile, inventory.player, TEXTURE);

		generateInfo("tab.thermalexpansion.machine.extruder", 3);

		myTile = (TileExtruder) tile;
	}

	@Override
	public void initGui() {

		super.initGui();

		slotInputLava = addElement(new ElementSlotOverlay(this, 8, 9).setSlotInfo(0, 3, 2));
		slotInputWater = addElement(new ElementSlotOverlay(this, 152, 9).setSlotInfo(0, 3, 2));
		slotOutput = addElement(new ElementSlotOverlay(this, 76, 45).setSlotInfo(3, 1, 2));

		addElement(new ElementFluidTank(this, 8, 9, myTile.getTank(0)).setAlwaysShow(true));
		addElement(new ElementFluidTank(this, 152, 9, myTile.getTank(1)).setAlwaysShow(true));
		progressLava = (ElementFluid) addElement(new ElementFluid(this, 40, 49).setFluid(FluidHelper.LAVA).setSize(24, 16));
		progressWater = (ElementFluid) addElement(new ElementFluid(this, 112, 49).setFluid(FluidHelper.WATER).setSize(24, 16));
		progressLavaOverlay = (ElementDualScaled) addElement(new ElementDualScaled(this, 40, 49).setMode(1).setBackground(false).setSize(24, 16)
				.setTexture(TEX_DROP_RIGHT, 64, 16));
		progressWaterOverlay = (ElementDualScaled) addElement(new ElementDualScaled(this, 112, 49).setMode(2).setBackground(false).setSize(24, 16)
				.setTexture(TEX_DROP_LEFT, 64, 16));
	}

	@Override
	protected void updateElementInformation() {

		super.updateElementInformation();

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
	protected void mouseClicked(int mX, int mY, int mouseButton) {

		if (48 <= mouseX && mouseX < 128 && 18 <= mouseY && mouseY < 36) {
			if (49 <= mouseX && mouseX < 67) {
				myTile.setMode(0);
			} else if (79 <= mouseX && mouseX < 97) {
				myTile.setMode(1);
			} else if (109 <= mouseX && mouseX < 127) {
				myTile.setMode(2);
			}
		} else {
			super.mouseClicked(mX, mY, mouseButton);
		}
	}

	protected void drawCurSelection() {

		int offset = 32;
		if (myTile.getPrevSelection() == myTile.getCurSelection() && myTile.isActive) {
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
