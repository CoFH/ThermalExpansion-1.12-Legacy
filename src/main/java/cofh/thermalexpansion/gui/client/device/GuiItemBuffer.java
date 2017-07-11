package cofh.thermalexpansion.gui.client.device;

import cofh.core.gui.element.ElementButton;
import cofh.core.gui.element.ElementSimple;
import cofh.core.util.helpers.StringHelper;
import cofh.thermalexpansion.block.device.TileItemBuffer;
import cofh.thermalexpansion.gui.container.device.ContainerItemBuffer;
import cofh.thermalexpansion.init.TEProps;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

public class GuiItemBuffer extends GuiDeviceBase {

	public static final String TEX_PATH = TEProps.PATH_GUI_DEVICE + "item_buffer.png";
	public static final ResourceLocation TEXTURE = new ResourceLocation(TEX_PATH);

	private TileItemBuffer myTile;

	private ElementButton decInput;
	private ElementButton incInput;
	private ElementButton decOutput;
	private ElementButton incOutput;

	public GuiItemBuffer(InventoryPlayer inventory, TileEntity tile) {

		super(new ContainerItemBuffer(inventory, tile), tile, inventory.player, TEXTURE);

		generateInfo("tab.thermalexpansion.device.item_buffer");

		myTile = (TileItemBuffer) tile;
	}

	@Override
	public void initGui() {

		super.initGui();

		ElementSimple infoInput = (ElementSimple) new ElementSimple(this, 24, 16).setSize(20, 20).setTexture(TEX_INFO_INPUT, 20, 20);
		ElementSimple infoOutput = (ElementSimple) new ElementSimple(this, 132, 16).setSize(20, 20).setTexture(TEX_INFO_OUTPUT, 20, 20);

		addElement(infoInput);
		addElement(infoOutput);

		decInput = new ElementButton(this, 19, 56, "DecInput", 176, 0, 176, 14, 176, 28, 14, 14, TEX_PATH).setToolTipLocalized(true);
		incInput = new ElementButton(this, 35, 56, "IncInput", 190, 0, 190, 14, 190, 28, 14, 14, TEX_PATH).setToolTipLocalized(true);
		decOutput = new ElementButton(this, 127, 56, "DecOutput", 176, 0, 176, 14, 176, 28, 14, 14, TEX_PATH).setToolTipLocalized(true);
		incOutput = new ElementButton(this, 143, 56, "IncOutput", 190, 0, 190, 14, 190, 28, 14, 14, TEX_PATH).setToolTipLocalized(true);

		addElement(decInput);
		addElement(incInput);
		addElement(decOutput);
		addElement(incOutput);
	}

	@Override
	protected void updateElementInformation() {

		super.updateElementInformation();

		int change;
		int change2;

		if (GuiScreen.isShiftKeyDown()) {
			change = 64;
			change2 = 32;
		} else if (GuiScreen.isCtrlKeyDown()) {
			change = 4;
			change2 = 1;
		} else {
			change = 16;
			change2 = 8;
		}
		if (myTile.amountInput > 0) {
			decInput.setActive();
			decInput.setToolTip(StringHelper.localize("gui.thermalexpansion.device.item_buffer.decInput") + " " + StringHelper.formatNumber(change) + "/" + StringHelper.formatNumber(change2));
		} else {
			decInput.setDisabled();
			decInput.clearToolTip();
		}
		if (myTile.amountInput < 64) {
			incInput.setActive();
			incInput.setToolTip(StringHelper.localize("gui.thermalexpansion.device.item_buffer.incInput") + " " + StringHelper.formatNumber(change) + "/" + StringHelper.formatNumber(change2));
		} else {
			incInput.setDisabled();
			incInput.clearToolTip();
		}
		if (myTile.amountOutput > 0) {
			decOutput.setActive();
			decOutput.setToolTip(StringHelper.localize("gui.thermalexpansion.device.item_buffer.decOutput") + " " + StringHelper.formatNumber(change) + "/" + StringHelper.formatNumber(change2));
		} else {
			decOutput.setDisabled();
			decOutput.clearToolTip();
		}
		if (myTile.amountOutput < 64) {
			incOutput.setActive();
			incOutput.setToolTip(StringHelper.localize("gui.thermalexpansion.device.item_buffer.incOutput") + " " + StringHelper.formatNumber(change) + "/" + StringHelper.formatNumber(change2));
		} else {
			incOutput.setDisabled();
			incOutput.clearToolTip();
		}
	}

	@Override
	public void handleElementButtonClick(String buttonName, int mouseButton) {

		int change;
		float pitch;

		if (GuiScreen.isShiftKeyDown()) {
			change = 64;
			pitch = 0.9F;
			if (mouseButton == 1) {
				change = 32;
				pitch = 0.8F;
			}
		} else if (GuiScreen.isCtrlKeyDown()) {
			change = 4;
			pitch = 0.5F;
			if (mouseButton == 1) {
				change = 1;
				pitch = 0.4F;
			}
		} else {
			change = 16;
			pitch = 0.7F;
			if (mouseButton == 1) {
				change = 8;
				pitch = 0.6F;
			}
		}
		int curInput = myTile.amountInput;
		int curOutput = myTile.amountOutput;

		if (buttonName.equalsIgnoreCase("DecInput")) {
			myTile.amountInput -= change;
			pitch -= 0.1F;
		} else if (buttonName.equalsIgnoreCase("IncInput")) {
			myTile.amountInput += change;
			pitch += 0.1F;
		} else if (buttonName.equalsIgnoreCase("DecOutput")) {
			myTile.amountOutput -= change;
			pitch -= 0.1F;
		} else if (buttonName.equalsIgnoreCase("IncOutput")) {
			myTile.amountOutput += change;
			pitch += 0.1F;
		}
		playClickSound(pitch);

		myTile.sendModePacket();

		myTile.amountInput = curInput;
		myTile.amountOutput = curOutput;
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int x, int y) {

		String input = "" + myTile.amountInput;
		String output = "" + myTile.amountOutput;

		int xInput = 29;
		int xOutput = 137;

		if (myTile.amountInput < 10) {
			xInput += 3;
		}
		if (myTile.amountOutput < 10) {
			xOutput += 3;
		}
		fontRenderer.drawString(input, xInput, 42, 0x404040);
		fontRenderer.drawString(output, xOutput, 42, 0x404040);

		super.drawGuiContainerForegroundLayer(x, y);
	}

}
