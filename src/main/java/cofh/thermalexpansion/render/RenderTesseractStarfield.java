package cofh.thermalexpansion.render;

import codechicken.lib.render.CCRenderState;
import codechicken.lib.texture.TextureUtils;
import cofh.core.render.ShaderHelper;
import cofh.lib.render.RenderHelper;
import cofh.thermalexpansion.block.ender.TileTesseract;
import cofh.thermalexpansion.core.TEProps;
import cofh.thermalfoundation.render.shader.ShaderStarfield;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.TexGen;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import org.lwjgl.opengl.GL11;

import java.nio.FloatBuffer;
import java.util.Random;

public class RenderTesseractStarfield extends TileEntitySpecialRenderer<TileTesseract> {

    public static RenderTesseractStarfield instance = new RenderTesseractStarfield();

    public static void register() {

        ClientRegistry.bindTileEntitySpecialRenderer(TileTesseract.class, RenderTesseractStarfield.instance);
    }

    private static final ResourceLocation field_147529_c = new ResourceLocation("textures/environment/end_sky.png");
    private static final ResourceLocation field_147526_d = new ResourceLocation("textures/entity/end_portal.png");
    private static final Random random = new Random(0);
    FloatBuffer field_147528_b = GLAllocation.createDirectFloatBuffer(16);

    public void renderDefaultStarField(TileTesseract tile, double x, double y, double z, float time) {

        World world = tile.getWorld();
        random.setSeed(31110L + tile.frequency);

        GlStateManager.disableLighting();

        GlStateManager.texGen(TexGen.S, GL11.GL_EYE_LINEAR);
        GlStateManager.texGen(TexGen.T, GL11.GL_EYE_LINEAR);
        GlStateManager.texGen(TexGen.R, GL11.GL_EYE_LINEAR);
        GlStateManager.texGen(TexGen.Q, GL11.GL_EYE_LINEAR);
        GlStateManager.enableTexGenCoord(TexGen.S);
        GlStateManager.enableTexGenCoord(TexGen.T);
        GlStateManager.enableTexGenCoord(TexGen.R);
        GlStateManager.enableTexGenCoord(TexGen.Q);
        GlStateManager.matrixMode(GL11.GL_TEXTURE);
        GlStateManager.pushMatrix();
        GlStateManager.loadIdentity();
        GlStateManager.translate(random.nextFloat(), world.getTotalWorldTime() % 50000L / 50000F, random.nextFloat());

        Tessellator tessellator = Tessellator.getInstance();
        VertexBuffer buffer = tessellator.getBuffer();

        final int end = 8;
        for (int i = 0; i < end; ++i) {
            float f5 = end - i;
            float f7 = 1.0F / (f5 + 1.0F);

            if (i == 0) {
                this.bindTexture(field_147529_c);
                f7 = 0.0F;
                f5 = 65.0F;
                GlStateManager.enableBlend();
                GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            }

            if (i == 1) {
                this.bindTexture(field_147526_d);
                GlStateManager.enableBlend();
                GlStateManager.blendFunc(GL11.GL_ONE, GL11.GL_ONE);
            }

            GlStateManager.translate(random.nextFloat() * (1 - f7), 0, random.nextFloat() * (1 - f7));

            float f11 = (float) random.nextDouble() * 0.5F + 0.1F;
            float f12 = (float) random.nextDouble() * 0.5F + 0.4F;
            float f13 = (float) random.nextDouble() * 0.5F + 0.5F;
            if (i == 0) {
                f13 = 1.0F;
                f12 = 1.0F;
                f11 = 1.0F;
            }
            f13 *= f7;
            f12 *= f7;
            f11 *= f7;

            GlStateManager.texGen(TexGen.S, GL11.GL_EYE_PLANE, this.func_147525_a(1, 0, 0, 0));
            GlStateManager.texGen(TexGen.T, GL11.GL_EYE_PLANE, this.func_147525_a(0, 0, 1, 0));
            GlStateManager.texGen(TexGen.R, GL11.GL_EYE_PLANE, this.func_147525_a(0, 0, 0, 1));
            GlStateManager.texGen(TexGen.Q, GL11.GL_EYE_PLANE, this.func_147525_a(0, 1, 0, 0));

            int l1 = 0xF000F0 >> 16 & 65535;
            int l2 = 0xF000F0 & 65535;

            GlStateManager.rotate(180, 0, 0, 1);
            buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);//TODO Should this format have Lmap. Seems pointless as lighting is disabled.
            //tessellator.setColorOpaque_F(f11, f12, f13);
            //tessellator.setBrightness(0xF000F0);
            buffer.pos(x + 0.14, y + 0.14, z + 0.87).color(f11, f12, f13, 1.0F).endVertex();
            buffer.pos(x + 0.14, y + 0.14, z + 0.14).color(f11, f12, f13, 1.0F).endVertex();
            buffer.pos(x + 0.87, y + 0.14, z + 0.14).color(f11, f12, f13, 1.0F).endVertex();
            buffer.pos(x + 0.87, y + 0.14, z + 0.87).color(f11, f12, f13, 1.0F).endVertex();
            tessellator.draw();

            buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
            //tessellator.setColorOpaque_F(f11, f12, f13);
            //tessellator.setBrightness(0xF000F0);
            buffer.pos(x + 0.87, y + 0.87, z + 0.87).color(f11, f12, f13, 1.0F).endVertex();
            buffer.pos(x + 0.87, y + 0.87, z + 0.14).color(f11, f12, f13, 1.0F).endVertex();
            buffer.pos(x + 0.14, y + 0.87, z + 0.14).color(f11, f12, f13, 1.0F).endVertex();
            buffer.pos(x + 0.14, y + 0.87, z + 0.87).color(f11, f12, f13, 1.0F).endVertex();
            tessellator.draw();

            GlStateManager.texGen(TexGen.S, GL11.GL_EYE_PLANE, this.func_147525_a(0, 1, 0, 0));
            GlStateManager.texGen(TexGen.T, GL11.GL_EYE_PLANE, this.func_147525_a(1, 0, 0, 0));
            GlStateManager.texGen(TexGen.R, GL11.GL_EYE_PLANE, this.func_147525_a(0, 0, 0, 1));
            GlStateManager.texGen(TexGen.Q, GL11.GL_EYE_PLANE, this.func_147525_a(0, 0, 1, 0));

            buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
            //tessellator.setColorOpaque_F(f11, f12, f13);
            //tessellator.setBrightness(0xF000F0);
            buffer.pos(x + 0.14, y + 0.14, z + 0.14).color(f11, f12, f13, 1.0F).endVertex();
            buffer.pos(x + 0.14, y + 0.87, z + 0.14).color(f11, f12, f13, 1.0F).endVertex();
            buffer.pos(x + 0.87, y + 0.87, z + 0.14).color(f11, f12, f13, 1.0F).endVertex();
            buffer.pos(x + 0.87, y + 0.14, z + 0.14).color(f11, f12, f13, 1.0F).endVertex();

            buffer.pos(x + 0.87, y + 0.14, z + 0.87).color(f11, f12, f13, 1.0F).endVertex();
            buffer.pos(x + 0.87, y + 0.87, z + 0.87).color(f11, f12, f13, 1.0F).endVertex();
            buffer.pos(x + 0.14, y + 0.87, z + 0.87).color(f11, f12, f13, 1.0F).endVertex();
            buffer.pos(x + 0.14, y + 0.14, z + 0.87).color(f11, f12, f13, 1.0F).endVertex();
            tessellator.draw();

            GlStateManager.texGen(TexGen.S, GL11.GL_EYE_PLANE, this.func_147525_a(0, 1, 0, 0));
            GlStateManager.texGen(TexGen.T, GL11.GL_EYE_PLANE, this.func_147525_a(0, 0, 1, 0));
            GlStateManager.texGen(TexGen.R, GL11.GL_EYE_PLANE, this.func_147525_a(0, 0, 0, 1));
            GlStateManager.texGen(TexGen.Q, GL11.GL_EYE_PLANE, this.func_147525_a(1, 0, 0, 0));

            buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
            //tessellator.setColorOpaque_F(f11, f12, f13);
            //tessellator.setBrightness(0xF000F0);
            buffer.pos(x + 0.14, y + 0.14, z + 0.87).color(f11, f12, f13, 1.0F).endVertex();
            buffer.pos(x + 0.14, y + 0.87, z + 0.87).color(f11, f12, f13, 1.0F).endVertex();
            buffer.pos(x + 0.14, y + 0.87, z + 0.14).color(f11, f12, f13, 1.0F).endVertex();
            buffer.pos(x + 0.14, y + 0.14, z + 0.14).color(f11, f12, f13, 1.0F).endVertex();
            tessellator.draw();

            buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
            //tessellator.setColorOpaque_F(f11, f12, f13);
            //tessellator.setBrightness(0xF000F0);
            buffer.pos(x + 0.87, y + 0.14, z + 0.14).color(f11, f12, f13, 1.0F).endVertex();
            buffer.pos(x + 0.87, y + 0.87, z + 0.14).color(f11, f12, f13, 1.0F).endVertex();
            buffer.pos(x + 0.87, y + 0.87, z + 0.87).color(f11, f12, f13, 1.0F).endVertex();
            buffer.pos(x + 0.87, y + 0.14, z + 0.87).color(f11, f12, f13, 1.0F).endVertex();
            tessellator.draw();
        }

