package cofh.thermalexpansion.gui.client.dynamo;

import cofh.core.gui.GuiCore;
import cofh.core.gui.element.*;
import cofh.core.util.helpers.SecurityHelper;
import cofh.lib.gui.container.IAugmentableContainer;
import cofh.lib.gui.element.ElementEnergyStored;
import cofh.lib.gui.element.TabBase;
import cofh.lib.util.helpers.StringHelper;
import cofh.thermalexpansion.block.dynamo.TileDynamoBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

import java.util.UUID;

public abstract class GuiDynamoBase extends GuiCore {

	protected TileDynamoBase baseTile;
	protected UUID playerName;

	protected String myTutorial = "";

	protected TabBase augmentTab;
	protected TabBase redstoneTab;
	protected TabBase securityTab;

	public GuiDynamoBase(Container container, TileEntity tile, EntityPlayer player, ResourceLocation texture) {

		super(container, texture);

		baseTile = (TileDynamoBase) tile;
		name = baseTile.getName();
		playerName = SecurityHelper.getID(player);

		if (baseTile.isAugmentable()) {
			myTutorial = StringHelper.tutorialTabAugment() + "\n\n";
		}
		if (baseTile.enableSecurity() && baseTile.isSecured()) {
			myTutorial += StringHelper.tutorialTabSecurity() + "\n\n";
		}
		if (baseTile.hasRedstoneControl()) {
			myTutorial += StringHelper.tutorialTabRedstone();
		}
	}

	@Override
	public void initGui() {

		super.initGui();

		addElement(new ElementEnergyStored(this, 80, 18, baseTile.getEnergyStorage()));

		// Right Side
		if (baseTile.isAugmentable()) {
			addTab(new TabAugment(this, (IAugmentableContainer) inventorySlots));
		}
		redstoneTab = addTab(new TabRedstoneControl(this, baseTile));
		redstoneTab.setVisible(baseTile.hasRedstoneControl());

		// Left Side
		securityTab = addTab(new TabSecurity(this, baseTile, playerName));
		securityTab.setVisible(baseTile.enableSecurity() && baseTile.isSecured());

		if (baseTile.getMaxEnergyStored(null) > 0) {
			addTab(new TabEnergy(this, baseTile, true));
		}
		addTab(new TabInfo(this, myInfo + "\n\n" + StringHelper.localize("tab.thermalexpansion.dynamo.0")));

		if (!myTutorial.isEmpty()) {
			addTab(new TabTutorial(this, myTutorial));
		}
	}

	@Override
	public void updateScreen() {

		super.updateScreen();

		if (!baseTile.canAccess()) {
			this.mc.thePlayer.closeScreen();
		}
		redstoneTab.setVisible(baseTile.hasRedstoneControl());

		securityTab.setVisible(baseTile.enableSecurity() && baseTile.isSecured());
	}

}
