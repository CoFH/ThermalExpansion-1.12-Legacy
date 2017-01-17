package cofh.thermalexpansion.gui.element;

import cofh.core.CoFHProps;
import cofh.lib.gui.GuiBase;
import cofh.lib.gui.element.ElementBase;
import cofh.lib.render.RenderHelper;
import cofh.thermalexpansion.init.TEProps;

public class ElementSlotOverlayAssembler extends ElementBase {

	public int slotColor;
	public int slotRender;

	public ElementSlotOverlayAssembler(GuiBase gui, int posX, int posY) {

		super(gui, posX, posY);
		this.texture = TEProps.textureGuiAssembler;
	}

	public ElementSlotOverlayAssembler setSlotInfo(int color, int render) {

		slotColor = color;
		slotRender = render;
		return this;
	}

	public ElementSlotOverlayAssembler setSlotColor(int color) {

		slotColor = color;
		return this;
	}

	public ElementSlotOverlayAssembler setSlotRender(int render) {

		slotRender = render;
		return this;
	}

	@Override
	public void drawBackground(int mouseX, int mouseY, float gameTicks) {

		if (!isVisible()) {
			return;
		}
		RenderHelper.bindTexture(texture);
		if (CoFHProps.enableGUISlotBorders) {
			drawSlotWithBorder(posX, posY);
		} else {
			drawSlotNoBorder(posX, posY);
		}
	}

	@Override
	public void drawForeground(int mouseX, int mouseY) {

		return;
	}

	@Override
	public boolean intersectsWith(int mouseX, int mouseY) {

		return false;
	}

	protected void drawSlotNoBorder(int x, int y) {

		sizeX = 160;
		sizeY = 34;
		int offsetX = 8;
		int offsetY = 4 + slotColor * 40;

		switch (slotRender) {
			case 0:
				sizeY -= 17;
				break;
			case 1:
				sizeY -= 17;
				y += 17;
				offsetY += 17;
				break;
			case 2:
				sizeY -= 26;
				break;
			case 3:
				sizeY -= 26;
				y += 26;
				offsetY += 26;
				break;
			default:
				break;
		}
		gui.drawTexturedModalRect(x, y, offsetX, offsetY, sizeX, sizeY);
	}

	protected void drawSlotWithBorder(int x, int y) {

		int sizeX = 164;
		int sizeY = 38;
		int offsetX = 6;
		int offsetY = 2 + slotColor * 40;

		x -= 2;
		y -= 2;

		switch (slotRender) {
			case 0:
				sizeY -= 19;
				break;
			case 1:
				sizeY -= 19;
				y += 19;
				offsetY += 19;
				break;
			case 2:
				sizeY -= 28;
				break;
			case 3:
				sizeY -= 28;
				y += 28;
				offsetY += 28;
				break;
			default:
				break;
		}
		gui.drawTexturedModalRect(x, y, offsetX, offsetY, sizeX, sizeY);
	}

}
