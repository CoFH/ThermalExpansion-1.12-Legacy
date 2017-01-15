package cofh.thermalexpansion;

import cofh.CoFHCore;
import cofh.core.CoFHProps;
import cofh.core.network.PacketCoFHBase;
import cofh.core.util.ConfigHandler;
import cofh.lib.util.helpers.StringHelper;
import cofh.thermalexpansion.block.TEBlocks;
import cofh.thermalexpansion.block.cell.BlockCell;
import cofh.thermalexpansion.block.cell.TileCell;
import cofh.thermalexpansion.block.device.BlockDevice;
import cofh.thermalexpansion.block.device.TileDeviceBase;
import cofh.thermalexpansion.block.dynamo.BlockDynamo;
import cofh.thermalexpansion.block.dynamo.TileDynamoBase;
import cofh.thermalexpansion.block.machine.BlockMachine;
import cofh.thermalexpansion.block.machine.TileMachineBase;
import cofh.thermalexpansion.block.strongbox.TileStrongbox;
import cofh.thermalexpansion.block.workbench.TileWorkbench;
import cofh.thermalexpansion.core.TEAchievements;
import cofh.thermalexpansion.core.TEProps;
import cofh.thermalexpansion.gui.GuiHandler;
import cofh.thermalexpansion.gui.TECreativeTab;
import cofh.thermalexpansion.gui.TECreativeTabFlorbs;
import cofh.thermalexpansion.item.ItemSatchel;
import cofh.thermalexpansion.item.TEItems;
import cofh.thermalexpansion.network.PacketTEBase;
import cofh.thermalexpansion.network.PacketTEBase.PacketTypes;
import cofh.thermalexpansion.proxy.Proxy;
import cofh.thermalexpansion.util.FMLEventHandler;
import cofh.thermalexpansion.util.FuelManager;
import cofh.thermalexpansion.util.IMCHandler;
import cofh.thermalexpansion.util.crafting.*;
import cofh.thermalfoundation.ThermalFoundation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.CustomProperty;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.*;
import net.minecraftforge.fml.common.event.FMLInterModComms.IMCEvent;
import net.minecraftforge.fml.common.event.FMLMissingMappingsEvent.MissingMapping;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.RecipeSorter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.Arrays;
import java.util.List;

@Mod (modid = ThermalExpansion.MOD_ID, name = ThermalExpansion.MOD_NAME, version = ThermalExpansion.VERSION, dependencies = ThermalExpansion.DEPENDENCIES, guiFactory = ThermalExpansion.MOD_GUI_FACTORY, customProperties = @CustomProperty (k = "cofhversion", v = "true"))
public class ThermalExpansion {

	public static final String MOD_ID = "thermalexpansion";
	public static final String MOD_NAME = "Thermal Expansion";

	public static final String VERSION = "4.2.0";
	public static final String VERSION_MAX = "4.3.0";
	public static final String VERSION_GROUP = "required-after:" + MOD_ID + "@[" + VERSION + "," + VERSION_MAX + ");";

	public static final String DEPENDENCIES = CoFHCore.VERSION_GROUP + ThermalFoundation.VERSION_GROUP + "required-after:CodeChickenLib";
	public static final String MOD_GUI_FACTORY = "cofh.thermalexpansion.gui.GuiConfigTEFactory";

	@Instance (MOD_ID)
	public static ThermalExpansion instance;

	@SidedProxy (clientSide = "cofh.thermalexpansion.proxy.ProxyClient", serverSide = "cofh.thermalexpansion.proxy.Proxy")
	public static Proxy proxy;

	public static final Logger log = LogManager.getLogger(MOD_ID);
	public static final ConfigHandler config = new ConfigHandler(VERSION);
	public static final ConfigHandler configClient = new ConfigHandler(VERSION);
	public static final GuiHandler guiHandler = new GuiHandler();

	public static CreativeTabs tabCommon = null;
	public static CreativeTabs tabBlocks = tabCommon;
	public static CreativeTabs tabItems = tabCommon;
	public static CreativeTabs tabTools = tabCommon;
	public static CreativeTabs tabFlorbs = tabCommon;

	/* INIT SEQUENCE */
	public ThermalExpansion() {

		super();
	}

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {

		FMLLog.info("ThermalExpansion");

		//UpdateManager.registerUpdater(new UpdateManager(this, RELEASE_URL, CoFHProps.DOWNLOAD_URL));
		config.setConfiguration(new Configuration(new File(CoFHProps.configDir, "cofh/thermalexpansion/common.cfg"), true));
		configClient.setConfiguration(new Configuration(new File(CoFHProps.configDir, "cofh/thermalexpansion/client.cfg"), true));

		FMLEventHandler.initialize();
		TECraftingHandler.initialize();
		TECraftingParser.initialize();

		RecipeSorter.register("thermalexpansion:machine", RecipeMachine.class, RecipeSorter.Category.SHAPED, "before:cofh:upgrade");
		RecipeSorter.register("thermalexpansion:machineUpgrade", RecipeMachineUpgrade.class, RecipeSorter.Category.SHAPED, "before:cofh:upgrade");
		//RecipeSorter.register("thermalexpansion:style", RecipeStyle.class, RecipeSorter.Category.SHAPED, "after:forge:shapedore");
		//RecipeSorter.register("thermalexpansion:NEIWrapper", NEIRecipeWrapper.class, RecipeSorter.Category.UNKNOWN, "after:forge:shapedore");

		cleanConfig(true);
		configOptions();

		TEItems.preInit();
		TEBlocks.preInit();
		proxy.preInit();
	}

