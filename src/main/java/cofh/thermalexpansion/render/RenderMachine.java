package cofh.thermalexpansion.render;

import codechicken.lib.render.CCModel;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;


@SideOnly(Side.CLIENT)
public class RenderMachine //implements ISimpleBlockRenderingHandler, IItemRenderer
{

	public static final RenderMachine instance = new RenderMachine();

	static CCModel model = CCModel.quadModel(24).generateBlock(0, 0, 0, 0, 1, 1, 1).computeNormals();

	/* ISimpleBlockRenderingHandler */
	/*@Override
	public void renderInventoryBlock(Block block, int metadata, int modelId, RenderBlocks renderer) {

	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {

		return true;
	}

	@Override
	public boolean shouldRender3DInInventory(int modelId) {

		return true;
	}

	@Override
	public int getRenderId() {

		return TEProps.renderIdMachine;
	}*/

	/* IItemRenderer */
	/*@Override
	public boolean handleRenderType(ItemStack item, ItemRenderType type) {

		return true;
	}

	@Override
	public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {

		return true;
	}

	@Override
	public void renderItem(ItemRenderType type, ItemStack item, Object... data) {

	}*/

}
