package cofh.thermalexpansion.gui.client.device;

import cofh.core.gui.element.ElementBase;
import cofh.core.gui.element.ElementDualScaled;
import cofh.thermalexpansion.block.device.TileCatcher;
import cofh.thermalexpansion.block.device.TileChunkLoader;
import cofh.thermalexpansion.gui.container.device.ContainerDiffuser;
import cofh.thermalexpansion.gui.element.ElementSlotOverlay;
import cofh.thermalexpansion.gui.element.ElementSlotOverlayQuad;
import cofh.thermalexpansion.gui.element.ElementSlotOverlay.SlotColor;
import cofh.thermalexpansion.gui.element.ElementSlotOverlay.SlotRender;
import cofh.thermalexpansion.gui.element.ElementSlotOverlay.SlotType;
import cofh.thermalexpansion.init.TEProps;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

public class GuiCatcher extends GuiDeviceBase{
	
	public static final ResourceLocation TEXTURE = new ResourceLocation(TEProps.PATH_GUI_DEVICE + "catcher.png");
	
	private ElementBase slotInput;
	private ElementSlotOverlayQuad slotOutput;
	
	public GuiCatcher(InventoryPlayer inventory, TileEntity tile) {
		super(new ContainerDiffuser(inventory, tile), tile, inventory.player, TEXTURE);
		
		generateInfo("tab.thermalexpansion.device.catcher");
	}
	
	@Override
	public void initGui() {

		super.initGui();

		slotInput = addElement(new ElementSlotOverlay(this, 35, 35).setSlotInfo(SlotColor.BLUE, SlotType.STANDARD, SlotRender.FULL));
		slotOutput = (ElementSlotOverlayQuad) addElement(new ElementSlotOverlayQuad(this, 107, 26).setSlotInfo(SlotColor.ORANGE, SlotRender.FULL));
	}
	
	@Override
	protected void updateElementInformation() {

		super.updateElementInformation();

		slotInput.setVisible(baseTile.hasSideType(INPUT_ALL) || baseTile.hasSideType(OMNI));
		slotOutput.setVisible(baseTile.hasSideType(OUTPUT_ALL) || baseTile.hasSideType(OMNI));
	}
}
