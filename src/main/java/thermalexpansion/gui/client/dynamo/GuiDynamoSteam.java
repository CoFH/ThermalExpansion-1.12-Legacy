package thermalexpansion.gui.client.dynamo;

import cofh.gui.element.ElementDualScaled;
import cofh.gui.element.ElementFluidTank;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

import thermalexpansion.core.TEProps;
import thermalexpansion.gui.container.dynamo.ContainerDynamoSteam;

public class GuiDynamoSteam extends GuiDynamoBase {

	static final ResourceLocation TEXTURE = new ResourceLocation(TEProps.PATH_GUI_DYNAMO + "DynamoSteam.png");
	static final String INFO = "Generates Redstone Flux using Steam.\n\nSolid Fuels and Water can be used to generate Steam.\n\nGeneration rate varies according to energy demand.";

	ElementDualScaled duration;

	public GuiDynamoSteam(InventoryPlayer inventory, TileEntity tile) {

		super(new ContainerDynamoSteam(inventory, tile), tile, inventory.player, TEXTURE);

		myInfo = INFO;
	}

	@Override
	public void initGui() {

		super.initGui();

		addElement(new ElementFluidTank(this, 8, 9, myTile.getTank(0)));
		addElement(new ElementFluidTank(this, 152, 9, myTile.getTank(1)));
		duration = (ElementDualScaled) addElement(new ElementDualScaled(this, 115, 35).setSize(16, 16).setTexture(TEX_FLAME, 32, 16));
	}

	@Override
	protected void updateElementInformation() {

		super.updateElementInformation();

		duration.setQuantity(myTile.getScaledDuration(SPEED));
	}

}
