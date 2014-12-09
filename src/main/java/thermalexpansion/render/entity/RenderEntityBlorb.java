package thermalexpansion.render.entity;

import cofh.core.render.IconRegistry;
import cofh.lib.render.RenderHelper;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import thermalexpansion.entity.projectile.EntityFlorb;

public class RenderEntityBlorb extends Render {

	public static final RenderEntityBlorb instance = new RenderEntityBlorb();

	static {
		// RenderingRegistry.registerEntityRenderingHandler(EntityBlorb.class, instance);
	}

	public static void initialize() {

	}

	@Override
	public void doRender(Entity entity, double d0, double d1, double d2, float f, float f1) {

		doRenderBlorb((EntityFlorb) entity, d0, d1, d2, f, f1);
	}

	@Override
	protected ResourceLocation getEntityTexture(Entity entity) {

		return TextureMap.locationItemsTexture;
	}

	protected void doRenderBlorb(EntityFlorb blorb, double d0, double d1, double d2, float f, float f1) {

		if (blorb.getFluid() == null) {
			return;
		}
		GL11.glPushMatrix();
		GL11.glTranslated(d0, d1, d2);
		GL11.glEnable(GL12.GL_RESCALE_NORMAL);
		GL11.glScalef(0.5F, 0.5F, 0.5F);

		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glDisable(GL11.GL_CULL_FACE);

		GL11.glRotatef(180.0F - this.renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
		GL11.glRotatef(-this.renderManager.playerViewX, 1.0F, 0.0F, 0.0F);

		RenderHelper.setItemTextureSheet();
		this.renderIcon(Tessellator.instance, IconRegistry.getIcon("FlorbMask"));

		GL11.glDepthFunc(GL11.GL_EQUAL);
		GL11.glDepthMask(false);

		RenderHelper.setBlockTextureSheet();
		this.renderIcon(Tessellator.instance, blorb.getFluid().getIcon());

		GL11.glDepthMask(true);
		GL11.glDepthFunc(GL11.GL_LEQUAL);

		RenderHelper.setItemTextureSheet();
		this.renderIcon(Tessellator.instance, IconRegistry.getIcon("FlorbOutline"));

		GL11.glDisable(GL11.GL_BLEND);
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glColor4f(1, 1, 1, 1);

		GL11.glDisable(GL12.GL_RESCALE_NORMAL);
		GL11.glPopMatrix();
	}

	private void renderIcon(Tessellator tessellator, IIcon icon) {

		if (icon == null) {
			icon = Blocks.stone.getIcon(3, 0);
		}
		float minU = icon.getMinU();
		float maxU = icon.getMaxU();
		float minV = icon.getMinV();
		float maxV = icon.getMaxV();
		float f4 = 1.0F;
		float f5 = 0.5F;
		float f6 = 0.25F;
		tessellator.startDrawingQuads();
		tessellator.setNormal(0.0F, 1.0F, 0.0F);
		tessellator.addVertexWithUV(0f - f5, 0f - f6, 0.0D, minU, maxV);
		tessellator.addVertexWithUV(f4 - f5, 0f - f6, 0.0D, maxU, maxV);
		tessellator.addVertexWithUV(f4 - f5, f4 - f6, 0.0D, maxU, minV);
		tessellator.addVertexWithUV(0f - f5, f4 - f6, 0.0D, minU, minV);
		tessellator.draw();
	}

}
