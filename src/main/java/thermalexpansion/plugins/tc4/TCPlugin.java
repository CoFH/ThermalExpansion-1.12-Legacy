package thermalexpansion.plugins.tc4;

import static cofh.api.modhelpers.ThaumcraftHelper.parseAspects;

import thermalexpansion.ThermalExpansion;
import thermalexpansion.block.machine.BlockMachine;
import thermalexpansion.block.simple.BlockFrame;
import thermalexpansion.block.simple.BlockGlass;
import thermalexpansion.block.simple.BlockRockwool;
import thermalexpansion.item.TEEquipment;
import thermalexpansion.item.TEFlorbs;
import thermalexpansion.item.TEItems;
import thermalfoundation.block.BlockOre;
import thermalfoundation.block.BlockStorage;
import thermalfoundation.item.TFItems;

public class TCPlugin {

	public static void preInit() {

	}

	public static void initialize() {

	}

	public static void postInit() throws Throwable {

		parseAspects(TFItems.dustCoal, "2 ignis, 1 potentia, 1 perditio");
		parseAspects(TFItems.dustObsidian, "2 ignis, 1 tenebrae, 1 perditio");
		parseAspects(TFItems.dustCopper, "1 metallum, 1 perditio");
		parseAspects(TFItems.dustTin, "1 metallum, 1 perditio");
		parseAspects(TFItems.dustSilver, "1 metallum, 1 perditio");
		parseAspects(TFItems.dustLead, "1 metallum, 1 perditio");
		parseAspects(TFItems.dustNickel, "1 metallum, 1 perditio");
		parseAspects(TFItems.dustPlatinum, "1 metallum, 1 perditio");
		parseAspects(TFItems.dustMithril, "1 metallum, 1 perditio");
		parseAspects(TFItems.dustElectrum, "1 metallum, 1 perditio");
		parseAspects(TFItems.dustInvar, "1 metallum, 1 perditio");
		parseAspects(TFItems.dustBronze, "1 metallum, 1 perditio");
		parseAspects(TFItems.dustSignalum, "1 metallum, 1 perditio");
		parseAspects(TFItems.dustLumium, "1 metallum, 1 perditio");
		parseAspects(TFItems.dustEnderium, "1 metallum, 1 perditio");

		parseAspects(TFItems.ingotCopper, "3 metallum, 1 vacuos");
		parseAspects(TFItems.ingotTin, "3 metallum, 1 lucrum");
		parseAspects(TFItems.ingotSilver, "3 metallum, 1 lucrum");
		parseAspects(TFItems.ingotLead, "3 metallum, 1 tutamen");
		parseAspects(TFItems.ingotNickel, "3 metallum, 1 vacuos");
		parseAspects(TFItems.ingotPlatinum, "3 metallum, 1 lucrum");
		parseAspects(TFItems.ingotMithril, "3 metallum, 1 praecantatio");
		parseAspects(TFItems.ingotElectrum, "3 metallum, 1 lucrum");
		parseAspects(TFItems.ingotInvar, "3 metallum, 1 tutamen");
		parseAspects(TFItems.ingotBronze, "3 metallum, 1 fabrico");
		parseAspects(TFItems.ingotSignalum, "3 metallum, 1 potentia");
		parseAspects(TFItems.ingotLumium, "3 metallum, 1 lux");
		parseAspects(TFItems.ingotEnderium, "3 metallum, 1 alienis");

		parseAspects(TFItems.nuggetCopper, "1 metallum");
		parseAspects(TFItems.nuggetTin, "1 metallum");
		parseAspects(TFItems.nuggetSilver, "1 metallum");
		parseAspects(TFItems.nuggetLead, "1 metallum");
		parseAspects(TFItems.nuggetNickel, "1 metallum");
		parseAspects(TFItems.nuggetPlatinum, "1 metallum");
		parseAspects(TFItems.nuggetMithril, "1 metallum");
		parseAspects(TFItems.nuggetElectrum, "1 metallum");
		parseAspects(TFItems.nuggetInvar, "1 metallum");
		parseAspects(TFItems.nuggetBronze, "1 metallum");
		parseAspects(TFItems.nuggetSignalum, "1 metallum");
		parseAspects(TFItems.nuggetLumium, "1 metallum");
		parseAspects(TFItems.nuggetEnderium, "1 metallum");

		parseAspects(TFItems.gearCopper, "7 metallum, 2 machina, 2 permutatio");
		parseAspects(TFItems.gearTin, "7 metallum, 2 machina, 2 vitreus");
		parseAspects(TFItems.gearSilver, "7 metallum, 2 machina, 2 vitreus");
		parseAspects(TFItems.gearLead, "7 metallum, 2 machina, 2 tutamen");
		parseAspects(TFItems.gearNickel, "7 metallum, 2 machina, 2 permutatio");
		parseAspects(TFItems.gearPlatinum, "7 metallum, 2 machina, 2 vitreus");
		parseAspects(TFItems.gearMithril, "7 metallum, 2 machina, 2 praecantatio");
		parseAspects(TFItems.gearElectrum, "7 metallum, 2 machina, 2 lucrum");
		parseAspects(TFItems.gearInvar, "7 metallum, 2 machina, 2 tutamen");
		parseAspects(TFItems.gearBronze, "7 metallum, 2 machina, 2 fabrico");
		parseAspects(TFItems.gearSignalum, "7 metallum, 2 machina, 2 potentia");
		parseAspects(TFItems.gearLumium, "7 metallum, 2 machina, 2 lux");
		parseAspects(TFItems.gearEnderium, "7 metallum, 2 machina, 2 alienis");

		parseAspects(BlockOre.oreCopper, "2 metallum, 1 perditio, 1 vacuos");
		parseAspects(BlockOre.oreTin, "2 metallum, 1 perditio, 1 lucrum");
		parseAspects(BlockOre.oreSilver, "2 metallum, 1 perditio, 1 lucrum");
		parseAspects(BlockOre.oreLead, "2 metallum, 1 perditio, 1 tutamen");
		parseAspects(BlockOre.oreNickel, "2 metallum, 1 perditio, 1 vacuos");
		parseAspects(BlockOre.orePlatinum, "2 metallum, 1 perditio, 1 lucrum");
		parseAspects(BlockOre.oreMithril, "2 metallum, 1 perditio, 1 praecantatio");

		parseAspects(BlockStorage.blockCopper, "11 metallum, 5 vacuos");
		parseAspects(BlockStorage.blockTin, "11 metallum, 5 lucrum");
		parseAspects(BlockStorage.blockSilver, "11 metallum, 5 lucrum");
		parseAspects(BlockStorage.blockLead, "11 metallum, 5 tutamen");
		parseAspects(BlockStorage.blockNickel, "11 metallum, 5 vacuos");
		parseAspects(BlockStorage.blockPlatinum, "11 metallum, 5 lucrum");
		parseAspects(BlockStorage.blockMithril, "11 metallum, 5 praecantatio");
		parseAspects(BlockStorage.blockElectrum, "11 metallum, 5 lucrum");
		parseAspects(BlockStorage.blockInvar, "11 metallum, 5 tutamen");
		parseAspects(BlockStorage.blockBronze, "11 metallum, 5 fabrico");
		parseAspects(BlockStorage.blockSignalum, "11 metallum, 5 potentia");
		parseAspects(BlockStorage.blockLumium, "11 metallum, 5 lux");
		parseAspects(BlockStorage.blockEnderium, "11 metallum, 5 alienis");

		parseAspects(TFItems.bucketRedstone, "8 metallum, 10 potentia, 8 machina, 4 ignis, 2 aqua");
		parseAspects(TFItems.bucketGlowstone, "8 metallum, 10 lux, 8 sensus, 4 ignis, 2 aqua");
		parseAspects(TFItems.bucketEnder, "8 metallum, 14 alienis, 8 iter, 4 ignis, 2 praecantatio, 2 aqua");
		parseAspects(TFItems.bucketPyrotheum, "8 metallum, 16 ignis, 14 potentia, 2 praecantatio, 2 aqua");
		parseAspects(TFItems.bucketCryotheum, "8 metallum, 16 gelum, 14 potentia, 2 praecantatio, 2 aqua");
		parseAspects(TFItems.bucketMana, "8 metallum, 16 praecantatio, 8 potentia, 8 perditio, 2 aqua");
		parseAspects(TFItems.bucketCoal, "8 metallum, 14 ignis, 2 potentia, 2 vacuos, 2 aqua");

		parseAspects(TFItems.dustSulfur, "3 ignis, 1 terra");
		parseAspects(TFItems.dustNiter, "3 aer, 1 terra");
		parseAspects(TFItems.crystalCinnabar, "2 terra, 1 permutatio, 1 venenum");

		parseAspects(TFItems.dustPyrotheum, "4 potentia, 4 ignis, 1 praecantatio");
		parseAspects(TFItems.dustCryotheum, "4 potentia, 4 gelum, 1 praecantatio");
		parseAspects(TFItems.rodBlizz, "4 gelum, 2 praecantatio");
		parseAspects(TFItems.dustBlizz, "2 gelum, 1 praecantatio");

		parseAspects(TEFlorbs.florb, "1 terra, 2 limus, 1 vacuos");
		parseAspects(TEFlorbs.florbMagmatic, "1 terra, 1 ignis, 2 limus, 1 vacuos");

		parseAspects(TEItems.pneumaticServo, "4 metallum, 2 machina, 1 potentia, 4 motus");
		parseAspects(TEItems.powerCoilGold, "4 metallum, 2 machina, 2 potentia");
		parseAspects(TEItems.powerCoilSilver, "4 metallum, 2 machina, 2 potentia");
		parseAspects(TEItems.powerCoilElectrum, "4 metallum, 2 machina, 2 potentia");
		parseAspects(TEItems.slag, "2 terra, 2 perditio");
		parseAspects(TEItems.slagRich, "2 terra, 2 perditio, 2 lucrum");
		parseAspects(TEItems.sawdust, "1 arbor, 1 perditio");
		parseAspects(TEItems.sawdustCompressed, "3 arbor, 3 perditio");

		parseAspects(BlockRockwool.rockWool, "1 fabrico, 2 pannus, 2 perditio");
		parseAspects(BlockGlass.glassHardened, "1 ignis, 1 tutamen, 1 vitreus");

		parseAspects(TEEquipment.toolInvarShears, "4 metallum, 4 harvest, 2 tutamen");
		parseAspects(TEEquipment.toolInvarFishingRod, "1 metallum, 1 aqua, 1 instrumentum");
		parseAspects(TEEquipment.toolInvarSickle, "5 metallum, 4 tutamen, 4 harvest");
		parseAspects(TEEquipment.toolInvarBattleWrench, "6 metallum, 1 instrumentum, 1 machina, 3 telum");

		parseAspects(TEItems.toolWrench, "4 metallum, 1 instrumentum");
		parseAspects(TEItems.toolMultimeter, "4 metallum, 2 potentia, 3 machina, 2 sensus, 2 instrumentum");

		parseAspects(TEItems.diagramSchematic, "3 cognitio");
		parseAspects(TEItems.diagramRedprint, "3 cognitio, 1 potentia");

		parseAspects(TEItems.capacitorPotato, "1 fames, 1 terra, 1 messis, 3 potentia, 1 machina");
		parseAspects(TEItems.capacitorBasic, "4 metallum, 2 ordo, 2 permutatio, 6 potentia, 3 machina, 1 ignis");
		parseAspects(TEItems.capacitorHardened, "7 metallum, 2 ordo, 2 permutatio, 9 potentia, 3 machina, 1 ignis, 1 tutamen");
		parseAspects(TEItems.capacitorReinforced, "10 metallum, 2 ordo, 2 permutatio, 12 potentia, 3 machina, 1 ignis, 1 tutamen, 4 vitreus");
		parseAspects(TEItems.capacitorResonant,
				"13 metallum, 2 ordo, 2 permutatio, 15 potentia, 3 machina, 1 ignis, 1 tutamen, 4 vitreus, 1 alienis, 2 praecantatio");

		parseAspects(BlockFrame.frameMachineBasic, "4 metallum, 2 vitreus, 4 machina");
		parseAspects(BlockMachine.furnace, "8 metallum, 2 vitreus, 6 machina, 2 potentia, 4 ignis, 2 fabrico");
		parseAspects(BlockMachine.pulverizer, "8 metallum, 2 vitreus, 6 machina, 2 potentia, 4 perditio, 2 motus");
		parseAspects(BlockMachine.sawmill, "8 metallum, 2 vitreus, 6 machina, 2 potentia, 4 instrumentum, 2 arbor");
		parseAspects(BlockMachine.smelter, "8 metallum, 2 vitreus, 6 machina, 2 potentia, 4 ignis, 2 permutatio");
		parseAspects(BlockMachine.crucible, "8 metallum, 2 vitreus, 6 machina, 2 potentia, 4 ignis, 2 aqua");
		parseAspects(BlockMachine.transposer, "8 metallum, 2 vitreus, 6 machina, 2 potentia, 4 aqua, 2 motus");
		parseAspects(BlockMachine.precipitator, "8 metallum, 2 vitreus, 6 machina, 2 potentia, 4 gelum, 2 aqua");
		parseAspects(BlockMachine.extruder, "8 metallum, 2 vitreus, 6 machina, 2 potentia, 4 perditio, 1 aqua, 1 ignis");
		parseAspects(BlockMachine.accumulator, "8 metallum, 2 vitreus, 6 machina, 2 potentia, 4 gelum, 2 aqua");
		parseAspects(BlockMachine.assembler, "8 metallum, 2 vitreus, 6 machina, 2 potentia, 4 fabrico, 2 motus");
		parseAspects(BlockMachine.charger, "8 metallum, 2 vitreus, 6 machina, 6 potentia, 2 permutatio");

		ThermalExpansion.log.info("Thaumcraft Plugin Enabled.");
	}

}
