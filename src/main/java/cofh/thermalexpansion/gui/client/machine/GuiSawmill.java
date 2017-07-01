package cofh.thermalexpansion.gui.client.machine;

import cofh.core.gui.element.*;
import cofh.thermalexpansion.block.machine.TileSawmill;
import cofh.thermalexpansion.gui.client.GuiPoweredBase;
import cofh.thermalexpansion.gui.container.machine.ContainerSawmill;
import cofh.thermalexpansion.gui.element.ElementSlotOverlay;
import cofh.thermalexpansion.gui.element.ElementSlotOverlay.SlotColor;
import cofh.thermalexpansion.gui.element.ElementSlotOverlay.SlotRender;
import cofh.thermalexpansion.gui.element.ElementSlotOverlay.SlotType;
import cofh.thermalexpansion.init.TEProps;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

public class GuiSawmill extends GuiPoweredBase {

	public static final String TEX_PATH = TEProps.PATH_GUI_MACHINE + "sawmill.png";
	public static final ResourceLocation TEXTURE = new ResourceLocation(TEX_PATH);

	private TileSawmill myTile;

	private ElementBase slotInput;
	private ElementSlotOverlay[] slotPrimaryOutput = new ElementSlotOverlay[2];
	private ElementSlotOverlay[] slotSecondaryOutput = new ElementSlotOverlay[2];
	private ElementSlotOverlay[] slotTank = new ElementSlotOverlay[2];

	private ElementSimple tankBackground;
	private ElementFluidTank tank;

	private ElementDualScaled progress;
	private ElementDualScaled speed;
	private ElementFluid progressFluid;
	private ElementDualScaled progressOverlay;

	public GuiSawmill(InventoryPlayer inventory, TileEntity tile) {

		super(new ContainerSawmill(inventory, tile), tile, inventory.player, TEXTURE);

		generateInfo("tab.thermalexpansion.machine.sawmill");

		myTile = (TileSawmill) tile;
	}

	@Override
	public void initGui() {

		super.initGui();

		tankBackground = (ElementSimple) addElement(new ElementSimple(this, 151, 8).setTextureOffsets(176, 104).setSize(18, 62).setTexture(TEX_PATH, 256, 256));

		slotInput = addElement(new ElementSlotOverlay(this, 53, 26).setSlotInfo(SlotColor.BLUE, SlotType.STANDARD, SlotRender.FULL));
		slotPrimaryOutput[0] = (ElementSlotOverlay) addElement(new ElementSlotOverlay(this, 112, 22).setSlotInfo(SlotColor.ORANGE, SlotType.OUTPUT, SlotRender.FULL));
		slotPrimaryOutput[1] = (ElementSlotOverlay) addElement(new ElementSlotOverlay(this, 112, 22).setSlotInfo(SlotColor.RED, SlotType.OUTPUT, SlotRender.BOTTOM));
		slotSecondaryOutput[0] = (ElementSlotOverlay) addElement(new ElementSlotOverlay(this, 116, 53).setSlotInfo(SlotColor.ORANGE, SlotType.STANDARD, SlotRender.FULL));
		slotSecondaryOutput[1] = (ElementSlotOverlay) addElement(new ElementSlotOverlay(this, 116, 53).setSlotInfo(SlotColor.YELLOW, SlotType.STANDARD, SlotRender.BOTTOM));

		slotTank[0] = (ElementSlotOverlay) addElement(new ElementSlotOverlay(this, 152, 9).setSlotInfo(SlotColor.ORANGE, SlotType.TANK, SlotRender.FULL).setVisible(false));
		slotTank[1] = (ElementSlotOverlay) addElement(new ElementSlotOverlay(this, 152, 9).setSlotInfo(SlotColor.YELLOW, SlotType.TANK, SlotRender.BOTTOM).setVisible(false));

		addElement(new ElementEnergyStored(this, 8, 8, baseTile.getEnergyStorage()));

		progress = (ElementDualScaled) addElement(new ElementDualScaled(this, 79, 34).setMode(1).setSize(24, 16).setTexture(TEX_ARROW_RIGHT, 64, 16));
		speed = (ElementDualScaled) addElement(new ElementDualScaled(this, 53, 44).setSize(16, 16).setTexture(TEX_SAW, 32, 16));

		tank = (ElementFluidTank) addElement(new ElementFluidTank(this, 152, 9, myTile.getTank()).setGauge(0).setAlwaysShow(true));
		progressFluid = (ElementFluid) addElement(new ElementFluid(this, 79, 34).setFluid(myTile.getRenderFluid()).setSize(24, 16));
		progressOverlay = (ElementDualScaled) addElement(new ElementDualScaled(this, 79, 34).setBackground(false).setMode(1).setSize(24, 16).setTexture(TEX_ARROW_FLUID_RIGHT, 64, 16));

		slotTank[0].setVisible(myTile.augmentTapper());
		slotTank[1].setVisible(myTile.augmentTapper());

		tankBackground.setVisible(myTile.augmentTapper());
		tank.setVisible(myTile.augmentTapper());
		progressFluid.setVisible(myTile.fluidArrow());
		progressOverlay.setVisible(myTile.fluidArrow());

	}

	@Override
	protected void updateElementInformation() {

		super.updateElementInformation();

		slotInput.setVisible(baseTile.hasSideType(INPUT_ALL) || baseTile.hasSideType(OMNI));

		slotPrimaryOutput[0].setVisible(baseTile.hasSideType(OUTPUT_ALL) || baseTile.hasSideType(OMNI));
		slotPrimaryOutput[1].setVisible(baseTile.hasSideType(OUTPUT_PRIMARY));
		slotSecondaryOutput[0].setVisible(baseTile.hasSideType(OUTPUT_ALL) || baseTile.hasSideType(OMNI));
		slotSecondaryOutput[1].setVisible(baseTile.hasSideType(OUTPUT_SECONDARY));

		slotTank[0].setVisible(myTile.augmentTapper() && (baseTile.hasSideType(OUTPUT_ALL) || baseTile.hasSideType(OMNI)));
		slotTank[1].setVisible(myTile.augmentTapper() && baseTile.hasSideType(OUTPUT_SECONDARY));

		if (!baseTile.hasSideType(OUTPUT_ALL) && !baseTile.hasSideType(OMNI)) {
			slotPrimaryOutput[1].setSlotRender(SlotRender.FULL);
			slotSecondaryOutput[1].setSlotRender(SlotRender.FULL);
			slotTank[1].setSlotRender(SlotRender.FULL);
		} else {
			slotPrimaryOutput[1].setSlotRender(SlotRender.BOTTOM);
			slotSecondaryOutput[1].setSlotRender(SlotRender.BOTTOM);
			slotTank[1].setSlotRender(SlotRender.BOTTOM);
		}
		progress.setQuantity(baseTile.getScaledProgress(PROGRESS));
		speed.setQuantity(baseTile.getScaledSpeed(SPEED));

		progressFluid.setSize(baseTile.getScaledProgress(PROGRESS), 16);
		progressOverlay.setQuantity(baseTile.getScaledProgress(PROGRESS));

		progress.setVisible(!myTile.fluidArrow());

		tankBackground.setVisible(myTile.augmentTapper());
		tank.setVisible(myTile.augmentTapper());
		progressFluid.setVisible(myTile.fluidArrow());
		progressOverlay.setVisible(myTile.fluidArrow());
	}

}
