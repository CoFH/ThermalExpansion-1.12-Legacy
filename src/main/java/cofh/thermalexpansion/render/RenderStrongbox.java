package cofh.thermalexpansion.render;

import codechicken.lib.render.item.IItemRenderer;
import codechicken.lib.render.particle.IModelParticleProvider;
import codechicken.lib.texture.TextureUtils;
import codechicken.lib.util.TransformUtils;
import cofh.core.render.RenderUtils;
import cofh.thermalexpansion.block.storage.BlockStrongbox;
import cofh.thermalexpansion.block.storage.TileStrongbox;
import cofh.thermalexpansion.init.TEProps;
import cofh.thermalexpansion.render.model.ModelStrongbox;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.model.IModelState;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.Set;

public class RenderStrongbox extends TileEntitySpecialRenderer<TileStrongbox> implements IItemRenderer, IModelParticleProvider {

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

	/* TileEntitySpecialRenderer */
	@Override
	public void render(TileStrongbox tile, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {

		model.boxLid.rotateAngleX = (float) tile.getRadianLidAngle(partialTicks);

		render(tile.isCreative ? 5 : tile.getLevel(), tile.getAccess().ordinal(), tile.getFacing(), x, y, z);
	}

	/* IItemRenderer */
	@Override
	public void renderItem(ItemStack stack, TransformType transformType) {

		double offset = 0;
		int access = 0;

		if (stack.getTagCompound() != null) {
			access = stack.getTagCompound().getByte("Access");
		}
		model.boxLid.rotateAngleX = 0;
		boolean isCreative = BlockStrongbox.itemBlock.isCreative(stack);
		int level = isCreative ? 5 : BlockStrongbox.itemBlock.getLevel(stack);
		render(level, access, 2, offset, offset, offset);
	}

	@Override
	public IModelState getTransforms() {

		return TransformUtils.DEFAULT_BLOCK;
	}

	/* IBakedModel */
	@Override
	public TextureAtlasSprite getParticleTexture() {

		return IItemRenderer.super.getParticleTexture();
	}

	@Override
	public boolean isAmbientOcclusion() {

		return true;
	}

	@Override
	public boolean isGui3d() {

		return true;
	}

	/* IModelParticleProvider */
	@Override
	public Set<TextureAtlasSprite> getHitEffects(@Nonnull RayTraceResult traceResult, IBlockState state, IBlockAccess world, BlockPos pos) {

		TileEntity tileEntity = world.getTileEntity(pos);
		if (tileEntity instanceof TileStrongbox) {
			return Collections.singleton(((TileStrongbox) tileEntity).getBreakTexture());
		}
		return Collections.singleton(TextureUtils.getMissingSprite());
	}

	@Override
	public Set<TextureAtlasSprite> getDestroyEffects(IBlockState state, IBlockAccess world, BlockPos pos) {

		TileEntity tileEntity = world.getTileEntity(pos);
		if (tileEntity instanceof TileStrongbox) {
			return Collections.singleton(((TileStrongbox) tileEntity).getBreakTexture());
		}
		return Collections.singleton(TextureUtils.getMissingSprite());
	}
}
