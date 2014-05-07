package thermalexpansion.gui.client.dynamo;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import thermalexpansion.block.dynamo.TileDynamoReactant;
import thermalexpansion.core.TEProps;
import thermalexpansion.gui.container.dynamo.ContainerDynamoReactant;
import cofh.core.CoFHProps;
import cofh.gui.GuiBaseAdv;
import cofh.gui.element.ElementDualScaled;
import cofh.gui.element.ElementEnergyStored;
import cofh.gui.element.ElementFluidTank;
import cofh.gui.element.TabEnergy;
import cofh.gui.element.TabInfo;
import cofh.gui.element.TabRedstone;
import cofh.gui.element.TabTutorial;

public class GuiDynamoReactant extends GuiBaseAdv {

	static final ResourceLocation TEXTURE = new ResourceLocation(TEProps.PATH_GUI_DYNAMO + "DynamoReactant.png");
	static final String INFO = "Generates Redstone Flux using alchemical reactions.\n\nA solid reactant and a fluid fuel must be provided.\n\nGeneration rate varies according to energy demand.";

	TileDynamoReactant myTile;

	ElementDualScaled duration;

	public GuiDynamoReactant(InventoryPlayer inventory, TileEntity theTile) {

		super(new ContainerDynamoReactant(inventory, theTile), TEXTURE);
		myTile = (TileDynamoReactant) theTile;
		name = myTile.getInventoryName();
	}

	@Override
	public void initGui() {

		super.initGui();

		addElement(new ElementEnergyStored(this, 80, 18, myTile.getEnergyStorage()));
		addElement(new ElementFluidTank(this, 152, 9, myTile.getTank(0)));
		duration = (ElementDualScaled) addElement(new ElementDualScaled(this, 115, 35).setSize(16, 16).setTexture(TEX_ALCHEMY, 32, 16));

		addTab(new TabEnergy(this, myTile, true));
		addTab(new TabRedstone(this, myTile));
		addTab(new TabInfo(this, INFO));
		addTab(new TabTutorial(this, CoFHProps.tutorialTabRedstone));
	}

	@Override
	protected void updateElements() {

		duration.setQuantity(myTile.getScaledDuration(SPEED));
	}

}
