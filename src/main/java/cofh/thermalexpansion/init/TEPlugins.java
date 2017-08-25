package cofh.thermalexpansion.init;

import cofh.core.util.core.IInitializer;
import cofh.thermalexpansion.plugins.*;
import cofh.thermalexpansion.plugins.forestry.PluginExtraBees;
import cofh.thermalexpansion.plugins.forestry.PluginExtraTrees;
import cofh.thermalexpansion.plugins.forestry.PluginForestry;
import cofh.thermalexpansion.plugins.forestry.PluginMagicBees;

import java.util.ArrayList;

public class TEPlugins {

	private TEPlugins() {

	}

	public static void initialize() {

		pluginForestry = new PluginForestry();
		pluginExtraBees = new PluginExtraBees();
		pluginExtraTrees = new PluginExtraTrees();
		pluginMagicBees = new PluginMagicBees();

		pluginActuallyAdditions = new PluginActuallyAdditions();
		pluginBiomesOPlenty = new PluginBiomesOPlenty();
		pluginHarvestcraft = new PluginHarvestcraft();
		pluginIC2 = new PluginIC2();
		pluginNatura = new PluginNatura();
		pluginQuark = new PluginQuark();
		pluginRustic = new PluginRustic();
		pluginTConstruct = new PluginTConstruct();

		initList.add(pluginForestry);
		initList.add(pluginExtraBees);
		initList.add(pluginExtraTrees);
		initList.add(pluginMagicBees);

		initList.add(pluginActuallyAdditions);
		initList.add(pluginBiomesOPlenty);
		initList.add(pluginHarvestcraft);
		initList.add(pluginIC2);
		initList.add(pluginNatura);
		initList.add(pluginQuark);
		initList.add(pluginRustic);
		initList.add(pluginTConstruct);

		for (IInitializer init : initList) {
			init.initialize();
		}
	}

	public static void postInit() {

		for (IInitializer init : initList) {
			init.register();
		}
	}

	private static ArrayList<IInitializer> initList = new ArrayList<>();

	/* REFERENCES */
	public static PluginForestry pluginForestry;
	public static PluginExtraBees pluginExtraBees;
	public static PluginExtraTrees pluginExtraTrees;
	public static PluginMagicBees pluginMagicBees;

	public static PluginActuallyAdditions pluginActuallyAdditions;
	public static PluginBiomesOPlenty pluginBiomesOPlenty;
	public static PluginHarvestcraft pluginHarvestcraft;
	public static PluginIC2 pluginIC2;
	public static PluginNatura pluginNatura;
	public static PluginQuark pluginQuark;
	public static PluginRustic pluginRustic;
	public static PluginTConstruct pluginTConstruct;

}
