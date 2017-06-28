package cofh.thermalexpansion.gui.element;

import cofh.lib.gui.GuiBase;
import cofh.thermalexpansion.init.TEProps;

public class ElementSlotOverlayQuad extends ElementSlotOverlay {

	public ElementSlotOverlayQuad(GuiBase gui, int posX, int posY) {

		super(gui, posX, posY);
		this.texture = TEProps.textureGuiSlots4;
	}

	public ElementSlotOverlayQuad setSlotInfo(SlotColor color, SlotRender render) {

		slotColor = color;
		slotRender = render;
		return this;
	}

	protected void drawSlotNoBorder(int x, int y) {

		sizeX = 34;
		sizeY = 34;
		int offsetX = 6;
		int offsetY = 6 + slotColor.ordinal() * 42;

		switch (slotRender) {
			case TOP:
				offsetX += 42;
				break;
			case BOTTOM:
				offsetX += 84;
				break;
			default:
				break;
		}
		gui.drawTexturedModalRect(x, y, offsetX, offsetY, sizeX, sizeY);
	}

	protected void drawSlotWithBorder(int x, int y) {

		int sizeX = 38;
		int sizeY = 38;
		int offsetX = 4;
		int offsetY = 4 + slotColor.ordinal() * 42;

		x -= 2;
		y -= 2;

		switch (slotRender) {
			case TOP:
				offsetX += 42;
				break;
			case BOTTOM:
				offsetX += 84;
				break;
			default:
				break;
		}
		gui.drawTexturedModalRect(x, y, offsetX, offsetY, sizeX, sizeY);
	}

}
