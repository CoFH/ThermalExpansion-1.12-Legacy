package cofh.thermalexpansion.init;

import cofh.core.init.CoreProps;
import cofh.thermalfoundation.init.TFFluids;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.TextureStitchEvent;

public class TETextures {

	private TETextures() {

	}

	public static void registerIcons(TextureStitchEvent.Pre event) {

		TextureMap map = event.getMap();

		// @formatter:off \o/ Formatter!
		CONFIG_NONE =						register(map, CONFIG_ + "none");
		CONFIG_BLUE =						registerCB(map, CONFIG_ + "blue");
		CONFIG_RED =						registerCB(map, CONFIG_ + "red");
		CONFIG_YELLOW =						registerCB(map, CONFIG_ + "yellow");
		CONFIG_ORANGE =						registerCB(map, CONFIG_ + "orange");
		CONFIG_GREEN =						registerCB(map, CONFIG_ + "green");
		CONFIG_PURPLE =						registerCB(map, CONFIG_ + "purple");
		CONFIG_OPEN =						register(map, CONFIG_ + "open");

		/* MACHINES */
		MACHINE_BOTTOM =                    register(map, MACHINE_ + "bottom");
		MACHINE_TOP =                       register(map, MACHINE_ + "top");
		MACHINE_SIDE =                      register(map, MACHINE_ + "side");

		MACHINE_OVERLAY_0 =                 register(map, MACHINE_ + "overlay_0");
		MACHINE_OVERLAY_1 =                 register(map, MACHINE_ + "overlay_1");
		MACHINE_OVERLAY_2 =                 register(map, MACHINE_ + "overlay_2");
		MACHINE_OVERLAY_3 =                 register(map, MACHINE_ + "overlay_3");
		MACHINE_OVERLAY_4 =                 register(map, MACHINE_ + "overlay_4");
		MACHINE_OVERLAY_C =                 register(map, MACHINE_ + "overlay_c");

		MACHINE_FACE_FURNACE =				register(map, MACHINE_FACE_ + "furnace");
		MACHINE_FACE_PULVERIZER =			register(map, MACHINE_FACE_ + "pulverizer");
		MACHINE_FACE_SAWMILL =				register(map, MACHINE_FACE_ + "sawmill");
		MACHINE_FACE_SMELTER =				register(map, MACHINE_FACE_ + "smelter");
		MACHINE_FACE_INSOLATOR =			register(map, MACHINE_FACE_ + "insolator");
		MACHINE_FACE_COMPACTOR =			register(map, MACHINE_FACE_ + "compactor");
		MACHINE_FACE_CRUCIBLE =				register(map, MACHINE_FACE_ + "crucible");
		MACHINE_FACE_REFINERY =             register(map, MACHINE_FACE_ + "refinery");
		MACHINE_FACE_TRANSPOSER =			register(map, MACHINE_FACE_ + "transposer");
		MACHINE_FACE_CHARGER =				register(map, MACHINE_FACE_ + "charger");
		MACHINE_FACE_CENTRIFUGE =			map.getMissingSprite();//TODO register(map, MACHINE_FACE_ + "centrifuge");
		MACHINE_FACE_CRAFTER =				register(map, MACHINE_FACE_ + "crafter");
		MACHINE_FACE_BREWER =				map.getMissingSprite();//TODO register(map, MACHINE_FACE_ + "brewer");
		MACHINE_FACE_ENCHANTER =			map.getMissingSprite();//TODO register(map, MACHINE_FACE_ + "enchanter");
		MACHINE_FACE_PRECIPITATOR =			register(map, MACHINE_FACE_ + "precipitator");
		MACHINE_FACE_EXTRUDER =				register(map, MACHINE_FACE_ + "extruder");

		MACHINE_ACTIVE_FURNACE =			register(map, MACHINE_ACTIVE_ + "furnace");
		MACHINE_ACTIVE_PULVERIZER =			register(map, MACHINE_ACTIVE_ + "pulverizer");
		MACHINE_ACTIVE_SAWMILL =			register(map, MACHINE_ACTIVE_ + "sawmill");
		MACHINE_ACTIVE_SMELTER =			register(map, MACHINE_ACTIVE_ + "smelter");
		MACHINE_ACTIVE_INSOLATOR =			register(map, MACHINE_ACTIVE_ + "insolator");
		MACHINE_ACTIVE_COMPACTOR =			register(map, MACHINE_ACTIVE_ + "compactor");
		MACHINE_ACTIVE_CRUCIBLE =			register(map, MACHINE_ACTIVE_ + "crucible");
		MACHINE_ACTIVE_REFINERY =           register(map, MACHINE_ACTIVE_ + "refinery");
		MACHINE_ACTIVE_TRANSPOSER =			register(map, MACHINE_ACTIVE_ + "transposer");
		MACHINE_ACTIVE_CHARGER =			register(map, MACHINE_ACTIVE_ + "charger");
		MACHINE_ACTIVE_CENTRIFUGE =			map.getMissingSprite();//TODO register(map, MACHINE_ACTIVE_ + "centrifuge");
		MACHINE_ACTIVE_CRAFTER =			register(map, MACHINE_ACTIVE_ + "crafter");
		MACHINE_ACTIVE_BREWER =				map.getMissingSprite();//TODO register(map, MACHINE_ACTIVE_ + "brewer");
		MACHINE_ACTIVE_ENCHANTER =			map.getMissingSprite();//TODO register(map, MACHINE_ACTIVE_ + "enchanter");
		MACHINE_ACTIVE_PRECIPITATOR =		register(map, MACHINE_ACTIVE_ + "precipitator");
		MACHINE_ACTIVE_EXTRUDER =			register(map, MACHINE_ACTIVE_ + "extruder");

		MACHINE_FRAME_TOP =                 register(map, MACHINE_ + "frame_top");
		MACHINE_FRAME_SIDE =                register(map, MACHINE_ + "frame_side");
		MACHINE_FRAME_BOTTOM =              register(map, MACHINE_ + "frame_bottom");
		MACHINE_FRAME_INNER =               register(map, MACHINE_ + "frame_inner");

		/* AUTOMATA */
		AUTOMATON_SIDE =					register(map, BLOCKS_ + "automaton/automaton_side");

		AUTOMATON_FACE_BREAKER =			register(map, AUTOMATON_FACE_ + "breaker");
		AUTOMATON_FACE_COLLECTOR =			register(map, AUTOMATON_FACE_ + "collector");

		AUTOMATON_ACTIVE_BREAKER =			register(map, AUTOMATON_ACTIVE_ + "breaker");
		AUTOMATON_ACTIVE_COLLECTOR =		register(map, AUTOMATON_ACTIVE_ + "collector");

		/* DEVICES */
		DEVICE_TOP =                        register(map, BLOCKS_ + "device/device_top");
		DEVICE_BOTTOM =                     register(map, BLOCKS_ + "device/device_bottom");
		DEVICE_SIDE =						register(map, BLOCKS_ + "device/device_side");

		DEVICE_FACE_WATERGEN =				register(map, DEVICE_FACE_ + "water_gen");
		DEVICE_FACE_NULLIFIER =				register(map, DEVICE_FACE_ + "nullifier");
		DEVICE_FACE_HEAT_SINK =             register(map, DEVICE_FACE_ + "heat_sink");
		DEVICE_FACE_TAPPER =                register(map, DEVICE_FACE_ + "tapper");

		DEVICE_FACE_ITEM_BUFFER =			register(map, DEVICE_FACE_ + "item_buffer");
		DEVICE_FACE_EXTENDER =				map.getMissingSprite();//TODO register(map, DEVICE_FACE_ + "extender");

		DEVICE_ACTIVE_WATERGEN =			register(map, DEVICE_ACTIVE_ + "water_gen");
		DEVICE_ACTIVE_NULLIFIER =			register(map, DEVICE_ACTIVE_ + "nullifier");
		DEVICE_ACTIVE_HEAT_SINK =           register(map, DEVICE_ACTIVE_ + "heat_sink");
		DEVICE_ACTIVE_TAPPER =              register(map, DEVICE_ACTIVE_ + "tapper");

		DEVICE_ACTIVE_ITEM_BUFFER =			register(map, DEVICE_ACTIVE_ + "item_buffer");
		DEVICE_ACTIVE_EXTENDER =			map.getMissingSprite();//TODO register(map, DEVICE_ACTIVE_ + "extender");

        DEVICE_FRAME_TOP =                  register(map, DEVICE_ + "frame_top");
		DEVICE_FRAME_SIDE =                 register(map, DEVICE_ + "frame_side");
		DEVICE_FRAME_BOTTOM =               register(map, DEVICE_ + "frame_bottom");
		DEVICE_FRAME_INNER =                register(map, DEVICE_ + "frame_inner");

		/* DYNAMOS */
		DYNAMO_COIL_REDSTONE =				register(map, DYNAMO_ + "coil_redstone");

		DYNAMO_STEAM =						register(map, DYNAMO_ + "steam");
		DYNAMO_MAGMATIC =					register(map, DYNAMO_ + "magmatic");
		DYNAMO_COMPRESSION =				register(map, DYNAMO_ + "compression");
		DYNAMO_REACTANT =					register(map, DYNAMO_ + "reactant");
		DYNAMO_ENERVATION =					register(map, DYNAMO_ + "enervation");
		DYNAMO_NUMISMATIC =					register(map, DYNAMO_ + "numismatic");

		/* ICONS */
		ICON_ACCEPT =						map.getMissingSprite();//TODO
		ICON_ACCEPT_INACTIVE =				map.getMissingSprite();//TODO
		ICON_SCHEMATIC =					map.getMissingSprite();//TODO

		/* CELLS */
		CELL_CONFIG_BLUE =                  registerCB(map, CELL_CONFIG_ + "blue");
		CELL_CONFIG_ORANGE =                registerCB(map, CELL_CONFIG_ + "orange");

		CELL_INNER_0 =                      register(map, CELL_ + "inner_0");
		CELL_INNER_1 =                      register(map, CELL_ + "inner_1");
		CELL_INNER_2 =                      register(map, CELL_ + "inner_2");
		CELL_INNER_3 =                      register(map, CELL_ + "inner_3");
		CELL_INNER_4 =                      register(map, CELL_ + "inner_4");
		CELL_INNER_C =                      register(map, CELL_ + "inner_c");

		CELL_SIDE_0 =                       register(map, CELL_ + "side_0");
		CELL_SIDE_1 =                       register(map, CELL_ + "side_1");
		CELL_SIDE_2 =                       register(map, CELL_ + "side_2");
		CELL_SIDE_3 =                       register(map, CELL_ + "side_3");
		CELL_SIDE_4 =                       register(map, CELL_ + "side_4");
		CELL_SIDE_C =                       register(map, CELL_ + "side_c");

		CELL_METER_0 =                      register(map, CELL_ + "meter_0");
		CELL_METER_1 =                      register(map, CELL_ + "meter_1");
		CELL_METER_2 =                      register(map, CELL_ + "meter_2");
		CELL_METER_3 =                      register(map, CELL_ + "meter_3");
		CELL_METER_4 =                      register(map, CELL_ + "meter_4");
		CELL_METER_5 =                      register(map, CELL_ + "meter_5");
		CELL_METER_6 =                      register(map, CELL_ + "meter_6");
		CELL_METER_7 =                      register(map, CELL_ + "meter_7");
		CELL_METER_8 =                      register(map, CELL_ + "meter_8");
		CELL_METER_C =                      register(map, CELL_ + "meter_c");

		TANK_BOTTOM_0_0 =                   register(map, TANK_ + "bottom_0_0");
		TANK_BOTTOM_0_1 =                   register(map, TANK_ + "bottom_0_1");
		TANK_BOTTOM_0_2 =                   register(map, TANK_ + "bottom_0_2");
		TANK_BOTTOM_0_3 =                   register(map, TANK_ + "bottom_0_3");
		TANK_BOTTOM_0_4 =                   register(map, TANK_ + "bottom_0_4");
		TANK_BOTTOM_0_C =                   register(map, TANK_ + "bottom_0_c");

		TANK_BOTTOM_1_0 =                   register(map, TANK_ + "bottom_1_0");
		TANK_BOTTOM_1_1 =                   register(map, TANK_ + "bottom_1_1");
		TANK_BOTTOM_1_2 =                   register(map, TANK_ + "bottom_1_2");
		TANK_BOTTOM_1_3 =                   register(map, TANK_ + "bottom_1_3");
		TANK_BOTTOM_1_4 =                   register(map, TANK_ + "bottom_1_4");
		TANK_BOTTOM_1_C =                   register(map, TANK_ + "bottom_1_c");

		TANK_TOP_0 =                        register(map, TANK_ + "top_0");
		TANK_TOP_1 =                        register(map, TANK_ + "top_1");
		TANK_TOP_2 =                        register(map, TANK_ + "top_2");
		TANK_TOP_3 =                        register(map, TANK_ + "top_3");
		TANK_TOP_4 =                        register(map, TANK_ + "top_4");
		TANK_TOP_C =                        register(map, TANK_ + "top_c");

		TANK_SIDE_0_0 =                     register(map, TANK_ + "side_0_0");
		TANK_SIDE_0_1 =                     register(map, TANK_ + "side_0_1");
		TANK_SIDE_0_2 =                     register(map, TANK_ + "side_0_2");
		TANK_SIDE_0_3 =                     register(map, TANK_ + "side_0_3");
		TANK_SIDE_0_4 =                     register(map, TANK_ + "side_0_4");
		TANK_SIDE_0_C =                     register(map, TANK_ + "side_0_c");

		TANK_SIDE_1_0 =                     register(map, TANK_ + "side_1_0");
		TANK_SIDE_1_1 =                     register(map, TANK_ + "side_1_1");
		TANK_SIDE_1_2 =                     register(map, TANK_ + "side_1_2");
		TANK_SIDE_1_3 =                     register(map, TANK_ + "side_1_3");
		TANK_SIDE_1_4 =                     register(map, TANK_ + "side_1_4");
		TANK_SIDE_1_C =                     register(map, TANK_ + "side_1_c");

		CACHE_BOTTOM_0 =                    register(map, CACHE_ + "bottom_0");
		CACHE_BOTTOM_1 =                    register(map, CACHE_ + "bottom_1");
		CACHE_BOTTOM_2 =                    register(map, CACHE_ + "bottom_2");
		CACHE_BOTTOM_3 =                    register(map, CACHE_ + "bottom_3");
		CACHE_BOTTOM_4 =                    register(map, CACHE_ + "bottom_4");
		CACHE_BOTTOM_C =                    register(map, CACHE_ + "bottom_c");

		CACHE_TOP_0 =                       register(map, CACHE_ + "top_0");
		CACHE_TOP_1 =                       register(map, CACHE_ + "top_1");
		CACHE_TOP_2 =                       register(map, CACHE_ + "top_2");
		CACHE_TOP_3 =                       register(map, CACHE_ + "top_3");
		CACHE_TOP_4 =                       register(map, CACHE_ + "top_4");
		CACHE_TOP_C =                       register(map, CACHE_ + "top_c");

		CACHE_SIDE_0 =                      register(map, CACHE_ + "side_0");
		CACHE_SIDE_1 =                      register(map, CACHE_ + "side_1");
		CACHE_SIDE_2 =                      register(map, CACHE_ + "side_2");
		CACHE_SIDE_3 =                      register(map, CACHE_ + "side_3");
		CACHE_SIDE_4 =                      register(map, CACHE_ + "side_4");
		CACHE_SIDE_C =                      register(map, CACHE_ + "side_c");

		CACHE_FACE_0 =                      register(map, CACHE_ + "face_0");
		CACHE_FACE_1 =                      register(map, CACHE_ + "face_1");
		CACHE_FACE_2 =                      register(map, CACHE_ + "face_2");
		CACHE_FACE_3 =                      register(map, CACHE_ + "face_3");
		CACHE_FACE_4 =                      register(map, CACHE_ + "face_4");
		CACHE_FACE_C =                      register(map, CACHE_ + "face_c");

		CACHE_METER_0 =                     register(map, CACHE_ + "meter_0");
		CACHE_METER_1 =                     register(map, CACHE_ + "meter_1");
		CACHE_METER_2 =                     register(map, CACHE_ + "meter_2");
		CACHE_METER_3 =                     register(map, CACHE_ + "meter_3");
		CACHE_METER_4 =                     register(map, CACHE_ + "meter_4");
		CACHE_METER_5 =                     register(map, CACHE_ + "meter_5");
		CACHE_METER_6 =                     register(map, CACHE_ + "meter_6");
		CACHE_METER_7 =                     register(map, CACHE_ + "meter_7");
		CACHE_METER_8 =                     register(map, CACHE_ + "meter_8");
		CACHE_METER_C =                     register(map, CACHE_ + "meter_c");

		ILLUMINATOR_FRAME =                 register(map, BLOCKS_ + "light/illuminator_frame");

		/* ARRAYS */
		CONFIG = new TextureAtlasSprite[] {
				CONFIG_NONE,
				CONFIG_BLUE,
				CONFIG_RED,
				CONFIG_YELLOW,
				CONFIG_ORANGE,
				CONFIG_GREEN,
				CONFIG_PURPLE,
				CONFIG_OPEN
		};

		MACHINE_OVERLAY = new TextureAtlasSprite[] {
				MACHINE_OVERLAY_0,
				MACHINE_OVERLAY_1,
				MACHINE_OVERLAY_2,
				MACHINE_OVERLAY_3,
				MACHINE_OVERLAY_4,
				MACHINE_OVERLAY_C
		};

		MACHINE_FACE = new TextureAtlasSprite[] {
				MACHINE_FACE_FURNACE,
				MACHINE_FACE_PULVERIZER,
				MACHINE_FACE_SAWMILL,
				MACHINE_FACE_SMELTER,
				MACHINE_FACE_INSOLATOR,
				MACHINE_FACE_COMPACTOR,
				MACHINE_FACE_CRUCIBLE,
				MACHINE_FACE_REFINERY,
				MACHINE_FACE_TRANSPOSER,
				MACHINE_FACE_CHARGER,
				MACHINE_FACE_CENTRIFUGE,
				MACHINE_FACE_CRAFTER,
				MACHINE_FACE_BREWER,
				MACHINE_FACE_ENCHANTER,
				MACHINE_FACE_PRECIPITATOR,
				MACHINE_FACE_EXTRUDER

		};
		MACHINE_ACTIVE = new TextureAtlasSprite[] {
				MACHINE_ACTIVE_FURNACE,
				MACHINE_ACTIVE_PULVERIZER,
				MACHINE_ACTIVE_SAWMILL,
				MACHINE_ACTIVE_SMELTER,
				MACHINE_ACTIVE_INSOLATOR,
				MACHINE_ACTIVE_COMPACTOR,
				MACHINE_ACTIVE_CRUCIBLE,
				MACHINE_ACTIVE_REFINERY,
				MACHINE_ACTIVE_TRANSPOSER,
				MACHINE_ACTIVE_CHARGER,
				MACHINE_ACTIVE_CENTRIFUGE,
				MACHINE_ACTIVE_CRAFTER,
				MACHINE_ACTIVE_BREWER,
				MACHINE_ACTIVE_ENCHANTER,
				MACHINE_ACTIVE_PRECIPITATOR,
				MACHINE_ACTIVE_EXTRUDER

		};
		AUTOMATON_FACE = new TextureAtlasSprite[] {
				AUTOMATON_FACE_BREAKER,
				AUTOMATON_FACE_COLLECTOR
		};
		AUTOMATON_ACTIVE = new TextureAtlasSprite[] {
				AUTOMATON_ACTIVE_BREAKER,
				AUTOMATON_ACTIVE_COLLECTOR
		};
		DEVICE_FACE = new TextureAtlasSprite[] {
				DEVICE_FACE_WATERGEN,
				DEVICE_FACE_NULLIFIER,
				DEVICE_FACE_HEAT_SINK,
				DEVICE_FACE_TAPPER

//				DEVICE_FACE_EXTENDER,
//				DEVICE_FACE_CONCENTRATOR,
//				DEVICE_FACE_ITEM_BUFFER,
//				DEVICE_FACE_FLUID_BUFFER,
//				DEVICE_FACE_ENERGY_BUFFER
		};
		DEVICE_ACTIVE = new TextureAtlasSprite[] {
				DEVICE_ACTIVE_WATERGEN,
				DEVICE_ACTIVE_NULLIFIER,
				DEVICE_ACTIVE_HEAT_SINK,
				DEVICE_ACTIVE_TAPPER

//				DEVICE_ACTIVE_EXTENDER,
//				DEVICE_ACTIVE_CONCENTRATOR,
//				DEVICE_ACTIVE_ITEM_BUFFER,
//				DEVICE_ACTIVE_FLUID_BUFFER,
//				DEVICE_ACTIVE_ENERGY_BUFFER
		};
		DYNAMO = new TextureAtlasSprite[] {
				DYNAMO_STEAM,
				DYNAMO_MAGMATIC,
				DYNAMO_COMPRESSION,
				DYNAMO_REACTANT,
				DYNAMO_ENERVATION,
				DYNAMO_NUMISMATIC
		};

		CELL_CONFIG = new TextureAtlasSprite[] {
				CONFIG_NONE,
				CELL_CONFIG_BLUE,
				CELL_CONFIG_ORANGE
		};

		CELL_INNER = new TextureAtlasSprite[] {
				CELL_INNER_0,
				CELL_INNER_1,
				CELL_INNER_2,
				CELL_INNER_3,
				CELL_INNER_4,
				CELL_INNER_C
		};

		CELL_SIDE = new TextureAtlasSprite[] {
				CELL_SIDE_0,
				CELL_SIDE_1,
				CELL_SIDE_2,
				CELL_SIDE_3,
				CELL_SIDE_4,
				CELL_SIDE_C
		};

		CELL_METER = new TextureAtlasSprite[] {
				CELL_METER_0,
				CELL_METER_1,
				CELL_METER_2,
				CELL_METER_3,
				CELL_METER_4,
				CELL_METER_5,
				CELL_METER_6,
				CELL_METER_7,
				CELL_METER_8,
				CELL_METER_C
		};

		TANK_BOTTOM = new TextureAtlasSprite[][] {
				new TextureAtlasSprite[] {
						TANK_BOTTOM_0_0,
						TANK_BOTTOM_0_1,
						TANK_BOTTOM_0_2,
						TANK_BOTTOM_0_3,
						TANK_BOTTOM_0_4,
						TANK_BOTTOM_0_C
				},
				new TextureAtlasSprite[] {
						TANK_BOTTOM_1_0,
						TANK_BOTTOM_1_1,
						TANK_BOTTOM_1_2,
						TANK_BOTTOM_1_3,
						TANK_BOTTOM_1_4,
						TANK_BOTTOM_1_C
				}
		};

		TANK_TOP = new TextureAtlasSprite[] {
				TANK_TOP_0,
				TANK_TOP_1,
				TANK_TOP_2,
				TANK_TOP_3,
				TANK_TOP_4,
				TANK_TOP_C
		};

		TANK_SIDE = new TextureAtlasSprite[][] {
				new TextureAtlasSprite[] {
						TANK_SIDE_0_0,
						TANK_SIDE_0_1,
						TANK_SIDE_0_2,
						TANK_SIDE_0_3,
						TANK_SIDE_0_4,
						TANK_SIDE_0_C
				},
				new TextureAtlasSprite[] {
						TANK_SIDE_1_0,
						TANK_SIDE_1_1,
						TANK_SIDE_1_2,
						TANK_SIDE_1_3,
						TANK_SIDE_1_4,
						TANK_SIDE_1_C
				}
		};

		CACHE_BOTTOM = new TextureAtlasSprite[] {
				CACHE_BOTTOM_0,
				CACHE_BOTTOM_1,
				CACHE_BOTTOM_2,
				CACHE_BOTTOM_3,
				CACHE_BOTTOM_4,
				CACHE_BOTTOM_C
		};

		CACHE_TOP = new TextureAtlasSprite[] {
				CACHE_TOP_0,
				CACHE_TOP_1,
				CACHE_TOP_2,
				CACHE_TOP_3,
				CACHE_TOP_4,
				CACHE_TOP_C
		};

		CACHE_SIDE = new TextureAtlasSprite[] {
				CACHE_SIDE_0,
				CACHE_SIDE_1,
				CACHE_SIDE_2,
				CACHE_SIDE_3,
				CACHE_SIDE_4,
				CACHE_SIDE_C
		};

		CACHE_FACE = new TextureAtlasSprite[] {
				CACHE_FACE_0,
				CACHE_FACE_1,
				CACHE_FACE_2,
				CACHE_FACE_3,
				CACHE_FACE_4,
				CACHE_FACE_C
		};

		CACHE_METER = new TextureAtlasSprite[] {
				CACHE_METER_0,
				CACHE_METER_1,
				CACHE_METER_2,
				CACHE_METER_3,
				CACHE_METER_4,
				CACHE_METER_5,
				CACHE_METER_6,
				CACHE_METER_7,
				CACHE_METER_8,
				CACHE_METER_C
		};
		// @formatter:on
	}

