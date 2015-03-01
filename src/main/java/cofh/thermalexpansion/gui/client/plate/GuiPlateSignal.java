package cofh.thermalexpansion.gui.client.plate;

import cofh.core.gui.GuiBaseAdv;
import cofh.core.gui.element.TabInfo;
import cofh.core.gui.element.TabSecurity;
import cofh.core.render.IconRegistry;
import cofh.lib.gui.element.ElementButton;
import cofh.lib.gui.element.ElementFluid;
import cofh.lib.gui.element.ElementIcon;
import cofh.lib.gui.element.ElementSimpleToolTip;
import cofh.thermalexpansion.block.plate.TilePlateSignal;
import cofh.thermalexpansion.core.TEProps;
import cofh.thermalexpansion.gui.container.ContainerTEBase;
import cofh.thermalfoundation.fluid.TFFluids;

import java.util.UUID;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

public class GuiPlateSignal extends GuiBaseAdv {

	static final String TEX_PATH = TEProps.PATH_GUI + "Plate.png";
	static final ResourceLocation TEXTURE = new ResourceLocation(TEX_PATH);

	TilePlateSignal myTile;
	UUID playerName;

	ElementButton decDistance;
	ElementButton incDistance;
	ElementButton decIntensity;
	ElementButton incIntensity;
	ElementButton decDuration;
	ElementButton incDuration;

	ElementIcon plateTop;

	public GuiPlateSignal(InventoryPlayer inventory, TileEntity theTile) {

		super(new ContainerTEBase(inventory, theTile, false, false), TEXTURE);
		myTile = (TilePlateSignal) theTile;
		name = myTile.getInventoryName();
		playerName = inventory.player.getGameProfile().getId();
		drawInventory = false;
		this.height = 100;

		generateInfo("tab.thermalexpansion.plate.signal", 2);
	}

	@Override
	public void initGui() {

		super.initGui();

		addTab(new TabInfo(this, myInfo));
		if (myTile.enableSecurity() && myTile.isSecured()) {
			addTab(new TabSecurity(this, myTile, playerName));
		}

		addElement(new ElementSimpleToolTip(this, 13, 24).setToolTip("info.cofh.distance").setSize(24, 24).setTexture(TEX_INFO_DISTANCE, 24, 24));
		addElement(new ElementSimpleToolTip(this, 49, 24).setToolTip("info.cofh.strength").setSize(24, 24).setTexture(TEX_INFO_SIGNAL, 24, 24));
		addElement(new ElementSimpleToolTip(this, 85, 24).setToolTip("info.cofh.duration").setSize(24, 24).setTexture(TEX_INFO_DURATION, 24, 24));

		addElement(new ElementFluid(this, 134, 32).setFluid(TFFluids.fluidRedstone).setSize(16, 16));

		decDistance = new ElementButton(this, 10, 66, "decDistance", 176, 0, 176, 14, 176, 28, 14, 14, TEX_PATH).setToolTipLocalized(true);
		incDistance = new ElementButton(this, 26, 66, "incDistance", 190, 0, 190, 14, 190, 28, 14, 14, TEX_PATH).setToolTipLocalized(true);
		decIntensity = new ElementButton(this, 46, 66, "decIntensity", 176, 0, 176, 14, 176, 28, 14, 14, TEX_PATH).setToolTipLocalized(true);
		incIntensity = new ElementButton(this, 62, 66, "incIntensity", 190, 0, 190, 14, 190, 28, 14, 14, TEX_PATH).setToolTipLocalized(true);
		decDuration = new ElementButton(this, 82, 66, "decDuration", 176, 0, 176, 14, 176, 28, 14, 14, TEX_PATH).setToolTipLocalized(true);
		incDuration = new ElementButton(this, 98, 66, "incDuration", 190, 0, 190, 14, 190, 28, 14, 14, TEX_PATH).setToolTipLocalized(true);

		addElement(decDistance);
		addElement(incDistance);
		addElement(decIntensity);
		addElement(incIntensity);
		addElement(decDuration);
		addElement(incDuration);

		plateTop = new ElementIcon(this, 134, 32, IconRegistry.getIcon("PlateTop", myTile.getFacing()));
		addElement(plateTop);
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

		int change = 1;
		float pitch = 0.7F;

		byte curDistance = myTile.distance;
		byte curIntensity = myTile.intensity;
		byte curDuration = myTile.duration;

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
		} else if (buttonName.equalsIgnoreCase("decIntensity")) {
			myTile.intensity -= change;
			pitch -= 0.1F;
		} else if (buttonName.equalsIgnoreCase("incIntensity")) {
			myTile.intensity += change;
			pitch += 0.1F;
		} else if (buttonName.equalsIgnoreCase("decDuration")) {
			myTile.duration -= change;
			pitch -= 0.1F;
		} else if (buttonName.equalsIgnoreCase("incDuration")) {
			myTile.duration += change;
			pitch += 0.1F;
		}
		playSound("random.click", 1.0F, pitch);

