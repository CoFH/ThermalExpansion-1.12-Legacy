package cofh.thermalexpansion.plugins.jei;

import cofh.core.util.helpers.RenderHelper;
import cofh.thermalexpansion.block.storage.BlockCache;
import cofh.thermalexpansion.block.storage.BlockCell;
import cofh.thermalexpansion.block.storage.BlockStrongbox;
import cofh.thermalexpansion.block.storage.BlockTank;
import cofh.thermalexpansion.plugins.jei.device.coolant.CoolantCategory;
import cofh.thermalexpansion.plugins.jei.dynamo.compression.CompressionFuelCategory;
import cofh.thermalexpansion.plugins.jei.dynamo.enervation.EnervationFuelCategory;
import cofh.thermalexpansion.plugins.jei.dynamo.magmatic.MagmaticFuelCategory;
import cofh.thermalexpansion.plugins.jei.dynamo.numismatic.NumismaticFuelCategory;
import cofh.thermalexpansion.plugins.jei.dynamo.reactant.ReactantFuelCategory;
import cofh.thermalexpansion.plugins.jei.dynamo.steam.SteamFuelCategory;
import cofh.thermalexpansion.plugins.jei.machine.brewer.BrewerRecipeCategory;
import cofh.thermalexpansion.plugins.jei.machine.centrifuge.CentrifugeRecipeCategory;
import cofh.thermalexpansion.plugins.jei.machine.charger.ChargerRecipeCategory;
import cofh.thermalexpansion.plugins.jei.machine.compactor.CompactorRecipeCategory;
import cofh.thermalexpansion.plugins.jei.machine.crucible.CrucibleRecipeCategory;
import cofh.thermalexpansion.plugins.jei.machine.enchanter.EnchanterRecipeCategory;
import cofh.thermalexpansion.plugins.jei.machine.extruder.ExtruderRecipeCategory;
import cofh.thermalexpansion.plugins.jei.machine.furnace.FurnaceRecipeCategory;
import cofh.thermalexpansion.plugins.jei.machine.insolator.InsolatorRecipeCategory;
import cofh.thermalexpansion.plugins.jei.machine.precipitator.PrecipitatorRecipeCategory;
import cofh.thermalexpansion.plugins.jei.machine.pulverizer.PulverizerRecipeCategory;
import cofh.thermalexpansion.plugins.jei.machine.refinery.RefineryRecipeCategory;
import cofh.thermalexpansion.plugins.jei.machine.sawmill.SawmillRecipeCategory;
import cofh.thermalexpansion.plugins.jei.machine.smelter.SmelterRecipeCategory;
import cofh.thermalexpansion.plugins.jei.machine.transposer.TransposerRecipeCategory;
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

	public static IJeiHelpers jeiHelpers;
	public static IGuiHelper guiHelper;
	public static IJeiRuntime jeiRuntime;

	@Override
	public void registerItemSubtypes(ISubtypeRegistry subtypeRegistry) {

		subtypeRegistry.useNbtForSubtypes(BlockCell.itemBlock);
		subtypeRegistry.useNbtForSubtypes(BlockTank.itemBlock);
		subtypeRegistry.useNbtForSubtypes(BlockCache.itemBlock);
		subtypeRegistry.useNbtForSubtypes(BlockStrongbox.itemBlock);
	}

	@Override
	public void registerCategories(IRecipeCategoryRegistration registry) {

		jeiHelpers = registry.getJeiHelpers();
		guiHelper = jeiHelpers.getGuiHelper();

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
		BrewerRecipeCategory.register(registry);
		EnchanterRecipeCategory.register(registry);
		PrecipitatorRecipeCategory.register(registry);
		ExtruderRecipeCategory.register(registry);

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

		jeiHelpers = registry.getJeiHelpers();
		guiHelper = jeiHelpers.getGuiHelper();

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
		BrewerRecipeCategory.initialize(registry);
		EnchanterRecipeCategory.initialize(registry);
		PrecipitatorRecipeCategory.initialize(registry);
		ExtruderRecipeCategory.initialize(registry);

		SteamFuelCategory.initialize(registry);
		MagmaticFuelCategory.initialize(registry);
		CompressionFuelCategory.initialize(registry);
		ReactantFuelCategory.initialize(registry);
		EnervationFuelCategory.initialize(registry);
		NumismaticFuelCategory.initialize(registry);

		CoolantCategory.initialize(registry);

		Descriptions.register(registry);
	}

	@Override
	public void onRuntimeAvailable(IJeiRuntime jeiRuntime) {

		JEIPluginTE.jeiRuntime = jeiRuntime;
	}

	public static void refresh() {

		EnchanterRecipeCategory.refresh();
	}

	/* HELPERS */
	public static void drawFluid(int x, int y, FluidStack fluid, int width, int height) {

		if (fluid == null) {
			return;
		}
		GL11.glPushMatrix();
		GlStateManager.enableBlend();
		GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

		RenderHelper.setBlockTextureSheet();
		int color = fluid.getFluid().getColor(fluid);
		RenderHelper.setGLColorFromInt(color);
		drawTiledTexture(x, y, RenderHelper.getTexture(fluid.getFluid().getStill(fluid)), width, height);
		GL11.glPopMatrix();
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
