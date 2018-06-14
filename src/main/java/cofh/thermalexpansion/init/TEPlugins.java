package cofh.thermalexpansion.init;

import cofh.core.util.core.IInitializer;
import cofh.thermalexpansion.plugins.*;
import cofh.thermalexpansion.plugins.forestry.*;
import cofh.thermalexpansion.plugins.pam.PluginBoneCraft;
import cofh.thermalexpansion.plugins.pam.PluginHarvestCraft;
import cofh.thermalexpansion.plugins.pam.PluginRedbudTree;
import cofh.thermalexpansion.plugins.pam.PluginSpookyTree;
import cofh.thermalexpansion.plugins.top.PluginTOP;

import java.util.ArrayList;

public class TEPlugins {

	private TEPlugins() {

	}

	public static void preInit() {

		pluginTOP = new PluginTOP();

		pluginForestry = new PluginForestry();
		pluginExtraBees = new PluginExtraBees();
		pluginExtraTrees = new PluginExtraTrees();
		pluginGendustry = new PluginGendustry();
		pluginMagicBees = new PluginMagicBees();

		pluginHarvestCraft = new PluginHarvestCraft();
		pluginBoneCraft = new PluginBoneCraft();
		pluginRedbudTree = new PluginRedbudTree();
		pluginSpookyTree = new PluginSpookyTree();

		pluginAbyssalCraft = new PluginAbyssalCraft();
		pluginActuallyAdditions = new PluginActuallyAdditions();
		pluginAppliedEnergistics2 = new PluginAppliedEnergistics2();
		pluginAstralSorcery = new PluginAstralSorcery();
		pluginBetweenlands = new PluginBetweenlands();
		pluginBiomesOPlenty = new PluginBiomesOPlenty();
		pluginChisel = new PluginChisel();
		pluginComputronics = new PluginComputronics();
		pluginElementalDimensions = new PluginElementalDimensions();
		pluginEnderIO = new PluginEnderIO();
		pluginEvilCraft = new PluginEvilCraft();
		pluginExtraAlchemy = new PluginExtraAlchemy();
		pluginExU2 = new PluginExU2();
		pluginFamiliarFauna = new PluginFamiliarFauna();
		pluginIC2 = new PluginIC2();
		pluginIceAndFire = new PluginIceAndFire();
		pluginImmersiveEngineering = new PluginImmersiveEngineering();
		pluginIntegratedDynamics = new PluginIntegratedDynamics();
		pluginMowziesMobs = new PluginMowziesMobs();
		pluginMysticalAgriculture = new PluginMysticalAgriculture();
		pluginNatura = new PluginNatura();
		pluginPlants = new PluginPlants();
		pluginPrimalCore = new PluginPrimalCore();
		pluginQuark = new PluginQuark();
		pluginRustic = new PluginRustic();
		pluginTConstruct = new PluginTConstruct();
		pluginTechReborn = new PluginTechReborn();
		pluginTerraqueous = new PluginTerraqueous();
		pluginThaumcraft = new PluginThaumcraft();
		pluginTraverse = new PluginTraverse();
		pluginTropicraft = new PluginTropicraft();
		pluginTwilightForest = new PluginTwilightForest();

		initList.add(pluginTOP);

		initList.add(pluginForestry);
		initList.add(pluginExtraBees);
		initList.add(pluginExtraTrees);
		initList.add(pluginGendustry);
		initList.add(pluginMagicBees);

		initList.add(pluginHarvestCraft);
		initList.add(pluginBoneCraft);
		initList.add(pluginRedbudTree);
		initList.add(pluginSpookyTree);

		initList.add(pluginAbyssalCraft);
		initList.add(pluginActuallyAdditions);
		initList.add(pluginAppliedEnergistics2);
		initList.add(pluginAstralSorcery);
		initList.add(pluginBetweenlands);
		initList.add(pluginBiomesOPlenty);
		initList.add(pluginChisel);
		initList.add(pluginComputronics);
		initList.add(pluginElementalDimensions);
		initList.add(pluginEnderIO);
		initList.add(pluginEvilCraft);
		initList.add(pluginExtraAlchemy);
		initList.add(pluginExU2);
		initList.add(pluginFamiliarFauna);
		initList.add(pluginIC2);
		initList.add(pluginIceAndFire);
		initList.add(pluginImmersiveEngineering);
		initList.add(pluginIntegratedDynamics);
		initList.add(pluginMowziesMobs);
		initList.add(pluginMysticalAgriculture);
		initList.add(pluginNatura);
		initList.add(pluginPlants);
		initList.add(pluginPrimalCore);
		initList.add(pluginQuark);
		initList.add(pluginRustic);
		initList.add(pluginTConstruct);
		initList.add(pluginTechReborn);
		initList.add(pluginTerraqueous);
		initList.add(pluginThaumcraft);
		initList.add(pluginTraverse);
		initList.add(pluginTropicraft);
		initList.add(pluginTwilightForest);

		for (IInitializer init : initList) {
			init.preInit();
		}
	}

	public static void initialize() {

		for (IInitializer init : initList) {
			init.initialize();
		}
	}

	private static ArrayList<IInitializer> initList = new ArrayList<>();

	/* REFERENCES */
	private static PluginTOP pluginTOP;

	private static PluginForestry pluginForestry;
	private static PluginExtraBees pluginExtraBees;
	private static PluginExtraTrees pluginExtraTrees;
	private static PluginGendustry pluginGendustry;
	private static PluginMagicBees pluginMagicBees;

	private static PluginHarvestCraft pluginHarvestCraft;
	private static PluginBoneCraft pluginBoneCraft;
	private static PluginRedbudTree pluginRedbudTree;
	private static PluginSpookyTree pluginSpookyTree;

	private static PluginAbyssalCraft pluginAbyssalCraft;
	private static PluginActuallyAdditions pluginActuallyAdditions;
	private static PluginAppliedEnergistics2 pluginAppliedEnergistics2;
	private static PluginAstralSorcery pluginAstralSorcery;
	private static PluginBetweenlands pluginBetweenlands;
	private static PluginBiomesOPlenty pluginBiomesOPlenty;
	private static PluginChisel pluginChisel;
	private static PluginComputronics pluginComputronics;
	private static PluginElementalDimensions pluginElementalDimensions;
	private static PluginEnderIO pluginEnderIO;
	private static PluginEvilCraft pluginEvilCraft;
	private static PluginExtraAlchemy pluginExtraAlchemy;
	private static PluginExU2 pluginExU2;
	private static PluginFamiliarFauna pluginFamiliarFauna;
	private static PluginIC2 pluginIC2;
	private static PluginIceAndFire pluginIceAndFire;
	private static PluginImmersiveEngineering pluginImmersiveEngineering;
	private static PluginIntegratedDynamics pluginIntegratedDynamics;
	private static PluginMowziesMobs pluginMowziesMobs;
	private static PluginMysticalAgriculture pluginMysticalAgriculture;
	private static PluginNatura pluginNatura;
	private static PluginPlants pluginPlants;
	private static PluginPrimalCore pluginPrimalCore;
	private static PluginQuark pluginQuark;
	private static PluginRustic pluginRustic;
	private static PluginTConstruct pluginTConstruct;
	private static PluginTechReborn pluginTechReborn;
	private static PluginTerraqueous pluginTerraqueous;
	private static PluginThaumcraft pluginThaumcraft;
	private static PluginTraverse pluginTraverse;
	private static PluginTropicraft pluginTropicraft;
	private static PluginTwilightForest pluginTwilightForest;

}
