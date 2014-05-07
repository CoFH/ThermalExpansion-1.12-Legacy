package thermalexpansion.gui.client.machine;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import thermalexpansion.block.machine.TileTransposer;
import thermalexpansion.core.TEProps;
import thermalexpansion.gui.container.machine.ContainerTransposer;
import thermalexpansion.gui.element.ElementSlotOverlay;
import cofh.core.CoFHProps;
import cofh.gui.GuiBaseAdv;
import cofh.gui.element.ElementBase;
import cofh.gui.element.ElementButton;
import cofh.gui.element.ElementDualScaled;
import cofh.gui.element.ElementEnergyStored;
import cofh.gui.element.ElementFluid;
import cofh.gui.element.ElementFluidTank;
import cofh.gui.element.ElementSimple;
import cofh.gui.element.TabConfiguration;
import cofh.gui.element.TabEnergy;
import cofh.gui.element.TabInfo;
import cofh.gui.element.TabRedstone;
import cofh.gui.element.TabTutorial;

public class GuiTransposer extends GuiBaseAdv {

	static final String TEX_PATH = TEProps.PATH_GUI_MACHINE + "Transposer.png";
	public static final ResourceLocation TEXTURE = new ResourceLocation(TEX_PATH);
	static final String INFO = "Fills or empties things that hold fluids.\n\nPress the button to change between Fill and Extract.\n\nTypically reversible.";

	TileTransposer myTile;

	ElementBase slotInput;
	ElementSlotOverlay[] slotOutput = new ElementSlotOverlay[2];
	ElementBase slotTank;
	ElementSlotOverlay[] slotTankRev = new ElementSlotOverlay[2];
	ElementFluid progressFluid;
	ElementSimple progressBackgroundRev;
	ElementDualScaled progressOverlay;
	ElementDualScaled progressOverlayRev;
	ElementDualScaled speed;
	ElementButton mode;

	public GuiTransposer(InventoryPlayer inventory, TileEntity theTile) {

		super(new ContainerTransposer(inventory, theTile), TEXTURE);
		myTile = (TileTransposer) theTile;
		name = myTile.getInventoryName();
	}

	@Override
	public void initGui() {

		super.initGui();

		slotInput = addElement(new ElementSlotOverlay(this, 80, 19).setSlotInfo(0, 0, 2));
		slotOutput[0] = (ElementSlotOverlay) addElement(new ElementSlotOverlay(this, 76, 45).setSlotInfo(3, 1, 2));
		slotOutput[1] = (ElementSlotOverlay) addElement(new ElementSlotOverlay(this, 76, 45).setSlotInfo(1, 1, 1));
		slotTank = addElement(new ElementSlotOverlay(this, 152, 9).setSlotInfo(0, 3, 2));
		slotTankRev[0] = (ElementSlotOverlay) addElement(new ElementSlotOverlay(this, 152, 9).setSlotInfo(3, 3, 2).setVisible(false));
		slotTankRev[1] = (ElementSlotOverlay) addElement(new ElementSlotOverlay(this, 152, 9).setSlotInfo(2, 3, 1).setVisible(false));

		addElement(new ElementEnergyStored(this, 8, 8, myTile.getEnergyStorage()));
		addElement(new ElementFluidTank(this, 152, 9, myTile.getTank()).setGauge(1));
		progressBackgroundRev = (ElementSimple) addElement(new ElementSimple(this, 112, 19).setSize(24, 16).setTexture(TEX_DROP_RIGHT, 48, 16));
		progressFluid = (ElementFluid) addElement(new ElementFluid(this, 112, 19).setFluid(myTile.getTankFluid()).setSize(24, 16));
		progressOverlay = (ElementDualScaled) addElement(new ElementDualScaled(this, 112, 19).setMode(2).setBackground(false).setSize(24, 16)
				.setTexture(TEX_DROP_LEFT, 48, 16));
		progressOverlayRev = (ElementDualScaled) addElement(new ElementDualScaled(this, 112, 19).setMode(1).setBackground(false).setSize(24, 16)
				.setTexture(TEX_DROP_RIGHT, 48, 16));
		speed = (ElementDualScaled) addElement(new ElementDualScaled(this, 44, 49).setSize(16, 16).setTexture(TEX_BUBBLE, 32, 16));
		mode = (ElementButton) addElement(new ElementButton(this, 116, 49, "Mode", 176, 0, 176, 16, 176, 32, 16, 16, TEX_PATH));

		addTab(new TabEnergy(this, myTile, false));
		addTab(new TabRedstone(this, myTile));
		addTab(new TabConfiguration(this, myTile));
		addTab(new TabInfo(this, INFO));
		addTab(new TabTutorial(this, CoFHProps.tutorialTabRedstone + "\n\n" + CoFHProps.tutorialTabConfiguration + "\n\n" + CoFHProps.tutorialTabFluxRequired));
	}

	@Override
	protected void updateElements() {

		slotInput.setVisible(myTile.hasSide(1));
		slotOutput[0].setVisible(myTile.hasSide(4));
		slotOutput[1].setVisible(myTile.hasSide(2));
		slotTank.setVisible(!myTile.reverse && myTile.hasSide(1));
		slotTankRev[0].setVisible(myTile.reverse && myTile.hasSide(4));
		slotTankRev[1].setVisible(myTile.reverse && myTile.hasSide(3));

		progressBackgroundRev.setVisible(myTile.reverse);
		progressFluid.setFluid(myTile.getTankFluid());
		progressFluid.setSize(myTile.getScaledProgress(PROGRESS), 16);

		if (!myTile.hasSide(4)) {
			slotOutput[1].slotRender = 2;
			slotTankRev[1].slotRender = 2;
		} else {
			slotOutput[1].slotRender = 1;
			slotTankRev[1].slotRender = 1;
		}
		if (myTile.reverse) {
			progressFluid.setPosition(112, 19);
		} else {
			progressFluid.setPosition(112 + PROGRESS - myTile.getScaledProgress(PROGRESS), 19);
		}
		progressOverlay.setVisible(!myTile.reverse);
		progressOverlay.setQuantity(myTile.getScaledProgress(PROGRESS));
		progressOverlayRev.setVisible(myTile.reverse);
		progressOverlayRev.setQuantity(myTile.getScaledProgress(PROGRESS));
		speed.setQuantity(myTile.getScaledSpeed(SPEED));

		if (myTile.reverse) {
			if (!myTile.reverseFlag) {
				mode.setToolTip("info.thermalexpansion.transposer.toggleWait");
				mode.setDisabled();
			} else {
				mode.setToolTip("info.thermalexpansion.transposer.toggleFill");
				mode.setSheetX(192);
				mode.setHoverX(192);
				mode.setActive();
			}
		} else {
			if (myTile.reverseFlag) {
				mode.setToolTip("info.thermalexpansion.transposer.toggleWait");
				mode.setDisabled();
			} else {
				mode.setToolTip("info.thermalexpansion.transposer.toggleEmpty");
				mode.setSheetX(176);
				mode.setHoverX(176);
				mode.setActive();
			}
		}
	}

	@Override
	public void handleElementButtonClick(String buttonName, int mouseButton) {

		if (buttonName.equals("Mode")) {
			if (myTile.reverse == myTile.reverseFlag) {
				if (myTile.reverse) {
					playSound("random.click", 1.0F, 0.8F);
				} else {
					playSound("random.click", 1.0F, 0.6F);
				}
				myTile.setMode(!myTile.reverse);
			}
		}
	}

}
