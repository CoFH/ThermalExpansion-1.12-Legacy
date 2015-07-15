package cofh.thermalexpansion.gui.client;

import cofh.core.gui.GuiBaseAdv;
import cofh.core.gui.element.TabInfo;
import cofh.core.gui.element.TabSecurity;
import cofh.lib.gui.element.ElementButton;
import cofh.lib.util.helpers.SecurityHelper;
import cofh.thermalexpansion.block.workbench.TileWorkbench;
import cofh.thermalexpansion.core.TEProps;
import cofh.thermalexpansion.gui.container.ContainerWorkbenchNew;
import cofh.thermalexpansion.network.PacketTEBase;

import java.util.UUID;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

public class GuiWorkbenchNew extends GuiBaseAdv {

	static ResourceLocation[] TEXTURES;

	static {
		for (int i = 0; i < 4; i++) {
			TEXTURES[i] = new ResourceLocation(TEProps.PATH_GUI_WORKBENCH + "Workbench" + i + 1 + ".png");
		}
	}

	TileWorkbench myTile;
	UUID playerName;
	int type;

	ElementButton setSchematic;
	ElementButton getSchematic;

	public GuiWorkbenchNew(InventoryPlayer inventory, TileEntity theTile) {

		super(new ContainerWorkbenchNew(inventory, theTile));

		myTile = (TileWorkbench) theTile;
		playerName = SecurityHelper.getID(inventory.player);
		type = myTile.getType();

		switch (type) {
		case 1:
			texture = TEXTURES[0];
			ySize = 210;
			break;
		case 2:
			texture = TEXTURES[1];
			ySize = 228;
			break;
		case 3:
			texture = TEXTURES[2];
			xSize = 212;
			ySize = 228;
			break;
		default:
			texture = TEXTURES[3];
			xSize = 230;
			ySize = 228;
			break;
		}
		name = myTile.getInventoryName();

		generateInfo("tab.thermalexpansion.workbench", 3);
	}

	@Override
	public void initGui() {

		super.initGui();

		addTab(new TabInfo(this, myInfo));
		if (myTile.enableSecurity() && myTile.isSecured()) {
			addTab(new TabSecurity(this, myTile, playerName));
		}
		switch (type) {
		case 1:
			setSchematic = (ElementButton) addElement(new ElementButton(this, 98, 55, "Set", 240, 0, 240, 16, 240, 32, 16, 16, texture.getResourcePath()));
			getSchematic = (ElementButton) addElement(new ElementButton(this, 98, 19, "Get", 240, 48, 240, 64, 240, 80, 16, 16, texture.getResourcePath()));
			break;
		case 2:
			setSchematic = (ElementButton) addElement(new ElementButton(this, 108, 55, "Set", 240, 0, 240, 16, 240, 32, 16, 16, texture.getResourcePath()));
			getSchematic = (ElementButton) addElement(new ElementButton(this, 108, 19, "Get", 240, 48, 240, 64, 240, 80, 16, 16, texture.getResourcePath()));
			break;
		case 3:
			setSchematic = (ElementButton) addElement(new ElementButton(this, 134, 55, "Set", 240, 0, 240, 16, 240, 32, 16, 16, texture.getResourcePath()));
			getSchematic = (ElementButton) addElement(new ElementButton(this, 134, 19, "Get", 240, 48, 240, 64, 240, 80, 16, 16, texture.getResourcePath()));
			break;
		default:
			setSchematic = (ElementButton) addElement(new ElementButton(this, 152, 55, "Set", 240, 0, 240, 16, 240, 32, 16, 16, texture.getResourcePath()));
			getSchematic = (ElementButton) addElement(new ElementButton(this, 152, 19, "Get", 240, 48, 240, 64, 240, 80, 16, 16, texture.getResourcePath()));
			break;
		}
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int x, int y) {

		super.drawGuiContainerBackgroundLayer(f, x, y);

		mc.renderEngine.bindTexture(texture);
		drawCurSelection();
		drawCurMissing();
	}

