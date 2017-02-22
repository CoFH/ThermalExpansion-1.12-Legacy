package cofh.thermalexpansion.gui.client.dynamo;

import cofh.lib.gui.element.ElementDualScaled;
import cofh.lib.gui.element.ElementEnergyStored;
import cofh.thermalexpansion.gui.container.dynamo.ContainerDynamoNumismatic;
import cofh.thermalexpansion.init.TEProps;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

public class GuiDynamoNumismatic extends GuiDynamoBase {

	private static final ResourceLocation TEXTURE = new ResourceLocation(TEProps.PATH_GUI_DYNAMO + "numismatic.png");

	private ElementDualScaled duration;

	public GuiDynamoNumismatic(InventoryPlayer inventory, TileEntity tile) {

		super(new ContainerDynamoNumismatic(inventory, tile), tile, inventory.player, TEXTURE);

		generateInfo("tab.thermalexpansion.dynamo.numismatic");
	}

	@Override
	public void initGui() {

		super.initGui();

		addElement(new ElementEnergyStored(this, 80, 18, baseTile.getEnergyStorage()));
		duration = (ElementDualScaled) addElement(new ElementDualScaled(this, 115, 35).setSize(16, 16).setTexture(TEX_ALCHEMY, 32, 16));
	}

	@Override
	protected void updateElementInformation() {

		super.updateElementInformation();

		duration.setQuantity(baseTile.getScaledDuration(SPEED));
	}

}
