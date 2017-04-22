package cofh.thermalexpansion.render;

import cofh.thermalexpansion.block.storage.TileStrongbox;
import cofh.thermalexpansion.render.model.ModelStrongbox;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraftforge.fml.client.registry.ClientRegistry;

public class RenderStrongbox extends TileEntitySpecialRenderer<TileStrongbox> {

	public static final RenderStrongbox INSTANCE = new RenderStrongbox();
	private static ModelStrongbox model = new ModelStrongbox();

	static {
		ClientRegistry.bindTileEntitySpecialRenderer(TileStrongbox.class, INSTANCE);
	}

	@Override
	public void renderTileEntityAt(TileStrongbox tile, double x, double y, double z, float f, int destroyStage) {

		model.boxLid.rotateAngleX = (float) tile.getRadianLidAngle(f);


	}

}
