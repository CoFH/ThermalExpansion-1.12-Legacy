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

public class MFRPlugin {

	public static void preInit() {

	}

	public static void initialize() {

	}

	public static void postInit() {

		if (Loader.isModLoaded("MineFactoryReloaded")) {
			FactoryRegistry.sendMessage("registerFertilizer", new IFactoryFertilizer() {

				@Override
				public Item getFertilizer() {

					return TEItems.itemMaterial;
				}

				@Override
				public FertilizerType getFertilizerType(ItemStack stack) {

					if (TEItems.fertilizer.isItemEqual(stack)) {
						return FertilizerType.GrowPlant;
					} else if (TEItems.fertilizerRich.isItemEqual(stack)) {
						return FertilizerType.GrowPlant;
					}
					return FertilizerType.None;
				}

				@Override
				public void consume(ItemStack fertilizer) {

					if (TEItems.fertilizerRich.isItemEqual(fertilizer)) {
						if (MathHelper.RANDOM.nextBoolean()) {
							fertilizer.stackSize += 1;
						}
					} else {
						fertilizer.stackSize -= 1;
					}
					fertilizer.stackSize -= 1;
				}

			});
			ThermalExpansion.log.info("MineFactoryReloaded Plugin Enabled.");
		}
	}

}
