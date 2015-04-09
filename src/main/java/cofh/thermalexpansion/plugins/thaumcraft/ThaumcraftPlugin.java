package cofh.thermalexpansion.plugins.thaumcraft;

import static cofh.api.modhelpers.ThaumcraftHelper.parseAspects;

import cofh.asm.relauncher.Strippable;
import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalexpansion.block.machine.BlockMachine;
import cofh.thermalexpansion.block.simple.BlockFrame;
import cofh.thermalexpansion.block.simple.BlockGlass;
import cofh.thermalexpansion.block.simple.BlockRockwool;
import cofh.thermalexpansion.item.TEEquipment;
import cofh.thermalexpansion.item.TEFlorbs;
import cofh.thermalexpansion.item.TEItems;

public class ThaumcraftPlugin {

	public static void preInit() {

	}

	public static void initialize() {

	}

	public static void postInit() {

	}

	@Strippable("api:Thaumcraft|API")
	public static void loadComplete() throws Throwable {

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
		parseAspects(BlockGlass.glassHardenedIlluminated, "1 ignis, 1 tutamen, 1 vitreus, 1 lux");

		parseAspects(TEEquipment.toolInvarShears, "4 metallum, 4 harvest, 2 tutamen");
		parseAspects(TEEquipment.toolInvarFishingRod, "1 metallum, 1 aqua, 1 instrumentum");
		parseAspects(TEEquipment.toolInvarSickle, "5 metallum, 4 tutamen, 4 harvest");

		parseAspects(TEItems.toolWrench, "4 metallum, 1 instrumentum");
		parseAspects(TEItems.toolMultimeter, "4 metallum, 2 potentia, 3 machina, 2 sensus, 2 instrumentum");
		parseAspects(TEItems.toolBattleWrench, "6 metallum, 1 instrumentum, 1 machina, 3 telum");

		parseAspects(TEItems.diagramSchematic, "3 cognitio");
		parseAspects(TEItems.diagramRedprint, "3 cognitio, 1 potentia");
		parseAspects(TEItems.diagramPattern, "3 cognitio, 1 potentia");

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

		ThermalExpansion.log.info("Thermal Expansion: Thaumcraft Plugin Enabled.");
	}

}
