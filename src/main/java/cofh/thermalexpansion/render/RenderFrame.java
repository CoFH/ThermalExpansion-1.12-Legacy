package cofh.thermalexpansion.render;

import codechicken.lib.lighting.LightModel;
import codechicken.lib.render.CCModel;
import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.block.ICCBlockRenderer;
import codechicken.lib.render.item.IItemRenderer;
import codechicken.lib.vec.Cuboid6;
import codechicken.lib.vec.Translation;
import cofh.core.render.RenderUtils;
import cofh.lib.render.RenderHelper;
import cofh.thermalexpansion.block.TEBlocks;
import cofh.thermalexpansion.block.simple.BlockFrame;
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
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nullable;
import java.util.List;

public class RenderFrame implements ICCBlockRenderer, IItemRenderer {

    public static final RenderFrame instance = new RenderFrame();

    static CCModel modelCenter = CCModel.quadModel(24);
    static CCModel modelFrame = CCModel.quadModel(48);

    static {
        //TEProps.renderIdFrame = RenderingRegistry.getNextAvailableRenderId();
        //RenderingRegistry.registerBlockHandler(instance);

        //MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(TEBlocks.blockFrame), instance);

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

    }

    public void renderCenter(CCRenderState ccrs, Block block, int metadata, double x, double y, double z) {

        //modelCenter.render(x, y, z, RenderUtils.getIconTransformation(block.getIcon(7, metadata)));
    }

    public void renderFrame(CCRenderState ccrs, Block block, int metadata, double x, double y, double z) {

        Translation trans = RenderUtils.getRenderVector(x, y, z).translation();
        for (int i = 0; i < 6; i++) {
            //modelFrame.render(i * 4, i * 4 + 4, trans, RenderUtils.getIconTransformation(block.getIcon(i, metadata)));
            //modelFrame.render(i * 4 + 24, i * 4 + 28, trans, RenderUtils.getIconTransformation(block.getIcon(6, metadata)));
        }
    }

    /* ISimpleBlockRenderingHandler */
    //@Override
    //public void renderInventoryBlock(Block block, int metadata, int modelId, RenderBlocks renderer) {
    //}

    @Override
    public void handleRenderBlockDamage(IBlockAccess world, BlockPos pos, IBlockState state, TextureAtlasSprite sprite, VertexBuffer buffer) {

    }

    @Override
    public boolean renderBlock(IBlockAccess world, BlockPos pos, IBlockState state, VertexBuffer buffer) {
        int metadata = state.getBlock().getMetaFromState(state);

        CCRenderState ccrs = CCRenderState.instance();
        ccrs.bind(buffer);

        RenderUtils.preWorldRender(world, pos);
        if (BlockFrame.renderPass == 0) {
            renderFrame(ccrs, state.getBlock(), metadata, pos.getX(), pos.getY(), pos.getZ());
        } else {
            renderCenter(ccrs, state.getBlock(), metadata, pos.getX(), pos.getY(), pos.getZ());
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
    public boolean shouldRender3DInInventory(int modelId) {
        return true;
    }

    //@Override
    public int getRenderId() {
        return TEProps.renderIdFrame;
    }

    /* IItemRenderer */
    //@Override
    //public boolean handleRenderType(ItemStack item, ItemRenderType type) {
    //    return true;
    //}

    //@Override
    //public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
    //    return true;
    //}

    @Override
    public void renderItem(ItemStack item) {
        GL11.glPushMatrix();
        double offset = -0.5;
        //if (type == ItemRenderType.EQUIPPED || type == ItemRenderType.EQUIPPED_FIRST_PERSON) {
        //    offset = 0;
        //}
        Block block = Block.getBlockFromItem(item.getItem());
        int metadata = item.getItemDamage();
        RenderUtils.preItemRender();


        CCRenderState ccrs = CCRenderState.instance();

        ccrs.startDrawing(7, DefaultVertexFormats.ITEM);
        renderFrame(ccrs, block, metadata, offset, offset, offset);
        renderCenter(ccrs, block, metadata, offset, offset, offset);
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
