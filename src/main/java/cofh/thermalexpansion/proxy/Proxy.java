package cofh.thermalexpansion.proxy;

import cofh.core.render.IModelRegister;
import cofh.thermalexpansion.entity.projectile.EntityFlorb;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;

public class Proxy {

	/* INIT */
	public void preInit(FMLPreInitializationEvent event) {

		registerEntities();
	}

	public void initialize(FMLInitializationEvent event) {

	}

	public void postInit(FMLPostInitializationEvent event) {

	}

	/* REGISTRATION */
	public void registerEntities() {

		EntityFlorb.initialize(0);
	}

	/* EVENT HANDLING */
	@SubscribeEvent
	public void handlePlayerLoggedInEvent(PlayerLoggedInEvent event) {

		// PacketTEBase.sendConfigSyncPacketToClient(event.player);
	}

	/* HELPERS */
	public boolean addIModelRegister(IModelRegister modelRegister) {

		return false;
	}

}
