package cofh.thermalexpansion.render;

import cofh.core.render.RenderUtils;
import cofh.core.util.helpers.RenderHelper;
import cofh.thermalexpansion.block.storage.TileCache;
import cofh.thermalfoundation.item.ItemSecurity;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import org.lwjgl.opengl.GL11;

public class RenderCache extends TileEntitySpecialRenderer<TileCache> {

	public static final RenderCache INSTANCE = new RenderCache();
	private static ItemStack lock = new ItemStack(Items.DIAMOND);

	public static void initialize() {

		lock = ItemSecurity.lock.copy();
	}

	@Override
	public void render(TileCache tile, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {

		if (tile.getStoredInstance().isEmpty()) {
			return;
		}
		if (tile.isLocked()) {
			GlStateManager.pushMatrix();
			GlStateManager.pushMatrix();

			switch (tile.getFacing()) {
				case 2:
					GlStateManager.translate(x + 3 / 16f, y + 3 / 16f, z + RenderHelper.RENDER_OFFSET * 150);
					break;
				case 3:
					GlStateManager.translate(x + 13 / 16f, y + 3 / 16f, z + 1 - RenderHelper.RENDER_OFFSET * 150);
					GlStateManager.rotate(180, 0, 1, 0);
					break;
				case 4:
					GlStateManager.translate(x + RenderHelper.RENDER_OFFSET * 150, y + 3 / 16f, z + 13 / 16f);
					GlStateManager.rotate(90, 0, 1, 0);
					break;
				case 5:
					GlStateManager.translate(x + 1 - RenderHelper.RENDER_OFFSET * 150, y + 3 / 16f, z + 3 / 16f);
					GlStateManager.rotate(-90, 0, 1, 0);
					break;
				default:
			}
			GlStateManager.scale(0.03125 / 4, 0.03125 / 4, -RenderHelper.RENDER_OFFSET);
			GlStateManager.rotate(180, 0, 0, 1);

			RenderUtils.setupLight(tile, EnumFacing.VALUES[tile.getFacing()]);
			RenderHelper.enableGUIStandardItemLighting();

			RenderHelper.renderItem().renderItemAndEffectIntoGUI(lock, 0, 0);

			GlStateManager.enableAlpha();
			GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1F);
			GlStateManager.enableBlend();
			GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
			GlStateManager.popMatrix();
			RenderHelper.enableStandardItemLighting();
			GlStateManager.popMatrix();
		}
		GlStateManager.pushMatrix();
		RenderUtils.renderItemOnBlockSide(tile, tile.getStoredInstance(), tile.getFacing(), x, y, z);
		GlStateManager.popMatrix();
	}

}
