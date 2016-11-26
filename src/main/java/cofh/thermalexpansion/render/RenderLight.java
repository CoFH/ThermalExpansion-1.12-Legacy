package cofh.thermalexpansion.render;

import codechicken.lib.model.bakery.PlanarFaceBakery;
import codechicken.lib.render.CCModel;
import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.buffer.BakingVertexBuffer;
import codechicken.lib.texture.TextureUtils.IIconRegister;
import codechicken.lib.vec.Cuboid6;
import codechicken.lib.vec.Transformation;
import codechicken.lib.vec.uv.IconTransformation;
import cofh.core.render.IconRegistry;
import cofh.lib.render.RenderHelper;
import cofh.thermalexpansion.block.CommonProperties;
import cofh.thermalexpansion.block.light.BlockLight;
import cofh.thermalexpansion.block.light.TileLight;
import cofh.thermalexpansion.client.bakery.ILayeredBlockBakery;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.property.IExtendedBlockState;

import java.util.ArrayList;
import java.util.List;

public class RenderLight implements ILayeredBlockBakery, IIconRegister {

    public static final RenderLight instance = new RenderLight();

    static final int NUM_RENDERS = 2;

    static TextureAtlasSprite[] textureFrame = new TextureAtlasSprite[NUM_RENDERS];
    static TextureAtlasSprite[] textureCenter = new TextureAtlasSprite[2];
    static TextureAtlasSprite textureHalo;

    static final int NUM_STYLES = 16;

    static CCModel[] modelFrame = new CCModel[NUM_STYLES];
    static CCModel[] modelCenter = new CCModel[NUM_STYLES];
    static CCModel[] modelHalo = new CCModel[NUM_STYLES];

    static {

        generateModels();
    }

    public static void initialize() {

        for (int i = 0; i < NUM_RENDERS; i++) {
            textureFrame[i] = IconRegistry.getIcon("Light", i);
        }
        textureCenter[0] = IconRegistry.getIcon("FluidGlowstone");
        textureCenter[1] = IconRegistry.getIcon("LightEffect");
        textureHalo = IconRegistry.getIcon("LightHalo");
    }

    private static void generateModels() {

        final double d1 = RenderHelper.RENDER_OFFSET;
        final double d2 = 2.0D * d1;
        final double d3 = 0.0625D - d1;

        Cuboid6 model = new Cuboid6(Cuboid6.full);
        for (int i = BlockLight.models.length; i-- > 0; ) {
            model.set(BlockLight.models[i]);
            modelFrame[i] = CCModel.quadModel(24).generateBlock(0, model);
            modelFrame[i].computeNormals().shrinkUVs(d1);
            modelCenter[i] = CCModel.quadModel(24).generateBlock(0, model.expand(-d2));
            modelCenter[i].computeNormals();
            modelHalo[i] = CCModel.quadModel(24).generateBlock(0, model.expand(d2 + d3));
            modelHalo[i].computeNormals().shrinkUVs(d3);
        }
    }

    @Override
    public void registerIcons(TextureMap textureMap) {
        IconRegistry.addIcon("Light0", "thermalexpansion:blocks/light/illuminator_frame", textureMap);
        IconRegistry.addIcon("Light1", "thermalexpansion:blocks/light/lamp_effect", textureMap);
        IconRegistry.addIcon("LightEffect", "thermalexpansion:blocks/light/illuminator_effect", textureMap);
        IconRegistry.addIcon("LightHalo", "thermalexpansion:blocks/light/lamp_halo", textureMap);
    }

    @Override
    public IExtendedBlockState handleState(IExtendedBlockState state, TileEntity tileEntity) {
        TileLight light = ((TileLight) tileEntity);
        state = state.withProperty(CommonProperties.TYPE_PROPERTY, state.getBlock().getMetaFromState(state));
        state = state.withProperty(BlockLight.COLOUR_MULTIPLIER_PROPERTY, light.getColorMultiplier());
        state = state.withProperty(BlockLight.STYLE_PROPERTY, (int) light.style);
        state = state.withProperty(BlockLight.ALIGNMENT_PROPERTY, (int) light.alignment);
        state = state.withProperty(BlockLight.MODIFIED_PROPERTY, light.modified);
        state = state.withProperty(CommonProperties.ACTIVE_PROPERTY, light.getInternalLight() > 0);
        return state;
    }

