package cofh.thermalexpansion.gui.client.dynamo;

import cofh.core.gui.GuiBaseAdv;
import cofh.core.gui.element.*;
import cofh.lib.gui.container.IAugmentableContainer;
import cofh.lib.gui.element.ElementEnergyStored;
import cofh.lib.gui.element.TabBase;
import cofh.lib.util.helpers.SecurityHelper;
import cofh.lib.util.helpers.StringHelper;
import cofh.thermalexpansion.block.dynamo.TileDynamoBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

import java.util.UUID;

public abstract class GuiDynamoBase extends GuiBaseAdv {

	protected TileDynamoBase myTile;
	protected UUID playerName;

	public String myTutorial = StringHelper.tutorialTabAugment();

	protected TabBase redstoneTab;

	public GuiDynamoBase(Container container, TileEntity tile, EntityPlayer player, ResourceLocation texture) {

		super(container, texture);

		myTile = (TileDynamoBase) tile;
		name = myTile.getName();
		playerName = SecurityHelper.getID(player);

		if (myTile.enableSecurity() && myTile.isSecured()) {
			myTutorial += "\n\n" + StringHelper.tutorialTabSecurity();
		}
		if (myTile.hasRedstoneControl) {
			myTutorial += "\n\n" + StringHelper.tutorialTabRedstone();
		}
	}

	@Override
	public void initGui() {

		super.initGui();

		addElement(new ElementEnergyStored(this, 80, 18, myTile.getEnergyStorage()));

		addTab(new TabAugment(this, (IAugmentableContainer) inventorySlots));
		if (myTile.enableSecurity() && myTile.isSecured()) {
			addTab(new TabSecurity(this, myTile, playerName));
		}
		redstoneTab = addTab(new TabRedstone(this, myTile));

		if (myTile.getMaxEnergyStored(null) > 0) {
			addTab(new TabEnergy(this, myTile, true));
		}
		addTab(new TabInfo(this, myInfo + "\n\n" + StringHelper.localize("tab.thermalexpansion.dynamo.0")));
		addTab(new TabTutorial(this, myTutorial));
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

		super.updateElementInformation();

		redstoneTab.setVisible(myTile.hasRedstoneControl);
	}

}
