package cofh.thermalexpansion.gui.client.machine;

import cofh.core.gui.element.*;
import cofh.thermalexpansion.block.machine.TileCharger;
import cofh.thermalexpansion.gui.client.GuiPoweredBase;
import cofh.thermalexpansion.gui.container.machine.ContainerCharger;
import cofh.thermalexpansion.gui.element.ElementSlotOverlay;
import cofh.thermalexpansion.gui.element.ElementSlotOverlay.SlotColor;
import cofh.thermalexpansion.gui.element.ElementSlotOverlay.SlotRender;
import cofh.thermalexpansion.gui.element.ElementSlotOverlay.SlotType;
import cofh.thermalexpansion.init.TEProps;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

public class GuiCharger extends GuiPoweredBase {

	public static final String TEX_PATH = TEProps.PATH_GUI_MACHINE + "charger.png";
	public static final ResourceLocation TEXTURE = new ResourceLocation(TEX_PATH);

	private TileCharger myTile;

	private ElementBase slotInput;
	private ElementBase slotOutput;
	private ElementDualScaled progress;

	private ElementSimple tankBackground;
	private ElementFluidTank tank;

	private ElementSimple modeOverlay;

	public GuiCharger(InventoryPlayer inventory, TileEntity tile) {

		super(new ContainerCharger(inventory, tile), tile, inventory.player, TEXTURE);

		generateInfo("tab.thermalexpansion.machine.charger");

		myTile = (TileCharger) tile;
	}

	@Override
	public void initGui() {

		super.initGui();

		tankBackground = (ElementSimple) addElement(new ElementSimple(this, 151, 8).setTextureOffsets(176, 104).setSize(18, 62).setTexture(TEX_PATH, 256, 256));

		slotInput = addElement(new ElementSlotOverlay(this, 44, 35).setSlotInfo(SlotColor.BLUE, SlotType.STANDARD, SlotRender.FULL));
		slotOutput = addElement(new ElementSlotOverlay(this, 121, 31).setSlotInfo(SlotColor.ORANGE, SlotType.OUTPUT, SlotRender.FULL));

		if (!myTile.smallStorage()) {
			addElement(new ElementEnergyStored(this, 8, 8, myTile.getEnergyStorage()));
		}
		progress = (ElementDualScaled) addElement(new ElementDualScaled(this, 80, 53).setSize(16, 16).setTexture(TEX_FLUX, 32, 16));

		tank = (ElementFluidTank) addElement(new ElementFluidTank(this, 152, 9, myTile.getTank()).setGauge(0).setAlwaysShow(true));

		modeOverlay = (ElementSimple) addElement(new ElementSimple(this, 44, 35).setTextureOffsets(176, 80).setSize(16, 16).setTexture(TEX_PATH, 256, 256));

		tankBackground.setVisible(myTile.augmentRepair());
		tank.setVisible(myTile.augmentRepair());

		modeOverlay.setVisible(myTile.augmentWireless());
	}

	@Override
	protected void updateElementInformation() {

		super.updateElementInformation();

		slotInput.setVisible(baseTile.hasSideType(INPUT_ALL) || baseTile.hasSideType(OMNI));
		slotOutput.setVisible(baseTile.hasSideType(OUTPUT_ALL) || baseTile.hasSideType(OMNI));

		progress.setQuantity(baseTile.getScaledProgress(SPEED));

		tankBackground.setVisible(myTile.augmentRepair());
		tank.setVisible(myTile.augmentRepair());

		modeOverlay.setVisible(myTile.augmentWireless());
	}

}
