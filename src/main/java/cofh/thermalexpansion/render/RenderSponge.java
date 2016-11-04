package cofh.thermalexpansion.render;

import codechicken.lib.render.CCModel;
import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.buffer.BakingVertexBuffer;
import codechicken.lib.texture.TextureUtils.IIconRegister;
import codechicken.lib.vec.Cuboid6;
import cofh.core.render.IconRegistry;
import cofh.core.render.RenderUtils;
import cofh.thermalexpansion.block.sponge.BlockSponge;
import cofh.thermalexpansion.block.sponge.TileSponge;
import cofh.thermalexpansion.client.bakery.ISimpleBlockBakery;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.property.IExtendedBlockState;

import java.util.ArrayList;
import java.util.List;

public class RenderSponge implements ISimpleBlockBakery, IIconRegister {

    public static final RenderSponge instance = new RenderSponge();

    static TextureAtlasSprite[] textures = new TextureAtlasSprite[5];
    static CCModel modelSponge = CCModel.quadModel(24);

    static {
        Cuboid6 box = new Cuboid6(0, 0, 0, 1, 1, 1);
        modelSponge.generateBlock(0, box);
    }

    public static void initialize() {
        for (int i = 0; i < 5; i++) {
            textures[i] = IconRegistry.getIcon("Sponge" + i);
        }
    }

    @Override
    public void registerIcons(TextureMap textureMap) {
        IconRegistry.addIcon("Sponge" + 0, "thermalexpansion:blocks/sponge/sponge_creative", textureMap);
        IconRegistry.addIcon("Sponge" + 1, "thermalexpansion:blocks/sponge/sponge_basic", textureMap);
        IconRegistry.addIcon("Sponge" + 2, "thermalexpansion:blocks/sponge/sponge_magmatic", textureMap);

        IconRegistry.addIcon("Sponge" + 3, "thermalexpansion:blocks/sponge/sponge_basic_soaked", textureMap);
        IconRegistry.addIcon("Sponge" + 4, "thermalexpansion:blocks/sponge/sponge_magmatic_soaked", textureMap);
    }

    @Override
    public List<BakedQuad> bakeQuads(EnumFacing face, IExtendedBlockState state) {
        if (face == null) {
            BakingVertexBuffer buffer = BakingVertexBuffer.create();
            buffer.begin(7, DefaultVertexFormats.ITEM);
            CCRenderState ccrs = CCRenderState.instance();
            ccrs.reset();
            ccrs.bind(buffer);
            int meta = state.getBlock().getMetaFromState(state);
            boolean soaked = state.getValue(BlockSponge.SOAKED);
            renderSponge(ccrs, meta, soaked);
            buffer.finishDrawing();
            return buffer.bake();
        }
        return new ArrayList<BakedQuad>();
    }

    private void renderSponge(CCRenderState ccrs, int meta, boolean soaked) {
        if (!soaked) {
            modelSponge.render(ccrs, RenderUtils.getIconTransformation(textures[meta]));
        } else {
            modelSponge.render(ccrs, RenderUtils.getIconTransformation(textures[meta + 2]));
        }
    }

    @Override
    public IExtendedBlockState handleState(IExtendedBlockState state, TileEntity tileEntity) {

        TileSponge tile = (TileSponge) tileEntity;
        if (tile != null) {
            return state.withProperty(BlockSponge.SOAKED, tile.getFluid() != null);
        }
        return state.withProperty(BlockSponge.SOAKED, false);
    }

    @Override
    public List<BakedQuad> bakeItemQuads(EnumFacing face, ItemStack stack) {
        if (face == null) {
            BakingVertexBuffer buffer = BakingVertexBuffer.create();
            buffer.begin(7, DefaultVertexFormats.ITEM);
            CCRenderState ccrs = CCRenderState.instance();
            ccrs.reset();
            ccrs.bind(buffer);
            int meta = stack.getMetadata();
            boolean soaked = !(stack.getTagCompound() == null || !stack.getTagCompound().hasKey("Fluid"));
            renderSponge(ccrs, meta, soaked);
            buffer.finishDrawing();
            return buffer.bake();
        }
        return new ArrayList<BakedQuad>();
    }
}
