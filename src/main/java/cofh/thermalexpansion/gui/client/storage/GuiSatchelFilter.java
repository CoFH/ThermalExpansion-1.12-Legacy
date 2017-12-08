package cofh.thermalexpansion.gui.client.storage;

import cofh.api.core.IFilterable;
import cofh.api.core.ISecurable;
import cofh.core.gui.GuiContainerCore;
import cofh.core.gui.element.ElementButton;
import cofh.core.gui.element.tab.TabInfo;
import cofh.core.gui.element.tab.TabSecurity;
import cofh.core.util.helpers.SecurityHelper;
import cofh.thermalexpansion.gui.container.storage.ContainerSatchelFilter;
import cofh.thermalexpansion.init.TEProps;
import cofh.thermalexpansion.item.ItemSatchel;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;

import java.util.UUID;

public class GuiSatchelFilter extends GuiContainerCore {

	static final String TEXTURE_PATH = TEProps.PATH_GUI_STORAGE + "satchel_filter.png";
	static final ResourceLocation TEXTURE = new ResourceLocation(TEXTURE_PATH);

	ElementButton buttonList;
	ElementButton buttonMeta;
	ElementButton buttonNbt;
	ElementButton buttonOre;

	int level;
	boolean secure;

	UUID playerName;

	public GuiSatchelFilter(InventoryPlayer inventory, ContainerSatchelFilter container) {

		super(container);

		level = ItemSatchel.getLevel(container.getFilterStack());
		secure = SecurityHelper.isSecure(container.getFilterStack());

		playerName = SecurityHelper.getID(inventory.player);
		texture = TEXTURE;
		name = container.getInventoryName();

		allowUserInput = false;

		xSize = 176;
		ySize = 214;

		generateInfo("tab.thermalexpansion.storage.satchel_filter");
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
		buttonList = new ElementButton(this, 144, 20, "FilterList", 176, 0, 176, 20, 20, 20, TEXTURE_PATH);
		buttonMeta = new ElementButton(this, 144, 43, "FilterMeta", 176, 60, 176, 80, 20, 20, TEXTURE_PATH);
		buttonNbt = new ElementButton(this, 144, 67, "FilterNbt", 216, 0, 216, 20, 20, 20, TEXTURE_PATH);
		buttonOre = new ElementButton(this, 144, 90, "FilterOre", 216, 60, 216, 80, 20, 20, TEXTURE_PATH);

		addElement(buttonList);
		addElement(buttonMeta);
		addElement(buttonNbt);
		addElement(buttonOre);

		updateButtons();
	}

	@Override
	public void handleElementButtonClick(String buttonName, int mouseButton) {

		ContainerSatchelFilter container = (ContainerSatchelFilter) inventorySlots;
		int flag = 0;
		switch (buttonName) {
			case "FilterList":
				flag = IFilterable.FLAG_BLACKLIST;
				break;
			case "FilterMeta":
				flag = IFilterable.FLAG_META;
				break;
			case "FilterNbt":
				flag = IFilterable.FLAG_NBT;
				break;
			case "FilterOre":
				flag = IFilterable.FLAG_ORE_DICT;
				break;
		}
		playClickSound(container.getFlag(flag) ? 0.5F : 0.8F);
		container.setFlag(flag, !container.getFlag(flag));
		updateButtons();
	}

	private void updateButtons() {

		ContainerSatchelFilter container = (ContainerSatchelFilter) inventorySlots;

		int x = container.getFlag(IFilterable.FLAG_BLACKLIST) ? 196 : 176;
		buttonList.setSheetX(x);
		buttonList.setHoverX(x);

		x = container.getFlag(IFilterable.FLAG_META) ? 176 : 196;
		buttonMeta.setSheetX(x);
		buttonMeta.setHoverX(x);

		x = container.getFlag(IFilterable.FLAG_NBT) ? 216 : 236;
		buttonNbt.setSheetX(x);
		buttonNbt.setHoverX(x);

		x = container.getFlag(IFilterable.FLAG_ORE_DICT) ? 216 : 236;
		buttonOre.setSheetX(x);
		buttonOre.setHoverX(x);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTick, int x, int y) {

		super.drawGuiContainerBackgroundLayer(partialTick, x, y);

		GlStateManager.color(1, 1, 1, 1);
		bindTexture(texture);

		drawSlots();
	}

	private void drawSlots() {

		int x0 = guiLeft + 6;
		int y0 = guiTop + 20;

		for (int i = 0; i <= level; i++) {
			for (int j = 0; j < 7; j++) {
				drawTexturedModalRect(x0 + (18 * j), y0 + (18 * i), 7, 132, 18, 18);
			}
		}
	}

}
