package cofh.thermalexpansion.plugins.jei.crafting.smelter;

import cofh.core.util.helpers.StringHelper;
import cofh.thermalexpansion.block.machine.BlockMachine;
import cofh.thermalexpansion.gui.client.machine.GuiSmelter;
import cofh.thermalexpansion.plugins.jei.Drawables;
import cofh.thermalexpansion.plugins.jei.RecipeUidsTE;
import cofh.thermalexpansion.plugins.jei.crafting.BaseRecipeCategory;
import cofh.thermalexpansion.util.managers.machine.SmelterManager;
import cofh.thermalexpansion.util.managers.machine.SmelterManager.SmelterRecipe;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.gui.IDrawableStatic;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeCategoryRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class SmelterRecipeCategory extends BaseRecipeCategory<SmelterRecipeWrapper> {

	public static boolean enable = true;

	public static void register(IRecipeCategoryRegistration registry) {

		if (!enable) {
			return;
		}
		IJeiHelpers jeiHelpers = registry.getJeiHelpers();
		IGuiHelper guiHelper = jeiHelpers.getGuiHelper();

		registry.addRecipeCategories(new SmelterRecipeCategory(guiHelper));
		registry.addRecipeCategories(new SmelterRecipeCategoryPyrotheum(guiHelper));
	}

	public static void initialize(IModRegistry registry) {

		if (!enable) {
			return;
		}
		IJeiHelpers jeiHelpers = registry.getJeiHelpers();
		IGuiHelper guiHelper = jeiHelpers.getGuiHelper();

		registry.addRecipes(getRecipes(guiHelper), RecipeUidsTE.SMELTER);
		registry.addRecipeClickArea(GuiSmelter.class, 79, 34, 24, 16, RecipeUidsTE.SMELTER, RecipeUidsTE.SMELTER_PYROTHEUM);
		registry.addRecipeCatalyst(BlockMachine.machineSmelter, RecipeUidsTE.SMELTER);

		SmelterRecipeCategoryPyrotheum.initialize(registry);
	}

	public static List<SmelterRecipeWrapper> getRecipes(IGuiHelper guiHelper) {

		List<SmelterRecipeWrapper> recipes = new ArrayList<>();

		for (SmelterRecipe recipe : SmelterManager.getRecipeList()) {
			recipes.add(new SmelterRecipeWrapper(guiHelper, recipe));
		}
		return recipes;
	}

	final IDrawableStatic progress;
	final IDrawableStatic speed;

	public SmelterRecipeCategory(IGuiHelper guiHelper) {

		background = guiHelper.createDrawable(GuiSmelter.TEXTURE, 26, 11, 124, 62, 0, 0, 16, 24);
		energyMeter = Drawables.getDrawables(guiHelper).getEnergyEmpty();
		localizedName = StringHelper.localize("tile.thermalexpansion.machine.smelter.name");

		progress = Drawables.getDrawables(guiHelper).getProgress(Drawables.PROGRESS_ARROW);
		speed = Drawables.getDrawables(guiHelper).getScale(Drawables.SCALE_FLAME);
	}

	@Nonnull
	@Override
	public String getUid() {

		return RecipeUidsTE.SMELTER;
	}

	@Override
	public void drawExtras(@Nonnull Minecraft minecraft) {

		progress.draw(minecraft, 69, 23);
		speed.draw(minecraft, 34, 33);
		energyMeter.draw(minecraft, 2, 8);
	}

	@Override
	public void setRecipe(IRecipeLayout recipeLayout, SmelterRecipeWrapper recipeWrapper, IIngredients ingredients) {

		List<List<ItemStack>> inputs = ingredients.getInputs(ItemStack.class);
		List<List<ItemStack>> outputs = ingredients.getOutputs(ItemStack.class);

		IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();

		guiItemStacks.init(0, true, 21, 14);
		guiItemStacks.init(1, true, 45, 14);
		guiItemStacks.init(2, false, 105, 14);

		guiItemStacks.set(0, inputs.get(0));
		guiItemStacks.set(1, inputs.get(1));
		guiItemStacks.set(2, outputs.get(0));

		if (outputs.size() > 1) {
			guiItemStacks.init(3, false, 105, 41);
			guiItemStacks.set(3, outputs.get(1));

			guiItemStacks.addTooltipCallback((slotIndex, input, ingredient, tooltip) -> {

				if (slotIndex == 3) {
					tooltip.add(StringHelper.localize("info.cofh.chance") + ": " + recipeWrapper.chance + "%");
				}
			});
		}
	}

}
