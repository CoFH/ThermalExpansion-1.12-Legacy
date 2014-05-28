package thermalexpansion.render;

import cofh.render.IconRegistry;
import cofh.render.RenderHelper;
import cofh.render.RenderUtils;

import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

import org.lwjgl.opengl.GL11;

public class RenderItemFlorb implements IItemRenderer {

	@Override
	public boolean handleRenderType(ItemStack item, ItemRenderType type) {

		return true;
	}

	@Override
	public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {

		return type.equals(ItemRenderType.ENTITY);
	}

	@Override
	public void renderItem(ItemRenderType type, ItemStack item, Object... data) {

		GL11.glPushMatrix();
		RenderUtils.preItemRender();
		if (type.equals(ItemRenderType.ENTITY)) {
			GL11.glRotated(180, 0, 0, 1);
			GL11.glRotated(90, 0, 1, 0);
			GL11.glScaled(0.75, 0.75, 0.75);
			GL11.glTranslated(-0.5, -0.6, 0);
		} else if (type.equals(ItemRenderType.EQUIPPED_FIRST_PERSON)) {
			GL11.glTranslated(1, 1, 0);
			GL11.glRotated(180, 0, 0, 1);
		} else if (type.equals(ItemRenderType.EQUIPPED)) {
			GL11.glRotated(180, 0, 0, 1);
			GL11.glTranslated(-1, -1, 0);
		}
		if (item.stackTagCompound != null) {
			Fluid fluid = FluidRegistry.getFluid(item.stackTagCompound.getString("Fluid"));

			if (fluid != null) {
				RenderHelper.setItemTextureSheet();
				RenderUtils.renderMask(IconRegistry.getIcon("FlorbMask"), fluid.getIcon(), null, type);
			}
		}
		RenderHelper.setItemTextureSheet();

		if (!type.equals(ItemRenderType.INVENTORY)) {
			if (item.getItemDamage() == 1) {
				RenderHelper.renderItemIn2D(IconRegistry.getIcon("FlorbMagmatic"));
			} else {
				RenderHelper.renderItemIn2D(IconRegistry.getIcon("Florb"));
			}
		} else {
			if (item.getItemDamage() == 1) {
				RenderHelper.renderIcon(IconRegistry.getIcon("FlorbMagmatic"), 0);
			} else {
				RenderHelper.renderIcon(IconRegistry.getIcon("Florb"), 0);
			}
		}
		RenderUtils.postItemRender();
		GL11.glPopMatrix();
	}

}
