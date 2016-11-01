package cofh.thermalexpansion.gui.client.dynamo;

import cofh.lib.gui.element.ElementDualScaled;
import cofh.lib.gui.element.ElementEnergyStored;
import cofh.lib.util.helpers.StringHelper;
import cofh.thermalexpansion.core.TEProps;
import cofh.thermalexpansion.gui.container.dynamo.ContainerDynamoEnervation;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

public class GuiDynamoEnervation extends GuiDynamoBase {

	static final ResourceLocation TEXTURE = new ResourceLocation(TEProps.PATH_GUI_DYNAMO + "dynamo_enervation.png");

	ElementDualScaled duration;

	public GuiDynamoEnervation(InventoryPlayer inventory, TileEntity tile) {

		super(new ContainerDynamoEnervation(inventory, tile), tile, inventory.player, TEXTURE);

		myInfo = StringHelper.localize("tab.thermalexpansion.dynamo.enervation.0");
	}

	@Override
	public void initGui() {

		super.initGui();

		addElement(new ElementEnergyStored(this, 80, 18, myTile.getEnergyStorage()));
		duration = (ElementDualScaled) addElement(new ElementDualScaled(this, 115, 35).setSize(16, 16).setTexture(TEX_FLUX, 32, 16));
	}

	@Override
	protected void updateElementInformation() {

		super.updateElementInformation();

		duration.setQuantity(myTile.getScaledDuration(SPEED));
	}

}