	public static void getIcons(TextureStitchEvent.Post event) {

		TextureMap map = event.getMap();

		// @formatter:off \o/ Formatter!
		CELL_CENTER_0 = map.getAtlasSprite(TFFluids.fluidRedstone.getStill().toString());
		CELL_CENTER_1 = map.getAtlasSprite(TFFluids.fluidRedstone.getStill().toString());

		/* ARRAYS */
		CELL_CENTER = new TextureAtlasSprite[] {
				CELL_CENTER_0,
				CELL_CENTER_1
		};
		// @formatter:on
	}

	// Bouncer to make the class readable.
	private static TextureAtlasSprite register(TextureMap map, String sprite) {

		return map.registerSprite(new ResourceLocation(sprite));
	}

	// Bouncer for registering ColorBlind textures.
	private static TextureAtlasSprite registerCB(TextureMap map, String sprite) {

		if (CoreProps.enableColorBlindTextures) {
			sprite += CB_POSTFIX;
		}
		return register(map, sprite);
	}

	private static String CB_POSTFIX = "_cb";

	private static final String BLOCKS_ = "thermalexpansion:blocks/";
	private static final String CONFIG_ = BLOCKS_ + "config/config_";
	private static final String MACHINE_ = BLOCKS_ + "machine/machine_";
	private static final String MACHINE_FACE_ = MACHINE_ + "face_";
	private static final String MACHINE_ACTIVE_ = MACHINE_ + "active_";
	private static final String AUTOMATON_FACE_ = BLOCKS_ + "automaton/automaton_face_";
	private static final String AUTOMATON_ACTIVE_ = BLOCKS_ + "automaton/automaton_active_";
	private static final String DEVICE_ = BLOCKS_ + "device/device_";
	private static final String DEVICE_FACE_ = DEVICE_ + "face_";
	private static final String DEVICE_ACTIVE_ = DEVICE_ + "active_";
	private static final String DYNAMO_ = BLOCKS_ + "dynamo/dynamo_";
	private static final String CELL_ = BLOCKS_ + "storage/cell_";
	private static final String CELL_CONFIG_ = BLOCKS_ + "storage/cell_config_";
	private static final String TANK_ = BLOCKS_ + "storage/tank_";
	private static final String CACHE_ = BLOCKS_ + "storage/cache_";

