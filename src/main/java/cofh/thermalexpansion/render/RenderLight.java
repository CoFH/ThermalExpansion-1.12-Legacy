package cofh.thermalexpansion.render;

import cofh.core.block.BlockCoFHBase;
import cofh.core.render.IconRegistry;
import cofh.core.render.RenderUtils;
import cofh.lib.render.RenderHelper;
import cofh.repack.codechicken.lib.render.CCModel;
import cofh.repack.codechicken.lib.render.CCRenderState;
import cofh.repack.codechicken.lib.vec.Cuboid6;
import cofh.repack.codechicken.lib.vec.Transformation;
import cofh.repack.codechicken.lib.vec.Vector3;
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

	static final int NUM_STYLES = 16;

	static CCModel[] modelFrame = new CCModel[NUM_STYLES];
	static CCModel[] modelCenter = new CCModel[NUM_STYLES];
	static CCModel[] modelHalo = new CCModel[NUM_STYLES];

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

		final double d1 = RenderHelper.RENDER_OFFSET;
		final double d2 = 2.0D * d1;
		final double d3 = 0.0625D - d1;

		Cuboid6 model = new Cuboid6(Cuboid6.full);
		for (int i = BlockLight.models.length; i-- > 0; ) {
			model.set(BlockLight.models[i]);
			modelFrame[i] = CCModel.quadModel(24).generateBlock(0, model);
			modelFrame[i].computeNormals().shrinkUVs(d1);
			modelCenter[i] = CCModel.quadModel(24).generateBlock(0, model.expand(-d2));
			modelCenter[i].computeNormals();
			modelHalo[i] = CCModel.quadModel(24).generateBlock(0, model.expand(d2 + d3));
			modelHalo[i].computeNormals().shrinkUVs(d3);
		}
	}

	public void renderCenter(int style, int color, boolean modified, Transformation t) {

		modelCenter[style].setColour(color);
		modelCenter[style].render(t, RenderUtils.getIconTransformation(textureCenter[modified ? 1 : 0]));
		modelCenter[style].setColour(0xFFFFFFFF);
	}

	public void renderFrame(int style, int color, int type, Transformation t) {

		modelFrame[style].setColour(color);
		modelFrame[style].render(t, RenderUtils.getIconTransformation(textureFrame[type]));
		modelFrame[style].setColour(0xFFFFFFFF);
	}

	public void renderHalo(int style, int color, Transformation t) {

		modelHalo[style].setColour(color & ~0x80);
		modelHalo[style].render(t, RenderUtils.getIconTransformation(textureHalo));
		modelHalo[style].setColour(0xFFFFFFFF);
	}

	public boolean renderWorldIlluminator(int pass, int style, int color, boolean modified, Transformation t) {

		if (pass == 0) {
			renderFrame(style, -1, 0, t);
			return true;
		}
		renderCenter(style, color, modified, t);

		return true;
	}

	public boolean renderWorldLampLumium(int pass, int style, int color, boolean active, Transformation t) {

		if (pass == 0) {
			renderFrame(style, color, 1, t);
			return true;
		} else if (active) {
			renderHalo(style, color, t);
		}
		return active;
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
		int renderPass = BlockCoFHBase.renderPass;

		int color = theTile.getColorMultiplier();
		boolean active = false;

		int style = theTile.style;
		int alignment = theTile.alignment;

		Transformation pos = BlockLight.getTransformation(style, alignment).with(new Vector3(x, y, z).translation());

		switch (BlockLight.Types.getType(bMeta)) {
		case ILLUMINATOR:
			return renderWorldIlluminator(renderPass, style, color, theTile.modified, pos);
		case LAMP_LUMIUM_RADIANT:
			active = theTile.getInternalLight() > 0;
		case LAMP_LUMIUM:
			return renderWorldLampLumium(renderPass, style, color, active, pos);
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

	public void renderItemIlluminator(int style, int color, boolean modified, Transformation t) {

		renderCenter(style, color, modified, t);
		CCRenderState.draw();

		CCRenderState.startDrawing();
		renderFrame(style, -1, 0, t);
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

		int style = 0;
		int alignment = 0;

		if (item.hasTagCompound()) {
			if (item.stackTagCompound.hasKey("Color")) {
				color = item.getTagCompound().getInteger("Color");
				color = (color << 8) + 0xFF;
				modified = true;
			}
			style = item.stackTagCompound.getByte("Style");
		}

		RenderUtils.preItemRender();
		RenderHelper.setBlockTextureSheet();

		Transformation pos = BlockLight.getTransformation(style, alignment).with(new Vector3(offset, offset, offset).translation());

		CCRenderState.startDrawing();
		switch (BlockLight.Types.getType(metadata)) {
		case ILLUMINATOR:
			renderItemIlluminator(style, color, modified, pos);
			break;
		case LAMP_LUMIUM_RADIANT:
		case LAMP_LUMIUM:
			renderWorldLampLumium(0, style, color, false, pos);
			break;
		}
		CCRenderState.draw();
		RenderUtils.postItemRender();
		GL11.glPopMatrix();
	}

}
