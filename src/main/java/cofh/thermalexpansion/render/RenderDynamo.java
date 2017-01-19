package cofh.thermalexpansion.render;

import codechicken.lib.model.blockbakery.ILayeredBlockBakery;
import codechicken.lib.render.CCModel;
import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.buffer.BakingVertexBuffer;
import codechicken.lib.texture.TextureUtils;
import codechicken.lib.texture.TextureUtils.IIconRegister;
import codechicken.lib.vec.Translation;
import codechicken.lib.vec.Vector3;
import codechicken.lib.vec.uv.IconTransformation;
import cofh.core.render.IconRegistry;
import cofh.lib.render.RenderHelper;
import cofh.thermalexpansion.block.CommonProperties;
import cofh.thermalexpansion.block.dynamo.BlockDynamo;
import cofh.thermalexpansion.block.dynamo.BlockDynamo.Type;
import cofh.thermalexpansion.block.dynamo.TileDynamoBase;
import cofh.thermalexpansion.init.TEProps;
import cofh.thermalexpansion.init.TETextures;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.property.IExtendedBlockState;

import java.util.ArrayList;
import java.util.List;

public class RenderDynamo implements IIconRegister, ILayeredBlockBakery {

	public static final RenderDynamo INSTANCE = new RenderDynamo();

	static CCModel[][] modelCoil = new CCModel[2][6];
	static CCModel[][] modelBase = new CCModel[2][6];
	static CCModel[] modelAnimation = new CCModel[6];

	static {

		generateModels();
	}

	@Override
	public void registerIcons(TextureMap textureMap) {

		IconRegistry.addIcon("DynamoCoilRedstone", "thermalexpansion:blocks/dynamo/dynamo_coil_redstone", textureMap);

		IconRegistry.addIcon("Dynamo" + Type.STEAM.ordinal(), "thermalexpansion:blocks/dynamo/dynamo_steam", textureMap);
		IconRegistry.addIcon("Dynamo" + Type.MAGMATIC.ordinal(), "thermalexpansion:blocks/dynamo/dynamo_magmatic", textureMap);
		IconRegistry.addIcon("Dynamo" + Type.COMPRESSION.ordinal(), "thermalexpansion:blocks/dynamo/dynamo_compression", textureMap);
		IconRegistry.addIcon("Dynamo" + Type.REACTANT.ordinal(), "thermalexpansion:blocks/dynamo/dynamo_reactant", textureMap);
		IconRegistry.addIcon("Dynamo" + Type.ENERVATION.ordinal(), "thermalexpansion:blocks/dynamo/dynamo_enervation", textureMap);
	}

	@Override
	public IExtendedBlockState handleState(IExtendedBlockState state, TileEntity tile) {

		TileDynamoBase dynamo = (TileDynamoBase) tile;
		state = state.withProperty(TEProps.FACING, EnumFacing.VALUES[dynamo.getFacing()]);
		state = state.withProperty(TEProps.ACTIVE, dynamo.isActive);
		state = state.withProperty(CommonProperties.ACTIVE_SPRITE_PROPERTY, new ResourceLocation(dynamo.getActiveIcon().getIconName()));
		return state;
	}

	@Override
	public List<BakedQuad> bakeLayerFace(EnumFacing face, BlockRenderLayer layer, IExtendedBlockState state) {

		if (face == null) {
			int facing = state.getValue(TEProps.FACING).ordinal();
			boolean active = state.getValue(TEProps.ACTIVE);
			int type = state.getValue(BlockDynamo.VARIANT).getMetadata();
			TextureAtlasSprite activeSprite = TextureUtils.getTexture(state.getValue(CommonProperties.ACTIVE_SPRITE_PROPERTY));

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
			}
			buffer.finishDrawing();
			return buffer.bake();
		}
		return new ArrayList<BakedQuad>();
	}

	@Override
	public List<BakedQuad> bakeItemQuads(EnumFacing face, ItemStack stack) {

		if (face == null) {
			BakingVertexBuffer buffer = BakingVertexBuffer.create();
			buffer.begin(7, DefaultVertexFormats.ITEM);
			CCRenderState ccrs = CCRenderState.instance();
			ccrs.reset();
			ccrs.bind(buffer);
			renderCoil(ccrs, 1, false);
			renderBase(ccrs, 1, false, stack.getMetadata());
			buffer.finishDrawing();
			return buffer.bake();
		}
		return new ArrayList<BakedQuad>();
	}

	private static void generateModels() {

		double d1 = RenderHelper.RENDER_OFFSET;
		double d2 = 6F / 16F;
		double d3 = 10F / 16F;

		modelCoil[0][1] = CCModel.quadModel(24).generateBox(0, -4, 0, -4, 8, 8, 8, 0, 0, 32, 32, 16).computeNormals().shrinkUVs(d1);
		modelCoil[1][1] = CCModel.quadModel(24).generateBox(0, -4, 0, -4, 8, 8, 8, 0, 16, 32, 32, 16).computeNormals().shrinkUVs(d1);

		modelBase[0][1] = CCModel.quadModel(24).generateBox(0, -8, -8, -8, 16, 10, 16, 0, 0, 64, 64, 16).computeNormals().shrinkUVs(d1);
		modelBase[1][1] = CCModel.quadModel(24).generateBox(0, -8, -8, -8, 16, 10, 16, 0, 32, 64, 64, 16).computeNormals().shrinkUVs(d1);

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

	public void renderCoil(CCRenderState ccrs, int facing, boolean active) {

		if (active) {
			modelCoil[0][facing].render(ccrs, new Translation(0.5, 0.5, 0.5), new IconTransformation(TETextures.DYNAMO_COIL_REDSTONE));
		} else {
			modelCoil[1][facing].render(ccrs, new Translation(0.5, 0.5, 0.5), new IconTransformation(TETextures.DYNAMO_COIL_REDSTONE));
		}
	}

	public void renderBase(CCRenderState ccrs, int facing, boolean active, int type) {

		if (active) {
			modelBase[0][facing].render(ccrs, new Translation(0.5, 0.5, 0.5), new IconTransformation(TETextures.DYNAMO[type]));
		} else {
			modelBase[1][facing].render(ccrs, new Translation(0.5, 0.5, 0.5), new IconTransformation(TETextures.DYNAMO[type]));
		}
	}

	public void renderAnimation(CCRenderState ccrs, int facing, boolean active, int type, TextureAtlasSprite icon) {

		if (active) {
			modelAnimation[facing].render(ccrs, new IconTransformation(icon));
		}
	}

}
