package thermalexpansion.gui.element;

import cofh.gui.GuiBase;
import cofh.gui.element.TabBase;
import cofh.util.StringHelper;

import java.util.List;

import org.lwjgl.opengl.GL11;

import thermalexpansion.block.ender.TileTesseract;

public class TabConfigTesseract extends TabBase {

	public static final String[] TOOLTIPS = { StringHelper.localize("info.thermalexpansion.modeSend"), StringHelper.localize("info.thermalexpansion.modeRecv"), StringHelper.localize("info.thermalexpansion.modeSendRecv"), StringHelper.localize("info.thermalexpansion.modeBlocked") };

	TileTesseract myTile;
	String myPlayer;
	int headerColor = 0xe1c92f;
	int subheaderColor = 0xaaafb8;
	int textColor = 0x000000;

	public TabConfigTesseract(GuiBase gui, TileTesseract theTile, String playerName) {

		super(gui);

		myPlayer = playerName;
		myTile = theTile;
		maxHeight = 92;
		maxWidth = 100;
		backgroundColor = 0x089e4c;
	}

	public static final String[] buttonNames = { "IconSendOnly", "IconRecvOnly", "IconSendRecv", "IconBlocked" };

	@Override
	public void draw() {

		drawBackground();
		drawTabIcon("IconConfigTesseract");
		if (!isFullyOpened()) {
			return;
		}
		GuiBase.guiFontRenderer.drawStringWithShadow(StringHelper.localize("info.cofh.configuration"), posX + 20, posY + 6, headerColor);
		GuiBase.guiFontRenderer.drawStringWithShadow(StringHelper.localize("info.cofh.sending") + ":", posX + 8, posY + 42, subheaderColor);
		GuiBase.guiFontRenderer.drawStringWithShadow(StringHelper.localize("info.cofh.receiving") + ":", posX + 8, posY + 66, subheaderColor);

		gui.drawButton(buttonNames[myTile.modeItem], posX + 24, posY + 20, 1, 0);
		gui.drawButton(buttonNames[myTile.modeFluid], posX + 42, posY + 20, 1, 0);
		gui.drawButton(buttonNames[myTile.modeEnergy], posX + 60, posY + 20, 1, 0);

		String sending = "";
		String receiving = "";

		if (canReceiveAll()) {
			receiving = StringHelper.localize("info.cofh.all");
		} else {
			if (myTile.canReceiveItems()) {
				receiving += StringHelper.localize("info.cofh.items");
			}
			if (myTile.canReceiveFluid()) {
				if (myTile.canReceiveItems()) {
					receiving += ", ";
				}
				receiving += StringHelper.localize("info.cofh.fluid");
			}
			if (myTile.canReceiveEnergy()) {
				if (myTile.canReceiveItems() || myTile.canReceiveFluid()) {
					receiving += ", ";
				}
				receiving += StringHelper.localize("info.cofh.energy");
			}
			if (receiving.isEmpty()) {
				receiving = StringHelper.localize("info.cofh.none");
			}
		}
		if (canSendAll()) {
			sending = StringHelper.localize("info.cofh.all");
		} else {
			if (myTile.canSendItems()) {
				sending += StringHelper.localize("info.cofh.items");
			}
			if (myTile.canSendFluid()) {
				if (myTile.canSendItems()) {
					sending += ", ";
				}
				sending += StringHelper.localize("info.cofh.fluid");
			}
			if (myTile.canSendEnergy()) {
				if (myTile.canSendItems() || myTile.canSendFluid()) {
					sending += ", ";
				}
				sending += StringHelper.localize("info.cofh.energy");
			}
			if (sending.isEmpty()) {
				sending = StringHelper.localize("info.cofh.none");
			}
		}
		GuiBase.guiFontRenderer.drawString(sending, posX + 16, posY + 54, textColor);
		GuiBase.guiFontRenderer.drawString(receiving, posX + 16, posY + 78, textColor);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
	}

	private boolean canReceiveAll() {

		return myTile.canReceiveItems() && myTile.canReceiveFluid() && myTile.canReceiveEnergy();
	}

	private boolean canSendAll() {

		return myTile.canSendItems() && myTile.canSendFluid() && myTile.canSendEnergy();
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
	public boolean handleMouseClicked(int x, int y, int mouseButton) {

		if (!isFullyOpened()) {
			return false;
		}
		x -= currentShiftX;
		y -= currentShiftY;

		if (x < 18 || x > 82 || y < 16 || y > 40) {
			return false;
		}
		if (24 <= x && x < 40 && 20 <= y && y < 36) {
			if (mouseButton == 0) {
				myTile.incItemMode();
				GuiBase.playSound("random.click", 1.0F, 0.8F);
			} else if (mouseButton == 1) {
				myTile.decItemMode();
				GuiBase.playSound("random.click", 1.0F, 0.6F);
			}
			myTile.setTileInfo(myTile.frequency);
		} else if (42 <= x && x < 58 && 20 <= y && y < 36) {
			if (mouseButton == 0) {
				myTile.incFluidMode();
				GuiBase.playSound("random.click", 1.0F, 0.8F);
			} else if (mouseButton == 1) {
				myTile.decFluidMode();
				GuiBase.playSound("random.click", 1.0F, 0.6F);
			}
			myTile.setTileInfo(myTile.frequency);
		} else if (60 <= x && x < 76 && 20 <= y && y < 36) {
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
		gui.drawTexturedModalRect(posX + 18, posY + 16, 16, 20, 64, 24);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
	}

}
