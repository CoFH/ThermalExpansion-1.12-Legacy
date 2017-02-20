package cofh.thermalexpansion.plugins.jei.gui;

import cofh.lib.render.RenderHelper;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraftforge.fluids.FluidStack;
import org.lwjgl.opengl.GL11;

/**
 * this class represents the fluid progress bar in jei gui.
 */
public class InfusionProgress extends Progressable {
    private FluidStack fluid;

    public InfusionProgress(int x, int y, boolean reversed) {
        super(176, 48, x, y, 24, 16, 5, reversed, false);
    }

    public void setFluid(FluidStack fluid) {
        this.fluid = fluid;
    }

    @Override
    public void draw(int progressTicks) {
        if (fluid == null || fluid.getFluid() == null) {
            return;
        }

        GlStateManager.disableBlend();
        RenderHelper.setBlockTextureSheet();
        int colour = fluid.getFluid().getColor(fluid);
        GlStateManager.color((colour >> 16) & 0xFF, (colour >> 8) & 0xFF, colour & 0xFF, (colour >> 24) & 0xFF);

        TextureAtlasSprite sprite = RenderHelper.getTexture(fluid.getFluid().getStill(fluid));
        drawTiledTexture(sprite);
        super.draw(progressTicks);
        GlStateManager.enableBlend();
    }

    private void drawTiledTexture(TextureAtlasSprite icon) {

        int drawHeight;
        int drawWidth;

        for (int i = 0; i < width; i += 16) {
            for (int j = 0; j < height; j += 16) {
                drawWidth = Math.min(width - i, 16);
                drawHeight = Math.min(height - j, 16);
                drawScaledTexturedModelRectFromIcon(x + i, y + j, icon, drawWidth, drawHeight);
            }
        }
        GlStateManager.color(1, 1, 1, 1);
    }

    private void drawScaledTexturedModelRectFromIcon(int x, int y, TextureAtlasSprite icon, int width, int height) {

        if (icon == null) {
            return;
        }
        double minU = icon.getMinU();
        double maxU = icon.getMaxU();
        double minV = icon.getMinV();
        double maxV = icon.getMaxV();

        VertexBuffer buffer = Tessellator.getInstance().getBuffer();
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        buffer.pos(x, y + height, 0).tex(minU, minV + (maxV - minV) * height / 16F).endVertex();
        buffer.pos(x + width, y + height, 0).tex(minU + (maxU - minU) * width / 16F, minV + (maxV - minV) * height / 16F).endVertex();
        buffer.pos(x + width, y, 0).tex(minU + (maxU - minU) * width / 16F, minV).endVertex();
        buffer.pos(x, y, 0).tex(minU, minV).endVertex();
        Tessellator.getInstance().draw();
    }
}
