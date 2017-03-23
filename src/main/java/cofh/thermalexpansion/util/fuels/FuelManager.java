package cofh.thermalexpansion.util.fuels;

import cofh.core.init.CoreProps;
import cofh.core.util.ConfigHandler;
import cofh.lib.util.helpers.ItemHelper;
import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalexpansion.block.dynamo.*;
import cofh.thermalfoundation.item.ItemCoin;
import cofh.thermalfoundation.item.ItemMaterial;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
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
	public static boolean addSteamFuel(ItemStack stack, int energy) {

		return TileDynamoSteam.addFuel(stack, energy);
	}

	public static boolean removeSteamFuel(ItemStack stack) {

		return TileDynamoSteam.removeFuel(stack);
	}

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
	public static boolean addEnervationFuel(ItemStack stack, int energy) {

		return TileDynamoEnervation.addFuel(stack, energy);
	}

	public static boolean removeEnervationFuel(ItemStack stack) {

		return TileDynamoEnervation.removeFuel(stack);
	}

	/* ENERVATION */
	public static boolean addNumismaticFuel(ItemStack stack, int energy) {

		return TileDynamoNumismatic.addFuel(stack, energy);
	}

	public static boolean removeNumismaticFuel(ItemStack stack) {

		return TileDynamoNumismatic.removeFuel(stack);
	}

	public static void addDefaultFuels() {

		/* STEAM */
		{
			String category = "Fuels.Steam";

			addSteamFuel(new ItemStack(Items.COAL, 1, 0), 32000);
			addSteamFuel(new ItemStack(Blocks.COAL_BLOCK), 32000 * 10);
			addSteamFuel(new ItemStack(Items.COAL, 1, 1), 24000);
			addSteamFuel(ItemHelper.cloneStack(ItemMaterial.gemCoke, 1), 64000);

			//			addSteamFuel(new ItemStack(Items.COAL, 1, 0), configFuels.get(category, "coal", 32000));
			//			addSteamFuel(new ItemStack(Blocks.COAL_BLOCK), configFuels.get(category, "coal", 32000) * 10);
			//			addSteamFuel(new ItemStack(Items.COAL, 1, 1), configFuels.get(category, "charcoal", 24000));
			//			addSteamFuel(ItemHelper.cloneStack(ItemMaterial.gemCoke, 1), configFuels.get(category, "coke", 64000));
		}

		/* MAGMATIC */
		{
			String category = "Fuels.Magmatic";

			addMagmaticFuel("lava", CoreProps.LAVA_RF * 9 / 10);
			addMagmaticFuel("pyrotheum", 2000000);

			//			addMagmaticFuel("lava", configFuels.get(category, "lava", CoreProps.LAVA_RF * 9 / 10));
			//			addMagmaticFuel("pyrotheum", configFuels.get(category, "pyrotheum", 2000000));
		}

		/* COMPRESSION */
		{
			String category = "Fuels.Compression";

			addCompressionFuel("coal", 500000);
			addCompressionFuel("tree_oil", 1000000);
			addCompressionFuel("refined_oil", 1250000);
			addCompressionFuel("fuel", 2000000);

			//			addCompressionFuel("coal", configFuels.get(category, "coal", 500000));
			//			addCompressionFuel("tree_oil", configFuels.get(category, "tree_oil", 1000000));
			//			addCompressionFuel("refined_oil", configFuels.get(category, "refined_oil", 1250000));
			//			addCompressionFuel("fuel", configFuels.get(category, "fuel", 2000000));
		}

		/* REACTANT */
		{
			String category = "Fuels.Reactant";

			addReactantFuel("redstone", 500000);
			addReactantFuel("glowstone", 600000);
			addReactantFuel("aerotheum", 1250000);

			//			addReactantFuel("redstone", configFuels.get(category, "redstone", 500000));
			//			addReactantFuel("glowstone", configFuels.get(category, "glowstone", 600000));
			//			addReactantFuel("aerotheum", configFuels.get(category, "aerotheum", 1250000));
		}

		/* ENERVATION */
		{
			String category = "Fuels.Enervation";

			addEnervationFuel(new ItemStack(Items.REDSTONE), 64000);
			addEnervationFuel(new ItemStack(Blocks.REDSTONE_BLOCK), 64000 * 10);

			//			addEnervationFuel(new ItemStack(Items.REDSTONE), configFuels.get(category, "redstone", 64000));
			//			addEnervationFuel(new ItemStack(Blocks.REDSTONE_BLOCK), configFuels.get(category, "redstone", 64000) * 10);
		}

		/* NUMISMATIC */
		{
			String category = "Fuels.Numismatic";

			addNumismaticFuel(ItemCoin.coinIron, 16000);
			addNumismaticFuel(ItemCoin.coinGold, 24000);

			addNumismaticFuel(ItemCoin.coinCopper, 16000);
			addNumismaticFuel(ItemCoin.coinTin, 16000);
			addNumismaticFuel(ItemCoin.coinSilver, 24000);
			addNumismaticFuel(ItemCoin.coinLead, 24000);
			addNumismaticFuel(ItemCoin.coinAluminum, 32000);
			addNumismaticFuel(ItemCoin.coinNickel, 32000);
			addNumismaticFuel(ItemCoin.coinPlatinum, 48000);
			addNumismaticFuel(ItemCoin.coinIridium, 64000);
			addNumismaticFuel(ItemCoin.coinMithril, 64000);

			addNumismaticFuel(ItemCoin.coinSteel, 32000);
			addNumismaticFuel(ItemCoin.coinElectrum, 24000);
			addNumismaticFuel(ItemCoin.coinInvar, 21000);
			addNumismaticFuel(ItemCoin.coinBronze, 16000);
			addNumismaticFuel(ItemCoin.coinConstantan, 24000);
			addNumismaticFuel(ItemCoin.coinSignalum, 48000);
			addNumismaticFuel(ItemCoin.coinLumium, 48000);
			addNumismaticFuel(ItemCoin.coinEnderium, 64000);
		}

		/* COOLANTS */
		{
			//			String category = "Coolants";
			//			configFuels.getCategory(category).setComment("You can add Coolants in this section. Fluid names only, as they are registered in Minecraft. Currently only used by the Compression Dynamo.");
			//
			//			addCoolant("water", configFuels.get(category, "water", 500000));
			//			addCoolant("cryotheum", configFuels.get(category, "cryotheum", 4000000));
			//
			//			addCoolant("ice", configFuels.get(category, "ice", 2000000));
		}
		configFuels.save();
	}

	/* PARSER */
	public static void parseFuels() {

		/* STEAM */
		{
			String category = "Fuels.Steam";
			configFuels.getCategory(category).setComment("You can adjust fuel values for the Steam Dynamo in this section. New fuels cannot be added at this time.");
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
			//			String category = "Coolants";
			//			configFuels.getCategory(category).setComment("You can add Coolants in this section. Fluid names only, as they are registered in Minecraft.");
			//
			//			Set<String> catKeys = configFuels.getCategoryKeys(category);
			//			for (String s : catKeys) {
			//				addCoolant(s.toLowerCase(Locale.ENGLISH), configFuels.get(category, s, 400000));
			//			}
		}

		/* ENERVATION */
		{
			String category = "Fuels.Enervation";
			configFuels.getCategory(category).setComment("You can adjust fuel values for the Enervation Dynamo in this section. New fuels cannot be added at this time.");
		}
		configFuels.cleanUp(true, false);
	}

}
