package cofh.thermalexpansion.render.entity;

import cofh.thermalexpansion.entity.projectile.EntityMorb;
import cofh.thermalexpansion.init.TEItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class RenderEntityMorb extends Render<EntityMorb> {

	public RenderEntityMorb(RenderManager renderManager) {

		super(renderManager);
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityMorb entity) {

		return TextureMap.LOCATION_BLOCKS_TEXTURE;
	}

	@Override
	public void doRender(EntityMorb morb, double d0, double d1, double d2, float f, float f1) {

		GlStateManager.pushMatrix();
		GlStateManager.translate(d0, d1, d2);
		GlStateManager.enableRescaleNormal();
		GlStateManager.scale(0.5F, 0.5F, 0.5F);

		GlStateManager.rotate(180.0F - this.renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
		GlStateManager.rotate(-this.renderManager.playerViewX, 1.0F, 0.0F, 0.0F);

		ItemStack stack = new ItemStack(TEItems.itemMorb);
		stack.setTagCompound(morb.getEntity());
		Minecraft.getMinecraft().getRenderItem().renderItem(stack, ItemCameraTransforms.TransformType.GROUND);

		GlStateManager.disableRescaleNormal();
		GlStateManager.popMatrix();
	}
}
