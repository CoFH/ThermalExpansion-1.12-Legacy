package cofh.thermalexpansion.plugins.jei.dynamos;

import cofh.lib.util.helpers.StringHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.BlankRecipeWrapper;
import net.minecraft.client.Minecraft;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;

public final class DynamoFuelWrapper<T> extends BlankRecipeWrapper {

	final DynamoFuelCategory<T> categoryBase;
	final List<T> inputs;
	final String displayText;
	public int drawX;

	public DynamoFuelWrapper(T inputs, DynamoFuelCategory<T> categoryBase, int energy) {

		this(inputs, categoryBase, energy, "RF");
	}

	public DynamoFuelWrapper(T inputs, DynamoFuelCategory<T> categoryBase, int energy, String rf_suffix) {

		this(Collections.singletonList(inputs), categoryBase, StringHelper.formatNumber(energy) + " " + rf_suffix);
	}

	public DynamoFuelWrapper(T inputs, DynamoFuelCategory<T> categoryBase, String displayText) {

		this(Collections.singletonList(inputs), categoryBase, displayText);
	}

	public DynamoFuelWrapper(List<T> inputs, DynamoFuelCategory<T> categoryBase, String displayText) {

		this.categoryBase = categoryBase;
		this.inputs = inputs;
		this.displayText = displayText;
	}

	public String getUid() {

		return categoryBase.getUid();
	}

	@Override
	public void getIngredients(@Nonnull IIngredients ingredients) {

		ingredients.setInputLists(categoryBase.getIngredientClass(), Collections.singletonList(inputs));
	}

	@Override
	public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {

		minecraft.fontRendererObj.drawString(displayText, drawX, (recipeHeight - 9) / 2, 0x404040);
	}

}
