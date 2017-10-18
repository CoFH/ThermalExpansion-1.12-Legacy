package cofh.thermalexpansion.gui.client.device;

import cofh.core.gui.element.ElementDualScaled;
import cofh.core.gui.element.ElementFluidTank;
import cofh.core.util.helpers.StringHelper;
import cofh.thermalexpansion.block.device.TileDiffuser;
import cofh.thermalexpansion.gui.container.device.ContainerDiffuser;
import cofh.thermalexpansion.gui.element.ElementSlotOverlay;
import cofh.thermalexpansion.gui.element.ElementSlotOverlay.SlotColor;
import cofh.thermalexpansion.gui.element.ElementSlotOverlay.SlotRender;
import cofh.thermalexpansion.gui.element.ElementSlotOverlay.SlotType;
import cofh.thermalexpansion.init.TEProps;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

public class GuiDiffuser extends GuiDeviceBase {

	public static final ResourceLocation TEXTURE = new ResourceLocation(TEProps.PATH_GUI_DEVICE + "diffuser.png");

	private TileDiffuser myTile;

	private ElementSlotOverlay[] slotInput = new ElementSlotOverlay[2];
	private ElementSlotOverlay[] slotTankInput = new ElementSlotOverlay[2];

	private ElementDualScaled duration;

	public GuiDiffuser(InventoryPlayer inventory, TileEntity tile) {

		super(new ContainerDiffuser(inventory, tile), tile, inventory.player, TEXTURE);

		generateInfo("tab.thermalexpansion.device.diffuser");

		myTile = (TileDiffuser) tile;
	}

	@Override
	public void initGui() {

		super.initGui();

		slotInput[0] = (ElementSlotOverlay) addElement(new ElementSlotOverlay(this, 35, 35).setSlotInfo(SlotColor.BLUE, SlotType.STANDARD, SlotRender.FULL));
		slotInput[1] = (ElementSlotOverlay) addElement(new ElementSlotOverlay(this, 35, 35).setSlotInfo(SlotColor.GREEN, SlotType.STANDARD, SlotRender.BOTTOM));
		slotTankInput[0] = (ElementSlotOverlay) addElement(new ElementSlotOverlay(this, 152, 9).setSlotInfo(SlotColor.BLUE, SlotType.TANK, SlotRender.FULL));
		slotTankInput[1] = (ElementSlotOverlay) addElement(new ElementSlotOverlay(this, 152, 9).setSlotInfo(SlotColor.PURPLE, SlotType.TANK, SlotRender.BOTTOM));

		duration = (ElementDualScaled) addElement(new ElementDualScaled(this, 62, 35).setSize(16, 16).setTexture(TEX_ALCHEMY, 32, 16));

		addElement(new ElementFluidTank(this, 152, 9, baseTile.getTank()).setAlwaysShow(true));
	}

	@Override
	protected void updateElementInformation() {

		super.updateElementInformation();

		slotInput[0].setVisible(myTile.hasSideType(INPUT_ALL) || baseTile.hasSideType(OMNI));
		slotInput[1].setVisible(myTile.hasSideType(INPUT_PRIMARY));
		slotTankInput[0].setVisible(myTile.hasSideType(INPUT_ALL) || baseTile.hasSideType(OMNI));
		slotTankInput[1].setVisible(myTile.hasSideType(INPUT_SECONDARY));

		if (!baseTile.hasSideType(INPUT_ALL)) {
			slotInput[1].setSlotRender(SlotRender.FULL);
			slotTankInput[1].setSlotRender(SlotRender.FULL);
		} else {
			slotInput[1].setSlotRender(SlotRender.BOTTOM);
			slotTankInput[1].setSlotRender(SlotRender.BOTTOM);
		}
		duration.setQuantity(myTile.getScaledSpeed(SPEED));
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int x, int y) {

		if (myTile.getBoostAmp() > 0) {
			fontRenderer.drawString("+" + myTile.getBoostAmp() + " " + StringHelper.localize("info.cofh.potency"), 82, myTile.getBoostDur() > 0 ? 35 : 40, 0x404040);
		}
		if (myTile.getBoostDur() > 0) {
			fontRenderer.drawString("x" + (1 + myTile.getBoostDur()) + " " + StringHelper.localize("info.cofh.duration"), 82, myTile.getBoostAmp() > 0 ? 45 : 40, 0x404040);
		}
		super.drawGuiContainerForegroundLayer(x, y);
	}

}
