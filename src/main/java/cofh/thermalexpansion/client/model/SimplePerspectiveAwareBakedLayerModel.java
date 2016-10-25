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
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.client.model.IPerspectiveAwareModel;
import net.minecraftforge.common.model.TRSRTransformation;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;
import javax.vecmath.Matrix4f;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by covers1624 on 26/10/2016.
 */
public class SimplePerspectiveAwareBakedLayerModel implements IPerspectiveAwareModel {

    private final ImmutableMap<TransformType, TRSRTransformation> transforms;
    private final ImmutableMap<EnumFacing, Map<BlockRenderLayer, List<BakedQuad>>> quadLayerMap;
    private final ImmutableMap<EnumFacing, List<BakedQuad>> itemQuadMap;
    private final TextureAtlasSprite particle;

    public SimplePerspectiveAwareBakedLayerModel(Map<EnumFacing, Map<BlockRenderLayer, List<BakedQuad>>> quadLayerMap,Map<EnumFacing, List<BakedQuad>> itemQuadMap, TextureAtlasSprite particle, CCModelState modelState) {
        this(quadLayerMap, itemQuadMap, particle, modelState.getTransforms());
    }

    public SimplePerspectiveAwareBakedLayerModel(Map<EnumFacing, Map<BlockRenderLayer, List<BakedQuad>>> quadLayerMap, Map<EnumFacing, List<BakedQuad>> itemQuadMap, TextureAtlasSprite particle, ImmutableMap<TransformType, TRSRTransformation> transforms) {
        this.transforms = ImmutableMap.copyOf(transforms);
        this.quadLayerMap = ImmutableMap.copyOf(quadLayerMap);
        this.itemQuadMap = ImmutableMap.copyOf(itemQuadMap);
        this.particle = particle;
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable IBlockState state, @Nullable EnumFacing side, long rand) {
        if (state == null && side != null) {
            if (itemQuadMap.containsKey(side)){
                return itemQuadMap.get(side);
            }
        }

        if (state != null && side != null && quadLayerMap.containsKey(side)) {
            Map<BlockRenderLayer, List<BakedQuad>> sideLayerQuadMap = quadLayerMap.get(side);
            BlockRenderLayer layer = MinecraftForgeClient.getRenderLayer();
            if (sideLayerQuadMap.containsKey(layer)) {
                return sideLayerQuadMap.get(layer);
            }
        }

        return new ArrayList<BakedQuad>();
    }

    @Override
    public Pair<? extends IBakedModel, Matrix4f> handlePerspective(TransformType cameraTransformType) {
        return MapWrapper.handlePerspective(this, transforms, cameraTransformType);
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
        return particle;
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
