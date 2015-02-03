package thermalexpansion.gui.client.light;

import cofh.core.gui.GuiBaseAdv;
import cofh.core.render.IconRegistry;
import cofh.lib.gui.GuiColor;
import cofh.lib.gui.element.ElementButton;
import cofh.lib.gui.element.ElementIcon;
import cofh.lib.gui.element.ElementSlider;
import cofh.lib.gui.element.ElementTextField;
import cofh.lib.gui.element.ElementTextFieldLimited;
import cofh.lib.gui.element.listbox.SliderHorizontal;
import cofh.lib.util.helpers.StringHelper;
import cpw.mods.fml.relauncher.Side;

import net.minecraft.inventory.Container;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import thermalexpansion.block.light.TileLight;
import thermalexpansion.core.TEProps;


public class GuiLight extends GuiBaseAdv {

	static final String TEX_PATH = TEProps.PATH_GUI + "Light.png";
	static final String LOC_PATH = "chat.thermalexpansion.light.";
	static final ResourceLocation TEXTURE = new ResourceLocation(TEX_PATH);
	static final ResourceLocation SLIDER = new ResourceLocation(TEProps.PATH_GUI + "/elements/Slider_Light.png");

	private static int getValue(char[] text) {

		int v = 0;
		for (int i = 0; i --> 0; )
			if (text[i] >= '0')
				v = v * 10 + (text[i] - '0');
		return v;
	}

	TileLight myTile;

	ElementIcon colorDisplay;
	ElementButton dimButton;
	int mode;
	ElementSlider sliderR;
	ElementSlider sliderG;
	ElementSlider sliderB;

	public GuiLight(Container container, TileLight tile) {

		super(container, TEXTURE);
		myTile = tile;
	}

