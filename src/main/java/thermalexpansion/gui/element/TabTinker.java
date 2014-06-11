package thermalexpansion.gui.element;

import cofh.api.tileentity.ITinkerableTile;
import cofh.gui.GuiBase;
import cofh.gui.element.TabBase;
import cofh.util.StringHelper;

import java.util.List;

import org.lwjgl.opengl.GL11;

public class TabTinker extends TabBase {

	ITinkerableTile myTile;

	public TabTinker(GuiBase gui, ITinkerableTile tile) {

		super(gui);

		myTile = tile;
		maxHeight = 92;
		maxWidth = 112;
	}

	@Override
	public void draw() {

		drawBackground();
		drawTabIcon("tinker");

		if (!isFullyOpened()) {
			return;
		}
		getFontRenderer().drawStringWithShadow(StringHelper.localize("info.thermalexpansion.tinker"), posX + 20, posY + 6, headerColor);

		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
	}

	@Override
	public void addTooltip(List<String> list) {

		if (!isFullyOpened()) {
			list.add(StringHelper.localize("info.thermalexpansion.tinker"));
		}
	}

}
