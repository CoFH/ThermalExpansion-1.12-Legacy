package cofh.thermalexpansion.gui.client.machine;

import cofh.lib.gui.element.ElementButton;
import cofh.lib.gui.element.ElementDualScaled;
import cofh.lib.gui.element.ElementEnergyStored;
import cofh.lib.gui.element.ElementSimple;
import cofh.thermalexpansion.block.machine.TileSmelter;
import cofh.thermalexpansion.core.TEProps;
import cofh.thermalexpansion.gui.client.GuiAugmentableBase;
import cofh.thermalexpansion.gui.container.machine.ContainerSmelter;
import cofh.thermalexpansion.gui.element.ElementSlotOverlay;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

public class GuiSmelter extends GuiAugmentableBase {

	static final String TEX_PATH = TEProps.PATH_GUI_MACHINE + "Smelter.png";
	public static final ResourceLocation TEXTURE = new ResourceLocation(TEX_PATH);

	TileSmelter myTile;

	ElementSlotOverlay[] slotPrimaryInput = new ElementSlotOverlay[2];
	ElementSlotOverlay[] slotSecondaryInput = new ElementSlotOverlay[2];

	ElementSlotOverlay[] slotPrimaryOutput = new ElementSlotOverlay[2];
	ElementSlotOverlay[] slotSecondaryOutput = new ElementSlotOverlay[2];

	ElementDualScaled progress;
	ElementDualScaled speed;

	ElementButton mode;
	ElementSimple modeOverlay;

	public GuiSmelter(InventoryPlayer inventory, TileEntity tile) {

		super(new ContainerSmelter(inventory, tile), tile, inventory.player, TEXTURE);

		generateInfo("tab.thermalexpansion.machine.smelter", 4);

		myTile = (TileSmelter) tile;
	}

	@Override
	public void initGui() {

		super.initGui();

		slotPrimaryInput[0] = (ElementSlotOverlay) addElement(new ElementSlotOverlay(this, 32, 26).setSlotInfo(0, 0, 2));
		slotPrimaryInput[1] = (ElementSlotOverlay) addElement(new ElementSlotOverlay(this, 32, 26).setSlotInfo(4, 0, 1));
		slotSecondaryInput[0] = (ElementSlotOverlay) addElement(new ElementSlotOverlay(this, 56, 26).setSlotInfo(0, 0, 2));
		slotSecondaryInput[1] = (ElementSlotOverlay) addElement(new ElementSlotOverlay(this, 56, 26).setSlotInfo(5, 0, 1));
		slotPrimaryOutput[0] = (ElementSlotOverlay) addElement(new ElementSlotOverlay(this, 112, 22).setSlotInfo(3, 2, 2));
		slotPrimaryOutput[1] = (ElementSlotOverlay) addElement(new ElementSlotOverlay(this, 112, 22).setSlotInfo(1, 2, 1));
		slotSecondaryOutput[0] = (ElementSlotOverlay) addElement(new ElementSlotOverlay(this, 116, 53).setSlotInfo(3, 0, 2));
		slotSecondaryOutput[1] = (ElementSlotOverlay) addElement(new ElementSlotOverlay(this, 116, 53).setSlotInfo(2, 0, 1));

		addElement(new ElementEnergyStored(this, 8, 8, myTile.getEnergyStorage()));
		progress = (ElementDualScaled) addElement(new ElementDualScaled(this, 79, 34).setMode(1).setSize(24, 16).setTexture(TEX_ARROW_RIGHT, 64, 16));
		speed = (ElementDualScaled) addElement(new ElementDualScaled(this, 44, 44).setSize(16, 16).setTexture(TEX_FLAME, 32, 16));

		mode = (ElementButton) addElement(new ElementButton(this, 80, 53, "Mode", 176, 0, 176, 16, 176, 32, 16, 16, TEX_PATH));
		modeOverlay = (ElementSimple) addElement(new ElementSimple(this, 32, 26).setTextureOffsets(176, 48).setSize(16, 16).setTexture(TEX_PATH, 256, 256));
	}

	@Override
	protected void updateElementInformation() {

		super.updateElementInformation();

		slotPrimaryInput[0].setVisible(myTile.hasSide(1));
		slotPrimaryInput[1].setVisible(myTile.hasSide(5));
		slotSecondaryInput[0].setVisible(myTile.hasSide(1));
		slotSecondaryInput[1].setVisible(myTile.hasSide(6));

		slotPrimaryOutput[0].setVisible(myTile.hasSide(4));
		slotPrimaryOutput[1].setVisible(myTile.hasSide(2));
		slotSecondaryOutput[0].setVisible(myTile.hasSide(4));
		slotSecondaryOutput[1].setVisible(myTile.hasSide(3));

		if (!myTile.hasSide(1)) {
			slotPrimaryInput[1].slotRender = 2;
			slotSecondaryInput[1].slotRender = 2;
		} else {
			slotPrimaryInput[1].slotRender = 1;
			slotSecondaryInput[1].slotRender = 1;
		}
		if (!myTile.hasSide(4)) {
			slotPrimaryOutput[1].slotRender = 2;
			slotSecondaryOutput[1].slotRender = 2;
		} else {
			slotPrimaryOutput[1].slotRender = 1;
			slotSecondaryOutput[1].slotRender = 1;
		}
		progress.setQuantity(myTile.getScaledProgress(PROGRESS));
		speed.setQuantity(myTile.getScaledSpeed(SPEED));

		if (myTile.lockPrimary) {
			mode.setToolTip("info.thermalexpansion.toggleSmelterUnlock");
			mode.setSheetX(176);
			mode.setHoverX(176);
			modeOverlay.setVisible(true);
		} else {
			mode.setToolTip("info.thermalexpansion.toggleSmelterLock");
			mode.setSheetX(192);
			mode.setHoverX(192);
			modeOverlay.setVisible(false);
		}
	}

	@Override
	public void handleElementButtonClick(String buttonName, int mouseButton) {

		if (buttonName.equals("Mode")) {
			if (myTile.lockPrimary) {
				playSound("random.click", 1.0F, 0.6F);
			} else {
				playSound("random.click", 1.0F, 0.8F);
			}
			myTile.setMode(!myTile.lockPrimary);
		}
	}

}
