package thermalexpansion.core;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidTankInfo;

public class TEProps {

	private TEProps() {

	}

	/* Graphics */
	public static final String PATH_GFX = "thermalexpansion:textures/";
	public static final String PATH_ARMOR = PATH_GFX + "armor/";
	public static final String PATH_GUI = PATH_GFX + "gui/";
	public static final String PATH_ENTITY = PATH_GFX + "entity/";
	public static final String PATH_RENDER = PATH_GFX + "blocks/";
	public static final String PATH_ELEMENTS = PATH_GUI + "elements/";

	public static final String PATH_GUI_DEVICE = PATH_GUI + "device/";
	public static final String PATH_GUI_DYNAMO = PATH_GUI + "dynamo/";
	public static final String PATH_GUI_ENDER = PATH_GUI + "ender/";
	public static final String PATH_GUI_MACHINE = PATH_GUI + "machine/";
	public static final String PATH_GUI_STRONGBOX = PATH_GUI + "strongbox/";

	public static final ResourceLocation PATH_COMMON = new ResourceLocation(PATH_ELEMENTS + "Slots.png");
	public static final ResourceLocation PATH_COMMON_CB = new ResourceLocation(PATH_ELEMENTS + "SlotsCB.png");
	public static final String PATH_ICON = PATH_GUI + "icons/";

	public static final String TEXTURE_DEFAULT = "Config_";
	public static final String TEXTURE_CB = "Config_CB_";

	public static ResourceLocation textureGuiCommon = PATH_COMMON;
	public static String textureSelection = TEXTURE_DEFAULT;

	public static final int LAVA_MAX_RF = 400000;

	public static enum Ores {
		COPPER, TIN, SILVER, LEAD, NICKEL
	}

	public static enum PacketID {
		GUI, FLUID, MODE, AUGMENT
	}

	public static boolean holidayChristmas = true;

	public static int lavaRF = 200000;

	public static int[] EMPTY_INVENTORY = new int[] {};
	public static FluidTankInfo[] EMPTY_TANK_INFO = new FluidTankInfo[] {};

	public static int[] oreMinY = new int[] { 40, 20, 5, 10, 5 };
	public static int[] oreMaxY = new int[] { 75, 55, 30, 35, 20 };
	public static int[] oreNumCluster = new int[] { 10, 8, 3, 4, 2 };
	public static int[] oreClusterSize = new int[] { 8, 8, 8, 8, 4 };

	public static boolean enableGuiBorders = true;
	public static boolean enableUpdateNotice = true;
	public static boolean enableDismantleLogging = false;
	public static boolean enableAchievements = false;
	public static boolean enableDebugOutput = false;

	/* Render Ids */
	public static int renderIdDynamo = -1;
	public static int renderIdCell = -1;
	public static int renderIdTank = -1;
	public static int renderIdTesseract = -1;
	public static int renderIdPlate = -1;
	public static int renderIdLamp = -1;

}
