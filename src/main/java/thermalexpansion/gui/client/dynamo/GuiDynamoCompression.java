package thermalexpansion.gui.client.dynamo;

import cofh.core.CoFHProps;
import cofh.gui.GuiBaseAdv;
import cofh.gui.container.IAugmentableContainer;
import cofh.gui.element.ElementEnergyStored;
import cofh.gui.element.ElementFluidTank;
import cofh.gui.element.TabAugment;
import cofh.gui.element.TabEnergy;
import cofh.gui.element.TabInfo;
import cofh.gui.element.TabRedstone;
import cofh.gui.element.TabTutorial;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

import thermalexpansion.block.dynamo.TileDynamoCompression;
import thermalexpansion.core.TEProps;
import thermalexpansion.gui.container.ContainerTEBase;

public class GuiDynamoCompression extends GuiBaseAdv {

	static final ResourceLocation TEXTURE = new ResourceLocation(TEProps.PATH_GUI_DYNAMO + "DynamoCompression.png");
	static final String INFO = "Generates Redstone Flux using fluid fuels.\n\nGeneration rate varies according to energy demand.";

	TileDynamoCompression myTile;

	public GuiDynamoCompression(InventoryPlayer inventory, TileEntity theTile) {

		super(new ContainerTEBase(inventory, theTile), TEXTURE);
		myTile = (TileDynamoCompression) theTile;
		name = myTile.getInventoryName();
	}

	@Override
	public void initGui() {

		super.initGui();

		addElement(new ElementEnergyStored(this, 80, 18, myTile.getEnergyStorage()));
		addElement(new ElementFluidTank(this, 8, 9, myTile.getTank(0)));
		addElement(new ElementFluidTank(this, 152, 9, myTile.getTank(1)));

		addTab(new TabEnergy(this, myTile, true));
		addTab(new TabRedstone(this, myTile));

		addTab(new TabInfo(this, INFO));
		addTab(new TabTutorial(this, CoFHProps.tutorialTabRedstone));
		addTab(new TabAugment(this, (IAugmentableContainer) inventorySlots));
	}

}
