package thermalexpansion.render.entity;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.Entity;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidRegistry;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import thermalexpansion.entity.projectile.EntityFlorb;
import cofh.render.IconRegistry;
import cofh.render.RenderHelper;
import cpw.mods.fml.client.registry.RenderingRegistry;

public class RenderEntityFlorb extends Render {

	public static final RenderEntityFlorb instance = new RenderEntityFlorb();

	static {
		RenderingRegistry.registerEntityRenderingHandler(EntityFlorb.class, instance);
	}

	public static void initialize() {

	}

	@Override
	public void doRender(Entity entity, double d0, double d1, double d2, float f, float f1) {

		doRenderFlorb((EntityFlorb) entity, d0, d1, d2, f, f1);
	}

	@Override
	protected ResourceLocation getEntityTexture(Entity entity) {

		return TextureMap.locationItemsTexture;
	}

	protected void doRenderFlorb(EntityFlorb florb, double d0, double d1, double d2, float f, float f1) {

		if (florb.getFluid() == null) {
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
		this.renderIcon(Tessellator.instance, florb.getFluid().getIcon());

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
			icon = FluidRegistry.WATER.getIcon();
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
		tessellator.addVertexWithUV(0.0F - f5, 0.0F - f6, 0.0D, icon.getMinU(), maxV);
		tessellator.addVertexWithUV(f4 - f5, 0.0F - f6, 0.0D, maxU, maxV);
		tessellator.addVertexWithUV(f4 - f5, f4 - f6, 0.0D, maxU, minV);
		tessellator.addVertexWithUV(0.0F - f5, f4 - f6, 0.0D, icon.getMinU(), minV);
		tessellator.draw();
	}

}
