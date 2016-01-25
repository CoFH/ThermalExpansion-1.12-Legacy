package cofh.thermalexpansion.gui.client.plate;

import cofh.core.gui.GuiBaseAdv;
import cofh.core.gui.element.TabInfo;
import cofh.core.gui.element.TabRedstone;
import cofh.core.gui.element.TabSecurity;
import cofh.core.network.PacketHandler;
import cofh.core.network.PacketTileInfo;
import cofh.lib.gui.element.ElementButton;
import cofh.lib.gui.element.ElementEnergyStored;
import cofh.lib.gui.element.ElementListBox;
import cofh.lib.gui.element.ElementSlider;
import cofh.lib.gui.element.ElementTextField;
import cofh.lib.gui.element.listbox.IListBoxElement;
import cofh.lib.gui.element.listbox.SliderVertical;
import cofh.lib.transport.IEnderChannelRegistry;
import cofh.lib.transport.IEnderChannelRegistry.Frequency;
import cofh.lib.util.helpers.SecurityHelper;
import cofh.thermalexpansion.block.plate.TilePlateTeleporter;
import cofh.thermalexpansion.core.TEProps;
import cofh.thermalexpansion.core.TeleportChannelRegistry;
import cofh.thermalexpansion.gui.container.ContainerTEBase;
import cofh.thermalexpansion.gui.element.ListBoxElementEnderText;

import java.util.UUID;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.input.Keyboard;

public class GuiPlateTeleport extends GuiBaseAdv {

	static final String TEX_PATH = TEProps.PATH_GUI + "plate/Teleport.png";
	static final ResourceLocation TEXTURE = new ResourceLocation(TEX_PATH);

	TilePlateTeleporter myTile;
	UUID playerName;

	int updated;

	ElementListBox frequencies;
	ElementSlider slider;
	ElementTextField plate_name;
	ElementTextField title;

	ElementButton assign;
	ElementButton clear;
	ElementButton add;
	ElementButton remove;

	public GuiPlateTeleport(InventoryPlayer inventory, TileEntity theTile) {

		super(new ContainerTEBase(inventory, theTile, false, false), new ResourceLocation(TEX_PATH));

		myTile = (TilePlateTeleporter) theTile;
		name = myTile.getInventoryName();
		playerName = SecurityHelper.getID(inventory.player);
		drawInventory = false;
	}

