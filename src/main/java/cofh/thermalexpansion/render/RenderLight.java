package cofh.thermalexpansion.render;

import codechicken.lib.render.CCModel;
import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.block.ICCBlockRenderer;
import codechicken.lib.render.item.IItemRenderer;
import codechicken.lib.vec.Cuboid6;
import codechicken.lib.vec.Transformation;
import codechicken.lib.vec.Vector3;
import cofh.core.block.BlockCoFHBase;
import cofh.core.render.IconRegistry;
import cofh.core.render.RenderUtils;
import cofh.lib.render.RenderHelper;
import cofh.thermalexpansion.block.light.BlockLight;
import cofh.thermalexpansion.block.light.TileLight;
import cofh.thermalexpansion.core.TEProps;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nullable;
import java.util.List;

public class RenderLight implements ICCBlockRenderer, IItemRenderer {

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
        //TEProps.renderIdLight = RenderingRegistry.getNextAvailableRenderId();
        //RenderingRegistry.registerBlockHandler(instance);

        //MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(TEBlocks.blockLight), instance);

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

    public void renderCenter(CCRenderState ccrs, int style, int color, boolean modified, Transformation t) {

        modelCenter[style].setColour(color);
        modelCenter[style].render(ccrs, t, RenderUtils.getIconTransformation(textureCenter[modified ? 1 : 0]));
        modelCenter[style].setColour(0xFFFFFFFF);
    }

    public void renderFrame(CCRenderState ccrs, int style, int color, int type, Transformation t) {

        modelFrame[style].setColour(color);
        modelFrame[style].render(ccrs, t, RenderUtils.getIconTransformation(textureFrame[type]));
        modelFrame[style].setColour(0xFFFFFFFF);
    }

    public void renderHalo(CCRenderState ccrs, int style, int color, Transformation t) {

        modelHalo[style].setColour(color & ~0x80);
        modelHalo[style].render(ccrs, t, RenderUtils.getIconTransformation(textureHalo));
        modelHalo[style].setColour(0xFFFFFFFF);
    }

    public boolean renderWorldIlluminator(CCRenderState ccrs, int pass, int style, int color, boolean modified, Transformation t) {

        if (pass == 0) {
            renderFrame(ccrs, style, -1, 0, t);
            return true;
        }
        renderCenter(ccrs, style, color, modified, t);

        return true;
    }

    public boolean renderWorldLampLumium(CCRenderState ccrs, int pass, int style, int color, boolean active, Transformation t) {

        if (pass == 0) {
            renderFrame(ccrs, style, color, 1, t);
            return true;
        } else if (active) {
            renderHalo(ccrs, style, color, t);
        }
        return active;
    }

    /* ISimpleBlockRenderingHandler */
    //@Override
    //public void renderInventoryBlock(Block block, int metadata, int modelID, RenderBlocks renderer) {
    //}

    @Override
    public void handleRenderBlockDamage(IBlockAccess world, BlockPos pos, IBlockState state, TextureAtlasSprite sprite, VertexBuffer buffer) {

    }

    @Override
    public boolean renderBlock(IBlockAccess world, BlockPos pos, IBlockState state, VertexBuffer buffer) {
        TileEntity tile = world.getTileEntity(pos);
        if (!(tile instanceof TileLight)) {
            return false;
        }
        TileLight theTile = (TileLight) tile;
        int bMeta = state.getBlock().getMetaFromState(state);

        RenderUtils.preWorldRender(world, pos);
        int renderPass = BlockCoFHBase.renderPass;

        int color = theTile.getColorMultiplier();
        boolean active = false;

        int style = theTile.style;
        int alignment = theTile.alignment;

        Transformation transformation = BlockLight.getTransformation(style, alignment).with(new Vector3(pos).translation());

        CCRenderState ccrs = CCRenderState.instance();
        ccrs.bind(buffer);

        switch (BlockLight.Types.getType(bMeta)) {
            case ILLUMINATOR:
                return renderWorldIlluminator(ccrs, renderPass, style, color, theTile.modified, transformation);
            case LAMP_LUMIUM_RADIANT:
                active = theTile.getInternalLight() > 0;
            case LAMP_LUMIUM:
                return renderWorldLampLumium(ccrs, renderPass, style, color, active, transformation);
        }
        return false;
    }

    @Override
    public void renderBrightness(IBlockState state, float brightness) {

    }

    @Override
    public void registerTextures(TextureMap map) {

    }

    /*@Override
    public boolean shouldRender3DInInventory(int modelId) {
        return false;
    }

    @Override
    public int getRenderId() {
        return TEProps.renderIdLight;
    }*/

    /* IItemRenderer */
    /*@Override
    public boolean handleRenderType(ItemStack item, ItemRenderType type) {
        return true;
    }

    @Override
    public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
        return true;
    }*/

    public void renderItemIlluminator(CCRenderState ccrs, int style, int color, boolean modified, Transformation t) {

        renderCenter(ccrs, style, color, modified, t);
        ccrs.draw();

        ccrs.startDrawing(7, DefaultVertexFormats.ITEM);
        renderFrame(ccrs, style, -1, 0, t);
    }

    @Override
    public void renderItem(ItemStack item) {

        GL11.glPushMatrix();
        double offset = -0.5;
        //if (type == ItemRenderType.EQUIPPED || type == ItemRenderType.EQUIPPED_FIRST_PERSON) {
        //    offset = 0;
        //}
        int metadata = item.getItemDamage();
        int color = 0xFFFFFFFF;
        boolean modified = false;

        int style = 0;
        int alignment = 0;

        if (item.hasTagCompound()) {
            if (item.getTagCompound().hasKey("Color")) {
                color = item.getTagCompound().getInteger("Color");
                color = (color << 8) + 0xFF;
                modified = true;
            }
            style = item.getTagCompound().getByte("Style");
        }

        RenderUtils.preItemRender();
        RenderHelper.setBlockTextureSheet();

        Transformation pos = BlockLight.getTransformation(style, alignment).with(new Vector3(offset, offset, offset).translation());

        CCRenderState ccrs = CCRenderState.instance();

        ccrs.startDrawing(7, DefaultVertexFormats.ITEM);
        switch (BlockLight.Types.getType(metadata)) {
            case ILLUMINATOR:
                renderItemIlluminator(ccrs, style, color, modified, pos);
                break;
            case LAMP_LUMIUM_RADIANT:
            case LAMP_LUMIUM:
                renderWorldLampLumium(ccrs, 0, style, color, false, pos);
                break;
        }
        ccrs.draw();
        RenderUtils.postItemRender();
        GL11.glPopMatrix();
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable IBlockState state, @Nullable EnumFacing side, long rand) {
        return null;
    }

    @Override
    public boolean isAmbientOcclusion() {
        return false;
    }

    @Override
    public boolean isGui3d() {
        return false;
    }

    @Override
    public boolean isBuiltInRenderer() {
        return false;
    }

    @Override
    public TextureAtlasSprite getParticleTexture() {
        return null;
    }

    @Override
    public ItemCameraTransforms getItemCameraTransforms() {
        return ItemCameraTransforms.DEFAULT;
    }

    @Override
    public ItemOverrideList getOverrides() {
        return ItemOverrideList.NONE;
    }
}
