package cofh.thermalexpansion.gui.client.device;

import cofh.core.gui.GuiContainerCore;
import cofh.core.gui.element.tab.*;
import cofh.core.util.helpers.SecurityHelper;
import cofh.thermalexpansion.block.device.TileDeviceBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

import java.util.UUID;

public class GuiDeviceBase extends GuiContainerCore {

	protected TileDeviceBase baseTile;
	protected UUID playerName;

	protected TabBase redstoneTab;
	protected TabBase configTab;
	protected TabBase securityTab;

	public GuiDeviceBase(Container container, TileEntity tile, EntityPlayer player, ResourceLocation texture) {

		super(container, texture);

		baseTile = (TileDeviceBase) tile;
		name = baseTile.getName();
		playerName = SecurityHelper.getID(player);
	}

	@Override
	public void initGui() {

		super.initGui();

		// Right Side
		redstoneTab = addTab(new TabRedstoneControl(this, baseTile));

		if (baseTile.hasTransferIn() || baseTile.hasTransferOut()) {
			configTab = addTab(new TabConfigurationTransfer(this, baseTile));
		} else {
			configTab = addTab(new TabConfiguration(this, baseTile));
		}

		// Left Side
		securityTab = addTab(new TabSecurity(this, baseTile, playerName));
		securityTab.setVisible(baseTile.enableSecurity() && baseTile.isSecured());

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
