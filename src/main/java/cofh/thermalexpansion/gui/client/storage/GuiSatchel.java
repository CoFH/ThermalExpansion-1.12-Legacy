package cofh.thermalexpansion.gui.client.storage;

import cofh.api.core.ISecurable;
import cofh.core.gui.GuiCore;
import cofh.core.gui.element.TabInfo;
import cofh.core.gui.element.TabSecurity;
import cofh.core.init.CoreProps;
import cofh.core.util.helpers.SecurityHelper;
import cofh.lib.util.helpers.MathHelper;
import cofh.lib.util.helpers.StringHelper;
import cofh.thermalexpansion.gui.container.storage.ContainerSatchel;
import cofh.thermalexpansion.item.ItemSatchel;
import net.minecraft.entity.player.InventoryPlayer;

import java.util.UUID;

public class GuiSatchel extends GuiCore {

	int level;
	boolean secure;

	UUID playerName;
	int storageIndex;

	public GuiSatchel(InventoryPlayer inventory, ContainerSatchel container) {

		super(container);

		level = ItemSatchel.getLevel(container.getContainerStack());
		secure = SecurityHelper.isSecure(container.getContainerStack());

		playerName = SecurityHelper.getID(inventory.player);
		storageIndex = ItemSatchel.getStorageIndex(container.getContainerStack());
		texture = CoreProps.TEXTURE_STORAGE[storageIndex];
		name = container.getInventoryName();

		allowUserInput = false;

		xSize = 14 + 18 * MathHelper.clamp(storageIndex + 1, 9, 13);
		ySize = 112 + 18 * MathHelper.clamp(storageIndex, 2, 8);

		generateInfo("tab.thermalexpansion.storage.satchel");

		if (!ItemSatchel.hasHoldingEnchant(container.getContainerStack())) {
			myInfo += "\n\n" + StringHelper.localize("tab.thermalexpansion.storage.enchant");
		}
	}

	@Override
	public void initGui() {

		super.initGui();

		addTab(new TabInfo(this, myInfo));
		if (ItemSatchel.enableSecurity && secure) {
			addTab(new TabSecurity(this, (ISecurable) inventorySlots, playerName));
		}
	}

}
