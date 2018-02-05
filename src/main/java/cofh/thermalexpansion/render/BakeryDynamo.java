package cofh.thermalexpansion.render;

import codechicken.lib.model.bakery.ModelErrorStateProperty;
import codechicken.lib.model.bakery.ModelErrorStateProperty.ErrorState;
import codechicken.lib.model.bakery.generation.ILayeredBlockBakery;
import codechicken.lib.render.CCModel;
import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.buffer.BakingVertexBuffer;
import codechicken.lib.vec.Translation;
import codechicken.lib.vec.Vector3;
import codechicken.lib.vec.uv.IconTransformation;
import cofh.core.util.helpers.RenderHelper;
import cofh.thermalexpansion.block.dynamo.BlockDynamo;
import cofh.thermalexpansion.block.dynamo.TileDynamoBase;
import cofh.thermalexpansion.init.TEProps;
import cofh.thermalexpansion.init.TETextures;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.property.IExtendedBlockState;

import java.util.ArrayList;
import java.util.List;

public class BakeryDynamo implements ILayeredBlockBakery {

	public static final BakeryDynamo INSTANCE = new BakeryDynamo();

	private static CCModel[][] modelCoil = new CCModel[2][6];
	private static CCModel[][] modelBase = new CCModel[2][6];
	private static CCModel[][] modelBaseOverlay = new CCModel[2][6];

	private static CCModel[] modelCoilAnimation = new CCModel[6];
	private static CCModel[] modelBaseAnimation = new CCModel[6];

	static {
		generateModels();
	}

	private static void generateModels() {

		double d1 = RenderHelper.RENDER_OFFSET;
		double d2 = 6F / 16F;
		double d3 = 10F / 16F;

		double d4 = 4F / 16F;
		double d5 = 12F / 16F;

		modelCoil[0][1] = CCModel.quadModel(48).generateBox(0, -4, 0, -4, 8, 8 - d1, 8, 0, 0, 32, 32, 16);
		modelCoil[1][1] = CCModel.quadModel(48).generateBox(0, -4, 0, -4, 8, 8 - d1, 8, 0, 16, 32, 32, 16);

		CCModel.generateBackface(modelCoil[0][1], 0, modelCoil[0][1], 24, 24);
		CCModel.generateBackface(modelCoil[1][1], 0, modelCoil[1][1], 24, 24);

		modelCoil[0][1].computeNormals().shrinkUVs(d1);
		modelCoil[1][1].computeNormals().shrinkUVs(d1);

		modelBase[0][1] = CCModel.quadModel(24).generateBox(0, -8, -8, -8, 16, 10, 16, 0, 0, 64, 64, 16).computeNormals().shrinkUVs(d1);
		modelBase[1][1] = CCModel.quadModel(24).generateBox(0, -8, -8, -8, 16, 10, 16, 0, 32, 64, 64, 16).computeNormals().shrinkUVs(d1);

		modelBaseOverlay[0][1] = CCModel.quadModel(24).generateBox(0, -8 - d1, -8 - d1, -8 - d1, 16 + 2 * d1, 10 + 2 * d1, 16 + 2 * d1, 0, 0, 64, 64, 16).computeNormals().shrinkUVs(d1);
		modelBaseOverlay[1][1] = CCModel.quadModel(24).generateBox(0, -8 - d1, -8 - d1, -8 - d1, 16 + 2 * d1, 10 + 2 * d1, 16 + 2 * d1, 0, 32, 64, 64, 16).computeNormals().shrinkUVs(d1);

		modelCoilAnimation[0] = CCModel.quadModel(16).generateBlock(0, d4 + d1, d1, d4 + d1, d5 - d1, 1 - d1, d5 - d1, 3).computeNormals();
		modelCoilAnimation[1] = CCModel.quadModel(16).generateBlock(0, d4 + d1, d1, d4 + d1, d5 - d1, 1 - d1, d5 - d1, 3).computeNormals();

		modelCoilAnimation[2] = CCModel.quadModel(16).generateBlock(0, d4 + d1, d4 + d1, d1, d5 - d1, d5 - d1, 1 - d1, 12).computeNormals();
		modelCoilAnimation[3] = CCModel.quadModel(16).generateBlock(0, d4 + d1, d4 + d1, d1, d5 - d1, d5 - d1, 1 - d1, 12).computeNormals();

		modelCoilAnimation[4] = CCModel.quadModel(16).generateBlock(0, d1, d4 + d1, d4 + d1, 1 - d1, d5 - d1, d5 - d1, 48).computeNormals();
		modelCoilAnimation[5] = CCModel.quadModel(16).generateBlock(0, d1, d4 + d1, d4 + d1, 1 - d1, d5 - d1, d5 - d1, 48).computeNormals();

		modelBaseAnimation[0] = CCModel.quadModel(16).generateBlock(0, d1, d2 + d1, d1, 1 - d1, 1 - d1, 1 - d1, 3).computeNormals();
		modelBaseAnimation[1] = CCModel.quadModel(16).generateBlock(0, d1, d1, d1, 1 - d1, d3 - d1, 1 - d1, 3).computeNormals();

		modelBaseAnimation[2] = CCModel.quadModel(16).generateBlock(0, d1, d1, d2 + d1, 1 - d1, 1 - d1, 1 - d1, 12).computeNormals();
		modelBaseAnimation[3] = CCModel.quadModel(16).generateBlock(0, d1, d1, d1, 1 - d1, 1 - d1, d3 - d1, 12).computeNormals();

		modelBaseAnimation[4] = CCModel.quadModel(16).generateBlock(0, d2 + d1, d1, d1, 1 - d1, 1 - d1, 1 - d1, 48).computeNormals();
		modelBaseAnimation[5] = CCModel.quadModel(16).generateBlock(0, d1, d1, d1, d3 - d1, 1 - d1, 1 - d1, 48).computeNormals();

		for (CCModel[] model : modelCoil) {
			CCModel.generateSidedModels(model, 1, new Vector3());
		}
		for (CCModel[] model : modelBase) {
			CCModel.generateSidedModels(model, 1, new Vector3());
		}
		for (CCModel[] model : modelBaseOverlay) {
			CCModel.generateSidedModels(model, 1, new Vector3());
		}
	}

