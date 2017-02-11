package cofh.thermalexpansion.gui.client.device;

import cofh.lib.gui.element.ElementBase;
import cofh.lib.gui.element.ElementFluidTank;
import cofh.thermalexpansion.gui.container.ContainerTEBase;
import cofh.thermalexpansion.gui.element.ElementSlotOverlay;
import cofh.thermalexpansion.init.TEProps;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

public class GuiTapper extends GuiDeviceBase {

	public static final ResourceLocation TEXTURE = new ResourceLocation(TEProps.PATH_GUI_DEVICE + "tapper.png");

	ElementBase tankOverlay;

	public GuiTapper(InventoryPlayer inventory, TileEntity tile) {

		super(new ContainerTEBase(inventory, tile), tile, inventory.player, TEXTURE);

		generateInfo("tab.thermalexpansion.device.tapper", 3);
	}

	@Override
	public void initGui() {

		super.initGui();

		tankOverlay = addElement(new ElementSlotOverlay(this, 152, 9).setSlotInfo(3, 3, 2));

		addElement(new ElementFluidTank(this, 152, 9, baseTile.getTank()).setAlwaysShow(true));
	}

	@Override
	protected void updateElementInformation() {

		super.updateElementInformation();

		tankOverlay.setVisible(baseTile.hasSide(1));
	}

}
