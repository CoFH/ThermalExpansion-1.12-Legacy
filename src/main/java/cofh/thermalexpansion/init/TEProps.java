package cofh.thermalexpansion.init;

import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalexpansion.block.BlockTEBase;
import cofh.thermalexpansion.block.BlockTEBase.EnumSideConfig;
import cofh.thermalexpansion.gui.CreativeTabTE;
import cofh.thermalexpansion.gui.CreativeTabTEFlorbs;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.common.property.Properties;
import net.minecraftforge.fluids.Fluid;

public class TEProps {

	private TEProps() {

	}

	public static void preInit() {

		configClient();
	}

	public static void loadComplete() {

	}

	private static void configClient() {

		String category;
		String comment;

		category = "Interface";
		boolean itemTabCommon = false;
		boolean florbTabCommon = false;

		comment = "If TRUE, Thermal Expansion Items and Tools appear under the general \"Thermal Expansion\" Creative Tab.";
		itemTabCommon = ThermalExpansion.CONFIG_CLIENT.getConfiguration().getBoolean("ItemsInCommonTab", category, itemTabCommon, comment);

		comment = "If TRUE, Thermal Expansion Florbs appear under the general \"Thermal Expansion\" Creative Tab. Not really recommended.";
		florbTabCommon = ThermalExpansion.CONFIG_CLIENT.getConfiguration().getBoolean("FlorbsInCommonTab", category, florbTabCommon, comment);


		/* CREATIVE TABS */
		ThermalExpansion.tabCommon = new CreativeTabTE();

		if (itemTabCommon) {
			ThermalExpansion.tabItems = ThermalExpansion.tabCommon;
		} else {
			ThermalExpansion.tabItems = new CreativeTabTE("Items");
		}
		ThermalExpansion.tabFlorbs = florbTabCommon ? ThermalExpansion.tabCommon : new CreativeTabTEFlorbs();

	}

	/* GENERAL */
	public static final int MAX_FLUID_SMALL = Fluid.BUCKET_VOLUME * 4;
	public static final int MAX_FLUID_LARGE = Fluid.BUCKET_VOLUME * 10;
	public static final int MAGMATIC_TEMPERATURE = 1000;

	public static boolean enableAchievements = false;

	/* TEXTURES */
	public static final String PATH_GFX = "thermalexpansion:textures/";
	public static final String PATH_ARMOR = PATH_GFX + "armor/";
	public static final String PATH_GUI = PATH_GFX + "gui/";
	public static final String PATH_ENTITY = PATH_GFX + "entity/";
	public static final String PATH_RENDER = PATH_GFX + "blocks/";
	public static final String PATH_ELEMENTS = PATH_GUI + "elements/";

	public static final String PATH_GUI_AUTOMATON = PATH_GUI + "automaton/";
	public static final String PATH_GUI_DEVICE = PATH_GUI + "device/";
	public static final String PATH_GUI_DYNAMO = PATH_GUI + "dynamo/";
	public static final String PATH_GUI_MACHINE = PATH_GUI + "machine/";
	public static final String PATH_GUI_WORKBENCH = PATH_GUI + "workbench/";

	public static final ResourceLocation PATH_COMMON = new ResourceLocation(PATH_ELEMENTS + "slots.png");
	public static final ResourceLocation PATH_COMMON_CB = new ResourceLocation(PATH_ELEMENTS + "slots_cb.png");
	public static final ResourceLocation PATH_CENTRIFUGE = new ResourceLocation(PATH_ELEMENTS + "slots_centrifuge.png");
	public static final ResourceLocation PATH_CENTRIFUGE_CB = new ResourceLocation(PATH_ELEMENTS + "slots_centrifuge_cb.png");
	public static final ResourceLocation PATH_CRAFTER = new ResourceLocation(PATH_ELEMENTS + "slots_crafter.png");
	public static final ResourceLocation PATH_CRAFTER_CB = new ResourceLocation(PATH_ELEMENTS + "slots_crafter_cb.png");
	public static final String PATH_ICON = PATH_GUI + "icons/";

	public static final String TEXTURE_DEFAULT = "config_";
	public static final String TEXTURE_CB = "config_cb_";

	public static ResourceLocation textureGuiCommon = PATH_COMMON;
	public static ResourceLocation textureGuiCentrifuge = PATH_CENTRIFUGE;
	public static ResourceLocation textureGuiCrafter = PATH_CRAFTER;
	public static String textureSelection = TEXTURE_DEFAULT;
	public static boolean useAlternateStarfieldShader = false;

	/* COMMON BLOCK PROPERTIES */
	public static final IUnlistedProperty<Boolean> ACTIVE = Properties.toUnlisted(PropertyBool.create("active"));
	public static final IUnlistedProperty<EnumFacing> FACING = Properties.toUnlisted(PropertyEnum.<EnumFacing>create("facing", EnumFacing.class));
	public static final IUnlistedProperty<BlockTEBase.EnumSideConfig>[] SIDE_CONFIG = new IUnlistedProperty[6];

	static {
		for (int i = 0; i < 6; i++) {
			TEProps.SIDE_CONFIG[i] = Properties.toUnlisted(PropertyEnum.<EnumSideConfig>create("config_" + EnumFacing.VALUES[i].name(), EnumSideConfig.class));
		}
	}

}
