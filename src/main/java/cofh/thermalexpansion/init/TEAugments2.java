package cofh.thermalexpansion.init;

import cofh.thermalexpansion.item.ItemAugment2;
import cofh.thermalfoundation.item.ItemMaterial;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;

import static cofh.lib.util.helpers.ItemHelper.ShapedRecipe;

public class TEAugments2 {

	public static byte NUM_DYNAMO_EFFICIENCY = 3;
	public static byte NUM_DYNAMO_OUTPUT = 3;

	public static byte NUM_ENERGY_STORAGE = 3;

	public static byte NUM_MACHINE_SECONDARY = 3;
	public static byte NUM_MACHINE_SPEED = 3;

	public static byte NUM_MACHINE_EXTRUDER = 3;
	public static byte NUM_MACHINE_CHARGER = 3;

	public static final int[] DYNAMO_EFFICIENCY_MOD = { 0, 10, 25, 50 };
	public static final int[] DYNAMO_EFFICIENCY_MOD_SUM = { 0, 10, 35, 85 };
	public static final int[] DYNAMO_OUTPUT_MOD = { 1, 2, 4, 8 };
	public static final int[] DYNAMO_OUTPUT_EFFICIENCY_MOD = { 0, 15, 10, 5 };
	public static final int[] DYNAMO_OUTPUT_EFFICIENCY_SUM = { 0, 15, 25, 30 };

	public static final int[] ENERGY_STORAGE_MOD = { 1, 2, 4, 8 };

	public static final int[] MACHINE_SPEED_PROCESS_MOD = { 1, 2, 4, 8 };
	public static final int[] MACHINE_SPEED_ENERGY_MOD = { 1, 3, 8, 20 };
	public static final int[] MACHINE_SPEED_ENERGY_MOD_TOOLTIP = { 1, 50, 100, 150 };
	// public static final int[] MACHINE_SPEED_SECONDARY_MOD = { 0, 5, 10, 15 }; TODO: May bring this back; not sure.
	// public static final int[] MACHINE_SPEED_SECONDARY_MOD_TOOLTIP = { 0, 5, 15, 25 };
	public static final int[] MACHINE_SECONDARY_MOD = { 0, 10, 15, 20 };
	public static final int[] MACHINE_SECONDARY_MOD_TOOLTIP = { 0, 11, 33, 81 };

	public static final int[][] MACHINE_EXTRUDER_PROCESS_MOD = { { 1, 16, 32, 64 }, { 1, 8, 16, 32 }, { 1, 4, 8, 16 } };
	public static final int[] MACHINE_EXTRUDER_WATER_MOD = { 1000, 500, 250, 125 };

	public static ItemAugment2 itemAugment;

	public static ItemStack dynamoCoilDuct;
	public static ItemStack[] dynamoEfficiency = new ItemStack[NUM_DYNAMO_EFFICIENCY];
	public static ItemStack[] dynamoOutput = new ItemStack[NUM_DYNAMO_OUTPUT];
	public static ItemStack dynamoThrottle;

	public static ItemStack enderEnergy;
	public static ItemStack enderFluid;
	public static ItemStack enderItem;

	public static ItemStack generalAutoOutput;
	public static ItemStack generalAutoInput;
	public static ItemStack generalReconfigSides;
	public static ItemStack generalRedstoneControl;

	public static ItemStack[] machineSecondary = new ItemStack[NUM_MACHINE_SECONDARY];
	public static ItemStack[] machineSpeed = new ItemStack[NUM_MACHINE_SPEED];

	public static ItemStack machineNull;

	public static ItemStack machineFurnaceFood;
	public static ItemStack[] machineExtruderBoost = new ItemStack[NUM_MACHINE_EXTRUDER];
	public static ItemStack[] machineChargerBoost = new ItemStack[NUM_MACHINE_CHARGER];

	/* Augment Helpers */
	public static String DYNAMO_COIL_DUCT = "dynamoCoilDuct";
	public static String DYNAMO_EFFICIENCY = "dynamoEfficiency";
	public static String DYNAMO_OUTPUT = "dynamoOutput";
	public static String DYNAMO_THROTTLE = "dynamoThrottle";

	public static String ENDER_ENERGY = "enderEnergy";
	public static String ENDER_FLUID = "enderFluid";
	public static String ENDER_ITEM = "enderItem";

	public static String ENERGY_STORAGE = "energyStorage";

	public static String MACHINE_SECONDARY = "machineSecondary";
	public static String MACHINE_SPEED = "machineSpeed";

	public static String MACHINE_NULL = "machineNull";

	public static String MACHINE_FURNACE_FOOD = "machineFurnaceFood";
	public static String MACHINE_EXTRUDER_BOOST = "machineExtruderBoost";
	public static String MACHINE_CHARGER_BOOST = "machineChargerBoost";

	private TEAugments2() {

	}

	public static void preInit() {

	}

	public static void initialize() {

	}

