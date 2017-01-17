package cofh.thermalexpansion.init;

import cofh.lib.util.helpers.EnergyHelper;
import cofh.lib.util.helpers.ItemHelper;
import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalexpansion.item.TEAugments;
import cofh.thermalexpansion.item.TEFlorbs;
import cofh.thermalexpansion.item.tool.*;
import cofh.thermalexpansion.util.crafting.TECraftingHandler;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;

import static cofh.lib.util.helpers.ItemHelper.ShapedRecipe;
import static cofh.lib.util.helpers.ItemHelper.ShapelessRecipe;

public class TEItemsOld {

	public static ItemIgniter itemIgniter;
	public static ItemChiller itemChiller;
	public static ItemPump itemPump;
	public static ItemTransfuser itemTransfuser;
	public static ItemMiner itemMiner;

	public static ItemStack toolIgniter;
	public static ItemStack toolChiller;
	public static ItemStack toolPump;
	public static ItemStack toolTransfuser;
	public static ItemStack toolMiner;

	public static ItemStack capacitorPotato;
	public static ItemStack capacitorBasic;
	public static ItemStack capacitorHardened;
	public static ItemStack capacitorReinforced;
	public static ItemStack capacitorResonant;
	public static ItemStack capacitorCreative;

	public static ItemStack satchelBasic;
	public static ItemStack satchelHardened;
	public static ItemStack satchelReinforced;
	public static ItemStack satchelResonant;
	public static ItemStack satchelCreative;

	public static ItemStack pneumaticServo;
	public static ItemStack powerCoilGold;
	public static ItemStack powerCoilSilver;
	public static ItemStack powerCoilElectrum;

	public static ItemStack lock;

	public static ItemStack sawdust;
	public static ItemStack sawdustCompressed;
	public static ItemStack slag;
	public static ItemStack slagRich;
	public static ItemStack fertilizer;
	public static ItemStack fertilizerRich;

	public static boolean enableIgniter = true;
	public static boolean enableChiller = true;
	public static boolean enablePump = true;
	public static boolean enableTransfuser = true;

	static {
		String category2 = "Item.Tool.";
		String category = category2 + "Igniter";
		enableIgniter = ThermalExpansion.CONFIG.get(category, "Recipe", true);

		category = category2 + "Chiller";
		enableChiller = ThermalExpansion.CONFIG.get(category, "Recipe", true);

		category = category2 + "Pump";
		enablePump = ThermalExpansion.CONFIG.get(category, "Recipe", true);

		category = category2 + "Transfuser";
		enableTransfuser = ThermalExpansion.CONFIG.get(category, "Recipe", true);
	}

	private TEItemsOld() {

	}

	public static void preInit() {

		itemIgniter = (ItemIgniter) new ItemIgniter().setUnlocalizedName("tool", "igniter");
		itemChiller = (ItemChiller) new ItemChiller().setUnlocalizedName("tool", "chiller");
		itemPump = (ItemPump) new ItemPump().setUnlocalizedName("tool", "pump");
		itemTransfuser = (ItemTransfuser) new ItemTransfuser().setUnlocalizedName("tool", "transfuser");

		//GameRegistry.register(itemWrench);
		//GameRegistry.register(itemBattleWrench);
		//GameRegistry.register(itemMultimeter);
		//GameRegistry.register(itemIgniter);
		//GameRegistry.register(itemChiller);
		//GameRegistry.register(itemPump);
		//GameRegistry.register(itemTransfuser);
		//GameRegistry.register(itemSatchel);
		//GameRegistry.register(itemDiagram);

        		/* Tools */
		toolIgniter = new ItemStack(itemIgniter);
		toolChiller = new ItemStack(itemChiller);
		toolPump = new ItemStack(itemPump);
		toolTransfuser = new ItemStack(itemTransfuser);

		EnergyHelper.setDefaultEnergyTag(capacitorCreative, 0);
		EnergyHelper.setDefaultEnergyTag(capacitorBasic, 0);
		EnergyHelper.setDefaultEnergyTag(capacitorHardened, 0);
		EnergyHelper.setDefaultEnergyTag(capacitorReinforced, 0);
		EnergyHelper.setDefaultEnergyTag(capacitorResonant, 0);

		TEAugments.preInit();
		TEFlorbs.preInit();
	}

	public static void initialize() {

		TEAugments.initialize();
		TEFlorbs.initialize();
	}

