package cofh.thermalexpansion.render;

import cofh.core.render.RenderUtils;
import cofh.lib.render.RenderHelper;
import cofh.thermalexpansion.block.cache.TileCache;
import cofh.thermalexpansion.item.TEItems;
import cpw.mods.fml.client.registry.ClientRegistry;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.IItemRenderer;

import org.lwjgl.opengl.GL11;

public class RenderCache extends TileEntitySpecialRenderer implements IItemRenderer {

	public static final RenderCache instance = new RenderCache();
	private static ItemStack lock = new ItemStack(Items.apple);

	static {
		ClientRegistry.bindTileEntitySpecialRenderer(TileCache.class, instance);
	}

	public static void initialize() {

		lock = TEItems.lock.copy();
	}

	@Override
	public void renderTileEntityAt(TileEntity entity, double x, double y, double z, float f) {

		TileCache tile = (TileCache) entity;

		if (tile.storedStack == null) {
			return;
		}
		if (tile.locked) {
			GL11.glPushMatrix();
			GL11.glPushMatrix();

			switch (tile.getFacing()) {
			case 0:
				break;
			case 1:
				break;
			case 2:
				GL11.glTranslated(x + 3/16f, y + 3/16f, z - RenderHelper.RENDER_OFFSET * 3);
				break;
			case 3:
				GL11.glTranslated(x + 13/16f, y + 3/16f, z + 1 + RenderHelper.RENDER_OFFSET * 3);
				GL11.glRotated(180, 0, 1, 0);
				break;
			case 4:
				GL11.glTranslated(x - RenderHelper.RENDER_OFFSET * 3, y + 3/16f, z + 13/16f);
				GL11.glRotated(90, 0, 1, 0);
				break;
			case 5:
				GL11.glTranslated(x + 1 + RenderHelper.RENDER_OFFSET * 3, y + 3/16f, z + 3/16f);
				GL11.glRotated(-90, 0, 1, 0);
				break;
			default:
			}
			GL11.glScaled(0.03125 / 4, 0.03125 / 4, -RenderHelper.RENDER_OFFSET);
			GL11.glRotated(180, 0, 0, 1);

			RenderUtils.setupLight(tile, tile.getFacing());
			RenderHelper.enableGUIStandardItemLighting();

			if (!ForgeHooksClient.renderInventoryItem(RenderUtils.renderBlocks, RenderHelper.engine(), lock, true, 0.0F, 0.0F, 0.0F)) {
				RenderUtils.renderItem.renderItemIntoGUI(Minecraft.getMinecraft().fontRenderer, RenderHelper.engine(), lock, 0, 0);
			}
			GL11.glEnable(GL11.GL_ALPHA_TEST);
			GL11.glAlphaFunc(GL11.GL_GREATER, 0.1F);
			GL11.glEnable(GL11.GL_BLEND);
			OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
			GL11.glPopMatrix();
			net.minecraft.client.renderer.RenderHelper.enableStandardItemLighting();
			GL11.glPopMatrix();
		}
		GL11.glPushMatrix();
		RenderUtils.renderItemOnBlockSide(tile, tile.storedStack, tile.getFacing(), x, y, z);
		GL11.glPopMatrix();
	}

	/* IItemRenderer */
	@Override
	public boolean handleRenderType(ItemStack item, ItemRenderType type) {

		return true;
	}

	@Override
	public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {

		return true;
	}

	@Override
	public void renderItem(ItemRenderType type, ItemStack item, Object... data) {

	}

}
