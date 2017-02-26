package cofh.thermalexpansion.render;

import codechicken.lib.model.PerspectiveAwareModelProperties;
import codechicken.lib.model.bakery.PlanarFaceBakery;
import codechicken.lib.model.blockbakery.IItemBakery;
import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.buffer.BakingVertexBuffer;
import codechicken.lib.texture.TextureUtils;
import codechicken.lib.vec.uv.IconTransformation;
import cofh.thermalexpansion.init.TETextures;
import com.google.common.collect.ImmutableList;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;

import java.util.List;

public class RenderFrame implements IItemBakery {

	public static final RenderFrame INSTANCE = new RenderFrame();

	@Override
	public List<BakedQuad> bakeItemQuads(EnumFacing dir, ItemStack stack) {

		if (dir == null) {
			BakingVertexBuffer buffer = BakingVertexBuffer.create();
			buffer.begin(0x07, DefaultVertexFormats.ITEM);
			CCRenderState ccrs = CCRenderState.instance();
			ccrs.reset();
			ccrs.bind(buffer);
			for (EnumFacing face : EnumFacing.VALUES) {
				int i = face.ordinal();
				RenderCell.modelFrame.render(ccrs, i * 4, i * 4 + 4, new IconTransformation(getFrameTexture(face, stack)));
				TextureAtlasSprite inner = getInnerTexture(face, stack);
				if (inner != null) {
					RenderCell.modelFrame.render(ccrs, i * 4 + 24, i * 4 + 28, new IconTransformation(inner));
				}
			}
			TextureAtlasSprite center = getCenterTexture(stack);
			if (center != null) {
				RenderCell.modelCenter.render(ccrs, new IconTransformation(center));
			}
			buffer.finishDrawing();
			return PlanarFaceBakery.shadeQuadFaces(buffer.bake());
		}
		return ImmutableList.of();
	}

	@Override
	public PerspectiveAwareModelProperties getModelProperties(ItemStack stack) {

		return PerspectiveAwareModelProperties.DEFAULT_BLOCK;
	}

	private TextureAtlasSprite getFrameTexture(EnumFacing face, ItemStack stack) {

		switch (stack.getMetadata()) {
			case 0: // Machine
				if (face == EnumFacing.UP) {
					return TETextures.MACHINE_FRAME_TOP;
				} else if (face == EnumFacing.DOWN) {
					return TETextures.MACHINE_FRAME_BOTTOM;
				} else {
					return TETextures.MACHINE_FRAME_SIDE;
				}
			case 64: // Device
				if (face == EnumFacing.UP) {
					return TETextures.DEVICE_FRAME_TOP;
				} else if (face == EnumFacing.DOWN) {
					return TETextures.DEVICE_FRAME_BOTTOM;
				} else {
					return TETextures.DEVICE_FRAME_SIDE;
				}
			case 128: // Cell
				return TETextures.CELL_SIDE_0;
			case 160: // Illuminator
				return TETextures.ILLUMINATOR_FRAME;
			default:
				return TextureUtils.getMissingSprite();
		}
	}

	private TextureAtlasSprite getInnerTexture(EnumFacing face, ItemStack stack) {

		switch (stack.getMetadata()) {
			case 0: // Machine
				return TETextures.MACHINE_FRAME_INNER;
			case 64: // Device
				return TETextures.DEVICE_FRAME_INNER;
			case 128: // Cell
				return TETextures.CELL_INNER_0;
			case 160: // Illuminator
			default:
				return null;
		}
	}

	private TextureAtlasSprite getCenterTexture(ItemStack stack) {

		switch (stack.getMetadata()) {
			case 0:
				return TextureUtils.getBlockTexture("thermalfoundation:storage/block_tin");
			case 64:
				return TextureUtils.getBlockTexture("thermalfoundation:storage/block_copper");
			case 128:
				return TextureUtils.getBlockTexture("thermalfoundation:storage/block_lead");
			case 160: // Illuminator
			default:
				return null;
		}
	}

}
