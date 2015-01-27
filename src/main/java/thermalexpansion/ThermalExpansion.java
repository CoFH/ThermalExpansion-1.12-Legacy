package thermalexpansion;

import cofh.CoFHCore;
import cofh.core.CoFHProps;
import cofh.core.network.PacketCoFHBase;
import cofh.core.util.ConfigHandler;
import cofh.core.util.CoreUtils;
import cofh.lib.util.helpers.StringHelper;
import cofh.mod.BaseMod;
import cofh.mod.updater.UpdateManager;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.CustomProperty;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLInterModComms;
import cpw.mods.fml.common.event.FMLInterModComms.IMCEvent;
import cpw.mods.fml.common.event.FMLLoadCompleteEvent;
import cpw.mods.fml.common.event.FMLMissingMappingsEvent;
import cpw.mods.fml.common.event.FMLMissingMappingsEvent.MissingMapping;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;

import java.io.File;
import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.oredict.RecipeSorter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import thermalexpansion.block.TEBlocks;
import thermalexpansion.block.cell.BlockCell;
import thermalexpansion.block.cell.TileCell;
import thermalexpansion.block.device.TileActivator;
import thermalexpansion.block.device.TileBreaker;
import thermalexpansion.block.device.TileNullifier;
import thermalexpansion.block.device.TileWorkbench;
import thermalexpansion.block.dynamo.TileDynamoBase;
import thermalexpansion.block.machine.TileMachineBase;
import thermalexpansion.block.strongbox.TileStrongbox;
import thermalexpansion.core.Proxy;
import thermalexpansion.core.TEProps;
import thermalexpansion.gui.CreativeTabBlocks;
import thermalexpansion.gui.CreativeTabFlorbs;
import thermalexpansion.gui.CreativeTabItems;
import thermalexpansion.gui.CreativeTabTools;
import thermalexpansion.gui.GuiHandler;
import thermalexpansion.item.ItemSatchel;
import thermalexpansion.item.TEItems;
import thermalexpansion.network.PacketTEBase;
import thermalexpansion.network.PacketTEBase.PacketTypes;
import thermalexpansion.plugins.TEPlugins;
import thermalexpansion.util.FMLEventHandler;
import thermalexpansion.util.FuelHandler;
import thermalexpansion.util.IMCHandler;
import thermalexpansion.util.crafting.ChargerManager;
import thermalexpansion.util.crafting.CrucibleManager;
import thermalexpansion.util.crafting.ExtruderManager;
import thermalexpansion.util.crafting.FurnaceManager;
import thermalexpansion.util.crafting.PrecipitatorManager;
import thermalexpansion.util.crafting.PulverizerManager;
import thermalexpansion.util.crafting.RecipeMachine;
import thermalexpansion.util.crafting.RecipeMachineUpgrade;
import thermalexpansion.util.crafting.SawmillManager;
import thermalexpansion.util.crafting.SmelterManager;
import thermalexpansion.util.crafting.TECraftingHandler;
import thermalexpansion.util.crafting.TransposerManager;
import thermalfoundation.ThermalFoundation;

@Mod(modid = ThermalExpansion.modId, name = ThermalExpansion.modName, version = ThermalExpansion.version, dependencies = ThermalExpansion.dependencies,
		guiFactory = ThermalExpansion.modGuiFactory, customProperties = @CustomProperty(k = "cofhversion", v = "true"))
public class ThermalExpansion extends BaseMod {

	public static final String modId = "ThermalExpansion";
	public static final String modName = "Thermal Expansion";
	public static final String version = "1.7.10R4.0.0B9";
	public static final String dependencies = "required-after:ThermalFoundation@[" + ThermalFoundation.version + ",)";
	public static final String releaseURL = "https://raw.github.com/CoFH/VERSION/master/ThermalExpansion";
	public static final String modGuiFactory = "thermalexpansion.gui.GuiConfigTEFactory";

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

