package cofh.thermalexpansion.gui.client.storage;

import cofh.core.gui.GuiContainerCore;
import cofh.core.gui.element.tab.TabBase;
import cofh.core.gui.element.tab.TabInfo;
import cofh.core.gui.element.tab.TabSecurity;
import cofh.core.init.CoreProps;
import cofh.core.util.helpers.MathHelper;
import cofh.core.util.helpers.SecurityHelper;
import cofh.core.util.helpers.StringHelper;
import cofh.thermalexpansion.block.storage.TileStrongbox;
import cofh.thermalexpansion.gui.container.storage.ContainerStrongbox;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.common.Optional;
import vazkii.quark.api.IChestButtonCallback;

import java.util.UUID;

@Optional.Interface (iface = "vazkii.quark.api.IChestButtonCallback", modid = "quark")
public class GuiStrongbox extends GuiContainerCore implements IChestButtonCallback {

	protected TileStrongbox baseTile;
	protected UUID playerName;
	protected int storageIndex;

	protected TabBase securityTab;

	public GuiStrongbox(InventoryPlayer inventory, TileEntity tile) {

		super(new ContainerStrongbox(inventory, tile));

		baseTile = (TileStrongbox) tile;
		playerName = SecurityHelper.getID(inventory.player);
		storageIndex = baseTile.getStorageIndex();
		texture = CoreProps.TEXTURE_STORAGE[storageIndex];
		name = baseTile.getName();

		xSize = 14 + 18 * MathHelper.clamp(storageIndex, 9, 14);
		ySize = 112 + 18 * MathHelper.clamp(storageIndex, 2, 9);

		generateInfo("tab.thermalexpansion.storage.strongbox");

		if (!baseTile.isCreative && baseTile.enchantHolding <= 0) {
			myInfo += "\n\n" + StringHelper.localize("tab.thermalexpansion.storage.enchant");
		}
	}

	@Override
	public void initGui() {

		super.initGui();

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
		securityTab.setVisible(baseTile.enableSecurity() && baseTile.isSecured());
	}

	/* IChestButtonCallback */
	@Override
	public boolean onAddChestButton(GuiButton button, int buttonType) {

		button.x += xSize + 20;
		return true;
	}

}
