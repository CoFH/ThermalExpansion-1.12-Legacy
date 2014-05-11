package thermalexpansion.fluid;

import cofh.core.CoFHProps;
import cofh.util.ConfigHandler;
import cofh.util.ItemHelper;
import cofh.util.fluid.DispenserEmptyBucketHandler;
import cofh.util.fluid.DispenserFilledBucketHandler;
import cpw.mods.fml.common.registry.GameRegistry;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import net.minecraft.block.BlockDispenser;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.ShapelessOreRecipe;

import thermalexpansion.block.dynamo.TileDynamoCompression;
import thermalexpansion.block.dynamo.TileDynamoMagmatic;
import thermalexpansion.block.dynamo.TileDynamoReactant;
import thermalexpansion.core.TEProps;
import thermalexpansion.item.TEItems;
import thermalexpansion.item.tool.ItemBucket;
import thermalexpansion.item.tool.ItemFlorb;
import thermalexpansion.util.crafting.TransposerManager;

public class TEFluids {

	public static void preInit() {

		configFlorbs.setConfiguration(new Configuration(new File(CoFHProps.configDir, "cofh/ThermalExpansion-Florbs.cfg")));
		configFuels.setConfiguration(new Configuration(new File(CoFHProps.configDir, "cofh/ThermalExpansion-Fuels.cfg")));

		String category = "tweak";
		String comment = null;

		category = "item.feature";
		comment = "This allows you to disable Florbs entirely. It also means that you actively dislike fun things.";
		enableFlorbs = configFlorbs.get(category, "Florb.Enable", true, comment);
	}

	public static void initialize() {

		itemFlorb = (ItemFlorb) new ItemFlorb().setUnlocalizedName("florb");

		florb = itemFlorb.addItem(0, "florb");
		florbMagmatic = itemFlorb.addItem(1, "florbMagmatic");
	}

	public static void postInit() {

		parseFlorbs();
		parseFuels();

		fluidFlowrate.put("steam", 360);

		configFlorbs.cleanUp(true, false);
		configFuels.cleanUp(true, false);
	}

	/* HELPER FUNCTIONS */
	public static void registerDispenserHandlers() {

		BlockDispenser.dispenseBehaviorRegistry.putObject(itemBucket, new DispenserFilledBucketHandler());
		BlockDispenser.dispenseBehaviorRegistry.putObject(Items.bucket, new DispenserEmptyBucketHandler());
	}

	public static boolean registerMagmaticFuel(String name, int energy) {

		if (!FluidRegistry.isFluidRegistered(name)) {
			return false;
		}
		return TileDynamoMagmatic.registerFuel(FluidRegistry.getFluid(name), energy);
	}

	public static boolean registerCompressionFuel(String name, int energy) {

		if (!FluidRegistry.isFluidRegistered(name)) {
			return false;
		}
		return TileDynamoCompression.registerFuel(FluidRegistry.getFluid(name), energy);
	}

	public static boolean registerReactantFuel(String name, int energy) {

		if (!FluidRegistry.isFluidRegistered(name)) {
			return false;
		}
		return TileDynamoReactant.registerFuel(FluidRegistry.getFluid(name), energy);
	}

	public static boolean registerCoolant(String name, int cooling) {

		if (!FluidRegistry.isFluidRegistered(name)) {
			return false;
		}
		return TileDynamoCompression.registerCoolant(FluidRegistry.getFluid(name), cooling);
	}

