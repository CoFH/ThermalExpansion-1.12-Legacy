package cofh.thermalexpansion.plugins;

import cofh.thermalexpansion.util.managers.machine.CentrifugeManager;
import net.minecraft.item.ItemStack;

import static java.util.Arrays.asList;

public class PluginComputronics extends PluginTEBase {

	public static final String MOD_ID = "computronics";
	public static final String MOD_NAME = "Computronics";

	public PluginComputronics() {

		super(MOD_ID, MOD_NAME);
	}

	@Override
	public void initializeDelegate() {

		ItemStack combCaustic = getItemStack("forestry_parts", 1, 0);
		ItemStack dropCaustic = getItemStack("forestry_parts", 1, 1);

		/* CENTRIFUGE */
		{
			int energy = CentrifugeManager.DEFAULT_ENERGY;

			CentrifugeManager.addRecipe(energy, combCaustic, asList(dropCaustic, dropCaustic), asList(100, 30), null);
		}
	}

}
