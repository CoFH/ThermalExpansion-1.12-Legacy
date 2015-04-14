package cofh.thermalexpansion.gui.client.device;

import cofh.core.gui.GuiBaseAdv;
import cofh.core.gui.element.TabInfo;
import cofh.core.gui.element.TabSecurity;
import cofh.lib.gui.element.ElementButton;
import cofh.lib.util.helpers.SecurityHelper;
import cofh.thermalexpansion.block.device.TileWorkbench;
import cofh.thermalexpansion.core.TEProps;
import cofh.thermalexpansion.gui.container.device.ContainerWorkbench;
import cofh.thermalexpansion.network.PacketTEBase;

import java.util.UUID;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

public class GuiWorkbench extends GuiBaseAdv {

	static final String TEX_PATH = TEProps.PATH_GUI_DEVICE + "Workbench.png";
	static final ResourceLocation TEXTURE = new ResourceLocation(TEX_PATH);

	public TileWorkbench myTile;
	UUID playerName;

	ElementButton setSchematic;
	ElementButton getSchematic;

	public GuiWorkbench(InventoryPlayer inventory, TileEntity theTile) {

		super(new ContainerWorkbench(inventory, theTile), TEXTURE);
		myTile = (TileWorkbench) theTile;
		name = myTile.getInventoryName();
		playerName = SecurityHelper.getID(inventory.player);
		ySize = 210;

		generateInfo("tab.thermalexpansion.device.workbench", 3);
	}

	@Override
	public void initGui() {

		super.initGui();

		addTab(new TabInfo(this, myInfo));
		if (myTile.enableSecurity() && myTile.isSecured()) {
			addTab(new TabSecurity(this, myTile, playerName));
		}

		setSchematic = (ElementButton) addElement(new ElementButton(this, 98, 55, "Set", 176, 32, 176, 48, 176, 64, 16, 16, TEX_PATH));
		getSchematic = (ElementButton) addElement(new ElementButton(this, 98, 19, "Get", 192, 32, 192, 48, 192, 64, 16, 16, TEX_PATH));
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
			getSchematic.setSheetX(208);
			getSchematic.setHoverX(208);
			getSchematic.setActive();
		} else {
			if (hasValidSchematic()) {
				getSchematic.setToolTip("info.thermalexpansion.gridSet");
				getSchematic.setSheetX(192);
				getSchematic.setHoverX(192);
				getSchematic.setActive();
			} else {
				getSchematic.clearToolTip();
				getSchematic.setDisabled();
			}
		}
		if (((ContainerWorkbench) inventorySlots).canWriteSchematic()) {
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
			myTile.createItemClient(false, ((ContainerWorkbench) inventorySlots).myOutput.getStackNoUpdate());
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
			if (((ContainerWorkbench) inventorySlots).canWriteSchematic()) {
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
			getSchematic.setSheetX(192);
			getSchematic.setHoverX(192);
		} else {
			getSchematic.setToolTip("info.thermalexpansion.gridClear");
			getSchematic.setSheetX(208);
			getSchematic.setHoverX(208);
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
