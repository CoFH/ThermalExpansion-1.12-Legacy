package thermalexpansion.gui.element;

import java.util.List;

import javax.swing.Icon;

import net.minecraft.client.gui.GuiScreen;

import org.lwjgl.opengl.GL11;

import thermalexpansion.block.energycell.TileEnergyCell;
import cofh.gui.GuiBase;
import cofh.gui.element.TabBase;
import cofh.render.RenderHelper;
import cofh.util.BlockHelper;
import cofh.util.StringHelper;

public class TabConfigEnergyCell extends TabBase {

	public static String TUTORIAL_CONFIG = "The Configuration tab determines how energy is transferred to and from this device.";

	TileEnergyCell myTile;
	int headerColor = 0xe1c92f;
	int subheaderColor = 0xaaafb8;
	int textColor = 0x000000;

	public TabConfigEnergyCell(GuiBase gui, TileEnergyCell theTile) {

		super(gui);

		myTile = theTile;
		maxHeight = 92;
		maxWidth = 100;
		backgroundColor = 0x089e4c;
	}

	@Override
	public void draw() {

		drawBackground();
		drawTabIcon("IconConfigMachine");
		if (!isFullyOpened()) {
			return;
		}
		GuiBase.guiFontRenderer.drawStringWithShadow(StringHelper.localize("info.cofh.configuration"), posX + 20, posY + 6, headerColor);
		GuiBase.guiFontRenderer.drawString("", posX, posY, 0xffffff);
		RenderHelper.setBlockTextureSheet();
		Icon texIndex;

		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

		for (int i = 0; i < 3; i++) {
			gui.drawIcon(myTile.getBlockTexture(1, i), posX + 40, posY + 24, 0);
			gui.drawIcon(myTile.getBlockTexture(BlockHelper.SIDE_LEFT[myTile.getFacing()], i), posX + 20, posY + 44, 0);
			gui.drawIcon(myTile.getBlockTexture(myTile.getFacing(), i), posX + 40, posY + 44, 0);
			gui.drawIcon(myTile.getBlockTexture(BlockHelper.SIDE_RIGHT[myTile.getFacing()], i), posX + 60, posY + 44, 0);
			gui.drawIcon(myTile.getBlockTexture(0, i), posX + 40, posY + 64, 0);
			gui.drawIcon(myTile.getBlockTexture(BlockHelper.SIDE_OPPOSITE[myTile.getFacing()], i), posX + 60, posY + 64, 0);
		}
		gui.drawIcon(myTile.getBlockTexture(myTile.getFacing(), 3), posX + 40, posY + 44, 0);

		GL11.glDisable(GL11.GL_BLEND);
		RenderHelper.setDefaultFontTextureSheet();
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
	}

	@Override
	public void addTooltip(List<String> list) {

		if (!isFullyOpened()) {
			list.add(StringHelper.localize("info.cofh.configuration"));
		}
	}

	@Override
	public boolean handleMouseClicked(int x, int y, int mouseButton) {

		if (!isFullyOpened()) {
			return false;
		}
		x -= currentShiftX;
		y -= currentShiftY;
		if (x < 16 || x >= 80 || y < 20 || y >= 84) {
			return false;
		}
		if (40 <= x && x < 56 && 24 <= y && y < 40) {
			handleSideChange(1, mouseButton);
		} else if (20 <= x && x < 36 && 44 <= y && y < 60) {
			handleSideChange(BlockHelper.SIDE_LEFT[myTile.getFacing()], mouseButton);
		} else if (40 <= x && x < 56 && 44 <= y && y < 60) {
			handleSideChange(myTile.getFacing(), mouseButton);
		} else if (60 <= x && x < 76 && 44 <= y && y < 60) {
			handleSideChange(BlockHelper.SIDE_RIGHT[myTile.getFacing()], mouseButton);
		} else if (40 <= x && x < 56 && 64 <= y && y < 80) {
			handleSideChange(0, mouseButton);
		} else if (60 <= x && x < 76 && 64 <= y && y < 80) {
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
		gui.drawTexturedModalRect(posX + 16, posY + 20, 16, 20, 64, 64);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
	}

	void handleSideChange(int side, int mouseButton) {

		if (GuiScreen.isShiftKeyDown()) {
			if (side == myTile.getFacing()) {
				if (myTile.resetSides()) {
					GuiBase.playSound("random.click", 1.0F, 0.2F);
				}
			} else if (myTile.setSide(side, 0)) {
				GuiBase.playSound("random.click", 1.0F, 0.4F);
			}
			return;
		}
		if (mouseButton == 0) {
			if (myTile.incrSide(side)) {
				GuiBase.playSound("random.click", 1.0F, 0.8F);
			}
		} else if (mouseButton == 1) {
			if (myTile.decrSide(side)) {
				GuiBase.playSound("random.click", 1.0F, 0.6F);
			}
		}
	}

}
