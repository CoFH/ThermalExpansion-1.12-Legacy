package cofh.thermalexpansion.gui.client.dynamo;

import cofh.core.gui.element.ElementDualScaled;
import cofh.core.gui.element.ElementEnergyStored;
import cofh.core.gui.element.ElementFluidTank;
import cofh.thermalexpansion.gui.container.dynamo.ContainerDynamoReactant;
import cofh.thermalexpansion.init.TEProps;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

public class GuiDynamoReactant extends GuiDynamoBase {

	public static final ResourceLocation TEXTURE = new ResourceLocation(TEProps.PATH_GUI_DYNAMO + "reactant.png");

	public GuiDynamoReactant(InventoryPlayer inventory, TileEntity tile) {

		super(new ContainerDynamoReactant(inventory, tile), tile, inventory.player, TEXTURE);

		generateInfo("tab.thermalexpansion.dynamo.reactant");
	}

	@Override
	public void initGui() {

		super.initGui();

		addElement(new ElementEnergyStored(this, 80, 18, baseTile.getEnergyStorage()));
		addElement(new ElementFluidTank(this, 152, 9, baseTile.getTank(0)));
		duration = (ElementDualScaled) addElement(new ElementDualScaled(this, 115, 35).setSize(16, 16).setTexture(TEX_FLAME_GREEN, 32, 16));
	}

}
