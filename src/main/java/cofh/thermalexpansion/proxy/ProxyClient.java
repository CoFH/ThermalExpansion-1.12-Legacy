package cofh.thermalexpansion.proxy;

import codechicken.lib.texture.TextureUtils;
import cofh.api.core.IModelRegister;
import cofh.thermalexpansion.block.storage.TileCache;
import cofh.thermalexpansion.entity.projectile.EntityFlorb;
import cofh.thermalexpansion.init.TETextures;
import cofh.thermalexpansion.render.RenderCache;
import cofh.thermalexpansion.render.entity.RenderEntityFlorb;
import cofh.thermalexpansion.render.item.ModelFlorb;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ArrayList;

public class ProxyClient extends Proxy {

	/* INIT */
	@Override
	public void preInit(FMLPreInitializationEvent event) {

		super.preInit(event);

		for (int i = 0; i < modelList.size(); i++) {
			modelList.get(i).registerModels();
		}
		registerRenderInformation();
	}

	@Override
	public void initialize(FMLInitializationEvent event) {

		super.initialize(event);
		RenderCache.initialize();
	}

	@Override
	public void postInit(FMLPostInitializationEvent event) {

		super.postInit(event);
	}

	/* REGISTRATION */
	public void registerRenderInformation() {

		TextureUtils.addIconRegister(ModelFlorb.INSTANCE);
		RenderingRegistry.registerEntityRenderingHandler(EntityFlorb.class, RenderEntityFlorb::new);
		ClientRegistry.bindTileEntitySpecialRenderer(TileCache.class, new RenderCache());
	}

	/* EVENT HANDLERS */
	@SubscribeEvent
	public void registerIcons(TextureStitchEvent.Pre event) {

		TETextures.registerIcons(event);
	}

	/* HELPERS */
	public boolean addIModelRegister(IModelRegister modelRegister) {

		return modelList.add(modelRegister);
	}

	private static ArrayList<IModelRegister> modelList = new ArrayList<>();

}
