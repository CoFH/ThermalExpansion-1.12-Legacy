package cofh.thermalexpansion.core;

import codechicken.lib.model.DummyBakedModel;
import codechicken.lib.model.ModelRegistryHelper;
import codechicken.lib.texture.TextureUtils;
import codechicken.lib.texture.TextureUtils.IIconRegister;
import cofh.api.tileentity.ISecurable;
import cofh.core.render.IconRegistry;
import cofh.core.render.RenderItemModular;
import cofh.lib.util.helpers.SecurityHelper;
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
import cofh.thermalexpansion.block.sponge.BlockSponge;
import cofh.thermalexpansion.block.strongbox.BlockStrongbox;
import cofh.thermalexpansion.block.tank.BlockTank;
import cofh.thermalexpansion.client.IItemStackKeyGenerator;
import cofh.thermalexpansion.client.bakery.BlockBakery;
import cofh.thermalexpansion.client.model.TEBakedModel;
import cofh.thermalexpansion.item.ItemSatchel;
import cofh.thermalexpansion.item.TEAugments;
import cofh.thermalexpansion.item.TEFlorbs;
import cofh.thermalexpansion.item.TEItems;
import cofh.thermalexpansion.item.tool.ItemToolBase;
import cofh.thermalexpansion.render.*;
import cofh.thermalexpansion.render.entity.RenderEntityFlorb;
import cofh.thermalexpansion.render.item.ModelFlorb;
import cofh.thermalexpansion.render.item.SchematicBakedModel;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMap;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Locale;

public class ProxyClient extends Proxy {

    public static RenderItemModular rendererComponent = new RenderItemModular();

    private static DummyBakedModel DUMMY_MODEL = new DummyBakedModel();


    @Override
    public void registerRenderInformation() {

        //MinecraftForgeClient.registerItemRenderer(TEItems.itemDiagram, rendererComponent);
        //MinecraftForgeClient.registerItemRenderer(TEFlorbs.itemFlorb, rendererFlorb);

        //ItemRenderRegistry.addItemRenderer(TEItems.diagramSchematic, RenderSchematic.instance);
    }

