package cofh.thermalexpansion.gui.client.storage;

import cofh.core.gui.GuiContainerCore;
import cofh.core.gui.element.ElementButton;
import cofh.core.gui.element.tab.TabBase;
import cofh.core.gui.element.tab.TabInfo;
import cofh.core.gui.element.tab.TabSecurity;
import cofh.core.util.helpers.SecurityHelper;
import cofh.core.util.helpers.StringHelper;
import cofh.thermalexpansion.block.storage.TileCache;
import cofh.thermalexpansion.gui.container.storage.ContainerCache;
import cofh.thermalexpansion.init.TEProps;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

import java.util.UUID;

public class GuiCache extends GuiContainerCore {

	public static final String TEX_PATH = TEProps.PATH_GUI_STORAGE + "cache.png";
	public static final ResourceLocation TEXTURE = new ResourceLocation(TEX_PATH);

	protected TileCache baseTile;
	protected UUID playerName;

	// protected TabBase redstoneTab;
	protected TabBase securityTab;

	private ElementButton lock;
	private ElementButton decMax;
	private ElementButton incMax;

	public GuiCache(InventoryPlayer inventory, TileEntity tile) {

		super(new ContainerCache(inventory, tile), TEXTURE);

		baseTile = (TileCache) tile;
		name = baseTile.getName();
		playerName = SecurityHelper.getID(inventory.player);

		generateInfo("tab.thermalexpansion.storage.cache");
	}

	@Override
	public void initGui() {

		super.initGui();

		// Right Side
		// redstoneTab = addTab(new TabRedstoneControl(this, baseTile));

		// Left Side
		securityTab = addTab(new TabSecurity(this, baseTile, playerName));
		securityTab.setVisible(baseTile.enableSecurity() && baseTile.isSecured());

		if (!myInfo.isEmpty()) {
			addTab(new TabInfo(this, myInfo));
		}
		lock = new ElementButton(this, 44, 54, "Lock", 176, 48, 176, 64, 176, 80, 16, 16, TEX_PATH).setToolTipLocalized(true);
		decMax = new ElementButton(this, 109, 56, "DecMax", 176, 0, 176, 14, 176, 28, 14, 14, TEX_PATH).setToolTipLocalized(true);
		incMax = new ElementButton(this, 125, 56, "IncMax", 190, 0, 190, 14, 190, 28, 14, 14, TEX_PATH).setToolTipLocalized(true);

		addElement(lock);
		addElement(decMax);
		addElement(incMax);
	}

	@Override
	public void updateScreen() {

		super.updateScreen();

		if (!baseTile.canAccess()) {
			this.mc.player.closeScreen();
		}
		securityTab.setVisible(baseTile.enableSecurity() && baseTile.isSecured());
	}

	@Override
	protected void updateElementInformation() {

		super.updateElementInformation();

		if (baseTile.getStoredInstance().isEmpty()) {
			lock.setDisabled();
		} else {
			lock.setActive();
		}
		if (baseTile.isLocked()) {
			lock.setToolTip(StringHelper.localize("info.cofh.locked"));
			lock.setSheetX(176);
			lock.setHoverX(176);
		} else {
			lock.setToolTip(StringHelper.localize("info.cofh.unlocked"));
			lock.setSheetX(192);
			lock.setHoverX(192);
		}
	}

	@Override
	public void handleElementButtonClick(String buttonName, int mouseButton) {

		if (buttonName.equalsIgnoreCase("Lock")) {
			baseTile.toggleLock();
			playClickSound(baseTile.isLocked() ? 0.8F : 0.4F);

			baseTile.sendModePacket();
			baseTile.toggleLock();
		}
	}

}
