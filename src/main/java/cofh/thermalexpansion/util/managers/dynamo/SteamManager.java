package cofh.thermalexpansion.util.managers.dynamo;

import cofh.core.init.CoreProps;
import cofh.lib.inventory.ComparableItemStack;
import cofh.lib.util.helpers.ItemHelper;
import cofh.thermalfoundation.item.ItemMaterial;
import com.google.common.collect.ImmutableSet;
import gnu.trove.iterator.TObjectIntIterator;
import gnu.trove.map.hash.TObjectIntHashMap;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityFurnace;

import java.util.Set;

public class SteamManager {

	private static TObjectIntHashMap<ComparableItemStack> fuelMap = new TObjectIntHashMap<>();

	public static int DEFAULT_ENERGY = 32000;

	public static Set<ComparableItemStack> getFuels() {

		return ImmutableSet.copyOf(fuelMap.keySet());
	}

	public static int getFuelEnergy(ItemStack stack) {

		if (stack == null) {
			return 0;
		}
		if (stack.getItem().hasContainerItem(stack)) {
			return 0;
		}
		int energy = fuelMap.get(new ComparableItemStack(stack));

		return energy > 0 ? energy : TileEntityFurnace.getItemBurnTime(stack) * CoreProps.RF_PER_MJ;
	}

	public static void initialize() {

		addFuel(new ItemStack(Items.COAL, 1, 0), 24000);
		addFuel(new ItemStack(Blocks.COAL_BLOCK), 24000 * 10);
		addFuel(new ItemStack(Items.COAL, 1, 1), 16000);
		addFuel(ItemHelper.cloneStack(ItemMaterial.gemCoke, 1), 40000);

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
