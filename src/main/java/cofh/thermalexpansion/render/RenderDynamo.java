package cofh.thermalexpansion.render;

import codechicken.lib.model.bakery.generation.ILayeredBlockBakery;
import codechicken.lib.render.CCModel;
import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.buffer.BakingVertexBuffer;
import codechicken.lib.vec.Translation;
import codechicken.lib.vec.Vector3;
import codechicken.lib.vec.uv.IconTransformation;
import cofh.core.render.TextureHelper;
import cofh.core.util.helpers.RenderHelper;
import cofh.thermalexpansion.block.dynamo.BlockDynamo;
import cofh.thermalexpansion.block.dynamo.TileDynamoBase;
import cofh.thermalexpansion.init.TEProps;
import cofh.thermalexpansion.init.TETextures;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.property.IExtendedBlockState;

import java.util.ArrayList;
import java.util.List;

public class RenderDynamo implements ILayeredBlockBakery {

	public static final RenderDynamo INSTANCE = new RenderDynamo();

	private static CCModel[][] modelCoil = new CCModel[2][6];
	private static CCModel[][] modelBase = new CCModel[2][6];
	private static CCModel[][] modelBaseOverlay = new CCModel[2][6];
	private static CCModel[] modelAnimation = new CCModel[6];

	static {
		generateModels();
	}

	private static void generateModels() {

		double d1 = RenderHelper.RENDER_OFFSET;
		double d2 = 6F / 16F;
		double d3 = 10F / 16F;

		modelCoil[0][1] = CCModel.quadModel(24).generateBox(0, -4, 0, -4, 8, 8, 8, 0, 0, 32, 32, 16).computeNormals().shrinkUVs(d1);
		modelCoil[1][1] = CCModel.quadModel(24).generateBox(0, -4, 0, -4, 8, 8, 8, 0, 16, 32, 32, 16).computeNormals().shrinkUVs(d1);

		modelBase[0][1] = CCModel.quadModel(24).generateBox(0, -8, -8, -8, 16, 10, 16, 0, 0, 64, 64, 16).computeNormals().shrinkUVs(d1);
		modelBase[1][1] = CCModel.quadModel(24).generateBox(0, -8, -8, -8, 16, 10, 16, 0, 32, 64, 64, 16).computeNormals().shrinkUVs(d1);

		modelBaseOverlay[0][1] = CCModel.quadModel(24).generateBox(0, -8 - d1, -8 - d1, -8 - d1, 16 + 2 * d1, 10 + 2 * d1, 16 + 2 * d1, 0, 0, 64, 64, 16).computeNormals().shrinkUVs(d1);
		modelBaseOverlay[1][1] = CCModel.quadModel(24).generateBox(0, -8 - d1, -8 - d1, -8 - d1, 16 + 2 * d1, 10 + 2 * d1, 16 + 2 * d1, 0, 32, 64, 64, 16).computeNormals().shrinkUVs(d1);

		modelAnimation[0] = CCModel.quadModel(16).generateBlock(0, d1, d2 + d1, d1, 1 - d1, 1 - d1, 1 - d1, 3).computeNormals();
		modelAnimation[1] = CCModel.quadModel(16).generateBlock(0, d1, d1, d1, 1 - d1, d3 - d1, 1 - d1, 3).computeNormals();

		modelAnimation[2] = CCModel.quadModel(16).generateBlock(0, d1, d1, d2 + d1, 1 - d1, 1 - d1, 1 - d1, 12).computeNormals();
		modelAnimation[3] = CCModel.quadModel(16).generateBlock(0, d1, d1, d1, 1 - d1, 1 - d1, d3 - d1, 12).computeNormals();

		modelAnimation[4] = CCModel.quadModel(16).generateBlock(0, d2 + d1, d1, d1, 1 - d1, 1 - d1, 1 - d1, 48).computeNormals();
		modelAnimation[5] = CCModel.quadModel(16).generateBlock(0, d1, d1, d1, d3 - d1, 1 - d1, 1 - d1, 48).computeNormals();

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
	protected void renderCoil(CCRenderState ccrs, int facing, boolean active) {

		if (active) {
			modelCoil[0][facing].render(ccrs, new Translation(0.5, 0.5, 0.5), new IconTransformation(TETextures.DYNAMO_COIL_REDSTONE));
		} else {
			modelCoil[1][facing].render(ccrs, new Translation(0.5, 0.5, 0.5), new IconTransformation(TETextures.DYNAMO_COIL_REDSTONE));
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

	protected void renderAnimation(CCRenderState ccrs, int facing, boolean active, int type, TextureAtlasSprite icon) {

		if (active) {
			modelAnimation[facing].render(ccrs, new IconTransformation(icon));
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

	/* ICustomBlockBakery */
	@Override
	public IExtendedBlockState handleState(IExtendedBlockState state, IBlockAccess world, BlockPos pos) {

		TileDynamoBase dynamo = (TileDynamoBase) world.getTileEntity(pos);

		if (dynamo == null) {
			return null;
		}
		state = state.withProperty(TEProps.CREATIVE, dynamo.isCreative);
		state = state.withProperty(TEProps.LEVEL, dynamo.getLevel());
		state = state.withProperty(TEProps.ACTIVE, dynamo.isActive);

		state = state.withProperty(TEProps.FACING, EnumFacing.VALUES[dynamo.getFacing()]);
		state = state.withProperty(TEProps.ACTIVE_SPRITE_PROPERTY, new ResourceLocation(dynamo.getActiveIcon().getIconName()));
		return state;
	}

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
			renderCoil(ccrs, 1, false);
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
			boolean creative = state.getValue(TEProps.CREATIVE);
			int level = state.getValue(TEProps.LEVEL);
			boolean active = state.getValue(TEProps.ACTIVE);
			int facing = state.getValue(TEProps.FACING).ordinal();
			int type = state.getValue(BlockDynamo.VARIANT).getMetadata();

			TextureAtlasSprite activeSprite = TextureHelper.getTexture(state.getValue(TEProps.ACTIVE_SPRITE_PROPERTY));

			BakingVertexBuffer buffer = BakingVertexBuffer.create();
			buffer.begin(7, DefaultVertexFormats.ITEM);
			CCRenderState ccrs = CCRenderState.instance();
			ccrs.reset();
			ccrs.bind(buffer);
			if (layer == BlockRenderLayer.SOLID) {
				renderCoil(ccrs, facing, active);
				renderAnimation(ccrs, facing, active, type, activeSprite);
			} else {
				renderBase(ccrs, facing, active, type);

				if (level > 0) {
					renderBaseOverlay(ccrs, facing, active, creative ? TETextures.DYNAMO_OVERLAY_C : getOverlaySprite(face, level));
				}
			}
			buffer.finishDrawing();
			return buffer.bake();
		}
		return new ArrayList<>();
	}

}
