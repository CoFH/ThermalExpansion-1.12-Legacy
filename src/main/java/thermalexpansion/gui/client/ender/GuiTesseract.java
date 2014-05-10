package thermalexpansion.gui.client.ender;

import cofh.core.CoFHProps;
import cofh.gui.GuiBaseAdv;
import cofh.gui.GuiLimitedTextField;
import cofh.gui.GuiTextList;
import cofh.gui.element.TabInfo;
import cofh.gui.element.TabRedstone;
import cofh.gui.element.TabSecurity;
import cofh.gui.element.TabTutorial;
import cofh.util.RegistryEnderAttuned;

import geologic.fluid.GLFluids;

import java.util.LinkedList;
import java.util.List;

import net.minecraft.client.gui.GuiTextField;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import thermalexpansion.block.ender.TileTesseract;
import thermalexpansion.core.TEProps;
import thermalexpansion.gui.container.ender.ContainerTesseract;
import thermalexpansion.gui.element.TabConfigTesseract;

public class GuiTesseract extends GuiBaseAdv {

	static final ResourceLocation TEXTURE = new ResourceLocation(TEProps.PATH_GUI_ENDER + "Tesseract.png");
	static final String INFO = "Use these to quickly transport things across vast distances.\n\nTune the Ender Frequency to determine links.";

	static final int TB_HEIGHT = 12;

	TileTesseract myTile;
	String playerName;

	GuiTextField tbName;
	GuiLimitedTextField tbFreq;
	GuiTextList taNamesList;

	int tempFreq = -1;
	String tempName = "";

	int tbNameX = 0;
	int tbNameY = 0;
	int tbFreqX = 0;
	int tbFreqY = 0;
	int taX = 0;
	int taY = 0;

	public GuiTesseract(InventoryPlayer inventory, TileEntity theTile) {

		super(new ContainerTesseract(inventory, theTile), TEXTURE);
		myTile = (TileTesseract) theTile;
		name = myTile.getInventoryName();
		drawInventory = false;

		playerName = inventory.player.getDisplayName();
		tempFreq = myTile.frequency;
	}

