package cofh.thermalexpansion.util;

import cofh.core.init.CoreProps;
import cofh.core.util.ConfigHandler;
import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalexpansion.block.dynamo.TileDynamoCompression;
import cofh.thermalexpansion.block.dynamo.TileDynamoMagmatic;
import cofh.thermalexpansion.block.dynamo.TileDynamoReactant;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fluids.FluidRegistry;

import java.io.File;
import java.util.Locale;
import java.util.Set;

public class FuelManager {

	public static ConfigHandler configFuels = new ConfigHandler(ThermalExpansion.VERSION);

	static {
		configFuels.setConfiguration(new Configuration(new File(CoreProps.configDir, "cofh/thermalexpansion/fuels.cfg"), true));
	}

	private FuelManager() {

	}

	/* STEAM */

	/* MAGMATIC */
	public static boolean addMagmaticFuel(String name, int energy) {

		if (!FluidRegistry.isFluidRegistered(name)) {
			return false;
		}
		return TileDynamoMagmatic.addFuel(FluidRegistry.getFluid(name), energy);
	}

	public static boolean removeMagmaticFuel(String name) {

		if (!FluidRegistry.isFluidRegistered(name)) {
			return false;
		}
		return TileDynamoMagmatic.removeFuel(FluidRegistry.getFluid(name));
	}

	/* COMPRESSION */
	public static boolean addCompressionFuel(String name, int energy) {

		if (!FluidRegistry.isFluidRegistered(name)) {
			return false;
		}
		return TileDynamoCompression.addFuel(FluidRegistry.getFluid(name), energy);
	}

	public static boolean removeCompressionFuel(String name) {

		if (!FluidRegistry.isFluidRegistered(name)) {
			return false;
		}
		return TileDynamoCompression.removeFuel(FluidRegistry.getFluid(name));
	}

	public static boolean addCoolant(String name, int cooling) {

		if (!FluidRegistry.isFluidRegistered(name)) {
			return false;
		}
		return TileDynamoCompression.addCoolant(FluidRegistry.getFluid(name), cooling);
	}

	public static boolean removeCoolant(String name) {

		if (!FluidRegistry.isFluidRegistered(name)) {
			return false;
		}
		return TileDynamoCompression.removeCoolant(FluidRegistry.getFluid(name));
	}

	/* REACTANT */
	public static boolean addReactantFuel(String name, int energy) {

		if (!FluidRegistry.isFluidRegistered(name)) {
			return false;
		}
		return TileDynamoReactant.addFuel(FluidRegistry.getFluid(name), energy);
	}

	public static boolean removeReactantFuel(String name) {

		if (!FluidRegistry.isFluidRegistered(name)) {
			return false;
		}
		return TileDynamoReactant.removeFuel(FluidRegistry.getFluid(name));
	}

	public static boolean addReactant(ItemStack stack, int energy) {

		return TileDynamoReactant.addReactant(stack, energy);
	}

	public static boolean removeReactant(ItemStack stack) {

		return TileDynamoReactant.removeReactant(stack);
	}

	/* ENERVATION */

