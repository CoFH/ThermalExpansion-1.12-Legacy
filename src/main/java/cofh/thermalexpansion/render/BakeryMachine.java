package cofh.thermalexpansion.render;

import codechicken.lib.model.bakery.ModelErrorStateProperty;
import codechicken.lib.model.bakery.ModelErrorStateProperty.ErrorState;
import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.buffer.BakingVertexBuffer;
import codechicken.lib.texture.IItemBlockTextureProvider;
import codechicken.lib.texture.IWorldBlockTextureProvider;
import cofh.thermalexpansion.block.machine.BlockMachine;
import cofh.thermalexpansion.block.machine.TileMachineBase;
import cofh.thermalexpansion.init.TEBlocks;
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

public class BakeryMachine extends CubeBakeryBase {

	public static final BakeryMachine INSTANCE = new BakeryMachine();

	/* HELPERS */

	/**
	 * Used to get the overlay texture for the given side.
	 * This should specifically relate to the level of the machine and not its state.
	 *
	 * @param face  The face.
	 * @param level The level.
	 * @return The texture, Null if there is no texture for the face.
	 */
	private static TextureAtlasSprite getOverlaySprite(EnumFacing face, int level) {

		if (level == 0) {
			return null;
		}
		return TETextures.MACHINE_OVERLAY[level];
	}

	/* IBlockBakery */
	@Override
	public IExtendedBlockState handleState(IExtendedBlockState state, IBlockAccess world, BlockPos pos) {

		TileEntity tile = world.getTileEntity(pos);

		if (tile == null) {
			return state.withProperty(ModelErrorStateProperty.ERROR_STATE, ErrorState.of("Null tile. Position: %s", pos));
		} else if (!(tile instanceof TileMachineBase)) {
			return state.withProperty(ModelErrorStateProperty.ERROR_STATE, ErrorState.of("Tile is not an instance of TileMachineBase, was %s. Pos: %s", tile.getClass().getName(), pos));
		}
		state = state.withProperty(ModelErrorStateProperty.ERROR_STATE, ErrorState.OK);
		state = state.withProperty(TEProps.TILE_MACHINE, (TileMachineBase) tile);
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

			boolean creative = BlockMachine.itemBlock.isCreative(stack);
			int level = BlockMachine.itemBlock.getLevel(stack);
			IItemBlockTextureProvider provider = TEBlocks.blockMachine;
			renderFace(ccrs, face, provider.getTexture(face, stack), 0xFFFFFFFF);

			if (level > 0) {
				renderFaceOverlay(ccrs, face, creative ? TETextures.MACHINE_OVERLAY_C : getOverlaySprite(face, level), 0xFFFFFFFF);
			}
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
			IWorldBlockTextureProvider provider = (IWorldBlockTextureProvider) block;
			TileMachineBase tile = state.getValue(TEProps.TILE_MACHINE);
			IBlockAccess world = state.getValue(TEProps.BAKERY_WORLD);

			boolean creative = tile.isCreative;
			int level = tile.getLevel();

			BakingVertexBuffer buffer = BakingVertexBuffer.create();
			buffer.begin(0x07, DefaultVertexFormats.ITEM);
			CCRenderState ccrs = CCRenderState.instance();
			ccrs.reset();
			ccrs.bind(buffer);

			renderFace(ccrs, face, provider.getTexture(face, state, layer, world, tile.getPos()), tile.getColorMask(layer, face));

			if (layer == BlockRenderLayer.CUTOUT && level > 0) {
				renderFace(ccrs, face, creative ? TETextures.MACHINE_OVERLAY_C : getOverlaySprite(face, level), 0xFFFFFFFF);
			}
			buffer.finishDrawing();
			quads.addAll(buffer.bake());
		}
		return quads;
	}

}
