package thermalexpansion.util;

import java.util.EnumSet;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiMainMenu;

import thermalexpansion.ThermalExpansion;

public class TickHandlerClientConfig implements ITickHandler {

	public static TickHandlerClientConfig instance = new TickHandlerClientConfig();

	public boolean needsMenu = false;

	@Override
	public void tickStart(EnumSet<TickType> type, Object... tickData) {

	}

	@Override
	public void tickEnd(EnumSet<TickType> type, Object... tickData) {

		Minecraft mc = Minecraft.getMinecraft();

		if (type.contains(TickType.CLIENT)) {
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

	@Override
	public EnumSet<TickType> ticks() {

		return EnumSet.of(TickType.CLIENT);
	}

	@Override
	public String getLabel() {

		return "thermalexpansion.clientconfig";
	}

	public void onMainMenu() {

		ThermalExpansion.instance.resetClientConfigs();
	}

}
