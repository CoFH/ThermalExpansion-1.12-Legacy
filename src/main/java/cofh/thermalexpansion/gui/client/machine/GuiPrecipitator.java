package cofh.thermalexpansion.gui.client.machine;

import cofh.core.gui.element.*;
import cofh.thermalexpansion.block.machine.TilePrecipitator;
import cofh.thermalexpansion.gui.client.GuiPoweredBase;
import cofh.thermalexpansion.gui.container.machine.ContainerPrecipitator;
import cofh.thermalexpansion.gui.element.ElementSlotOverlay;
import cofh.thermalexpansion.gui.element.ElementSlotOverlay.SlotColor;
import cofh.thermalexpansion.gui.element.ElementSlotOverlay.SlotRender;
import cofh.thermalexpansion.gui.element.ElementSlotOverlay.SlotType;
import cofh.thermalexpansion.init.TEProps;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

public class GuiPrecipitator extends GuiPoweredBase {

	public static final String TEX_PATH = TEProps.PATH_GUI_MACHINE + "precipitator.png";
	public static final ResourceLocation TEXTURE = new ResourceLocation(TEX_PATH);

	private TilePrecipitator myTile;

	private ElementBase slotInput;
	private ElementBase slotOutput;
	private ElementFluid progressFluid;
	private ElementDualScaled progressOverlay;
	private ElementDualScaled speed;

	private ElementButton prevOutput;
	private ElementButton nextOutput;

	public GuiPrecipitator(InventoryPlayer inventory, TileEntity tile) {

		super(new ContainerPrecipitator(inventory, tile), tile, inventory.player, TEXTURE);

		generateInfo("tab.thermalexpansion.machine.precipitator");

		myTile = (TilePrecipitator) tile;
	}

	@Override
	public void initGui() {

		super.initGui();

		slotInput = addElement(new ElementSlotOverlay(this, 44, 19).setSlotInfo(SlotColor.BLUE, SlotType.TANK_SHORT, SlotRender.FULL));
		slotOutput = addElement(new ElementSlotOverlay(this, 130, 22).setSlotInfo(SlotColor.ORANGE, SlotType.OUTPUT, SlotRender.FULL));

		if (!myTile.smallStorage()) {
			addElement(new ElementEnergyStored(this, 8, 8, myTile.getEnergyStorage()));
		}
		addElement(new ElementFluidTank(this, 44, 19, myTile.getTank()).setAlwaysShow(true).setSmall());

		progressFluid = (ElementFluid) addElement(new ElementFluid(this, 85, 26).setFluid(myTile.getTankFluid()).setSize(24, 16));
		progressOverlay = (ElementDualScaled) addElement(new ElementDualScaled(this, 85, 26).setMode(1).setBackground(false).setSize(24, 16).setTexture(TEX_DROP_RIGHT, 64, 16));
		speed = (ElementDualScaled) addElement(new ElementDualScaled(this, 44, 53).setSize(16, 16).setTexture(TEX_SNOWFLAKE, 32, 16));

		prevOutput = new ElementButton(this, 72, 54, "PrevOutput", 176, 0, 176, 14, 176, 28, 14, 14, TEX_PATH).setToolTipLocalized(true);
		nextOutput = new ElementButton(this, 108, 54, "NextOutput", 190, 0, 190, 14, 190, 28, 14, 14, TEX_PATH).setToolTipLocalized(true);

		addElement(prevOutput);
		addElement(nextOutput);
	}

	@Override
	protected void updateElementInformation() {

		super.updateElementInformation();

		slotInput.setVisible(myTile.hasSideType(INPUT_ALL) || baseTile.hasSideType(OMNI));
		slotOutput.setVisible(myTile.hasSideType(OUTPUT_ALL) || baseTile.hasSideType(OMNI));

		progressFluid.setSize(myTile.getScaledProgress(PROGRESS), 16);
		progressOverlay.setQuantity(myTile.getScaledProgress(PROGRESS));
		speed.setQuantity(myTile.getScaledSpeed(SPEED));
	}

	@Override
	public void handleElementButtonClick(String buttonName, int mouseButton) {

		byte direction = 0;
		float pitch = 0.7F;

		if (buttonName.equalsIgnoreCase("PrevOutput")) {
			direction -= 1;
			pitch -= 0.1F;
		} else if (buttonName.equalsIgnoreCase("NextOutput")) {
			pitch += 0.1F;
			direction += 1;
		}
		playClickSound(pitch);
		myTile.setMode(direction);
	}

}
