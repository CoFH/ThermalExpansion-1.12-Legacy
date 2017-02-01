package cofh.thermalexpansion.gui.client.automaton;

import cofh.thermalexpansion.gui.client.GuiPoweredBase;
import cofh.thermalexpansion.gui.container.ContainerTEBase;
import cofh.thermalexpansion.init.TEProps;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

public class GuiCollector extends GuiPoweredBase {

	public static final String TEX_PATH = TEProps.PATH_GUI_AUTOMATON + "breaker.png";
	public static final ResourceLocation TEXTURE = new ResourceLocation(TEX_PATH);

	public GuiCollector(InventoryPlayer inventory, TileEntity tile) {

		super(new ContainerTEBase(inventory, tile), tile, inventory.player, TEXTURE);

		generateInfo("tab.thermalexpansion.automaton.collector", 3);
	}

}
