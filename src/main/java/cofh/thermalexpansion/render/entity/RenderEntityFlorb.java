package cofh.thermalexpansion.render.entity;

import cofh.thermalexpansion.entity.projectile.EntityFlorb;
import cofh.thermalexpansion.init.TEFlorbs;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;

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
		Fluid fluid = florb.getFluid();
		GlStateManager.pushMatrix();
		GlStateManager.translate(d0, d1, d2);
		GlStateManager.enableRescaleNormal();
		GlStateManager.scale(0.5F, 0.5F, 0.5F);

		GlStateManager.rotate(180.0F - this.renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
		GlStateManager.rotate(-this.renderManager.playerViewX, 1.0F, 0.0F, 0.0F);

		//K, So the entity has been thrown, Fluid isn't null, This HAS to be a florb. Ignore null return.
		Minecraft.getMinecraft().getRenderItem().renderItem(TEFlorbs.getFlorb(fluid), TransformType.GROUND);

		GlStateManager.disableRescaleNormal();
		GlStateManager.popMatrix();
	}
}