	@Override
	protected void updateElementInformation() {

		if (gridNotEmpty()) {
			getSchematic.setToolTip("info.thermalexpansion.gridClear");
			getSchematic.setSheetY(48);
			getSchematic.setHoverY(64);
			getSchematic.setActive();
		} else {
			if (hasValidSchematic()) {
				getSchematic.setToolTip("info.thermalexpansion.gridSet");
				getSchematic.setSheetX(96);
				getSchematic.setHoverX(112);
				getSchematic.setActive();
			} else {
				getSchematic.clearToolTip();
				getSchematic.setDisabled();
			}
		}
		if (((ContainerWorkbenchNew) inventorySlots).canWriteSchematic()) {
			setSchematic.setToolTip("info.thermalexpansion.writeSchematic");
			setSchematic.setActive();
		} else if (hasSchematic()) {
			setSchematic.setToolTip("info.thermalexpansion.noSchematic");
			setSchematic.setDisabled();
		} else {
			setSchematic.setToolTip("info.thermalexpansion.noRecipe");
			setSchematic.setDisabled();
		}
		if (myTile.updateClient) {
			myTile.createItemClient(false, ((ContainerWorkbenchNew) inventorySlots).myOutput.getStackNoUpdate());
			myTile.updateClient = false;
		}
	}

	@Override
	public void updateScreen() {

		super.updateScreen();

		if (!myTile.canAccess()) {
			this.mc.thePlayer.closeScreen();
		}
	}

	@Override
	public void handleElementButtonClick(String buttonName, int mouseButton) {

		if (buttonName.equals("Set")) {
			if (((ContainerWorkbenchNew) inventorySlots).canWriteSchematic()) {
				PacketTEBase.sendTabSchematicPacketToServer();
				playSound("random.click", 1.0F, 0.8F);
			}
		} else if (buttonName.equals("Get")) {
			if (gridNotEmpty()) {
				myTile.clearCraftingGrid();
				playSound("random.click", 1.0F, 0.6F);
			} else if (hasValidSchematic()) {
				myTile.setCraftingGrid();
				playSound("random.click", 1.0F, 0.8F);
			}
		}
	}

	@Override
	public void overlayRecipe() {

		if (!gridNotEmpty()) {
			getSchematic.setToolTip("info.thermalexpansion.gridSet");
			getSchematic.setSheetX(48);
			getSchematic.setHoverX(64);
		} else {
			getSchematic.setToolTip("info.thermalexpansion.gridClear");
			getSchematic.setSheetX(96);
			getSchematic.setHoverX(112);
		}
	}

	protected boolean gridNotEmpty() {

		for (int i = 0; i < 9; i++) {
			if (myTile.getInventorySlots(0)[i] != null) {
				return true;
			}
		}
		return false;
	}

	protected boolean hasSchematic() {

		return myTile.inventory[myTile.getCurrentSchematicSlot()] != null;
	}

	protected boolean hasValidSchematic() {

		return myTile.inventory[myTile.getCurrentSchematicSlot()] != null && myTile.inventory[myTile.getCurrentSchematicSlot()].stackTagCompound != null;
	}

	protected void drawCurMissing() {

		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				if (myTile.missingItem[j + i * 3]) {
					drawTexturedModalRect(guiLeft + 44 + j * 18, guiTop + 19 + i * 18, 176, 80, 16, 16);
				}
			}
		}
	}

	protected void drawCurSelection() {

		int offset = 0;

		if (!hasSchematic()) {
			offset = 32;
		}
		switch (myTile.selectedSchematic) {
		case 0:
			drawTexturedModalRect(guiLeft + 15, guiTop + 15, 176 + offset, 0, 20, 20);
			break;
		case 1:
			drawTexturedModalRect(guiLeft + 15, guiTop + 35, 176 + offset, 0, 20, 20);
			break;
		case 2:
			drawTexturedModalRect(guiLeft + 15, guiTop + 55, 176 + offset, 0, 20, 20);
			break;
		}
	}

}
