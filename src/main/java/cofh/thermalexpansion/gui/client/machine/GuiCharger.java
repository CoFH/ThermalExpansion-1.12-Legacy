package cofh.thermalexpansion.gui.client.machine;

import cofh.lib.gui.element.ElementBase;
import cofh.lib.gui.element.ElementDualScaled;
import cofh.lib.gui.element.ElementEnergyStored;
import cofh.thermalexpansion.gui.client.GuiPoweredBase;
import cofh.thermalexpansion.gui.container.machine.ContainerCharger;
import cofh.thermalexpansion.gui.element.ElementSlotOverlay;
import cofh.thermalexpansion.init.TEProps;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

public class GuiCharger extends GuiPoweredBase {

	public static final ResourceLocation TEXTURE = new ResourceLocation(TEProps.PATH_GUI_MACHINE + "charger.png");

	private ElementBase slotInput;
	private ElementBase slotOutput;
	private ElementDualScaled progress;

	public GuiCharger(InventoryPlayer inventory, TileEntity tile) {

		super(new ContainerCharger(inventory, tile), tile, inventory.player, TEXTURE);

		generateInfo("tab.thermalexpansion.machine.charger", 3);
	}

	@Override
	public void initGui() {

		super.initGui();

		slotInput = addElement(new ElementSlotOverlay(this, 44, 35).setSlotInfo(0, 0, 2));
		slotOutput = addElement(new ElementSlotOverlay(this, 121, 31).setSlotInfo(3, 1, 2));

		addElement(new ElementEnergyStored(this, 8, 8, baseTile.getEnergyStorage()));
		progress = (ElementDualScaled) addElement(new ElementDualScaled(this, 80, 53).setSize(16, 16).setTexture(TEX_FLUX, 32, 16));
	}

	@Override
	protected void updateElementInformation() {

		super.updateElementInformation();

		slotInput.setVisible(baseTile.hasSide(1));
		slotOutput.setVisible(baseTile.hasSide(2));

		progress.setQuantity(baseTile.getScaledProgress(SPEED));
	}

}
