package cofh.thermalexpansion.util;

import cofh.core.CoFHProps;
import cofh.core.util.ConfigHandler;
import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalexpansion.block.dynamo.TileDynamoCompression;
import cofh.thermalexpansion.block.dynamo.TileDynamoMagmatic;
import cofh.thermalexpansion.block.dynamo.TileDynamoReactant;
import cofh.thermalexpansion.core.TEProps;

import java.io.File;
import java.util.Locale;
import java.util.Set;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fluids.FluidRegistry;


public class FuelHandler {

	public static ConfigHandler configFuels = new ConfigHandler(ThermalExpansion.version);

	static {
		configFuels.setConfiguration(new Configuration(new File(CoFHProps.configDir, "cofh/ThermalExpansion-Fuels.cfg")));
	}

	private FuelHandler() {

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
		configFuels.cleanUp(true, false);
	}

}
