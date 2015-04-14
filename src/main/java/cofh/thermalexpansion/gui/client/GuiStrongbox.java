package cofh.thermalexpansion.gui.client;

import cofh.core.CoFHProps;
import cofh.core.gui.GuiBaseAdv;
import cofh.core.gui.element.TabInfo;
import cofh.core.gui.element.TabSecurity;
import cofh.lib.util.helpers.MathHelper;
import cofh.lib.util.helpers.SecurityHelper;
import cofh.lib.util.helpers.StringHelper;
import cofh.thermalexpansion.block.strongbox.BlockStrongbox;
import cofh.thermalexpansion.block.strongbox.TileStrongbox;
import cofh.thermalexpansion.gui.container.ContainerStrongbox;

import java.util.UUID;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.tileentity.TileEntity;

public class GuiStrongbox extends GuiBaseAdv {

	TileStrongbox myTile;
	UUID playerName;
	int storageIndex;

	public GuiStrongbox(InventoryPlayer inventory, TileEntity tile) {

		super(new ContainerStrongbox(inventory, tile));

		myTile = (TileStrongbox) tile;
		playerName = SecurityHelper.getID(inventory.player);
		storageIndex = myTile.getStorageIndex();
		texture = CoFHProps.TEXTURE_STORAGE[storageIndex];
		name = myTile.getInventoryName();

		xSize = 14 + 18 * MathHelper.clampI(storageIndex + 1, 9, 13);
		ySize = 112 + 18 * MathHelper.clampI(storageIndex, 2, 8);

		if (myTile.type == BlockStrongbox.Types.CREATIVE.ordinal()) {
			myInfo = StringHelper.localize("tab.thermalexpansion.strongbox.creative");
		} else {
			myInfo = StringHelper.localize("tab.thermalexpansion.strongbox.0");

			if (myTile.enchant <= 0) {
				myInfo += "\n\n" + StringHelper.localize("tab.thermalexpansion.storage.enchant");
			}
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
	}

}
