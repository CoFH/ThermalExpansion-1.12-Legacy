package cofh.thermalexpansion.render;

import cofh.core.block.BlockCoFHBase;
import cofh.core.render.IconRegistry;
import cofh.core.render.RenderUtils;
import cofh.lib.render.RenderHelper;
import cofh.repack.codechicken.lib.render.CCModel;
import cofh.repack.codechicken.lib.render.CCRenderState;
import cofh.thermalexpansion.block.TEBlocks;
import cofh.thermalexpansion.block.light.BlockLight;
import cofh.thermalexpansion.block.light.TileLight;
import cofh.thermalexpansion.core.TEProps;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.client.registry.RenderingRegistry;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.MinecraftForgeClient;

import org.lwjgl.opengl.GL11;

public class RenderLight implements ISimpleBlockRenderingHandler, IItemRenderer {

	public static final RenderLight instance = new RenderLight();

	static final int NUM_RENDERS = 2;

	static IIcon[] textureFrame = new IIcon[NUM_RENDERS];
	static IIcon[] textureCenter = new IIcon[2];
	static IIcon textureHalo;

	static CCModel[] modelFrame = new CCModel[NUM_RENDERS];
	static CCModel[] modelCenter = new CCModel[NUM_RENDERS];
	static CCModel[] modelHalo = new CCModel[NUM_RENDERS];

	static {
		TEProps.renderIdLight = RenderingRegistry.getNextAvailableRenderId();
		RenderingRegistry.registerBlockHandler(instance);

		MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(TEBlocks.blockLight), instance);

		generateModels();
	}

	public static void initialize() {

		for (int i = 0; i < NUM_RENDERS; i++) {
			textureFrame[i] = IconRegistry.getIcon("Light", i);
		}
		textureCenter[0] = IconRegistry.getIcon("FluidGlowstone");
		textureCenter[1] = IconRegistry.getIcon("LightEffect");
		textureHalo = IconRegistry.getIcon("LightHalo");
	}

	private static void generateModels() {

		double d1 = RenderHelper.RENDER_OFFSET;
		double d2 = 2.0D * d1;
		double d3 = 0.0625D;

		modelFrame[0] = CCModel.quadModel(24).generateBlock(0, d1, d1, d1, 1 - d1, 1 - d1, 1 - d1).computeNormals();
		modelFrame[1] = CCModel.quadModel(24).generateBlock(0, d1, d1, d1, 1 - d1, 1 - d1, 1 - d1).computeNormals();
		modelCenter[0] = CCModel.quadModel(24).generateBlock(0, d2, d2, d2, 1 - d2, 1 - d2, 1 - d2).computeNormals();
		modelHalo[1] = CCModel.quadModel(24).generateBlock(0, -d3, -d3, -d3, 1 + d3, 1 + d3, 1 + d3).computeNormals().shrinkUVs(d3);
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

	public void renderHalo(int metadata, double x, double y, double z) {

		modelHalo[metadata].render(x, y, z, RenderUtils.getIconTransformation(textureHalo));
	}

	public boolean renderWorldIlluminator(int color, boolean modified, double x, double y, double z) {

		if (BlockCoFHBase.renderPass == 0) {
			return false;
		}
		modelCenter[0].setColour(color);
		renderCenter(0, modified, x, y, z);
		modelCenter[0].setColour(0xFFFFFFFF);

		renderFrame(0, x, y, z);
		return true;
	}

	public boolean renderWorldLampHalo(int color, boolean active, double x, double y, double z) {

		if (BlockCoFHBase.renderPass == 0) {
			modelFrame[1].setColour(color);
			renderFrame(1, x, y, z);
			modelFrame[1].setColour(0xFFFFFFFF);
			return true;
		} else if (active) {
			modelHalo[1].setColour(color - 0x80);
			renderHalo(1, x, y, z);
			modelHalo[1].setColour(0xFFFFFFFF);
		}
		return active;
	}

	public boolean renderWorldLampLumium(int color, boolean active, double x, double y, double z) {

		if (BlockCoFHBase.renderPass == 0) {
			modelFrame[1].setColour(color);
			renderFrame(1, x, y, z);
			modelFrame[1].setColour(0xFFFFFFFF);
			return true;
		}
		return BlockCoFHBase.renderPass == 0;
	}

	public void renderItemIlluminator(int color, boolean modified, double offset) {

		CCRenderState.startDrawing();
		modelCenter[0].setColour(color);
		renderCenter(0, modified, offset, offset, offset);
		modelCenter[0].setColour(0xFFFFFFFF);
		CCRenderState.draw();

		CCRenderState.startDrawing();
		renderFrame(0, offset, offset, offset);
		CCRenderState.draw();
	}

	public void renderItemLampLumium(int color, double offset) {

		CCRenderState.startDrawing();
		modelFrame[1].setColour(color);
		renderFrame(1, offset, offset, offset);
		modelFrame[1].setColour(0xFFFFFFFF);
		CCRenderState.draw();
	}

	/* ISimpleBlockRenderingHandler */
	@Override
	public void renderInventoryBlock(Block block, int metadata, int modelID, RenderBlocks renderer) {

	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {

		TileEntity tile = world.getTileEntity(x, y, z);
		if (!(tile instanceof TileLight)) {
			return false;
		}
		TileLight theTile = (TileLight) tile;
		int bMeta = world.getBlockMetadata(x, y, z);

		RenderUtils.preWorldRender(world, x, y, z);

		int color = theTile.getColorMultiplier();
		boolean modified = theTile.modified;
		boolean active = theTile.getInternalLight() > 0;

		switch (BlockLight.Types.getType(bMeta)) {
		case ILLUMINATOR:
			return renderWorldIlluminator(color, modified, x, y, z);
		case LAMP_LUMIUM_RADIANT:
			return renderWorldLampHalo(color, active, x, y, z);
		case LAMP_LUMIUM:
			return renderWorldLampLumium(color, active, x, y, z);
		}
		return false;
	}

	@Override
	public boolean shouldRender3DInInventory(int modelId) {

		return false;
	}

	@Override
	public int getRenderId() {

		return TEProps.renderIdLight;
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
		}
		int metadata = item.getItemDamage();
		int color = 0xFFFFFFFF;
		boolean modified = false;

		if (item.hasTagCompound() && item.stackTagCompound.hasKey("Color")) {
			color = item.getTagCompound().getInteger("Color");
			color = (color << 8) + 0xFF;
			modified = true;
		}

		RenderUtils.preItemRender();
		RenderHelper.setBlockTextureSheet();

		switch (BlockLight.Types.getType(metadata)) {
		case ILLUMINATOR:
			renderItemIlluminator(color, modified, offset);
			break;
		case LAMP_LUMIUM_RADIANT:
		case LAMP_LUMIUM:
			renderItemLampLumium(color, offset);
			break;
		}
		RenderUtils.postItemRender();
		GL11.glPopMatrix();
	}

}
