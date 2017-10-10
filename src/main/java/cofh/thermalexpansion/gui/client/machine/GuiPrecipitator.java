package cofh.thermalexpansion.gui.client.machine;

import cofh.core.gui.element.*;
import cofh.thermalexpansion.block.machine.TilePrecipitator;
import cofh.thermalexpansion.gui.client.GuiPoweredBase;
import cofh.thermalexpansion.gui.container.machine.ContainerPrecipitator;
import cofh.thermalexpansion.gui.element.ElementSlotOverlay;
import cofh.thermalexpansion.gui.element.ElementSlotOverlay.SlotColor;
import cofh.thermalexpansion.gui.element.ElementSlotOverlay.SlotRender;
import cofh.thermalexpansion.gui.element.ElementSlotOverlay.SlotType;
import cofh.thermalexpansion.init.TEProps;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

import java.io.IOException;

public class GuiPrecipitator extends GuiPoweredBase {

	public static final ResourceLocation TEXTURE = new ResourceLocation(TEProps.PATH_GUI_MACHINE + "precipitator.png");

	private TilePrecipitator myTile;

	private ElementBase slotInput;
	private ElementBase slotOutput;
	private ElementFluid progressFluid;
	private ElementDualScaled progressOverlay;
	private ElementDualScaled speed;

	public GuiPrecipitator(InventoryPlayer inventory, TileEntity tile) {

		super(new ContainerPrecipitator(inventory, tile), tile, inventory.player, TEXTURE);

		generateInfo("tab.thermalexpansion.machine.precipitator");

		myTile = (TilePrecipitator) tile;
	}

	@Override
	public void initGui() {

		super.initGui();

		slotInput = addElement(new ElementSlotOverlay(this, 152, 9).setSlotInfo(SlotColor.BLUE, SlotType.TANK, SlotRender.FULL));
		slotOutput = addElement(new ElementSlotOverlay(this, 76, 45).setSlotInfo(SlotColor.ORANGE, SlotType.OUTPUT, SlotRender.FULL));

		if (!myTile.smallStorage()) {
			addElement(new ElementEnergyStored(this, 8, 8, myTile.getEnergyStorage()));
		}
		addElement(new ElementFluidTank(this, 152, 9, myTile.getTank()).setAlwaysShow(true));
		progressFluid = (ElementFluid) addElement(new ElementFluid(this, 112, 49).setFluid(myTile.getTankFluid()).setSize(24, 16));
		progressOverlay = (ElementDualScaled) addElement(new ElementDualScaled(this, 112, 49).setMode(2).setBackground(false).setSize(24, 16).setTexture(TEX_DROP_LEFT, 64, 16));
		speed = (ElementDualScaled) addElement(new ElementDualScaled(this, 44, 49).setSize(16, 16).setTexture(TEX_SNOWFLAKE, 32, 16));
	}

	@Override
	protected void updateElementInformation() {

		super.updateElementInformation();

		slotInput.setVisible(myTile.hasSideType(INPUT_ALL) || baseTile.hasSideType(OMNI));
		slotOutput.setVisible(myTile.hasSideType(OUTPUT_ALL) || baseTile.hasSideType(OMNI));

		progressFluid.setPosition(112 + PROGRESS - myTile.getScaledProgress(PROGRESS), 49);
		progressFluid.setSize(myTile.getScaledProgress(PROGRESS), 16);
		progressOverlay.setQuantity(myTile.getScaledProgress(PROGRESS));
		speed.setQuantity(myTile.getScaledSpeed(SPEED));
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int x, int y) {

		super.drawGuiContainerBackgroundLayer(f, x, y);

		mc.renderEngine.bindTexture(texture);
		drawCurSelection();
		drawPrevSelection();
	}

	@Override
	protected void mouseClicked(int mX, int mY, int mButton) throws IOException {

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

	private void drawCurSelection() {

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

	private void drawPrevSelection() {

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
