package cofh.thermalexpansion.render.item;

import codechicken.lib.model.PerspectiveAwareModelProperties;
import codechicken.lib.model.bakery.ItemModelBakery;
import codechicken.lib.model.blockbakery.IItemBakery;
import codechicken.lib.texture.TextureUtils;
import codechicken.lib.texture.TextureUtils.IIconRegister;
import com.google.common.collect.ImmutableList;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ItemTextureQuadConverter;
import net.minecraftforge.common.model.TRSRTransformation;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.List;

public final class ModelFlorb implements IItemBakery, IIconRegister {

	// minimal Z offset to prevent depth-fighting
	private static final float NORTH_Z_FLUID = 7.498f / 16f;
	private static final float SOUTH_Z_FLUID = 8.502f / 16f;

	public static final ModelFlorb INSTANCE = new ModelFlorb();

	public static TextureAtlasSprite BASE;
	public static TextureAtlasSprite MAGMATIC_BASE;
	public static TextureAtlasSprite MASK;
	public static TextureAtlasSprite OUTLINE;

	@Override
	public List<BakedQuad> bakeItemQuads(EnumFacing face, ItemStack stack) {

		List<BakedQuad> quads = new ArrayList<>();
		if (face == null) {
			boolean magmatic = stack.getMetadata() != 0;
			Fluid fluid = null;

			if (stack.getTagCompound() != null) {
				fluid = FluidRegistry.getFluid(stack.getTagCompound().getString("Fluid"));
			}

			quads.addAll(ItemModelBakery.bakeItem(ImmutableList.of(magmatic ? MAGMATIC_BASE : BASE)));

			if (fluid != null) {
				TextureAtlasSprite fluidSprite = TextureUtils.getTexture(fluid.getStill(new FluidStack(fluid, 1)));
				quads.addAll(ItemTextureQuadConverter.convertTexture(DefaultVertexFormats.ITEM, TRSRTransformation.identity(), MASK, fluidSprite, NORTH_Z_FLUID, EnumFacing.NORTH, fluid.getColor()));
				quads.addAll(ItemTextureQuadConverter.convertTexture(DefaultVertexFormats.ITEM, TRSRTransformation.identity(), MASK, fluidSprite, SOUTH_Z_FLUID, EnumFacing.SOUTH, fluid.getColor()));
			}

		}
		return quads;
	}

	@Override
	public PerspectiveAwareModelProperties getModelProperties(ItemStack stack) {

		return PerspectiveAwareModelProperties.DEFAULT_ITEM;
	}

	@Override
	public void registerIcons(TextureMap textureMap) {

		BASE = textureMap.registerSprite(new ResourceLocation("thermalexpansion:items/florb/florb"));
		MAGMATIC_BASE = textureMap.registerSprite(new ResourceLocation("thermalexpansion:items/florb/florb_magmatic"));
		MASK = textureMap.registerSprite(new ResourceLocation("thermalexpansion:items/florb/florb_mask"));
		OUTLINE = textureMap.registerSprite(new ResourceLocation("thermalexpansion:items/florb/florb_outline"));
	}

}