	@Override
	public void initGui() {

		super.initGui();

		addTab(new TabSecurity(this, myTile, playerName));
		addTab(new TabRedstone(this, myTile));
		addTab(new TabConfigTesseract(this, myTile, playerName));
		addTab(new TabInfo(this, INFO));
		addTab(new TabTutorial(this, CoFHProps.tutorialTabRedstone + "\n\n" + CoFHProps.tutorialTabOperation));

		tbFreqX = guiLeft + 102 + 4;
		tbFreqY = guiTop + 26 + 2;

		tbNameX = guiLeft + 8 + 4;
		tbNameY = guiTop + 42 + 2;

		taX = guiLeft + 8;
		taY = guiTop + 58;

		// Setup Text Box
		String temp = "";
		if (tbName != null) { // Stops GUI resize deleting text.
			temp = tbName.getText();
		}
		tbName = new GuiTextField(this.fontRendererObj, tbNameX, tbNameY, 128, TB_HEIGHT);
		tbName.setMaxStringLength(20);
		tbName.setText(temp);
		tbName.setEnableBackgroundDrawing(false);

		// Setup Freq Text Box
		temp = String.valueOf(tempFreq);
		if (tbFreq != null) { // Stops GUI resize deleting text.
			temp = tbFreq.getText();
		}
		tbFreq = new GuiLimitedTextField(this.fontRendererObj, tbFreqX, tbFreqY, 26, TB_HEIGHT, "0123456789");
		tbFreq.setMaxStringLength(3);
		tbFreq.setFocused(true);
		tbFreq.setEnableBackgroundDrawing(false);

		// Setup Text Area
		List<String> temp2 = new LinkedList<String>();
		if (taNamesList != null) { // Again stops GUI Resize deleting the text
			temp2 = taNamesList.textLines;
		}
		taNamesList = new GuiTextList(this.fontRendererObj, taX, taY, 128, 10);
		taNamesList.textLines = temp2;
		taNamesList.drawBackground = false;
		taNamesList.drawBorder = false;

		Keyboard.enableRepeatEvents(true);
		updateNames();
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int x, int y) {

		super.drawGuiContainerBackgroundLayer(f, x, y);
		mc.renderEngine.bindTexture(TEXTURE);

		if (canSet()) {
			if (131 <= mouseX && mouseX < 151 && 18 <= mouseY && mouseY < 38) {
				drawTexturedModalRect(guiLeft + 131, guiTop + 18, 208, 212, 20, 20);
			} else {
				drawTexturedModalRect(guiLeft + 131, guiTop + 18, 208, 192, 20, 20);
			}
		}
		if (canDisable()) {
			if (151 <= mouseX && mouseX < 171 && 18 <= mouseY && mouseY < 38) {
				drawTexturedModalRect(guiLeft + 151, guiTop + 18, 228, 212, 20, 20);
			} else {
				drawTexturedModalRect(guiLeft + 151, guiTop + 18, 228, 192, 20, 20);
			}
		}
		if (canAddEntry()) {
			if (139 <= mouseX && mouseX < 155 && 40 <= mouseY && mouseY < 56) {
				drawTexturedModalRect(guiLeft + 139, guiTop + 40, 208, 144, 16, 16);
			} else {
				drawTexturedModalRect(guiLeft + 139, guiTop + 40, 208, 128, 16, 16);
			}
		}
		if (canRemoveEntry()) {
			if (155 <= mouseX && mouseX < 171 && 40 <= mouseY && mouseY < 56) {
				drawTexturedModalRect(guiLeft + 155, guiTop + 40, 224, 144, 16, 16);
			} else {
				drawTexturedModalRect(guiLeft + 155, guiTop + 40, 224, 128, 16, 16);
			}
		}
		if (canScrollUp()) {
			if (147 <= mouseX && mouseX < 163 && 65 <= mouseY && mouseY < 81) {
				drawTexturedModalRect(guiLeft + 147, guiTop + 65, 208, 80, 16, 16);
			} else {
				drawTexturedModalRect(guiLeft + 147, guiTop + 65, 208, 64, 16, 16);
			}
		}
		if (canScrollDown()) {
			if (147 <= mouseX && mouseX < 163 && 137 <= mouseY && mouseY < 153) {
				drawTexturedModalRect(guiLeft + 147, guiTop + 137, 224, 80, 16, 16);
			} else {
				drawTexturedModalRect(guiLeft + 147, guiTop + 137, 224, 64, 16, 16);
			}
		}

		tbName.drawTextBox();
		tbFreq.drawTextBox();

		taNamesList.drawBackground();
		if (canDisable()) {
			int yHighlight = taNamesList.getSelectedLineYPos();
			if (yHighlight > -1) {
				drawFluid(taX, yHighlight, new FluidStack(GLFluids.fluidEnder, 1000), taNamesList.width, taNamesList.lineHeight);
			}
		}
		taNamesList.drawText();
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int x, int y) {

		if (myTile.frequency == -1) {
			fontRendererObj.drawString("Device Inactive", 8, 28, 0x404040);
		} else {
			fontRendererObj.drawString("Frequency: " + myTile.frequency, 8, 28, 0x404040);
		}
		super.drawGuiContainerForegroundLayer(x, y);
	}

	@Override
	public void onGuiClosed() {

		Keyboard.enableRepeatEvents(false);
		super.onGuiClosed();
	}

	@Override
	public void updateScreen() {

		super.updateScreen();

		if (!myTile.canAccess) {
			this.mc.thePlayer.closeScreen();
		}
		tbName.updateCursorCounter();
		tbFreq.updateCursorCounter();
	}

