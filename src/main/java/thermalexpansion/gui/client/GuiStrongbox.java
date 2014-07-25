package thermalexpansion.gui.client;

import cofh.core.CoFHProps;
import cofh.gui.GuiBaseAdv;
import cofh.gui.element.TabInfo;
import cofh.gui.element.TabSecurity;
import cofh.util.MathHelper;
import cofh.util.StringHelper;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.tileentity.TileEntity;

import thermalexpansion.block.strongbox.BlockStrongbox;
import thermalexpansion.block.strongbox.TileStrongbox;
import thermalexpansion.gui.container.ContainerStrongbox;

public class GuiStrongbox extends GuiBaseAdv {

	TileStrongbox myTile;
	String playerName;
	int storageIndex;
	String myInfo = "";

	public GuiStrongbox(InventoryPlayer inventory, TileEntity tile) {

		super(new ContainerStrongbox(inventory, tile));

		myTile = (TileStrongbox) tile;
		playerName = inventory.player.getCommandSenderName();
		storageIndex = myTile.getStorageIndex();
		texture = CoFHProps.TEXTURE_STORAGE[storageIndex];
		name = myTile.getInventoryName();

		xSize = 14 + 18 * MathHelper.clampI(storageIndex + 1, 9, 13);
		ySize = 112 + 18 * MathHelper.clampI(storageIndex, 2, 8);
	}

	@Override
	public void initGui() {

		super.initGui();

		if (myTile.type == BlockStrongbox.Types.CREATIVE.ordinal()) {
			myInfo = StringHelper.localize("tab.thermalexpansion.strongbox.creative");
		} else {
			myInfo = StringHelper.localize("tab.thermalexpansion.strongbox.0");

			if (myTile.enchant <= 0) {
				myInfo += "\n\n" + StringHelper.localize("tab.thermalexpansion.storage.enchant");
			}
		}
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
