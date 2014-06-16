package thermalexpansion.core;

import net.minecraft.stats.Achievement;
import net.minecraftforge.common.AchievementPage;

public class TEAchievements {

	public static AchievementPage pageTE;

	static Achievement[] achievementList;

	/* Machines */
	public static Achievement baseTE;

	public static Achievement furnace;
	public static Achievement pulverizer;
	public static Achievement sawmill;
	public static Achievement smelter;
	public static Achievement crucible;
	public static Achievement transposer;
	public static Achievement precipitator;
	public static Achievement extruder;
	public static Achievement waterGen;
	public static Achievement assembler;
	public static Achievement charger;

	public static Achievement glass;

	public static Achievement resonantCell;
	public static Achievement resonantTank;
	public static Achievement resonantCache;
	public static Achievement resonantStrongbox;

	public static Achievement fluidRedstone;
	public static Achievement fluidGlowstone;
	public static Achievement fluidEnder;
	public static Achievement fluidCoal;
	public static Achievement fluidPyrotheum;
	public static Achievement fluidCryotheum;

	/* Items */
	public static Achievement wrench;
	public static Achievement multimeter;
	public static Achievement florb;

	public static Achievement capacitorPotato;

	public static Achievement invar;

	/* Invar */
	// public static Achievement invarSword;
	// public static Achievement invarShovel;
	// public static Achievement invarPickaxe;
	// public static Achievement invarAxe;
	// public static Achievement invarHoe;
	// public static Achievement invarFishingRod;
	// public static Achievement invarSickle;

	public static void initialize() {

		// baseTE = new Achievement(Utils.getStatId(), "te.base", 0, 0, BlockMachine.pulverizer, null).registerAchievement();
		//
		// machineFrame = new Achievement(Utils.getStatId(), "te.machineFrame", 0, 2, BlockMachine.machineFrame, baseTE).registerAchievement();
		//
		// furnace = new Achievement(Utils.getStatId(), "te.furnace", -2, 2, BlockMachine.furnace, machineFrame).registerAchievement();
		// pulverizer = new Achievement(Utils.getStatId(), "te.pulverizer", -2, 3, BlockMachine.pulverizer, machineFrame).registerAchievement();
		// sawmill = new Achievement(Utils.getStatId(), "te.sawmill", -2, 4, BlockMachine.sawmill, machineFrame).registerAchievement();
		// smelter = new Achievement(Utils.getStatId(), "te.smelter", -2, 5, BlockMachine.smelter, machineFrame).registerAchievement();
		// crucible = new Achievement(Utils.getStatId(), "te.crucible", -2, 7, BlockMachine.crucible, machineFrame).registerAchievement();
		// transposer = new Achievement(Utils.getStatId(), "te.transposer", -2, 6, BlockMachine.transposer, machineFrame).registerAchievement();
		// precipitator = new Achievement(Utils.getStatId(), "te.precipitator", 2, 2, BlockMachine.precipitator, machineFrame).registerAchievement();
		// extruder = new Achievement(Utils.getStatId(), "te.extruder", 2, 3, BlockMachine.extruder, machineFrame).registerAchievement();
		// waterGen = new Achievement(Utils.getStatId(), "te.waterGen", 2, 4, BlockMachine.waterGen, machineFrame).registerAchievement();
		// assembler = new Achievement(Utils.getStatId(), "te.assembler", 2, 5, BlockMachine.assembler, machineFrame).registerAchievement();
		// charger = new Achievement(Utils.getStatId(), "te.charger", 2, 6, BlockMachine.charger, machineFrame).registerAchievement();
		//
		// wrench = new Achievement(Utils.getStatId(), "te.wrench", -2, -1, TEItems.toolWrench, baseTE).registerAchievement();
		// multimeter = new Achievement(Utils.getStatId(), "te.multimeter", 2, -1, TEItems.toolMultimeter, baseTE).registerAchievement();
		//
		// capacitorPotato = new Achievement(Utils.getStatId(), "te.potatoBattery", 4, 0, TEItems.capacitorPotato, baseTE).registerAchievement();
		//
		// fluidRedstone = new Achievement(Utils.getStatId(), "te.fluidRedstone", -3, 8, TEFluids.blockRedstone, null).registerAchievement();
		// fluidGlowstone = new Achievement(Utils.getStatId(), "te.fluidGlowstone", -3, 10, TEFluids.blockGlowstone, null).registerAchievement();
		// fluidEnder = new Achievement(Utils.getStatId(), "te.fluidEnder", -3, 12, TEFluids.blockEnder, null).registerAchievement();
		// fluidPyrotheum = new Achievement(Utils.getStatId(), "te.fluidPyrotheum", -1, 10, TEFluids.blockPyrotheum, null).registerAchievement().setSpecial();
		// fluidCryotheum = new Achievement(Utils.getStatId(), "te.fluidCryotheum", -1, 12, TEFluids.blockCryotheum, null).registerAchievement().setSpecial();
		// fluidCoal = new Achievement(Utils.getStatId(), "te.fluidCoal", -1, 8, TEFluids.blockCoal, null).registerAchievement();
		//
		// achievementList = new Achievement[] { baseTE, machineFrame, furnace, pulverizer, sawmill, smelter, crucible, transposer, precipitator, extruder,
		// waterGen,
		// assembler, charger, wrench, multimeter, capacitorPotato, fluidRedstone, fluidGlowstone, fluidEnder, fluidCoal, fluidPyrotheum, fluidCryotheum };
		//
		// pageTE = new AchievementPage("Thermal Expansion", achievementList);
		// AchievementPage.registerAchievementPage(pageTE);
	}

}
