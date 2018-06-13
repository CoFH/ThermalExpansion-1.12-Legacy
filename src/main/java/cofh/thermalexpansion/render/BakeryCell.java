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
import cofh.redstoneflux.api.IEnergyContainerItem;
import cofh.thermalexpansion.block.storage.BlockCell;
import cofh.thermalexpansion.block.storage.TileCell;
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
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.List;

@SideOnly (Side.CLIENT)
public class BakeryCell implements ILayeredBlockBakery {

	public static final BakeryCell INSTANCE = new BakeryCell();

	static CCModel modelCenter = CCModel.quadModel(24);
	static CCModel modelFrame = CCModel.quadModel(48);

	static {
		modelCenter.generateBlock(0, 0.15, 0.15, 0.15, 0.85, 0.85, 0.85).computeNormals();

		Cuboid6 box = new Cuboid6(0, 0, 0, 1, 1, 1);
		double inset = 0.1875;
		modelFrame.generateBlock(0, box);
		CCModel.generateBackface(modelFrame, 0, modelFrame, 24, 24);
		modelFrame.computeNormals();
		for (int i = 24; i < 48; i++) {
			modelFrame.verts[i].vec.add(modelFrame.normals()[i].copy().multiply(inset));
		}
		modelFrame.shrinkUVs(RenderHelper.RENDER_OFFSET);
	}

	/* RENDER */
	public void renderCenter(CCRenderState ccrs) {

		//if (metadata == 1 || metadata == 2) {
		//    modelCenter.render(ccrs, new IconTransformation(TETextures.CELL_CENTER_SOLID));
		//} else {
		modelCenter.render(ccrs, new IconTransformation(TETextures.CELL_CENTER_1));
		//}
	}

	public void renderFrame(CCRenderState ccrs, boolean creative, int level, byte[] sideCache, int facing, TextureAtlasSprite faceTexture) {

		if (creative) {
			level = 5;
		}
		for (int i = 0; i < 6; i++) {
			modelFrame.render(ccrs, i * 4, i * 4 + 4, new IconTransformation(TETextures.CELL_SIDE[level]));//Side
			modelFrame.render(ccrs, i * 4 + 24, i * 4 + 28, new IconTransformation(TETextures.CELL_INNER[level]));//Inner
		}
		if (sideCache != null) {
			for (EnumFacing face : EnumFacing.VALUES) {
				modelFrame.render(ccrs, face.ordinal() * 4, face.ordinal() * 4 + 4, new IconTransformation(TETextures.CELL_CONFIG[sideCache[face.ordinal()]]));
			}
			modelFrame.render(ccrs, facing * 4, facing * 4 + 4, new IconTransformation(faceTexture));
		}
	}

	/* HELPERS */
	private int getScaledEnergyStored(ItemStack container, int scale) {

		IEnergyContainerItem containerItem = (IEnergyContainerItem) container.getItem();
		return (int) (containerItem.getEnergyStored(container) * (long) scale / containerItem.getMaxEnergyStored(container));
	}

	/* ICustomBlockBakery */
	@Override
	public IExtendedBlockState handleState(IExtendedBlockState state, IBlockAccess world, BlockPos pos) {

		TileEntity tile = world.getTileEntity(pos);

		if (tile == null) {
			return state.withProperty(ModelErrorStateProperty.ERROR_STATE, ErrorState.of("Null tile. Position: %s", pos));
		} else if (!(tile instanceof TileCell)) {
			return state.withProperty(ModelErrorStateProperty.ERROR_STATE, ErrorState.of("Tile is not an instance of TileCell, was %s. Pos: %s", tile.getClass().getName(), pos));
		}
		state = state.withProperty(ModelErrorStateProperty.ERROR_STATE, ErrorState.OK);
		state = state.withProperty(TEProps.TILE_CELL, (TileCell) tile);
		return state;
	}

	@Override
	public List<BakedQuad> bakeItemQuads(EnumFacing face, ItemStack stack) {

		List<BakedQuad> quads = new ArrayList<>();

		if (face == null && !stack.isEmpty()) {
			BakingVertexBuffer buffer = BakingVertexBuffer.create();
			buffer.begin(7, DefaultVertexFormats.ITEM);
			CCRenderState ccrs = CCRenderState.instance();
			ccrs.reset();
			ccrs.bind(buffer);

			boolean creative = BlockCell.itemBlock.isCreative(stack);
			int level = BlockCell.itemBlock.getLevel(stack);
			int scale = Math.min(8, getScaledEnergyStored(stack, 9));

			renderFrame(ccrs, creative, level, null, 0, null);
			ccrs.brightness = 160 + scale * 10;
			renderCenter(ccrs);

			buffer.finishDrawing();
			quads.addAll(buffer.bake());
		}
		return quads;
	}

	/* ILayeredBlockBakery */
	@Override
	public List<BakedQuad> bakeLayerFace(EnumFacing face, BlockRenderLayer layer, IExtendedBlockState state) {

		List<BakedQuad> quads = new ArrayList<>();

		if (face == null && state != null) {
			TileCell cell = state.getValue(TEProps.TILE_CELL);
			boolean creative = cell.isCreative;
			int level = cell.getLevel();
			int scale = cell.getLightValue();
			TextureAtlasSprite meter = creative ? TETextures.CELL_METER_C : TETextures.CELL_METER[scale];

			int facing = cell.getFacing();
			byte[] sideCache = cell.sideCache;

			BakingVertexBuffer buffer = BakingVertexBuffer.create();
			buffer.begin(7, DefaultVertexFormats.ITEM);
			CCRenderState ccrs = CCRenderState.instance();
			ccrs.reset();
			ccrs.bind(buffer);

			if (layer == BlockRenderLayer.CUTOUT) {
				renderFrame(ccrs, creative, level, sideCache, facing, meter);
			} else if (layer == BlockRenderLayer.TRANSLUCENT) {
				ccrs.brightness = 160 + scale * 10;
				renderCenter(ccrs);
			}
			buffer.finishDrawing();
			quads.addAll(buffer.bake());
		}
		return quads;
	}

}
