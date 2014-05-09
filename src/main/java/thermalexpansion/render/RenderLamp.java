package thermalexpansion.render;

import codechicken.lib.lighting.LightModel;
import codechicken.lib.render.CCModel;
import codechicken.lib.render.CCRenderState;
import cofh.render.IconRegistry;
import cofh.render.RenderHelper;
import cofh.render.RenderUtils;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.client.registry.RenderingRegistry;

import javax.swing.Icon;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.MinecraftForgeClient;

import org.lwjgl.opengl.GL11;

import thermalexpansion.block.TEBlocks;
import thermalexpansion.block.lamp.TileLamp;
import thermalexpansion.core.TEProps;

public class RenderLamp implements ISimpleBlockRenderingHandler, IItemRenderer {

	public static final RenderLamp instance = new RenderLamp();

	static final int NUM_RENDERS = 1;

	static Icon[] textureCenter = new Icon[2];
	static Icon[] textureFrame = new Icon[NUM_RENDERS];
	static CCModel[] modelCenter = new CCModel[NUM_RENDERS];
	static CCModel[] modelFrame = new CCModel[NUM_RENDERS];

	static {
		TEProps.renderIdLamp = RenderingRegistry.getNextAvailableRenderId();
		RenderingRegistry.registerBlockHandler(instance);

		MinecraftForgeClient.registerItemRenderer(TEBlocks.blockLamp.blockID, instance);

		generateModels();
	}

	public static void initialize() {

		textureCenter[0] = IconRegistry.getIcon("FluidGlowstone");
		textureCenter[1] = IconRegistry.getIcon("LampEffect");

		for (int i = 0; i < NUM_RENDERS; i++) {
			textureFrame[i] = IconRegistry.getIcon("Lamp", i);
		}
	}

	private static void generateModels() {

		double d1 = RenderHelper.RENDER_OFFSET;
		double d2 = 2.0D * d1;

		modelFrame[0] = CCModel.quadModel(24).generateBlock(0, d1, d1, d1, 1 - d1, 1 - d1, 1 - d1).computeNormals()
				.computeLighting(LightModel.standardLightModel);
		modelCenter[0] = CCModel.quadModel(24).generateBlock(0, d2, d2, d2, 1 - d2, 1 - d2, 1 - d2).computeNormals();
	}

	public void renderCenter(int metadata, boolean modified, double x, double y, double z) {

		if (modified) {
			modelCenter[metadata].render(x, y, z, RenderUtils.getIconTransformation(textureCenter[1]));
		} else {
			modelCenter[metadata].render(x, y, z, RenderUtils.getIconTransformation(textureCenter[0]));
		}
	}

	public void renderFrame(int metadata, double x, double y, double z) {

		modelFrame[metadata].render(x, y, z, RenderUtils.getIconTransformation(textureFrame[metadata]));
	}

	/* ISimpleBlockRenderingHandler */
	@Override
	public void renderInventoryBlock(Block block, int metadata, int modelID, RenderBlocks renderer) {

	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {

		TileEntity tile = world.getTileEntity(x, y, z);
		if (!(tile instanceof TileLamp)) {
			return false;
		}
		TileLamp theTile = (TileLamp) tile;
		int bMeta = world.getBlockMetadata(x, y, z);

		RenderUtils.beforeWorldRender(world, x, y, z);

		CCRenderState.setColour(theTile.getColorMultiplier());
		renderCenter(bMeta, theTile.modified, x, y, z);

		CCRenderState.setColour(0xFFFFFFFF);
		renderFrame(bMeta, x, y, z);

		RenderUtils.afterWorldRender(world, x, y, z);

		return true;
	}

	@Override
	public boolean shouldRender3DInInventory() {

		return true;
	}

	@Override
	public int getRenderId() {

		return TEProps.renderIdLamp;
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

		double offset = -0.5;
		if (type == ItemRenderType.EQUIPPED || type == ItemRenderType.EQUIPPED_FIRST_PERSON) {
			offset = 0;
		}
		int metadata = item.getItemDamage();
		boolean modified = false;
		int color = 0xFFFFFFFF;

		if (item.hasTagCompound()) {
			modified = item.getTagCompound().getBoolean("modified");
			color = item.getTagCompound().getInteger("color");
			color = (color << 8) + 0xFF;
		}
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

		RenderHelper.setBlockTextureSheet();
		RenderUtils.preRender();

		CCRenderState.startDrawing(7);
		CCRenderState.setColour(color);
		instance.renderCenter(metadata, modified, offset, offset, offset);
		CCRenderState.draw();

		CCRenderState.startDrawing(7);
		CCRenderState.setColour(0xFFFFFFFF);
		instance.renderFrame(metadata, offset, offset, offset);
		CCRenderState.draw();

		CCRenderState.useNormals(false);
		RenderHelper.setItemTextureSheet();

		GL11.glDisable(GL11.GL_BLEND);
	}

}
