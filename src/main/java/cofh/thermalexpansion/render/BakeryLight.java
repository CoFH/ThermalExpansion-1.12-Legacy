package cofh.thermalexpansion.render;

import codechicken.lib.model.bakery.ModelErrorStateProperty;
import codechicken.lib.model.bakery.ModelErrorStateProperty.ErrorState;
import codechicken.lib.model.bakery.generation.ILayeredBlockBakery;
import codechicken.lib.render.CCModel;
import cofh.thermalexpansion.block.light.TileLightBase;
import cofh.thermalexpansion.init.TEProps;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.BakedQuad;
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

	static final int NUM_RENDERS = 2;
	static final int NUM_STYLES = 16;

	static CCModel[] modelFrame = new CCModel[NUM_STYLES];
	static CCModel[] modelCenter = new CCModel[NUM_STYLES];
	static CCModel[] modelHalo = new CCModel[NUM_STYLES];

	static {

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

		//		if (face != null && !stack.isEmpty()) {
		//			BakingVertexBuffer buffer = BakingVertexBuffer.create();
		//			CCRenderState ccrs = CCRenderState.instance();
		//			buffer.begin(0x07, DefaultVertexFormats.ITEM);
		//			ccrs.reset();
		//			ccrs.bind(buffer);
		//
		//			IItemBlockTextureProvider provider = TEBlocks.blockLight;
		//			renderFace(ccrs, face, provider.getTexture(face, stack), 0xFFFFFFFF);
		//
		//			buffer.finishDrawing();
		//			quads.addAll(buffer.bake());
		//		}
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

}
