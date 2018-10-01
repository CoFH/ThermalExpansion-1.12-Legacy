package cofh.thermalexpansion;

import codechicken.lib.CodeChickenLib;
import cofh.CoFHCore;
import cofh.core.init.CoreProps;
import cofh.core.util.ConfigHandler;
import cofh.thermalexpansion.gui.GuiHandler;
import cofh.thermalexpansion.init.*;
import cofh.thermalexpansion.item.ItemFlorb;
import cofh.thermalexpansion.item.ItemMorb;
import cofh.thermalexpansion.network.PacketTEBase;
import cofh.thermalexpansion.proxy.Proxy;
import cofh.thermalexpansion.util.IMCHandler;
import cofh.thermalexpansion.util.managers.device.*;
import cofh.thermalexpansion.util.managers.dynamo.*;
import cofh.thermalexpansion.util.managers.machine.*;
import cofh.thermalexpansion.util.parsers.ContentParser;
import cofh.thermalfoundation.ThermalFoundation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.*;
import net.minecraftforge.fml.common.event.FMLInterModComms.IMCEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;

@Mod (modid = ThermalExpansion.MOD_ID, name = ThermalExpansion.MOD_NAME, version = ThermalExpansion.VERSION, dependencies = ThermalExpansion.DEPENDENCIES, updateJSON = ThermalExpansion.UPDATE_URL, certificateFingerprint = "8a6abf2cb9e141b866580d369ba6548732eff25f")
public class ThermalExpansion {

	public static final String MOD_ID = "thermalexpansion";
	public static final String MOD_NAME = "Thermal Expansion";

	public static final String VERSION = "5.5.0";
	public static final String VERSION_MAX = "5.6.0";
	public static final String VERSION_GROUP = "required-after:" + MOD_ID + "@[" + VERSION + "," + VERSION_MAX + ");";
	public static final String UPDATE_URL = "https://raw.github.com/cofh/version/master/" + MOD_ID + "_update.json";

	public static final String DEPENDENCIES = CoFHCore.VERSION_GROUP + ThermalFoundation.VERSION_GROUP + CodeChickenLib.MOD_VERSION_DEP + "before:enderio";
	public static final String MOD_GUI_FACTORY = "cofh.thermalexpansion.gui.GuiConfigTEFactory";

	@Instance (MOD_ID)
	public static ThermalExpansion instance;

	@SidedProxy (clientSide = "cofh.thermalexpansion.proxy.ProxyClient", serverSide = "cofh.thermalexpansion.proxy.Proxy")
	public static Proxy proxy;

	public static final Logger LOG = LogManager.getLogger(MOD_ID);
	public static final ConfigHandler CONFIG = new ConfigHandler(VERSION);
	public static final ConfigHandler CONFIG_CLIENT = new ConfigHandler(VERSION);
	public static final GuiHandler GUI_HANDLER = new GuiHandler();

	public static CreativeTabs tabCommon;       // Blocks and general stuff.
	public static CreativeTabs tabItems;        // Non-usable items.
	public static CreativeTabs tabUtils;        // Usable items, non-tiered.
	public static CreativeTabs tabTools;        // Usable items, tiered.                (Unified Tabs Only)

	public static CreativeTabs tabFlorbs;
	public static CreativeTabs tabMorbs;

	public ThermalExpansion() {

		super();
	}

	/* INIT */
	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {

		CONFIG.setConfiguration(new Configuration(new File(CoreProps.configDir, "/cofh/" + MOD_ID + "/common.cfg"), true));
		CONFIG_CLIENT.setConfiguration(new Configuration(new File(CoreProps.configDir, "/cofh/" + MOD_ID + "/client.cfg"), true));

		TEProps.preInit();

		TEBlocks.preInit();
		TEItems.preInit();
		TESounds.preInit();
		TEPlugins.preInit();

		/* Register Handlers */
		registerHandlers();

		proxy.preInit(event);
	}

	@EventHandler
	public void initialize(FMLInitializationEvent event) {

		ContentParser.initialize();

		proxy.initialize(event);
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {

		ContentParser.parseFiles();

		ItemFlorb.parseFlorbs();
		ItemMorb.parseMorbs();

		TEPlugins.initialize();

		initManagers();

		ContentParser.postProcess();

		proxy.postInit(event);
	}

	@EventHandler
	public void loadComplete(FMLLoadCompleteEvent event) {

		IMCHandler.INSTANCE.handleIMC(FMLInterModComms.fetchRuntimeMessages(this));

		CONFIG.cleanUp(false, true);
		CONFIG_CLIENT.cleanUp(false, true);

		LOG.info(MOD_NAME + ": Load Complete.");
	}

	@EventHandler
	public void handleIdMappingEvent(FMLModIdMappingEvent event) {

		refreshManagers();

		proxy.onIdRemap();
	}

	@EventHandler
	public void handleIMC(IMCEvent event) {

		preInitManagers();

		IMCHandler.INSTANCE.handleIMC(event.getMessages());
	}

	/* HELPERS */
	private void registerHandlers() {

		NetworkRegistry.INSTANCE.registerGuiHandler(instance, GUI_HANDLER);

		PacketTEBase.initialize();
	}

	private void preInitManagers() {

		SmelterManager.preInit();
		InsolatorManager.preInit();
		TransposerManager.preInit();
		EnchanterManager.preInit();
	}

	private void initManagers() {

		FurnaceManager.initialize();
		PulverizerManager.initialize();
		SawmillManager.initialize();
		SmelterManager.initialize();
		InsolatorManager.initialize();
		CompactorManager.initialize();
		CrucibleManager.initialize();
		RefineryManager.initialize();
		TransposerManager.initialize();
		ChargerManager.initialize();
		CentrifugeManager.initialize();
		BrewerManager.initialize();
		EnchanterManager.initialize();
		PrecipitatorManager.initialize();
		ExtruderManager.initialize();

		CoolantManager.initialize();
		TapperManager.initialize();
		FisherManager.initialize();
		XpCollectorManager.initialize();
		DiffuserManager.initialize();
		FactorizerManager.initialize();
	}

	private void refreshManagers() {

		FurnaceManager.refresh();
		PulverizerManager.refresh();
		SawmillManager.refresh();
		SmelterManager.refresh();
		InsolatorManager.refresh();
		CompactorManager.refresh();
		CrucibleManager.refresh();
		RefineryManager.refresh();
		TransposerManager.refresh();
		ChargerManager.refresh();
		CentrifugeManager.refresh();
		BrewerManager.refresh();
		EnchanterManager.refresh();
		PrecipitatorManager.refresh();
		ExtruderManager.refresh();

		TapperManager.refresh();
		FisherManager.refresh();
		XpCollectorManager.refresh();
		DiffuserManager.refresh();
		FactorizerManager.refresh();

		SteamManager.refresh();
		MagmaticManager.refresh();
		CompressionManager.refresh();
		ReactantManager.refresh();
		EnervationManager.refresh();
		NumismaticManager.refresh();
	}

}
