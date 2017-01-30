package cofh.thermalexpansion.gui.client.dynamo;

import cofh.lib.gui.element.ElementDualScaled;
import cofh.lib.gui.element.ElementEnergyStored;
import cofh.lib.gui.element.ElementFluidTank;
import cofh.lib.util.helpers.StringHelper;
import cofh.thermalexpansion.gui.container.dynamo.ContainerDynamoReactant;
import cofh.thermalexpansion.init.TEProps;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

public class GuiDynamoReactant extends GuiDynamoBase {

	private static final ResourceLocation TEXTURE = new ResourceLocation(TEProps.PATH_GUI_DYNAMO + "reactant.png");

	private ElementDualScaled duration;

	public GuiDynamoReactant(InventoryPlayer inventory, TileEntity tile) {

		super(new ContainerDynamoReactant(inventory, tile), tile, inventory.player, TEXTURE);

		myInfo = StringHelper.localize("tab.thermalexpansion.dynamo.reactant.0") + "\n\n" + StringHelper.localize("tab.thermalexpansion.dynamo.reactant.1");
	}

	@Override
	public void initGui() {

		super.initGui();

		addElement(new ElementEnergyStored(this, 80, 18, baseTile.getEnergyStorage()));
		addElement(new ElementFluidTank(this, 152, 9, baseTile.getTank(0)));
		duration = (ElementDualScaled) addElement(new ElementDualScaled(this, 115, 35).setSize(16, 16).setTexture(TEX_ALCHEMY, 32, 16));
	}

	@Override
	protected void updateElementInformation() {

		super.updateElementInformation();

		duration.setQuantity(baseTile.getScaledDuration(SPEED));
	}

}
