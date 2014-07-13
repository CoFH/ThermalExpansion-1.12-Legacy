package thermalexpansion.render;

import codechicken.lib.lighting.LightModel;
import codechicken.lib.render.CCModel;
import codechicken.lib.render.CCRenderState;
import codechicken.lib.vec.Cuboid6;
import codechicken.lib.vec.Translation;
import cofh.api.energy.IEnergyContainerItem;
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
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.MinecraftForgeClient;

import org.lwjgl.opengl.GL11;

import thermalexpansion.block.TEBlocks;
import thermalexpansion.block.cell.BlockCell;
import thermalexpansion.block.cell.TileCell;
import thermalexpansion.core.TEProps;

@SideOnly(Side.CLIENT)
public class RenderCell implements ISimpleBlockRenderingHandler, IItemRenderer {

	public static final RenderCell instance = new RenderCell();

	static IIcon[] textureCenter = new IIcon[2];
	static IIcon[] textureFrame = new IIcon[BlockCell.Types.values().length * 2];
	static CCModel modelCenter = CCModel.quadModel(24);
	static CCModel modelFrame = CCModel.quadModel(48);

	static {
		TEProps.renderIdCell = RenderingRegistry.getNextAvailableRenderId();
		RenderingRegistry.registerBlockHandler(instance);

		MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(TEBlocks.blockCell), instance);

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

		textureCenter[0] = IconRegistry.getIcon("StorageRedstone");
		textureCenter[1] = IconRegistry.getIcon("FluidRedstone");

		for (int i = 0; i < textureFrame.length; i++) {
			textureFrame[i] = IconRegistry.getIcon("Cell", i);
		}
	}

	public void renderCenter(int metadata, double x, double y, double z) {

		if (metadata == 1 || metadata == 2) {
			modelCenter.render(x, y, z, RenderUtils.getIconTransformation(textureCenter[0]));
		} else {
			modelCenter.render(x, y, z, RenderUtils.getIconTransformation(textureCenter[1]));
		}
	}

	public void renderFrame(int metadata, TileCell tile, double x, double y, double z) {

		Translation trans = RenderUtils.getRenderVector(x, y, z).translation();
		for (int i = 0; i < 6; i++) {
			modelFrame.render(i * 4, i * 4 + 4, trans, RenderUtils.getIconTransformation(textureFrame[2 * metadata]));
			modelFrame.render(i * 4 + 24, i * 4 + 28, trans, RenderUtils.getIconTransformation(textureFrame[2 * metadata + 1]));
		}
		if (tile != null) {
			for (int i = 0; i < 6; i++) {
				modelFrame.render(i * 4, i * 4 + 4, trans, RenderUtils.getIconTransformation(tile.getTexture(i, 2)));
			}
			int facing = tile.getFacing();
			modelFrame.render(facing * 4, facing * 4 + 4, trans, RenderUtils.getIconTransformation(tile.getTexture(facing, 3)));
		}
	}

	private int getScaledEnergyStored(ItemStack container, int scale) {

		IEnergyContainerItem containerItem = (IEnergyContainerItem) container.getItem();

		return containerItem.getEnergyStored(container) * scale / containerItem.getMaxEnergyStored(container);
	}

	/* ISimpleBlockRenderingHandler */
	@Override
	public void renderInventoryBlock(Block block, int metadata, int modelID, RenderBlocks renderer) {

	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {

		TileEntity tile = world.getTileEntity(x, y, z);
		if (!(tile instanceof TileCell)) {
			return false;
		}
		TileCell theTile = (TileCell) tile;
		int chargeLevel = Math.min(15, theTile.getScaledEnergyStored(16));

		RenderUtils.preWorldRender(world, x, y, z);
		if (BlockCoFHBase.renderPass == 0) {
			renderFrame(theTile.type, theTile, x, y, z);
		} else {
			CCRenderState.setBrightness(165 + chargeLevel * 5);
			renderCenter(theTile.type, x, y, z);
		}
		return true;
	}

	@Override
	public boolean shouldRender3DInInventory(int modelId) {

		return true;
	}

	@Override
	public int getRenderId() {

		return TEProps.renderIdCell;
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
		int chargeLevel = 0;

		if (item.stackTagCompound != null) {
			chargeLevel = Math.min(15, getScaledEnergyStored(item, 16));
		}
		int metadata = item.getItemDamage();
		RenderUtils.preItemRender();

		CCRenderState.startDrawing();
		renderFrame(metadata, null, offset, offset, offset);
		CCRenderState.draw();

		CCRenderState.startDrawing();
		CCRenderState.setBrightness(165 + chargeLevel * 5);
		renderCenter(metadata, offset, offset, offset);
		CCRenderState.draw();

		RenderUtils.postItemRender();
		GL11.glPopMatrix();
	}

}