	/* RENDER */
	protected void renderCoil(CCRenderState ccrs, int facing, boolean active, int type) {

		if (active) {
			modelCoil[0][facing].render(ccrs, new Translation(0.5, 0.5, 0.5), new IconTransformation(TETextures.DYNAMO_COIL[type]));
		} else {
			modelCoil[1][facing].render(ccrs, new Translation(0.5, 0.5, 0.5), new IconTransformation(TETextures.DYNAMO_COIL[type]));
		}
	}

	protected void renderBase(CCRenderState ccrs, int facing, boolean active, int type) {

		if (active) {
			modelBase[0][facing].render(ccrs, new Translation(0.5, 0.5, 0.5), new IconTransformation(TETextures.DYNAMO[type]));
		} else {
			modelBase[1][facing].render(ccrs, new Translation(0.5, 0.5, 0.5), new IconTransformation(TETextures.DYNAMO[type]));
		}
	}

	protected void renderBaseOverlay(CCRenderState ccrs, int facing, boolean active, TextureAtlasSprite sprite) {

		if (sprite != null) {
			if (active) {
				modelBaseOverlay[0][facing].render(ccrs, new Translation(0.5, 0.5, 0.5), new IconTransformation(sprite));
			} else {
				modelBaseOverlay[1][facing].render(ccrs, new Translation(0.5, 0.5, 0.5), new IconTransformation(sprite));
			}
		}
	}

	protected void renderCoilAnimation(CCRenderState ccrs, int facing, boolean active, TextureAtlasSprite icon) {

		if (active) {
			modelCoilAnimation[facing].render(ccrs, new IconTransformation(icon));
		}
	}

	protected void renderBaseAnimation(CCRenderState ccrs, int facing, boolean active, TextureAtlasSprite icon) {

		if (active) {
			modelBaseAnimation[facing].render(ccrs, new IconTransformation(icon));
		}
	}

	/* HELPERS */