	public static void parseFlorbs() {

		ItemStack florbStack = ItemHelper.cloneStack(florb, 4);
		ItemStack florbMagmaticStack = ItemHelper.cloneStack(florbMagmatic, 4);

		if (!enableFlorbs) {
			return;
		}
		GameRegistry.addRecipe(new ShapelessOreRecipe(florbStack, new Object[] { TEItems.sawdust, TEItems.slag, "slimeball" }));
		GameRegistry.addRecipe(new ShapelessOreRecipe(florbMagmaticStack, new Object[] { TEItems.sawdust, TEItems.slag, "slimeball", Items.blaze_powder }));
		GameRegistry.addRecipe(new ShapelessOreRecipe(florbMagmaticStack, new Object[] { TEItems.sawdust, TEItems.slag, Items.magma_cream }));

		for (Fluid fluid : FluidRegistry.getRegisteredFluids().values()) {
			if (fluid.canBePlacedInWorld() && configFlorbs.get("whitelist", fluid.getName(), true)) {
				if (fluid.getTemperature() < MAGMATIC_FLORB_TEMPERATURE) {
					florbList.add(ItemFlorb.setTag(new ItemStack(itemFlorb, 1, 0), fluid));
					TransposerManager.addFillRecipe(1600, florb, florbList.get(florbList.size() - 1), new FluidStack(fluid, 1000), false);
				} else {
					florbList.add(ItemFlorb.setTag(new ItemStack(itemFlorb, 1, 1), fluid));
					TransposerManager.addFillRecipe(1600, florbMagmatic, florbList.get(florbList.size() - 1), new FluidStack(fluid, 1000), false);
				}
			}
		}
	}

	public static void parseFuels() {

		String category = "fuels.magmatic";
		registerMagmaticFuel("lava", configFuels.get(category, "lava", TEProps.lavaRF * 9 / 10));
		registerMagmaticFuel("pyrotheum", configFuels.get(category, "pyrotheum", 2000000));

		Set<String> catKeys = configFuels.getCategoryKeys(category);
		for (String s : catKeys) {
			registerMagmaticFuel(s.toLowerCase(Locale.ENGLISH), configFuels.get(category, s, TEProps.lavaRF * 9 / 10));
		}

		category = "fuels.compression";
		registerCompressionFuel("coal", configFuels.get(category, "coal", 1000000));

		registerCompressionFuel("biofuel", configFuels.get(category, "biofuel", 500000));

		registerCompressionFuel("bioethanol", configFuels.get(category, "bioethanol", 500000));

		registerCompressionFuel("fuel", configFuels.get(category, "fuel", 1500000));
		registerCompressionFuel("oil", configFuels.get(category, "oil", 150000));

		catKeys = configFuels.getCategoryKeys(category);
		for (String s : catKeys) {
			registerCompressionFuel(s.toLowerCase(Locale.ENGLISH), configFuels.get(category, s, 500000));
		}

		category = "fuels.reactant";
		registerReactantFuel("redstone", configFuels.get(category, "redstone", 600000));
		registerReactantFuel("glowstone", configFuels.get(category, "glowstone", 750000));

		registerReactantFuel("mobessence", configFuels.get(category, "mobessence", 500000));
		registerReactantFuel("sewage", configFuels.get(category, "sewage", 12000));
		registerReactantFuel("sludge", configFuels.get(category, "sludge", 12000));

		registerReactantFuel("seedoil", configFuels.get(category, "seedoil", 250000));
		registerReactantFuel("biomass", configFuels.get(category, "biomass", 450000));

		registerReactantFuel("creosote", configFuels.get(category, "creosote", 200000));

		catKeys = configFuels.getCategoryKeys(category);
		for (String s : catKeys) {
			registerReactantFuel(s.toLowerCase(Locale.ENGLISH), configFuels.get(category, s, 200000));
		}

		category = "coolants";
		registerCoolant("water", configFuels.get(category, "water", 400000));
		registerCoolant("cryotheum", configFuels.get(category, "cryotheum", 4000000));
		registerCoolant("ice", configFuels.get(category, "ice", 2000000));

		catKeys = configFuels.getCategoryKeys(category);
		for (String s : catKeys) {
			registerCoolant(s.toLowerCase(Locale.ENGLISH), configFuels.get(category, s, 400000));
		}
	}

	public static Fluid fluidSteam;

	public static ItemBucket itemBucket;
	public static ItemFlorb itemFlorb;

	public static ItemStack florb;
	public static ItemStack florbMagmatic;
	public static ArrayList<ItemStack> florbList = new ArrayList();

	public static boolean enableFlorbs = true;
	public static final int MAGMATIC_FLORB_TEMPERATURE = 1000;

	public static final Map<String, Integer> fluidFlowrate = new HashMap<String, Integer>();

	public static ConfigHandler configFlorbs = new ConfigHandler(TEProps.VERSION);
	public static ConfigHandler configFuels = new ConfigHandler(TEProps.VERSION);

}
