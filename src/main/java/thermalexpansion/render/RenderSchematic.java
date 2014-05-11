package thermalexpansion.render;

import cofh.CoFHCore;
import cofh.render.RenderItemUtils;
import cofh.util.StringHelper;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;

import org.lwjgl.opengl.GL11;

import thermalexpansion.item.SchematicHelper;
import thermalexpansion.item.TEItems;

public class RenderSchematic implements IItemRenderer {

	public static final RenderSchematic instance = new RenderSchematic();

	ItemStack currentItem;

	@Override
	public boolean handleRenderType(ItemStack item, ItemRenderType type) {

		if (StringHelper.isShiftKeyDown() && type == ItemRenderType.INVENTORY) {
			currentItem = SchematicHelper.getOutput(item, CoFHCore.proxy.getClientPlayer().worldObj);
			if (currentItem != null && !currentItem.getUnlocalizedName().contentEquals(TEItems.diagramSchematic.getUnlocalizedName())) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {

		return false;
	}

	@Override
	public void renderItem(ItemRenderType type, ItemStack item, Object... data) {

		GL11.glPushMatrix();
		net.minecraft.client.renderer.RenderHelper.enableGUIStandardItemLighting();
		RenderItemUtils.renderItemStack(0, 0, 1, currentItem /* SchematicHelper.getOutput(item, CoFHCore.proxy.getClientPlayer().worldObj) */,
				Minecraft.getMinecraft());
		GL11.glPopMatrix();
	}

}
