package cofh.thermalexpansion.gui.client.device;

import cofh.lib.gui.element.ElementEnergyStored;
import cofh.thermalexpansion.block.device.TileLexicon;
import cofh.thermalexpansion.core.TEProps;
import cofh.thermalexpansion.gui.client.GuiAugmentableBase;
import cofh.thermalexpansion.gui.container.device.ContainerLexicon;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

public class GuiLexicon extends GuiAugmentableBase {

	public static final ResourceLocation TEXTURE = new ResourceLocation(TEProps.PATH_GUI_DEVICE + "Lexicon.png");

	TileLexicon myTile;

	public GuiLexicon(InventoryPlayer inventory, TileEntity tile) {

		super(new ContainerLexicon(inventory, tile), tile, inventory.player, TEXTURE);

		generateInfo("tab.thermalexpansion.device.lexicon", 3);

		myTile = (TileLexicon) tile;
		ySize = 197;
	}

	@Override
	public void initGui() {

		super.initGui();

		addElement(new ElementEnergyStored(this, 8, 8, myTile.getEnergyStorage()));
	}

}
