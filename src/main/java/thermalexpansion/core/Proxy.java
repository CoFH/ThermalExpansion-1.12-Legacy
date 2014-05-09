package thermalexpansion.core;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.event.sound.SoundLoadEvent;

import thermalexpansion.ThermalExpansion;
import thermalexpansion.entity.projectile.EntityFlorb;

public class Proxy {

	public void registerEntities() {

		EntityFlorb.initialize();
	}

	public void registerRenderInformation() {

	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void registerIcons(TextureStitchEvent.Pre event) {

	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void initializeIcons(TextureStitchEvent.Post event) {

	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void initializeSounds(SoundLoadEvent event) {

	}

	public int registerGui(String guiName, String packageName, boolean isTileEntity) {

		if (packageName == null) {
			packageName = new String("");
		} else {
			packageName += ".";
		}
		Class gui = null;
		Class container = null;
		try {
			gui = Proxy.class.getClassLoader().loadClass("thermalexpansion.gui.client." + packageName + "Gui" + guiName);
		} catch (ClassNotFoundException e) {

		}
		try {
			container = Proxy.class.getClassLoader().loadClass("thermalexpansion.gui.container." + packageName + "Container" + guiName);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		if (gui == null) {
			if (isTileEntity) {
				return ThermalExpansion.guiHandler.registerServerGuiTile(container);
			}
			return ThermalExpansion.guiHandler.registerServerGui(container);
		} else {
			if (isTileEntity) {
				return ThermalExpansion.guiHandler.registerClientGuiTile(gui, container);
			}
			return ThermalExpansion.guiHandler.registerClientGui(gui, container);
		}
	}

	public int registerGui(String guiName, String packageGuiName, String containerName, String packageContainerName, boolean isTileEntity) {

		if (packageGuiName == null) {
			packageGuiName = new String("");
		} else {
			packageGuiName += ".";
		}
		if (packageContainerName == null) {
			packageContainerName = new String("");
		} else {
			packageContainerName += ".";
		}
		Class gui = null;
		Class container = null;
		try {
			gui = Proxy.class.getClassLoader().loadClass("thermalexpansion.gui.client." + packageGuiName + "Gui" + guiName);
		} catch (ClassNotFoundException e) {
		}
		try {
			container = Proxy.class.getClassLoader().loadClass("thermalexpansion.gui.container." + packageContainerName + "Container" + containerName);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		if (gui == null) {
			if (isTileEntity) {
				return ThermalExpansion.guiHandler.registerServerGuiTile(container);
			}
			return ThermalExpansion.guiHandler.registerServerGui(container);
		} else {
			if (isTileEntity) {
				return ThermalExpansion.guiHandler.registerClientGuiTile(gui, container);
			}
			return ThermalExpansion.guiHandler.registerClientGui(gui, container);
		}
	}

	public void updateTesseractGui() {

	}

}
