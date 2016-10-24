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

	ElementButton settingItems;

	public GuiPlateCharge(InventoryPlayer inventory, TileEntity theTile) {

		super(new ContainerTEBase(inventory, theTile, false, false), TEXTURE);

		myTile = (TilePlateCharge) theTile;
		name = myTile.getName();
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

		settingItems = new ElementButton(this, 120, 36, "Items", 176, 42, 176, 62, 176, 82, 20, 20, TEX_PATH);

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
	protected void updateElementInformation() {

		int x = !myTile.chargeItems ? 196 : 176;
		settingItems.setDisabledX(x);
		settingItems.setHoverX(x);
		settingItems.setSheetX(x);

		settingItems.setToolTip("info.thermalexpansion.plate.charge." + (myTile.chargeItems ? "anything" : "playersOnly"));
	}

	@Override
	public void handleElementButtonClick(String buttonName, int mouseButton) {

		if (buttonName.equalsIgnoreCase("Items")) {
			myTile.chargeItems = !myTile.chargeItems;

			playSound("random.click", 1.0F, myTile.chargeItems ? 0.8F : 0.6F);
		}
		myTile.sendModePacket();
	}

}
