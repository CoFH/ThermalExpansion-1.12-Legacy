package cofh.thermalexpansion.render;

import codechicken.lib.model.bakery.PlanarFaceBakery;
import codechicken.lib.render.CCModel;
import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.buffer.BakingVertexBuffer;
import codechicken.lib.texture.TextureUtils;
import codechicken.lib.texture.TextureUtils.IIconRegister;
import codechicken.lib.vec.Cuboid6;
import cofh.api.energy.IEnergyContainerItem;
import cofh.core.render.IconRegistry;
import cofh.core.render.RenderUtils;
import cofh.lib.render.RenderHelper;
import cofh.thermalexpansion.block.CommonProperties;
import cofh.thermalexpansion.block.cell.BlockCell;
import cofh.thermalexpansion.block.cell.TileCell;
import cofh.thermalexpansion.client.bakery.ILayeredBlockBakery;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SideOnly(Side.CLIENT)
public class RenderCell implements IIconRegister, ILayeredBlockBakery {

    public static final RenderCell instance = new RenderCell();

    static TextureAtlasSprite[] textureCenter = new TextureAtlasSprite[2];
    static TextureAtlasSprite[] textureFrame = new TextureAtlasSprite[BlockCell.Types.values().length * 2];
    static CCModel modelCenter = CCModel.quadModel(24);
    static CCModel modelFrame = CCModel.quadModel(48);

    static {

        modelCenter.generateBlock(0, 0.15, 0.15, 0.15, 0.85, 0.85, 0.85).computeNormals();

        Cuboid6 box = new Cuboid6(0, 0, 0, 1, 1, 1);
        double inset = 0.1875;
        modelFrame = CCModel.quadModel(48).generateBlock(0, box);
        CCModel.generateBackface(modelFrame, 0, modelFrame, 24, 24);
        modelFrame.computeNormals();
        for (int i = 24; i < 48; i++) {
            modelFrame.verts[i].vec.add(modelFrame.normals()[i].copy().multiply(inset));//TODO Model shrinking inside CCModel.
        }
        modelFrame.shrinkUVs(RenderHelper.RENDER_OFFSET);
    }

    public static void initialize() {

        textureCenter[0] = IconRegistry.getIcon("StorageRedstone");
        textureCenter[1] = IconRegistry.getIcon("FluidRedstone");

        for (int i = 0; i < textureFrame.length; i++) {
            textureFrame[i] = IconRegistry.getIcon("Cell", i);
        }
    }

