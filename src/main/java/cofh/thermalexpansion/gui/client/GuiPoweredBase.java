package cofh.thermalexpansion.gui.client;

import cofh.core.gui.GuiCore;
import cofh.core.gui.element.*;
import cofh.lib.gui.container.IAugmentableContainer;
import cofh.lib.gui.element.TabBase;
import cofh.lib.util.helpers.SecurityHelper;
import cofh.lib.util.helpers.StringHelper;
import cofh.thermalexpansion.block.TilePowered;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

import java.util.UUID;

public abstract class GuiPoweredBase extends GuiCore {

	protected TilePowered baseTile;
	protected UUID playerName;

	public String myTutorial = "";

	protected TabBase augmentTab;
	protected TabBase redstoneTab;
	protected TabBase configTab;
	protected TabBase securityTab;

	public GuiPoweredBase(Container container, TileEntity tile, EntityPlayer player, ResourceLocation texture) {

		super(container, texture);

		baseTile = (TilePowered) tile;
		name = baseTile.getName();
		playerName = SecurityHelper.getID(player);

		if (baseTile.isAugmentable()) {
			myTutorial = StringHelper.tutorialTabAugment() + "\n\n";
		} else {
			myTutorial = "upgrade\n\n";
		}
		if (baseTile.enableSecurity() && baseTile.isSecured()) {
			myTutorial += StringHelper.tutorialTabSecurity() + "\n\n";
		}
		if (baseTile.hasRedstoneControl()) {
			myTutorial += StringHelper.tutorialTabRedstone() + "\n\n";
		}
		myTutorial += StringHelper.tutorialTabConfiguration() + "\n\n";

		if (baseTile.getMaxEnergyStored(null) > 0) {
			myTutorial += StringHelper.tutorialTabFluxRequired();
		}
	}

	@Override
	public void initGui() {

		super.initGui();

		// Right Side
		if (baseTile.isAugmentable()) {
			augmentTab = addTab(new TabAugment(this, (IAugmentableContainer) inventorySlots));
		}
		redstoneTab = addTab(new TabRedstone(this, baseTile));
		redstoneTab.setVisible(baseTile.hasRedstoneControl());

		configTab = addTab(new TabConfigurationTransfer(this, baseTile));

		// Left Side
		securityTab = addTab(new TabSecurity(this, baseTile, playerName));
		securityTab.setVisible(baseTile.enableSecurity() && baseTile.isSecured());

		if (baseTile.getMaxEnergyStored(null) > 0) {
			addTab(new TabEnergy(this, baseTile, false));
		}
		if (!myInfo.isEmpty()) {
			addTab(new TabInfo(this, myInfo));
		}
		addTab(new TabTutorial(this, myTutorial));
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
