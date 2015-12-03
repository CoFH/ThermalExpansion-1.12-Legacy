package cofh.thermalexpansion.render;

import cofh.core.render.IconRegistry;
import cofh.core.render.RenderUtils;
import cofh.core.render.RenderUtils.ScaledIconTransformation;
import cofh.lib.render.RenderHelper;
import cofh.repack.codechicken.lib.lighting.LightModel;
import cofh.repack.codechicken.lib.render.CCModel;
import cofh.repack.codechicken.lib.render.CCRenderState;
import cofh.repack.codechicken.lib.vec.Translation;
import cofh.repack.codechicken.lib.vec.Vector3;
import cofh.thermalexpansion.block.plate.BlockPlate;
import cofh.thermalexpansion.block.plate.TilePlateBase;
import cofh.thermalexpansion.core.TEProps;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.client.registry.RenderingRegistry;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;

public class RenderPlate implements ISimpleBlockRenderingHandler {

	public static final RenderPlate instance = new RenderPlate();

	static IIcon[] texture_frame = new IIcon[8];
	static IIcon[] texture_fluid = new IIcon[6];
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
		texture_fluid[3] = IconRegistry.getIcon("FluidRedstone");
		texture_fluid[4] = IconRegistry.getIcon("FluidGlowstone");
		texture_fluid[5] = IconRegistry.getIcon("FluidEnder");

		texture_frame[6] = IconRegistry.getIcon("PlateBottom");
		texture_frame[7] = IconRegistry.getIcon("PlateTopO");
		for (int i = 0; i < 6; i++) {
			texture_frame[i] = IconRegistry.getIcon("PlateTop", i);
		}
	}

	private static void generateModels() {

		double d = RenderHelper.RENDER_OFFSET;
		side_model[0] = CCModel.quadModel(48).generateBlock(0, 0, 0, 0, 1, 1. / 16, 1);
		CCModel temp = CCModel.quadModel(24).generateBlock(0, d, d, d, 1 - d, 1. / 16 - d, 1 - d);
		CCModel.generateBackface(temp, 0, side_model[0], 24, 24);
		side_model[0].shrinkUVs(RenderHelper.RENDER_OFFSET);
		CCModel.generateSidedModels(side_model, 0, new Vector3(0.5, 0.5, 0.5));
		for (int i = side_model.length; i-- > 0;) {
			side_model[i].computeNormals().computeLighting(LightModel.standardLightModel);
		}
	}

	public void render(int alignment, int direction, int type, double x, double y, double z) {

		Translation trans = RenderUtils.getRenderVector(x, y, z).translation();
		if (direction < 6) {
			int flip = alignment == 1 ? ((direction >> 1) & 1) ^ 1 : 1;
			// top plates need north/south inverted specially (otherwise flip would always be 1)
			int off = (alignment > 1 & (direction >> 1 == alignment >> 1)) ? 1 : flip ^ 1;
			// if the alignment and direction are the same class and not up/down, invert. apply special case from above
			int s = (alignment & 1) ^ flip;
			// if the alignment needs inversion
			direction ^= s & off;
		}

		CCModel model = side_model[alignment];
		if (type > 0) {
			model.render(4, 8, trans, RenderUtils.getIconTransformation(texture_fluid[type - 1]));
		}
		model.render(4, 8, trans, RenderUtils.getIconTransformation(texture_frame[direction]));
		ScaledIconTransformation transform = RenderUtils.getIconTransformation(texture_frame[6]);
		model.render(0, 4, trans, transform);
		model.render(24, 28, trans, transform);

		for (int i = 8; i < 24; i += 4) {
			model.render(i, i + 4, trans, transform);
			model.render(24 + i, 24 + i + 4, trans, transform);
		}
	}

	/* ISimpleBlockRenderingHandler */
	@Override
	public void renderInventoryBlock(Block block, int metadata, int modelID, RenderBlocks renderer) {

		RenderUtils.preItemRender();

		CCRenderState.startDrawing();
		render(0, BlockPlate.Types.values()[metadata].texture, metadata, 0, 0, 0);
		CCRenderState.draw();

		RenderUtils.postItemRender();
	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {

		TileEntity tile = world.getTileEntity(x, y, z);
		if (!(tile instanceof TilePlateBase)) {
			return false;
		}
		TilePlateBase theTile = (TilePlateBase) tile;

		RenderUtils.preWorldRender(world, x, y, z);
		render(theTile.getAlignment(), theTile.getFacing(), theTile.getType(), x, y, z);
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
