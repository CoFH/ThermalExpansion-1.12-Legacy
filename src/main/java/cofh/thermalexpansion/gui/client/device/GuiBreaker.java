package cofh.thermalexpansion.gui.client.device;

import cofh.thermalexpansion.core.TEProps;
import cofh.thermalexpansion.gui.client.GuiAugmentableBase;
import cofh.thermalexpansion.gui.container.ContainerTEBase;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

public class GuiBreaker extends GuiAugmentableBase {

	public static final ResourceLocation TEXTURE = new ResourceLocation(TEProps.PATH_GUI_DEVICE + "Breaker.png");

	public GuiBreaker(InventoryPlayer inventory, TileEntity tile) {

		super(new ContainerTEBase(inventory, tile), tile, inventory.player, TEXTURE);

		generateInfo("tab.thermalexpansion.device.breaker", 2);
	}

}
