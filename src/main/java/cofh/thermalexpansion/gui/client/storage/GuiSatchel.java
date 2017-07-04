package cofh.thermalexpansion.gui.client.storage;

import cofh.api.core.ISecurable;
import cofh.core.gui.GuiCore;
import cofh.core.gui.element.tab.TabInfo;
import cofh.core.gui.element.tab.TabSecurity;
import cofh.core.init.CoreProps;
import cofh.core.util.helpers.MathHelper;
import cofh.core.util.helpers.SecurityHelper;
import cofh.core.util.helpers.StringHelper;
import cofh.thermalexpansion.gui.container.storage.ContainerSatchel;
import cofh.thermalexpansion.item.ItemSatchel;
import net.minecraft.client.renderer.GlStateManager;
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

		xSize = 14 + 18 * MathHelper.clamp(storageIndex, 9, 14);
		ySize = 112 + 18 * MathHelper.clamp(storageIndex, 2, 9);

		generateInfo("tab.thermalexpansion.storage.satchel");

		if (container.getContainerStack().isItemEnchantable() && !ItemSatchel.hasHoldingEnchant(container.getContainerStack())) {
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

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTick, int x, int y) {

		GlStateManager.color(1, 1, 1, 1);
		bindTexture(texture);

		if (xSize > 256 || ySize > 256) {
			drawSizedTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize, 512, 512);
		} else {
			drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
		}
		mouseX = x - guiLeft;
		mouseY = y - guiTop;

		GlStateManager.pushMatrix();
		GlStateManager.translate(guiLeft, guiTop, 0.0F);
		drawElements(partialTick, false);
		drawTabs(partialTick, false);
		GlStateManager.popMatrix();
	}

}