	/* REFERENCES */
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

	public static TextureAtlasSprite[] MACHINE_OVERLAY;
	public static TextureAtlasSprite MACHINE_OVERLAY_0;
	public static TextureAtlasSprite MACHINE_OVERLAY_1;
	public static TextureAtlasSprite MACHINE_OVERLAY_2;
	public static TextureAtlasSprite MACHINE_OVERLAY_3;
	public static TextureAtlasSprite MACHINE_OVERLAY_4;
	public static TextureAtlasSprite MACHINE_OVERLAY_C;

	public static TextureAtlasSprite[] MACHINE_FACE;
	public static TextureAtlasSprite MACHINE_FACE_FURNACE;
	public static TextureAtlasSprite MACHINE_FACE_PULVERIZER;
	public static TextureAtlasSprite MACHINE_FACE_SAWMILL;
	public static TextureAtlasSprite MACHINE_FACE_SMELTER;
	public static TextureAtlasSprite MACHINE_FACE_INSOLATOR;
	public static TextureAtlasSprite MACHINE_FACE_COMPACTOR;
	public static TextureAtlasSprite MACHINE_FACE_CRUCIBLE;
	public static TextureAtlasSprite MACHINE_FACE_REFINERY;
	public static TextureAtlasSprite MACHINE_FACE_TRANSPOSER;
	public static TextureAtlasSprite MACHINE_FACE_CHARGER;
	public static TextureAtlasSprite MACHINE_FACE_CENTRIFUGE;
	public static TextureAtlasSprite MACHINE_FACE_CRAFTER;
	public static TextureAtlasSprite MACHINE_FACE_BREWER;
	public static TextureAtlasSprite MACHINE_FACE_ENCHANTER;
	public static TextureAtlasSprite MACHINE_FACE_PRECIPITATOR;
	public static TextureAtlasSprite MACHINE_FACE_EXTRUDER;

