package cofh.thermalexpansion.gui.element;

import cofh.core.gui.GuiContainerCore;
import cofh.core.gui.element.ElementBase;
import cofh.core.init.CoreProps;
import cofh.core.util.helpers.RenderHelper;
import cofh.thermalexpansion.init.TEProps;

public class ElementSlotOverlayCrafter extends ElementBase {

	public ElementSlotOverlayCrafter(GuiContainerCore gui, int posX, int posY) {

		super(gui, posX, posY);
		this.texture = TEProps.textureGuiSlotsCrafter;
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
		int offsetY = 198;

		gui.drawTexturedModalRect(x, y, offsetX, offsetY, sizeX, sizeY);
	}

	protected void drawSlotWithBorder(int x, int y) {

		int sizeX = 164;
		int sizeY = 38;
		int offsetX = 6;
		int offsetY = 196;

		x -= 2;
		y -= 2;

		gui.drawTexturedModalRect(x, y, offsetX, offsetY, sizeX, sizeY);
	}

}
