package cofh.thermalexpansion.gui.client.plate;

import cofh.core.gui.GuiBaseAdv;
import cofh.core.gui.element.TabInfo;
import cofh.core.gui.element.TabRedstone;
import cofh.core.gui.element.TabSecurity;
import cofh.lib.gui.element.ElementButton;
import cofh.lib.gui.element.ElementEnergyStored;
import cofh.lib.gui.element.ElementSimpleToolTip;
import cofh.lib.util.helpers.SecurityHelper;
import cofh.thermalexpansion.block.plate.TilePlateExcursion;
import cofh.thermalexpansion.core.TEProps;
import cofh.thermalexpansion.gui.container.ContainerTEBase;

import java.util.UUID;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;

public class GuiPlateExcursion extends GuiBaseAdv {

	static final String TEX_PATH = TEProps.PATH_GUI + "plate/Plate.png";
	static final ResourceLocation TEXTURE = new ResourceLocation(TEX_PATH);

	TilePlateExcursion myTile;
	UUID playerName;

	ElementButton decDistance;
	ElementButton incDistance;

	public GuiPlateExcursion(InventoryPlayer inventory, TilePlateExcursion theTile) {

		super(new ContainerTEBase(inventory, theTile, false, false), TEXTURE);

		myTile = theTile;
		name = myTile.getName();
		playerName = SecurityHelper.getID(inventory.player);
		drawInventory = false;
		this.ySize = 100;
	}

	@Override
	public void initGui() {

		super.initGui();

		generateInfo("tab.thermalexpansion.plate.excursion", 2);

		if (!myInfo.isEmpty()) {
			addTab(new TabInfo(this, myInfo));
		}
		addTab(new TabRedstone(this, myTile));
		if (myTile.enableSecurity() && myTile.isSecured()) {
			addTab(new TabSecurity(this, myTile, playerName));
		}

		addElement(new ElementEnergyStored(this, 8, 18, myTile.getEnergyStorage()));

		addElement(new ElementSimpleToolTip(this, 49, 24).setToolTip("info.cofh.distance").setSize(24, 24).setTexture(TEX_INFO_DISTANCE, 32, 32));

		decDistance = new ElementButton(this, 46, 66, "decDistance", 176, 0, 176, 14, 176, 28, 14, 14, TEX_PATH).setToolTipLocalized(true);
		incDistance = new ElementButton(this, 62, 66, "incDistance", 190, 0, 190, 14, 190, 28, 14, 14, TEX_PATH).setToolTipLocalized(true);

		addElement(decDistance);
		addElement(incDistance);
	}

	@Override
	public void handleElementButtonClick(String buttonName, int mouseButton) {

		int change = 1;
		float pitch = 0.7F;

		byte curDistance = myTile.distance;

		if (GuiScreen.isShiftKeyDown()) {
			change = 10;
			pitch = 0.9F;
			if (mouseButton == 1) {
				change = 5;
				pitch = 0.8F;
			}
		}
		if (buttonName.equalsIgnoreCase("decDistance")) {
			myTile.distance -= change;
			pitch -= 0.1F;
		} else if (buttonName.equalsIgnoreCase("incDistance")) {
			myTile.distance += change;
			pitch += 0.1F;
		}
		playSound("random.click", 1.0F, pitch);

		myTile.sendModePacket();

		myTile.distance = curDistance;
	}

	@Override
	protected void updateElementInformation() {

		decDistance.setEnabled(myTile.distance > TilePlateExcursion.MIN_DISTANCE);
		incDistance.setEnabled(myTile.distance < TilePlateExcursion.MAX_DISTANCE);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int x, int y) {

		int xDistance = 62;

		String strDistance = String.format("%-8s", "" + (myTile.distance + 1));

		if (myTile.distance < 9) {
			xDistance += 6;
		}
		fontRendererObj.drawString(strDistance, xDistance, 56, 0x404040);

		super.drawGuiContainerForegroundLayer(x, y);
	}

	@Override
	public void updateScreen() {

		super.updateScreen();

		if (!myTile.canAccess()) {
			this.mc.thePlayer.closeScreen();
		}
	}

}
