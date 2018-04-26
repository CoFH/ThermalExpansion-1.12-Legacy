package cofh.thermalexpansion.plugins;

import cofh.thermalexpansion.util.managers.machine.CentrifugeManager;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;

public class PluginIceAndFire extends PluginTEBase {

	public static final String MOD_ID = "iceandfire";
	public static final String MOD_NAME = "Ice and Fire";

	public PluginIceAndFire() {

		super(MOD_ID, MOD_NAME);
	}

	@Override
	public void initializeDelegate() {

		/* CENTRIFUGE */
		{
			CentrifugeManager.addDefaultMobRecipe(MOD_ID + ":snowvillager", singletonList(new ItemStack(Items.EMERALD)), singletonList(2), 0);
			CentrifugeManager.addDefaultMobRecipe(MOD_ID + ":hippogryph", asList(new ItemStack(Items.FEATHER, 5), new ItemStack(Items.LEATHER, 5)), asList(50, 50), 2);
			CentrifugeManager.addDefaultMobRecipe(MOD_ID + ":if_pixie", singletonList(getItemStack("pixie_dust")), singletonList(50), 3);
			CentrifugeManager.addDefaultMobRecipe(MOD_ID + ":cyclops", asList(new ItemStack(Items.LEATHER, 10), new ItemStack(Items.MUTTON, 10), new ItemStack(Blocks.WOOL, 5)), asList(50, 50, 50), 5);
			CentrifugeManager.addDefaultMobRecipe(MOD_ID + ":siren", asList(new ItemStack(Items.PRISMARINE_CRYSTALS, 5), new ItemStack(Items.PRISMARINE_SHARD, 3), getItemStack("shiny_scales", 6)), asList(50, 50, 50), 5);
			CentrifugeManager.addDefaultMobRecipe(MOD_ID + ":hippocampus", singletonList(getItemStack("shiny_scales", 4)), singletonList(50), 2);
		}
	}

}
