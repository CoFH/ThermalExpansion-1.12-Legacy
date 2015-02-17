package cofh.thermalexpansion;

import cofh.core.CoFHProps;
import cofh.core.network.PacketCoFHBase;
import cofh.core.util.ConfigHandler;
import cofh.lib.util.helpers.StringHelper;
import cofh.mod.BaseMod;
import cofh.mod.updater.UpdateManager;
import cofh.thermalexpansion.block.TEBlocks;
import cofh.thermalexpansion.block.cell.BlockCell;
import cofh.thermalexpansion.block.cell.TileCell;
import cofh.thermalexpansion.block.device.BlockDevice;
import cofh.thermalexpansion.block.device.TileActivator;
import cofh.thermalexpansion.block.device.TileBreaker;
import cofh.thermalexpansion.block.device.TileNullifier;
import cofh.thermalexpansion.block.device.TileWorkbench;
import cofh.thermalexpansion.block.dynamo.BlockDynamo;
import cofh.thermalexpansion.block.dynamo.TileDynamoBase;
import cofh.thermalexpansion.block.machine.BlockMachine;
import cofh.thermalexpansion.block.machine.TileMachineBase;
import cofh.thermalexpansion.block.strongbox.TileStrongbox;
import cofh.thermalexpansion.core.Proxy;
import cofh.thermalexpansion.core.TEAchievements;
import cofh.thermalexpansion.core.TEProps;
import cofh.thermalexpansion.gui.GuiHandler;
import cofh.thermalexpansion.gui.TECreativeTab;
import cofh.thermalexpansion.gui.TECreativeTabFlorbs;
import cofh.thermalexpansion.item.ItemSatchel;
import cofh.thermalexpansion.item.TEItems;
import cofh.thermalexpansion.network.PacketTEBase;
import cofh.thermalexpansion.network.PacketTEBase.PacketTypes;
import cofh.thermalexpansion.plugins.TEPlugins;
import cofh.thermalexpansion.util.FMLEventHandler;
import cofh.thermalexpansion.util.FuelHandler;
import cofh.thermalexpansion.util.IMCHandler;
import cofh.thermalexpansion.util.crafting.ChargerManager;
import cofh.thermalexpansion.util.crafting.CrucibleManager;
import cofh.thermalexpansion.util.crafting.ExtruderManager;
import cofh.thermalexpansion.util.crafting.FurnaceManager;
import cofh.thermalexpansion.util.crafting.InsolatorManager;
import cofh.thermalexpansion.util.crafting.PrecipitatorManager;
import cofh.thermalexpansion.util.crafting.PulverizerManager;
import cofh.thermalexpansion.util.crafting.RecipeMachine;
import cofh.thermalexpansion.util.crafting.RecipeMachineUpgrade;
import cofh.thermalexpansion.util.crafting.SawmillManager;
import cofh.thermalexpansion.util.crafting.SmelterManager;
import cofh.thermalexpansion.util.crafting.TECraftingHandler;
import cofh.thermalexpansion.util.crafting.TransposerManager;
import cofh.thermalfoundation.ThermalFoundation;
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
import cpw.mods.fml.common.event.FMLServerStartedEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.RecipeSorter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(modid = ThermalExpansion.modId, name = ThermalExpansion.modName, version = ThermalExpansion.version, dependencies = ThermalExpansion.dependencies,
		guiFactory = ThermalExpansion.modGuiFactory, customProperties = @CustomProperty(k = "cofhversion", v = "true"))
public class ThermalExpansion extends BaseMod {

	public static final String modId = "ThermalExpansion";
	public static final String modName = "Thermal Expansion";
	public static final String version = "1.7.10R4.0.0B9";
	public static final String dependencies = "required-after:ThermalFoundation@[" + ThermalFoundation.version + ",)";
	public static final String releaseURL = "https://raw.github.com/CoFH/Version/master/ThermalExpansion";
	public static final String modGuiFactory = "cofh.thermalexpansion.gui.GuiConfigTEFactory";

	@Instance(modId)
	public static ThermalExpansion instance;

	@SidedProxy(clientSide = "cofh.thermalexpansion.core.ProxyClient", serverSide = "cofh.thermalexpansion.core.Proxy")
	public static Proxy proxy;

