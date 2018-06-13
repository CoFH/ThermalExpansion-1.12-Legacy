package cofh.thermalexpansion.render;

import codechicken.lib.model.bakedmodels.ModelProperties.PerspectiveProperties;
import codechicken.lib.model.bakery.generation.IItemBakery;
import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.buffer.BakingVertexBuffer;
import codechicken.lib.vec.uv.IconTransformation;
import cofh.thermalexpansion.init.TETextures;
import cofh.thermalexpansion.item.ItemFrame;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;

import java.util.ArrayList;
import java.util.List;

public class BakeryFrame implements IItemBakery {

	public static final BakeryFrame INSTANCE = new BakeryFrame();

	/* IItemBakery */
	@Override
	public List<BakedQuad> bakeItemQuads(EnumFacing dir, ItemStack stack) {

		List<BakedQuad> quads = new ArrayList<>();

		if (dir == null) {
			BakingVertexBuffer buffer = BakingVertexBuffer.create();
			buffer.begin(0x07, DefaultVertexFormats.ITEM);
			CCRenderState ccrs = CCRenderState.instance();
			ccrs.reset();
			ccrs.bind(buffer);
			for (EnumFacing face : EnumFacing.VALUES) {
				int i = face.ordinal();
				BakeryCell.modelFrame.render(ccrs, i * 4, i * 4 + 4, new IconTransformation(getFrameTexture(face, stack)));
				TextureAtlasSprite inner = getInnerTexture(face, stack);
				if (inner != null) {
					BakeryCell.modelFrame.render(ccrs, i * 4 + 24, i * 4 + 28, new IconTransformation(inner));
				}
			}
			TextureAtlasSprite center = getCenterTexture(stack);
			if (center != null) {
				BakeryCell.modelCenter.render(ccrs, new IconTransformation(center));
			}
			buffer.finishDrawing();
			quads.addAll(buffer.bake());
		}
		return quads;
	}

	@Override
	public PerspectiveProperties getModelProperties(ItemStack stack) {

		return PerspectiveProperties.DEFAULT_BLOCK;
	}

	/* HELPERS */
	private TextureAtlasSprite getFrameTexture(EnumFacing face, ItemStack stack) {

		switch (stack.getMetadata()) {
			case ItemFrame.MACHINE:
				if (face == EnumFacing.UP) {
					return TETextures.MACHINE_FRAME_TOP;
				} else if (face == EnumFacing.DOWN) {
					return TETextures.MACHINE_FRAME_BOTTOM;
				} else {
					return TETextures.MACHINE_FRAME_SIDE;
				}
			case ItemFrame.DEVICE:
				if (face == EnumFacing.UP) {
					return TETextures.DEVICE_FRAME_TOP;
				} else if (face == EnumFacing.DOWN) {
					return TETextures.DEVICE_FRAME_BOTTOM;
				} else {
					return TETextures.DEVICE_FRAME_SIDE;
				}
			case ItemFrame.CELL:
				return TETextures.CELL_SIDE_0;
			case ItemFrame.CELL + 1:
				return TETextures.CELL_SIDE_1;
			case ItemFrame.CELL + 2:
			case ItemFrame.CELL + 2 + 16:
				return TETextures.CELL_SIDE_2;
			case ItemFrame.CELL + 3:
			case ItemFrame.CELL + 3 + 16:
				return TETextures.CELL_SIDE_3;
			case ItemFrame.CELL + 4:
			case ItemFrame.CELL + 4 + 16:
				return TETextures.CELL_SIDE_4;
			case ItemFrame.LIGHT:
				return TETextures.ILLUMINATOR_FRAME;
			default:
				return TETextures.MACHINE_OVERLAY_0;
		}
	}

	private TextureAtlasSprite getInnerTexture(EnumFacing face, ItemStack stack) {

		switch (stack.getMetadata()) {
			case ItemFrame.MACHINE:
				return TETextures.MACHINE_FRAME_INNER;
			case ItemFrame.DEVICE:
				return TETextures.DEVICE_FRAME_INNER;
			case ItemFrame.CELL:
				return TETextures.CELL_INNER_0;
			case ItemFrame.CELL + 1:
				return TETextures.CELL_INNER_1;
			case ItemFrame.CELL + 2:
			case ItemFrame.CELL + 2 + 16:
				return TETextures.CELL_INNER_2;
			case ItemFrame.CELL + 3:
			case ItemFrame.CELL + 3 + 16:
				return TETextures.CELL_INNER_3;
			case ItemFrame.CELL + 4:
			case ItemFrame.CELL + 4 + 16:
				return TETextures.CELL_INNER_4;
			case ItemFrame.LIGHT:
			default:
				return null;
		}
	}

	private TextureAtlasSprite getCenterTexture(ItemStack stack) {

		switch (stack.getMetadata()) {
			case ItemFrame.CELL + 2 + 16:
			case ItemFrame.CELL + 3 + 16:
			case ItemFrame.CELL + 4 + 16:
				return TETextures.CELL_CENTER_1;
			case ItemFrame.LIGHT:
			default:
				return null;
		}
	}

}
