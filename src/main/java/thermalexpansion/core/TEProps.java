package thermalexpansion.core;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidContainerRegistry;

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

	public static final ResourceLocation PATH_COMMON = new ResourceLocation(PATH_ELEMENTS + "Slots.png");
	public static final ResourceLocation PATH_COMMON_CB = new ResourceLocation(PATH_ELEMENTS + "SlotsCB.png");
	public static final ResourceLocation PATH_ASSEMBLER = new ResourceLocation(PATH_ELEMENTS + "SlotsAssembler.png");
	public static final ResourceLocation PATH_ASSEMBLER_CB = new ResourceLocation(PATH_ELEMENTS + "SlotsAssemblerCB.png");
	public static final String PATH_ICON = PATH_GUI + "icons/";

	public static final String TEXTURE_DEFAULT = "Config_";
	public static final String TEXTURE_CB = "Config_CB_";

	public static boolean colorBlind;

	public static ResourceLocation textureGuiCommon = PATH_COMMON;
	public static ResourceLocation textureGuiAssembler = PATH_ASSEMBLER;
	public static String textureSelection = TEXTURE_DEFAULT;

	public static final int MAX_FLUID_SMALL = FluidContainerRegistry.BUCKET_VOLUME * 4;
	public static final int MAX_FLUID_LARGE = FluidContainerRegistry.BUCKET_VOLUME * 10;
	public static final int MAGMATIC_TEMPERATURE = 1000;

	public static enum PacketID {
		GUI, FLUID, MODE
	}

	public static boolean holidayChristmas = true;

	public static int lavaRF = 200000;

	public static boolean enableGuiBorders = true;
	public static boolean enableAchievements = false;
	public static boolean enableDebugOutput = false;

	/* Render Ids */
	public static int renderIdCell = -1;
	public static int renderIdDynamo = -1;
	public static int renderIdFrame = -1;
	public static int renderIdLight = -1;
	public static int renderIdPlate = -1;
	public static int renderIdTank = -1;
	public static int renderIdEnder = -1;

}