	public static void addDefaultFuels() {

		/* STEAM */
		{
			String category = "Fuels.Steam";
		}

		/* MAGMATIC */
		{
			String category = "Fuels.Magmatic";
			configFuels.getCategory(category).setComment("You can add fuels to the Magmatic Dynamo in this section. Fluid names only, as they are registered in Minecraft.");

			addMagmaticFuel("lava", configFuels.get(category, "lava", CoreProps.LAVA_RF * 9 / 10));
			addMagmaticFuel("pyrotheum", configFuels.get(category, "pyrotheum", 2000000));

		}

		/* COMPRESSION */
		{
			String category = "Fuels.Compression";
			configFuels.getCategory(category).setComment("You can add fuels to the Compression Dynamo in this section. Fluid names only, as they are registered in Minecraft.");

			addCompressionFuel("coal", configFuels.get(category, "coal", 1000000));

			addCompressionFuel("biofuel", configFuels.get(category, "biofuel", 500000));
			addCompressionFuel("bioethanol", configFuels.get(category, "bioethanol", 500000));

			addCompressionFuel("fuel", configFuels.get(category, "fuel", 1500000));
			addCompressionFuel("oil", configFuels.get(category, "oil", 150000));
		}

		/* REACTANT */
		{
			String category = "Fuels.Reactant";
			configFuels.getCategory(category).setComment("You can add fuels to the Reactant Dynamo in this section. Fluid names only, as they are registered in Minecraft.");

			addReactantFuel("redstone", configFuels.get(category, "redstone", 600000));
			addReactantFuel("glowstone", configFuels.get(category, "glowstone", 750000));

			addReactantFuel("mobessence", configFuels.get(category, "mobessence", 500000));
			addReactantFuel("sewage", configFuels.get(category, "sewage", 12000));
			addReactantFuel("sludge", configFuels.get(category, "sludge", 12000));

			addReactantFuel("seedoil", configFuels.get(category, "seedoil", 250000));
			addReactantFuel("biomass", configFuels.get(category, "biomass", 450000));

			addReactantFuel("creosote", configFuels.get(category, "creosote", 200000));
		}

		/* ENERVATION */
		{
			String category = "Fuels.Enervation";
		}

		/* COOLANTS */
		{
			String category = "Coolants";
			configFuels.getCategory(category).setComment("You can add Coolants in this section. Fluid names only, as they are registered in Minecraft. Currently only used by the Compression Dynamo.");

			addCoolant("water", configFuels.get(category, "water", 400000));
			addCoolant("cryotheum", configFuels.get(category, "cryotheum", 4000000));

			addCoolant("ice", configFuels.get(category, "ice", 2000000));
		}
	}

	/* PARSER */
	public static void parseFuels() {

		/* STEAM */
		{
			String category = "Fuels.Steam";
		}

		/* MAGMATIC */
		{
			String category = "Fuels.Magmatic";
			configFuels.getCategory(category).setComment("You can add fuels to the Magmatic Dynamo in this section. Fluid names only, as they are registered in Minecraft.");

			Set<String> catKeys = configFuels.getCategoryKeys(category);
			for (String s : catKeys) {
				addMagmaticFuel(s.toLowerCase(Locale.ENGLISH), configFuels.get(category, s, CoreProps.LAVA_RF * 9 / 10));
			}
		}

		/* COMPRESSION */
		{
			String category = "Fuels.Compression";
			configFuels.getCategory(category).setComment("You can add fuels to the Compression Dynamo in this section. Fluid names only, as they are registered in Minecraft.");

			Set<String> catKeys = configFuels.getCategoryKeys(category);
			for (String s : catKeys) {
				addCompressionFuel(s.toLowerCase(Locale.ENGLISH), configFuels.get(category, s, 500000));
			}
		}

		/* REACTANT */
		{
			String category = "Fuels.Reactant";
			configFuels.getCategory(category).setComment("You can add fuels to the Reactant Dynamo in this section. Fluid names only, as they are registered in Minecraft.");

			Set<String> catKeys = configFuels.getCategoryKeys(category);
			for (String s : catKeys) {
				addReactantFuel(s.toLowerCase(Locale.ENGLISH), configFuels.get(category, s, 200000));
			}
		}

		/* COOLANTS */
		{
			String category = "Coolants";
			configFuels.getCategory(category).setComment("You can add Coolants in this section. Fluid names only, as they are registered in Minecraft. Currently only used by the Compression Dynamo.");

			Set<String> catKeys = configFuels.getCategoryKeys(category);
			for (String s : catKeys) {
				addCoolant(s.toLowerCase(Locale.ENGLISH), configFuels.get(category, s, 400000));
			}
		}

		/* ENERVATION */
		{
			String category = "Fuels.Enervation";
		}

		configFuels.cleanUp(true, false);
	}

}
