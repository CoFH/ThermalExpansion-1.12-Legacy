package cofh.thermalexpansion.plugins.jei.dynamos;

import cofh.thermalexpansion.gui.client.dynamo.GuiDynamoMagmatic;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.*;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.BlankRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public abstract class DynamoFuelCategory<T> extends BlankRecipeCategory<DynamoFuelWrapper<T>> {

	final IDrawable background;
	final IDrawable slotDrawable;
	final String categoryUID;
	final String categoryTitle;
	@Nullable
	final IDrawable flameDrawable;

	public DynamoFuelCategory(IGuiHelper helper, String categoryUID, String categoryTitle) {
		slotDrawable = createSlotDrawable(helper);
		background = createBackground(helper);
		flameDrawable = createAnimatedFlame(helper);

		this.categoryUID = categoryUID;
		this.categoryTitle = categoryTitle;
	}

	@Nullable
	protected IDrawable createAnimatedFlame(IGuiHelper guiHelper){
		ResourceLocation furnaceBackgroundLocation = new ResourceLocation("minecraft", "textures/gui/container/furnace.png");
		IDrawableStatic flameDrawable = guiHelper.createDrawable(furnaceBackgroundLocation, 176, 0, 14, 14);
		return guiHelper.createAnimatedDrawable(flameDrawable, 60, IDrawableAnimated.StartDirection.TOP, true);
	}

	protected abstract IDrawable createSlotDrawable(IGuiHelper helper);

	protected IDrawable createBackground(IGuiHelper helper) {
		return helper.createBlankDrawable(90, Math.max(slotDrawable.getHeight(), 40));
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
		return categoryUID;
	}

	@Nonnull
	@Override
	public String getTitle() {
		return categoryTitle;
	}

	@Override
	public String toString() {
		return "DynamoFuelCategory{" +
				"categoryUID='" + categoryUID + '\'' +
				", categoryTitle='" + categoryTitle + '\'' +
				'}';
	}

	@Override
	public void setRecipe(@Nonnull IRecipeLayout recipeLayout, @Nonnull DynamoFuelWrapper<T> recipeWrapper, @Nonnull IIngredients ingredients) {
		List<List<T>> inputs = ingredients.getInputs(getIngredientClass());
		initSlot(recipeLayout);
		IGuiIngredientGroup<T> ingredientsGroup = recipeLayout.getIngredientsGroup(getIngredientClass());
		ingredientsGroup.set(0, inputs.get(0));
		recipeWrapper.drawX = getSlotX() + slotDrawable.getWidth() + 4 + (flameDrawable != null ? flameDrawable.getWidth() + 4 : 0) ;
	}

	protected abstract void initSlot(IRecipeLayout recipeLayout);

	@Override
	public void drawExtras(Minecraft minecraft) {
		slotDrawable.draw(minecraft, getSlotX(), getSlotY());
		if (flameDrawable != null) {
			flameDrawable.draw(minecraft, getSlotX() + 4 + slotDrawable.getWidth(), (background.getHeight() - flameDrawable.getHeight()) / 2);
		}
	}

	protected int getSlotX() {
		return 0;
	}
	protected int getSlotY(){
		return (background.getHeight() - slotDrawable.getHeight()) / 2;
	}

	public static class FuelFluid extends DynamoFuelCategory<FluidStack> {

		public FuelFluid(IGuiHelper helper, String categoryUID, String categoryTitle) {
			super(helper, categoryUID, categoryTitle);
		}

		@Override
		protected IDrawable createSlotDrawable(IGuiHelper helper) {
			return helper.createDrawable(GuiDynamoMagmatic.TEXTURE, 176, 0, 18, 62);
		}

		@Override
		public Class<FluidStack> getIngredientClass() {
			return FluidStack.class;
		}

		@Override
		protected void initSlot(IRecipeLayout recipeLayout) {
			int padding = 1;
			int padding2 = 2 * padding;
			recipeLayout.getFluidStacks().init(0, true,
					getSlotX() + padding, padding, slotDrawable.getWidth()- padding2, slotDrawable.getHeight()- padding2,
					1000, false, getOverlay());
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
		protected IDrawable createSlotDrawable(IGuiHelper helper) {
			return helper.getSlotDrawable();
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