	@Override
	protected void keyTyped(char i, int j) {

		this.tbName.textboxKeyTyped(i, j);
		this.tbFreq.textboxKeyTyped(i, j);
		if (j == 1) { // esc
			this.mc.thePlayer.closeScreen();
			return;
		}
		if (this.tbFreq.isFocused()) {
			if (RegistryEnderAttuned.clientFrequencyNames.get(this.tbFreq.getText()) != null) {
				tbName.setText(RegistryEnderAttuned.clientFrequencyNames.get(tbFreq.getText()));
			}

			if (j == 18) {
				this.mc.thePlayer.closeScreen();
			}

			if (j == 28 && canSet()) { // enter

				tempFreq = Integer.parseInt(tbFreq.getText());
				myTile.setTileInfo(tempFreq);
				playSound("random.click", 1.0F, 0.8F);
			}
		} else if (this.tbName.isFocused()) {

			if (j == 28 && canAddEntry()) { // enter

				tempFreq = Integer.parseInt(tbFreq.getText());
				myTile.addEntry(tempFreq, tbName.getText());
				playSound("random.click", 1.0F, 0.7F);
			}
		}
	}

	@Override
	protected void mouseClicked(int mX, int mY, int mButton) {

		int textAreaX = taNamesList.xPos - guiLeft;
		int textAreaY = taNamesList.yPos - guiTop;

		if (textAreaX <= mouseX && mouseX < textAreaX + taNamesList.width && mouseY >= textAreaY && mouseY < textAreaY + taNamesList.height) {
			if (!taNamesList.mouseClicked(mouseX, mouseY, mButton, textAreaY).equalsIgnoreCase(tbName.getText())) {
				tbName.setText(taNamesList.mouseClicked(mouseX, mouseY, mButton, textAreaY));

				if (RegistryEnderAttuned.clientFrequencyNamesReversed.get(tbName.getText()) != null) {
					tempFreq = Integer.valueOf(RegistryEnderAttuned.clientFrequencyNamesReversed.get(tbName.getText()));
					tbFreq.setText(String.valueOf(tempFreq));
				}
			}
		} else if (tbNameX - guiLeft <= mouseX && mouseX < tbNameX - guiLeft + tbName.getWidth() && mouseY >= tbNameY - guiTop && mouseY < tbNameY - guiTop + 12) {
			tbName.setFocused(true);
			tbFreq.setFocused(false);

		} else if (tbFreqX - guiLeft <= mouseX && mouseX < tbFreqX - guiLeft + tbFreq.getWidth() && mouseY >= tbFreqY - guiTop && mouseY < tbFreqY - guiTop + 12) {
			tbName.setFocused(false);
			tbFreq.setFocused(true);

		} else if (131 <= mouseX && mouseX < 151 && 18 <= mouseY && mouseY < 38 && canSet()) {

			tempFreq = Integer.parseInt(tbFreq.getText());
			myTile.setTileInfo(tempFreq);
			playSound("random.click", 1.0F, 0.8F);

		} else if (151 <= mouseX && mouseX < 171 && 18 <= mouseY && mouseY < 38 && canDisable()) {

			myTile.setTileInfo(-1);
			playSound("random.click", 1.0F, 0.6F);

		} else if (139 <= mouseX && mouseX < 155 && 40 <= mouseY && mouseY < 56 && canAddEntry()) {

			tempFreq = Integer.parseInt(tbFreq.getText());
			myTile.addEntry(tempFreq, tbName.getText());
			playSound("random.click", 1.0F, 0.7F);

		} else if (155 <= mouseX && mouseX < 171 && 40 <= mouseY && mouseY < 56 && canRemoveEntry()) {

			tempFreq = Integer.parseInt(tbFreq.getText());
			myTile.removeEntry(tempFreq, tbName.getText());
			taNamesList.selectedLine = -1;
			playSound("random.click", 1.0F, 0.5F);

		} else if (147 <= mouseX && mouseX < 163 && 65 <= mouseY && mouseY < 81) {

			taNamesList.scrollDown();

		} else if (147 <= mouseX && mouseX < 163 && 137 <= mouseY && mouseY < 153) {

			taNamesList.scrollUp();

		} else {
			super.mouseClicked(mX, mY, mButton);
		}
	}