    @Override
    public void preInit() {

        RenderStrongbox.registerRenderers();

        ModelLoaderRegistry.registerLoader(ModelFlorb.LoaderFlorb.INSTANCE);

        TEAugments.itemAugment.registerModelVariants();
        TEItems.itemMaterial.registerModelVariants();
        TEItems.itemCapacitor.registerModelVariants();

        registerToolModel(TEItems.itemBattleWrench, "battleWrench");
        registerToolModel(TEItems.itemChiller, "chiller");
        registerToolModel(TEItems.toolDebugger, "debugger");
        registerToolModel(TEItems.itemIgniter, "igniter");
        registerToolModel(TEItems.toolMultimeter, "multimeter");
        registerToolModel(TEItems.itemWrench, "wrench");
        registerModedToolModel(TEItems.itemPump, "pump");
        registerModedToolModel(TEItems.itemTransfuser, "transfuser");

        final int accessCount = ISecurable.AccessMode.values().length;
        final ModelResourceLocation[] satchelLocations = new ModelResourceLocation[ItemSatchel.NAMES.length * accessCount];
        for (int meta = 0; meta < ItemSatchel.NAMES.length; meta++) {
            for (int access = 0; access < accessCount; access++) {
                satchelLocations[meta * accessCount + access] = getSatchelLocation(meta, ISecurable.AccessMode.values()[access]);
            }
        }

        ModelLoader.setCustomMeshDefinition(TEItems.itemSatchel, new ItemMeshDefinition() {
            @Override
            public ModelResourceLocation getModelLocation(ItemStack stack) {
                return satchelLocations[stack.getMetadata() * accessCount + SecurityHelper.getAccess(stack).ordinal()];
            }
        });
        ModelLoader.registerItemVariants(TEItems.itemSatchel, satchelLocations);

        ModelLoader.setCustomModelResourceLocation(TEFlorbs.itemFlorb, 0, ModelFlorb.MODEL_LOCATION);
        ModelLoader.setCustomModelResourceLocation(TEFlorbs.itemFlorb, 1, ModelFlorb.MAGMATIC_MODEL_LOCATION);

        ModelLoader.setCustomModelResourceLocation(TEItems.itemDiagram, 0, getDiagramLocation("schematic_override"));
        ModelRegistryHelper.register(getDiagramLocation("schematic_override"), new SchematicBakedModel());
        ModelLoader.registerItemVariants(TEItems.itemDiagram, getDiagramLocation("schematic"));
        ModelLoader.setCustomModelResourceLocation(TEItems.itemDiagram, 1, getDiagramLocation("redprint"));

        registerBlockBakeryStuff(TEBlocks.blockMachine, "thermalexpansion:blocks/machine/machine_side", BlockMachine.TYPES);
        registerBlockBakeryStuff(TEBlocks.blockDevice, "thermalexpansion:blocks/device/device_side", BlockDevice.TYPES);
        registerBlockBakeryStuff(TEBlocks.blockDynamo, "", BlockDynamo.TYPES, RenderDynamo.instance);
        registerBlockBakeryStuff(TEBlocks.blockCell, "", BlockCell.TYPES, RenderCell.instance);
        registerBlockBakeryStuff(TEBlocks.blockTank, "", BlockTank.TYPES, RenderTank.instance);
        registerBlockBakeryStuff(TEBlocks.blockCache, "", BlockCache.TYPES);
        registerBlockBakeryStuff(TEBlocks.blockTesseract, "", BlockEnder.TYPES, RenderTesseract.instance);
        registerBlockBakeryStuff(TEBlocks.blockPlate, "", BlockPlate.TYPES, RenderPlate.instance);
        registerBlockBakeryStuff(TEBlocks.blockLight, "", BlockLight.TYPES, RenderLight.instance);

        ModelLoader.setCustomMeshDefinition(Item.getItemFromBlock(TEBlocks.blockSponge), new ItemMeshDefinition() {
            @Override
            public ModelResourceLocation getModelLocation(ItemStack stack) {
                boolean soaked = stack.getTagCompound() != null && stack.getTagCompound().hasKey("Fluid");
                return new ModelResourceLocation("thermalexpansion:sponge", "soaked=" + String.valueOf(soaked).toLowerCase() + ",type=" + BlockSponge.Types.values()[stack.getMetadata()].getName());
            }
        });

        for (int i = 0; i < BlockSponge.NAMES.length; i++) {
            ModelLoader.registerItemVariants(Item.getItemFromBlock(TEBlocks.blockSponge), new ModelResourceLocation("thermalexpansion:sponge", "soaked=false,type=" + BlockSponge.NAMES[i]));
            ModelLoader.registerItemVariants(Item.getItemFromBlock(TEBlocks.blockSponge), new ModelResourceLocation("thermalexpansion:sponge", "soaked=true,type=" + BlockSponge.NAMES[i]));
        }

        BlockBakery.registerItemKeyGenerator(Item.getItemFromBlock(TEBlocks.blockLight), new IItemStackKeyGenerator() {
            @Override
            public String generateKey(ItemStack stack) {
                StringBuilder builder = new StringBuilder();
                builder.append(stack.getMetadata());
                builder.append(",");
                builder.append(stack.getItem().getRegistryName().toString());
                builder.append(",");
                if (stack.hasTagCompound()) {
                    builder.append(stack.getTagCompound().getByte("Style"));
                }
                return builder.toString();
            }
        });

        registerBlockBakeryStuff(TEBlocks.blockFrame, "", BlockFrame.TYPES, RenderFrame.instance);

        for (EnumType type : EnumType.values()) {
            ModelResourceLocation location = new ModelResourceLocation(TEBlocks.blockWorkbench.getRegistryName(), "type=" + type.getName().toLowerCase(Locale.US));
            ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(TEBlocks.blockWorkbench), type.ordinal(), location);
        }

