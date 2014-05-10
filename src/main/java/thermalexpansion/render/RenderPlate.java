package thermalexpansion.render;

import codechicken.lib.lighting.LightModel;
import codechicken.lib.render.CCModel;
import codechicken.lib.render.CCRenderState;
import codechicken.lib.vec.Translation;
import codechicken.lib.vec.Vector3;
import cofh.render.IconRegistry;
import cofh.render.RenderHelper;
import cofh.render.RenderUtils;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.client.registry.RenderingRegistry;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;

import thermalexpansion.core.TEProps;

public class RenderPlate implements ISimpleBlockRenderingHandler {

	public static final RenderPlate instance = new RenderPlate();

	static IIcon[] texture_frame = new IIcon[7];
	static IIcon[] texture_fluid = new IIcon[3];
	static CCModel[] side_model = new CCModel[6];

	static {
		TEProps.renderIdPlate = RenderingRegistry.getNextAvailableRenderId();
		RenderingRegistry.registerBlockHandler(instance);

		generateModels();
	}

	public static void initialize() {

		texture_fluid[0] = IconRegistry.getIcon("FluidRedstone");
		texture_fluid[1] = IconRegistry.getIcon("FluidGlowstone");
		texture_fluid[2] = IconRegistry.getIcon("FluidEnder");

		texture_frame[6] = IconRegistry.getIcon("PlateBottom");
		for (int i = 0; i < 6; i++) {
			texture_frame[i] = IconRegistry.getIcon("PlateTop", i);
		}
	}

	private static void generateModels() {

		double d = RenderHelper.RENDER_OFFSET;
		side_model[0] = CCModel.quadModel(24).generateBlock(0, d, d, d, 1 - d, 0.0625 - d, 1 - d).computeNormals().computeLighting(LightModel.standardLightModel);
		CCModel.generateSidedModels(side_model, 0, Vector3.zero);
	}

	public void render(int alignment, int direction, int type, double x, double y, double z) {

		Translation trans = RenderUtils.getRenderVector(x, y, z).translation();
		side_model[alignment].render(4, 4, trans, RenderUtils.getIconTransformation(texture_fluid[type]), null);
		side_model[alignment].render(4, 4, trans, RenderUtils.getIconTransformation(texture_frame[direction]), null);
		side_model[alignment].render(0, 4, trans, RenderUtils.getIconTransformation(texture_frame[6]), null);

		for (int i = 8; i < 24; i += 4) {
			side_model[alignment].render(i, 4, trans, RenderUtils.getIconTransformation(texture_frame[6]), null);
		}
	}

	/* ISimpleBlockRenderingHandler */
	@Override
	public void renderInventoryBlock(Block block, int metadata, int modelID, RenderBlocks renderer) {

		RenderUtils.preRender();
		CCRenderState.startDrawing();
		render(0, 2, metadata % 3, 0, 0, 0);
		CCRenderState.draw();
		CCRenderState.useNormals = false;
	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {

		TileEntity aTile = world.getTileEntity(x, y, z);
		if (!(aTile instanceof TilePlateBase)) {
			return false;
		}
		TilePlateBase theTile = (TilePlateBase) aTile;

		RenderUtils.beforeWorldRender(world, x, y, z);
		render(theTile.getAlignment(), theTile.getFacing(), theTile.getType(), x, y, z);
		RenderUtils.afterWorldRender(world, x, y, z);

		return true;
	}

	@Override
	public boolean shouldRender3DInInventory(int modelId) {

		return true;
	}

	@Override
	public int getRenderId() {

		return TEProps.renderIdPlate;
	}

}
