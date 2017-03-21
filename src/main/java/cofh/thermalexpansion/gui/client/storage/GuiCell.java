package cofh.thermalexpansion.gui.client.storage;

import cofh.core.gui.GuiCore;
import cofh.core.gui.element.*;
import cofh.core.util.helpers.SecurityHelper;
import cofh.lib.gui.element.ElementButton;
import cofh.lib.gui.element.ElementEnergyStored;
import cofh.lib.gui.element.TabBase;
import cofh.lib.util.helpers.StringHelper;
import cofh.thermalexpansion.block.storage.TileCell;
import cofh.thermalexpansion.gui.container.ContainerTEBase;
import cofh.thermalexpansion.init.TEProps;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

import java.util.UUID;

public class GuiCell extends GuiCore {

	public static final String TEX_PATH = TEProps.PATH_GUI_STORAGE + "cell.png";
	public static final ResourceLocation TEXTURE = new ResourceLocation(TEX_PATH);

	protected TileCell baseTile;
	protected UUID playerName;

	protected String myTutorial = "";

	protected TabBase redstoneTab;
	protected TabBase configTab;
	protected TabBase securityTab;

	private ElementButton decRecv;
	private ElementButton incRecv;
	private ElementButton decSend;
	private ElementButton incSend;

	public GuiCell(InventoryPlayer inventory, TileEntity tile) {

		super(new ContainerTEBase(inventory, tile), TEXTURE);

		baseTile = (TileCell) tile;
		name = baseTile.getName();
		playerName = SecurityHelper.getID(inventory.player);

		if (baseTile.enableSecurity() && baseTile.isSecured()) {
			myTutorial += StringHelper.tutorialTabSecurity() + "\n\n";
		}
		if (baseTile.hasRedstoneControl()) {
			myTutorial += StringHelper.tutorialTabRedstone() + "\n\n";
		}
		myTutorial += StringHelper.tutorialTabConfiguration();

		generateInfo("tab.thermalexpansion.storage.cell");
	}

	@Override
	public void initGui() {

		super.initGui();

		// Right Side
		redstoneTab = addTab(new TabRedstoneControl(this, baseTile));
		configTab = addTab(new TabConfiguration(this, baseTile));

		// Left Side
		securityTab = addTab(new TabSecurity(this, baseTile, playerName));
		securityTab.setVisible(baseTile.enableSecurity() && baseTile.isSecured());

		if (!myInfo.isEmpty()) {
			addTab(new TabInfo(this, myInfo));
		}
		addTab(new TabTutorial(this, myTutorial));

		addElement(new ElementEnergyStored(this, 80, 18, baseTile.getEnergyStorage()));

		decRecv = new ElementButton(this, 28, 56, "DecRecv", 176, 0, 176, 14, 176, 28, 14, 14, TEX_PATH).setToolTipLocalized(true);
		incRecv = new ElementButton(this, 44, 56, "IncRecv", 190, 0, 190, 14, 190, 28, 14, 14, TEX_PATH).setToolTipLocalized(true);
		decSend = new ElementButton(this, 118, 56, "DecSend", 176, 0, 176, 14, 176, 28, 14, 14, TEX_PATH).setToolTipLocalized(true);
		incSend = new ElementButton(this, 134, 56, "IncSend", 190, 0, 190, 14, 190, 28, 14, 14, TEX_PATH).setToolTipLocalized(true);

		addElement(decRecv);
		addElement(incRecv);
		addElement(decSend);
		addElement(incSend);
	}

	@Override
	public void updateScreen() {

		super.updateScreen();

		if (!baseTile.canAccess()) {
			this.mc.thePlayer.closeScreen();
		}
		redstoneTab.setVisible(baseTile.hasRedstoneControl());

		securityTab.setVisible(baseTile.enableSecurity() && baseTile.isSecured());
	}

