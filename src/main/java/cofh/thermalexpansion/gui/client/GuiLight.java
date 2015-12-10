package cofh.thermalexpansion.gui.client;

import cofh.core.gui.GuiBaseAdv;
import cofh.core.gui.element.TabInfo;
import cofh.core.render.IconRegistry;
import cofh.lib.gui.GuiColor;
import cofh.lib.gui.element.ElementButton;
import cofh.lib.gui.element.ElementIcon;
import cofh.lib.gui.element.ElementSlider;
import cofh.lib.gui.element.ElementTextField;
import cofh.lib.gui.element.ElementTextFieldLimited;
import cofh.lib.gui.element.listbox.SliderHorizontal;
import cofh.lib.util.helpers.StringHelper;
import cofh.thermalexpansion.block.light.TileLight;
import cofh.thermalexpansion.core.TEProps;
import cofh.thermalexpansion.gui.container.ContainerTEBase;
import cpw.mods.fml.relauncher.Side;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

public class GuiLight extends GuiBaseAdv {

	static final String TEX_PATH = TEProps.PATH_GUI + "Light.png";
	static final String LOC_PATH = "chat.thermalexpansion.light.";
	static final ResourceLocation TEXTURE = new ResourceLocation(TEX_PATH);
	static final ResourceLocation SLIDER = new ResourceLocation(TEProps.PATH_GUI + "elements/Slider_Light.png");

	public static int GRAY = 80;

	private static int getValue(char[] text, int end) {

		int v = 0;
		for (int i = 0; i < end; ++i) {
			if (text[i] >= '0') {
				v = v * 10 + (text[i] - '0');
			}
		}
		return v;
	}

	TileLight myTile;

	ElementIcon baseDisplay;
	ElementIcon colorDisplay;

	ElementButton buttonDim;
	ElementButton buttonReset;
	int mode;

	ElementTextField textR;
	ElementTextField textG;
	ElementTextField textB;

	ElementSlider sliderR;
	ElementSlider sliderG;
	ElementSlider sliderB;

	GuiColor colorR;
	GuiColor colorG;
	GuiColor colorB;

	boolean sendUpdates = true;

	public GuiLight(InventoryPlayer inventory, TileLight theTile) {

		super(new ContainerTEBase(inventory, theTile, false, false), TEXTURE);
		myTile = theTile;
		name = myTile.getName();
		drawInventory = false;
		this.ySize = 100;

		generateInfo("tab.thermalexpansion.light", 3);
	}

