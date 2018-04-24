package cofh.thermalexpansion.proxy;

import cofh.core.render.IModelRegister;
import cofh.thermalexpansion.block.storage.TileCache;
import cofh.thermalexpansion.block.storage.TileStrongbox;
import cofh.thermalexpansion.entity.projectile.EntityFlorb;
import cofh.thermalexpansion.entity.projectile.EntityMorb;
import cofh.thermalexpansion.init.TEItems;
import cofh.thermalexpansion.plugins.jei.machine.enchanter.EnchanterRecipeCategory;
import cofh.thermalexpansion.render.RenderCache;
import cofh.thermalexpansion.render.RenderStrongbox;
import cofh.thermalexpansion.render.entity.RenderEntityFlorb;
import cofh.thermalexpansion.render.entity.RenderEntityMorb;
import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import java.util.ArrayList;

public class ProxyClient extends Proxy {

	/* INIT */
	@Override
	public void preInit(FMLPreInitializationEvent event) {

		super.preInit(event);

		MinecraftForge.EVENT_BUS.register(EventHandlerClient.INSTANCE);

		for (IModelRegister register : modelList) {
			register.registerModels();
		}
		registerRenderInformation();
	}

	@Override
	public void initialize(FMLInitializationEvent event) {

		super.initialize(event);

		RenderCache.initialize();
		RenderStrongbox.initialize();

		Minecraft.getMinecraft().getItemColors().registerItemColorHandler(TEItems.itemCapacitor::colorMultiplier, TEItems.itemCapacitor);
		Minecraft.getMinecraft().getItemColors().registerItemColorHandler(TEItems.itemReservoir::colorMultiplier, TEItems.itemReservoir);
		Minecraft.getMinecraft().getItemColors().registerItemColorHandler(TEItems.itemSatchel::colorMultiplier, TEItems.itemSatchel);
		Minecraft.getMinecraft().getItemColors().registerItemColorHandler(TEItems.itemMorb::colorMultiplier, TEItems.itemMorb);
	}

	@Override
	public void postInit(FMLPostInitializationEvent event) {

		super.postInit(event);
	}

	@Override
	public void onIdRemap() {

		if (Loader.isModLoaded("jei")) {
			Minecraft.getMinecraft().addScheduledTask(EnchanterRecipeCategory::refresh);
		}
	}

	/* REGISTRATION */
	public void registerRenderInformation() {

		RenderingRegistry.registerEntityRenderingHandler(EntityFlorb.class, RenderEntityFlorb::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityMorb.class, RenderEntityMorb::new);
		ClientRegistry.bindTileEntitySpecialRenderer(TileCache.class, RenderCache.INSTANCE);
		ClientRegistry.bindTileEntitySpecialRenderer(TileStrongbox.class, RenderStrongbox.INSTANCE);
	}

	/* HELPERS */
	public boolean addIModelRegister(IModelRegister modelRegister) {

		return modelList.add(modelRegister);
	}

	private static ArrayList<IModelRegister> modelList = new ArrayList<>();

}
