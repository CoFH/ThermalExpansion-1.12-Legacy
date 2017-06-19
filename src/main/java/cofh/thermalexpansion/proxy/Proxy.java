package cofh.thermalexpansion.proxy;

import cofh.core.render.IModelRegister;
import cofh.thermalexpansion.entity.projectile.EntityFlorb;
import cofh.thermalexpansion.init.TEAchievements;
import cofh.thermalexpansion.init.TESounds;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;

public class Proxy {

	/* INIT */
	public void preInit(FMLPreInitializationEvent event) {

		registerEntities();

		TESounds.initialize();
	}

	public void initialize(FMLInitializationEvent event) {

	}

	public void postInit(FMLPostInitializationEvent event) {

	}

	/* REGISTRATION */
	public void registerEntities() {

		EntityFlorb.initialize(0);
	}

	/* EVENT HANDLERS */
	@SubscribeEvent
	public void onPlayerLogin(PlayerLoggedInEvent event) {

		if (TEAchievements.enable) {
			event.player.addStat(TEAchievements.welcome, 1);
		}
		// PacketTEBase.sendConfigSyncPacketToClient(event.player);
	}

	/* HELPERS */
	public boolean addIModelRegister(IModelRegister modelRegister) {

		return false;
	}

}
