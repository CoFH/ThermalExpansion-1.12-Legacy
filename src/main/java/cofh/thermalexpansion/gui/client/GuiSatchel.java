package cofh.thermalexpansion.gui.client;

import cofh.api.tileentity.ISecurable;
import cofh.core.CoFHProps;
import cofh.core.gui.GuiBaseAdv;
import cofh.core.gui.element.TabInfo;
import cofh.core.gui.element.TabSecurity;
import cofh.lib.util.helpers.MathHelper;
import cofh.lib.util.helpers.SecurityHelper;
import cofh.lib.util.helpers.StringHelper;
import cofh.thermalexpansion.gui.container.ContainerSatchel;
import cofh.thermalexpansion.item.ItemSatchel;
import net.minecraft.entity.player.InventoryPlayer;

import java.util.UUID;

public class GuiSatchel extends GuiBaseAdv {

	boolean enchanted;
	boolean secure;
	UUID playerName;
	int storageIndex;

	public GuiSatchel(InventoryPlayer inventory, ContainerSatchel container) {

		super(container);

		playerName = SecurityHelper.getID(inventory.player);
		storageIndex = ItemSatchel.getStorageIndex(container.getContainerStack());
		enchanted = ItemSatchel.isEnchanted(container.getContainerStack());
		secure = SecurityHelper.isSecure(container.getContainerStack());

		texture = CoFHProps.TEXTURE_STORAGE[storageIndex];
		name = container.getInventoryName();
		allowUserInput = false;

		xSize = 14 + 18 * MathHelper.clamp(storageIndex + 1, 9, 13);
		ySize = 112 + 18 * MathHelper.clamp(storageIndex, 2, 8);

		if (storageIndex == ItemSatchel.Types.CREATIVE.ordinal()) {
			myInfo = StringHelper.localize("tab.thermalexpansion.satchel.creative");
		} else {
			myInfo = StringHelper.localize("tab.thermalexpansion.satchel.0") + "\n\n" + StringHelper.localize("tab.thermalexpansion.satchel.1");

			if (!enchanted) {
				myInfo += "\n\n" + StringHelper.localize("tab.thermalexpansion.storage.enchant");
			}
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
