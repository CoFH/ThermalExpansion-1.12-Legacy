package cofh.thermalexpansion.gui.client.dynamo;

import cofh.core.gui.element.ElementFluidTank;
import cofh.core.util.helpers.StringHelper;
import cofh.thermalexpansion.gui.container.ContainerTEBase;
import cofh.thermalexpansion.init.TEProps;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

public class GuiDynamoCompression extends GuiDynamoBase {

	public static final ResourceLocation TEXTURE = new ResourceLocation(TEProps.PATH_GUI_DYNAMO + "compression.png");

	public GuiDynamoCompression(InventoryPlayer inventory, TileEntity tile) {

		super(new ContainerTEBase(inventory, tile), tile, inventory.player, TEXTURE);

		myInfo = StringHelper.localize("tab.thermalexpansion.dynamo.compression.0");
	}

	@Override
	public void initGui() {

		super.initGui();

		addElement(new ElementFluidTank(this, 8, 9, baseTile.getTank(0)));
		addElement(new ElementFluidTank(this, 152, 9, baseTile.getTank(1)));
	}

}
