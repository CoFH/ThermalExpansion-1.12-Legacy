package cofh.thermalexpansion.gui.element;

import cofh.core.init.CoreProps;
import cofh.lib.gui.GuiBase;
import cofh.lib.gui.element.ElementBase;
import cofh.lib.util.helpers.RenderHelper;
import cofh.thermalexpansion.init.TEProps;

public class ElementSlotOverlayCrafter extends ElementBase {

	public int slotColor;
	public int slotRender;

	public ElementSlotOverlayCrafter(GuiBase gui, int posX, int posY) {

		super(gui, posX, posY);
		this.texture = TEProps.textureGuiCrafter;
	}

	public ElementSlotOverlayCrafter setSlotInfo(int color, int render) {

		slotColor = color;
		slotRender = render;
		return this;
	}

	public ElementSlotOverlayCrafter setSlotColor(int color) {

		slotColor = color;
		return this;
	}

	public ElementSlotOverlayCrafter setSlotRender(int render) {

		slotRender = render;
		return this;
	}

	@Override
	public void drawBackground(int mouseX, int mouseY, float gameTicks) {

		if (!isVisible()) {
			return;
		}
		RenderHelper.bindTexture(texture);
		if (CoreProps.enableGUISlotBorders) {
			drawSlotWithBorder(posX, posY);
		} else {
			drawSlotNoBorder(posX, posY);
		}
	}

	@Override
	public void drawForeground(int mouseX, int mouseY) {

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
				sizeY /= 2;
				break;
			case 1:
				sizeY /= 2;
				y += sizeY;
				offsetY += sizeY;
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
				sizeY /= 2;
				break;
			case 1:
				sizeY /= 2;
				y += sizeY;
				offsetY += sizeY;
				break;
			default:
				break;
		}
		gui.drawTexturedModalRect(x, y, offsetX, offsetY, sizeX, sizeY);
	}

}
