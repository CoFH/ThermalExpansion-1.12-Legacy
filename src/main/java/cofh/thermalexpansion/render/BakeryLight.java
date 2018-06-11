package cofh.thermalexpansion.render;

import codechicken.lib.model.bakery.ModelErrorStateProperty;
import codechicken.lib.model.bakery.ModelErrorStateProperty.ErrorState;
import codechicken.lib.model.bakery.generation.ILayeredBlockBakery;
import codechicken.lib.render.CCModel;
import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.buffer.BakingVertexBuffer;
import codechicken.lib.vec.Cuboid6;
import codechicken.lib.vec.uv.IconTransformation;
import cofh.core.util.helpers.ItemHelper;
import cofh.core.util.helpers.RenderHelper;
import cofh.thermalexpansion.block.light.BlockLight;
import cofh.thermalexpansion.block.light.TileLightBase;
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

		modelHalo.generateBlock(0, Cuboid6.full.expand(0.125F));
		modelHalo.computeNormals();
		modelHalo.shrinkUVs(RenderHelper.RENDER_OFFSET);
	}

	/* RENDER */
	public void renderCenter(CCRenderState ccrs, int color, TextureAtlasSprite texture) {

		if (texture != null) {
			modelCenter.setColour(color);
			modelCenter.render(ccrs, new IconTransformation(texture));
			modelCenter.setColour(0xFFFFFFFF);
		}
	}

	public void renderFrame(CCRenderState ccrs, int color, TextureAtlasSprite texture) {

		if (texture != null) {
			modelFrame.setColour(color);
			modelFrame.render(ccrs, new IconTransformation(texture));
			modelFrame.setColour(0xFFFFFFFF);
		}
	}

	public void renderHalo(CCRenderState ccrs, int color, TextureAtlasSprite texture) {

		if (texture != null) {
			modelHalo.setColour(color & ~0x80);
			modelHalo.render(ccrs, new IconTransformation(texture));
			modelHalo.setColour(0xFFFFFFFF);
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
		return state;
	}

	/* IItemBakery */
	@Override
	public List<BakedQuad> bakeItemQuads(EnumFacing face, ItemStack stack) {

		List<BakedQuad> quads = new ArrayList<>();

		if (face != null && !stack.isEmpty()) {
			int color = 0xFFFFFFFF;
			boolean modified = false;

			if (stack.hasTagCompound()) {
				if (stack.getTagCompound().hasKey("Color")) {
					int stackColor = stack.getTagCompound().getInteger("Color");
					if (stackColor != -1) {
						color = (stackColor << 8) + 0xFF;
						modified = true;
					}
				}
			}
			BakingVertexBuffer buffer = BakingVertexBuffer.create();
			CCRenderState ccrs = CCRenderState.instance();
			buffer.begin(0x07, DefaultVertexFormats.ITEM);
			ccrs.reset();
			ccrs.bind(buffer);

			int metadata = ItemHelper.getItemDamage(stack);

			renderCenter(ccrs, color, getCenterTexture(metadata, modified));
			renderFrame(ccrs, color, getFrameTexture(metadata));

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
			TileLightBase light = state.getValue(TEProps.TILE_LIGHT);
			boolean modified = false; // light.isModified();
			int color = 0xFFFFFFFF; // light.color;
			int type = state.getValue(BlockLight.VARIANT).getMetadata();

			BakingVertexBuffer buffer = BakingVertexBuffer.create();
			buffer.begin(0x07, DefaultVertexFormats.ITEM);
			CCRenderState ccrs = CCRenderState.instance();
			ccrs.reset();
			ccrs.bind(buffer);

			if (layer == BlockRenderLayer.SOLID) {
				renderCenter(ccrs, color, getCenterTexture(type, modified));
			} else if (layer == BlockRenderLayer.CUTOUT) {
				renderFrame(ccrs, color, getFrameTexture(type));
			} else if (layer == BlockRenderLayer.TRANSLUCENT) {
				renderHalo(ccrs, color, getHaloTexture(type));
			}
			buffer.finishDrawing();
			quads.addAll(buffer.bake());
		}
		return quads;
	}

	/* HELPERS */
	private TextureAtlasSprite getCenterTexture(int metadata, boolean modified) {

		switch (metadata) {
			case BlockLight.ILLUMINATOR:
				return modified ? TETextures.ILLUMINATOR_CENTER_1 : TETextures.ILLUMINATOR_CENTER_0;
			case BlockLight.LUMIUM_LAMP:
			case BlockLight.RADIANT_LAMP:
				return null;
		}
		return null;
	}

	private TextureAtlasSprite getFrameTexture(int metadata) {

		switch (metadata) {
			case BlockLight.ILLUMINATOR:
				return TETextures.ILLUMINATOR_FRAME;
			case BlockLight.LUMIUM_LAMP:
			case BlockLight.RADIANT_LAMP:
				return TETextures.LAMP_CENTER;
		}
		return null;
	}

	private TextureAtlasSprite getHaloTexture(int metadata) {

		switch (metadata) {
			case BlockLight.ILLUMINATOR:
			case BlockLight.LUMIUM_LAMP:
				return null;
			case BlockLight.RADIANT_LAMP:
				return TETextures.LAMP_HALO;
		}
		return null;
	}

}
