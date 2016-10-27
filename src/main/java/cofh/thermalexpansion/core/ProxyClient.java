package cofh.thermalexpansion.core;

import codechicken.lib.model.ModelRegistryHelper;
import codechicken.lib.texture.TextureUtils;
import cofh.core.render.IconRegistry;
import cofh.core.render.RenderItemModular;
import cofh.thermalexpansion.block.TEBlocks;
import cofh.thermalexpansion.block.device.BlockDevice;
import cofh.thermalexpansion.block.dynamo.BlockDynamo;
import cofh.thermalexpansion.block.machine.BlockMachine;
import cofh.thermalexpansion.block.machine.BlockMachine.Types;
import cofh.thermalexpansion.client.model.TEBakedModel;
import cofh.thermalexpansion.render.*;
import cofh.thermalexpansion.render.entity.RenderEntityFlorb;
import cofh.thermalexpansion.render.item.RenderItemFlorb;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMap;
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

        TEBakedModel model = new TEBakedModel("thermalexpansion:blocks/machine/machine_side");
        ModelResourceLocation locationNormal = new ModelResourceLocation(TEBlocks.blockMachine.getRegistryName(), "normal");
        ModelResourceLocation locationInventory = new ModelResourceLocation(TEBlocks.blockMachine.getRegistryName(), "inventory");
        for (int i = 0; i < BlockMachine.Types.values().length; i++) {
            ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(TEBlocks.blockMachine), i, locationInventory);
        }
        ModelRegistryHelper.register(locationNormal, model);
        ModelRegistryHelper.register(locationInventory, model);

        TextureUtils.addIconRegister(TEBlocks.blockMachine);

        StateMap deviceStateMap = new StateMap.Builder().ignore(BlockDevice.TYPES).build();
        ModelLoader.setCustomStateMapper(TEBlocks.blockDevice, deviceStateMap);
        model = new TEBakedModel("thermalexpansion:blocks/device/device_side");
        locationNormal = new ModelResourceLocation(TEBlocks.blockDevice.getRegistryName(), "normal");
        locationInventory = new ModelResourceLocation(TEBlocks.blockDevice.getRegistryName(), "inventory");
        for (int i = 0; i < BlockDevice.Types.values().length; i++) {
            ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(TEBlocks.blockDevice), i, locationInventory);
        }
        ModelRegistryHelper.register(locationNormal, model);
        ModelRegistryHelper.register(locationInventory, model);

        TextureUtils.addIconRegister(TEBlocks.blockDevice);

        StateMap dynamoStateMap = new StateMap.Builder().ignore(BlockDynamo.TYPES).build();
        ModelLoader.setCustomStateMapper(TEBlocks.blockDynamo, dynamoStateMap);
        model = new TEBakedModel("");
        locationNormal = new ModelResourceLocation(TEBlocks.blockDynamo.getRegistryName(), "normal");
        locationInventory = new ModelResourceLocation(TEBlocks.blockDynamo.getRegistryName(), "inventory");
        for (int i = 0; i < BlockDynamo.Types.values().length; i++) {
            ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(TEBlocks.blockDynamo), i, locationInventory);
        }

        ModelRegistryHelper.register(locationNormal, model);
        ModelRegistryHelper.register(locationInventory, model);

        TextureUtils.addIconRegister(RenderDynamo.instance);
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
