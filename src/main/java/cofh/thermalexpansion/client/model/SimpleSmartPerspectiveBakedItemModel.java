package cofh.thermalexpansion.client.model;

import codechicken.lib.render.CCModelState;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.client.model.IPerspectiveAwareModel;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;
import javax.vecmath.Matrix4f;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by covers1624 on 28/10/2016.
 */
public class SimpleSmartPerspectiveBakedItemModel implements IPerspectiveAwareModel {

    private final CCModelState modelState;
    private final ImmutableMap<EnumFacing, List<BakedQuad>> faceQuadMap;
    private final ImmutableList<BakedQuad> generalQuads;

    public SimpleSmartPerspectiveBakedItemModel(Map<EnumFacing, List<BakedQuad>> faceQuadMap, List<BakedQuad> generalQuads, CCModelState modelState) {
        this.modelState = modelState;
        this.faceQuadMap = ImmutableMap.copyOf(faceQuadMap);
        this.generalQuads = ImmutableList.copyOf(generalQuads);
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable IBlockState state, @Nullable EnumFacing side, long rand) {
        if (state == null) {
            if (side == null) {
                return generalQuads;
            }
            if (faceQuadMap.containsKey(side)){
                faceQuadMap.get(side);
            }
        }
        return new ArrayList<BakedQuad>();
    }

    @Override
    public boolean isAmbientOcclusion() {
        return true;
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

    @Override
    public Pair<? extends IBakedModel, Matrix4f> handlePerspective(TransformType cameraTransformType) {
        return MapWrapper.handlePerspective(this, modelState.getTransforms(), cameraTransformType);
    }
}
