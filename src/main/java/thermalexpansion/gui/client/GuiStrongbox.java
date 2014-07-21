package thermalexpansion.gui.client;

import cofh.core.CoFHProps;
import cofh.gui.GuiBaseAdv;
import cofh.gui.element.TabInfo;
import cofh.gui.element.TabSecurity;
import cofh.util.MathHelper;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.tileentity.TileEntity;

import thermalexpansion.block.strongbox.BlockStrongbox;
import thermalexpansion.block.strongbox.TileStrongbox;
import thermalexpansion.gui.container.ContainerStrongbox;

public class GuiStrongbox extends GuiBaseAdv {

	static final String INFO = "Stores things securely!\n\nWill not store some objects.\n\nWrench while sneaking to dismantle.";
	static final String INFO_ENCHANT = "\n\nCan be enchanted to hold more items!";
	static final String INFO_CREATIVE = "Stores something securely!\n\nAllows you to pull out infinite amounts of the item stored inside.";

	TileStrongbox myTile;
	String playerName;
	int storageIndex;

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
			addTab(new TabInfo(this, INFO_CREATIVE));
		} else {
			addTab(new TabInfo(this, myTile.enchant > 0 ? INFO : INFO + INFO_ENCHANT));
		}
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