		myTile.sendModePacket();

		myTile.distance = curDistance;
		myTile.intensity = curIntensity;
		myTile.duration = curDuration;
	}

	@Override
	protected void updateElementInformation() {

		if (myTile.distance > TilePlateSignal.MIN_DISTANCE) {
			decDistance.setActive();
		} else {
			decDistance.setDisabled();
		}
		if (myTile.distance < TilePlateSignal.MAX_DISTANCE) {
			incDistance.setActive();
		} else {
			incDistance.setDisabled();
		}
		if (myTile.intensity > TilePlateSignal.MIN_INTENSITY) {
			decIntensity.setActive();
		} else {
			decIntensity.setDisabled();
		}
		if (myTile.intensity < TilePlateSignal.MAX_INTENSITY) {
			incIntensity.setActive();
		} else {
			incIntensity.setDisabled();
		}
		if (myTile.duration > TilePlateSignal.MIN_DURATION) {
			decDuration.setActive();
		} else {
			decDuration.setDisabled();
		}
		if (myTile.duration < TilePlateSignal.MAX_DURATION) {
			incDuration.setActive();
		} else {
			incDuration.setDisabled();
		}
		plateTop.setIcon(IconRegistry.getIcon("PlateTop", myTile.getFacing()));
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int x, int y) {

		int xDistance = 26;
		int xIntensity = 62;
		int xDuration = 98;

		String strDistance = String.format("%-8s", "" + (myTile.distance + 1));
		String strIntensity = String.format("%-8s", "" + myTile.intensity);
		String strDuration = String.format("%-8s", "" + myTile.duration);

		if (myTile.distance < 10) {
			xDistance += 6;
		}
		if (myTile.intensity < 10) {
			xIntensity += 6;
		}
		if (myTile.duration < 10) {
			xDuration += 6;
		}
		fontRendererObj.drawString(strDistance, xDistance, 56, 0x404040);
		fontRendererObj.drawString(strIntensity, xIntensity, 56, 0x404040);
		fontRendererObj.drawString(strDuration, xDuration, 56, 0x404040);

		super.drawGuiContainerForegroundLayer(x, y);
	}

	@Override
	protected void mouseClicked(int mX, int mY, int mouseButton) {

		if (134 <= mouseX && mouseX < 150 && 32 <= mouseY && mouseY < 48) {
			int facing = myTile.getFacing();
			float pitch = 0.7F;

			if (mouseButton == 1) {
				facing += 5;
				pitch -= 0.1F;
			} else {
				facing++;
				pitch += 0.1F;
			}
			facing %= 6;
			if (myTile.setFacing(facing)) {
				playSound("random.click", 1.0F, pitch);
				myTile.sendModePacket();
			}
		} else {
			super.mouseClicked(mX, mY, mouseButton);
		}
	}

}
