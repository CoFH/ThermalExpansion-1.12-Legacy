package thermalexpansion.gui.client.machine;

import cofh.core.CoFHProps;
import cofh.gui.GuiBaseAdv;
import cofh.gui.element.ElementBase;
import cofh.gui.element.ElementFluidTank;
import cofh.gui.element.TabConfiguration;
import cofh.gui.element.TabInfo;
import cofh.gui.element.TabRedstone;
import cofh.gui.element.TabTutorial;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

import thermalexpansion.block.machine.TileAccumulator;
import thermalexpansion.core.TEProps;
import thermalexpansion.gui.container.ContainerTEBase;
import thermalexpansion.gui.element.ElementSlotOverlay;

public class GuiAccumulator extends GuiBaseAdv {

	static final ResourceLocation TEXTURE = new ResourceLocation(TEProps.PATH_GUI_MACHINE + "Accumulator.png");
	static final String INFO = "Extracts water from its surroundings.\n\nPlace in a pool of water to speed this up.\n\nDon't drown.";

	TileAccumulator myTile;

	ElementBase slotOutput;

	public GuiAccumulator(InventoryPlayer inventory, TileEntity theTile) {

		super(new ContainerTEBase(inventory, theTile), TEXTURE);
		myTile = (TileAccumulator) theTile;
		name = myTile.getInventoryName();
	}

	@Override
	public void initGui() {

		super.initGui();

		slotOutput = addElement(new ElementSlotOverlay(this, 152, 9).setSlotInfo(3, 3, 2));

		addElement(new ElementFluidTank(this, 152, 9, myTile.getTank()));

		addTab(new TabRedstone(this, myTile));
		addTab(new TabConfiguration(this, myTile));
		addTab(new TabInfo(this, INFO));
		addTab(new TabTutorial(this, CoFHProps.tutorialTabRedstone + "\n\n" + CoFHProps.tutorialTabConfiguration));
	}

	@Override
	protected void updateElementInformation() {

		slotOutput.setVisible(myTile.hasSide(1));
	}

}
