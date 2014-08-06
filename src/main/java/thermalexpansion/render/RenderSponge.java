package thermalexpansion.render;

import cofh.core.render.IconRegistry;
import cofh.lib.render.RenderHelper;

import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.MinecraftForgeClient;

import thermalexpansion.block.TEBlocks;

public class RenderSponge implements IItemRenderer {

	public static final RenderSponge instance = new RenderSponge();

	static {
		MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(TEBlocks.blockSponge), instance);
	}

	public static void initialize() {

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
		IIcon texture;

		if (item.stackTagCompound == null || !item.stackTagCompound.hasKey("Fluid")) {
			texture = IconRegistry.getIcon("Sponge", item.getItemDamage());
		} else {
			texture = IconRegistry.getIcon("Sponge", item.getItemDamage() + 8);
		}
		RenderHelper.renderTextureAsBlock((RenderBlocks) data[0], texture, offset, offset, offset);
	}

}
