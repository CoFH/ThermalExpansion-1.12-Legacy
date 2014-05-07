package thermalexpansion.gui.client.device;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import thermalexpansion.block.device.TileBreaker;
import thermalexpansion.core.TEProps;
import thermalexpansion.gui.container.ContainerTEBase;
import cofh.core.CoFHProps;
import cofh.gui.GuiBaseAdv;
import cofh.gui.element.TabConfiguration;
import cofh.gui.element.TabInfo;
import cofh.gui.element.TabRedstone;
import cofh.gui.element.TabTutorial;

public class GuiBreaker extends GuiBaseAdv {

	static final String TEXTURE_PATH = TEProps.PATH_GUI_DEVICE + "Breaker.png";
	static final ResourceLocation TEXTURE = new ResourceLocation(TEXTURE_PATH);
	static final String INFO = "Breaks all the things!\n\nKeep face away from front of device.";

	TileBreaker myTile;

	public GuiBreaker(InventoryPlayer inventory, TileEntity theTile) {

		super(new ContainerTEBase(inventory, theTile), TEXTURE);
		myTile = (TileBreaker) theTile;
		name = myTile.getName();
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
