package thermalexpansion.gui.client.plate;

import cofh.core.gui.GuiBaseAdv;
import cofh.core.gui.element.TabInfo;
import cofh.core.gui.element.TabSecurity;
import cofh.lib.gui.element.ElementButton;
import cofh.lib.gui.element.ElementFluid;
import cofh.lib.gui.element.ElementSimpleToolTip;
import cofh.lib.gui.element.listbox.SliderHorizontal;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

import thermalexpansion.block.plate.TilePlateSignal;
import thermalexpansion.core.TEProps;
import thermalexpansion.gui.container.ContainerTEBase;
import thermalfoundation.fluid.TFFluids;

public class GuiPlateSignal extends GuiBaseAdv {

	static final String TEX_PATH = TEProps.PATH_GUI_PLATE + "Signal.png";
	static final ResourceLocation TEXTURE = new ResourceLocation(TEX_PATH);
	static final String INFO = "Emits a redstone signal at another location upon contact.\n\nSignal parameters can be configured.\n\nWrench while sneaking to dismantle.";

	TilePlateSignal myTile;
	String playerName;

	ElementButton decDistance;
	ElementButton incDistance;
	ElementButton decIntensity;
	ElementButton incIntensity;
	ElementButton decDuration;
	ElementButton incDuration;

	public GuiPlateSignal(InventoryPlayer inventory, TileEntity theTile) {

		super(new ContainerTEBase(inventory, theTile), TEXTURE);
		myTile = (TilePlateSignal) theTile;
		name = myTile.getInventoryName();
		playerName = inventory.player.getCommandSenderName();

	}

	@Override
	public void initGui() {

		super.initGui();

		addTab(new TabInfo(this, INFO));
		if (myTile.enableSecurity() && myTile.isSecured()) {
			addTab(new TabSecurity(this, myTile, playerName));
		}

		addElement(new ElementSimpleToolTip(this, 13, 20).setToolTip("Dist").setSize(24, 16).setTexture(TEX_DROP_RIGHT, 48, 16));
		addElement(new ElementSimpleToolTip(this, 49, 20).setToolTip("Strength").setSize(24, 16).setTexture(TEX_DROP_RIGHT, 48, 16));
		addElement(new ElementSimpleToolTip(this, 85, 20).setToolTip("Pants").setSize(24, 16).setTexture(TEX_DROP_RIGHT, 48, 16));

		addElement(new ElementFluid(this, 134, 32).setFluid(TFFluids.fluidRedstone).setSize(16, 16));

		SliderHorizontal slider = new SliderHorizontal(this, 10, 56, 30, 16, 16) {

			@Override
			public void onValueChanged(int value) {

			}
		};

		addElement(slider);

		decDistance = new ElementButton(this, 10, 56, "decDistance", 176, 0, 176, 14, 176, 28, 14, 14, TEX_PATH).setToolTipLocalized(true);
		incDistance = new ElementButton(this, 26, 56, "incDistance", 190, 0, 190, 14, 190, 28, 14, 14, TEX_PATH).setToolTipLocalized(true);
		decIntensity = new ElementButton(this, 46, 56, "decIntensity", 176, 0, 176, 14, 176, 28, 14, 14, TEX_PATH).setToolTipLocalized(true);
		incIntensity = new ElementButton(this, 62, 56, "incIntensity", 190, 0, 190, 14, 190, 28, 14, 14, TEX_PATH).setToolTipLocalized(true);
		decDuration = new ElementButton(this, 82, 56, "decDuration", 176, 0, 176, 14, 176, 28, 14, 14, TEX_PATH).setToolTipLocalized(true);
		incDuration = new ElementButton(this, 98, 56, "incDuration", 190, 0, 190, 14, 190, 28, 14, 14, TEX_PATH).setToolTipLocalized(true);

		addElement(decDistance);
		addElement(incDistance);
		addElement(decIntensity);
		addElement(incIntensity);
		addElement(decDuration);
		addElement(incDuration);
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
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int x, int y) {

		int xDistance = 26;
		int xIntensity = 62;
		int xDuration = 98;

		String strDistance = String.format("%-8s", "" + myTile.distance);
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
		fontRendererObj.drawString(strDistance, xDistance, 47, 0x404040);
		fontRendererObj.drawString(strIntensity, xIntensity, 47, 0x404040);
		fontRendererObj.drawString(strDuration, xDuration, 47, 0x404040);

		super.drawGuiContainerForegroundLayer(x, y);
	}

	@Override
	protected void mouseClicked(int mX, int mY, int mouseButton) {

		if (134 <= mouseX && mouseX < 150 && 32 <= mouseY && mouseY < 48) {
			int facing = myTile.getFacing();

			if (mouseButton == 1) {
				facing += 5;
			} else {
				facing++;
			}
			facing %= 6;
			if (myTile.setFacing(facing)) {
				myTile.sendModePacket();
			}
		} else {
			super.mouseClicked(mX, mY, mouseButton);
		}
	}

}
