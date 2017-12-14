package cofh.thermalexpansion.gui.client.storage;

import cofh.api.core.IFilterable;
import cofh.api.core.ISecurable;
import cofh.core.gui.GuiContainerCore;
import cofh.core.gui.element.ElementButton;
import cofh.core.gui.element.tab.TabInfo;
import cofh.core.gui.element.tab.TabSecurity;
import cofh.core.init.CoreProps;
import cofh.core.util.helpers.SecurityHelper;
import cofh.thermalexpansion.gui.container.storage.ContainerSatchelFilter;
import cofh.thermalexpansion.item.ItemSatchel;
import net.minecraft.entity.player.InventoryPlayer;

import java.util.UUID;

public class GuiSatchelFilter extends GuiContainerCore {

	String texturePath;

	int level;
	boolean secure;

	UUID playerName;
	int filterIndex;

	private ElementButton buttonList;
	private ElementButton buttonOre;
	private ElementButton buttonMeta;
	private ElementButton buttonNbt;

	public GuiSatchelFilter(InventoryPlayer inventory, ContainerSatchelFilter container) {

		super(container);

		level = ItemSatchel.getLevel(container.getFilterStack());
		secure = SecurityHelper.isSecure(container.getFilterStack());

		playerName = SecurityHelper.getID(inventory.player);
		filterIndex = ItemSatchel.getLevel(container.getFilterStack());
		texture = CoreProps.TEXTURE_FILTER[filterIndex];
		texturePath = texture.toString();
		name = container.getInventoryName();

		allowUserInput = false;

		xSize = 176;
		ySize = filterIndex > 1 ? 202 : 184;

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
		buttonList = new ElementButton(this, 24, ySize - 118, "FilterList", 176, 0, 176, 20, 20, 20, texturePath);
		buttonOre = new ElementButton(this, 60, ySize - 118, "FilterOre", 216, 0, 216, 20, 20, 20, texturePath);
		buttonMeta = new ElementButton(this, 96, ySize - 118, "FilterMeta", 176, 60, 176, 80, 20, 20, texturePath);
		buttonNbt = new ElementButton(this, 132, ySize - 118, "FilterNbt", 216, 60, 216, 80, 20, 20, texturePath);

		addElement(buttonList);
		addElement(buttonOre);
		addElement(buttonMeta);
		addElement(buttonNbt);

		updateButtons();
	}

	@Override
	public void handleElementButtonClick(String buttonName, int mouseButton) {

		ContainerSatchelFilter container = (ContainerSatchelFilter) inventorySlots;
		int flag = 0;
		switch (buttonName) {
			case "FilterList":
				flag = IFilterable.FLAG_WHITELIST;
				break;
			case "FilterOre":
				flag = IFilterable.FLAG_ORE_DICT;
				break;
			case "FilterMeta":
				flag = IFilterable.FLAG_METADATA;
				break;
			case "FilterNbt":
				flag = IFilterable.FLAG_NBT;
				break;
		}
		playClickSound(container.getFlag(flag) ? 0.5F : 0.8F);
		container.setFlag(flag, !container.getFlag(flag));
		updateButtons();
	}

	private void updateButtons() {

		ContainerSatchelFilter container = (ContainerSatchelFilter) inventorySlots;

		int x = container.getFlag(IFilterable.FLAG_WHITELIST) ? 176 : 196;
		buttonList.setSheetX(x);
		buttonList.setHoverX(x);
		buttonList.setToolTip("info.cofh.filter.list." + (container.getFlag(IFilterable.FLAG_WHITELIST) ? "on" : "off"));

		x = container.getFlag(IFilterable.FLAG_ORE_DICT) ? 216 : 236;
		buttonOre.setSheetX(x);
		buttonOre.setHoverX(x);
		buttonOre.setToolTip("info.cofh.filter.oreDict." + (container.getFlag(IFilterable.FLAG_ORE_DICT) ? "on" : "off"));

		x = container.getFlag(IFilterable.FLAG_METADATA) ? 176 : 196;
		buttonMeta.setSheetX(x);
		buttonMeta.setHoverX(x);
		buttonMeta.setToolTip("info.cofh.filter.metadata." + (container.getFlag(IFilterable.FLAG_METADATA) ? "on" : "off"));

		x = container.getFlag(IFilterable.FLAG_NBT) ? 216 : 236;
		buttonNbt.setSheetX(x);
		buttonNbt.setHoverX(x);
		buttonNbt.setToolTip("info.cofh.filter.nbt." + (container.getFlag(IFilterable.FLAG_NBT) ? "on" : "off"));
	}

}
