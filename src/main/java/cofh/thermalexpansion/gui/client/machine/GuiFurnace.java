package cofh.thermalexpansion.gui.client.machine;

import cofh.core.gui.element.*;
import cofh.thermalexpansion.block.machine.TileFurnace;
import cofh.thermalexpansion.gui.client.GuiPoweredBase;
import cofh.thermalexpansion.gui.container.machine.ContainerFurnace;
import cofh.thermalexpansion.gui.element.ElementSlotOverlay;
import cofh.thermalexpansion.gui.element.ElementSlotOverlay.SlotColor;
import cofh.thermalexpansion.gui.element.ElementSlotOverlay.SlotRender;
import cofh.thermalexpansion.gui.element.ElementSlotOverlay.SlotType;
import cofh.thermalexpansion.init.TEProps;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

public class GuiFurnace extends GuiPoweredBase {

	public static final String TEX_PATH = TEProps.PATH_GUI_MACHINE + "furnace.png";
	public static final ResourceLocation TEXTURE = new ResourceLocation(TEX_PATH);

	private TileFurnace myTile;

	private ElementBase slotInput;
	private ElementBase slotOutput;
	private ElementSlotOverlay slotTank;

	private ElementSimple tankBackground;
	private ElementFluidTank tank;

	private ElementDualScaled progress;
	private ElementDualScaled speed;
	private ElementFluid progressFluid;
	private ElementDualScaled progressOverlay;

	public GuiFurnace(InventoryPlayer inventory, TileEntity tile) {

		super(new ContainerFurnace(inventory, tile), tile, inventory.player, TEXTURE);

		generateInfo("tab.thermalexpansion.machine.furnace");

		myTile = (TileFurnace) tile;
	}

	@Override
	public void initGui() {

		super.initGui();

		tankBackground = (ElementSimple) addElement(new ElementSimple(this, 151, 8).setTextureOffsets(176, 104).setSize(18, 62).setTexture(TEX_PATH, 256, 256));

		slotInput = addElement(new ElementSlotOverlay(this, 53, 26).setSlotInfo(SlotColor.BLUE, SlotType.STANDARD, SlotRender.FULL));
		slotOutput = addElement(new ElementSlotOverlay(this, 112, 31).setSlotInfo(SlotColor.ORANGE, SlotType.OUTPUT, SlotRender.FULL));

		slotTank = (ElementSlotOverlay) addElement(new ElementSlotOverlay(this, 152, 9).setSlotInfo(SlotColor.ORANGE, SlotType.TANK, SlotRender.FULL).setVisible(false));

		addElement(new ElementEnergyStored(this, 8, 8, baseTile.getEnergyStorage()));

		progress = (ElementDualScaled) addElement(new ElementDualScaled(this, 79, 34).setMode(1).setSize(24, 16).setTexture(TEX_ARROW_RIGHT, 64, 16));
		speed = (ElementDualScaled) addElement(new ElementDualScaled(this, 53, 44).setSize(16, 16).setTexture(TEX_FLAME, 32, 16));

		tank = (ElementFluidTank) addElement(new ElementFluidTank(this, 152, 9, myTile.getTank()).setGauge(0).setAlwaysShow(true));
		progressFluid = (ElementFluid) addElement(new ElementFluid(this, 79, 34).setFluid(myTile.getTankFluid()).setSize(24, 16));
		progressOverlay = (ElementDualScaled) addElement(new ElementDualScaled(this, 79, 34).setBackground(false).setMode(1).setSize(24, 16).setTexture(TEX_ARROW_FLUID_RIGHT, 64, 16));

		tankBackground.setVisible(myTile.augmentPyrolysisClient());
		tank.setVisible(myTile.augmentPyrolysisClient());
		progressFluid.setVisible(myTile.augmentPyrolysisClient());
		progressOverlay.setVisible(myTile.augmentPyrolysisClient());
	}

	@Override
	protected void updateElementInformation() {

		super.updateElementInformation();

		slotInput.setVisible(baseTile.hasSideType(INPUT_ALL) || baseTile.hasSideType(OMNI));
		slotOutput.setVisible(baseTile.hasSideType(OUTPUT_ALL) || baseTile.hasSideType(OMNI));

		slotTank.setVisible(myTile.augmentPyrolysisClient() && (baseTile.hasSideType(OUTPUT_ALL) || baseTile.hasSideType(OMNI)));

		progress.setQuantity(baseTile.getScaledProgress(PROGRESS));
		speed.setQuantity(baseTile.getScaledSpeed(SPEED));

		progressFluid.setSize(baseTile.getScaledProgress(PROGRESS), 16);
		progressOverlay.setQuantity(baseTile.getScaledProgress(PROGRESS));

		progress.setVisible(!myTile.augmentPyrolysisClient());

		tankBackground.setVisible(myTile.augmentPyrolysisClient());
		tank.setVisible(myTile.augmentPyrolysisClient());
		progressFluid.setVisible(myTile.augmentPyrolysisClient());
		progressOverlay.setVisible(myTile.augmentPyrolysisClient());
	}

}
