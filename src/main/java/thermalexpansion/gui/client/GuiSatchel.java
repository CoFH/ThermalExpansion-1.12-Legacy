package thermalexpansion.gui.client;

import cofh.api.core.ISecurable;
import cofh.gui.GuiBaseAdv;
import cofh.gui.element.TabInfo;
import cofh.gui.element.TabSecurity;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;

import thermalexpansion.core.TEProps;
import thermalexpansion.gui.container.ContainerSatchel;
import thermalexpansion.item.tool.ItemSatchel;

public class GuiSatchel extends GuiBaseAdv {

	static final ResourceLocation[] TEXTURE = new ResourceLocation[7];

	static {
		for (int i = 0; i < 7; i++) {
			TEXTURE[i] = new ResourceLocation(TEProps.PATH_GUI_SATCHEL + "Satchel" + i + ".png");
		}
	}

	static final String INFO = "It's a satchel. It stores most things.\n\nNo refunds.";
	static final String INFO_ENCHANT = "\n\nCan be enchanted to hold more items!";
	static final String INFO_CREATIVE = "Only holds one thing...kind of.";

	int satchelType;
	boolean enchanted;
	boolean secure;
	String playerName;

	public GuiSatchel(InventoryPlayer inventory, ContainerSatchel container) {

		super(container, TEXTURE[0]);
		playerName = inventory.player.getCommandSenderName();
		satchelType = container.getContainerStack().getItemDamage();

		if (true) {
			// enchant check
		}
		if (true) {
			// secure check
		}
		texture = TEXTURE[satchelType];
		name = container.getInventoryName();
		allowUserInput = false;

		ySize = 112 + Math.max(2, satchelType) * 18;
	}

	@Override
	public void initGui() {

		super.initGui();

		if (satchelType == ItemSatchel.Types.CREATIVE.ordinal()) {
			addTab(new TabInfo(this, INFO_CREATIVE));
		} else {
			addTab(new TabInfo(this, enchanted ? INFO : INFO + INFO_ENCHANT));
		}
		if (ItemSatchel.enableSecurity && secure) {
			addTab(new TabSecurity(this, (ISecurable) inventorySlots, playerName));
		}
	}

	@Override
	protected void updateElementInformation() {

	}

	@Override
	public void updateScreen() {

		super.updateScreen();

		if (secure && !((ISecurable) inventorySlots).canPlayerAccess(playerName)) {
			this.mc.thePlayer.closeScreen();
		}
	}

}