	public static TextureAtlasSprite[] MACHINE_ACTIVE;
	public static TextureAtlasSprite MACHINE_ACTIVE_FURNACE;
	public static TextureAtlasSprite MACHINE_ACTIVE_PULVERIZER;
	public static TextureAtlasSprite MACHINE_ACTIVE_SAWMILL;
	public static TextureAtlasSprite MACHINE_ACTIVE_SMELTER;
	public static TextureAtlasSprite MACHINE_ACTIVE_INSOLATOR;
	public static TextureAtlasSprite MACHINE_ACTIVE_COMPACTOR;
	public static TextureAtlasSprite MACHINE_ACTIVE_CRUCIBLE;
	public static TextureAtlasSprite MACHINE_ACTIVE_REFINERY;
	public static TextureAtlasSprite MACHINE_ACTIVE_TRANSPOSER;
	public static TextureAtlasSprite MACHINE_ACTIVE_CHARGER;
	public static TextureAtlasSprite MACHINE_ACTIVE_CENTRIFUGE;
	public static TextureAtlasSprite MACHINE_ACTIVE_CRAFTER;
	public static TextureAtlasSprite MACHINE_ACTIVE_BREWER;
	public static TextureAtlasSprite MACHINE_ACTIVE_ENCHANTER;
	public static TextureAtlasSprite MACHINE_ACTIVE_PRECIPITATOR;
	public static TextureAtlasSprite MACHINE_ACTIVE_EXTRUDER;

