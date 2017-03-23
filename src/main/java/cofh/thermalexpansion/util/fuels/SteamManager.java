package cofh.thermalexpansion.util.fuels;

import cofh.core.init.CoreProps;
import cofh.lib.util.helpers.ItemHelper;
import cofh.thermalfoundation.item.ItemMaterial;
import com.google.common.collect.ImmutableSet;
import gnu.trove.map.hash.TObjectIntHashMap;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityFurnace;

import java.util.Set;

public class SteamManager {

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

		addFuel(new ItemStack(Items.COAL, 1, 0), 32000);
		addFuel(new ItemStack(Blocks.COAL_BLOCK), 32000 * 10);
		addFuel(new ItemStack(Items.COAL, 1, 1), 24000);
		addFuel(ItemHelper.cloneStack(ItemMaterial.gemCoke, 1), 64000);
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
