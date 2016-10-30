package cofh.thermalexpansion.core;

import codechicken.lib.model.ModelRegistryHelper;
import codechicken.lib.texture.TextureUtils;
import codechicken.lib.texture.TextureUtils.IIconRegister;
import cofh.core.render.IconRegistry;
import cofh.core.render.RenderItemModular;
import cofh.thermalexpansion.block.EnumType;
import cofh.thermalexpansion.block.TEBlocks;
import cofh.thermalexpansion.block.cache.BlockCache;
import cofh.thermalexpansion.block.cell.BlockCell;
import cofh.thermalexpansion.block.device.BlockDevice;
import cofh.thermalexpansion.block.dynamo.BlockDynamo;
import cofh.thermalexpansion.block.ender.BlockEnder;
import cofh.thermalexpansion.block.light.BlockLight;
import cofh.thermalexpansion.block.machine.BlockMachine;
import cofh.thermalexpansion.block.plate.BlockPlate;
import cofh.thermalexpansion.block.simple.BlockFrame;
import cofh.thermalexpansion.block.simple.BlockGlass;
import cofh.thermalexpansion.block.tank.BlockTank;
import cofh.thermalexpansion.client.model.TEBakedModel;
import cofh.thermalexpansion.item.TEAugments;
import cofh.thermalexpansion.render.*;
import cofh.thermalexpansion.render.entity.RenderEntityFlorb;
import cofh.thermalexpansion.render.item.RenderItemFlorb;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMap;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Locale;

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

        RenderStrongbox.registerRenderers();

        TEAugments.itemAugment.registerModelVariants();



        registerBlockBakeryStuff(TEBlocks.blockMachine, "thermalexpansion:blocks/machine/machine_side", BlockMachine.TYPES);
        registerBlockBakeryStuff(TEBlocks.blockDevice, "thermalexpansion:blocks/device/device_side", BlockDevice.TYPES);
        registerBlockBakeryStuff(TEBlocks.blockDynamo, "", BlockDynamo.TYPES, RenderDynamo.instance);
        registerBlockBakeryStuff(TEBlocks.blockCell, "", BlockCell.TYPES, RenderCell.instance);
        registerBlockBakeryStuff(TEBlocks.blockTank, "", BlockTank.TYPES, RenderTank.instance);
        registerBlockBakeryStuff(TEBlocks.blockCache, "", BlockCache.TYPES);
        registerBlockBakeryStuff(TEBlocks.blockTesseract, "", BlockEnder.TYPES, RenderTesseract.instance);
        registerBlockBakeryStuff(TEBlocks.blockPlate, "", BlockPlate.TYPES, RenderPlate.instance);
        registerBlockBakeryStuff(TEBlocks.blockLight, "", BlockLight.TYPES, RenderLight.instance);
        registerBlockBakeryStuff(TEBlocks.blockFrame, "", BlockFrame.TYPES, RenderFrame.instance);

        for (EnumType type : EnumType.values()) {
            ModelResourceLocation location = new ModelResourceLocation(TEBlocks.blockWorkbench.getRegistryName(), "type=" + type.getName().toLowerCase(Locale.US));
            ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(TEBlocks.blockWorkbench), type.ordinal(), location);
        }

        for (BlockGlass.Types type : BlockGlass.Types.values()){
            ModelResourceLocation location = new ModelResourceLocation(TEBlocks.blockGlass.getRegistryName(), "type=" + type.getName().toLowerCase(Locale.US));
            ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(TEBlocks.blockGlass), type.ordinal(), location);
        }

        for (EnumDyeColor color : EnumDyeColor.values()){
            ModelResourceLocation location = new ModelResourceLocation(TEBlocks.blockRockwool.getRegistryName(), "color=" + color.getName().toLowerCase(Locale.US));
            ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(TEBlocks.blockRockwool), color.ordinal(), location);
        }
    }

    private static void registerBlockBakeryStuff(Block block, String particle, IProperty typeProperty, IProperty... ignores) {
        IIconRegister register = block instanceof IIconRegister ? ((IIconRegister) block) : null;
        registerBlockBakeryStuff(block, particle, typeProperty, register, ignores);
    }

    //TODO Override the particle grabbing.
    private static void registerBlockBakeryStuff(Block block, String particle, IProperty typeProperty, IIconRegister iconRegister, IProperty... ignores) {
        StateMap.Builder builder = new StateMap.Builder();
        builder.ignore(typeProperty);
        for (IProperty property : ignores) {
            builder.ignore(property);
        }

        StateMap stateMap = builder.build();
        ModelLoader.setCustomStateMapper(block, stateMap);
        IBakedModel model = new TEBakedModel(particle);
        ModelResourceLocation locationNormal = new ModelResourceLocation(block.getRegistryName(), "normal");
        ModelResourceLocation locationInventory = new ModelResourceLocation(block.getRegistryName(), "inventory");
        for (int i = 0; i < typeProperty.getAllowedValues().size(); i++) {
            ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(block), i, locationInventory);
        }

        ModelRegistryHelper.register(locationNormal, model);
        ModelRegistryHelper.register(locationInventory, model);

        if (iconRegister != null) {
            TextureUtils.addIconRegister(iconRegister);
        }
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
