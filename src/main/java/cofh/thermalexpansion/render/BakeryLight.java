package cofh.thermalexpansion.render;

import codechicken.lib.model.bakery.ModelErrorStateProperty;
import codechicken.lib.model.bakery.ModelErrorStateProperty.ErrorState;
import codechicken.lib.model.bakery.generation.ILayeredBlockBakery;
import codechicken.lib.render.CCModel;
import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.buffer.BakingVertexBuffer;
import codechicken.lib.vec.Cuboid6;
import codechicken.lib.vec.uv.IconTransformation;
import cofh.core.util.helpers.RenderHelper;
import cofh.thermalexpansion.block.light.BlockLight;
import cofh.thermalexpansion.block.light.TileLightBase;
import cofh.thermalexpansion.init.TEProps;
import cofh.thermalexpansion.init.TETextures;
import net.minecraft.block.Block;
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

public class BakeryLight implements ILayeredBlockBakery {

	public static final BakeryLight INSTANCE = new BakeryLight();

	static CCModel modelCenter = CCModel.quadModel(24);
	static CCModel modelFrame = CCModel.quadModel(24);
	static CCModel modelHalo = CCModel.quadModel(24);

	static {
		modelCenter.generateBlock(0, Cuboid6.full.expand(-RenderHelper.RENDER_OFFSET));
		modelCenter.computeNormals();
		modelCenter.shrinkUVs(RenderHelper.RENDER_OFFSET);

		modelFrame.generateBlock(0, Cuboid6.full);
		modelFrame.computeNormals();
		modelFrame.shrinkUVs(RenderHelper.RENDER_OFFSET);

		modelHalo.generateBlock(0, Cuboid6.full.expand(.0625F));
		modelHalo.computeNormals();
		modelHalo.shrinkUVs(RenderHelper.RENDER_OFFSET);
	}

	/* RENDER */
	public void renderCenter(CCRenderState ccrs, TextureAtlasSprite texture) {

		if (texture != null) {
			modelCenter.render(ccrs, new IconTransformation(texture));
		}
	}

	public void renderFrame(CCRenderState ccrs, TextureAtlasSprite texture) {

		if (texture != null) {
			modelFrame.render(ccrs, new IconTransformation(texture));
		}
	}

	public void renderHalo(CCRenderState ccrs, TextureAtlasSprite texture) {

		if (texture != null) {
			modelHalo.render(ccrs, new IconTransformation(texture));
		}
	}

	/* IBlockBakery */
	@Override
	public IExtendedBlockState handleState(IExtendedBlockState state, IBlockAccess world, BlockPos pos) {

		TileEntity tile = world.getTileEntity(pos);

		if (tile == null) {
			return state.withProperty(ModelErrorStateProperty.ERROR_STATE, ErrorState.of("Null tile. Position: %s", pos));
		} else if (!(tile instanceof TileLightBase)) {
			return state.withProperty(ModelErrorStateProperty.ERROR_STATE, ErrorState.of("Tile is not an instance of TileLightBase, was %s. Pos: %s", tile.getClass().getName(), pos));
		}
		state = state.withProperty(ModelErrorStateProperty.ERROR_STATE, ErrorState.OK);
		state = state.withProperty(TEProps.TILE_LIGHT, (TileLightBase) tile);
		state = state.withProperty(TEProps.BAKERY_WORLD, world);
		return state;
	}

	/* IItemBakery */
	@Override
	public List<BakedQuad> bakeItemQuads(EnumFacing face, ItemStack stack) {

		List<BakedQuad> quads = new ArrayList<>();

		if (face != null && !stack.isEmpty()) {
			BakingVertexBuffer buffer = BakingVertexBuffer.create();
			CCRenderState ccrs = CCRenderState.instance();
			buffer.begin(0x07, DefaultVertexFormats.ITEM);
			ccrs.reset();
			ccrs.bind(buffer);

			renderCenter(ccrs, getCenterTexture(stack));
			renderFrame(ccrs, getFrameTexture(stack));
			// renderHalo(ccrs, getHaloTexture(stack));

			buffer.finishDrawing();
			quads.addAll(buffer.bake());
		}
		return quads;
	}

	/* ILayeredBlockBakery */
	@Override
	public List<BakedQuad> bakeLayerFace(EnumFacing face, BlockRenderLayer layer, IExtendedBlockState state) {

		List<BakedQuad> quads = new ArrayList<>();

		if (face != null && state != null) {
			Block block = state.getBlock();
			TileLightBase tile = state.getValue(TEProps.TILE_LIGHT);
		}

		//		if (face != null && state != null) {
		//			Block block = state.getBlock();
		//			IWorldBlockTextureProvider provider = (IWorldBlockTextureProvider) block;
		//			TileLightBase tile = state.getValue(TEProps.TILE_LIGHT);
		//			IBlockAccess world = state.getValue(TEProps.BAKERY_WORLD);
		//
		//			BakingVertexBuffer buffer = BakingVertexBuffer.create();
		//			buffer.begin(0x07, DefaultVertexFormats.ITEM);
		//			CCRenderState ccrs = CCRenderState.instance();
		//			ccrs.reset();
		//			ccrs.bind(buffer);
		//
		//			renderFace(ccrs, face, provider.getTexture(face, state, layer, world, tile.getPos()), tile.getColorMask(layer, face));
		//
		//			buffer.finishDrawing();
		//			quads.addAll(buffer.bake());
		//		}
		return quads;
	}

	/* HELPERS */
	private TextureAtlasSprite getCenterTexture(ItemStack stack) {

		switch (stack.getMetadata()) {
			case BlockLight.ILLUMINATOR:
				return TETextures.ILLUMINATOR_CENTER;
			case BlockLight.LUMIUM_LAMP:
			case BlockLight.RADIANT_LAMP:
				return null;
		}
		return null;
	}

	private TextureAtlasSprite getFrameTexture(ItemStack stack) {

		switch (stack.getMetadata()) {
			case BlockLight.ILLUMINATOR:
				return TETextures.ILLUMINATOR_FRAME;
			case BlockLight.LUMIUM_LAMP:
			case BlockLight.RADIANT_LAMP:
				return TETextures.LAMP_CENTER;
		}
		return null;
	}

	//	private TextureAtlasSprite getHaloTexture(ItemStack stack) {
	//
	//		switch (stack.getMetadata()) {
	//			case BlockLight.ILLUMINATOR:
	//			case BlockLight.LUMIUM_LAMP:
	//				return null;
	//			case BlockLight.RADIANT_LAMP:
	//				return TETextures.LAMP_HALO;
	//		}
	//		return null;
	//	}

}
