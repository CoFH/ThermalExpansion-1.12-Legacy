package cofh.thermalexpansion.util.managers.dynamo;

import cofh.core.inventory.ComparableItemStack;
import cofh.core.util.helpers.ItemHelper;
import cofh.thermalfoundation.item.ItemCoin;
import com.google.common.collect.ImmutableSet;
import gnu.trove.iterator.TObjectIntIterator;
import gnu.trove.map.hash.TObjectIntHashMap;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import java.util.Set;

public class NumismaticManager {

	private static TObjectIntHashMap<ComparableItemStack> fuelMap = new TObjectIntHashMap<>();
	private static TObjectIntHashMap<ComparableItemStack> gemFuelMap = new TObjectIntHashMap<>();

	public static int DEFAULT_ENERGY = 30000;

	public static Set<ComparableItemStack> getFuels() {

		return ImmutableSet.copyOf(fuelMap.keySet());
	}

	public static Set<ComparableItemStack> getGemFuels() {

		return ImmutableSet.copyOf(gemFuelMap.keySet());
	}

	public static int getFuelEnergy(ItemStack stack) {

		if (stack.isEmpty()) {
			return 0;
		}
		return fuelMap.get(new ComparableItemStack(stack));
	}

	public static int getGemFuelEnergy(ItemStack stack) {

		if (stack.isEmpty()) {
			return 0;
		}
		return gemFuelMap.get(new ComparableItemStack(stack));
	}

	public static void initialize() {

		addFuel(ItemCoin.coinIron, 30000);
		addFuel(ItemCoin.coinGold, 40000);

		addFuel(ItemCoin.coinCopper, 30000);
		addFuel(ItemCoin.coinTin, 30000);
		addFuel(ItemCoin.coinSilver, 40000);
		addFuel(ItemCoin.coinLead, 40000);
		addFuel(ItemCoin.coinAluminum, 40000);
		addFuel(ItemCoin.coinNickel, 60000);
		addFuel(ItemCoin.coinPlatinum, 80000);
		addFuel(ItemCoin.coinIridium, 100000);
		addFuel(ItemCoin.coinMithril, 150000);

		addFuel(ItemCoin.coinSteel, 40000);
		addFuel(ItemCoin.coinElectrum, 40000);
		addFuel(ItemCoin.coinInvar, 40000);
		addFuel(ItemCoin.coinBronze, 30000);
		addFuel(ItemCoin.coinConstantan, 45000);
		addFuel(ItemCoin.coinSignalum, 100000);
		addFuel(ItemCoin.coinLumium, 100000);
		addFuel(ItemCoin.coinEnderium, 150000);

		addFuel(new ItemStack(Items.EMERALD), 200000);

		addGemFuel(new ItemStack(Items.QUARTZ), 40000);
		addGemFuel(new ItemStack(Items.DYE, 1, 4), 80000);
		addGemFuel(new ItemStack(Items.PRISMARINE_SHARD), 150000);
		addGemFuel(new ItemStack(Items.EMERALD), 200000);
		addGemFuel(new ItemStack(Items.DIAMOND), 1200000);

		loadFuels();
	}

	public static void loadFuels() {

		/* BIOMES O' PLENTY */
		{
			if (ItemHelper.oreNameExists("gemAmethyst")) {
				addGemFuel(ItemHelper.getOre("gemAmethyst"), 200000);
			}
			if (ItemHelper.oreNameExists("gemRuby")) {
				addGemFuel(ItemHelper.getOre("gemRuby"), 200000);
			}
			if (ItemHelper.oreNameExists("gemPeridot")) {
				addGemFuel(ItemHelper.getOre("gemPeridot"), 200000);
			}
			if (ItemHelper.oreNameExists("gemTopaz")) {
				addGemFuel(ItemHelper.getOre("gemTopaz"), 200000);
			}
			if (ItemHelper.oreNameExists("gemTanzanite")) {
				addGemFuel(ItemHelper.getOre("gemTanzanite"), 200000);
			}
			if (ItemHelper.oreNameExists("gemMalachite")) {
				addGemFuel(ItemHelper.getOre("gemMalachite"), 200000);
			}
			if (ItemHelper.oreNameExists("gemSapphire")) {
				addGemFuel(ItemHelper.getOre("gemSapphire"), 200000);
			}
			if (ItemHelper.oreNameExists("gemAmber")) {
				addGemFuel(ItemHelper.getOre("gemAmber"), 200000);
			}
		}

		/* FORESTRY */
		{
			if (ItemHelper.oreNameExists("gemApatite")) {
				addGemFuel(ItemHelper.getOre("gemApatite"), 40000);
			}
		}

		/* REDSTONE ARSENAL */
		{
			if (ItemHelper.oreNameExists("gemCrystalFlux")) {
				addGemFuel(ItemHelper.getOre("gemCrystalFlux"), 1500000);
			}
		}

		/* TECH REBORN */
		{
			if (ItemHelper.oreNameExists("gemRedGarnet")) {
				addGemFuel(ItemHelper.getOre("gemRedGarnet"), 200000);
			}
			if (ItemHelper.oreNameExists("gemYellowGarnet")) {
				addGemFuel(ItemHelper.getOre("gemYellowGarnet"), 200000);
			}
		}

	}

	public static void refresh() {

		TObjectIntHashMap<ComparableItemStack> tempMap = new TObjectIntHashMap<>(fuelMap.size());
		TObjectIntHashMap<ComparableItemStack> tempMap2 = new TObjectIntHashMap<>(gemFuelMap.size());

		for (TObjectIntIterator<ComparableItemStack> it = fuelMap.iterator(); it.hasNext(); ) {
			it.advance();
			tempMap.put(new ComparableItemStack(it.key().toItemStack()), it.value());
		}
		for (TObjectIntIterator<ComparableItemStack> it = gemFuelMap.iterator(); it.hasNext(); ) {
			it.advance();
			tempMap2.put(new ComparableItemStack(it.key().toItemStack()), it.value());
		}
		fuelMap.clear();
		fuelMap = tempMap;

		gemFuelMap.clear();
		gemFuelMap = tempMap2;
	}

	/* ADD FUELS */
	public static boolean addFuel(ItemStack stack, int energy) {

		if (stack.isEmpty() || energy < 1000 || energy > 200000000) {
			return false;
		}
		fuelMap.put(new ComparableItemStack(stack), energy);
		return true;
	}

	public static boolean addGemFuel(ItemStack stack, int energy) {

		if (stack.isEmpty() || energy < 1000 || energy > 200000000) {
			return false;
		}
		gemFuelMap.put(new ComparableItemStack(stack), energy);
		return true;
	}

	/* REMOVE FUELS */
	public static boolean removeFuel(ItemStack stack) {

		fuelMap.remove(new ComparableItemStack(stack));
		return true;
	}

	public static boolean removeGemFuel(ItemStack stack) {

		gemFuelMap.remove(new ComparableItemStack(stack));
		return true;
	}

}
