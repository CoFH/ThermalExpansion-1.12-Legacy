package cofh.thermalexpansion.gui.client.storage;

import cofh.core.gui.GuiCore;
import cofh.core.gui.element.*;
import cofh.lib.gui.element.ElementButton;
import cofh.lib.gui.element.TabBase;
import cofh.lib.util.helpers.SecurityHelper;
import cofh.lib.util.helpers.StringHelper;
import cofh.thermalexpansion.block.storage.TileCell;
import cofh.thermalexpansion.gui.container.ContainerTEBase;
import cofh.thermalexpansion.init.TEProps;
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

	ElementButton decRecv;
	ElementButton incRecv;
	ElementButton decSend;
	ElementButton incSend;

	public GuiCell(InventoryPlayer inventory, TileEntity tile) {

		super(new ContainerTEBase(inventory, tile), TEXTURE);

		baseTile = (TileCell) tile;
		name = baseTile.getName();
		playerName = SecurityHelper.getID(inventory.player);

		if (baseTile.enableSecurity() && baseTile.isSecured()) {
			myTutorial += StringHelper.tutorialTabSecurity() + "\n\n";
		}
		myTutorial += StringHelper.tutorialTabRedstone() + "\n\n";
		myTutorial += StringHelper.tutorialTabConfiguration();
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

}
