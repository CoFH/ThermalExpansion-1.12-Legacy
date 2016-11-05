package cofh.thermalexpansion.render;

import codechicken.lib.model.bakery.PlanarFaceBakery;
import codechicken.lib.render.CCModel;
import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.buffer.BakingVertexBuffer;
import codechicken.lib.texture.TextureUtils.IIconRegister;
import codechicken.lib.vec.Vector3;
import codechicken.lib.vec.uv.IconTransformation;
import cofh.core.render.IconRegistry;
import cofh.lib.render.RenderHelper;
import cofh.thermalexpansion.block.CommonProperties;
import cofh.thermalexpansion.block.plate.BlockPlate;
import cofh.thermalexpansion.block.plate.TilePlateBase;
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

public class RenderPlate implements ISimpleBlockBakery, IIconRegister {

    public static final RenderPlate instance = new RenderPlate();

    static TextureAtlasSprite[] texture_frame = new TextureAtlasSprite[8];
    static TextureAtlasSprite[] texture_fluid = new TextureAtlasSprite[6];
    static CCModel[] side_model = new CCModel[6];

    static {

        generateModels();
    }

    public static void initialize() {

        texture_fluid[0] = IconRegistry.getIcon("FluidRedstone");
        texture_fluid[1] = IconRegistry.getIcon("FluidGlowstone");
        texture_fluid[2] = IconRegistry.getIcon("FluidEnder");
        texture_fluid[3] = IconRegistry.getIcon("FluidRedstone");
        texture_fluid[4] = IconRegistry.getIcon("FluidGlowstone");
        texture_fluid[5] = IconRegistry.getIcon("FluidEnder");

        texture_frame[6] = IconRegistry.getIcon("PlateBottom");
        texture_frame[7] = IconRegistry.getIcon("PlateTopO");
        for (int i = 0; i < 6; i++) {
            texture_frame[i] = IconRegistry.getIcon("PlateTop", i);
        }
    }

    private static void generateModels() {

        double d = RenderHelper.RENDER_OFFSET;
        side_model[0] = CCModel.quadModel(48).generateBlock(0, 0, 0, 0, 1, 1. / 16, 1);
        CCModel temp = CCModel.quadModel(24).generateBlock(0, d, d, d, 1 - d, 1. / 16 - d, 1 - d);
        CCModel.generateBackface(temp, 0, side_model[0], 24, 24);
        side_model[0].shrinkUVs(RenderHelper.RENDER_OFFSET);
        CCModel.generateSidedModels(side_model, 0, new Vector3(0.5, 0.5, 0.5));
        for (int i = side_model.length; i-- > 0; ) {
            side_model[i].computeNormals();
        }
    }

    @Override
    public void registerIcons(TextureMap textureMap) {
        IconRegistry.addIcon("PlateBottom", "thermalexpansion:blocks/plate/plate_bottom", textureMap);
        IconRegistry.addIcon("PlateTopO", "thermalexpansion:blocks/plate/plate_top_circle", textureMap);
        IconRegistry.addIcon("PlateTop0", "thermalexpansion:blocks/plate/plate_top_down", textureMap);
        IconRegistry.addIcon("PlateTop1", "thermalexpansion:blocks/plate/plate_top_up", textureMap);
        IconRegistry.addIcon("PlateTop2", "thermalexpansion:blocks/plate/plate_top_north", textureMap);
        IconRegistry.addIcon("PlateTop3", "thermalexpansion:blocks/plate/plate_top_south", textureMap);
        IconRegistry.addIcon("PlateTop4", "thermalexpansion:blocks/plate/plate_top_west", textureMap);
        IconRegistry.addIcon("PlateTop5", "thermalexpansion:blocks/plate/plate_top_east", textureMap);
    }

    public void render(CCRenderState ccrs, int alignment, int direction, int type) {

        if (direction < 6) {
            int flip = alignment == 1 ? ((direction >> 1) & 1) ^ 1 : 1;
            // top plates need north/south inverted specially (otherwise flip would always be 1)
            int off = (alignment > 1 & (direction >> 1 == alignment >> 1)) ? 1 : flip ^ 1;
            // if the alignment and direction are the same class and not up/down, invert. apply special case from above
            int s = (alignment & 1) ^ flip;
            // if the alignment needs inversion
            direction ^= s & off;
        }

        CCModel model = side_model[alignment];
        if (type > 0) {
            model.render(ccrs, 4, 8, new IconTransformation(texture_fluid[type - 1]));
        }
        model.render(ccrs, 4, 8, new IconTransformation(texture_frame[direction]));
        IconTransformation transform = new IconTransformation(texture_frame[6]);
        model.render(ccrs, 0, 4, transform);
        model.render(ccrs, 24, 28, transform);

        for (int i = 8; i < 24; i += 4) {
            model.render(ccrs, i, i + 4, transform);
            model.render(ccrs, 24 + i, 24 + i + 4, transform);
        }
    }

    @Override
    public IExtendedBlockState handleState(IExtendedBlockState state, TileEntity tileEntity) {
        TilePlateBase plate = (TilePlateBase) tileEntity;
        state = state.withProperty(BlockPlate.ALIGNMENT_PROPERTY, plate.getAlignment());
        state = state.withProperty(CommonProperties.FACING_PROPERTY, plate.getFacing());
        state = state.withProperty(CommonProperties.TYPE_PROPERTY, plate.getType());

        return state;
    }

    @Override
    public List<BakedQuad> bakeQuads(EnumFacing face, IExtendedBlockState state) {
        if (face == null) {
            int alignment = state.getValue(BlockPlate.ALIGNMENT_PROPERTY);
            int facing = state.getValue(CommonProperties.FACING_PROPERTY);
            int type = state.getValue(CommonProperties.TYPE_PROPERTY);
            BakingVertexBuffer buffer = BakingVertexBuffer.create();
            buffer.begin(7, DefaultVertexFormats.ITEM);
            CCRenderState ccrs = CCRenderState.instance();
            ccrs.reset();
            ccrs.bind(buffer);

            render(ccrs, alignment, facing, type);

            buffer.finishDrawing();
            return buffer.bake();
        }
        return new ArrayList<BakedQuad>();
    }

    @Override
    public List<BakedQuad> bakeItemQuads(EnumFacing face, ItemStack stack) {
        if (face == null) {
            BakingVertexBuffer buffer = BakingVertexBuffer.create();
            buffer.begin(7, DefaultVertexFormats.ITEM);
            CCRenderState ccrs = CCRenderState.instance();
            ccrs.reset();
            ccrs.bind(buffer);

            render(ccrs, 0, BlockPlate.Types.values()[stack.getMetadata()].texture, stack.getMetadata());

            buffer.finishDrawing();
            return PlanarFaceBakery.shadeQuadFaces(buffer.bake());
        }
        return new ArrayList<BakedQuad>();
    }
}
