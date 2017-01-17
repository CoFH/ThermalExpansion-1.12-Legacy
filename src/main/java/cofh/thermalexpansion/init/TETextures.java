package cofh.thermalexpansion.init;

import codechicken.lib.texture.TextureUtils.IIconRegister;
import cofh.core.CoFHProps;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.ResourceLocation;

/**
 * Holds references for TE's Textures.
 *
 * TODO Order entries based on Registry order.
 *
 * Created by covers1624 on 17/01/2017.
 */
public class TETextures implements IIconRegister {

	@Override
	public void registerIcons(TextureMap map) {

		CONFIG_NONE = register(map, CONFIG_ + "none");
		CONFIG_BLUE = registerCB(map, CONFIG_ + "blue");
		CONFIG_RED = registerCB(map, CONFIG_ + "red");
		CONFIG_YELLOW = registerCB(map, CONFIG_ + "yellow");
		CONFIG_ORANGE = registerCB(map, CONFIG_ + "orange");
		CONFIG_GREEN = registerCB(map, CONFIG_ + "green");
		CONFIG_PURPLE = registerCB(map, CONFIG_ + "purple");
		CONFIG_OPEN = register(map, CONFIG_ + "open");

		MACHINE_BOTTOM = register(map, BLOCKS_ + "machine/machine_bottom");
		MACHINE_TOP = register(map, BLOCKS_ + "machine/machine_top");
		MACHINE_SIDE = register(map, BLOCKS_ + "machine/machine_side");

		MACHINE_FACE_FURNACE = register(map, MACHINE_FACE_ + "furnace");
		MACHINE_FACE_PULVERIZER = register(map, MACHINE_FACE_ + "pulverizer");
		MACHINE_FACE_SAWMILL = register(map, MACHINE_FACE_ + "sawmill");
		MACHINE_FACE_SMELTER = register(map, MACHINE_FACE_ + "smelter");
		MACHINE_FACE_CRUCIBLE = register(map, MACHINE_FACE_ + "crucible");
		MACHINE_FACE_TRANSPOSER = register(map, MACHINE_FACE_ + "transposer");
		MACHINE_FACE_PRECIPITATOR = register(map, MACHINE_FACE_ + "precipitator");
		MACHINE_FACE_EXTRUDER = register(map, MACHINE_FACE_ + "extruder");
		MACHINE_FACE_ACCUMULATOR = register(map, MACHINE_FACE_ + "accumulator");
		MACHINE_FACE_ASSEMBLER = register(map, MACHINE_FACE_ + "assembler");
		MACHINE_FACE_CHARGER = register(map, MACHINE_FACE_ + "charger");
		MACHINE_FACE_INSOLATOR = register(map, MACHINE_FACE_ + "insolator");

		MACHINE_ACTIVE_FURNACE = register(map, MACHINE_ACTIVE_ + "furnace");
		MACHINE_ACTIVE_PULVERIZER = register(map, MACHINE_ACTIVE_ + "pulverizer");
		MACHINE_ACTIVE_SAWMILL = register(map, MACHINE_ACTIVE_ + "sawmill");
		MACHINE_ACTIVE_SMELTER = register(map, MACHINE_ACTIVE_ + "smelter");
		MACHINE_ACTIVE_CRUCIBLE = register(map, MACHINE_ACTIVE_ + "crucible");
		MACHINE_ACTIVE_TRANSPOSER = register(map, MACHINE_ACTIVE_ + "transposer");
		MACHINE_ACTIVE_PRECIPITATOR = register(map, MACHINE_ACTIVE_ + "precipitator");
		MACHINE_ACTIVE_EXTRUDER = register(map, MACHINE_ACTIVE_ + "extruder");
		MACHINE_ACTIVE_ACCUMULATOR = register(map, MACHINE_ACTIVE_ + "accumulator");
		MACHINE_ACTIVE_ASSEMBLER = register(map, MACHINE_ACTIVE_ + "assembler");
		MACHINE_ACTIVE_CHARGER = register(map, MACHINE_ACTIVE_ + "charger");
		MACHINE_ACTIVE_INSOLATOR = register(map, MACHINE_ACTIVE_ + "insolator");

		CELL_CONFIG_NONE = CONFIG_NONE;
		CELL_CONFIG_ORANGE = registerCB(map, CELL_ + "cell_config_orange");
		CELL_CONFIG_BLUE = registerCB(map, CELL_ + "cell_config_blue");

		CELL_METER_0 = register(map, CELL_ + "cell_meter_0");
		CELL_METER_1 = register(map, CELL_ + "cell_meter_1");
		CELL_METER_2 = register(map, CELL_ + "cell_meter_2");
		CELL_METER_3 = register(map, CELL_ + "cell_meter_3");
		CELL_METER_4 = register(map, CELL_ + "cell_meter_4");
		CELL_METER_5 = register(map, CELL_ + "cell_meter_5");
		CELL_METER_6 = register(map, CELL_ + "cell_meter_6");
		CELL_METER_7 = register(map, CELL_ + "cell_meter_7");
		CELL_METER_8 = register(map, CELL_ + "cell_meter_8");

		CELL_METER_CREATIVE = register(map, CELL_ + "cell_meter_creative");

		CELL_CREATIVE = register(map, CELL_ + "cell_creative");
		CELL_CREATIVE_INNER = register(map, CELL_ + "cell_creative_inner");
		CELL_BASIC = register(map, CELL_ + "cell_basic");
		CELL_BASIC_INNER = register(map, CELL_ + "cell_basic_inner");
		CELL_HARDENED = register(map, CELL_ + "cell_hardened");
		CELL_HARDENED_INNER = register(map, CELL_ + "cell_hardened_inner");
		CELL_REINFORCED = register(map, CELL_ + "cell_reinforced");
		CELL_REINFORCED_INNER = register(map, CELL_ + "cell_reinforced_inner");
		CELL_RESONANT = register(map, CELL_ + "cell_resonant");
		CELL_RESONANT_INNER = register(map, CELL_ + "cell_resonant_inner");
		CELL_CENTER_SOLID = register(map, CELL_ + "cell_center_solid");

		CONFIG = new TextureAtlasSprite[] { CONFIG_NONE, CONFIG_BLUE, CONFIG_RED, CONFIG_YELLOW, CONFIG_ORANGE, CONFIG_GREEN, CONFIG_PURPLE, CONFIG_OPEN };

		MACHINE_FACE = new TextureAtlasSprite[] { MACHINE_FACE_FURNACE, MACHINE_FACE_PULVERIZER, MACHINE_FACE_SAWMILL, MACHINE_FACE_SMELTER, MACHINE_FACE_CRUCIBLE, MACHINE_FACE_TRANSPOSER, MACHINE_FACE_PRECIPITATOR, MACHINE_FACE_EXTRUDER, MACHINE_FACE_ACCUMULATOR, MACHINE_FACE_ASSEMBLER, MACHINE_FACE_CHARGER, MACHINE_FACE_INSOLATOR };
		MACHINE_ACTIVE = new TextureAtlasSprite[] { MACHINE_ACTIVE_FURNACE, MACHINE_ACTIVE_PULVERIZER, MACHINE_ACTIVE_SAWMILL, MACHINE_ACTIVE_SMELTER, MACHINE_ACTIVE_CRUCIBLE, MACHINE_ACTIVE_TRANSPOSER, MACHINE_ACTIVE_PRECIPITATOR, MACHINE_ACTIVE_EXTRUDER, MACHINE_ACTIVE_ACCUMULATOR, MACHINE_ACTIVE_ASSEMBLER, MACHINE_ACTIVE_CHARGER, MACHINE_ACTIVE_INSOLATOR };

		CELL_CONFIG = new TextureAtlasSprite[] { CELL_CONFIG_NONE, CELL_CONFIG_ORANGE, CELL_CONFIG_BLUE };

		CELL_METER = new TextureAtlasSprite[] { CELL_METER_0, CELL_METER_1, CELL_METER_2, CELL_METER_3, CELL_METER_4, CELL_METER_5, CELL_METER_6, CELL_METER_7, CELL_METER_8 };
		CELL = new TextureAtlasSprite[] { CELL_CREATIVE, CELL_CREATIVE_INNER, CELL_BASIC, CELL_BASIC_INNER, CELL_HARDENED, CELL_HARDENED_INNER, CELL_REINFORCED, CELL_REINFORCED_INNER, CELL_RESONANT, CELL_RESONANT_INNER };

	}

