package cofh.thermalexpansion.render;

import cofh.core.render.ShaderHelper;
import cofh.lib.render.RenderHelper;
import cofh.repack.codechicken.lib.render.CCRenderState;
import cofh.thermalexpansion.block.ender.TileTesseract;
import cofh.thermalfoundation.render.shader.ShaderStarfield;
import cpw.mods.fml.client.registry.ClientRegistry;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import org.lwjgl.opengl.GL11;

public class RenderTesseractStarfield extends TileEntitySpecialRenderer {

	public static RenderTesseractStarfield instance = new RenderTesseractStarfield();

	public static void register() {

		ClientRegistry.bindTileEntitySpecialRenderer(TileTesseract.class, RenderTesseractStarfield.instance);
	}

	@Override
	public void renderTileEntityAt(TileEntity tile, double x, double y, double z, float f) {

		if (!((TileTesseract) tile).isActive || ShaderStarfield.starfieldShader == 0) {
			return;
		}

		GL11.glPushMatrix();

		CCRenderState.changeTexture(ShaderStarfield.starsTexture);

		GL11.glTranslated(x + 0.5, y + 0.5, z + 0.5);
		GL11.glScaled(1 + RenderHelper.RENDER_OFFSET, 1 + RenderHelper.RENDER_OFFSET, 1 + RenderHelper.RENDER_OFFSET);
		ShaderStarfield.alpha = 0;

		ShaderHelper.useShader(ShaderStarfield.starfieldShader, ShaderStarfield.callback);
		CCRenderState.startDrawing();
		RenderTesseract.instance.renderCenter(0, (TileTesseract) tile, -0.5, -0.5, -0.5);
		CCRenderState.draw();
		ShaderHelper.releaseShader();

		GL11.glPopMatrix();
	}
}
