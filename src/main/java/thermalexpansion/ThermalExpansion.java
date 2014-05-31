package thermalexpansion;

import cofh.gui.GuiHandler;
import cofh.mod.BaseMod;
import cofh.network.CoFHPacket;
import cofh.updater.UpdateManager;
import cofh.util.ConfigHandler;
import cofh.util.StringHelper;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.FMLModContainer;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLInterModComms.IMCEvent;
import cpw.mods.fml.common.event.FMLLoadCompleteEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;

import java.io.File;
import java.lang.reflect.Field;

import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import thermalexpansion.block.TEBlocks;
import thermalexpansion.block.cell.BlockCell;
import thermalexpansion.block.device.TileWorkbench;
import thermalexpansion.block.strongbox.TileStrongbox;
import thermalexpansion.core.Proxy;
import thermalexpansion.core.TEProps;
import thermalexpansion.entity.TEPlayerTracker;
import thermalexpansion.gui.CreativeTabBlocks;
import thermalexpansion.gui.CreativeTabFlorbs;
import thermalexpansion.gui.CreativeTabItems;
import thermalexpansion.gui.CreativeTabTools;
import thermalexpansion.item.TEItems;
import thermalexpansion.network.GenericTEPacket;
import thermalexpansion.network.GenericTEPacket.PacketTypes;
import thermalexpansion.plugins.TEPlugins;
import thermalexpansion.util.GenericEventHandler;
import thermalexpansion.util.IMCHandler;
import thermalexpansion.util.crafting.CrucibleManager;
import thermalexpansion.util.crafting.ExtruderManager;
import thermalexpansion.util.crafting.FurnaceManager;
import thermalexpansion.util.crafting.PrecipitatorManager;
import thermalexpansion.util.crafting.PulverizerManager;
import thermalexpansion.util.crafting.SawmillManager;
import thermalexpansion.util.crafting.SmelterManager;
import thermalexpansion.util.crafting.TECraftingHandler;
import thermalexpansion.util.crafting.TransposerManager;
import thermalfoundation.ThermalFoundation;

@Mod(modid = ThermalExpansion.modId, name = ThermalExpansion.modName, version = ThermalExpansion.version, dependencies = ThermalExpansion.dependencies)
public class ThermalExpansion extends BaseMod {

	public static final String modId = "ThermalExpansion";
	public static final String modName = "Thermal Expansion";
	public static final String version = "1.7.2R3.1.0B1";
	public static final String dependencies = "required-after:ThermalFoundation@[" + ThermalFoundation.version + ",)";
	public static final String releaseURL = "http://teamcofh.com/thermalexpansion/version/version.txt";

	@Instance(modId)
	public static ThermalExpansion instance;

	@SidedProxy(clientSide = "thermalexpansion.core.ProxyClient", serverSide = "thermalexpansion.core.Proxy")
	public static Proxy proxy;

	public static final Logger log = LogManager.getLogger(modId);

	public static final ConfigHandler config = new ConfigHandler(version);
	public static final GuiHandler guiHandler = new GuiHandler();

	public static final CreativeTabs tabBlocks = new CreativeTabBlocks();
	public static final CreativeTabs tabItems = new CreativeTabItems();
	public static final CreativeTabs tabTools = new CreativeTabTools();
	public static final CreativeTabs tabFlorbs = new CreativeTabFlorbs();

	public static final Material CLOTH_FIREPROOF = new Material(MapColor.clothColor);
	public static final Material WOOD_FIREPROOF = new Material(MapColor.woodColor);

	/* INIT SEQUENCE */
	public ThermalExpansion() {

		super(log);
	}

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {

		UpdateManager.registerUpdater(new UpdateManager(this, releaseURL));

		GenericEventHandler.initialize();
		TECraftingHandler.initialize();
		TEPlayerTracker.initialize();

		boolean optionColorBlind = false;
		boolean optionDrawBorders = true;
		boolean optionEnableAchievements = true;

		int tweakLavaRF = TEProps.lavaRF;

		config.setConfiguration(new Configuration(new File(event.getModConfigurationDirectory(), "cofh/ThermalExpansion.cfg")));

		cleanConfig(true);

		TEItems.preInit();
		TEBlocks.preInit();
		TEPlugins.preInit();

		String category = "general";
		String comment = null;

		TEProps.enableUpdateNotice = config.get(category, "EnableUpdateNotifications", TEProps.enableUpdateNotice);
		TEProps.enableDismantleLogging = config.get(category, "EnableDismantleLogging", TEProps.enableDismantleLogging);
		TEProps.enableDebugOutput = config.get(category, "EnableDebugOutput", TEProps.enableDebugOutput);
		// TEProps.enableAchievements = config.get(category, "EnableAchievements", TEProps.enableAchievements);
		optionColorBlind = config.get(category, "ColorBlindTextures", false);
		optionDrawBorders = config.get(category, "DrawGUISlotBorders", true);

		category = "tweak";
		tweakLavaRF = config.get(category, "LavaRFValue", tweakLavaRF);

		comment = "Set this to 0 to disable getting dye from wools. Acceptable Ranges: 0-100. This is the percentage chance that you will get a dye as a secondary output on the pulverizer.";
		PulverizerManager.secondaryWoolPercentages = config.get(category, "WoolColorChances", PulverizerManager.secondaryWoolPercentages, comment);

		category = "holiday";
		comment = "Set this to true to disable Christmas cheer. Scrooge. :(";
		TEProps.holidayChristmas = !config.get(category, "HoHoNo", false, comment);

		/* Graphics Config */
		if (optionColorBlind) {
			TEProps.textureGuiCommon = TEProps.PATH_COMMON_CB;
			TEProps.textureSelection = TEProps.TEXTURE_CB;
			BlockCell.textureSelection = BlockCell.TEXTURE_CB;
		}
		TEProps.enableGuiBorders = optionDrawBorders;

		/* Tweaks */
		if (tweakLavaRF >= 10000 && tweakLavaRF < TEProps.LAVA_MAX_RF) {
			TEProps.lavaRF = tweakLavaRF;
		} else {
			log.info("'LavaRFValue' config value is out of acceptable range. Using default.");
		}
		if (PulverizerManager.secondaryWoolPercentages < 0 || PulverizerManager.secondaryWoolPercentages > 100) {
			PulverizerManager.secondaryWoolPercentages = 25;
			log.info("'WoolColorChances' config value is out of acceptable range. Using default. Must be 0-100.");
		}

		TEPlayerTracker.initialize();
	}

