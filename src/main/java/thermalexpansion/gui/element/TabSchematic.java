package thermalexpansion.gui.element;

import java.util.List;

import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import thermalexpansion.core.TEProps;
import thermalexpansion.gui.container.ISetSchematic;
import thermalexpansion.network.TEPacketHandler;
import cofh.gui.GuiBase;
import cofh.gui.element.TabBase;
import cofh.render.RenderHelper;
import cofh.util.StringHelper;

public class TabSchematic extends TabBase {

	public static ResourceLocation GRID_TEXTURE = new ResourceLocation(TEProps.PATH_ELEMENTS + "Slot_Grid_Schematic.png");
	public static ResourceLocation OUTPUT_TEXTURE = new ResourceLocation(TEProps.PATH_ELEMENTS + "Slot_Output_Schematic.png");

	ISetSchematic myTile;
	int headerColor = 0xe1c92f;
	int subheaderColor = 0xaaafb8;
	int textColor = 0x000000;

	public TabSchematic(GuiBase gui, ISetSchematic tile) {

		super(gui);

		myTile = tile;
		maxHeight = 92;
		maxWidth = 112;
		backgroundColor = 0x2020B0;
		// backgroundColor = 0xCA1F7B;
	}

	@Override
	public void draw() {

		drawBackground();
		drawTabIcon("schematic");

		if (!isFullyOpened()) {
			return;
		}
		GuiBase.guiFontRenderer.drawStringWithShadow(StringHelper.localize("info.thermalexpansion.schematic"), posX + 20, posY + 6, headerColor);
		GuiBase.guiFontRenderer.drawString("", posX, posY, 0xffffff);

		if (myTile.canWriteSchematic()) {
			gui.drawButton("IconAccept", posX + 77, posY + 60, 1, 0);
		} else {
			gui.drawButton("IconAcceptInactive", posX + 77, posY + 60, 1, 2);
		}
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
	}

	@Override
	public void addTooltip(List<String> list) {

		if (!isFullyOpened()) {
			list.add(StringHelper.localize("info.thermalexpansion.schematic"));
		}
	}

	@Override
	public boolean handleMouseClicked(int x, int y, int mouseButton) {

		if (!isFullyOpened()) {
			return false;
		}
		x -= currentShiftX;
		y -= currentShiftY;

		if (x < 8 || x >= 102 || y < 20 || y >= 84) {
			return false;
		}
		if (77 < x && x < 93 && 60 < y && y < 76) {

			if (myTile.canWriteSchematic()) {
				writeSchematic();
			}
		} else {
			gui.mouseClicked(1);
		}
		return true;
	}

	@Override
	protected void drawBackground() {

		super.drawBackground();

		if (!isFullyOpened()) {
			return;
		}
		float colorR = (backgroundColor >> 16 & 255) / 255.0F * 0.6F;
		float colorG = (backgroundColor >> 8 & 255) / 255.0F * 0.6F;
		float colorB = (backgroundColor & 255) / 255.0F * 0.6F;
		GL11.glColor4f(colorR, colorG, colorB, 1.0F);
		gui.drawTexturedModalRect(posX + 8, posY + 20, 16, 20, 94, 64);

		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		RenderHelper.bindTexture(GRID_TEXTURE);
		gui.drawSizedTexturedModalRect(posX + 8, posY + 20, 0, 0, 64, 64, 64, 64);
		RenderHelper.bindTexture(OUTPUT_TEXTURE);
		gui.drawSizedTexturedModalRect(posX + 72, posY + 25, 0, 0, 26, 26, 26, 26);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
	}

	@Override
	public void setFullyOpen() {

		super.setFullyOpen();
		for (int i = 0; i < myTile.getCraftingSlots().length; i++) {
			myTile.getCraftingSlots()[i].xDisplayPosition = posX - gui.getGuiLeft() + 14 + 18 * (i % 3);
			myTile.getCraftingSlots()[i].yDisplayPosition = posY - gui.getGuiTop() + 26 + 18 * (i / 3);
		}
		myTile.getResultSlot().xDisplayPosition = posX - gui.getGuiLeft() + 77;
		myTile.getResultSlot().yDisplayPosition = posY - gui.getGuiTop() + 30;
	}

	@Override
	public void toggleOpen() {

		if (open) {
			for (int i = 0; i < myTile.getCraftingSlots().length; i++) {
				myTile.getCraftingSlots()[i].xDisplayPosition = -gui.getGuiLeft() - 16;
				myTile.getCraftingSlots()[i].yDisplayPosition = -gui.getGuiTop() - 16;
			}
			myTile.getResultSlot().xDisplayPosition = -gui.getGuiLeft() - 16;
			myTile.getResultSlot().yDisplayPosition = -gui.getGuiTop() - 16;
		}
		super.toggleOpen();
	}

	private boolean writeSchematic() {

		if (myTile.canWriteSchematic()) {
			TEPacketHandler.sendCreateSchematicPacketToServer();
			return true;
		}
		return false;
	}

}
