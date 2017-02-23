package cofh.thermalexpansion.gui.client.machine;

import cofh.lib.gui.element.*;
import cofh.thermalexpansion.block.machine.TilePulverizer;
import cofh.thermalexpansion.gui.client.GuiPoweredBase;
import cofh.thermalexpansion.gui.container.machine.ContainerPulverizer;
import cofh.thermalexpansion.gui.element.ElementSlotOverlay;
import cofh.thermalexpansion.init.TEProps;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

public class GuiPulverizer extends GuiPoweredBase {

	public static final String TEX_PATH = TEProps.PATH_GUI_MACHINE + "pulverizer.png";
	public static final ResourceLocation TEXTURE = new ResourceLocation(TEX_PATH);

	private TilePulverizer myTile;

	private ElementBase slotInput;
	private ElementSlotOverlay[] slotPrimaryOutput = new ElementSlotOverlay[2];
	private ElementSlotOverlay[] slotSecondaryOutput = new ElementSlotOverlay[2];

	private ElementDualScaled progress;
	private ElementDualScaled speed;

	private ElementSimple tankBackground;
	private ElementFluidTank tank;
	private ElementFluid progressFluid;
	private ElementDualScaled progressOverlay;

	public GuiPulverizer(InventoryPlayer inventory, TileEntity tile) {

		super(new ContainerPulverizer(inventory, tile), tile, inventory.player, TEXTURE);

		generateInfo("tab.thermalexpansion.machine.pulverizer");

		myTile = (TilePulverizer) tile;
	}

	@Override
	public void initGui() {

		super.initGui();

		tankBackground = (ElementSimple) addElement(new ElementSimple(this, 151, 8).setTextureOffsets(176, 104).setSize(18, 62).setTexture(TEX_PATH, 256, 256));

		slotInput = addElement(new ElementSlotOverlay(this, 53, 26).setSlotInfo(0, 0, 2));
		slotPrimaryOutput[0] = (ElementSlotOverlay) addElement(new ElementSlotOverlay(this, 112, 22).setSlotInfo(3, 1, 2));
		slotPrimaryOutput[1] = (ElementSlotOverlay) addElement(new ElementSlotOverlay(this, 112, 22).setSlotInfo(1, 1, 1));
		slotSecondaryOutput[0] = (ElementSlotOverlay) addElement(new ElementSlotOverlay(this, 116, 53).setSlotInfo(3, 0, 2));
		slotSecondaryOutput[1] = (ElementSlotOverlay) addElement(new ElementSlotOverlay(this, 116, 53).setSlotInfo(2, 0, 1));

		addElement(new ElementEnergyStored(this, 8, 8, baseTile.getEnergyStorage()));

		progress = (ElementDualScaled) addElement(new ElementDualScaled(this, 79, 34).setMode(1).setSize(24, 16).setTexture(TEX_ARROW_RIGHT, 64, 16));
		speed = (ElementDualScaled) addElement(new ElementDualScaled(this, 53, 44).setSize(16, 16).setTexture(TEX_CRUSH, 32, 16));

		tank = (ElementFluidTank) addElement(new ElementFluidTank(this, 152, 9, myTile.getTank()).setGauge(0).setAlwaysShow(true));
		progressFluid = (ElementFluid) addElement(new ElementFluid(this, 79, 34).setFluid(myTile.getTankFluid()).setSize(24, 16));
		progressOverlay = (ElementDualScaled) addElement(new ElementDualScaled(this, 79, 34).setBackground(false).setMode(1).setSize(24, 16).setTexture(TEX_ARROW_FLUID_RIGHT, 64, 16));

		tankBackground.setVisible(myTile.augmentPetrotheum());
		tank.setVisible(myTile.augmentPetrotheum());
		progressFluid.setVisible(myTile.fluidArrow());
		progressOverlay.setVisible(myTile.fluidArrow());
	}

	@Override
	protected void updateElementInformation() {

		super.updateElementInformation();

		slotInput.setVisible(baseTile.hasSide(1));

		slotPrimaryOutput[0].setVisible(baseTile.hasSide(4));
		slotPrimaryOutput[1].setVisible(baseTile.hasSide(2));
		slotSecondaryOutput[0].setVisible(baseTile.hasSide(4));
		slotSecondaryOutput[1].setVisible(baseTile.hasSide(3));

		if (!baseTile.hasSide(4)) {
			slotPrimaryOutput[1].slotRender = 2;
			slotSecondaryOutput[1].slotRender = 2;
		} else {
			slotPrimaryOutput[1].slotRender = 1;
			slotSecondaryOutput[1].slotRender = 1;
		}
		progress.setQuantity(baseTile.getScaledProgress(PROGRESS));
		speed.setQuantity(baseTile.getScaledSpeed(SPEED));

		progressFluid.setSize(baseTile.getScaledProgress(PROGRESS), 16);
		progressOverlay.setQuantity(baseTile.getScaledProgress(PROGRESS));

		progress.setVisible(!myTile.fluidArrow());

		tankBackground.setVisible(myTile.augmentPetrotheum());
		tank.setVisible(myTile.augmentPetrotheum());
		progressFluid.setVisible(myTile.fluidArrow());
		progressOverlay.setVisible(myTile.fluidArrow());
	}

}
