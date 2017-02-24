package cofh.thermalexpansion.plugins.jei.category;

import cofh.thermalexpansion.plugins.jei.JeiPluginTE;
import cofh.thermalexpansion.plugins.jei.MachineRecipeWrapper;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.recipe.BlankRecipeCategory;
import mezz.jei.gui.DrawableResource;
import net.minecraft.item.ItemStack;

import java.util.List;

public abstract class CategoryBase extends BlankRecipeCategory<MachineRecipeWrapper> {

	private static final IDrawable BACKGROUND = new DrawableResource(JeiPluginTE.JEI_HANDLER_LOCATION, 0, 0, 175, 95);
	protected String uId;
	private String title;

	protected CategoryBase(String uId, String title) {

		this.uId = uId;
		this.title = title;
	}

	@Override
	public IDrawable getBackground() {

		return BACKGROUND;
	}

	public abstract ItemStack getCraftingItem();

	@Override
	public String getTitle() {

		return title;
	}

	public List<String> getTooltips(int mouseX, int mouseY) {

		return null;
	}

	@Override
	public String getUid() {

		return uId;
	}

	public void registerClickToShowCategoryAreas(IModRegistry registry) {

	}
}
