package cofh.thermalexpansion.gui.client.device;

import cofh.lib.gui.element.ElementBase;
import cofh.lib.gui.element.ElementDualScaled;
import cofh.thermalexpansion.block.device.TileFisher;
import cofh.thermalexpansion.gui.container.device.ContainerFisher;
import cofh.thermalexpansion.gui.element.ElementSlotOverlay;
import cofh.thermalexpansion.gui.element.ElementSlotOverlay.SlotColor;
import cofh.thermalexpansion.gui.element.ElementSlotOverlay.SlotRender;
import cofh.thermalexpansion.gui.element.ElementSlotOverlay.SlotType;
import cofh.thermalexpansion.gui.element.ElementSlotOverlayQuad;
import cofh.thermalexpansion.init.TEProps;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

public class GuiFisher extends GuiDeviceBase {

	public static final ResourceLocation TEXTURE = new ResourceLocation(TEProps.PATH_GUI_DEVICE + "fisher.png");

	private TileFisher myTile;

	private ElementBase slotInput;
	private ElementSlotOverlayQuad slotOutput;

	private ElementDualScaled duration;

	public GuiFisher(InventoryPlayer inventory, TileEntity tile) {

		super(new ContainerFisher(inventory, tile), tile, inventory.player, TEXTURE);

		generateInfo("tab.thermalexpansion.device.fisher");

		myTile = (TileFisher) tile;
	}

	@Override
	public void initGui() {

		super.initGui();

		slotInput = addElement(new ElementSlotOverlay(this, 35, 35).setSlotInfo(SlotColor.BLUE, SlotType.STANDARD, SlotRender.FULL));
		slotOutput = (ElementSlotOverlayQuad) addElement(new ElementSlotOverlayQuad(this, 107, 26).setSlotInfo(SlotColor.ORANGE, SlotRender.FULL));

		duration = (ElementDualScaled) addElement(new ElementDualScaled(this, 62, 35).setSize(16, 16).setTexture(TEX_ALCHEMY, 32, 16));
	}

	@Override
	protected void updateElementInformation() {

		super.updateElementInformation();

		slotInput.setVisible(baseTile.hasSideType(INPUT_ALL) || baseTile.hasSideType(OMNI));
		slotOutput.setVisible(baseTile.hasSideType(OUTPUT_ALL) || baseTile.hasSideType(OMNI));

		duration.setQuantity(baseTile.getScaledSpeed(SPEED));
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int x, int y) {

		if (myTile.getBoostMult() > 0) {
			fontRenderer.drawString("x" + myTile.getBoostMult(), 82, 42, 0x404040);
		}
		super.drawGuiContainerForegroundLayer(x, y);
	}

}
