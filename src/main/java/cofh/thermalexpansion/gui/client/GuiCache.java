package cofh.thermalexpansion.gui.client;

import cofh.core.gui.GuiBaseAdv;
import cofh.core.gui.element.TabAugment;
import cofh.core.gui.element.TabInfo;
import cofh.core.gui.element.TabSecurity;
import cofh.core.gui.element.TabTutorial;
import cofh.lib.gui.container.IAugmentableContainer;
import cofh.lib.util.helpers.SecurityHelper;
import cofh.lib.util.helpers.StringHelper;
import cofh.thermalexpansion.block.plate.TilePlateImpulse;
import cofh.thermalexpansion.core.TEProps;
import cofh.thermalexpansion.gui.container.ContainerTEBase;

import java.util.UUID;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

public class GuiCache extends GuiBaseAdv {

	static final String TEX_PATH = TEProps.PATH_GUI + "Cache.png";
	static final ResourceLocation TEXTURE = new ResourceLocation(TEX_PATH);

	TilePlateImpulse myTile;
	UUID playerName;

	public GuiCache(InventoryPlayer inventory, TileEntity theTile) {

		super(new ContainerTEBase(inventory, theTile, false, false), TEXTURE);
		myTile = (TilePlateImpulse) theTile;
		name = myTile.getName();
		playerName = SecurityHelper.getID(inventory.player);
		drawInventory = false;
		this.height = 100;

		// generateInfo("tab.thermalexpansion.cache", 2);
	}

	@Override
	public void initGui() {

		super.initGui();

		if (!myInfo.isEmpty()) {
			addTab(new TabInfo(this, myInfo));
		}
		addTab(new TabAugment(this, (IAugmentableContainer) inventorySlots));
		if (myTile.enableSecurity() && myTile.isSecured()) {
			addTab(new TabSecurity(this, myTile, playerName));
		}
		String myTutorial = StringHelper.tutorialTabAugment();
		if (myTile.enableSecurity() && myTile.isSecured()) {
			myTutorial += "\n\n" + StringHelper.tutorialTabSecurity();
		}
		// redstoneTab = addTab(new TabRedstone(this, myTile));
		// if (myTile.augmentRedstoneControl) {
		// myTutorial += "\n\n" + StringHelper.tutorialTabRedstone();
		// }
		// if (myTile.augmentReconfigSides) {
		// myTutorial += "\n\n" + StringHelper.tutorialTabConfiguration();
		// }
		addTab(new TabTutorial(this, myTutorial));
	}

	@Override
	protected void updateElementInformation() {

	}

	@Override
	public void updateScreen() {

		super.updateScreen();

		if (!myTile.canAccess()) {
			this.mc.thePlayer.closeScreen();
		}
	}

}
