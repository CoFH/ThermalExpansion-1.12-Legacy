package cofh.thermalexpansion.gui.client.machine;

import cofh.core.gui.element.*;
import cofh.thermalexpansion.block.machine.TileBrewer;
import cofh.thermalexpansion.gui.client.GuiPoweredBase;
import cofh.thermalexpansion.gui.container.machine.ContainerBrewer;
import cofh.thermalexpansion.gui.element.ElementSlotOverlay;
import cofh.thermalexpansion.gui.element.ElementSlotOverlay.SlotColor;
import cofh.thermalexpansion.gui.element.ElementSlotOverlay.SlotRender;
import cofh.thermalexpansion.gui.element.ElementSlotOverlay.SlotType;
import cofh.thermalexpansion.init.TEProps;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

public class GuiBrewer extends GuiPoweredBase {

	public static final String TEX_PATH = TEProps.PATH_GUI_MACHINE + "brewer.png";
	public static final ResourceLocation TEXTURE = new ResourceLocation(TEX_PATH);

	private TileBrewer myTile;

	private ElementSlotOverlay[] slotInput = new ElementSlotOverlay[2];
	private ElementSlotOverlay[] slotTankInput = new ElementSlotOverlay[2];
	private ElementBase slotTankOutput;

	private ElementFluid progressFluid;
	private ElementDualScaled progressOverlay;
	private ElementDualScaled speed;

	public GuiBrewer(InventoryPlayer inventory, TileEntity tile) {

		super(new ContainerBrewer(inventory, tile), tile, inventory.player, TEXTURE);

		generateInfo("tab.thermalexpansion.machine.brewer");

		myTile = (TileBrewer) tile;
	}

	@Override
	public void initGui() {

		super.initGui();

		slotInput[0] = (ElementSlotOverlay) addElement(new ElementSlotOverlay(this, 80, 34).setSlotInfo(SlotColor.BLUE, SlotType.STANDARD, SlotRender.FULL));
		slotInput[1] = (ElementSlotOverlay) addElement(new ElementSlotOverlay(this, 80, 34).setSlotInfo(SlotColor.GREEN, SlotType.STANDARD, SlotRender.BOTTOM));
		slotTankInput[0] = (ElementSlotOverlay) addElement(new ElementSlotOverlay(this, 32, 27).setSlotInfo(SlotColor.BLUE, SlotType.TANK_SHORT, SlotRender.FULL));
		slotTankInput[1] = (ElementSlotOverlay) addElement(new ElementSlotOverlay(this, 32, 27).setSlotInfo(SlotColor.PURPLE, SlotType.TANK_SHORT, SlotRender.BOTTOM));
		slotTankOutput = addElement(new ElementSlotOverlay(this, 152, 9).setSlotInfo(SlotColor.ORANGE, SlotType.TANK, SlotRender.FULL));

		if (!myTile.smallStorage()) {
			addElement(new ElementEnergyStored(this, 8, 8, myTile.getEnergyStorage()));
		}
		addElement(new ElementFluidTank(this, 32, 27, myTile.getTank(0)).setGauge(0).setAlwaysShow(true).setShort());
		addElement(new ElementFluidTank(this, 152, 9, myTile.getTank(1)).setGauge(1).setAlwaysShow(true));

		progressFluid = (ElementFluid) addElement(new ElementFluid(this, 112, 34).setFluid(myTile.getTankFluid(1)).setSize(24, 16));
		progressOverlay = (ElementDualScaled) addElement(new ElementDualScaled(this, 112, 34).setMode(1).setBackground(false).setSize(24, 16).setTexture(TEX_DROP_RIGHT, 64, 16));
		speed = (ElementDualScaled) addElement(new ElementDualScaled(this, 57, 34).setSize(16, 16).setTexture(TEX_ALCHEMY, 32, 16));
	}

	@Override
	protected void updateElementInformation() {

		super.updateElementInformation();

		slotInput[0].setVisible(myTile.hasSideType(INPUT_ALL) || baseTile.hasSideType(OMNI));
		slotInput[1].setVisible(myTile.hasSideType(INPUT_PRIMARY));
		slotTankInput[0].setVisible(myTile.hasSideType(INPUT_ALL) || baseTile.hasSideType(OMNI));
		slotTankInput[1].setVisible(myTile.hasSideType(INPUT_SECONDARY));

		slotTankOutput.setVisible(baseTile.hasSideType(OUTPUT_ALL) || baseTile.hasSideType(OMNI));

		if (!baseTile.hasSideType(INPUT_ALL) && !baseTile.hasSideType(OMNI)) {
			slotInput[1].setSlotRender(SlotRender.FULL);
			slotTankInput[1].setSlotRender(SlotRender.FULL);
		} else {
			slotInput[1].setSlotRender(SlotRender.BOTTOM);
			slotTankInput[1].setSlotRender(SlotRender.BOTTOM);
		}
		progressFluid.setFluid(myTile.getTankFluid(0));
		progressFluid.setSize(baseTile.getScaledProgress(PROGRESS), 16);
		progressOverlay.setQuantity(baseTile.getScaledProgress(PROGRESS));
		speed.setQuantity(baseTile.getScaledSpeed(SPEED));
	}

}
