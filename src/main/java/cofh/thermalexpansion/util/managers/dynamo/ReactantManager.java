package cofh.thermalexpansion.util.managers.dynamo;

import cofh.core.inventory.ComparableItemStack;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import static java.util.Arrays.asList;

public class ReactantManager {

	private static Map<List<Integer>, Reaction> reactionMap = new Object2ObjectOpenHashMap<>();
	private static Set<ComparableItemStack> validReactants = new ObjectOpenHashSet<>();
	private static Set<String> validFluids = new ObjectOpenHashSet<>();

	private static Set<ComparableItemStack> validReactantsElemental = new ObjectOpenHashSet<>();
	private static Set<String> validFluidsElemental = new ObjectOpenHashSet<>();

	public static int DEFAULT_ENERGY = 100000;

	public static Reaction getReaction(ItemStack reactant, FluidStack fluid) {

		return reactant.isEmpty() || fluid == null ? null : reactionMap.get(asList(new ComparableItemStack(reactant).hashCode(), fluid.getFluid().getName().hashCode()));
	}

	public static Reaction getReaction(ItemStack reactant, Fluid fluid) {

		return reactant.isEmpty() || fluid == null ? null : reactionMap.get(asList(new ComparableItemStack(reactant).hashCode(), fluid.getName().hashCode()));
	}

	public static boolean reactionExists(ItemStack reactant, FluidStack fluid) {

		return getReaction(reactant, fluid) != null;
	}

	public static boolean reactionExists(ItemStack reactant, Fluid fluid) {

		return getReaction(reactant, fluid) != null;
	}

	public static boolean reactionExistsElemental(ItemStack reactant, Fluid fluid) {

		return validReactantElemental(reactant) && validFluidElemental(fluid);
	}

	public static Reaction[] getReactionList() {

		return reactionMap.values().toArray(new Reaction[reactionMap.size()]);
	}

	public static boolean validReactant(ItemStack reactant) {

		return !reactant.isEmpty() && validReactants.contains(new ComparableItemStack(reactant));
	}

	public static boolean validReactantElemental(ItemStack reactant) {

		return !reactant.isEmpty() && validReactantsElemental.contains(new ComparableItemStack(reactant));
	}

	public static boolean validFluid(FluidStack fluid) {

		return fluid != null && validFluids.contains(fluid.getFluid().getName());
	}

	public static boolean validFluidElemental(FluidStack fluid) {

		return fluid != null && validFluidsElemental.contains(fluid.getFluid().getName());
	}

	public static boolean validFluidElemental(Fluid fluid) {

		return fluid != null && validFluidsElemental.contains(fluid.getName());
	}

	public static void refresh() {

		Map<List<Integer>, Reaction> tempReactionMap = new Object2ObjectOpenHashMap<>(reactionMap.size());
		Set<ComparableItemStack> tempSet = new ObjectOpenHashSet<>();
		Set<ComparableItemStack> tempSet2 = new ObjectOpenHashSet<>();
		Reaction tempReaction;

		for (Entry<List<Integer>, Reaction> entry : reactionMap.entrySet()) {
			tempReaction = entry.getValue();
			ComparableItemStack reactant = new ComparableItemStack(tempReaction.reactant);
			tempReactionMap.put(asList(reactant.hashCode(), tempReaction.getFluidName().hashCode()), tempReaction);
			tempSet.add(reactant);

			if (validFluidsElemental.contains(tempReaction.getFluidName())) {
				tempSet2.add(reactant);
			}
		}
		reactionMap.clear();
		reactionMap = tempReactionMap;

		validReactants.clear();
		validReactants = tempSet;

		validReactantsElemental.clear();
		validReactantsElemental = tempSet2;
	}

	/* ADD REACTIONS */
	public static boolean addReaction(ItemStack reactant, Fluid fluid, int energy) {

		if (reactant.isEmpty() || fluid == null || energy < 10000 || energy > 200000000) {
			return false;
		}
		if (reactionExists(reactant, fluid)) {
			return false;
		}
		Reaction reaction = new Reaction(reactant, fluid, energy);
		reactionMap.put(asList(new ComparableItemStack(reactant).hashCode(), fluid.getName().hashCode()), reaction);
		validReactants.add(new ComparableItemStack(reactant));
		validFluids.add(fluid.getName());
		return true;
	}

	public static boolean addElementalReaction(ItemStack reactant, Fluid fluid, int energy) {

		if (addReaction(reactant, fluid, energy)) {
			validReactantsElemental.add(new ComparableItemStack(reactant));
			validFluidsElemental.add(fluid.getName());
			return true;
		}
		return false;
	}

	/* REMOVE REACTIONS */
	public static boolean removeReaction(ItemStack reactant, Fluid fluid) {

		return reactionMap.remove(asList(new ComparableItemStack(reactant).hashCode(), fluid.getName().hashCode())) != null;
	}

	public static boolean removeElementalReaction(ItemStack reactant, Fluid fluid) {

		validReactantsElemental.remove(new ComparableItemStack(reactant));
		validFluidsElemental.remove(fluid.getName());
		return true;
	}

	/* REACTION CLASS */
	public static class Reaction {

		final ItemStack reactant;
		final Fluid fluid;
		final int energy;

		Reaction(ItemStack reactant, Fluid fluid, int energy) {

			this.reactant = reactant;
			this.fluid = fluid;
			this.energy = energy;
		}

		public ItemStack getReactant() {

			return reactant;
		}

		public Fluid getFluid() {

			return fluid;
		}

		public String getFluidName() {

			return fluid.getName();
		}

		public int getEnergy() {

			return energy;
		}
	}

}
