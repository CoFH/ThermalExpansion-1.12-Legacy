package cofh.thermalexpansion.init;

import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalexpansion.block.machine.BlockMachine;
import cofh.thermalfoundation.block.BlockGlass;
import cofh.thermalfoundation.item.ItemMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.Achievement;
import net.minecraftforge.common.AchievementPage;

import java.util.ArrayList;

public class TEAchievements {

	private TEAchievements() {

	}

	public static void preInit() {

	}

	public static void initialize() {

		welcome = addAchievement("thermalexpansion:achievement_welcome", "thermalexpansion.welcome", 0, 0, BlockMachine.machineFurnace, null);

		ingotInvar = addAchievement("thermalexpansion:achievement_ingotInvar", "thermalexpansion.ingotInvar", 0, -1, ItemMaterial.ingotInvar, null);
		blockGlassHardened = addAchievement("thermalexpansion:achievement_blockGlassHardened", "thermalexpansion.blockGlassHardened", 0, -2, BlockGlass.glassLead, null);
		ingotSignalum = addAchievement("thermalexpansion:achievement_ingotSignalum", "thermalexpansion.ingotSignalum", 0, -3, ItemMaterial.ingotSignalum, null);
		ingotEnderium = addAchievement("thermalexpansion:achievement_ingotEnderium", "thermalexpansion.ingotEnderium", 0, -4, ItemMaterial.ingotEnderium, null);

		machineInsolator = addAchievement("thermalexpansion:achievement_insolator", "thermalexpansion.machineInsolator", -10, 0, BlockMachine.machineInsolator, null);
		machineTransposer = addAchievement("thermalexpansion:achievement_transposer", "thermalexpansion.machineTransposer", -9, 0, BlockMachine.machineTransposer, null);

		florb = addAchievement("thermalexpansion:achievement_florb", "thermalexpansion.florb", -1, 0, TEFlorbs.florb, null);
		florbMagmatic = addAchievement("thermalexpansion:achievement_florbMagmatic", "thermalexpansion.florbMagmatic", -1, -1, TEFlorbs.florbMagmatic, null);
	}

	public static void postInit() {

		if (!enable) {
			return;
		}
		achievementList.add(welcome);

		achievementList.add(ingotInvar);
		achievementList.add(blockGlassHardened);
		achievementList.add(ingotSignalum);
		achievementList.add(ingotEnderium);

		achievementList.add(machineInsolator);
		achievementList.add(machineTransposer);

		achievementList.add(florb);
		achievementList.add(florbMagmatic);

		achievementPage = new AchievementPage(ThermalExpansion.MOD_NAME, achievementList.toArray(new Achievement[achievementList.size()]));
		AchievementPage.registerAchievementPage(achievementPage);
	}

	/* HELPER */
	static Achievement addAchievement(String id, String localization, int xPos, int yPos, ItemStack stack, Achievement requirement) {

		return new Achievement(id, localization, xPos, yPos, stack, requirement).registerStat();
	}

	public static boolean enable = true;
	public static final ArrayList<Achievement> achievementList = new ArrayList<>();

	/* REFERENCES */
	public static AchievementPage achievementPage;

	public static Achievement welcome;

	public static Achievement ingotInvar;
	public static Achievement blockGlassHardened;
	public static Achievement ingotSignalum;
	public static Achievement ingotEnderium;

	public static Achievement machineInsolator;
	public static Achievement machineTransposer;

	public static Achievement wrench;
	public static Achievement multimeter;
	public static Achievement florb;
	public static Achievement florbMagmatic;

}
