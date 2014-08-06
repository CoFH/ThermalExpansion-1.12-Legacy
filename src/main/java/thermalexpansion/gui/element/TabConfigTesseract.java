package thermalexpansion.gui.element;

import cofh.core.gui.element.TabConfiguration;
import cofh.lib.gui.GuiBase;
import cofh.lib.gui.element.TabBase;
import cofh.lib.util.helpers.StringHelper;

import java.util.List;

import org.lwjgl.opengl.GL11;

import thermalexpansion.block.ender.TileTesseract;

public class TabConfigTesseract extends TabBase {

	public static final String[] TOOLTIPS = { StringHelper.localize("info.thermalexpansion.modeSend"), StringHelper.localize("info.thermalexpansion.modeRecv"),
			StringHelper.localize("info.thermalexpansion.modeSendRecv"), StringHelper.localize("info.thermalexpansion.modeBlocked") };

	TileTesseract myTile;
	String myPlayer;

	public TabConfigTesseract(GuiBase gui, TileTesseract theTile, String playerName) {

		this(gui, TabConfiguration.defaultSide, theTile, playerName);
	}

	public TabConfigTesseract(GuiBase gui, int side, TileTesseract theTile, String playerName) {

		super(gui, side);

		headerColor = TabConfiguration.defaultHeaderColor;
		subheaderColor = TabConfiguration.defaultSubHeaderColor;
		textColor = TabConfiguration.defaultTextColor;
		backgroundColor = TabConfiguration.defaultBackgroundColor;

		maxHeight = 92;
		maxWidth = 100;
		myTile = theTile;
		myPlayer = playerName;
	}

	public static final String[] buttonNames = { "IconSendOnly", "IconRecvOnly", "IconSendRecv", "IconBlocked" };

	@Override
	public void draw() {

		drawBackground();
		drawTabIcon("IconConfigTesseract");
		if (!isFullyOpened()) {
			return;
		}
		getFontRenderer().drawStringWithShadow(StringHelper.localize("info.cofh.configuration"), posXOffset() + 18, posY + 6, headerColor);
		getFontRenderer().drawStringWithShadow(StringHelper.localize("info.cofh.sending") + ":", posXOffset() + 6, posY + 42, subheaderColor);
		getFontRenderer().drawStringWithShadow(StringHelper.localize("info.cofh.receiving") + ":", posXOffset() + 6, posY + 66, subheaderColor);

		gui.drawButton(buttonNames[myTile.modeItem], posX() + 24, posY + 20, 1, 0);
		gui.drawButton(buttonNames[myTile.modeFluid], posX() + 42, posY + 20, 1, 0);
		gui.drawButton(buttonNames[myTile.modeEnergy], posX() + 60, posY + 20, 1, 0);

		String sending = "";
		String receiving = "";

		if (modeReceiveAll()) {
			receiving = StringHelper.localize("info.cofh.all");
		} else {
			if (myTile.modeReceiveItems()) {
				receiving += StringHelper.localize("info.cofh.items");
			}
			if (myTile.modeReceiveFluid()) {
				if (myTile.modeReceiveItems()) {
					receiving += ", ";
				}
				receiving += StringHelper.localize("info.cofh.fluid");
			}
			if (myTile.modeReceiveEnergy()) {
				if (myTile.modeReceiveItems() || myTile.modeReceiveFluid()) {
					receiving += ", ";
				}
				receiving += StringHelper.localize("info.cofh.energy");
			}
			if (receiving.isEmpty()) {
				receiving = StringHelper.localize("info.cofh.none");
			}
		}
		if (modeSendAll()) {
			sending = StringHelper.localize("info.cofh.all");
		} else {
			if (myTile.modeSendItems()) {
				sending += StringHelper.localize("info.cofh.items");
			}
			if (myTile.modeSendFluid()) {
				if (myTile.modeSendItems()) {
					sending += ", ";
				}
				sending += StringHelper.localize("info.cofh.fluid");
			}
			if (myTile.modeSendEnergy()) {
				if (myTile.modeSendItems() || myTile.modeSendFluid()) {
					sending += ", ";
				}
				sending += StringHelper.localize("info.cofh.energy");
			}
			if (sending.isEmpty()) {
				sending = StringHelper.localize("info.cofh.none");
			}
		}
		getFontRenderer().drawString(sending, posXOffset() + 14, posY + 54, textColor);
		getFontRenderer().drawString(receiving, posXOffset() + 14, posY + 78, textColor);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
	}

	private boolean modeReceiveAll() {

		return myTile.modeReceiveItems() && myTile.modeReceiveFluid() && myTile.modeReceiveEnergy();
	}

	private boolean modeSendAll() {

		return myTile.modeSendItems() && myTile.modeSendFluid() && myTile.modeSendEnergy();
	}

	@Override
	public void addTooltip(List<String> list) {

		if (!isFullyOpened()) {
			list.add(StringHelper.localize("info.cofh.configuration"));
		}
		int x = gui.getMouseX() - currentShiftX;
		int y = gui.getMouseY() - currentShiftY;
		if (24 <= x && x < 40 && 20 <= y && y < 36) {
			list.add(StringHelper.localize("info.thermalexpansion.modeItem") + ": " + TOOLTIPS[myTile.modeItem]);
		} else if (42 <= x && x < 58 && 20 <= y && y < 36) {
			list.add(StringHelper.localize("info.thermalexpansion.modeFluid") + ": " + TOOLTIPS[myTile.modeFluid]);
		} else if (60 <= x && x < 76 && 20 <= y && y < 36) {
			list.add(StringHelper.localize("info.thermalexpansion.modeEnergy") + ": " + TOOLTIPS[myTile.modeEnergy]);
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

		if (mouseX < 18 || mouseX > 82 || mouseY < 16 || mouseY > 40) {
			return false;
		}
		if (24 <= mouseX && mouseX < 40 && 20 <= mouseY && mouseY < 36) {
			if (mouseButton == 0) {
				myTile.incItemMode();
				GuiBase.playSound("random.click", 1.0F, 0.8F);
			} else if (mouseButton == 1) {
				myTile.decItemMode();
				GuiBase.playSound("random.click", 1.0F, 0.6F);
			}
			myTile.setTileInfo(myTile.frequency);
		} else if (42 <= mouseX && mouseX < 58 && 20 <= mouseY && mouseY < 36) {
			if (mouseButton == 0) {
				myTile.incFluidMode();
				GuiBase.playSound("random.click", 1.0F, 0.8F);
			} else if (mouseButton == 1) {
				myTile.decFluidMode();
				GuiBase.playSound("random.click", 1.0F, 0.6F);
			}
			myTile.setTileInfo(myTile.frequency);
		} else if (60 <= mouseX && mouseX < 76 && 20 <= mouseY && mouseY < 36) {
			if (mouseButton == 0) {
				myTile.incEnergyMode();
				GuiBase.playSound("random.click", 1.0F, 0.8F);
			} else if (mouseButton == 1) {
				myTile.decEnergyMode();
				GuiBase.playSound("random.click", 1.0F, 0.6F);
			}
			myTile.setTileInfo(myTile.frequency);
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
		gui.drawTexturedModalRect(posX() + 18, posY + 16, 16, 20, 64, 24);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
	}

}
