package cofh.thermalexpansion.plugins.mfr;

import cofh.lib.util.helpers.MathHelper;
import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalexpansion.item.TEItems;
import cpw.mods.fml.common.Loader;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import powercrystals.minefactoryreloaded.api.FactoryRegistry;
import powercrystals.minefactoryreloaded.api.FertilizerType;
import powercrystals.minefactoryreloaded.api.IFactoryFertilizer;
import powercrystals.minefactoryreloaded.api.ValuedItem;

public class MFRPlugin {

	public static void preInit() {

		String comment;
		String category = "Plugins.MineFactoryReloaded.Straw";

		strawRedstone = ThermalExpansion.config.get(category, "Redstone", true);
		strawGlowstone = ThermalExpansion.config.get(category, "Glowstone", true);
		strawEnder = ThermalExpansion.config.get(category, "Ender", true);
		strawPyrotheum = ThermalExpansion.config.get(category, "Pyrotheum", true);
		strawCryotheum = ThermalExpansion.config.get(category, "Cryotheum", true);
		strawCoal = ThermalExpansion.config.get(category, "Coal", true);

		comment = "This controls the maximum distance (in blocks) a player will teleport from drinking Ender. (Min: 8, Max: 65536)";
		strawEnderRange = ThermalExpansion.config.get(category, "Ender.Range", strawEnderRange, comment);
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
			FactoryRegistry.sendMessage("registerFertilizer", new IFactoryFertilizer() {

				@Override
				public Item getFertilizer() {

					return TEItems.itemMaterial;
				}

				@Override
				public FertilizerType getFertilizerType(ItemStack stack) {

					if (TEItems.fertilizer.isItemEqual(stack))
						return FertilizerType.GrowPlant;
					else if (TEItems.fertilizerRich.isItemEqual(stack))
						return FertilizerType.GrowPlant;
					return FertilizerType.None;
				}

				@Override
				public void consume(ItemStack fertilizer) {

					if (TEItems.fertilizerRich.isItemEqual(fertilizer)) {
						if (MathHelper.RANDOM.nextBoolean())
							fertilizer.stackSize += 1;
					} else
						fertilizer.stackSize -= 1;
					fertilizer.stackSize -= 1;
				}

			});
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
