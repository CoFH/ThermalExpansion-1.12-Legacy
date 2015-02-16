package cofh.thermalexpansion.core;

import cofh.thermalexpansion.block.cache.BlockCache;
import cofh.thermalexpansion.block.cell.BlockCell;
import cofh.thermalexpansion.block.machine.BlockMachine;
import cofh.thermalexpansion.block.simple.BlockFrame;
import cofh.thermalexpansion.block.strongbox.BlockStrongbox;
import cofh.thermalexpansion.block.tank.BlockTank;
import cofh.thermalexpansion.item.TEItems;

import net.minecraft.stats.Achievement;
import net.minecraftforge.common.AchievementPage;

public class TEAchievements {

	public static AchievementPage page;

	static Achievement[] achievementList;

	/* Machines */
	public static Achievement base;

	public static Achievement machineFrame;

	public static Achievement furnace;
	public static Achievement pulverizer;
	public static Achievement sawmill;
	public static Achievement smelter;
	public static Achievement crucible;
	public static Achievement transposer;
	public static Achievement precipitator;
	public static Achievement extruder;
	public static Achievement accumulator;
	public static Achievement assembler;
	public static Achievement charger;
	public static Achievement insolator;

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

	public static void initialize() {

		// Welcome Achievement
		base = new Achievement("cofh.te.base", "thermalexpansion.base", 0, 0, BlockMachine.furnace, null).registerStat();

		// Crafting Step Achievements
		machineFrame = new Achievement("cofh.te.machineFrame", "thermalexpansion.machineFrame", 0, 2, BlockFrame.frameMachineBasic, base).registerStat();

		// Machine Achievements
		furnace = new Achievement("cofh.te.furnace", "thermalexpansion.furnace", -2, 2, BlockMachine.furnace, machineFrame).registerStat();
		pulverizer = new Achievement("cofh.te.pulverizer", "thermalexpansion.pulverizer", -2, 3, BlockMachine.pulverizer, machineFrame).registerStat();
		sawmill = new Achievement("cofh.te.sawmill", "thermalexpansion.sawmill", -2, 4, BlockMachine.sawmill, machineFrame).registerStat();
		smelter = new Achievement("cofh.te.smelter", "thermalexpansion.smelter", -2, 5, BlockMachine.smelter, machineFrame).registerStat();
		crucible = new Achievement("cofh.te.crucible", "thermalexpansion.crucible", -2, 7, BlockMachine.crucible, machineFrame).registerStat();
		transposer = new Achievement("cofh.te.transposer", "thermalexpansion.transposer", -2, 6, BlockMachine.transposer, machineFrame).registerStat();
		precipitator = new Achievement("cofh.te.precipitator", "thermalexpansion.precipitator", 2, 2, BlockMachine.precipitator, machineFrame).registerStat();
		extruder = new Achievement("cofh.te.extruder", "thermalexpansion.extruder", 2, 3, BlockMachine.extruder, machineFrame).registerStat();
		accumulator = new Achievement("cofh.te.accumulator", "thermalexpansion.accumulator", 2, 4, BlockMachine.accumulator, machineFrame).registerStat();
		assembler = new Achievement("cofh.te.assembler", "thermalexpansion.assembler", 2, 5, BlockMachine.assembler, machineFrame).registerStat();
		charger = new Achievement("cofh.te.charger", "thermalexpansion.charger", 2, 6, BlockMachine.charger, machineFrame).registerStat();
		insolator = new Achievement("cofh.te.insolator", "thermalexpansion.insolator", 2, 6, BlockMachine.charger, machineFrame).registerStat();

		// Resonant Achievements
		resonantCell = new Achievement("cofh.te.resonantCell", "thermalexpansion.resonantCell", 0, 3, BlockCell.cellResonant, null).registerStat();
		resonantTank = new Achievement("cofh.te.resonantTank", "thermalexpansion.resonantTank", 0, 4, BlockTank.tankResonant, null).registerStat();
		resonantCache = new Achievement("cofh.te.resonantCache", "thermalexpansion.resonantCache", 0, 5, BlockCache.cacheResonant, null).registerStat();
		resonantStrongbox = new Achievement("cofh.te.resonantStrongbox", "thermalexpansion.resonantStrongbox", 0, 6, BlockStrongbox.strongboxResonant, null)
		.registerStat();

		// Tool Achievements
		wrench = new Achievement("cofh.te.wrench", "thermalexpansion.wrench", -2, -1, TEItems.toolWrench, base).registerStat();
		multimeter = new Achievement("cofh.te.multimeter", "thermalexpansion.multimeter", 2, -1, TEItems.toolMultimeter, base).registerStat();
		//
		// capacitorPotato = new Achievement(Utils.getStatId(), "te.potatoBattery", 4, 0, TEItems.capacitorPotato, baseTE).registerAchievement();
		//
		// fluidRedstone = new Achievement(Utils.getStatId(), "te.fluidRedstone", -3, 8, TEFluids.blockRedstone, null).registerAchievement();
		// fluidGlowstone = new Achievement(Utils.getStatId(), "te.fluidGlowstone", -3, 10, TEFluids.blockGlowstone, null).registerAchievement();
		// fluidEnder = new Achievement(Utils.getStatId(), "te.fluidEnder", -3, 12, TEFluids.blockEnder, null).registerAchievement();
		// fluidPyrotheum = new Achievement(Utils.getStatId(), "te.fluidPyrotheum", -1, 10, TEFluids.blockPyrotheum, null).registerAchievement().setSpecial();
		// fluidCryotheum = new Achievement(Utils.getStatId(), "te.fluidCryotheum", -1, 12, TEFluids.blockCryotheum, null).registerAchievement().setSpecial();
		// fluidCoal = new Achievement(Utils.getStatId(), "te.fluidCoal", -1, 8, TFFluids.blockCoal, null).registerAchievement();

		// achievementList = new Achievement[] { base, machineFrame, furnace, pulverizer, sawmill, smelter, crucible, transposer, precipitator, extruder,
		// accumulator, assembler, charger, wrench, multimeter, capacitorPotato, fluidRedstone, fluidGlowstone, fluidEnder, fluidCoal, fluidPyrotheum,
		// fluidCryotheum };

		achievementList = new Achievement[] { base, machineFrame, resonantCell, resonantTank, resonantCache, resonantStrongbox };

		page = new AchievementPage("Thermal Expansion", achievementList);
		AchievementPage.registerAchievementPage(page);
	}
}
