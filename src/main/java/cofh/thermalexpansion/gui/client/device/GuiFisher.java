package cofh.thermalexpansion.gui.client.device;

import cofh.thermalexpansion.gui.container.device.ContainerFisher;
import cofh.thermalexpansion.init.TEProps;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

public class GuiFisher extends GuiDeviceBase {

	public static final ResourceLocation TEXTURE = new ResourceLocation(TEProps.PATH_GUI_DEVICE + "fisher.png");

	public GuiFisher(InventoryPlayer inventory, TileEntity tile) {

		super(new ContainerFisher(inventory, tile), tile, inventory.player, TEXTURE);

		generateInfo("tab.thermalexpansion.device.fisher");
	}

}
