package cofh.thermalexpansion.plugins.jei.machine.crafter;

import cofh.thermalexpansion.block.machine.BlockMachine;
import cofh.thermalexpansion.gui.client.machine.GuiCrafter;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.recipe.IRecipeCategoryRegistration;
import mezz.jei.api.recipe.VanillaRecipeCategoryUid;
import mezz.jei.api.recipe.transfer.IRecipeTransferRegistry;

public class CrafterRecipeCategory {

	public static boolean enable = true;

	public static void register(IRecipeCategoryRegistration registry) {

		if (!enable) {
			return;
		}

	}

	public static void initialize(IModRegistry registry) {

		if (!enable) {
			return;
		}
		registry.addRecipeClickArea(GuiCrafter.class, 92, 21, 24, 16, VanillaRecipeCategoryUid.CRAFTING);
		registry.addRecipeCatalyst(BlockMachine.machineCrafter, VanillaRecipeCategoryUid.CRAFTING);

		IRecipeTransferRegistry transferRegistry = registry.getRecipeTransferRegistry();
		transferRegistry.addRecipeTransferHandler(new CrafterRecipeTransferHandler(), VanillaRecipeCategoryUid.CRAFTING);
	}

}
