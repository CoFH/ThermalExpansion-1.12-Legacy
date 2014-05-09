package thermalexpansion.gui.client.device;

import cofh.core.CoFHProps;
import cofh.gui.GuiBaseAdv;
import cofh.gui.element.TabConfiguration;
import cofh.gui.element.TabInfo;
import cofh.gui.element.TabRedstone;
import cofh.gui.element.TabTutorial;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

import thermalexpansion.block.device.TileNullifier;
import thermalexpansion.core.TEProps;
import thermalexpansion.gui.container.device.ContainerNullifier;

public class GuiNullifier extends GuiBaseAdv {

	static final String TEXTURE_PATH = TEProps.PATH_GUI_DEVICE + "Nullifier.png";
	static final ResourceLocation TEXTURE = new ResourceLocation(TEXTURE_PATH);
	static final String INFO = "Sends all the things to /dev/null!\n\nIt is a horrible place, even fluid cannot escape.";

	TileNullifier myTile;

	public GuiNullifier(InventoryPlayer inventory, TileEntity theTile) {

		super(new ContainerNullifier(inventory, theTile), TEXTURE);
		myTile = (TileNullifier) theTile;
		name = myTile.getInventoryName();
		ySize = 148;
	}

	@Override
	public void initGui() {

		super.initGui();

		addTab(new TabRedstone(this, myTile));
		addTab(new TabConfiguration(this, myTile));
		addTab(new TabInfo(this, INFO));
		addTab(new TabTutorial(this, CoFHProps.tutorialTabRedstone + "\n\n" + CoFHProps.tutorialTabConfiguration + "\n\n"));
	}

}
