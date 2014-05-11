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

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

import org.lwjgl.opengl.GL11;

import thermalexpansion.block.TEBlocks;
import thermalexpansion.block.tank.BlockTank;
import thermalexpansion.block.tank.TileTank;
import thermalexpansion.core.TEProps;

public class RenderTank implements ISimpleBlockRenderingHandler, IItemRenderer {

	public static final RenderTank instance = new RenderTank();

	static IIcon[] textureTop = new IIcon[BlockTank.Types.values().length * 2];
	static IIcon[] textureBottom = new IIcon[BlockTank.Types.values().length * 2];
	static IIcon[] textureSides = new IIcon[BlockTank.Types.values().length * 2];

	static CCModel[] modelFluid = new CCModel[TileTank.RENDER_LEVELS];
	static CCModel modelFrame = CCModel.quadModel(48);

	static {
		TEProps.renderIdTank = RenderingRegistry.getNextAvailableRenderId();
		RenderingRegistry.registerBlockHandler(instance);

		MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(TEBlocks.blockTank), instance);

		generateFluidModels();

		Cuboid6 box = new Cuboid6(0.125, 0, 0.125, 0.875, 1, 0.875);
		double inset = 0.0625;
		modelFrame = CCModel.quadModel(48).generateBlock(0, box);
		CCModel.generateBackface(modelFrame, 0, modelFrame, 24, 24);
		modelFrame.computeNormals();
		for (int i = 24; i < 48; i++) {
			modelFrame.verts[i].vec.add(modelFrame.normals()[i].copy().multiply(inset));
		}
		modelFrame.computeLighting(LightModel.standardLightModel);
	}

	public static void initialize() {

		for (int i = 0; i < textureSides.length; i++) {
			textureTop[i] = IconRegistry.getIcon("TankTop", i);
			textureBottom[i] = IconRegistry.getIcon("TankBottom", i);
			textureSides[i] = IconRegistry.getIcon("Tank", i);
		}
	}

	private static void generateFluidModels() {

		double minXZ = 0.1875 - RenderHelper.RENDER_OFFSET;
		double maxXZ = 0.8125 + RenderHelper.RENDER_OFFSET;
		double minY = 0.0625 - RenderHelper.RENDER_OFFSET;
		double maxY = 1 - minY;
		double increment = (maxY - minY) / TileTank.RENDER_LEVELS;

		for (int i = 1; i < TileTank.RENDER_LEVELS + 1; i++) {
			double yLevel = minY + increment * i;
			modelFluid[i - 1] = CCModel.quadModel(24).generateBlock(0, minXZ, minY, minXZ, maxXZ, yLevel, maxXZ).computeNormals();
		}
	}

	public void renderFrame(int metadata, int mode, double x, double y, double z) {

		Translation trans = RenderUtils.getRenderVector(x, y, z).translation();

		modelFrame.render(0, 4, trans, RenderUtils.getIconTransformation(textureBottom[2 * metadata + mode]));
		modelFrame.render(24, 4, trans, RenderUtils.getIconTransformation(textureTop[2 * metadata + mode]));
		modelFrame.render(4, 4, trans, RenderUtils.getIconTransformation(textureTop[2 * metadata]));
		modelFrame.render(28, 4, trans, RenderUtils.getIconTransformation(textureBottom[2 * metadata]));

		for (int i = 8; i < 24; i += 4) {
			modelFrame.render(i, 4, trans, RenderUtils.getIconTransformation(textureSides[2 * metadata + mode]));
		}
		for (int i = 32; i < 48; i += 4) {
			modelFrame.render(i, 4, trans, RenderUtils.getIconTransformation(textureSides[2 * metadata + mode]));
		}
	}

	public void renderFluid(int metadata, FluidStack stack, double x, double y, double z) {

		if (stack == null || stack.amount <= 0) {
			return;
		}
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

		CCRenderState.startDrawing();
		Fluid fluid = stack.getFluid();

		RenderUtils.setFluidRenderColor(stack);
		IIcon fluidTex = RenderHelper.getFluidTexture(stack);
		int level = TileTank.RENDER_LEVELS - 1;

		if (fluid.isGaseous(stack)) {
			CCRenderState.setColour(RenderUtils.getFluidRenderColor(stack) << 8 | 32 + 192 * stack.amount / TileTank.CAPACITY[metadata]);
		} else {
			level = Math.min(TileTank.RENDER_LEVELS - 1, stack.amount * TileTank.RENDER_LEVELS / TileTank.CAPACITY[metadata]);
		}
		modelFluid[level].render(x, y, z, RenderUtils.getIconTransformation(fluidTex));
		CCRenderState.draw();
	}

	/* ISimpleBlockRenderingHandler */
	@Override
	public void renderInventoryBlock(Block block, int metadata, int modelID, RenderBlocks renderer) {

	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {

		TileEntity tile = world.getTileEntity(x, y, z);
		if (!(tile instanceof TileTank)) {
			return false;
		}
		TileTank theTile = (TileTank) tile;

		RenderUtils.beforeWorldRender(world, x, y, z);
		if (BlockCoFHBase.renderPass == 0) {
			renderFrame(theTile.type, theTile.mode, x, y, z);
		} else {
			CCRenderState.draw();
			renderFluid(theTile.getBlockMetadata(), theTile.getTankFluid(), x, y, z);
			CCRenderState.startDrawing();
		}

		return true;
	}

	@Override
	public boolean shouldRender3DInInventory(int modelId) {

		return true;
	}

	@Override
	public int getRenderId() {

		return TEProps.renderIdTank;
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
		FluidStack fluid = null;
		if (item.stackTagCompound != null) {
			fluid = FluidStack.loadFluidStackFromNBT(item.stackTagCompound.getCompoundTag("Fluid"));
		}
		RenderUtils.preRender();

		CCRenderState.startDrawing();
		instance.renderFrame(item.getItemDamage(), 0, offset, offset, offset);
		CCRenderState.draw();

		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

		instance.renderFluid(item.getItemDamage(), fluid, offset, offset, offset);

		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glDisable(GL11.GL_BLEND);

		CCRenderState.useNormals = false;
	}

}
