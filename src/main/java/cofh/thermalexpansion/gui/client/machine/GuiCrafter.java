package cofh.thermalexpansion.gui.client.machine;

import cofh.core.gui.element.ElementBase;
import cofh.core.gui.element.ElementEnergyStored;
import cofh.core.gui.element.ElementFluidTank;
import cofh.thermalexpansion.gui.client.GuiPoweredBase;
import cofh.thermalexpansion.gui.container.machine.ContainerCrafter;
import cofh.thermalexpansion.gui.element.ElementSlotOverlay;
import cofh.thermalexpansion.gui.element.ElementSlotOverlay.SlotColor;
import cofh.thermalexpansion.gui.element.ElementSlotOverlay.SlotRender;
import cofh.thermalexpansion.gui.element.ElementSlotOverlay.SlotType;
import cofh.thermalexpansion.gui.element.ElementSlotOverlayCrafter;
import cofh.thermalexpansion.init.TEProps;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

public class GuiCrafter extends GuiPoweredBase {

	static final ResourceLocation TEXTURE = new ResourceLocation(TEProps.PATH_GUI_MACHINE + "crafter.png");

	ElementSlotOverlayCrafter[] slotInput = new ElementSlotOverlayCrafter[3];
	ElementBase slotOutput;
	ElementBase slotInputFluid;

	public GuiCrafter(InventoryPlayer inventory, TileEntity tile) {

		super(new ContainerCrafter(inventory, tile), tile, inventory.player, TEXTURE);

		generateInfo("tab.thermalexpansion.machine.crafter");

		ySize = 205;
	}

	@Override
	public void initGui() {

		super.initGui();

		slotInput[0] = (ElementSlotOverlayCrafter) addElement(new ElementSlotOverlayCrafter(this, 8, 74).setSlotInfo(0, 4));
		slotInput[1] = (ElementSlotOverlayCrafter) addElement(new ElementSlotOverlayCrafter(this, 8, 74).setSlotInfo(4, 0));
		slotInput[2] = (ElementSlotOverlayCrafter) addElement(new ElementSlotOverlayCrafter(this, 8, 74).setSlotInfo(5, 1));

		slotInputFluid = addElement(new ElementSlotOverlay(this, 152, 9).setSlotInfo(SlotColor.BLUE, SlotType.TANK, SlotRender.FULL));
		slotOutput = addElement(new ElementSlotOverlay(this, 112, 31).setSlotInfo(SlotColor.ORANGE, SlotType.OUTPUT, SlotRender.FULL));

		addElement(new ElementEnergyStored(this, 8, 8, baseTile.getEnergyStorage()));
		addElement(new ElementFluidTank(this, 152, 9, baseTile.getTank()).setGauge(1).setAlwaysShow(true));
	}

	@Override
	protected void updateElementInformation() {

		super.updateElementInformation();

		slotInput[0].setVisible(baseTile.hasSideType(INPUT_ALL) || baseTile.hasSideType(OMNI));
		slotInput[1].setVisible(baseTile.hasSideType(INPUT_PRIMARY));
		slotInput[2].setVisible(baseTile.hasSideType(INPUT_SECONDARY));
		slotInputFluid.setVisible(baseTile.hasSideType(INPUT_ALL) || baseTile.hasSideType(INPUT_PRIMARY) || baseTile.hasSideType(INPUT_SECONDARY));

		slotOutput.setVisible(baseTile.hasSideType(OUTPUT_ALL) || baseTile.hasSideType(OMNI));

		if (!baseTile.hasSideType(INPUT_ALL) && !baseTile.hasSideType(OMNI)) {
			slotInput[1].slotRender = 0;
			slotInput[2].slotRender = 1;
		} else {
			slotInput[1].slotRender = 2;
			slotInput[2].slotRender = 3;
		}
	}

}
