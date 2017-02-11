package cofh.thermalexpansion.gui.client.machine;

import cofh.lib.gui.element.ElementBase;
import cofh.lib.gui.element.ElementButton;
import cofh.lib.gui.element.ElementDualScaled;
import cofh.lib.gui.element.ElementEnergyStored;
import cofh.thermalexpansion.block.machine.TileCompactor;
import cofh.thermalexpansion.gui.client.GuiPoweredBase;
import cofh.thermalexpansion.gui.container.machine.ContainerCompactor;
import cofh.thermalexpansion.gui.element.ElementSlotOverlay;
import cofh.thermalexpansion.init.TEProps;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

public class GuiCompactor extends GuiPoweredBase {

	public static final String TEX_PATH = TEProps.PATH_GUI_MACHINE + "compactor.png";
	public static final ResourceLocation TEXTURE = new ResourceLocation(TEX_PATH);

	private TileCompactor myTile;

	private ElementBase slotInput;
	private ElementBase slotOutput;

	private ElementDualScaled progress;
	private ElementDualScaled speed;

	private ElementButton mode;

	public GuiCompactor(InventoryPlayer inventory, TileEntity tile) {

		super(new ContainerCompactor(inventory, tile), tile, inventory.player, TEXTURE);

		generateInfo("tab.thermalexpansion.machine.compactor", 3);

		myTile = (TileCompactor) tile;
	}

	@Override
	public void initGui() {

		super.initGui();

		slotInput = addElement(new ElementSlotOverlay(this, 53, 26).setSlotInfo(0, 0, 2));
		slotOutput = addElement(new ElementSlotOverlay(this, 112, 31).setSlotInfo(3, 1, 2));

		addElement(new ElementEnergyStored(this, 8, 8, baseTile.getEnergyStorage()));

		progress = (ElementDualScaled) addElement(new ElementDualScaled(this, 79, 34).setMode(1).setSize(24, 16).setTexture(TEX_ARROW_RIGHT, 64, 16));
		speed = (ElementDualScaled) addElement(new ElementDualScaled(this, 53, 44).setSize(16, 16).setTexture(TEX_FLAME, 32, 16));
		mode = (ElementButton) addElement(new ElementButton(this, 80, 53, "Mode", 176, 0, 176, 16, 176, 32, 16, 16, TEX_PATH));
	}

	@Override
	protected void updateElementInformation() {

		super.updateElementInformation();

		slotInput.setVisible(baseTile.hasSide(1));
		slotOutput.setVisible(baseTile.hasSide(2));

		progress.setQuantity(baseTile.getScaledProgress(PROGRESS));
		speed.setQuantity(baseTile.getScaledSpeed(SPEED));
	}

	@Override
	public void handleElementButtonClick(String buttonName, int mouseButton) {

		//		if (buttonName.equals("Mode")) {
		//			if (myTile.mode == myTile.modeFlag) {
		//				if (myTile.reverse) {
		//					playClickSound(1.0F, 0.8F);
		//				} else {
		//					playClickSound(1.0F, 0.6F);
		//				}
		//				myTile.setMode(!myTile.mode);
		//			}
		//		}
	}

}
