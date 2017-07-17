package cofh.thermalexpansion.render.item;

import codechicken.lib.model.PerspectiveAwareModelProperties;
import codechicken.lib.model.bakery.generation.IItemBakery;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;

import java.util.ArrayList;
import java.util.List;

public class ModelReservoir implements IItemBakery {

	public static final ModelReservoir INSTANCE = new ModelReservoir();

	/* IItemBakery */
	@Override
	public List<BakedQuad> bakeItemQuads(EnumFacing face, ItemStack stack) {

		List<BakedQuad> quads = new ArrayList<>();

		return quads;
	}

	@Override
	public PerspectiveAwareModelProperties getModelProperties(ItemStack stack) {

		return PerspectiveAwareModelProperties.DEFAULT_ITEM;
	}

}
