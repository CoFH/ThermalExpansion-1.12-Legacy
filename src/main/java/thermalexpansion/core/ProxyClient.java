package thermalexpansion.core;

import cofh.render.IconRegistry;
import cofh.render.ItemRenderRegistry;
import cofh.render.RenderItemAsBlock;
import cofh.render.RenderItemModular;
import cofh.util.StringHelper;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.fluids.Fluid;

import thermalexpansion.block.ender.BlockTesseract;
import thermalexpansion.block.energycell.BlockEnergyCell;
import thermalexpansion.block.lamp.BlockLamp;
import thermalexpansion.block.machine.BlockMachine;
import thermalexpansion.fluid.TEFluids;
import thermalexpansion.gui.client.ender.GuiTesseract;
import thermalexpansion.gui.element.ElementSlotOverlay;
import thermalexpansion.item.TEItems;
import thermalexpansion.render.RenderDynamo;
import thermalexpansion.render.RenderEnergyCell;
import thermalexpansion.render.RenderItemFlorb;
import thermalexpansion.render.RenderLamp;
import thermalexpansion.render.RenderSchematic;
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

		MinecraftForgeClient.registerItemRenderer(TEItems.itemComponent, rendererComponent);
		MinecraftForgeClient.registerItemRenderer(TEItems.itemDiagram, rendererComponent);
		MinecraftForgeClient.registerItemRenderer(TEFluids.itemFlorb, rendererFlorb);

		ItemRenderRegistry.addItemRenderer(BlockMachine.machineFrame, RenderItemAsBlock.instance);
		ItemRenderRegistry.addItemRenderer(BlockEnergyCell.cellBasicFrame, RenderEnergyCell.instance);
		ItemRenderRegistry.addItemRenderer(BlockEnergyCell.cellReinforcedFrameEmpty, RenderEnergyCell.instance);
		ItemRenderRegistry.addItemRenderer(BlockEnergyCell.cellReinforcedFrameFull, RenderEnergyCell.instance);
		ItemRenderRegistry.addItemRenderer(BlockTesseract.tesseractFrameEmpty, RenderTesseract.instance);
		ItemRenderRegistry.addItemRenderer(BlockTesseract.tesseractFrameFull, RenderTesseract.instance);
		ItemRenderRegistry.addItemRenderer(BlockLamp.lampFrame, RenderItemAsBlock.instance);

		ItemRenderRegistry.addItemRenderer(TEItems.diagramSchematic, RenderSchematic.instance);
	}

	@Override
	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void registerIcons(TextureStitchEvent.Pre event) {

		if (event.map.getTextureType() == 0) {
			// registerFluidIcons(TEFluids.fluidSteam, event.map);
		} else if (event.map.getTextureType() == 1) {
			IconRegistry.addIcon("machineFrame", "thermalexpansion:component/MachineFrame", event.map);
			IconRegistry.addIcon("lampFrame", "thermalexpansion:component/LampFrame", event.map);

			IconRegistry.addIcon("IconConfigMachine", "thermalexpansion:icons/Icon_Config_Machine", event.map);
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

		// setFluidIcons(TEFluids.fluidSteam);

		RenderDynamo.initialize();
		RenderEnergyCell.initialize();
		RenderTank.initialize();
		RenderStrongbox.initialize();
		RenderTesseract.initialize();
		RenderLamp.initialize();

		RenderEntityFlorb.initialize();
	}

	@Override
	public void updateTesseractGui() {

		if (Minecraft.getMinecraft().currentScreen instanceof GuiTesseract) {
			((GuiTesseract) Minecraft.getMinecraft().currentScreen).updateNames();
		}
	}

	public static void registerFluidIcons(Fluid fluid, IIconRegister ir) {

		String name = StringHelper.titleCase(fluid.getName());
		IconRegistry.addIcon("Fluid" + name, "thermalexpansion:fluid/Fluid_" + name + "_Still", ir);
		IconRegistry.addIcon("Fluid" + name + 1, "thermalexpansion:fluid/Fluid_" + name + "_Flow", ir);
	}

	public static void setFluidIcons(Fluid fluid) {

		String name = StringHelper.titleCase(fluid.getName());
		fluid.setIcons(IconRegistry.getIcon("Fluid" + name), IconRegistry.getIcon("Fluid" + name, 1));
	}

}