	@EventHandler
	public void initialize(FMLInitializationEvent event) {

		TEItems.initialize();
		TEBlocks.initialize();
		TEPlugins.initialize();

		if (TEProps.enableAchievements) {
			// TEAchievements.initialize();
		}

		/* Init World Gen */
		loadWorldGeneration();

		/* Register Handlers */
		NetworkRegistry.INSTANCE.registerGuiHandler(instance, guiHandler);
		GenericTEPacket.initialize();
		MinecraftForge.EVENT_BUS.register(proxy);

		try {
			Field eBus = FMLModContainer.class.getDeclaredField("eventBus");
			eBus.setAccessible(true);
			EventBus FMLbus = (EventBus) eBus.get(FMLCommonHandler.instance().findContainerFor(this));
			FMLbus.register(this);
		} catch (Throwable t) {
			if (TEProps.enableDebugOutput) {
				t.printStackTrace();
			}
		}
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {

		TEItems.postInit();
		TEBlocks.postInit();
		TEPlugins.postInit();

		proxy.registerEntities();
		proxy.registerRenderInformation();
	}

	@Subscribe
	public void loadComplete(FMLLoadCompleteEvent event) {

		TECraftingHandler.loadRecipes();
		FurnaceManager.loadRecipes();
		PulverizerManager.loadRecipes();
		SawmillManager.loadRecipes();
		SmelterManager.loadRecipes();
		CrucibleManager.loadRecipes();
		TransposerManager.loadRecipes();
		PrecipitatorManager.loadRecipes();
		ExtruderManager.loadRecipes();

		cleanConfig(false);
		config.cleanUp(false, true);

		log.info("Load Complete.");
	}

	@EventHandler
	public void handleIMC(IMCEvent theIMC) {

		IMCHandler.instance.handleIMC(theIMC);
	}

	public void handleConfigSync(CoFHPacket payload) {

		TileWorkbench.enableSecurity = payload.getBool();
		TileStrongbox.enableSecurity = payload.getBool();

		log.info(StringHelper.localize("message.cofh.receiveConfig"));
	}

	public CoFHPacket getConfigSync() {

		CoFHPacket payload = GenericTEPacket.getPacket(PacketTypes.CONFIG_SYNC);

		payload.addBool(TileWorkbench.enableSecurity);
		payload.addBool(TileStrongbox.enableSecurity);

		return payload;
	}

	// Called when the client is d/ced from the server.
	public void resetClientConfigs() {

		TileWorkbench.configure();
		TileStrongbox.configure();

		log.info(StringHelper.localize("message.cofh.restoreConfig"));
	}

	/* LOADING FUNCTIONS */
	void loadWorldGeneration() {

		// String category = "world.thermalexpansion";
		//
		// List<WeightedRandomBlock>[] oreList = new List[TEProps.Ores.values().length];
		//
		// for (int i = 0; i < oreList.length; i++) {
		// oreList[i] = new ArrayList<WeightedRandomBlock>();
		// }
		// oreList[TEProps.Ores.COPPER.ordinal()].add(new WeightedRandomBlock(BlockOre.oreCopper));
		// oreList[TEProps.Ores.TIN.ordinal()].add(new WeightedRandomBlock(BlockOre.oreTin));
		// oreList[TEProps.Ores.SILVER.ordinal()].add(new WeightedRandomBlock(BlockOre.oreSilver, 90));
		// oreList[TEProps.Ores.LEAD.ordinal()].add(new WeightedRandomBlock(BlockOre.oreLead, 80));
		// oreList[TEProps.Ores.NICKEL.ordinal()].add(new WeightedRandomBlock(BlockOre.oreNickel));
		//
		// if (BlockOre.enable[TEProps.Ores.LEAD.ordinal()]) {
		// oreList[TEProps.Ores.SILVER.ordinal()].add(new WeightedRandomBlock(BlockOre.oreLead, 10));
		// }
		// if (BlockOre.enable[TEProps.Ores.SILVER.ordinal()]) {
		// oreList[TEProps.Ores.LEAD.ordinal()].add(new WeightedRandomBlock(BlockOre.oreSilver, 20));
		// }
		// for (int i = 0; i < oreList.length; i++) {
		// ThermalFoundation.addFeature(category, oreList[i], BlockOre.NAMES[i], TEProps.oreClusterSize[i], TEProps.oreNumCluster[i], TEProps.oreMinY[i],
		// TEProps.oreMaxY[i], ThermalFoundation.ORE_UNIFORM, true, BlockOre.enable[i]);
		// }
	}

	void cleanConfig(boolean preInit) {

		if (preInit) {

		}
	}

	/* BaseMod */
	@Override
	public String getModId() {

		return modId;
	}

	@Override
	public String getModName() {

		return modName;
	}

	@Override
	public String getModVersion() {

		return version;
	}

}