        GlStateManager.popMatrix();
        GlStateManager.matrixMode(GL11.GL_MODELVIEW);
        GlStateManager.disableBlend();
        GlStateManager.disableTexGenCoord(TexGen.S);
        GlStateManager.disableTexGenCoord(TexGen.T);
        GlStateManager.disableTexGenCoord(TexGen.R);
        GlStateManager.disableTexGenCoord(TexGen.Q);
        GlStateManager.enableLighting();
    }

    private FloatBuffer func_147525_a(float x, float y, float z, float w) {

        this.field_147528_b.clear();
        this.field_147528_b.put(x).put(y).put(z).put(w);
        this.field_147528_b.flip();
        return this.field_147528_b;
    }

    @Override
    public void renderTileEntityAt(TileTesseract tile, double x, double y, double z, float f, int destroyStage) {

        if (!tile.isActive) {
            return;
        }

        if (TEProps.useAlternateStarfieldShader || ShaderStarfield.starfieldShader == 0) {
            renderDefaultStarField(tile, x, y, z, 1 - f);
            return;
        }
        GlStateManager.pushMatrix();

        TextureUtils.changeTexture(ShaderStarfield.starsTexture);

        GlStateManager.translate(x + 0.5, y + 0.5, z + 0.5);
        GlStateManager.scale(1 + RenderHelper.RENDER_OFFSET, 1 + RenderHelper.RENDER_OFFSET, 1 + RenderHelper.RENDER_OFFSET);
        ShaderStarfield.alpha = 0;

        ShaderHelper.useShader(ShaderStarfield.starfieldShader, ShaderStarfield.callback);
        CCRenderState ccrs = CCRenderState.instance();

        ccrs.startDrawing(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);
        RenderTesseract.instance.renderCenter(ccrs, false, tile.isActive, -0.5, -0.5, -0.5);
        ccrs.draw();
        ShaderHelper.releaseShader();

        GlStateManager.popMatrix();
    }

}
