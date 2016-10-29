package cofh.thermalexpansion.client.model;

import codechicken.lib.render.CCModelState;
import com.google.common.collect.ImmutableMap;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.MinecraftForgeClient;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by covers1624 on 28/10/2016.
 */
public class SimpleSmartLayeredModel implements IBakedModel {

    private final CCModelState modelState;
    private final ImmutableMap<EnumFacing, Map<BlockRenderLayer, List<BakedQuad>>> faceLayerQuadMap;
    private final ImmutableMap<BlockRenderLayer, List<BakedQuad>> generalQuads;

    public SimpleSmartLayeredModel(Map<EnumFacing, Map<BlockRenderLayer, List<BakedQuad>>> faceLayerQuadMap, Map<BlockRenderLayer, List<BakedQuad>> generalQuads, CCModelState modelState) {
        this.modelState = modelState;
        this.faceLayerQuadMap = ImmutableMap.copyOf(faceLayerQuadMap);
        this.generalQuads = ImmutableMap.copyOf(generalQuads);
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable IBlockState state, @Nullable EnumFacing side, long rand) {
        BlockRenderLayer layer = MinecraftForgeClient.getRenderLayer();
        if (state != null) {
            if (side == null) {
                if (generalQuads.containsKey(layer)){
                    return generalQuads.get(layer);
                }
            }
            if (faceLayerQuadMap.containsKey(side)){
                Map<BlockRenderLayer, List<BakedQuad>> faceQuadMap = faceLayerQuadMap.get(side);
                if (faceQuadMap.containsKey(layer)){
                    return faceQuadMap.get(layer);
                }
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
