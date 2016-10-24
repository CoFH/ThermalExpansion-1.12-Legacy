package cofh.thermalexpansion.render;

import codechicken.lib.lighting.LightModel;
import codechicken.lib.render.CCModel;
import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.block.ICCBlockRenderer;
import codechicken.lib.render.item.IItemRenderer;
import codechicken.lib.vec.Cuboid6;
import codechicken.lib.vec.Translation;
import cofh.core.block.BlockCoFHBase;
import cofh.core.render.IconRegistry;
import cofh.core.render.RenderUtils;
import cofh.core.render.ShaderHelper;
import cofh.lib.render.RenderHelper;
import cofh.thermalexpansion.block.ender.TileTesseract;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.GlStateManager;
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
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

@SideOnly(Side.CLIENT)
public class RenderTesseract implements ICCBlockRenderer, IItemRenderer {

    public static final RenderTesseract instance = new RenderTesseract();

    static TextureAtlasSprite[] textureCenter = new TextureAtlasSprite[2];
    static TextureAtlasSprite[] textureFrame = new TextureAtlasSprite[4];
    static CCModel modelCenter = CCModel.quadModel(24);
    static CCModel modelFrame = CCModel.quadModel(48);

    static {
        //TEProps.renderIdEnder = RenderingRegistry.getNextAvailableRenderId();
        //RenderingRegistry.registerBlockHandler(instance);

        if (ShaderHelper.useShaders()) {
            RenderTesseractStarfield.register();
        }

        //MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(TEBlocks.blockTesseract), instance);

        modelCenter.generateBlock(0, 0.15, 0.15, 0.15, 0.85, 0.85, 0.85).computeNormals();

        Cuboid6 box = new Cuboid6(0, 0, 0, 1, 1, 1);
        double inset = 0.1875;
        modelFrame = CCModel.quadModel(48).generateBlock(0, box);
        CCModel.generateBackface(modelFrame, 0, modelFrame, 24, 24);
        modelFrame.computeNormals();
        for (int i = 24; i < 48; i++) {
            modelFrame.verts[i].vec.add(modelFrame.normals()[i].copy().multiply(inset));
        }
        modelFrame.computeLighting(LightModel.standardLightModel).shrinkUVs(RenderHelper.RENDER_OFFSET);
    }

    public static void initialize() {

        textureCenter[0] = IconRegistry.getIcon("FluidEnder");
        textureCenter[1] = IconRegistry.getIcon("SkyEnder");
        textureFrame[0] = IconRegistry.getIcon("Tesseract");
        textureFrame[1] = IconRegistry.getIcon("TesseractInner");
        textureFrame[2] = IconRegistry.getIcon("TesseractActive");
        textureFrame[3] = IconRegistry.getIcon("TesseractInnerActive");
    }

    public void renderCenter(CCRenderState ccrs, int metadata, TileTesseract tile, double x, double y, double z) {

        if (tile != null && tile.isActive) {
            modelCenter.render(ccrs, x, y, z, RenderUtils.getIconTransformation(textureCenter[1]));
        } else {
            modelCenter.render(ccrs, x, y, z, RenderUtils.getIconTransformation(textureCenter[0]));
        }
    }

    public void renderFrame(CCRenderState ccrs, int metadata, TileTesseract tile, double x, double y, double z) {

        Translation trans = new Translation(x, y, z);
        for (int i = 0; i < 6; i++) {
            if (tile != null && tile.isActive && tile.redstoneControlOrDisable()) {
                modelFrame.render(ccrs, i * 4, i * 4 + 4, trans, RenderUtils.getIconTransformation(textureFrame[2]));
                modelFrame.render(ccrs, i * 4 + 24, i * 4 + 28, trans, RenderUtils.getIconTransformation(textureFrame[3]));
            } else {
                modelFrame.render(ccrs, i * 4, i * 4 + 4, trans, RenderUtils.getIconTransformation(textureFrame[0]));
                modelFrame.render(ccrs, i * 4 + 24, i * 4 + 28, trans, RenderUtils.getIconTransformation(textureFrame[1]));
            }
        }
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
        if (!(tile instanceof TileTesseract)) {
            return false;
        }
        TileTesseract theTile = (TileTesseract) tile;

        RenderUtils.preWorldRender(world, pos);
        CCRenderState ccrs = CCRenderState.instance();
        ccrs.bind(buffer);

        if (BlockCoFHBase.renderPass == 0) {
            renderFrame(ccrs, 0, theTile, pos.getX(), pos.getY(), pos.getZ());
        } else {
            renderCenter(ccrs, 0, theTile, pos.getX(), pos.getY(), pos.getZ());
        }
        return true;
    }

    @Override
    public void renderBrightness(IBlockState state, float brightness) {
    }

    @Override
    public void registerTextures(TextureMap map) {
    }

    //@Override
    //public boolean shouldRender3DInInventory(int modelId) {
    //	return true;
    //}

    //@Override
    //public int getRenderId() {
    //	return TEProps.renderIdEnder;
    //}

	/* IItemRenderer */
    //@Override
    //public boolean handleRenderType(ItemStack item, ItemRenderType type) {
    //	return true;
    //}

    //@Override
    //public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
    //	return true;
    //}

    @Override
    public void renderItem(ItemStack item) {

        GlStateManager.pushMatrix();
        double offset = -0.5;
        //if (type == ItemRenderType.EQUIPPED || type == ItemRenderType.EQUIPPED_FIRST_PERSON) {
        //	offset = 0;
        //}
        int metadata = item.getItemDamage();
        RenderUtils.preItemRender();

        CCRenderState ccrs = CCRenderState.instance();

        ccrs.startDrawing(GL11.GL_QUADS, DefaultVertexFormats.ITEM);
        renderFrame(ccrs, metadata, null, offset, offset, offset);
        renderCenter(ccrs, metadata, null, offset, offset, offset);
        ccrs.draw();

        RenderUtils.postItemRender();
        GlStateManager.popMatrix();
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable IBlockState state, @Nullable EnumFacing side, long rand) {
        return new ArrayList<BakedQuad>();
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
        return true;
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
