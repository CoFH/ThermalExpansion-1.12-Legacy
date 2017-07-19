package cofh.thermalexpansion.util.managers.dynamo;

import cofh.lib.inventory.ComparableItemStack;
import cofh.thermalfoundation.init.TFFluids;
import cofh.thermalfoundation.item.ItemMaterial;
import gnu.trove.map.hash.THashMap;
import gnu.trove.set.hash.THashSet;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class ReactantManager {

	private static Map<List<Integer>, Reaction> reactionMap = new THashMap<>();
	private static Set<ComparableItemStack> validReactants = new THashSet<>();
	private static Set<String> validFluids = new THashSet<>();

	private static Set<ComparableItemStack> validReactantsElemental = new THashSet<>();
	private static Set<String> validFluidsElemental = new THashSet<>();

	static final ItemStack SUGAR = new ItemStack(Items.SUGAR);
	static final ItemStack NETHER_WART = new ItemStack(Items.NETHER_WART);
	static final ItemStack GUNPOWDER = new ItemStack(Items.GUNPOWDER);
	static final ItemStack BLAZE_POWDER = new ItemStack(Items.BLAZE_POWDER);
	static final ItemStack GHAST_TEAR = new ItemStack(Items.GHAST_TEAR);

	public static int DEFAULT_ENERGY = 100000;

	public static Reaction getReaction(ItemStack reactant, FluidStack fluid) {

		return reactant == null || fluid == null ? null : reactionMap.get(Arrays.asList(new ComparableItemStack(reactant).hashCode(), fluid.getFluid().getName().hashCode()));
	}

	public static Reaction getReaction(ItemStack reactant, Fluid fluid) {

		return reactant == null || fluid == null ? null : reactionMap.get(Arrays.asList(new ComparableItemStack(reactant).hashCode(), fluid.getName().hashCode()));
	}

	public static boolean reactionExists(ItemStack reactant, FluidStack fluid) {

		return getReaction(reactant, fluid.getFluid()) != null;
	}

	public static boolean reactionExists(ItemStack reactant, Fluid fluid) {

		return getReaction(reactant, fluid) != null;
	}

	public static Reaction[] getReactionList() {

		return reactionMap.values().toArray(new Reaction[reactionMap.size()]);
	}

	public static boolean validReactant(ItemStack reactant) {

		return reactant != null && validReactants.contains(new ComparableItemStack(reactant));
	}

	public static boolean validFluid(FluidStack fluid) {

		return fluid != null && validFluids.contains(fluid.getFluid().getName());
	}

	public static boolean isElementalReaction(ItemStack reactant, FluidStack fluid) {

		return validReactantElemental(reactant) && validFluidElemental(fluid);
	}

	public static boolean validReactantElemental(ItemStack reactant) {

		return reactant != null && validReactantsElemental.contains(new ComparableItemStack(reactant));
	}

	public static boolean validFluidElemental(FluidStack fluid) {

		return fluid != null && validFluidsElemental.contains(fluid.getFluid().getName());
	}

	public static void initialize() {

		addReaction(SUGAR, TFFluids.fluidRedstone, 80000);
		addReaction(NETHER_WART, TFFluids.fluidRedstone, 100000);
		addReaction(GUNPOWDER, TFFluids.fluidRedstone, 100000);
		addReaction(BLAZE_POWDER, TFFluids.fluidRedstone, 150000);
		addReaction(GHAST_TEAR, TFFluids.fluidRedstone, 150000);

		addReaction(SUGAR, TFFluids.fluidGlowstone, 100000);
		addReaction(NETHER_WART, TFFluids.fluidGlowstone, 125000);
		addReaction(GUNPOWDER, TFFluids.fluidGlowstone, 125000);
		addReaction(BLAZE_POWDER, TFFluids.fluidGlowstone, 200000);
		addReaction(GHAST_TEAR, TFFluids.fluidGlowstone, 200000);

		addElementalReaction(ItemMaterial.dustPyrotheum, TFFluids.fluidCryotheum, 400000);
		addElementalReaction(ItemMaterial.dustCryotheum, TFFluids.fluidPyrotheum, 400000);
		addElementalReaction(ItemMaterial.dustAerotheum, TFFluids.fluidPetrotheum, 400000);
		addElementalReaction(ItemMaterial.dustPetrotheum, TFFluids.fluidAerotheum, 400000);

		loadReactions();
	}

	public static void loadReactions() {

	}

	public static void refresh() {

		Map<List<Integer>, Reaction> tempReactionMap = new THashMap<>(reactionMap.size());
		Set<ComparableItemStack> tempSet = new THashSet<>();
		Set<ComparableItemStack> tempSet2 = new THashSet<>();
		Reaction tempReaction;

		for (Entry<List<Integer>, Reaction> entry : reactionMap.entrySet()) {
			tempReaction = entry.getValue();
			ComparableItemStack reactant = new ComparableItemStack(tempReaction.reactant);
			tempReactionMap.put(Arrays.asList(reactant.hashCode(), tempReaction.getFluidName().hashCode()), tempReaction);
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

		if (reactant == null || fluid == null || energy < 10000 || energy > 200000000) {
			return false;
		}
		if (reactionExists(reactant, fluid)) {
			return false;
		}
		Reaction reaction = new Reaction(reactant, fluid, energy);
		reactionMap.put(Arrays.asList(new ComparableItemStack(reactant).hashCode(), fluid.getName().hashCode()), reaction);
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

		return reactionMap.remove(Arrays.asList(new ComparableItemStack(reactant).hashCode(), fluid.getName().hashCode())) != null;
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
