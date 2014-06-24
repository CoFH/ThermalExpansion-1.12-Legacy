package thermalexpansion.core;

import cofh.render.IconRegistry;
import cofh.render.ItemRenderRegistry;
import cofh.render.RenderItemModular;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import net.minecraft.client.Minecraft;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.client.event.TextureStitchEvent;

import thermalexpansion.gui.client.ender.GuiTesseract;
import thermalexpansion.gui.element.ElementSlotOverlay;
import thermalexpansion.item.TEFlorbs;
import thermalexpansion.item.TEItems;
import thermalexpansion.render.RenderCache;
import thermalexpansion.render.RenderCell;
import thermalexpansion.render.RenderDynamo;
import thermalexpansion.render.RenderFrame;
import thermalexpansion.render.RenderItemFlorb;
import thermalexpansion.render.RenderLamp;
import thermalexpansion.render.RenderSchematic;
import thermalexpansion.render.RenderSponge;
import thermalexpansion.render.RenderStrongbox;
import thermalexpansion.render.RenderTank;
import thermalexpansion.render.RenderTesseract;
import thermalexpansion.render.entity.RenderEntityFlorb;

public class ProxyClient extends Proxy {

	public static RenderItemModular rendererComponent = new RenderItemModular();
	public static RenderItemFlorb rendererFlorb = new RenderItemFlorb();

	@Override
	public void registerRenderInformation() {

		ElementSlotOverlay.enableBorders = TEProps.enableGuiBorders;

		MinecraftForgeClient.registerItemRenderer(TEItems.itemDiagram, rendererComponent);
		MinecraftForgeClient.registerItemRenderer(TEFlorbs.itemFlorb, rendererFlorb);

		ItemRenderRegistry.addItemRenderer(TEItems.diagramSchematic, RenderSchematic.instance);
	}

	@Override
	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void registerIcons(TextureStitchEvent.Pre event) {

		if (event.map.getTextureType() == 1) {
			IconRegistry.addIcon("machineFrame", "thermalexpansion:component/MachineFrame", event.map);
			IconRegistry.addIcon("lampFrame", "thermalexpansion:component/LampFrame", event.map);

			IconRegistry.addIcon("IconConfigTesseract", "thermalexpansion:icons/Icon_Config_Tesseract", event.map);
			IconRegistry.addIcon("IconRecvOnly", "thermalexpansion:icons/Icon_RecvOnly", event.map);
			IconRegistry.addIcon("IconSendOnly", "thermalexpansion:icons/Icon_SendOnly", event.map);
			IconRegistry.addIcon("IconSendRecv", "thermalexpansion:icons/Icon_SendRecv", event.map);
			IconRegistry.addIcon("IconBlocked", "thermalexpansion:icons/Icon_Blocked", event.map);
			IconRegistry.addIcon("IconSlotSchematic", "thermalexpansion:icons/Icon_SlotSchematic", event.map);
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void initializeIcons(TextureStitchEvent.Post event) {

		RenderCache.initialize();
		RenderCell.initialize();
		RenderDynamo.initialize();
		RenderFrame.initialize();
		RenderLamp.initialize();
		RenderSponge.initialize();
		RenderStrongbox.initialize();
		RenderTank.initialize();
		RenderTesseract.initialize();

		RenderItemFlorb.initialize();

		RenderEntityFlorb.initialize();
	}

	@Override
	public void updateTesseractGui() {

		if (Minecraft.getMinecraft().currentScreen instanceof GuiTesseract) {
			((GuiTesseract) Minecraft.getMinecraft().currentScreen).updateNames();
		}
	}

}
