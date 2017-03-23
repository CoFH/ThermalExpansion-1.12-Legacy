package cofh.thermalexpansion.util.fuels;

import cofh.core.init.CoreProps;
import cofh.thermalfoundation.item.ItemCoin;
import com.google.common.collect.ImmutableSet;
import gnu.trove.map.hash.TObjectIntHashMap;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityFurnace;

import java.util.Set;

public class NumismaticManager {

	private static TObjectIntHashMap<ItemStack> fuelMap = new TObjectIntHashMap<>();

	public static int DEFAULT_ENERGY = 32000;

	public static Set<ItemStack> getFuels() {

		return ImmutableSet.copyOf(fuelMap.keySet());
	}

	public static int getFuelEnergy(ItemStack stack) {

		if (stack == null) {
			return 0;
		}
		if (stack.getItem().hasContainerItem(stack)) {
			return 0;
		}
		int energy = fuelMap.get(stack);

		return energy > 0 ? energy : TileEntityFurnace.getItemBurnTime(stack) * CoreProps.RF_PER_MJ;
	}

	public static void addDefaultFuels() {

		addFuel(ItemCoin.coinIron, 16000);
		addFuel(ItemCoin.coinGold, 24000);

		addFuel(ItemCoin.coinCopper, 16000);
		addFuel(ItemCoin.coinTin, 16000);
		addFuel(ItemCoin.coinSilver, 24000);
		addFuel(ItemCoin.coinLead, 24000);
		addFuel(ItemCoin.coinAluminum, 32000);
		addFuel(ItemCoin.coinNickel, 32000);
		addFuel(ItemCoin.coinPlatinum, 48000);
		addFuel(ItemCoin.coinIridium, 64000);
		addFuel(ItemCoin.coinMithril, 64000);

		addFuel(ItemCoin.coinSteel, 32000);
		addFuel(ItemCoin.coinElectrum, 24000);
		addFuel(ItemCoin.coinInvar, 21000);
		addFuel(ItemCoin.coinBronze, 16000);
		addFuel(ItemCoin.coinConstantan, 24000);
		addFuel(ItemCoin.coinSignalum, 48000);
		addFuel(ItemCoin.coinLumium, 48000);
		addFuel(ItemCoin.coinEnderium, 64000);
	}

	public static void loadFuels() {

	}

	/* ADD FUELS */
	public static boolean addFuel(ItemStack stack, int energy) {

		if (stack == null || energy < 1000 || energy > 200000000) {
			return false;
		}
		fuelMap.put(stack, energy);
		return true;
	}

	/* REMOVE FUELS */
	public static boolean removeFuel(ItemStack stack) {

		fuelMap.remove(stack);
		return true;
	}

}
