package thermalexpansion.gui.client.machine;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import thermalexpansion.block.machine.TileSmelter;
import thermalexpansion.core.TEProps;
import thermalexpansion.gui.container.machine.ContainerSmelter;
import thermalexpansion.gui.element.ElementSlotOverlay;
import cofh.core.CoFHProps;
import cofh.gui.GuiBaseAdv;
import cofh.gui.element.ElementDualScaled;
import cofh.gui.element.ElementEnergyStored;
import cofh.gui.element.TabConfiguration;
import cofh.gui.element.TabEnergy;
import cofh.gui.element.TabInfo;
import cofh.gui.element.TabRedstone;
import cofh.gui.element.TabTutorial;

public class GuiSmelter extends GuiBaseAdv {

	public static final ResourceLocation TEXTURE = new ResourceLocation(TEProps.PATH_GUI_MACHINE + "Smelter.png");
	static final String INFO = "Smelts metals and things that require high temperatures.\n\nUseful for processing ores and building advanced devices.\n\nWill absolutely not cook food.";

	TileSmelter myTile;

	ElementSlotOverlay[] slotPrimaryInput = new ElementSlotOverlay[2];
	ElementSlotOverlay[] slotSecondaryInput = new ElementSlotOverlay[2];

	ElementSlotOverlay[] slotPrimaryOutput = new ElementSlotOverlay[2];
	ElementSlotOverlay[] slotSecondaryOutput = new ElementSlotOverlay[2];
	ElementDualScaled progress;
	ElementDualScaled speed;

	public GuiSmelter(InventoryPlayer inventory, TileEntity theTile) {

		super(new ContainerSmelter(inventory, theTile), TEXTURE);
		myTile = (TileSmelter) theTile;
		name = myTile.getInventoryName();
	}

	@Override
	public void initGui() {

		super.initGui();

		slotPrimaryInput[0] = (ElementSlotOverlay) addElement(new ElementSlotOverlay(this, 32, 26).setSlotInfo(0, 0, 2));
		slotPrimaryInput[1] = (ElementSlotOverlay) addElement(new ElementSlotOverlay(this, 32, 26).setSlotInfo(4, 0, 1));
		slotSecondaryInput[0] = (ElementSlotOverlay) addElement(new ElementSlotOverlay(this, 56, 26).setSlotInfo(0, 0, 2));
		slotSecondaryInput[1] = (ElementSlotOverlay) addElement(new ElementSlotOverlay(this, 56, 26).setSlotInfo(5, 0, 1));
		slotPrimaryOutput[0] = (ElementSlotOverlay) addElement(new ElementSlotOverlay(this, 112, 22).setSlotInfo(3, 2, 2));
		slotPrimaryOutput[1] = (ElementSlotOverlay) addElement(new ElementSlotOverlay(this, 112, 22).setSlotInfo(1, 2, 1));
		slotSecondaryOutput[0] = (ElementSlotOverlay) addElement(new ElementSlotOverlay(this, 116, 53).setSlotInfo(3, 0, 2));
		slotSecondaryOutput[1] = (ElementSlotOverlay) addElement(new ElementSlotOverlay(this, 116, 53).setSlotInfo(2, 0, 1));

		addElement(new ElementEnergyStored(this, 8, 8, myTile.getEnergyStorage()));
		progress = (ElementDualScaled) addElement(new ElementDualScaled(this, 79, 34).setMode(1).setSize(24, 16).setTexture(TEX_ARROW_RIGHT, 48, 16));
		speed = (ElementDualScaled) addElement(new ElementDualScaled(this, 44, 44).setSize(16, 16).setTexture(TEX_FLAME, 32, 16));

		addTab(new TabEnergy(this, myTile, false));
		addTab(new TabRedstone(this, myTile));
		addTab(new TabConfiguration(this, myTile));
		addTab(new TabInfo(this, INFO));
		addTab(new TabTutorial(this, CoFHProps.tutorialTabRedstone + "\n\n" + CoFHProps.tutorialTabConfiguration + "\n\n" + CoFHProps.tutorialTabFluxRequired));
	}

	@Override
	protected void updateElements() {

		slotPrimaryInput[0].setVisible(myTile.hasSide(1));
		slotPrimaryInput[1].setVisible(myTile.hasSide(5));
		slotSecondaryInput[0].setVisible(myTile.hasSide(1));
		slotSecondaryInput[1].setVisible(myTile.hasSide(6));

		slotPrimaryOutput[0].setVisible(myTile.hasSide(4));
		slotPrimaryOutput[1].setVisible(myTile.hasSide(2));
		slotSecondaryOutput[0].setVisible(myTile.hasSide(4));
		slotSecondaryOutput[1].setVisible(myTile.hasSide(3));

		if (!myTile.hasSide(1)) {
			slotPrimaryInput[1].slotRender = 2;
			slotSecondaryInput[1].slotRender = 2;
		} else {
			slotPrimaryInput[1].slotRender = 1;
			slotSecondaryInput[1].slotRender = 1;
		}
		if (!myTile.hasSide(4)) {
			slotPrimaryOutput[1].slotRender = 2;
			slotSecondaryOutput[1].slotRender = 2;
		} else {
			slotPrimaryOutput[1].slotRender = 1;
			slotSecondaryOutput[1].slotRender = 1;
		}

		progress.setQuantity(myTile.getScaledProgress(PROGRESS));
		speed.setQuantity(myTile.getScaledSpeed(SPEED));
	}

}
