package cofh.thermalexpansion.render.item;

import codechicken.lib.model.ItemQuadBakery;
import codechicken.lib.model.bakedmodels.ModelProperties.PerspectiveProperties;
import codechicken.lib.model.bakery.generation.IItemBakery;
import cofh.core.render.TextureHelper;
import cofh.thermalexpansion.init.TETextures;
import com.google.common.collect.ImmutableList;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.client.model.ItemTextureQuadConverter;
import net.minecraftforge.common.model.TRSRTransformation;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.List;

public final class ModelFlorb implements IItemBakery {

	// minimal Z offset to prevent depth-fighting
	private static final float NORTH_Z_FLUID = 7.498f / 16f;
	private static final float SOUTH_Z_FLUID = 8.502f / 16f;

	public static final ModelFlorb INSTANCE = new ModelFlorb();

	/* IItemBakery */
	@Override
	public List<BakedQuad> bakeItemQuads(EnumFacing face, ItemStack stack) {

		List<BakedQuad> quads = new ArrayList<>();
		if (face == null) {
			boolean magmatic = stack.getMetadata() != 0;
			Fluid fluid = null;

			if (stack.getTagCompound() != null) {
				fluid = FluidRegistry.getFluid(stack.getTagCompound().getString("Fluid"));
			}
			quads.addAll(ItemQuadBakery.bakeItem(ImmutableList.of(magmatic ? TETextures.FLORB_MAGMATIC : TETextures.FLORB_STANDARD)));

			if (fluid != null) {
				TextureAtlasSprite fluidSprite = TextureHelper.getTexture(fluid.getStill(new FluidStack(fluid, 1)));
				quads.addAll(ItemTextureQuadConverter.convertTexture(DefaultVertexFormats.ITEM, TRSRTransformation.identity(), TETextures.FLORB_MASK, fluidSprite, NORTH_Z_FLUID, EnumFacing.NORTH, fluid.getColor()));
				quads.addAll(ItemTextureQuadConverter.convertTexture(DefaultVertexFormats.ITEM, TRSRTransformation.identity(), TETextures.FLORB_MASK, fluidSprite, SOUTH_Z_FLUID, EnumFacing.SOUTH, fluid.getColor()));
			}

		}
		return quads;
	}

	@Override
	public PerspectiveProperties getModelProperties(ItemStack stack) {

		return PerspectiveProperties.DEFAULT_ITEM;
	}

}
