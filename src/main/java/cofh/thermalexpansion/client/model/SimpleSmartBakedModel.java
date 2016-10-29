package cofh.thermalexpansion.client.model;

import codechicken.lib.render.CCModelState;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by covers1624 on 28/10/2016.
 */
public class SimpleSmartBakedModel implements IBakedModel {

    private final CCModelState modelState;
    private final ImmutableMap<EnumFacing, List<BakedQuad>> faceQuadMap;
    private final ImmutableList<BakedQuad> generalQuads;

    public SimpleSmartBakedModel(Map<EnumFacing, List<BakedQuad>> faceQuadMap, List<BakedQuad> generalQuads, CCModelState modelState) {
        this.modelState = modelState;
        this.faceQuadMap = faceQuadMap != null ? ImmutableMap.copyOf(faceQuadMap) : null;
        this.generalQuads = generalQuads != null ? ImmutableList.copyOf(generalQuads) : null;
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable IBlockState state, @Nullable EnumFacing side, long rand) {
        if (state != null) {
            if (side == null && generalQuads != null) {
                return generalQuads;
            }
            if (faceQuadMap != null && faceQuadMap.containsKey(side)){
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
        return modelState.toVanillaTransform();
    }

    @Override
    public ItemOverrideList getOverrides() {
        return ItemOverrideList.NONE;
    }
}
