package cofh.thermalexpansion.plugins.jei.crafting.charger;

import cofh.api.energy.IEnergyContainerItem;
import cofh.lib.inventory.ComparableItemStack;
import cofh.lib.inventory.ComparableItemStackNBT;
import cofh.lib.util.helpers.StringHelper;
import cofh.thermalexpansion.block.machine.BlockMachine;
import cofh.thermalexpansion.gui.client.machine.GuiCharger;
import cofh.thermalexpansion.plugins.jei.Drawables;
import cofh.thermalexpansion.plugins.jei.RecipeUidsTE;
import cofh.thermalexpansion.plugins.jei.crafting.BaseRecipeCategory;
import cofh.thermalexpansion.util.managers.machine.ChargerManager;
import cofh.thermalexpansion.util.managers.machine.ChargerManager.ChargerRecipe;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeCategoryRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class ChargerRecipeCategory extends BaseRecipeCategory<ChargerRecipeWrapper> {

	public static boolean enable = true;

	public static void initialize(IModRegistry registry) {

		if (!enable) {
			return;
		}
		IJeiHelpers jeiHelpers = registry.getJeiHelpers();
		IGuiHelper guiHelper = jeiHelpers.getGuiHelper();

		((IRecipeCategoryRegistration) registry).addRecipeCategories(new ChargerRecipeCategory(guiHelper));
		registry.addRecipes(getRecipes(guiHelper), RecipeUidsTE.CHARGER);
		registry.addRecipeClickArea(GuiCharger.class, 79, 53, 18, 16, RecipeUidsTE.CHARGER);
		registry.addRecipeCatalyst(BlockMachine.machineCharger, RecipeUidsTE.CHARGER);
	}

	public static List<ChargerRecipeWrapper> getRecipes(IGuiHelper guiHelper) {

		List<ChargerRecipeWrapper> recipes = new ArrayList<>();

		for (ChargerRecipe recipe : ChargerManager.getRecipeList()) {
			recipes.add(new ChargerRecipeWrapper(guiHelper, recipe));
		}
		for (Item item : Item.REGISTRY) {
			if (item instanceof IEnergyContainerItem) {
				try {
					HashSet<ComparableItemStack> processedStacks = new HashSet<>();
					NonNullList<ItemStack> list = NonNullList.create();
					item.getSubItems(item, item.getCreativeTab(), list);

					for (ItemStack chargable : list) {
						IEnergyContainerItem energyContainerItem = (IEnergyContainerItem) item;
						int maxEnergyStored = energyContainerItem.getMaxEnergyStored(chargable);

						if (maxEnergyStored != 0 && energyContainerItem.receiveEnergy(chargable, Integer.MAX_VALUE, true) > 0) {

							ItemStack input = chargable.copy();
							ItemStack output = chargable.copy();

							energyContainerItem.extractEnergy(input, Integer.MAX_VALUE, false);
							energyContainerItem.receiveEnergy(output, Integer.MAX_VALUE, false);

							if (!processedStacks.add(new ComparableItemStackNBT(input))) {
								continue;
							}
							ChargerRecipe recipe = new ChargerRecipe(input, output, maxEnergyStored);
							recipes.add(new ChargerRecipeWrapper(guiHelper, recipe));
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return recipes;
	}

	public ChargerRecipeCategory(IGuiHelper guiHelper) {

		background = guiHelper.createDrawable(GuiCharger.TEXTURE, 62, 11, 88, 62, 0, 0, 16, 24);

		energyMeter = Drawables.getDrawables(guiHelper).getEnergyEmpty();
		localizedName = StringHelper.localize("tile.thermalexpansion.machine.charger.name");
	}

	@Nonnull
	@Override
	public String getUid() {

		return RecipeUidsTE.CHARGER;
	}

	@Override
	public void drawExtras(@Nonnull Minecraft minecraft) {

		energyMeter.draw(minecraft, 2, 8);
	}

	@Override
	public void setRecipe(IRecipeLayout recipeLayout, ChargerRecipeWrapper recipeWrapper, IIngredients ingredients) {

		List<List<ItemStack>> inputs = ingredients.getInputs(ItemStack.class);
		List<List<ItemStack>> outputs = ingredients.getOutputs(ItemStack.class);

		IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();

		guiItemStacks.init(0, true, 33, 23);
		guiItemStacks.init(1, false, 78, 23);

		guiItemStacks.set(0, inputs.get(0));
		guiItemStacks.set(1, outputs.get(0));
	}

}