	public static TextureAtlasSprite MACHINE_FRAME_TOP;
	public static TextureAtlasSprite MACHINE_FRAME_SIDE;
	public static TextureAtlasSprite MACHINE_FRAME_BOTTOM;
	public static TextureAtlasSprite MACHINE_FRAME_INNER;

	public static TextureAtlasSprite AUTOMATON_SIDE;

	public static TextureAtlasSprite[] AUTOMATON_FACE;
	public static TextureAtlasSprite AUTOMATON_FACE_BREAKER;
	public static TextureAtlasSprite AUTOMATON_FACE_COLLECTOR;

	public static TextureAtlasSprite[] AUTOMATON_ACTIVE;
	public static TextureAtlasSprite AUTOMATON_ACTIVE_BREAKER;
	public static TextureAtlasSprite AUTOMATON_ACTIVE_COLLECTOR;

	public static TextureAtlasSprite DEVICE_TOP;
	public static TextureAtlasSprite DEVICE_BOTTOM;
	public static TextureAtlasSprite DEVICE_SIDE;

	public static TextureAtlasSprite[] DEVICE_FACE;
	public static TextureAtlasSprite DEVICE_FACE_WATERGEN;
	public static TextureAtlasSprite DEVICE_FACE_NULLIFIER;
	public static TextureAtlasSprite DEVICE_FACE_HEAT_SINK;
	public static TextureAtlasSprite DEVICE_FACE_TAPPER;

