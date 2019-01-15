package cofh.thermalexpansion.plugins.jei.machine.smelter;

import cofh.core.util.helpers.ItemHelper;
import cofh.core.util.helpers.StringHelper;
import cofh.thermalexpansion.block.machine.TileSmelter;
import cofh.thermalexpansion.plugins.jei.Drawables;
import cofh.thermalexpansion.plugins.jei.JEIPluginTE;
import cofh.thermalexpansion.plugins.jei.RecipeUidsTE;
import cofh.thermalexpansion.plugins.jei.machine.BaseRecipeWrapper;
import cofh.thermalexpansion.util.managers.machine.SmelterManager;
import cofh.thermalexpansion.util.managers.machine.SmelterManager.SmelterRecipe;
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

public class SmelterRecipeWrapper extends BaseRecipeWrapper {

	/* Recipe */
	protected List<List<ItemStack>> inputs;
	protected List<FluidStack> inputFluids;
	protected List<ItemStack> outputs;

	protected int chance;

	/* Animation */
	protected IDrawableAnimated fluid;
	protected IDrawableAnimated progress;
	protected IDrawableAnimated speed;

	public SmelterRecipeWrapper(IGuiHelper guiHelper, SmelterRecipe recipe) {

		this(guiHelper, recipe, RecipeUidsTE.SMELTER);
	}

	public SmelterRecipeWrapper(IGuiHelper guiHelper, SmelterRecipe recipe, String uIdIn) {

		uId = uIdIn;

		List<List<ItemStack>> recipeInputs = new ArrayList<>();
		List<FluidStack> recipeInputFluids = new ArrayList<>();
		List<ItemStack> recipeOutputs = new ArrayList<>();

		List<ItemStack> recipeInputsPrimary = new ArrayList<>();
		List<ItemStack> recipeInputsSecondary = new ArrayList<>();

		int oreID = SmelterManager.convertInput(recipe.getPrimaryInput()).oreID;
		if (oreID != -1) {
			for (ItemStack ore : OreDictionary.getOres(ItemHelper.oreProxy.getOreName(oreID), false)) {
				recipeInputsPrimary.add(ItemHelper.cloneStack(ore, recipe.getPrimaryInput().getCount()));
			}
		} else {
			recipeInputsPrimary.add(recipe.getPrimaryInput());
		}
		oreID = SmelterManager.convertInput(recipe.getSecondaryInput()).oreID;
		if (oreID != -1) {
			for (ItemStack ore : OreDictionary.getOres(ItemHelper.oreProxy.getOreName(oreID), false)) {
				recipeInputsSecondary.add(ItemHelper.cloneStack(ore, recipe.getSecondaryInput().getCount()));
			}
		} else {
			recipeInputsSecondary.add(recipe.getSecondaryInput());
		}
		recipeInputs.add(recipeInputsSecondary);
		recipeInputs.add(recipeInputsPrimary);

		if (uId.equals(RecipeUidsTE.SMELTER_PYROTHEUM)) {
			recipeInputFluids.add(new FluidStack(TFFluids.fluidPyrotheum, TileSmelter.FLUID_AMOUNT));
			inputFluids = recipeInputFluids;
			recipeOutputs.add(ItemHelper.cloneStack(recipe.getPrimaryOutput(), TileSmelter.getPyrotheumOutputAmount(recipe.getPrimaryOutput())));
			energy = recipe.getEnergy() * (100 + TileSmelter.PYROTHEUM_ENERGY_MOD) / 100;
		} else {
			inputFluids = Collections.emptyList();
			recipeOutputs.add(recipe.getPrimaryOutput());
			energy = recipe.getEnergy();
		}
		if (recipe.getSecondaryOutput() != null) {
			recipeOutputs.add(recipe.getSecondaryOutput());
		}
		inputs = recipeInputs;
		outputs = recipeOutputs;

		chance = recipe.getSecondaryOutputChance();

		IDrawableStatic fluidDrawable = Drawables.getDrawables(guiHelper).getProgress(Drawables.PROGRESS_ARROW_FLUID);
		IDrawableStatic progressDrawable = Drawables.getDrawables(guiHelper).getProgressFill(uId.equals(RecipeUidsTE.SMELTER_PYROTHEUM) ? Drawables.PROGRESS_ARROW_FLUID : Drawables.PROGRESS_ARROW);
		IDrawableStatic speedDrawable = Drawables.getDrawables(guiHelper).getScaleFill(Drawables.SCALE_FLAME);
		IDrawableStatic energyDrawable = Drawables.getDrawables(guiHelper).getEnergyFill();

		fluid = guiHelper.createAnimatedDrawable(fluidDrawable, energy / TileSmelter.basePower, StartDirection.LEFT, true);
		progress = guiHelper.createAnimatedDrawable(progressDrawable, energy / TileSmelter.basePower, StartDirection.LEFT, false);
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

		if (uId.equals(RecipeUidsTE.SMELTER_PYROTHEUM)) {
			JEIPluginTE.drawFluid(69, 23, inputFluids.get(0), 24, 16);
			fluid.draw(minecraft, 69, 23);
		}
		progress.draw(minecraft, 69, 23);
		speed.draw(minecraft, 34, 33);
		energyMeter.draw(minecraft, 2, 8);

		if (chance > 0) {
			String dispChance = StringHelper.formatNumber(chance) + "%";
			minecraft.fontRenderer.drawString(dispChance, 102 - 6 * dispChance.length(), 48, 0x808080);
		}
	}

}
