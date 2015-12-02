package cofh.thermalexpansion.gui.client.plate;

import cofh.core.gui.GuiBaseAdv;
import cofh.core.gui.element.TabInfo;
import cofh.core.gui.element.TabSecurity;
import cofh.lib.gui.element.ElementButton;
import cofh.lib.gui.element.ElementEnergyStored;
import cofh.lib.util.helpers.SecurityHelper;
import cofh.thermalexpansion.block.plate.TilePlateCharge;
import cofh.thermalexpansion.core.TEProps;
import cofh.thermalexpansion.gui.container.ContainerTEBase;

import java.util.UUID;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

public class GuiPlateCharge extends GuiBaseAdv {

	static final String TEX_PATH = TEProps.PATH_GUI + "plate/Plate.png";
	static final ResourceLocation TEXTURE = new ResourceLocation(TEX_PATH);

	TilePlateCharge myTile;
	UUID playerName;

	ElementButton settingOwner;
	ElementButton settingFriends;
	ElementButton settingPublic;
	ElementButton settingItems;

	public GuiPlateCharge(InventoryPlayer inventory, TileEntity theTile) {

		super(new ContainerTEBase(inventory, theTile, false, false), TEXTURE);

		myTile = (TilePlateCharge) theTile;
		name = myTile.getInventoryName();
		playerName = SecurityHelper.getID(inventory.player);
		drawInventory = false;
		this.ySize = 100;

		generateInfo("tab.thermalexpansion.plate.charge", 2);
	}

	@Override
	public void initGui() {

		super.initGui();

		addTab(new TabInfo(this, myInfo));
		if (myTile.enableSecurity() && myTile.isSecured()) {
			addTab(new TabSecurity(this, myTile, playerName));
		}
		addElement(new ElementEnergyStored(this, 16, 25, myTile.getEnergyStorage()));

		settingOwner = new ElementButton(this, 48, 36, "Owner", 176, 42, 176, 62, 176, 82, 20, 20, TEX_PATH);
		settingFriends = new ElementButton(this, 72, 36, "Friends", 196, 42, 196, 62, 196, 82, 20, 20, TEX_PATH);
		settingPublic = new ElementButton(this, 96, 36, "Public", 216, 42, 216, 62, 216, 82, 20, 20, TEX_PATH);
		settingItems = new ElementButton(this, 120, 36, "Items", 236, 42, 236, 62, 236, 82, 20, 20, TEX_PATH);

		addElement(settingOwner);
		addElement(settingFriends);
		addElement(settingPublic);
		addElement(settingItems);
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

		if (buttonName.equalsIgnoreCase("Owner")) {
			myTile.chargeOwner = !myTile.chargeOwner;

			playSound("random.click", 1.0F, myTile.chargeOwner ? 0.8F : 0.6F);
		} else if (buttonName.equalsIgnoreCase("Friends")) {
			myTile.chargeFriends = !myTile.chargeFriends;

			playSound("random.click", 1.0F, myTile.chargeFriends ? 0.8F : 0.6F);
		} else if (buttonName.equalsIgnoreCase("Public")) {
			myTile.chargePublic = !myTile.chargePublic;

			playSound("random.click", 1.0F, myTile.chargePublic ? 0.8F : 0.6F);
		} else if (buttonName.equalsIgnoreCase("Items")) {
			myTile.chargeItems = !myTile.chargeItems;

			playSound("random.click", 1.0F, myTile.chargeItems ? 0.8F : 0.6F);
		}
		myTile.sendModePacket();
	}

}