	public static TextureAtlasSprite DEVICE_FACE_EXTENDER;
	public static TextureAtlasSprite DEVICE_FACE_CONCENTRATOR;
	public static TextureAtlasSprite DEVICE_FACE_ITEM_BUFFER;
	public static TextureAtlasSprite DEVICE_FACE_FLUID_BUFFER;
	public static TextureAtlasSprite DEVICE_FACE_ENERGY_BUFFER;

	public static TextureAtlasSprite[] DEVICE_ACTIVE;
	public static TextureAtlasSprite DEVICE_ACTIVE_WATERGEN;
	public static TextureAtlasSprite DEVICE_ACTIVE_NULLIFIER;
	public static TextureAtlasSprite DEVICE_ACTIVE_HEAT_SINK;
	public static TextureAtlasSprite DEVICE_ACTIVE_TAPPER;

	public static TextureAtlasSprite DEVICE_ACTIVE_EXTENDER;
	public static TextureAtlasSprite DEVICE_ACTIVE_CONCENTRATOR;
	public static TextureAtlasSprite DEVICE_ACTIVE_ITEM_BUFFER;
	public static TextureAtlasSprite DEVICE_ACTIVE_FLUID_BUFFER;
	public static TextureAtlasSprite DEVICE_ACTIVE_ENERGY_BUFFER;

