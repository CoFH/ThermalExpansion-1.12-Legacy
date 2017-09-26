package cofh.thermalexpansion.init;

import cofh.core.util.core.IInitializer;
import cofh.thermalexpansion.plugins.*;
import cofh.thermalexpansion.plugins.forestry.*;
import cofh.thermalexpansion.plugins.top.PluginTOP;

import java.util.ArrayList;

public class TEPlugins {

	private TEPlugins() {

	}

	public static void initialize() {

		pluginTOP = new PluginTOP();

		pluginForestry = new PluginForestry();
		pluginExtraBees = new PluginExtraBees();
		pluginExtraTrees = new PluginExtraTrees();
		pluginGendustry = new PluginGendustry();
		pluginMagicBees = new PluginMagicBees();

		pluginActuallyAdditions = new PluginActuallyAdditions();
		pluginBiomesOPlenty = new PluginBiomesOPlenty();
		pluginHarvestcraft = new PluginHarvestcraft();
		pluginIC2 = new PluginIC2();
		pluginNatura = new PluginNatura();
		pluginPlants = new PluginPlants();
		pluginPlantsLegacy = new PluginPlantsLegacy();
		pluginQuark = new PluginQuark();
		pluginRustic = new PluginRustic();
		pluginTConstruct = new PluginTConstruct();
		pluginTraverse = new PluginTraverse();
		pluginTechReborn = new PluginTechReborn();

		initList.add(pluginTOP);

		initList.add(pluginForestry);
		initList.add(pluginExtraBees);
		initList.add(pluginExtraTrees);
		initList.add(pluginGendustry);
		initList.add(pluginMagicBees);

		initList.add(pluginActuallyAdditions);
		initList.add(pluginBiomesOPlenty);
		initList.add(pluginHarvestcraft);
		initList.add(pluginIC2);
		initList.add(pluginNatura);
		initList.add(pluginPlants);
		initList.add(pluginPlantsLegacy);
		initList.add(pluginQuark);
		initList.add(pluginRustic);
		initList.add(pluginTConstruct);
		initList.add(pluginTraverse);
		initList.add(pluginTechReborn);

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
	public static PluginTOP pluginTOP;

	public static PluginForestry pluginForestry;
	public static PluginExtraBees pluginExtraBees;
	public static PluginExtraTrees pluginExtraTrees;
	public static PluginGendustry pluginGendustry;
	public static PluginMagicBees pluginMagicBees;

	public static PluginActuallyAdditions pluginActuallyAdditions;
	public static PluginBiomesOPlenty pluginBiomesOPlenty;
	public static PluginHarvestcraft pluginHarvestcraft;
	public static PluginIC2 pluginIC2;
	public static PluginNatura pluginNatura;
	public static PluginPlants pluginPlants;
	public static PluginPlantsLegacy pluginPlantsLegacy;
	public static PluginQuark pluginQuark;
	public static PluginRustic pluginRustic;
	public static PluginTConstruct pluginTConstruct;
	public static PluginTraverse pluginTraverse;
	public static PluginTechReborn pluginTechReborn;

}
