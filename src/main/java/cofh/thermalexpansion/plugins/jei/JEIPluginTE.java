package cofh.thermalexpansion.plugins.jei;

import cofh.lib.render.RenderHelper;
import cofh.thermalexpansion.plugins.jei.charger.ChargerRecipeCategory;
import cofh.thermalexpansion.plugins.jei.compactor.CompactorRecipeCategory;
import cofh.thermalexpansion.plugins.jei.crucible.CrucibleRecipeCategory;
import cofh.thermalexpansion.plugins.jei.furnace.FurnaceRecipeCategory;
import cofh.thermalexpansion.plugins.jei.insolator.InsolatorRecipeCategory;
import cofh.thermalexpansion.plugins.jei.pulverizer.PulverizerRecipeCategory;
import cofh.thermalexpansion.plugins.jei.refinery.RefineryRecipeCategory;
import cofh.thermalexpansion.plugins.jei.sawmill.SawmillRecipeCategory;
import cofh.thermalexpansion.plugins.jei.smelter.SmelterRecipeCategory;
import cofh.thermalexpansion.plugins.jei.transposer.Descriptions;
import cofh.thermalexpansion.plugins.jei.transposer.TransposerRecipeCategory;
import mezz.jei.api.BlankModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.ISubtypeRegistry;
import mezz.jei.api.JEIPlugin;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraftforge.fluids.FluidStack;
import org.lwjgl.opengl.GL11;

@JEIPlugin
public class JEIPluginTE extends BlankModPlugin {

	@Override
	public void registerItemSubtypes(ISubtypeRegistry subtypeRegistry) {

	}

	@Override
	public void register(IModRegistry registry) {

		FurnaceRecipeCategory.initialize(registry);
		PulverizerRecipeCategory.initialize(registry);
		SawmillRecipeCategory.initialize(registry);
		SmelterRecipeCategory.initialize(registry);
		InsolatorRecipeCategory.initialize(registry);
		CompactorRecipeCategory.initialize(registry);
		CrucibleRecipeCategory.initialize(registry);
		RefineryRecipeCategory.initialize(registry);
		TransposerRecipeCategory.initialize(registry);
		ChargerRecipeCategory.initialize(registry);

		Descriptions.register(registry);
	}

	/* HELPERS */
	public static void drawFluid(int x, int y, FluidStack fluid, int width, int height) {

		if (fluid == null) {
			return;
		}
		RenderHelper.setBlockTextureSheet();
		int colour = fluid.getFluid().getColor(fluid);
		GlStateManager.color((colour >> 16) & 0xFF, (colour >> 8) & 0xFF, colour & 0xFF, (colour >> 24) & 0xFF);
		drawTiledTexture(x, y, RenderHelper.getTexture(fluid.getFluid().getStill(fluid)), width, height);
	}

	public static void drawTiledTexture(int x, int y, TextureAtlasSprite icon, int width, int height) {

		int i;
		int j;

		int drawHeight;
		int drawWidth;

		for (i = 0; i < width; i += 16) {
			for (j = 0; j < height; j += 16) {
				drawWidth = Math.min(width - i, 16);
				drawHeight = Math.min(height - j, 16);
				drawScaledTexturedModelRectFromIcon(x + i, y + j, icon, drawWidth, drawHeight);
			}
		}
		GlStateManager.color(1, 1, 1, 1);
	}

	public static void drawScaledTexturedModelRectFromIcon(int x, int y, TextureAtlasSprite icon, int width, int height) {

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
