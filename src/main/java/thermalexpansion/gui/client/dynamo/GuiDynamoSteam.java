package thermalexpansion.gui.client.dynamo;

import cofh.core.CoFHProps;
import cofh.gui.GuiBaseAdv;
import cofh.gui.element.ElementDualScaled;
import cofh.gui.element.ElementEnergyStored;
import cofh.gui.element.ElementFluidTank;
import cofh.gui.element.TabEnergy;
import cofh.gui.element.TabInfo;
import cofh.gui.element.TabRedstone;
import cofh.gui.element.TabTutorial;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

import thermalexpansion.block.dynamo.TileDynamoSteam;
import thermalexpansion.core.TEProps;
import thermalexpansion.gui.container.dynamo.ContainerDynamoSteam;

public class GuiDynamoSteam extends GuiBaseAdv {

	static final ResourceLocation TEXTURE = new ResourceLocation(TEProps.PATH_GUI_DYNAMO + "DynamoSteam.png");
	static final String INFO = "Generates Redstone Flux using Steam.\n\nSolid Fuels and Water can be used to generate Steam.\n\nGeneration rate varies according to energy demand.";

	TileDynamoSteam myTile;

	ElementDualScaled duration;

	public GuiDynamoSteam(InventoryPlayer inventory, TileEntity theTile) {

		super(new ContainerDynamoSteam(inventory, theTile), TEXTURE);
		myTile = (TileDynamoSteam) theTile;
		name = myTile.getInventoryName();
	}

	@Override
	public void initGui() {

		super.initGui();

		addElement(new ElementEnergyStored(this, 80, 18, myTile.getEnergyStorage()));
		addElement(new ElementFluidTank(this, 8, 9, myTile.getTank(0)));
		addElement(new ElementFluidTank(this, 152, 9, myTile.getTank(1)));
		duration = (ElementDualScaled) addElement(new ElementDualScaled(this, 115, 35).setSize(16, 16).setTexture(TEX_FLAME, 32, 16));

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
