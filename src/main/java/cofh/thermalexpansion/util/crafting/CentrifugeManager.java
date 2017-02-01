package cofh.thermalexpansion.util.crafting;

import cofh.core.util.oredict.OreDictionaryArbiter;
import cofh.lib.inventory.ComparableItemStack;
import cofh.lib.util.helpers.ItemHelper;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;

public class CentrifugeManager {

	/* RECIPE CLASS */
	public static class RecipeCentrifuge {

		final ItemStack input;
		final ArrayList<ItemStack> output;
		final int energy;

		RecipeCentrifuge(ItemStack input, ArrayList<ItemStack> output, int energy) {

			this.input = input;
			this.output = output;
			this.energy = energy;

			if (input.stackSize <= 0) {
				input.stackSize = 1;
			}
		}

		public ItemStack getInput() {

			return input.copy();
		}

		public ArrayList<ItemStack> getOutput() {

			return output;
		}

		public int getEnergy() {

			return energy;
		}
	}

	/* ITEMSTACK CLASS */
	public static class ComparableItemStackCentrifuge extends ComparableItemStack {

		static final String DUST = "dust";

		static boolean safeOreType(String oreName) {

			return oreName.startsWith(DUST);
		}

		static int getOreID(ItemStack stack) {

			ArrayList<Integer> ids = OreDictionaryArbiter.getAllOreIDs(stack);

			if (ids != null) {
				for (int i = 0, e = ids.size(); i < e; ) {
					int id = ids.get(i++);
					if (id != -1 && safeOreType(ItemHelper.oreProxy.getOreName(id))) {
						return id;
					}
				}
			}
			return -1;
		}

		ComparableItemStackCentrifuge(ItemStack stack) {

			super(stack);
			oreID = getOreID(stack);
		}

		@Override
		public ComparableItemStackCentrifuge set(ItemStack stack) {

			super.set(stack);
			oreID = getOreID(stack);

			return this;
		}
	}

}