	@Override
	public void initGui() {

		super.initGui();

		addTab(new TabInfo(this, myInfo));

		GuiColor tileColor = new GuiColor((byte) 255, myTile.color);
		int type = myTile.getBlockMetadata();
		IIcon icon;

		switch (type) {
		default:
		case 0:
			icon = IconRegistry.getIcon("FluidGlowstone");
			break;
		case 1:
		case 2:
			icon = IconRegistry.getIcon("Light1");
		}
		addElement(baseDisplay = new ElementIcon(this, 135, 24, icon));

		switch (type) {
		default:
		case 0:
			icon = IconRegistry.getIcon("LightEffect");
			break;
		case 1:
		case 2:
			icon = IconRegistry.getIcon("Light1");
		}
		addElement(colorDisplay = new ElementIcon(this, 135, 24, icon).setColor(tileColor));

		switch (type) {
		case 0:
			icon = IconRegistry.getIcon("Light0");
			break;
		case 1:
		case 2:
		default:
			icon = null;
		}
		addElement(new ElementIcon(this, 135, 24, icon));

		addElement(new SliderHorizontal(this, 53, 26, 70, 12, 5) {

			@Override
			public void onValueChanged(int value) {

				mode = value;
				if (!_isDragging) {
					onStopDragging();
				}
			}

			@Override
			public void onStopDragging() {

				myTile.mode = (byte) _value;
				myTile.sendUpdatePacket(Side.SERVER);
			}
		}.setValue(myTile.mode));

		buttonDim = new ElementButton(this, 29, 22, "Dim", 176, 0, 176, 20, 176, 40, 20, 20, TEX_PATH);
		buttonReset = new ElementButton(this, 7, 22, "Reset", 216, 0, 216, 20, 216, 40, 20, 20, TEX_PATH);
		addElement(buttonDim);
		addElement(buttonReset);

		textR = new ElementTextFieldLimited(this, 8 + 13 + 55 * 0, 6 + 68, 24, 10, (short) 4) {

			@Override
			protected boolean onEnter() {

				onFocusLost();
				return true;
			}

			@Override
			protected void onFocusLost() {

				sliderR.setValue(getValue(text, textLength));
			}

			@Override
			protected void onCharacterEntered(boolean success) {

				if (getValue(text, textLength) > 255) {
					renderStartX = 0;
					setText("255");
				}
			}
		}.setFilter("0123456789", true).setText("0").setBackgroundColor(0, 0, 0);
		textG = new ElementTextFieldLimited(this, 8 + 13 + 55 * 1, 6 + 68, 24, 10, (short) 4) {

			@Override
			protected boolean onEnter() {

				onFocusLost();
				return true;
			}

			@Override
			protected void onFocusLost() {

				sliderG.setValue(getValue(text, textLength));
			}

			@Override
			protected void onCharacterEntered(boolean success) {

				if (getValue(text, textLength) > 255) {
					renderStartX = 0;
					setText("255");
				}
			}
		}.setFilter("0123456789", true).setText("0").setBackgroundColor(0, 0, 0);
		textB = new ElementTextFieldLimited(this, 8 + 13 + 55 * 2, 6 + 68, 24, 10, (short) 4) {

			@Override
			protected boolean onEnter() {

				onFocusLost();
				return true;
			}

			@Override
			protected void onFocusLost() {

				sliderB.setValue(getValue(text, textLength));
			}

			@Override
			protected void onCharacterEntered(boolean success) {

				if (getValue(text, textLength) > 255) {
					renderStartX = 0;
					setText("255");
				}
			}
		}.setFilter("0123456789", true).setText("0").setBackgroundColor(0, 0, 0);

		sliderR = new SliderHorizontal(this, 8 + 55 * 0, 6 + 52, 50, 12, 255) {

			@Override
			protected void drawSlider(int mx, int my, int sliderX, int sliderY) {

				gui.bindTexture(SLIDER);
				GL11.glColor4f(1f, 1f, 1f, 1f);
				gui.drawTexturedModalRect(sliderX, sliderY, 0, 0, _sliderWidth, _sliderHeight);
			}

			@Override
			public void onValueChanged(int value) {

				GuiColor color = new GuiColor(colorDisplay.getColor());
				colorR = new GuiColor(GRAY + value / 2, GRAY, GRAY, 255);
				colorDisplay.setColor(new GuiColor(value, color.getIntG(), color.getIntB(), color.getIntA()));
				textR.setText(String.valueOf(value));
				if (!_isDragging) {
					onStopDragging();
				} else {
					myTile.modified = true;
				}
			}

			@Override
			public void onStopDragging() {

				myTile.setColor(colorDisplay.getColor() & 0xFFFFFF);
				if (sendUpdates) {
					myTile.sendUpdatePacket(Side.SERVER);
				}
			}

			@Override
			public boolean onMouseWheel(int mouseX, int mouseY, int movement) {

				return false;
			}
		}.setValue(tileColor.getIntR()).setSliderSize(2, 12);
		sliderG = new SliderHorizontal(this, 8 + 55 * 1, 6 + 52, 50, 12, 255) {

			@Override
			protected void drawSlider(int mx, int my, int sliderX, int sliderY) {

				gui.bindTexture(SLIDER);
				GL11.glColor4f(1f, 1f, 1f, 1f);
				gui.drawTexturedModalRect(sliderX, sliderY, 0, 0, _sliderWidth, _sliderHeight);
			}

			@Override
			public void onValueChanged(int value) {

				GuiColor color = new GuiColor(colorDisplay.getColor());
				colorG = new GuiColor(GRAY, GRAY + value / 2, GRAY, 255);
				colorDisplay.setColor(new GuiColor(color.getIntR(), value, color.getIntB(), color.getIntA()));
				textG.setText(String.valueOf(value));
				if (!_isDragging) {
					onStopDragging();
				} else {
					myTile.modified = true;
				}
			}

			@Override
			public void onStopDragging() {

				myTile.setColor(colorDisplay.getColor() & 0xFFFFFF);
				if (sendUpdates) {
					myTile.sendUpdatePacket(Side.SERVER);
				}
			}

			@Override
			public boolean onMouseWheel(int mouseX, int mouseY, int movement) {

				return false;
			}
		}.setValue(tileColor.getIntG()).setSliderSize(2, 12);
		sliderB = new SliderHorizontal(this, 8 + 55 * 2, 6 + 52, 50, 12, 255) {

			@Override
			protected void drawSlider(int mx, int my, int sliderX, int sliderY) {

				gui.bindTexture(SLIDER);
				GL11.glColor4f(1f, 1f, 1f, 1f);
				gui.drawTexturedModalRect(sliderX, sliderY, 0, 0, _sliderWidth, _sliderHeight);
			}

			@Override
			public void onValueChanged(int value) {

				GuiColor color = new GuiColor(colorDisplay.getColor());
				colorB = new GuiColor(GRAY, GRAY, GRAY + value / 2, 255);
				colorDisplay.setColor(new GuiColor(color.getIntR(), color.getIntG(), value, color.getIntA()));
				textB.setText(String.valueOf(value));
				if (!_isDragging) {
					onStopDragging();
				} else {
					myTile.modified = true;
				}
			}

			@Override
			public void onStopDragging() {

				myTile.setColor(colorDisplay.getColor() & 0xFFFFFF);
				if (sendUpdates) {
					myTile.sendUpdatePacket(Side.SERVER);
				}
			}

			@Override
			public boolean onMouseWheel(int mouseX, int mouseY, int movement) {

				return false;
			}
		}.setValue(tileColor.getIntB()).setSliderSize(2, 12);

		GuiColor color = new GuiColor(colorDisplay.getColor());
		colorR = new GuiColor(GRAY + color.getIntR() / 2, GRAY, GRAY, 255);
		colorG = new GuiColor(GRAY, GRAY + color.getIntG() / 2, GRAY, 255);
		colorB = new GuiColor(GRAY, GRAY, GRAY + color.getIntB() / 2, 255);

		addElement(textR);
		addElement(textG);
		addElement(textB);

		addElement(sliderR);
		addElement(sliderG);
		addElement(sliderB);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int x, int y) {

		super.drawGuiContainerForegroundLayer(x, y);

		fontRendererObj.drawString(StringHelper.localize(LOC_PATH + mode), 8, 45, 0x404040);
	}

