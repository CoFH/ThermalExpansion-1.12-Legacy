package thermalexpansion.render;

import cofh.render.RenderHelper;
import cofh.render.RenderUtils;
import cofh.repack.codechicken.lib.lighting.LightModel;
import cofh.repack.codechicken.lib.render.CCModel;
import cofh.repack.codechicken.lib.render.CCRenderState;
import cofh.repack.codechicken.lib.vec.Cuboid6;
import cofh.repack.codechicken.lib.vec.Translation;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.client.registry.RenderingRegistry;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.MinecraftForgeClient;

import org.lwjgl.opengl.GL11;

import thermalexpansion.block.TEBlocks;
import thermalexpansion.block.simple.BlockFrame;
import thermalexpansion.core.TEProps;

public class RenderFrame implements ISimpleBlockRenderingHandler, IItemRenderer {

	public static final RenderFrame instance = new RenderFrame();

	static CCModel modelCenter = CCModel.quadModel(24);
	static CCModel modelFrame = CCModel.quadModel(48);

	static {
		TEProps.renderIdFrame = RenderingRegistry.getNextAvailableRenderId();
		RenderingRegistry.registerBlockHandler(instance);

		MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(TEBlocks.blockFrame), instance);

		modelCenter.generateBlock(0, 0.15, 0.15, 0.15, 0.85, 0.85, 0.85).computeNormals();

		Cuboid6 box = new Cuboid6(0, 0, 0, 1, 1, 1);
		double inset = 0.1875;
		modelFrame = CCModel.quadModel(48).generateBlock(0, box);
		CCModel.generateBackface(modelFrame, 0, modelFrame, 24, 24);
		modelFrame.computeNormals();
		for (int i = 24; i < 48; i++) {
			modelFrame.verts[i].vec.add(modelFrame.normals()[i].copy().multiply(inset));
		}
		modelFrame.computeLighting(LightModel.standardLightModel).shrinkUVs(RenderHelper.RENDER_OFFSET);
	}

	public static void initialize() {

	}

	public void renderCenter(Block block, int metadata, double x, double y, double z) {

		modelCenter.render(x, y, z, RenderUtils.getIconTransformation(block.getIcon(7, metadata)));
	}

	public void renderFrame(Block block, int metadata, double x, double y, double z) {

		Translation trans = RenderUtils.getRenderVector(x, y, z).translation();
		for (int i = 0; i < 6; i++) {
			modelFrame.render(i * 4, i * 4 + 4, trans, RenderUtils.getIconTransformation(block.getIcon(i, metadata)));
			modelFrame.render(i * 4 + 24, i * 4 + 28, trans, RenderUtils.getIconTransformation(block.getIcon(6, metadata)));
		}
	}

	/* ISimpleBlockRenderingHandler */
	@Override
	public void renderInventoryBlock(Block block, int metadata, int modelId, RenderBlocks renderer) {

	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {

		int metadata = world.getBlockMetadata(x, y, z);

		RenderUtils.preWorldRender(world, x, y, z);
		if (BlockFrame.renderPass == 0) {
			renderFrame(block, metadata, x, y, z);
		} else {
			renderCenter(block, metadata, x, y, z);
		}
		return true;
	}

	@Override
	public boolean shouldRender3DInInventory(int modelId) {

		return true;
	}

	@Override
	public int getRenderId() {

		return TEProps.renderIdFrame;
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
		Block block = Block.getBlockFromItem(item.getItem());
		int metadata = item.getItemDamage();
		RenderUtils.preItemRender();

		CCRenderState.startDrawing();
		renderFrame(block, metadata, offset, offset, offset);
		renderCenter(block, metadata, offset, offset, offset);
		CCRenderState.draw();

		RenderUtils.postItemRender();
		GL11.glPopMatrix();
	}

}
