package cofh.thermalexpansion.render;

import codechicken.lib.render.item.IItemRenderer;
import codechicken.lib.util.TransformUtils;
import cofh.core.render.RenderUtils;
import cofh.lib.render.RenderHelper;
import cofh.thermalexpansion.block.EnumType;
import cofh.thermalexpansion.block.strongbox.TileStrongbox;
import cofh.thermalexpansion.block.strongbox.TileStrongboxCreative;
import cofh.thermalexpansion.core.TEProps;
import cofh.thermalexpansion.render.model.ModelStrongbox;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.*;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IPerspectiveAwareModel;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;
import javax.vecmath.Matrix4f;
import java.util.List;

public class RenderStrongbox extends TileEntitySpecialRenderer<TileStrongbox> implements IItemRenderer, IPerspectiveAwareModel {

    public static final RenderStrongbox instance = new RenderStrongbox();
    static ResourceLocation[] texture = new ResourceLocation[EnumType.values().length];

    static ModelStrongbox model = new ModelStrongbox();

    public static void registerRenderers() {
        ClientRegistry.bindTileEntitySpecialRenderer(TileStrongbox.class, instance);
        ClientRegistry.bindTileEntitySpecialRenderer(TileStrongboxCreative.class, instance);
    }

    public static void initialize() {

        texture[EnumType.BASIC.ordinal()] = new ResourceLocation(TEProps.PATH_RENDER + "strongbox/strongbox_basic.png");
        texture[EnumType.HARDENED.ordinal()] = new ResourceLocation(TEProps.PATH_RENDER + "strongbox/strongbox_hardened.png");
        texture[EnumType.REINFORCED.ordinal()] = new ResourceLocation(TEProps.PATH_RENDER + "strongbox/strongbox_reinforced.png");
        texture[EnumType.RESONANT.ordinal()] = new ResourceLocation(TEProps.PATH_RENDER + "strongbox/strongbox_resonant.png");
        texture[EnumType.CREATIVE.ordinal()] = new ResourceLocation(TEProps.PATH_RENDER + "strongbox/strongbox_creative.png");
    }

    public void render(int metadata, int access, int facing, double x, double y, double z) {

        RenderHelper.bindTexture(texture[metadata]);

        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y + 1.0, z + 1.0);
        GlStateManager.scale(1.0F, -1F, -1F);
        GlStateManager.translate(0.5F, 0.5F, 0.5F);
        GlStateManager.rotate(RenderUtils.facingAngle[facing], 0.0F, 1.0F, 0.0F);
        GlStateManager.translate(-0.5F, -0.5F, -0.5F);
        GlStateManager.enableRescaleNormal();
        model.render(access);
        GlStateManager.disableRescaleNormal();
        GlStateManager.popMatrix();
    }

    @Override
    public void renderTileEntityAt(TileStrongbox strongbox, double x, double y, double z, float f, int destroyStage) {

        model.boxLid.rotateAngleX = (float) strongbox.getRadianLidAngle(f);
        render(strongbox.type, strongbox.getAccess().ordinal(), strongbox.getFacing(), x, y, z);
    }

    @Override
    public void renderItem(ItemStack item) {
        double offset = 0;
        int access = 0;

        if (item.getTagCompound() != null) {
            access = item.getTagCompound().getByte("Access");
        }
        model.boxLid.rotateAngleX = 0;
        render(item.getItemDamage(), access, 2, offset, offset, offset);
        GlStateManager.enableRescaleNormal();
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
    public Pair<? extends IBakedModel, Matrix4f> handlePerspective(TransformType cameraTransformType) {
        return MapWrapper.handlePerspective(this, TransformUtils.DEFAULT_BLOCK.getTransforms(), cameraTransformType);
    }

    @Override
    public ItemOverrideList getOverrides() {
        return ItemOverrideList.NONE;
    }
}
