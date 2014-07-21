package thermalexpansion.gui.client.device;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

import thermalexpansion.core.TEProps;
import thermalexpansion.gui.client.GuiAugmentableBase;
import thermalexpansion.gui.container.device.ContainerNullifier;

public class GuiNullifier extends GuiAugmentableBase {

	public static final ResourceLocation TEXTURE = new ResourceLocation(TEProps.PATH_GUI_DEVICE + "Nullifier.png");

	public GuiNullifier(InventoryPlayer inventory, TileEntity tile) {

		super(new ContainerNullifier(inventory, tile), tile, inventory.player, TEXTURE);

		generateInfo("tab.thermalexpansion.device.nullifier", 2);
	}

}