	@Override
	public void initGui() {

		super.initGui();

		generateInfo("tab.thermalexpansion.plate.translocate", 2);

		Keyboard.enableRepeatEvents(true);
		TeleportChannelRegistry.requestChannelList(myTile.getChannelString());

		if (!myInfo.isEmpty()) {
			addTab(new TabInfo(this, myInfo));
		}
		addTab(new TabRedstone(this, myTile));
		if (myTile.enableSecurity() && myTile.isSecured()) {
			addTab(new TabSecurity(this, myTile, playerName));
		}

		addElement(new ElementEnergyStored(this, 8, 8, myTile.getEnergyStorage()));

		addElement(title = new ElementTextField(this, 28, 27, 100, 11, (short) 20).setBackgroundColor(0, 0, 0));

		addElement(plate_name = new ElementTextField(this, 28, 57, 108, 11, (short) 25).setBackgroundColor(0, 0, 0).setFocusable(false));

		addElement(assign = new ElementButton(this, 131, 22, 20, 20, 176, 0, 176, 20, 176, 40, TEX_PATH) {

			@Override
			public void onClick() {

				PacketHandler.sendToServer(PacketTileInfo.newPacket(myTile).addBool(true).addBool(true).addByte(0).addString(myTile.getChannelString())
						.addString(GuiPlateTeleport.this.title.getText()));
			}
		}.setToolTip("info.cofh.enable"));
		addElement(clear = new ElementButton(this, 151, 22, 20, 20, 196, 0, 196, 20, 196, 40, TEX_PATH) {

			@Override
			public void onClick() {

				PacketHandler.sendToServer(PacketTileInfo.newPacket(myTile).addBool(true).addBool(true).addByte(-1).addString(myTile.getChannelString())
						.addString(""));
			}
		}.setToolTip("info.cofh.disable"));

		addElement(add = new ElementButton(this, 139, 54, 16, 16, 176, 60, 176, 76, 176, 92, TEX_PATH) {

			@Override
			public void onClick() {

				int tempFreq = ((Frequency) frequencies.getSelectedElement().getValue()).freq;
				PacketHandler.sendToServer(PacketTileInfo.newPacket(myTile).addBool(true).addBool(false).addInt(tempFreq));
			}
		}.setToolTip("info.cofh.setDestination"));
		addElement(remove = new ElementButton(this, 155, 54, 16, 16, 192, 60, 192, 76, 192, 92, TEX_PATH) {

			@Override
			public void onClick() {

				int tempFreq = -1;
				PacketHandler.sendToServer(PacketTileInfo.newPacket(myTile).addBool(true).addBool(false).addInt(tempFreq));
			}
		}.setToolTip("info.cofh.removeDestination"));

		addElement(frequencies = new ElementListBox(this, 7, 73, 130, 87) {

			@Override
			protected void onElementClicked(IListBoxElement element) {

				Frequency freq = (Frequency) element.getValue();
				GuiPlateTeleport.this.plate_name.setText(freq.name);
			}

			@Override
			protected void onScrollV(int newStartIndex) {

				slider.setValue(newStartIndex);
			}

			@Override
			protected int drawElement(int elementIndex, int x, int y) {

				IListBoxElement element = _elements.get(elementIndex);
				if (((Frequency) element.getValue()).freq == myTile.getDestination()) {
					element.draw(this, x, y, 1, selectedTextColor);
				} else if (elementIndex == _selectedIndex) {
					element.draw(this, x, y, selectedLineColor, selectedTextColor);
				} else {
					element.draw(this, x, y, backgroundColor, textColor);
				}

				return element.getHeight();
			}

		}.setBackgroundColor(0, 0));
		frequencies.setSelectedIndex(-1);
		IEnderChannelRegistry data = TeleportChannelRegistry.getChannels(false);
		updated = data.updated();
		for (Frequency freq : data.getFrequencyList(null)) {
			frequencies.add(new ListBoxElementEnderText(freq));
			boolean isSelf = freq.freq == myTile.getFrequency();
			if (isSelf) {
				title.setText(freq.name);
			}
			if (freq.freq == myTile.getDestination()) {
				frequencies.setSelectedIndex(frequencies.getElementCount() - 1);
				plate_name.setText(freq.name);
			} else if (isSelf) {
				frequencies.removeAt(frequencies.getElementCount() - 1);
			}
		}
		addElement(slider = new SliderVertical(this, 140, 73, 14, 87, frequencies.getLastScrollPosition()) {

			@Override
			public void onValueChanged(int value) {

				frequencies.scrollToV(value);
			}

		}.setColor(0, 0));
	}

	@Override
	public void onGuiClosed() {

		Keyboard.enableRepeatEvents(false);
		super.onGuiClosed();
	}

	@Override
	public void updateScreen() {

		super.updateScreen();

		if (!myTile.canAccess()) {
			this.mc.thePlayer.closeScreen();
		}
	}

	@Override
	protected void updateElementInformation() {

		IEnderChannelRegistry data = TeleportChannelRegistry.getChannels(false);
		if (updated != data.updated()) {
			updated = data.updated();
			IListBoxElement ele = frequencies.getSelectedElement();
			int sel = ele != null ? ((Frequency) ele.getValue()).freq : -1;
			int pos = slider.getSliderY();
			frequencies.removeAll();
			frequencies.setSelectedIndex(-1);
			for (Frequency freq : data.getFrequencyList(null)) {
				frequencies.add(new ListBoxElementEnderText(freq));
				if (freq.freq == sel) {
					frequencies.setSelectedIndex(frequencies.getElementCount() - 1);
					plate_name.setText(freq.name);
				}
				boolean isSelf = freq.freq == myTile.getFrequency();
				if (isSelf) {
					title.setText(freq.name);
				}
				if (freq.freq == myTile.getDestination()) {
					plate_name.setText(freq.name);
				} else if (isSelf) {
					frequencies.removeAt(frequencies.getElementCount() - 1);
				}
			}
			slider.setLimits(0, frequencies.getLastScrollPosition());
			slider.setValue(pos);
		}

		boolean hasName = title.getContentLength() > 0;
		assign.setEnabled(hasName && myTile.getFrequency() == -1);
		clear.setEnabled(myTile.getFrequency() != -1);
		IListBoxElement ele = frequencies.getSelectedElement();
		add.setEnabled(myTile.getDestination() == -1 && plate_name.getContentLength() > 0 && ele != null
				&& myTile.getFrequency() != ((Frequency) ele.getValue()).freq);
		remove.setEnabled(myTile.getDestination() != -1);
	}

}
