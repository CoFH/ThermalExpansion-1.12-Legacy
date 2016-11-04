package cofh.thermalexpansion.render;

import codechicken.lib.model.bakery.PlanarFaceBakery;
import codechicken.lib.render.CCModel;
import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.buffer.BakingVertexBuffer;
import codechicken.lib.texture.TextureUtils.IIconRegister;
import codechicken.lib.vec.Cuboid6;
import codechicken.lib.vec.uv.IconTransformation;
import cofh.core.render.IconRegistry;
import cofh.core.render.RenderUtils;
import cofh.lib.render.RenderHelper;
import cofh.thermalexpansion.block.CommonProperties;
import cofh.thermalexpansion.block.tank.BlockTank;
import cofh.thermalexpansion.block.tank.TileTank;
import cofh.thermalexpansion.client.bakery.ISimpleBlockBakery;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.List;

public class RenderTank implements ISimpleBlockBakery, IIconRegister {

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
        modelFrame/*.computeLighting(LightModel.standardLightModel)*/.shrinkUVs(RenderHelper.RENDER_OFFSET);
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

    public void renderFrame(CCRenderState ccrs, int metadata, int mode) {

        modelFrame.render(ccrs, 0, 4, new IconTransformation(textureBottom[2 * metadata + mode]));//Bottom
        modelFrame.render(ccrs, 24, 28, new IconTransformation(textureTop[2 * metadata + mode]));//Bottom inside
        modelFrame.render(ccrs, 4, 8, new IconTransformation(textureTop[2 * metadata]));//Top
        modelFrame.render(ccrs, 28, 32, new IconTransformation(textureBottom[2 * metadata]));//Top Inside.

        for (int i = 8; i < 24; i += 4) {
            modelFrame.render(ccrs, i, i + 4, new IconTransformation(textureSides[2 * metadata + mode]));//Sides.
        }
        for (int i = 32; i < 48; i += 4) {
            modelFrame.render(ccrs, i, i + 4, new IconTransformation(textureSides[2 * metadata + mode]));//Edges.
        }
    }

    public void renderFluid(CCRenderState ccrs, int metadata, FluidStack stack) {

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
        modelFluid[level].render(ccrs, new IconTransformation(fluidTex));
    }

    @Override
    public List<BakedQuad> bakeQuads(EnumFacing face, IExtendedBlockState state) {
        FluidStack fluidStack = state.getValue(BlockTank.FLUID_STACK_PROPERTY);
        byte mode = state.getValue(BlockTank.MODE_PROPERTY);
        int type = state.getValue(CommonProperties.TYPE_PROPERTY);
        if (face == null) {
            BakingVertexBuffer buffer = BakingVertexBuffer.create();
            buffer.begin(7, DefaultVertexFormats.ITEM);
            CCRenderState ccrs = CCRenderState.instance();
            ccrs.reset();
            ccrs.bind(buffer);

            renderFrame(ccrs, type, mode);
            renderFluid(ccrs, type, fluidStack);

            buffer.finishDrawing();
            return buffer.bake();
        }
        return new ArrayList<BakedQuad>();
    }

    @Override
    public IExtendedBlockState handleState(IExtendedBlockState state, TileEntity tileEntity) {
        TileTank tank = ((TileTank) tileEntity);
        state = state.withProperty(BlockTank.FLUID_STACK_PROPERTY, tank.getTankFluid());
        state = state.withProperty(BlockTank.MODE_PROPERTY, tank.mode);
        state = state.withProperty(CommonProperties.TYPE_PROPERTY, (int) tank.type);
        return state;
    }

    @Override
    public List<BakedQuad> bakeItemQuads(EnumFacing face, ItemStack stack) {
        if (face == null) {
            BakingVertexBuffer buffer = BakingVertexBuffer.create();
            buffer.begin(7, DefaultVertexFormats.ITEM);
            CCRenderState ccrs = CCRenderState.instance();
            ccrs.reset();
            ccrs.bind(buffer);

            FluidStack fluid = null;
            if (stack.getTagCompound() != null) {
                fluid = FluidStack.loadFluidStackFromNBT(stack.getTagCompound().getCompoundTag("Fluid"));
            }
            renderFrame(ccrs, stack.getItemDamage(), 0);
            renderFluid(ccrs, stack.getItemDamage(), fluid);

            buffer.finishDrawing();
            return PlanarFaceBakery.shadeQuadFaces(buffer.bake());
        }
        return new ArrayList<BakedQuad>();
    }

    @Override
    public void registerIcons(TextureMap textureMap) {
        for (int i = 0; i < BlockTank.Types.values().length; i++) {
            String name = BlockTank.NAMES[i];
            IconRegistry.addIcon("TankBottom" + 2 * i, "thermalexpansion:blocks/tank/tank_" + name + "_bottom_blue", textureMap);
            IconRegistry.addIcon("TankBottom" + (2 * i + 1), "thermalexpansion:blocks/tank/tank_" + name + "_bottom_orange", textureMap);

            IconRegistry.addIcon("TankTop" + 2 * i, "thermalexpansion:blocks/tank/tank_" + name + "_top_blue", textureMap);
            IconRegistry.addIcon("TankTop" + (2 * i + 1), "thermalexpansion:blocks/tank/tank_" + name + "_top_orange", textureMap);

            IconRegistry.addIcon("TankSide" + 2 * i, "thermalexpansion:blocks/tank/tank_" + name + "_side_blue", textureMap);
            IconRegistry.addIcon("TankSide" + (2 * i + 1), "thermalexpansion:blocks/tank/tank_" + name + "_side_orange", textureMap);
        }
    }
}
