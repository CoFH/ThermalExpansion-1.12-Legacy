package cofh.thermalexpansion.gui.client.device;

import cofh.core.gui.element.ElementBase;
import cofh.core.gui.element.ElementDualScaled;
import cofh.core.gui.element.ElementFluidTank;
import cofh.thermalexpansion.block.device.TileXpCollector;
import cofh.thermalexpansion.gui.container.device.ContainerXpCollector;
import cofh.thermalexpansion.gui.element.ElementSlotOverlay;
import cofh.thermalexpansion.gui.element.ElementSlotOverlay.SlotColor;
import cofh.thermalexpansion.gui.element.ElementSlotOverlay.SlotRender;
import cofh.thermalexpansion.gui.element.ElementSlotOverlay.SlotType;
import cofh.thermalexpansion.init.TEProps;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

public class GuiXpCollector extends GuiDeviceBase {

	public static final ResourceLocation TEXTURE = new ResourceLocation(TEProps.PATH_GUI_DEVICE + "xp_collector.png");

	private TileXpCollector myTile;

	private ElementBase slotInput;
	private ElementBase tankOverlay;

	private ElementDualScaled duration;

	public GuiXpCollector(InventoryPlayer inventory, TileEntity tile) {

		super(new ContainerXpCollector(inventory, tile), tile, inventory.player, TEXTURE);

		generateInfo("tab.thermalexpansion.device.xp_collector");

		myTile = (TileXpCollector) tile;
	}

	@Override
	public void initGui() {

		super.initGui();

		slotInput = addElement(new ElementSlotOverlay(this, 35, 35).setSlotInfo(SlotColor.BLUE, SlotType.STANDARD, SlotRender.FULL));
		tankOverlay = addElement(new ElementSlotOverlay(this, 152, 9).setSlotInfo(SlotColor.ORANGE, SlotType.TANK, SlotRender.FULL));

		duration = (ElementDualScaled) addElement(new ElementDualScaled(this, 62, 35).setSize(16, 16).setTexture(TEX_FLAME_GREEN, 32, 16));

		addElement(new ElementFluidTank(this, 152, 9, baseTile.getTank()).setAlwaysShow(true));
	}

	@Override
	protected void updateElementInformation() {

		super.updateElementInformation();

		slotInput.setVisible(baseTile.hasSideType(INPUT_ALL) || baseTile.hasSideType(OMNI));
		tankOverlay.setVisible(baseTile.hasSideType(OUTPUT_ALL));

		duration.setQuantity(baseTile.getScaledSpeed(SPEED));
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int x, int y) {

		if (myTile.getBoostFactor() > 0) {
			fontRenderer.drawString("+" + myTile.getBoostFactor() + "%", 82, 42, 0x404040);
		}
		super.drawGuiContainerForegroundLayer(x, y);
	}

}
