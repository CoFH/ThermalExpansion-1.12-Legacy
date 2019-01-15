package cofh.thermalexpansion.plugins;

import cofh.thermalexpansion.util.managers.machine.CentrifugeManager;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;

public class PluginMysticalWildlife extends PluginTEBase {

	public static final String MOD_ID = "mysticalwildlife";
	public static final String MOD_NAME = "Mystical Wildlife";

	public PluginMysticalWildlife() {

		super(MOD_ID, MOD_NAME);
	}

	@Override
	public void initializeDelegate() {

		/* CENTRIFUGE */
		{
			CentrifugeManager.addDefaultMobRecipe(MOD_ID + ":cicaptera_azure", asList(getItemStack("cicaptera_husk", 2, 0), getItemStack("cicaptera_meat_raw", 1)), asList(50, 50), 2);
			CentrifugeManager.addDefaultMobRecipe(MOD_ID + ":cicaptera_crimson", asList(getItemStack("cicaptera_husk", 2, 2), getItemStack("cicaptera_meat_raw", 1)), asList(50, 50), 2);
			CentrifugeManager.addDefaultMobRecipe(MOD_ID + ":cicaptera_sandy", asList(getItemStack("cicaptera_husk", 2, 3), getItemStack("cicaptera_meat_raw", 1)), asList(50, 50), 2);
			CentrifugeManager.addDefaultMobRecipe(MOD_ID + ":cicaptera_verdant", asList(getItemStack("cicaptera_husk", 2, 1), getItemStack("cicaptera_meat_raw", 1)), asList(50, 50), 2);
			CentrifugeManager.addDefaultMobRecipe(MOD_ID + ":cicaptera_wintry", asList(getItemStack("cicaptera_husk", 2, 4), getItemStack("cicaptera_meat_raw", 1)), asList(50, 50), 2);

			CentrifugeManager.addDefaultMobRecipe(MOD_ID + ":dusk_lurker", asList(getItemStack("dusk_ash", 2), getItemStack("dusk_lurker_fur", 2), getItemStack("dusk_lurker_meat_raw", 2)), asList(50, 50, 50), 2);
			CentrifugeManager.addDefaultMobRecipe(MOD_ID + ":krill", singletonList(getItemStack("krill_meat_raw", 2)), singletonList(50), 2);
			CentrifugeManager.addDefaultMobRecipe(MOD_ID + ":plumper", asList(getItemStack("plumper_blubber", 3), getItemStack("plumper_meat_raw", 2)), asList(70, 50), 2);
			CentrifugeManager.addDefaultMobRecipe(MOD_ID + ":vrontausaurus", asList(getItemStack("vrontausaurus_fur", 3), getItemStack("vrontausaurus_meat_raw", 4)), asList(70, 75), 2);
			CentrifugeManager.addDefaultMobRecipe(MOD_ID + ":yaga_hog", asList(new ItemStack(Blocks.DIRT, 2), getItemStack("yaga_hog_meat_raw", 2)), asList(50, 50), 2);
		}
	}

}
