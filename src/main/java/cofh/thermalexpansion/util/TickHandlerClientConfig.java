package cofh.thermalexpansion.util;

import cofh.thermalexpansion.ThermalExpansion;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.ClientTickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiMainMenu;


public class TickHandlerClientConfig {

	public static TickHandlerClientConfig instance = new TickHandlerClientConfig();

	public boolean needsMenu = false;

	@SubscribeEvent
	public void clientTick(ClientTickEvent theEvt) {

		if (theEvt.phase == Phase.END) {
			Minecraft mc = Minecraft.getMinecraft();

			if (mc.currentScreen instanceof GuiMainMenu) {
				if (needsMenu) {
					onMainMenu();
					needsMenu = false;
				}
			} else if (mc.inGameHasFocus) {
				needsMenu = true;
			}

		}
	}

	public void onMainMenu() {

		ThermalExpansion.instance.resetClientConfigs();
	}

}
