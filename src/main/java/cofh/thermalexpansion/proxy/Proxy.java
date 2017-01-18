package cofh.thermalexpansion.proxy;

import cofh.api.core.IModelRegister;
import cofh.thermalexpansion.entity.projectile.EntityFlorb;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class Proxy {


	/* INIT */
	public void preInit(FMLPreInitializationEvent event) {

	}

	public void initialize(FMLInitializationEvent event) {

	}

	public void postInit(FMLPostInitializationEvent event) {

	}

	/* REGISTRATION */
	public void registerEntities() {

		EntityFlorb.initialize(1);
	}

	/* HELPERS */
	public boolean addIModelRegister(IModelRegister modelRegister) {

		return false;
	}

}
