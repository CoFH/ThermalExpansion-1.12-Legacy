package cofh.thermalexpansion.gui.client.device;

import cofh.thermalexpansion.core.TEProps;
import cofh.thermalexpansion.gui.client.GuiAugmentableBase;
import cofh.thermalexpansion.gui.container.device.ContainerNullifier;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

public class GuiNullifier extends GuiAugmentableBase {

	public static final String TEX_PATH = TEProps.PATH_GUI_DEVICE + "Nullifier.png";
	public static final ResourceLocation TEXTURE = new ResourceLocation(TEX_PATH);

	public GuiNullifier(InventoryPlayer inventory, TileEntity tile) {

		super(new ContainerNullifier(inventory, tile), tile, inventory.player, TEXTURE);

		generateInfo("tab.thermalexpansion.device.nullifier", 2);
	}

}
