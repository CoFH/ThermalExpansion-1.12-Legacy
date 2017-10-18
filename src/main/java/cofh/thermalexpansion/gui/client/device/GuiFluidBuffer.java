package cofh.thermalexpansion.gui.client.device;

import cofh.core.gui.element.ElementButton;
import cofh.core.gui.element.ElementFluidTank;
import cofh.core.gui.element.ElementSimple;
import cofh.core.util.helpers.StringHelper;
import cofh.thermalexpansion.block.device.TileFluidBuffer;
import cofh.thermalexpansion.gui.container.ContainerTEBase;
import cofh.thermalexpansion.init.TEProps;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;

public class GuiFluidBuffer extends GuiDeviceBase {

	public static final String TEX_PATH = TEProps.PATH_GUI_DEVICE + "fluid_buffer.png";
	public static final ResourceLocation TEXTURE = new ResourceLocation(TEX_PATH);

	private TileFluidBuffer myTile;

	private ElementButton decInput;
	private ElementButton incInput;
	private ElementButton decOutput;
	private ElementButton incOutput;

	private ElementButton[] lock = new ElementButton[3];

	public GuiFluidBuffer(InventoryPlayer inventory, TileEntity tile) {

		super(new ContainerTEBase(inventory, tile), tile, inventory.player, TEXTURE);

		generateInfo("tab.thermalexpansion.device.fluid_buffer");

		myTile = (TileFluidBuffer) tile;
	}

	@Override
	public void initGui() {

		super.initGui();

		ElementSimple infoInput = (ElementSimple) new ElementSimple(this, 24, 16).setSize(20, 20).setTexture(TEX_INFO_INPUT, 20, 20);
		ElementSimple infoOutput = (ElementSimple) new ElementSimple(this, 132, 16).setSize(20, 20).setTexture(TEX_INFO_OUTPUT, 20, 20);

		addElement(infoInput);
		addElement(infoOutput);

		addElement(new ElementFluidTank(this, 62, 21, myTile.getTank(0)).setGauge(0).setAlwaysShow(true).setShort());
		addElement(new ElementFluidTank(this, 80, 21, myTile.getTank(1)).setGauge(0).setAlwaysShow(true).setShort());
		addElement(new ElementFluidTank(this, 98, 21, myTile.getTank(2)).setGauge(0).setAlwaysShow(true).setShort());

		decInput = new ElementButton(this, 19, 56, "DecInput", 176, 0, 176, 14, 176, 28, 14, 14, TEX_PATH).setToolTipLocalized(true);
		incInput = new ElementButton(this, 35, 56, "IncInput", 190, 0, 190, 14, 190, 28, 14, 14, TEX_PATH).setToolTipLocalized(true);
		decOutput = new ElementButton(this, 127, 56, "DecOutput", 176, 0, 176, 14, 176, 28, 14, 14, TEX_PATH).setToolTipLocalized(true);
		incOutput = new ElementButton(this, 143, 56, "IncOutput", 190, 0, 190, 14, 190, 28, 14, 14, TEX_PATH).setToolTipLocalized(true);

		addElement(decInput);
		addElement(incInput);
		addElement(decOutput);
		addElement(incOutput);

		for (int i = 0; i < lock.length; i++) {
			lock[i] = new ElementButton(this, 62 + i * 18, 55, "Lock" + i, 176, 48, 176, 64, 176, 80, 16, 16, TEX_PATH).setToolTipLocalized(true);
			addElement(lock[i]);
		}
	}