	@EventHandler
	public void initialize(FMLInitializationEvent event) {

		registerMachineOreDict();
		TEItems.initialize();
		TEBlocks.initialize();
		//TeleportChannelRegistry.initialize();

		if (TEProps.enableAchievements) {
			TEAchievements.initialize();
		}
		/* Register Handlers */
		NetworkRegistry.INSTANCE.registerGuiHandler(instance, guiHandler);
		MinecraftForge.EVENT_BUS.register(proxy);
		PacketTEBase.initialize();
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {

		try {
			TECraftingParser.parseCraftingFiles();
		} catch (Throwable t) {
			t.printStackTrace();
		}
		FurnaceManager.addDefaultRecipes();
		PulverizerManager.addDefaultRecipes();
		SawmillManager.addDefaultRecipes();
		SmelterManager.addDefaultRecipes();
		CrucibleManager.addDefaultRecipes();
		TransposerManager.addDefaultRecipes();
		PrecipitatorManager.addDefaultRecipes();
		ExtruderManager.addDefaultRecipes();
		ChargerManager.addDefaultRecipes();
		InsolatorManager.addDefaultRecipes();

		TEItems.postInit();
		TEBlocks.postInit();

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
		InsolatorManager.loadRecipes();

		FuelManager.parseFuels();

		cleanConfig(false);
		config.cleanUp(false, true);
		configClient.cleanUp(false, true);

		log.info("Thermal Expansion: Load Complete.");
	}

	@EventHandler
	public void serverStart(FMLServerAboutToStartEvent event) {

		//TeleportChannelRegistry.createServerRegistry();
		//TeleportChannelRegistry.createClientRegistry();
	}

	@EventHandler
	public void serverStarting(FMLServerStartedEvent event) {

		handleIdMapping();
	}

	@EventHandler
	public void handleIMC(IMCEvent theIMC) {

		IMCHandler.instance.handleIMC(theIMC.getMessages());
	}

	public void handleConfigSync(PacketCoFHBase payload) {

		handleIdMapping();

		for (int i = 0; i < TileDeviceBase.enableSecurity.length; i++) {
			TileDeviceBase.enableSecurity[i] = payload.getBool();
		}
		for (int i = 0; i < TileMachineBase.enableSecurity.length; i++) {
			TileMachineBase.enableSecurity[i] = payload.getBool();
		}
		TileDynamoBase.enableSecurity = payload.getBool();
		TileCell.enableSecurity = payload.getBool();
		TileStrongbox.enableSecurity = payload.getBool();
		TileWorkbench.enableSecurity = payload.getBool();

		ItemSatchel.enableSecurity = payload.getBool();

		log.info("Receiving Server Configuration...");
		//TeleportChannelRegistry.createClientRegistry();
	}

	public PacketCoFHBase getConfigSync() {

		PacketCoFHBase payload = PacketTEBase.getPacket(PacketTypes.CONFIG_SYNC);

		for (int i = 0; i < TileDeviceBase.enableSecurity.length; i++) {
			payload.addBool(TileDeviceBase.enableSecurity[i]);
		}
		for (int i = 0; i < TileMachineBase.enableSecurity.length; i++) {
			payload.addBool(TileMachineBase.enableSecurity[i]);
		}
		payload.addBool(TileDynamoBase.enableSecurity);
		payload.addBool(TileCell.enableSecurity);
		payload.addBool(TileStrongbox.enableSecurity);
		payload.addBool(TileWorkbench.enableSecurity);

		payload.addBool(ItemSatchel.enableSecurity);

		return payload;
	}

	public synchronized void handleIdMapping() {

		FurnaceManager.refreshRecipes();
		PulverizerManager.refreshRecipes();
		SawmillManager.refreshRecipes();
		SmelterManager.refreshRecipes();
		CrucibleManager.refreshRecipes();
		TransposerManager.refreshRecipes();
		PrecipitatorManager.refreshRecipes();
		ExtruderManager.refreshRecipes();
		ChargerManager.refreshRecipes();
		InsolatorManager.refreshRecipes();

		BlockDevice.refreshItemStacks();
		BlockDynamo.refreshItemStacks();
		BlockMachine.refreshItemStacks();
	}

	// Called when the client disconnects from the server.
	public void resetClientConfigs() {

		TileCell.configure();
		TileDeviceBase.configure();
		TileDynamoBase.configure();
		TileMachineBase.configure();
		TileWorkbench.configure();
		TileStrongbox.configure();
		ItemSatchel.configure();

		handleIdMapping();

		log.info(StringHelper.localize("Restoring Client Configuration..."));
	}

	/* LOADING FUNCTIONS */
	void registerMachineOreDict() {

		String category;
		String comment;

		/* GENERAL */
		category = "General";
		comment = "If enabled, ingots are used instead of gears in many default recipes.";
		String iPrefix = ThermalExpansion.config.get(category, "UseIngots", false, comment) ? "ingot" : "gear";
		for (String entry : Arrays.asList("Iron", "Gold", "Copper", "Tin", "Silver", "Lead", "Nickel", "Platinum", "Mithril", "Electrum", "Invar", "Bronze", "Signalum", "Lumium", "Enderium")) {
			String prefix = "thermalexpansion:machine";
			List<ItemStack> partList = OreDictionary.getOres(iPrefix + entry);
			for (int i = 0; i < partList.size(); i++) {
				OreDictionary.registerOre(prefix + entry, partList.get(i));
			}
		}
	}

	void configOptions() {

		String category;
		@SuppressWarnings ("unused") String comment;

		/* GRAPHICS */
		if (CoFHProps.enableColorBlindTextures) {
			TEProps.textureGuiCommon = TEProps.PATH_COMMON_CB;
			TEProps.textureGuiAssembler = TEProps.PATH_ASSEMBLER_CB;
			TEProps.textureSelection = TEProps.TEXTURE_CB;
			BlockCell.textureSelection = BlockCell.TEXTURE_CB;
		}
		TEProps.useAlternateStarfieldShader = ThermalExpansion.configClient.get("Render", "UseAlternateShader", true, "Set to TRUE for Tesseracts to use an alternate starfield shader.");

		/* INTERFACE */
		category = "Interface.CreativeTab";
		boolean blockTab = false;
		boolean itemTab = false;
		boolean toolTab = false;
		boolean florbTab = false;

		comment = "Set to TRUE to put Thermal Expansion Blocks under a general \"Thermal Expansion\" Creative Tab.";
		blockTab = configClient.get(category, "BlocksInCommonTab", blockTab);

		comment = "Set to TRUE to put Thermal Expansion Items under a general \"Thermal Expansion\" Creative Tab.";
		itemTab = configClient.get(category, "ItemsInCommonTab", itemTab);

		comment = "Set to TRUE to put Thermal Expansion Tools under a general \"Thermal Expansion\" Creative Tab.";
		toolTab = configClient.get(category, "ToolsInCommonTab", toolTab);

		comment = "Set to TRUE to put Thermal Expansion Florbs under a general \"Thermal Expansion\" Creative Tab.";
		florbTab = configClient.get(category, "FlorbsInCommonTab", florbTab);

		if (blockTab || itemTab || toolTab || florbTab) {
			tabCommon = new TECreativeTab();
		}
		tabBlocks = blockTab ? tabCommon : new TECreativeTab("Blocks") {

			//	@Override
			//	protected ItemStack getStack() {
			//
			//		return BlockFrame.frameCellReinforcedFull;
			//	}
		};
		tabItems = itemTab ? tabCommon : new TECreativeTab("Items") {

			@Override
			protected ItemStack getStack() {

				return TEItems.powerCoilElectrum;
			}
		};
		tabTools = toolTab ? tabCommon : new TECreativeTab("Tools") {

			@Override
			protected ItemStack getStack() {

				return TEItems.toolWrench;
			}
		};
		tabFlorbs = florbTab ? tabCommon : new TECreativeTabFlorbs();
		// TEProps.enableDebugOutput = config.get(category, "EnableDebugOutput", TEProps.enableDebugOutput);
		// TEProps.enableAchievements = config.get(category, "EnableAchievements", TEProps.enableAchievements);
	}

	void cleanConfig(boolean preInit) {

		if (preInit) {

		}
		String prefix = "config.thermalexpansion.";
		String[] categoryNames = config.getCategoryNames().toArray(new String[config.getCategoryNames().size()]);
		for (int i = 0; i < categoryNames.length; i++) {
			config.getCategory(categoryNames[i]).setLanguageKey(prefix + categoryNames[i]).setRequiresMcRestart(true);
		}
		categoryNames = configClient.getCategoryNames().toArray(new String[configClient.getCategoryNames().size()]);
		for (int i = 0; i < categoryNames.length; i++) {
			configClient.getCategory(categoryNames[i]).setLanguageKey(prefix + categoryNames[i]).setRequiresMcRestart(true);
		}
	}

	@EventHandler
	@SuppressWarnings ("deprecation")
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

}