	@Override
	public void handleMouseInput() {

		super.handleMouseInput();

		int textAreaX = taNamesList.xPos - guiLeft;
		int textAreaY = taNamesList.yPos - guiTop;

		if (textAreaX <= mouseX && mouseX < textAreaX + taNamesList.width && mouseY >= textAreaY && mouseY < textAreaY + taNamesList.height) {
			int wheelDir = Mouse.getEventDWheel();

			if (wheelDir < 0) {
				taNamesList.scrollUp();
			}

			if (wheelDir > 0) {
				taNamesList.scrollDown();
			}
		}
	}

	@Override
	public void addTooltips(List<String> tooltip) {

		if (131 <= mouseX && mouseX < 151 && 18 <= mouseY && mouseY < 38 && canSet()) {
			tooltip.add("Set Frequency");
			return;
		}
		if (151 <= mouseX && mouseX < 171 && 18 <= mouseY && mouseY < 38 && canDisable()) {
			tooltip.add("Disable");
			return;
		}
		if (139 <= mouseX && mouseX < 155 && 40 <= mouseY && mouseY < 56 && canAddEntry()) {
			tooltip.add("Save Frequency");
			return;
		}
		if (155 <= mouseX && mouseX < 171 && 40 <= mouseY && mouseY < 56 && canRemoveEntry()) {
			tooltip.add("Delete Frequency");
			return;
		}
		super.addTooltips(tooltip);
	}

	public void updateNames() {

		taNamesList.textLines = new LinkedList<String>();
		taNamesList.selectedLine = -1;

		if (RegistryEnderAttuned.clientFrequencyNames != null && RegistryEnderAttuned.clientFrequencyNames.size() > 0) {
			if (RegistryEnderAttuned.clientFrequencyNames.get(String.valueOf(myTile.frequency)) != null) {
				tbName.setText(RegistryEnderAttuned.clientFrequencyNames.get(String.valueOf(myTile.frequency)));
				tempFreq = Integer.valueOf(RegistryEnderAttuned.clientFrequencyNamesReversed.get(tbName.getText()));
				tbFreq.setText(String.valueOf(tempFreq));
			}
			int i = 0;
			RegistryEnderAttuned.sortClientNames();
			for (String curName : RegistryEnderAttuned.clientFrequencyNames.values()) {
				taNamesList.addLine(curName);
				if (curName.equals(tbName.getText()) && myTile.frequency == Integer.valueOf(RegistryEnderAttuned.clientFrequencyNamesReversed.get(tbName.getText()))) {
					taNamesList.selectedLine = i;
				}
				i++;
			}
		}
	}

	/* CONDITIONAL HELPERS */
	private boolean canSet() {

		if (tbFreq.getText().length() == 0) {
			return false;
		}
		if (Integer.valueOf(tbFreq.getText()) == -1) {
			return false;
		}
		return true;
	}

	private boolean canDisable() {

		return myTile.frequency != -1;
	}

	private boolean canAddEntry() {

		if (tbName.getText().length() == 0) {
			return false;
		}
		if (tbFreq.getText().length() == 0) {
			return false;
		}
		String curFreq = RegistryEnderAttuned.clientFrequencyNamesReversed.get(tbName.getText());
		if (curFreq == null || curFreq.equals(tbFreq.getText())) {
			return true;
		}
		return false;
	}

	private boolean canRemoveEntry() {

		if (tbName.getText().length() == 0) {
			return false;
		}
		if (tbFreq.getText().length() == 0) {
			return false;
		}
		String curFreq = RegistryEnderAttuned.clientFrequencyNamesReversed.get(tbName.getText());

		if (curFreq == null) {
			return false;
		}

		if (curFreq.equals(tbFreq.getText())) {
			return true;
		}
		return false;
	}

	private boolean canScrollUp() {

		return taNamesList.startLine != 0;
	}

	private boolean canScrollDown() {

		return taNamesList.textLines.size() > taNamesList.displayLines && taNamesList.startLine < taNamesList.textLines.size() - taNamesList.displayLines;
	}

}
