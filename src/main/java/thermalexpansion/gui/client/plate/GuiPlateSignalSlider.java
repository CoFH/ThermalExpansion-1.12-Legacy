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

public class GuiPlateSignalSlider extends GuiBaseAdv {

	static final String TEX_PATH = TEProps.PATH_GUI + "Plate.png";
	static final ResourceLocation TEXTURE = new ResourceLocation(TEX_PATH);
	static final String INFO = "Emits a redstone signal at another location upon contact.\n\nSignal parameters can be configured.\n\nWrench while sneaking to dismantle.";

	TilePlateSignal myTile;
	String playerName;

	SliderHorizontal sliDistance;
	SliderHorizontal sliIntensity;
	SliderHorizontal sliDuration;

	int dispDistance;
	int dispIntensity;
	int dispDuration;

	ElementButton confirm;

	public GuiPlateSignalSlider(InventoryPlayer inventory, TileEntity theTile) {

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

		sliDistance = new SliderHorizontal(this, 10, 54, 30, 16, TilePlateSignal.MAX_DISTANCE, TilePlateSignal.MIN_DISTANCE) {

			@Override
			public void onValueChanged(int value) {

				dispDistance = value;
			}
		};

		sliIntensity = new SliderHorizontal(this, 46, 54, 30, 16, TilePlateSignal.MAX_INTENSITY, TilePlateSignal.MIN_INTENSITY) {

			@Override
			public void onValueChanged(int value) {

				dispIntensity = value;
			}
		};

		sliDuration = new SliderHorizontal(this, 82, 54, 30, 16, TilePlateSignal.MAX_DURATION, TilePlateSignal.MIN_DURATION) {

			@Override
			public void onValueChanged(int value) {

				dispDuration = value;
			}
		};

		addElement(sliDistance);
		addElement(sliIntensity);
		addElement(sliDuration);
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

	}

	@Override
	protected void drawGuiContainerForegroundLayer(int x, int y) {

		int xDistance = 26;
		int xIntensity = 62;
		int xDuration = 98;

		String strDistance = String.format("%-8s", "" + dispDistance);
		String strIntensity = String.format("%-8s", "" + dispIntensity);
		String strDuration = String.format("%-8s", "" + dispDuration);

		if (myTile.distance < 10) {
			xDistance += 6;
		}
		if (myTile.intensity < 10) {
			xIntensity += 6;
		}
		if (myTile.duration < 10) {
			xDuration += 6;
		}
		fontRendererObj.drawString(strDistance, xDistance, 45, 0x404040);
		fontRendererObj.drawString(strIntensity, xIntensity, 45, 0x404040);
		fontRendererObj.drawString(strDuration, xDuration, 45, 0x404040);

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
