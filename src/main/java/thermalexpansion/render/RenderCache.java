package thermalexpansion.render;

import cofh.render.RenderUtils;
import cpw.mods.fml.client.registry.ClientRegistry;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.client.IItemRenderer;

import thermalexpansion.block.cache.TileCache;

public class RenderCache extends TileEntitySpecialRenderer implements IItemRenderer {

	public static final RenderCache instance = new RenderCache();

	static {
		ClientRegistry.bindTileEntitySpecialRenderer(TileCache.class, instance);
	}

	public static void initialize() {

	}

	@Override
	public void renderTileEntityAt(TileEntity entity, double x, double y, double z, float f) {

		TileCache tile = (TileCache) entity;

		if (tile.storedStack == null) {
			return;
		}
		RenderUtils.renderItemOnBlockSide(tile, tile.storedStack, tile.facing, x, y, z);
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