	@Override
	public void initGui() {

		super.initGui();

		GuiColor tileColor = new GuiColor((byte)255, myTile.color);
		int type = myTile.getBlockMetadata();
		IIcon icon;
		switch (type) {
		default:
		case 0:
			icon = IconRegistry.getIcon("LightEffect");
			break;
		case 1:
		case 2:
			icon = IconRegistry.getIcon("Light1");
		}
		addElement(colorDisplay = new ElementIcon(this, 5, 5, icon).setColor(tileColor));

		switch (type) {
		case 0:
			icon = IconRegistry.getIcon("Light0");
			break;
		case 1:
		case 2:
		default:
			icon = null;
		}
		addElement(new ElementIcon(this, 5, 5, icon));

		addElement(new SliderHorizontal(this, 6 + 16 + 5, 6 + 10, 70, 12, 5) {
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

		dimButton = new ElementButton(this, 6 + 16 + 5 + 70 + 5, 5 + 10, "Dim", 176, 0, 176, 14, 176, 28, 14, 14, TEX_PATH);
		addElement(dimButton);


		final ElementTextField textR;
		final ElementTextField textG;
		final ElementTextField textB;

		addElement(textR = new ElementTextFieldLimited(this, 6 + 13 + 55 * 0, 6 + 55, 24, 10, (short) 4) {
			@Override
			protected boolean onEnter() {

				onFocusLost();
				return true;
			}

			@Override
			protected void onFocusLost() {

				sliderR.setValue(getValue(text));
			}

			@Override
			protected void onCharacterEntered(boolean success) {

				if (getValue(text) > 255)
					setText("255");
			}
		}.setFilter("0123456789", true).setText("0"));

		addElement(sliderR = new SliderHorizontal(this, 6 + 55 * 0, 6 + 40, 50, 12, 255) {
			@Override
			protected void drawSlider(int mx, int my, int sliderX, int sliderY) {

				gui.bindTexture(SLIDER);
				GL11.glColor4f(1f, 1f, 1f, 1f);
				gui.drawTexturedModalRect(sliderX, sliderY, 0, 0, _sliderWidth, _sliderHeight);
			}

			@Override
			public void onValueChanged(int value) {

				GuiColor color = new GuiColor(colorDisplay.getColor());
				colorDisplay.setColor(new GuiColor(value, color.getIntG(), color.getIntB(), color.getIntA()));
				textR.setText(String.valueOf(value));
				if (!_isDragging) {
					onStopDragging();
				}
			}

			@Override
			public void onStopDragging() {

				myTile.setColor(colorDisplay.getColor() & 0xFFFFFF);
				myTile.sendUpdatePacket(Side.SERVER);
			}
		}.setValue(tileColor.getIntR()).setSliderSize(2, 12));

		addElement(textG = new ElementTextFieldLimited(this, 6 + 13 + 55 * 1, 6 + 55, 24, 10, (short) 4) {
			@Override
			protected boolean onEnter() {

				onFocusLost();
				return true;
			}

			@Override
			protected void onFocusLost() {

				sliderG.setValue(getValue(text));
			}

			@Override
			protected void onCharacterEntered(boolean success) {

				if (getValue(text) > 255)
					setText("255");
			}
		}.setFilter("0123456789", true).setText("0"));

		addElement(sliderG = new SliderHorizontal(this, 6 + 55 * 1, 6 + 40, 50, 12, 255) {
			@Override
			protected void drawSlider(int mx, int my, int sliderX, int sliderY) {

				gui.bindTexture(SLIDER);
				GL11.glColor4f(1f, 1f, 1f, 1f);
				gui.drawTexturedModalRect(sliderX, sliderY, 0, 0, _sliderWidth, _sliderHeight);
			}

			@Override
			public void onValueChanged(int value) {

				GuiColor color = new GuiColor(colorDisplay.getColor());
				colorDisplay.setColor(new GuiColor(color.getIntR(), value, color.getIntB(), color.getIntA()));
				textG.setText(String.valueOf(value));
				if (!_isDragging) {
					onStopDragging();
				}
			}

			@Override
			public void onStopDragging() {

				myTile.setColor(colorDisplay.getColor() & 0xFFFFFF);
				myTile.sendUpdatePacket(Side.SERVER);
			}
		}.setValue(tileColor.getIntG()).setSliderSize(2, 12));

		addElement(textB = new ElementTextFieldLimited(this, 6 + 13 + 55 * 2, 6 + 55, 24, 10, (short) 4) {
			@Override
			protected boolean onEnter() {

				onFocusLost();
				return true;
			}

			@Override
			protected void onFocusLost() {

				sliderB.setValue(getValue(text));
			}

			@Override
			protected void onCharacterEntered(boolean success) {

				if (getValue(text) > 255)
					setText("255");
			}
		}.setFilter("0123456789", true).setText("0"));

		addElement(sliderB = new SliderHorizontal(this, 6 + 55 * 2, 6 + 40, 50, 12, 255) {
			@Override
			protected void drawSlider(int mx, int my, int sliderX, int sliderY) {

				gui.bindTexture(SLIDER);
				GL11.glColor4f(1f, 1f, 1f, 1f);
				gui.drawTexturedModalRect(sliderX, sliderY, 0, 0, _sliderWidth, _sliderHeight);
			}

			@Override
			public void onValueChanged(int value) {

				GuiColor color = new GuiColor(colorDisplay.getColor());
				colorDisplay.setColor(new GuiColor(color.getIntR(), color.getIntG(), value, color.getIntA()));
				textB.setText(String.valueOf(value));
				if (!_isDragging) {
					onStopDragging();
				}
			}

			@Override
			public void onStopDragging() {

				myTile.setColor(colorDisplay.getColor() & 0xFFFFFF);
				myTile.sendUpdatePacket(Side.SERVER);
			}
		}.setValue(tileColor.getIntB()).setSliderSize(2, 12));
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int x, int y) {

		super.drawGuiContainerForegroundLayer(x, y);

		fontRendererObj.drawString(StringHelper.localize(LOC_PATH + mode), 5 + 16 + 5, 5, 0x404040);
	}

	@Override
	public void handleElementButtonClick(String buttonName, int mouseButton) {

		if (buttonName == "Dim") {

			playSound("random.click", 1.0F, myTile.dim ? 0.6f : 0.4f);
			myTile.dim = !myTile.dim;
			myTile.getWorldObj().func_147451_t(myTile.xCoord, myTile.yCoord, myTile.zCoord);
			myTile.sendUpdatePacket(Side.SERVER);
		}
	}

	@Override
	protected void updateElementInformation() {

		int x = !myTile.dim ? 190 : 176;
		dimButton.setDisabledX(x);
		dimButton.setHoverX(x);
		dimButton.setSheetX(x);
	}

}
