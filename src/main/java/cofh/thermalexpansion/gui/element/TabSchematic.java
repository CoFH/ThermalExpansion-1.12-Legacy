package cofh.thermalexpansion.gui.element;

import cofh.lib.gui.GuiBase;
import cofh.lib.gui.element.TabBase;
import cofh.lib.render.RenderHelper;
import cofh.lib.util.helpers.StringHelper;
import cofh.thermalexpansion.gui.container.ISchematicContainer;
import cofh.thermalexpansion.init.TEProps;
import cofh.thermalexpansion.network.PacketTEBase;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.io.IOException;
import java.util.List;

public class TabSchematic extends TabBase {

	public static int defaultSide = TabBase.LEFT;
	public static ResourceLocation GRID_TEXTURE = new ResourceLocation(TEProps.PATH_ELEMENTS + "Slot_Grid_Schematic.png");
	public static ResourceLocation OUTPUT_TEXTURE = new ResourceLocation(TEProps.PATH_ELEMENTS + "Slot_Output_Schematic.png");

	ISchematicContainer myContainer;

	public TabSchematic(GuiBase gui, ISchematicContainer theTile) {

		this(gui, defaultSide, theTile);
	}

	public TabSchematic(GuiBase gui, int side, ISchematicContainer theTile) {

		super(gui, side);

		myContainer = theTile;
		maxHeight = 92;
		maxWidth = 112;
		backgroundColor = 0x2020B0;

		for (int i = 0; i < myContainer.getCraftingSlots().length; i++) {
			myContainer.getCraftingSlots()[i].xDisplayPosition = -gui.getGuiLeft() - 16;
			myContainer.getCraftingSlots()[i].yDisplayPosition = -gui.getGuiTop() - 16;
		}
		myContainer.getResultSlot().xDisplayPosition = -gui.getGuiLeft() - 16;
		myContainer.getResultSlot().yDisplayPosition = -gui.getGuiTop() - 16;
	}

	@Override
	public void addTooltip(List<String> list) {

		if (!isFullyOpened()) {
			list.add(StringHelper.localize("item.thermalexpansion.diagram.schematic.name"));
		}
	}

	@Override
	public boolean onMousePressed(int mouseX, int mouseY, int mouseButton) throws IOException {

		if (!isFullyOpened()) {
			return false;
		}
		if (side == LEFT) {
			mouseX += currentWidth;
		}
		mouseX -= currentShiftX;
		mouseY -= currentShiftY;

		if (mouseX < 8 || mouseX >= 102 || mouseY < 20 || mouseY >= 84) {
			return false;
		}
		if (77 < mouseX && mouseX < 93 && 60 < mouseY && mouseY < 76) {

			if (myContainer.canWriteSchematic()) {
				writeSchematic();
			}
		} else {
			gui.mouseClicked(mouseButton);
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
		gui.drawTexturedModalRect(posX() + 8, posY + 20, 16, 20, 94, 64);

		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		RenderHelper.bindTexture(GRID_TEXTURE);
		gui.drawSizedTexturedModalRect(posX() + 13, posY + 25, 5, 5, 54, 54, 64, 64);
		RenderHelper.bindTexture(OUTPUT_TEXTURE);
		gui.drawSizedTexturedModalRect(posX() + 72, posY + 25, 3, 3, 26, 26, 32, 32);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
	}

	@Override
	protected void drawForeground() {

		drawTabIcon("IconSchematic");
		if (!isFullyOpened()) {
			return;
		}
		getFontRenderer().drawStringWithShadow(StringHelper.localize("item.thermalexpansion.diagram.schematic.name"), posXOffset() + 18, posY + 6, headerColor);

		if (myContainer.canWriteSchematic()) {
			gui.drawButton("IconAccept", posX() + 77, posY + 60, 0);
		} else {
			gui.drawButton("IconAcceptInactive", posX() + 77, posY + 60, 2);
		}
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
	}

	@Override
	public void setFullyOpen() {

		super.setFullyOpen();
		for (int i = 0; i < myContainer.getCraftingSlots().length; i++) {
			myContainer.getCraftingSlots()[i].xDisplayPosition = posX() + 14 + 18 * (i % 3);
			myContainer.getCraftingSlots()[i].yDisplayPosition = posY + 26 + 18 * (i / 3);
		}
		myContainer.getResultSlot().xDisplayPosition = posX() + 77;
		myContainer.getResultSlot().yDisplayPosition = posY + 30;
	}

	@Override
	public void toggleOpen() {

		if (open) {
			for (int i = 0; i < myContainer.getCraftingSlots().length; i++) {
				myContainer.getCraftingSlots()[i].xDisplayPosition = -gui.getGuiLeft() - 16;
				myContainer.getCraftingSlots()[i].yDisplayPosition = -gui.getGuiTop() - 16;
			}
			myContainer.getResultSlot().xDisplayPosition = -gui.getGuiLeft() - 16;
			myContainer.getResultSlot().yDisplayPosition = -gui.getGuiTop() - 16;
		}
		super.toggleOpen();
	}

	private boolean writeSchematic() {

		if (myContainer.canWriteSchematic()) {
			PacketTEBase.sendTabSchematicPacketToServer();
			myContainer.writeSchematic();
			return true;
		}
		return false;
	}

}
