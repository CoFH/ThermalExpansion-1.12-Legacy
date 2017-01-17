package cofh.thermalexpansion.gui.element;

import cofh.core.gui.element.TabConfiguration;
import cofh.lib.gui.GuiBase;
import cofh.lib.gui.element.TabBase;
import cofh.lib.render.RenderHelper;
import cofh.lib.util.helpers.BlockHelper;
import cofh.lib.util.helpers.StringHelper;
import cofh.thermalexpansion.block.cell.TileCell;
import net.minecraft.client.gui.GuiScreen;
import org.lwjgl.opengl.GL11;

import java.util.List;

public class TabConfigCell extends TabBase {

	TileCell myTile;

	public TabConfigCell(GuiBase gui, TileCell theTile) {

		this(gui, TabConfiguration.defaultSide, theTile);
	}

	public TabConfigCell(GuiBase gui, int side, TileCell theTile) {

		super(gui, side);

		headerColor = TabConfiguration.defaultHeaderColor;
		subheaderColor = TabConfiguration.defaultSubHeaderColor;
		textColor = TabConfiguration.defaultTextColor;
		backgroundColor = TabConfiguration.defaultBackgroundColor;

		maxHeight = 92;
		maxWidth = 100;
		myTile = theTile;
	}

	@Override
	public void addTooltip(List<String> list) {

		if (!isFullyOpened()) {
			list.add(StringHelper.localize("info.cofh.configuration"));
		}
	}

	@Override
	public boolean onMousePressed(int mouseX, int mouseY, int mouseButton) {

		if (!isFullyOpened()) {
			return false;
		}
		if (side == LEFT) {
			mouseX += currentWidth;
		}
		mouseX -= currentShiftX;
		mouseY -= currentShiftY;

		if (mouseX < 16 || mouseX >= 80 || mouseY < 20 || mouseY >= 84) {
			return false;
		}
		if (40 <= mouseX && mouseX < 56 && 24 <= mouseY && mouseY < 40) {
			handleSideChange(1, mouseButton);
		} else if (20 <= mouseX && mouseX < 36 && 44 <= mouseY && mouseY < 60) {
			handleSideChange(BlockHelper.SIDE_LEFT[myTile.getFacing()], mouseButton);
		} else if (40 <= mouseX && mouseX < 56 && 44 <= mouseY && mouseY < 60) {
			handleSideChange(myTile.getFacing(), mouseButton);
		} else if (60 <= mouseX && mouseX < 76 && 44 <= mouseY && mouseY < 60) {
			handleSideChange(BlockHelper.SIDE_RIGHT[myTile.getFacing()], mouseButton);
		} else if (40 <= mouseX && mouseX < 56 && 64 <= mouseY && mouseY < 80) {
			handleSideChange(0, mouseButton);
		} else if (60 <= mouseX && mouseX < 76 && 64 <= mouseY && mouseY < 80) {
			handleSideChange(BlockHelper.SIDE_OPPOSITE[myTile.getFacing()], mouseButton);
		}
		return true;
	}

	@Override
	protected void drawBackground() {

		super.drawBackground();

		if (!isFullyOpened()) {
			return;
		}
		float colorR = (backgroundColor >> 16 & 255) / 255.0F * 0.6F;
		float colorG = (backgroundColor >> 8 & 255) / 255.0F * 0.6F;
		float colorB = (backgroundColor & 255) / 255.0F * 0.6F;
		GL11.glColor4f(colorR, colorG, colorB, 1.0F);
		gui.drawTexturedModalRect(posX() + 16, posY + 20, 16, 20, 64, 64);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
	}

	@Override
	protected void drawForeground() {

		drawTabIcon("IconConfig");
		if (!isFullyOpened()) {
			return;
		}
		getFontRenderer().drawStringWithShadow(StringHelper.localize("info.cofh.configuration"), posXOffset() + 18, posY + 6, headerColor);
		RenderHelper.setBlockTextureSheet();

		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

		for (int i = 0; i < 3; i++) {
			gui.drawIcon(myTile.getTexture(1, i), posX() + 40, posY + 24);
			gui.drawIcon(myTile.getTexture(BlockHelper.SIDE_LEFT[myTile.getFacing()], i), posX() + 20, posY + 44);
			gui.drawIcon(myTile.getTexture(myTile.getFacing(), i), posX() + 40, posY + 44);
			gui.drawIcon(myTile.getTexture(BlockHelper.SIDE_RIGHT[myTile.getFacing()], i), posX() + 60, posY + 44);
			gui.drawIcon(myTile.getTexture(0, i), posX() + 40, posY + 64);
			gui.drawIcon(myTile.getTexture(BlockHelper.SIDE_OPPOSITE[myTile.getFacing()], i), posX() + 60, posY + 64);
		}
		gui.drawIcon(myTile.getTexture(myTile.getFacing(), 3), posX() + 40, posY + 44);

		GL11.glDisable(GL11.GL_BLEND);
		RenderHelper.setDefaultFontTextureSheet();
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
	}

	void handleSideChange(int side, int mouseButton) {

		if (GuiScreen.isShiftKeyDown()) {
			if (side == myTile.getFacing()) {
				if (myTile.resetSides()) {
					GuiBase.playClickSound(1.0F, 0.2F);
				}
			} else if (myTile.setSide(side, 0)) {
				GuiBase.playClickSound(1.0F, 0.4F);
			}
			return;
		}
		if (mouseButton == 0) {
			if (myTile.incrSide(side)) {
				GuiBase.playClickSound(1.0F, 0.8F);
			}
		} else if (mouseButton == 1) {
			if (myTile.decrSide(side)) {
				GuiBase.playClickSound(1.0F, 0.6F);
			}
		}
	}

}
