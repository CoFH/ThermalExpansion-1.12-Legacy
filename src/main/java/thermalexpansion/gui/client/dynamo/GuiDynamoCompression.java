package thermalexpansion.gui.client.dynamo;

import cofh.gui.element.ElementFluidTank;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

import thermalexpansion.core.TEProps;
import thermalexpansion.gui.container.ContainerTEBase;

public class GuiDynamoCompression extends GuiDynamoBase {

	static final ResourceLocation TEXTURE = new ResourceLocation(TEProps.PATH_GUI_DYNAMO + "DynamoCompression.png");
	static final String INFO = "Generates Redstone Flux using fluid fuels.\n\nGeneration rate varies according to energy demand.";

	public GuiDynamoCompression(InventoryPlayer inventory, TileEntity tile) {

		super(new ContainerTEBase(inventory, tile), tile, inventory.player, TEXTURE);

		myInfo = INFO;
	}

	@Override
	public void initGui() {

		super.initGui();

		addElement(new ElementFluidTank(this, 8, 9, myTile.getTank(0)));
		addElement(new ElementFluidTank(this, 152, 9, myTile.getTank(1)));
	}

}
