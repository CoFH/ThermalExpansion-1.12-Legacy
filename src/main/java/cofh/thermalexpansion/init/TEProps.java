package cofh.thermalexpansion.init;

import codechicken.lib.block.property.unlisted.UnlistedGenericTile;
import cofh.CoFHCore;
import cofh.core.gui.CreativeTabCore;
import cofh.core.network.PacketBase;
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
import cofh.thermalexpansion.network.PacketTEBase;
import cofh.thermalexpansion.network.PacketTEBase.PacketTypes;
import cofh.thermalexpansion.util.UnlistedGenericProperty;
import cofh.thermalfoundation.ThermalFoundation;
import cofh.thermalfoundation.init.TFProps;
import cofh.thermalfoundation.item.ItemMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IBlockAccess;
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

		boolean slotOverlayAlt;
		boolean slotOverlayCB;

		comment = "If TRUE, Thermal Expansion Items and Tools appear under the general \"Thermal Expansion\" Creative Tab. Does not work if \"Thermal Series\" Creative Tabs are in use.";
		itemTabCommon = ThermalExpansion.CONFIG_CLIENT.getConfiguration().getBoolean("ItemsInCommonTab", category, itemTabCommon, comment);

		comment = "If TRUE, Thermal Expansion Florbs appear under the general \"Thermal Expansion\" Creative Tab. Does not work if \"Thermal Series\" Creative Tabs are in use.";
		florbTabCommon = ThermalExpansion.CONFIG_CLIENT.getConfiguration().getBoolean("FlorbsInCommonTab", category, florbTabCommon, comment);

		comment = "If TRUE, Thermal Expansion Morbs appear under the general \"Thermal Expansion\" Creative Tab. Does not work if \"Thermal Series\" Creative Tabs are in use.";
		morbTabCommon = ThermalExpansion.CONFIG_CLIENT.getConfiguration().getBoolean("MorbsInCommonTab", category, morbTabCommon, comment);

		category = "Interface.CreativeTabs";

		comment = "Set the default level for the Blocks shown in the Creative Tab, if all levels are not shown.";
		creativeTabLevel = ThermalExpansion.CONFIG_CLIENT.getConfiguration().getInt("DefaultLevel", category, creativeTabLevel, TFProps.LEVEL_MIN, TFProps.LEVEL_MAX, comment);

		comment = "If TRUE, all regular levels for a given Block will show in the Creative Tab.";
		creativeTabShowAllBlockLevels = ThermalExpansion.CONFIG_CLIENT.getConfiguration().getBoolean("ShowAllBlockLevels", category, creativeTabShowAllBlockLevels, comment);

		comment = "If TRUE, Creative version of Blocks will show in the Creative Tab.";
		creativeTabShowCreative = ThermalExpansion.CONFIG_CLIENT.getConfiguration().getBoolean("ShowCreativeBlocks", category, creativeTabShowCreative, comment);

		comment = "If TRUE, Florbs will be completely hidden from Creative Mode and JEI.";
		creativeTabHideFlorbs = ThermalExpansion.CONFIG_CLIENT.getConfiguration().getBoolean("HideFlorbs", category, creativeTabHideFlorbs, comment);

		comment = "If TRUE, Morbs will be completely hidden from Creative Mode and JEI.";
		creativeTabHideMorbs = ThermalExpansion.CONFIG_CLIENT.getConfiguration().getBoolean("HideMorbs", category, creativeTabHideMorbs, comment);

		category = "Interface.GUI";

		comment = "If TRUE, alternate slot overlay textures will be used in GUIs which use them. Can be combined with Color Blind textures.";
		slotOverlayAlt = ThermalExpansion.CONFIG_CLIENT.getConfiguration().getBoolean("AlternateSlotOverlayTextures", category, morbTabCommon, comment);

		comment = "If TRUE, color blind slot overlay textures will be used in GUIs which use them. Can be combined with Alternate textures.";
		slotOverlayCB = ThermalExpansion.CONFIG_CLIENT.getConfiguration().getBoolean("ColorBlindSlotOverlayTextures", category, morbTabCommon, comment);

		category = "Sounds";

		comment = "If TRUE, various Thermal Expansion Blocks will play ambient sounds when active.";
		enableSounds = ThermalExpansion.CONFIG_CLIENT.getConfiguration().getBoolean("EnableSounds", category, enableSounds, comment);

		category = "Render";

		comment = "If TRUE, Dynamos will have animated coil textures.";
		animatedDynamoCoilTexture = ThermalExpansion.CONFIG_CLIENT.getConfiguration().getBoolean("AnimatedDynamoCoilTextures", category, animatedDynamoCoilTexture, comment);

		comment = "If TRUE, Dynamos will display overlay textures corresponding to their block level.";
		renderDynamoOverlay = ThermalExpansion.CONFIG_CLIENT.getConfiguration().getBoolean("RenderDynamoLevelOverlay", category, renderDynamoOverlay, comment);

		comment = "If TRUE, Machines will display overlay textures corresponding to their block level.";
		renderMachineOverlay = ThermalExpansion.CONFIG_CLIENT.getConfiguration().getBoolean("RenderMachineLevelOverlay", category, renderMachineOverlay, comment);

		/* SLOT OVERLAYS */
		if (slotOverlayAlt) {
			if (slotOverlayCB) {
				textureGuiCommon = PATH_SLOTS_ALT_CB;
				textureGuiSlots4 = PATH_SLOTS_4_ALT_CB;
				textureGuiSlotsCrafter = PATH_SLOTS_ALT_CB;
			} else {
				textureGuiCommon = PATH_SLOTS_ALT;
				textureGuiSlots4 = PATH_SLOTS_4_ALT;
				textureGuiSlotsCrafter = PATH_SLOTS_ALT;
			}
		} else if (slotOverlayCB) {
			textureGuiCommon = PATH_SLOTS_CB;
			textureGuiSlots4 = PATH_SLOTS_4_CB;
			textureGuiSlotsCrafter = PATH_SLOTS_CB;
		}

		/* CREATIVE TABS */
		if (TFProps.useUnifiedTabs) {
			ThermalExpansion.tabCommon = ThermalFoundation.tabCommon;
			ThermalExpansion.tabItems = ThermalFoundation.tabItems;
			ThermalExpansion.tabUtils = ThermalFoundation.tabUtils;

			TFProps.initToolTab();
			ThermalExpansion.tabTools = ThermalFoundation.tabTools;

			TFProps.initMiscTab();
			ThermalExpansion.tabFlorbs = ThermalFoundation.tabMisc;
			ThermalExpansion.tabMorbs = ThermalFoundation.tabMisc;
		} else {
			ThermalExpansion.tabCommon = new CreativeTabCore("thermalexpansion") {

				@Override
				@SideOnly (Side.CLIENT)
				public ItemStack getTabIconItem() {

					return BlockMachine.machineFurnace;
				}
			};

			if (itemTabCommon) {
				ThermalExpansion.tabItems = ThermalExpansion.tabCommon;
			} else {
				ThermalExpansion.tabItems = new CreativeTabCore("thermalexpansion", "Items") {

					@Override
					@SideOnly (Side.CLIENT)
					public ItemStack getTabIconItem() {

						return ItemMaterial.powerCoilElectrum;
					}
				};
			}
			ThermalExpansion.tabFlorbs = florbTabCommon || creativeTabHideFlorbs ? ThermalExpansion.tabCommon : new CreativeTabCore("thermalexpansion", "Florbs") {

				int iconIndex = 0;
				TimeTracker iconTracker = new TimeTracker();

				public void updateIcon() {

					World world = CoFHCore.proxy.getClientWorld();
					if (CoreUtils.isClient() && iconTracker.hasDelayPassed(world, 80)) {
						int next = MathHelper.RANDOM.nextInt(ItemFlorb.florbList.size() - 1);
						iconIndex = next >= iconIndex ? next + 1 : next;
						iconTracker.markTime(world);
					}
				}

				@Override
				@SideOnly (Side.CLIENT)
				public ItemStack getTabIconItem() {

					updateIcon();
					return ItemFlorb.florbList.get(iconIndex);
				}
			};
			ThermalExpansion.tabMorbs = morbTabCommon || creativeTabHideMorbs ? ThermalExpansion.tabCommon : new CreativeTabCore("thermalexpansion", "Morbs") {

				int iconIndex = 0;
				TimeTracker iconTracker = new TimeTracker();

				public void updateIcon() {

					World world = CoFHCore.proxy.getClientWorld();
					if (CoreUtils.isClient() && iconTracker.hasDelayPassed(world, 80)) {
						int next = MathHelper.RANDOM.nextInt(ItemMorb.morbList.size() - 1);
						iconIndex = next >= iconIndex ? next + 1 : next;
						iconTracker.markTime(world);
					}
				}

				@Override
				@SideOnly (Side.CLIENT)
				public ItemStack getTabIconItem() {

					updateIcon();
					return ItemMorb.morbList.get(iconIndex);
				}
			};
			ThermalExpansion.tabUtils = ThermalExpansion.tabItems;
			ThermalExpansion.tabTools = ThermalExpansion.tabItems;
		}
	}

	public static PacketBase getConfigSync() {

		PacketBase payload = PacketTEBase.getPacket(PacketTypes.CONFIG_SYNC);

		payload.addBool(TileMachineBase.disableAutoInput);
		payload.addBool(TileMachineBase.disableAutoOutput);
		payload.addBool(TileMachineBase.smallStorage);

		payload.addBool(TileDeviceBase.disableAutoInput);
		payload.addBool(TileDeviceBase.disableAutoOutput);

		payload.addBool(TileDynamoBase.smallStorage);

		return payload;
	}

	public static void handleConfigSync(PacketBase payload) {

		TileMachineBase.disableAutoInput = payload.getBool();
		TileMachineBase.disableAutoOutput = payload.getBool();
		TileMachineBase.smallStorage = payload.getBool();

		TileDeviceBase.disableAutoInput = payload.getBool();
		TileDeviceBase.disableAutoOutput = payload.getBool();

		TileDynamoBase.smallStorage = payload.getBool();
	}

	/* GENERAL */
	public static final int MAX_FLUID_SMALL = Fluid.BUCKET_VOLUME * 4;
	public static final int MAX_FLUID_MEDIUM = Fluid.BUCKET_VOLUME * 8;
	public static final int MAX_FLUID_LARGE = Fluid.BUCKET_VOLUME * 10;

	public static boolean creativeTabShowAllBlockLevels = false;
	public static boolean creativeTabShowCreative = false;
	public static int creativeTabLevel = 0;

	public static boolean creativeTabHideFlorbs = false;
	public static boolean creativeTabHideMorbs = false;

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

	public static final ResourceLocation PATH_SLOTS_CB = new ResourceLocation(PATH_ELEMENTS + "slots_cb.png");
	public static final ResourceLocation PATH_SLOTS_4_CB = new ResourceLocation(PATH_ELEMENTS + "slots_4_cb.png");

	public static final ResourceLocation PATH_SLOTS_ALT = new ResourceLocation(PATH_ELEMENTS + "slots_alt.png");
	public static final ResourceLocation PATH_SLOTS_4_ALT = new ResourceLocation(PATH_ELEMENTS + "slots_4_alt.png");

	public static final ResourceLocation PATH_SLOTS_ALT_CB = new ResourceLocation(PATH_ELEMENTS + "slots_alt_cb.png");
	public static final ResourceLocation PATH_SLOTS_4_ALT_CB = new ResourceLocation(PATH_ELEMENTS + "slots_4_alt_cb.png");

	public static ResourceLocation textureGuiCommon = PATH_SLOTS;
	public static ResourceLocation textureGuiSlots4 = PATH_SLOTS_4;
	public static ResourceLocation textureGuiSlotsCrafter = PATH_SLOTS;

	public static boolean animatedDynamoCoilTexture = true;
	public static boolean renderDynamoOverlay = true;
	public static boolean renderMachineOverlay = true;

	/* BLOCKSTATE PROPERTIES */
	public static final UnlistedGenericTile<TileApparatusBase> TILE_APPARATUS = new UnlistedGenericTile<>("tile_apparatus", TileApparatusBase.class);
	public static final UnlistedGenericTile<TileMachineBase> TILE_MACHINE = new UnlistedGenericTile<>("tile_machine", TileMachineBase.class);
	public static final UnlistedGenericTile<TileDynamoBase> TILE_DYNAMO = new UnlistedGenericTile<>("tile_dynamo", TileDynamoBase.class);
	public static final UnlistedGenericTile<TileTank> TILE_TANK = new UnlistedGenericTile<>("tile_tank", TileTank.class);
	public static final UnlistedGenericTile<TileCell> TILE_CELL = new UnlistedGenericTile<>("tile_cell", TileCell.class);
	public static final UnlistedGenericTile<TileCache> TILE_CACHE = new UnlistedGenericTile<>("tile_cache", TileCache.class);
	public static final UnlistedGenericTile<TileDeviceBase> TILE_DEVICE = new UnlistedGenericTile<>("tile_device", TileDeviceBase.class);
	public static final UnlistedGenericProperty<IBlockAccess> BAKERY_WORLD = new UnlistedGenericProperty<>("bakery_world", IBlockAccess.class);

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

	public static final String MACHINE_COMPACTOR_COIN = "machineCompactorCoin";
	public static final String MACHINE_COMPACTOR_GEAR = "machineCompactorGear";

	public static final String MACHINE_CRUCIBLE_LAVA = "machineCrucibleLava";
	public static final String MACHINE_CRUCIBLE_ALLOY = "machineCrucibleAlloy";

	public static final String MACHINE_REFINERY_OIL = "machineRefineryOil";
	public static final String MACHINE_REFINERY_POTION = "machineRefineryPotion";

	public static final String MACHINE_CHARGER_THROUGHPUT = "machineChargerThroughput";
	public static final String MACHINE_CHARGER_REPAIR = "machineChargerRepair";
	public static final String MACHINE_CHARGER_WIRELESS = "machineChargerWireless";

	public static final String MACHINE_CENTRIFUGE_MOBS = "machineCentrifugeMobs";

	public static final String MACHINE_CRAFTER_INPUT = "machineCrafterInput";
	public static final String MACHINE_CRAFTER_TANK = "machineCrafterTank";

	public static final String MACHINE_BREWER_REAGENT = "machineBrewerReagent";

	public static final String MACHINE_ENCHANTER_EMPOWERED = "machineEnchanterEmpowered";

	public static final String MACHINE_EXTRUDER_NO_WATER = "machineExtruderNoWater";
	public static final String MACHINE_EXTRUDER_SEDIMENTARY = "machineExtruderSedimentary";

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
