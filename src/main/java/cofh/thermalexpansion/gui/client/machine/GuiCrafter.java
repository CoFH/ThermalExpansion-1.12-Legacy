package cofh.thermalexpansion.gui.client.machine;

import cofh.core.gui.element.ElementBase;
import cofh.core.gui.element.ElementButton;
import cofh.core.gui.element.ElementDualScaled;
import cofh.core.gui.element.ElementEnergyStored;
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

	public static final String TEX_PATH = TEProps.PATH_GUI_MACHINE + "crafter.png";
	public static final ResourceLocation TEXTURE = new ResourceLocation(TEX_PATH);

	private TileCrafter myTile;
	private ContainerCrafter myContainer;

	ElementSlotOverlayCrafter slotInput;
	ElementBase slotOutput;
	ElementBase slotTank;

	private ElementDualScaled progress;
	private ElementDualScaled speed;

	private ElementButton setRecipe;

	public GuiCrafter(InventoryPlayer inventory, TileEntity tile) {

		super(new ContainerCrafter(inventory, tile), tile, inventory.player, TEXTURE);

		generateInfo("tab.thermalexpansion.machine.crafter");

		myTile = (TileCrafter) tile;
		myContainer = (ContainerCrafter) inventorySlots;
		ySize = 208;
	}

	@Override
	public void initGui() {

		super.initGui();

		slotInput = (ElementSlotOverlayCrafter) addElement(new ElementSlotOverlayCrafter(this, 8, 77));
		slotOutput = addElement(new ElementSlotOverlay(this, 121, 17).setSlotInfo(SlotColor.ORANGE, SlotType.OUTPUT, SlotRender.FULL));

		//slotTank = addElement(new ElementSlotOverlay(this, 152, 9).setSlotInfo(SlotColor.BLUE, SlotType.TANK, SlotRender.FULL));

		if (!myTile.smallStorage()) {
			addElement(new ElementEnergyStored(this, 8, 8, myTile.getEnergyStorage()));
		}
		// addElement(new ElementFluidTank(this, 152, 9, baseTile.getTank()).setGauge(1).setAlwaysShow(true));
		progress = (ElementDualScaled) addElement(new ElementDualScaled(this, 92, 21).setMode(1).setSize(24, 16).setTexture(TEX_ARROW_RIGHT, 64, 16));

		setRecipe = (ElementButton) addElement(new ElementButton(this, 125, 53, "SetRecipe", 176, 0, 176, 16, 176, 32, 16, 16, TEX_PATH));
	}

	@Override
	protected void updateElementInformation() {

		super.updateElementInformation();

		slotInput.setVisible(baseTile.hasSideType(INPUT_ALL) || baseTile.hasSideType(OMNI));
		slotOutput.setVisible(baseTile.hasSideType(OUTPUT_ALL) || baseTile.hasSideType(OMNI));

		//slotTank.setVisible(baseTile.hasSideType(INPUT_ALL) || baseTile.hasSideType(OMNI));

		progress.setQuantity(baseTile.getScaledProgress(PROGRESS));

		if (myContainer.validRecipe()) {
			setRecipe.setActive();
		} else {
			setRecipe.setDisabled();
		}
	}

	@Override
	public void handleElementButtonClick(String buttonName, int mouseButton) {

		if (buttonName.equalsIgnoreCase("SetRecipe")) {
			myContainer.setRecipe();
		} else if (buttonName.equalsIgnoreCase("ClearRecipe")) {
			myContainer.clearRecipe();
		}
	}

}
