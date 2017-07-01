package cofh.thermalexpansion.gui.element;

import cofh.core.init.CoreProps;
import cofh.core.gui.GuiCore;
import cofh.core.gui.element.ElementBase;
import cofh.core.util.helpers.RenderHelper;
import cofh.thermalexpansion.init.TEProps;

public class ElementSlotOverlay extends ElementBase {

	protected SlotColor slotColor;
	protected SlotType slotType;
	protected SlotRender slotRender;

	public ElementSlotOverlay(GuiCore gui, int posX, int posY) {

		super(gui, posX, posY);
		this.texture = TEProps.textureGuiCommon;
	}

	public ElementSlotOverlay setSlotInfo(SlotColor color, SlotType type, SlotRender render) {

		slotColor = color;
		slotType = type;
		slotRender = render;
		return this;
	}

	public ElementSlotOverlay setSlotColor(SlotColor color) {

		slotColor = color;
		return this;
	}

	public ElementSlotOverlay setSlotRender(SlotRender render) {

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

		sizeX = 0;
		sizeY = 0;
		int offsetX = slotColor.ordinal() / 3 * 128;
		int offsetY = slotColor.ordinal() % 3 * 32;

		switch (slotType) {
			case STANDARD:
				sizeX = 16;
				sizeY = 16;
				offsetX += 8;
				offsetY += 8;
				break;
			case OUTPUT:
				sizeX = 24;
				sizeY = 24;
				offsetX += 36;
				offsetY += 4;
				break;
			case OUTPUT_DOUBLE:
				sizeX = 42;
				sizeY = 24;
				offsetX += 75;
				offsetY += 4;
				break;
			case TANK:
				sizeX = 16;
				sizeY = 60;
				offsetX = slotColor.ordinal() * 32 + 8;
				offsetY = 98;
				break;
			case TANK_SHORT:
				sizeX = 16;
				sizeY = 30;
				offsetX = slotColor.ordinal() * 32 + 8;
				offsetY = 162;
				break;
		}

		switch (slotRender) {
			case TOP:
				sizeY /= 2;
				break;
			case BOTTOM:
				sizeY /= 2;
				y += sizeY;
				offsetY += sizeY;
				break;
			case FULL:
				break;
		}
		gui.drawTexturedModalRect(x, y, offsetX, offsetY, sizeX, sizeY);
	}

	protected void drawSlotWithBorder(int x, int y) {

		int sizeX = 32;
		int sizeY = 32;
		int offsetX = slotColor.ordinal() / 3 * 128;
		int offsetY = slotColor.ordinal() % 3 * 32;

		offsetX += slotType.ordinal() * 32;

		switch (slotType) {
			case STANDARD:
				x -= 8;
				y -= 8;
				break;
			case OUTPUT:
				x -= 4;
				y -= 4;
				break;
			case OUTPUT_DOUBLE:
				sizeX = 64;
				x -= 11;
				y -= 4;
				break;
			case TANK:
				sizeY = 64;
				offsetX = slotColor.ordinal() * 32;
				offsetY = 96;
				x -= 8;
				y -= 2;
				break;
			case TANK_SHORT:
				sizeY = 48;
				offsetX = slotColor.ordinal() * 32;
				offsetY = 160;
				x -= 8;
				y -= 2;
				break;
		}

		switch (slotRender) {
			case TOP:
				sizeY /= 2;
				break;
			case BOTTOM:
				sizeY /= 2;
				y += sizeY;
				offsetY += sizeY;
				break;
			default:
				break;
		}
		gui.drawTexturedModalRect(x, y, offsetX, offsetY, sizeX, sizeY);
	}

	/* HELPERS */
	public enum SlotColor {
		BLUE, RED, YELLOW, ORANGE, GREEN, PURPLE
	}

	public enum SlotType {
		STANDARD, OUTPUT, OUTPUT_DOUBLE, TANK, TANK_SHORT
	}

	public enum SlotRender {
		TOP, BOTTOM, FULL
	}

}
