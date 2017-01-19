package cofh.thermalexpansion.init;

import cofh.core.CoFHProps;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * Holds references for TE's Textures.
 *
 * TODO Order entries based on Registry order.
 *
 * Created by covers1624 on 17/01/2017.
 */
public class TETextures {

	@SubscribeEvent
	public void registerIcons(TextureStitchEvent.Pre event) {

		TextureMap map = event.getMap();

		// @formatter:off \o/ Formatter!
		CONFIG_NONE =   register(map, CONFIG_ + "none");
		CONFIG_BLUE =   registerCB(map, CONFIG_ + "blue");
		CONFIG_RED =    registerCB(map, CONFIG_ + "red");
		CONFIG_YELLOW = registerCB(map, CONFIG_ + "yellow");
		CONFIG_ORANGE = registerCB(map, CONFIG_ + "orange");
		CONFIG_GREEN =  registerCB(map, CONFIG_ + "green");
		CONFIG_PURPLE = registerCB(map, CONFIG_ + "purple");
		CONFIG_OPEN =   register(map, CONFIG_ + "open");

		MACHINE_BOTTOM = register(map, BLOCKS_ + "machine/machine_bottom");
		MACHINE_TOP =    register(map, BLOCKS_ + "machine/machine_top");
		MACHINE_SIDE =  register(map, BLOCKS_ + "machine/machine_side");

		MACHINE_FACE_FURNACE =         register(map, MACHINE_FACE_ + "furnace");
		MACHINE_FACE_PULVERIZER =      register(map, MACHINE_FACE_ + "pulverizer");
		MACHINE_FACE_SAWMILL =         register(map, MACHINE_FACE_ + "sawmill");
		MACHINE_FACE_SMELTER =         register(map, MACHINE_FACE_ + "smelter");
		MACHINE_FACE_INSOLATOR =       register(map, MACHINE_FACE_ + "insolator");
		MACHINE_FACE_CHARGER =         register(map, MACHINE_FACE_ + "charger");
		MACHINE_FACE_CRUCIBLE =        register(map, MACHINE_FACE_ + "crucible");
		MACHINE_FACE_TRANSPOSER =      register(map, MACHINE_FACE_ + "transposer");
		MACHINE_FACE_TRANSCAPSULATOR = map.getMissingSprite();//TODO register(map, MACHINE_FACE_ + "transcapsulator");
		MACHINE_FACE_CENTRIFUGE =      map.getMissingSprite();//TODO register(map, MACHINE_FACE_ + "centrifuge");
		MACHINE_FACE_CRAFTER =         register(map, MACHINE_FACE_ + "crafter");
		MACHINE_FACE_BREWER =          map.getMissingSprite();//TODO register(map, MACHINE_FACE_ + "brewer");
		MACHINE_FACE_ENCHANTER =       map.getMissingSprite();//TODO register(map, MACHINE_FACE_ + "enchanter");
		MACHINE_FACE_PRECIPITATOR =    register(map, MACHINE_FACE_ + "precipitator");
		MACHINE_FACE_EXTRUDER =        register(map, MACHINE_FACE_ + "extruder");

		MACHINE_ACTIVE_FURNACE =         register(map, MACHINE_ACTIVE_ + "furnace");
		MACHINE_ACTIVE_PULVERIZER =      register(map, MACHINE_ACTIVE_ + "pulverizer");
		MACHINE_ACTIVE_SAWMILL =         register(map, MACHINE_ACTIVE_ + "sawmill");
		MACHINE_ACTIVE_SMELTER =         register(map, MACHINE_ACTIVE_ + "smelter");
		MACHINE_ACTIVE_INSOLATOR =       register(map, MACHINE_ACTIVE_ + "insolator");
		MACHINE_ACTIVE_CHARGER =         register(map, MACHINE_ACTIVE_ + "charger");
		MACHINE_ACTIVE_CRUCIBLE =        register(map, MACHINE_ACTIVE_ + "crucible");
		MACHINE_ACTIVE_TRANSPOSER =      register(map, MACHINE_ACTIVE_ + "transposer");
		MACHINE_ACTIVE_TRANSCAPSULATOR = map.getMissingSprite();//TODO register(map, MACHINE_ACTIVE_ + "transcapsulator");
		MACHINE_ACTIVE_CENTRIFUGE =      map.getMissingSprite();//TODO register(map, MACHINE_ACTIVE_ + "centrifuge");
		MACHINE_ACTIVE_CRAFTER =         register(map, MACHINE_ACTIVE_ + "assembler");
		MACHINE_ACTIVE_BREWER =          map.getMissingSprite();//TODO register(map, MACHINE_ACTIVE_ + "brewer");
		MACHINE_ACTIVE_ENCHANTER =       map.getMissingSprite();//TODO register(map, MACHINE_ACTIVE_ + "enchanter");
		MACHINE_ACTIVE_PRECIPITATOR =    register(map, MACHINE_ACTIVE_ + "precipitator");
		MACHINE_ACTIVE_EXTRUDER =        register(map, MACHINE_ACTIVE_ + "extruder");

		DEVICE_SIDE = register(map, BLOCKS_ + "device/device_side");

		DEVICE_FACE_ACTIVATOR = register(map, DEVICE_FACE_ + "activator");
		DEVICE_FACE_BREAKER =   register(map, DEVICE_FACE_ + "breaker");
		DEVICE_FACE_COLLECTOR = register(map, DEVICE_FACE_ + "collector");
		DEVICE_FACE_WATERGEN =  map.getMissingSprite();//TODO register(map, DEVICE_FACE_ + "watergen");
		DEVICE_FACE_NULLIFIER = register(map, DEVICE_FACE_ + "nullifier");
		DEVICE_FACE_BUFFER =    register(map, DEVICE_FACE_ + "buffer");
		DEVICE_FACE_EXTENDER =  map.getMissingSprite();//TODO register(map, DEVICE_FACE_ + "extender");

		DEVICE_ACTIVE_ACTIVATOR = register(map, DEVICE_ACTIVE_ + "activator");
		DEVICE_ACTIVE_BREAKER =   register(map, DEVICE_ACTIVE_ + "breaker");
		DEVICE_ACTIVE_COLLECTOR = register(map, DEVICE_ACTIVE_ + "collector");
		DEVICE_ACTIVE_WATERGEN =  map.getMissingSprite();//TODO register(map, DEVICE_ACTIVE_ + "watergen");
		DEVICE_ACTIVE_NULLIFIER = register(map, DEVICE_ACTIVE_ + "nullifier");
		DEVICE_ACTIVE_BUFFER =    register(map, DEVICE_ACTIVE_ + "buffer");
		DEVICE_ACTIVE_EXTENDER =  map.getMissingSprite();//TODO register(map, DEVICE_ACTIVE_ + "extender");

		DYNAMO_COIL_REDSTONE = register(map, DYNAMO_ + "coil_redstone");

		DYNAMO_STEAM =       register(map, DYNAMO_ + "steam");
		DYNAMO_MAGMATIC =    register(map, DYNAMO_ + "magmatic");
		DYNAMO_COMPRESSION = register(map, DYNAMO_ + "compression");
		DYNAMO_REACTANT =    register(map, DYNAMO_ + "reactant");
		DYNAMO_ENERVATION =  register(map, DYNAMO_ + "enervation");


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
				MACHINE_FACE_CHARGER,
				MACHINE_FACE_CRUCIBLE,
				MACHINE_FACE_TRANSPOSER,
				MACHINE_FACE_TRANSCAPSULATOR,
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
				MACHINE_ACTIVE_CHARGER,
				MACHINE_ACTIVE_CRUCIBLE,
				MACHINE_ACTIVE_TRANSPOSER,
				MACHINE_ACTIVE_TRANSCAPSULATOR,
				MACHINE_ACTIVE_CENTRIFUGE,
				MACHINE_ACTIVE_CRAFTER,
				MACHINE_ACTIVE_BREWER,
				MACHINE_ACTIVE_ENCHANTER,
				MACHINE_ACTIVE_PRECIPITATOR,
				MACHINE_ACTIVE_EXTRUDER

		};
		DEVICE_FACE = new TextureAtlasSprite[] {
				DEVICE_FACE_ACTIVATOR,
				DEVICE_FACE_BREAKER,
				DEVICE_FACE_COLLECTOR,
				DEVICE_FACE_WATERGEN,
				DEVICE_FACE_NULLIFIER,
				DEVICE_FACE_BUFFER,
				DEVICE_FACE_EXTENDER
		};
		DEVICE_ACTIVE = new TextureAtlasSprite[] {
				DEVICE_ACTIVE_ACTIVATOR,
				DEVICE_ACTIVE_BREAKER,
				DEVICE_ACTIVE_COLLECTOR,
				DEVICE_ACTIVE_WATERGEN,
				DEVICE_ACTIVE_NULLIFIER,
				DEVICE_ACTIVE_BUFFER,
				DEVICE_ACTIVE_EXTENDER
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

	// Bouncer for registering ColourBlind textures.
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
	private static String DEVICE_FACE_ = BLOCKS_ + "device/device_face_";
	private static String DEVICE_ACTIVE_ = BLOCKS_ + "device/device_active_";
	private static String DYNAMO_ = BLOCKS_ + "dynamo/dynamo_";

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
	public static TextureAtlasSprite MACHINE_FACE_CHARGER;
	public static TextureAtlasSprite MACHINE_FACE_CRUCIBLE;
	public static TextureAtlasSprite MACHINE_FACE_TRANSPOSER;
	public static TextureAtlasSprite MACHINE_FACE_TRANSCAPSULATOR;
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
	public static TextureAtlasSprite MACHINE_ACTIVE_CHARGER;
	public static TextureAtlasSprite MACHINE_ACTIVE_CRUCIBLE;
	public static TextureAtlasSprite MACHINE_ACTIVE_TRANSPOSER;
	public static TextureAtlasSprite MACHINE_ACTIVE_TRANSCAPSULATOR;
	public static TextureAtlasSprite MACHINE_ACTIVE_CENTRIFUGE;
	public static TextureAtlasSprite MACHINE_ACTIVE_CRAFTER;
	public static TextureAtlasSprite MACHINE_ACTIVE_BREWER;
	public static TextureAtlasSprite MACHINE_ACTIVE_ENCHANTER;
	public static TextureAtlasSprite MACHINE_ACTIVE_PRECIPITATOR;
	public static TextureAtlasSprite MACHINE_ACTIVE_EXTRUDER;

	public static TextureAtlasSprite DEVICE_SIDE;

	public static TextureAtlasSprite[] DEVICE_FACE;
	public static TextureAtlasSprite DEVICE_FACE_ACTIVATOR;
	public static TextureAtlasSprite DEVICE_FACE_BREAKER;
	public static TextureAtlasSprite DEVICE_FACE_COLLECTOR;
	public static TextureAtlasSprite DEVICE_FACE_WATERGEN;
	public static TextureAtlasSprite DEVICE_FACE_NULLIFIER;
	public static TextureAtlasSprite DEVICE_FACE_BUFFER;
	public static TextureAtlasSprite DEVICE_FACE_EXTENDER;

	public static TextureAtlasSprite[] DEVICE_ACTIVE;
	public static TextureAtlasSprite DEVICE_ACTIVE_ACTIVATOR;
	public static TextureAtlasSprite DEVICE_ACTIVE_BREAKER;
	public static TextureAtlasSprite DEVICE_ACTIVE_COLLECTOR;
	public static TextureAtlasSprite DEVICE_ACTIVE_WATERGEN;
	public static TextureAtlasSprite DEVICE_ACTIVE_NULLIFIER;
	public static TextureAtlasSprite DEVICE_ACTIVE_BUFFER;
	public static TextureAtlasSprite DEVICE_ACTIVE_EXTENDER;

	public static TextureAtlasSprite DYNAMO_COIL_REDSTONE;

	public static TextureAtlasSprite[] DYNAMO;
	public static TextureAtlasSprite DYNAMO_STEAM;
	public static TextureAtlasSprite DYNAMO_MAGMATIC;
	public static TextureAtlasSprite DYNAMO_COMPRESSION;
	public static TextureAtlasSprite DYNAMO_REACTANT;
	public static TextureAtlasSprite DYNAMO_ENERVATION;

}
