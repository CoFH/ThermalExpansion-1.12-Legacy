package cofh.thermalexpansion.plugins.jei.gui;

import cofh.lib.render.RenderHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.config.GuiUtils;

public final class ResourceUtils {
    private static boolean axisMode = false;

    private ResourceUtils() {
    }

    public static void drawScaledTexturedRectFromModel(int x, int y, int u, int v, int width, int height, ResourceLocation location) {
        RenderHelper.bindTexture(location);
        GuiUtils.drawTexturedModalRect(x, y, u, v, width, height, 0);
    }

    //sets the Axis axisMode of the Mixed renderer.
    public static void setAxisMode(boolean y) {
        axisMode = y;
    }

    //renders a texture up to switchPoint parameter and then renders another Texture.
    public static void drawMixedTextures(int x, int y, int u, int v, int width, int height, int switchPoint, ResourceLocation first, ResourceLocation second, boolean reversed) {
        if (axisMode) {
            Minecraft.getMinecraft().getTextureManager().bindTexture(first);
            int nonFilledY = reversed ? y + height - switchPoint : y;
            int nonFilledV = reversed ? v + height - switchPoint : v;
            GuiUtils.drawTexturedModalRect(x, nonFilledY, u, nonFilledV, width, switchPoint, 0);

            Minecraft.getMinecraft().getTextureManager().bindTexture(second);
            int filledY = reversed ? y : y + switchPoint;
            int filledV = reversed ? v : v + switchPoint;
            int filledU = u + width;
            int filledHeight = height - switchPoint;
            GuiUtils.drawTexturedModalRect(x, filledY, filledU, filledV, width, filledHeight, 0);
        } else {
            Minecraft.getMinecraft().getTextureManager().bindTexture(first);
            int nonFilledX = reversed ? x : x + switchPoint;
            int nonFilledU = reversed ? u : u + switchPoint;
            int nonFilledWidth = width - switchPoint;
            GuiUtils.drawTexturedModalRect(nonFilledX, y, nonFilledU, v, nonFilledWidth, height, 0);

            Minecraft.getMinecraft().getTextureManager().bindTexture(second);
            int filledX = reversed ? x + width - switchPoint : x;
            int filledU = reversed ? u + width + width - switchPoint : u + width;
            GuiUtils.drawTexturedModalRect(filledX, y, filledU, v, switchPoint, height, 0);
        }

    }
}
