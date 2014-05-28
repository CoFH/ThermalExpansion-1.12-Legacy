package thermalexpansion.render;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.client.IItemRenderer;

public class RenderCache extends TileEntitySpecialRenderer implements IItemRenderer {

	public static final RenderCache instance = new RenderCache();

	static {

	}

	public static void initialize() {

	}

	@Override
	public void renderTileEntityAt(TileEntity entity, double x, double y, double z, float f) {

		// TODO Auto-generated method stub

	}

	/* IItemRenderer */
	@Override
	public boolean handleRenderType(ItemStack item, ItemRenderType type) {

		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {

		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void renderItem(ItemRenderType type, ItemStack item, Object... data) {

		// TODO Auto-generated method stub

	}

}
