package cofh.thermalexpansion.gui.client.dynamo;

import cofh.core.gui.element.ElementDualScaled;
import cofh.core.gui.element.ElementEnergyStored;
import cofh.thermalexpansion.gui.container.dynamo.ContainerDynamoGourmand;
import cofh.thermalexpansion.init.TEProps;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

public class GuiDynamoGourmand extends GuiDynamoBase {

	public static final ResourceLocation TEXTURE = new ResourceLocation(TEProps.PATH_GUI_DYNAMO + "gourmand.png");

	public GuiDynamoGourmand(InventoryPlayer inventory, TileEntity tile) {

		super(new ContainerDynamoGourmand(inventory, tile), tile, inventory.player, TEXTURE);

		generateInfo("tab.thermalexpansion.dynamo.gourmand");
	}

	@Override
	public void initGui() {

		super.initGui();

		addElement(new ElementEnergyStored(this, 80, 18, baseTile.getEnergyStorage()));
		duration = (ElementDualScaled) addElement(new ElementDualScaled(this, 115, 35).setSize(16, 16).setTexture(TEX_ALCHEMY, 32, 16));
	}

}
