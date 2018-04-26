package cofh.thermalexpansion.plugins.forestry;

import cofh.thermalexpansion.plugins.PluginTEBase;
import cofh.thermalexpansion.util.managers.machine.CentrifugeManager;
import net.minecraft.item.ItemStack;

import static java.util.Arrays.asList;

public class PluginGendustry extends PluginTEBase {

	public static final String PARENT_ID = PluginForestry.MOD_ID;
	public static final String MOD_ID = "gendustry";
	public static final String MOD_NAME = "Gendustry";

	public PluginGendustry() {

		super(MOD_ID, MOD_NAME);
	}

	@Override
	public void initializeDelegate() {

		ItemStack dropHoney = getItemStack(PARENT_ID, "honey_drop", 1, 0);
		ItemStack wax = getItemStack(PARENT_ID, "beeswax", 1, 0);

		ItemStack[] tintedCombs = new ItemStack[16];
		ItemStack[] tintedDrops = new ItemStack[16];

		int tintedStart = 10;

		for (int i = 0; i < 16; i++) {
			tintedCombs[i] = getItemStack("honey_comb", 1, i + tintedStart);
			tintedDrops[i] = getItemStack("honey_drop", 1, i + tintedStart);
		}

		/* CENTRIFUGE */
		{
			int energy = CentrifugeManager.DEFAULT_ENERGY;

			for (int i = 0; i < 16; i++) {
				CentrifugeManager.addRecipe(energy, tintedCombs[i], asList(tintedDrops[i], dropHoney, wax), asList(100, 30, 50), null);
			}
		}
	}

}
