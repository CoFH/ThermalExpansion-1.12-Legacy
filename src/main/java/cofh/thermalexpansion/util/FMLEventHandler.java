package cofh.thermalexpansion.util;

import cofh.thermalexpansion.init.TEAchievements;
import cofh.thermalexpansion.init.TEProps;
import cofh.thermalexpansion.network.PacketTEBase;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;

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
