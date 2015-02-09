package cofh.thermalexpansion.gui.client.dynamo;

import cofh.lib.gui.element.ElementFluidTank;
import cofh.lib.util.helpers.StringHelper;
import cofh.thermalexpansion.core.TEProps;
import cofh.thermalexpansion.gui.container.ContainerTEBase;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

public class GuiDynamoMagmatic extends GuiDynamoBase {

	static final ResourceLocation TEXTURE = new ResourceLocation(TEProps.PATH_GUI_DYNAMO + "DynamoMagmatic.png");

	public GuiDynamoMagmatic(InventoryPlayer inventory, TileEntity tile) {

		super(new ContainerTEBase(inventory, tile), tile, inventory.player, TEXTURE);

		myInfo = StringHelper.localize("tab.thermalexpansion.dynamo.magmatic.0");
	}

	@Override
	public void initGui() {

		super.initGui();

		addElement(new ElementFluidTank(this, 152, 9, myTile.getTank(0)));
	}

}
