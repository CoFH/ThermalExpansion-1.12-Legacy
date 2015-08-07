package cofh.thermalexpansion.gui.client.plate;

import cofh.core.gui.GuiBaseAdv;
import cofh.core.gui.element.TabInfo;
import cofh.core.gui.element.TabSecurity;
import cofh.lib.gui.element.ElementEnergyStored;
import cofh.lib.util.helpers.SecurityHelper;
import cofh.thermalexpansion.block.plate.TilePlateCharger;
import cofh.thermalexpansion.core.TEProps;

import java.util.UUID;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;

public class GuiPlateCharger extends GuiBaseAdv {

	//static final String TEX_PATH = TEProps.PATH_GUI + "plate/Charger.png";
	static final String TEX_PATH = TEProps.PATH_GUI + "plate/Plate.png";
	static final ResourceLocation TEXTURE = new ResourceLocation(TEX_PATH);

	TilePlateCharger myTile;
	UUID playerName;

	public GuiPlateCharger(InventoryPlayer inventory, TilePlateCharger theTile) {

		super(theTile.getGuiServer(inventory), TEXTURE);
		myTile = theTile;
		name = myTile.getInventoryName();
		playerName = SecurityHelper.getID(inventory.player);
		drawInventory = false;
		this.height = 100;
	}

	@Override
	public void initGui() {

		super.initGui();

		// generateInfo("tab.thermalexpansion.plate.charge", 2);

		if (!myInfo.isEmpty()) {
			addTab(new TabInfo(this, myInfo));
		}
		if (myTile.enableSecurity() && myTile.isSecured()) {
			addTab(new TabSecurity(this, myTile, playerName));
		}

		addElement(new ElementEnergyStored(this, 80, 18, myTile.getEnergyStorage()));
	}

	@Override
	public void updateScreen() {

		super.updateScreen();

		if (!myTile.canAccess()) {
			this.mc.thePlayer.closeScreen();
		}
	}

}
