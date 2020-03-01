package cofh.thermalexpansion.plugins.jei.machine.pulverizer;

import cofh.core.util.helpers.StringHelper;
import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalexpansion.block.machine.BlockMachine;
import cofh.thermalexpansion.gui.client.machine.GuiPulverizer;
import cofh.thermalexpansion.plugins.jei.Drawables;
import cofh.thermalexpansion.plugins.jei.RecipeUidsTE;
import cofh.thermalexpansion.plugins.jei.machine.BaseRecipeCategory;
import cofh.thermalexpansion.util.managers.machine.PulverizerManager;
import cofh.thermalexpansion.util.managers.machine.PulverizerManager.PulverizerRecipe;
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

public class PulverizerRecipeCategory extends BaseRecipeCategory<PulverizerRecipeWrapper> {

	public static boolean enable = true;

	public static void register(IRecipeCategoryRegistration registry) {

		String category = "Plugins.JEI";
		enable = ThermalExpansion.CONFIG_CLIENT.get(category, "Machine.Pulverizer", enable);

		if (!enable) {
			return;
		}
		IJeiHelpers jeiHelpers = registry.getJeiHelpers();
		IGuiHelper guiHelper = jeiHelpers.getGuiHelper();

		registry.addRecipeCategories(new PulverizerRecipeCategory(guiHelper));
		registry.addRecipeCategories(new PulverizerRecipeCategoryPetrotheum(guiHelper));
	}

	public static void initialize(IModRegistry registry) {

		if (!enable) {
			return;
		}
		try {
			IJeiHelpers jeiHelpers = registry.getJeiHelpers();
			IGuiHelper guiHelper = jeiHelpers.getGuiHelper();

			registry.addRecipes(getRecipes(guiHelper), RecipeUidsTE.PULVERIZER);
			registry.addRecipeClickArea(GuiPulverizer.class, 79, 34, 24, 16, RecipeUidsTE.PULVERIZER, RecipeUidsTE.PULVERIZER_PETROTHEUM);
			registry.addRecipeCatalyst(BlockMachine.machinePulverizer, RecipeUidsTE.PULVERIZER);

			PulverizerRecipeCategoryPetrotheum.initialize(registry);
		} catch (Throwable t) {
			ThermalExpansion.LOG.error("Bad/null recipe!", t);
		}
	}

	public static List<PulverizerRecipeWrapper> getRecipes(IGuiHelper guiHelper) {

		List<PulverizerRecipeWrapper> recipes = new ArrayList<>();

		for (PulverizerRecipe recipe : PulverizerManager.getRecipeList()) {
			recipes.add(new PulverizerRecipeWrapper(guiHelper, recipe));
		}
		return recipes;
	}

	protected IDrawableStatic progress;
	protected IDrawableStatic speed;

	public PulverizerRecipeCategory(IGuiHelper guiHelper) {

		background = guiHelper.createDrawable(GuiPulverizer.TEXTURE, 26, 11, 124, 62, 0, 0, 16, 24);
		energyMeter = Drawables.getDrawables(guiHelper).getEnergyEmpty();
		localizedName = StringHelper.localize("tile.thermalexpansion.machine.pulverizer.name");

		progress = Drawables.getDrawables(guiHelper).getProgress(Drawables.PROGRESS_ARROW);
		speed = Drawables.getDrawables(guiHelper).getScale(Drawables.SCALE_CRUSH);
	}

	@Nonnull
	@Override
	public String getUid() {

		return RecipeUidsTE.PULVERIZER;
	}

	@Override
	public void drawExtras(@Nonnull Minecraft minecraft) {

		progress.draw(minecraft, 69, 23);
		speed.draw(minecraft, 43, 33);
		energyMeter.draw(minecraft, 2, 8);
	}

	@Override
	public void setRecipe(IRecipeLayout recipeLayout, PulverizerRecipeWrapper recipeWrapper, IIngredients ingredients) {

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
