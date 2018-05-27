package cofh.thermalexpansion.gui.client.device;

import cofh.thermalexpansion.block.device.TileItemCollector;
import cofh.thermalexpansion.gui.container.device.ContainerItemCollector;
import cofh.thermalexpansion.init.TEProps;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

public class GuiItemCollector extends GuiDeviceBase {

	public static final String TEX_PATH = TEProps.PATH_GUI_DEVICE + "item_collector.png";
	public static final ResourceLocation TEXTURE = new ResourceLocation(TEX_PATH);

	private TileItemCollector myTile;

	public GuiItemCollector(InventoryPlayer inventory, TileEntity tile) {

		super(new ContainerItemCollector(inventory, tile), tile, inventory.player, TEXTURE);

		generateInfo("tab.thermalexpansion.device.item_collector");

		myTile = (TileItemCollector) tile;
	}

}
