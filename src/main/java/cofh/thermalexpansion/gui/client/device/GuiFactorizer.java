package cofh.thermalexpansion.gui.client.device;

import cofh.core.gui.element.ElementBase;
import cofh.core.gui.element.ElementButton;
import cofh.thermalexpansion.block.device.TileFactorizer;
import cofh.thermalexpansion.gui.container.device.ContainerFactorizer;
import cofh.thermalexpansion.gui.element.ElementSlotOverlay;
import cofh.thermalexpansion.gui.element.ElementSlotOverlay.SlotColor;
import cofh.thermalexpansion.gui.element.ElementSlotOverlay.SlotRender;
import cofh.thermalexpansion.gui.element.ElementSlotOverlay.SlotType;
import cofh.thermalexpansion.init.TEProps;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

public class GuiFactorizer extends GuiDeviceBase {

	public static final String TEX_PATH = TEProps.PATH_GUI_DEVICE + "factorizer.png";
	public static final ResourceLocation TEXTURE = new ResourceLocation(TEX_PATH);

	private TileFactorizer myTile;

	private ElementBase slotInput;
	private ElementBase slotOutput;

	private ElementButton mode;

	public GuiFactorizer(InventoryPlayer inventory, TileEntity tile) {

		super(new ContainerFactorizer(inventory, tile), tile, inventory.player, TEXTURE);

		generateInfo("tab.thermalexpansion.device.factorizer");

		myTile = (TileFactorizer) tile;
	}

	@Override
	public void initGui() {

		super.initGui();

		slotInput = addElement(new ElementSlotOverlay(this, 44, 26).setSlotInfo(SlotColor.BLUE, SlotType.STANDARD, SlotRender.FULL));
		slotOutput = addElement(new ElementSlotOverlay(this, 112, 22).setSlotInfo(SlotColor.ORANGE, SlotType.OUTPUT, SlotRender.FULL));

		mode = (ElementButton) addElement(new ElementButton(this, 80, 53, "Mode", 176, 0, 176, 16, 176, 32, 16, 16, TEX_PATH));
	}

	@Override
	protected void updateElementInformation() {

		super.updateElementInformation();

		slotInput.setVisible(baseTile.hasSideType(INPUT_ALL) || baseTile.hasSideType(OMNI));
		slotOutput.setVisible(baseTile.hasSideType(OUTPUT_ALL) || baseTile.hasSideType(OMNI));

		if (myTile.recipeMode) {
			mode.setToolTip("gui.thermalexpansion.device.factorizer.modeSplit");
			mode.setSheetX(176);
			mode.setHoverX(176);
		} else {
			mode.setToolTip("gui.thermalexpansion.device.factorizer.modeCombine");
			mode.setSheetX(192);
			mode.setHoverX(192);
		}
	}

	@Override
	public void handleElementButtonClick(String buttonName, int mouseButton) {

		if (buttonName.equalsIgnoreCase("Mode")) {
			if (myTile.recipeMode) {
				playClickSound(0.6F);
			} else {
				playClickSound(0.8F);
			}
			myTile.setMode(!myTile.recipeMode);
		}
	}

}
