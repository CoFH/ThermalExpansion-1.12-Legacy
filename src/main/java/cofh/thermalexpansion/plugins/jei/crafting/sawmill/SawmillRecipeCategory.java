package cofh.thermalexpansion.plugins.jei.crafting.sawmill;

import cofh.lib.util.helpers.StringHelper;
import cofh.thermalexpansion.block.machine.BlockMachine;
import cofh.thermalexpansion.gui.client.machine.GuiSawmill;
import cofh.thermalexpansion.plugins.jei.Drawables;
import cofh.thermalexpansion.plugins.jei.RecipeUidsTE;
import cofh.thermalexpansion.util.managers.machine.SawmillManager;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IDrawableStatic;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.BlankRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class SawmillRecipeCategory extends BlankRecipeCategory<SawmillRecipeWrapper> {

	public static boolean enable = true;

	public static void initialize(IModRegistry registry) {

		if (!enable) {
			return;
		}
		IJeiHelpers jeiHelpers = registry.getJeiHelpers();
		IGuiHelper guiHelper = jeiHelpers.getGuiHelper();

		registry.addRecipeCategories(new SawmillRecipeCategory(guiHelper));
		registry.addRecipeHandlers(new SawmillRecipeHandler());
		registry.addRecipes(getRecipes(guiHelper));
		registry.addRecipeClickArea(GuiSawmill.class, 79, 34, 24, 16, RecipeUidsTE.SAWMILL, RecipeUidsTE.SAWMILL_TAPPER);
		registry.addRecipeCategoryCraftingItem(BlockMachine.machineSawmill, RecipeUidsTE.SAWMILL);

		SawmillRecipeCategoryTapper.initialize(registry);
	}

	public static List<SawmillRecipeWrapper> getRecipes(IGuiHelper guiHelper) {

		List<SawmillRecipeWrapper> recipes = new ArrayList<>();

		for (SawmillManager.RecipeSawmill recipe : SawmillManager.getRecipeList()) {
			recipes.add(new SawmillRecipeWrapper(guiHelper, recipe));
		}
		return recipes;
	}

	IDrawableStatic background;
	IDrawableStatic energyMeter;
	String localizedName;

	public SawmillRecipeCategory(IGuiHelper guiHelper) {

		background = guiHelper.createDrawable(GuiSawmill.TEXTURE, 26, 11, 124, 62, 0, 0, 16, 24);
		energyMeter = Drawables.getDrawables(guiHelper).getEnergyEmpty();
		localizedName = StringHelper.localize("tile.thermalexpansion.machine.sawmill.name");
	}

	@Nonnull
	@Override
	public String getUid() {

		return RecipeUidsTE.SAWMILL;
	}

	@Nonnull
	@Override
	public String getTitle() {

		return localizedName;
	}

	@Override
	public String getModName() {
		return "ThermalExpansion";
	}

	@Nonnull
	@Override
	public IDrawable getBackground() {

		return background;
	}

	@Override
	public void drawExtras(@Nonnull Minecraft minecraft) {

		energyMeter.draw(minecraft, 2, 8);
	}

	@Override
	public void setRecipe(IRecipeLayout recipeLayout, SawmillRecipeWrapper recipeWrapper, IIngredients ingredients) {

		List<List<ItemStack>> inputs = ingredients.getInputs(ItemStack.class);
		List<List<ItemStack>> outputs = ingredients.getOutputs(ItemStack.class);

		IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();

		guiItemStacks.init(0, true, 42, 14);
		guiItemStacks.init(1, false, 105, 14);

		guiItemStacks.set(0, inputs.get(0));
		guiItemStacks.set(1, outputs.get(0));

		if (outputs.size() > 1) {
			guiItemStacks.init(2, false, 105, 41);
			guiItemStacks.set(2, outputs.get(1));

			guiItemStacks.addTooltipCallback((slotIndex, input, ingredient, tooltip) -> {

				if (slotIndex == 2) {
					tooltip.add(StringHelper.localize("info.cofh.chance") + ": " + recipeWrapper.chance + "%");
				}
			});
		}
	}

}
