package cofh.thermalexpansion.gui.client.machine;

import cofh.core.gui.element.*;
import cofh.thermalexpansion.block.machine.TileTransposer;
import cofh.thermalexpansion.gui.client.GuiPoweredBase;
import cofh.thermalexpansion.gui.container.machine.ContainerTransposer;
import cofh.thermalexpansion.gui.element.ElementSlotOverlay;
import cofh.thermalexpansion.gui.element.ElementSlotOverlay.SlotColor;
import cofh.thermalexpansion.gui.element.ElementSlotOverlay.SlotRender;
import cofh.thermalexpansion.gui.element.ElementSlotOverlay.SlotType;
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
	private ElementButton modeSel;

	public GuiTransposer(InventoryPlayer inventory, TileEntity tile) {

		super(new ContainerTransposer(inventory, tile), tile, inventory.player, TEXTURE);

		generateInfo("tab.thermalexpansion.machine.transposer");

		myTile = (TileTransposer) tile;
	}

	@Override
	public void initGui() {

		super.initGui();

		slotInput = addElement(new ElementSlotOverlay(this, 44, 19).setSlotInfo(SlotColor.BLUE, SlotType.STANDARD, SlotRender.FULL));
		slotOutput[0] = (ElementSlotOverlay) addElement(new ElementSlotOverlay(this, 76, 45).setSlotInfo(SlotColor.ORANGE, SlotType.OUTPUT, SlotRender.FULL));
		slotOutput[1] = (ElementSlotOverlay) addElement(new ElementSlotOverlay(this, 76, 45).setSlotInfo(SlotColor.RED, SlotType.OUTPUT, SlotRender.BOTTOM));

		slotTank = addElement(new ElementSlotOverlay(this, 152, 9).setSlotInfo(SlotColor.BLUE, SlotType.TANK, SlotRender.FULL));
		slotTankRev[0] = (ElementSlotOverlay) addElement(new ElementSlotOverlay(this, 152, 9).setSlotInfo(SlotColor.ORANGE, SlotType.TANK, SlotRender.FULL).setVisible(false));
		slotTankRev[1] = (ElementSlotOverlay) addElement(new ElementSlotOverlay(this, 152, 9).setSlotInfo(SlotColor.YELLOW, SlotType.TANK, SlotRender.BOTTOM).setVisible(false));

		if (!myTile.smallStorage()) {
			addElement(new ElementEnergyStored(this, 8, 8, myTile.getEnergyStorage()));
		}
		addElement(new ElementFluidTank(this, 152, 9, myTile.getTank()).setGauge(1).setAlwaysShow(true));

		progressBackgroundRev = (ElementSimple) addElement(new ElementSimple(this, 112, 19).setSize(24, 16).setTexture(TEX_DROP_RIGHT, 64, 16));
		progressFluid = (ElementFluid) addElement(new ElementFluid(this, 112, 19).setFluid(myTile.getTankFluid()).setSize(24, 16));

		progressOverlay = (ElementDualScaled) addElement(new ElementDualScaled(this, 112, 19).setMode(2).setBackground(false).setSize(24, 16).setTexture(TEX_DROP_LEFT, 64, 16));
		progressOverlayRev = (ElementDualScaled) addElement(new ElementDualScaled(this, 112, 19).setMode(1).setBackground(false).setSize(24, 16).setTexture(TEX_DROP_RIGHT, 64, 16));

		speed = (ElementDualScaled) addElement(new ElementDualScaled(this, 44, 49).setSize(16, 16).setTexture(TEX_BUBBLE, 32, 16));
		modeSel = (ElementButton) addElement(new ElementButton(this, 116, 49, "Mode", 176, 0, 176, 16, 176, 32, 16, 16, TEX_PATH));
	}

	@Override
	protected void updateElementInformation() {

		super.updateElementInformation();

		slotInput.setVisible(myTile.hasSideType(INPUT_ALL) || myTile.hasSideType(OMNI));
		slotOutput[0].setVisible(myTile.hasSideType(OUTPUT_ALL) || myTile.hasSideType(OMNI));
		slotOutput[1].setVisible(myTile.hasSideType(OUTPUT_PRIMARY));
		slotTank.setVisible(!myTile.extractFlag && (myTile.hasSideType(INPUT_ALL) || myTile.hasSideType(OMNI)));
		slotTankRev[0].setVisible(myTile.extractFlag && (myTile.hasSideType(OUTPUT_ALL) || myTile.hasSideType(OMNI)));
		slotTankRev[1].setVisible(myTile.extractFlag && myTile.hasSideType(OUTPUT_SECONDARY));

		progressBackgroundRev.setVisible(myTile.extractFlag);
		progressFluid.setFluid(myTile.getTankFluid());
		progressFluid.setSize(myTile.getEnergyStored(null) > 0 ? myTile.getScaledProgress(PROGRESS) : 0, 16);

		if (!myTile.hasSideType(OUTPUT_ALL) && !baseTile.hasSideType(OMNI)) {
			slotOutput[1].setSlotRender(SlotRender.FULL);
			slotTankRev[1].setSlotRender(SlotRender.FULL);
		} else {
			slotOutput[1].setSlotRender(SlotRender.BOTTOM);
			slotTankRev[1].setSlotRender(SlotRender.BOTTOM);
		}
		if (myTile.extractFlag) {
			progressFluid.setPosition(112, 19);
		} else {
			progressFluid.setPosition(112 + PROGRESS - myTile.getScaledProgress(PROGRESS), 19);
		}
		progressOverlay.setVisible(!myTile.extractFlag);
		progressOverlay.setQuantity(myTile.getEnergyStored(null) > 0 ? myTile.getScaledProgress(PROGRESS) : 0);
		progressOverlayRev.setVisible(myTile.extractFlag);
		progressOverlayRev.setQuantity(myTile.getEnergyStored(null) > 0 ? myTile.getScaledProgress(PROGRESS) : 0);
		speed.setQuantity(myTile.getEnergyStored(null) > 0 ? myTile.getScaledSpeed(SPEED) : 0);

		if (myTile.isActive) {
			modeSel.setToolTip("gui.thermalexpansion.machine.transposer.modeLocked");
			modeSel.setDisabled();
		} else if (myTile.extractFlag) {
			modeSel.setToolTip("gui.thermalexpansion.machine.transposer.modeEmpty");
			modeSel.setSheetX(192);
			modeSel.setHoverX(192);
			modeSel.setActive();
		} else {
			modeSel.setToolTip("gui.thermalexpansion.machine.transposer.modeFill");
			modeSel.setSheetX(176);
			modeSel.setHoverX(176);
			modeSel.setActive();
		}
	}

	@Override
	public void handleElementButtonClick(String buttonName, int mouseButton) {

		if (buttonName.equalsIgnoreCase("Mode")) {
			if (myTile.extractFlag) {
				playClickSound(0.8F);
			} else {
				playClickSound(0.6F);
			}
			myTile.setMode(!myTile.extractFlag);
		}
	}

}
