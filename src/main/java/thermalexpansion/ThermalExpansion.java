package thermalexpansion;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import thermalexpansion.block.TEBlocks;
import thermalexpansion.block.device.TileWorkbench;
import thermalexpansion.block.energycell.BlockEnergyCell;
import thermalexpansion.block.strongbox.TileStrongbox;
import thermalexpansion.core.Proxy;
import thermalexpansion.core.TEProps;
import thermalexpansion.entity.TEPlayerTracker;
import thermalexpansion.fluid.TEFluids;
import thermalexpansion.gui.CreativeTabBlocks;
import thermalexpansion.gui.CreativeTabFlorbs;
import thermalexpansion.gui.CreativeTabItems;
import thermalexpansion.gui.CreativeTabTools;
import thermalexpansion.item.TEItems;
import thermalexpansion.network.TEPacketHandler;
import thermalexpansion.network.TEPacketHandler.PacketTypes;
import thermalexpansion.plugins.TEPlugins;
import thermalexpansion.util.GenericEventHandler;
import thermalexpansion.util.IMCHandler;
import thermalexpansion.util.crafting.CrucibleManager;
import thermalexpansion.util.crafting.FurnaceManager;
import thermalexpansion.util.crafting.PulverizerManager;
import thermalexpansion.util.crafting.SawmillManager;
import thermalexpansion.util.crafting.SmelterManager;
import thermalexpansion.util.crafting.TECraftingHandler;
import thermalexpansion.util.crafting.TransposerManager;
import cofh.CoFHWorld;
import cofh.api.world.WeightedRandomBlock;
import cofh.block.world.BlockOre;
import cofh.core.CoFHProps;
import cofh.gui.GuiHandler;
import cofh.util.ConfigHandler;
import cofh.util.StringHelper;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.FMLLog;
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
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.network.NetworkRegistry;

@Mod(name = TEProps.NAME, version = TEProps.VERSION, useMetadata = false, modid = "ThermalExpansion", dependencies = "required-after:Forge@[" + CoFHProps.FORGE_REQ + ",);required-after:CoFHCore@[" + CoFHProps.VERSION + ",);required-after:ForgeMultipart;before:IC2;before:Metallurgy")
public class ThermalExpansion {

	@SidedProxy(clientSide = "thermalexpansion.core.ProxyClient", serverSide = "thermalexpansion.core.Proxy")
	public static Proxy proxy;

	@Instance("ThermalExpansion")
	public static ThermalExpansion instance;
	public static final ConfigHandler config = new ConfigHandler(TEProps.VERSION);
	public static final Logger log = LogManager.getLogger(TEProps.modID);

