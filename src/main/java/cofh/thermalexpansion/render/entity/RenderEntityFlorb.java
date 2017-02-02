package cofh.thermalexpansion.render.entity;

import codechicken.lib.texture.TextureUtils;
import cofh.core.render.IconRegistry;
import cofh.thermalexpansion.entity.projectile.EntityFlorb;
import cofh.thermalexpansion.render.item.ModelFlorb;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidRegistry;
import org.lwjgl.opengl.GL11;

public class RenderEntityFlorb extends Render<EntityFlorb> {

	public RenderEntityFlorb(RenderManager renderManager) {

		super(renderManager);
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityFlorb entity) {

		return TextureMap.LOCATION_BLOCKS_TEXTURE;
	}

	@Override
	public void doRender(EntityFlorb florb, double d0, double d1, double d2, float f, float f1) {

		if (florb.getFluid() == null) {
			return;
		}
		GlStateManager.pushMatrix();
		GlStateManager.translate(d0, d1, d2);
		GlStateManager.enableRescaleNormal();
		GlStateManager.scale(0.5F, 0.5F, 0.5F);

		GlStateManager.enableBlend();
		GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GlStateManager.disableCull();

		GlStateManager.rotate(180.0F - this.renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
		GlStateManager.rotate(-this.renderManager.playerViewX, 1.0F, 0.0F, 0.0F);

		renderIcon(Tessellator.getInstance(), TextureUtils.getTexture(ModelFlorb.MASK));

		GlStateManager.depthFunc(GL11.GL_EQUAL);
		GlStateManager.depthMask(false);

		renderIcon(Tessellator.getInstance(), TextureUtils.getTexture(florb.getFluid().getStill()));

		GlStateManager.depthMask(true);
		GlStateManager.depthFunc(GL11.GL_LEQUAL);

		renderIcon(Tessellator.getInstance(), TextureUtils.getTexture(ModelFlorb.OUTLINE));

		GlStateManager.disableBlend();
		GlStateManager.enableCull();
		GlStateManager.color(1, 1, 1, 1);

		GlStateManager.disableRescaleNormal();
		GlStateManager.popMatrix();
	}

	private void renderIcon(Tessellator tessellator, TextureAtlasSprite icon) {

		if (icon == null) {
			icon = TextureUtils.getTexture(FluidRegistry.WATER.getStill());
		}
		float minU = icon.getMinU();
		float maxU = icon.getMaxU();
		float minV = icon.getMinV();
		float maxV = icon.getMaxV();
		float f4 = 1.0F;
		float f5 = 0.5F;
		float f6 = 0.25F;
		VertexBuffer buffer = tessellator.getBuffer();
		buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_NORMAL);
		buffer.pos(0f - f5, 0f - f6, 0.0D).tex(minU, maxV).normal(0.0F, 1.0F, 0.0F).endVertex();
		buffer.pos(f4 - f5, 0f - f6, 0.0D).tex(maxU, maxV).normal(0.0F, 1.0F, 0.0F).endVertex();
		buffer.pos(f4 - f5, f4 - f6, 0.0D).tex(maxU, minV).normal(0.0F, 1.0F, 0.0F).endVertex();
		buffer.pos(0f - f5, f4 - f6, 0.0D).tex(minU, minV).normal(0.0F, 1.0F, 0.0F).endVertex();
		tessellator.draw();
	}

}
