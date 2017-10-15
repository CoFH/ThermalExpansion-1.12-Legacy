package cofh.thermalexpansion.plugins.jei.crafting.enchanter;

import cofh.core.util.helpers.ItemHelper;
import cofh.thermalexpansion.block.machine.TileEnchanter;
import cofh.thermalexpansion.plugins.jei.Drawables;
import cofh.thermalexpansion.plugins.jei.JEIPluginTE;
import cofh.thermalexpansion.plugins.jei.RecipeUidsTE;
import cofh.thermalexpansion.plugins.jei.crafting.BaseRecipeWrapper;
import cofh.thermalexpansion.util.managers.machine.EnchanterManager.ComparableItemStackEnchanter;
import cofh.thermalexpansion.util.managers.machine.EnchanterManager.EnchanterRecipe;
import cofh.thermalexpansion.util.managers.machine.EnchanterManager.Type;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawableAnimated;
import mezz.jei.api.gui.IDrawableAnimated.StartDirection;
import mezz.jei.api.gui.IDrawableStatic;
import mezz.jei.api.ingredients.IIngredients;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.OreDictionary;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class EnchanterRecipeWrapper extends BaseRecipeWrapper {

	/* Recipe */
	final List<List<ItemStack>> inputs;
	final List<List<FluidStack>> inputFluids;
	final List<ItemStack> outputs;

	final Type type;

	/* Animation */
	final IDrawableStatic progressBack;
	final IDrawableStatic speedBack;
	final IDrawableAnimated fluid;
	final IDrawableAnimated progress;
	final IDrawableAnimated speed;

	public EnchanterRecipeWrapper(IGuiHelper guiHelper, EnchanterRecipe recipe) {

		this(guiHelper, recipe, RecipeUidsTE.ENCHANTER);
	}

	public EnchanterRecipeWrapper(IGuiHelper guiHelper, EnchanterRecipe recipe, String uIdIn) {

		uId = uIdIn;

		List<List<ItemStack>> recipeInputs = new ArrayList<>();
		List<FluidStack> recipeInputFluids = new ArrayList<>();
		List<ItemStack> recipeInputsPrimary = new ArrayList<>();
		List<ItemStack> recipeInputsSecondary = new ArrayList<>();

		int oreID = ComparableItemStackEnchanter.getOreID(recipe.getPrimaryInput());
		if (oreID != -1) {
			for (ItemStack ore : OreDictionary.getOres(ItemHelper.oreProxy.getOreName(oreID), false)) {
				recipeInputsPrimary.add(ItemHelper.cloneStack(ore, recipe.getPrimaryInput().getCount()));
			}
		} else {
			recipeInputsPrimary.add(recipe.getPrimaryInput());
		}
		oreID = ComparableItemStackEnchanter.getOreID(recipe.getSecondaryInput());
		if (oreID != -1) {
			for (ItemStack ore : OreDictionary.getOres(ItemHelper.oreProxy.getOreName(oreID), false)) {
				recipeInputsSecondary.add(ItemHelper.cloneStack(ore, recipe.getSecondaryInput().getCount()));
			}
		} else {
			recipeInputsSecondary.add(recipe.getSecondaryInput());
		}
		recipeInputs.add(recipeInputsPrimary);
		recipeInputs.add(recipeInputsSecondary);
		recipeInputFluids.add(new FluidStack(FluidRegistry.WATER, recipe.getExperience()));

		List<ItemStack> recipeOutputs = new ArrayList<>();
		recipeOutputs.add(recipe.getOutput());

		inputs = recipeInputs;
		inputFluids = Collections.singletonList(recipeInputFluids);
		outputs = recipeOutputs;

		energy = recipe.getEnergy();

		type = recipe.getType();

		progressBack = Drawables.getDrawables(guiHelper).getProgress(Drawables.PROGRESS_ARROW);
		speedBack = Drawables.getDrawables(guiHelper).getScale(Drawables.SCALE_SUN);

		IDrawableStatic fluidDrawable = Drawables.getDrawables(guiHelper).getProgress(Drawables.PROGRESS_ARROW_FLUID);
		IDrawableStatic progressDrawable = Drawables.getDrawables(guiHelper).getProgressFill(Drawables.PROGRESS_ARROW_FLUID);
		IDrawableStatic speedDrawable = Drawables.getDrawables(guiHelper).getScaleFill(Drawables.SCALE_SUN);
		IDrawableStatic energyDrawable = Drawables.getDrawables(guiHelper).getEnergyFill();

		fluid = guiHelper.createAnimatedDrawable(fluidDrawable, energy / TileEnchanter.basePower, StartDirection.LEFT, true);
		progress = guiHelper.createAnimatedDrawable(progressDrawable, energy / TileEnchanter.basePower, StartDirection.LEFT, false);
		speed = guiHelper.createAnimatedDrawable(speedDrawable, 1000, StartDirection.TOP, true);
		energyMeter = guiHelper.createAnimatedDrawable(energyDrawable, 1000, StartDirection.TOP, true);
	}

	@Override
	public void getIngredients(IIngredients ingredients) {

		ingredients.setInputLists(ItemStack.class, inputs);
		ingredients.setInputLists(FluidStack.class, inputFluids);
		ingredients.setOutputs(ItemStack.class, outputs);
	}

	@Override
	public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {

		progressBack.draw(minecraft, 69, 23);
		speedBack.draw(minecraft, 34, 33);

		JEIPluginTE.drawFluid(69, 23, inputFluids.get(0).get(0), 24, 16);

		fluid.draw(minecraft, 69, 23);
		progress.draw(minecraft, 69, 23);
		speed.draw(minecraft, 34, 33);
		energyMeter.draw(minecraft, 2, 8);
	}

}
