package thermalexpansion.gui.client;

import cofh.core.CoFHProps;
import cofh.gui.GuiBaseAdv;
import cofh.gui.element.ElementButton;
import cofh.gui.element.ElementEnergyStored;
import cofh.gui.element.TabInfo;
import cofh.gui.element.TabRedstone;
import cofh.gui.element.TabSecurity;
import cofh.gui.element.TabTutorial;
import cofh.util.StringHelper;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

import thermalexpansion.block.cell.TileCell;
import thermalexpansion.core.TEProps;
import thermalexpansion.gui.container.ContainerTEBase;
import thermalexpansion.gui.element.TabConfigCell;

public class GuiCell extends GuiBaseAdv {

	static final String TEX_PATH = TEProps.PATH_GUI + "Cell.png";
	static final ResourceLocation TEXTURE = new ResourceLocation(TEX_PATH);
	static final String INFO = "Stores Redstone Flux.\n\nHold Shift or Ctrl to fine tune energy control.\n\nWrench while sneaking to dismantle.";

	TileCell myTile;
	String playerName;

	public ElementButton decRecv;
	public ElementButton incRecv;
	public ElementButton decSend;
	public ElementButton incSend;

	public GuiCell(InventoryPlayer inventory, TileEntity theTile) {

		super(new ContainerTEBase(inventory, theTile), TEXTURE);
		myTile = (TileCell) theTile;
		name = myTile.getInventoryName();
		playerName = inventory.player.getDisplayName();
	}

	@Override
	public void initGui() {

		super.initGui();

		addElement(new ElementEnergyStored(this, 80, 18, myTile.getEnergyStorage()));

		addTab(new TabRedstone(this, myTile));
		addTab(new TabConfigCell(this, myTile));
		addTab(new TabInfo(this, INFO));
		addTab(new TabTutorial(this, CoFHProps.tutorialTabRedstone + "\n\n" + TabConfigCell.TUTORIAL_CONFIG));
		if (myTile.enableSecurity() && myTile.isSecured()) {
			addTab(new TabSecurity(this, myTile, playerName));
		}

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

		if (!myTile.canAccess()) {
			this.mc.thePlayer.closeScreen();
		}
	}

	@Override
	public void handleElementButtonClick(String buttonName, int mouseButton) {

		int change = 0;
		float pitch = 1.0F;

		if (GuiScreen.isShiftKeyDown()) {
			change = 1000;
			pitch = 0.9F;
			if (mouseButton == 1) {
				change = 100;
				pitch = 0.8F;
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

		int curReceive = myTile.energyReceive;
		int curSend = myTile.energySend;

		if (buttonName.equalsIgnoreCase("DecRecv")) {
			myTile.energyReceive -= change;
			pitch -= 0.1F;
		} else if (buttonName.equalsIgnoreCase("IncRecv")) {
			myTile.energyReceive += change;
			pitch += 0.1F;
		} else if (buttonName.equalsIgnoreCase("DecSend")) {
			myTile.energySend -= change;
			pitch -= 0.1F;
		} else if (buttonName.equalsIgnoreCase("IncSend")) {
			myTile.energySend += change;
			pitch += 0.1F;
		}
		playSound("random.click", 1.0F, pitch);

		myTile.sendModePacket();

		myTile.energyReceive = curReceive;
		myTile.energySend = curSend;
	}

	@Override
	protected void updateElementInformation() {

		int change = 0;
		int change2 = 0;

		if (GuiScreen.isShiftKeyDown()) {
			change = 1000;
			change2 = 100;
		} else if (GuiScreen.isCtrlKeyDown()) {
			change = 5;
			change2 = 1;
		} else {
			change = 50;
			change2 = 10;
		}

		if (myTile.energyReceive > 0) {
			decRecv.setActive();
			decRecv.setToolTip(StringHelper.localize("info.thermalexpansion.cell.decRecv") + " " + change + "/" + change2);
		} else {
			decRecv.setDisabled();
			decRecv.clearToolTip();
		}
		if (myTile.energyReceive < TileCell.MAX_RECEIVE[myTile.type]) {
			incRecv.setActive();
			incRecv.setToolTip(StringHelper.localize("info.thermalexpansion.cell.incRecv") + " " + change + "/" + change2);
		} else {
			incRecv.setDisabled();
			incRecv.clearToolTip();
		}
		if (myTile.energySend > 0) {
			decSend.setActive();
			decSend.setToolTip(StringHelper.localize("info.thermalexpansion.cell.decSend") + " " + change + "/" + change2);
		} else {
			decSend.setDisabled();
			decSend.clearToolTip();
		}
		if (myTile.energySend < TileCell.MAX_SEND[myTile.type]) {
			incSend.setActive();
			incSend.setToolTip(StringHelper.localize("info.thermalexpansion.cell.incSend") + " " + change + "/" + change2);
		} else {
			incSend.setDisabled();
			incSend.clearToolTip();
		}
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int x, int y) {

		fontRendererObj.drawString(StringHelper.localize("info.thermalexpansion.cell.maxIn") + ":",
				getCenteredOffset(StringHelper.localize("info.thermalexpansion.cell.maxIn"), xSize / 2), 32, 0x404040);
		fontRendererObj.drawString(StringHelper.localize("info.thermalexpansion.cell.maxOut") + ":",
				xSize / 2 + getCenteredOffset(StringHelper.localize("info.thermalexpansion.cell.maxOut"), xSize / 2), 32, 0x404040);

		String recv = String.format("%-8s", "" + myTile.energyReceive + " RF/t");
		String send = String.format("%-8s", "" + myTile.energySend + " RF/t");

		int xRecv = 20;
		int xSend = 110;

		if (myTile.energyReceive < 10) {
			xRecv = 32;
		} else if (myTile.energyReceive < 100) {
			xRecv = 26;
		}
		if (myTile.energySend < 10) {
			xSend = 122;
		} else if (myTile.energySend < 100) {
			xSend = 116;
		}
		fontRendererObj.drawString(recv, xRecv, 44, 0x404040);
		fontRendererObj.drawString(send, xSend, 44, 0x404040);

		super.drawGuiContainerForegroundLayer(x, y);
	}

}
