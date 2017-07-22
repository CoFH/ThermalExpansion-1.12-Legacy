package cofh.thermalexpansion.gui.client;

import cofh.core.gui.GuiCore;
import cofh.core.gui.container.IAugmentableContainer;
import cofh.core.gui.element.tab.*;
import cofh.core.util.helpers.SecurityHelper;
import cofh.thermalexpansion.block.TilePowered;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

import java.util.UUID;

public abstract class GuiPoweredBase extends GuiCore {

	protected TilePowered baseTile;
	protected UUID playerName;

	protected TabBase augmentTab;
	protected TabBase redstoneTab;
	protected TabBase configTab;
	protected TabBase securityTab;

	public GuiPoweredBase(Container container, TileEntity tile, EntityPlayer player, ResourceLocation texture) {

		super(container, texture);

		baseTile = (TilePowered) tile;
		name = baseTile.getName();
		playerName = SecurityHelper.getID(player);
	}

	@Override
	public void initGui() {

		super.initGui();

		// Right Side
		augmentTab = addTab(new TabAugment(this, (IAugmentableContainer) inventorySlots));

		redstoneTab = addTab(new TabRedstoneControl(this, baseTile));
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
	}

	@Override
	public void updateScreen() {

		super.updateScreen();

		if (!baseTile.canAccess()) {
			this.mc.player.closeScreen();
		}
		redstoneTab.setVisible(baseTile.hasRedstoneControl());

		securityTab.setVisible(baseTile.enableSecurity() && baseTile.isSecured());
	}

}