	/* INIT SEQUENCE */
	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {

		GenericEventHandler.initialize();
		TECraftingHandler.initialize();
		TEPlayerTracker.initialize();

		version.checkForNewVersion();

		boolean optionColorBlind = false;
		boolean optionDrawBorders = true;
		boolean optionEnableAchievements = true;

		int tweakLavaRF = TEProps.lavaRF;

		config.setConfiguration(new Configuration(new File(event.getModConfigurationDirectory(), "cofh/ThermalExpansion.cfg")));

		cleanConfig(true);

		TEItems.preInit();
		TEBlocks.preInit();
		TEFluids.preInit();
		TEPlugins.preInit();

		String category = "general";
		String version = config.get(category, "Version", TEProps.VERSION);
		String comment = "";

		TEProps.enableUpdateNotice = config.get(category, "EnableUpdateNotifications", TEProps.enableUpdateNotice);
		TEProps.enableDismantleLogging = config.get(category, "EnableDismantleLogging", TEProps.enableDismantleLogging);
		TEProps.enableDebugOutput = config.get(category, "EnableDebugOutput", TEProps.enableDebugOutput);
		// TEProps.enableAchievements = config.get(category, "EnableAchievements", TEProps.enableAchievements);
		optionColorBlind = config.get(category, "ColorBlindTextures", false);
		optionDrawBorders = config.get(category, "DrawGUISlotBorders", true);

		category = "gui.hud";
		TEProps.enableFluidModule = config.get(category, "EnableFluidModule", TEProps.enableFluidModule);
		TEProps.enableStuffedItemModule = config.get(category, "EnableStuffedItemModule", TEProps.enableStuffedItemModule);

		category = "tweak";
		tweakLavaRF = config.get(category, "LavaRFValue", tweakLavaRF);

		comment = "Setting this to anything but 0 (min 10) will result in items being lost for players! Everytime the conduits tick if there is a stuffed conduit it will be limited to x items. All items past x will be erased! Please use with caution.";
		GridTickHandler.maxItemsInDucts = config.get(category, "MaxItemsInStuffedDucts", GridTickHandler.maxItemsInDucts, comment);
		comment = "Set this to 0 to disable getting dye from wools. Acceptable Ranges: 0-100. This is the percentage chance that you will get a dye as a secondary output on the pulverizer.";
		PulverizerManager.secondaryWoolPercentages = config.get(category, "WoolColorChances", PulverizerManager.secondaryWoolPercentages, comment);

		category = "holiday";
		comment = "Set this to true to disable Christmas cheer. Scrooge. :(";
		TEProps.holidayChristmas = !config.get(category, "HoHoNo", false, comment);

		/* Graphics Config */
		if (optionColorBlind) {
			TEProps.textureGuiCommon = TEProps.PATH_COMMON_CB;
			TEProps.textureSelection = TEProps.TEXTURE_CB;
			BlockEnergyCell.textureSelection = BlockEnergyCell.TEXTURE_CB;
		}
		TEProps.enableGuiBorders = optionDrawBorders;

		/* Tweaks */
		if (tweakLavaRF >= 10000 && tweakLavaRF < TEProps.LAVA_MAX_RF) {
			TEProps.lavaRF = tweakLavaRF;
		} else {
			log.log(Level.INFO, "'LavaRFValue' config value is out of acceptable range. Using default.");
		}
		if (GridTickHandler.maxItemsInDucts < 10 && GridTickHandler.maxItemsInDucts != 0) {
			GridTickHandler.maxItemsInDucts = 0;
			log.log(Level.INFO, "'MaxItemsInStuffedDucts' config value is out of acceptable range. Using default. Must be 0 or greater then 10.");
		}
		if (PulverizerManager.secondaryWoolPercentages < 0 || PulverizerManager.secondaryWoolPercentages > 100) {
			PulverizerManager.secondaryWoolPercentages = 25;
			log.log(Level.INFO, "'WoolColorChances' config value is out of acceptable range. Using default. Must be 0-100.");
		}
	}

	@EventHandler
	public void initialize(FMLInitializationEvent event) {

		config.initialize();

		TEItems.initialize();
		TEBlocks.initialize();
		TEFluids.initialize();
		TEPlugins.initialize();
		TEPartFactory.initialize();

		if (TEProps.enableAchievements) {
			// TEAchievements.initialize();
		}

		/* Init World Gen */
		loadWorldGeneration();

		/* Register Handlers */
		NetworkRegistry.instance().registerGuiHandler(instance, guiHandler);
		TEPacketHandler.initialize();
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
		TEFluids.postInit();
		TEPlugins.postInit();

		proxy.registerEntities();
		proxy.registerRenderInformation();
		proxy.registerTickHandlers();
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

		cleanConfig(false);
		config.cleanUp(false, true);

		log.log(Level.INFO, "Load Complete.");
	}

	@EventHandler
	public void serverStarting(FMLServerStartingEvent event) {

		TEFluids.registerDispenserHandlers();
	}

	@EventHandler
	public void handleIMC(IMCEvent theIMC) {

		IMCHandler.instance.handleIMC(theIMC);
	}

	public void handleConfigSync(Payload payload) {

		TileWorkbench.enableSecurity = payload.getBool();
		TileStrongbox.enableSecurity = payload.getBool();

		log.info(StringHelper.localize("message.cofh.receiveConfig"));
	}

	public Payload getConfigSync(int packetID) {

		Payload myPayload = Payload.getPayload(packetID);
		myPayload.addByte(PacketTypes.CONFIG_SYNC.ordinal());

		myPayload.addBool(TileWorkbench.enableSecurity);
		myPayload.addBool(TileStrongbox.enableSecurity);

		return myPayload;
	}

	// Called when the client is d/ced from the server.
	public void resetClientConfigs() {

		TileWorkbench.configure();
		TileStrongbox.configure();

		log.info(StringHelper.localize("message.cofh.restoreConfig"));
	}

