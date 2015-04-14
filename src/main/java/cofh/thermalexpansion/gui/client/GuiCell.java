package cofh.thermalexpansion.gui.client;

import cofh.core.gui.GuiBaseAdv;
import cofh.core.gui.element.TabInfo;
import cofh.core.gui.element.TabRedstone;
import cofh.core.gui.element.TabSecurity;
import cofh.core.gui.element.TabTutorial;
import cofh.lib.gui.element.ElementButton;
import cofh.lib.gui.element.ElementEnergyStored;
import cofh.lib.util.helpers.SecurityHelper;
import cofh.lib.util.helpers.StringHelper;
import cofh.thermalexpansion.block.cell.TileCell;
import cofh.thermalexpansion.core.TEProps;
import cofh.thermalexpansion.gui.container.ContainerTEBase;
import cofh.thermalexpansion.gui.element.TabConfigCell;

import java.util.UUID;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

public class GuiCell extends GuiBaseAdv {

	static final String TEX_PATH = TEProps.PATH_GUI + "Cell.png";
	static final ResourceLocation TEXTURE = new ResourceLocation(TEX_PATH);

	TileCell myTile;
	UUID playerName;

	ElementButton decRecv;
	ElementButton incRecv;
	ElementButton decSend;
	ElementButton incSend;

	public GuiCell(InventoryPlayer inventory, TileEntity theTile) {

		super(new ContainerTEBase(inventory, theTile), TEXTURE);
		myTile = (TileCell) theTile;
		name = myTile.getInventoryName();
		playerName = SecurityHelper.getID(inventory.player);

		generateInfo("tab.thermalexpansion.cell", 2);
	}

	@Override
	public void initGui() {

		super.initGui();

		addElement(new ElementEnergyStored(this, 80, 18, myTile.getEnergyStorage()));

		addTab(new TabRedstone(this, myTile));
		addTab(new TabConfigCell(this, myTile));

		addTab(new TabInfo(this, myInfo));
		addTab(new TabTutorial(this, StringHelper.tutorialTabRedstone() + "\n\n" + StringHelper.tutorialTabConfigurationEnergy()));
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
	protected void updateElementInformation() {

		int change = 0;
		int change2 = 0;

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

		if (myTile.energyReceive > 0) {
			decRecv.setActive();
			decRecv.setToolTip(StringHelper.localize("info.thermalexpansion.decRecv") + " " + change + "/" + change2);
		} else {
			decRecv.setDisabled();
			decRecv.clearToolTip();
		}
		if (myTile.energyReceive < TileCell.MAX_RECEIVE[myTile.type]) {
			incRecv.setActive();
			incRecv.setToolTip(StringHelper.localize("info.thermalexpansion.incRecv") + " " + change + "/" + change2);
		} else {
			incRecv.setDisabled();
			incRecv.clearToolTip();
		}
		if (myTile.energySend > 0) {
			decSend.setActive();
			decSend.setToolTip(StringHelper.localize("info.thermalexpansion.decSend") + " " + change + "/" + change2);
		} else {
			decSend.setDisabled();
			decSend.clearToolTip();
		}
		if (myTile.energySend < TileCell.MAX_SEND[myTile.type]) {
			incSend.setActive();
			incSend.setToolTip(StringHelper.localize("info.thermalexpansion.incSend") + " " + change + "/" + change2);
		} else {
			incSend.setDisabled();
			incSend.clearToolTip();
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
	protected void drawGuiContainerForegroundLayer(int x, int y) {

		fontRendererObj.drawString(StringHelper.localize("info.thermalexpansion.maxIn") + ":",
				getCenteredOffset(StringHelper.localize("info.thermalexpansion.maxIn"), xSize / 2), 32, 0x404040);
		fontRendererObj.drawString(StringHelper.localize("info.thermalexpansion.maxOut") + ":",
				xSize / 2 + getCenteredOffset(StringHelper.localize("info.thermalexpansion.maxOut"), xSize / 2), 32, 0x404040);

		String recv = String.format("%-8s", "" + myTile.energyReceive + " RF/t");
		String send = String.format("%-8s", "" + myTile.energySend + " RF/t");

		int xRecv = 20;
		int xSend = 110;

		if (myTile.energyReceive < 10) {
			xRecv += 6;
		}
		if (myTile.energyReceive < 100) {
			xRecv += 6;
		}
		if (myTile.energyReceive < 1000) {
			xRecv += 6;
		}
		if (myTile.energyReceive >= 10000) {
			xRecv -= 6;
		}
		if (myTile.energyReceive >= 100000) {
			xRecv -= 3;
		}

		if (myTile.energySend < 10) {
			xSend += 6;
		}
		if (myTile.energySend < 100) {
			xSend += 6;
		}
		if (myTile.energySend < 1000) {
			xSend += 6;
		}
		if (myTile.energySend >= 10000) {
			xSend -= 6;
		}
		if (myTile.energySend >= 100000) {
			xSend -= 3;
		}

		// if (myTile.energyReceive < 10) {
		// xRecv = 32;
		// } else if (myTile.energyReceive < 100) {
		// xRecv = 26;
		// } else if (myTile.energyReceive >= 10000) {
		// xRecv = 14;
		// } else if (myTile.energyReceive >= 1000) {
		// xRecv = 17;
		// }
		// if (myTile.energySend < 10) {
		// xSend = 122;
		// } else if (myTile.energySend < 100) {
		// xSend = 116;
		// } else if (myTile.energySend >= 10000) {
		// xSend = 104;
		// } else if (myTile.energySend >= 1000) {
		// xSend = 107;
		// }
		fontRendererObj.drawString(recv, xRecv, 44, 0x404040);
		fontRendererObj.drawString(send, xSend, 44, 0x404040);

		super.drawGuiContainerForegroundLayer(x, y);
	}

}