	@Override
	protected void updateElementInformation() {

		super.updateElementInformation();

		int change;
		int change2;

		if (GuiScreen.isShiftKeyDown()) {
			change = 8000;
			change2 = 4000;
		} else if (GuiScreen.isCtrlKeyDown()) {
			change = 500;
			change2 = 100;
		} else {
			change = 2000;
			change2 = 1000;
		}
		if (myTile.amountInput > 0) {
			decInput.setActive();
			decInput.setToolTip(StringHelper.localize("gui.thermalexpansion.device.item_buffer.decInput") + " " + StringHelper.formatNumber(change) + "/" + StringHelper.formatNumber(change2));
		} else {
			decInput.setDisabled();
			decInput.clearToolTip();
		}
		if (myTile.amountInput < 8000) {
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
		if (myTile.amountOutput < 8000) {
			incOutput.setActive();
			incOutput.setToolTip(StringHelper.localize("gui.thermalexpansion.device.item_buffer.incOutput") + " " + StringHelper.formatNumber(change) + "/" + StringHelper.formatNumber(change2));
		} else {
			incOutput.setDisabled();
			incOutput.clearToolTip();
		}

		for (int i = 0; i < lock.length; i++) {
			if (myTile.getTank(i).getFluid() == null) {
				lock[i].setDisabled();
			} else {
				lock[i].setActive();
			}
			if (myTile.locks[i]) {
				String color = StringHelper.WHITE;
				FluidStack fluid = myTile.getTank(i).getFluid();
				if (fluid.getFluid().getRarity() == EnumRarity.UNCOMMON) {
					color = StringHelper.YELLOW;
				} else if (fluid.getFluid().getRarity() == EnumRarity.RARE) {
					color = StringHelper.BRIGHT_BLUE;
				} else if (fluid.getFluid().getRarity() == EnumRarity.EPIC) {
					color = StringHelper.PINK;
				}
				lock[i].setToolTip(StringHelper.localize("gui.thermalexpansion.device.fluid_buffer.tankLocked") + ": " + color + StringHelper.localize(fluid.getFluid().getLocalizedName(fluid)) + StringHelper.END);
				lock[i].setSheetX(176);
				lock[i].setHoverX(176);
			} else {
				lock[i].setToolTip(StringHelper.localize("gui.thermalexpansion.device.fluid_buffer.tankUnlocked"));
				lock[i].setSheetX(192);
				lock[i].setHoverX(192);
			}
		}
	}

	@Override
	public void handleElementButtonClick(String buttonName, int mouseButton) {

		int change;
		float pitch;

		if (GuiScreen.isShiftKeyDown()) {
			change = 8000;
			pitch = 0.9F;
			if (mouseButton == 1) {
				change = 4000;
				pitch = 0.8F;
			}
		} else if (GuiScreen.isCtrlKeyDown()) {
			change = 500;
			pitch = 0.5F;
			if (mouseButton == 1) {
				change = 100;
				pitch = 0.4F;
			}
		} else {
			change = 2000;
			pitch = 0.7F;
			if (mouseButton == 1) {
				change = 1000;
				pitch = 0.6F;
			}
		}
		int curInput = myTile.amountInput;
		int curOutput = myTile.amountOutput;
		boolean[] curLocks = myTile.locks.clone();

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
		} else if (buttonName.equalsIgnoreCase("Lock0")) {
			if (myTile.locks[0]) {
				myTile.locks[0] = false;
				pitch = 0.6F;
			} else {
				myTile.locks[0] = true;
				pitch = 0.8F;
			}
		} else if (buttonName.equalsIgnoreCase("Lock1")) {
			if (myTile.locks[1]) {
				myTile.locks[1] = false;
				pitch = 0.6F;
			} else {
				myTile.locks[1] = true;
				pitch = 0.8F;
			}
		} else if (buttonName.equalsIgnoreCase("Lock2")) {
			if (myTile.locks[2]) {
				myTile.locks[2] = false;
				pitch = 0.6F;
			} else {
				myTile.locks[2] = true;
				pitch = 0.8F;
			}
		}
		playClickSound(pitch);

		myTile.sendModePacket();

		myTile.amountInput = curInput;
		myTile.amountOutput = curOutput;
		myTile.locks = curLocks;
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int x, int y) {

		String input = "" + myTile.amountInput;
		String output = "" + myTile.amountOutput;

		int xInput = 26;
		int xOutput = 134;

		if (myTile.amountInput < 100) {
			xInput += 6;
		}
		if (myTile.amountOutput < 100) {
			xOutput += 6;
		}
		if (myTile.amountInput >= 1000) {
			xInput -= 3;
		}
		if (myTile.amountOutput >= 1000) {
			xOutput -= 3;
		}

		fontRenderer.drawString(input, xInput, 42, 0x404040);
		fontRenderer.drawString(output, xOutput, 42, 0x404040);

		super.drawGuiContainerForegroundLayer(x, y);
	}

}
