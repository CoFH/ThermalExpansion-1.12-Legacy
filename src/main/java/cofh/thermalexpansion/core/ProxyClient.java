package cofh.thermalexpansion.core;

import codechicken.lib.model.ModelRegistryHelper;
import codechicken.lib.render.CCIconRegister;
import codechicken.lib.texture.TextureUtils;
import cofh.core.render.IconRegistry;
import cofh.core.render.RenderItemModular;
import cofh.thermalexpansion.block.TEBlocks;
import cofh.thermalexpansion.block.machine.BlockMachine;
import cofh.thermalexpansion.block.machine.BlockMachine.Types;
import cofh.thermalexpansion.client.model.TFBakedModel;
import cofh.thermalexpansion.render.*;
import cofh.thermalexpansion.render.entity.RenderEntityFlorb;
import cofh.thermalexpansion.render.item.RenderItemFlorb;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMap;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.item.Item;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ProxyClient extends Proxy {

    public static RenderItemModular rendererComponent = new RenderItemModular();
    public static RenderItemFlorb rendererFlorb = new RenderItemFlorb();

    @Override
    public void registerRenderInformation() {

        //MinecraftForgeClient.registerItemRenderer(TEItems.itemDiagram, rendererComponent);
        //MinecraftForgeClient.registerItemRenderer(TEFlorbs.itemFlorb, rendererFlorb);

        //ItemRenderRegistry.addItemRenderer(TEItems.diagramSchematic, RenderSchematic.instance);
    }

    @Override
    public void preInit() {
        StateMap stateMap = new StateMap.Builder().ignore(BlockMachine.TYPES).build();
        ModelLoader.setCustomStateMapper(TEBlocks.blockMachine, stateMap);

        TFBakedModel model = new TFBakedModel();
        ModelResourceLocation locationNormal = new ModelResourceLocation(TEBlocks.blockMachine.getRegistryName(), "normal");
        ModelResourceLocation locationInventory = new ModelResourceLocation(TEBlocks.blockMachine.getRegistryName(), "inventory");
        for (int i = 0; i < Types.values().length; i++) {
            ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(TEBlocks.blockMachine), i, locationInventory);
        }
        ModelRegistryHelper.register(locationNormal, model);
        ModelRegistryHelper.register(locationInventory, model);

        TextureUtils.addIconRegister(TEBlocks.blockMachine);


    }

    @Override
    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void registerIcons(TextureStitchEvent.Pre event) {

        IconRegistry.addIcon("IconConfigTesseract", "thermalexpansion:icons/Icon_Config_Tesseract", event.getMap());
        IconRegistry.addIcon("IconRecvOnly", "thermalexpansion:icons/Icon_RecvOnly", event.getMap());
        IconRegistry.addIcon("IconSendOnly", "thermalexpansion:icons/Icon_SendOnly", event.getMap());
        IconRegistry.addIcon("IconSendRecv", "thermalexpansion:icons/Icon_SendRecv", event.getMap());
        IconRegistry.addIcon("IconBlocked", "thermalexpansion:icons/Icon_Blocked", event.getMap());
        IconRegistry.addIcon("IconSchematic", "thermalexpansion:diagram/Schematic", event.getMap());
        IconRegistry.addIcon("IconSlotSchematic", "thermalexpansion:icons/Icon_SlotSchematic", event.getMap());

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

}
