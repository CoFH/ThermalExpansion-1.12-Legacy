package cofh.thermalexpansion.render;

import codechicken.lib.model.blockbakery.ILayeredBlockBakery;
import codechicken.lib.render.CCModel;
import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.buffer.BakingVertexBuffer;
import codechicken.lib.texture.TextureUtils;
import codechicken.lib.vec.Cuboid6;
import codechicken.lib.vec.uv.IconTransformation;
import cofh.api.energy.IEnergyContainerItem;
import cofh.lib.render.RenderHelper;
import cofh.thermalexpansion.block.storage.BlockCell;
import cofh.thermalexpansion.block.storage.ItemBlockCell;
import cofh.thermalexpansion.block.storage.TileCell;
import cofh.thermalexpansion.init.TEProps;
import cofh.thermalexpansion.init.TETextures;
import cofh.thermalfoundation.init.TFFluids;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.List;

@SideOnly (Side.CLIENT)
public class RenderCell implements ILayeredBlockBakery {

	public static final RenderCell instance = new RenderCell();

	static CCModel modelCenter = CCModel.quadModel(24);
	static CCModel modelFrame = CCModel.quadModel(48);

	static {

		modelCenter.generateBlock(0, 0.15, 0.15, 0.15, 0.85, 0.85, 0.85).computeNormals();

		Cuboid6 box = new Cuboid6(0, 0, 0, 1, 1, 1);
		double inset = 0.1875;
		modelFrame = CCModel.quadModel(48).generateBlock(0, box);
		CCModel.generateBackface(modelFrame, 0, modelFrame, 24, 24);
		modelFrame.computeNormals();
		for (int i = 24; i < 48; i++) {
			modelFrame.verts[i].vec.add(modelFrame.normals()[i].copy().multiply(inset));
		}
		modelFrame.shrinkUVs(RenderHelper.RENDER_OFFSET);
	}

	public static void initialize() {

	}

	@Override
	public IExtendedBlockState handleState(IExtendedBlockState state, TileEntity tileEntity) {

		TileCell cell = (TileCell) tileEntity;
		state = state.withProperty(TEProps.LEVEL, cell.getLevel());
		//state = state.withProperty(TEProps.CREATIVE, false);// TODO
		state = state.withProperty(TEProps.SIDE_CONFIG_RAW, cell.sideCache.clone());
		//state = state.withProperty(BlockCell.CHARGE_PROPERTY, Math.min(15, cell.getScaledEnergyStored(16)));
		state = state.withProperty(TEProps.FACING, EnumFacing.VALUES[cell.getFacing()]);
		state = state.withProperty(BlockCell.METER_LEVEL, cell.getLightValue());
		return state;
	}

	@Override
	public List<BakedQuad> bakeLayerFace(EnumFacing face, BlockRenderLayer layer, IExtendedBlockState state) {

		if (face == null) {
			int level = state.getValue(TEProps.LEVEL);
			byte[] sideCache = state.getValue(TEProps.SIDE_CONFIG_RAW);
			//int charge = state.getValue(BlockCell.CHARGE_PROPERTY);
			int facing = state.getValue(TEProps.FACING).ordinal();
			TextureAtlasSprite meter = TETextures.CELL_METER[state.getValue(BlockCell.METER_LEVEL)];

			BakingVertexBuffer buffer = BakingVertexBuffer.create();
			buffer.begin(7, DefaultVertexFormats.ITEM);
			CCRenderState ccrs = CCRenderState.instance();
			ccrs.reset();
			ccrs.bind(buffer);

			if (layer == BlockRenderLayer.CUTOUT) {
				renderFrame(ccrs, level, sideCache, facing, meter);
			} else if (layer == BlockRenderLayer.TRANSLUCENT) {
				//TODO Center brightness.
				//ccrs.brightness = 165 + charge * 5;
				renderCenter(ccrs);
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

			renderFrame(ccrs, ItemBlockCell.getLevel(stack), null, 0, null);
			//TODO Center brightness.
			//ccrs.brightness = 165 + charge * 5;
			renderCenter(ccrs);

			buffer.finishDrawing();
			return buffer.bake();
		}
		return new ArrayList<BakedQuad>();
	}

	public void renderCenter(CCRenderState ccrs) {

		//if (metadata == 1 || metadata == 2) {
		//    modelCenter.render(ccrs, new IconTransformation(TETextures.CELL_CENTER_SOLID));
		//} else {
		modelCenter.render(ccrs, new IconTransformation(TextureUtils.getTexture(TFFluids.fluidRedstone.getStill())));
		//}
	}

	public void renderFrame(CCRenderState ccrs, int level, byte[] sideCache, int facing, TextureAtlasSprite faceTexture) {

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

	private int getScaledEnergyStored(ItemStack container, int scale) {

		IEnergyContainerItem containerItem = (IEnergyContainerItem) container.getItem();

		return (int) (containerItem.getEnergyStored(container) * (long) scale / containerItem.getMaxEnergyStored(container));
	}

}
