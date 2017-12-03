package cofh.thermalexpansion.gui.client.machine;

import cofh.core.gui.element.ElementBase;
import cofh.core.gui.element.ElementButton;
import cofh.core.gui.element.ElementDualScaled;
import cofh.core.gui.element.ElementEnergyStored;
import cofh.thermalexpansion.block.machine.TileCompactor;
import cofh.thermalexpansion.gui.client.GuiPoweredBase;
import cofh.thermalexpansion.gui.container.machine.ContainerCompactor;
import cofh.thermalexpansion.gui.element.ElementSlotOverlay;
import cofh.thermalexpansion.gui.element.ElementSlotOverlay.SlotColor;
import cofh.thermalexpansion.gui.element.ElementSlotOverlay.SlotRender;
import cofh.thermalexpansion.gui.element.ElementSlotOverlay.SlotType;
import cofh.thermalexpansion.init.TEProps;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

public class GuiCompactor extends GuiPoweredBase {

	public static final String TEX_PATH = TEProps.PATH_GUI_MACHINE + "compactor.png";
	public static final ResourceLocation TEXTURE = new ResourceLocation(TEX_PATH);

	// @formatter:off
	public static final String[] TOOLTIPS = new String[] {
			"gui.thermalexpansion.machine.compactor.modePress",
			"gui.thermalexpansion.machine.compactor.modeStorage",
			"gui.thermalexpansion.machine.compactor.modeMint",
			"gui.thermalexpansion.machine.compactor.modeGear"};
	// @formatter:on

	private TileCompactor myTile;

	private ElementBase slotInput;
	private ElementBase slotOutput;

	private ElementDualScaled progress;
	private ElementDualScaled speed;

	private ElementButton modeSel;

	public GuiCompactor(InventoryPlayer inventory, TileEntity tile) {

		super(new ContainerCompactor(inventory, tile), tile, inventory.player, TEXTURE);

		generateInfo("tab.thermalexpansion.machine.compactor");

		myTile = (TileCompactor) tile;
	}

	@Override
	public void initGui() {

		super.initGui();

		slotInput = addElement(new ElementSlotOverlay(this, 53, 26).setSlotInfo(SlotColor.BLUE, SlotType.STANDARD, SlotRender.FULL));
		slotOutput = addElement(new ElementSlotOverlay(this, 112, 31).setSlotInfo(SlotColor.ORANGE, SlotType.OUTPUT, SlotRender.FULL));

		if (!myTile.smallStorage()) {
			addElement(new ElementEnergyStored(this, 8, 8, myTile.getEnergyStorage()));
		}
		progress = (ElementDualScaled) addElement(new ElementDualScaled(this, 79, 34).setMode(1).setSize(24, 16).setTexture(TEX_ARROW_RIGHT, 64, 16));
		speed = (ElementDualScaled) addElement(new ElementDualScaled(this, 53, 44).setSize(16, 16).setTexture(TEX_COMPACT, 32, 16));
		modeSel = (ElementButton) addElement(new ElementButton(this, 80, 53, "Mode", 176, 0, 176, 16, 176, 32, 16, 16, TEX_PATH));
	}

	@Override
	protected void updateElementInformation() {

		super.updateElementInformation();

		slotInput.setVisible(baseTile.hasSideType(INPUT_ALL) || baseTile.hasSideType(OMNI));
		slotOutput.setVisible(baseTile.hasSideType(OUTPUT_ALL) || baseTile.hasSideType(OMNI));

		progress.setQuantity(baseTile.getScaledProgress(PROGRESS));
		speed.setQuantity(baseTile.getScaledSpeed(SPEED));

		modeSel.setToolTip(TOOLTIPS[myTile.modeFlag]);
		modeSel.setSheetX(176 + myTile.modeFlag * 16);
		modeSel.setHoverX(176 + myTile.modeFlag * 16);
	}

	@Override
	public void handleElementButtonClick(String buttonName, int mouseButton) {

		if (buttonName.equalsIgnoreCase("Mode")) {
			if (myTile.modeFlag == 1) {
				playClickSound(0.8F);
			} else {
				playClickSound(0.6F);
			}
			myTile.toggleMode();
		}
	}

}
