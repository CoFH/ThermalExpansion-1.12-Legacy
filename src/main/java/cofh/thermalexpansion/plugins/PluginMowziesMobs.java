package cofh.thermalexpansion.plugins;

import cofh.thermalexpansion.item.ItemMorb;
import cofh.thermalexpansion.util.managers.machine.CentrifugeManager;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;

public class PluginMowziesMobs extends PluginTEBase {

	public static final String MOD_ID = "mowziesmobs";
	public static final String MOD_NAME = "Mowzie's Mobs";

	public PluginMowziesMobs() {

		super(MOD_ID, MOD_NAME);
	}

	@Override
	public void initializeDelegate() {

		ItemMorb.addMorb(MOD_ID + ":foliaath");
		ItemMorb.addMorb(MOD_ID + ":ferrous_wroughtnaut");
		ItemMorb.addMorb(MOD_ID + ":barako");
		ItemMorb.addMorb(MOD_ID + ":frostmaw");

		/* CENTRIFUGE */
		{
			CentrifugeManager.addDefaultMobRecipe(MOD_ID + ":foliaath", singletonList(getItemStack("foliaath_seed")), singletonList(50), 10);
			CentrifugeManager.addDefaultMobRecipe(MOD_ID + ":ferrous_wroughtnaut", asList(getItemStack("wrought_axe"), getItemStack("wrought_helmet")), asList(100, 100), 30);
			CentrifugeManager.addDefaultMobRecipe(MOD_ID + ":barako", singletonList(getItemStack("barako_mask")), singletonList(100), 45);
			CentrifugeManager.addDefaultMobRecipe(MOD_ID + ":frostmaw", singletonList(getItemStack("ice_crystal")), singletonList(100), 60);
		}
	}

}
