package cofh.thermalexpansion.render;

import codechicken.lib.model.bakery.generation.ILayeredBlockBakery;
import codechicken.lib.render.CCModel;
import codechicken.lib.render.CCRenderState;
import codechicken.lib.vec.Cuboid6;
import codechicken.lib.vec.uv.IconTransformation;
import cofh.core.util.helpers.RenderHelper;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;

public abstract class CubeBakeryBase implements ILayeredBlockBakery {


	static CCModel model = CCModel.quadModel(48);

	static {
		model.generateBlock(0, Cuboid6.full);
		model.generateBlock(24, Cuboid6.full.copy().expand(.0004F));
		model.computeNormals();
		model.shrinkUVs(RenderHelper.RENDER_OFFSET);
	}

	/* RENDER */
	public void renderFace(CCRenderState ccrs, EnumFacing face, TextureAtlasSprite sprite, int colorMask) {

		if (sprite != null) {
			int i = face.ordinal();
			ccrs.baseColour = colorMask;
			model.render(ccrs, i * 4, i * 4 + 4, new IconTransformation(sprite));
			ccrs.baseColour = 0xFFFFFFFF;
		}
	}

	public void renderFaceOverlay(CCRenderState ccrs, EnumFacing face, TextureAtlasSprite sprite, int colorMask) {

		if (sprite != null) {
			int i = face.ordinal();
			ccrs.baseColour = colorMask;
			model.render(ccrs, i * 4 + 24, i * 4 + 4 + 24, new IconTransformation(sprite));
			ccrs.baseColour = 0xFFFFFFFF;
		}
	}

}
