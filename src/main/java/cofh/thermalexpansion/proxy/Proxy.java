package cofh.thermalexpansion.proxy;

import cofh.core.render.IModelRegister;
import cofh.thermalexpansion.entity.projectile.EntityFlorb;
import cofh.thermalexpansion.entity.projectile.EntityMorb;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class Proxy {

	/* INIT */
	public void preInit(FMLPreInitializationEvent event) {

		MinecraftForge.EVENT_BUS.register(EventHandler.INSTANCE);

		registerEntities();
	}

	public void initialize(FMLInitializationEvent event) {

	}

	public void postInit(FMLPostInitializationEvent event) {

	}

	/* REGISTRATION */
	public void registerEntities() {

		EntityFlorb.initialize(0);
		EntityMorb.initialize(1);
	}

	/* HELPERS */
	public boolean addIModelRegister(IModelRegister modelRegister) {

		return false;
	}

}
