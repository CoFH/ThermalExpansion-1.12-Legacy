package cofh.thermalexpansion.render;

import codechicken.lib.model.blockbakery.ILayeredBlockBakery;
import codechicken.lib.render.CCModel;
import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.buffer.BakingVertexBuffer;
import codechicken.lib.texture.IItemBlockTextureProvider;
import codechicken.lib.texture.IWorldBlockTextureProvider;
import codechicken.lib.vec.Cuboid6;
import codechicken.lib.vec.uv.IconTransformation;
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
import net.minecraftforge.common.property.IExtendedBlockState;

import java.util.ArrayList;
import java.util.List;

public class RenderMachine implements ILayeredBlockBakery {

	public static final RenderMachine INSTANCE = new RenderMachine();

	static CCModel model = CCModel.quadModel(48);

	static {
		model.generateBlock(0, Cuboid6.full);
		model.generateBlock(24, Cuboid6.full.copy().expand(.0004F));
		model.computeNormals();
	}

	/* RENDER */
	public void renderFace(CCRenderState ccrs, EnumFacing face, TextureAtlasSprite sprite) {

		if (sprite != null) {
			int i = face.ordinal();
			model.render(ccrs, i * 4, i * 4 + 4, new IconTransformation(sprite));
		}
	}

	public void renderFaceOverlay(CCRenderState ccrs, EnumFacing face, TextureAtlasSprite sprite) {

		if (sprite != null) {
			int i = face.ordinal();
			model.render(ccrs, i * 4 + 24, i * 4 + 4 + 24, new IconTransformation(sprite));
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
		return TETextures.MACHINE_OVERLAY[level];
	}

	/* ICustomBlockBakery */
	@Override
	public IExtendedBlockState handleState(IExtendedBlockState state, TileEntity tileEntity) {

		TileMachineBase machineBase = ((TileMachineBase) tileEntity);

		state = state.withProperty(TEProps.CREATIVE, machineBase.isCreative);
		state = state.withProperty(TEProps.LEVEL, machineBase.getLevel());
		state = state.withProperty(TEProps.ACTIVE, machineBase.isActive);

		state = state.withProperty(TEProps.FACING, EnumFacing.VALUES[machineBase.getFacing()]);
		state = state.withProperty(TEProps.SIDE_CONFIG, machineBase.sideCache);
		state = state.withProperty(TEProps.TILE, machineBase); // Kinda hacky, but we need this to grab textures from the block.
		return state;
	}

	@Override
	public List<BakedQuad> bakeItemQuads(EnumFacing face, ItemStack stack) {

		List<BakedQuad> quads = new ArrayList<>();

		if (face != null) {
			BakingVertexBuffer buffer = BakingVertexBuffer.create();
			CCRenderState ccrs = CCRenderState.instance();
			buffer.begin(0x07, DefaultVertexFormats.ITEM);
			ccrs.reset();
			ccrs.bind(buffer);

			boolean creative = BlockMachine.itemBlock.isCreative(stack);
			int level = BlockMachine.itemBlock.getLevel(stack);
			IItemBlockTextureProvider provider = TEBlocks.blockMachine;
			renderFace(ccrs, face, provider.getTexture(face, stack));

			if (level > 0) {
				renderFaceOverlay(ccrs, face, creative ? TETextures.MACHINE_OVERLAY_C : getOverlaySprite(face, level));
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

		if (face != null) {
			Block block = state.getBlock();
			IWorldBlockTextureProvider provider = ((IWorldBlockTextureProvider) block);

			boolean creative = state.getValue(TEProps.CREATIVE);
			int level = state.getValue(TEProps.LEVEL);
			TileMachineBase tile = ((TileMachineBase) state.getValue(TEProps.TILE));

			BakingVertexBuffer buffer = BakingVertexBuffer.create();
			buffer.begin(0x07, DefaultVertexFormats.ITEM);
			CCRenderState ccrs = CCRenderState.instance();
			ccrs.reset();
			ccrs.bind(buffer);

			renderFace(ccrs, face, provider.getTexture(face, state, layer, tile.getWorld(), tile.getPos()));

			if (layer == BlockRenderLayer.CUTOUT && level > 0) {
				renderFace(ccrs, face, creative ? TETextures.MACHINE_OVERLAY_C : getOverlaySprite(face, level));
			}
			buffer.finishDrawing();
			quads.addAll(buffer.bake());
		}
		return quads;
	}

}
