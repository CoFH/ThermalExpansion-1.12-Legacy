package thermalexpansion.gui.client.light;

import cofh.core.gui.GuiBaseAdv;
import cofh.core.render.IconRegistry;
import cofh.lib.gui.GuiColor;
import cofh.lib.gui.element.ElementButton;
import cofh.lib.gui.element.ElementIcon;
import cofh.lib.gui.element.listbox.SliderHorizontal;
import cofh.lib.util.helpers.StringHelper;
import cpw.mods.fml.relauncher.Side;

import net.minecraft.inventory.Container;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;

import thermalexpansion.block.light.TileLight;
import thermalexpansion.core.TEProps;


public class GuiLight extends GuiBaseAdv {

	static final String TEX_PATH = TEProps.PATH_GUI + "Light.png";
	static final String LOC_PATH = "chat.thermalexpansion.light.";
	static final ResourceLocation TEXTURE = new ResourceLocation(TEX_PATH);

	TileLight myTile;

	ElementIcon colorDisplay;
	ElementButton dimButton;
	int mode;

	public GuiLight(Container container, TileLight tile) {

		super(container, TEXTURE);
		myTile = tile;
	}

	@Override
	public void initGui() {

		super.initGui();

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
		addElement(colorDisplay = new ElementIcon(this, 5, 5, icon).setColor(new GuiColor(myTile.getColorMultiplier(), null)));

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
			}

			@Override
			public void onStopDragging() {

				myTile.mode = (byte) _value;
				myTile.sendUpdatePacket(Side.SERVER);
			}
		}.setValue(myTile.mode));

		dimButton = new ElementButton(this, 6 + 16 + 5 + 70 + 5, 5 + 10, "Dim", 176, 0, 176, 14, 176, 28, 14, 14, TEX_PATH);
		addElement(dimButton);
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
		dimButton.setDisabledX(x).setHoverX(x).setSheetX(x);
	}

}