	/**
	 * Used to get the overlay texture for the given side.
	 * This should specifically relate to the level of the machine and not it's state.
	 *
	 * @param face  The face.
	 * @param level The level.
	 * @return The texture, Null if there is no texture for the face.
	 */
	private static TextureAtlasSprite getOverlaySprite(EnumFacing face, int level) {

		if (level == 0) {
			return null;
		}
		return TETextures.DYNAMO_OVERLAY[level];
	}

	/* IBlockBakery */
	@Override
	public IExtendedBlockState handleState(IExtendedBlockState state, IBlockAccess world, BlockPos pos) {

		TileEntity tile = world.getTileEntity(pos);

		if (tile == null) {
			return state.withProperty(ModelErrorStateProperty.ERROR_STATE, ErrorState.of("Null tile. Position: %s", pos));
		} else if (!(tile instanceof TileDynamoBase)) {
			return state.withProperty(ModelErrorStateProperty.ERROR_STATE, ErrorState.of("Tile is not an instance of TileDynamoBase, was %s. Pos: %s", tile.getClass().getName(), pos));
		}
		state = state.withProperty(ModelErrorStateProperty.ERROR_STATE, ErrorState.OK);
		state = state.withProperty(TEProps.TILE_DYNAMO, (TileDynamoBase) tile);
		return state;
	}

	/* IItemBakery */
	@Override
	public List<BakedQuad> bakeItemQuads(EnumFacing face, ItemStack stack) {

		if (face == null && !stack.isEmpty()) {
			BakingVertexBuffer buffer = BakingVertexBuffer.create();
			buffer.begin(7, DefaultVertexFormats.ITEM);
			CCRenderState ccrs = CCRenderState.instance();
			ccrs.reset();
			ccrs.bind(buffer);

			boolean creative = BlockDynamo.itemBlock.isCreative(stack);
			int level = BlockDynamo.itemBlock.getLevel(stack);
			renderCoil(ccrs, 1, false, 0);
			renderBase(ccrs, 1, false, stack.getMetadata());

			if (level > 0) {
				renderBaseOverlay(ccrs, 1, false, creative ? TETextures.DYNAMO_OVERLAY_C : getOverlaySprite(face, level));
			}
			buffer.finishDrawing();
			return buffer.bake();
		}
		return new ArrayList<>();
	}

	/* ILayeredBlockBakery */
	@Override
	public List<BakedQuad> bakeLayerFace(EnumFacing face, BlockRenderLayer layer, IExtendedBlockState state) {

		if (face == null && state != null) {
			TileDynamoBase dynamo = state.getValue(TEProps.TILE_DYNAMO);
			boolean creative = dynamo.isCreative;
			boolean active = dynamo.isActive;
			int level = dynamo.getLevel();
			int facing = dynamo.getFacing();
			int coil = dynamo.getCoil();
			int type = state.getValue(BlockDynamo.VARIANT).getMetadata();

			TextureAtlasSprite coilUnderlay = dynamo.getCoilUnderlayTexture();
			TextureAtlasSprite baseUnderlay = dynamo.getBaseUnderlayTexture();

			BakingVertexBuffer buffer = BakingVertexBuffer.create();
			buffer.begin(7, DefaultVertexFormats.ITEM);
			CCRenderState ccrs = CCRenderState.instance();
			ccrs.reset();
			ccrs.bind(buffer);
			if (layer == BlockRenderLayer.SOLID) {
				renderBaseAnimation(ccrs, facing, active, baseUnderlay);
			} else if (layer == BlockRenderLayer.CUTOUT) {
				renderCoil(ccrs, facing, active, coil);
				renderBase(ccrs, facing, active, type);

				if (level > 0) {
					renderBaseOverlay(ccrs, facing, active, creative ? TETextures.DYNAMO_OVERLAY_C : getOverlaySprite(face, level));
				}
			} else if (TileDynamoBase.COIL_UNDERLAY[coil]) {
				renderCoilAnimation(ccrs, facing, active, coilUnderlay);
			}
			buffer.finishDrawing();
			return buffer.bake();
		}
		return new ArrayList<>();
	}

}
