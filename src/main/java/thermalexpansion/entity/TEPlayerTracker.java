package thermalexpansion.entity;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;

import thermalexpansion.core.TEAchievements;
import thermalexpansion.core.TEProps;
import thermalexpansion.network.GenericTEPacket;

public class TEPlayerTracker {

	public static TEPlayerTracker instance = new TEPlayerTracker();

	public static void initialize() {

		FMLCommonHandler.instance().bus().register(instance);
	}

	@SubscribeEvent
	public void onPlayerLogin(PlayerLoggedInEvent evt) {

		if (TEProps.enableAchievements) {
			evt.player.addStat(TEAchievements.baseTE, 1);
		}
		GenericTEPacket.sendConfigSyncPacketToClient(evt.player);
	}

}
