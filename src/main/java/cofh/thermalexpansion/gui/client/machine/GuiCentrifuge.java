package cofh.thermalexpansion.gui.client.machine;

import cofh.lib.gui.element.*;
import cofh.thermalexpansion.block.machine.TileCentrifuge;
import cofh.thermalexpansion.gui.client.GuiPoweredBase;
import cofh.thermalexpansion.gui.container.machine.ContainerCrucible;
import cofh.thermalexpansion.gui.element.ElementSlotOverlay;
import cofh.thermalexpansion.gui.element.ElementSlotOverlay.SlotColor;
import cofh.thermalexpansion.gui.element.ElementSlotOverlay.SlotRender;
import cofh.thermalexpansion.gui.element.ElementSlotOverlay.SlotType;
import cofh.thermalexpansion.init.TEProps;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

public class GuiCentrifuge extends GuiPoweredBase {

	public static final ResourceLocation TEXTURE = new ResourceLocation(TEProps.PATH_GUI_MACHINE + "centrifuge.png");

	private TileCentrifuge myTile;

	private ElementBase slotInput;
	private ElementSlotOverlay[] slotOutput = new ElementSlotOverlay[2];
	private ElementSlotOverlay[] slotTank = new ElementSlotOverlay[2];

	private ElementDualScaled progress;
	private ElementDualScaled speed;
	private ElementFluid progressFluid;
	private ElementDualScaled progressOverlay;

	public GuiCentrifuge(InventoryPlayer inventory, TileEntity tile) {

		super(new ContainerCrucible(inventory, tile), tile, inventory.player, TEXTURE);

		generateInfo("tab.thermalexpansion.machine.centrifuge");

		myTile = (TileCentrifuge) tile;
	}

	@Override
	public void initGui() {

		super.initGui();

		slotInput = addElement(new ElementSlotOverlay(this, 44, 26).setSlotInfo(SlotColor.BLUE, SlotType.STANDARD, SlotRender.FULL));

		// TODO: Finish
		//		slotOutput[0] = (ElementSlotOverlay) addElement(new ElementSlotOverlay(this, 112, 22).setSlotInfo(SlotColor.ORANGE, SlotType.OUTPUT, SlotRender.FULL));
		//		slotOutput[1] = (ElementSlotOverlay) addElement(new ElementSlotOverlay(this, 112, 22).setSlotInfo(SlotColor.RED, SlotType.OUTPUT, SlotRender.BOTTOM));

		slotTank[0] = (ElementSlotOverlay) addElement(new ElementSlotOverlay(this, 152, 9).setSlotInfo(SlotColor.ORANGE, SlotType.TANK, SlotRender.FULL));
		slotTank[1] = (ElementSlotOverlay) addElement(new ElementSlotOverlay(this, 152, 9).setSlotInfo(SlotColor.YELLOW, SlotType.TANK, SlotRender.BOTTOM));

		addElement(new ElementEnergyStored(this, 8, 8, baseTile.getEnergyStorage()));
		addElement(new ElementFluidTank(this, 152, 9, baseTile.getTank()).setGauge(1).setAlwaysShow(true));

		progress = (ElementDualScaled) addElement(new ElementDualScaled(this, 71, 34).setMode(1).setSize(24, 16).setTexture(TEX_ARROW_RIGHT, 64, 16));
		speed = (ElementDualScaled) addElement(new ElementDualScaled(this, 44, 44).setSize(16, 16).setTexture(TEX_FLAME, 32, 16));

		progressFluid = (ElementFluid) addElement(new ElementFluid(this, 103, 34).setFluid(baseTile.getTankFluid()).setSize(24, 16));
		progressOverlay = (ElementDualScaled) addElement(new ElementDualScaled(this, 103, 34).setMode(1).setBackground(false).setSize(24, 16).setTexture(TEX_DROP_RIGHT, 64, 16));
	}

	@Override
	protected void updateElementInformation() {

		super.updateElementInformation();

		slotInput.setVisible(baseTile.hasSideType(INPUT_ALL) || baseTile.hasSideType(OMNI));

		slotOutput[0].setVisible(baseTile.hasSideType(OUTPUT_ALL) || baseTile.hasSideType(OMNI));
		slotOutput[1].setVisible(baseTile.hasSideType(OUTPUT_PRIMARY));

		slotTank[0].setVisible(baseTile.hasSideType(OUTPUT_ALL) || baseTile.hasSideType(OMNI));
		slotTank[1].setVisible(baseTile.hasSideType(OUTPUT_SECONDARY));

		if (!baseTile.hasSideType(OUTPUT_ALL) && !baseTile.hasSideType(OMNI)) {
			slotOutput[1].setSlotRender(SlotRender.FULL);
			slotTank[1].setSlotRender(SlotRender.FULL);
		} else {
			slotOutput[1].setSlotRender(SlotRender.BOTTOM);
			slotTank[1].setSlotRender(SlotRender.BOTTOM);
		}
		progress.setQuantity(baseTile.getScaledProgress(PROGRESS));
		speed.setQuantity(baseTile.getScaledSpeed(SPEED));

		progressFluid.setVisible(myTile.fluidArrow());
		progressOverlay.setVisible(myTile.fluidArrow());
	}

}