	public static void postInit() {

		/* GENERAL */
		//GameRegistry.addRecipe(ShapedRecipe(generalAutoOutput, " I ", "IXI", " I ", 'I', "nuggetTin", 'X', TEItemsOld.pneumaticServo));
		//GameRegistry.addRecipe(ShapedRecipe(generalAutoInput, " I ", "IXI", " I ", 'I', "nuggetIron", 'X', TEItemsOld.pneumaticServo));
		GameRegistry.addRecipe(ShapedRecipe(generalReconfigSides, " I ", "IXI", " I ", 'I', "nuggetTin", 'X', "ingotGold"));
		GameRegistry.addRecipe(ShapedRecipe(generalRedstoneControl, " I ", "IXI", " I ", 'I', "nuggetTin", 'X', "dustRedstone"));

		/* DYNAMO */
		GameRegistry.addRecipe(ShapedRecipe(dynamoCoilDuct, " I ", "IXI", " I ", 'I', "nuggetLead", 'X', "ingotCopper"));
		GameRegistry.addRecipe(ShapedRecipe(dynamoThrottle, " I ", "IXI", "YIY", 'I', "nuggetLead", 'X', "ingotElectrum", 'Y', "dustRedstone"));

		GameRegistry.addRecipe(ShapedRecipe(dynamoEfficiency[0], " N ", "NXN", "YNY", 'N', "ingotLead", 'X', ItemMaterial.powerCoilSilver, 'Y', "ingotTin", 'Y', "dustRedstone"));
		GameRegistry.addRecipe(ShapedRecipe(dynamoEfficiency[1], "ZIZ", "NXN", "YIY", 'N', "ingotLead", 'I', "ingotElectrum", 'X', ItemMaterial.powerCoilSilver, 'Y', "dustGlowstone", 'Z', "dustRedstone"));
		GameRegistry.addRecipe(ShapedRecipe(dynamoEfficiency[2], "ZIZ", "IXI", "YIY", 'I', "ingotElectrum", 'X', ItemMaterial.powerCoilSilver, 'Y', "dustCryotheum", 'Z', "dustGlowstone"));

		GameRegistry.addRecipe(ShapedRecipe(dynamoOutput[0], " N ", "NXN", "YNY", 'N', "ingotCopper", 'X', ItemMaterial.powerCoilSilver, 'Y', "dustRedstone"));
		GameRegistry.addRecipe(ShapedRecipe(dynamoOutput[1], "ZIZ", "NXN", "YIY", 'N', "ingotCopper", 'I', "ingotSilver", 'X', ItemMaterial.powerCoilSilver, 'Y', "dustGlowstone", 'Z', "dustRedstone"));
		GameRegistry.addRecipe(ShapedRecipe(dynamoOutput[2], "ZIZ", "IXI", "YIY", 'I', "ingotSilver", 'X', ItemMaterial.powerCoilSilver, 'Y', "dustCryotheum", 'Z', "dustGlowstone"));

		/* ENDER */

		/* ENERGY */

		/* MACHINE */
		GameRegistry.addRecipe(ShapedRecipe(machineSecondary[0], " N ", "NXN", "YNY", 'N', "ingotBronze", 'X', "blockCloth", 'Y', "blockCloth"));
		GameRegistry.addRecipe(ShapedRecipe(machineSecondary[1], "ZIZ", "NXN", "YIY", 'N', "ingotBronze", 'I', "blockGlassHardened", 'X', "blockClothRock", 'Y', "dustGlowstone", 'Z', "blockCloth"));
		//GameRegistry.addRecipe(ShapedRecipe(machineSecondary[2], "ZIZ", "IXI", "YIY", 'I', "blockGlassHardened", 'X', TEItemsOld.pneumaticServo, 'Y', "dustCryotheum", 'Z', "dustGlowstone"));

		GameRegistry.addRecipe(ShapedRecipe(machineSpeed[0], " N ", "NXN", "YNY", 'N', "ingotBronze", 'X', ItemMaterial.powerCoilGold, 'Y', "dustRedstone"));
		GameRegistry.addRecipe(ShapedRecipe(machineSpeed[1], "ZIZ", "NXN", "YIY", 'N', "ingotBronze", 'I', "ingotGold", 'X', ItemMaterial.powerCoilGold, 'Y', "dustPyrotheum", 'Z', "dustRedstone"));
		GameRegistry.addRecipe(ShapedRecipe(machineSpeed[2], "ZIZ", "IXI", "YIY", 'I', "ingotGold", 'X', ItemMaterial.powerCoilGold, 'Y', Items.ENDER_PEARL, 'Z', "dustPyrotheum"));

		GameRegistry.addRecipe(ShapedRecipe(machineNull, " I ", "NXN", "YIY", 'N', "ingotInvar", 'I', "ingotSilver", 'X', Items.LAVA_BUCKET, 'Y', "dustRedstone"));

		/* MACHINE SPECIFIC */
		GameRegistry.addRecipe(ShapedRecipe(machineFurnaceFood, " I ", "NXN", "YIY", 'N', "dustRedstone", 'I', "ingotSilver", 'X', ItemMaterial.powerCoilGold, 'Y', Blocks.BRICK_BLOCK));

		//GameRegistry.addRecipe(ShapedRecipe(machineExtruderBoost[0], " N ", "NXN", "YNY", 'N', "ingotBronze", 'X', TEItemsOld.pneumaticServo, 'Y', Blocks.COBBLESTONE));
		//GameRegistry.addRecipe(ShapedRecipe(machineExtruderBoost[1], "ZIZ", "NXN", "YIY", 'N', "ingotBronze", 'I', "ingotGold", 'X', TEItemsOld.pneumaticServo, 'Y', Blocks.STONE, 'Z', Blocks.COBBLESTONE));
		//GameRegistry.addRecipe(ShapedRecipe(machineExtruderBoost[2], "ZIZ", "IXI", "YIY", 'I', "ingotGold", 'X', TEItemsOld.pneumaticServo, 'Y', Blocks.OBSIDIAN, 'Z', Blocks.STONE));
	}

}
