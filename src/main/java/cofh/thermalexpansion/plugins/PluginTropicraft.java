package cofh.thermalexpansion.plugins;

import cofh.thermalexpansion.util.managers.machine.InsolatorManager;
import net.minecraft.item.ItemStack;

public class PluginTropicraft extends PluginTEBase {

	public static final String MOD_ID = "tropicraft";
	public static final String MOD_NAME = "Tropicraft";

	public PluginTropicraft() {

		super(MOD_ID, MOD_NAME);
	}

	@Override
	public void initializeDelegate() {

	}

	@Override
	public void registerDelegate() {

		/* INSOLATOR */
		{
			String plant = "flower";
			for (int i = 0; i < 15; i++) {
				InsolatorManager.addDefaultRecipe(getItemStack(plant, 1, i), getItemStack(plant, 3, i), ItemStack.EMPTY, 0);
			}
			plant = "coral";
			for (int i = 0; i < 7; i++) {
				InsolatorManager.addDefaultRecipe(getItemStack(plant, 1, i), getItemStack(plant, 2, i), ItemStack.EMPTY, 0);
			}
		}
	}

}