	@Override
	protected void updateElementInformation() {

		super.updateElementInformation();

		int change;
		int change2;

		if (GuiScreen.isShiftKeyDown()) {
			change = 1000;
			change2 = 100;

			if (GuiScreen.isCtrlKeyDown()) {
				change *= 10;
				change2 *= 10;
			}
		} else if (GuiScreen.isCtrlKeyDown()) {
			change = 5;
			change2 = 1;
		} else {
			change = 50;
			change2 = 10;
		}
		if (baseTile.amountRecv > 0) {
			decRecv.setActive();
			decRecv.setToolTip(StringHelper.localize("gui.thermalexpansion.storage.cell.decRecv") + " " + change + "/" + change2);
		} else {
			decRecv.setDisabled();
			decRecv.clearToolTip();
		}
		if (baseTile.amountRecv < TileCell.RECV[baseTile.getLevel()]) {
			incRecv.setActive();
			incRecv.setToolTip(StringHelper.localize("gui.thermalexpansion.storage.cell.incRecv") + " " + change + "/" + change2);
		} else {
			incRecv.setDisabled();
			incRecv.clearToolTip();
		}
		if (baseTile.amountSend > 0) {
			decSend.setActive();
			decSend.setToolTip(StringHelper.localize("gui.thermalexpansion.storage.cell.decSend") + " " + change + "/" + change2);
		} else {
			decSend.setDisabled();
			decSend.clearToolTip();
		}
		if (baseTile.amountSend < TileCell.SEND[baseTile.getLevel()]) {
			incSend.setActive();
			incSend.setToolTip(StringHelper.localize("gui.thermalexpansion.storage.cell.incSend") + " " + change + "/" + change2);
		} else {
			incSend.setDisabled();
			incSend.clearToolTip();
		}
	}

	@Override
	public void handleElementButtonClick(String buttonName, int mouseButton) {

		int change;
		float pitch;

		if (GuiScreen.isShiftKeyDown()) {
			change = 1000;
			pitch = 0.9F;
			if (mouseButton == 1) {
				change = 100;
				pitch = 0.8F;
			}
			if (GuiScreen.isCtrlKeyDown()) {
				change *= 10;
			}
		} else if (GuiScreen.isCtrlKeyDown()) {
			change = 5;
			pitch = 0.5F;
			if (mouseButton == 1) {
				change = 1;
				pitch = 0.4F;
			}
		} else {
			change = 50;
			pitch = 0.7F;
			if (mouseButton == 1) {
				change = 10;
				pitch = 0.6F;
			}
		}
		int curReceive = baseTile.amountRecv;
		int curSend = baseTile.amountSend;

		if (buttonName.equalsIgnoreCase("DecRecv")) {
			baseTile.amountRecv -= change;
			pitch -= 0.1F;
		} else if (buttonName.equalsIgnoreCase("IncRecv")) {
			baseTile.amountRecv += change;
			pitch += 0.1F;
		} else if (buttonName.equalsIgnoreCase("DecSend")) {
			baseTile.amountSend -= change;
			pitch -= 0.1F;
		} else if (buttonName.equalsIgnoreCase("IncSend")) {
			baseTile.amountSend += change;
			pitch += 0.1F;
		}
		playClickSound(1.0F, pitch);

		baseTile.sendModePacket();

		baseTile.amountRecv = curReceive;
		baseTile.amountSend = curSend;
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int x, int y) {

		fontRendererObj.drawString(StringHelper.localize("gui.thermalexpansion.storage.cell.maxRecv") + ":", getCenteredOffset(StringHelper.localize("gui.thermalexpansion.storage.cell.maxRecv"), xSize / 2), 32, 0x404040);
		fontRendererObj.drawString(StringHelper.localize("gui.thermalexpansion.storage.cell.maxSend") + ":", xSize / 2 + getCenteredOffset(StringHelper.localize("gui.thermalexpansion.storage.cell.maxSend"), xSize / 2), 32, 0x404040);

		String recv = String.format("%-8s", "" + baseTile.amountRecv + " RF/t");
		String send = String.format("%-8s", "" + baseTile.amountSend + " RF/t");

		int xRecv = 20;
		int xSend = 110;

		if (baseTile.amountRecv < 10) {
			xRecv += 6;
		}
		if (baseTile.amountRecv < 100) {
			xRecv += 6;
		}
		if (baseTile.amountRecv < 1000) {
			xRecv += 6;
		}
		if (baseTile.amountRecv >= 10000) {
			xRecv -= 6;
		}
		if (baseTile.amountRecv >= 100000) {
			xRecv -= 3;
		}

		if (baseTile.amountSend < 10) {
			xSend += 6;
		}
		if (baseTile.amountSend < 100) {
			xSend += 6;
		}
		if (baseTile.amountSend < 1000) {
			xSend += 6;
		}
		if (baseTile.amountSend >= 10000) {
			xSend -= 6;
		}
		if (baseTile.amountSend >= 100000) {
			xSend -= 3;
		}
		fontRendererObj.drawString(recv, xRecv, 44, 0x404040);
		fontRendererObj.drawString(send, xSend, 44, 0x404040);

		super.drawGuiContainerForegroundLayer(x, y);
	}

}
