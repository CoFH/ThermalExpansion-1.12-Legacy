package thermalexpansion.gui.client.machine;

import cofh.core.CoFHProps;
import cofh.gui.GuiBaseAdv;
import cofh.gui.element.ElementBase;
import cofh.gui.element.ElementEnergyStored;
import cofh.gui.element.TabConfiguration;
import cofh.gui.element.TabEnergy;
import cofh.gui.element.TabInfo;
import cofh.gui.element.TabRedstone;
import cofh.gui.element.TabSecurity;
import cofh.gui.element.TabTutorial;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

import thermalexpansion.block.machine.TileCharger;
import thermalexpansion.core.TEProps;
import thermalexpansion.gui.container.machine.ContainerCharger;
import thermalexpansion.gui.element.ElementSlotOverlay;

public class GuiCharger extends GuiBaseAdv {

	static final ResourceLocation TEXTURE = new ResourceLocation(TEProps.PATH_GUI_MACHINE + "Charger.png");
	static final String INFO = "Infuse energy into compatible items.\n\nCharge rate may be limited by the item.\n\nFeel the hum.";

	TileCharger myTile;
	String playerName;

	ElementBase slotInput;
	ElementBase slotOutput;

	public GuiCharger(InventoryPlayer inventory, TileEntity theTile) {

		super(new ContainerCharger(inventory, theTile), TEXTURE);
		myTile = (TileCharger) theTile;
		name = myTile.getInventoryName();
		playerName = inventory.player.getCommandSenderName();
	}

	@Override
	public void initGui() {

		super.initGui();

		slotInput = addElement(new ElementSlotOverlay(this, 35, 31).setSlotInfo(0, 0, 2));
		slotOutput = addElement(new ElementSlotOverlay(this, 121, 27).setSlotInfo(3, 1, 2));

		addElement(new ElementEnergyStored(this, 80, 18, myTile.getEnergyStorage()));

		addTab(new TabEnergy(this, myTile, false));
		addTab(new TabRedstone(this, myTile));
		addTab(new TabConfiguration(this, myTile));
		addTab(new TabInfo(this, INFO));
		addTab(new TabTutorial(this, CoFHProps.tutorialTabRedstone));
		if (myTile.enableSecurity() && myTile.isSecured()) {
			addTab(new TabSecurity(this, myTile, playerName));
		}
	}

	@Override
	public void updateScreen() {

		super.updateScreen();

		if (!myTile.canAccess()) {
			this.mc.thePlayer.closeScreen();
		}
	}

	@Override
	protected void updateElementInformation() {

		slotInput.setVisible(myTile.hasSide(1));
		slotOutput.setVisible(myTile.hasSide(2));

	}

}
