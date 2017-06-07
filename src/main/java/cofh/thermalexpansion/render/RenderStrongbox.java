package cofh.thermalexpansion.render;

import codechicken.lib.render.item.IItemRenderer;
import codechicken.lib.texture.TextureUtils;
import codechicken.lib.util.TransformUtils;
import cofh.core.render.RenderUtils;
import cofh.thermalexpansion.block.storage.BlockStrongbox;
import cofh.thermalexpansion.block.storage.TileStrongbox;
import cofh.thermalexpansion.init.TEProps;
import cofh.thermalexpansion.render.model.ModelStrongbox;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IPerspectiveAwareModel;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;
import javax.vecmath.Matrix4f;
import java.util.ArrayList;
import java.util.List;

public class RenderStrongbox extends TileEntitySpecialRenderer<TileStrongbox> implements IItemRenderer, IPerspectiveAwareModel {

	private static final ResourceLocation[] TEXTURES = new ResourceLocation[6];

	public static final RenderStrongbox INSTANCE = new RenderStrongbox();
	private static ModelStrongbox model = new ModelStrongbox();

	public static void initialize() {

		TEXTURES[0] = new ResourceLocation(TEProps.PATH_RENDER + "storage/strongbox_0.png");
		TEXTURES[1] = new ResourceLocation(TEProps.PATH_RENDER + "storage/strongbox_1.png");
		TEXTURES[2] = new ResourceLocation(TEProps.PATH_RENDER + "storage/strongbox_2.png");
		TEXTURES[3] = new ResourceLocation(TEProps.PATH_RENDER + "storage/strongbox_3.png");
		TEXTURES[4] = new ResourceLocation(TEProps.PATH_RENDER + "storage/strongbox_4.png");
		TEXTURES[5] = new ResourceLocation(TEProps.PATH_RENDER + "storage/strongbox_c.png");
	}

	public void render(int type, int access, int facing, double x, double y, double z) {

		TextureUtils.changeTexture(TEXTURES[type]);

		GlStateManager.pushMatrix();

		GlStateManager.translate(x, y + 1.0, z + 1.0);
		GlStateManager.scale(1.0F, -1F, -1F);
		GlStateManager.translate(0.5F, 0.5F, 0.5F);
		GlStateManager.rotate(RenderUtils.facingAngle[facing], 0.0F, 1.0F, 0.0F);
		GlStateManager.translate(-0.5F, -0.5F, -0.5F);

		GlStateManager.enableRescaleNormal();
		model.render(access);
		GlStateManager.disableRescaleNormal();

		GlStateManager.popMatrix();
	}

	@Override
	public void renderTileEntityAt(TileStrongbox tile, double x, double y, double z, float f, int destroyStage) {

		model.boxLid.rotateAngleX = (float) tile.getRadianLidAngle(f);

		render(tile.isCreative ? 5 : tile.getLevel(), tile.getAccess().ordinal(), tile.getFacing(), x, y, z);
	}

	/* IItemRenderer */
	@Override
	public void renderItem(ItemStack item) {

		double offset = 0;
		int access = 0;

		if (item.getTagCompound() != null) {
			access = item.getTagCompound().getByte("Access");
		}
		model.boxLid.rotateAngleX = 0;
		boolean isCreative = BlockStrongbox.itemBlock.isCreative(item);
		int level = isCreative ? 5 : BlockStrongbox.itemBlock.getLevel(item);
		render(level, access, 2, offset, offset, offset);
	}

	@Override
	public Pair<? extends IBakedModel, Matrix4f> handlePerspective(TransformType cameraTransformType) {

		return MapWrapper.handlePerspective(this, TransformUtils.DEFAULT_BLOCK, cameraTransformType);
	}

	/* IBakedModel */
	@Override
	public List<BakedQuad> getQuads(@Nullable IBlockState state, @Nullable EnumFacing side, long rand) {

		return new ArrayList<>();
	}

	@Override
	public boolean isAmbientOcclusion() {

		return true;
	}

	@Override
	public boolean isGui3d() {

		return true;
	}

	@Override
	public boolean isBuiltInRenderer() {

		return true;
	}

	@Override
	public TextureAtlasSprite getParticleTexture() {

		return TextureUtils.getMissingSprite();
	}

	@Override
	public ItemCameraTransforms getItemCameraTransforms() {

		return ItemCameraTransforms.DEFAULT;
	}

	@Override
	public ItemOverrideList getOverrides() {

		return ItemOverrideList.NONE;
	}

}
