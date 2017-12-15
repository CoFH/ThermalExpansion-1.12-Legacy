package cofh.thermalexpansion.init;

import codechicken.lib.block.property.unlisted.UnlistedGenericTile;
import cofh.CoFHCore;
import cofh.core.gui.CreativeTabCore;
import cofh.core.util.CoreUtils;
import cofh.core.util.TimeTracker;
import cofh.core.util.helpers.MathHelper;
import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalexpansion.block.apparatus.TileApparatusBase;
import cofh.thermalexpansion.block.device.TileDeviceBase;
import cofh.thermalexpansion.block.dynamo.TileDynamoBase;
import cofh.thermalexpansion.block.machine.BlockMachine;
import cofh.thermalexpansion.block.machine.TileMachineBase;
import cofh.thermalexpansion.block.storage.TileCache;
import cofh.thermalexpansion.block.storage.TileCell;
import cofh.thermalexpansion.block.storage.TileTank;
import cofh.thermalexpansion.item.ItemFlorb;
import cofh.thermalexpansion.item.ItemMorb;
import cofh.thermalfoundation.init.TFProps;
import cofh.thermalfoundation.item.ItemMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TEProps {

	private TEProps() {

	}

	public static void preInit() {

		configCommon();
		configClient();
	}

	/* HELPERS */
	private static void configCommon() {

		String category;
		String comment;

		category = "Upgrades";

		comment = "This sets the minimum upgradeable block tier for Automatic Input functionality.";
		levelAutoInput = ThermalExpansion.CONFIG.getConfiguration().getInt("LevelAutoInput", category, levelAutoInput, TFProps.LEVEL_MIN, TFProps.LEVEL_MAX, comment);

		comment = "This sets the minimum upgradeable block tier for Automatic Output functionality.";
		levelAutoOutput = ThermalExpansion.CONFIG.getConfiguration().getInt("LevelAutoOutput", category, levelAutoOutput, TFProps.LEVEL_MIN, TFProps.LEVEL_MAX, comment);

		comment = "This sets the minimum upgradeable block tier for Redstone Control functionality.";
		levelRedstoneControl = ThermalExpansion.CONFIG.getConfiguration().getInt("LevelRedstoneControl", category, levelRedstoneControl, TFProps.LEVEL_MIN, TFProps.LEVEL_MAX, comment);
	}

	private static void configClient() {

		String category;
		String comment;

		category = "Interface";
		boolean itemTabCommon = false;
		boolean florbTabCommon = false;
		boolean morbTabCommon = false;

		comment = "If TRUE, Thermal Expansion Items and Tools appear under the general \"Thermal Expansion\" Creative Tab.";
		itemTabCommon = ThermalExpansion.CONFIG_CLIENT.getConfiguration().getBoolean("ItemsInCommonTab", category, itemTabCommon, comment);

		comment = "If TRUE, Thermal Expansion Florbs appear under the general \"Thermal Expansion\" Creative Tab. Not really recommended.";
		florbTabCommon = ThermalExpansion.CONFIG_CLIENT.getConfiguration().getBoolean("FlorbsInCommonTab", category, florbTabCommon, comment);

		comment = "If TRUE, Thermal Expansion Morbs appear under the general \"Thermal Expansion\" Creative Tab. Not really recommended.";
		morbTabCommon = ThermalExpansion.CONFIG_CLIENT.getConfiguration().getBoolean("MorbsInCommonTab", category, morbTabCommon, comment);

		category = "Interface.CreativeTabs";

		comment = "Set the default level for the Blocks shown in the Creative Tab, if all levels are not shown.";
		creativeTabLevel = ThermalExpansion.CONFIG_CLIENT.getConfiguration().getInt("DefaultLevel", category, creativeTabLevel, TFProps.LEVEL_MIN, TFProps.LEVEL_MAX, comment);

		comment = "If TRUE, all regular levels for a given Block will show in the Creative Tab.";
		creativeTabShowAllLevels = ThermalExpansion.CONFIG_CLIENT.getConfiguration().getBoolean("ShowAllBlockLevels", category, creativeTabShowAllLevels, comment);

		comment = "If TRUE, Creative version of Blocks will show in the Creative Tab.";
		creativeTabShowCreative = ThermalExpansion.CONFIG_CLIENT.getConfiguration().getBoolean("ShowCreativeBlocks", category, creativeTabShowCreative, comment);

		category = "Sounds";

		comment = "If TRUE, various Thermal Expansion Blocks will play ambient sounds when active.";
		enableSounds = ThermalExpansion.CONFIG_CLIENT.getConfiguration().getBoolean("EnableSounds", category, enableSounds, comment);

		/* CREATIVE TABS */
		ThermalExpansion.tabCommon = new CreativeTabCore("thermalexpansion") {

			@Override
			@SideOnly (Side.CLIENT)
			public ItemStack getIconItemStack() {

				return BlockMachine.machineFurnace;
			}

		};

		if (itemTabCommon) {
			ThermalExpansion.tabItems = ThermalExpansion.tabCommon;
		} else {
			ThermalExpansion.tabItems = new CreativeTabCore("thermalexpansion", "Items") {

				@Override
				@SideOnly (Side.CLIENT)
				public ItemStack getIconItemStack() {

					return ItemMaterial.powerCoilElectrum;
				}

			};
		}
		ThermalExpansion.tabFlorbs = florbTabCommon ? ThermalExpansion.tabCommon : new CreativeTabCore("thermalexpansion", "Florbs") {

			int iconIndex = 0;
			TimeTracker iconTracker = new TimeTracker();

			void updateIcon() {

				World world = CoFHCore.proxy.getClientWorld();
				if (CoreUtils.isClient() && iconTracker.hasDelayPassed(world, 80)) {
					int next = MathHelper.RANDOM.nextInt(ItemFlorb.florbList.size() - 1);
					iconIndex = next >= iconIndex ? next + 1 : next;
					iconTracker.markTime(world);
				}
			}

			@Override
			@SideOnly (Side.CLIENT)
			public ItemStack getIconItemStack() {

				updateIcon();
				return ItemFlorb.florbList.get(iconIndex);
			}

		};
		ThermalExpansion.tabMorbs = morbTabCommon ? ThermalExpansion.tabCommon : new CreativeTabCore("thermalexpansion", "Morbs") {

			int iconIndex = 0;
			TimeTracker iconTracker = new TimeTracker();

			void updateIcon() {

				World world = CoFHCore.proxy.getClientWorld();
				if (CoreUtils.isClient() && iconTracker.hasDelayPassed(world, 80)) {
					int next = MathHelper.RANDOM.nextInt(ItemMorb.morbList.size() - 1);
					iconIndex = next >= iconIndex ? next + 1 : next;
					iconTracker.markTime(world);
				}
			}

			@Override
			@SideOnly (Side.CLIENT)
			public ItemStack getIconItemStack() {

				updateIcon();
				return ItemMorb.morbList.get(iconIndex);
			}

		};
	}

	/* GENERAL */
	public static final int MAX_FLUID_SMALL = Fluid.BUCKET_VOLUME * 4;
	public static final int MAX_FLUID_MEDIUM = Fluid.BUCKET_VOLUME * 8;
	public static final int MAX_FLUID_LARGE = Fluid.BUCKET_VOLUME * 10;

	public static boolean creativeTabShowAllLevels = false;
	public static boolean creativeTabShowCreative = false;
	public static int creativeTabLevel = 0;

	public static boolean enableSounds = true;

	/* UPGRADE FEATURES */
	public static int levelAutoInput = 0;
	public static int levelAutoOutput = 0;
	public static int levelRedstoneControl = 0;

	/* TEXTURES */
	public static final String PATH_GFX = "thermalexpansion:textures/";
	public static final String PATH_ARMOR = PATH_GFX + "armor/";
	public static final String PATH_GUI = PATH_GFX + "gui/";
	public static final String PATH_ENTITY = PATH_GFX + "entity/";
	public static final String PATH_RENDER = PATH_GFX + "blocks/";
	public static final String PATH_ELEMENTS = PATH_GUI + "elements/";

	public static final String PATH_GUI_APPARATUS = PATH_GUI + "apparatus/";
	public static final String PATH_GUI_DEVICE = PATH_GUI + "device/";
	public static final String PATH_GUI_DYNAMO = PATH_GUI + "dynamo/";
	public static final String PATH_GUI_MACHINE = PATH_GUI + "machine/";
	public static final String PATH_GUI_STORAGE = PATH_GUI + "storage/";

	public static final ResourceLocation PATH_SLOTS = new ResourceLocation(PATH_ELEMENTS + "slots.png");
	public static final ResourceLocation PATH_SLOTS_4 = new ResourceLocation(PATH_ELEMENTS + "slots_4.png");

	public static final String TEXTURE_DEFAULT = "config_";

	public static ResourceLocation textureGuiCommon = PATH_SLOTS;
	public static ResourceLocation textureGuiSlots4 = PATH_SLOTS_4;
	public static ResourceLocation textureGuiSlotsCrafter = PATH_SLOTS;
	public static String textureSelection = TEXTURE_DEFAULT;

	/* BLOCKSTATE PROPERTIES */
	public static final UnlistedGenericTile<TileApparatusBase> TILE_APPARATUS = new UnlistedGenericTile<>("tile_apparatus", TileApparatusBase.class);
	public static final UnlistedGenericTile<TileMachineBase> TILE_MACHINE = new UnlistedGenericTile<>("tile_machine", TileMachineBase.class);
	public static final UnlistedGenericTile<TileDynamoBase> TILE_DYNAMO = new UnlistedGenericTile<>("tile_dynamo", TileDynamoBase.class);
	public static final UnlistedGenericTile<TileTank> TILE_TANK = new UnlistedGenericTile<>("tile_tank", TileTank.class);
	public static final UnlistedGenericTile<TileCell> TILE_CELL = new UnlistedGenericTile<>("tile_cell", TileCell.class);
	public static final UnlistedGenericTile<TileCache> TILE_CACHE = new UnlistedGenericTile<>("tile_cache", TileCache.class);
	public static final UnlistedGenericTile<TileDeviceBase> TILE_DEVICE = new UnlistedGenericTile<>("tile_device", TileDeviceBase.class);

	/* AUGMENT IDENTIFIERS */

	/* MACHINES */
	public static final String MACHINE_POWER = "machinePower";
	public static final String MACHINE_SECONDARY = "machineSecondary";

	public static final String MACHINE_SECONDARY_NULL = "machineSecondaryNull";

	public static final String MACHINE_FURNACE_FOOD = "machineFurnaceFood";
	public static final String MACHINE_FURNACE_ORE = "machineFurnaceOre";
	public static final String MACHINE_FURNACE_PYROLYSIS = "machineFurnacePyrolysis";

	public static final String MACHINE_PULVERIZER_GEODE = "machinePulverizerGeode";
	public static final String MACHINE_PULVERIZER_PETROTHEUM = "machinePulverizerPetrotheum";

	public static final String MACHINE_SAWMILL_TAPPER = "machineSawmillTapper";

	public static final String MACHINE_SMELTER_PYROTHEUM = "machineSmelterPyrotheum";
	public static final String MACHINE_SMELTER_FLUX = "machineSmelterFlux";

	public static final String MACHINE_INSOLATOR_FERTILIZER = "machineInsolatorFertilizer";
	public static final String MACHINE_INSOLATOR_MONOCULTURE = "machineInsolatorMonoculture";
	public static final String MACHINE_INSOLATOR_TREE = "machineInsolatorTree";

	public static final String MACHINE_COMPACTOR_MINT = "machineCompactorMint";
	public static final String MACHINE_COMPACTOR_GEAR = "machineCompactorGear";

	public static final String MACHINE_CRUCIBLE_LAVA = "machineCrucibleLava";
	public static final String MACHINE_CRUCIBLE_ALLOY = "machineCrucibleAlloy";

	public static final String MACHINE_REFINERY_OIL = "machineRefineryOil";
	public static final String MACHINE_REFINERY_POTION = "machineRefineryPotion";

	public static final String MACHINE_CHARGER_THROUGHPUT = "machineChargerThroughput";
	public static final String MACHINE_CHARGER_REPAIR = "machineChargerRepair";
	public static final String MACHINE_CHARGER_WIRELESS = "machineChargerWireless";

	public static final String MACHINE_BREWER_REAGENT = "machineBrewerReagent";

	public static final String MACHINE_ENCHANTER_EMPOWERED = "machineEnchanterEmpowered";

	public static final String MACHINE_EXTRUDER_NO_WATER = "machineExtruderNoWater";

	/* DYNAMOS */
	public static final String DYNAMO_POWER = "dynamoPower";
	public static final String DYNAMO_EFFICIENCY = "dynamoEfficiency";
	public static final String DYNAMO_COIL_DUCT = "dynamoCoilDuct";
	public static final String DYNAMO_THROTTLE = "dynamoThrottle";

	public static final String DYNAMO_BOILER = "dynamoBoiler";

	public static final String DYNAMO_STEAM_TURBINE = "dynamoSteamTurbine";

	public static final String DYNAMO_MAGMATIC_COOLANT = "dynamoMagmaticCoolant";

	public static final String DYNAMO_COMPRESSION_COOLANT = "dynamoCompressionCoolant";
	public static final String DYNAMO_COMPRESSION_FUEL = "dynamoCompressionFuel";

	public static final String DYNAMO_REACTANT_ELEMENTAL = "dynamoReactantElemental";

	public static final String DYNAMO_ENERVATION_ENCHANT = "dynamoEnervationEnchant";

	public static final String DYNAMO_NUMISMATIC_GEM = "dynamoNumismaticGem";

	/* AUTOMATA */
	public static final String APPARATUS_DEPTH = "apparatusDepth";
	public static final String APPARATUS_RADIUS = "apparatusRadius";

}
