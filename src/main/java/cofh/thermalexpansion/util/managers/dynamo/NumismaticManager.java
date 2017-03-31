package cofh.thermalexpansion.util.managers.dynamo;

import cofh.lib.inventory.ComparableItemStack;
import cofh.thermalfoundation.item.ItemCoin;
import com.google.common.collect.ImmutableSet;
import gnu.trove.iterator.TObjectIntIterator;
import gnu.trove.map.hash.TObjectIntHashMap;
import net.minecraft.item.ItemStack;

import java.util.Set;

public class NumismaticManager {

	private static TObjectIntHashMap<ComparableItemStack> fuelMap = new TObjectIntHashMap<>();

	public static int DEFAULT_ENERGY = 32000;

	public static Set<ComparableItemStack> getFuels() {

		return ImmutableSet.copyOf(fuelMap.keySet());
	}

	public static int getFuelEnergy(ItemStack stack) {

		if (stack == null) {
			return 0;
		}
		return fuelMap.get(new ComparableItemStack(stack));
	}

	public static void initialize() {

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

		loadFuels();
	}

	public static void loadFuels() {

	}

	public static void refresh() {

		TObjectIntHashMap<ComparableItemStack> tempMap = new TObjectIntHashMap<>(fuelMap.size());

		for (TObjectIntIterator<ComparableItemStack> it = fuelMap.iterator(); it.hasNext(); ) {
			it.advance();
			tempMap.put(new ComparableItemStack(it.key().toItemStack()), it.value());
		}
		fuelMap.clear();
		fuelMap = tempMap;
	}

	/* ADD FUELS */
	public static boolean addFuel(ItemStack stack, int energy) {

		if (stack == null || energy < 1000 || energy > 200000000) {
			return false;
		}
		fuelMap.put(new ComparableItemStack(stack), energy);
		return true;
	}

	/* REMOVE FUELS */
	public static boolean removeFuel(ItemStack stack) {

		fuelMap.remove(new ComparableItemStack(stack));
		return true;
	}

}
