package cofh.thermalexpansion.gui.client;

import cofh.core.gui.GuiBaseAdv;
import cofh.core.gui.element.TabInfo;
import cofh.core.gui.element.TabSecurity;
import cofh.lib.gui.element.ElementButton;
import cofh.lib.util.helpers.SecurityHelper;
import cofh.lib.util.helpers.StringHelper;
import cofh.thermalexpansion.block.EnumType;
import cofh.thermalexpansion.block.workbench.TileWorkbench;
import cofh.thermalexpansion.core.TEProps;
import cofh.thermalexpansion.gui.container.ContainerWorkbench;
import cofh.thermalexpansion.network.PacketTEBase;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

import java.util.UUID;

public class GuiWorkbench extends GuiBaseAdv {

	static String[] TEX_PATH = new String[4];
	static ResourceLocation[] TEXTURES = new ResourceLocation[4];

	static {
		for (int i = 0; i < 4; i++) {
			TEX_PATH[i] = TEProps.PATH_GUI_WORKBENCH + "workbench_" + (i + 1) + ".png";
			TEXTURES[i] = new ResourceLocation(TEX_PATH[i]);
		}
	}

	public TileWorkbench myTile;
	UUID playerName;
	int type;
	int gridXOffset = 44;

	int schematicOffset = 17;
	int schematicPerRow = 1;

	ElementButton setSchematic;
	ElementButton getSchematic;

	public GuiWorkbench(InventoryPlayer inventory, TileEntity theTile) {

		super(new ContainerWorkbench(inventory, theTile));

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
				gridXOffset = 54;
				schematicOffset = 10;
				schematicPerRow = 2;
				break;
			case 3:
				texture = TEXTURES[2];
				xSize = 212;
				ySize = 228;
				gridXOffset = 80;
				schematicOffset = 16;
				schematicPerRow = 3;
				break;
			default:
				texture = TEXTURES[3];
				xSize = 230;
				ySize = 228;
				gridXOffset = 98;
				schematicOffset = 16;
				schematicPerRow = 4;
				break;
		}
		name = myTile.getName();

		generateInfo("tab.thermalexpansion.workbench", 3);

		if (myTile.type == EnumType.CREATIVE.ordinal()) {
			myInfo += "\n\n" + StringHelper.localize("tab.thermalexpansion.workbench.creative");
		}

	}

	@Override
	public void initGui() {

		super.initGui();

		addTab(new TabInfo(this, myInfo));
		if (myTile.enableSecurity() && myTile.isSecured()) {
			addTab(new TabSecurity(this, myTile, playerName));
		}
		setSchematic = (ElementButton) addElement(new ElementButton(this, gridXOffset + 54, 55, "Set", 240, 0, 240, 16, 240, 32, 16, 16, TEX_PATH[0]));
		getSchematic = (ElementButton) addElement(new ElementButton(this, gridXOffset + 54, 19, "Get", 240, 48, 240, 64, 240, 80, 16, 16, TEX_PATH[0]));
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
			getSchematic.setSheetY(96);
			getSchematic.setHoverY(112);
			getSchematic.setActive();
		} else {
			if (hasValidSchematic()) {
				getSchematic.setToolTip("info.thermalexpansion.gridSet");
				getSchematic.setSheetY(48);
				getSchematic.setHoverY(64);
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
			setSchematic.setToolTip("info.thermalexpansion.noRecipe");
			setSchematic.setDisabled();
		} else {
			setSchematic.setToolTip("info.thermalexpansion.noSchematic");
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
				playClickSound(1.0F, 0.8F);
			}
		} else if (buttonName.equals("Get")) {
			if (gridNotEmpty()) {
				myTile.clearCraftingGrid();
				playClickSound(1.0F, 0.6F);
			} else if (hasValidSchematic()) {
				myTile.setCraftingGrid();
				playClickSound(1.0F, 0.8F);
			}
		}
	}

	@Override
	public void overlayRecipe() {

		if (!gridNotEmpty()) {
			getSchematic.setToolTip("info.thermalexpansion.gridSet");
			getSchematic.setSheetY(48);
			getSchematic.setHoverY(64);
		} else {
			getSchematic.setToolTip("info.thermalexpansion.gridClear");
			getSchematic.setSheetY(96);
			getSchematic.setHoverY(112);
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

		return myTile.inventory[myTile.getCurrentSchematicSlot()] != null && myTile.inventory[myTile.getCurrentSchematicSlot()].getTagCompound() != null;
	}

	protected void drawCurMissing() {

		int offset = 144;

		if (myTile.type == EnumType.CREATIVE.ordinal()) {
			offset = 160;
		}
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				if (myTile.missingItem[j + i * 3]) {
					drawTexturedModalRect(guiLeft + gridXOffset + j * 18, guiTop + 19 + i * 18, 240, offset, 16, 16);
				}
			}
		}
	}

	protected void drawCurSelection() {

		int offset = !hasSchematic() ? 20 : 0;
		int x = guiLeft + schematicOffset - 2 + (myTile.selectedSchematic % schematicPerRow) * 19;
		int y = guiTop + 16 + (myTile.selectedSchematic / schematicPerRow) * 19;

		drawTexturedModalRect(x, y, 236, 176 + offset, 20, 20);
	}

}
