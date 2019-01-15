package cofh.thermalexpansion.plugins.jei.machine.crafter;

import cofh.thermalexpansion.gui.container.machine.ContainerCrafter;
import mezz.jei.api.gui.IGuiIngredient;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.recipe.transfer.IRecipeTransferError;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

public class CrafterRecipeTransferHandler implements IRecipeTransferHandler<ContainerCrafter> {

	@Override
	public Class<ContainerCrafter> getContainerClass() {

		return ContainerCrafter.class;
	}

	@Nullable
	@Override
	public IRecipeTransferError transferRecipe(ContainerCrafter container, IRecipeLayout recipeLayout, EntityPlayer player, boolean maxTransfer, boolean doTransfer) {

		if (doTransfer) {
			Map<Integer, ? extends IGuiIngredient<ItemStack>> guiIngredients = recipeLayout.getItemStacks().getGuiIngredients();

			container.craftMatrix.clear();
			container.craftResult.clear();

			for (Map.Entry<Integer, ? extends IGuiIngredient<ItemStack>> entry : guiIngredients.entrySet()) {
				int recipeSlot = entry.getKey();
				List<ItemStack> allIngredients = entry.getValue().getAllIngredients();

				if (!allIngredients.isEmpty()) {
					if (recipeSlot != 0) { // skip the output slot
						container.craftMatrix.setInventorySlotContents(recipeSlot - 1, allIngredients.get(0));
					}
				}
				container.calcCraftingGridClient();
				container.setRecipe();
			}
		}
		return null;
	}

}
