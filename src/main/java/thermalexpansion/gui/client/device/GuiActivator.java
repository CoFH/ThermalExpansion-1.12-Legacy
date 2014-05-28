package thermalexpansion.gui.client.device;

import cofh.core.CoFHProps;
import cofh.gui.GuiBaseAdv;
import cofh.gui.element.ElementButton;
import cofh.gui.element.TabConfiguration;
import cofh.gui.element.TabInfo;
import cofh.gui.element.TabRedstone;
import cofh.gui.element.TabTutorial;
import cofh.network.CoFHPacket;
import cofh.network.CoFHTileInfoPacket;
import cofh.network.PacketHandler;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

import thermalexpansion.block.device.TileActivator;
import thermalexpansion.core.TEProps;
import thermalexpansion.gui.container.device.ContainerActivator;

public class GuiActivator extends GuiBaseAdv {

	static final String TEX_PATH = TEProps.PATH_GUI_DEVICE + "Activator.png";
	static final ResourceLocation TEXTURE = new ResourceLocation(TEX_PATH);
	static final String INFO = "It slices! It dices! It pours things, it throws things!\n\nMaybe it's random, maybe it isn't. Maybe it's sneaky, maybe not.\n\nDeceptively versatile!";

	TileActivator myTile;

	public ElementButton settingClick;
	public ElementButton settingSneak;
	public ElementButton settingSlot;
	public ElementButton settingAngle;

	public GuiActivator(InventoryPlayer inventory, TileEntity theTile) {

		super(new ContainerActivator(inventory, theTile), TEXTURE);
		myTile = (TileActivator) theTile;
		name = myTile.getInventoryName();
	}

	@Override
	public void initGui() {

		super.initGui();

		addTab(new TabRedstone(this, myTile));
		addTab(new TabConfiguration(this, myTile));
		addTab(new TabInfo(this, INFO));
		addTab(new TabTutorial(this, CoFHProps.tutorialTabRedstone + "\n\n" + CoFHProps.tutorialTabConfiguration + "\n\n"));

		settingClick = new ElementButton(this, 120, 20, "LeftClick", myTile.leftClick ? 176 : 196, 0, myTile.leftClick ? 176 : 196, 20, 20, 20, TEX_PATH)
				.setToolTip(myTile.leftClick ? "info.thermalexpansion.activator.clickLeft" : "info.thermalexpansion.activator.clickRight");
		settingSneak = new ElementButton(this, 144, 20, "Sneak", myTile.actsSneaking ? 176 : 196, 60, myTile.actsSneaking ? 176 : 196, 80, 20, 20, TEX_PATH)
				.setToolTip(myTile.actsSneaking ? "info.thermalexpansion.activator.sneakOn" : "info.thermalexpansion.activator.sneakOff");
		settingSlot = new ElementButton(this, 120, 44, "tickSlot", myTile.tickSlot == 0 ? 176 : myTile.tickSlot == 1 ? 196 : 216, 120,
				myTile.tickSlot == 0 ? 176 : myTile.tickSlot == 1 ? 196 : 216, 140, 20, 20, TEX_PATH)
				.setToolTip(myTile.tickSlot == 0 ? "info.thermalexpansion.activator.slotsRR"
						: myTile.tickSlot == 1 ? "info.thermalexpansion.activator.slotsRand" : "info.thermalexpansion.activator.slotsFirst");
		settingAngle = new ElementButton(this, 144, 44, "Angle", myTile.angle == 0 ? 176 : myTile.angle == 1 ? 196 : 216, 180, myTile.angle == 0 ? 176
				: myTile.angle == 1 ? 196 : 216, 200, 20, 20, TEX_PATH).setToolTip(myTile.angle == 0 ? "info.thermalexpansion.activator.angleLow"
				: myTile.angle == 1 ? "info.thermalexpansion.activator.angleLevel" : "info.thermalexpansion.activator.angleHigh");

		addElement(settingClick);
		addElement(settingSneak);
		addElement(settingSlot);
		addElement(settingAngle);
	}

	@Override
	public void handleElementButtonClick(String buttonName, int mouseButton) {

		if (buttonName.equalsIgnoreCase("LeftClick")) {
			myTile.leftClick = !myTile.leftClick;
			settingClick.setToolTip(myTile.leftClick ? "info.thermalexpansion.activator.clickLeft" : "info.thermalexpansion.activator.clickRight");
			settingClick.setSheetX(myTile.leftClick ? 176 : 196);
			settingClick.setHoverX(myTile.leftClick ? 176 : 196);
			sendUpdatePacket();
			playSound("random.click", 1.0F, myTile.leftClick ? 0.8F : 0.6F);
		} else if (buttonName.equalsIgnoreCase("Sneak")) {
			myTile.actsSneaking = !myTile.actsSneaking;
			settingSneak.setToolTip(myTile.actsSneaking ? "info.thermalexpansion.activator.sneakOn" : "info.thermalexpansion.activator.sneakOff");
			settingSneak.setSheetX(myTile.actsSneaking ? 176 : 196);
			settingSneak.setHoverX(myTile.actsSneaking ? 176 : 196);
			sendUpdatePacket();
			playSound("random.click", 1.0F, myTile.actsSneaking ? 0.6F : 0.8F);
		} else if (buttonName.equalsIgnoreCase("tickSlot")) {
			myTile.tickSlot++;
			myTile.tickSlot %= 3;
			settingSlot.setToolTip(myTile.tickSlot == 0 ? "info.thermalexpansion.activator.slotsRR"
					: myTile.tickSlot == 1 ? "info.thermalexpansion.activator.slotsRand" : "info.thermalexpansion.activator.slotsFirst");
			settingSlot.setSheetX(myTile.tickSlot == 0 ? 176 : myTile.tickSlot == 1 ? 196 : 216);
			settingSlot.setHoverX(myTile.tickSlot == 0 ? 176 : myTile.tickSlot == 1 ? 196 : 216);
			sendUpdatePacket();
			playSound("random.click", 1.0F, 0.6F + myTile.tickSlot * 0.1F);
		} else if (buttonName.equalsIgnoreCase("Angle")) {
			myTile.angle++;
			myTile.angle %= 3;
			settingAngle.setToolTip(myTile.angle == 0 ? "info.thermalexpansion.activator.angleLow"
					: myTile.angle == 1 ? "info.thermalexpansion.activator.angleLevel" : "info.thermalexpansion.activator.angleHigh");
			settingAngle.setSheetX(myTile.angle == 0 ? 176 : myTile.angle == 1 ? 196 : 216);
			settingAngle.setHoverX(myTile.angle == 0 ? 176 : myTile.angle == 1 ? 196 : 216);
			sendUpdatePacket();
			playSound("random.click", 1.0F, 0.6F + myTile.angle * 0.1F);
		}
	}

	public void sendUpdatePacket() {

		CoFHPacket payload = CoFHTileInfoPacket.newPacket(myTile);
		payload.addByte(TEProps.PacketID.MODE.ordinal());
		payload.addBool(myTile.leftClick);
		payload.addBool(myTile.actsSneaking);
		payload.addByte(myTile.tickSlot);
		payload.addByte(myTile.angle);

		PacketHandler.sendToServer(payload);
	}

}