	public static TextureAtlasSprite DEVICE_FRAME_TOP;
	public static TextureAtlasSprite DEVICE_FRAME_SIDE;
	public static TextureAtlasSprite DEVICE_FRAME_BOTTOM;
	public static TextureAtlasSprite DEVICE_FRAME_INNER;

	public static TextureAtlasSprite DYNAMO_COIL_REDSTONE;

	public static TextureAtlasSprite[] DYNAMO;
	public static TextureAtlasSprite DYNAMO_STEAM;
	public static TextureAtlasSprite DYNAMO_MAGMATIC;
	public static TextureAtlasSprite DYNAMO_COMPRESSION;
	public static TextureAtlasSprite DYNAMO_REACTANT;
	public static TextureAtlasSprite DYNAMO_ENERVATION;
	public static TextureAtlasSprite DYNAMO_NUMISMATIC;

	public static TextureAtlasSprite ICON_ACCEPT;
	public static TextureAtlasSprite ICON_ACCEPT_INACTIVE;
	public static TextureAtlasSprite ICON_SCHEMATIC;

	public static TextureAtlasSprite[] CELL_CONFIG;
	public static TextureAtlasSprite CELL_CONFIG_BLUE;
	public static TextureAtlasSprite CELL_CONFIG_ORANGE;

	public static TextureAtlasSprite[] CELL_CENTER;
	public static TextureAtlasSprite CELL_CENTER_0;
	public static TextureAtlasSprite CELL_CENTER_1;

	public static TextureAtlasSprite[] CELL_INNER;
	public static TextureAtlasSprite CELL_INNER_0;
	public static TextureAtlasSprite CELL_INNER_1;
	public static TextureAtlasSprite CELL_INNER_2;
	public static TextureAtlasSprite CELL_INNER_3;
	public static TextureAtlasSprite CELL_INNER_4;
	public static TextureAtlasSprite CELL_INNER_C;

