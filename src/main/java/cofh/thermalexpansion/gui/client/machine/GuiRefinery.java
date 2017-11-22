package cofh.thermalexpansion.gui.client.machine;

import cofh.core.gui.element.*;
import cofh.thermalexpansion.block.machine.TileRefinery;
import cofh.thermalexpansion.gui.client.GuiPoweredBase;
import cofh.thermalexpansion.gui.container.machine.ContainerRefinery;
import cofh.thermalexpansion.gui.element.ElementSlotOverlay;
import cofh.thermalexpansion.gui.element.ElementSlotOverlay.SlotColor;
import cofh.thermalexpansion.gui.element.ElementSlotOverlay.SlotRender;
import cofh.thermalexpansion.gui.element.ElementSlotOverlay.SlotType;
import cofh.thermalexpansion.init.TEProps;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

public class GuiRefinery extends GuiPoweredBase {

	public static final ResourceLocation TEXTURE = new ResourceLocation(TEProps.PATH_GUI_MACHINE + "refinery.png");

	private TileRefinery myTile;

	private ElementBase slotInput;
	private ElementSlotOverlay[] slotOutput = new ElementSlotOverlay[2];
	private ElementSlotOverlay[] slotTankOutput = new ElementSlotOverlay[2];

	private ElementFluid progressFluid;
	private ElementDualScaled progressOverlay;
	private ElementDualScaled speed;

	public GuiRefinery(InventoryPlayer inventory, TileEntity tile) {

		super(new ContainerRefinery(inventory, tile), tile, inventory.player, TEXTURE);

		generateInfo("tab.thermalexpansion.machine.refinery");

		myTile = (TileRefinery) tile;
	}

	@Override
	public void initGui() {

		super.initGui();

		slotInput = addElement(new ElementSlotOverlay(this, 44, 19).setSlotInfo(SlotColor.BLUE, SlotType.TANK_SHORT, SlotRender.FULL));
		slotOutput[0] = (ElementSlotOverlay) addElement(new ElementSlotOverlay(this, 112, 31).setSlotInfo(SlotColor.ORANGE, SlotType.OUTPUT, SlotRender.FULL));
		slotOutput[1] = (ElementSlotOverlay) addElement(new ElementSlotOverlay(this, 112, 31).setSlotInfo(SlotColor.YELLOW, SlotType.OUTPUT, SlotRender.BOTTOM));
		slotTankOutput[0] = (ElementSlotOverlay) addElement(new ElementSlotOverlay(this, 152, 9).setSlotInfo(SlotColor.ORANGE, SlotType.TANK, SlotRender.FULL).setVisible(false));
		slotTankOutput[1] = (ElementSlotOverlay) addElement(new ElementSlotOverlay(this, 152, 9).setSlotInfo(SlotColor.RED, SlotType.TANK, SlotRender.BOTTOM).setVisible(false));

		if (!myTile.smallStorage()) {
			addElement(new ElementEnergyStored(this, 8, 8, myTile.getEnergyStorage()));
		}
		addElement(new ElementFluidTank(this, 44, 19, myTile.getTank(0)).setGauge(0).setAlwaysShow(true).setSmall());
		addElement(new ElementFluidTank(this, 152, 9, myTile.getTank(1)).setGauge(1).setAlwaysShow(true));

		progressFluid = (ElementFluid) addElement(new ElementFluid(this, 76, 34).setFluid(myTile.getTankFluid(0)).setSize(24, 16));
		progressOverlay = (ElementDualScaled) addElement(new ElementDualScaled(this, 76, 34).setMode(1).setBackground(false).setSize(24, 16).setTexture(TEX_DROP_RIGHT, 64, 16));
		speed = (ElementDualScaled) addElement(new ElementDualScaled(this, 44, 52).setSize(16, 16).setTexture(TEX_FLAME, 32, 16));
	}

	@Override
	protected void updateElementInformation() {

		super.updateElementInformation();

		slotInput.setVisible(baseTile.hasSideType(INPUT_ALL) || baseTile.hasSideType(OMNI));
		slotOutput[0].setVisible(baseTile.hasSideType(OUTPUT_ALL) || baseTile.hasSideType(OMNI));
		slotOutput[1].setVisible(baseTile.hasSideType(OUTPUT_SECONDARY));

		slotTankOutput[0].setVisible(baseTile.hasSideType(OUTPUT_ALL) || baseTile.hasSideType(OMNI));
		slotTankOutput[1].setVisible(baseTile.hasSideType(OUTPUT_PRIMARY));

		if (!baseTile.hasSideType(OUTPUT_ALL) && !baseTile.hasSideType(OMNI)) {
			slotOutput[1].setSlotRender(SlotRender.FULL);
			slotTankOutput[1].setSlotRender(SlotRender.FULL);
		} else {
			slotOutput[1].setSlotRender(SlotRender.BOTTOM);
			slotTankOutput[1].setSlotRender(SlotRender.BOTTOM);
		}
		progressFluid.setFluid(myTile.getTankFluid(0));
		progressFluid.setSize(baseTile.getScaledProgress(PROGRESS), 16);
		progressOverlay.setQuantity(baseTile.getScaledProgress(PROGRESS));
		speed.setQuantity(baseTile.getScaledSpeed(SPEED));

	}

}
