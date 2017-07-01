package cofh.thermalexpansion.plugins.jei;

import cofh.core.util.helpers.RenderHelper;
import cofh.thermalexpansion.plugins.jei.crafting.centrifuge.CentrifugeRecipeCategory;
import cofh.thermalexpansion.plugins.jei.crafting.charger.ChargerRecipeCategory;
import cofh.thermalexpansion.plugins.jei.crafting.compactor.CompactorRecipeCategory;
import cofh.thermalexpansion.plugins.jei.crafting.crucible.CrucibleRecipeCategory;
import cofh.thermalexpansion.plugins.jei.crafting.furnace.FurnaceRecipeCategory;
import cofh.thermalexpansion.plugins.jei.crafting.insolator.InsolatorRecipeCategory;
import cofh.thermalexpansion.plugins.jei.crafting.pulverizer.PulverizerRecipeCategory;
import cofh.thermalexpansion.plugins.jei.crafting.refinery.RefineryRecipeCategory;
import cofh.thermalexpansion.plugins.jei.crafting.sawmill.SawmillRecipeCategory;
import cofh.thermalexpansion.plugins.jei.crafting.smelter.SmelterRecipeCategory;
import cofh.thermalexpansion.plugins.jei.crafting.transposer.TransposerRecipeCategory;
import cofh.thermalexpansion.plugins.jei.fuels.compression.CompressionFuelCategory;
import cofh.thermalexpansion.plugins.jei.fuels.coolant.CoolantCategory;
import cofh.thermalexpansion.plugins.jei.fuels.enervation.EnervationFuelCategory;
import cofh.thermalexpansion.plugins.jei.fuels.magmatic.MagmaticFuelCategory;
import cofh.thermalexpansion.plugins.jei.fuels.numismatic.NumismaticFuelCategory;
import cofh.thermalexpansion.plugins.jei.fuels.reactant.ReactantFuelCategory;
import cofh.thermalexpansion.plugins.jei.fuels.steam.SteamFuelCategory;
import mezz.jei.api.*;
import mezz.jei.api.recipe.IRecipeCategoryRegistration;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraftforge.fluids.FluidStack;
import org.lwjgl.opengl.GL11;

@JEIPlugin
public class JEIPluginTE implements IModPlugin {

	@Override
	public void registerCategories(IRecipeCategoryRegistration registry) {

		FurnaceRecipeCategory.register(registry);
		PulverizerRecipeCategory.register(registry);
		SawmillRecipeCategory.register(registry);
		SmelterRecipeCategory.register(registry);
		InsolatorRecipeCategory.register(registry);
		CompactorRecipeCategory.register(registry);
		CrucibleRecipeCategory.register(registry);
		RefineryRecipeCategory.register(registry);
		TransposerRecipeCategory.register(registry);
		ChargerRecipeCategory.register(registry);
		CentrifugeRecipeCategory.register(registry);

		SteamFuelCategory.register(registry);
		MagmaticFuelCategory.register(registry);
		CompressionFuelCategory.register(registry);
		ReactantFuelCategory.register(registry);
		EnervationFuelCategory.register(registry);
		NumismaticFuelCategory.register(registry);

		CoolantCategory.register(registry);
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
		CentrifugeRecipeCategory.initialize(registry);

		SteamFuelCategory.initialize(registry);
		MagmaticFuelCategory.initialize(registry);
		CompressionFuelCategory.initialize(registry);
		ReactantFuelCategory.initialize(registry);
		EnervationFuelCategory.initialize(registry);
		NumismaticFuelCategory.initialize(registry);

		CoolantCategory.initialize(registry);

		Descriptions.register(registry);
	}

	/* HELPERS */
	public static void drawFluid(int x, int y, FluidStack fluid, int width, int height) {

		if (fluid == null) {
			return;
		}
		RenderHelper.setBlockTextureSheet();
		int color = fluid.getFluid().getColor(fluid);
		RenderHelper.setGLColorFromInt(color);
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
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
	}

	public static void drawScaledTexturedModelRectFromIcon(int x, int y, TextureAtlasSprite icon, int width, int height) {

		if (icon == null) {
			return;
		}
		double minU = icon.getMinU();
		double maxU = icon.getMaxU();
		double minV = icon.getMinV();
		double maxV = icon.getMaxV();

		BufferBuilder buffer = Tessellator.getInstance().getBuffer();
		buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
		buffer.pos(x, y + height, 0).tex(minU, minV + (maxV - minV) * height / 16F).endVertex();
		buffer.pos(x + width, y + height, 0).tex(minU + (maxU - minU) * width / 16F, minV + (maxV - minV) * height / 16F).endVertex();
		buffer.pos(x + width, y, 0).tex(minU + (maxU - minU) * width / 16F, minV).endVertex();
		buffer.pos(x, y, 0).tex(minU, minV).endVertex();
		Tessellator.getInstance().draw();
	}

}
