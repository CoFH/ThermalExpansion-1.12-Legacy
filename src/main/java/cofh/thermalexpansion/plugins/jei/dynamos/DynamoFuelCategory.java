package cofh.thermalexpansion.plugins.jei.dynamos;

import cofh.lib.util.helpers.StringHelper;
import cofh.thermalexpansion.gui.client.dynamo.GuiDynamoMagmatic;
import cofh.thermalexpansion.plugins.jei.Drawables;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.*;
import mezz.jei.api.gui.IDrawableAnimated.StartDirection;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.BlankRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public abstract class DynamoFuelCategory<T> extends BlankRecipeCategory<DynamoFuelWrapper<T>> {

	final IDrawableStatic background;
	final IDrawableStatic slotDrawable;

	final IDrawableStatic animatedBackground;
	final IDrawableAnimated animatedOverlay;

	final String uId;
	final String localizedName;

	public DynamoFuelCategory(IGuiHelper guiHelper, String uIdIn, String nameIn) {

		slotDrawable = createSlotDrawable(guiHelper);
		background = createBackground(guiHelper);

		animatedBackground = createAnimationBackground(guiHelper);
		animatedOverlay = createAnimationOverlay(guiHelper);

		uId = uIdIn;
		localizedName = StringHelper.localize(nameIn);
	}

	protected IDrawableStatic createAnimationBackground(IGuiHelper guiHelper) {

		return Drawables.getDrawables(guiHelper).getSpeed(2);
	}

	protected IDrawableAnimated createAnimationOverlay(IGuiHelper guiHelper) {

		return guiHelper.createAnimatedDrawable(Drawables.getDrawables(guiHelper).getSpeedFill(2), 200, StartDirection.TOP, true);
	}

	protected abstract IDrawableStatic createSlotDrawable(IGuiHelper guiHelper);

	protected IDrawableStatic createBackground(IGuiHelper guiHelper) {

		return guiHelper.createBlankDrawable(90, Math.max(slotDrawable.getHeight(), 40));
	}

	public abstract Class<T> getIngredientClass();

	@Nonnull
	@Override
	public IDrawable getBackground() {

		return background;
	}

	@Nonnull
	@Override
	public String getUid() {

		return uId;
	}

	@Nonnull
	@Override
	public String getTitle() {

		return localizedName;
	}

	@Override
	public String toString() {

		return "DynamoFuelCategory{" + "categoryUID='" + uId + '\'' + ", categoryTitle='" + localizedName + '\'' + '}';
	}

	@Override
	public void setRecipe(@Nonnull IRecipeLayout recipeLayout, @Nonnull DynamoFuelWrapper<T> recipeWrapper, @Nonnull IIngredients ingredients) {

		List<List<T>> inputs = ingredients.getInputs(getIngredientClass());
		initSlot(recipeLayout);
		IGuiIngredientGroup<T> ingredientsGroup = recipeLayout.getIngredientsGroup(getIngredientClass());
		ingredientsGroup.set(0, inputs.get(0));
		recipeWrapper.drawX = getSlotX() + slotDrawable.getWidth() + 4 + (animatedOverlay != null ? animatedOverlay.getWidth() + 4 : 0);
	}

	protected abstract void initSlot(IRecipeLayout recipeLayout);

	@Override
	public void drawExtras(Minecraft minecraft) {

		slotDrawable.draw(minecraft, getSlotX(), getSlotY());
		animatedBackground.draw(minecraft, getSlotX() + 4 + slotDrawable.getWidth(), (background.getHeight() - animatedOverlay.getHeight()) / 2);
		animatedOverlay.draw(minecraft, getSlotX() + 4 + slotDrawable.getWidth(), (background.getHeight() - animatedOverlay.getHeight()) / 2);
	}

	protected int getSlotX() {

		return 0;
	}

	protected int getSlotY() {

		return (background.getHeight() - slotDrawable.getHeight()) / 2;
	}

	public static class FuelFluid extends DynamoFuelCategory<FluidStack> {

		public FuelFluid(IGuiHelper helper, String categoryUID, String categoryTitle) {

			super(helper, categoryUID, categoryTitle);
		}

		@Override
		protected IDrawableStatic createSlotDrawable(IGuiHelper guiHelper) {

			return guiHelper.createDrawable(GuiDynamoMagmatic.TEXTURE, 176, 0, 18, 62);
		}

		@Override
		public Class<FluidStack> getIngredientClass() {

			return FluidStack.class;
		}

		@Override
		protected void initSlot(IRecipeLayout recipeLayout) {

			int padding = 1;
			int padding2 = 2 * padding;
			recipeLayout.getFluidStacks().init(0, true, getSlotX() + padding, padding, slotDrawable.getWidth() - padding2, slotDrawable.getHeight() - padding2, 1000, false, getOverlay());
		}

		@Nullable
		private IDrawable getOverlay() {

			return null;
		}
	}

	public static class FuelItem extends DynamoFuelCategory<ItemStack> {

		public FuelItem(IGuiHelper helper, String categoryUID, String categoryTitle) {

			super(helper, categoryUID, categoryTitle);
		}

		@Override
		protected IDrawableStatic createSlotDrawable(IGuiHelper guiHelper) {

			return guiHelper.getSlotDrawable();
		}

		@Override
		public Class<ItemStack> getIngredientClass() {

			return ItemStack.class;
		}

		@Override
		protected void initSlot(IRecipeLayout recipeLayout) {

			recipeLayout.getItemStacks().init(0, true, getSlotX(), getSlotY());
		}
	}

}
