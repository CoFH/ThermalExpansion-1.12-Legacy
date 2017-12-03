package cofh.thermalexpansion;

import codechicken.lib.CodeChickenLib;
import cofh.CoFHCore;
import cofh.core.init.CoreProps;
import cofh.core.util.ConfigHandler;
import cofh.thermalexpansion.gui.GuiHandler;
import cofh.thermalexpansion.init.*;
import cofh.thermalexpansion.item.ItemFlorb;
import cofh.thermalexpansion.network.PacketTEBase;
import cofh.thermalexpansion.proxy.Proxy;
import cofh.thermalexpansion.util.IMCHandler;
import cofh.thermalexpansion.util.managers.*;
import cofh.thermalexpansion.util.managers.dynamo.*;
import cofh.thermalexpansion.util.managers.machine.*;
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

@Mod (modid = ThermalExpansion.MOD_ID, name = ThermalExpansion.MOD_NAME, version = ThermalExpansion.VERSION, dependencies = ThermalExpansion.DEPENDENCIES, updateJSON = ThermalExpansion.UPDATE_URL)
public class ThermalExpansion {

	public static final String MOD_ID = "thermalexpansion";
	public static final String MOD_NAME = "Thermal Expansion";

	public static final String VERSION = "5.3.8";
	public static final String VERSION_MAX = "5.4.0";
	public static final String VERSION_GROUP = "required-after:" + MOD_ID + "@[" + VERSION + "," + VERSION_MAX + ");";
	public static final String UPDATE_URL = "https://raw.github.com/cofh/version/master/" + MOD_ID + "_update.json";

	public static final String DEPENDENCIES = CoFHCore.VERSION_GROUP + ThermalFoundation.VERSION_GROUP + CodeChickenLib.MOD_VERSION_DEP;
	public static final String MOD_GUI_FACTORY = "cofh.thermalexpansion.gui.GuiConfigTEFactory";

	@Instance (MOD_ID)
	public static ThermalExpansion instance;

	@SidedProxy (clientSide = "cofh.thermalexpansion.proxy.ProxyClient", serverSide = "cofh.thermalexpansion.proxy.Proxy")
	public static Proxy proxy;

	public static final Logger LOG = LogManager.getLogger(MOD_ID);
	public static final ConfigHandler CONFIG = new ConfigHandler(VERSION);
	public static final ConfigHandler CONFIG_CLIENT = new ConfigHandler(VERSION);
	public static final GuiHandler GUI_HANDLER = new GuiHandler();

	public static CreativeTabs tabCommon;
	public static CreativeTabs tabItems;
	public static CreativeTabs tabFlorbs;

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

		proxy.initialize(event);
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {

		TEPlugins.postInit();

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

		managerRefresh();
	}

	@EventHandler
	public void handleIMC(IMCEvent event) {

		managerInitialize();

		IMCHandler.INSTANCE.handleIMC(event.getMessages());
	}

	/* HELPERS */
	private void registerHandlers() {

		NetworkRegistry.INSTANCE.registerGuiHandler(instance, GUI_HANDLER);

		PacketTEBase.initialize();
	}

	private void managerInitialize() {

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

		SteamManager.initialize();
		MagmaticManager.initialize();
		CompressionManager.initialize();
		ReactantManager.initialize();
		EnervationManager.initialize();
		NumismaticManager.initialize();

		ItemFlorb.parseFlorbs();
	}

	private synchronized void managerRefresh() {

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

		SteamManager.refresh();
		MagmaticManager.refresh();
		CompressionManager.refresh();
		ReactantManager.refresh();
		EnervationManager.refresh();
		NumismaticManager.refresh();
	}

}
