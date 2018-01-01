package cofh.thermalexpansion.gui.client.storage;

import cofh.api.core.ISecurable;
import cofh.core.gui.GuiContainerCore;
import cofh.core.gui.element.tab.TabInfo;
import cofh.core.gui.element.tab.TabSecurity;
import cofh.core.init.CoreProps;
import cofh.core.util.helpers.MathHelper;
import cofh.core.util.helpers.SecurityHelper;
import cofh.core.util.helpers.StringHelper;
import cofh.thermalexpansion.gui.container.storage.ContainerSatchel;
import cofh.thermalexpansion.item.ItemSatchel;
import net.minecraft.entity.player.InventoryPlayer;

import java.util.UUID;

public class GuiSatchel extends GuiContainerCore {

	boolean isCreative;
	boolean isVoid;

	boolean secure;
	UUID playerName;
	int storageIndex;

	public GuiSatchel(InventoryPlayer inventory, ContainerSatchel container) {

		super(container);

		isCreative = ItemSatchel.isCreative(container.getContainerStack());
		isVoid = ItemSatchel.isVoid(container.getContainerStack());

		secure = SecurityHelper.isSecure(container.getContainerStack());
		playerName = SecurityHelper.getID(inventory.player);
		storageIndex = ItemSatchel.getStorageIndex(container.getContainerStack());
		texture = CoreProps.TEXTURE_STORAGE[storageIndex];
		name = container.getInventoryName();

		allowUserInput = false;

		xSize = 14 + 18 * MathHelper.clamp(storageIndex, 9, 14);
		ySize = 112 + 18 * MathHelper.clamp(storageIndex, 2, 9);

		if (isCreative) {
			generateInfo("tab.thermalexpansion.storage.satchel_c");
		} else if (isVoid) {
			generateInfo("tab.thermalexpansion.storage.satchel_v");
		} else {
			generateInfo("tab.thermalexpansion.storage.satchel");
		}
		if (container.getContainerStack().isItemEnchantable() && !ItemSatchel.hasHoldingEnchant(container.getContainerStack())) {
			myInfo += "\n\n" + StringHelper.localize("tab.thermalexpansion.storage.enchant");
		}
	}

	@Override
	public void initGui() {

		super.initGui();

		if (!myInfo.isEmpty()) {
			addTab(new TabInfo(this, myInfo));
		}
		if (ItemSatchel.enableSecurity && secure) {
			addTab(new TabSecurity(this, (ISecurable) inventorySlots, playerName));
		}
	}

}
