package cofh.thermalexpansion.gui.client.machine;

import cofh.core.gui.element.ElementBase;
import cofh.core.gui.element.ElementDualScaled;
import cofh.core.gui.element.ElementEnergyStored;
import cofh.core.gui.element.ElementFluidTank;
import cofh.thermalexpansion.block.machine.TileCrafter;
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

	public static final ResourceLocation TEXTURE = new ResourceLocation(TEProps.PATH_GUI_MACHINE + "crafter.png");

	private TileCrafter myTile;

	ElementSlotOverlayCrafter slotInput;
	ElementBase slotOutput;
	ElementBase slotTank;

	private ElementDualScaled progress;
	private ElementDualScaled speed;

	public GuiCrafter(InventoryPlayer inventory, TileEntity tile) {

		super(new ContainerCrafter(inventory, tile), tile, inventory.player, TEXTURE);

		generateInfo("tab.thermalexpansion.machine.crafter");

		myTile = (TileCrafter) tile;
		ySize = 208;
	}

	@Override
	public void initGui() {

		super.initGui();

		slotInput = (ElementSlotOverlayCrafter) addElement(new ElementSlotOverlayCrafter(this, 8, 77));
		slotOutput = addElement(new ElementSlotOverlay(this, 121, 31).setSlotInfo(SlotColor.ORANGE, SlotType.OUTPUT, SlotRender.FULL));

		//slotTank = addElement(new ElementSlotOverlay(this, 152, 9).setSlotInfo(SlotColor.BLUE, SlotType.TANK, SlotRender.FULL));

		if (!myTile.smallStorage()) {
			addElement(new ElementEnergyStored(this, 8, 8, myTile.getEnergyStorage()));
		}
		addElement(new ElementFluidTank(this, 152, 9, baseTile.getTank()).setGauge(1).setAlwaysShow(true));
		progress = (ElementDualScaled) addElement(new ElementDualScaled(this, 92, 35).setMode(1).setSize(24, 16).setTexture(TEX_ARROW_RIGHT, 64, 16));
	}

	@Override
	protected void updateElementInformation() {

		super.updateElementInformation();

		slotInput.setVisible(baseTile.hasSideType(INPUT_ALL) || baseTile.hasSideType(OMNI));
		slotOutput.setVisible(baseTile.hasSideType(OUTPUT_ALL) || baseTile.hasSideType(OMNI));

		//slotTank.setVisible(baseTile.hasSideType(INPUT_ALL) || baseTile.hasSideType(OMNI));

		progress.setQuantity(baseTile.getScaledProgress(PROGRESS));
	}

}
