package cofh.thermalexpansion.render.item;

import codechicken.lib.model.BakedModelProperties;
import codechicken.lib.model.bakedmodels.PerspectiveAwareBakedModel;
import codechicken.lib.model.bakedmodels.PerspectiveAwareOverrideModel;
import codechicken.lib.texture.TextureUtils;
import codechicken.lib.util.TransformUtils;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.minecraft.client.renderer.block.model.*;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.model.*;
import net.minecraftforge.common.model.IModelPart;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.common.model.TRSRTransformation;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public final class ModelFlorb implements IModel, IModelCustomData {

	// minimal Z offset to prevent depth-fighting
	private static final float NORTH_Z_FLUID = 7.498f / 16f;
	private static final float SOUTH_Z_FLUID = 8.502f / 16f;

	public static final ModelFlorb MODEL = new ModelFlorb();
	public static final ModelFlorb MAGMATIC_MODEL = new ModelFlorb(true);

	public static final ModelResourceLocation MODEL_LOCATION = new ModelResourceLocation("thermalexpansion:florb");
	public static final ModelResourceLocation MAGMATIC_MODEL_LOCATION = new ModelResourceLocation("thermalexpansion:florb_magmatic");

	public static final ResourceLocation BASE = new ResourceLocation("thermalexpansion:items/florb/florb");
	public static final ResourceLocation MAGMATIC_BASE = new ResourceLocation("thermalexpansion:items/florb/florb_magmatic");
	public static final ResourceLocation MASK = new ResourceLocation("thermalexpansion:items/florb/florb_mask");
	public static final ResourceLocation OUTLINE = new ResourceLocation("thermalexpansion:items/florb/florb_outline");

	private static final Map<String, IBakedModel> modelCache = new HashMap<String, IBakedModel>();

	private final Fluid fluid;
	private boolean magmatic;

	public ModelFlorb() {

		this(false);
	}

	public ModelFlorb(boolean magmatic) {

		this(null, magmatic);
	}

	public ModelFlorb(Fluid fluid, boolean magmatic) {

		this.fluid = fluid;
		this.magmatic = magmatic;
	}

	@Override
	public Collection<ResourceLocation> getDependencies() {

		return ImmutableList.of();
	}

	@Override
	public Collection<ResourceLocation> getTextures() {

		return ImmutableList.of(MAGMATIC_BASE, BASE, MASK, OUTLINE);
	}

	@Override
	public IBakedModel bake(IModelState state, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter) {

		ImmutableMap<TransformType, TRSRTransformation> transformMap = IPerspectiveAwareModel.MapWrapper.getTransforms(state);

		TRSRTransformation transform = state.apply(Optional.<IModelPart>absent()).or(TRSRTransformation.identity());
		TextureAtlasSprite fluidSprite = null;
		ImmutableList.Builder<BakedQuad> builder = ImmutableList.builder();

		if (fluid != null) {
			fluidSprite = bakedTextureGetter.apply(fluid.getStill());
		}

		// build base (insidest)
		IBakedModel model = (new ItemLayerModel(ImmutableList.of(magmatic ? MAGMATIC_BASE : BASE))).bake(state, format, bakedTextureGetter);
		builder.addAll(model.getQuads(null, null, 0));
		BakedModelProperties properties = new BakedModelProperties(true, false, fluidSprite);

		if (fluidSprite != null) {
			//We have a valid florb, bake liquid.
			TextureAtlasSprite liquid = bakedTextureGetter.apply(MASK);
			builder.addAll(ItemTextureQuadConverter.convertTexture(format, transform, liquid, fluidSprite, NORTH_Z_FLUID, EnumFacing.NORTH, fluid.getColor()));
			builder.addAll(ItemTextureQuadConverter.convertTexture(format, transform, liquid, fluidSprite, SOUTH_Z_FLUID, EnumFacing.SOUTH, fluid.getColor()));
			return new PerspectiveAwareBakedModel(builder.build(), state, properties);
		}
		//Invalid or empty florb, No liquid bake but we need our override handler.
		return new PerspectiveAwareOverrideModel(FlorbOverrideHandler.INSTANCE, state, properties, builder.build());
	}

	@Override
	public IModelState getDefaultState() {

		return TransformUtils.DEFAULT_ITEM;
	}

	/* IModelCustomData */
	@Override
	public ModelFlorb process(ImmutableMap<String, String> customData) {

		Fluid fluid = null;
		if (customData.containsKey("fluid")) {
			String fluidName = customData.get("fluid");
			fluid = FluidRegistry.getFluid(fluidName);
		}
		boolean magmatic = Boolean.parseBoolean(customData.get("magmatic"));

		return new ModelFlorb(fluid, magmatic);
	}

	public static class LoaderFlorb implements ICustomModelLoader {

		public static final LoaderFlorb INSTANCE = new LoaderFlorb();

		@Override
		public boolean accepts(ResourceLocation modelLocation) {

			return modelLocation.getResourceDomain().equals("thermalexpansion") && modelLocation.getResourcePath().startsWith("florb");
		}

		@Override
		public IModel loadModel(ResourceLocation modelLocation) {

			return modelLocation.getResourcePath().equals("florb_magmatic") ? MAGMATIC_MODEL : MODEL;
		}

		@Override
		public void onResourceManagerReload(IResourceManager resourceManager) {

			ModelFlorb.modelCache.clear();
		}
	}

	private static final class FlorbOverrideHandler extends ItemOverrideList {

		public static final FlorbOverrideHandler INSTANCE = new FlorbOverrideHandler();

		private FlorbOverrideHandler() {

			super(ImmutableList.<ItemOverride>of());
		}

		@Override
		public IBakedModel handleItemState(IBakedModel originalModel, ItemStack stack, World world, EntityLivingBase entity) {

			boolean magmatic = stack.getMetadata() != 0;

			String fluidName = "";
			String cacheAppend = "";

			if (stack.getTagCompound() != null) {
				fluidName = stack.getTagCompound().getString("Fluid");
			}

			if (!Strings.isNullOrEmpty(fluidName)) {
				cacheAppend = ":" + fluidName;
			}

			if (!ModelFlorb.modelCache.containsKey(magmatic + cacheAppend)) {
				ModelFlorb parent = magmatic ? ModelFlorb.MAGMATIC_MODEL : ModelFlorb.MODEL;
				Map<String, String> customData = new HashMap<String, String>();
				customData.put("magmatic", String.valueOf(magmatic));
				if (!Strings.isNullOrEmpty(fluidName)) {
					customData.put("fluid", fluidName);
				}
				parent = parent.process(ImmutableMap.copyOf(customData));

				IBakedModel bakedModel = parent.bake(TransformUtils.DEFAULT_ITEM, DefaultVertexFormats.ITEM, TextureUtils.bakedTextureGetter);
				ModelFlorb.modelCache.put(magmatic + cacheAppend, bakedModel);
				return bakedModel;
			}

			return ModelFlorb.modelCache.get(magmatic + cacheAppend);
		}
	}

}
