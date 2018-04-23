package cofh.thermalexpansion.plugins;

import cofh.core.util.helpers.ItemHelper;
import cofh.thermalexpansion.util.managers.machine.InsolatorManager;
import net.minecraft.item.ItemStack;

public class PluginThaumcraft extends PluginTEBase {

	public static final String MOD_ID = "thaumcraft";
	public static final String MOD_NAME = "Thaumcraft";

	public PluginThaumcraft() {

		super(MOD_ID, MOD_NAME);
	}

	@Override
	public void registerDelegate() {

		ItemStack saplingGreatwood = getItemStack("sapling_greatwood");
		ItemStack saplingSilverwood = getItemStack("sapling_silverwood");

		ItemStack logGreatwood = getItemStack("log_greatwood");
		ItemStack logSilverwood = getItemStack("log_silverwood");

		/* INSOLATOR */
		{
			InsolatorManager.addDefaultTreeRecipe(saplingGreatwood, ItemHelper.cloneStack(logGreatwood, 6), saplingGreatwood);
			InsolatorManager.addDefaultTreeRecipe(saplingSilverwood, ItemHelper.cloneStack(logSilverwood, 6), saplingSilverwood);
		}
	}

}
