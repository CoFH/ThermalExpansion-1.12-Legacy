package thermalexpansion.gui.client;

import cofh.api.tileentity.ISecurable;
import cofh.core.CoFHProps;
import cofh.gui.GuiBaseAdv;
import cofh.gui.element.TabInfo;
import cofh.gui.element.TabSecurity;
import cofh.util.MathHelper;
import cofh.util.SecurityHelper;

import net.minecraft.entity.player.InventoryPlayer;

import thermalexpansion.gui.container.ContainerSatchel;
import thermalexpansion.item.ItemSatchel;

public class GuiSatchel extends GuiBaseAdv {

	static final String INFO = "It's a satchel. Will not store some objects.\n\nNo refunds.";
	static final String INFO_ENCHANT = "\n\nCan be enchanted to hold more items!";
	static final String INFO_CREATIVE = "Only holds one thing...kind of.";

	boolean enchanted;
	boolean secure;
	String playerName;
	int storageIndex;

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
			addTab(new TabInfo(this, INFO_CREATIVE));
		} else {
			addTab(new TabInfo(this, enchanted ? INFO : INFO + INFO_ENCHANT));
		}
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
