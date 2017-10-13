package cofh.thermalexpansion.gui.client.device;

import cofh.core.gui.element.ElementBase;
import cofh.core.gui.element.ElementButton;
import cofh.core.gui.element.ElementSimple;
import cofh.core.util.helpers.StringHelper;
import cofh.thermalexpansion.block.device.TileItemBuffer;
import cofh.thermalexpansion.block.device.TileLexicon;
import cofh.thermalexpansion.gui.container.device.ContainerItemBuffer;
import cofh.thermalexpansion.gui.container.device.ContainerLexicon;
import cofh.thermalexpansion.gui.element.ElementSlotOverlay;
import cofh.thermalexpansion.gui.element.ElementSlotOverlay.SlotColor;
import cofh.thermalexpansion.gui.element.ElementSlotOverlay.SlotRender;
import cofh.thermalexpansion.gui.element.ElementSlotOverlay.SlotType;
import cofh.thermalexpansion.init.TEProps;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

public class GuiLexicon extends GuiDeviceBase {

	public static final String TEX_PATH = TEProps.PATH_GUI_DEVICE + "lexicon.png";
	public static final ResourceLocation TEXTURE = new ResourceLocation(TEX_PATH);

	private TileLexicon myTile;

	private ElementBase slotInput;
	private ElementBase slotOutput;

	public GuiLexicon(InventoryPlayer inventory, TileEntity tile) {

		super(new ContainerLexicon(inventory, tile), tile, inventory.player, TEXTURE);

		generateInfo("tab.thermalexpansion.device.lexicon");

		myTile = (TileLexicon) tile;
	}

	@Override
	public void initGui() {

		super.initGui();

		slotInput = addElement(new ElementSlotOverlay(this, 26, 35).setSlotInfo(SlotColor.BLUE, SlotType.STANDARD, SlotRender.FULL));
		slotOutput = addElement(new ElementSlotOverlay(this, 130, 31).setSlotInfo(SlotColor.ORANGE, SlotType.OUTPUT, SlotRender.FULL));
	}

	@Override
	protected void updateElementInformation() {

		super.updateElementInformation();

		slotInput.setVisible(baseTile.hasSideType(INPUT_ALL) || baseTile.hasSideType(OMNI));
		slotOutput.setVisible(baseTile.hasSideType(OUTPUT_ALL) || baseTile.hasSideType(OMNI));
	}

}
