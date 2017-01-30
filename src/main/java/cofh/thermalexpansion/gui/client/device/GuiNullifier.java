package cofh.thermalexpansion.gui.client.device;

import cofh.thermalexpansion.gui.container.device.ContainerNullifier;
import cofh.thermalexpansion.init.TEProps;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

public class GuiNullifier extends GuiDeviceBase {

	public static final ResourceLocation TEXTURE = new ResourceLocation(TEProps.PATH_GUI_DEVICE + "nullifier.png");

	public GuiNullifier(InventoryPlayer inventory, TileEntity tile) {

		super(new ContainerNullifier(inventory, tile), tile, inventory.player, TEXTURE);

		generateInfo("tab.thermalexpansion.device.nullifier", 2);
	}

}
