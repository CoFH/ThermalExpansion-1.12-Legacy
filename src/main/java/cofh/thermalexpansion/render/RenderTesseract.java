package cofh.thermalexpansion.render;

import codechicken.lib.render.CCModel;
import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.buffer.BakingVertexBuffer;
import codechicken.lib.texture.TextureUtils.IIconRegister;
import codechicken.lib.vec.Cuboid6;
import codechicken.lib.vec.Translation;
import codechicken.lib.vec.uv.IconTransformation;
import cofh.core.render.IconRegistry;
import cofh.core.render.ShaderHelper;
import cofh.lib.render.RenderHelper;
import cofh.thermalexpansion.block.CommonProperties;
import cofh.thermalexpansion.block.ender.BlockEnder;
import cofh.thermalexpansion.block.ender.TileTesseract;
import codechicken.lib.model.blockbakery.ILayeredBlockBakery;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.List;

@SideOnly(Side.CLIENT)
public class RenderTesseract implements IIconRegister, ILayeredBlockBakery {

    public static final RenderTesseract instance = new RenderTesseract();

    static TextureAtlasSprite[] textureCenter = new TextureAtlasSprite[2];
    static TextureAtlasSprite[] textureFrame = new TextureAtlasSprite[4];
    static CCModel modelCenter = CCModel.quadModel(24);
    static CCModel modelFrame = CCModel.quadModel(48);

    static {
        if (ShaderHelper.useShaders()) {
            RenderTesseractStarfield.register();
        }

        modelCenter.generateBlock(0, 0.15, 0.15, 0.15, 0.85, 0.85, 0.85).computeNormals();

        Cuboid6 box = new Cuboid6(0, 0, 0, 1, 1, 1);
        double inset = 0.1875;
        modelFrame = CCModel.quadModel(48).generateBlock(0, box);
        CCModel.generateBackface(modelFrame, 0, modelFrame, 24, 24);
        modelFrame.computeNormals();
        for (int i = 24; i < 48; i++) {
            modelFrame.verts[i].vec.add(modelFrame.normals()[i].copy().multiply(inset));
        }
        modelFrame.shrinkUVs(RenderHelper.RENDER_OFFSET);
    }

    public static void initialize() {

        textureCenter[0] = IconRegistry.getIcon("FluidEnder");
        textureCenter[1] = IconRegistry.getIcon("SkyEnder");
        textureFrame[0] = IconRegistry.getIcon("Tesseract");
        textureFrame[1] = IconRegistry.getIcon("TesseractInner");
        textureFrame[2] = IconRegistry.getIcon("TesseractActive");
        textureFrame[3] = IconRegistry.getIcon("TesseractInnerActive");
    }

    @Override
    public void registerIcons(TextureMap textureMap) {
        IconRegistry.addIcon("Tesseract", "thermalexpansion:blocks/tesseract/tesseract", textureMap);
        IconRegistry.addIcon("TesseractInner", "thermalexpansion:blocks/tesseract/tesseract_inner", textureMap);
        IconRegistry.addIcon("TesseractActive", "thermalexpansion:blocks/tesseract/tesseract_active", textureMap);
        IconRegistry.addIcon("TesseractInnerActive", "thermalexpansion:blocks/tesseract/tesseract_inner_active", textureMap);
        IconRegistry.addIcon("SkyEnder", "thermalexpansion:blocks/tesseract/sky_ender", textureMap);
    }

    @Override
    public IExtendedBlockState handleState(IExtendedBlockState state, TileEntity tileEntity) {
        TileTesseract tesseract = (TileTesseract) tileEntity;
        state = state.withProperty(CommonProperties.ACTIVE_PROPERTY, tesseract.isActive);
        state = state.withProperty(BlockEnder.DISABLED_PROPERTY, tesseract.redstoneControlOrDisable());
        return state;
    }

    @Override
    public List<BakedQuad> bakeLayerFace(EnumFacing face, BlockRenderLayer layer, IExtendedBlockState state) {
        if (face == null) {
            boolean isActive = state.getValue(CommonProperties.ACTIVE_PROPERTY);
            boolean rsContOrDisable = state.getValue(BlockEnder.DISABLED_PROPERTY);
            BakingVertexBuffer buffer = BakingVertexBuffer.create();
            buffer.begin(7, DefaultVertexFormats.ITEM);
            CCRenderState ccrs = CCRenderState.instance();
            ccrs.reset();
            ccrs.bind(buffer);

            if (layer == BlockRenderLayer.CUTOUT) {
                renderFrame(ccrs, false, isActive, rsContOrDisable, 0, 0, 0);
            } else {
                renderCenter(ccrs, false, isActive, 0, 0, 0);
            }

            buffer.finishDrawing();
            return buffer.bake();
        }
        return new ArrayList<BakedQuad>();
    }

    @Override
    public List<BakedQuad> bakeItemQuads(EnumFacing face, ItemStack stack) {
        if (face == null) {
            BakingVertexBuffer buffer = BakingVertexBuffer.create();
            buffer.begin(7, DefaultVertexFormats.ITEM);
            CCRenderState ccrs = CCRenderState.instance();
            ccrs.reset();
            ccrs.bind(buffer);

            renderFrame(ccrs, true, false, false, 0, 0, 0);
            renderCenter(ccrs, false, false, 0, 0, 0);

            buffer.finishDrawing();
            return buffer.bake();
        }
        return new ArrayList<BakedQuad>();
    }

    public void renderCenter(CCRenderState ccrs, boolean isItem, boolean isActive, double x, double y, double z) {

        if (!isItem && isActive) {
            modelCenter.render(ccrs, x, y, z, new IconTransformation(textureCenter[1]));
        } else {
            modelCenter.render(ccrs, x, y, z, new IconTransformation(textureCenter[0]));
        }
    }

    public void renderFrame(CCRenderState ccrs, boolean isItem, boolean isActive, boolean rsContOrDisable, double x, double y, double z) {

        Translation trans = new Translation(x, y, z);
        for (int i = 0; i < 6; i++) {
            if (!isItem && isActive && rsContOrDisable) {
                modelFrame.render(ccrs, i * 4, i * 4 + 4, trans, new IconTransformation(textureFrame[2]));
                modelFrame.render(ccrs, i * 4 + 24, i * 4 + 28, trans, new IconTransformation(textureFrame[3]));
            } else {
                modelFrame.render(ccrs, i * 4, i * 4 + 4, trans, new IconTransformation(textureFrame[0]));
                modelFrame.render(ccrs, i * 4 + 24, i * 4 + 28, trans, new IconTransformation(textureFrame[1]));
            }
        }
    }
}
