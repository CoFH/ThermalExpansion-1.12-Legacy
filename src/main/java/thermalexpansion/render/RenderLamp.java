package thermalexpansion.render;

import codechicken.lib.render.CCModel;
import codechicken.lib.render.CCRenderState;
import cofh.block.BlockCoFHBase;
import cofh.render.IconRegistry;
import cofh.render.RenderHelper;
import cofh.render.RenderUtils;
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

import thermalexpansion.block.TEBlocks;
import thermalexpansion.block.lamp.TileLamp;
import thermalexpansion.core.TEProps;

public class RenderLamp implements ISimpleBlockRenderingHandler, IItemRenderer {

	public static final RenderLamp instance = new RenderLamp();

	static final int NUM_RENDERS = 1;

	static IIcon[] textureBase = new IIcon[NUM_RENDERS];
	static IIcon textureHalo;
	static CCModel[] modelBase = new CCModel[NUM_RENDERS];
	static CCModel[] modelHalo = new CCModel[NUM_RENDERS];

	static {
		TEProps.renderIdLamp = RenderingRegistry.getNextAvailableRenderId();
		RenderingRegistry.registerBlockHandler(instance);

		MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(TEBlocks.blockLamp), instance);

		generateModels();
	}

	public static void initialize() {

		for (int i = 0; i < NUM_RENDERS; i++) {
			textureBase[i] = IconRegistry.getIcon("Lamp", i);
		}
		textureHalo = IconRegistry.getIcon("LampHalo");
	}

	private static void generateModels() {

		double d1 = RenderHelper.RENDER_OFFSET;
		double d3 = 0.0625D;

		modelBase[0] = CCModel.quadModel(24).generateBlock(0, d1, d1, d1, 1 - d1, 1 - d1, 1 - d1).computeNormals();
		modelHalo[0] = CCModel.quadModel(24).generateBlock(0, -d3, -d3, -d3, 1 + d3, 1 + d3, 1 + d3).shrinkUVs(d3).computeNormals();
	}

	public void renderBase(int metadata, double x, double y, double z) {

		modelBase[metadata].render(x, y, z, RenderUtils.getIconTransformation(textureBase[metadata]));
	}

	public void renderHalo(int metadata, double x, double y, double z) {

		modelHalo[metadata].render(x, y, z, RenderUtils.getIconTransformation(textureHalo));
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

		RenderUtils.preWorldRender(world, x, y, z);
		if (BlockCoFHBase.renderPass == 0) {
			modelBase[bMeta].setColour(0);
			renderBase(bMeta, x, y, z);
			return true;
		} else if (theTile.getLightValue() > 0) {
			renderHalo(bMeta, x, y, z);
		}
		return theTile.getLightValue() > 0;
	}

	@Override
	public boolean shouldRender3DInInventory(int modelId) {

		return false;
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

		GL11.glPushMatrix();
		double offset = -0.5;
		if (type == ItemRenderType.EQUIPPED || type == ItemRenderType.EQUIPPED_FIRST_PERSON) {
			offset = 0;
		}
		int metadata = item.getItemDamage();
		int color = 0xFFFFFFFF;

		if (item.hasTagCompound()) {
			color = item.getTagCompound().getInteger("Color");
			color = (color << 8) + 0xFF;
		}
		RenderUtils.preItemRender();
		RenderHelper.setBlockTextureSheet();

		CCRenderState.setColour(color);
		CCRenderState.startDrawing();
		instance.renderBase(metadata, offset, offset, offset);
		CCRenderState.draw();

		RenderUtils.postItemRender();
		GL11.glPopMatrix();
	}

}
