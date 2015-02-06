package cofh.thermalexpansion.plugins.mfr;

import cofh.lib.util.helpers.MathHelper;
import cofh.thermalexpansion.ThermalExpansion;
import cpw.mods.fml.common.Loader;

import powercrystals.minefactoryreloaded.api.FactoryRegistry;
import powercrystals.minefactoryreloaded.api.ValuedItem;


public class MFRPlugin {

	public static void preInit() {

		String category = "plugins.tweak";

		strawRedstone = ThermalExpansion.config.get(category, "MFR.Redstone.Straw", true);
		strawGlowstone = ThermalExpansion.config.get(category, "MFR.Glowstone.Straw", true);
		strawEnder = ThermalExpansion.config.get(category, "MFR.Ender.Straw", true);
		strawPyrotheum = ThermalExpansion.config.get(category, "MFR.Pyrotheum.Straw", true);
		strawCryotheum = ThermalExpansion.config.get(category, "MFR.Cryotheum.Straw", true);
		strawCoal = ThermalExpansion.config.get(category, "MFR.Coal.Straw", true);

		strawEnderRange = ThermalExpansion.config.get(category, "MFR.Ender.Range", strawEnderRange,
				"This controls the maximum distance (in blocks) a player will teleport from drinking Ender. (Max: 65536)");
		strawEnderRange = MathHelper.clampI(strawEnderRange, 8, 65536);
	}

	public static void initialize() {

	}

	public static void postInit() {

		if (Loader.isModLoaded("MineFactoryReloaded")) {
			if (strawRedstone) {
				FactoryRegistry.sendMessage("registerLiquidDrinkHandler", new ValuedItem("redstone", DrinkHandlerRedstone.instance));
			}
			if (strawGlowstone) {
				FactoryRegistry.sendMessage("registerLiquidDrinkHandler", new ValuedItem("glowstone", DrinkHandlerGlowstone.instance));
			}
			if (strawEnder) {
				FactoryRegistry.sendMessage("registerLiquidDrinkHandler", new ValuedItem("ender", DrinkHandlerEnder.instance));
			}
			if (strawPyrotheum) {
				FactoryRegistry.sendMessage("registerLiquidDrinkHandler", new ValuedItem("pyrotheum", DrinkHandlerPyrotheum.instance));
			}
			if (strawCryotheum) {
				FactoryRegistry.sendMessage("registerLiquidDrinkHandler", new ValuedItem("cryotheum", DrinkHandlerCryotheum.instance));
			}
			if (strawCoal) {
				FactoryRegistry.sendMessage("registerLiquidDrinkHandler", new ValuedItem("coal", DrinkHandlerCoal.instance));
			}
			ThermalExpansion.log.info("MineFactoryReloaded Plugin Enabled.");
		}
	}

	public static boolean strawRedstone = true;
	public static boolean strawGlowstone = true;
	public static boolean strawEnder = true;
	public static boolean strawPyrotheum = true;
	public static boolean strawCryotheum = true;
	public static boolean strawCoal = true;

	public static int strawEnderRange = 16384;

}
