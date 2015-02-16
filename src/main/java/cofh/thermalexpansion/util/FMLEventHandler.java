package cofh.thermalexpansion.util;

import cofh.thermalexpansion.core.TEAchievements;
import cofh.thermalexpansion.core.TEProps;
import cofh.thermalexpansion.network.PacketTEBase;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;

public class FMLEventHandler {

	public static FMLEventHandler instance = new FMLEventHandler();

	public static void initialize() {

		FMLCommonHandler.instance().bus().register(instance);
	}

	@SubscribeEvent
	public void onPlayerLogin(PlayerLoggedInEvent event) {

		if (TEProps.enableAchievements) {
			event.player.addStat(TEAchievements.base, 1);
		}
		PacketTEBase.sendConfigSyncPacketToClient(event.player);
	}

}