    @Override
    public List<BakedQuad> bakeLayerFace(EnumFacing face, BlockRenderLayer layer, IExtendedBlockState state) {
        if (face == null) {
            int meta = state.getValue(CommonProperties.TYPE_PROPERTY);
            int colour = state.getValue(BlockLight.COLOUR_MULTIPLIER_PROPERTY);
            int style = state.getValue(BlockLight.STYLE_PROPERTY);
            int alignment = state.getValue(BlockLight.ALIGNMENT_PROPERTY);
            boolean modified = state.getValue(BlockLight.MODIFIED_PROPERTY);
            boolean active = false;

            BakingVertexBuffer buffer = BakingVertexBuffer.create();
            buffer.begin(7, DefaultVertexFormats.ITEM);
            CCRenderState ccrs = CCRenderState.instance();
            ccrs.reset();
            ccrs.bind(buffer);

            Transformation transformation = BlockLight.getTransformation(style, alignment);

            switch (BlockLight.Types.getType(meta)) {
                case ILLUMINATOR:
                    renderWorldIlluminator(ccrs, layer, style, colour, modified, transformation);
                    break;
                case LAMP_LUMIUM_RADIANT:
                    active = state.getValue(CommonProperties.ACTIVE_PROPERTY);
                case LAMP_LUMIUM:
                    renderWorldLampLumium(ccrs, layer, style, colour, active, transformation);
                    break;
            }

            buffer.finishDrawing();
            return buffer.bake();
        }
        return new ArrayList<BakedQuad>();
    }

    @Override
    public List<BakedQuad> bakeItemQuads(EnumFacing face, ItemStack stack) {
        if (face == null) {
            int metadata = stack.getItemDamage();
            int color = 0xFFFFFFFF;
            boolean modified = false;

            int style = 0;
            int alignment = 0;

            if (stack.hasTagCompound()) {
                if (stack.getTagCompound().hasKey("Color")) {
                    color = stack.getTagCompound().getInteger("Color");
                    color = (color << 8) + 0xFF;
                    modified = true;
                }
                style = stack.getTagCompound().getByte("Style");
            }

            BakingVertexBuffer buffer = BakingVertexBuffer.create();
            buffer.begin(7, DefaultVertexFormats.ITEM);
            CCRenderState ccrs = CCRenderState.instance();
            ccrs.reset();
            ccrs.bind(buffer);

            Transformation pos = BlockLight.getTransformation(style, alignment);

            switch (BlockLight.Types.getType(metadata)) {
                case ILLUMINATOR:
                    renderItemIlluminator(ccrs, style, color, modified, pos);
                    break;
                case LAMP_LUMIUM_RADIANT:
                case LAMP_LUMIUM:
                    renderWorldLampLumium(ccrs, BlockRenderLayer.SOLID, style, color, false, pos);
                    break;
            }

            buffer.finishDrawing();
            return buffer.bake();
        }
        return new ArrayList<BakedQuad>();
    }

    public void renderCenter(CCRenderState ccrs, int style, int color, boolean modified, Transformation t) {

        modelCenter[style].setColour(color);
        modelCenter[style].render(ccrs, t, new IconTransformation(textureCenter[modified ? 1 : 0]));
        modelCenter[style].setColour(0xFFFFFFFF);
    }

    public void renderFrame(CCRenderState ccrs, int style, int color, int type, Transformation t) {

        modelFrame[style].setColour(color);
        modelFrame[style].render(ccrs, t, new IconTransformation(textureFrame[type]));
        modelFrame[style].setColour(0xFFFFFFFF);
    }

    public void renderHalo(CCRenderState ccrs, int style, int color, Transformation t) {

        modelHalo[style].setColour(color & ~0x80);
        modelHalo[style].render(ccrs, t, new IconTransformation(textureHalo));
        modelHalo[style].setColour(0xFFFFFFFF);
    }

    public boolean renderWorldIlluminator(CCRenderState ccrs, BlockRenderLayer layer, int style, int color, boolean modified, Transformation t) {

        if (layer == BlockRenderLayer.SOLID) {
            renderCenter(ccrs, style, color, modified, t);
            return true;
        } else if (layer == BlockRenderLayer.CUTOUT) {
            renderFrame(ccrs, style, -1, 0, t);
        }

        return true;
    }

    public boolean renderWorldLampLumium(CCRenderState ccrs, BlockRenderLayer layer, int style, int color, boolean active, Transformation t) {

        if (layer == BlockRenderLayer.SOLID) {
            renderFrame(ccrs, style, color, 1, t);
            return true;
        } else if (layer == BlockRenderLayer.TRANSLUCENT && active) {
            renderHalo(ccrs, style, color, t);
        }
        return active;
    }

    public void renderItemIlluminator(CCRenderState ccrs, int style, int color, boolean modified, Transformation t) {

        renderCenter(ccrs, style, color, modified, t);
        renderFrame(ccrs, style, -1, 0, t);
    }
}
