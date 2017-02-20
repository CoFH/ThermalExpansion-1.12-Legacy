package cofh.thermalexpansion.gui.client.machine;

import cofh.lib.gui.element.*;
import cofh.thermalexpansion.block.machine.TileTransposer;
import cofh.thermalexpansion.gui.client.GuiPoweredBase;
import cofh.thermalexpansion.gui.container.machine.ContainerTransposer;
import cofh.thermalexpansion.gui.element.ElementSlotOverlay;
import cofh.thermalexpansion.init.TEProps;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

public class GuiTransposer extends GuiPoweredBase {

	public static final String TEX_PATH = TEProps.PATH_GUI_MACHINE + "transposer.png";
	public static final ResourceLocation TEXTURE = new ResourceLocation(TEX_PATH);

	private TileTransposer myTile;

	private ElementBase slotInput;
	private ElementSlotOverlay[] slotOutput = new ElementSlotOverlay[2];

	private ElementBase slotTank;
	private ElementSlotOverlay[] slotTankRev = new ElementSlotOverlay[2];

	private ElementFluid progressFluid;
	private ElementSimple progressBackgroundRev;

	private ElementDualScaled progressOverlay;
	private ElementDualScaled progressOverlayRev;

	private ElementDualScaled speed;
	private ElementButton mode;

	public GuiTransposer(InventoryPlayer inventory, TileEntity tile) {

		super(new ContainerTransposer(inventory, tile), tile, inventory.player, TEXTURE);

		generateInfo("tab.thermalexpansion.machine.transposer", 3);

		myTile = (TileTransposer) tile;
	}

	@Override
	public void initGui() {

		super.initGui();

		slotInput = addElement(new ElementSlotOverlay(this, 44, 19).setSlotInfo(0, 0, 2));
		slotOutput[0] = (ElementSlotOverlay) addElement(new ElementSlotOverlay(this, 76, 45).setSlotInfo(3, 1, 2));
		slotOutput[1] = (ElementSlotOverlay) addElement(new ElementSlotOverlay(this, 76, 45).setSlotInfo(1, 1, 1));

		slotTank = addElement(new ElementSlotOverlay(this, 152, 9).setSlotInfo(0, 3, 2));
		slotTankRev[0] = (ElementSlotOverlay) addElement(new ElementSlotOverlay(this, 152, 9).setSlotInfo(3, 3, 2).setVisible(false));
		slotTankRev[1] = (ElementSlotOverlay) addElement(new ElementSlotOverlay(this, 152, 9).setSlotInfo(2, 3, 1).setVisible(false));

		addElement(new ElementEnergyStored(this, 8, 8, myTile.getEnergyStorage()));
		addElement(new ElementFluidTank(this, 152, 9, myTile.getTank()).setGauge(1).setAlwaysShow(true));

		progressBackgroundRev = (ElementSimple) addElement(new ElementSimple(this, 112, 19).setSize(24, 16).setTexture(TEX_DROP_RIGHT, 64, 16));
		progressFluid = (ElementFluid) addElement(new ElementFluid(this, 112, 19).setFluid(myTile.getTankFluid()).setSize(24, 16));

		progressOverlay = (ElementDualScaled) addElement(new ElementDualScaled(this, 112, 19).setMode(2).setBackground(false).setSize(24, 16).setTexture(TEX_DROP_LEFT, 64, 16));
		progressOverlayRev = (ElementDualScaled) addElement(new ElementDualScaled(this, 112, 19).setMode(1).setBackground(false).setSize(24, 16).setTexture(TEX_DROP_RIGHT, 64, 16));

		speed = (ElementDualScaled) addElement(new ElementDualScaled(this, 44, 49).setSize(16, 16).setTexture(TEX_BUBBLE, 32, 16));
		mode = (ElementButton) addElement(new ElementButton(this, 116, 49, "Mode", 176, 0, 176, 16, 176, 32, 16, 16, TEX_PATH));
	}

	@Override
	protected void updateElementInformation() {

		super.updateElementInformation();

		slotInput.setVisible(myTile.hasSide(1));
		slotOutput[0].setVisible(myTile.hasSide(4));
		slotOutput[1].setVisible(myTile.hasSide(2));
		slotTank.setVisible(!myTile.extractMode && myTile.hasSide(1));
		slotTankRev[0].setVisible(myTile.extractMode && myTile.hasSide(4));
		slotTankRev[1].setVisible(myTile.extractMode && myTile.hasSide(3));

		progressBackgroundRev.setVisible(myTile.extractMode);
		progressFluid.setFluid(myTile.getTankFluid());
		progressFluid.setSize(myTile.getEnergyStored(null) > 0 ? myTile.getScaledProgress(PROGRESS) : 0, 16);

		if (!myTile.hasSide(4)) {
			slotOutput[1].slotRender = 2;
			slotTankRev[1].slotRender = 2;
		} else {
			slotOutput[1].slotRender = 1;
			slotTankRev[1].slotRender = 1;
		}
		if (myTile.extractMode) {
			progressFluid.setPosition(112, 19);
		} else {
			progressFluid.setPosition(112 + PROGRESS - myTile.getScaledProgress(PROGRESS), 19);
		}
		progressOverlay.setVisible(!myTile.extractMode);
		progressOverlay.setQuantity(myTile.getEnergyStored(null) > 0 ? myTile.getScaledProgress(PROGRESS) : 0);
		progressOverlayRev.setVisible(myTile.extractMode);
		progressOverlayRev.setQuantity(myTile.getEnergyStored(null) > 0 ? myTile.getScaledProgress(PROGRESS) : 0);
		speed.setQuantity(myTile.getEnergyStored(null) > 0 ? myTile.getScaledSpeed(SPEED) : 0);

		if (myTile.extractMode) {
			if (!myTile.extractFlag) {
				mode.setToolTip("gui.thermalexpansion.machine.transposer.modeWait");
				mode.setDisabled();
			} else {
				mode.setToolTip("gui.thermalexpansion.machine.transposer.modeEmpty");
				mode.setSheetX(192);
				mode.setHoverX(192);
				mode.setActive();
			}
		} else {
			if (myTile.extractFlag) {
				mode.setToolTip("gui.thermalexpansion.machine.transposer.modeWait");
				mode.setDisabled();
			} else {
				mode.setToolTip("gui.thermalexpansion.machine.transposer.modeFill");
				mode.setSheetX(176);
				mode.setHoverX(176);
				mode.setActive();
			}
		}
	}

	@Override
	public void handleElementButtonClick(String buttonName, int mouseButton) {

		if (buttonName.equals("Mode")) {
			if (myTile.extractMode == myTile.extractFlag) {
				if (myTile.extractMode) {
					playClickSound(1.0F, 0.8F);
				} else {
					playClickSound(1.0F, 0.6F);
				}
				myTile.setMode(!myTile.extractMode);
			}
		}
	}

}
