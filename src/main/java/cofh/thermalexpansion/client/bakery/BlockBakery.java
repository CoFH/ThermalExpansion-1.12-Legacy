package cofh.thermalexpansion.client.bakery;

import codechicken.lib.model.SimplePerspectiveAwareBakedModel;
import codechicken.lib.model.bakery.PlanarFaceBakery;
import codechicken.lib.util.TransformUtils;
import cofh.api.tileentity.ISidedTexture;
import cofh.thermalexpansion.client.UnlistedMapProperty;
import cofh.thermalexpansion.client.UnlistedSpriteProperty;
import cofh.thermalexpansion.client.model.SimplePerspectiveAwareBakedLayerModel;
import cofh.thermalexpansion.core.IBlockTextureProvider;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.ImmutableList;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.property.IExtendedBlockState;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by covers1624 on 25/10/2016.
 */
public class BlockBakery {

    public static final UnlistedMapProperty SPRITE_FACE_LAYER_PROPERTY = new UnlistedMapProperty("sprite_face_layer_map");//Contains face layer sprites. A.K.A, Blocks.
    public static final UnlistedSpriteProperty PARTICLE_SPRITE_PROPERTY = new UnlistedSpriteProperty("sprite");//Contains the.. Particle sprite.

    private static Cache<IExtendedBlockState, IBakedModel> stateModelCache = CacheBuilder.newBuilder().expireAfterAccess(5, TimeUnit.MINUTES).build();

    public static IBlockState handleExtendedState(IExtendedBlockState state, TileEntity tileEntity) {
        HashMap<EnumFacing, Map<BlockRenderLayer, TextureAtlasSprite>> spriteFaceLayerMap = new HashMap<EnumFacing, Map<BlockRenderLayer, TextureAtlasSprite>>();
        Block block = state.getBlock();
        IBlockTextureProvider textureProvider = (IBlockTextureProvider) block;
        int blockMeta = block.getMetaFromState(state);
        for (EnumFacing face : EnumFacing.VALUES) {
            Map<BlockRenderLayer, TextureAtlasSprite> spriteLayerMap = new HashMap<BlockRenderLayer, TextureAtlasSprite>();
            for (int pass = 0; pass < textureProvider.getTexturePasses(); pass++) {
                BlockRenderLayer renderLayer = textureProvider.getRenderlayerForPass(pass);

                TextureAtlasSprite sprite = ((ISidedTexture) tileEntity).getTexture(face.ordinal(), pass);
                spriteLayerMap.put(renderLayer, sprite);
            }
            spriteFaceLayerMap.put(face, spriteLayerMap);
        }
        IExtendedBlockState returnState = state;
        returnState = returnState.withProperty(SPRITE_FACE_LAYER_PROPERTY, spriteFaceLayerMap);
        returnState = returnState.withProperty(PARTICLE_SPRITE_PROPERTY, textureProvider.getTexture(EnumFacing.UP, blockMeta));
        return returnState;
    }

    public static IBakedModel generateItemModel(ItemStack stack) {
        IBlockTextureProvider block = (IBlockTextureProvider) Block.getBlockFromItem(stack.getItem());
        LinkedList<BakedQuad> itemQuads = new LinkedList<BakedQuad>();
        itemQuads.addAll(bakeItemFace(EnumFacing.UP, block.getTexture(EnumFacing.UP, stack.getMetadata())));
        itemQuads.addAll(bakeItemFace(EnumFacing.DOWN, block.getTexture(EnumFacing.DOWN, stack.getMetadata())));
        for (EnumFacing face : EnumFacing.HORIZONTALS) {
            itemQuads.addAll(bakeItemFace(face, block.getTexture(face.getOpposite(), stack.getMetadata())));
        }
        return new SimplePerspectiveAwareBakedModel(itemQuads, TransformUtils.DEFAULT_BLOCK);
    }

    public static IBakedModel getCachedModel(IExtendedBlockState state) {
        IBakedModel model = stateModelCache.getIfPresent(state);
        if (model == null) {
            model = generateModel(state);
            stateModelCache.put(state, model);
        }
        return model;
    }

    public static IBakedModel generateModel(IExtendedBlockState state) {
        Map<EnumFacing, Map<BlockRenderLayer, List<BakedQuad>>> quadFaceLayerMap = generateQuadFaceLayerMap(state);
        Map<EnumFacing, List<BakedQuad>> quadFaceMap = new HashMap<EnumFacing, List<BakedQuad>>();//Dummy map.
        TextureAtlasSprite particle = state.getValue(PARTICLE_SPRITE_PROPERTY);
        return new SimplePerspectiveAwareBakedLayerModel(quadFaceLayerMap, quadFaceMap, particle, TransformUtils.DEFAULT_BLOCK);
    }

    public static Map<EnumFacing, Map<BlockRenderLayer, List<BakedQuad>>> generateQuadFaceLayerMap(IExtendedBlockState state) {
        Map<EnumFacing, Map<BlockRenderLayer, TextureAtlasSprite>> spriteFaceLayerMap = state.getValue(SPRITE_FACE_LAYER_PROPERTY);
        Map<EnumFacing, Map<BlockRenderLayer, List<BakedQuad>>> quadFaceLayerMap = new HashMap<EnumFacing, Map<BlockRenderLayer, List<BakedQuad>>>();
        for (EnumFacing face : spriteFaceLayerMap.keySet()) {
            Map<BlockRenderLayer, TextureAtlasSprite> spriteLayerMap = spriteFaceLayerMap.get(face);
            Map<BlockRenderLayer, List<BakedQuad>> quadLayerMap = new HashMap<BlockRenderLayer, List<BakedQuad>>();
            for (BlockRenderLayer layer : spriteLayerMap.keySet()) {
                LinkedList<BakedQuad> quads = new LinkedList<BakedQuad>();
                quads.addAll(bakeBlockFace(face, spriteLayerMap.get(layer)));
                quadLayerMap.put(layer, quads);
            }
            quadFaceLayerMap.put(face, quadLayerMap);
        }
        return quadFaceLayerMap;
    }

    public static List<BakedQuad> bakeItemFace(EnumFacing face, TextureAtlasSprite sprite) {
        return ImmutableList.of(PlanarFaceBakery.bakeFace(face, sprite, DefaultVertexFormats.ITEM));
    }

    public static List<BakedQuad> bakeBlockFace(EnumFacing face, TextureAtlasSprite sprite) {
        return ImmutableList.of(PlanarFaceBakery.bakeFace(face, sprite, DefaultVertexFormats.POSITION_TEX_COLOR_NORMAL));
    }

}
