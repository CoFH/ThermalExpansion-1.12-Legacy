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
import cofh.lib.render.RenderHelper;
import cofh.thermalexpansion.block.tank.BlockTank;
import cofh.thermalexpansion.block.tank.TileTank;
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
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nullable;
import java.util.List;

public class RenderTank implements ICCBlockRenderer, IItemRenderer {

    public static final RenderTank instance = new RenderTank();

    static TextureAtlasSprite[] textureTop = new TextureAtlasSprite[BlockTank.Types.values().length * 2];
    static TextureAtlasSprite[] textureBottom = new TextureAtlasSprite[BlockTank.Types.values().length * 2];
    static TextureAtlasSprite[] textureSides = new TextureAtlasSprite[BlockTank.Types.values().length * 2];

    static CCModel[] modelFluid = new CCModel[TileTank.RENDER_LEVELS];
    static CCModel modelFrame = CCModel.quadModel(48);

    static {
        //TEProps.renderIdTank = RenderingRegistry.getNextAvailableRenderId();
        //RenderingRegistry.registerBlockHandler(instance);

        //MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(TEBlocks.blockTank), instance);

        generateFluidModels();

        Cuboid6 box = new Cuboid6(0.125, 0, 0.125, 0.875, 1, 0.875);
        double inset = 0.0625;
        modelFrame = CCModel.quadModel(48).generateBlock(0, box);
        CCModel.generateBackface(modelFrame, 0, modelFrame, 24, 24);
        modelFrame.computeNormals();
        for (int i = 24; i < 48; i++) {
            modelFrame.verts[i].vec.add(modelFrame.normals()[i].copy().multiply(inset));
        }
        modelFrame.computeLighting(LightModel.standardLightModel).shrinkUVs(RenderHelper.RENDER_OFFSET);
    }

    public static void initialize() {

        for (int i = 0; i < textureSides.length; i++) {
            textureTop[i] = IconRegistry.getIcon("TankTop", i);
            textureBottom[i] = IconRegistry.getIcon("TankBottom", i);
            textureSides[i] = IconRegistry.getIcon("TankSide", i);
        }
    }

    private static void generateFluidModels() {

        double minXZ = 0.1875 - RenderHelper.RENDER_OFFSET;
        double maxXZ = 0.8125 + RenderHelper.RENDER_OFFSET;
        double minY = 0.0625 - RenderHelper.RENDER_OFFSET;
        double maxY = 1 - minY;
        double increment = (maxY - minY) / TileTank.RENDER_LEVELS;

        for (int i = 1; i < TileTank.RENDER_LEVELS + 1; i++) {
            double yLevel = minY + increment * i;
            modelFluid[i - 1] = CCModel.quadModel(24).generateBlock(0, minXZ, minY, minXZ, maxXZ, yLevel, maxXZ).computeNormals();
        }
    }

    public void renderFrame(CCRenderState ccrs, int metadata, int mode, double x, double y, double z) {

        Translation trans = RenderUtils.getRenderVector(x, y, z).translation();

        modelFrame.render(ccrs, 0, 4, trans, RenderUtils.getIconTransformation(textureBottom[2 * metadata + mode]));
        modelFrame.render(ccrs, 24, 28, trans, RenderUtils.getIconTransformation(textureTop[2 * metadata + mode]));
        modelFrame.render(ccrs, 4, 8, trans, RenderUtils.getIconTransformation(textureTop[2 * metadata]));
        modelFrame.render(ccrs, 28, 32, trans, RenderUtils.getIconTransformation(textureBottom[2 * metadata]));

        for (int i = 8; i < 24; i += 4) {
            modelFrame.render(ccrs, i, i + 4, trans, RenderUtils.getIconTransformation(textureSides[2 * metadata + mode]));
        }
        for (int i = 32; i < 48; i += 4) {
            modelFrame.render(ccrs, i, i + 4, trans, RenderUtils.getIconTransformation(textureSides[2 * metadata + mode]));
        }
    }

    public void renderFluid(CCRenderState ccrs, int metadata, FluidStack stack, double x, double y, double z) {

        if (stack == null || stack.amount <= 0) {
            return;
        }
        Fluid fluid = stack.getFluid();

        RenderUtils.setFluidRenderColor(stack);
        TextureAtlasSprite fluidTex = RenderHelper.getFluidTexture(stack);
        int level = TileTank.RENDER_LEVELS - 1;

        if (fluid.isGaseous(stack)) {
            ccrs.alphaOverride = 32 + 192 * stack.amount / TileTank.CAPACITY[metadata];
        } else {
            level = (int) Math.min(TileTank.RENDER_LEVELS - 1, (long) stack.amount * TileTank.RENDER_LEVELS / TileTank.CAPACITY[metadata]);
        }
        modelFluid[level].render(ccrs, x, y, z, RenderUtils.getIconTransformation(fluidTex));
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
        if (!(tile instanceof TileTank)) {
            return false;
        }
        TileTank theTile = (TileTank) tile;

        RenderUtils.preWorldRender(world, pos);
        CCRenderState ccrs = CCRenderState.instance();

        if (BlockCoFHBase.renderPass == 0) {
            renderFrame(ccrs, theTile.type, theTile.mode, pos.getX(), pos.getY(), pos.getZ());
        } else {
            if (theTile.getTankFluid() == null) {
                return false;
            }
            renderFluid(ccrs, theTile.getBlockMetadata(), theTile.getTankFluid(), pos.getX(), pos.getY(), pos.getZ());
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
    //	return TEProps.renderIdTank;
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
        FluidStack fluid = null;
        if (item.getTagCompound() != null) {
            fluid = FluidStack.loadFluidStackFromNBT(item.getTagCompound().getCompoundTag("Fluid"));
        }
        RenderUtils.preItemRender();
        CCRenderState ccrs = CCRenderState.instance();

        ccrs.startDrawing(7, DefaultVertexFormats.POSITION_TEX);
        renderFluid(ccrs, item.getItemDamage(), fluid, offset, offset, offset);
        ccrs.draw();

        ccrs.alphaOverride = -1;
        ccrs.startDrawing(7, DefaultVertexFormats.ITEM);
        renderFrame(ccrs, item.getItemDamage(), 0, offset, offset, offset);
        ccrs.draw();

        RenderUtils.postItemRender();
        GlStateManager.popMatrix();
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
