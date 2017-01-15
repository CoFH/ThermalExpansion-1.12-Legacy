package cofh.thermalexpansion.proxy;

import cofh.thermalexpansion.entity.projectile.EntityFlorb;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.event.sound.SoundLoadEvent;
import net.minecraftforge.event.world.WorldEvent.Save;

public class Proxy {

	public void registerEntities() {

		EntityFlorb.initialize();
	}

	public void registerRenderInformation() {

	}

	public void preInit(){

    }

	@SubscribeEvent
	public void save(Save evt) {

//		if (evt.getWorld().provider.getDimension() == 0) {
//			TeleportChannelRegistry.save();
//		}
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void registerIcons(TextureStitchEvent.Pre event) {

	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void initializeIcons(TextureStitchEvent.Post event) {

	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void initializeSounds(SoundLoadEvent event) {

	}

	public void updateTesseractGui() {

	}

}
