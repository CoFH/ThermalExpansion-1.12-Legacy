package thermalexpansion.item;

import net.minecraft.item.ItemStack;

public class TEAugments {

	private TEAugments() {

	}

	public static void preInit() {

		itemAugment = (ItemAugment) new ItemAugment().setUnlocalizedName("augment");
	}

	public static void initialize() {

		generalAutoTransfer = itemAugment.addItem(0, GENERAL_AUTO_TRANSFER);
		generalReconfigSides = itemAugment.addItem(16, GENERAL_RECONFIG_SIDES);
		generalRedstoneControl = itemAugment.addItem(32, GENERAL_REDSTONE_CONTROL);

		itemAugment.addAugmentInfo(0, GENERAL_AUTO_TRANSFER, 1);
		itemAugment.addAugmentInfo(16, GENERAL_RECONFIG_SIDES, 1);
		itemAugment.addAugmentInfo(32, GENERAL_REDSTONE_CONTROL, 1);

		dynamoCoilDuct = itemAugment.addItem(48, DYNAMO_COIL_DUCT);
		itemAugment.addAugmentInfo(48, DYNAMO_COIL_DUCT, 1);

		for (int i = 0; i < NUM_DYNAMO_EFFICIENCY; i++) {
			dynamoEfficiency[i] = itemAugment.addItem(64 + i, DYNAMO_EFFICIENCY + i);
			itemAugment.addAugmentInfo(64 + i, DYNAMO_EFFICIENCY, 1 + i);
		}
		for (int i = 0; i < NUM_DYNAMO_OUTPUT; i++) {
			dynamoOutput[i] = itemAugment.addItem(80 + i, DYNAMO_OUTPUT + i);
			itemAugment.addAugmentInfo(80 + i, DYNAMO_OUTPUT, 1 + i);
		}
		dynamoThrottle = itemAugment.addItem(96, DYNAMO_THROTTLE);
		itemAugment.addAugmentInfo(96, DYNAMO_THROTTLE, 1);

		for (int i = 0; i < NUM_MACHINE_SECONDARY; i++) {
			machineSecondary[i] = itemAugment.addItem(112 + i, MACHINE_SECONDARY + i);
			itemAugment.addAugmentInfo(112 + i, MACHINE_SECONDARY, 1 + i);
		}
		for (int i = 0; i < NUM_MACHINE_SPEED; i++) {
			dynamoOutput[i] = itemAugment.addItem(128 + i, MACHINE_SPEED + i);
			itemAugment.addAugmentInfo(128 + i, MACHINE_SPEED, 1 + i);
		}
	}

	public static void postInit() {

	}

	public static byte NUM_DYNAMO_EFFICIENCY = 3;
	public static byte NUM_DYNAMO_OUTPUT = 3;

	public static byte NUM_MACHINE_SECONDARY = 3;
	public static byte NUM_MACHINE_SPEED = 3;

	public static final int[] MACHINE_SPEED_PROCESS_MOD = { 1, 2, 4, 8 };
	public static final int[] MACHINE_SPEED_ENERGY_MOD = { 1, 3, 8, 20 };

	public static ItemAugment itemAugment;

	public static ItemStack dynamoCoilDuct;
	public static ItemStack[] dynamoEfficiency = new ItemStack[NUM_DYNAMO_EFFICIENCY];
	public static ItemStack[] dynamoOutput = new ItemStack[NUM_DYNAMO_OUTPUT];
	public static ItemStack dynamoThrottle;

	public static ItemStack enderEnergy;
	public static ItemStack enderFluid;
	public static ItemStack enderItem;

	public static ItemStack generalAutoTransfer;
	public static ItemStack generalReconfigSides;
	public static ItemStack generalRedstoneControl;

	public static ItemStack[] machineSecondary = new ItemStack[NUM_MACHINE_SECONDARY];
	public static ItemStack[] machineSpeed = new ItemStack[NUM_MACHINE_SPEED];

	/* Augment Helpers */
	public static String DYNAMO_COIL_DUCT = "dynamoCoilDuct";
	public static String DYNAMO_EFFICIENCY = "dynamoEfficiency";
	public static String DYNAMO_OUTPUT = "dynamoOutput";
	public static String DYNAMO_THROTTLE = "dynamoThrottle";

	public static String ENDER_ENERGY = "enderEnergy";
	public static String ENDER_FLUID = "enderFluid";
	public static String ENDER_ITEM = "enderItem";

	public static String GENERAL_AUTO_TRANSFER = "generalAutoTransfer";
	public static String GENERAL_RECONFIG_SIDES = "generalReconfigSides";
	public static String GENERAL_REDSTONE_CONTROL = "generalRedstoneControl";

	public static String MACHINE_SECONDARY = "machineSecondary";
	public static String MACHINE_SPEED = "machineSpeed";
}