        for (BlockGlass.Types type : BlockGlass.Types.values()) {
            ModelResourceLocation location = new ModelResourceLocation(TEBlocks.blockGlass.getRegistryName(), "type=" + type.getName().toLowerCase(Locale.US));
            ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(TEBlocks.blockGlass), type.ordinal(), location);
        }

        for (EnumDyeColor color : EnumDyeColor.values()) {
            ModelResourceLocation location = new ModelResourceLocation(TEBlocks.blockRockwool.getRegistryName(), "color=" + color.getName().toLowerCase(Locale.US));
            ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(TEBlocks.blockRockwool), color.ordinal(), location);
        }

        final ModelResourceLocation normalLoc = new ModelResourceLocation(TEBlocks.blockStrongbox.getRegistryName(), "normal");
        ModelLoader.setCustomStateMapper(TEBlocks.blockStrongbox, (new StateMap.Builder()).ignore(BlockStrongbox.TYPES).build());
        ModelRegistryHelper.register(normalLoc, DUMMY_MODEL);
        ModelRegistryHelper.registerItemRenderer(Item.getItemFromBlock(TEBlocks.blockStrongbox), RenderStrongbox.instance);

        registerDummyModel(TEBlocks.blockAirBarrier);
        registerDummyModel(TEBlocks.blockAirForce);
        registerDummyModel(TEBlocks.blockAirLight);
        registerDummyModel(TEBlocks.blockAirSignal);

        RenderEntityFlorb.initialize();
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

    private void registerDummyModel(Block block) {
        final ModelResourceLocation normalLoc = new ModelResourceLocation(block.getRegistryName(), "normal");
        ModelLoader.setCustomStateMapper(block, (new StateMap.Builder()).build());
        ModelRegistryHelper.register(normalLoc, DUMMY_MODEL);
    }

    private ModelResourceLocation getSatchelLocation(int meta, ISecurable.AccessMode access) {
        return new ModelResourceLocation("thermalexpansion:satchel", "latch=" + access.toString().toLowerCase() + ",type=" + ItemSatchel.NAMES[meta]);
    }

    private ModelResourceLocation getToolLocation(String name) {
        return new ModelResourceLocation("thermalexpansion:tool", "type=" + name.toLowerCase());
    }

    private ModelResourceLocation getDiagramLocation(String name) {
        return new ModelResourceLocation("thermalexpansion:diagram", "type=" + name.toLowerCase());
    }

    private void registerModedToolModel(final ItemToolBase item, String name) {
        final int OUTPUT = 1;
        final ModelResourceLocation input = getToolLocation(name + "input");
        final ModelResourceLocation output = getToolLocation(name + "output");

        ModelLoader.setCustomMeshDefinition(item, new ItemMeshDefinition() {
            @Override
            public ModelResourceLocation getModelLocation(ItemStack stack) {
                return item.getMode(stack) == OUTPUT ? output : input;
            }
        });
        ModelLoader.registerItemVariants(item, input, output);
    }

    private void registerToolModel(Item item, String name) {
        registerToolModel(item, 0, name);
    }

    private void registerToolModel(ItemStack stack, String name) {
        registerToolModel(stack.getItem(), stack.getMetadata(), name);
    }

    private void registerToolModel(Item item, int metadata, String name) {
        final ModelResourceLocation location = getToolLocation(name);
        ModelLoader.setCustomModelResourceLocation(item, metadata, location);
    }

    @Override
    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void registerIcons(TextureStitchEvent.Pre event) {

        IconRegistry.addIcon("IconConfigTesseract", "thermalexpansion:items/icons/icon_Config_Tesseract", event.getMap());
        IconRegistry.addIcon("IconRecvOnly", "thermalexpansion:items/icons/icon_recvonly", event.getMap());
        IconRegistry.addIcon("IconSendOnly", "thermalexpansion:items/icons/icon_sendonly", event.getMap());
        IconRegistry.addIcon("IconSendRecv", "thermalexpansion:items/icons/icon_sendrecv", event.getMap());
        IconRegistry.addIcon("IconBlocked", "thermalexpansion:items/icons/icon_blocked", event.getMap());
        IconRegistry.addIcon("IconSchematic", "thermalexpansion:items/diagram/schematic", event.getMap());
        IconRegistry.addIcon("IconSlotSchematic", "thermalexpansion:items/icons/Icon_SlotSchematic", event.getMap());
        IconRegistry.addIcon("FlorbMask", "thermalexpansion:items/florb/florb_mask", event.getMap());
        IconRegistry.addIcon("FlorbOutline", "thermalexpansion:items/florb/florb_outline", event.getMap());

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
        RenderStrongbox.initialize();
        RenderTank.initialize();
        RenderTesseract.initialize();
    }

}
