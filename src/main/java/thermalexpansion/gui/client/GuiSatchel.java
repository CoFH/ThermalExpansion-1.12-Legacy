package thermalexpansion.gui.client;

import cofh.api.tileentity.ISecurable;
import cofh.core.CoFHProps;
import cofh.gui.GuiBaseAdv;
import cofh.gui.element.TabInfo;
import cofh.gui.element.TabSecurity;
import cofh.util.MathHelper;
import cofh.util.SecurityHelper;
import cofh.util.StringHelper;

import net.minecraft.entity.player.InventoryPlayer;

import thermalexpansion.gui.container.ContainerSatchel;
import thermalexpansion.item.ItemSatchel;

public class GuiSatchel extends GuiBaseAdv {

	boolean enchanted;
	boolean secure;
	String playerName;
	int storageIndex;
	String myInfo = "";

	public GuiSatchel(InventoryPlayer inventory, ContainerSatchel container) {

		super(container);

		playerName = inventory.player.getCommandSenderName();
		storageIndex = ItemSatchel.getStorageIndex(container.getContainerStack());
		enchanted = ItemSatchel.isEnchanted(container.getContainerStack());
		secure = SecurityHelper.isSecure(container.getContainerStack());

		texture = CoFHProps.TEXTURE_STORAGE[storageIndex];
		name = container.getInventoryName();
		allowUserInput = false;

		xSize = 14 + 18 * MathHelper.clampI(storageIndex + 1, 9, 13);
		ySize = 112 + 18 * MathHelper.clampI(storageIndex, 2, 8);
	}

	@Override
	public void initGui() {

		super.initGui();

		if (storageIndex == ItemSatchel.Types.CREATIVE.ordinal()) {
			myInfo = StringHelper.localize("tab.thermalexpansion.satchel.creative");
		} else {
			myInfo = StringHelper.localize("tab.thermalexpansion.satchel.0") + "\n\n" + StringHelper.localize("tab.thermalexpansion.satchel.1");

			if (!enchanted) {
				myInfo += "\n\n" + StringHelper.localize("tab.thermalexpansion.storage.enchant");
			}
		}
		addTab(new TabInfo(this, myInfo));
		if (ItemSatchel.enableSecurity && secure) {
			addTab(new TabSecurity(this, (ISecurable) inventorySlots, playerName));
		}
	}

	@Override
	public void updateScreen() {

		super.updateScreen();

		if (secure && !((ISecurable) inventorySlots).canPlayerAccess(playerName)) {
			this.mc.thePlayer.closeScreen();
		}
	}

}