	public static File worldGen;
	public static final String worldGenInternal = "assets/thermalexpansion/world/ThermalExpansion-Ores.json";

	/* INIT SEQUENCE */
	public ThermalExpansion() {

		super(log);
	}

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {

		// loadLang();

		UpdateManager.registerUpdater(new UpdateManager(this, releaseURL));
		config.setConfiguration(new Configuration(new File(event.getModConfigurationDirectory(), "cofh/ThermalExpansion.cfg")));

		FMLEventHandler.initialize();
		TECraftingHandler.initialize();

		RecipeSorter.register("thermalexpansion:machine", RecipeMachine.class, RecipeSorter.Category.SHAPED, "before:cofh:upgrade");
		RecipeSorter.register("thermalexpansion:machineUpgrade", RecipeMachineUpgrade.class, RecipeSorter.Category.SHAPED, "before:cofh:upgrade");

		cleanConfig(true);

		TEItems.preInit();
		TEBlocks.preInit();
		TEPlugins.preInit();

		configOptions();
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
		MinecraftForge.EVENT_BUS.register(proxy);
		PacketTEBase.initialize();
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {

		FurnaceManager.addDefaultRecipes();
		PulverizerManager.addDefaultRecipes();
		SawmillManager.addDefaultRecipes();
		SmelterManager.addDefaultRecipes();
		CrucibleManager.addDefaultRecipes();
		TransposerManager.addDefaultRecipes();
		PrecipitatorManager.addDefaultRecipes();
		ExtruderManager.addDefaultRecipes();
		ChargerManager.addDefaultRecipes();

		TEItems.postInit();
		TEBlocks.postInit();
		TEPlugins.postInit();

		proxy.registerEntities();
		proxy.registerRenderInformation();
	}

	@EventHandler
	public void loadComplete(FMLLoadCompleteEvent event) {

		IMCHandler.instance.handleIMC(FMLInterModComms.fetchRuntimeMessages(this));

		TECraftingHandler.loadRecipes();
		FurnaceManager.loadRecipes();
		PulverizerManager.loadRecipes();
		SawmillManager.loadRecipes();
		SmelterManager.loadRecipes();
		CrucibleManager.loadRecipes();
		TransposerManager.loadRecipes();
		PrecipitatorManager.loadRecipes();
		ExtruderManager.loadRecipes();
		ChargerManager.loadRecipes();

		FuelHandler.parseFuels();

		TEPlugins.loadComplete();

		cleanConfig(false);
		config.cleanUp(false, true);

		log.info("Load Complete.");
	}

	@EventHandler
	public void handleIMC(IMCEvent theIMC) {

		IMCHandler.instance.handleIMC(theIMC.getMessages());
	}

	public void handleConfigSync(PacketCoFHBase payload) {

		FMLEventHandler.instance.handleIdMappingEvent(null);

		TileCell.enableSecurity = payload.getBool();
		TileWorkbench.enableSecurity = payload.getBool();
		TileActivator.enableSecurity = payload.getBool();
		TileBreaker.enableSecurity = payload.getBool();
		TileNullifier.enableSecurity = payload.getBool();
		TileDynamoBase.enableSecurity = payload.getBool();
		for (int i = 0; i < TileMachineBase.enableSecurity.length; i++) {
			TileMachineBase.enableSecurity[i] = payload.getBool();
		}
		TileStrongbox.enableSecurity = payload.getBool();
		ItemSatchel.enableSecurity = payload.getBool();

		log.info("Receiving Server Configuration...");
	}

	public PacketCoFHBase getConfigSync() {

		PacketCoFHBase payload = PacketTEBase.getPacket(PacketTypes.CONFIG_SYNC);

		payload.addBool(TileCell.enableSecurity);

		payload.addBool(TileWorkbench.enableSecurity);
		payload.addBool(TileActivator.enableSecurity);
		payload.addBool(TileBreaker.enableSecurity);
		payload.addBool(TileNullifier.enableSecurity);

		payload.addBool(TileDynamoBase.enableSecurity);

		for (int i = 0; i < TileMachineBase.enableSecurity.length; i++) {
			payload.addBool(TileMachineBase.enableSecurity[i]);
		}
		payload.addBool(TileStrongbox.enableSecurity);

		payload.addBool(ItemSatchel.enableSecurity);

		return payload;
	}

	// Called when the client disconnects from the server.
	public void resetClientConfigs() {

		TileCell.configure();
		TileWorkbench.configure();
		TileActivator.configure();
		TileBreaker.configure();
		TileNullifier.configure();
		TileDynamoBase.configure();
		TileMachineBase.configure();
		TileStrongbox.configure();
		ItemSatchel.configure();

		FMLEventHandler.instance.handleIdMappingEvent(null);

		log.info(StringHelper.localize("Restoring Client Configuration..."));
	}

	/* LOADING FUNCTIONS */
	void loadWorldGeneration() {

		if (!config
				.get("world", "GenerateDefaultFiles", true,
						"If enabled, Thermal Expansion will create default world generation files - if it cannot find existing ones. Only disable this if you know what you are doing.")) {
			return;
		}
		worldGen = new File(CoFHProps.configDir, "/cofh/world/ThermalExpansion-Ores.json");

		if (!worldGen.exists()) {
			try {
				worldGen.createNewFile();
				CoreUtils.copyFileUsingStream(worldGenInternal, worldGen);
			} catch (Throwable t) {
				t.printStackTrace();
			}
		}
	}

	void configOptions() {

		boolean optionColorBlind = false;
		boolean optionDrawBorders = true;

		String category = "general";
		String comment = null;

		TEProps.enableDebugOutput = config.get(category, "EnableDebugOutput", TEProps.enableDebugOutput);
		// TEProps.enableAchievements = config.get(category, "EnableAchievements", TEProps.enableAchievements);
		optionColorBlind = CoFHCore.configClient.get(category, "ColorBlindTextures", false);
		optionDrawBorders = CoFHCore.configClient.get(category, "DrawGUISlotBorders", true);

		category = "holiday";
		comment = "Set this to true to disable Christmas cheer. Scrooge. :(";
		TEProps.holidayChristmas = !config.get(category, "HoHoNo", false, comment);

		/* Graphics Config */
		if (optionColorBlind) {
			TEProps.textureGuiCommon = TEProps.PATH_COMMON_CB;
			TEProps.textureGuiAssembler = TEProps.PATH_ASSEMBLER_CB;
			TEProps.textureSelection = TEProps.TEXTURE_CB;
			BlockCell.textureSelection = BlockCell.TEXTURE_CB;
		}
		TEProps.enableGuiBorders = optionDrawBorders;
	}

	void cleanConfig(boolean preInit) {

		if (preInit) {

		}
		String prefix = "config.thermalexpansion.";
		String[] categoryNames = config.getCategoryNames().toArray(new String[config.getCategoryNames().size()]);
		for (int i = 0; i < categoryNames.length; i++) {
			config.getCategory(categoryNames[i]).setLanguageKey(prefix + categoryNames[i]).setRequiresMcRestart(true);
		}
	}

	@EventHandler
	@SuppressWarnings("deprecation")
	public void missingMappings(FMLMissingMappingsEvent e) {

		List<MissingMapping> list = e.get();
		if (list.size() > 0) {
			for (MissingMapping mapping : list) {

				String name = mapping.name;
				if (name.indexOf(':') >= 0) {
					name = name.substring(name.indexOf(':') + 1);
				}
				switch (mapping.type) {
				case ITEM:
					if (name.indexOf("tool.") != 0 && name.indexOf("armor.") != 0) {
						break;
					}
					Item item = GameRegistry.findItem("ThermalFoundation", name);
					if (item != null) {
						mapping.remap(item);
					} else {
						mapping.warn();
					}
				default:
					break;
				}
			}
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
