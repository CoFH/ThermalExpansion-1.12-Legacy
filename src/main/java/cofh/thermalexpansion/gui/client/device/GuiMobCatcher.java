package cofh.thermalexpansion.gui.client.device;

import cofh.core.gui.element.ElementBase;
import cofh.core.gui.element.ElementButton;
import cofh.thermalexpansion.block.device.TileMobCatcher;
import cofh.thermalexpansion.gui.container.device.ContainerMobCatcher;
import cofh.thermalexpansion.gui.element.ElementSlotOverlay;
import cofh.thermalexpansion.gui.element.ElementSlotOverlay.SlotColor;
import cofh.thermalexpansion.gui.element.ElementSlotOverlay.SlotRender;
import cofh.thermalexpansion.gui.element.ElementSlotOverlay.SlotType;
import cofh.thermalexpansion.gui.element.ElementSlotOverlayQuad;
import cofh.thermalexpansion.init.TEProps;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

public class GuiMobCatcher extends GuiDeviceBase {

	public static final String TEX_PATH = TEProps.PATH_GUI_DEVICE + "mob_catcher.png";
	public static final ResourceLocation TEXTURE = new ResourceLocation(TEX_PATH);

	// @formatter:off
	public static final String[] TOOLTIPS = new String[] {
			"gui.thermalexpansion.device.mob_catcher.modeAll",
			"gui.thermalexpansion.device.mob_catcher.modeHostile",
			"gui.thermalexpansion.device.mob_catcher.modeFriendly"};
	// @formatter:on

	private TileMobCatcher myTile;

	private ElementBase slotInput;
	private ElementSlotOverlayQuad slotOutput;

	private ElementButton modeSel;

	public GuiMobCatcher(InventoryPlayer inventory, TileEntity tile) {

		super(new ContainerMobCatcher(inventory, tile), tile, inventory.player, TEXTURE);

		generateInfo("tab.thermalexpansion.device.mob_catcher");

		myTile = (TileMobCatcher) tile;
	}

	@Override
	public void initGui() {

		super.initGui();

		slotInput = addElement(new ElementSlotOverlay(this, 35, 35).setSlotInfo(SlotColor.BLUE, SlotType.STANDARD, SlotRender.FULL));
		slotOutput = (ElementSlotOverlayQuad) addElement(new ElementSlotOverlayQuad(this, 107, 26).setSlotInfo(SlotColor.ORANGE, SlotRender.FULL));

		modeSel = (ElementButton) addElement(new ElementButton(this, 80, 53, "Mode", 176, 0, 176, 16, 176, 32, 16, 16, TEX_PATH));
	}

	@Override
	protected void updateElementInformation() {

		super.updateElementInformation();

		slotInput.setVisible(baseTile.hasSideType(INPUT_ALL) || baseTile.hasSideType(OMNI));
		slotOutput.setVisible(baseTile.hasSideType(OUTPUT_ALL) || baseTile.hasSideType(OMNI));

		modeSel.setToolTip(TOOLTIPS[myTile.modeFlag]);
		modeSel.setSheetX(176 + myTile.modeFlag * 16);
		modeSel.setHoverX(176 + myTile.modeFlag * 16);
	}

	@Override
	public void handleElementButtonClick(String buttonName, int mouseButton) {

		if (buttonName.equalsIgnoreCase("Mode")) {
			playClickSound(0.6F + myTile.mode * 0.1F);
			myTile.toggleMode(mouseButton == 1);
		}
	}

}
