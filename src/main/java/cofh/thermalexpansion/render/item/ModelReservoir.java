package cofh.thermalexpansion.render.item;

import codechicken.lib.model.ItemQuadBakery;
import codechicken.lib.model.bakedmodels.ModelProperties.PerspectiveProperties;
import codechicken.lib.model.bakery.generation.IItemBakery;
import codechicken.lib.texture.TextureUtils;
import cofh.core.init.CoreProps;
import cofh.core.item.ItemMulti;
import cofh.core.render.TextureHelper;
import cofh.core.util.helpers.ColorHelper;
import cofh.thermalexpansion.init.TEItems;
import cofh.thermalexpansion.init.TETextures;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.client.model.ItemTextureQuadConverter;
import net.minecraftforge.common.model.TRSRTransformation;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class ModelReservoir implements IItemBakery {

	// minimal Z offset to prevent depth-fighting
	private static final float NORTH_Z_FLUID = 7.498f / 16f;
	private static final float SOUTH_Z_FLUID = 8.502f / 16f;

	public static final ModelReservoir INSTANCE = new ModelReservoir();

	/* IItemBakery */
	@Override
	public List<BakedQuad> bakeItemQuads(EnumFacing face, ItemStack stack) {

		List<BakedQuad> quads = new ArrayList<>();
		if (face == null) {
			int index = ItemMulti.isCreative(stack) ? 5 : stack.getMetadata();
			boolean hasColor = ColorHelper.hasColor0(stack);
			boolean active = TEItems.itemReservoir.isActive(stack);
			int mode = TEItems.itemReservoir.getMode(stack);

			// Insertion order matters, index is used for the quad's tint index.
			List<TextureAtlasSprite> sprites = new LinkedList<>();
			// Safe guard metadata from the item, could cause weird crashes.
			sprites.add(index >= TETextures.RESERVOIR.length ? TextureUtils.getMissingSprite() : TETextures.RESERVOIR[index]);
			sprites.add(TETextures.RESERVOIR_MODE[((active ? 1 : 0) << 1) | mode]);
			if (hasColor) {
				sprites.add(TETextures.RESERVOIR_COLOR_0);
			}
			quads.addAll(ItemQuadBakery.bakeItem(sprites));

			if (stack.hasTagCompound() && stack.getTagCompound().hasKey(CoreProps.FLUID)) {
				FluidStack fluid_stack = FluidStack.loadFluidStackFromNBT(stack.getTagCompound().getCompoundTag(CoreProps.FLUID));
				if (fluid_stack != null) {
					Fluid fluid = fluid_stack.getFluid();
					TextureAtlasSprite fluidSprite = TextureHelper.getTexture(fluid.getStill(fluid_stack));
					quads.addAll(ItemTextureQuadConverter.convertTexture(DefaultVertexFormats.ITEM, TRSRTransformation.identity(), TETextures.RESERVOIR_MASK, fluidSprite, NORTH_Z_FLUID, EnumFacing.NORTH, fluid.getColor(fluid_stack)));
					quads.addAll(ItemTextureQuadConverter.convertTexture(DefaultVertexFormats.ITEM, TRSRTransformation.identity(), TETextures.RESERVOIR_MASK, fluidSprite, SOUTH_Z_FLUID, EnumFacing.SOUTH, fluid.getColor(fluid_stack)));
				}
			}

		}
		return quads;
	}

	@Override
	public PerspectiveProperties getModelProperties(ItemStack stack) {

		return PerspectiveProperties.DEFAULT_ITEM;
	}

}
