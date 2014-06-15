package thermalexpansion.gui.client.dynamo;

import cofh.core.CoFHProps;
import cofh.gui.GuiBaseAdv;
import cofh.gui.container.IAugmentableContainer;
import cofh.gui.element.ElementDualScaled;
import cofh.gui.element.ElementEnergyStored;
import cofh.gui.element.TabAugment;
import cofh.gui.element.TabEnergy;
import cofh.gui.element.TabInfo;
import cofh.gui.element.TabRedstone;
import cofh.gui.element.TabTutorial;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

import thermalexpansion.block.dynamo.TileDynamoEnervation;
import thermalexpansion.core.TEProps;
import thermalexpansion.gui.container.dynamo.ContainerDynamoEnervation;

public class GuiDynamoEnervation extends GuiBaseAdv {

	static final ResourceLocation TEXTURE = new ResourceLocation(TEProps.PATH_GUI_DYNAMO + "DynamoEnervation.png");
	static final String INFO = "Extracts Redstone Flux directly from natural sources or from objects which store it.\n\nGeneration rate varies according to energy demand.";

	TileDynamoEnervation myTile;

	ElementDualScaled duration;

	public GuiDynamoEnervation(InventoryPlayer inventory, TileEntity theTile) {

		super(new ContainerDynamoEnervation(inventory, theTile), TEXTURE);
		myTile = (TileDynamoEnervation) theTile;
		name = myTile.getInventoryName();
	}

	@Override
	public void initGui() {

		super.initGui();

		addElement(new ElementEnergyStored(this, 80, 18, myTile.getEnergyStorage()));
		duration = (ElementDualScaled) addElement(new ElementDualScaled(this, 115, 35).setSize(16, 16).setTexture(TEX_FLAME, 32, 16));

		addTab(new TabEnergy(this, myTile, true));
		addTab(new TabRedstone(this, myTile));

		addTab(new TabInfo(this, INFO, 1));
		addTab(new TabTutorial(this, CoFHProps.tutorialTabRedstone));
		addTab(new TabAugment(this, (IAugmentableContainer) inventorySlots));
	}

	@Override
	protected void updateElementInformation() {

		duration.setQuantity(myTile.getScaledDuration(SPEED));
	}

}
