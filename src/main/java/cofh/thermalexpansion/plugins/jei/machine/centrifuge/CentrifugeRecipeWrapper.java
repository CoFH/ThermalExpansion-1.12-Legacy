package cofh.thermalexpansion.plugins.jei.machine.centrifuge;

import cofh.core.util.helpers.ItemHelper;
import cofh.thermalexpansion.block.machine.TileCentrifuge;
import cofh.thermalexpansion.plugins.jei.Drawables;
import cofh.thermalexpansion.plugins.jei.RecipeUidsTE;
import cofh.thermalexpansion.plugins.jei.machine.BaseRecipeWrapper;
import cofh.thermalexpansion.util.managers.machine.CentrifugeManager;
import cofh.thermalexpansion.util.managers.machine.CentrifugeManager.CentrifugeRecipe;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawableAnimated;
import mezz.jei.api.gui.IDrawableAnimated.StartDirection;
import mezz.jei.api.gui.IDrawableStatic;
import mezz.jei.api.ingredients.IIngredients;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.OreDictionary;

import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.singletonList;

public class CentrifugeRecipeWrapper extends BaseRecipeWrapper {

	/* Recipe */
	protected List<List<ItemStack>> inputs;
	protected List<ItemStack> outputs;
	protected List<FluidStack> outputFluids;

	protected List<Integer> chance;

	/* Animation */
	protected IDrawableAnimated progress;
	protected IDrawableAnimated speed;

	public CentrifugeRecipeWrapper(IGuiHelper guiHelper, CentrifugeRecipe recipe) {

		this(guiHelper, recipe, RecipeUidsTE.CENTRIFUGE);
	}

	public CentrifugeRecipeWrapper(IGuiHelper guiHelper, CentrifugeRecipe recipe, String uIdIn) {

		List<ItemStack> recipeInputs = new ArrayList<>();

		int oreID = CentrifugeManager.convertInput(recipe.getInput()).oreID;
		if (oreID != -1) {
			for (ItemStack ore : OreDictionary.getOres(ItemHelper.oreProxy.getOreName(oreID), false)) {
				recipeInputs.add(ItemHelper.cloneStack(ore, recipe.getInput().getCount()));
			}
		} else {
			recipeInputs.add(recipe.getInput());
		}

		List<ItemStack> recipeOutputs = new ArrayList<>();
		recipeOutputs.addAll(recipe.getOutput());

		List<FluidStack> recipeFluids = new ArrayList<>();
		recipeFluids.add(recipe.getFluid());

		inputs = singletonList(recipeInputs);
		outputs = recipeOutputs;
		outputFluids = recipeFluids;

		chance = recipe.getChance();
		energy = recipe.getEnergy();

		IDrawableStatic progressDrawable = Drawables.getDrawables(guiHelper).getProgressFill(Drawables.PROGRESS_ARROW);
		IDrawableStatic speedDrawable = Drawables.getDrawables(guiHelper).getScaleFill(Drawables.SCALE_SPIN);
		IDrawableStatic energyDrawable = Drawables.getDrawables(guiHelper).getEnergyFill();

		progress = guiHelper.createAnimatedDrawable(progressDrawable, energy / TileCentrifuge.basePower, StartDirection.LEFT, false);
		speed = guiHelper.createAnimatedDrawable(speedDrawable, 1000, StartDirection.TOP, true);
		energyMeter = guiHelper.createAnimatedDrawable(energyDrawable, 1000, StartDirection.TOP, true);
	}

	@Override
	public void getIngredients(IIngredients ingredients) {

		ingredients.setInputLists(ItemStack.class, inputs);
		ingredients.setOutputs(ItemStack.class, outputs);
		ingredients.setOutputs(FluidStack.class, outputFluids);
	}

	@Override
	public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {

		progress.draw(minecraft, 62, 23);
		speed.draw(minecraft, 34, 32);
		energyMeter.draw(minecraft, 2, 8);
	}

}
