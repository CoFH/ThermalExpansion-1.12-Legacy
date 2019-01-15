package cofh.thermalexpansion.plugins.jei.machine.pulverizer;

import cofh.core.util.helpers.ItemHelper;
import cofh.core.util.helpers.StringHelper;
import cofh.thermalexpansion.block.machine.TilePulverizer;
import cofh.thermalexpansion.plugins.jei.Drawables;
import cofh.thermalexpansion.plugins.jei.JEIPluginTE;
import cofh.thermalexpansion.plugins.jei.RecipeUidsTE;
import cofh.thermalexpansion.plugins.jei.machine.BaseRecipeWrapper;
import cofh.thermalexpansion.util.managers.machine.PulverizerManager;
import cofh.thermalexpansion.util.managers.machine.PulverizerManager.PulverizerRecipe;
import cofh.thermalfoundation.init.TFFluids;
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
import java.util.Collections;
import java.util.List;

import static java.util.Collections.singletonList;

public class PulverizerRecipeWrapper extends BaseRecipeWrapper {

	/* Recipe */
	protected List<List<ItemStack>> inputs;
	protected List<FluidStack> inputFluids;
	protected List<ItemStack> outputs;

	protected int chance;

	/* Animation */
	protected IDrawableAnimated fluid;
	protected IDrawableAnimated progress;
	protected IDrawableAnimated speed;

	public PulverizerRecipeWrapper(IGuiHelper guiHelper, PulverizerRecipe recipe) {

		this(guiHelper, recipe, RecipeUidsTE.PULVERIZER);
	}

	public PulverizerRecipeWrapper(IGuiHelper guiHelper, PulverizerRecipe recipe, String uIdIn) {

		uId = uIdIn;

		List<ItemStack> recipeInputs = new ArrayList<>();
		List<FluidStack> recipeInputFluids = new ArrayList<>();
		List<ItemStack> recipeOutputs = new ArrayList<>();

		int oreID = PulverizerManager.convertInput(recipe.getInput()).oreID;
		if (oreID != -1) {
			for (ItemStack ore : OreDictionary.getOres(ItemHelper.oreProxy.getOreName(oreID), false)) {
				recipeInputs.add(ItemHelper.cloneStack(ore, recipe.getInput().getCount()));
			}
		} else {
			recipeInputs.add(recipe.getInput());
		}
		if (uId.equals(RecipeUidsTE.PULVERIZER_PETROTHEUM)) {
			recipeInputFluids.add(new FluidStack(TFFluids.fluidPetrotheum, TilePulverizer.FLUID_AMOUNT));
			inputFluids = recipeInputFluids;
			recipeOutputs.add(ItemHelper.cloneStack(recipe.getPrimaryOutput(), TilePulverizer.getPetrotheumOutputAmount(recipe.getPrimaryOutput())));
			energy = recipe.getEnergy() * (100 + TilePulverizer.PETROTHEUM_ENERGY_MOD) / 100;
		} else {
			inputFluids = Collections.emptyList();
			recipeOutputs.add(recipe.getPrimaryOutput());
			energy = recipe.getEnergy();
		}
		if (recipe.getSecondaryOutput() != null) {
			recipeOutputs.add(recipe.getSecondaryOutput());
		}
		inputs = singletonList(recipeInputs);
		outputs = recipeOutputs;

		chance = recipe.getSecondaryOutputChance();

		IDrawableStatic fluidDrawable = Drawables.getDrawables(guiHelper).getProgress(Drawables.PROGRESS_ARROW_FLUID);
		IDrawableStatic progressDrawable = Drawables.getDrawables(guiHelper).getProgressFill(uId.equals(RecipeUidsTE.PULVERIZER_PETROTHEUM) ? Drawables.PROGRESS_ARROW_FLUID : Drawables.PROGRESS_ARROW);
		IDrawableStatic speedDrawable = Drawables.getDrawables(guiHelper).getScaleFill(Drawables.SCALE_CRUSH);
		IDrawableStatic energyDrawable = Drawables.getDrawables(guiHelper).getEnergyFill();

		fluid = guiHelper.createAnimatedDrawable(fluidDrawable, energy / TilePulverizer.basePower, StartDirection.LEFT, true);
		progress = guiHelper.createAnimatedDrawable(progressDrawable, energy / TilePulverizer.basePower, StartDirection.LEFT, false);
		speed = guiHelper.createAnimatedDrawable(speedDrawable, 1000, StartDirection.TOP, true);
		energyMeter = guiHelper.createAnimatedDrawable(energyDrawable, 1000, StartDirection.TOP, true);
	}

	@Override
	public void getIngredients(IIngredients ingredients) {

		ingredients.setInputLists(ItemStack.class, inputs);
		ingredients.setInputs(FluidStack.class, inputFluids);
		ingredients.setOutputs(ItemStack.class, outputs);
	}

	@Override
	public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {

		if (uId.equals(RecipeUidsTE.PULVERIZER_PETROTHEUM)) {
			JEIPluginTE.drawFluid(69, 23, inputFluids.get(0), 24, 16);
			fluid.draw(minecraft, 69, 23);
		}
		progress.draw(minecraft, 69, 23);
		speed.draw(minecraft, 43, 33);
		energyMeter.draw(minecraft, 2, 8);

		if (chance > 0) {
			String dispChance = StringHelper.formatNumber(chance) + "%";
			minecraft.fontRenderer.drawString(dispChance, 102 - 6 * dispChance.length(), 48, 0x808080);
		}
	}

}
