package thermalexpansion.gui.client.dynamo;

import cofh.gui.element.ElementDualScaled;
import cofh.gui.element.ElementEnergyStored;
import cofh.gui.element.ElementFluidTank;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

import thermalexpansion.core.TEProps;
import thermalexpansion.gui.container.dynamo.ContainerDynamoReactant;

public class GuiDynamoReactant extends GuiDynamoBase {

	static final ResourceLocation TEXTURE = new ResourceLocation(TEProps.PATH_GUI_DYNAMO + "DynamoReactant.png");
	static final String INFO = "Generates Redstone Flux using alchemical reactions.\n\nA solid reactant and a fluid fuel must be provided.\n\nGeneration rate varies according to energy demand.";

	ElementDualScaled duration;

	public GuiDynamoReactant(InventoryPlayer inventory, TileEntity tile) {

		super(new ContainerDynamoReactant(inventory, tile), tile, inventory.player, TEXTURE);

		myInfo = INFO;
	}

	@Override
	public void initGui() {

		super.initGui();

		addElement(new ElementEnergyStored(this, 80, 18, myTile.getEnergyStorage()));
		addElement(new ElementFluidTank(this, 152, 9, myTile.getTank(0)));
		duration = (ElementDualScaled) addElement(new ElementDualScaled(this, 115, 35).setSize(16, 16).setTexture(TEX_ALCHEMY, 32, 16));
	}

	@Override
	protected void updateElementInformation() {

		super.updateElementInformation();

		duration.setQuantity(myTile.getScaledDuration(SPEED));
	}

}
