package thermalexpansion.gui.client.machine;

import cofh.gui.element.ElementBase;
import cofh.gui.element.ElementEnergyStored;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

import thermalexpansion.core.TEProps;
import thermalexpansion.gui.client.GuiAugmentableBase;
import thermalexpansion.gui.container.machine.ContainerCharger;
import thermalexpansion.gui.element.ElementSlotOverlay;

public class GuiCharger extends GuiAugmentableBase {

	static final ResourceLocation TEXTURE = new ResourceLocation(TEProps.PATH_GUI_MACHINE + "Charger.png");

	ElementBase slotInput;
	ElementBase slotOutput;

	public GuiCharger(InventoryPlayer inventory, TileEntity tile) {

		super(new ContainerCharger(inventory, tile), tile, inventory.player, TEXTURE);

		generateInfo("tab.thermalexpansion.machine.charger", 3);
	}

	@Override
	public void initGui() {

		super.initGui();

		slotInput = addElement(new ElementSlotOverlay(this, 35, 31).setSlotInfo(0, 0, 2));
		slotOutput = addElement(new ElementSlotOverlay(this, 121, 27).setSlotInfo(3, 1, 2));

		addElement(new ElementEnergyStored(this, 80, 18, myTile.getEnergyStorage()));
	}

	@Override
	protected void updateElementInformation() {

		super.updateElementInformation();

		slotInput.setVisible(myTile.hasSide(1));
		slotOutput.setVisible(myTile.hasSide(2));

	}

}