	public static void postInit() {

		/* Tools */
		if (enableIgniter) {
			GameRegistry.addRecipe(ShapedRecipe(toolIgniter, " R ", "IXI", " G ", 'I', "ingotIron", 'R', "dustRedstone", 'X', capacitorBasic, 'G', Items.FLINT));
		}
		if (enableChiller) {
			GameRegistry.addRecipe(ShapedRecipe(toolChiller, " R ", "IXI", " G ", 'I', "ingotIron", 'R', "dustRedstone", 'X', capacitorBasic, 'G', Items.SNOWBALL));
		}
		if (enablePump) {
			GameRegistry.addRecipe(ShapedRecipe(toolPump, "RR ", "RGI", " XY", 'I', "ingotInvar", 'R', "dustRedstone", 'X', capacitorBasic, 'Y', Items.BUCKET, 'G', "gearBronze"));
		}
		if (enableTransfuser) {
			GameRegistry.addRecipe(ShapedRecipe(toolTransfuser, "RR ", "RGI", " IY", 'I', "ingotInvar", 'R', "dustRedstone", 'X', "ingotSignalum", 'Y', powerCoilElectrum, 'G', "gearElectrum"));
		}

		TECraftingHandler.addSecureRecipe(satchelCreative);
		TECraftingHandler.addSecureRecipe(satchelBasic);
		TECraftingHandler.addSecureRecipe(satchelHardened);
		TECraftingHandler.addSecureRecipe(satchelReinforced);
		TECraftingHandler.addSecureRecipe(satchelResonant);

		/* Parts */
		String category = "General";
		boolean servosAllowSilver = ThermalExpansion.CONFIG.get(category, "PneumaticServo.AllowSilver", false);
		boolean servosAllowInvar = ThermalExpansion.CONFIG.get(category, "PneumaticServo.AllowInvar", false);
		boolean servosAllowBronze = ThermalExpansion.CONFIG.get(category, "PneumaticServo.AllowBronze", false);
		boolean servosAllowSteel = ThermalExpansion.CONFIG.get(category, "PneumaticServo.AllowSteel", false);

		GameRegistry.addRecipe(ShapedRecipe(pneumaticServo, " I ", "GRG", " I ", 'R', "dustRedstone", 'G', "blockGlass", 'I', "ingotIron"));

		if (servosAllowSilver) {
			GameRegistry.addRecipe(ShapedRecipe(pneumaticServo, " I ", "GRG", " I ", 'R', "dustRedstone", 'G', "blockGlass", 'I', "ingotSilver"));
		}
		if (servosAllowInvar) {
			GameRegistry.addRecipe(ShapedRecipe(pneumaticServo, " I ", "GRG", " I ", 'R', "dustRedstone", 'G', "blockGlass", 'I', "ingotInvar"));
		}
		if (servosAllowBronze) {
			GameRegistry.addRecipe(ShapedRecipe(pneumaticServo, " I ", "GRG", " I ", 'R', "dustRedstone", 'G', "blockGlass", 'I', "ingotBronze"));
		}
		if (servosAllowSteel) {
			GameRegistry.addRecipe(ShapedRecipe(pneumaticServo, " I ", "GRG", " I ", 'R', "dustRedstone", 'G', "blockGlass", 'I', "ingotSteel"));
		}
		GameRegistry.addRecipe(ShapedRecipe(powerCoilGold, "  R", " G ", "R  ", 'R', "dustRedstone", 'G', "ingotGold"));
		GameRegistry.addRecipe(ShapedRecipe(powerCoilSilver, "  R", " G ", "R  ", 'R', "dustRedstone", 'G', "ingotSilver"));
		GameRegistry.addRecipe(ShapedRecipe(powerCoilElectrum, "R  ", " G ", "  R", 'R', "dustRedstone", 'G', "ingotElectrum"));

		GameRegistry.addRecipe(ShapedRecipe(lock, " S ", "SBS", "SSS", 'B', "ingotBronze", 'S', "nuggetSignalum"));

		/* Misc Items */
		GameRegistry.addRecipe(ShapelessRecipe(new ItemStack(Items.GUNPOWDER, 2), "dustSaltpeter", "dustSaltpeter", "dustSulfur", "dustCoal"));
		GameRegistry.addRecipe(ShapelessRecipe(new ItemStack(Items.GUNPOWDER, 2), "dustSaltpeter", "dustSaltpeter", "dustSulfur", "dustCharcoal"));

		ItemHelper.addGearRecipe(new ItemStack(Items.PAPER, 2), "dustWood", new ItemStack(Items.WATER_BUCKET));
		GameRegistry.addRecipe(ShapedRecipe(sawdustCompressed, "###", "# #", "###", '#', "dustWood"));
		GameRegistry.addRecipe(ShapelessRecipe(new ItemStack(Items.CLAY_BALL, 4), slag, slag, Blocks.DIRT, Items.WATER_BUCKET));

		GameRegistry.addSmelting(sawdustCompressed, new ItemStack(Items.COAL, 1, 1), 0.15F);

		GameRegistry.addRecipe(ShapelessRecipe(ItemHelper.cloneStack(fertilizer, 8), "dustWood", "dustWood", "dustSaltpeter", slag));
		GameRegistry.addRecipe(ShapelessRecipe(ItemHelper.cloneStack(fertilizer, 32), "dustCharcoal", "dustSaltpeter", slag));
		GameRegistry.addRecipe(ShapelessRecipe(ItemHelper.cloneStack(fertilizerRich, 8), "dustWood", "dustWood", "dustSaltpeter", slagRich));
		GameRegistry.addRecipe(ShapelessRecipe(ItemHelper.cloneStack(fertilizerRich, 32), "dustCharcoal", "dustSaltpeter", slagRich));

		TEAugments.postInit();
		TEFlorbs.postInit();
	}
}