	public static TextureAtlasSprite[] CELL_SIDE;
	public static TextureAtlasSprite CELL_SIDE_0;
	public static TextureAtlasSprite CELL_SIDE_1;
	public static TextureAtlasSprite CELL_SIDE_2;
	public static TextureAtlasSprite CELL_SIDE_3;
	public static TextureAtlasSprite CELL_SIDE_4;
	public static TextureAtlasSprite CELL_SIDE_C;

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
	public static TextureAtlasSprite CELL_METER_C;

	public static TextureAtlasSprite[] TANK_TOP;
	public static TextureAtlasSprite TANK_TOP_0;
	public static TextureAtlasSprite TANK_TOP_1;
	public static TextureAtlasSprite TANK_TOP_2;
	public static TextureAtlasSprite TANK_TOP_3;
	public static TextureAtlasSprite TANK_TOP_4;
	public static TextureAtlasSprite TANK_TOP_C;

	public static TextureAtlasSprite[][] TANK_BOTTOM;
	public static TextureAtlasSprite TANK_BOTTOM_0_0;
	public static TextureAtlasSprite TANK_BOTTOM_0_1;
	public static TextureAtlasSprite TANK_BOTTOM_0_2;
	public static TextureAtlasSprite TANK_BOTTOM_0_3;
	public static TextureAtlasSprite TANK_BOTTOM_0_4;
	public static TextureAtlasSprite TANK_BOTTOM_0_C;
	public static TextureAtlasSprite TANK_BOTTOM_1_0;
	public static TextureAtlasSprite TANK_BOTTOM_1_1;
	public static TextureAtlasSprite TANK_BOTTOM_1_2;
	public static TextureAtlasSprite TANK_BOTTOM_1_3;
	public static TextureAtlasSprite TANK_BOTTOM_1_4;
	public static TextureAtlasSprite TANK_BOTTOM_1_C;

	public static TextureAtlasSprite[][] TANK_SIDE;
	public static TextureAtlasSprite TANK_SIDE_0_0;
	public static TextureAtlasSprite TANK_SIDE_0_1;
	public static TextureAtlasSprite TANK_SIDE_0_2;
	public static TextureAtlasSprite TANK_SIDE_0_3;
	public static TextureAtlasSprite TANK_SIDE_0_4;
	public static TextureAtlasSprite TANK_SIDE_0_C;
	public static TextureAtlasSprite TANK_SIDE_1_0;
	public static TextureAtlasSprite TANK_SIDE_1_1;
	public static TextureAtlasSprite TANK_SIDE_1_2;
	public static TextureAtlasSprite TANK_SIDE_1_3;
	public static TextureAtlasSprite TANK_SIDE_1_4;
	public static TextureAtlasSprite TANK_SIDE_1_C;

	public static TextureAtlasSprite[] CACHE_TOP;
	public static TextureAtlasSprite CACHE_TOP_0;
	public static TextureAtlasSprite CACHE_TOP_1;
	public static TextureAtlasSprite CACHE_TOP_2;
	public static TextureAtlasSprite CACHE_TOP_3;
	public static TextureAtlasSprite CACHE_TOP_4;
	public static TextureAtlasSprite CACHE_TOP_C;

	public static TextureAtlasSprite[] CACHE_BOTTOM;
	public static TextureAtlasSprite CACHE_BOTTOM_0;
	public static TextureAtlasSprite CACHE_BOTTOM_1;
	public static TextureAtlasSprite CACHE_BOTTOM_2;
	public static TextureAtlasSprite CACHE_BOTTOM_3;
	public static TextureAtlasSprite CACHE_BOTTOM_4;
	public static TextureAtlasSprite CACHE_BOTTOM_C;

	public static TextureAtlasSprite[] CACHE_SIDE;
	public static TextureAtlasSprite CACHE_SIDE_0;
	public static TextureAtlasSprite CACHE_SIDE_1;
	public static TextureAtlasSprite CACHE_SIDE_2;
	public static TextureAtlasSprite CACHE_SIDE_3;
	public static TextureAtlasSprite CACHE_SIDE_4;
	public static TextureAtlasSprite CACHE_SIDE_C;

	public static TextureAtlasSprite[] CACHE_FACE;
	public static TextureAtlasSprite CACHE_FACE_0;
	public static TextureAtlasSprite CACHE_FACE_1;
	public static TextureAtlasSprite CACHE_FACE_2;
	public static TextureAtlasSprite CACHE_FACE_3;
	public static TextureAtlasSprite CACHE_FACE_4;
	public static TextureAtlasSprite CACHE_FACE_C;

	public static TextureAtlasSprite[] CACHE_METER;
	public static TextureAtlasSprite CACHE_METER_0;
	public static TextureAtlasSprite CACHE_METER_1;
	public static TextureAtlasSprite CACHE_METER_2;
	public static TextureAtlasSprite CACHE_METER_3;
	public static TextureAtlasSprite CACHE_METER_4;
	public static TextureAtlasSprite CACHE_METER_5;
	public static TextureAtlasSprite CACHE_METER_6;
	public static TextureAtlasSprite CACHE_METER_7;
	public static TextureAtlasSprite CACHE_METER_8;
	public static TextureAtlasSprite CACHE_METER_C;

	public static TextureAtlasSprite ILLUMINATOR_FRAME;

}
