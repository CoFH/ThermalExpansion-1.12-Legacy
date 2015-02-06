package cofh.thermalexpansion.gui.client.device;

import cofh.lib.gui.element.ElementButton;
import cofh.lib.gui.element.ElementEnergyStored;
import cofh.thermalexpansion.block.device.TileActivator;
import cofh.thermalexpansion.core.TEProps;
import cofh.thermalexpansion.gui.client.GuiAugmentableBase;
import cofh.thermalexpansion.gui.container.device.ContainerActivator;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;


public class GuiActivator extends GuiAugmentableBase {

	public static final String TEX_PATH = TEProps.PATH_GUI_DEVICE + "Activator.png";
	public static final ResourceLocation TEXTURE = new ResourceLocation(TEX_PATH);

	TileActivator myTile;

	public ElementButton settingClick;
	public ElementButton settingSneak;
	public ElementButton settingSlot;
	public ElementButton settingAngle;

	public GuiActivator(InventoryPlayer inventory, TileEntity tile) {

		super(new ContainerActivator(inventory, tile), tile, inventory.player, TEXTURE);

		generateInfo("tab.thermalexpansion.device.activator", 3);

		myTile = (TileActivator) tile;
	}

	@Override
	public void initGui() {

		super.initGui();

		addElement(new ElementEnergyStored(this, 8, 8, myTile.getEnergyStorage()));

		settingClick = new ElementButton(this, 120, 20, "LeftClick", myTile.leftClick ? 176 : 196, 0, myTile.leftClick ? 176 : 196, 20, 20, 20, TEX_PATH)
		.setToolTip(myTile.leftClick ? "info.thermalexpansion.clickLeft" : "info.thermalexpansion.clickRight");
		settingSneak = new ElementButton(this, 144, 20, "Sneak", myTile.actsSneaking ? 176 : 196, 60, myTile.actsSneaking ? 176 : 196, 80, 20, 20, TEX_PATH)
		.setToolTip(myTile.actsSneaking ? "info.thermalexpansion.sneakOn" : "info.thermalexpansion.sneakOff");
		settingSlot = new ElementButton(this, 120, 44, "tickSlot", myTile.tickSlot == 0 ? 176 : myTile.tickSlot == 1 ? 196 : 216, 120,
				myTile.tickSlot == 0 ? 176 : myTile.tickSlot == 1 ? 196 : 216, 140, 20, 20, TEX_PATH)
		.setToolTip(myTile.tickSlot == 0 ? "info.thermalexpansion.slotsRR" : myTile.tickSlot == 1 ? "info.thermalexpansion.slotsRand"
				: "info.thermalexpansion.slotsFirst");
		settingAngle = new ElementButton(this, 144, 44, "Angle", myTile.angle == 0 ? 176 : myTile.angle == 1 ? 196 : 216, 180, myTile.angle == 0 ? 176
				: myTile.angle == 1 ? 196 : 216, 200, 20, 20, TEX_PATH).setToolTip(myTile.angle == 0 ? "info.thermalexpansion.angleLow"
						: myTile.angle == 1 ? "info.thermalexpansion.angleLevel" : "info.thermalexpansion.angleHigh");

		addElement(settingClick);
		addElement(settingSneak);
		addElement(settingSlot);
		addElement(settingAngle);
	}

	@Override
	public void handleElementButtonClick(String buttonName, int mouseButton) {

		if (buttonName.equalsIgnoreCase("LeftClick")) {
			myTile.leftClick = !myTile.leftClick;
			settingClick.setToolTip(myTile.leftClick ? "info.thermalexpansion.clickLeft" : "info.thermalexpansion.clickRight");
			settingClick.setSheetX(myTile.leftClick ? 176 : 196);
			settingClick.setHoverX(myTile.leftClick ? 176 : 196);
			playSound("random.click", 1.0F, myTile.leftClick ? 0.8F : 0.6F);
		} else if (buttonName.equalsIgnoreCase("Sneak")) {
			myTile.actsSneaking = !myTile.actsSneaking;
			settingSneak.setToolTip(myTile.actsSneaking ? "info.thermalexpansion.sneakOn" : "info.thermalexpansion.sneakOff");
			settingSneak.setSheetX(myTile.actsSneaking ? 176 : 196);
			settingSneak.setHoverX(myTile.actsSneaking ? 176 : 196);
			playSound("random.click", 1.0F, myTile.actsSneaking ? 0.6F : 0.8F);
		} else if (buttonName.equalsIgnoreCase("tickSlot")) {
			myTile.tickSlot++;
			myTile.tickSlot %= 3;
			settingSlot.setToolTip(myTile.tickSlot == 0 ? "info.thermalexpansion.slotsRR" : myTile.tickSlot == 1 ? "info.thermalexpansion.slotsRand"
					: "info.thermalexpansion.slotsFirst");
			settingSlot.setSheetX(myTile.tickSlot == 0 ? 176 : myTile.tickSlot == 1 ? 196 : 216);
			settingSlot.setHoverX(myTile.tickSlot == 0 ? 176 : myTile.tickSlot == 1 ? 196 : 216);
			playSound("random.click", 1.0F, 0.6F + myTile.tickSlot * 0.1F);
		} else if (buttonName.equalsIgnoreCase("Angle")) {
			myTile.angle++;
			myTile.angle %= 3;
			settingAngle.setToolTip(myTile.angle == 0 ? "info.thermalexpansion.angleLow" : myTile.angle == 1 ? "info.thermalexpansion.angleLevel"
					: "info.thermalexpansion.angleHigh");
			settingAngle.setSheetX(myTile.angle == 0 ? 176 : myTile.angle == 1 ? 196 : 216);
			settingAngle.setHoverX(myTile.angle == 0 ? 176 : myTile.angle == 1 ? 196 : 216);
			playSound("random.click", 1.0F, 0.6F + myTile.angle * 0.1F);
		}
		myTile.sendModePacket();
	}

}
