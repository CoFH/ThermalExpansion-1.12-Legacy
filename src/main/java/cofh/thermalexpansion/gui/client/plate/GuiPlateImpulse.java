package cofh.thermalexpansion.gui.client.plate;

import cofh.core.gui.GuiBaseAdv;
import cofh.core.gui.element.TabInfo;
import cofh.core.gui.element.TabSecurity;
import cofh.core.render.IconRegistry;
import cofh.lib.gui.element.ElementButton;
import cofh.lib.gui.element.ElementFluid;
import cofh.lib.gui.element.ElementIcon;
import cofh.lib.gui.element.ElementSimpleToolTip;
import cofh.thermalexpansion.block.plate.TilePlateImpulse;
import cofh.thermalexpansion.core.TEProps;
import cofh.thermalexpansion.gui.container.ContainerTEBase;
import cofh.thermalfoundation.fluid.TFFluids;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

public class GuiPlateImpulse extends GuiBaseAdv {

	static final String TEX_PATH = TEProps.PATH_GUI + "Plate.png";
	static final ResourceLocation TEXTURE = new ResourceLocation(TEX_PATH);

	TilePlateImpulse myTile;
	String playerName;

	ElementButton decIntensity;
	ElementButton incIntensity;
	ElementButton decAngle;
	ElementButton incAngle;

	ElementIcon plateTop;

	public GuiPlateImpulse(InventoryPlayer inventory, TileEntity theTile) {

		super(new ContainerTEBase(inventory, theTile, false, false), TEXTURE);
		myTile = (TilePlateImpulse) theTile;
		name = myTile.getInventoryName();
		playerName = inventory.player.getCommandSenderName();
		drawInventory = false;
		this.height = 100;

		generateInfo("tab.thermalexpansion.plate.impulse", 2);
	}

	@Override
	public void initGui() {

		super.initGui();

		addTab(new TabInfo(this, myInfo));
		if (myTile.enableSecurity() && myTile.isSecured()) {
			addTab(new TabSecurity(this, myTile, playerName));
		}

		addElement(new ElementSimpleToolTip(this, 13, 24).setToolTip("info.cofh.force").setSize(24, 24).setTexture(TEX_INFO_FORCE, 24, 24));
		addElement(new ElementSimpleToolTip(this, 85, 24).setToolTip("info.cofh.angle").setSize(24, 24).setTexture(TEX_INFO_ANGLE, 24, 24));

		addElement(new ElementFluid(this, 134, 32).setFluid(TFFluids.fluidGlowstone).setSize(16, 16));

		decIntensity = new ElementButton(this, 10, 66, "decIntensity", 176, 0, 176, 14, 176, 28, 14, 14, TEX_PATH).setToolTipLocalized(true);
		incIntensity = new ElementButton(this, 26, 66, "incIntensity", 190, 0, 190, 14, 190, 28, 14, 14, TEX_PATH).setToolTipLocalized(true);
		decAngle = new ElementButton(this, 82, 66, "decAngle", 176, 0, 176, 14, 176, 28, 14, 14, TEX_PATH).setToolTipLocalized(true);
		incAngle = new ElementButton(this, 98, 66, "incAngle", 190, 0, 190, 14, 190, 28, 14, 14, TEX_PATH).setToolTipLocalized(true);

		addElement(decIntensity);
		addElement(incIntensity);
		addElement(decAngle);
		addElement(incAngle);

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
		float pitch = 1.0F;

		int curIntensity = myTile.intensity;
		int curAngle = myTile.angle;

		if (GuiScreen.isShiftKeyDown()) {
			change = 100;
			pitch = 0.9F;
			if (mouseButton == 1) {
				change = 10;
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
		if (buttonName.equalsIgnoreCase("decIntensity")) {
			myTile.intensity -= (Math.max(1, change / 5));
			pitch -= 0.1F;
		} else if (buttonName.equalsIgnoreCase("incIntensity")) {
			myTile.intensity += (Math.max(1, change / 5));
			pitch += 0.1F;
		} else if (buttonName.equalsIgnoreCase("decAngle")) {
			myTile.angle -= change;
			pitch -= 0.1F;
		} else if (buttonName.equalsIgnoreCase("incAngle")) {
			myTile.angle += change;
			pitch += 0.1F;
		}
		playSound("random.click", 1.0F, pitch);

		myTile.sendModePacket();

		myTile.intensity = curIntensity;
		myTile.angle = curAngle;
	}

	@Override
	protected void updateElementInformation() {

		if (myTile.intensity > TilePlateImpulse.MIN_INTENSITY) {
			decIntensity.setActive();
		} else {
			decIntensity.setDisabled();
		}
		if (myTile.intensity < TilePlateImpulse.MAX_INTENSITY) {
			incIntensity.setActive();
		} else {
			incIntensity.setDisabled();
		}
		if (myTile.angle > TilePlateImpulse.MIN_ANGLE) {
			decAngle.setActive();
		} else {
			decAngle.setDisabled();
		}
		if (myTile.angle < TilePlateImpulse.MAX_ANGLE) {
			incAngle.setActive();
		} else {
			incAngle.setDisabled();
		}
		plateTop.setIcon(IconRegistry.getIcon("PlateTop", myTile.getFacing()));
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int x, int y) {

		int xIntensity = 26;
		int xAngle = 92;

		String strIntensity = String.format("%-8.1f", myTile.intensity / 10D);
		String strAngle = String.format("%-8.1f", myTile.angle / 10D);

		if (myTile.angle < 100 && myTile.angle >= 0) {
			xAngle += 6;
		} else if (myTile.angle <= -100) {
			xAngle -= 6;
		}
		fontRendererObj.drawString(strIntensity, xIntensity, 56, 0x404040);
		fontRendererObj.drawString(strAngle, xAngle, 56, 0x404040);

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
