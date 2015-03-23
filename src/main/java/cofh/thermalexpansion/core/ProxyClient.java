package cofh.thermalexpansion.core;

import cofh.core.render.IconRegistry;
import cofh.core.render.ItemRenderRegistry;
import cofh.core.render.RenderItemModular;
import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalexpansion.gui.client.ender.GuiTesseract;
import cofh.thermalexpansion.item.TEFlorbs;
import cofh.thermalexpansion.item.TEItems;
import cofh.thermalexpansion.render.RenderCache;
import cofh.thermalexpansion.render.RenderCell;
import cofh.thermalexpansion.render.RenderDynamo;
import cofh.thermalexpansion.render.RenderFrame;
import cofh.thermalexpansion.render.RenderLight;
import cofh.thermalexpansion.render.RenderPlate;
import cofh.thermalexpansion.render.RenderSchematic;
import cofh.thermalexpansion.render.RenderSponge;
import cofh.thermalexpansion.render.RenderStrongbox;
import cofh.thermalexpansion.render.RenderTank;
import cofh.thermalexpansion.render.RenderTesseract;
import cofh.thermalexpansion.render.entity.RenderEntityFlorb;
import cofh.thermalexpansion.render.item.RenderItemFlorb;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import net.minecraft.client.Minecraft;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.client.event.TextureStitchEvent;

public class ProxyClient extends Proxy {

	public static RenderItemModular rendererComponent = new RenderItemModular();
	public static RenderItemFlorb rendererFlorb = new RenderItemFlorb();

	@Override
	public void registerRenderInformation() {

		MinecraftForgeClient.registerItemRenderer(TEItems.itemDiagram, rendererComponent);
		MinecraftForgeClient.registerItemRenderer(TEFlorbs.itemFlorb, rendererFlorb);

		ItemRenderRegistry.addItemRenderer(TEItems.diagramSchematic, RenderSchematic.instance);

		TEProps.useAlternateShader = ThermalExpansion.configClient.get("Render", "UseAlternateShader", false,
			"Tesseracts will use an alternate starfield shader if true.");
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
			IconRegistry.addIcon("IconSchematic", "thermalexpansion:diagram/Schematic", event.map);
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
		RenderPlate.initialize();
		RenderLight.initialize();
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
