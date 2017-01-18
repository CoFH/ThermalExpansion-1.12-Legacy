package cofh.thermalexpansion.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;

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

		//ThermalExpansion.instance.resetClientConfigs();
	}

}
