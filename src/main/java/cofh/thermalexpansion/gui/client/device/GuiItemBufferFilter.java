package cofh.thermalexpansion.gui.client.device;

import cofh.api.core.IFilterable;
import cofh.core.gui.element.ElementButton;
import cofh.core.init.CoreProps;
import cofh.core.util.filter.ItemFilter;
import cofh.thermalexpansion.block.device.TileItemBuffer;
import cofh.thermalexpansion.gui.container.device.ContainerItemBufferFilter;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

public class GuiItemBufferFilter extends GuiDeviceBase {

	public static final String TEX_PATH = CoreProps.PATH_GUI_FILTER + "filter_9.png";
	public static final ResourceLocation TEXTURE = new ResourceLocation(TEX_PATH);

	protected TileItemBuffer myTile;
	protected ItemFilter myFilter;

	private ElementButton buttonList;
	private ElementButton buttonOre;
	private ElementButton buttonMeta;
	private ElementButton buttonNbt;

	public GuiItemBufferFilter(InventoryPlayer inventory, TileEntity tile) {

		super(new ContainerItemBufferFilter(inventory, tile), tile, inventory.player, TEXTURE);

		generateInfo("tab.thermalexpansion.device.item_buffer");

		myTile = (TileItemBuffer) tile;
		myFilter = myTile.getFilter();
		ySize = 202;
	}

	@Override
	public void initGui() {

		super.initGui();

		buttonList = new ElementButton(this, 24, ySize - 118, "FilterList", 176, 0, 176, 20, 20, 20, TEX_PATH);
		buttonOre = new ElementButton(this, 60, ySize - 118, "FilterOre", 216, 0, 216, 20, 20, 20, TEX_PATH);
		buttonMeta = new ElementButton(this, 96, ySize - 118, "FilterMeta", 176, 60, 176, 80, 20, 20, TEX_PATH);
		buttonNbt = new ElementButton(this, 132, ySize - 118, "FilterNbt", 216, 60, 216, 80, 20, 20, TEX_PATH);

		addElement(buttonList);
		addElement(buttonOre);
		addElement(buttonMeta);
		addElement(buttonNbt);
	}

	@Override
	protected void updateElementInformation() {

		int x = myFilter.getFlag(IFilterable.FLAG_WHITELIST) ? 176 : 196;
		buttonList.setSheetX(x);
		buttonList.setHoverX(x);
		buttonList.setToolTip("info.cofh.filter.list." + (myFilter.getFlag(IFilterable.FLAG_WHITELIST) ? "on" : "off"));

		x = myFilter.getFlag(IFilterable.FLAG_ORE_DICT) ? 216 : 236;
		buttonOre.setSheetX(x);
		buttonOre.setHoverX(x);
		buttonOre.setToolTip("info.cofh.filter.oreDict." + (myFilter.getFlag(IFilterable.FLAG_ORE_DICT) ? "on" : "off"));

		x = myFilter.getFlag(IFilterable.FLAG_METADATA) ? 176 : 196;
		buttonMeta.setSheetX(x);
		buttonMeta.setHoverX(x);
		buttonMeta.setToolTip("info.cofh.filter.metadata." + (myFilter.getFlag(IFilterable.FLAG_METADATA) ? "on" : "off"));

		x = myFilter.getFlag(IFilterable.FLAG_NBT) ? 216 : 236;
		buttonNbt.setSheetX(x);
		buttonNbt.setHoverX(x);
		buttonNbt.setToolTip("info.cofh.filter.nbt." + (myFilter.getFlag(IFilterable.FLAG_NBT) ? "on" : "off"));
	}

	@Override
	public void handleElementButtonClick(String buttonName, int mouseButton) {

		ContainerItemBufferFilter container = (ContainerItemBufferFilter) inventorySlots;
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
		playClickSound(myFilter.getFlag(flag) ? 0.5F : 0.8F);
		container.setFlag(flag, !myFilter.getFlag(flag));
	}

}
