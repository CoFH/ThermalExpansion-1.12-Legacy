package cofh.thermalexpansion.plugins.jei.machine.enchanter;

import cofh.core.util.helpers.StringHelper;
import cofh.thermalexpansion.block.machine.BlockMachine;
import cofh.thermalexpansion.gui.client.machine.GuiEnchanter;
import cofh.thermalexpansion.init.TEProps;
import cofh.thermalexpansion.plugins.jei.Drawables;
import cofh.thermalexpansion.plugins.jei.JEIPluginTE;
import cofh.thermalexpansion.plugins.jei.RecipeUidsTE;
import cofh.thermalexpansion.plugins.jei.machine.BaseRecipeCategory;
import cofh.thermalexpansion.util.managers.machine.EnchanterManager;
import cofh.thermalexpansion.util.managers.machine.EnchanterManager.EnchanterRecipe;
import cofh.thermalexpansion.util.managers.machine.EnchanterManager.Type;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.IRecipeRegistry;
import mezz.jei.api.gui.IDrawableStatic;
import mezz.jei.api.gui.IGuiFluidStackGroup;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeCategoryRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class EnchanterRecipeCategory extends BaseRecipeCategory<EnchanterRecipeWrapper> {

	public static boolean enable = true;

	public static EnchanterRecipeCategory categoryStandard;
	public static EnchanterRecipeCategoryEmpowered categoryEmpowered;

	public static void register(IRecipeCategoryRegistration registry) {

		if (!enable) {
			return;
		}
		IJeiHelpers jeiHelpers = registry.getJeiHelpers();
		IGuiHelper guiHelper = jeiHelpers.getGuiHelper();

		categoryStandard = new EnchanterRecipeCategory(guiHelper);
		categoryEmpowered = new EnchanterRecipeCategoryEmpowered(guiHelper);

		registry.addRecipeCategories(categoryStandard);
		registry.addRecipeCategories(categoryEmpowered);
	}

	public static void initialize(IModRegistry registry) {

		if (!enable) {
			return;
		}
		IJeiHelpers jeiHelpers = registry.getJeiHelpers();
		IGuiHelper guiHelper = jeiHelpers.getGuiHelper();

		registry.addRecipes(getRecipes(guiHelper), RecipeUidsTE.ENCHANTER);
		registry.addRecipeClickArea(GuiEnchanter.class, 79, 34, 24, 16, RecipeUidsTE.ENCHANTER, RecipeUidsTE.ENCHANTER_EMPOWERED);
		registry.addRecipeCatalyst(BlockMachine.machineEnchanter, RecipeUidsTE.ENCHANTER);

		// EnchanterRecipeCategoryEmpowered.initialize(registry);
	}

	public static void refresh() {

		if (!enable) {
			return;
		}
		IRecipeRegistry recipeRegistry = JEIPluginTE.jeiRuntime.getRecipeRegistry();

		List<EnchanterRecipeWrapper> enchanterRecipeWrappers = recipeRegistry.getRecipeWrappers(categoryStandard);
		for (EnchanterRecipeWrapper wrapper : enchanterRecipeWrappers) {
			//recipeRegistry.removeRecipe(wrapper, RecipeUidsTE.ENCHANTER);
			wrapper.refresh();
		}
		// TODO: This is a temporary half-solution til Mezz gets back to me.
		//		enchanterRecipeWrappers.clear();
		//
		//		for (EnchanterRecipe recipe : EnchanterManager.getRecipeList()) {
		//			if (recipe.getType() == Type.STANDARD) {
		//				enchanterRecipeWrappers.add(new EnchanterRecipeWrapper(JEIPluginTE.guiHelper, recipe));
		//			}
		//		}
		//		for (EnchanterRecipeWrapper wrapper : enchanterRecipeWrappers) {
		//			recipeRegistry.addRecipe(wrapper, RecipeUidsTE.ENCHANTER);
		//		}
		// EnchanterRecipeCategoryEmpowered.refresh(registry);
	}

	public static List<EnchanterRecipeWrapper> getRecipes(IGuiHelper guiHelper) {

		List<EnchanterRecipeWrapper> recipes = new ArrayList<>();

		for (EnchanterRecipe recipe : EnchanterManager.getRecipeList()) {
			if (recipe.getType() == Type.STANDARD) {
				recipes.add(new EnchanterRecipeWrapper(guiHelper, recipe));
			}
		}
		return recipes;
	}

	protected IDrawableStatic progress;
	protected IDrawableStatic speed;
	protected IDrawableStatic tank;
	protected IDrawableStatic tankOverlay;

	public EnchanterRecipeCategory(IGuiHelper guiHelper) {

		background = guiHelper.createDrawable(GuiEnchanter.TEXTURE, 26, 11, 124, 62, 0, 0, 16, 24);
		energyMeter = Drawables.getDrawables(guiHelper).getEnergyEmpty();
		localizedName = StringHelper.localize("tile.thermalexpansion.machine.enchanter.name");

		progress = Drawables.getDrawables(guiHelper).getProgress(Drawables.PROGRESS_ARROW);
		speed = Drawables.getDrawables(guiHelper).getScale(Drawables.SCALE_BOOK);
		tank = Drawables.getDrawables(guiHelper).getTank(Drawables.TANK);
		tankOverlay = Drawables.getDrawables(guiHelper).getTankLargeOverlay(Drawables.TANK);
	}

	@Nonnull
	@Override
	public String getUid() {

		return RecipeUidsTE.ENCHANTER;
	}

	@Override
	public void drawExtras(@Nonnull Minecraft minecraft) {

		progress.draw(minecraft, 69, 23);
		speed.draw(minecraft, 34, 33);
		tank.draw(minecraft, 140, 0);
		energyMeter.draw(minecraft, 2, 8);
	}

	@Override
	public void setRecipe(IRecipeLayout recipeLayout, EnchanterRecipeWrapper recipeWrapper, IIngredients ingredients) {

		List<List<ItemStack>> inputs = ingredients.getInputs(ItemStack.class);
		List<List<FluidStack>> inputFluids = ingredients.getInputs(FluidStack.class);
		List<List<ItemStack>> outputs = ingredients.getOutputs(ItemStack.class);

		IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();
		IGuiFluidStackGroup guiFluidStacks = recipeLayout.getFluidStacks();

		guiItemStacks.init(0, true, 21, 14);
		guiItemStacks.init(1, true, 45, 14);
		guiItemStacks.init(2, false, 105, 23);

		guiFluidStacks.init(0, true, 141, 1, 16, 60, TEProps.MAX_FLUID_LARGE, false, tankOverlay);

		guiItemStacks.set(0, inputs.get(0));
		guiItemStacks.set(1, inputs.get(1));
		guiItemStacks.set(2, outputs.get(0));

		guiFluidStacks.set(0, inputFluids.get(0));
	}

}
