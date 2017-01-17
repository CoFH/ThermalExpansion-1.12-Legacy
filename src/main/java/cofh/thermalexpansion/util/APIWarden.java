package cofh.thermalexpansion.util;

import cofh.thermalexpansion.api.crafting.*;
import cofh.thermalexpansion.api.fuels.*;
import cofh.thermalexpansion.util.crafting.*;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

/**
 * This basically is the manager for the "unsafe" API interactions. It initializes external handles and maintains the Handler objects.
 *
 * @author King Lemming
 */
public class APIWarden {

	private APIWarden() {

	}

	static {
		CraftingHandlers.furnace = new FurnaceHandler();
		CraftingHandlers.pulverizer = new PulverizerHandler();
		CraftingHandlers.sawmill = new SawmillHandler();
		CraftingHandlers.smelter = new SmelterHandler();
		CraftingHandlers.crucible = new CrucibleHandler();
		CraftingHandlers.transposer = new TransposerHandler();
		CraftingHandlers.charger = new ChargerHandler();
		CraftingHandlers.insolator = new InsolatorHandler();
	}

	/**
	 * MACHINES
	 */

	/* FURNACE */
	public static class FurnaceHandler implements IFurnaceHandler {

		@Override
		public boolean addRecipe(int energy, ItemStack input, ItemStack output, boolean overwrite) {

			return FurnaceManager.addRecipe(energy, input, output, overwrite);
		}

		@Override
		public boolean removeRecipe(ItemStack input) {

			return FurnaceManager.removeRecipe(input);
		}
	}

	/* PULVERIZER */
	public static class PulverizerHandler implements IPulverizerHandler {

		@Override
		public boolean addRecipe(int energy, ItemStack input, ItemStack primaryOutput, ItemStack secondaryOutput, int secondaryChance, boolean overwrite) {

			return PulverizerManager.addRecipe(energy, input, primaryOutput, secondaryOutput, secondaryChance, overwrite);
		}

		@Override
		public boolean removeRecipe(ItemStack input) {

			return PulverizerManager.removeRecipe(input);
		}
	}

	/* SAWMILL */
	public static class SawmillHandler implements ISawmillHandler {

		@Override
		public boolean addRecipe(int energy, ItemStack input, ItemStack primaryOutput, ItemStack secondaryOutput, int secondaryChance, boolean overwrite) {

			return SawmillManager.addRecipe(energy, input, primaryOutput, secondaryOutput, secondaryChance, overwrite);
		}

		@Override
		public boolean removeRecipe(ItemStack input) {

			return SawmillManager.removeRecipe(input);
		}
	}

	/* SMELTER */
	public static class SmelterHandler implements ISmelterHandler {

		@Override
		public boolean addRecipe(int energy, ItemStack primaryInput, ItemStack secondaryInput, ItemStack primaryOutput, ItemStack secondaryOutput, int secondaryChance, boolean overwrite) {

			return SmelterManager.addRecipe(energy, primaryInput, secondaryInput, primaryOutput, secondaryOutput, secondaryChance, overwrite);
		}

		@Override
		public boolean removeRecipe(ItemStack primaryInput, ItemStack secondaryInput) {

			return SmelterManager.removeRecipe(primaryInput, secondaryInput);
		}
	}

	/* CRUCIBLE */
	public static class CrucibleHandler implements ICrucibleHandler {

		@Override
		public boolean addRecipe(int energy, ItemStack input, FluidStack output, boolean overwrite) {

			return CrucibleManager.addRecipe(energy, input, output, overwrite);
		}

		@Override
		public boolean removeRecipe(ItemStack input) {

			return CrucibleManager.removeRecipe(input);
		}
	}

	/* TRANSPOSER */
	public static class TransposerHandler implements ITransposerHandler {

		@Override
		public boolean addFillRecipe(int energy, ItemStack input, ItemStack output, FluidStack fluid, boolean reversible, boolean overwrite) {

			return TransposerManager.addFillRecipe(energy, input, output, fluid, reversible, overwrite);
		}

		@Override
		public boolean addExtractionRecipe(int energy, ItemStack input, ItemStack output, FluidStack fluid, int chance, boolean reversible, boolean overwrite) {

			return TransposerManager.addExtractionRecipe(energy, input, output, fluid, chance, reversible, overwrite);
		}

		@Override
		public boolean removeFillRecipe(ItemStack input, FluidStack fluid) {

			return TransposerManager.removeFillRecipe(input, fluid);
		}

		@Override
		public boolean removeExtractionRecipe(ItemStack input) {

			return TransposerManager.removeExtractionRecipe(input);
		}
	}

	/* CHARGER */
	public static class ChargerHandler implements IChargerHandler {

		@Override
		public boolean addRecipe(int energy, ItemStack input, ItemStack output, boolean overwrite) {

			return ChargerManager.addRecipe(energy, input, output, overwrite);
		}

		@Override
		public boolean removeRecipe(ItemStack input) {

			return ChargerManager.removeRecipe(input);
		}
	}

	/* INSOLATOR */
	public static class InsolatorHandler implements IInsolatorHandler {

		@Override
		public boolean addRecipe(int energy, ItemStack primaryInput, ItemStack secondaryInput, ItemStack primaryOutput, ItemStack secondaryOutput, int secondaryChance, boolean overwrite) {

			return InsolatorManager.addRecipe(energy, primaryInput, secondaryInput, primaryOutput, secondaryOutput, secondaryChance, overwrite);
		}

		@Override
		public boolean removeRecipe(ItemStack primaryInput, ItemStack secondaryInput) {

			return InsolatorManager.removeRecipe(primaryInput, secondaryInput);
		}
	}

	/**
	 * DYNAMOS
	 */

	/* STEAM */
	public static class SteamHandler implements ISteamHandler {

		@Override
		public boolean addFuel(ItemStack input, int energy) {

			return false;
		}

		@Override
		public boolean removeFuel(ItemStack input) {

			return false;
		}

	}

	/* MAGMATIC */
	public static class MagmaticHandler implements IMagmaticHandler {

		@Override
		public boolean addFuel(String name, int energy) {

			return FuelManager.addMagmaticFuel(name, energy);
		}

		@Override
		public boolean removeFuel(String name) {

			return false;
		}

	}

	/* COMPRESSION */
	public static class CompressionHandler implements ICompressionHandler {

		@Override
		public boolean addFuel(String name, int energy) {

			return FuelManager.addCompressionFuel(name, energy);
		}

		@Override
		public boolean addCoolant(String name, int cooling) {

			return FuelManager.addCoolant(name, cooling);
		}

		@Override
		public boolean removeFuel(String name) {

			return false;
		}

		@Override
		public boolean removeCoolant(String name) {

			return false;
		}

	}

	/* REACTANT */
	public static class ReactantHandler implements IReactantHandler {

		@Override
		public boolean addFuel(String name, int energy) {

			return FuelManager.addReactantFuel(name, energy);
		}

		@Override
		public boolean addReactant(ItemStack input, int energy) {

			return false;
		}

		@Override
		public boolean removeFuel(String name) {

			return false;
		}

		@Override
		public boolean removeReactant(ItemStack input) {

			return false;
		}

	}

	/* ENERVATION */
	public static class EnervationHandler implements IEnervationHandler {

		@Override
		public boolean addFuel(ItemStack input, int energy) {

			return false;
		}

		@Override
		public boolean removeFuel(ItemStack input) {

			return false;
		}

	}

}
