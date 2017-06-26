package cofh.thermalexpansion.gui.client.machine;

import cofh.lib.gui.element.ElementBase;
import cofh.lib.gui.element.ElementDualScaled;
import cofh.lib.gui.element.ElementEnergyStored;
import cofh.lib.gui.element.ElementFluidTank;
import cofh.thermalexpansion.gui.client.GuiPoweredBase;
import cofh.thermalexpansion.gui.container.machine.ContainerCentrifuge;
import cofh.thermalexpansion.gui.element.ElementSlotOverlay;
import cofh.thermalexpansion.gui.element.ElementSlotOverlay.SlotColor;
import cofh.thermalexpansion.gui.element.ElementSlotOverlay.SlotRender;
import cofh.thermalexpansion.gui.element.ElementSlotOverlay.SlotType;
import cofh.thermalexpansion.gui.element.ElementSlotOverlayCentrifuge;
import cofh.thermalexpansion.init.TEProps;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

public class GuiCentrifuge extends GuiPoweredBase {

	public static final ResourceLocation TEXTURE = new ResourceLocation(TEProps.PATH_GUI_MACHINE + "centrifuge.png");

	private ElementBase slotInput;
	private ElementSlotOverlayCentrifuge[] slotOutput = new ElementSlotOverlayCentrifuge[2];
	private ElementSlotOverlay[] slotTank = new ElementSlotOverlay[2];

	private ElementDualScaled progress;
	private ElementDualScaled speed;

	public GuiCentrifuge(InventoryPlayer inventory, TileEntity tile) {

		super(new ContainerCentrifuge(inventory, tile), tile, inventory.player, TEXTURE);

		generateInfo("tab.thermalexpansion.machine.centrifuge");
	}

	@Override
	public void initGui() {

		super.initGui();

		slotInput = addElement(new ElementSlotOverlay(this, 44, 26).setSlotInfo(SlotColor.BLUE, SlotType.STANDARD, SlotRender.FULL));

		slotOutput[0] = (ElementSlotOverlayCentrifuge) addElement(new ElementSlotOverlayCentrifuge(this, 106, 26).setSlotInfo(SlotColor.ORANGE, SlotRender.FULL));
		slotOutput[1] = (ElementSlotOverlayCentrifuge) addElement(new ElementSlotOverlayCentrifuge(this, 106, 26).setSlotInfo(SlotColor.RED, SlotRender.BOTTOM));

		slotTank[0] = (ElementSlotOverlay) addElement(new ElementSlotOverlay(this, 152, 9).setSlotInfo(SlotColor.ORANGE, SlotType.TANK, SlotRender.FULL));
		slotTank[1] = (ElementSlotOverlay) addElement(new ElementSlotOverlay(this, 152, 9).setSlotInfo(SlotColor.YELLOW, SlotType.TANK, SlotRender.BOTTOM));

		addElement(new ElementEnergyStored(this, 8, 8, baseTile.getEnergyStorage()));
		addElement(new ElementFluidTank(this, 152, 9, baseTile.getTank()).setGauge(0).setAlwaysShow(true));

		progress = (ElementDualScaled) addElement(new ElementDualScaled(this, 71, 34).setMode(1).setSize(24, 16).setTexture(TEX_ARROW_RIGHT, 64, 16));
		speed = (ElementDualScaled) addElement(new ElementDualScaled(this, 44, 44).setSize(16, 16).setTexture(TEX_SPIN, 32, 16));
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
	}

}