	public static final Logger log = LogManager.getLogger(modId);

	public static final ConfigHandler config = new ConfigHandler(version);
	public static final ConfigHandler configClient = new ConfigHandler(version);
	public static final GuiHandler guiHandler = new GuiHandler();

	public static final CreativeTabs tabBlocks = new TECreativeTab("Blocks") {

		// @Override
		// protected ItemStack getStack() {
		//
		// return BlockFrame.frameCellReinforcedFull;
		// }
	};
	public static final CreativeTabs tabItems = new TECreativeTab("Items") {

		@Override
		protected ItemStack getStack() {

			return TEItems.powerCoilElectrum;
		}
	};
	public static final CreativeTabs tabTools = new TECreativeTab("Tools") {

		@Override
		protected ItemStack getStack() {

			return TEItems.toolWrench;
		}
	};
	public static final CreativeTabs tabFlorbs = new TECreativeTabFlorbs();

	/* INIT SEQUENCE */
	public ThermalExpansion() {

		super(log);
	}

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {

		UpdateManager.registerUpdater(new UpdateManager(this, releaseURL));
		config.setConfiguration(new Configuration(new File(event.getModConfigurationDirectory(), "cofh/thermalexpansion/common.cfg"), true));
		configClient.setConfiguration(new Configuration(new File(event.getModConfigurationDirectory(), "cofh/thermalexpansion/client.cfg"), true));

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
			TEAchievements.initialize();
		}

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
		InsolatorManager.addDefaultRecipes();

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
		InsolatorManager.loadRecipes();

		FuelHandler.parseFuels();

		TEPlugins.loadComplete();

		cleanConfig(false);
		config.cleanUp(false, true);
		configClient.cleanUp(false, true);

		log.info("Load Complete.");
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
		TileWorkbench.configure();
		TileActivator.configure();
		TileBreaker.configure();
		TileNullifier.configure();
		TileDynamoBase.configure();
		TileMachineBase.configure();
		TileStrongbox.configure();
		ItemSatchel.configure();

		handleIdMapping();

		log.info(StringHelper.localize("Restoring Client Configuration..."));
	}

	/* LOADING FUNCTIONS */
	void configOptions() {

		String category;
		String comment;

		category = "Holiday";
		comment = "Set this to true to disable Christmas cheer. Scrooge. :(";
		TEProps.holidayChristmas = !config.get(category, "HoHoNo", false, comment);

		/* Graphics Config */
		if (CoFHProps.enableColorBlindTextures) {
			TEProps.textureGuiCommon = TEProps.PATH_COMMON_CB;
			TEProps.textureGuiAssembler = TEProps.PATH_ASSEMBLER_CB;
			TEProps.textureSelection = TEProps.TEXTURE_CB;
			BlockCell.textureSelection = BlockCell.TEXTURE_CB;
		}

		category = "General";
		comment = "If enabled, ingots are used instead of gears in many default recipes.";
		String iPrefix = ThermalExpansion.config.get(category, "UseIngots", false, comment) ? "ingot" : "gear";
		for (String entry : Arrays.asList("Iron", "Gold", "Copper", "Tin", "Silver", "Lead", "Nickel", "Platinum", "Mithril", "Electrum", "Invar", "Bronze",
				"Signalum", "Lumium", "Enderium")) {
			String prefix = "thermalexpansion:machine";
			ArrayList<ItemStack> partList = OreDictionary.getOres(iPrefix + entry);
			for (int i = 0; i < partList.size(); i++) {
				OreDictionary.registerOre(prefix + entry, partList.get(i));
			}
		}

		// TEProps.enableDebugOutput = config.get(category, "EnableDebugOutput", TEProps.enableDebugOutput);
		// TEProps.enableAchievements = config.get(category, "EnableAchievements", TEProps.enableAchievements);
	}

	void cleanConfig(boolean preInit) {

		if (preInit) {
			// BEGIN TEMP CODE
			// TODO: Remove after 4.1

			config.renameCategory("security", "Security");
			config.removeCategory("world");
			config.removeCategory("World");

			// END TEMP CODE
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