	@Override
	public void handleElementButtonClick(String buttonName, int mouseButton) {

		if (buttonName == "Dim") {
			playSound("random.click", 1.0F, myTile.dim ? 0.6F : 0.4F);
			myTile.dim = !myTile.dim;
			myTile.getWorldObj().func_147451_t(myTile.xCoord, myTile.yCoord, myTile.zCoord);
			myTile.sendUpdatePacket(Side.SERVER);
		} else if (buttonName == "Reset") {
			playSound("random.click", 1.0F, 0.8F);
			sendUpdates = false;
			sliderR.setValue(255);
			sliderG.setValue(255);
			sliderB.setValue(255);
			sendUpdates = true;
			myTile.sendModePacket();
		}
	}

	@Override
	protected void updateElementInformation() {

		baseDisplay.setVisible(!myTile.modified);
		colorDisplay.setVisible(myTile.modified);

		int x = !myTile.dim ? 196 : 176;
		buttonDim.setDisabledX(x);
		buttonDim.setHoverX(x);
		buttonDim.setSheetX(x);

		if (myTile.modified) {
			buttonReset.setActive();
		} else {
			buttonReset.setDisabled();
		}

		sliderR.backgroundColor = colorR.getColor();
		sliderG.backgroundColor = colorG.getColor();
		sliderB.backgroundColor = colorB.getColor();
	}

}
