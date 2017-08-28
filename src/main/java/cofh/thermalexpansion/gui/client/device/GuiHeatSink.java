package cofh.thermalexpansion.gui.client.device;

import cofh.core.gui.element.ElementBase;
import cofh.core.gui.element.ElementDualScaled;
import cofh.core.gui.element.ElementFluidTank;
import cofh.thermalexpansion.block.device.TileHeatSink;
import cofh.thermalexpansion.block.device.TileTapper;
import cofh.thermalexpansion.gui.container.ContainerTEBase;
import cofh.thermalexpansion.gui.element.ElementSlotOverlay;
import cofh.thermalexpansion.gui.element.ElementSlotOverlay.SlotColor;
import cofh.thermalexpansion.gui.element.ElementSlotOverlay.SlotRender;
import cofh.thermalexpansion.gui.element.ElementSlotOverlay.SlotType;
import cofh.thermalexpansion.init.TEProps;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

public class GuiHeatSink extends GuiDeviceBase {

	public static final ResourceLocation TEXTURE = new ResourceLocation(TEProps.PATH_GUI_DEVICE + "heat_sink.png");

	private TileHeatSink myTile;

	private ElementBase tankOverlay;

	private ElementDualScaled duration;

	public GuiHeatSink(InventoryPlayer inventory, TileEntity tile) {

		super(new ContainerTEBase(inventory, tile), tile, inventory.player, TEXTURE);

		generateInfo("tab.thermalexpansion.device.heat_sink");

		myTile = (TileHeatSink) tile;
	}

	@Override
	public void initGui() {

		super.initGui();

		tankOverlay = addElement(new ElementSlotOverlay(this, 152, 9).setSlotInfo(SlotColor.BLUE, SlotType.TANK, SlotRender.FULL));

		duration = (ElementDualScaled) addElement(new ElementDualScaled(this, 44, 35).setSize(16, 16).setTexture(TEX_SNOWFLAKE, 32, 16));

		addElement(new ElementFluidTank(this, 152, 9, baseTile.getTank()).setAlwaysShow(true));
	}

	@Override
	protected void updateElementInformation() {

		super.updateElementInformation();

		tankOverlay.setVisible(baseTile.hasSideType(INPUT_ALL));

		duration.setQuantity(baseTile.getScaledSpeed(SPEED));
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int x, int y) {

		if (myTile.getCoolantRF() > 0) {
			fontRenderer.drawString("+" + myTile.getCoolantFactor() + "%", 64, 42, 0x404040);
		}
		super.drawGuiContainerForegroundLayer(x, y);
	}

}