	//Bouncer to make the class readable.
	private static TextureAtlasSprite register(TextureMap map, String sprite) {

		return map.registerSprite(new ResourceLocation(sprite));
	}

	//Bouncer for registering ColourBlind textures.
	private static TextureAtlasSprite registerCB(TextureMap map, String sprite) {

		if (CoFHProps.enableColorBlindTextures) {
			sprite += CB_POSTFIX;
		}
		return register(map, sprite);
	}

	private static String CB_POSTFIX = "_cb";

	private static String BLOCKS_ = "thermalexpansion:blocks/";
	private static String CONFIG_ = BLOCKS_ + "config/config_";
	private static String MACHINE_FACE_ = BLOCKS_ + "machine/machine_face_";
	private static String MACHINE_ACTIVE_ = BLOCKS_ + "machine/machine_active_";
	private static String CELL_ = BLOCKS_ + "cell/";

	public static TextureAtlasSprite[] CONFIG;
	public static TextureAtlasSprite CONFIG_NONE;
	public static TextureAtlasSprite CONFIG_BLUE;
	public static TextureAtlasSprite CONFIG_RED;
	public static TextureAtlasSprite CONFIG_YELLOW;
	public static TextureAtlasSprite CONFIG_ORANGE;
	public static TextureAtlasSprite CONFIG_GREEN;
	public static TextureAtlasSprite CONFIG_PURPLE;
	public static TextureAtlasSprite CONFIG_OPEN;

