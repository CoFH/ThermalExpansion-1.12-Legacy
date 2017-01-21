package cofh.thermalexpansion;

import cofh.CoFHCore;
import cofh.core.CoFHProps;
import cofh.core.util.ConfigHandler;
import cofh.thermalexpansion.gui.CreativeTabTE;
import cofh.thermalexpansion.gui.GuiHandler;
import cofh.thermalexpansion.init.TEBlocks;
import cofh.thermalexpansion.init.TEProps;
import cofh.thermalexpansion.network.PacketTEBase;
import cofh.thermalexpansion.proxy.Proxy;
import cofh.thermalexpansion.util.IMCHandler;
import cofh.thermalexpansion.util.crafting.*;
import cofh.thermalfoundation.ThermalFoundation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.CustomProperty;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.*;
import net.minecraftforge.fml.common.event.FMLInterModComms.IMCEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;

@Mod (modid = ThermalExpansion.MOD_ID, name = ThermalExpansion.MOD_NAME, version = ThermalExpansion.VERSION, dependencies = ThermalExpansion.DEPENDENCIES, guiFactory = ThermalExpansion.MOD_GUI_FACTORY, customProperties = @CustomProperty (k = "cofhversion", v = "true"))
public class ThermalExpansion {

	public static final String MOD_ID = "thermalexpansion";
	public static final String MOD_NAME = "Thermal Expansion";

	public static final String VERSION = "5.0.0";
	public static final String VERSION_MAX = "5.1.0";
	public static final String VERSION_GROUP = "required-after:" + MOD_ID + "@[" + VERSION + "," + VERSION_MAX + ");";

	public static final String DEPENDENCIES = CoFHCore.VERSION_GROUP + ThermalFoundation.VERSION_GROUP + "required-after:CodeChickenLib";
	public static final String MOD_GUI_FACTORY = "cofh.thermalexpansion.gui.GuiConfigTEFactory";

	@Instance (MOD_ID)
	public static ThermalExpansion instance;

	@SidedProxy (clientSide = "cofh.thermalexpansion.proxy.ProxyClient", serverSide = "cofh.thermalexpansion.proxy.Proxy")
	public static Proxy proxy;

	public static final Logger LOG = LogManager.getLogger(MOD_ID);
	public static final ConfigHandler CONFIG = new ConfigHandler(VERSION);
	public static final ConfigHandler CONFIG_CLIENT = new ConfigHandler(VERSION);
	public static final GuiHandler GUI_HANDLER = new GuiHandler();

	public static CreativeTabs tabCommon = new CreativeTabTE();
	public static CreativeTabs tabBlocks = tabCommon;
	public static CreativeTabs tabItems = tabCommon;
	public static CreativeTabs tabTools = tabCommon;
	public static CreativeTabs tabFlorbs = tabCommon;

	public ThermalExpansion() {

		super();
	}

	/* INIT */
	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {

		CONFIG.setConfiguration(new Configuration(new File(CoFHProps.configDir, "/cofh/" + MOD_ID + "/common.cfg"), true));
		CONFIG_CLIENT.setConfiguration(new Configuration(new File(CoFHProps.configDir, "/cofh/" + MOD_ID + "/client.cfg"), true));

		TEProps.preInit();
		TEBlocks.preInit();
		// TEFlorbs.preInit();

		proxy.preInit(event);
	}

	@EventHandler
	public void initialize(FMLInitializationEvent event) {

		TEBlocks.initialize();
		// TEFlorbs.initialize();

		/* Register Handlers */
		registerHandlers();

		proxy.initialize(event);
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {

		TEBlocks.postInit();
		// TEFlorbs.postInit();

		addDefaultRecipes();

		proxy.postInit(event);
	}

	@EventHandler
	public void loadComplete(FMLLoadCompleteEvent event) {

		IMCHandler.instance.handleIMC(FMLInterModComms.fetchRuntimeMessages(this));

		TEProps.loadComplete();
		CONFIG.cleanUp(false, true);
		CONFIG_CLIENT.cleanUp(false, true);

		LOG.info(MOD_NAME + ": Load Complete.");
	}

	@EventHandler
	public void serverStart(FMLServerAboutToStartEvent event) {

	}

	@EventHandler
	public void serverStarting(FMLServerStartedEvent event) {

	}

	@EventHandler
	public void handleIMC(IMCEvent theIMC) {

		IMCHandler.instance.handleIMC(theIMC.getMessages());
	}

	/* HELPERS */
	private void registerHandlers() {

		NetworkRegistry.INSTANCE.registerGuiHandler(instance, GUI_HANDLER);
		MinecraftForge.EVENT_BUS.register(proxy);

		PacketTEBase.initialize();
	}

	private void addDefaultRecipes() {

		FurnaceManager.addDefaultRecipes();
		PulverizerManager.addDefaultRecipes();
		SawmillManager.addDefaultRecipes();
		SmelterManager.addDefaultRecipes();
		InsolatorManager.addDefaultRecipes();
		ChargerManager.addDefaultRecipes();
		CrucibleManager.addDefaultRecipes();
		TransposerManager.addDefaultRecipes();

		PrecipitatorManager.addDefaultRecipes();
		ExtruderManager.addDefaultRecipes();
	}

}
