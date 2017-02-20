package cofh.thermalexpansion.init;

import cofh.core.init.CoreProps;
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
		MACHINE_BOTTOM =					register(map, BLOCKS_ + "machine/machine_bottom");
		MACHINE_TOP =						register(map, BLOCKS_ + "machine/machine_top");
		MACHINE_SIDE =						register(map, BLOCKS_ + "machine/machine_side");

		MACHINE_FACE_FURNACE =				register(map, MACHINE_FACE_ + "furnace");
		MACHINE_FACE_PULVERIZER =			register(map, MACHINE_FACE_ + "pulverizer");
		MACHINE_FACE_SAWMILL =				register(map, MACHINE_FACE_ + "sawmill");
		MACHINE_FACE_SMELTER =				register(map, MACHINE_FACE_ + "smelter");
		MACHINE_FACE_INSOLATOR =			register(map, MACHINE_FACE_ + "insolator");
		MACHINE_FACE_COMPACTOR =			map.getMissingSprite();
		MACHINE_FACE_CRUCIBLE =				register(map, MACHINE_FACE_ + "crucible");
		MACHINE_FACE_REFINERY =             map.getMissingSprite();//TODO register(map, MACHINE_FACE_ + "refinery");
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
		MACHINE_ACTIVE_REFINERY =           map.getMissingSprite();//TODO register(map, MACHINE_ACTIVE_ + "refinery");
		MACHINE_ACTIVE_TRANSPOSER =			register(map, MACHINE_ACTIVE_ + "transposer");
		MACHINE_ACTIVE_CHARGER =			register(map, MACHINE_ACTIVE_ + "charger");
		MACHINE_ACTIVE_CENTRIFUGE =			map.getMissingSprite();//TODO register(map, MACHINE_ACTIVE_ + "centrifuge");
		MACHINE_ACTIVE_CRAFTER =			register(map, MACHINE_ACTIVE_ + "crafter");
		MACHINE_ACTIVE_BREWER =				map.getMissingSprite();//TODO register(map, MACHINE_ACTIVE_ + "brewer");
		MACHINE_ACTIVE_ENCHANTER =			map.getMissingSprite();//TODO register(map, MACHINE_ACTIVE_ + "enchanter");
		MACHINE_ACTIVE_PRECIPITATOR =		register(map, MACHINE_ACTIVE_ + "precipitator");
		MACHINE_ACTIVE_EXTRUDER =			register(map, MACHINE_ACTIVE_ + "extruder");

		/* AUTOMATA */
		AUTOMATON_SIDE =					register(map, BLOCKS_ + "automaton/automaton_side");

		AUTOMATON_FACE_BREAKER =			register(map, AUTOMATON_FACE_ + "breaker");
		AUTOMATON_FACE_COLLECTOR =			register(map, AUTOMATON_FACE_ + "collector");

		AUTOMATON_ACTIVE_BREAKER =			register(map, AUTOMATON_ACTIVE_ + "breaker");
		AUTOMATON_ACTIVE_COLLECTOR =		register(map, AUTOMATON_ACTIVE_ + "collector");

		/* DEVICES */
		DEVICE_SIDE =						register(map, BLOCKS_ + "device/device_side");

		DEVICE_FACE_WATERGEN =				map.getMissingSprite();//TODO register(map, DEVICE_FACE_ + "water_gen");
		DEVICE_FACE_NULLIFIER =				register(map, DEVICE_FACE_ + "nullifier");
		DEVICE_FACE_HEAT_SINK =             map.getMissingSprite();//TODO register(map, DEVICE_FACE_ + "heat_sink");
		DEVICE_FACE_TAPPER =                map.getMissingSprite();//TODO register(map, DEVICE_FACE_ + "tapper");

		DEVICE_FACE_ITEM_BUFFER =			register(map, DEVICE_FACE_ + "item_buffer");
		DEVICE_FACE_EXTENDER =				map.getMissingSprite();//TODO register(map, DEVICE_FACE_ + "extender");

		DEVICE_ACTIVE_WATERGEN =			map.getMissingSprite();//TODO register(map, DEVICE_ACTIVE_ + "water_gen");
		DEVICE_ACTIVE_NULLIFIER =			register(map, DEVICE_ACTIVE_ + "nullifier");
		DEVICE_ACTIVE_HEAT_SINK =           map.getMissingSprite();//TODO register(map, DEVICE_ACTIVE_ + "heat_sink");
		DEVICE_ACTIVE_TAPPER =              map.getMissingSprite();//TODO register(map, DEVICE_ACTIVE_ + "tapper");

		DEVICE_ACTIVE_ITEM_BUFFER =			register(map, DEVICE_ACTIVE_ + "item_buffer");
		DEVICE_ACTIVE_EXTENDER =			map.getMissingSprite();//TODO register(map, DEVICE_ACTIVE_ + "extender");

		/* DYNAMOS */
		DYNAMO_COIL_REDSTONE =				register(map, DYNAMO_ + "coil_redstone");

		DYNAMO_STEAM =						register(map, DYNAMO_ + "steam");
		DYNAMO_MAGMATIC =					register(map, DYNAMO_ + "magmatic");
		DYNAMO_COMPRESSION =				register(map, DYNAMO_ + "compression");
		DYNAMO_REACTANT =					register(map, DYNAMO_ + "reactant");
		DYNAMO_ENERVATION =					register(map, DYNAMO_ + "enervation");

		/* ICONS */
		ICON_ACCEPT =						map.getMissingSprite();//TODO
		ICON_ACCEPT_INACTIVE =				map.getMissingSprite();//TODO
		ICON_SCHEMATIC =					map.getMissingSprite();//TODO

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
				DYNAMO_ENERVATION
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
	private static final String MACHINE_FACE_ = BLOCKS_ + "machine/machine_face_";
	private static final String MACHINE_ACTIVE_ = BLOCKS_ + "machine/machine_active_";
	private static final String AUTOMATON_FACE_ = BLOCKS_ + "automaton/automaton_face_";
	private static final String AUTOMATON_ACTIVE_ = BLOCKS_ + "automaton/automaton_active_";
	private static final String DEVICE_FACE_ = BLOCKS_ + "device/device_face_";
	private static final String DEVICE_ACTIVE_ = BLOCKS_ + "device/device_active_";
	private static final String DYNAMO_ = BLOCKS_ + "dynamo/dynamo_";

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

	public static TextureAtlasSprite AUTOMATON_SIDE;

	public static TextureAtlasSprite[] AUTOMATON_FACE;
	public static TextureAtlasSprite AUTOMATON_FACE_BREAKER;
	public static TextureAtlasSprite AUTOMATON_FACE_COLLECTOR;

	public static TextureAtlasSprite[] AUTOMATON_ACTIVE;
	public static TextureAtlasSprite AUTOMATON_ACTIVE_BREAKER;
	public static TextureAtlasSprite AUTOMATON_ACTIVE_COLLECTOR;

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

	public static TextureAtlasSprite DYNAMO_COIL_REDSTONE;

	public static TextureAtlasSprite[] DYNAMO;
	public static TextureAtlasSprite DYNAMO_STEAM;
	public static TextureAtlasSprite DYNAMO_MAGMATIC;
	public static TextureAtlasSprite DYNAMO_COMPRESSION;
	public static TextureAtlasSprite DYNAMO_REACTANT;
	public static TextureAtlasSprite DYNAMO_ENERVATION;

	public static TextureAtlasSprite ICON_ACCEPT;
	public static TextureAtlasSprite ICON_ACCEPT_INACTIVE;
	public static TextureAtlasSprite ICON_SCHEMATIC;

}
