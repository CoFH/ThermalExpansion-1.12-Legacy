package cofh.thermalexpansion.proxy;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;

public class EventHandler {

	public static final EventHandler INSTANCE = new EventHandler();

	@SubscribeEvent
	public void handlePlayerLoggedInEvent(PlayerLoggedInEvent event) {

		// PacketTEBase.sendConfigSyncPacketToClient(event.player);
	}

}