	public static TextureAtlasSprite MACHINE_BOTTOM;
	public static TextureAtlasSprite MACHINE_TOP;
	public static TextureAtlasSprite MACHINE_SIDE;

	public static TextureAtlasSprite[] MACHINE_FACE;
	public static TextureAtlasSprite MACHINE_FACE_FURNACE;
	public static TextureAtlasSprite MACHINE_FACE_PULVERIZER;
	public static TextureAtlasSprite MACHINE_FACE_SAWMILL;
	public static TextureAtlasSprite MACHINE_FACE_SMELTER;
	public static TextureAtlasSprite MACHINE_FACE_CRUCIBLE;
	public static TextureAtlasSprite MACHINE_FACE_TRANSPOSER;
	public static TextureAtlasSprite MACHINE_FACE_PRECIPITATOR;
	public static TextureAtlasSprite MACHINE_FACE_EXTRUDER;
	public static TextureAtlasSprite MACHINE_FACE_ACCUMULATOR;
	public static TextureAtlasSprite MACHINE_FACE_ASSEMBLER;
	public static TextureAtlasSprite MACHINE_FACE_CHARGER;
	public static TextureAtlasSprite MACHINE_FACE_INSOLATOR;

	public static TextureAtlasSprite[] MACHINE_ACTIVE;
	public static TextureAtlasSprite MACHINE_ACTIVE_FURNACE;
	public static TextureAtlasSprite MACHINE_ACTIVE_PULVERIZER;
	public static TextureAtlasSprite MACHINE_ACTIVE_SAWMILL;
	public static TextureAtlasSprite MACHINE_ACTIVE_SMELTER;
	public static TextureAtlasSprite MACHINE_ACTIVE_CRUCIBLE;
	public static TextureAtlasSprite MACHINE_ACTIVE_TRANSPOSER;
	public static TextureAtlasSprite MACHINE_ACTIVE_PRECIPITATOR;
	public static TextureAtlasSprite MACHINE_ACTIVE_EXTRUDER;
	public static TextureAtlasSprite MACHINE_ACTIVE_ACCUMULATOR;
	public static TextureAtlasSprite MACHINE_ACTIVE_ASSEMBLER;
	public static TextureAtlasSprite MACHINE_ACTIVE_CHARGER;
	public static TextureAtlasSprite MACHINE_ACTIVE_INSOLATOR;

	public static TextureAtlasSprite[] CELL_CONFIG;
	public static TextureAtlasSprite CELL_CONFIG_NONE;
	public static TextureAtlasSprite CELL_CONFIG_ORANGE;
	public static TextureAtlasSprite CELL_CONFIG_BLUE;

	public static TextureAtlasSprite[] CELL_METER;
	public static TextureAtlasSprite CELL_METER_0;
	public static TextureAtlasSprite CELL_METER_1;
	public static TextureAtlasSprite CELL_METER_2;
	public static TextureAtlasSprite CELL_METER_3;
	public static TextureAtlasSprite CELL_METER_4;
	public static TextureAtlasSprite CELL_METER_5;
	public static TextureAtlasSprite CELL_METER_6;
	public static TextureAtlasSprite CELL_METER_7;
	public static TextureAtlasSprite CELL_METER_8;
	public static TextureAtlasSprite CELL_METER_CREATIVE;

	public static TextureAtlasSprite[] CELL;
	public static TextureAtlasSprite CELL_CREATIVE;
	public static TextureAtlasSprite CELL_CREATIVE_INNER;
	public static TextureAtlasSprite CELL_BASIC;
	public static TextureAtlasSprite CELL_BASIC_INNER;
	public static TextureAtlasSprite CELL_HARDENED;
	public static TextureAtlasSprite CELL_HARDENED_INNER;
	public static TextureAtlasSprite CELL_REINFORCED;
	public static TextureAtlasSprite CELL_REINFORCED_INNER;
	public static TextureAtlasSprite CELL_RESONANT;
	public static TextureAtlasSprite CELL_RESONANT_INNER;
	public static TextureAtlasSprite CELL_CENTER_SOLID;

}
