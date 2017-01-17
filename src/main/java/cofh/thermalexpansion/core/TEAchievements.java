package cofh.thermalexpansion.core;

import cofh.thermalexpansion.block.cache.BlockCache;
import cofh.thermalexpansion.block.cell.BlockCell;
import cofh.thermalexpansion.block.machine.BlockMachine;
import cofh.thermalexpansion.block.simple.BlockFrame;
import cofh.thermalexpansion.block.strongbox.BlockStrongbox;
import cofh.thermalexpansion.block.tank.BlockTank;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.Achievement;
import net.minecraftforge.common.AchievementPage;

import java.util.ArrayList;

public class TEAchievements {

	public static AchievementPage page;

	static ArrayList<Achievement> achievementList = new ArrayList<Achievement>();

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

	public static void initialize() {

		// Welcome Achievement
		base = new Achievement("cofh.te.base", "thermalexpansion.base", 0, 0, BlockMachine.furnace, null);

		// Crafting Step Achievements
		machineFrame = addAchievement("cofh.te.machineFrame", "thermalexpansion.machineFrame", 0, 2, BlockFrame.frameMachineBasic, base);

		// Machine Achievements
		furnace = addAchievement("cofh.te.furnace", "thermalexpansion.furnace", -2, 2, BlockMachine.furnace, machineFrame);
		pulverizer = addAchievement("cofh.te.pulverizer", "thermalexpansion.pulverizer", -2, 3, BlockMachine.pulverizer, machineFrame);
		sawmill = addAchievement("cofh.te.sawmill", "thermalexpansion.sawmill", -2, 4, BlockMachine.sawmill, machineFrame);
		smelter = addAchievement("cofh.te.smelter", "thermalexpansion.smelter", -2, 5, BlockMachine.smelter, machineFrame);
		crucible = addAchievement("cofh.te.crucible", "thermalexpansion.crucible", -2, 7, BlockMachine.crucible, machineFrame);
		transposer = addAchievement("cofh.te.transposer", "thermalexpansion.transposer", -2, 6, BlockMachine.transposer, machineFrame);
		precipitator = addAchievement("cofh.te.precipitator", "thermalexpansion.precipitator", 2, 2, BlockMachine.precipitator, machineFrame);
		extruder = addAchievement("cofh.te.extruder", "thermalexpansion.extruder", 2, 3, BlockMachine.extruder, machineFrame);
		accumulator = addAchievement("cofh.te.accumulator", "thermalexpansion.accumulator", 2, 4, BlockMachine.accumulator, machineFrame);
		assembler = addAchievement("cofh.te.assembler", "thermalexpansion.assembler", 2, 5, BlockMachine.assembler, machineFrame);
		charger = addAchievement("cofh.te.charger", "thermalexpansion.charger", 2, 6, BlockMachine.charger, machineFrame);
		insolator = addAchievement("cofh.te.insolator", "thermalexpansion.insolator", 2, 7, BlockMachine.insolator, machineFrame);

		// Resonant Achievements
		resonantCell = addAchievement("cofh.te.resonantCell", "thermalexpansion.resonantCell", 0, 3, BlockCell.cellResonant, null);
		resonantTank = addAchievement("cofh.te.resonantTank", "thermalexpansion.resonantTank", 0, 4, BlockTank.tankResonant, null);
		resonantCache = addAchievement("cofh.te.resonantCache", "thermalexpansion.resonantCache", 0, 5, BlockCache.cacheResonant, null);
		resonantStrongbox = addAchievement("cofh.te.resonantStrongbox", "thermalexpansion.resonantStrongbox", 0, 6, BlockStrongbox.strongboxResonant, null);

		// Page
		page = new AchievementPage("Thermal Expansion", (Achievement[]) achievementList.toArray());
		AchievementPage.registerAchievementPage(page);
	}

	static Achievement addAchievement(String id, String localization, int xPos, int yPos, ItemStack stack, Achievement requirement) {

		return new Achievement(id, localization, xPos, yPos, stack, requirement).registerStat();
	}

}
