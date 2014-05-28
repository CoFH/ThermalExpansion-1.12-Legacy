package thermalexpansion.render;

import codechicken.lib.lighting.LightModel;
import codechicken.lib.render.CCModel;
import codechicken.lib.render.CCRenderState;
import codechicken.lib.vec.Cuboid6;
import codechicken.lib.vec.Translation;
import cofh.block.BlockCoFHBase;
import cofh.render.IconRegistry;
import cofh.render.RenderHelper;
import cofh.render.RenderUtils;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.client.IItemRenderer;

import org.lwjgl.opengl.GL11;

import thermalexpansion.block.ender.BlockTesseract;
import thermalexpansion.block.ender.TileTesseract;
import thermalexpansion.core.TEProps;

@SideOnly(Side.CLIENT)
public class RenderTesseract implements ISimpleBlockRenderingHandler, IItemRenderer {

	public static final RenderTesseract instance = new RenderTesseract();

	static IIcon[] textureCenter = new IIcon[2];
	static IIcon[] textureFrame = new IIcon[4];
	static CCModel modelCenter = CCModel.quadModel(24);
	static CCModel modelFrame = CCModel.quadModel(48);

	static {
		TEProps.renderIdTesseract = RenderingRegistry.getNextAvailableRenderId();
		RenderingRegistry.registerBlockHandler(instance);

		modelCenter.generateBlock(0, 0.15, 0.15, 0.15, 0.85, 0.85, 0.85).computeNormals();

		Cuboid6 box = new Cuboid6(0, 0, 0, 1, 1, 1);
		double inset = 0.1875;
		modelFrame = CCModel.quadModel(48).generateBlock(0, box);
		CCModel.generateBackface(modelFrame, 0, modelFrame, 24, 24);
		modelFrame.computeNormals();
		for (int i = 24; i < 48; i++) {
			modelFrame.verts[i].vec.add(modelFrame.normals()[i].copy().multiply(inset));
		}
		modelFrame.computeLighting(LightModel.standardLightModel);
	}

	public static void initialize() {

		textureCenter[0] = IconRegistry.getIcon("FluidEnder");
		textureCenter[1] = IconRegistry.getIcon("SkyEnder");
		textureFrame[0] = IconRegistry.getIcon("Tesseract");
		textureFrame[1] = IconRegistry.getIcon("TesseractInner");
		textureFrame[2] = IconRegistry.getIcon("TesseractActive");
		textureFrame[3] = IconRegistry.getIcon("TesseractInnerActive");
	}

	public void renderCenter(int metadata, TileTesseract tile, double x, double y, double z) {

		if (tile != null && tile.isActive) {
			modelCenter.render(x, y, z, RenderUtils.getIconTransformation(textureCenter[1]));
		} else {
			modelCenter.render(x, y, z, RenderUtils.getIconTransformation(textureCenter[0]));
		}
	}

	public void renderFrame(int metadata, TileTesseract tile, double x, double y, double z) {

		Translation trans = RenderUtils.getRenderVector(x, y, z).translation();
		for (int i = 0; i < 6; i++) {
			if (tile != null && tile.isActive && tile.redstoneControlOrDisable()) {
				modelFrame.render(i * 4, i * 4 + 4, trans, RenderUtils.getIconTransformation(textureFrame[2]));
				modelFrame.render(i * 4 + 24, i * 4 + 28, trans, RenderUtils.getIconTransformation(textureFrame[3]));
			} else {
				modelFrame.render(i * 4, i * 4 + 4, trans, RenderUtils.getIconTransformation(textureFrame[0]));
				modelFrame.render(i * 4 + 24, i * 4 + 28, trans, RenderUtils.getIconTransformation(textureFrame[1]));
			}
		}
	}

	/* ISimpleBlockRenderingHandler */
	@Override
	public void renderInventoryBlock(Block block, int metadata, int modelID, RenderBlocks renderer) {

		RenderUtils.preItemRender();

		CCRenderState.startDrawing();
		renderFrame(metadata, null, -0.5, -0.5, -0.5);
		renderCenter(metadata, null, -0.5, -0.5, -0.5);
		CCRenderState.draw();

		RenderUtils.postItemRender();
	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {

		TileEntity tile = world.getTileEntity(x, y, z);
		if (!(tile instanceof TileTesseract)) {
			return false;
		}
		TileTesseract theTile = (TileTesseract) tile;

		RenderUtils.preWorldRender(world, x, y, z);
		if (BlockCoFHBase.renderPass == 0) {
			renderFrame(0, theTile, x, y, z);
		} else {
			renderCenter(0, theTile, x, y, z);
		}
		return true;
	}

	@Override
	public boolean shouldRender3DInInventory(int modelId) {

		return true;
	}

	@Override
	public int getRenderId() {

		return TEProps.renderIdTesseract;
	}

	/* IItemRenderer */
	@Override
	public boolean handleRenderType(ItemStack item, ItemRenderType type) {

		return true;
	}

	@Override
	public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {

		return true;
	}

	@Override
	public void renderItem(ItemRenderType type, ItemStack item, Object... data) {

		GL11.glPushMatrix();
		double offset = -0.5;
		if (type == ItemRenderType.EQUIPPED || type == ItemRenderType.EQUIPPED_FIRST_PERSON) {
			offset = 0;
		} else if (type == ItemRenderType.ENTITY) {
			GL11.glScaled(0.5, 0.5, 0.5);
		}
		RenderUtils.preItemRender();
		RenderHelper.setBlockTextureSheet();

		CCRenderState.startDrawing();
		instance.renderFrame(0, null, offset, offset, offset);

		if (item.getItemDamage() == BlockTesseract.TESSERACT_FRAME_FULL_ID) {
			instance.renderCenter(0, null, offset, offset, offset);
		}
		CCRenderState.draw();

		RenderHelper.setItemTextureSheet();
		RenderUtils.postItemRender();
		GL11.glPopMatrix();
	}

}
