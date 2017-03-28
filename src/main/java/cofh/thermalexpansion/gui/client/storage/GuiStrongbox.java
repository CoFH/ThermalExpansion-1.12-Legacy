package cofh.thermalexpansion.gui.client.storage;

import cofh.core.gui.GuiCore;
import cofh.core.gui.element.TabInfo;
import cofh.core.gui.element.TabSecurity;
import cofh.core.init.CoreProps;
import cofh.core.util.helpers.SecurityHelper;
import cofh.lib.gui.element.TabBase;
import cofh.lib.util.helpers.MathHelper;
import cofh.lib.util.helpers.StringHelper;
import cofh.thermalexpansion.block.storage.TileStrongbox;
import cofh.thermalexpansion.gui.container.storage.ContainerStrongbox;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.tileentity.TileEntity;

import java.util.UUID;

public class GuiStrongbox extends GuiCore {

	protected TileStrongbox myTile;
	protected UUID playerName;
	protected int storageIndex;

	protected TabBase securityTab;

	public GuiStrongbox(InventoryPlayer inventory, TileEntity tile) {

		super(new ContainerStrongbox(inventory, tile));

		myTile = (TileStrongbox) tile;
		playerName = SecurityHelper.getID(inventory.player);
		storageIndex = myTile.getStorageIndex();
		texture = CoreProps.TEXTURE_STORAGE[storageIndex];
		name = myTile.getName();

		xSize = 14 + 18 * MathHelper.clamp(storageIndex + 1, 9, 13);
		ySize = 112 + 18 * MathHelper.clamp(storageIndex, 2, 8);

		myInfo = StringHelper.localize("tab.thermalexpansion.storage.strongbox.0");

		if (myTile.enchantHolding <= 0) {
			myInfo += "\n\n" + StringHelper.localize("tab.thermalexpansion.storage.enchant");
		}
	}

	@Override
	public void initGui() {

		super.initGui();

		addTab(new TabInfo(this, myInfo));
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
		securityTab.setVisible(myTile.enableSecurity() && myTile.isSecured());
	}

}