	/* LOADING FUNCTIONS */
	void loadWorldGeneration() {

		String category = "world.thermalexpansion";

		List<WeightedRandomBlock>[] oreList = new List[TEProps.Ores.values().length];

		for (int i = 0; i < oreList.length; i++) {
			oreList[i] = new ArrayList<WeightedRandomBlock>();
		}
		oreList[TEProps.Ores.COPPER.ordinal()].add(new WeightedRandomBlock(BlockOre.oreCopper));
		oreList[TEProps.Ores.TIN.ordinal()].add(new WeightedRandomBlock(BlockOre.oreTin));
		oreList[TEProps.Ores.SILVER.ordinal()].add(new WeightedRandomBlock(BlockOre.oreSilver, 90));
		oreList[TEProps.Ores.LEAD.ordinal()].add(new WeightedRandomBlock(BlockOre.oreLead, 80));
		oreList[TEProps.Ores.NICKEL.ordinal()].add(new WeightedRandomBlock(BlockOre.oreNickel));

		if (BlockOre.enable[TEProps.Ores.LEAD.ordinal()]) {
			oreList[TEProps.Ores.SILVER.ordinal()].add(new WeightedRandomBlock(BlockOre.oreLead, 10));
		}
		if (BlockOre.enable[TEProps.Ores.SILVER.ordinal()]) {
			oreList[TEProps.Ores.LEAD.ordinal()].add(new WeightedRandomBlock(BlockOre.oreSilver, 20));
		}
		for (int i = 0; i < oreList.length; i++) {
			CoFHWorld.addFeature(category, oreList[i], BlockOre.NAMES[i], TEProps.oreClusterSize[i], TEProps.oreNumCluster[i], TEProps.oreMinY[i], TEProps.oreMaxY[i], CoFHWorld.ORE_UNIFORM, true, BlockOre.enable[i]);
		}
	}

	void cleanConfig(boolean preInit) {

		if (preInit) {

			String category = "tweak";
			String newCategory = "tweak.crafting";

			config.renameProperty(category, "Pulverizer.Sandstone", newCategory, "Pulverizer.Sandstone", true);
			config.renameProperty(category, "Pulverizer.Netherrack", newCategory, "Pulverizer.Netherrack", true);
			config.renameProperty(category, "Pulverizer.Cloth", newCategory, "Pulverizer.Cloth", true);
			config.renameProperty(category, "Pulverizer.Reed", newCategory, "Pulverizer.Reed", true);
			config.renameProperty(category, "Pulverizer.Bone", newCategory, "Pulverizer.Bone", true);
			config.renameProperty(category, "Pulverizer.BlazeRod", newCategory, "Pulverizer.BlazeRod", true);
			config.renameProperty(category, "Pulverizer.Cinnabar.Chance", newCategory, "Pulverizer.Cinnabar.Chance", true);

			config.renameProperty(category, "Smelter.Bronze.Quantity", newCategory, "Smelter.Bronze.Quantity", true);

			config.renameProperty(category, "RockGen.Cobblestone.Lava", newCategory, "RockGen.Cobblestone.Lava", true);
			config.renameProperty(category, "RockGen.Stone.Lava", newCategory, "RockGen.Stone.Lava", true);
			config.renameProperty(category, "RockGen.Obsidian.Lava", newCategory, "RockGen.Obsidian.Lava", true);

			config.renameProperty(category, "RockGen.Cobblestone.Water", newCategory, "RockGen.Cobblestone.Water", true);
			config.renameProperty(category, "RockGen.Stone.Water", newCategory, "RockGen.Stone.Water", true);
			config.renameProperty(category, "RockGen.Obsidian.Water", newCategory, "RockGen.Obsidian.Water", true);

			config.renameProperty(category, "RockGen.Cobblestone.Time", newCategory, "RockGen.Cobblestone.Time", true);
			config.renameProperty(category, "RockGen.Stone.Time", newCategory, "RockGen.Stone.Time", true);
			config.renameProperty(category, "RockGen.Obsidian.Time", newCategory, "RockGen.Obsidian.Time", true);

			config.removeProperty(category, "Pulverizer.IngotsToDust");
			config.removeProperty(newCategory, "Pulverizer.IngotsToDust");
			config.removeProperty(newCategory, "Smelter.Bronze.Quantity");

			category = "tweak.recipe";

			config.renameProperty(category, "MachineFrame.UseSteel", category, "MachineFrame.RequireSteel", true);

			category = "general";

			config.removeProperty(category, "EnableStuffedItemHudModule");
		}
	}

	public static final GuiHandler guiHandler = new GuiHandler();
	public static final VersionHandler version = new VersionHandler(TEProps.NAME, TEProps.VERSION, TEProps.RELEASE_URL, log);

	public static final CreativeTabs tabBlocks = new CreativeTabBlocks();
	public static final CreativeTabs tabItems = new CreativeTabItems();
	public static final CreativeTabs tabTools = new CreativeTabTools();
	public static final CreativeTabs tabFlorbs = new CreativeTabFlorbs();

	static {
		log.setParent(FMLLog.getLogger());
	}

	public static final Material CLOTH_FIREPROOF = new Material(MapColor.clothColor);
	public static final Material WOOD_FIREPROOF = new Material(MapColor.woodColor);

}
