package cofh.thermalexpansion.gui.client.device;

import cofh.lib.gui.element.ElementButton;
import cofh.thermalexpansion.block.device.TileItemBuffer;
import cofh.thermalexpansion.gui.client.GuiTEBase;
import cofh.thermalexpansion.gui.container.device.ContainerBuffer;
import cofh.thermalexpansion.init.TEProps;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

public class GuiBuffer extends GuiTEBase {

	static final String TEX_PATH = TEProps.PATH_GUI_DEVICE + "buffer.png";
	public static final ResourceLocation TEXTURE = new ResourceLocation(TEX_PATH);

	TileItemBuffer myTile;

	ElementButton decInput;
	ElementButton incInput;
	ElementButton decOutput;
	ElementButton incOutput;

	ElementButton enableInput;
	ElementButton enableOutput;

	public GuiBuffer(InventoryPlayer inventory, TileEntity tile) {

		super(new ContainerBuffer(inventory, tile), tile, inventory.player, TEXTURE);

		generateInfo("tab.thermalexpansion.device.buffer", 3);

		myTile = (TileItemBuffer) tile;
	}

	@Override
	public void initGui() {

		super.initGui();

		decInput = new ElementButton(this, 19, 56, "DecInput", 176, 0, 176, 14, 176, 28, 14, 14, TEX_PATH).setToolTipLocalized(true);
		incInput = new ElementButton(this, 35, 56, "IncInput", 190, 0, 190, 14, 190, 28, 14, 14, TEX_PATH).setToolTipLocalized(true);
		decOutput = new ElementButton(this, 127, 56, "DecOutput", 176, 0, 176, 14, 176, 28, 14, 14, TEX_PATH).setToolTipLocalized(true);
		incOutput = new ElementButton(this, 143, 56, "IncOutput", 190, 0, 190, 14, 190, 28, 14, 14, TEX_PATH).setToolTipLocalized(true);

		enableInput = new ElementButton(this, 26, 17, "EnInput", 176, 42, 176, 58, 176, 74, 16, 16, TEX_PATH);
		enableOutput = new ElementButton(this, 134, 17, "EnOutput", 208, 42, 208, 58, 208, 74, 16, 16, TEX_PATH);

		addElement(decInput);
		addElement(incInput);
		addElement(decOutput);
		addElement(incOutput);

		addElement(enableInput);
		addElement(enableOutput);
	}

	@Override
	protected void updateElementInformation() {

		super.updateElementInformation();

		if (myTile.enableInput) {
			enableInput.setToolTip("info.thermalexpansion.buffer.disableInput");
			enableInput.setSheetX(176);
			enableInput.setHoverX(176);
		} else {
			enableInput.setToolTip("info.thermalexpansion.buffer.enableInput");
			enableInput.setSheetX(192);
			enableInput.setHoverX(192);
		}

		if (myTile.enableOutput) {
			enableOutput.setToolTip("info.thermalexpansion.buffer.disableOutput");
			enableOutput.setSheetX(208);
			enableOutput.setHoverX(208);
		} else {
			enableOutput.setToolTip("info.thermalexpansion.buffer.enableOutput");
			enableOutput.setSheetX(224);
			enableOutput.setHoverX(224);
		}
	}

	@Override
	public void handleElementButtonClick(String buttonName, int mouseButton) {

		boolean enInput = myTile.enableInput;
		boolean enOutput = myTile.enableOutput;
		int curInput = myTile.quantityInput;
		int curOutput = myTile.quantityOutput;

		boolean modeToggle = false;
		int change = 0;
		float pitch = 1.0F;

		if (buttonName.equals("EnInput")) {
			myTile.enableInput = !myTile.enableInput;
			modeToggle = true;
			pitch = myTile.enableInput ? 1.0F : 0.8F;
		}
		if (buttonName.equals("EnOutput")) {
			myTile.enableOutput = !myTile.enableOutput;
			modeToggle = true;
			pitch = myTile.enableOutput ? 1.0F : 0.8F;
		}
		if (!modeToggle) {
			if (GuiScreen.isShiftKeyDown()) {
				change = 32;
				pitch = 0.9F;
				if (mouseButton == 1) {
					change = 16;
					pitch = 0.8F;
				}
			} else if (GuiScreen.isCtrlKeyDown()) {
				change = 8;
				pitch = 0.7F;
				if (mouseButton == 1) {
					change = 4;
					pitch = 0.6F;
				}
			} else {
				change = 1;
				pitch = 0.5F;
			}
			if (buttonName.equalsIgnoreCase("DecInput")) {
				myTile.quantityInput -= change;
				pitch -= 0.1F;
			} else if (buttonName.equalsIgnoreCase("IncInput")) {
				myTile.quantityInput += change;
				pitch += 0.1F;
			} else if (buttonName.equalsIgnoreCase("DecOutput")) {
				myTile.quantityOutput -= change;
				pitch -= 0.1F;
			} else if (buttonName.equalsIgnoreCase("IncOutput")) {
				myTile.quantityOutput += change;
				pitch += 0.1F;
			}
		}
		playClickSound(1.0F, pitch);

		myTile.sendModePacket();

		myTile.enableInput = enInput;
		myTile.enableOutput = enOutput;
		myTile.quantityInput = curInput;
		myTile.quantityOutput = curOutput;
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int x, int y) {

		String input = "" + myTile.quantityInput;
		String output = "" + myTile.quantityOutput;

		int xInput = 29;
		int xOutput = 137;

		if (myTile.quantityInput < 10) {
			xInput += 3;
		}
		if (myTile.quantityOutput < 10) {
			xOutput += 3;
		}
		fontRendererObj.drawString(input, xInput, 42, 0x404040);
		fontRendererObj.drawString(output, xOutput, 42, 0x404040);

		super.drawGuiContainerForegroundLayer(x, y);
	}

}