    @Override
    public IExtendedBlockState handleState(IExtendedBlockState state, TileEntity tileEntity) {
        TileCell cell = (TileCell) tileEntity;
        HashMap<EnumFacing, TextureAtlasSprite> p2 = new HashMap<EnumFacing, TextureAtlasSprite>();
        for (EnumFacing face : EnumFacing.VALUES) {
            p2.put(face, cell.getTexture(face.ordinal(), 2));
        }
        state = state.withProperty(CommonProperties.SPRITE_FACE_LAYER_PROPERTY, p2);
        state = state.withProperty(CommonProperties.TYPE_PROPERTY, (int) cell.type);
        state = state.withProperty(BlockCell.CHARGE_PROPERTY, Math.min(15, cell.getScaledEnergyStored(16)));
        state = state.withProperty(CommonProperties.FACING_PROPERTY, cell.getFacing());
        state = state.withProperty(CommonProperties.ACTIVE_SPRITE_PROPERTY, new ResourceLocation(cell.getTexture(cell.getFacing(), 3).getIconName()));
        return state;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<BakedQuad> bakeLayerFace(EnumFacing face, int pass, BlockRenderLayer layer, IExtendedBlockState state) {
        if (face == null) {
            Map<EnumFacing, TextureAtlasSprite> spriteMap = state.getValue(CommonProperties.SPRITE_FACE_LAYER_PROPERTY);
            int type = state.getValue(CommonProperties.TYPE_PROPERTY);
            int charge = state.getValue(BlockCell.CHARGE_PROPERTY);
            int facing = state.getValue(CommonProperties.FACING_PROPERTY);
            TextureAtlasSprite frontFace = TextureUtils.getTexture(state.getValue(CommonProperties.ACTIVE_SPRITE_PROPERTY));

            BakingVertexBuffer buffer = BakingVertexBuffer.create();
            buffer.begin(7, DefaultVertexFormats.ITEM);
            CCRenderState ccrs = CCRenderState.instance();
            ccrs.reset();
            ccrs.bind(buffer);

            if (pass == 0) {
                renderFrame(ccrs, type, spriteMap, facing, frontFace);
                if (hasSolidCenter(type)) {
                    renderCenter(ccrs, type);
                }
            } else if (!hasSolidCenter(type)) {
                //TODO Center brightness.
                //ccrs.brightness = 165 + charge * 5;
                renderCenter(ccrs, type);
            }

            buffer.finishDrawing();
            return buffer.bake();
        }
        return new ArrayList<BakedQuad>();
    }

    private boolean hasSolidCenter(int type) {
        return type == BlockCell.Types.BASIC.meta() || type == BlockCell.Types.HARDENED.meta();
    }

    @Override
    public List<BakedQuad> bakeItemQuads(EnumFacing face, ItemStack stack) {
        if (face == null) {
            BakingVertexBuffer buffer = BakingVertexBuffer.create();
            buffer.begin(7, DefaultVertexFormats.ITEM);
            CCRenderState ccrs = CCRenderState.instance();
            ccrs.reset();
            ccrs.bind(buffer);

            //if (pass == 0) {
            renderFrame(ccrs, stack.getItemDamage(), null, 0, null);
            //TODO Center brightness.
            //ccrs.brightness = 165 + charge * 5;
            renderCenter(ccrs, stack.getItemDamage());
            //} else {
            //}

            buffer.finishDrawing();
            return PlanarFaceBakery.shadeQuadFaces(buffer.bake());
        }
        return new ArrayList<BakedQuad>();
    }

    @Override
    public void registerIcons(TextureMap textureMap) {

        for (int i = 0; i < 9; i++) {
            IconRegistry.addIcon("CellMeter" + i, "thermalexpansion:blocks/cell/cell_meter_" + i, textureMap);
        }
        IconRegistry.addIcon("CellMeterCreative", "thermalexpansion:blocks/cell/cell_meter_creative", textureMap);
        IconRegistry.addIcon("Cell" + 0, "thermalexpansion:blocks/cell/cell_creative", textureMap);
        IconRegistry.addIcon("Cell" + 1, "thermalexpansion:blocks/cell/cell_creative_inner", textureMap);
        IconRegistry.addIcon("Cell" + 2, "thermalexpansion:blocks/cell/cell_basic", textureMap);
        IconRegistry.addIcon("Cell" + 3, "thermalexpansion:blocks/cell/cell_basic_inner", textureMap);
        IconRegistry.addIcon("Cell" + 4, "thermalexpansion:blocks/cell/cell_hardened", textureMap);
        IconRegistry.addIcon("Cell" + 5, "thermalexpansion:blocks/cell/cell_hardened_inner", textureMap);
        IconRegistry.addIcon("Cell" + 6, "thermalexpansion:blocks/cell/cell_reinforced", textureMap);
        IconRegistry.addIcon("Cell" + 7, "thermalexpansion:blocks/cell/cell_reinforced_inner", textureMap);
        IconRegistry.addIcon("Cell" + 8, "thermalexpansion:blocks/cell/cell_resonant", textureMap);
        IconRegistry.addIcon("Cell" + 9, "thermalexpansion:blocks/cell/cell_resonant_inner", textureMap);

        IconRegistry.addIcon(BlockCell.TEXTURE_DEFAULT + 0, "thermalexpansion:blocks/config/config_none", textureMap);
        IconRegistry.addIcon(BlockCell.TEXTURE_DEFAULT + 1, "thermalexpansion:blocks/cell/cell_config_orange", textureMap);
        IconRegistry.addIcon(BlockCell.TEXTURE_DEFAULT + 2, "thermalexpansion:blocks/cell/cell_config_blue", textureMap);

        IconRegistry.addIcon(BlockCell.TEXTURE_CB + 0, "thermalexpansion:blocks/config/config_none", textureMap);
        IconRegistry.addIcon(BlockCell.TEXTURE_CB + 1, "thermalexpansion:blocks/cell/cell_config_orange_cb", textureMap);
        IconRegistry.addIcon(BlockCell.TEXTURE_CB + 2, "thermalexpansion:blocks/cell/cell_config_blue_cb", textureMap);

        IconRegistry.addIcon("StorageRedstone", "thermalexpansion:blocks/cell/cell_center_solid", textureMap);
    }

    public void renderCenter(CCRenderState ccrs, int metadata) {

        if (metadata == 1 || metadata == 2) {
            modelCenter.render(ccrs, RenderUtils.getIconTransformation(textureCenter[0]));
        } else {
            modelCenter.render(ccrs, RenderUtils.getIconTransformation(textureCenter[1]));
        }
    }

    public void renderFrame(CCRenderState ccrs, int metadata, Map<EnumFacing, TextureAtlasSprite> spriteMap, int facing, TextureAtlasSprite faceTexture) {

        for (int i = 0; i < 6; i++) {
            modelFrame.render(ccrs, i * 4, i * 4 + 4, RenderUtils.getIconTransformation(textureFrame[2 * metadata]));
            modelFrame.render(ccrs, i * 4 + 24, i * 4 + 28, RenderUtils.getIconTransformation(textureFrame[2 * metadata + 1]));
        }
        if (spriteMap != null) {
            for (EnumFacing face : EnumFacing.VALUES) {
                modelFrame.render(ccrs, face.ordinal() * 4, face.ordinal() * 4 + 4, RenderUtils.getIconTransformation(spriteMap.get(face)));
            }
            modelFrame.render(ccrs, facing * 4, facing * 4 + 4, RenderUtils.getIconTransformation(faceTexture));
        }
    }

    private int getScaledEnergyStored(ItemStack container, int scale) {

        IEnergyContainerItem containerItem = (IEnergyContainerItem) container.getItem();

        return (int) (containerItem.getEnergyStored(container) * (long) scale / containerItem.getMaxEnergyStored(container));
    }

}
