package thermalexpansion.render;

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

import thermalexpansion.block.dynamo.BlockDynamo;
import thermalexpansion.block.dynamo.TileDynamoBase;
import thermalexpansion.core.TEProps;

public class RenderDynamo implements ISimpleBlockRenderingHandler {

	public static final RenderDynamo instance = new RenderDynamo();

	static IIcon textureCoil;
	static IIcon[] textureBase = new IIcon[BlockDynamo.Types.values().length];
	static CCModel[][] modelCoil = new CCModel[2][6];
	static CCModel[][] modelBase = new CCModel[2][6];
	static CCModel[] modelAnimation = new CCModel[6];

	static {
		TEProps.renderIdDynamo = RenderingRegistry.getNextAvailableRenderId();
		RenderingRegistry.registerBlockHandler(instance);

		generateModels();
	}

	public static void initialize() {

		textureCoil = IconRegistry.getIcon("DynamoCoilRedstone");

		for (int i = 0; i < textureBase.length; i++) {
			textureBase[i] = IconRegistry.getIcon("Dynamo", i);
		}
	}

	private static void generateModels() {

		double d1 = 2 * RenderHelper.RENDER_OFFSET;
		double d2 = 6F / 16F;
		double d3 = 10F / 16F;

		modelCoil[0][1] = CCModel.quadModel(24).generateBox(0, -4, 0, -4, 8, 8, 8, 0, 0, 32, 32, 16).computeNormals();
		modelCoil[1][1] = CCModel.quadModel(24).generateBox(0, -4, 0, -4, 8, 8, 8, 0, 16, 32, 32, 16).computeNormals();

		modelBase[0][1] = CCModel.quadModel(24).generateBox(0, -8, -8, -8, 16, 10, 16, 0, 0, 64, 64, 16).computeNormals();
		modelBase[1][1] = CCModel.quadModel(24).generateBox(0, -8, -8, -8, 16, 10, 16, 0, 32, 64, 64, 16).computeNormals();

		modelAnimation[0] = CCModel.quadModel(16).generateBlock(0, d1, d2 - d1, d1, 1 - d1, 1 - d1, 1 - d1, 3).computeNormals();
		modelAnimation[1] = CCModel.quadModel(16).generateBlock(0, d1, d1, d1, 1 - d1, d3 - d1, 1 - d1, 3).computeNormals();

		modelAnimation[2] = CCModel.quadModel(16).generateBlock(0, d1, d1, d2 - d1, 1 - d1, 1 - d1, 1 - d1, 12).computeNormals();
		modelAnimation[3] = CCModel.quadModel(16).generateBlock(0, d1, d1, d1, 1 - d1, 1 - d1, d3 - d1, 12).computeNormals();

		modelAnimation[4] = CCModel.quadModel(16).generateBlock(0, d2 - d1, d1, d1, 1 - d1, 1 - d1, 1 - d1, 48).computeNormals();
		modelAnimation[5] = CCModel.quadModel(16).generateBlock(0, d1, d1, d1, d3 - d1, 1 - d1, 1 - d1, 48).computeNormals();

		for (int i = 0; i < modelCoil.length; i++) {
			CCModel.generateSidedModels(modelCoil[i], 1, new Vector3());
		}
		for (int i = 0; i < modelBase.length; i++) {
			CCModel.generateSidedModels(modelBase[i], 1, new Vector3());
		}
	}

	public void renderCoil(int facing, boolean active, double x, double y, double z) {

		x += 0.5;
		y += 0.5;
		z += 0.5;

		Translation trans = RenderUtils.getRenderVector(x, y, z).translation();

		if (active) {
			modelCoil[0][facing].render(trans, RenderUtils.getIconTransformation(textureCoil));
		} else {
			modelCoil[1][facing].render(trans, RenderUtils.getIconTransformation(textureCoil));
		}
	}

	public void renderBase(int facing, boolean active, int type, double x, double y, double z) {

		x += 0.5;
		y += 0.5;
		z += 0.5;

		Translation trans = RenderUtils.getRenderVector(x, y, z).translation();

		if (active) {
			modelBase[0][facing].render(trans, RenderUtils.getIconTransformation(textureBase[type]));
		} else {
			modelBase[1][facing].render(trans, RenderUtils.getIconTransformation(textureBase[type]));
		}
	}

	public void renderAnimation(int facing, boolean active, int type, IIcon icon, double x, double y, double z) {

		if (active) {
			modelAnimation[facing].render(x, y, z, RenderUtils.getIconTransformation(icon));
		}
	}

	@Override
	public void renderInventoryBlock(Block block, int metadata, int modelID, RenderBlocks renderer) {

		RenderUtils.preRender();

		CCRenderState.startDrawing();
		renderCoil(1, false, -0.5, -0.5, -0.5);
		renderBase(1, false, metadata, -0.5, -0.5, -0.5);
		CCRenderState.draw();

		CCRenderState.useNormals = false;
	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {

		TileEntity tile = world.getTileEntity(x, y, z);
		if (!(tile instanceof TileDynamoBase)) {
			return false;
		}
		TileDynamoBase theTile = (TileDynamoBase) tile;

		RenderUtils.beforeWorldRender(world, x, y, z);
		renderCoil(theTile.getFacing(), theTile.isActive(), x, y, z);
		renderAnimation(theTile.getFacing(), theTile.isActive(), theTile.getType(), theTile.getActiveIcon(), x, y, z);
		renderBase(theTile.getFacing(), theTile.isActive(), theTile.getType(), x, y, z);
		RenderUtils.afterWorldRender(world, x, y, z);

		return true;
	}

	@Override
	public boolean shouldRender3DInInventory(int modelId) {

		return true;
	}

	@Override
	public int getRenderId() {

		return TEProps.renderIdDynamo;
	}

}
